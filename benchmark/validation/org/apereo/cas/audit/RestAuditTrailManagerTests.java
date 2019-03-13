package org.apereo.cas.audit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.audit.spi.BaseAuditConfigurationTests;
import org.apereo.cas.config.CasCoreUtilSerializationConfiguration;
import org.apereo.cas.config.CasSupportRestAuditConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.inspektr.audit.AuditActionContext;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link RestAuditTrailManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasSupportRestAuditConfiguration.class, CasCoreUtilSerializationConfiguration.class })
@Tag("RestfulApi")
@Slf4j
@TestPropertySource(properties = { "cas.audit.rest.url=http://localhost:9296", "cas.audit.rest.asynchronous=false" })
@Getter
public class RestAuditTrailManagerTests extends BaseAuditConfigurationTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String DATA;

    static {
        val audit = new AuditActionContext("casuser", "resource", "action", "CAS", new Date(), "123.456.789.000", "123.456.789.000");
        try {
            DATA = RestAuditTrailManagerTests.MAPPER.writeValueAsString(CollectionUtils.wrapSet(audit));
            LOGGER.debug("DATA: [{}]", RestAuditTrailManagerTests.DATA);
        } catch (final JsonProcessingException e) {
            throw new AssertionError(e);
        }
    }

    @Autowired
    @Qualifier("restAuditTrailManager")
    private AuditTrailManager auditTrailManager;

    @Test
    @Override
    public void verifyAuditManager() {
        try (val webServer = new org.apereo.cas.util.MockWebServer(9296, new ByteArrayResource(RestAuditTrailManagerTests.DATA.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            Assertions.assertTrue(webServer.isRunning());
            super.verifyAuditManager();
        }
    }
}

