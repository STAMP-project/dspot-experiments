package org.apereo.cas.authentication;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GroovyMultifactorAuthenticationProviderBypassTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
public class GroovyMultifactorAuthenticationProviderBypassTests {
    @Test
    public void verifyAction() {
        Assertions.assertTrue(GroovyMultifactorAuthenticationProviderBypassTests.runGroovyBypassFor("casuser"));
        Assertions.assertFalse(GroovyMultifactorAuthenticationProviderBypassTests.runGroovyBypassFor("anotheruser"));
    }
}

