package org.apereo.cas.gauth;


import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.ICredentialRepository;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountNotFoundException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.gauth.credential.GoogleAuthenticatorTokenCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This is {@link GoogleAuthenticatorAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class GoogleAuthenticatorAuthenticationHandlerTests {
    private IGoogleAuthenticator googleAuthenticator;

    private GoogleAuthenticatorAuthenticationHandler handler;

    private GoogleAuthenticatorKey googleAuthenticatorAccount;

    @Test
    public void verifySupports() {
        val credential = new GoogleAuthenticatorTokenCredential();
        Assertions.assertTrue(handler.supports(credential));
        Assertions.assertTrue(handler.supports(GoogleAuthenticatorTokenCredential.class));
    }

    @Test
    public void verifyAuthnAccountNotFound() {
        val credential = getGoogleAuthenticatorTokenCredential();
        Assertions.assertThrows(AccountNotFoundException.class, () -> handler.authenticate(credential));
    }

    @Test
    public void verifyAuthnFailsTokenNotFound() {
        val credential = getGoogleAuthenticatorTokenCredential();
        handler.getTokenRepository().store(new org.apereo.cas.authentication.OneTimeToken(Integer.valueOf(credential.getToken()), "casuser"));
        handler.getCredentialRepository().save("casuser", googleAuthenticatorAccount.getKey(), googleAuthenticatorAccount.getVerificationCode(), googleAuthenticatorAccount.getScratchCodes());
        Assertions.assertThrows(AccountExpiredException.class, () -> handler.authenticate(credential));
    }

    @Test
    @SneakyThrows
    public void verifyAuthnTokenFound() {
        val credential = getGoogleAuthenticatorTokenCredential();
        handler.getCredentialRepository().save("casuser", googleAuthenticatorAccount.getKey(), googleAuthenticatorAccount.getVerificationCode(), googleAuthenticatorAccount.getScratchCodes());
        val result = handler.authenticate(credential);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(handler.getTokenRepository().get("casuser", Integer.valueOf(credential.getToken())));
    }

    @Test
    @SneakyThrows
    public void verifyAuthnTokenScratchCode() {
        val credential = getGoogleAuthenticatorTokenCredential();
        handler.getCredentialRepository().save("casuser", googleAuthenticatorAccount.getKey(), googleAuthenticatorAccount.getVerificationCode(), googleAuthenticatorAccount.getScratchCodes());
        credential.setToken(Integer.toString(googleAuthenticatorAccount.getScratchCodes().get(0)));
        val result = handler.authenticate(credential);
        Assertions.assertNotNull(result);
        val otp = Integer.valueOf(credential.getToken());
        Assertions.assertNotNull(handler.getTokenRepository().get("casuser", otp));
        Assertions.assertFalse(handler.getCredentialRepository().get("casuser").getScratchCodes().contains(otp));
    }

    private static class DummyCredentialRepository implements ICredentialRepository {
        private final Map<String, String> accounts = new LinkedHashMap<>();

        @Override
        public String getSecretKey(final String s) {
            return accounts.get(s);
        }

        @Override
        public void saveUserCredentials(final String s, final String s1, final int i, final List<Integer> list) {
            accounts.put(s, s1);
        }
    }
}

