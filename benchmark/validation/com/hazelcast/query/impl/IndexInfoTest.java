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
package com.hazelcast.query.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndexInfoTest {
    private IndexInfo indexInfo;

    private IndexInfo indexInfoSameAttributes;

    private IndexInfo indexInfoOtherIsOrdered;

    private IndexInfo indexInfoOtherAttributeName;

    private IndexInfo indexInfoNullAttributeName;

    @Test
    public void testEquals() {
        Assert.assertEquals(indexInfo, indexInfo);
        Assert.assertEquals(indexInfo, indexInfoSameAttributes);
        Assert.assertNotEquals(indexInfo, null);
        Assert.assertNotEquals(indexInfo, new Object());
        Assert.assertNotEquals(indexInfo, indexInfoOtherIsOrdered);
        Assert.assertNotEquals(indexInfo, indexInfoOtherAttributeName);
        Assert.assertNotEquals(indexInfo, indexInfoNullAttributeName);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(indexInfo.hashCode(), indexInfo.hashCode());
        Assert.assertEquals(indexInfo.hashCode(), indexInfoSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(indexInfo.hashCode(), indexInfoOtherIsOrdered.hashCode());
        Assert.assertNotEquals(indexInfo.hashCode(), indexInfoOtherAttributeName.hashCode());
        Assert.assertNotEquals(indexInfo.hashCode(), indexInfoNullAttributeName.hashCode());
    }

    @Test
    public void testCompareTo() {
        Assert.assertEquals(0, indexInfo.compareTo(indexInfoSameAttributes));
        Assert.assertEquals(1, indexInfo.compareTo(indexInfoOtherIsOrdered));
        Assert.assertEquals((-1), indexInfoOtherIsOrdered.compareTo(indexInfo));
        Assert.assertEquals(4, indexInfo.compareTo(indexInfoOtherAttributeName));
        Assert.assertEquals((-4), indexInfoOtherAttributeName.compareTo(indexInfo));
    }
}

