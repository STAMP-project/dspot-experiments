/**
 * Copyright 2016 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import BundleProtocol.PACKED_PARAM_CONTENT_URI_ARRAY;
import BundleProtocol.PACKED_PARAM_CONTENT_URI_FLAGS_ARRAY;
import Constraint.DEVICE_CHARGING;
import Constraint.DEVICE_IDLE;
import Constraint.ON_ANY_NETWORK;
import Constraint.ON_UNMETERED_NETWORK;
import ContactsContract.AUTHORITY_URI;
import Flags.FLAG_NOTIFY_FOR_DESCENDANTS;
import GooglePlayJobWriter.LEGACY_NETWORK_ANY;
import GooglePlayJobWriter.LEGACY_NETWORK_CONNECTED;
import GooglePlayJobWriter.LEGACY_NETWORK_UNMETERED;
import GooglePlayJobWriter.LEGACY_RETRY_POLICY_EXPONENTIAL;
import GooglePlayJobWriter.LEGACY_RETRY_POLICY_LINEAR;
import JobTrigger.ExecutionWindowTrigger;
import Lifetime.FOREVER;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import RetryStrategy.DEFAULT_LINEAR;
import Trigger.NOW;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;
import com.firebase.jobdispatcher.ObservedUri.Flags;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static Lifetime.UNTIL_NEXT_BOOT;


/**
 * Tests for the {@link GooglePlayJobWriter} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class GooglePlayJobWriterTest {
    private static final boolean[] ALL_BOOLEANS = new boolean[]{ true, false };

    private GooglePlayJobWriter writer;

    @Test
    public void testWriteToBundle_tags() {
        for (String tag : Arrays.asList("foo", "bar", "foobar", "this is a tag")) {
            Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setTag(tag).build(), new Bundle());
            Assert.assertEquals("tag", tag, b.getString("tag"));
        }
    }

    @Test
    public void testWriteToBundle_updateCurrent() {
        for (boolean replaceCurrent : GooglePlayJobWriterTest.ALL_BOOLEANS) {
            Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setReplaceCurrent(replaceCurrent).build(), new Bundle());
            Assert.assertEquals("update_current", replaceCurrent, b.getBoolean("update_current"));
        }
    }

    @Test
    public void testWriteToBundle_persisted() {
        Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setLifetime(FOREVER).build(), new Bundle());
        Assert.assertTrue("persisted", b.getBoolean("persisted"));
        for (int lifetime : new int[]{ UNTIL_NEXT_BOOT }) {
            b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setLifetime(lifetime).build(), new Bundle());
            Assert.assertFalse("persisted", b.getBoolean("persisted"));
        }
    }

    @Test
    public void testWriteToBundle_service() {
        Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setService(TestJobService.class).build(), new Bundle());
        Assert.assertEquals("service", GooglePlayReceiver.class.getName(), b.getString("service"));
    }

    @Test
    public void testWriteToBundle_requiredNetwork() {
        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(ON_ANY_NETWORK, LEGACY_NETWORK_CONNECTED);
        mapping.put(ON_UNMETERED_NETWORK, LEGACY_NETWORK_UNMETERED);
        mapping.put(0, LEGACY_NETWORK_ANY);
        for (Map.Entry<Integer, Integer> testCase : mapping.entrySet()) {
            @SuppressWarnings("WrongConstant")
            Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(testCase.getKey()).build(), new Bundle());
            Assert.assertEquals("requiredNetwork", ((int) (testCase.getValue())), b.getInt("requiredNetwork"));
        }
    }

    @Test
    public void testWriteToBundle_unmeteredConstraintShouldTakePrecendence() {
        Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(ON_ANY_NETWORK, ON_UNMETERED_NETWORK).build(), new Bundle());
        Assert.assertEquals("expected ON_UNMETERED_NETWORK to take precendence over ON_ANY_NETWORK", LEGACY_NETWORK_UNMETERED, b.getInt("requiredNetwork"));
    }

    @Test
    public void testWriteToBundle_requiresCharging() {
        Assert.assertTrue("requiresCharging", writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(DEVICE_CHARGING).build(), new Bundle()).getBoolean("requiresCharging"));
        for (Integer constraint : Arrays.asList(ON_ANY_NETWORK, ON_UNMETERED_NETWORK)) {
            Assert.assertFalse("requiresCharging", writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(constraint).build(), new Bundle()).getBoolean("requiresCharging"));
        }
    }

    @Test
    public void testWriteToBundle_requiresIdle() {
        Assert.assertTrue("requiresIdle", writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(DEVICE_IDLE).build(), new Bundle()).getBoolean("requiresIdle"));
        for (Integer constraint : Arrays.asList(ON_ANY_NETWORK, ON_UNMETERED_NETWORK)) {
            Assert.assertFalse("requiresIdle", writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setConstraints(constraint).build(), new Bundle()).getBoolean("requiresIdle"));
        }
    }

    @Test
    public void testWriteToBundle_retryPolicy() {
        Assert.assertEquals("retry_policy", LEGACY_RETRY_POLICY_EXPONENTIAL, writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setRetryStrategy(DEFAULT_EXPONENTIAL).build(), new Bundle()).getBundle("retryStrategy").getInt("retry_policy"));
        Assert.assertEquals("retry_policy", LEGACY_RETRY_POLICY_LINEAR, writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setRetryStrategy(DEFAULT_LINEAR).build(), new Bundle()).getBundle("retryStrategy").getInt("retry_policy"));
    }

    @Test
    public void testWriteToBundle_backoffSeconds() {
        for (RetryStrategy retryStrategy : Arrays.asList(DEFAULT_EXPONENTIAL, DEFAULT_LINEAR)) {
            Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setRetryStrategy(retryStrategy).build(), new Bundle()).getBundle("retryStrategy");
            Assert.assertEquals("initial_backoff_seconds", retryStrategy.getInitialBackoff(), b.getInt("initial_backoff_seconds"));
            Assert.assertEquals("maximum_backoff_seconds", retryStrategy.getMaximumBackoff(), b.getInt("maximum_backoff_seconds"));
        }
    }

    @Test
    public void testWriteToBundle_triggers() {
        // immediate
        Bundle b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setTrigger(NOW).build(), new Bundle());
        Assert.assertEquals("window_start", 0, b.getLong("window_start"));
        Assert.assertEquals("window_end", 1, b.getLong("window_end"));
        // execution window (oneoff)
        JobTrigger.ExecutionWindowTrigger t = Trigger.executionWindow(631, 978);
        b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setTrigger(t).build(), new Bundle());
        Assert.assertEquals("window_start", t.getWindowStart(), b.getLong("window_start"));
        Assert.assertEquals("window_end", t.getWindowEnd(), b.getLong("window_end"));
        // execution window (periodic)
        b = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setRecurring(true).setTrigger(t).build(), new Bundle());
        Assert.assertEquals("period", t.getWindowEnd(), b.getLong("period"));
        Assert.assertEquals("period_flex", ((t.getWindowEnd()) - (t.getWindowStart())), b.getLong("period_flex"));
    }

    @Test
    public void testWriteToBundle_contentUriTrigger() {
        ObservedUri observedUri = new ObservedUri(ContactsContract.AUTHORITY_URI, Flags.FLAG_NOTIFY_FOR_DESCENDANTS);
        ContentUriTrigger contentUriTrigger = Trigger.contentUriTrigger(Arrays.asList(observedUri));
        Bundle bundle = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setTrigger(contentUriTrigger).build(), new Bundle());
        Uri[] uris = ((Uri[]) (bundle.getParcelableArray(PACKED_PARAM_CONTENT_URI_ARRAY)));
        int[] flags = bundle.getIntArray(PACKED_PARAM_CONTENT_URI_FLAGS_ARRAY);
        Assert.assertTrue("Array size", (((uris.length) == (flags.length)) && ((flags.length) == 1)));
        Assert.assertEquals(PACKED_PARAM_CONTENT_URI_ARRAY, AUTHORITY_URI, uris[0]);
        Assert.assertEquals(PACKED_PARAM_CONTENT_URI_FLAGS_ARRAY, FLAG_NOTIFY_FOR_DESCENDANTS, flags[0]);
    }

    @Test
    public void testWriteToBundle_extras() {
        Bundle extras = new Bundle();
        extras.putString("bar", "foo");
        extras.putInt("an_int", 1);
        extras.putBoolean("a bool", true);
        Bundle result = writer.writeToBundle(GooglePlayJobWriterTest.initializeDefaultBuilder().setExtras(extras).build(), new Bundle()).getBundle("extras");
        // Can't use assertBundlesEqual because we write non-user metadata to the extras Bundle, so
        // there'll always be unexpected values. Instead, check that all the user-provided extras are
        // there.
        for (String key : extras.keySet()) {
            Assert.assertEquals(((('"' + key) + '"') + " mismatch"), extras.get(key), result.get(key));
        }
    }
}

