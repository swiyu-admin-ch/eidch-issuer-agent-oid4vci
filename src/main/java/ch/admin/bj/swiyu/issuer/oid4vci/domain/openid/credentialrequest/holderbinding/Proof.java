/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.openid.credentialrequest.holderbinding;

import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialOffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class Proof {
    public final ProofType proofType;

    public abstract boolean isValidHolderBinding(String issuerId, List<String> supportedSigningAlgorithms, CredentialOffer offer);

    public abstract String getBinding();
}
