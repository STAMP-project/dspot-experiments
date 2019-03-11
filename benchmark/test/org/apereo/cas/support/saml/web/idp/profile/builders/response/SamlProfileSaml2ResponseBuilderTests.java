package org.apereo.cas.support.saml.web.idp.profile.builders.response;


import NameID.ENCRYPTED;
import SAMLConstants.SAML2_POST_BINDING_URI;
import lombok.val;
import org.apache.xerces.xs.XSObject;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensaml.messaging.context.MessageContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link SamlProfileSaml2ResponseBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("FileSystem")
public class SamlProfileSaml2ResponseBuilderTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifySamlResponseAllSigned() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib(true, true);
        service.getAttributeValueTypes().put("permissions", XSObject.class.getSimpleName());
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val assertion = BaseSamlIdPConfigurationTests.getAssertion();
        val samlResponse = samlProfileSamlResponseBuilder.build(authnRequest, request, response, assertion, service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertNotNull(samlResponse);
    }

    @Test
    public void verifySamlResponseAllSignedEncrypted() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib(true, true, true);
        service.setRequiredNameIdFormat(ENCRYPTED);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val assertion = BaseSamlIdPConfigurationTests.getAssertion();
        val samlResponse = samlProfileSamlResponseBuilder.build(authnRequest, request, response, assertion, service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertNotNull(samlResponse);
        Assertions.assertTrue(samlResponse.getAssertions().isEmpty());
        Assertions.assertFalse(samlResponse.getEncryptedAssertions().isEmpty());
    }

    @Test
    public void verifySamlResponseAssertionSigned() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib(false, true);
        service.setRequiredNameIdFormat(ENCRYPTED);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val assertion = BaseSamlIdPConfigurationTests.getAssertion();
        val samlResponse = samlProfileSamlResponseBuilder.build(authnRequest, request, response, assertion, service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertNotNull(samlResponse);
        val assertions = samlResponse.getAssertions();
        Assertions.assertFalse(assertions.isEmpty());
        Assertions.assertNull(assertions.get(0).getSubject().getNameID());
        Assertions.assertNotNull(assertions.get(0).getSubject().getEncryptedID());
    }

    @Test
    public void verifySamlResponseResponseSigned() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib(true, false);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val assertion = BaseSamlIdPConfigurationTests.getAssertion();
        val samlResponse = samlProfileSamlResponseBuilder.build(authnRequest, request, response, assertion, service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertNotNull(samlResponse);
    }

    @Test
    public void verifySamlResponseNothingSigned() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib(false, false);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val assertion = BaseSamlIdPConfigurationTests.getAssertion();
        val samlResponse = samlProfileSamlResponseBuilder.build(authnRequest, request, response, assertion, service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertNotNull(samlResponse);
    }
}

