package org.apereo.cas.authentication;


import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_PRINCIPAL;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_USER;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link SurrogateAuthenticationMetaDataPopulatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateAuthenticationMetaDataPopulatorTests {
    @Test
    public void verifyAction() {
        val p = new SurrogateAuthenticationMetaDataPopulator();
        Assertions.assertFalse(p.supports(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
        val c = new SurrogateUsernamePasswordCredential();
        c.setSurrogateUsername("cassurrogate");
        c.setUsername("casuser");
        c.setPassword("password");
        val builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        p.populateAttributes(builder, DefaultAuthenticationTransaction.of(c));
        val auth = builder.build();
        Assertions.assertTrue(auth.getAttributes().containsKey(AUTHENTICATION_ATTR_SURROGATE_ENABLED));
        Assertions.assertTrue(auth.getAttributes().containsKey(AUTHENTICATION_ATTR_SURROGATE_PRINCIPAL));
        Assertions.assertTrue(auth.getAttributes().containsKey(AUTHENTICATION_ATTR_SURROGATE_USER));
    }
}

