/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * information for encrypting the Credential Response.
 */
@Validated
@Data
@NoArgsConstructor
public class CredentialResponseEncryption {
    @NotNull
    private Map<String, Object> jwk;
    @NotNull
    private String alg;
    @NotNull
    private String enc;
}
