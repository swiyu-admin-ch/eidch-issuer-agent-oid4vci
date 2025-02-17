/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.metadata;

import ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.CredentialResponseEncryption;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class IssuerCredentialResponseEncryption {
    @JsonProperty("alg_values_supported")
    @NotNull
    private List<String> algValuesSupported;
    @JsonProperty("enc_values_supported")
    @NotNull
    private List<String> encValuesSupported;
    @JsonProperty("encryption_required")
    @NotNull
    private boolean encRequired;

    public boolean contains(CredentialResponseEncryption requestedEncryption) {
        return algValuesSupported.contains(requestedEncryption.getAlg())
                && encValuesSupported.contains(requestedEncryption.getEnc());
    }
}
