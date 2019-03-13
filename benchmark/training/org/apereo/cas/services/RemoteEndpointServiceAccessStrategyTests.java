package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class RemoteEndpointServiceAccessStrategyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "remoteEndpointServiceAccessStrategy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeAX509CertificateCredentialToJson() throws IOException {
        val strategyWritten = new RemoteEndpointServiceAccessStrategy();
        RemoteEndpointServiceAccessStrategyTests.MAPPER.writeValue(RemoteEndpointServiceAccessStrategyTests.JSON_FILE, strategyWritten);
        val credentialRead = RemoteEndpointServiceAccessStrategyTests.MAPPER.readValue(RemoteEndpointServiceAccessStrategyTests.JSON_FILE, RemoteEndpointServiceAccessStrategy.class);
        Assertions.assertEquals(strategyWritten, credentialRead);
    }

    @Test
    public void verifyOperation() {
        val strategy = new RemoteEndpointServiceAccessStrategy();
        strategy.setEndpointUrl("http://localhost:8755");
        strategy.setAcceptableResponseCodes("200,201");
        try (val webServer = new org.apereo.cas.util.MockWebServer(8755, new ByteArrayResource("OK".getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            Assertions.assertTrue(strategy.doPrincipalAttributesAllowServiceAccess("casuser", CoreAuthenticationTestUtils.getAttributes()));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

