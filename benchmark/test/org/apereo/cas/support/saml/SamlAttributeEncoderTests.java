package org.apereo.cas.support.saml;


import java.util.HashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.ProtocolAttributeEncoder;
import org.apereo.cas.support.saml.web.idp.profile.builders.enc.attribute.SamlAttributeEncoder;
import org.apereo.cas.util.EncodingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SamlAttributeEncoderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class SamlAttributeEncoderTests {
    @Test
    public void verifyAction() {
        val encoder = new SamlAttributeEncoder();
        val original = CoreAuthenticationTestUtils.getAttributes();
        original.put("address", EncodingUtils.hexEncode("123 Main Street"));
        val attributes = encoder.encodeAttributes(original, CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertEquals(original.size(), attributes.size());
        Assertions.assertTrue(attributes.containsKey("address"));
    }

    @Test
    public void ensureSamlUrnAttributesEncoded() {
        val encoder = new SamlAttributeEncoder();
        val attributes = new HashMap<String, Object>();
        attributes.put(ProtocolAttributeEncoder.encodeAttribute("urn:oid:2.5.4.3"), "testValue");
        val result = encoder.encodeAttributes(attributes, CoreAuthenticationTestUtils.getRegisteredService("test"));
        Assertions.assertTrue(result.containsKey("urn:oid:2.5.4.3"));
    }

    @Test
    public void ensureSamlMsftClaimsAttributesEncoded() {
        val encoder = new SamlAttributeEncoder();
        val attributes = new HashMap<String, Object>();
        attributes.put("http://schemas.microsoft.com/ws/2008/06/identity/claims/windowsaccountname", "testValue");
        val result = encoder.encodeAttributes(attributes, CoreAuthenticationTestUtils.getRegisteredService("test"));
        Assertions.assertTrue(result.containsKey("http://schemas.microsoft.com/ws/2008/06/identity/claims/windowsaccountname"));
    }
}

