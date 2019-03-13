package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.impl.token.InMemoryPasswordlessTokenRepository;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link PasswordlessTokenAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PasswordlessTokenAuthenticationHandlerTests {
    @Test
    public void verifyAction() throws Exception {
        val repository = new InMemoryPasswordlessTokenRepository(60);
        repository.saveToken("casuser", "123456");
        val h = new PasswordlessTokenAuthenticationHandler(null, Mockito.mock(ServicesManager.class), PrincipalFactoryUtils.newPrincipalFactory(), 0, repository);
        val c = new OneTimePasswordCredential("casuser", "123456");
        Assertions.assertNotNull(h.authenticate(c));
    }
}

