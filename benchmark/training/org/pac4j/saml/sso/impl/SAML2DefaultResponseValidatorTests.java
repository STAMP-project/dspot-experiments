package org.pac4j.saml.sso.impl;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;


public class SAML2DefaultResponseValidatorTests {
    @Test
    public void testDoesNotWantAssertionsSignedWithNullContext() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        Assert.assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testWantsAssertionsSignedWithNullContext() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        Assert.assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        Assert.assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        Assert.assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();
        Assert.assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        Assert.assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        SPSSODescriptor roleDescriptor = Mockito.mock(SPSSODescriptor.class);
        Mockito.when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);
        Assert.assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        Assert.assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithValidSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        SPSSODescriptor roleDescriptor = Mockito.mock(SPSSODescriptor.class);
        Mockito.when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);
        Assert.assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        Assert.assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test(expected = SAMLException.class)
    public void testAssertionWithoutSignatureThrowsException() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test
    public void testAssertionWithoutSignatureDoesNotThrowException() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
        // expected no exceptions
    }
}

