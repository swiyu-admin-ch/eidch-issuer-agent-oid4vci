/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class CredentialConfiguration {
    @NotNull
    private String format;

    /**
     * SD-JWT specific field <a href="https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0-ID1.html#appendix-A.3.2">see specs</a>
     * Optional
     */
    private String vct;

    /**
     * SD-JWT specific field <a href="https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0-ID1.html#appendix-A.3.2">see specs</a>
     * Optional
     */
    @JsonProperty("claims")
    private HashMap<String, Object> claims;

    /**
     * Field for VC not SD-JWT (VC Signed as JWT or JSON-LD)
     * Optional
     */
    @JsonProperty("credential_definition")
    private CredentialDefinition credentialDefinition;

    /**
     * Identification of cryptographic binding.
     * example for use with base registry
     * ["did:tdw"]
     */
    @JsonProperty("cryptographic_binding_methods_supported")
    private List<String> cryptographicBindingMethodsSupported;

    /**
     * Case-sensitive strings that identify the algorithms that the Issuer uses to sign the issued Credential
     */
    @JsonProperty("credential_signing_alg_values_supported")
    private List<String> credentialSigningAlgorithmsSupported;

    /**
     * Define what kind of proof the holder is allowed to provide for the credential
     */
    @JsonProperty("proof_types_supported")
    private Map<String, SupportedProofType> proofTypesSupported;

}
