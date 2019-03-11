package org.apereo.cas.adaptors.generic.remote;


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
 * @since 5.0.0
 */
public class RemoteAddressCredentialTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "remoteAddressCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeARemoteAddressCredentialToJson() throws IOException {
        val credentialWritten = new RemoteAddressCredential("80.123.456.78");
        RemoteAddressCredentialTests.MAPPER.writeValue(RemoteAddressCredentialTests.JSON_FILE, credentialWritten);
        val credentialRead = RemoteAddressCredentialTests.MAPPER.readValue(RemoteAddressCredentialTests.JSON_FILE, RemoteAddressCredential.class);
        Assertions.assertEquals(credentialWritten, credentialRead);
    }
}

