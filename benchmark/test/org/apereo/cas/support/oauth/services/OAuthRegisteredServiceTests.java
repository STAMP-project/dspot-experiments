package org.apereo.cas.support.oauth.services;


import java.io.File;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.ServiceRegistry;
import org.apereo.cas.services.replication.NoOpRegisteredServiceReplicationStrategy;
import org.apereo.cas.services.resource.DefaultRegisteredServiceResourceNamingStrategy;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class OAuthRegisteredServiceTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "oAuthRegisteredService.json");

    private static final ClassPathResource RESOURCE = new ClassPathResource("services");

    private final ServiceRegistry dao;

    public OAuthRegisteredServiceTests() throws Exception {
        this.dao = new org.apereo.cas.services.JsonServiceRegistry(OAuthRegisteredServiceTests.RESOURCE, false, Mockito.mock(ApplicationEventPublisher.class), new NoOpRegisteredServiceReplicationStrategy(), new DefaultRegisteredServiceResourceNamingStrategy());
    }

    @Test
    public void checkSaveMethod() {
        val r = new OAuthRegisteredService();
        r.setName("checkSaveMethod");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");
        r.setClientId("clientid");
        r.setServiceId("secret");
        r.setBypassApprovalPrompt(true);
        val r2 = this.dao.save(r);
        Assertions.assertTrue((r2 instanceof OAuthRegisteredService));
        this.dao.load();
        val r3 = this.dao.findServiceById(r2.getId());
        Assertions.assertTrue((r3 instanceof OAuthRegisteredService));
        Assertions.assertEquals(r, r2);
        Assertions.assertEquals(r2, r3);
    }

    @Test
    public void verifySerializeAOAuthRegisteredServiceToJson() {
        val serviceWritten = new OAuthRegisteredService();
        serviceWritten.setName("checkSaveMethod");
        serviceWritten.setServiceId("testId");
        serviceWritten.setTheme("theme");
        serviceWritten.setDescription("description");
        serviceWritten.setClientId("clientid");
        serviceWritten.setServiceId("secret");
        serviceWritten.setBypassApprovalPrompt(true);
        serviceWritten.setSupportedGrantTypes(CollectionUtils.wrapHashSet("something"));
        serviceWritten.setSupportedResponseTypes(CollectionUtils.wrapHashSet("something"));
        val serializer = new RegisteredServiceJsonSerializer();
        serializer.to(OAuthRegisteredServiceTests.JSON_FILE, serviceWritten);
        val serviceRead = serializer.from(OAuthRegisteredServiceTests.JSON_FILE);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }
}

