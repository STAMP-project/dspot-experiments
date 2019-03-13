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


import RuntimeEnvironment.application;
import android.support.v4.content.LocalBroadcastManager;
import org.junit.Assert;
import org.junit.Test;


public class ProfileTrackerTest extends FacebookPowerMockTestCase {
    @Test
    public void testStartStopTrackingAndBroadcast() {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.setAutoLogAppEventsEnabled(false);
        FacebookSdk.sdkInitialize(application);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(application);
        ProfileTrackerTest.TestProfileTracker testProfileTracker = new ProfileTrackerTest.TestProfileTracker();
        // Starts tracking
        Assert.assertTrue(isTracking());
        stopTracking();
        Assert.assertFalse(isTracking());
        ProfileTrackerTest.sendBroadcast(localBroadcastManager, null, ProfileTest.createDefaultProfile());
        Assert.assertFalse(testProfileTracker.isCallbackCalled);
        startTracking();
        Assert.assertTrue(isTracking());
        Profile profile = ProfileTest.createDefaultProfile();
        ProfileTrackerTest.sendBroadcast(localBroadcastManager, null, profile);
        Assert.assertNull(testProfileTracker.oldProfile);
        Assert.assertEquals(profile, testProfileTracker.currentProfile);
        Assert.assertTrue(testProfileTracker.isCallbackCalled);
        Profile profile1 = ProfileTest.createMostlyNullsProfile();
        Profile profile2 = ProfileTest.createDefaultProfile();
        ProfileTrackerTest.sendBroadcast(localBroadcastManager, profile1, profile2);
        ProfileTest.assertMostlyNullsObjectGetters(testProfileTracker.oldProfile);
        ProfileTest.assertDefaultObjectGetters(testProfileTracker.currentProfile);
        Assert.assertEquals(profile1, testProfileTracker.oldProfile);
        Assert.assertEquals(profile2, testProfileTracker.currentProfile);
        stopTracking();
    }

    static class TestProfileTracker extends ProfileTracker {
        Profile oldProfile;

        Profile currentProfile;

        boolean isCallbackCalled = false;

        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            this.oldProfile = oldProfile;
            this.currentProfile = currentProfile;
            isCallbackCalled = true;
        }
    }
}

