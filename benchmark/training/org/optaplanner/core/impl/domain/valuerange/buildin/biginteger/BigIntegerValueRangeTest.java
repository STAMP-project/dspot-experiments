/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.domain.valuerange.buildin.biginteger;


import java.math.BigInteger;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class BigIntegerValueRangeTest {
    @Test
    public void getSize() {
        Assert.assertEquals(10L, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).getSize());
        Assert.assertEquals(20L, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).getSize());
        Assert.assertEquals(40L, new BigIntegerValueRange(new BigInteger("-15"), new BigInteger("25")).getSize());
        Assert.assertEquals(0L, new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7")).getSize());
        // IncrementUnit
        Assert.assertEquals(5L, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).getSize());
        Assert.assertEquals(5L, new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).getSize());
        Assert.assertEquals(4L, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).getSize());
    }

    @Test
    public void get() {
        Assert.assertEquals(new BigInteger("3"), new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).get(3L));
        Assert.assertEquals(new BigInteger("103"), new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).get(3L));
        Assert.assertEquals(new BigInteger("-4"), new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).get(1L));
        Assert.assertEquals(new BigInteger("1"), new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).get(6L));
        // IncrementUnit
        Assert.assertEquals(new BigInteger("6"), new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).get(3L));
        Assert.assertEquals(new BigInteger("5"), new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).get(3L));
        Assert.assertEquals(new BigInteger("115"), new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).get(3L));
    }

    @Test
    public void contains() {
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(new BigInteger("3")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(new BigInteger("10")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(null));
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).contains(new BigInteger("100")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).contains(new BigInteger("99")));
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).contains(new BigInteger("-4")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).contains(new BigInteger("-20")));
        // IncrementUnit
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).contains(new BigInteger("2")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).contains(new BigInteger("3")));
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).contains(new BigInteger("1")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).contains(new BigInteger("2")));
        Assert.assertEquals(true, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).contains(new BigInteger("115")));
        Assert.assertEquals(false, new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).contains(new BigInteger("114")));
    }

    @Test
    public void createOriginalIterator() {
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("4")).createOriginalIterator(), new BigInteger("0"), new BigInteger("1"), new BigInteger("2"), new BigInteger("3"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("104")).createOriginalIterator(), new BigInteger("100"), new BigInteger("101"), new BigInteger("102"), new BigInteger("103"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("-4"), new BigInteger("3")).createOriginalIterator(), new BigInteger("-4"), new BigInteger("-3"), new BigInteger("-2"), new BigInteger("-1"), new BigInteger("0"), new BigInteger("1"), new BigInteger("2"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7")).createOriginalIterator());
        // IncrementUnit
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).createOriginalIterator(), new BigInteger("0"), new BigInteger("2"), new BigInteger("4"), new BigInteger("6"), new BigInteger("8"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).createOriginalIterator(), new BigInteger("-1"), new BigInteger("1"), new BigInteger("3"), new BigInteger("5"), new BigInteger("7"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).createOriginalIterator(), new BigInteger("100"), new BigInteger("105"), new BigInteger("110"), new BigInteger("115"));
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("7")).createRandomIterator(workingRandom), new BigInteger("3"), new BigInteger("0"));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("104")).createRandomIterator(workingRandom), new BigInteger("103"), new BigInteger("100"));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("-4"), new BigInteger("3")).createRandomIterator(workingRandom), new BigInteger("-1"), new BigInteger("-4"));
        PlannerAssert.assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7")).createRandomIterator(workingRandom));
        // IncrementUnit
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).createRandomIterator(workingRandom), new BigInteger("6"), new BigInteger("0"));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).createRandomIterator(workingRandom), new BigInteger("5"), new BigInteger("-1"));
        Mockito.when(workingRandom.nextInt(ArgumentMatchers.anyInt())).thenReturn(3, 0);
        PlannerAssert.assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).createRandomIterator(workingRandom), new BigInteger("115"), new BigInteger("100"));
    }
}

