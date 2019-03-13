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
package com.hazelcast.query.impl.getters;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ImmutableMultiResultTest {
    private ImmutableMultiResult<Integer> immutableMultiResult;

    @Test
    public void testGetResults() {
        List<Integer> results = immutableMultiResult.getResults();
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(23));
        Assert.assertTrue(results.contains(42));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertFalse(immutableMultiResult.isEmpty());
    }

    @Test
    public void testIsEmpty_whenEmpty() {
        MultiResult<Integer> multiResult = new MultiResult<Integer>();
        immutableMultiResult = new ImmutableMultiResult<Integer>(multiResult);
        Assert.assertTrue(immutableMultiResult.isEmpty());
    }

    @Test
    public void testIsNullEmptyTarget() {
        Assert.assertFalse(immutableMultiResult.isNullEmptyTarget());
    }

    @Test
    public void testIsNullEmptyTarget_whenEmpty() {
        MultiResult<Integer> multiResult = new MultiResult<Integer>();
        multiResult.setNullOrEmptyTarget(true);
        immutableMultiResult = new ImmutableMultiResult<Integer>(multiResult);
        Assert.assertTrue(immutableMultiResult.isNullEmptyTarget());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        immutableMultiResult.add(1234);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddNullOrEmptyTarget() {
        immutableMultiResult.addNullOrEmptyTarget();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetNullOrEmptyTarget() {
        immutableMultiResult.setNullOrEmptyTarget(true);
    }
}

