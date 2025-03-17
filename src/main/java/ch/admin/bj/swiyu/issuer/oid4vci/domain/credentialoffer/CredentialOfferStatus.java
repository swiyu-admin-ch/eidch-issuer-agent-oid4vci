/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credential_offer_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
@AllArgsConstructor // test data
public class CredentialOfferStatus {

    @EmbeddedId
    private CredentialOfferStatusKey id;

    @ManyToOne
    @MapsId("offerId")
    @JoinColumn(name = "credential_offer_id", referencedColumnName = "id", updatable = false)
    // We have no reason to propagate the update, as there *should* be no use case for this
    private CredentialOffer offer;

    @ManyToOne
    @MapsId("statusListId")
    @JoinColumn(name = "status_list_id", referencedColumnName = "id", updatable = false)
    // We have no reason to propagate the update, as there *should* be no use case for this
    private StatusList statusList;

    @Column
    private Integer index;

}
