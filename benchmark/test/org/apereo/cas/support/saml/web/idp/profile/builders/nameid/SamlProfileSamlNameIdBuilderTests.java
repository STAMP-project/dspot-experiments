package org.apereo.cas.support.saml.web.idp.profile.builders.nameid;


import NameID.EMAIL;
import NameID.ENCRYPTED;
import NameID.TRANSIENT;
import SAMLConstants.SAML2_POST_BINDING_URI;
import java.util.Date;
import lombok.val;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.support.saml.web.idp.profile.builders.SamlProfileObjectBuilder;
import org.apereo.cas.util.CollectionUtils;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.validation.Assertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link SamlProfileSamlNameIdBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("FileSystem")
public class SamlProfileSamlNameIdBuilderTests extends BaseSamlIdPConfigurationTests {
    @Autowired
    @Qualifier("samlProfileSamlNameIdBuilder")
    private SamlProfileObjectBuilder<NameID> samlProfileSamlNameIdBuilder;

    @Autowired
    @Qualifier("samlProfileSamlSubjectBuilder")
    private SamlProfileObjectBuilder<Subject> samlProfileSamlSubjectBuilder;

    @Test
    public void verifyAction() {
        val authnRequest = Mockito.mock(AuthnRequest.class);
        val issuer = Mockito.mock(Issuer.class);
        Mockito.when(issuer.getValue()).thenReturn("https://idp.example.org");
        Mockito.when(authnRequest.getIssuer()).thenReturn(issuer);
        val policy = Mockito.mock(NameIDPolicy.class);
        Mockito.when(policy.getFormat()).thenReturn(EMAIL);
        Mockito.when(authnRequest.getNameIDPolicy()).thenReturn(policy);
        val service = new SamlRegisteredService();
        service.setServiceId("entity-id");
        service.setRequiredNameIdFormat(EMAIL);
        val facade = Mockito.mock(SamlRegisteredServiceServiceProviderMetadataFacade.class);
        Mockito.when(facade.getEntityId()).thenReturn(service.getServiceId());
        val assertion = Mockito.mock(Assertion.class);
        Mockito.when(assertion.getPrincipal()).thenReturn(new AttributePrincipalImpl("casuser"));
        Mockito.when(facade.getSupportedNameIdFormats()).thenReturn(CollectionUtils.wrapList(TRANSIENT, EMAIL));
        val result = samlProfileSamlNameIdBuilder.build(authnRequest, new MockHttpServletRequest(), new MockHttpServletResponse(), assertion, service, facade, SAML2_POST_BINDING_URI, Mockito.mock(MessageContext.class));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(EMAIL, result.getFormat());
        Assertions.assertEquals("casuser", result.getValue());
    }

    @Test
    public void verifyEncryptedNameIdFormat() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        service.setRequiredNameIdFormat(ENCRYPTED);
        service.setSkipGeneratingSubjectConfirmationNameId(false);
        val authnRequest = BaseSamlIdPConfigurationTests.getAuthnRequestFor(service);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(this.samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val assertion = Mockito.mock(Assertion.class);
        Mockito.when(assertion.getPrincipal()).thenReturn(new AttributePrincipalImpl("casuser"));
        Mockito.when(assertion.getValidFromDate()).thenReturn(new Date());
        val subject = samlProfileSamlSubjectBuilder.build(authnRequest, new MockHttpServletRequest(), new MockHttpServletResponse(), assertion, service, adaptor, SAML2_POST_BINDING_URI, Mockito.mock(MessageContext.class));
        Assertions.assertNull(subject.getNameID());
        Assertions.assertNotNull(subject.getEncryptedID());
        Assertions.assertFalse(subject.getSubjectConfirmations().isEmpty());
        val subjectConfirmation = subject.getSubjectConfirmations().get(0);
        Assertions.assertNotNull(subjectConfirmation.getEncryptedID());
        Assertions.assertNull(subjectConfirmation.getNameID());
    }
}

