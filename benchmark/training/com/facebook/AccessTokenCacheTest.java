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


import AccessTokenCache.CACHED_ACCESS_TOKEN_KEY;
import AccessTokenCache.SharedPreferencesTokenCachingStrategyFactory;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.facebook.internal.Utility;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ AccessTokenCache.class, FacebookSdk.class, LegacyTokenHelper.class, Utility.class })
public class AccessTokenCacheTest extends FacebookPowerMockTestCase {
    private final String TOKEN_STRING = "A token of my esteem";

    private final String USER_ID = "1000";

    private final List<String> PERMISSIONS = Arrays.asList("walk", "chew gum");

    private final Date EXPIRES = new Date(2025, 5, 3);

    private final Date LAST_REFRESH = new Date(2023, 8, 15);

    private final String APP_ID = "1234";

    private SharedPreferences sharedPreferences;

    @Mock
    private LegacyTokenHelper cachingStrategy;

    private SharedPreferencesTokenCachingStrategyFactory cachingStrategyFactory;

    @Test
    public void testLoadReturnsFalseIfNoCachedToken() {
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken accessToken = cache.load();
        Assert.assertNull(accessToken);
        PowerMockito.verifyZeroInteractions(cachingStrategy);
    }

    @Test
    public void testLoadReturnsFalseIfNoCachedOrLegacyToken() {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken accessToken = cache.load();
        Assert.assertNull(accessToken);
    }

    @Test
    public void testLoadReturnsFalseIfEmptyCachedTokenAndDoesNotCheckLegacy() {
        JSONObject jsonObject = new JSONObject();
        sharedPreferences.edit().putString(CACHED_ACCESS_TOKEN_KEY, jsonObject.toString()).commit();
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken accessToken = cache.load();
        Assert.assertNull(accessToken);
        Mockito.verifyZeroInteractions(cachingStrategy);
    }

    @Test
    public void testLoadReturnsFalseIfNoCachedTokenAndEmptyLegacyToken() {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        when(cachingStrategy.load()).thenReturn(new Bundle());
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken accessToken = cache.load();
        Assert.assertNull(accessToken);
    }

    @Test
    public void testLoadValidCachedToken() throws JSONException {
        AccessToken accessToken = createAccessToken();
        JSONObject jsonObject = accessToken.toJSONObject();
        sharedPreferences.edit().putString(CACHED_ACCESS_TOKEN_KEY, jsonObject.toString()).commit();
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken loadedAccessToken = cache.load();
        Assert.assertNotNull(loadedAccessToken);
        Assert.assertEquals(accessToken, loadedAccessToken);
    }

    @Test
    public void testLoadSetsCurrentTokenIfNoCachedTokenButValidLegacyToken() {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        AccessToken accessToken = createAccessToken();
        when(cachingStrategy.load()).thenReturn(AccessTokenTestHelper.toLegacyCacheBundle(accessToken));
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        AccessToken loadedAccessToken = cache.load();
        Assert.assertNotNull(loadedAccessToken);
        Assert.assertEquals(accessToken, loadedAccessToken);
    }

    @Test
    public void testLoadSavesTokenWhenUpgradingFromLegacyToken() throws JSONException {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        AccessToken accessToken = createAccessToken();
        when(cachingStrategy.load()).thenReturn(AccessTokenTestHelper.toLegacyCacheBundle(accessToken));
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        cache.load();
        Assert.assertTrue(sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY));
        AccessToken savedAccessToken = AccessToken.createFromJSONObject(new JSONObject(sharedPreferences.getString(CACHED_ACCESS_TOKEN_KEY, null)));
        Assert.assertEquals(accessToken, savedAccessToken);
    }

    @Test
    public void testLoadClearsLegacyCacheWhenUpgradingFromLegacyToken() {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        AccessToken accessToken = createAccessToken();
        when(cachingStrategy.load()).thenReturn(AccessTokenTestHelper.toLegacyCacheBundle(accessToken));
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        cache.load();
        Mockito.verify(cachingStrategy, Mockito.times(1)).clear();
    }

    @Test
    public void testSaveRequiresToken() {
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        try {
            cache.save(null);
            Assert.fail();
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testSaveWritesToCacheIfToken() throws JSONException {
        AccessToken accessToken = createAccessToken();
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        cache.save(accessToken);
        Mockito.verify(cachingStrategy, Mockito.never()).save(ArgumentMatchers.any(Bundle.class));
        Assert.assertTrue(sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY));
        AccessToken savedAccessToken = AccessToken.createFromJSONObject(new JSONObject(sharedPreferences.getString(CACHED_ACCESS_TOKEN_KEY, null)));
        Assert.assertEquals(accessToken, savedAccessToken);
    }

    @Test
    public void testClearCacheClearsCache() {
        AccessToken accessToken = createAccessToken();
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        cache.save(accessToken);
        cache.clear();
        Assert.assertFalse(sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY));
        Mockito.verify(cachingStrategy, Mockito.never()).clear();
    }

    @Test
    public void testClearCacheClearsLegacyCache() {
        when(FacebookSdk.isLegacyTokenUpgradeSupported()).thenReturn(true);
        AccessToken accessToken = createAccessToken();
        AccessTokenCache cache = new AccessTokenCache(sharedPreferences, cachingStrategyFactory);
        cache.save(accessToken);
        cache.clear();
        Assert.assertFalse(sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY));
        Mockito.verify(cachingStrategy, Mockito.times(1)).clear();
    }
}

