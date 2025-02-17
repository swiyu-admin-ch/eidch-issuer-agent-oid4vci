/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.metadata;

import ch.admin.bj.swiyu.issuer.oid4vci.common.exception.CredentialRequestError;
import ch.admin.bj.swiyu.issuer.oid4vci.common.exception.Oid4vcException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * The issuer metadata represented here are the fields used for technical decisions in creating the VC.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class IssuerMetadataTechnical {

    @JsonProperty("credential_issuer")
    @NotNull
    private String credentialIssuer;

    /**
     * Information for the holder where to get the credential.
     * Must be present or the holder will not be able to fetch the credential
     */
    @JsonProperty("credential_endpoint")
    @NotNull
    private String credentialEndpoint;

    @JsonProperty("credential_configurations_supported")
    @NotNull
    private Map<String, CredentialConfiguration> credentialConfigurationSupported;

    @JsonProperty("credential_response_encryption")
    private IssuerCredentialResponseEncryption responseEncryption;

    @JsonProperty("version")
    @NotNull
    private String version;

    @PostConstruct
    public void validateVersion() {
        if (!"1.0".equals(version)) {
            throw new IllegalArgumentException("Version must be 1.0");
        }
    }

    public @NotNull CredentialConfiguration getCredentialConfigurationById(String credentialConfigurationSupportedId) {
        CredentialConfiguration credentialConfiguration = credentialConfigurationSupported.get(credentialConfigurationSupportedId);
        if (credentialConfiguration == null) {
            throw new Oid4vcException(CredentialRequestError.INVALID_CREDENTIAL_REQUEST, "Requested Credential is not offered (anymore).");
        }
        return credentialConfiguration;
    }
}
