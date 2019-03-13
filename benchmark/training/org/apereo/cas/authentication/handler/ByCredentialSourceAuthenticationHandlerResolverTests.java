package org.apereo.cas.authentication.handler;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.DefaultAuthenticationTransaction;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ByCredentialSourceAuthenticationHandlerResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class ByCredentialSourceAuthenticationHandlerResolverTests {
    @Test
    public void verifySupports() {
        val resolver = new ByCredentialSourceAuthenticationHandlerResolver();
        Assertions.assertTrue(resolver.supports(CollectionUtils.wrapSet(new SimpleTestUsernamePasswordAuthenticationHandler()), DefaultAuthenticationTransaction.of(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword())));
    }

    @Test
    public void verifyResolves() {
        val resolver = new ByCredentialSourceAuthenticationHandlerResolver();
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        c.setSource("TestHandler");
        val handler = new SimpleTestUsernamePasswordAuthenticationHandler("TESTHANDLER");
        val results = resolver.resolve(CollectionUtils.wrapSet(handler), DefaultAuthenticationTransaction.of(c));
        Assertions.assertFalse(results.isEmpty());
    }
}

