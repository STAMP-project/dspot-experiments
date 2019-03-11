package org.apereo.cas.util;


import lombok.val;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.apereo.cas.configuration.model.support.saml.sps.SamlServiceProviderProperties;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.cache.SamlRegisteredServiceCachingMetadataResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link SamlSPUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("FileSystem")
@TestPropertySource(properties = { "cas.authn.samlIdp.metadata.location=file:/tmp" })
public class SamlSPUtilsTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyNewSamlServiceProvider() throws Exception {
        val entity = Mockito.mock(EntityDescriptor.class);
        Mockito.when(entity.getEntityID()).thenReturn("https://dropbox.com");
        val resolver = Mockito.mock(SamlRegisteredServiceCachingMetadataResolver.class);
        val metadata = Mockito.mock(MetadataResolver.class);
        Mockito.when(metadata.resolveSingle(ArgumentMatchers.any(CriteriaSet.class))).thenReturn(entity);
        Mockito.when(resolver.resolve(ArgumentMatchers.any(SamlRegisteredService.class))).thenReturn(metadata);
        val sp = new SamlServiceProviderProperties.Dropbox();
        sp.setMetadata("https://metadata.dropbox.com");
        sp.setEntityIds(CollectionUtils.wrap(entity.getEntityID()));
        val service = SamlSPUtils.newSamlServiceProviderService(sp, resolver);
        Assertions.assertNotNull(service);
        Assertions.assertEquals(entity.getEntityID(), service.getServiceId());
    }
}

