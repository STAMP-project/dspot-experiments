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


import AccessTokenSource.FACEBOOK_APPLICATION_NATIVE;
import AccessTokenSource.FACEBOOK_APPLICATION_WEB;
import android.os.Bundle;
import com.facebook.internal.Utility;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;


@PrepareForTest({ Utility.class })
public final class LegacyTokenCacheTest extends FacebookPowerMockTestCase {
    private static final String BOOLEAN_KEY = "booleanKey";

    private static final String BOOLEAN_ARRAY_KEY = "booleanArrayKey";

    private static final String BYTE_KEY = "byteKey";

    private static final String BYTE_ARRAY_KEY = "byteArrayKey";

    private static final String SHORT_KEY = "shortKey";

    private static final String SHORT_ARRAY_KEY = "shortArrayKey";

    private static final String INT_KEY = "intKey";

    private static final String INT_ARRAY_KEY = "intArrayKey";

    private static final String LONG_KEY = "longKey";

    private static final String LONG_ARRAY_KEY = "longArrayKey";

    private static final String FLOAT_ARRAY_KEY = "floatKey";

    private static final String FLOAT_KEY = "floatArrayKey";

    private static final String DOUBLE_KEY = "doubleKey";

    private static final String DOUBLE_ARRAY_KEY = "doubleArrayKey";

    private static final String CHAR_KEY = "charKey";

    private static final String CHAR_ARRAY_KEY = "charArrayKey";

    private static final String STRING_KEY = "stringKey";

    private static final String STRING_LIST_KEY = "stringListKey";

    private static final String SERIALIZABLE_KEY = "serializableKey";

    private static Random random = new Random(new Date().getTime());

