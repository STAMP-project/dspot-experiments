package org.apereo.cas.adaptors.trusted.authentication.principal;


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
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class PrincipalBearingCredentialsTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "principalBearingCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PrincipalBearingCredential principalBearingCredentials;

    @Test
    public void verifyGetOfPrincipal() {
        Assertions.assertEquals("test", this.principalBearingCredentials.getPrincipal().getId());
    }

    @Test
    public void verifySerializeAPrincipalBearingCredentialToJson() throws IOException {
        PrincipalBearingCredentialsTests.MAPPER.writeValue(PrincipalBearingCredentialsTests.JSON_FILE, principalBearingCredentials);
        val credentialRead = PrincipalBearingCredentialsTests.MAPPER.readValue(PrincipalBearingCredentialsTests.JSON_FILE, PrincipalBearingCredential.class);
        Assertions.assertEquals(principalBearingCredentials, credentialRead);
    }
}

