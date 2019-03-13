package org.apereo.cas.authentication.adaptive.intel;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.core.authentication.AdaptiveAuthenticationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link BlackDotIPAddressIntelligenceServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
// @Test
// public void verifyAllowedOperation() {
// val props = new AdaptiveAuthenticationProperties();
// props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
// val service = new BlackDotIPAddressIntelligenceService(props);
// val response = service.examine(new MockRequestContext(), "8.8.8.8");
// assertTrue(response.isAllowed());
// }
@Tag("RestfulApi")
public class BlackDotIPAddressIntelligenceServiceTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyBannedOperation() {
        try (val webServer = new org.apereo.cas.util.MockWebServer(9355, new ByteArrayResource(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.TOO_MANY_REQUESTS)) {
            webServer.start();
            val props = new AdaptiveAuthenticationProperties();
            props.getIpIntel().getBlackDot().setUrl("http://localhost:9355?ip=%s");
            props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
            val service = new BlackDotIPAddressIntelligenceService(props);
            val response = service.examine(new MockRequestContext(), "37.58.59.181");
            Assertions.assertTrue(response.isBanned());
        }
    }

    @Test
    public void verifyErrorStatusOperation() throws Exception {
        val data = BlackDotIPAddressIntelligenceServiceTests.MAPPER.writeValueAsString(Collections.singletonMap("status", "error"));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9356, new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.OK)) {
            webServer.start();
            val props = new AdaptiveAuthenticationProperties();
            props.getIpIntel().getBlackDot().setUrl("http://localhost:9356?ip=%s");
            props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
            val service = new BlackDotIPAddressIntelligenceService(props);
            val response = service.examine(new MockRequestContext(), "37.58.59.181");
            Assertions.assertTrue(response.isBanned());
        }
    }

    @Test
    public void verifySuccessStatusAndBannedWithRank() throws Exception {
        val data = BlackDotIPAddressIntelligenceServiceTests.MAPPER.writeValueAsString(CollectionUtils.wrap("status", "success", "result", 1));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9357, new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.OK)) {
            webServer.start();
            val props = new AdaptiveAuthenticationProperties();
            props.getIpIntel().getBlackDot().setUrl("http://localhost:9357?ip=%s");
            props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
            val service = new BlackDotIPAddressIntelligenceService(props);
            val response = service.examine(new MockRequestContext(), "37.58.59.181");
            Assertions.assertTrue(response.isBanned());
        }
    }

    @Test
    public void verifySuccessStatus() throws Exception {
        val data = BlackDotIPAddressIntelligenceServiceTests.MAPPER.writeValueAsString(CollectionUtils.wrap("status", "success", "result", 0));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9358, new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.OK)) {
            webServer.start();
            val props = new AdaptiveAuthenticationProperties();
            props.getIpIntel().getBlackDot().setUrl("http://localhost:9358?ip=%s");
            props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
            val service = new BlackDotIPAddressIntelligenceService(props);
            val response = service.examine(new MockRequestContext(), "37.58.59.181");
            Assertions.assertFalse(response.isBanned());
        }
    }

    @Test
    public void verifySuccessStatusRanked() throws Exception {
        val data = BlackDotIPAddressIntelligenceServiceTests.MAPPER.writeValueAsString(CollectionUtils.wrap("status", "success", "result", 0.4351));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9359, new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.OK)) {
            webServer.start();
            val props = new AdaptiveAuthenticationProperties();
            props.getIpIntel().getBlackDot().setUrl("http://localhost:9359?ip=%s");
            props.getIpIntel().getBlackDot().setEmailAddress("cas@apereo.org");
            val service = new BlackDotIPAddressIntelligenceService(props);
            val response = service.examine(new MockRequestContext(), "37.58.59.181");
            Assertions.assertFalse(response.isBanned());
            Assertions.assertEquals(0.4351, response.getScore());
        }
    }
}

