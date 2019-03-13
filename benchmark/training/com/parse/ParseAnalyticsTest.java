/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParsePushBroadcastReceiver.KEY_PUSH_DATA;
import android.content.Intent;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For android.os.BaseBundle
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseAnalyticsTest {
    ParseAnalyticsController controller;

    // No need to test ParseAnalytics since it has no instance fields and all methods are static.
    @Test
    public void testGetAnalyticsController() {
        Assert.assertSame(controller, ParseAnalytics.getAnalyticsController());
    }

    // region trackEventInBackground
    @Test(expected = IllegalArgumentException.class)
    public void testTrackEventInBackgroundNullName() throws Exception {
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrackEventInBackgroundEmptyName() throws Exception {
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground(""));
    }

    @Test
    public void testTrackEventInBackgroundNormalName() throws Exception {
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground("test"));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), Matchers.<Map<String, String>>eq(null), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackEventInBackgroundNullParameters() throws Exception {
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground("test", ((Map<String, String>) (null))));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), Matchers.<Map<String, String>>eq(null), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackEventInBackgroundEmptyParameters() throws Exception {
        Map<String, String> dimensions = new HashMap<>();
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground("test", dimensions));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.eq(dimensions), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackEventInBackgroundNormalParameters() throws Exception {
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("key", "value");
        ParseTaskUtils.wait(ParseAnalytics.trackEventInBackground("test", dimensions));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.eq(dimensions), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackEventInBackgroundNullCallback() {
        Map<String, String> dimensions = new HashMap<>();
        ParseAnalytics.trackEventInBackground("test", dimensions, null);
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.eq(dimensions), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackEventInBackgroundNormalCallback() throws Exception {
        final Map<String, String> dimensions = new HashMap<>();
        dimensions.put("key", "value");
        final Semaphore done = new Semaphore(0);
        ParseAnalytics.trackEventInBackground("test", dimensions, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Assert.assertNull(e);
                done.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.eq(dimensions), ArgumentMatchers.isNull(String.class));
        final Semaphore doneAgain = new Semaphore(0);
        ParseAnalytics.trackEventInBackground("test", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Assert.assertNull(e);
                doneAgain.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(doneAgain.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).trackEventInBackground(ArgumentMatchers.eq("test"), Matchers.<Map<String, String>>eq(null), ArgumentMatchers.isNull(String.class));
    }

    // endregion
    // region testTrackAppOpenedInBackground
    @Test
    public void testTrackAppOpenedInBackgroundNullIntent() throws Exception {
        ParseTaskUtils.wait(ParseAnalytics.trackAppOpenedInBackground(null));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackAppOpenedInBackgroundEmptyIntent() throws Exception {
        Intent intent = new Intent();
        ParseTaskUtils.wait(ParseAnalytics.trackAppOpenedInBackground(intent));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.isNull(String.class), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackAppOpenedInBackgroundNormalIntent() throws Exception {
        Intent intent = makeIntentWithParseData("test");
        ParseTaskUtils.wait(ParseAnalytics.trackAppOpenedInBackground(intent));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackAppOpenedInBackgroundDuplicatedIntent() throws Exception {
        Intent intent = makeIntentWithParseData("test");
        ParseTaskUtils.wait(ParseAnalytics.trackAppOpenedInBackground(intent));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.isNull(String.class));
        ParseTaskUtils.wait(ParseAnalytics.trackAppOpenedInBackground(intent));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackAppOpenedInBackgroundNullCallback() throws Exception {
        Intent intent = makeIntentWithParseData("test");
        ParseAnalytics.trackAppOpenedInBackground(intent, null);
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testTrackAppOpenedInBackgroundNormalCallback() throws Exception {
        Intent intent = makeIntentWithParseData("test");
        final Semaphore done = new Semaphore(0);
        ParseAnalytics.trackAppOpenedInBackground(intent, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Assert.assertNull(e);
                done.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).trackAppOpenedInBackground(ArgumentMatchers.eq("test"), ArgumentMatchers.isNull(String.class));
    }

    // endregion
    // region testGetPushHashFromIntent
    @Test
    public void testGetPushHashFromIntentNullIntent() {
        String pushHash = ParseAnalytics.getPushHashFromIntent(null);
        Assert.assertEquals(null, pushHash);
    }

    @Test
    public void testGetPushHashFromIntentEmptyIntent() throws Exception {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        JSONObject json = new JSONObject();
        json.put("push_hash_wrong_key", "test");
        bundle.putString("data_wrong_key", json.toString());
        intent.putExtras(bundle);
        String pushHash = ParseAnalytics.getPushHashFromIntent(intent);
        Assert.assertEquals(null, pushHash);
    }

    @Test
    public void testGetPushHashFromIntentEmptyPushHashIntent() throws Exception {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        JSONObject json = new JSONObject();
        json.put("push_hash_wrong_key", "test");
        bundle.putString(KEY_PUSH_DATA, json.toString());
        intent.putExtras(bundle);
        String pushHash = ParseAnalytics.getPushHashFromIntent(intent);
        Assert.assertEquals("", pushHash);
    }

    @Test
    public void testGetPushHashFromIntentWrongPushHashIntent() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PUSH_DATA, "error_data");
        intent.putExtras(bundle);
        String pushHash = ParseAnalytics.getPushHashFromIntent(intent);
        Assert.assertEquals(null, pushHash);
    }

    @Test
    public void testGetPushHashFromIntentNormalIntent() throws Exception {
        Intent intent = makeIntentWithParseData("test");
        String pushHash = ParseAnalytics.getPushHashFromIntent(intent);
        Assert.assertEquals("test", pushHash);
    }
}

