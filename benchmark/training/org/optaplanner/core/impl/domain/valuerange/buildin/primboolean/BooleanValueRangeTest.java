/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.valuerange.buildin.primboolean;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class BooleanValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(2L, new BooleanValueRange().getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(Boolean.FALSE, new BooleanValueRange().get(0L));
        Assert.assertEquals(Boolean.TRUE, new BooleanValueRange().get(1L));
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, new BooleanValueRange().contains(Boolean.FALSE));
        Assert.assertEquals(true, new BooleanValueRange().contains(Boolean.TRUE));
        Assert.assertEquals(false, new BooleanValueRange().contains(null));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(new BooleanValueRange().createOriginalIterator(), Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextBoolean()).thenReturn(true, true, false, true);
        PlannerAssert.assertElementsOfIterator(new BooleanValueRange().createRandomIterator(workingRandom), Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndexNegative() {
        new BooleanValueRange().get((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndexGreaterThanSize() {
        new BooleanValueRange().get(2);
    }
}

