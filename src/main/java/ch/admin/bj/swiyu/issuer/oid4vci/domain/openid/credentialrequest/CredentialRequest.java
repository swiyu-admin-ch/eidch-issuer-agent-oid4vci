/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest;

import java.util.Map;
import java.util.Optional;

import ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.holderbinding.Proof;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.holderbinding.ProofJwt;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.holderbinding.ProofType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Representation of an <a href="https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0-ID1.html#section-7.2">OID4VCI Credential Request</a>
 * using the parameters for the pre-authenticated flow
 */
@Data
@Builder
@AllArgsConstructor
public class CredentialRequest {

    @NotNull
    @Pattern(regexp = "^vc\\+sd-jwt$", message = "Only vc+sd-jwt format is supported")
    private String format;

    @Schema(description = "Proof for holder binding. Can be in key:did or cnf format.")
    @CredentialRequestProofConstraint
    private Map<String, Object> proof;

    /**
     * If this request element is not present, the corresponding credential response returned is not encrypted
     */
    @JsonProperty("credential_response_encryption")
    private CredentialResponseEncryption credentialResponseEncryption;

    public Optional<Proof> getProof(int acceptaleProofTimeWindow) {
        var key = "proof_type";
        if (proof == null || proof.get(key) == null) {
            return Optional.empty();
        }
        if (proof.get(key).equals(ProofType.JWT.toString())) {
            Optional.ofNullable(proof.get("jwt"))
                    .filter(jwt -> jwt instanceof String)
                    .map(jwt -> (String) jwt)
                    .orElseThrow(() -> new IllegalArgumentException("jwt property needs to be present when proof_type is jwt"));
            return Optional.of(new ProofJwt(ProofType.JWT, proof.get(ProofType.JWT.toString()).toString(), acceptaleProofTimeWindow));
        } else {
            throw new IllegalArgumentException("Any other proof type than jwt is not supported");
        }
    }
}
