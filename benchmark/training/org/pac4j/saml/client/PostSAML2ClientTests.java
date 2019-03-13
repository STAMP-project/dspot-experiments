package org.pac4j.saml.client;


import AuthnContextComparisonTypeEnumeration.EXACT;
import SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.OkAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * POST tests on the {@link SAML2Client}.
 */
public final class PostSAML2ClientTests extends AbstractSAML2ClientTests {
    public PostSAML2ClientTests() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = ((OkAction) (client.redirect(context).get()));
        Assert.assertTrue(PostSAML2ClientTests.getDecodedAuthnRequest(action.getContent()).contains(("<saml2:Issuer " + (("Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" " + "NameQualifier=\"http://localhost:8080/cb\" ") + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/cb</saml2:Issuer>"))));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = ((OkAction) (client.redirect(context).get()));
        Assert.assertTrue(PostSAML2ClientTests.getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(EXACT.toString());
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = ((OkAction) (client.redirect(context).get()));
        Assert.assertTrue(PostSAML2ClientTests.getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() {
        final SAML2Client client = getClient();
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.getSessionStore().set(context, SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final OkAction action = ((OkAction) (client.redirect(context).get()));
        Assert.assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }
}

