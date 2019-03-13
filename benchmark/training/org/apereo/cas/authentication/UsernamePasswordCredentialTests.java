package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class UsernamePasswordCredentialTests {
    @Test
    public void verifySetGetUsername() {
        val c = new UsernamePasswordCredential();
        val userName = "test";
        c.setUsername(userName);
        Assertions.assertEquals(userName, c.getUsername());
    }

    @Test
    public void verifySetGetPassword() {
        val c = new UsernamePasswordCredential();
        val password = "test";
        c.setPassword(password);
        Assertions.assertEquals(password, c.getPassword());
    }

    @Test
    public void verifyEquals() {
        Assertions.assertNotEquals(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(), null);
        Assertions.assertNotEquals(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        Assertions.assertEquals(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword(), CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword());
    }
}

