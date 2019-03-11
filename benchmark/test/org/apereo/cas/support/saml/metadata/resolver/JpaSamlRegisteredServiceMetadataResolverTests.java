package org.apereo.cas.support.saml.metadata.resolver;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apereo.cas.support.saml.BaseJpaSamlMetadataTests;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlMetadataDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JpaSamlRegisteredServiceMetadataResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@TestPropertySource(properties = "cas.authn.samlIdp.metadata.location=file:/tmp")
public class JpaSamlRegisteredServiceMetadataResolverTests extends BaseJpaSamlMetadataTests {
    @Test
    public void verifyResolver() throws Exception {
        val res = new ClassPathResource("samlsp-metadata.xml");
        val md = new SamlMetadataDocument();
        md.setName("SP");
        md.setValue(IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8));
        resolver.saveOrUpdate(md);
        val service = new SamlRegisteredService();
        service.setName("SAML Service");
        service.setServiceId("https://carmenwiki.osu.edu/shibboleth");
        service.setDescription("Testing");
        service.setMetadataLocation("jdbc://");
        Assertions.assertTrue(resolver.supports(service));
        val resolvers = resolver.resolve(service);
        Assertions.assertTrue(((resolvers.size()) == 1));
    }
}

