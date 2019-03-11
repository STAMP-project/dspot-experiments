package org.apereo.cas.support.saml.web.idp.profile.builders.attr;


import SAMLConstants.SAML2_POST_BINDING_URI;
import lombok.val;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.support.saml.web.idp.profile.builders.SamlProfileObjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link SamlProfileSamlRegisteredServiceAttributeBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("FileSystem")
public class SamlProfileSamlRegisteredServiceAttributeBuilderTests extends BaseSamlIdPConfigurationTests {
    @Autowired
    @Qualifier("samlProfileSamlAttributeStatementBuilder")
    private SamlProfileObjectBuilder<AttributeStatement> samlProfileSamlAttributeStatementBuilder;

    @Test
    public void verifyEncryptionForAllUndefined() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        service.setEncryptAttributes(true);
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val statement = samlProfileSamlAttributeStatementBuilder.build(BaseSamlIdPConfigurationTests.getAuthnRequestFor(service), new MockHttpServletRequest(), new MockHttpServletResponse(), BaseSamlIdPConfigurationTests.getAssertion(), service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertFalse(statement.getEncryptedAttributes().isEmpty());
        Assertions.assertTrue(statement.getAttributes().isEmpty());
    }

    @Test
    public void verifyEncryptionForAll() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        service.setEncryptAttributes(true);
        service.getEncryptableAttributes().add("*");
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val statement = samlProfileSamlAttributeStatementBuilder.build(BaseSamlIdPConfigurationTests.getAuthnRequestFor(service), new MockHttpServletRequest(), new MockHttpServletResponse(), BaseSamlIdPConfigurationTests.getAssertion(), service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertFalse(statement.getEncryptedAttributes().isEmpty());
        Assertions.assertTrue(statement.getAttributes().isEmpty());
    }

    @Test
    public void verifyEncryptionForSome() {
        val service = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        service.setEncryptAttributes(true);
        service.getEncryptableAttributes().add("uid");
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade.get(samlRegisteredServiceCachingMetadataResolver, service, service.getServiceId()).get();
        val statement = samlProfileSamlAttributeStatementBuilder.build(BaseSamlIdPConfigurationTests.getAuthnRequestFor(service), new MockHttpServletRequest(), new MockHttpServletResponse(), BaseSamlIdPConfigurationTests.getAssertion(), service, adaptor, SAML2_POST_BINDING_URI, new MessageContext());
        Assertions.assertFalse(statement.getEncryptedAttributes().isEmpty());
        Assertions.assertFalse(statement.getAttributes().isEmpty());
    }
}

