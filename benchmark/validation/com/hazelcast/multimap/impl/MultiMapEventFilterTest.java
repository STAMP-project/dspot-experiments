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
package com.hazelcast.multimap.impl;


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
public class MultiMapEventFilterTest {
    private MultiMapEventFilter multiMapEventFilter;

    private MultiMapEventFilter multiMapEventFilterSameAttributes;

    private MultiMapEventFilter multiMapEventFilterOtherIncludeValue;

    private MultiMapEventFilter multiMapEventFilterOtherKey;

    private MultiMapEventFilter multiMapEventFilterDefaultParameters;

    @Test
    public void testEval() {
        Assert.assertFalse(multiMapEventFilter.eval(null));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(multiMapEventFilter, multiMapEventFilter);
        Assert.assertEquals(multiMapEventFilter, multiMapEventFilterSameAttributes);
        Assert.assertNotEquals(multiMapEventFilter, null);
        Assert.assertNotEquals(multiMapEventFilter, new Object());
        Assert.assertNotEquals(multiMapEventFilter, multiMapEventFilterOtherIncludeValue);
        Assert.assertNotEquals(multiMapEventFilter, multiMapEventFilterOtherKey);
        Assert.assertNotEquals(multiMapEventFilter, multiMapEventFilterDefaultParameters);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(multiMapEventFilter.hashCode(), multiMapEventFilter.hashCode());
        Assert.assertEquals(multiMapEventFilter.hashCode(), multiMapEventFilterSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(multiMapEventFilter.hashCode(), multiMapEventFilterOtherIncludeValue.hashCode());
        Assert.assertNotEquals(multiMapEventFilter.hashCode(), multiMapEventFilterOtherKey.hashCode());
        Assert.assertNotEquals(multiMapEventFilter.hashCode(), multiMapEventFilterDefaultParameters.hashCode());
    }
}

