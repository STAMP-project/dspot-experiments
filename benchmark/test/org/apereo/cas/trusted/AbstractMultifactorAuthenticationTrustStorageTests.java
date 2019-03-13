package org.apereo.cas.trusted;


import java.time.LocalDateTime;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.trusted.authentication.storage.MultifactorAuthenticationTrustStorageCleaner;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustWebflowConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustedDeviceFingerprintConfiguration;
import org.apereo.cas.trusted.web.flow.fingerprint.DeviceFingerprintStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.webflow.execution.Action;


/**
 * This is {@link AbstractMultifactorAuthenticationTrustStorageTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreAuditConfiguration.class, MultifactorAuthnTrustWebflowConfiguration.class, MultifactorAuthnTrustConfiguration.class, MultifactorAuthnTrustedDeviceFingerprintConfiguration.class })
public abstract class AbstractMultifactorAuthenticationTrustStorageTests {
    @Autowired
    @Qualifier("mfaVerifyTrustAction")
    protected Action mfaVerifyTrustAction;

    @Autowired
    @Qualifier(BEAN_DEVICE_FINGERPRINT_STRATEGY)
    protected DeviceFingerprintStrategy deviceFingerprintStrategy;

    @Autowired
    @Qualifier("mfaTrustStorageCleaner")
    protected MultifactorAuthenticationTrustStorageCleaner mfaTrustStorageCleaner;

    @Test
    public void verifyTrustEngine() {
        val record = AbstractMultifactorAuthenticationTrustStorageTests.getMultifactorAuthenticationTrustRecord();
        getMfaTrustEngine().set(record);
        Assertions.assertFalse(getMfaTrustEngine().get(record.getPrincipal()).isEmpty());
        Assertions.assertFalse(getMfaTrustEngine().get(LocalDateTime.now()).isEmpty());
        Assertions.assertFalse(getMfaTrustEngine().get(record.getPrincipal(), LocalDateTime.now()).isEmpty());
    }
}

