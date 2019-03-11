package io.github.hidroh.materialistic.accounts;


import AccountManager.KEY_INTENT;
import BuildConfig.APPLICATION_ID;
import LoginActivity.EXTRA_ADD_ACCOUNT;
import RuntimeEnvironment.application;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import io.github.hidroh.materialistic.LoginActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(TestRunner.class)
public class AccountAuthenticatorTest {
    private AccountAuthenticator authenticator;

    @Test
    public void testAddAccount() throws NetworkErrorException {
        Bundle actual = authenticator.addAccount(Mockito.mock(AccountAuthenticatorResponse.class), APPLICATION_ID, null, null, null);
        assertThat(actual).hasKey(KEY_INTENT);
        Intent actualIntent = actual.getParcelable(KEY_INTENT);
        assertThat(actualIntent).hasComponent(application, LoginActivity.class).hasExtra(EXTRA_ADD_ACCOUNT, true);
    }

    @Test
    public void testUnimplemented() throws NetworkErrorException {
        Assert.assertNull(authenticator.editProperties(null, null));
        Assert.assertNull(authenticator.confirmCredentials(null, null, null));
        Assert.assertNull(authenticator.getAuthToken(null, null, null, null));
        Assert.assertNull(authenticator.getAuthTokenLabel(null));
        Assert.assertNull(authenticator.updateCredentials(null, null, null, null));
        Assert.assertNull(authenticator.hasFeatures(null, null, null));
    }
}

