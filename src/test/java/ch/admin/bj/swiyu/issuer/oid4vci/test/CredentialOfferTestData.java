/*
 * SPDX-FileCopyrightText: 2025 Swiss Confederation
 *
 * SPDX-License-Identifier: MIT
 */

package ch.admin.bj.swiyu.issuer.oid4vci.test;

import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialOffer;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialOfferStatus;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialOfferStatusKey;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.CredentialStatus;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.StatusList;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.StatusListType;
import ch.admin.bj.swiyu.issuer.oid4vci.domain.credentialoffer.TokenStatusListToken;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class CredentialOfferTestData {

    public static CredentialOffer createTestOffer(UUID offerID, UUID preAuthCode, CredentialStatus status, String metadataId) {
        return createTestOffer(offerID, preAuthCode, status, metadataId, Instant.now(), Instant.now().plusSeconds(120));
    }

    public static CredentialOffer createTestOffer(UUID preAuthCode, CredentialStatus status, String metadataId) {
        return createTestOffer(UUID.randomUUID(), preAuthCode, status, metadataId, Instant.now(), Instant.now().plusSeconds(120));
    }

    public static CredentialOffer createTestOffer(UUID preAuthCode, CredentialStatus status, String metadataId, Instant validFrom, Instant validUntil) {
        return createTestOffer(UUID.randomUUID(), preAuthCode, status, metadataId, validFrom, validUntil);
    }

    public static StatusList createStatusList() {
        var statusListToken = new TokenStatusListToken(2, 10000);
        return new StatusList(
                UUID.randomUUID(),
                StatusListType.TOKEN_STATUS_LIST,
                Map.of("bits", 2),
                "https://localhost:8080/status",
                statusListToken.getStatusListClaims().get("lst").toString(),
                0,
                10000,
                Collections.emptySet()

        );
    }

    public static CredentialOffer createTestOffer(UUID offerID, UUID preAuthCode, CredentialStatus status, String metadataId, Instant validFrom, Instant validUntil) {
        HashMap<String, Object> offerData = new HashMap<>();
        offerData.put("data", new GsonBuilder().create().toJson(addIllegalClaims(getUniversityCredentialSubjectData())));
        return new CredentialOffer(
                offerID,
                status,
                List.of(metadataId),
                offerData,
                Map.of("vct#integrity", "sha256-SVHLfKfcZcBrw+d9EL/1EXxvGCdkQ7tMGvZmd0ysMck="),
                UUID.randomUUID(),
                Instant.now().plusSeconds(600).getEpochSecond(),
                UUID.randomUUID(),
                preAuthCode,
                (int) Instant.now().plusSeconds(120).getEpochSecond(),
                validFrom,
                validUntil,
                null
        );
    }

    /**
     * illegally overriding some properties. They should be ignored in all tests this is used
     *
     * @param credentialSubjectData the credential subject data to be manipulated
     * @return a new copy of the credentialSubjectData with additional sd-jwt illegal claims
     */
    public static Map<String, String> addIllegalClaims(Map<String, String> credentialSubjectData) {
        var alteredCredentialSubjectData = new HashMap<>(credentialSubjectData);
        alteredCredentialSubjectData.put("iss", "did:example:test-university");
        alteredCredentialSubjectData.put("vct", "lorem ipsum");
        alteredCredentialSubjectData.put("iat", "0");
        return alteredCredentialSubjectData;
    }

    public static Map<String, String> getUniversityCredentialSubjectData() {
        Map<String, String> credentialSubjectData = new HashMap<>();
        credentialSubjectData.put("degree", "Bachelor of Science");
        credentialSubjectData.put("name", "Data Science");
        credentialSubjectData.put("average_grade", "5.33");
        return credentialSubjectData;
    }

    public static CredentialOfferStatus linkStatusList(CredentialOffer offer, StatusList statusList) {
        return new CredentialOfferStatus(
                new CredentialOfferStatusKey(offer.getId(), statusList.getId()),
                offer,
                statusList,
                statusList.getNextFreeIndex()
        );
    }
}
