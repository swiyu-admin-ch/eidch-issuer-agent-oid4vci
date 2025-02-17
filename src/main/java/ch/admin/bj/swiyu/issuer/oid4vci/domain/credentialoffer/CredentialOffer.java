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
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "credential_offer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
@AllArgsConstructor // test data
public class CredentialOffer {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private CredentialStatus credentialStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> metadataCredentialSupportedId;

    /**
     * Offer data comes wrapped with some metadata
     * <p>
     * for raw offer data it is
     * { "data": $json_value }
     * <p>
     * for a JWT Encoded Offer it is
     * {"data": $jwt_string, "data_integrity": "jwt"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> offerData;
    private UUID accessToken;
    @NotNull
    private Long tokenExpirationTimestamp;
    private UUID nonce;
    @NotNull
    private long offerExpirationTimestamp;
    private Instant credentialValidFrom;
    private Instant credentialValidUntil;

    @OneToMany(mappedBy = "offer")
    @Cascade(CascadeType.ALL)
    private Set<CredentialOfferStatus> offerStatusSet;

    public void markAsIssued() {
        this.offerData = null;
        this.credentialStatus = CredentialStatus.ISSUED;
    }

    public void markAsInProgress() {
        this.credentialStatus = CredentialStatus.IN_PROGRESS;
        if (this.accessToken == null) {
            this.accessToken = UUID.randomUUID();
        }
    }

    public void setTokenIssuanceTimestamp(long tokenTTL) {
        this.tokenExpirationTimestamp = Instant.now().plusSeconds(tokenTTL).getEpochSecond();
    }

    public void markAsExpired() {
        this.credentialStatus = CredentialStatus.EXPIRED;
        this.offerData = null;
    }

    public boolean hasExpirationTimeStampPassed() {
        return Instant.now().isAfter(Instant.ofEpochSecond(this.offerExpirationTimestamp));
    }
}
