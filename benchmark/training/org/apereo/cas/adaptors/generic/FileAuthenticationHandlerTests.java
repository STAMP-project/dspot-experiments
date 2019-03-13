package org.apereo.cas.adaptors.generic;


import java.net.MalformedURLException;
import java.net.URL;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.core.io.ClassPathResource;

import static FileAuthenticationHandler.DEFAULT_SEPARATOR;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class FileAuthenticationHandlerTests {
    private FileAuthenticationHandler authenticationHandler;

    @Test
    public void verifySupportsProperUserCredentials() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("scott");
        c.setPassword("rutgers");
        Assertions.assertNotNull(this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyDoesNotSupportBadUserCredentials() {
        try {
            val c = new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("http://www.rutgers.edu"), CoreAuthenticationTestUtils.getRegisteredService());
            Assertions.assertFalse(this.authenticationHandler.supports(c));
        } catch (final MalformedURLException e) {
            throw new AssertionError("MalformedURLException caught.");
        }
    }

    @Test
    public void verifyAuthenticatesUserInFileWithDefaultSeparator() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("scott");
        c.setPassword("rutgers");
        Assertions.assertNotNull(this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyFailsUserNotInFileWithDefaultSeparator() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("fds");
        c.setPassword("rutgers");
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyFailsNullUserName() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername(null);
        c.setPassword("user");
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyFailsNullUserNameAndPassword() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername(null);
        c.setPassword(null);
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyFailsNullPassword() throws Exception {
        val c = new UsernamePasswordCredential();
        c.setUsername("scott");
        c.setPassword(null);
        Assertions.assertThrows(FailedLoginException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyAuthenticatesUserInFileWithCommaSeparator() throws Exception {
        val c = new UsernamePasswordCredential();
        this.authenticationHandler = new FileAuthenticationHandler("", null, null, new ClassPathResource("authentication2.txt"), ",");
        c.setUsername("scott");
        c.setPassword("rutgers");
        Assertions.assertNotNull(this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyFailsUserNotInFileWithCommaSeparator() throws Exception {
        val c = new UsernamePasswordCredential();
        this.authenticationHandler = new FileAuthenticationHandler("", null, null, new ClassPathResource("authentication2.txt"), ",");
        c.setUsername("fds");
        c.setPassword("rutgers");
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.authenticationHandler.authenticate(c);
        });
    }

    @Test
    public void verifyFailsGoodUsernameBadPassword() {
        val c = new UsernamePasswordCredential();
        this.authenticationHandler = new FileAuthenticationHandler("", null, null, new ClassPathResource("authentication2.txt"), ",");
        c.setUsername("scott");
        c.setPassword("rutgers1");
        Assertions.assertThrows(FailedLoginException.class, () -> this.authenticationHandler.authenticate(c));
    }

    @Test
    public void verifyAuthenticateNoFileName() {
        val c = new UsernamePasswordCredential();
        this.authenticationHandler = new FileAuthenticationHandler("", null, null, new ClassPathResource("fff"), DEFAULT_SEPARATOR);
        c.setUsername("scott");
        c.setPassword("rutgers");
        Assertions.assertThrows(PreventedException.class, () -> this.authenticationHandler.authenticate(c));
    }
}

