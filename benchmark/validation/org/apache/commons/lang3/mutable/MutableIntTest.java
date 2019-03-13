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
 * @see MutableInt
 */
public class MutableIntTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(0, new MutableInt().intValue());
        Assertions.assertEquals(1, new MutableInt(1).intValue());
        Assertions.assertEquals(2, new MutableInt(Integer.valueOf(2)).intValue());
        Assertions.assertEquals(3, new MutableInt(new MutableLong(3)).intValue());
        Assertions.assertEquals(2, new MutableInt("2").intValue());
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableInt(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableInt mutNum = new MutableInt(0);
        Assertions.assertEquals(0, new MutableInt().intValue());
        Assertions.assertEquals(Integer.valueOf(0), new MutableInt().getValue());
        mutNum.setValue(1);
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(Integer.valueOf(1), mutNum.getValue());
        mutNum.setValue(Integer.valueOf(2));
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(Integer.valueOf(2), mutNum.getValue());
        mutNum.setValue(new MutableLong(3));
        Assertions.assertEquals(3, mutNum.intValue());
        Assertions.assertEquals(Integer.valueOf(3), mutNum.getValue());
    }

    @Test
    public void testSetNull() {
        final MutableInt mutNum = new MutableInt(0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testEquals() {
        this.testEquals(new MutableInt(0), new MutableInt(0), new MutableInt(1));
        // Should Numbers be supported? GaryG July-21-2005.
        // this.testEquals(mutNumA, Integer.valueOf(0), mutNumC);
    }

    @Test
    public void testHashCode() {
        final MutableInt mutNumA = new MutableInt(0);
        final MutableInt mutNumB = new MutableInt(0);
        final MutableInt mutNumC = new MutableInt(1);
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Integer.valueOf(0).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableInt mutNum = new MutableInt(0);
        Assertions.assertEquals(0, mutNum.compareTo(new MutableInt(0)));
        Assertions.assertEquals((+1), mutNum.compareTo(new MutableInt((-1))));
        Assertions.assertEquals((-1), mutNum.compareTo(new MutableInt(1)));
    }

    @Test
    public void testCompareToNull() {
        final MutableInt mutNum = new MutableInt(0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableInt mutNum = new MutableInt(1);
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1.0F, mutNum.floatValue());
        Assertions.assertEquals(1.0, mutNum.doubleValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToInteger() {
        Assertions.assertEquals(Integer.valueOf(0), new MutableInt(0).toInteger());
        Assertions.assertEquals(Integer.valueOf(123), new MutableInt(123).toInteger());
    }

    @Test
    public void testIncrement() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableInt mutNum = new MutableInt(1);
        final int result = mutNum.incrementAndGet();
        Assertions.assertEquals(2, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableInt mutNum = new MutableInt(1);
        final int result = mutNum.getAndIncrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableInt mutNum = new MutableInt(1);
        final int result = mutNum.decrementAndGet();
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableInt mutNum = new MutableInt(1);
        final int result = mutNum.getAndDecrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.add(1);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testAddValueObject() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.add(Integer.valueOf(1));
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableInt mutableInteger = new MutableInt(0);
        final int result = mutableInteger.getAndAdd(1);
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(1, mutableInteger.intValue());
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableInt mutableInteger = new MutableInt(0);
        final int result = mutableInteger.getAndAdd(Integer.valueOf(1));
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(1, mutableInteger.intValue());
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableInt mutableInteger = new MutableInt(0);
        final int result = mutableInteger.addAndGet(1);
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(1, mutableInteger.intValue());
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableInt mutableInteger = new MutableInt(0);
        final int result = mutableInteger.addAndGet(Integer.valueOf(1));
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(1, mutableInteger.intValue());
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.subtract(1);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testSubtractValueObject() {
        final MutableInt mutNum = new MutableInt(1);
        mutNum.subtract(Integer.valueOf(1));
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0", new MutableInt(0).toString());
        Assertions.assertEquals("10", new MutableInt(10).toString());
        Assertions.assertEquals("-123", new MutableInt((-123)).toString());
    }
}

