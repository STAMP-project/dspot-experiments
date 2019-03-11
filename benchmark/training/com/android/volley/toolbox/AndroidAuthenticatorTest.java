/**
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.volley.toolbox;


import AccountManager.KEY_AUTHTOKEN;
import AccountManager.KEY_INTENT;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.AuthFailureError;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AndroidAuthenticatorTest {
    private AccountManager mAccountManager;

    private Account mAccount;

    private AccountManagerFuture<Bundle> mFuture;

    private AndroidAuthenticator mAuthenticator;

    @Test(expected = AuthFailureError.class)
    public void failedGetAuthToken() throws Exception {
        Mockito.when(mAccountManager.getAuthToken(mAccount, "cooltype", false, null, null)).thenReturn(mFuture);
        Mockito.when(mFuture.getResult()).thenThrow(new AuthenticatorException("sadness!"));
        mAuthenticator.getAuthToken();
    }

    @Test(expected = AuthFailureError.class)
    public void resultContainsIntent() throws Exception {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        Mockito.when(mAccountManager.getAuthToken(mAccount, "cooltype", false, null, null)).thenReturn(mFuture);
        Mockito.when(mFuture.getResult()).thenReturn(bundle);
        Mockito.when(mFuture.isDone()).thenReturn(true);
        Mockito.when(mFuture.isCancelled()).thenReturn(false);
        mAuthenticator.getAuthToken();
    }

    @Test(expected = AuthFailureError.class)
    public void missingAuthToken() throws Exception {
        Bundle bundle = new Bundle();
        Mockito.when(mAccountManager.getAuthToken(mAccount, "cooltype", false, null, null)).thenReturn(mFuture);
        Mockito.when(mFuture.getResult()).thenReturn(bundle);
        Mockito.when(mFuture.isDone()).thenReturn(true);
        Mockito.when(mFuture.isCancelled()).thenReturn(false);
        mAuthenticator.getAuthToken();
    }

    @Test
    public void invalidateAuthToken() throws Exception {
        mAuthenticator.invalidateAuthToken("monkey");
        Mockito.verify(mAccountManager).invalidateAuthToken("cooltype", "monkey");
    }

    @Test
    public void goodToken() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_AUTHTOKEN, "monkey");
        Mockito.when(mAccountManager.getAuthToken(mAccount, "cooltype", false, null, null)).thenReturn(mFuture);
        Mockito.when(mFuture.getResult()).thenReturn(bundle);
        Mockito.when(mFuture.isDone()).thenReturn(true);
        Mockito.when(mFuture.isCancelled()).thenReturn(false);
        Assert.assertEquals("monkey", mAuthenticator.getAuthToken());
    }

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        Context context = Mockito.mock(Context.class);
        new AndroidAuthenticator(context, mAccount, "cooltype");
        new AndroidAuthenticator(context, mAccount, "cooltype", true);
        Assert.assertSame(mAccount, mAuthenticator.getAccount());
    }
}

