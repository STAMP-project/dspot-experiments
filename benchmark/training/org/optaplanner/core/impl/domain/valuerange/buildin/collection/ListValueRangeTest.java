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
package org.optaplanner.core.impl.domain.valuerange.buildin.collection;


import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class ListValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(4L, new ListValueRange(Arrays.asList(0, 2, 5, 10)).getSize());
        Assert.assertEquals(5L, new ListValueRange(Arrays.asList(100, 120, 5, 7, 8)).getSize());
        Assert.assertEquals(3L, new ListValueRange(Arrays.asList((-15), 25, 0)).getSize());
        Assert.assertEquals(3L, new ListValueRange(Arrays.asList("b", "z", "a")).getSize());
        Assert.assertEquals(0L, new ListValueRange(Collections.<String>emptyList()).getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(5, new ListValueRange(Arrays.asList(0, 2, 5, 10)).get(2L).intValue());
        Assert.assertEquals((-120), new ListValueRange(Arrays.asList(100, (-120))).get(1L).intValue());
        Assert.assertEquals("c", new ListValueRange(Arrays.asList("b", "z", "a", "c", "g", "d")).get(3L));
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, new ListValueRange(Arrays.asList(0, 2, 5, 10)).contains(5));
        Assert.assertEquals(false, new ListValueRange(Arrays.asList(0, 2, 5, 10)).contains(4));
        Assert.assertEquals(false, new ListValueRange(Arrays.asList(0, 2, 5, 10)).contains(null));
        Assert.assertEquals(true, new ListValueRange(Arrays.asList(100, 120, 5, 7, 8)).contains(7));
        Assert.assertEquals(false, new ListValueRange(Arrays.asList(100, 120, 5, 7, 8)).contains(9));
        Assert.assertEquals(true, new ListValueRange(Arrays.asList((-15), 25, 0)).contains((-15)));
        Assert.assertEquals(false, new ListValueRange(Arrays.asList((-15), 25, 0)).contains((-14)));
        Assert.assertEquals(true, new ListValueRange(Arrays.asList("b", "z", "a")).contains("a"));
        Assert.assertEquals(false, new ListValueRange(Arrays.asList("b", "z", "a")).contains("n"));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Arrays.asList(0, 2, 5, 10)).createOriginalIterator(), 0, 2, 5, 10);
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Arrays.asList(100, 120, 5, 7, 8)).createOriginalIterator(), 100, 120, 5, 7, 8);
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Arrays.asList((-15), 25, 0)).createOriginalIterator(), (-15), 25, 0);
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Arrays.asList("b", "z", "a")).createOriginalIterator(), "b", "z", "a");
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Collections.<String>emptyList()).createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(2, 0);
        PlannerAssert.assertElementsOfIterator(new ListValueRange(Arrays.asList(0, 2, 5, 10)).createRandomIterator(workingRandom), 5, 0);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(2, 0);
        PlannerAssert.assertElementsOfIterator(new ListValueRange(Arrays.asList(100, 120, 5, 7, 8)).createRandomIterator(workingRandom), 5, 100);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(2, 0);
        PlannerAssert.assertElementsOfIterator(new ListValueRange(Arrays.asList((-15), 25, 0)).createRandomIterator(workingRandom), 0, (-15));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(2, 0);
        PlannerAssert.assertElementsOfIterator(new ListValueRange(Arrays.asList("b", "z", "a")).createRandomIterator(workingRandom), "a", "b");
        PlannerAssert.assertAllElementsOfIterator(new ListValueRange(Collections.<String>emptyList()).createRandomIterator(workingRandom));
    }
}

