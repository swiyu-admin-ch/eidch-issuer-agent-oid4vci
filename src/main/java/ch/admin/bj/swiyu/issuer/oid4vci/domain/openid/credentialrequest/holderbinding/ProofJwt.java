/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.holderbinding;

import ch.admin.bj.swiyu.issuer.oid4vci.common.exception.CredentialRequestError;
import ch.admin.bj.swiyu.issuer.oid4vci.common.exception.Oid4vcException;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialOffer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ProofJwt extends Proof {

    private final String jwt;
    private String holderKeyJson;

    private static Oid4vcException proofException(String errorDescription) {
        return new Oid4vcException(CredentialRequestError.INVALID_PROOF, errorDescription);
    }

    public ProofJwt(ProofType proofType, String jwt) {
        super(proofType);
        this.jwt = jwt;
    }

    @Override
    public boolean isValidHolderBinding(String issuerId, List<String> supportedSigningAlgorithms, CredentialOffer offer) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(this.jwt);

            // check JOSE headers
            JWSHeader header = signedJWT.getHeader();

            // check if typ header is present and equals "openid4vci-proof+jwt"
            if (!header.getType().toString().equals(ProofType.JWT.getClaimTyp())) {
                throw proofException(String.format("Proof Type is not supported. Must be 'openid4vci-proof+jwt' but was %s", header.getType()));
            }

            // check if alg header is present and is supported
            if (header.getAlgorithm() == null || !supportedSigningAlgorithms.contains(header.getAlgorithm().getName())) {
                throw proofException("Proof Signing Algorithm is not supported");
            }

            // Check jwt body values:
            var claimSet = signedJWT.getJWTClaimsSet();

            // aud: REQUIRED (string). The value of this claim MUST be the Credential Issuer Identifier.
            if (claimSet.getAudience().isEmpty() || !claimSet.getAudience().contains(issuerId)) {
                throw proofException("Audience claim is missing or incorrect");
            }

            if (claimSet.getIssueTime() == null) {
                throw proofException("Issue Time claim is missing");
            }

            ECKey holderKey = getNormalizedECKey(header);
            JWSVerifier verifier = new ECDSAVerifier(holderKey);
            if (!signedJWT.verify(verifier)) {
                throw proofException("Proof JWT is not valid!");
            }

            // the nonce claim matches the server-provided c_nonce value, if the server had previously provided a c_nonce,
            var nonce = offer.getNonce().toString();
            if (nonce != null && !nonce.equals(signedJWT.getJWTClaimsSet().getStringClaim("nonce"))) {
                throw proofException("Nonce claim does not match the server-provided c_nonce value");
            }

            if (offer.getTokenExpirationTimestamp() != null && Instant.now().isAfter(Instant.ofEpochSecond(offer.getTokenExpirationTimestamp()))) {
                throw proofException("Token is expired");
            }

            this.holderKeyJson = holderKey.toJSONString();

        } catch (ParseException e) {
            throw proofException("Provided Proof JWT is not parseable; " + e.getMessage());
        } catch (JOSEException e) {
            throw proofException("Key is not usable; " + e.getMessage());
        }

        return true;
    }

    @Override
    public String getBinding() {
        return this.holderKeyJson;
    }

    /**
     * Gets the ECKey from either kid with did or the cnf entry
     *
     * @return the Holder's ECKey
     */
    private ECKey getNormalizedECKey(JWSHeader header) {
        var kid = header.getKeyID();
        if (kid != null && !kid.isEmpty()) {
            try {
                return DidJwk.createFromDidJwk(kid).getJWK().toECKey();
            } catch (ParseException e) {
                throw proofException(String.format("kid property %s could not be parsed to a JWK", kid));
            }
        }
        return Optional.ofNullable(header.getJWK()).orElseThrow(() ->
                proofException("Missing jwk entry in header.")
        ).toECKey();
    }
}
