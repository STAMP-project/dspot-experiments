package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for RememberMeUsernamePasswordCredential.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 */
public class RememberMePasswordCredentialTests {
    @Test
    public void verifyGettersAndSetters() {
        val c = new RememberMeUsernamePasswordCredential();
        c.setPassword("password");
        c.setUsername("username");
        c.setRememberMe(true);
        Assertions.assertEquals("username", c.getUsername());
        Assertions.assertEquals("password", c.getPassword());
        Assertions.assertTrue(c.isRememberMe());
    }
}

