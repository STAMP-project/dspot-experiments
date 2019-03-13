package org.apereo.cas.digest;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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
public class DigestCredentialTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "digestCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeADigestCredentialToJson() throws IOException {
        val credentialMetaDataWritten = new DigestCredential("uid", "realm", "hash");
        DigestCredentialTests.MAPPER.writeValue(DigestCredentialTests.JSON_FILE, credentialMetaDataWritten);
        val credentialMetaDataRead = DigestCredentialTests.MAPPER.readValue(DigestCredentialTests.JSON_FILE, DigestCredential.class);
        Assertions.assertEquals(credentialMetaDataWritten, credentialMetaDataRead);
    }
}

