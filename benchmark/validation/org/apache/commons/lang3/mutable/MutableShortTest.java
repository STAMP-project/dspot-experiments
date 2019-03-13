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
 * @see MutableShort
 */
public class MutableShortTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(((short) (0)), new MutableShort().shortValue());
        Assertions.assertEquals(((short) (1)), new MutableShort(((short) (1))).shortValue());
        Assertions.assertEquals(((short) (2)), new MutableShort(Short.valueOf(((short) (2)))).shortValue());
        Assertions.assertEquals(((short) (3)), new MutableShort(new MutableShort(((short) (3)))).shortValue());
        Assertions.assertEquals(((short) (2)), new MutableShort("2").shortValue());
        Assertions.assertThrows(NullPointerException.class, () -> new MutableShort(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableShort mutNum = new MutableShort(((short) (0)));
        Assertions.assertEquals(((short) (0)), new MutableShort().shortValue());
        Assertions.assertEquals(Short.valueOf(((short) (0))), new MutableShort().getValue());
        mutNum.setValue(((short) (1)));
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(Short.valueOf(((short) (1))), mutNum.getValue());
        mutNum.setValue(Short.valueOf(((short) (2))));
        Assertions.assertEquals(((short) (2)), mutNum.shortValue());
        Assertions.assertEquals(Short.valueOf(((short) (2))), mutNum.getValue());
        mutNum.setValue(new MutableShort(((short) (3))));
        Assertions.assertEquals(((short) (3)), mutNum.shortValue());
        Assertions.assertEquals(Short.valueOf(((short) (3))), mutNum.getValue());
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testEquals() {
        final MutableShort mutNumA = new MutableShort(((short) (0)));
        final MutableShort mutNumB = new MutableShort(((short) (0)));
        final MutableShort mutNumC = new MutableShort(((short) (1)));
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, Short.valueOf(((short) (0))));
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableShort mutNumA = new MutableShort(((short) (0)));
        final MutableShort mutNumB = new MutableShort(((short) (0)));
        final MutableShort mutNumC = new MutableShort(((short) (1)));
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Short.valueOf(((short) (0))).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableShort mutNum = new MutableShort(((short) (0)));
        Assertions.assertEquals(((short) (0)), mutNum.compareTo(new MutableShort(((short) (0)))));
        Assertions.assertEquals(((short) (+1)), mutNum.compareTo(new MutableShort(((short) (-1)))));
        Assertions.assertEquals(((short) (-1)), mutNum.compareTo(new MutableShort(((short) (1)))));
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        Assertions.assertEquals(1.0F, mutNum.floatValue());
        Assertions.assertEquals(1.0, mutNum.doubleValue());
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToShort() {
        Assertions.assertEquals(Short.valueOf(((short) (0))), new MutableShort(((short) (0))).toShort());
        Assertions.assertEquals(Short.valueOf(((short) (123))), new MutableShort(((short) (123))).toShort());
    }

    @Test
    public void testIncrement() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        final short result = mutNum.incrementAndGet();
        Assertions.assertEquals(2, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        final short result = mutNum.getAndIncrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        final short result = mutNum.decrementAndGet();
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        final short result = mutNum.getAndDecrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.add(((short) (1)));
        Assertions.assertEquals(((short) (2)), mutNum.shortValue());
    }

    @Test
    public void testAddValueObject() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.add(Short.valueOf(((short) (1))));
        Assertions.assertEquals(((short) (2)), mutNum.shortValue());
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableShort mutableShort = new MutableShort(((short) (0)));
        final short result = mutableShort.getAndAdd(((short) (1)));
        Assertions.assertEquals(((short) (0)), result);
        Assertions.assertEquals(((short) (1)), mutableShort.shortValue());
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableShort mutableShort = new MutableShort(((short) (0)));
        final short result = mutableShort.getAndAdd(Short.valueOf(((short) (1))));
        Assertions.assertEquals(((short) (0)), result);
        Assertions.assertEquals(((short) (1)), mutableShort.shortValue());
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableShort mutableShort = new MutableShort(((short) (0)));
        final short result = mutableShort.addAndGet(((short) (1)));
        Assertions.assertEquals(((short) (1)), result);
        Assertions.assertEquals(((short) (1)), mutableShort.shortValue());
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableShort mutableShort = new MutableShort(((short) (0)));
        final short result = mutableShort.addAndGet(Short.valueOf(((short) (1))));
        Assertions.assertEquals(((short) (1)), result);
        Assertions.assertEquals(((short) (1)), mutableShort.shortValue());
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.subtract(((short) (1)));
        Assertions.assertEquals(((short) (0)), mutNum.shortValue());
    }

    @Test
    public void testSubtractValueObject() {
        final MutableShort mutNum = new MutableShort(((short) (1)));
        mutNum.subtract(Short.valueOf(((short) (1))));
        Assertions.assertEquals(((short) (0)), mutNum.shortValue());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0", new MutableShort(((short) (0))).toString());
        Assertions.assertEquals("10", new MutableShort(((short) (10))).toString());
        Assertions.assertEquals("-123", new MutableShort(((short) (-123))).toString());
    }
}

