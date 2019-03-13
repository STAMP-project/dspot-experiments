package org.apereo.cas.authentication.principal;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class SimplePrincipalTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "simplePrincipal.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    @SneakyThrows
    public void verifySerializeACompletePrincipalToJson() {
        val attributes = new HashMap<String, Object>();
        attributes.put("attribute", "value");
        val principalWritten = new SimplePrincipal("id", attributes);
        SimplePrincipalTests.MAPPER.writeValue(SimplePrincipalTests.JSON_FILE, principalWritten);
        val principalRead = SimplePrincipalTests.MAPPER.readValue(SimplePrincipalTests.JSON_FILE, SimplePrincipal.class);
        Assertions.assertEquals(principalWritten, principalRead);
    }

    @Test
    @SneakyThrows
    public void verifySerializeAPrincipalWithEmptyAttributesToJson() {
        val principalWritten = new SimplePrincipal("id", new HashMap(0));
        SimplePrincipalTests.MAPPER.writeValue(SimplePrincipalTests.JSON_FILE, principalWritten);
        val principalRead = SimplePrincipalTests.MAPPER.readValue(SimplePrincipalTests.JSON_FILE, SimplePrincipal.class);
        Assertions.assertEquals(principalWritten, principalRead);
    }
}

