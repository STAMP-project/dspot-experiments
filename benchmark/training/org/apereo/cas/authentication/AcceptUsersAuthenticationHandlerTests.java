package org.apereo.cas.authentication;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class AcceptUsersAuthenticationHandlerTests {
    private static final String SCOTT = "scott";

    private static final String RUTGERS = "rutgers";

    private final AcceptUsersAuthenticationHandler authenticationHandler;

    public AcceptUsersAuthenticationHandlerTests() {
        val users = new HashMap<String, String>();
        users.put(AcceptUsersAuthenticationHandlerTests.SCOTT, AcceptUsersAuthenticationHandlerTests.RUTGERS);
        users.put("dima", "javarules");
        users.put("bill", "thisisAwesoME");
        users.put("brian", "t?st");
        this.authenticationHandler = new AcceptUsersAuthenticationHandler("", null, new DefaultPrincipalFactory(), null, users);
    }

    @Test
    @SneakyThrows
    public void verifySupportsSpecialCharacters() {
        val c = new UsernamePasswordCredential();
        c.setUsername("brian");
        c.setPassword("t?st");
        Assertions.assertEquals("brian", this.authenticationHandler.authenticate(c).getPrincipal().getId());
    }

    @Test
    public void verifySupportsProperUserCredentials() {
        val c = new UsernamePasswordCredential();
        c.setUsername(AcceptUsersAuthenticationHandlerTests.SCOTT);
        c.setPassword(AcceptUsersAuthenticationHandlerTests.RUTGERS);
        Assertions.assertTrue(this.authenticationHandler.supports(c));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        try {
            Assertions.assertFalse(this.authenticationHandler.supports(new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("http://www.rutgers.edu"), CoreAuthenticationTestUtils.getRegisteredService("https://some.app.edu"))));
        } catch (final MalformedURLException e) {
            throw new AssertionError("Could not resolve URL.", e);
        }
    }

    @Test
    @SneakyThrows
    public void verifyAuthenticatesUserInMap() {
        val c = new UsernamePasswordCredential();
        c.setUsername(AcceptUsersAuthenticationHandlerTests.SCOTT);
        c.setPassword(AcceptUsersAuthenticationHandlerTests.RUTGERS);
        try {
            Assertions.assertEquals(AcceptUsersAuthenticationHandlerTests.SCOTT, this.authenticationHandler.authenticate(c).getPrincipal().getId());
        } catch (final GeneralSecurityException e) {
            throw new AssertionError("Authentication exception caught but it should not have been thrown.", e);
        }
    }

    @Test
    public void verifyFailsUserNotInMap() {
        val c = new UsernamePasswordCredential();
        c.setUsername("fds");
        c.setPassword(AcceptUsersAuthenticationHandlerTests.RUTGERS);
        Assertions.assertThrows(AccountNotFoundException.class, () -> this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyFailsNullUserName() {
        val c = new UsernamePasswordCredential();
        c.setUsername(null);
        c.setPassword("user");
        Assertions.assertThrows(AccountNotFoundException.class, () -> this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyFailsNullUserNameAndPassword() {
        val c = new UsernamePasswordCredential();
        c.setUsername(null);
        c.setPassword(null);
        Assertions.assertThrows(AccountNotFoundException.class, () -> this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyFailsNullPassword() {
        val c = new UsernamePasswordCredential();
        c.setUsername(AcceptUsersAuthenticationHandlerTests.SCOTT);
        c.setPassword(null);
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(c));
    }
}

