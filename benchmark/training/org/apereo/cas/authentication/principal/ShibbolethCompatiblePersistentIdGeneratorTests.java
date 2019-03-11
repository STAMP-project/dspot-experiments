package org.apereo.cas.authentication.principal;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class ShibbolethCompatiblePersistentIdGeneratorTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "shibbolethCompatiblePersistentIdGenerator.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifyGenerator() {
        val generator = new ShibbolethCompatiblePersistentIdGenerator("scottssalt");
        val p = Mockito.mock(Principal.class);
        Mockito.when(p.getId()).thenReturn("testuser");
        val value = generator.generate(p, RegisteredServiceTestUtils.getService());
        Assertions.assertNotNull(value);
    }

    @Test
    public void realTestOfGeneratorThatVerifiesValueReturned() {
        val generator = new ShibbolethCompatiblePersistentIdGenerator("thisisasalt");
        val p = Mockito.mock(Principal.class);
        Mockito.when(p.getId()).thenReturn("grudkin");
        val s = Mockito.mock(Service.class);
        Mockito.when(s.getId()).thenReturn("https://shibboleth.irbmanager.com/");
        val value = generator.generate(p, s);
        Assertions.assertEquals("jvZO/wYedArYIEIORGdHoMO4qkw=", value);
    }

    @Test
    public void verifySerializeAShibbolethCompatiblePersistentIdGeneratorToJson() throws IOException {
        val generatorWritten = new ShibbolethCompatiblePersistentIdGenerator("scottssalt");
        ShibbolethCompatiblePersistentIdGeneratorTests.MAPPER.writeValue(ShibbolethCompatiblePersistentIdGeneratorTests.JSON_FILE, generatorWritten);
        val credentialRead = ShibbolethCompatiblePersistentIdGeneratorTests.MAPPER.readValue(ShibbolethCompatiblePersistentIdGeneratorTests.JSON_FILE, ShibbolethCompatiblePersistentIdGenerator.class);
        Assertions.assertEquals(generatorWritten, credentialRead);
    }
}

