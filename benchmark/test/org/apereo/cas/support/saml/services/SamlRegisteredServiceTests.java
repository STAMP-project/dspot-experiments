package org.apereo.cas.support.saml.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.services.ChainingAttributeReleasePolicy;
import org.apereo.cas.services.DenyAllAttributeReleasePolicy;
import org.apereo.cas.services.replication.NoOpRegisteredServiceReplicationStrategy;
import org.apereo.cas.services.resource.DefaultRegisteredServiceResourceNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;


/**
 * The {@link SamlRegisteredServiceTests} handles test cases for {@link SamlRegisteredService}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SamlRegisteredServiceTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "samlRegisteredService.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ClassPathResource RESOURCE = new ClassPathResource("services");

    private static final String SAML_SERVICE = "SAMLService";

    private static final String METADATA_LOCATION = "classpath:/metadata/idp-metadata.xml";

    @Test
    public void verifySavingSamlService() throws Exception {
        val service = new SamlRegisteredService();
        service.setName(SamlRegisteredServiceTests.SAML_SERVICE);
        service.setServiceId("http://mmoayyed.unicon.net");
        service.setMetadataLocation(SamlRegisteredServiceTests.METADATA_LOCATION);
        val dao = new org.apereo.cas.services.JsonServiceRegistry(SamlRegisteredServiceTests.RESOURCE, false, Mockito.mock(ApplicationEventPublisher.class), new NoOpRegisteredServiceReplicationStrategy(), new DefaultRegisteredServiceResourceNamingStrategy());
        dao.save(service);
        dao.load();
    }

    @Test
    public void verifySavingInCommonSamlService() throws Exception {
        val service = new SamlRegisteredService();
        service.setName(SamlRegisteredServiceTests.SAML_SERVICE);
        service.setServiceId("http://mmoayyed.unicon.net");
        service.setMetadataLocation(SamlRegisteredServiceTests.METADATA_LOCATION);
        val policy = new InCommonRSAttributeReleasePolicy();
        val chain = new ChainingAttributeReleasePolicy();
        chain.setPolicies(Arrays.asList(policy, new DenyAllAttributeReleasePolicy()));
        service.setAttributeReleasePolicy(chain);
        val dao = new org.apereo.cas.services.JsonServiceRegistry(SamlRegisteredServiceTests.RESOURCE, false, Mockito.mock(ApplicationEventPublisher.class), new NoOpRegisteredServiceReplicationStrategy(), new DefaultRegisteredServiceResourceNamingStrategy());
        dao.save(service);
        dao.load();
    }

    @Test
    public void checkPattern() {
        val service = new SamlRegisteredService();
        service.setName(SamlRegisteredServiceTests.SAML_SERVICE);
        service.setServiceId("^http://.+");
        service.setMetadataLocation(SamlRegisteredServiceTests.METADATA_LOCATION);
        val dao = new org.apereo.cas.services.InMemoryServiceRegistry(Mockito.mock(ApplicationEventPublisher.class), Collections.singletonList(service));
        val impl = new org.apereo.cas.services.DefaultServicesManager(dao, Mockito.mock(ApplicationEventPublisher.class), new HashSet());
        impl.load();
        val s = impl.findServiceBy(new WebApplicationServiceFactory().createService("http://mmoayyed.unicon.net:8081/sp/saml/SSO"));
        Assertions.assertNotNull(s);
    }

    @Test
    public void verifySerializeAReturnMappedAttributeReleasePolicyToJson() throws IOException {
        val serviceWritten = new SamlRegisteredService();
        serviceWritten.setName(SamlRegisteredServiceTests.SAML_SERVICE);
        serviceWritten.setServiceId("http://mmoayyed.unicon.net");
        serviceWritten.setMetadataLocation(SamlRegisteredServiceTests.METADATA_LOCATION);
        SamlRegisteredServiceTests.MAPPER.writeValue(SamlRegisteredServiceTests.JSON_FILE, serviceWritten);
        val serviceRead = SamlRegisteredServiceTests.MAPPER.readValue(SamlRegisteredServiceTests.JSON_FILE, SamlRegisteredService.class);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }
}

