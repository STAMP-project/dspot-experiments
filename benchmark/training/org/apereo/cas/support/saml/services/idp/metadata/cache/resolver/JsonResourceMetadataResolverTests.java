package org.apereo.cas.support.saml.services.idp.metadata.cache.resolver;


import java.io.File;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.configuration.model.support.saml.idp.SamlIdPProperties;
import org.apereo.cas.support.saml.services.BaseSamlIdPServicesTests;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JsonResourceMetadataResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("FileSystem")
@TestPropertySource(properties = { "cas.authn.samlIdp.metadata.location=file:/tmp" })
public class JsonResourceMetadataResolverTests extends BaseSamlIdPServicesTests {
    @Test
    public void verifyResolverResolves() throws Exception {
        val props = new SamlIdPProperties();
        props.getMetadata().setLocation(new FileSystemResource(FileUtils.getTempDirectory()));
        FileUtils.copyFile(new ClassPathResource("saml-sp-metadata.json").getFile(), new File(FileUtils.getTempDirectory(), "saml-sp-metadata.json"));
        val service = new SamlRegisteredService();
        val resolver = new JsonResourceMetadataResolver(props, openSamlConfigBean);
        service.setName("Example");
        service.setId(1000);
        service.setServiceId("https://example.org/saml");
        service.setMetadataLocation("json://");
        Assertions.assertTrue(resolver.isAvailable(service));
        Assertions.assertTrue(resolver.supports(service));
        val results = resolver.resolve(service);
        Assertions.assertFalse(results.isEmpty());
        val metadataResolver = results.iterator().next();
        val resolved = metadataResolver.resolveSingle(new net.shibboleth.utilities.java.support.resolver.CriteriaSet(new EntityIdCriterion("https://example.org/saml")));
        Assertions.assertNotNull(resolved);
    }
}

