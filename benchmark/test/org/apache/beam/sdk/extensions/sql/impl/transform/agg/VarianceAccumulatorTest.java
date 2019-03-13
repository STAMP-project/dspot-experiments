/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.impl.transform.agg;


import VarianceAccumulator.EMPTY;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link VarianceAccumulator}.
 */
public class VarianceAccumulatorTest {
    private static final double DELTA = 1.0E-7;

    private static final BigDecimal FIFTEEN = new BigDecimal(15);

    private static final BigDecimal SIXTEEN = new BigDecimal(16);

    private static final BigDecimal THREE = new BigDecimal(3);

    private static final BigDecimal FOUR = new BigDecimal(4);

    private static final BigDecimal FIVE = new BigDecimal(5);

    @Test
    public void testInitialized() {
        VarianceAccumulator accumulator = VarianceAccumulator.newVarianceAccumulator(VarianceAccumulatorTest.FIFTEEN, VarianceAccumulatorTest.THREE, VarianceAccumulatorTest.FOUR);
        Assert.assertNotEquals(EMPTY, accumulator);
        Assert.assertEquals(VarianceAccumulatorTest.FIFTEEN, accumulator.variance());
        Assert.assertEquals(VarianceAccumulatorTest.THREE, accumulator.count());
        Assert.assertEquals(VarianceAccumulatorTest.FOUR, accumulator.sum());
    }

    @Test
    public void testEmpty() {
        VarianceAccumulator zeroAccumulator = VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        Assert.assertEquals(zeroAccumulator, EMPTY);
        Assert.assertEquals(zeroAccumulator, VarianceAccumulator.ofZeroElements());
    }

    @Test
    public void testAccumulatorOfSingleElement() {
        VarianceAccumulator accumulatorOfSingleElement = VarianceAccumulator.ofSingleElement(VarianceAccumulatorTest.THREE);
        Assert.assertEquals(VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ONE, VarianceAccumulatorTest.THREE), accumulatorOfSingleElement);
    }

    @Test
    public void testCombinesEmptyWithEmpty() {
        VarianceAccumulator result = EMPTY.combineWith(EMPTY);
        Assert.assertEquals(EMPTY, result);
    }

    @Test
    public void testCombinesEmptyWithNonEmpty() {
        VarianceAccumulator result = EMPTY.combineWith(VarianceAccumulator.ofSingleElement(VarianceAccumulatorTest.THREE));
        Assert.assertEquals(VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ONE, VarianceAccumulatorTest.THREE), result);
    }

    @Test
    public void testCombinesNonEmptyWithEmpty() {
        VarianceAccumulator result = VarianceAccumulator.ofSingleElement(VarianceAccumulatorTest.THREE).combineWith(EMPTY);
        Assert.assertEquals(VarianceAccumulator.newVarianceAccumulator(BigDecimal.ZERO, BigDecimal.ONE, VarianceAccumulatorTest.THREE), result);
    }

    @Test
    public void testCombinesNonTrivial() {
        VarianceAccumulator accumulator1 = VarianceAccumulator.newVarianceAccumulator(VarianceAccumulatorTest.FIFTEEN, VarianceAccumulatorTest.THREE, VarianceAccumulatorTest.FOUR);
        VarianceAccumulator accumulator2 = VarianceAccumulator.newVarianceAccumulator(VarianceAccumulatorTest.SIXTEEN, VarianceAccumulatorTest.FOUR, VarianceAccumulatorTest.FIVE);
        // values:
        // var(x)=15, m=3, sum(x)=4;
        // var(y)=16, n=4, sum(y)=5;
        // 
        // formula:
        // var(combine(x,y)) = var(x) + var(y) + increment();
        // increment() = m/(n(m+n))  *  (sum(x) * n/m  - sum(y))^2;
        // 
        // result:
        // increment() = 3/(4(3+4))  *  (4 * 4/3 - 5)^2 = 0.107142857 * 0.111111111 = 0.011904762
        // var(combine(x,y)) = 15 + 16 + increment() = 31.011904762
        VarianceAccumulator expectedCombined = VarianceAccumulator.newVarianceAccumulator(VarianceAccumulatorTest.FIFTEEN.add(VarianceAccumulatorTest.SIXTEEN).add(new BigDecimal(0.011904762)), VarianceAccumulatorTest.THREE.add(VarianceAccumulatorTest.FOUR), VarianceAccumulatorTest.FOUR.add(VarianceAccumulatorTest.FIVE));
        VarianceAccumulator combined1 = accumulator1.combineWith(accumulator2);
        VarianceAccumulator combined2 = accumulator2.combineWith(accumulator1);
        Assert.assertEquals(expectedCombined.variance().doubleValue(), combined1.variance().doubleValue(), VarianceAccumulatorTest.DELTA);
        Assert.assertEquals(expectedCombined.variance().doubleValue(), combined2.variance().doubleValue(), VarianceAccumulatorTest.DELTA);
        Assert.assertEquals(expectedCombined.count(), combined1.count());
        Assert.assertEquals(expectedCombined.sum(), combined1.sum());
        Assert.assertEquals(expectedCombined.count(), combined2.count());
        Assert.assertEquals(expectedCombined.sum(), combined2.sum());
    }
}

