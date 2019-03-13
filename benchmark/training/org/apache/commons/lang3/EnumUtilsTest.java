/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.lang3;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 */
public class EnumUtilsTest {
    @Test
    public void testConstructable() {
        // enforce public constructor
        new EnumUtils();
    }

    @Test
    public void test_getEnumMap() {
        final Map<String, Traffic> test = EnumUtils.getEnumMap(Traffic.class);
        Assertions.assertEquals("{RED=RED, AMBER=AMBER, GREEN=GREEN}", test.toString(), "getEnumMap not created correctly");
        Assertions.assertEquals(3, test.size());
        Assertions.assertTrue(test.containsKey("RED"));
        Assertions.assertEquals(Traffic.RED, test.get("RED"));
        Assertions.assertTrue(test.containsKey("AMBER"));
        Assertions.assertEquals(Traffic.AMBER, test.get("AMBER"));
        Assertions.assertTrue(test.containsKey("GREEN"));
        Assertions.assertEquals(Traffic.GREEN, test.get("GREEN"));
        Assertions.assertFalse(test.containsKey("PURPLE"));
    }

    @Test
    public void test_getEnumList() {
        final List<Traffic> test = EnumUtils.getEnumList(Traffic.class);
        Assertions.assertEquals(3, test.size());
        Assertions.assertEquals(Traffic.RED, test.get(0));
        Assertions.assertEquals(Traffic.AMBER, test.get(1));
        Assertions.assertEquals(Traffic.GREEN, test.get(2));
    }

    @Test
    public void test_isValidEnum() {
        Assertions.assertTrue(EnumUtils.isValidEnum(Traffic.class, "RED"));
        Assertions.assertTrue(EnumUtils.isValidEnum(Traffic.class, "AMBER"));
        Assertions.assertTrue(EnumUtils.isValidEnum(Traffic.class, "GREEN"));
        Assertions.assertFalse(EnumUtils.isValidEnum(Traffic.class, "PURPLE"));
        Assertions.assertFalse(EnumUtils.isValidEnum(Traffic.class, null));
    }

