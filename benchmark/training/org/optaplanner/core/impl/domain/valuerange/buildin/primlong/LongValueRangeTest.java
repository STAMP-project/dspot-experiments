/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.domain.valuerange.buildin.primlong;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class LongValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(10L, new LongValueRange(0L, 10L).getSize());
        Assert.assertEquals(20L, new LongValueRange(100L, 120L).getSize());
        Assert.assertEquals(40L, new LongValueRange((-15L), 25L).getSize());
        Assert.assertEquals(0L, new LongValueRange(7L, 7L).getSize());
        Assert.assertEquals(((Long.MAX_VALUE) - 2000L), new LongValueRange((-1000L), ((Long.MAX_VALUE) - 3000L)).getSize());
        // IncrementUnit
        Assert.assertEquals(5L, new LongValueRange(0L, 10L, 2L).getSize());
        Assert.assertEquals(5L, new LongValueRange((-1L), 9L, 2L).getSize());
        Assert.assertEquals(4L, new LongValueRange(100L, 120L, 5L).getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(3L, new LongValueRange(0L, 10L).get(3L).intValue());
        Assert.assertEquals(103L, new LongValueRange(100L, 120L).get(3L).intValue());
        Assert.assertEquals((-4L), new LongValueRange((-5L), 25L).get(1L).intValue());
        Assert.assertEquals(1L, new LongValueRange((-5L), 25L).get(6L).intValue());
        Assert.assertEquals(4L, new LongValueRange((-1000L), ((Long.MAX_VALUE) - 3000L)).get(1004L).intValue());
        // IncrementUnit
        Assert.assertEquals(6L, new LongValueRange(0L, 10L, 2L).get(3L).intValue());
        Assert.assertEquals(5L, new LongValueRange((-1L), 9L, 2L).get(3L).intValue());
        Assert.assertEquals(115L, new LongValueRange(100L, 120L, 5L).get(3L).intValue());
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, new LongValueRange(0L, 10L).contains(3L));
        Assert.assertEquals(false, new LongValueRange(0L, 10L).contains(10L));
        Assert.assertEquals(false, new LongValueRange(0L, 10L).contains(null));
        Assert.assertEquals(true, new LongValueRange(100L, 120L).contains(100L));
        Assert.assertEquals(false, new LongValueRange(100L, 120L).contains(99L));
        Assert.assertEquals(true, new LongValueRange((-5L), 25L).contains((-4L)));
        Assert.assertEquals(false, new LongValueRange((-5L), 25L).contains((-20L)));
        // IncrementUnit
        Assert.assertEquals(true, new LongValueRange(0L, 10L, 2L).contains(2L));
        Assert.assertEquals(false, new LongValueRange(0L, 10L, 2L).contains(3L));
        Assert.assertEquals(true, new LongValueRange((-1L), 9L, 2L).contains(1L));
        Assert.assertEquals(false, new LongValueRange((-1L), 9L, 2L).contains(2L));
        Assert.assertEquals(true, new LongValueRange(100L, 120L, 5L).contains(115L));
        Assert.assertEquals(false, new LongValueRange(100L, 120L, 5L).contains(114L));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(0L, 7L).createOriginalIterator(), 0L, 1L, 2L, 3L, 4L, 5L, 6L);
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(100L, 104L).createOriginalIterator(), 100L, 101L, 102L, 103L);
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange((-4L), 3L).createOriginalIterator(), (-4L), (-3L), (-2L), (-1L), 0L, 1L, 2L);
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(7L, 7L).createOriginalIterator());
        // IncrementUnit
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(0L, 10L, 2L).createOriginalIterator(), 0L, 2L, 4L, 6L, 8L);
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange((-1L), 9L, 2L).createOriginalIterator(), (-1L), 1L, 3L, 5L, 7L);
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(100L, 120L, 5L).createOriginalIterator(), 100L, 105L, 110L, 115L);
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange(0L, 7L).createRandomIterator(workingRandom), 3L, 0L);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange(100L, 104L).createRandomIterator(workingRandom), 103L, 100L);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange((-4L), 3L).createRandomIterator(workingRandom), (-1L), (-4L));
        PlannerAssert.assertAllElementsOfIterator(new LongValueRange(7L, 7L).createRandomIterator(workingRandom));
        // IncrementUnit
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange(0L, 10L, 2L).createRandomIterator(workingRandom), 6L, 0L);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange((-1L), 9L, 2L).createRandomIterator(workingRandom), 5L, (-1L));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new LongValueRange(100L, 120L, 5L).createRandomIterator(workingRandom), 115L, 100L);
    }
}

