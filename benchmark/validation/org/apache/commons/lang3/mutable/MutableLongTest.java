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
 * @see MutableLong
 */
public class MutableLongTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(0, new MutableLong().longValue());
        Assertions.assertEquals(1, new MutableLong(1).longValue());
        Assertions.assertEquals(2, new MutableLong(Long.valueOf(2)).longValue());
        Assertions.assertEquals(3, new MutableLong(new MutableLong(3)).longValue());
        Assertions.assertEquals(2, new MutableLong("2").longValue());
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableLong(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableLong mutNum = new MutableLong(0);
        Assertions.assertEquals(0, new MutableLong().longValue());
        Assertions.assertEquals(Long.valueOf(0), new MutableLong().getValue());
        mutNum.setValue(1);
        Assertions.assertEquals(1, mutNum.longValue());
        Assertions.assertEquals(Long.valueOf(1), mutNum.getValue());
        mutNum.setValue(Long.valueOf(2));
        Assertions.assertEquals(2, mutNum.longValue());
        Assertions.assertEquals(Long.valueOf(2), mutNum.getValue());
        mutNum.setValue(new MutableLong(3));
        Assertions.assertEquals(3, mutNum.longValue());
        Assertions.assertEquals(Long.valueOf(3), mutNum.getValue());
    }

    @Test
    public void testSetNull() {
        final MutableLong mutNum = new MutableLong(0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testEquals() {
        final MutableLong mutNumA = new MutableLong(0);
        final MutableLong mutNumB = new MutableLong(0);
        final MutableLong mutNumC = new MutableLong(1);
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, Long.valueOf(0));
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableLong mutNumA = new MutableLong(0);
        final MutableLong mutNumB = new MutableLong(0);
        final MutableLong mutNumC = new MutableLong(1);
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Long.valueOf(0).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableLong mutNum = new MutableLong(0);
        Assertions.assertEquals(0, mutNum.compareTo(new MutableLong(0)));
        Assertions.assertEquals((+1), mutNum.compareTo(new MutableLong((-1))));
        Assertions.assertEquals((-1), mutNum.compareTo(new MutableLong(1)));
    }

    @Test
    public void testCompareToNull() {
        final MutableLong mutNum = new MutableLong(0);
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableLong mutNum = new MutableLong(1L);
        Assertions.assertEquals(1.0F, mutNum.floatValue());
        Assertions.assertEquals(1.0, mutNum.doubleValue());
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToLong() {
        Assertions.assertEquals(Long.valueOf(0L), new MutableLong(0L).toLong());
        Assertions.assertEquals(Long.valueOf(123L), new MutableLong(123L).toLong());
    }

    @Test
    public void testIncrement() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableLong mutNum = new MutableLong(1L);
        final long result = mutNum.incrementAndGet();
        Assertions.assertEquals(2, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableLong mutNum = new MutableLong(1L);
        final long result = mutNum.getAndIncrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableLong mutNum = new MutableLong(1L);
        final long result = mutNum.decrementAndGet();
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableLong mutNum = new MutableLong(1L);
        final long result = mutNum.getAndDecrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.add(1);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testAddValueObject() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.add(Long.valueOf(1));
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableLong mutableLong = new MutableLong(0L);
        final long result = mutableLong.getAndAdd(1L);
        Assertions.assertEquals(0L, result);
        Assertions.assertEquals(1L, mutableLong.longValue());
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableLong mutableLong = new MutableLong(0L);
        final long result = mutableLong.getAndAdd(Long.valueOf(1L));
        Assertions.assertEquals(0L, result);
        Assertions.assertEquals(1L, mutableLong.longValue());
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableLong mutableLong = new MutableLong(0L);
        final long result = mutableLong.addAndGet(1L);
        Assertions.assertEquals(1L, result);
        Assertions.assertEquals(1L, mutableLong.longValue());
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableLong mutableLong = new MutableLong(0L);
        final long result = mutableLong.addAndGet(Long.valueOf(1L));
        Assertions.assertEquals(1L, result);
        Assertions.assertEquals(1L, mutableLong.longValue());
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.subtract(1);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testSubtractValueObject() {
        final MutableLong mutNum = new MutableLong(1);
        mutNum.subtract(Long.valueOf(1));
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0", new MutableLong(0).toString());
        Assertions.assertEquals("10", new MutableLong(10).toString());
        Assertions.assertEquals("-123", new MutableLong((-123)).toString());
    }
}

