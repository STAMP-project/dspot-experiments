package org.apereo.cas.trusted.authentication.storage;


import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecord;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.config.JdbcMultifactorAuthnTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustedDeviceFingerprintConfiguration;
import org.apereo.cas.trusted.util.MultifactorAuthenticationTrustUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Test cases for {@link JpaMultifactorAuthenticationTrustStorage}.
 *
 * @author Daniel Frett
 * @since 5.3.0
 */
@SpringBootTest(classes = { JdbcMultifactorAuthnTrustConfiguration.class, MultifactorAuthnTrustedDeviceFingerprintConfiguration.class, MultifactorAuthnTrustConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuditConfiguration.class, RefreshAutoConfiguration.class })
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@TestPropertySource(properties = "cas.jdbc.physicalTableNames.MultifactorAuthenticationTrustRecord=mfaauthntrustedrec")
public class JpaMultifactorAuthenticationTrustStorageTests {
    private static final String PRINCIPAL = "principal";

    private static final String PRINCIPAL2 = "principal2";

    private static final String GEOGRAPHY = "geography";

    private static final String DEVICE_FINGERPRINT = "deviceFingerprint";

    @Autowired
    @Qualifier("mfaTrustEngine")
    private MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @Test
    public void verifyExpireByKey() {
        // create 2 records
        mfaTrustEngine.set(MultifactorAuthenticationTrustRecord.newInstance(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL, JpaMultifactorAuthenticationTrustStorageTests.GEOGRAPHY, JpaMultifactorAuthenticationTrustStorageTests.DEVICE_FINGERPRINT));
        mfaTrustEngine.set(MultifactorAuthenticationTrustRecord.newInstance(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL, JpaMultifactorAuthenticationTrustStorageTests.GEOGRAPHY, JpaMultifactorAuthenticationTrustStorageTests.DEVICE_FINGERPRINT));
        val records = mfaTrustEngine.get(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL);
        Assertions.assertEquals(2, records.size());
        // expire 1 of the records
        mfaTrustEngine.expire(records.stream().findFirst().orElseThrow().getRecordKey());
        Assertions.assertEquals(1, mfaTrustEngine.get(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL).size());
    }

    @Test
    public void verifyRetrieveAndExpireByDate() {
        Stream.of(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL, JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL2).forEach(( p) -> {
            for (var offset = 0; offset < 3; offset++) {
                val record = MultifactorAuthenticationTrustRecord.newInstance(p, JpaMultifactorAuthenticationTrustStorageTests.GEOGRAPHY, JpaMultifactorAuthenticationTrustStorageTests.DEVICE_FINGERPRINT);
                record.setRecordDate(LocalDateTime.now().minusDays(offset));
                mfaTrustEngine.set(record);
            }
        });
        Assertions.assertEquals(6, mfaTrustEngine.get(LocalDateTime.now().minusDays(30)).size());
        Assertions.assertEquals(2, mfaTrustEngine.get(LocalDateTime.now().minusSeconds(1)).size());
        // expire records older than today
        mfaTrustEngine.expire(LocalDateTime.now().minusDays(1));
        Assertions.assertEquals(2, mfaTrustEngine.get(LocalDateTime.now().minusDays(30)).size());
        Assertions.assertEquals(2, mfaTrustEngine.get(LocalDateTime.now().minusSeconds(1)).size());
    }

    @Test
    public void verifyStoreAndRetrieve() {
        // create record
        val original = MultifactorAuthenticationTrustRecord.newInstance(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL, JpaMultifactorAuthenticationTrustStorageTests.GEOGRAPHY, JpaMultifactorAuthenticationTrustStorageTests.DEVICE_FINGERPRINT);
        mfaTrustEngine.set(original);
        val records = mfaTrustEngine.get(JpaMultifactorAuthenticationTrustStorageTests.PRINCIPAL);
        Assertions.assertEquals(1, records.size());
        val record = records.stream().findFirst().orElseThrow();
        Assertions.assertEquals(MultifactorAuthenticationTrustUtils.generateKey(original), MultifactorAuthenticationTrustUtils.generateKey(record));
    }
}

