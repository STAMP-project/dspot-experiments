package org.apereo.cas.support.pac4j.authentication.handler.support;


import java.security.GeneralSecurityException;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.principal.ClientCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * Tests the {@link DelegatedClientAuthenticationHandler}.
 *
 * @author Jerome Leleu
 * @since 4.1.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
public class ClientAuthenticationHandlerTests {
    private static final String CALLBACK_URL = "http://localhost:8080/callback";

    private static final String ID = "123456789";

    private FacebookClient fbClient;

    private DelegatedClientAuthenticationHandler handler;

    private ClientCredential clientCredential;

    @Test
    public void verifyOk() throws GeneralSecurityException, PreventedException {
        val facebookProfile = new FacebookProfile();
        facebookProfile.setId(ClientAuthenticationHandlerTests.ID);
        this.fbClient.setProfileCreator(( oAuth20Credentials, webContext) -> facebookProfile);
        val result = this.handler.authenticate(this.clientCredential);
        val principal = result.getPrincipal();
        Assertions.assertEquals((((FacebookProfile.class.getName()) + '#') + (ClientAuthenticationHandlerTests.ID)), principal.getId());
    }

    @Test
    public void verifyOkWithSimpleIdentifier() throws GeneralSecurityException, PreventedException {
        this.handler.setTypedIdUsed(false);
        val facebookProfile = new FacebookProfile();
        facebookProfile.setId(ClientAuthenticationHandlerTests.ID);
        this.fbClient.setProfileCreator(( oAuth20Credentials, webContext) -> facebookProfile);
        val result = this.handler.authenticate(this.clientCredential);
        val principal = result.getPrincipal();
        Assertions.assertEquals(ClientAuthenticationHandlerTests.ID, principal.getId());
    }

    @Test
    public void verifyNoProfile() throws GeneralSecurityException, PreventedException {
        Assertions.assertThrows(FailedLoginException.class, () -> {
            this.fbClient.setProfileCreator(( oAuth20Credentials, webContext) -> null);
            this.handler.authenticate(this.clientCredential);
        });
    }
}

