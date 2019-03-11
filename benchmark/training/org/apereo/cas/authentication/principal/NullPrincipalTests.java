package org.apereo.cas.authentication.principal;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class NullPrincipalTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "nullPrincipal.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @SneakyThrows
    public void verifySerializeANullPrincipalToJson() {
        val serviceWritten = NullPrincipal.getInstance();
        NullPrincipalTests.MAPPER.writeValue(NullPrincipalTests.JSON_FILE, serviceWritten);
        val serviceRead = NullPrincipalTests.MAPPER.readValue(NullPrincipalTests.JSON_FILE, NullPrincipal.class);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }
}

