package org.apereo.cas.authentication;


import UsernamePasswordCredential.AUTHENTICATION_ATTRIBUTE_PASSWORD;
import lombok.val;
import org.apereo.cas.authentication.metadata.CacheCredentialsMetaDataPopulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link CacheCredentialsMetaDataPopulator}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class CacheCredentialsMetaDataPopulatorTests {
    @Test
    public void verifyPasswordAsAuthenticationAttribute() {
        val populator = new CacheCredentialsMetaDataPopulator();
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val builder = DefaultAuthenticationBuilder.newInstance(CoreAuthenticationTestUtils.getAuthentication());
        populator.populateAttributes(builder, DefaultAuthenticationTransaction.of(c));
        val authn = builder.build();
        Assertions.assertTrue(authn.getAttributes().containsKey(AUTHENTICATION_ATTRIBUTE_PASSWORD));
        Assertions.assertTrue(authn.getAttributes().get(AUTHENTICATION_ATTRIBUTE_PASSWORD).equals(c.getPassword()));
    }
}

