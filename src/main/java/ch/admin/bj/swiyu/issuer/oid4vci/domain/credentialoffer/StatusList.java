/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "status_list")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
public class StatusList {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusListType type;
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> config;
    private String uri;
    private String statusZipped;
    private Integer nextFreeIndex;
    private Integer maxLength;

    @OneToMany(mappedBy = "statusList")
    private Set<CredentialOfferStatus> offerStatusSet;

    // only needed for tests
    public void incrementIndex() {
        this.nextFreeIndex++;
    }
}
