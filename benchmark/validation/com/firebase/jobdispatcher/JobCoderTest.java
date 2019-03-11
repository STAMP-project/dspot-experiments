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


import BundleProtocol.PACKED_PARAM_TRIGGERED_URIS;
import BundleProtocol.TRIGGER_TYPE_CONTENT_URI;
import ContactsContract.AUTHORITY_URI;
import GooglePlayJobWriter.REQUEST_PARAM_EXTRAS;
import Media.EXTERNAL_CONTENT_URI;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import android.net.Uri;
import android.os.Bundle;
import com.firebase.jobdispatcher.Job.Builder;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static BundleProtocol.PACKED_PARAM_OBSERVED_URI;
import static BundleProtocol.PACKED_PARAM_TRIGGER_TYPE;


/**
 * Tests for the {@link JobCoder} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class JobCoderTest {
    private final JobCoder coder = new JobCoder(JobCoderTest.PREFIX);

    private static final String PREFIX = "prefix";

    private Builder builder;

    @Test
    public void testCodingIsLossless() {
        for (JobParameters input : TestUtil.getJobCombinations(builder)) {
            TestUtil.assertJobsEqual(input, coder.decode(coder.encode(input, new Bundle())).build());
        }
    }

    @Test
    public void testCodingForExtras() {
        Bundle extras = new Bundle();
        extras.putString("foo", "bar");
        builder.setExtras(extras);
        Bundle deserializedExtras = coder.decode(coder.encode(JobCoderTest.setValidBuilderDefaults(builder).build(), new Bundle())).build().getExtras();
        TestUtil.assertBundlesEqual(extras, deserializedExtras);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncode_throwsOnNullBundle() {
        coder.encode(builder.build(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecode_throwsOnNullBundle() {
        coder.decode(null);
    }

    @Test
    public void testDecode_failsWhenMissingFields() {
        Assert.assertNull("Expected null tag to cause decoding to fail", coder.decode(coder.encode(JobCoderTest.setValidBuilderDefaults(builder).setTag(null).build(), new Bundle())));
        Assert.assertNull("Expected null service to cause decoding to fail", coder.decode(coder.encode(JobCoderTest.setValidBuilderDefaults(builder).setService(null).build(), new Bundle())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecode_failsUnsupportedTrigger() {
        coder.decode(coder.encode(JobCoderTest.setValidBuilderDefaults(builder).setTrigger(null).build(), new Bundle()));
    }

    @Test
    public void testDecode_ignoresMissingRetryStrategy() {
        Assert.assertNotNull("Expected null retry strategy to cause decode to use a default", coder.decode(coder.encode(JobCoderTest.setValidBuilderDefaults(builder).setRetryStrategy(null).build(), new Bundle())));
    }

    @Test
    public void encode_contentUriTrigger() {
        Bundle encode = TestUtil.encodeContentUriJob(TestUtil.getContentUriTrigger(), coder);
        int triggerType = encode.getInt(((JobCoderTest.PREFIX) + (PACKED_PARAM_TRIGGER_TYPE)));
        Assert.assertEquals("Trigger type", TRIGGER_TYPE_CONTENT_URI, triggerType);
        String json = encode.getString(((JobCoderTest.PREFIX) + (PACKED_PARAM_OBSERVED_URI)));
        String expectedJson = "{\"uri_flags\":[1,0],\"uris\":[\"content:\\/\\/com.android.contacts" + "\",\"content:\\/\\/media\\/external\\/images\\/media\"]}";
        Assert.assertEquals("Json trigger", expectedJson, json);
    }

    @Test
    public void decode_contentUriTrigger() {
        ContentUriTrigger contentUriTrigger = TestUtil.getContentUriTrigger();
        Bundle bundle = TestUtil.encodeContentUriJob(contentUriTrigger, coder);
        JobInvocation decode = coder.decode(bundle).build();
        ContentUriTrigger trigger = ((ContentUriTrigger) (decode.getTrigger()));
        Assert.assertEquals(contentUriTrigger.getUris(), trigger.getUris());
    }

    @Test
    public void decode_addBundleAsExtras() {
        ContentUriTrigger contentUriTrigger = TestUtil.getContentUriTrigger();
        Bundle bundle = TestUtil.encodeContentUriJob(contentUriTrigger, coder);
        bundle.putString("test_key", "test_value");
        JobInvocation decode = coder.decode(bundle).build();
        Assert.assertEquals("test_value", decode.getExtras().getString("test_key"));
    }

    @Test
    public void decodeIntentBundle() {
        Bundle bundle = new Bundle();
        ContentUriTrigger uriTrigger = TestUtil.getContentUriTrigger();
        Bundle encode = TestUtil.encodeContentUriJob(uriTrigger, coder);
        bundle.putBundle(REQUEST_PARAM_EXTRAS, encode);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(AUTHORITY_URI);
        uris.add(EXTERNAL_CONTENT_URI);
        bundle.putParcelableArrayList(PACKED_PARAM_TRIGGERED_URIS, uris);
        JobInvocation jobInvocation = coder.decodeIntentBundle(bundle);
        Assert.assertEquals(uris, jobInvocation.getTriggerReason().getTriggeredContentUris());
        Assert.assertEquals("TAG", jobInvocation.getTag());
        Assert.assertEquals(uriTrigger.getUris(), getUris());
        Assert.assertEquals(TestJobService.class.getName(), jobInvocation.getService());
        Assert.assertEquals(DEFAULT_EXPONENTIAL.getPolicy(), jobInvocation.getRetryStrategy().getPolicy());
    }
}

