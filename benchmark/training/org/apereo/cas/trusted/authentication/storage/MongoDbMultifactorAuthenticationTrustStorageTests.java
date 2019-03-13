package org.apereo.cas.trusted.authentication.storage;


import java.time.LocalDateTime;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecord;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.config.MongoDbMultifactorAuthenticationTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustedDeviceFingerprintConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link MongoDbMultifactorAuthenticationTrustStorageTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("MongoDb")
@SpringBootTest(classes = { MongoDbMultifactorAuthenticationTrustConfiguration.class, MultifactorAuthnTrustedDeviceFingerprintConfiguration.class, MultifactorAuthnTrustConfiguration.class, CasCoreAuditConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.mfa.trusted.mongo.databaseName=mfa-trusted", "cas.authn.mfa.trusted.mongo.host=localhost", "cas.authn.mfa.trusted.mongo.port=27017", "cas.authn.mfa.trusted.mongo.userId=root", "cas.authn.mfa.trusted.mongo.password=secret", "cas.authn.mfa.trusted.mongo.authenticationDatabaseName=admin", "cas.authn.mfa.trusted.mongo.dropCollection=true" })
@EnabledIfPortOpen(port = 27017)
@EnabledIfContinuousIntegration
public class MongoDbMultifactorAuthenticationTrustStorageTests {
    @Autowired
    @Qualifier("mfaTrustEngine")
    private MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @Test
    public void verifySetAnExpireByKey() {
        mfaTrustEngine.set(MultifactorAuthenticationTrustRecord.newInstance("casuser", "geography", "fingerprint"));
        val records = mfaTrustEngine.get("casuser");
        Assertions.assertEquals(1, records.size());
        mfaTrustEngine.expire(records.stream().findFirst().get().getRecordKey());
        Assertions.assertTrue(mfaTrustEngine.get("casuser").isEmpty());
    }

    @Test
    public void verifyExpireByDate() {
        val r = MultifactorAuthenticationTrustRecord.newInstance("castest", "geography", "fingerprint");
        r.setRecordDate(LocalDateTime.now().minusDays(2));
        mfaTrustEngine.set(r);
        Assertions.assertEquals(1, mfaTrustEngine.get(LocalDateTime.now().minusDays(30)).size());
        Assertions.assertEquals(0, mfaTrustEngine.get(LocalDateTime.now().minusDays(2)).size());
    }
}

