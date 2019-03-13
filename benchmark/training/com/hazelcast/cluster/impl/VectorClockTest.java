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
package com.hazelcast.cluster.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class VectorClockTest {
    @Test
    public void testEquals() {
        final VectorClock clock = vectorClock("A", 1, "B", 2);
        Assert.assertEquals(clock, vectorClock("A", 1, "B", 2));
        Assert.assertEquals(clock, new VectorClock(clock));
    }

    @Test
    public void testIsAfter() {
        Assert.assertFalse(vectorClock().isAfter(vectorClock()));
        Assert.assertTrue(vectorClock("A", 1).isAfter(vectorClock()));
        Assert.assertFalse(vectorClock("A", 1).isAfter(vectorClock("A", 1)));
        Assert.assertFalse(vectorClock("A", 1).isAfter(vectorClock("B", 1)));
        Assert.assertTrue(vectorClock("A", 1, "B", 1).isAfter(vectorClock("A", 1)));
        Assert.assertFalse(vectorClock("A", 1).isAfter(vectorClock("A", 1, "B", 1)));
        Assert.assertTrue(vectorClock("A", 2).isAfter(vectorClock("A", 1)));
        Assert.assertFalse(vectorClock("A", 2).isAfter(vectorClock("A", 1, "B", 1)));
        Assert.assertTrue(vectorClock("A", 2, "B", 1).isAfter(vectorClock("A", 1, "B", 1)));
    }

    @Test
    public void testMerge() {
        assertMerged(vectorClock("A", 1), vectorClock(), vectorClock("A", 1));
        assertMerged(vectorClock("A", 1), vectorClock("A", 2), vectorClock("A", 2));
        assertMerged(vectorClock("A", 2), vectorClock("A", 1), vectorClock("A", 2));
        assertMerged(vectorClock("A", 3, "B", 1), vectorClock("A", 1, "B", 2, "C", 3), vectorClock("A", 3, "B", 2, "C", 3));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(vectorClock().isEmpty());
        Assert.assertFalse(vectorClock("A", 1).isEmpty());
    }
}

