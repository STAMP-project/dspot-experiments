package org.pac4j.saml.credentials.authenticator;


import Conditions.DEFAULT_ELEMENT_NAME;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.util.Configuration;


/**
 * This is {@link SAML2AuthenticatorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2AuthenticatorTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    private final SAMLObjectBuilder<Conditions> conditionsBuilder = ((SAMLObjectBuilder<Conditions>) (this.builderFactory.getBuilder(DEFAULT_ELEMENT_NAME)));

    private final SAMLObjectBuilder<NameID> nameIdBuilder = ((SAMLObjectBuilder<NameID>) (this.builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME)));

    @Test
    public void verifyAttributeMapping() {
        final NameID nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");
        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.setNotBefore(DateTime.now());
        conditions.setNotOnOrAfter(DateTime.now());
        final List<String> contexts = new ArrayList<>();
        contexts.add("cas-context");
        final List<Attribute> attributes = new ArrayList<>();
        attributes.add(createAttribute("username", "username", "pac4j"));
        attributes.add(createAttribute("displayName", "urn:oid:2.16.840.1.113730.3.1.241", "Pac4j Library"));
        attributes.add(createAttribute("givenName", "urn:oid:2.5.4.42", "developer"));
        attributes.add(createAttribute("surname", "urn:oid:2.5.4.4", "security"));
        final SAML2Credentials credentials = new SAML2Credentials(nameid, "example.issuer.com", attributes, conditions, "session-index", contexts);
        final Map<String, String> mappedAttributes = new LinkedHashMap<>();
        mappedAttributes.put("urn:oid:2.16.840.1.113730.3.1.241", "mapped-display-name");
        mappedAttributes.put("urn:oid:2.5.4.42", "mapped-given-name");
        mappedAttributes.put("urn:oid:2.5.4.4", "mapped-surname");
        final SAML2Authenticator authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create());
        final CommonProfile finalProfile = credentials.getUserProfile();
        Assert.assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        Assert.assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        Assert.assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }
}

