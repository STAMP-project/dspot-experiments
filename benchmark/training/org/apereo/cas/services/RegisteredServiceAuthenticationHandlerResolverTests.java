package org.apereo.cas.services;


import java.util.Set;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.DefaultAuthenticationTransaction;
import org.apereo.cas.authentication.handler.DefaultAuthenticationHandlerResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link RegisteredServiceAuthenticationHandlerResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class RegisteredServiceAuthenticationHandlerResolverTests {
    private DefaultServicesManager defaultServicesManager;

    private Set<AuthenticationHandler> authenticationHandlers;

    @Test
    public void checkAuthenticationHandlerResolutionDefault() {
        val resolver = new org.apereo.cas.authentication.handler.RegisteredServiceAuthenticationHandlerResolver(this.defaultServicesManager);
        val transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("serviceid1"), RegisteredServiceTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        val handlers = resolver.resolve(this.authenticationHandlers, transaction);
        Assertions.assertEquals(2, handlers.size());
    }

    @Test
    public void checkAuthenticationHandlerResolution() {
        val resolver = new DefaultAuthenticationHandlerResolver();
        val transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("serviceid2"), RegisteredServiceTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        val handlers = resolver.resolve(this.authenticationHandlers, transaction);
        Assertions.assertEquals(handlers.size(), this.authenticationHandlers.size());
    }
}

