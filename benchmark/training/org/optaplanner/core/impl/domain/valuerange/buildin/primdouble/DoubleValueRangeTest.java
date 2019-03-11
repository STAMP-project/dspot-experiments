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
package org.optaplanner.core.impl.domain.valuerange.buildin.primdouble;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class DoubleValueRangeTest {
    @Test
    public void contains() {
        Assert.assertEquals(true, new DoubleValueRange(0.0, 10.0).contains(3.0));
        Assert.assertEquals(false, new DoubleValueRange(0.0, 10.0).contains(10.0));
        Assert.assertEquals(false, new DoubleValueRange(0.0, 10.0).contains(null));
        Assert.assertEquals(true, new DoubleValueRange(100.0, 120.0).contains(100.0));
        Assert.assertEquals(false, new DoubleValueRange(100.0, 120.0).contains(99.9));
        Assert.assertEquals(true, new DoubleValueRange((-5.3), 25.2).contains((-5.2)));
        Assert.assertEquals(false, new DoubleValueRange((-5.3), 25.2).contains((-5.4)));
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        PlannerAssert.assertElementsOfIterator(new DoubleValueRange(0.0, 1.0).createRandomIterator(workingRandom), 0.3, 0.0);
        Mockito.when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        PlannerAssert.assertElementsOfIterator(new DoubleValueRange(100.0, 104.0).createRandomIterator(workingRandom), 101.2, 100.0);
        Mockito.when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        PlannerAssert.assertElementsOfIterator(new DoubleValueRange((-5.0), 5.0).createRandomIterator(workingRandom), (-2.0), (-5.0));
        PlannerAssert.assertAllElementsOfIterator(new DoubleValueRange(7.0, 7.0).createRandomIterator(workingRandom));
        Mockito.when(workingRandom.nextDouble()).thenReturn(Math.nextAfter(1.0, Double.NEGATIVE_INFINITY));
        PlannerAssert.assertElementsOfIterator(new DoubleValueRange(1.0E-6, 2.0E-6).createRandomIterator(workingRandom), Math.nextAfter(2.0E-6, Double.NEGATIVE_INFINITY));
        Mockito.when(workingRandom.nextDouble()).thenReturn(Math.nextAfter(1.0, Double.NEGATIVE_INFINITY));
        PlannerAssert.assertElementsOfIterator(new DoubleValueRange(1000000.0, 2000000.0).createRandomIterator(workingRandom), Math.nextAfter(2000000.0, Double.NEGATIVE_INFINITY));
    }
}

