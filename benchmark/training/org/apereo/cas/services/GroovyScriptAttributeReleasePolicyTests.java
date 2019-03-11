package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Tag("Groovy")
public class GroovyScriptAttributeReleasePolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "groovyScriptAttributeReleasePolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifySerializeAGroovyScriptAttributeReleasePolicyToJson() throws IOException {
        val policyWritten = new GroovyScriptAttributeReleasePolicy();
        GroovyScriptAttributeReleasePolicyTests.MAPPER.writeValue(GroovyScriptAttributeReleasePolicyTests.JSON_FILE, policyWritten);
        val policyRead = GroovyScriptAttributeReleasePolicyTests.MAPPER.readValue(GroovyScriptAttributeReleasePolicyTests.JSON_FILE, GroovyScriptAttributeReleasePolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }

    @Test
    public void verifyAction() {
        val policy = new GroovyScriptAttributeReleasePolicy();
        policy.setGroovyScript("classpath:GroovyAttributeRelease.groovy");
        val attributes = policy.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertTrue(attributes.containsKey("username"));
        Assertions.assertTrue(attributes.containsKey("likes"));
        Assertions.assertTrue(attributes.containsKey("id"));
        Assertions.assertTrue(attributes.containsKey("another"));
    }
}

