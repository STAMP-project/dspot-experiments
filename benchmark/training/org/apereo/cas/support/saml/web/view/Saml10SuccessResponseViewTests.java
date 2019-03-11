package org.apereo.cas.support.saml.web.view;


import CasProtocolConstants.VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME;
import RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME;
import SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD;
import SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_SSL_TLS_CLIENT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Unit test for {@link Saml10SuccessResponseView} class.
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.1
 */
public class Saml10SuccessResponseViewTests extends AbstractOpenSamlTests {
    private static final String TEST_VALUE = "testValue";

    private static final String TEST_ATTRIBUTE = "testAttribute";

    private static final String PRINCIPAL_ID = "testPrincipal";

    private Saml10SuccessResponseView response;

    @Test
    public void verifyResponse() throws Exception {
        val model = new HashMap<String, Object>();
        val attributes = new HashMap<String, Object>();
        attributes.put(Saml10SuccessResponseViewTests.TEST_ATTRIBUTE, Saml10SuccessResponseViewTests.TEST_VALUE);
        attributes.put("testEmptyCollection", new ArrayList(0));
        attributes.put("testAttributeCollection", Arrays.asList("tac1", "tac2"));
        val principal = new DefaultPrincipalFactory().createPrincipal(Saml10SuccessResponseViewTests.PRINCIPAL_ID, attributes);
        val authAttributes = new HashMap<String, Object>();
        authAttributes.put(ATTRIBUTE_AUTHENTICATION_METHOD, AUTHN_METHOD_SSL_TLS_CLIENT);
        authAttributes.put("testSamlAttribute", "value");
        val primary = CoreAuthenticationTestUtils.getAuthentication(principal, authAttributes);
        val assertion = new org.apereo.cas.validation.DefaultAssertionBuilder(primary).with(Collections.singletonList(primary)).with(CoreAuthenticationTestUtils.getService()).with(true).build();
        model.put("assertion", assertion);
        val servletResponse = new MockHttpServletResponse();
        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        val written = servletResponse.getContentAsString();
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.PRINCIPAL_ID));
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.TEST_ATTRIBUTE));
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.TEST_VALUE));
        Assertions.assertFalse(written.contains("testEmptyCollection"));
        Assertions.assertTrue(written.contains("testAttributeCollection"));
        Assertions.assertTrue(written.contains("tac1"));
        Assertions.assertTrue(written.contains("tac2"));
        Assertions.assertTrue(written.contains(AUTHN_METHOD_SSL_TLS_CLIENT));
        Assertions.assertTrue(written.contains("AuthenticationMethod"));
        Assertions.assertTrue(written.contains("AssertionID"));
        Assertions.assertTrue(written.contains("saml1:Attribute"));
        Assertions.assertTrue(written.contains("saml1p:Response"));
        Assertions.assertTrue(written.contains("saml1:Assertion"));
    }

    @Test
    public void verifyResponseWithNoAttributes() throws Exception {
        val model = new HashMap<String, Object>();
        val principal = new DefaultPrincipalFactory().createPrincipal(Saml10SuccessResponseViewTests.PRINCIPAL_ID);
        val authAttributes = new HashMap<String, Object>();
        authAttributes.put(ATTRIBUTE_AUTHENTICATION_METHOD, AUTHN_METHOD_SSL_TLS_CLIENT);
        authAttributes.put("testSamlAttribute", "value");
        val primary = CoreAuthenticationTestUtils.getAuthentication(principal, authAttributes);
        val assertion = new org.apereo.cas.validation.DefaultAssertionBuilder(primary).with(Collections.singletonList(primary)).with(CoreAuthenticationTestUtils.getService()).with(true).build();
        model.put("assertion", assertion);
        val servletResponse = new MockHttpServletResponse();
        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        val written = servletResponse.getContentAsString();
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.PRINCIPAL_ID));
        Assertions.assertTrue(written.contains(AUTHN_METHOD_SSL_TLS_CLIENT));
        Assertions.assertTrue(written.contains("AuthenticationMethod="));
    }

    @Test
    public void verifyResponseWithoutAuthMethod() throws Exception {
        val model = new HashMap<String, Object>();
        val attributes = new HashMap<String, Object>();
        attributes.put(Saml10SuccessResponseViewTests.TEST_ATTRIBUTE, Saml10SuccessResponseViewTests.TEST_VALUE);
        val principal = new DefaultPrincipalFactory().createPrincipal(Saml10SuccessResponseViewTests.PRINCIPAL_ID, attributes);
        val authnAttributes = new HashMap<String, Object>();
        authnAttributes.put("authnAttribute1", "authnAttrbuteV1");
        authnAttributes.put("authnAttribute2", "authnAttrbuteV2");
        authnAttributes.put(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, Boolean.TRUE);
        val primary = CoreAuthenticationTestUtils.getAuthentication(principal, authnAttributes);
        val assertion = new org.apereo.cas.validation.DefaultAssertionBuilder(primary).with(Collections.singletonList(primary)).with(CoreAuthenticationTestUtils.getService()).with(true).build();
        model.put("assertion", assertion);
        val servletResponse = new MockHttpServletResponse();
        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        val written = servletResponse.getContentAsString();
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.PRINCIPAL_ID));
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.TEST_ATTRIBUTE));
        Assertions.assertTrue(written.contains(Saml10SuccessResponseViewTests.TEST_VALUE));
        Assertions.assertTrue(written.contains("authnAttribute1"));
        Assertions.assertTrue(written.contains("authnAttribute2"));
        Assertions.assertTrue(written.contains(VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME));
        Assertions.assertTrue(written.contains("urn:oasis:names:tc:SAML:1.0:am:unspecified"));
    }
}

