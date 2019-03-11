package org.apereo.cas.support.saml.web.idp.profile.builders.enc;


import lombok.val;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link SamlObjectSignatureValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("FileSystem")
public class SamlObjectSignatureValidatorTests extends BaseSamlIdPConfigurationTests {
    private SAML2Configuration saml2ClientConfiguration;

    private SAML2MessageContext saml2MessageContext;

    private MessageContext<SAMLObject> samlContext;

    private SamlRegisteredServiceServiceProviderMetadataFacade adaptor;

    @Test
    public void verifySamlAuthnRequestNotSigned() throws Exception {
        val request = new MockHttpServletRequest();
        val builder = new org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder(saml2ClientConfiguration);
        val authnRequest = builder.build(saml2MessageContext);
        samlObjectSignatureValidator.verifySamlProfileRequestIfNeeded(authnRequest, adaptor, request, samlContext);
    }
}

