package org.apereo.cas.trusted.authentication.storage;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecord;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustConfiguration;
import org.apereo.cas.trusted.config.MultifactorAuthnTrustedDeviceFingerprintConfiguration;
import org.apereo.cas.trusted.config.RestMultifactorAuthenticationTrustConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;


/**
 * This is {@link RestMultifactorAuthenticationTrustStorageTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
@SpringBootTest(classes = { RestMultifactorAuthenticationTrustConfiguration.class, MultifactorAuthnTrustedDeviceFingerprintConfiguration.class, MultifactorAuthnTrustConfiguration.class, CasCoreAuditConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = "cas.authn.mfa.trusted.rest.url=http://localhost:9297")
public class RestMultifactorAuthenticationTrustStorageTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("mfaTrustEngine")
    private MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @Autowired
    @Qualifier("mfaTrustCipherExecutor")
    private CipherExecutor<Serializable, String> mfaTrustCipherExecutor;

    @Test
    @SneakyThrows
    public void verifySetAnExpireByKey() {
        val r = MultifactorAuthenticationTrustRecord.newInstance("casuser", "geography", "fingerprint");
        val data = RestMultifactorAuthenticationTrustStorageTests.MAPPER.writeValueAsString(CollectionUtils.wrap(r));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9297, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            mfaTrustEngine.set(r);
            val records = mfaTrustEngine.get("casuser");
            Assertions.assertNotNull(records);
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    @Test
    @SneakyThrows
    public void verifyExpireByDate() {
        val r = MultifactorAuthenticationTrustRecord.newInstance("castest", "geography", "fingerprint");
        r.setRecordDate(LocalDateTime.now().minusDays(2));
        val data = RestMultifactorAuthenticationTrustStorageTests.MAPPER.writeValueAsString(CollectionUtils.wrap(r));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9311, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val props = new CasConfigurationProperties();
            props.getAuthn().getMfa().getTrusted().getRest().setUrl("http://localhost:9311");
            val mfaEngine = new RestMultifactorAuthenticationTrustStorage(new RestTemplate(), props);
            mfaEngine.setCipherExecutor(mfaTrustCipherExecutor);
            mfaEngine.set(r);
            val records = mfaEngine.get(r.getPrincipal());
            Assertions.assertNotNull(records);
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

