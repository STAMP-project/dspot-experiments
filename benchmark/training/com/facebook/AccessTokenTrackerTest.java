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


import android.support.v4.content.LocalBroadcastManager;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ FacebookSdk.class })
public class AccessTokenTrackerTest extends FacebookPowerMockTestCase {
    private final List<String> PERMISSIONS = Arrays.asList("walk", "chew gum");

    private final Date EXPIRES = new Date(2025, 5, 3);

    private final Date LAST_REFRESH = new Date(2023, 8, 15);

    private final Date DATA_ACCESS_EXPIRATION_TIME = new Date(2025, 5, 3);

    private final String APP_ID = "1234";

    private final String USER_ID = "1000";

    private LocalBroadcastManager localBroadcastManager;

    private AccessTokenTrackerTest.TestAccessTokenTracker accessTokenTracker = null;

    @Test
    public void testRequiresSdkToBeInitialized() {
        try {
            when(FacebookSdk.isInitialized()).thenReturn(false);
            accessTokenTracker = new AccessTokenTrackerTest.TestAccessTokenTracker();
            Assert.fail();
        } catch (FacebookSdkNotInitializedException exception) {
        }
    }

    @Test
    public void testDefaultsToTracking() {
        accessTokenTracker = new AccessTokenTrackerTest.TestAccessTokenTracker();
        Assert.assertTrue(isTracking());
    }

    @Test
    public void testCanTurnTrackingOff() {
        accessTokenTracker = new AccessTokenTrackerTest.TestAccessTokenTracker();
        stopTracking();
        Assert.assertFalse(isTracking());
    }

    @Test
    public void testCanTurnTrackingOn() {
        accessTokenTracker = new AccessTokenTrackerTest.TestAccessTokenTracker();
        stopTracking();
        startTracking();
        Assert.assertTrue(isTracking());
    }

    @Test
    public void testCallbackCalledOnBroadcastReceived() throws Exception {
        accessTokenTracker = new AccessTokenTrackerTest.TestAccessTokenTracker();
        AccessToken oldAccessToken = createAccessToken("I'm old!");
        AccessToken currentAccessToken = createAccessToken("I'm current!");
        sendBroadcast(oldAccessToken, currentAccessToken);
        Assert.assertNotNull(accessTokenTracker.currentAccessToken);
        Assert.assertEquals(currentAccessToken.getToken(), accessTokenTracker.currentAccessToken.getToken());
        Assert.assertNotNull(accessTokenTracker.oldAccessToken);
        Assert.assertEquals(oldAccessToken.getToken(), accessTokenTracker.oldAccessToken.getToken());
    }

    class TestAccessTokenTracker extends AccessTokenTracker {
        public AccessToken currentAccessToken;

        public AccessToken oldAccessToken;

        public TestAccessTokenTracker() {
            super();
        }

        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            this.oldAccessToken = oldAccessToken;
            this.currentAccessToken = currentAccessToken;
        }
    }
}

