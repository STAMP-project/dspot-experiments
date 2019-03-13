package org.pac4j.saml.client;


import AuthnContextComparisonTypeEnumeration.EXACT;
import SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Redirection tests on the {@link SAML2Client}.
 */
public final class RedirectSAML2ClientTests extends AbstractSAML2ClientTests {
    public RedirectSAML2ClientTests() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        final String inflated = getInflatedAuthnRequest(action.getLocation());
        Assert.assertTrue(inflated.contains(("<saml2:Issuer " + (("Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" " + "NameQualifier=\"http://localhost:8080/callback\" ") + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>"))));
    }

    @Test
    public void testForceAuthIsSetForRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        Assert.assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(EXACT.toString());
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        Assert.assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testNameIdPolicyFormat() {
        final SAML2Client client = getClient();
        client.getConfiguration().setNameIdPolicyFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        final String loc = action.getLocation();
        Assert.assertTrue(getInflatedAuthnRequest(loc).contains(("<saml2p:NameIDPolicy AllowCreate=\"true\" " + "Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\"/></saml2p:AuthnRequest>")));
    }

    @Test
    public void testAuthnContextClassRef() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(EXACT.toString());
        client.getConfiguration().setAuthnContextClassRefs(Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"));
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        final String checkClass = "<saml2p:RequestedAuthnContext Comparison=\"exact\"><saml2:AuthnContextClassRef " + (("xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" + "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>") + "</saml2p:RequestedAuthnContext>");
        Assert.assertTrue(getInflatedAuthnRequest(action.getLocation()).contains(checkClass));
    }

    @Test
    public void testRelayState() {
        final SAML2Client client = getClient();
        final WebContext context = new org.pac4j.core.context.JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.getSessionStore().set(context, SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final FoundAction action = ((FoundAction) (client.redirect(context).get()));
        Assert.assertTrue(action.getLocation().contains("RelayState=relayState"));
    }
}

