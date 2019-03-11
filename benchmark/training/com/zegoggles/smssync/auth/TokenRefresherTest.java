package com.zegoggles.smssync.auth;


import AccountManager.KEY_AUTHTOKEN;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.os.Bundle;
import android.os.Handler;
import com.zegoggles.smssync.preferences.AuthPreferences;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class TokenRefresherTest {
    @Mock
    AccountManager accountManager;

    @Mock
    AuthPreferences authPreferences;

    @Mock
    OAuth2Client oauth2Client;

    TokenRefresher refresher;

    @Test
    public void shouldInvalidateTokenManually() throws Exception {
        assertThat(refresher.invalidateToken("token")).isTrue();
        Mockito.verify(accountManager).invalidateAuthToken(GOOGLE_TYPE, "token");
    }

    @Test
    public void shouldHandleSecurityExceptionWhenInvalidatingToken() throws Exception {
        Mockito.doThrow(new SecurityException()).when(accountManager).invalidateAuthToken(GOOGLE_TYPE, "token");
        assertThat(refresher.invalidateToken("token")).isFalse();
    }

    @Test
    public void shouldInvalidateTokenOnRefresh() throws Exception {
        Mockito.when(authPreferences.getOauth2Token()).thenReturn("token");
        Mockito.when(authPreferences.getOauth2Username()).thenReturn("username");
        Mockito.when(accountManager.getAuthToken(ArgumentMatchers.notNull(Account.class), ArgumentMatchers.anyString(), ArgumentMatchers.isNull(Bundle.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(AccountManagerCallback.class), ArgumentMatchers.any(Handler.class))).thenReturn(Mockito.mock(AccountManagerFuture.class));
        try {
            refresher.refreshOAuth2Token();
            Assert.fail("expected error ");
        } catch (TokenRefreshException e) {
            assertThat(e.getMessage()).isEqualTo("no bundle received from accountmanager");
        }
        Mockito.verify(accountManager).invalidateAuthToken(GOOGLE_TYPE, "token");
    }

    @Test
    public void shouldHandleExceptionsThrownByFuture() throws Exception {
        Mockito.when(authPreferences.getOauth2Token()).thenReturn("token");
        Mockito.when(authPreferences.getOauth2Username()).thenReturn("username");
        AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFuture.class);
        Mockito.when(accountManager.getAuthToken(ArgumentMatchers.notNull(Account.class), ArgumentMatchers.anyString(), ArgumentMatchers.isNull(Bundle.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(AccountManagerCallback.class), ArgumentMatchers.any(Handler.class))).thenReturn(future);
        AuthenticatorException exception = new AuthenticatorException();
        Mockito.when(future.getResult()).thenThrow(exception);
        try {
            refresher.refreshOAuth2Token();
            Assert.fail("expected exception");
        } catch (TokenRefreshException e) {
            assertThat(e.getCause()).isSameAs(exception);
        }
        Mockito.verify(accountManager).invalidateAuthToken(GOOGLE_TYPE, "token");
    }

    @Test
    public void shouldSetNewTokenAfterRefresh() throws Exception {
        Mockito.when(authPreferences.getOauth2Token()).thenReturn("token");
        Mockito.when(authPreferences.getOauth2Username()).thenReturn("username");
        AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFuture.class);
        Mockito.when(accountManager.getAuthToken(new Account("username", GOOGLE_TYPE), AUTH_TOKEN_TYPE, null, true, null, null)).thenReturn(future);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_AUTHTOKEN, "newToken");
        Mockito.when(future.getResult()).thenReturn(bundle);
        refresher.refreshOAuth2Token();
        Mockito.verify(authPreferences).setOauth2Token("username", "newToken", null);
    }

    @Test
    public void shouldUseOAuth2ClientWhenRefreshTokenIsPresent() throws Exception {
        Mockito.when(authPreferences.getOauth2Token()).thenReturn("token");
        Mockito.when(authPreferences.getOauth2RefreshToken()).thenReturn("refresh");
        Mockito.when(authPreferences.getOauth2Username()).thenReturn("username");
        Mockito.when(oauth2Client.refreshToken("refresh")).thenReturn(new OAuth2Token("newToken", "type", null, 0, null));
        refresher.refreshOAuth2Token();
        Mockito.verify(authPreferences).setOauth2Token("username", "newToken", "refresh");
    }

    @Test
    public void shouldUpdateRefreshTokenIfPresentInResponse() throws Exception {
        Mockito.when(authPreferences.getOauth2Token()).thenReturn("token");
        Mockito.when(authPreferences.getOauth2RefreshToken()).thenReturn("refresh");
        Mockito.when(authPreferences.getOauth2Username()).thenReturn("username");
        Mockito.when(oauth2Client.refreshToken("refresh")).thenReturn(new OAuth2Token("newToken", "type", "newRefresh", 0, null));
        refresher.refreshOAuth2Token();
        Mockito.verify(authPreferences).setOauth2Token("username", "newToken", "newRefresh");
    }
}

