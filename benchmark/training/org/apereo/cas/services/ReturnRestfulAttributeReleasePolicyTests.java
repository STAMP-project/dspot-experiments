package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


/**
 * This is {@link ReturnRestfulAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ReturnRestfulAttributeReleasePolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "verifySerializeAReturnAllowedAttributeReleasePolicyToJson.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifySerializeAttributeReleasePolicyToJson() throws IOException {
        val policyWritten = new ReturnRestfulAttributeReleasePolicy("http://endpoint.example.org");
        ReturnRestfulAttributeReleasePolicyTests.MAPPER.writeValue(ReturnRestfulAttributeReleasePolicyTests.JSON_FILE, policyWritten);
        val policyRead = ReturnRestfulAttributeReleasePolicyTests.MAPPER.readValue(ReturnRestfulAttributeReleasePolicyTests.JSON_FILE, ReturnRestfulAttributeReleasePolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }

    @Test
    public void verifyPolicy() throws IOException {
        val data = ReturnRestfulAttributeReleasePolicyTests.MAPPER.writeValueAsString(CollectionUtils.wrap("givenName", "CASUSER", "familyName", "CAS"));
        try (val webServer = new org.apereo.cas.util.MockWebServer(9299, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val policyWritten = new ReturnRestfulAttributeReleasePolicy("http://localhost:9299");
            val attributes = policyWritten.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
            Assertions.assertFalse(attributes.isEmpty());
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

