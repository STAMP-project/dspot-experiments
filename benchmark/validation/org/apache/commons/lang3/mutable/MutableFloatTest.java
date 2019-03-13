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
 * @see MutableFloat
 */
public class MutableFloatTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(0.0F, new MutableFloat().floatValue(), 1.0E-4F);
        Assertions.assertEquals(1.0F, new MutableFloat(1.0F).floatValue(), 1.0E-4F);
        Assertions.assertEquals(2.0F, new MutableFloat(Float.valueOf(2.0F)).floatValue(), 1.0E-4F);
        Assertions.assertEquals(3.0F, new MutableFloat(new MutableFloat(3.0F)).floatValue(), 1.0E-4F);
        Assertions.assertEquals(2.0F, new MutableFloat("2.0").floatValue(), 1.0E-4F);
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableFloat(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableFloat mutNum = new MutableFloat(0.0F);
        Assertions.assertEquals(0.0F, new MutableFloat().floatValue(), 1.0E-4F);
        Assertions.assertEquals(Float.valueOf(0), new MutableFloat().getValue());
        mutNum.setValue(1);
        Assertions.assertEquals(1.0F, mutNum.floatValue(), 1.0E-4F);
        Assertions.assertEquals(Float.valueOf(1.0F), mutNum.getValue());
        mutNum.setValue(Float.valueOf(2.0F));
        Assertions.assertEquals(2.0F, mutNum.floatValue(), 1.0E-4F);
        Assertions.assertEquals(Float.valueOf(2.0F), mutNum.getValue());
        mutNum.setValue(new MutableFloat(3.0F));
        Assertions.assertEquals(3.0F, mutNum.floatValue(), 1.0E-4F);
        Assertions.assertEquals(Float.valueOf(3.0F), mutNum.getValue());
    }

    @Test
    public void testSetNull() {
        final MutableFloat mutNum = new MutableFloat(0.0F);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testNanInfinite() {
        MutableFloat mutNum = new MutableFloat(Float.NaN);
        Assertions.assertTrue(mutNum.isNaN());
        mutNum = new MutableFloat(Float.POSITIVE_INFINITY);
        Assertions.assertTrue(mutNum.isInfinite());
        mutNum = new MutableFloat(Float.NEGATIVE_INFINITY);
        Assertions.assertTrue(mutNum.isInfinite());
    }

    @Test
    public void testEquals() {
        final MutableFloat mutNumA = new MutableFloat(0.0F);
        final MutableFloat mutNumB = new MutableFloat(0.0F);
        final MutableFloat mutNumC = new MutableFloat(1.0F);
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, Float.valueOf(0.0F));
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableFloat mutNumA = new MutableFloat(0.0F);
        final MutableFloat mutNumB = new MutableFloat(0.0F);
        final MutableFloat mutNumC = new MutableFloat(1.0F);
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Float.valueOf(0.0F).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableFloat mutNum = new MutableFloat(0.0F);
        Assertions.assertEquals(0, mutNum.compareTo(new MutableFloat(0.0F)));
        Assertions.assertEquals((+1), mutNum.compareTo(new MutableFloat((-1.0F))));
        Assertions.assertEquals((-1), mutNum.compareTo(new MutableFloat(1.0F)));
    }

    @Test
    public void testCompareToNull() {
        final MutableFloat mutNum = new MutableFloat(0.0F);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableFloat mutNum = new MutableFloat(1.7F);
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1.7, mutNum.doubleValue(), 1.0E-5);
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToFloat() {
        Assertions.assertEquals(Float.valueOf(0.0F), new MutableFloat(0.0F).toFloat());
        Assertions.assertEquals(Float.valueOf(12.3F), new MutableFloat(12.3F).toFloat());
    }

    @Test
    public void testIncrement() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableFloat mutNum = new MutableFloat(1.0F);
        final float result = mutNum.incrementAndGet();
        Assertions.assertEquals(2.0F, result, 0.01F);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableFloat mutNum = new MutableFloat(1.0F);
        final float result = mutNum.getAndIncrement();
        Assertions.assertEquals(1.0F, result, 0.01F);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableFloat mutNum = new MutableFloat(1.0F);
        final float result = mutNum.decrementAndGet();
        Assertions.assertEquals(0.0F, result, 0.01F);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableFloat mutNum = new MutableFloat(1.0F);
        final float result = mutNum.getAndDecrement();
        Assertions.assertEquals(1.0F, result, 0.01F);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.add(1.1F);
        Assertions.assertEquals(2.1F, mutNum.floatValue(), 0.01F);
    }

    @Test
    public void testAddValueObject() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.add(Float.valueOf(1.1F));
        Assertions.assertEquals(2.1F, mutNum.floatValue(), 0.01F);
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableFloat mutableFloat = new MutableFloat(1.25F);
        final float result = mutableFloat.getAndAdd(0.75F);
        Assertions.assertEquals(1.25F, result, 0.01F);
        Assertions.assertEquals(2.0F, mutableFloat.floatValue(), 0.01F);
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableFloat mutableFloat = new MutableFloat(7.75F);
        final float result = mutableFloat.getAndAdd(Float.valueOf(2.25F));
        Assertions.assertEquals(7.75F, result, 0.01F);
        Assertions.assertEquals(10.0F, mutableFloat.floatValue(), 0.01F);
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableFloat mutableFloat = new MutableFloat(0.5F);
        final float result = mutableFloat.addAndGet(1.0F);
        Assertions.assertEquals(1.5F, result, 0.01F);
        Assertions.assertEquals(1.5F, mutableFloat.floatValue(), 0.01F);
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableFloat mutableFloat = new MutableFloat(5.0F);
        final float result = mutableFloat.addAndGet(Float.valueOf(2.5F));
        Assertions.assertEquals(7.5F, result, 0.01F);
        Assertions.assertEquals(7.5F, mutableFloat.floatValue(), 0.01F);
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.subtract(0.9F);
        Assertions.assertEquals(0.1F, mutNum.floatValue(), 0.01F);
    }

    @Test
    public void testSubtractValueObject() {
        final MutableFloat mutNum = new MutableFloat(1);
        mutNum.subtract(Float.valueOf(0.9F));
        Assertions.assertEquals(0.1F, mutNum.floatValue(), 0.01F);
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0.0", new MutableFloat(0.0F).toString());
        Assertions.assertEquals("10.0", new MutableFloat(10.0F).toString());
        Assertions.assertEquals("-123.0", new MutableFloat((-123.0F)).toString());
    }
}

