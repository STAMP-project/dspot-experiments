package org.apereo.cas.oidc.discovery.webfinger;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.configuration.support.RestEndpointProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


/**
 * This is {@link OidcRestfulWebFingerUserInfoRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class OidcRestfulWebFingerUserInfoRepositoryTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules().configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private MockWebServer webServer;

    @Test
    public void verifyFindByEmail() throws Exception {
        var data = OidcRestfulWebFingerUserInfoRepositoryTests.MAPPER.writeValueAsString(CollectionUtils.wrap("email", "cas@example.org"));
        try (val webServer = new MockWebServer(9312, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            this.webServer = webServer;
            this.webServer.start();
            Assertions.assertTrue(this.webServer.isRunning());
            val props = new RestEndpointProperties();
            props.setUrl("http://localhost:9312");
            val repo = new org.apereo.cas.oidc.discovery.webfinger.userinfo.OidcRestfulWebFingerUserInfoRepository(props);
            val results = repo.findByEmailAddress("cas@example.org");
            Assertions.assertNotNull(results);
            Assertions.assertTrue(results.containsKey("email"));
            Assertions.assertEquals("cas@example.org", results.get("email"));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    @Test
    public void verifyFindByUsername() throws Exception {
        var data = OidcRestfulWebFingerUserInfoRepositoryTests.MAPPER.writeValueAsString(CollectionUtils.wrap("username", "casuser"));
        try (val webServer = new MockWebServer(9312, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            this.webServer = webServer;
            this.webServer.start();
            Assertions.assertTrue(this.webServer.isRunning());
            val props = new RestEndpointProperties();
            props.setUrl("http://localhost:9312");
            val repo = new org.apereo.cas.oidc.discovery.webfinger.userinfo.OidcRestfulWebFingerUserInfoRepository(props);
            val results = repo.findByUsername("casuser");
            Assertions.assertNotNull(results);
            Assertions.assertTrue(results.containsKey("username"));
            Assertions.assertEquals("casuser", results.get("username"));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

