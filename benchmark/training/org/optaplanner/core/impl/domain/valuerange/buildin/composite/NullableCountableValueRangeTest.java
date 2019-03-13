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


public class NullableCountableValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(5L, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).getSize());
        Assert.assertEquals(6L, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(100, 120, 5, 7, 8))).getSize());
        Assert.assertEquals(4L, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList((-15), 25, 0))).getSize());
        Assert.assertEquals(4L, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).getSize());
        Assert.assertEquals(1L, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Collections.<String>emptyList())).getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(5, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).get(2L).intValue());
        Assert.assertEquals(null, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).get(4L));
        Assert.assertEquals("c", new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(3L));
        Assert.assertEquals(null, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(6L));
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).contains(5));
        Assert.assertEquals(false, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).contains(4));
        Assert.assertEquals(true, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).contains(null));
        Assert.assertEquals(true, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).contains("a"));
        Assert.assertEquals(false, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).contains("n"));
        Assert.assertEquals(true, new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).contains(null));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5, 10))).createOriginalIterator(), null, 0, 2, 5, 10);
        PlannerAssert.assertAllElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(100, 120, 5, 7, 8))).createOriginalIterator(), null, 100, 120, 5, 7, 8);
        PlannerAssert.assertAllElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList((-15), 25, 0))).createOriginalIterator(), null, (-15), 25, 0);
        PlannerAssert.assertAllElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).createOriginalIterator(), null, "b", "z", "a");
        PlannerAssert.assertAllElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Collections.<String>emptyList())).createOriginalIterator(), new String[]{ null });
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(0, 2, 5))).createRandomIterator(workingRandom), null, 0);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList(100, 120, 5))).createRandomIterator(workingRandom), null, 100);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList((-15), 25, 0))).createRandomIterator(workingRandom), null, (-15));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Arrays.asList("b", "z", "a"))).createRandomIterator(workingRandom), null, "b");
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(0);
        PlannerAssert.assertElementsOfIterator(new NullableCountableValueRange(new org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange(Collections.<String>emptyList())).createRandomIterator(workingRandom), new String[]{ null });
    }
}

