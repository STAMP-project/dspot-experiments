package org.apereo.cas.services;


import CaseCanonicalizationMode.UPPER;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class DefaultRegisteredServiceUsernameProviderTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "defaultRegisteredServiceUsernameProvider.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifyRegServiceUsernameUpper() {
        val provider = new DefaultRegisteredServiceUsernameProvider();
        provider.setCanonicalizationMode(UPPER.name());
        val principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getId()).thenReturn("id");
        val id = provider.resolveUsername(principal, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService("usernameAttributeProviderService"));
        Assertions.assertEquals(id, principal.getId().toUpperCase());
    }

    @Test
    public void verifyRegServiceUsername() {
        val provider = new DefaultRegisteredServiceUsernameProvider();
        val principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getId()).thenReturn("id");
        val id = provider.resolveUsername(principal, RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService("id"));
        Assertions.assertEquals(id, principal.getId());
    }

    @Test
    public void verifyEquality() {
        val provider = new DefaultRegisteredServiceUsernameProvider();
        val provider2 = new DefaultRegisteredServiceUsernameProvider();
        Assertions.assertEquals(provider, provider2);
    }

    @Test
    public void verifySerializeADefaultRegisteredServiceUsernameProviderToJson() throws IOException {
        val providerWritten = new DefaultRegisteredServiceUsernameProvider();
        DefaultRegisteredServiceUsernameProviderTests.MAPPER.writeValue(DefaultRegisteredServiceUsernameProviderTests.JSON_FILE, providerWritten);
        val providerRead = DefaultRegisteredServiceUsernameProviderTests.MAPPER.readValue(DefaultRegisteredServiceUsernameProviderTests.JSON_FILE, DefaultRegisteredServiceUsernameProvider.class);
        Assertions.assertEquals(providerWritten, providerRead);
    }
}

