package org.apereo.cas.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test for JSON Serialization
 *
 * @author David Rodriguez
 * @since 5.0.0
 */
public class DefaultAuthenticationTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "defaultAuthentication.json");

    private ObjectMapper mapper;

    @Test
    @SneakyThrows
    public void verifySerializeADefaultAuthenticationToJson() {
        val serviceWritten = CoreAuthenticationTestUtils.getAuthentication();
        mapper.writeValue(DefaultAuthenticationTests.JSON_FILE, serviceWritten);
        val serviceRead = mapper.readValue(DefaultAuthenticationTests.JSON_FILE, Authentication.class);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }
}

