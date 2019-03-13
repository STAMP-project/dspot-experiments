package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GroovyRegisteredServiceUsernameProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Tag("Groovy")
public class GroovyRegisteredServiceUsernameProviderTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "GroovyRegisteredServiceUsernameProviderTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyUsernameProvider() {
        val p = new GroovyRegisteredServiceUsernameProvider();
        p.setGroovyScript("file:src/test/resources/uid.groovy");
        val id = p.resolveUsername(RegisteredServiceTestUtils.getPrincipal(), RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService());
        Assertions.assertEquals("test", id);
    }

    @Test
    public void verifyUsernameProviderInline() {
        val p = new GroovyRegisteredServiceUsernameProvider();
        p.setGroovyScript("groovy { return attributes['uid'] + '123456789' }");
        var id = p.resolveUsername(RegisteredServiceTestUtils.getPrincipal("casuser", CollectionUtils.wrap("uid", "CAS-System")), RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService());
        Assertions.assertEquals("CAS-System123456789", id);
    }

    @Test
    public void verifySerializationToJson() throws IOException {
        val p = new GroovyRegisteredServiceUsernameProvider();
        p.setGroovyScript("groovy { return 'something' }");
        p.setEncryptUsername(true);
        p.setCanonicalizationMode("NONE");
        GroovyRegisteredServiceUsernameProviderTests.MAPPER.writeValue(GroovyRegisteredServiceUsernameProviderTests.JSON_FILE, p);
        val repositoryRead = GroovyRegisteredServiceUsernameProviderTests.MAPPER.readValue(GroovyRegisteredServiceUsernameProviderTests.JSON_FILE, GroovyRegisteredServiceUsernameProvider.class);
        Assertions.assertEquals(p, repositoryRead);
    }
}

