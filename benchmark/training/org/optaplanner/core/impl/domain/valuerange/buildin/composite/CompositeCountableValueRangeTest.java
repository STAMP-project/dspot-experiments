/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.valuerange.buildin.composite;


import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class CompositeCountableValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(7L, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).getSize());
        Assert.assertEquals(4L, CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).getSize());
        Assert.assertEquals(0L, CompositeCountableValueRangeTest.createValueRange(Collections.<String>emptyList(), Collections.<String>emptyList()).getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(5, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).get(2L).intValue());
        Assert.assertEquals((-15), CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).get(4L).intValue());
        Assert.assertEquals((-1), CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).get(6L).intValue());
        Assert.assertEquals("c", CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).get(2L));
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains(5));
        Assert.assertEquals(false, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains(4));
        Assert.assertEquals(true, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains((-15)));
        Assert.assertEquals(false, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains((-14)));
        Assert.assertEquals(true, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains((-1)));
        Assert.assertEquals(false, CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).contains(1));
        Assert.assertEquals(true, CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).contains("c"));
        Assert.assertEquals(false, CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).contains("n"));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).createOriginalIterator(), 0, 2, 5, 10, (-15), 25, (-1));
        PlannerAssert.assertAllElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).createOriginalIterator(), "a", "b", "c", "d");
        PlannerAssert.assertAllElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Collections.<String>emptyList(), Collections.<String>emptyList()).createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList((-15), 25, (-1))).createRandomIterator(workingRandom), 10, 0);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).createRandomIterator(workingRandom), "d", "a");
        PlannerAssert.assertElementsOfIterator(CompositeCountableValueRangeTest.createValueRange(Collections.<String>emptyList(), Collections.<String>emptyList()).createRandomIterator(workingRandom));
    }
}

