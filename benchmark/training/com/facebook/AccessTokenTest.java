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


import AccessTokenSource.FACEBOOK_APPLICATION_WEB;
import RuntimeEnvironment.application;
import android.os.Bundle;
import com.facebook.internal.Utility;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static AccessTokenSource.CLIENT_TOKEN;
import static AccessTokenSource.FACEBOOK_APPLICATION_WEB;
import static AccessTokenSource.WEB_VIEW;


@PrepareForTest({ Utility.class })
public final class AccessTokenTest extends FacebookPowerMockTestCase {
    @Test
    public void testNullTokenThrows() {
        try {
            AccessToken token = new AccessToken(null, "1234", "1000", Utility.arrayList("something"), Utility.arrayList("something_else"), CLIENT_TOKEN, new Date(), new Date(), new Date());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testEmptyTokenThrows() {
        try {
            AccessToken token = new AccessToken("", "1234", "1000", Utility.arrayList("something"), Utility.arrayList("something_else"), CLIENT_TOKEN, new Date(), new Date(), new Date());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testNullUserIdThrows() {
        try {
            AccessToken token = new AccessToken("a token", "1234", null, Utility.arrayList("something"), Utility.arrayList("something_else"), CLIENT_TOKEN, new Date(), new Date(), new Date());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testEmptyUserIdThrows() {
        try {
            AccessToken token = new AccessToken("a token", "1234", "", Utility.arrayList("something"), Utility.arrayList("something_else"), CLIENT_TOKEN, new Date(), new Date(), new Date());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCreateFromRefreshFailure() {
        AccessToken accessToken = new AccessToken("a token", "1234", "1000", Utility.arrayList("stream_publish"), null, WEB_VIEW, null, null, null);
        String token = "AnImaginaryTokenValue";
        Bundle bundle = new Bundle();
        bundle.putString("access_token", "AnImaginaryTokenValue");
        bundle.putString("expires_in", "60");
        try {
            AccessToken.createFromRefresh(accessToken, bundle);
            Assert.fail("Expected exception");
        } catch (FacebookException ex) {
            Assert.assertEquals(("Invalid token source: " + (WEB_VIEW)), ex.getMessage());
        }
    }

    @Test
    public void testCacheRoundtrip() {
        Set<String> permissions = Utility.hashSet("stream_publish", "go_outside_and_play");
        Set<String> declinedPermissions = Utility.hashSet("no you may not", "no soup for you");
        String token = "AnImaginaryTokenValue";
        Date later = TestUtils.nowPlusSeconds(60);
        Date earlier = TestUtils.nowPlusSeconds((-60));
        String applicationId = "1234";
        Bundle bundle = new Bundle();
        LegacyTokenHelper.putToken(bundle, token);
        LegacyTokenHelper.putExpirationDate(bundle, later);
        LegacyTokenHelper.putSource(bundle, FACEBOOK_APPLICATION_WEB);
        LegacyTokenHelper.putLastRefreshDate(bundle, earlier);
        LegacyTokenHelper.putPermissions(bundle, permissions);
        LegacyTokenHelper.putDeclinedPermissions(bundle, declinedPermissions);
        LegacyTokenHelper.putApplicationId(bundle, applicationId);
        AccessToken accessToken = AccessToken.createFromLegacyCache(bundle);
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(FACEBOOK_APPLICATION_WEB, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
        Bundle cache = AccessTokenTestHelper.toLegacyCacheBundle(accessToken);
        TestUtils.assertEqualContentsWithoutOrder(bundle, cache);
    }

    @Test
    public void testFromCacheWithMissingApplicationId() {
        String token = "AnImaginaryTokenValue";
        String applicationId = "1234";
        Bundle bundle = new Bundle();
        LegacyTokenHelper.putToken(bundle, token);
        // no app id
        FacebookSdk.setApplicationId(applicationId);
        FacebookSdk.setAutoLogAppEventsEnabled(false);
        FacebookSdk.sdkInitialize(application);
        AccessToken accessToken = AccessToken.createFromLegacyCache(bundle);
        Assert.assertEquals(applicationId, accessToken.getApplicationId());
    }

    @Test
    public void testCachePutGet() {
        Bundle bundle = new Bundle();
        for (String token : new String[]{ "", "A completely random token value" }) {
            LegacyTokenHelper.putToken(bundle, token);
            Assert.assertEquals(token, LegacyTokenHelper.getToken(bundle));
        }
        for (Date date : new Date[]{ new Date(42), new Date() }) {
            LegacyTokenHelper.putExpirationDate(bundle, date);
            Assert.assertEquals(date, LegacyTokenHelper.getExpirationDate(bundle));
            LegacyTokenHelper.putLastRefreshDate(bundle, date);
            Assert.assertEquals(date, LegacyTokenHelper.getLastRefreshDate(bundle));
        }
        for (long milliseconds : new long[]{ 0, -1, System.currentTimeMillis() }) {
            LegacyTokenHelper.putExpirationMilliseconds(bundle, milliseconds);
            Assert.assertEquals(milliseconds, LegacyTokenHelper.getExpirationMilliseconds(bundle));
            LegacyTokenHelper.putLastRefreshMilliseconds(bundle, milliseconds);
            Assert.assertEquals(milliseconds, LegacyTokenHelper.getLastRefreshMilliseconds(bundle));
        }
        for (AccessTokenSource source : AccessTokenSource.values()) {
            LegacyTokenHelper.putSource(bundle, source);
            Assert.assertEquals(source, LegacyTokenHelper.getSource(bundle));
        }
        String userId = "1000";
        List<String> normalList = Arrays.asList("", "Another completely random token value");
        List<String> emptyList = Collections.emptyList();
        HashSet<String> normalArrayList = new HashSet<String>(normalList);
        HashSet<String> emptyArrayList = new HashSet<String>();
        @SuppressWarnings("unchecked")
        List<Collection<String>> permissionLists = Arrays.asList(normalList, emptyList, normalArrayList, emptyArrayList);
        for (Collection<String> list : permissionLists) {
            LegacyTokenHelper.putPermissions(bundle, list);
            TestUtils.assertSamePermissions(list, LegacyTokenHelper.getPermissions(bundle));
        }
        normalArrayList.add(null);
    }

    @Test
    public void testRoundtripJSONObject() throws JSONException {
        AccessToken accessToken = new AccessToken("a token", "1234", "1000", Arrays.asList("permission_1", "permission_2"), Arrays.asList("declined permission_1", "declined permission_2"), WEB_VIEW, new Date(2015, 3, 3), new Date(2015, 1, 1), new Date(2015, 3, 3));
        JSONObject jsonObject = accessToken.toJSONObject();
        AccessToken deserializedAccessToken = AccessToken.createFromJSONObject(jsonObject);
        Assert.assertEquals(accessToken, deserializedAccessToken);
    }

    @Test
    public void testJSONObjectWithoutDataAccess() throws JSONException {
        AccessToken accessToken = new AccessToken("a token", "1234", "1000", Arrays.asList("permission_1", "permission_2"), Arrays.asList("declined permission_1", "declined permission_2"), WEB_VIEW, new Date(2015, 3, 3), new Date(2015, 1, 1), new Date(0));
        JSONObject jsonObject = accessToken.toJSONObject();
        jsonObject.remove("data_access_expiration_time");
        AccessToken deserializedAccessToken = AccessToken.createFromJSONObject(jsonObject);
        Assert.assertEquals(accessToken, deserializedAccessToken);
    }

    @Test
    public void testParceling() throws IOException {
        String token = "a token";
        String appId = "1234";
        String userId = "1000";
        Set<String> permissions = new HashSet<String>(Arrays.asList("permission_1", "permission_2"));
        Set<String> declinedPermissions = new HashSet<String>(Collections.singletonList("permission_3"));
        AccessTokenSource source = WEB_VIEW;
        AccessToken accessToken1 = new AccessToken(token, appId, userId, permissions, declinedPermissions, source, null, null, null);
        AccessToken accessToken2 = TestUtils.parcelAndUnparcel(accessToken1);
        Assert.assertEquals(accessToken1, accessToken2);
        Assert.assertEquals(token, accessToken2.getToken());
        Assert.assertEquals(appId, accessToken2.getApplicationId());
        Assert.assertEquals(permissions, accessToken2.getPermissions());
        Assert.assertEquals(declinedPermissions, accessToken2.getDeclinedPermissions());
        Assert.assertEquals(accessToken1.getExpires(), accessToken2.getExpires());
        Assert.assertEquals(accessToken1.getLastRefresh(), accessToken2.getLastRefresh());
        Assert.assertEquals(accessToken1.getUserId(), accessToken2.getUserId());
        Assert.assertEquals(accessToken1.getExpires(), accessToken2.getExpires());
        Assert.assertEquals(accessToken1.getDataAccessExpirationTime(), accessToken2.getDataAccessExpirationTime());
    }

    @Test
    public void testPermissionsAreImmutable() {
        Set<String> permissions = Utility.hashSet("go to Jail", "do not pass Go");
        AccessToken accessToken = new AccessToken("some token", "1234", "1000", permissions, null, FACEBOOK_APPLICATION_WEB, new Date(), new Date(), new Date());
        permissions = accessToken.getPermissions();
        try {
            permissions.add("can't touch this");
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
        }
    }

    @Test
    public void testCreateFromExistingTokenDefaults() {
        final String token = "A token of my esteem";
        final String applicationId = "1234";
        final String userId = "1000";
        AccessToken accessToken = new AccessToken(token, applicationId, userId, null, null, null, null, null, null);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(new Date(Long.MAX_VALUE), accessToken.getExpires());
        Assert.assertEquals(FACEBOOK_APPLICATION_WEB, accessToken.getSource());
        Assert.assertEquals(0, accessToken.getPermissions().size());
        Assert.assertEquals(applicationId, accessToken.getApplicationId());
        Assert.assertEquals(userId, accessToken.getUserId());
        // Allow slight variation for test execution time
        long delta = (accessToken.getLastRefresh().getTime()) - (new Date().getTime());
        Assert.assertTrue((delta < 1000));
    }

    @Test
    public void testAccessTokenConstructor() {
        final String token = "A token of my esteem";
        final Set<String> permissions = Utility.hashSet("walk", "chew gum");
        final Set<String> declinedPermissions = Utility.hashSet("jump");
        final Date expires = new Date(2025, 5, 3);
        final Date lastRefresh = new Date(2023, 8, 15);
        final Date dataAccessExpirationTime = new Date(2025, 5, 3);
        final AccessTokenSource source = WEB_VIEW;
        final String applicationId = "1234";
        final String userId = "1000";
        AccessToken accessToken = new AccessToken(token, applicationId, userId, permissions, declinedPermissions, source, expires, lastRefresh, dataAccessExpirationTime);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(expires, accessToken.getExpires());
        Assert.assertEquals(lastRefresh, accessToken.getLastRefresh());
        Assert.assertEquals(source, accessToken.getSource());
        Assert.assertEquals(permissions, accessToken.getPermissions());
        Assert.assertEquals(declinedPermissions, accessToken.getDeclinedPermissions());
        Assert.assertEquals(applicationId, accessToken.getApplicationId());
        Assert.assertEquals(userId, accessToken.getUserId());
        Assert.assertEquals(dataAccessExpirationTime, accessToken.getDataAccessExpirationTime());
    }
}

