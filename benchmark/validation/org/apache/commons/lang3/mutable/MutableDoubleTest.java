/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.mutable;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * JUnit tests.
 *
 * @see MutableDouble
 */
public class MutableDoubleTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(0.0, new MutableDouble().doubleValue(), 1.0E-4);
        Assertions.assertEquals(1.0, new MutableDouble(1.0).doubleValue(), 1.0E-4);
        Assertions.assertEquals(2.0, new MutableDouble(Double.valueOf(2.0)).doubleValue(), 1.0E-4);
        Assertions.assertEquals(3.0, new MutableDouble(new MutableDouble(3.0)).doubleValue(), 1.0E-4);
        Assertions.assertEquals(2.0, new MutableDouble("2.0").doubleValue(), 1.0E-4);
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableDouble(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableDouble mutNum = new MutableDouble(0.0);
        Assertions.assertEquals(0.0, new MutableDouble().doubleValue(), 1.0E-4);
        Assertions.assertEquals(Double.valueOf(0), new MutableDouble().getValue());
        mutNum.setValue(1);
        Assertions.assertEquals(1.0, mutNum.doubleValue(), 1.0E-4);
        Assertions.assertEquals(Double.valueOf(1.0), mutNum.getValue());
        mutNum.setValue(Double.valueOf(2.0));
        Assertions.assertEquals(2.0, mutNum.doubleValue(), 1.0E-4);
        Assertions.assertEquals(Double.valueOf(2.0), mutNum.getValue());
        mutNum.setValue(new MutableDouble(3.0));
        Assertions.assertEquals(3.0, mutNum.doubleValue(), 1.0E-4);
        Assertions.assertEquals(Double.valueOf(3.0), mutNum.getValue());
    }

    @Test
    public void testSetNull() {
        final MutableDouble mutNum = new MutableDouble(0.0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testNanInfinite() {
        MutableDouble mutNum = new MutableDouble(Double.NaN);
        Assertions.assertTrue(mutNum.isNaN());
        mutNum = new MutableDouble(Double.POSITIVE_INFINITY);
        Assertions.assertTrue(mutNum.isInfinite());
        mutNum = new MutableDouble(Double.NEGATIVE_INFINITY);
        Assertions.assertTrue(mutNum.isInfinite());
    }

    @Test
    public void testEquals() {
        final MutableDouble mutNumA = new MutableDouble(0.0);
        final MutableDouble mutNumB = new MutableDouble(0.0);
        final MutableDouble mutNumC = new MutableDouble(1.0);
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, Double.valueOf(0.0));
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableDouble mutNumA = new MutableDouble(0.0);
        final MutableDouble mutNumB = new MutableDouble(0.0);
        final MutableDouble mutNumC = new MutableDouble(1.0);
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Double.valueOf(0.0).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableDouble mutNum = new MutableDouble(0.0);
        Assertions.assertEquals(0, mutNum.compareTo(new MutableDouble(0.0)));
        Assertions.assertEquals((+1), mutNum.compareTo(new MutableDouble((-1.0))));
        Assertions.assertEquals((-1), mutNum.compareTo(new MutableDouble(1.0)));
    }

    @Test
    public void testCompareToNull() {
        final MutableDouble mutNum = new MutableDouble(0.0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableDouble mutNum = new MutableDouble(1.7);
        Assertions.assertEquals(1.7F, mutNum.floatValue());
        Assertions.assertEquals(1.7, mutNum.doubleValue());
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToDouble() {
        Assertions.assertEquals(Double.valueOf(0.0), new MutableDouble(0.0).toDouble());
        Assertions.assertEquals(Double.valueOf(12.3), new MutableDouble(12.3).toDouble());
    }

    @Test
    public void testIncrement() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableDouble mutNum = new MutableDouble(1.0);
        final double result = mutNum.incrementAndGet();
        Assertions.assertEquals(2.0, result, 0.01);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableDouble mutNum = new MutableDouble(1.0);
        final double result = mutNum.getAndIncrement();
        Assertions.assertEquals(1.0, result, 0.01);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableDouble mutNum = new MutableDouble(1.0);
        final double result = mutNum.decrementAndGet();
        Assertions.assertEquals(0.0, result, 0.01);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableDouble mutNum = new MutableDouble(1.0);
        final double result = mutNum.getAndDecrement();
        Assertions.assertEquals(1.0, result, 0.01);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.add(1.1);
        Assertions.assertEquals(2.1, mutNum.doubleValue(), 0.01);
    }

    @Test
    public void testAddValueObject() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.add(Double.valueOf(1.1));
        Assertions.assertEquals(2.1, mutNum.doubleValue(), 0.01);
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableDouble mutableDouble = new MutableDouble(0.5);
        final double result = mutableDouble.getAndAdd(1.0);
        Assertions.assertEquals(0.5, result, 0.01);
        Assertions.assertEquals(1.5, mutableDouble.doubleValue(), 0.01);
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableDouble mutableDouble = new MutableDouble(0.5);
        final double result = mutableDouble.getAndAdd(Double.valueOf(2.0));
        Assertions.assertEquals(0.5, result, 0.01);
        Assertions.assertEquals(2.5, mutableDouble.doubleValue(), 0.01);
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableDouble mutableDouble = new MutableDouble(10.5);
        final double result = mutableDouble.addAndGet((-0.5));
        Assertions.assertEquals(10.0, result, 0.01);
        Assertions.assertEquals(10.0, mutableDouble.doubleValue(), 0.01);
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableDouble mutableDouble = new MutableDouble(7.5);
        final double result = mutableDouble.addAndGet(Double.valueOf((-2.5)));
        Assertions.assertEquals(5.0, result, 0.01);
        Assertions.assertEquals(5.0, mutableDouble.doubleValue(), 0.01);
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.subtract(0.9);
        Assertions.assertEquals(0.1, mutNum.doubleValue(), 0.01);
    }

    @Test
    public void testSubtractValueObject() {
        final MutableDouble mutNum = new MutableDouble(1);
        mutNum.subtract(Double.valueOf(0.9));
        Assertions.assertEquals(0.1, mutNum.doubleValue(), 0.01);
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0.0", new MutableDouble(0.0).toString());
        Assertions.assertEquals("10.0", new MutableDouble(10.0).toString());
        Assertions.assertEquals("-123.0", new MutableDouble((-123.0)).toString());
    }
}

