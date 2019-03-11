package org.apereo.cas.support.saml.util;


import SAMLConstants.SAML2_POST_BINDING_URI;
import lombok.val;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlIdPUtils;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;


/**
 * This is {@link SamlIdPUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("FileSystem")
public class SamlIdPUtilsTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyMetadataForAllServices() throws Exception {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        servicesManager.save(service);
        val md = SamlIdPUtils.getMetadataResolverForAllSamlServices(servicesManager, service.getServiceId(), samlRegisteredServiceCachingMetadataResolver);
        Assertions.assertNotNull(md);
        val criteriaSet = new CriteriaSet();
        criteriaSet.add(new org.opensaml.core.criterion.EntityIdCriterion(service.getServiceId()));
        criteriaSet.add(new org.opensaml.saml.criterion.EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        criteriaSet.add(new org.opensaml.saml.criterion.BindingCriterion(CollectionUtils.wrap(SAML2_POST_BINDING_URI)));
        val it = md.resolve(criteriaSet).iterator();
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals(service.getServiceId(), it.next().getEntityID());
    }

    @Test
    public void verifyAssertionConsumerServiceNoIndex() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        servicesManager.save(service);
        val authnRequest = Mockito.mock(AuthnRequest.class);
        Mockito.when(authnRequest.getProtocolBinding()).thenReturn(SAML2_POST_BINDING_URI);
        Mockito.when(authnRequest.getAssertionConsumerServiceIndex()).thenReturn(null);
        Mockito.when(authnRequest.getAssertionConsumerServiceURL()).thenReturn("https://sp.testshib.org/Shibboleth.sso/SAML/POST");
        val acs = SamlIdPUtils.getAssertionConsumerServiceFor(authnRequest, servicesManager, samlRegisteredServiceCachingMetadataResolver);
        Assertions.assertNotNull(acs);
    }

    @Test
    public void verifyAssertionConsumerServiceWithIndex() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        servicesManager.save(service);
        val authnRequest = Mockito.mock(AuthnRequest.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn(service.getServiceId());
        Mockito.when(authnRequest.getIssuer()).thenReturn(issuer);
        Mockito.when(authnRequest.getProtocolBinding()).thenReturn(SAML2_POST_BINDING_URI);
        Mockito.when(authnRequest.getAssertionConsumerServiceIndex()).thenReturn(0);
        val acs = SamlIdPUtils.getAssertionConsumerServiceFor(authnRequest, servicesManager, samlRegisteredServiceCachingMetadataResolver);
        Assertions.assertNotNull(acs);
    }

    @Test
    public void verifyAssertionConsumerServiceWithUrl() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        servicesManager.save(service);
        val authnRequest = Mockito.mock(AuthnRequest.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn(service.getServiceId());
        Mockito.when(authnRequest.getIssuer()).thenReturn(issuer);
        Mockito.when(authnRequest.getProtocolBinding()).thenReturn(SAML2_POST_BINDING_URI);
        val acsUrl = "https://some.acs.url";
        Mockito.when(authnRequest.getAssertionConsumerServiceURL()).thenReturn(acsUrl);
        val adapter = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId());
        val acs = SamlIdPUtils.determineEndpointForRequest(authnRequest, adapter.get(), SAML2_POST_BINDING_URI);
        Assertions.assertNotNull(acs);
        Assertions.assertEquals(acsUrl, acs.getLocation());
    }
}

