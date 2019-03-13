package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is test cases for
 * {@link GroovyRegisteredServiceAccessStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
public class GroovyRegisteredServiceAccessStrategyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "GroovyRegisteredServiceAccessStrategyTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void checkDefaultAuthzStrategyConfig() {
        val authz = new GroovyRegisteredServiceAccessStrategy();
        authz.setGroovyScript("classpath:accessstrategy.groovy");
        Assertions.assertTrue(authz.isServiceAccessAllowed());
        Assertions.assertTrue(authz.isServiceAccessAllowedForSso());
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess("test", new HashMap()));
        Assertions.assertNull(authz.getUnauthorizedRedirectUrl());
        Assertions.assertNotNull(authz.getDelegatedAuthenticationPolicy());
    }

    @Test
    public void verifySerializationToJson() throws IOException {
        val authz = new GroovyRegisteredServiceAccessStrategy();
        authz.setGroovyScript("classpath:accessstrategy.groovy");
        GroovyRegisteredServiceAccessStrategyTests.MAPPER.writeValue(GroovyRegisteredServiceAccessStrategyTests.JSON_FILE, authz);
        val strategyRead = GroovyRegisteredServiceAccessStrategyTests.MAPPER.readValue(GroovyRegisteredServiceAccessStrategyTests.JSON_FILE, GroovyRegisteredServiceAccessStrategy.class);
        Assertions.assertEquals(authz, strategyRead);
    }
}

