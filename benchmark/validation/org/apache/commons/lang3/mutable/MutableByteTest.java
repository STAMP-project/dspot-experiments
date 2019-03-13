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
 * @see MutableByte
 */
public class MutableByteTest {
    // ----------------------------------------------------------------
    @Test
    public void testConstructors() {
        Assertions.assertEquals(((byte) (0)), new MutableByte().byteValue());
        Assertions.assertEquals(((byte) (1)), new MutableByte(((byte) (1))).byteValue());
        Assertions.assertEquals(((byte) (2)), new MutableByte(Byte.valueOf(((byte) (2)))).byteValue());
        Assertions.assertEquals(((byte) (3)), new MutableByte(new MutableByte(((byte) (3)))).byteValue());
        Assertions.assertEquals(((byte) (2)), new MutableByte("2").byteValue());
    }

    @Test
    public void testConstructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new MutableByte(((Number) (null))));
    }

    @Test
    public void testGetSet() {
        final MutableByte mutNum = new MutableByte(((byte) (0)));
        Assertions.assertEquals(((byte) (0)), new MutableByte().byteValue());
        Assertions.assertEquals(Byte.valueOf(((byte) (0))), new MutableByte().getValue());
        mutNum.setValue(((byte) (1)));
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(Byte.valueOf(((byte) (1))), mutNum.getValue());
        mutNum.setValue(Byte.valueOf(((byte) (2))));
        Assertions.assertEquals(((byte) (2)), mutNum.byteValue());
        Assertions.assertEquals(Byte.valueOf(((byte) (2))), mutNum.getValue());
        mutNum.setValue(new MutableByte(((byte) (3))));
        Assertions.assertEquals(((byte) (3)), mutNum.byteValue());
        Assertions.assertEquals(Byte.valueOf(((byte) (3))), mutNum.getValue());
    }

    @Test
    public void testSetNull() {
        final MutableByte mutNum = new MutableByte(((byte) (0)));
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.setValue(null));
    }

    @Test
    public void testEquals() {
        final MutableByte mutNumA = new MutableByte(((byte) (0)));
        final MutableByte mutNumB = new MutableByte(((byte) (0)));
        final MutableByte mutNumC = new MutableByte(((byte) (1)));
        Assertions.assertEquals(mutNumA, mutNumA);
        Assertions.assertEquals(mutNumA, mutNumB);
        Assertions.assertEquals(mutNumB, mutNumA);
        Assertions.assertEquals(mutNumB, mutNumB);
        Assertions.assertNotEquals(mutNumA, mutNumC);
        Assertions.assertNotEquals(mutNumB, mutNumC);
        Assertions.assertEquals(mutNumC, mutNumC);
        Assertions.assertNotEquals(null, mutNumA);
        Assertions.assertNotEquals(mutNumA, Byte.valueOf(((byte) (0))));
        Assertions.assertNotEquals("0", mutNumA);
    }

    @Test
    public void testHashCode() {
        final MutableByte mutNumA = new MutableByte(((byte) (0)));
        final MutableByte mutNumB = new MutableByte(((byte) (0)));
        final MutableByte mutNumC = new MutableByte(((byte) (1)));
        Assertions.assertEquals(mutNumA.hashCode(), mutNumA.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), mutNumB.hashCode());
        Assertions.assertNotEquals(mutNumA.hashCode(), mutNumC.hashCode());
        Assertions.assertEquals(mutNumA.hashCode(), Byte.valueOf(((byte) (0))).hashCode());
    }

    @Test
    public void testCompareTo() {
        final MutableByte mutNum = new MutableByte(((byte) (0)));
        Assertions.assertEquals(((byte) (0)), mutNum.compareTo(new MutableByte(((byte) (0)))));
        Assertions.assertEquals(((byte) (+1)), mutNum.compareTo(new MutableByte(((byte) (-1)))));
        Assertions.assertEquals(((byte) (-1)), mutNum.compareTo(new MutableByte(((byte) (1)))));
    }

    @Test
    public void testCompareToNull() {
        final MutableByte mutNum = new MutableByte(((byte) (0)));
        Assertions.assertThrows(NullPointerException.class, () -> mutNum.compareTo(null));
    }

    @Test
    public void testPrimitiveValues() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        Assertions.assertEquals(1.0F, mutNum.floatValue());
        Assertions.assertEquals(1.0, mutNum.doubleValue());
        Assertions.assertEquals(((byte) (1)), mutNum.byteValue());
        Assertions.assertEquals(((short) (1)), mutNum.shortValue());
        Assertions.assertEquals(1, mutNum.intValue());
        Assertions.assertEquals(1L, mutNum.longValue());
    }

    @Test
    public void testToByte() {
        Assertions.assertEquals(Byte.valueOf(((byte) (0))), new MutableByte(((byte) (0))).toByte());
        Assertions.assertEquals(Byte.valueOf(((byte) (123))), new MutableByte(((byte) (123))).toByte());
    }

    @Test
    public void testIncrement() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.increment();
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testIncrementAndGet() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        final byte result = mutNum.incrementAndGet();
        Assertions.assertEquals(2, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testGetAndIncrement() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        final byte result = mutNum.getAndIncrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(2, mutNum.intValue());
        Assertions.assertEquals(2L, mutNum.longValue());
    }

    @Test
    public void testDecrement() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.decrement();
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testDecrementAndGet() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        final byte result = mutNum.decrementAndGet();
        Assertions.assertEquals(0, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testGetAndDecrement() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        final byte result = mutNum.getAndDecrement();
        Assertions.assertEquals(1, result);
        Assertions.assertEquals(0, mutNum.intValue());
        Assertions.assertEquals(0L, mutNum.longValue());
    }

    @Test
    public void testAddValuePrimitive() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.add(((byte) (1)));
        Assertions.assertEquals(((byte) (2)), mutNum.byteValue());
    }

    @Test
    public void testAddValueObject() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.add(Integer.valueOf(1));
        Assertions.assertEquals(((byte) (2)), mutNum.byteValue());
    }

    @Test
    public void testGetAndAddValuePrimitive() {
        final MutableByte mutableByte = new MutableByte(((byte) (0)));
        final byte result = mutableByte.getAndAdd(((byte) (1)));
        Assertions.assertEquals(((byte) (0)), result);
        Assertions.assertEquals(((byte) (1)), mutableByte.byteValue());
    }

    @Test
    public void testGetAndAddValueObject() {
        final MutableByte mutableByte = new MutableByte(((byte) (0)));
        final byte result = mutableByte.getAndAdd(Byte.valueOf(((byte) (1))));
        Assertions.assertEquals(((byte) (0)), result);
        Assertions.assertEquals(((byte) (1)), mutableByte.byteValue());
    }

    @Test
    public void testAddAndGetValuePrimitive() {
        final MutableByte mutableByte = new MutableByte(((byte) (0)));
        final byte result = mutableByte.addAndGet(((byte) (1)));
        Assertions.assertEquals(((byte) (1)), result);
        Assertions.assertEquals(((byte) (1)), mutableByte.byteValue());
    }

    @Test
    public void testAddAndGetValueObject() {
        final MutableByte mutableByte = new MutableByte(((byte) (0)));
        final byte result = mutableByte.addAndGet(Byte.valueOf(((byte) (1))));
        Assertions.assertEquals(((byte) (1)), result);
        Assertions.assertEquals(((byte) (1)), mutableByte.byteValue());
    }

    @Test
    public void testSubtractValuePrimitive() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.subtract(((byte) (1)));
        Assertions.assertEquals(((byte) (0)), mutNum.byteValue());
    }

    @Test
    public void testSubtractValueObject() {
        final MutableByte mutNum = new MutableByte(((byte) (1)));
        mutNum.subtract(Integer.valueOf(1));
        Assertions.assertEquals(((byte) (0)), mutNum.byteValue());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("0", new MutableByte(((byte) (0))).toString());
        Assertions.assertEquals("10", new MutableByte(((byte) (10))).toString());
        Assertions.assertEquals("-123", new MutableByte(((byte) (-123))).toString());
    }
}

