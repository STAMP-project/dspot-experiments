package org.apereo.cas.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class OneTimePasswordCredentialTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "oneTimePasswordCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    @SneakyThrows
    public void verifySerializeAnOneTimePasswordCredentialToJson() {
        val credentialWritten = new OneTimePasswordCredential("id", "password");
        OneTimePasswordCredentialTests.MAPPER.writeValue(OneTimePasswordCredentialTests.JSON_FILE, credentialWritten);
        val credentialRead = OneTimePasswordCredentialTests.MAPPER.readValue(OneTimePasswordCredentialTests.JSON_FILE, OneTimePasswordCredential.class);
        Assertions.assertEquals(credentialWritten, credentialRead);
    }
}

