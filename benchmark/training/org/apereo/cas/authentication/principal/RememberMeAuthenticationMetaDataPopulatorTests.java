package org.apereo.cas.authentication.principal;


import RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.metadata.RememberMeAuthenticationMetaDataPopulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.2.1
 */
public class RememberMeAuthenticationMetaDataPopulatorTests {
    private final RememberMeAuthenticationMetaDataPopulator p = new RememberMeAuthenticationMetaDataPopulator();

    @Test
    public void verifyWithTrueRememberMeCredentials() {
        val c = new RememberMeUsernamePasswordCredential();
        c.setRememberMe(true);
        val builder = newBuilder(c);
        val auth = builder.build();
        Assertions.assertEquals(true, auth.getAttributes().get(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME));
    }

    @Test
    public void verifyWithFalseRememberMeCredentials() {
        val c = new RememberMeUsernamePasswordCredential();
        c.setRememberMe(false);
        val builder = newBuilder(c);
        val auth = builder.build();
        Assertions.assertNull(auth.getAttributes().get(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME));
    }

    @Test
    public void verifyWithoutRememberMeCredentials() {
        val builder = newBuilder(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        val auth = builder.build();
        Assertions.assertNull(auth.getAttributes().get(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME));
    }
}

