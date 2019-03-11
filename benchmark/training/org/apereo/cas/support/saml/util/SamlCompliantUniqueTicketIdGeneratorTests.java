package org.apereo.cas.support.saml.util;


import lombok.val;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link SamlCompliantUniqueTicketIdGenerator}.
 *
 * @author Scott Battaglia
 * @since 3.4.3
 */
public class SamlCompliantUniqueTicketIdGeneratorTests extends AbstractOpenSamlTests {
    @Test
    public void verifySaml1Compliant() {
        val g = new SamlCompliantUniqueTicketIdGenerator("http://www.cnn.com");
        Assertions.assertNotNull(g.getNewTicketId("TT"));
    }

    @Test
    public void verifySaml2Compliant() {
        val g = new SamlCompliantUniqueTicketIdGenerator("http://www.cnn.com");
        g.setSaml2compliant(true);
        Assertions.assertNotNull(g.getNewTicketId("TT"));
    }
}

