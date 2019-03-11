package org.apereo.cas.pm.rest;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.pm.RestPasswordManagementConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.pm.PasswordChangeBean;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.pm.config.PasswordManagementConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;


/**
 * This is {@link RestPasswordManagementServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RestPasswordManagementConfiguration.class, PasswordManagementConfiguration.class, RestTemplateAutoConfiguration.class, CasCoreUtilConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(locations = { "classpath:/rest-pm.properties" })
@Tag("RestfulApi")
public class RestPasswordManagementServiceTests {
    @Autowired
    @Qualifier("passwordChangeService")
    private PasswordManagementService passwordChangeService;

    @Autowired
    @Qualifier("passwordManagementCipherExecutor")
    private CipherExecutor passwordManagementCipherExecutor;

    @Test
    public void verifyEmailFound() {
        val data = "casuser@example.org";
        try (val webServer = new org.apereo.cas.util.MockWebServer(9090, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val email = this.passwordChangeService.findEmail("casuser");
            webServer.stop();
            Assertions.assertNotNull(email);
            Assertions.assertEquals(data, email);
        }
    }

    @Test
    public void verifySecurityQuestions() {
        val data = "{\"question1\":\"answer1\"}";
        try (val webServer = new org.apereo.cas.util.MockWebServer(9308, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val props = new CasConfigurationProperties();
            val rest = props.getAuthn().getPm().getRest();
            rest.setEndpointUrlChange("http://localhost:9308");
            rest.setEndpointUrlSecurityQuestions("http://localhost:9308");
            rest.setEndpointUrlEmail("http://localhost:9308");
            val passwordService = new RestPasswordManagementService(passwordManagementCipherExecutor, props.getServer().getPrefix(), new RestTemplate(), props.getAuthn().getPm());
            val questions = passwordService.getSecurityQuestions("casuser");
            Assertions.assertFalse(questions.isEmpty());
            Assertions.assertTrue(questions.containsKey("question1"));
            webServer.stop();
        }
    }

    @Test
    public void verifyPasswordChanged() {
        val data = "true";
        try (val webServer = new org.apereo.cas.util.MockWebServer(9309, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val props = new CasConfigurationProperties();
            val rest = props.getAuthn().getPm().getRest();
            rest.setEndpointUrlChange("http://localhost:9309");
            rest.setEndpointUrlSecurityQuestions("http://localhost:9309");
            rest.setEndpointUrlEmail("http://localhost:9309");
            val passwordService = new RestPasswordManagementService(passwordManagementCipherExecutor, props.getServer().getPrefix(), new RestTemplate(), props.getAuthn().getPm());
            val result = passwordService.change(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), new PasswordChangeBean("123456", "123456"));
            Assertions.assertTrue(result);
            webServer.stop();
        }
    }
}

