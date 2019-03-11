package org.apereo.cas.services;


import RegisteredServiceMultifactorPolicy.FailureModes.OPEN;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GroovyRegisteredServiceMultifactorPolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
public class GroovyRegisteredServiceMultifactorPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "GroovyRegisteredServiceMultifactorPolicyTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void checkDefaultPolicyConfig() {
        val authz = new GroovyRegisteredServiceMultifactorPolicy();
        authz.setGroovyScript("classpath:mfapolicy.groovy");
        Assertions.assertEquals(OPEN, authz.getFailureMode());
        Assertions.assertEquals("Test", authz.getPrincipalAttributeNameTrigger());
        Assertions.assertEquals("TestMatch", authz.getPrincipalAttributeValueToMatch());
        Assertions.assertTrue(authz.getMultifactorAuthenticationProviders().contains("mfa-something"));
        Assertions.assertTrue(authz.isBypassEnabled());
    }

    @Test
    public void verifySerializationToJson() throws IOException {
        val authz = new GroovyRegisteredServiceMultifactorPolicy();
        authz.setGroovyScript("classpath:mfapolicy.groovy");
        GroovyRegisteredServiceMultifactorPolicyTests.MAPPER.writeValue(GroovyRegisteredServiceMultifactorPolicyTests.JSON_FILE, authz);
        val strategyRead = GroovyRegisteredServiceMultifactorPolicyTests.MAPPER.readValue(GroovyRegisteredServiceMultifactorPolicyTests.JSON_FILE, GroovyRegisteredServiceMultifactorPolicy.class);
        Assertions.assertEquals(authz, strategyRead);
        Assertions.assertEquals("Test", strategyRead.getPrincipalAttributeNameTrigger());
    }
}

