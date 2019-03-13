package org.apereo.cas.authentication;


import Credential.CREDENTIAL_TYPE_ATTRIBUTE;
import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.metadata.AuthenticationCredentialTypeMetaDataPopulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AuthenticationCredentialTypeMetaDataPopulatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class AuthenticationCredentialTypeMetaDataPopulatorTests {
    private final AuthenticationCredentialTypeMetaDataPopulator populator = new AuthenticationCredentialTypeMetaDataPopulator();

    @Test
    public void verifyPopulator() {
        val credentials = new UsernamePasswordCredential();
        val builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        this.populator.populateAttributes(builder, DefaultAuthenticationTransaction.of(credentials));
        val auth = builder.build();
        Assertions.assertEquals(credentials.getClass().getSimpleName(), auth.getAttributes().get(CREDENTIAL_TYPE_ATTRIBUTE));
    }
}

