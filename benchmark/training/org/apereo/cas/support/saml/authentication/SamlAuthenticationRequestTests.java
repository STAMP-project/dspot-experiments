package org.apereo.cas.support.saml.authentication;


import lombok.val;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.apereo.cas.util.CompressionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Utility class to ensure authentication requests are properly encoded and decoded.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class SamlAuthenticationRequestTests extends AbstractOpenSamlTests {
    @Test
    public void ensureDeflation() throws Exception {
        val deflator = CompressionUtils.deflate(AbstractOpenSamlTests.SAML_REQUEST);
        val deflatorStream = SamlAuthenticationRequestTests.deflateViaStream(AbstractOpenSamlTests.SAML_REQUEST);
        Assertions.assertEquals(deflatorStream, deflator);
    }
}

