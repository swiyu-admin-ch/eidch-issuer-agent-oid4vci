/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.infrastructure.web.controller;

import ch.admin.bj.swiyu.issuer.oid4vci.api.OAuthTokenDto;
import ch.admin.bj.swiyu.issuer.oid4vci.api.OpenIdConfigurationDto;
import ch.admin.bj.swiyu.issuer.oid4vci.common.config.OpenIdIssuerConfiguration;
import ch.admin.bj.swiyu.issuer.oid4vci.common.exception.OAuthException;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.CredentialRequest;
import ch.admin.bj.swiyu.issuer.oid4vci.infrastructure.web.config.OpenIdIssuerApiConfiguration;
import ch.admin.bj.swiyu.issuer.oid4vci.service.CredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * OpenID4VC Issuance Controller
 * <p>
 * Implements the OpenID4VCI defined endpoints
 * <a href="https://openid.github.io/OpenID4VCI/openid-4-verifiable-credential-issuance-wg-draft.html">OID4VCI Spec</a>
 */
@Deprecated
@RestController
@AllArgsConstructor
@Slf4j
@Tag(name = "Issuer OID4VCI", description = "OpenID for Verifiable Credential Issuance API")
@RequestMapping(value = {})
public class DeprecatedIssuanceController {
    private static final String OID4VCI_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:pre-authorized_code";

    private final OpenIdIssuerApiConfiguration openIDConfigurationDto;
    private final OpenIdIssuerConfiguration openIDConfiguration;
    private final CredentialService credentialService;

    /**
     * Endpoint for the wallet to fetch the token required for getting the credential
     * Does not yet support pin.IssuerMetadataTechnical
     *
     * @param grantType   should be always urn:ietf:params:oauth:grant-type:pre-authorized_code
     * @param preAuthCode single use code to get the token
     * @return OAuth Token or raises an exception
     */
    @PostMapping(value = {"/token"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Collect Bearer token with pre-authorized code")
    public OAuthTokenDto oauthAccessToken(
            @RequestParam(name = "grant_type", defaultValue = OID4VCI_GRANT_TYPE) String grantType,
            @RequestParam(name = "pre-authorized_code") String preAuthCode) {

        if (!OID4VCI_GRANT_TYPE.equals(grantType)) {
            throw OAuthException.invalidRequest("Grant type must be urn:ietf:params:oauth:grant-type:pre-authorized_code");
        }
        return credentialService.issueOAuthToken(preAuthCode);
    }

    /**
     * General information about the issuer
     *
     * @return OpenIdConfigurationDto as defined by OIDConnect and extended by OID4VCI
     */
    @GetMapping(value = {"/.well-known/openid-configuration"})
    @Operation(summary = "OpenID Connect information required for issuing VCs")
    public OpenIdConfigurationDto getOpenIDConfiguration() throws IOException {
        return openIDConfigurationDto.getOpenIdConfiguration();
    }

    /**
     * Data concerning OpenID4VC Issuance
     *
     * @return Issuer Metadata as defined by OID4VCI
     */
    @GetMapping(value = {"/.well-known/openid-credential-issuer"})
    @Operation(summary = "Information about credentials which can be issued.")
    public Map<String, Object> getIssuerMetadata() throws IOException {
        return openIDConfiguration.getIssuerMetadata();
    }

    @PostMapping(value = {"/credential"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/jwt"})
    @Operation(summary = "Collect credential associated with the bearer token with the requested credential properties.")
    public ResponseEntity<String> createCredential(
            @RequestHeader("Authorization") String bearerToken,
            @Validated @RequestBody CredentialRequest credentialRequest) {
        if (bearerToken == null) {
            throw OAuthException.invalidRequest("No authorization header found");
        }
        var regexPattern = Pattern.compile("bearer (.*)", Pattern.CASE_INSENSITIVE);
        var matcher = regexPattern.matcher(bearerToken);
        if (!matcher.find()) {
            throw OAuthException.invalidRequest("No bearer token found");
        }

        var credentialEnvelope = credentialService.createCredential(credentialRequest, matcher.group(1));

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, credentialEnvelope.getContentType());
        return ResponseEntity.ok()
                .headers(headers)
                .body(credentialEnvelope.getOid4vciCredentialJson());
    }
}
