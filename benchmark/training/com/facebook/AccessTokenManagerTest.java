/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook;


import AccessTokenManager.EXTRA_NEW_ACCESS_TOKEN;
import AccessTokenManager.EXTRA_OLD_ACCESS_TOKEN;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.facebook.internal.Utility;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static AccessTokenManager.ACTION_CURRENT_ACCESS_TOKEN_CHANGED;


@PrepareForTest({ FacebookSdk.class, AccessTokenCache.class, Utility.class })
public class AccessTokenManagerTest extends FacebookPowerMockTestCase {
    private final String TOKEN_STRING = "A token of my esteem";

    private final String USER_ID = "1000";

    private final List<String> PERMISSIONS = Arrays.asList("walk", "chew gum");

    private final Date EXPIRES = new Date(2025, 5, 3);

    private final Date LAST_REFRESH = new Date(2023, 8, 15);

    private final Date DATA_ACCESS_EXPIRATION_TIME = new Date(2025, 5, 3);

    private final String APP_ID = "1234";

    private LocalBroadcastManager localBroadcastManager;

    private AccessTokenCache accessTokenCache;

    @Test
    public void testRequiresLocalBroadcastManager() {
        try {
            AccessTokenManager accessTokenManager = new AccessTokenManager(null, accessTokenCache);
            Assert.fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testRequiresTokenCache() {
        try {
            AccessTokenManager accessTokenManager = new AccessTokenManager(localBroadcastManager, null);
            Assert.fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testDefaultsToNoCurrentAccessToken() {
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        Assert.assertNull(accessTokenManager.getCurrentAccessToken());
    }

    @Test
    public void testCanSetCurrentAccessToken() {
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        AccessToken accessToken = createAccessToken();
        accessTokenManager.setCurrentAccessToken(accessToken);
        Assert.assertEquals(accessToken, accessTokenManager.getCurrentAccessToken());
    }

    @Test
    public void testChangingAccessTokenSendsBroadcast() {
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        AccessToken accessToken = createAccessToken();
        accessTokenManager.setCurrentAccessToken(accessToken);
        final Intent[] intents = new Intent[1];
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intents[0] = intent;
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver, new android.content.IntentFilter(ACTION_CURRENT_ACCESS_TOKEN_CHANGED));
        AccessToken anotherAccessToken = createAccessToken("another string", "1000");
        accessTokenManager.setCurrentAccessToken(anotherAccessToken);
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        Intent intent = intents[0];
        Assert.assertNotNull(intent);
        AccessToken oldAccessToken = intent.getParcelableExtra(EXTRA_OLD_ACCESS_TOKEN);
        AccessToken newAccessToken = intent.getParcelableExtra(EXTRA_NEW_ACCESS_TOKEN);
        Assert.assertEquals(accessToken.getToken(), oldAccessToken.getToken());
        Assert.assertEquals(anotherAccessToken.getToken(), newAccessToken.getToken());
    }

    @Test
    public void testLoadReturnsFalseIfNoCachedToken() {
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        boolean result = accessTokenManager.loadCurrentAccessToken();
        Assert.assertFalse(result);
    }

    @Test
    public void testLoadReturnsTrueIfCachedToken() {
        AccessToken accessToken = createAccessToken();
        when(accessTokenCache.load()).thenReturn(accessToken);
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        boolean result = accessTokenManager.loadCurrentAccessToken();
        Assert.assertTrue(result);
    }

    @Test
    public void testLoadSetsCurrentTokenIfCached() {
        AccessToken accessToken = createAccessToken();
        when(accessTokenCache.load()).thenReturn(accessToken);
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        accessTokenManager.loadCurrentAccessToken();
        Assert.assertEquals(accessToken, accessTokenManager.getCurrentAccessToken());
    }

    @Test
    public void testSaveWritesToCacheIfToken() throws JSONException {
        AccessToken accessToken = createAccessToken();
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        accessTokenManager.setCurrentAccessToken(accessToken);
        Mockito.verify(accessTokenCache, Mockito.times(1)).save(ArgumentMatchers.any(AccessToken.class));
    }

    @Test
    public void testSetEmptyTokenClearsCache() {
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        accessTokenManager.setCurrentAccessToken(null);
        Mockito.verify(accessTokenCache, Mockito.times(1)).clear();
    }

    @Test
    public void testLoadDoesNotSave() {
        AccessToken accessToken = createAccessToken();
        when(accessTokenCache.load()).thenReturn(accessToken);
        AccessTokenManager accessTokenManager = createAccessTokenManager();
        accessTokenManager.loadCurrentAccessToken();
        Mockito.verify(accessTokenCache, Mockito.never()).save(ArgumentMatchers.any(AccessToken.class));
    }
}

