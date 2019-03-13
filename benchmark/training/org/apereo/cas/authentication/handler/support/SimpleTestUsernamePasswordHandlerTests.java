package org.apereo.cas.authentication.handler.support;


import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test of the simple username/password handler.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class SimpleTestUsernamePasswordHandlerTests {
    private SimpleTestUsernamePasswordAuthenticationHandler authenticationHandler;

    @Test
    public void verifySupportsProperUserCredentials() {
        Assertions.assertTrue(this.authenticationHandler.supports(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifySupportsRememberMeUserCredentials() {
        Assertions.assertTrue(this.authenticationHandler.supports(new RememberMeUsernamePasswordCredential()));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        Assertions.assertFalse(this.authenticationHandler.supports(CoreAuthenticationTestUtils.getHttpBasedServiceCredentials()));
    }

    @Test
    @SneakyThrows
    public void verifyValidUsernamePassword() {
        val result = authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        Assertions.assertEquals("SimpleTestUsernamePasswordAuthenticationHandler", result.getHandlerName());
    }

    @Test
    public void verifyInvalidUsernamePassword() {
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword()));
    }
}

