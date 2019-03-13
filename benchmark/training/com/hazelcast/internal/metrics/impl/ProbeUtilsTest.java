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
package com.hazelcast.internal.metrics.impl;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ProbeUtilsTest extends HazelcastTestSupport {
    @Test
    public void testPrivateConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ProbeUtils.class);
    }

    @Test
    public void isDouble() {
        Assert.assertTrue(ProbeUtils.isDouble(ProbeUtils.TYPE_DOUBLE_NUMBER));
        Assert.assertTrue(ProbeUtils.isDouble(ProbeUtils.TYPE_DOUBLE_PRIMITIVE));
        Assert.assertFalse(ProbeUtils.isDouble(ProbeUtils.TYPE_PRIMITIVE_LONG));
        Assert.assertFalse(ProbeUtils.isDouble(ProbeUtils.TYPE_LONG_NUMBER));
        Assert.assertFalse(ProbeUtils.isDouble(ProbeUtils.TYPE_COLLECTION));
        Assert.assertFalse(ProbeUtils.isDouble(ProbeUtils.TYPE_MAP));
        Assert.assertFalse(ProbeUtils.isDouble(ProbeUtils.TYPE_COUNTER));
    }
}

