/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests for {@link RandomUtils}
 */
public class RandomUtilsTest {
    /**
     * For comparing doubles and floats
     */
    private static final double DELTA = 1.0E-5;

    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new RandomUtils());
        final Constructor<?>[] cons = RandomUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(RandomUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(RandomUtils.class.getModifiers()));
    }

    @Test
    public void testNextBytesNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextBytes((-1)));
    }

    @Test
    public void testNextIntNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextInt((-1), 1));
    }

    @Test
    public void testNextLongNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextLong((-1), 1));
    }

    @Test
    public void testNextDoubleNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextDouble((-1), 1));
    }

    @Test
    public void testNextFloatNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextFloat((-1), 1));
    }

    @Test
    public void testNextIntLowerGreaterUpper() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextInt(2, 1));
    }

    @Test
    public void testNextLongLowerGreaterUpper() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextLong(2, 1));
    }

    @Test
    public void testNextDoubleLowerGreaterUpper() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextDouble(2, 1));
    }

    @Test
    public void testNextFloatLowerGreaterUpper() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextFloat(2, 1));
    }

    /**
     * Tests next boolean
     */
    @Test
    public void testBoolean() {
        final boolean result = RandomUtils.nextBoolean();
        Assertions.assertTrue(((result == true) || (result == false)));
    }

    /**
     * Tests a zero byte array length.
     */
    @Test
    public void testZeroLengthNextBytes() {
        Assertions.assertArrayEquals(new byte[0], RandomUtils.nextBytes(0));
    }

    /**
     * Tests random byte array.
     */
    @Test
    public void testNextBytes() {
        final byte[] result = RandomUtils.nextBytes(20);
        Assertions.assertEquals(20, result.length);
    }

    /**
     * Test next int range with minimal range.
     */
    @Test
    public void testNextIntMinimalRange() {
        Assertions.assertEquals(42, RandomUtils.nextInt(42, 42));
    }

    /**
     * Tests next int range.
     */
    @Test
    public void testNextInt() {
        final int result = RandomUtils.nextInt(33, 42);
        Assertions.assertTrue(((result >= 33) && (result < 42)));
    }

    /**
     * Tests next double range, random result.
     */
    @Test
    public void testNextIntRandomResult() {
        final int randomResult = RandomUtils.nextInt();
        Assertions.assertTrue((randomResult > 0));
        Assertions.assertTrue((randomResult < (Integer.MAX_VALUE)));
    }

    /**
     * Test next double range with minimal range.
     */
    @Test
    public void testNextDoubleMinimalRange() {
        Assertions.assertEquals(42.1, RandomUtils.nextDouble(42.1, 42.1), RandomUtilsTest.DELTA);
    }

    /**
     * Test next float range with minimal range.
     */
    @Test
    public void testNextFloatMinimalRange() {
        Assertions.assertEquals(42.1F, RandomUtils.nextFloat(42.1F, 42.1F), RandomUtilsTest.DELTA);
    }

    /**
     * Tests next double range.
     */
    @Test
    public void testNextDouble() {
        final double result = RandomUtils.nextDouble(33.0, 42.0);
        Assertions.assertTrue(((result >= 33.0) && (result <= 42.0)));
    }

    /**
     * Tests next double range, random result.
     */
    @Test
    public void testNextDoubleRandomResult() {
        final double randomResult = RandomUtils.nextDouble();
        Assertions.assertTrue((randomResult > 0));
        Assertions.assertTrue((randomResult < (Double.MAX_VALUE)));
    }

    /**
     * Tests next float range.
     */
    @Test
    public void testNextFloat() {
        final double result = RandomUtils.nextFloat(33.0F, 42.0F);
        Assertions.assertTrue(((result >= 33.0F) && (result <= 42.0F)));
    }

    /**
     * Tests next float range, random result.
     */
    @Test
    public void testNextFloatRandomResult() {
        final float randomResult = RandomUtils.nextFloat();
        Assertions.assertTrue((randomResult > 0));
        Assertions.assertTrue((randomResult < (Float.MAX_VALUE)));
    }

    /**
     * Test next long range with minimal range.
     */
    @Test
    public void testNextLongMinimalRange() {
        Assertions.assertEquals(42L, RandomUtils.nextLong(42L, 42L));
    }

    /**
     * Tests next long range.
     */
    @Test
    public void testNextLong() {
        final long result = RandomUtils.nextLong(33L, 42L);
        Assertions.assertTrue(((result >= 33L) && (result < 42L)));
    }

    /**
     * Tests next long range, random result.
     */
    @Test
    public void testNextLongRandomResult() {
        final long randomResult = RandomUtils.nextLong();
        Assertions.assertTrue((randomResult > 0));
        Assertions.assertTrue((randomResult < (Long.MAX_VALUE)));
    }

    /**
     * Tests extreme range.
     */
    @Test
    public void testExtremeRangeInt() {
        final int result = RandomUtils.nextInt(0, Integer.MAX_VALUE);
        Assertions.assertTrue(((result >= 0) && (result < (Integer.MAX_VALUE))));
    }

    /**
     * Tests extreme range.
     */
    @Test
    public void testExtremeRangeLong() {
        final long result = RandomUtils.nextLong(0, Long.MAX_VALUE);
        Assertions.assertTrue(((result >= 0) && (result < (Long.MAX_VALUE))));
    }

    /**
     * Tests extreme range.
     */
    @Test
    public void testExtremeRangeFloat() {
        final float result = RandomUtils.nextFloat(0, Float.MAX_VALUE);
        Assertions.assertTrue(((result >= 0.0F) && (result <= (Float.MAX_VALUE))));
    }

    /**
     * Tests extreme range.
     */
    @Test
    public void testExtremeRangeDouble() {
        final double result = RandomUtils.nextDouble(0, Double.MAX_VALUE);
        Assertions.assertTrue(((result >= 0) && (result <= (Double.MAX_VALUE))));
    }
}