    @Test
    public void test_isValidEnum_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.isValidEnum(null, "PURPLE"));
    }

    @Test
    public void test_isValidEnumIgnoreCase() {
        Assertions.assertTrue(EnumUtils.isValidEnumIgnoreCase(Traffic.class, "red"));
        Assertions.assertTrue(EnumUtils.isValidEnumIgnoreCase(Traffic.class, "Amber"));
        Assertions.assertTrue(EnumUtils.isValidEnumIgnoreCase(Traffic.class, "grEEn"));
        Assertions.assertFalse(EnumUtils.isValidEnumIgnoreCase(Traffic.class, "purple"));
        Assertions.assertFalse(EnumUtils.isValidEnumIgnoreCase(Traffic.class, null));
    }

    @Test
    public void test_isValidEnumIgnoreCase_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.isValidEnumIgnoreCase(null, "PURPLE"));
    }

    @Test
    public void test_getEnum() {
        Assertions.assertEquals(Traffic.RED, EnumUtils.getEnum(Traffic.class, "RED"));
        Assertions.assertEquals(Traffic.AMBER, EnumUtils.getEnum(Traffic.class, "AMBER"));
        Assertions.assertEquals(Traffic.GREEN, EnumUtils.getEnum(Traffic.class, "GREEN"));
        Assertions.assertNull(EnumUtils.getEnum(Traffic.class, "PURPLE"));
        Assertions.assertNull(EnumUtils.getEnum(Traffic.class, null));
    }

    @Test
    public void test_getEnum_nonEnumClass() {
        final Class rawType = Object.class;
        Assertions.assertNull(EnumUtils.getEnum(rawType, "rawType"));
    }

    @Test
    public void test_getEnum_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.getEnum(((Class<Traffic>) (null)), "PURPLE"));
    }

    @Test
    public void test_getEnumIgnoreCase() {
        Assertions.assertEquals(Traffic.RED, EnumUtils.getEnumIgnoreCase(Traffic.class, "red"));
        Assertions.assertEquals(Traffic.AMBER, EnumUtils.getEnumIgnoreCase(Traffic.class, "Amber"));
        Assertions.assertEquals(Traffic.GREEN, EnumUtils.getEnumIgnoreCase(Traffic.class, "grEEn"));
        Assertions.assertNull(EnumUtils.getEnumIgnoreCase(Traffic.class, "purple"));
        Assertions.assertNull(EnumUtils.getEnumIgnoreCase(Traffic.class, null));
    }

    @Test
    public void test_getEnumIgnoreCase_nonEnumClass() {
        final Class rawType = Object.class;
        Assertions.assertNull(EnumUtils.getEnumIgnoreCase(rawType, "rawType"));
    }

    @Test
    public void test_getEnumIgnoreCase_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.getEnumIgnoreCase(((Class<Traffic>) (null)), "PURPLE"));
    }

    @Test
    public void test_generateBitVector_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVector(null, EnumSet.of(Traffic.RED)));
    }

    @Test
    public void test_generateBitVectors_nullClass() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVectors(null, EnumSet.of(Traffic.RED)));
    }

    @Test
    public void test_generateBitVector_nullIterable() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVector(Traffic.class, ((Iterable<Traffic>) (null))));
    }

    @Test
    public void test_generateBitVectors_nullIterable() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVectors(null, ((Iterable<Traffic>) (null))));
    }

    @Test
    public void test_generateBitVector_nullElement() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(Traffic.class, Arrays.asList(Traffic.RED, null)));
    }

    @Test
    public void test_generateBitVectors_nullElement() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVectors(Traffic.class, Arrays.asList(Traffic.RED, null)));
    }

    @Test
    public void test_generateBitVector_nullClassWithArray() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVector(null, Traffic.RED));
    }

    @Test
    public void test_generateBitVectors_nullClassWithArray() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVectors(null, Traffic.RED));
    }

    @Test
    public void test_generateBitVector_nullArray() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVector(Traffic.class, ((Traffic[]) (null))));
    }

    @Test
    public void test_generateBitVectors_nullArray() {
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.generateBitVectors(Traffic.class, ((Traffic[]) (null))));
    }

    @Test
    public void test_generateBitVector_nullArrayElement() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(Traffic.class, Traffic.RED, null));
    }

    @Test
    public void test_generateBitVectors_nullArrayElement() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVectors(Traffic.class, Traffic.RED, null));
    }

    @Test
    public void test_generateBitVector_longClass() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(TooMany.class, EnumSet.of(TooMany.A1)));
    }

    @Test
    public void test_generateBitVector_longClassWithArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(TooMany.class, TooMany.A1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_generateBitVector_nonEnumClass() {
        @SuppressWarnings("rawtypes")
        final Class rawType = Object.class;
        @SuppressWarnings("rawtypes")
        final List rawList = new ArrayList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(rawType, rawList));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_generateBitVectors_nonEnumClass() {
        @SuppressWarnings("rawtypes")
        final Class rawType = Object.class;
        @SuppressWarnings("rawtypes")
        final List rawList = new ArrayList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVectors(rawType, rawList));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_generateBitVector_nonEnumClassWithArray() {
        @SuppressWarnings("rawtypes")
        final Class rawType = Object.class;
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVector(rawType));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_generateBitVectors_nonEnumClassWithArray() {
        @SuppressWarnings("rawtypes")
        final Class rawType = Object.class;
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.generateBitVectors(rawType));
    }

    @Test
    public void test_generateBitVector() {
        Assertions.assertEquals(0L, EnumUtils.generateBitVector(Traffic.class, EnumSet.noneOf(Traffic.class)));
        Assertions.assertEquals(1L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.RED)));
        Assertions.assertEquals(2L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.AMBER)));
        Assertions.assertEquals(4L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.GREEN)));
        Assertions.assertEquals(3L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.RED, Traffic.AMBER)));
        Assertions.assertEquals(5L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.RED, Traffic.GREEN)));
        Assertions.assertEquals(6L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.AMBER, Traffic.GREEN)));
        Assertions.assertEquals(7L, EnumUtils.generateBitVector(Traffic.class, EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN)));
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        Assertions.assertEquals((1L << 31), EnumUtils.generateBitVector(Enum64.class, EnumSet.of(Enum64.A31)));
        Assertions.assertEquals((1L << 32), EnumUtils.generateBitVector(Enum64.class, EnumSet.of(Enum64.A32)));
        Assertions.assertEquals((1L << 63), EnumUtils.generateBitVector(Enum64.class, EnumSet.of(Enum64.A63)));
        Assertions.assertEquals(Long.MIN_VALUE, EnumUtils.generateBitVector(Enum64.class, EnumSet.of(Enum64.A63)));
    }

    @Test
    public void test_generateBitVectors() {
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.noneOf(Traffic.class)), 0L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.RED)), 1L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.AMBER)), 2L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.GREEN)), 4L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.RED, Traffic.AMBER)), 3L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.RED, Traffic.GREEN)), 5L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.AMBER, Traffic.GREEN)), 6L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN)), 7L);
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, EnumSet.of(Enum64.A31)), (1L << 31));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, EnumSet.of(Enum64.A32)), (1L << 32));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, EnumSet.of(Enum64.A63)), (1L << 63));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, EnumSet.of(Enum64.A63)), Long.MIN_VALUE);
        // More than 64 values Enum
        assertArrayEquals(EnumUtils.generateBitVectors(TooMany.class, EnumSet.of(TooMany.M2)), 1L, 0L);
        assertArrayEquals(EnumUtils.generateBitVectors(TooMany.class, EnumSet.of(TooMany.L2, TooMany.M2)), 1L, (1L << 63));
    }

    @Test
    public void test_generateBitVectorFromArray() {
        Assertions.assertEquals(0L, EnumUtils.generateBitVector(Traffic.class));
        Assertions.assertEquals(1L, EnumUtils.generateBitVector(Traffic.class, Traffic.RED));
        Assertions.assertEquals(2L, EnumUtils.generateBitVector(Traffic.class, Traffic.AMBER));
        Assertions.assertEquals(4L, EnumUtils.generateBitVector(Traffic.class, Traffic.GREEN));
        Assertions.assertEquals(3L, EnumUtils.generateBitVector(Traffic.class, Traffic.RED, Traffic.AMBER));
        Assertions.assertEquals(5L, EnumUtils.generateBitVector(Traffic.class, Traffic.RED, Traffic.GREEN));
        Assertions.assertEquals(6L, EnumUtils.generateBitVector(Traffic.class, Traffic.AMBER, Traffic.GREEN));
        Assertions.assertEquals(7L, EnumUtils.generateBitVector(Traffic.class, Traffic.RED, Traffic.AMBER, Traffic.GREEN));
        // gracefully handles duplicates:
        Assertions.assertEquals(7L, EnumUtils.generateBitVector(Traffic.class, Traffic.RED, Traffic.AMBER, Traffic.GREEN, Traffic.GREEN));
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        Assertions.assertEquals((1L << 31), EnumUtils.generateBitVector(Enum64.class, Enum64.A31));
        Assertions.assertEquals((1L << 32), EnumUtils.generateBitVector(Enum64.class, Enum64.A32));
        Assertions.assertEquals((1L << 63), EnumUtils.generateBitVector(Enum64.class, Enum64.A63));
        Assertions.assertEquals(Long.MIN_VALUE, EnumUtils.generateBitVector(Enum64.class, Enum64.A63));
    }

    @Test
    public void test_generateBitVectorsFromArray() {
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class), 0L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.RED), 1L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.AMBER), 2L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.GREEN), 4L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.RED, Traffic.AMBER), 3L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.RED, Traffic.GREEN), 5L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.AMBER, Traffic.GREEN), 6L);
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.RED, Traffic.AMBER, Traffic.GREEN), 7L);
        // gracefully handles duplicates:
        assertArrayEquals(EnumUtils.generateBitVectors(Traffic.class, Traffic.RED, Traffic.AMBER, Traffic.GREEN, Traffic.GREEN), 7L);
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, Enum64.A31), (1L << 31));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, Enum64.A32), (1L << 32));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, Enum64.A63), (1L << 63));
        assertArrayEquals(EnumUtils.generateBitVectors(Enum64.class, Enum64.A63), Long.MIN_VALUE);
        // More than 64 values Enum
        assertArrayEquals(EnumUtils.generateBitVectors(TooMany.class, TooMany.M2), 1L, 0L);
        assertArrayEquals(EnumUtils.generateBitVectors(TooMany.class, TooMany.L2, TooMany.M2), 1L, (1L << 63));
    }

    @Test
    public void test_processBitVector_nullClass() {
        final Class<Traffic> empty = null;
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.processBitVector(empty, 0L));
    }

    @Test
    public void test_processBitVectors_nullClass() {
        final Class<Traffic> empty = null;
        Assertions.assertThrows(NullPointerException.class, () -> EnumUtils.processBitVectors(empty, 0L));
    }

    @Test
    public void test_processBitVector() {
        Assertions.assertEquals(EnumSet.noneOf(Traffic.class), EnumUtils.processBitVector(Traffic.class, 0L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED), EnumUtils.processBitVector(Traffic.class, 1L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER), EnumUtils.processBitVector(Traffic.class, 2L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER), EnumUtils.processBitVector(Traffic.class, 3L));
        Assertions.assertEquals(EnumSet.of(Traffic.GREEN), EnumUtils.processBitVector(Traffic.class, 4L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.GREEN), EnumUtils.processBitVector(Traffic.class, 5L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVector(Traffic.class, 6L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVector(Traffic.class, 7L));
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        Assertions.assertEquals(EnumSet.of(Enum64.A31), EnumUtils.processBitVector(Enum64.class, (1L << 31)));
        Assertions.assertEquals(EnumSet.of(Enum64.A32), EnumUtils.processBitVector(Enum64.class, (1L << 32)));
        Assertions.assertEquals(EnumSet.of(Enum64.A63), EnumUtils.processBitVector(Enum64.class, (1L << 63)));
        Assertions.assertEquals(EnumSet.of(Enum64.A63), EnumUtils.processBitVector(Enum64.class, Long.MIN_VALUE));
    }

    @Test
    public void test_processBitVectors() {
        Assertions.assertEquals(EnumSet.noneOf(Traffic.class), EnumUtils.processBitVectors(Traffic.class, 0L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED), EnumUtils.processBitVectors(Traffic.class, 1L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 2L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 3L));
        Assertions.assertEquals(EnumSet.of(Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 4L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 5L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 6L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 7L));
        Assertions.assertEquals(EnumSet.noneOf(Traffic.class), EnumUtils.processBitVectors(Traffic.class, 0L, 0L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED), EnumUtils.processBitVectors(Traffic.class, 0L, 1L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 0L, 2L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 0L, 3L));
        Assertions.assertEquals(EnumSet.of(Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 0L, 4L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 0L, 5L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 0L, 6L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 0L, 7L));
        // demonstrate tolerance of irrelevant high-order digits:
        Assertions.assertEquals(EnumSet.noneOf(Traffic.class), EnumUtils.processBitVectors(Traffic.class, 666L, 0L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED), EnumUtils.processBitVectors(Traffic.class, 666L, 1L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 666L, 2L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER), EnumUtils.processBitVectors(Traffic.class, 666L, 3L));
        Assertions.assertEquals(EnumSet.of(Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 666L, 4L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 666L, 5L));
        Assertions.assertEquals(EnumSet.of(Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 666L, 6L));
        Assertions.assertEquals(EnumSet.of(Traffic.RED, Traffic.AMBER, Traffic.GREEN), EnumUtils.processBitVectors(Traffic.class, 666L, 7L));
        // 64 values Enum (to test whether no int<->long jdk convertion issue exists)
        Assertions.assertEquals(EnumSet.of(Enum64.A31), EnumUtils.processBitVectors(Enum64.class, (1L << 31)));
        Assertions.assertEquals(EnumSet.of(Enum64.A32), EnumUtils.processBitVectors(Enum64.class, (1L << 32)));
        Assertions.assertEquals(EnumSet.of(Enum64.A63), EnumUtils.processBitVectors(Enum64.class, (1L << 63)));
        Assertions.assertEquals(EnumSet.of(Enum64.A63), EnumUtils.processBitVectors(Enum64.class, Long.MIN_VALUE));
    }

    @Test
    public void test_processBitVector_longClass() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.processBitVector(TooMany.class, 0L));
    }

    @Test
    public void test_processBitVectors_longClass() {
        Assertions.assertEquals(EnumSet.noneOf(TooMany.class), EnumUtils.processBitVectors(TooMany.class, 0L));
        Assertions.assertEquals(EnumSet.of(TooMany.A), EnumUtils.processBitVectors(TooMany.class, 1L));
        Assertions.assertEquals(EnumSet.of(TooMany.B), EnumUtils.processBitVectors(TooMany.class, 2L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B), EnumUtils.processBitVectors(TooMany.class, 3L));
        Assertions.assertEquals(EnumSet.of(TooMany.C), EnumUtils.processBitVectors(TooMany.class, 4L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 5L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 6L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 7L));
        Assertions.assertEquals(EnumSet.noneOf(TooMany.class), EnumUtils.processBitVectors(TooMany.class, 0L, 0L));
        Assertions.assertEquals(EnumSet.of(TooMany.A), EnumUtils.processBitVectors(TooMany.class, 0L, 1L));
        Assertions.assertEquals(EnumSet.of(TooMany.B), EnumUtils.processBitVectors(TooMany.class, 0L, 2L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B), EnumUtils.processBitVectors(TooMany.class, 0L, 3L));
        Assertions.assertEquals(EnumSet.of(TooMany.C), EnumUtils.processBitVectors(TooMany.class, 0L, 4L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 0L, 5L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 0L, 6L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 0L, 7L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C), EnumUtils.processBitVectors(TooMany.class, 0L, 7L));
        Assertions.assertEquals(EnumSet.of(TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 0L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 1L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 2L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 3L));
        Assertions.assertEquals(EnumSet.of(TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 4L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 5L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 6L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 7L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 1L, 7L));
        // demonstrate tolerance of irrelevant high-order digits:
        Assertions.assertEquals(EnumSet.of(TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 0L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 1L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 2L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 3L));
        Assertions.assertEquals(EnumSet.of(TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 4L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 5L));
        Assertions.assertEquals(EnumSet.of(TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 6L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 7L));
        Assertions.assertEquals(EnumSet.of(TooMany.A, TooMany.B, TooMany.C, TooMany.M2), EnumUtils.processBitVectors(TooMany.class, 9L, 7L));
    }
}

