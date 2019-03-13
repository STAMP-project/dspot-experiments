package org.apereo.cas.authentication;


import CoreAuthenticationTestUtils.CONST_GOOD_URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.credential.HttpBasedServiceCredential;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class HttpBasedServiceCredentialTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "httpBasedServiceCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String CNN_URL = "http://www.cnn.com";

    private static final String SOME_APP_URL = "https://some.app.edu";

    @Test
    public void verifyProperUrl() {
        Assertions.assertEquals(CONST_GOOD_URL, CoreAuthenticationTestUtils.getHttpBasedServiceCredentials().getCallbackUrl().toExternalForm());
    }

    @Test
    public void verifyEqualsWithNull() throws Exception {
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService(HttpBasedServiceCredentialTests.SOME_APP_URL);
        val c = new HttpBasedServiceCredential(new URL(HttpBasedServiceCredentialTests.CNN_URL), registeredService);
        Assertions.assertNotEquals(c, null);
    }

    @Test
    public void verifyEqualsWithFalse() throws Exception {
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService(HttpBasedServiceCredentialTests.SOME_APP_URL);
        val c = new HttpBasedServiceCredential(new URL(HttpBasedServiceCredentialTests.CNN_URL), registeredService);
        val c2 = new HttpBasedServiceCredential(new URL("http://www.msn.com"), registeredService);
        Assertions.assertFalse(c.equals(c2));
        Assertions.assertFalse(c.equals(new Object()));
    }

    @Test
    public void verifyEqualsWithTrue() throws Exception {
        val registeredService = RegisteredServiceTestUtils.getRegisteredService(HttpBasedServiceCredentialTests.SOME_APP_URL);
        val callbackUrl = new URL(HttpBasedServiceCredentialTests.CNN_URL);
        val c = new HttpBasedServiceCredential(callbackUrl, registeredService);
        val c2 = new HttpBasedServiceCredential(callbackUrl, registeredService);
        Assertions.assertTrue(c.equals(c2));
        Assertions.assertTrue(c2.equals(c));
    }

    @Test
    public void verifySerializeAnHttpBasedServiceCredentialToJson() throws IOException {
        val credentialMetaDataWritten = new HttpBasedServiceCredential(new URL(HttpBasedServiceCredentialTests.CNN_URL), RegisteredServiceTestUtils.getRegisteredService(HttpBasedServiceCredentialTests.SOME_APP_URL));
        HttpBasedServiceCredentialTests.MAPPER.writeValue(HttpBasedServiceCredentialTests.JSON_FILE, credentialMetaDataWritten);
        val credentialMetaDataRead = HttpBasedServiceCredentialTests.MAPPER.readValue(HttpBasedServiceCredentialTests.JSON_FILE, HttpBasedServiceCredential.class);
        Assertions.assertEquals(credentialMetaDataWritten, credentialMetaDataRead);
    }
}

