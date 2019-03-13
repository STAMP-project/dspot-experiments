/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.ascii.rest;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RestValueTest extends HazelcastTestSupport {
    private static final byte[] PAYLOAD = new byte[]{ 23, 42 };

    private RestValue restValue = new RestValue();

    @Test
    public void testSetContentType() {
        restValue.setContentType(RestValueTest.PAYLOAD);
        Assert.assertEquals(RestValueTest.PAYLOAD, restValue.getContentType());
        HazelcastTestSupport.assertContains(restValue.toString(), ("contentType='" + (StringUtil.bytesToString(RestValueTest.PAYLOAD))));
    }

    @Test
    public void testSetValue() {
        restValue.setValue(RestValueTest.PAYLOAD);
        Assert.assertEquals(RestValueTest.PAYLOAD, restValue.getValue());
        HazelcastTestSupport.assertContains(restValue.toString(), ("value.length=" + (RestValueTest.PAYLOAD.length)));
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(restValue.toString(), "unknown-content-type");
        HazelcastTestSupport.assertContains(restValue.toString(), "value.length=0");
    }

    @Test
    public void testToString_withText() {
        byte[] value = StringUtil.stringToBytes("foobar");
        byte[] contentType = StringUtil.stringToBytes("text");
        restValue = new RestValue(value, contentType);
        HazelcastTestSupport.assertContains(restValue.toString(), "contentType='text'");
        HazelcastTestSupport.assertContains(restValue.toString(), "value=\"foobar\"");
    }
}

