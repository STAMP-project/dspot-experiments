package org.apereo.cas.adaptors.generic;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class RejectUsersAuthenticationHandlerTests {
    private final RejectUsersAuthenticationHandler authenticationHandler;

    public RejectUsersAuthenticationHandlerTests() {
        val users = new HashSet<String>();
        users.add("scott");
        users.add("dima");
        users.add("bill");
        this.authenticationHandler = new RejectUsersAuthenticationHandler("", null, null, users);
    }

    @Test
    public void verifySupportsProperUserCredentials() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("fff");
        c.setPassword("rutgers");
        Assertions.assertNotNull(this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        try {
            Assertions.assertFalse(this.authenticationHandler.supports(new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("http://www.rutgers.edu"), CoreAuthenticationTestUtils.getRegisteredService())));
        } catch (final MalformedURLException e) {
            throw new AssertionError("Could not resolve URL.");
        }
    }

    @Test
    public void verifyFailsUserInMap() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("scott");
        c.setPassword("rutgers");
        Assertions.assertThrows(FailedLoginException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyPassesUserNotInMap() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("fds");
        c.setPassword("rutgers");
        Assertions.assertNotNull(this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyPassesNullUserName() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername(null);
        c.setPassword("user");
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyPassesNullUserNameAndPassword() throws Exception {
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(new UsernamePasswordCredential());
        });
    }
}