    @Test
    public void testAllTypes() {
        Bundle originalBundle = new Bundle();
        LegacyTokenCacheTest.putBoolean(LegacyTokenCacheTest.BOOLEAN_KEY, originalBundle);
        LegacyTokenCacheTest.putBooleanArray(LegacyTokenCacheTest.BOOLEAN_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putByte(LegacyTokenCacheTest.BYTE_KEY, originalBundle);
        LegacyTokenCacheTest.putByteArray(LegacyTokenCacheTest.BYTE_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putShort(LegacyTokenCacheTest.SHORT_KEY, originalBundle);
        LegacyTokenCacheTest.putShortArray(LegacyTokenCacheTest.SHORT_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putInt(LegacyTokenCacheTest.INT_KEY, originalBundle);
        LegacyTokenCacheTest.putIntArray(LegacyTokenCacheTest.INT_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putLong(LegacyTokenCacheTest.LONG_KEY, originalBundle);
        LegacyTokenCacheTest.putLongArray(LegacyTokenCacheTest.LONG_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putFloat(LegacyTokenCacheTest.FLOAT_KEY, originalBundle);
        LegacyTokenCacheTest.putFloatArray(LegacyTokenCacheTest.FLOAT_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putDouble(LegacyTokenCacheTest.DOUBLE_KEY, originalBundle);
        LegacyTokenCacheTest.putDoubleArray(LegacyTokenCacheTest.DOUBLE_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putChar(LegacyTokenCacheTest.CHAR_KEY, originalBundle);
        LegacyTokenCacheTest.putCharArray(LegacyTokenCacheTest.CHAR_ARRAY_KEY, originalBundle);
        LegacyTokenCacheTest.putString(LegacyTokenCacheTest.STRING_KEY, originalBundle);
        LegacyTokenCacheTest.putStringList(LegacyTokenCacheTest.STRING_LIST_KEY, originalBundle);
        originalBundle.putSerializable(LegacyTokenCacheTest.SERIALIZABLE_KEY, FACEBOOK_APPLICATION_WEB);
        ensureApplicationContext();
        LegacyTokenHelper cache = new LegacyTokenHelper(RuntimeEnvironment.application);
        cache.save(originalBundle);
        LegacyTokenHelper cache2 = new LegacyTokenHelper(RuntimeEnvironment.application);
        Bundle cachedBundle = cache2.load();
        Assert.assertEquals(originalBundle.getBoolean(LegacyTokenCacheTest.BOOLEAN_KEY), cachedBundle.getBoolean(LegacyTokenCacheTest.BOOLEAN_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getBooleanArray(LegacyTokenCacheTest.BOOLEAN_ARRAY_KEY), cachedBundle.getBooleanArray(LegacyTokenCacheTest.BOOLEAN_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getByte(LegacyTokenCacheTest.BYTE_KEY), cachedBundle.getByte(LegacyTokenCacheTest.BYTE_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getByteArray(LegacyTokenCacheTest.BYTE_ARRAY_KEY), cachedBundle.getByteArray(LegacyTokenCacheTest.BYTE_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getShort(LegacyTokenCacheTest.SHORT_KEY), cachedBundle.getShort(LegacyTokenCacheTest.SHORT_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getShortArray(LegacyTokenCacheTest.SHORT_ARRAY_KEY), cachedBundle.getShortArray(LegacyTokenCacheTest.SHORT_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getInt(LegacyTokenCacheTest.INT_KEY), cachedBundle.getInt(LegacyTokenCacheTest.INT_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getIntArray(LegacyTokenCacheTest.INT_ARRAY_KEY), cachedBundle.getIntArray(LegacyTokenCacheTest.INT_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getLong(LegacyTokenCacheTest.LONG_KEY), cachedBundle.getLong(LegacyTokenCacheTest.LONG_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getLongArray(LegacyTokenCacheTest.LONG_ARRAY_KEY), cachedBundle.getLongArray(LegacyTokenCacheTest.LONG_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getFloat(LegacyTokenCacheTest.FLOAT_KEY), cachedBundle.getFloat(LegacyTokenCacheTest.FLOAT_KEY), TestUtils.DOUBLE_EQUALS_DELTA);
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getFloatArray(LegacyTokenCacheTest.FLOAT_ARRAY_KEY), cachedBundle.getFloatArray(LegacyTokenCacheTest.FLOAT_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getDouble(LegacyTokenCacheTest.DOUBLE_KEY), cachedBundle.getDouble(LegacyTokenCacheTest.DOUBLE_KEY), TestUtils.DOUBLE_EQUALS_DELTA);
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getDoubleArray(LegacyTokenCacheTest.DOUBLE_ARRAY_KEY), cachedBundle.getDoubleArray(LegacyTokenCacheTest.DOUBLE_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getChar(LegacyTokenCacheTest.CHAR_KEY), cachedBundle.getChar(LegacyTokenCacheTest.CHAR_KEY));
        LegacyTokenCacheTest.assertArrayEquals(originalBundle.getCharArray(LegacyTokenCacheTest.CHAR_ARRAY_KEY), cachedBundle.getCharArray(LegacyTokenCacheTest.CHAR_ARRAY_KEY));
        Assert.assertEquals(originalBundle.getString(LegacyTokenCacheTest.STRING_KEY), cachedBundle.getString(LegacyTokenCacheTest.STRING_KEY));
        LegacyTokenCacheTest.assertListEquals(originalBundle.getStringArrayList(LegacyTokenCacheTest.STRING_LIST_KEY), cachedBundle.getStringArrayList(LegacyTokenCacheTest.STRING_LIST_KEY));
        Assert.assertEquals(originalBundle.getSerializable(LegacyTokenCacheTest.SERIALIZABLE_KEY), cachedBundle.getSerializable(LegacyTokenCacheTest.SERIALIZABLE_KEY));
    }

    @Test
    public void testMultipleCaches() {
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putInt(LegacyTokenCacheTest.INT_KEY, 10);
        bundle1.putString(LegacyTokenCacheTest.STRING_KEY, "ABC");
        bundle2.putInt(LegacyTokenCacheTest.INT_KEY, 100);
        bundle2.putString(LegacyTokenCacheTest.STRING_KEY, "xyz");
        ensureApplicationContext();
        LegacyTokenHelper cache1 = new LegacyTokenHelper(RuntimeEnvironment.application);
        LegacyTokenHelper cache2 = new LegacyTokenHelper(RuntimeEnvironment.application, "CustomCache");
        cache1.save(bundle1);
        cache2.save(bundle2);
        // Get new references to make sure we are getting persisted data.
        // Reverse the cache references for fun.
        cache1 = new LegacyTokenHelper(RuntimeEnvironment.application, "CustomCache");
        cache2 = new LegacyTokenHelper(RuntimeEnvironment.application);
        Bundle newBundle1 = cache1.load();
        Bundle newBundle2 = cache2.load();
        Assert.assertEquals(bundle2.getInt(LegacyTokenCacheTest.INT_KEY), newBundle1.getInt(LegacyTokenCacheTest.INT_KEY));
        Assert.assertEquals(bundle2.getString(LegacyTokenCacheTest.STRING_KEY), newBundle1.getString(LegacyTokenCacheTest.STRING_KEY));
        Assert.assertEquals(bundle1.getInt(LegacyTokenCacheTest.INT_KEY), newBundle2.getInt(LegacyTokenCacheTest.INT_KEY));
        Assert.assertEquals(bundle1.getString(LegacyTokenCacheTest.STRING_KEY), newBundle2.getString(LegacyTokenCacheTest.STRING_KEY));
    }

    @Test
    public void testCacheRoundtrip() {
        Set<String> permissions = Utility.hashSet("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";
        Date later = TestUtils.nowPlusSeconds(60);
        Date earlier = TestUtils.nowPlusSeconds((-60));
        String applicationId = "1234";
        LegacyTokenHelper cache = new LegacyTokenHelper(RuntimeEnvironment.application);
        cache.clear();
        Bundle bundle = new Bundle();
        LegacyTokenHelper.putToken(bundle, token);
        LegacyTokenHelper.putExpirationDate(bundle, later);
        LegacyTokenHelper.putSource(bundle, FACEBOOK_APPLICATION_NATIVE);
        LegacyTokenHelper.putLastRefreshDate(bundle, earlier);
        LegacyTokenHelper.putPermissions(bundle, permissions);
        LegacyTokenHelper.putDeclinedPermissions(bundle, Utility.arrayList("whatever"));
        LegacyTokenHelper.putApplicationId(bundle, applicationId);
        cache.save(bundle);
        bundle = cache.load();
        AccessToken accessToken = AccessToken.createFromLegacyCache(bundle);
        TestUtils.assertSamePermissions(permissions, accessToken);
        Assert.assertEquals(token, accessToken.getToken());
        Assert.assertEquals(FACEBOOK_APPLICATION_NATIVE, accessToken.getSource());
        Assert.assertTrue((!(accessToken.isExpired())));
        Bundle cachedBundle = AccessTokenTestHelper.toLegacyCacheBundle(accessToken);
        TestUtils.assertEqualContentsWithoutOrder(bundle, cachedBundle);
    }
}

