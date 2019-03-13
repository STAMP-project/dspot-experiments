package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class AnonymousRegisteredServiceUsernameAttributeProviderTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "anonymousRegisteredServiceUsernameAttributeProvider.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CASROX = "casrox";

    @Test
    public void verifyPrincipalResolution() {
        val provider = new AnonymousRegisteredServiceUsernameAttributeProvider(new ShibbolethCompatiblePersistentIdGenerator(AnonymousRegisteredServiceUsernameAttributeProviderTests.CASROX));
        val service = Mockito.mock(Service.class);
        Mockito.when(service.getId()).thenReturn("id");
        val principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getId()).thenReturn("uid");
        val id = provider.resolveUsername(principal, service, RegisteredServiceTestUtils.getRegisteredService("id"));
        Assertions.assertNotNull(id);
    }

    @Test
    public void verifyEquality() {
        val provider = new AnonymousRegisteredServiceUsernameAttributeProvider(new ShibbolethCompatiblePersistentIdGenerator(AnonymousRegisteredServiceUsernameAttributeProviderTests.CASROX));
        val provider2 = new AnonymousRegisteredServiceUsernameAttributeProvider(new ShibbolethCompatiblePersistentIdGenerator(AnonymousRegisteredServiceUsernameAttributeProviderTests.CASROX));
        Assertions.assertEquals(provider, provider2);
    }

    @Test
    public void verifySerializeADefaultRegisteredServiceUsernameProviderToJson() throws IOException {
        val providerWritten = new AnonymousRegisteredServiceUsernameAttributeProvider(new ShibbolethCompatiblePersistentIdGenerator(AnonymousRegisteredServiceUsernameAttributeProviderTests.CASROX));
        AnonymousRegisteredServiceUsernameAttributeProviderTests.MAPPER.writeValue(AnonymousRegisteredServiceUsernameAttributeProviderTests.JSON_FILE, providerWritten);
        val providerRead = AnonymousRegisteredServiceUsernameAttributeProviderTests.MAPPER.readValue(AnonymousRegisteredServiceUsernameAttributeProviderTests.JSON_FILE, AnonymousRegisteredServiceUsernameAttributeProvider.class);
        Assertions.assertEquals(providerWritten, providerRead);
    }

    @Test
    public void verifyGeneratedIdsMatch() {
        val salt = "nJ+G!VgGt=E2xCJp@Kb+qjEjE4R2db7NEW!9ofjMNas2Tq3h5h!nCJxc3Sr#kv=7JwU?#MN=7e+r!wpcMw5RF42G8J" + ("8tNkGp4g4rFZ#RnNECL@wZX5=yia+KPEwwq#CA9EM38=ZkjK2mzv6oczCVC!m8k!=6@!MW@xTMYH8eSV@7yc24Bz6NUstzbTWH3pnGojZm7pW8N" + "wjLypvZKqhn7agai295kFBhMmpS\n9Jz9+jhVkJfFjA32GiTkZ5hvYiFG104xWnMbHk7TsGrfw%tvACAs=f3C");
        val gen = new ShibbolethCompatiblePersistentIdGenerator(salt);
        gen.setAttribute("employeeId");
        val provider = new AnonymousRegisteredServiceUsernameAttributeProvider(gen);
        val result = provider.resolveUsername(CoreAuthenticationTestUtils.getPrincipal("anyuser", CollectionUtils.wrap("employeeId", "T911327")), CoreAuthenticationTestUtils.getService("https://cas.example.org/app"), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertEquals("ujWTRNKPPso8S+4geOvcOZtv778=", result);
    }

    @Test
    public void verifyGeneratedIdsMatchMultiValuedAttribute() {
        val salt = "whydontyoustringmealong";
        val gen = new ShibbolethCompatiblePersistentIdGenerator(salt);
        gen.setAttribute("uid");
        val provider = new AnonymousRegisteredServiceUsernameAttributeProvider(gen);
        val result = provider.resolveUsername(CoreAuthenticationTestUtils.getPrincipal("anyuser", CollectionUtils.wrap("uid", CollectionUtils.wrap("obegon"))), CoreAuthenticationTestUtils.getService("https://sp.testshib.org/shibboleth-sp"), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertEquals("lykoGRE9QbbrsEBlHJVEz0U8AJ0=", result);
    }
}

