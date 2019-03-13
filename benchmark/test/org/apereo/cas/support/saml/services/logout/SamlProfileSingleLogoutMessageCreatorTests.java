package org.apereo.cas.support.saml.services.logout;


import RegisteredServiceLogoutType.BACK_CHANNEL;
import SAMLConstants.SAML2_POST_BINDING_URI;
import SamlIdPSingleLogoutServiceLogoutUrlBuilder.PROPERTY_NAME_SINGLE_LOGOUT_BINDING;
import java.net.URL;
import lombok.val;
import org.apereo.cas.logout.DefaultSingleLogoutRequest;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlIdPTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SamlProfileSingleLogoutMessageCreatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class SamlProfileSingleLogoutMessageCreatorTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyOperation() throws Exception {
        val creator = new org.apereo.cas.support.saml.web.idp.profile.slo.SamlProfileSingleLogoutMessageCreator(openSamlConfigBean, servicesManager, defaultSamlRegisteredServiceCachingMetadataResolver, casProperties.getAuthn().getSamlIdp(), samlIdPObjectSigner);
        val logoutRequest = DefaultSingleLogoutRequest.builder().logoutUrl(new URL("https://sp.example.org/slo")).registeredService(SamlIdPTestUtils.getSamlRegisteredService()).service(RegisteredServiceTestUtils.getService("https://sp.testshib.org/shibboleth-sp")).ticketId("ST-123456789").ticketGrantingTicket(new MockTicketGrantingTicket("casuser")).logoutType(BACK_CHANNEL).properties(CollectionUtils.wrap(PROPERTY_NAME_SINGLE_LOGOUT_BINDING, SAML2_POST_BINDING_URI)).build();
        val result = creator.create(logoutRequest);
        Assertions.assertNotNull(result);
    }
}

