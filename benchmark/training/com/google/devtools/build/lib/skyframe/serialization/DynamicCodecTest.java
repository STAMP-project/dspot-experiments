/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.skyframe.serialization;


import SerializationException.NoCodecException;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DynamicCodec}.
 */
@RunWith(JUnit4.class)
public final class DynamicCodecTest {
    private static class SimpleExample {
        private final String elt;

        private final String elt2;

        private final int x;

        private SimpleExample(String elt, String elt2, int x) {
            this.elt = elt;
            this.elt2 = elt2;
            this.x = x;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.SimpleExample)) {
                return false;
            }
            DynamicCodecTest.SimpleExample that = ((DynamicCodecTest.SimpleExample) (other));
            return ((Objects.equals(elt, that.elt)) && (Objects.equals(elt2, that.elt2))) && ((x) == (that.x));
        }
    }

    @Test
    public void testExample() throws Exception {
        new SerializationTester(new DynamicCodecTest.SimpleExample("a", "b", (-5)), new DynamicCodecTest.SimpleExample("a", null, 10)).addCodec(new DynamicCodec(DynamicCodecTest.SimpleExample.class)).makeMemoizing().runTests();
    }

    private static class ExampleSubclass extends DynamicCodecTest.SimpleExample {
        private final String elt;// duplicate name with superclass


        private ExampleSubclass(String elt1, String elt2, String elt3, int x) {
            super(elt1, elt2, x);
            this.elt = elt3;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.ExampleSubclass)) {
                return false;
            }
            if (!(super.equals(other))) {
                return false;
            }
            DynamicCodecTest.ExampleSubclass that = ((DynamicCodecTest.ExampleSubclass) (other));
            return Objects.equals(elt, that.elt);
        }
    }

    @Test
    public void testExampleSubclass() throws Exception {
        new SerializationTester(new DynamicCodecTest.ExampleSubclass("a", "b", "c", 0), new DynamicCodecTest.ExampleSubclass("a", null, null, 15)).addCodec(new DynamicCodec(DynamicCodecTest.ExampleSubclass.class)).makeMemoizing().runTests();
    }

    private static class ExampleSmallPrimitives {
        private final Void v;

        private final boolean bit;

        private final byte b;

        private final short s;

        private final char c;

        private ExampleSmallPrimitives(boolean bit, byte b, short s, char c) {
            this.v = null;
            this.bit = bit;
            this.b = b;
            this.s = s;
            this.c = c;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.ExampleSmallPrimitives)) {
                return false;
            }
            DynamicCodecTest.ExampleSmallPrimitives that = ((DynamicCodecTest.ExampleSmallPrimitives) (other));
            return (((((v) == (that.v)) && ((bit) == (that.bit))) && ((b) == (that.b))) && ((s) == (that.s))) && ((c) == (that.c));
        }
    }

    @Test
    public void testExampleSmallPrimitives() throws Exception {
        new SerializationTester(new DynamicCodecTest.ExampleSmallPrimitives(false, ((byte) (0)), ((short) (0)), 'a'), new DynamicCodecTest.ExampleSmallPrimitives(false, ((byte) (120)), ((short) (18000)), 'x'), new DynamicCodecTest.ExampleSmallPrimitives(true, Byte.MIN_VALUE, Short.MIN_VALUE, Character.MIN_VALUE), new DynamicCodecTest.ExampleSmallPrimitives(true, Byte.MAX_VALUE, Short.MAX_VALUE, Character.MAX_VALUE)).addCodec(new DynamicCodec(DynamicCodecTest.ExampleSmallPrimitives.class)).makeMemoizing().runTests();
    }

    private static class ExampleMediumPrimitives {
        private final int i;

        private final float f;

        private ExampleMediumPrimitives(int i, float f) {
            this.i = i;
            this.f = f;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.ExampleMediumPrimitives)) {
                return false;
            }
            DynamicCodecTest.ExampleMediumPrimitives that = ((DynamicCodecTest.ExampleMediumPrimitives) (other));
            return ((i) == (that.i)) && ((f) == (that.f));
        }
    }

    @Test
    public void testExampleMediumPrimitives() throws Exception {
        new SerializationTester(new DynamicCodecTest.ExampleMediumPrimitives(12345, 1.0E12F), new DynamicCodecTest.ExampleMediumPrimitives(67890, (-6.0E9F)), new DynamicCodecTest.ExampleMediumPrimitives(Integer.MIN_VALUE, Float.MIN_VALUE), new DynamicCodecTest.ExampleMediumPrimitives(Integer.MAX_VALUE, Float.MAX_VALUE)).addCodec(new DynamicCodec(DynamicCodecTest.ExampleMediumPrimitives.class)).makeMemoizing().runTests();
    }

    private static class ExampleLargePrimitives {
        private final long l;

        private final double d;

        private ExampleLargePrimitives(long l, double d) {
            this.l = l;
            this.d = d;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.ExampleLargePrimitives)) {
                return false;
            }
            DynamicCodecTest.ExampleLargePrimitives that = ((DynamicCodecTest.ExampleLargePrimitives) (other));
            return ((l) == (that.l)) && ((d) == (that.d));
        }
    }

    @Test
    public void testExampleLargePrimitives() throws Exception {
        new SerializationTester(new DynamicCodecTest.ExampleLargePrimitives(12345346523453L, 1.0E300), new DynamicCodecTest.ExampleLargePrimitives(678900093045L, (-9.0E180)), new DynamicCodecTest.ExampleLargePrimitives(Long.MIN_VALUE, Double.MIN_VALUE), new DynamicCodecTest.ExampleLargePrimitives(Long.MAX_VALUE, Double.MAX_VALUE)).addCodec(new DynamicCodec(DynamicCodecTest.ExampleLargePrimitives.class)).makeMemoizing().runTests();
    }

    private static class ArrayExample {
        String[] text;

        byte[] numbers;

        char[] chars;

        long[] longs;

        private ArrayExample(String[] text, byte[] numbers, char[] chars, long[] longs) {
            this.text = text;
            this.numbers = numbers;
            this.chars = chars;
            this.longs = longs;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.ArrayExample)) {
                return false;
            }
            DynamicCodecTest.ArrayExample that = ((DynamicCodecTest.ArrayExample) (other));
            return (((Arrays.equals(text, that.text)) && (Arrays.equals(numbers, that.numbers))) && (Arrays.equals(chars, that.chars))) && (Arrays.equals(longs, that.longs));
        }
    }

    @Test
    public void testArray() throws Exception {
        new SerializationTester(new DynamicCodecTest.ArrayExample(null, null, null, null), new DynamicCodecTest.ArrayExample(new String[]{  }, new byte[]{  }, new char[]{  }, new long[]{  }), new DynamicCodecTest.ArrayExample(new String[]{ "a", "b", "cde" }, new byte[]{ -1, 0, 1 }, new char[]{ 'a', 'b', 'c', 'x', 'y', 'z' }, new long[]{ Long.MAX_VALUE, Long.MIN_VALUE, 27983741982341L, 52893748523495834L })).addCodec(new DynamicCodec(DynamicCodecTest.ArrayExample.class)).runTests();
    }

    private static class NestedArrayExample {
        int[][] numbers;

        private NestedArrayExample(int[][] numbers) {
            this.numbers = numbers;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DynamicCodecTest.NestedArrayExample)) {
                return false;
            }
            DynamicCodecTest.NestedArrayExample that = ((DynamicCodecTest.NestedArrayExample) (other));
            return Arrays.deepEquals(numbers, that.numbers);
        }
    }

    @Test
    public void testNestedArray() throws Exception {
        new SerializationTester(new DynamicCodecTest.NestedArrayExample(null), new DynamicCodecTest.NestedArrayExample(new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6, 9 }, new int[]{ 7 } }), new DynamicCodecTest.NestedArrayExample(new int[][]{ new int[]{ 1, 2, 3 }, null, new int[]{ 7 } })).addCodec(new DynamicCodec(DynamicCodecTest.NestedArrayExample.class)).runTests();
    }

    private static class CycleA {
        private final int value;

        private DynamicCodecTest.CycleB b;

        private CycleA(int value) {
            this.value = value;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object other) {
            // Integrity check. Not really part of equals.
            assertThat(b.a).isEqualTo(this);
            if (!(other instanceof DynamicCodecTest.CycleA)) {
                return false;
            }
            DynamicCodecTest.CycleA that = ((DynamicCodecTest.CycleA) (other));
            // Consistency check. Not really part of equals.
            assertThat(that.b.a).isEqualTo(that);
            return ((value) == (that.value)) && ((b.value()) == (that.b.value));
        }
    }

    private static class CycleB {
        private final int value;

        private DynamicCodecTest.CycleA a;

        private CycleB(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    @Test
    public void testCyclic() throws Exception {
        new SerializationTester(DynamicCodecTest.createCycle(1, 2), DynamicCodecTest.createCycle(3, 4)).addCodec(new DynamicCodec(DynamicCodecTest.CycleA.class)).addCodec(new DynamicCodec(DynamicCodecTest.CycleB.class)).makeMemoizing().runTests();
    }

    enum EnumExample {

        ZERO,
        ONE,
        TWO,
        THREE;}

    static class PrimitiveExample {
        private final boolean booleanValue;

        private final int intValue;

        private final double doubleValue;

        private final DynamicCodecTest.EnumExample enumValue;

        private final String stringValue;

        PrimitiveExample(boolean booleanValue, int intValue, double doubleValue, DynamicCodecTest.EnumExample enumValue, String stringValue) {
            this.booleanValue = booleanValue;
            this.intValue = intValue;
            this.doubleValue = doubleValue;
            this.enumValue = enumValue;
            this.stringValue = stringValue;
        }

        // Testing
        @SuppressWarnings("EqualsHashCode")
        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            DynamicCodecTest.PrimitiveExample that = ((DynamicCodecTest.PrimitiveExample) (object));
            return (((((booleanValue) == (that.booleanValue)) && ((intValue) == (that.intValue))) && ((doubleValue) == (that.doubleValue))) && (Objects.equals(enumValue, that.enumValue))) && (Objects.equals(stringValue, that.stringValue));
        }
    }

    @Test
    public void testPrimitiveExample() throws Exception {
        new SerializationTester(new DynamicCodecTest.PrimitiveExample(true, 1, 1.1, DynamicCodecTest.EnumExample.ZERO, "foo"), new DynamicCodecTest.PrimitiveExample(false, (-1), (-5.5), DynamicCodecTest.EnumExample.ONE, "bar"), new DynamicCodecTest.PrimitiveExample(true, 5, 20.0, DynamicCodecTest.EnumExample.THREE, null), new DynamicCodecTest.PrimitiveExample(true, 100, 100, null, "hello")).addCodec(new DynamicCodec(DynamicCodecTest.PrimitiveExample.class)).addCodec(new EnumCodec(DynamicCodecTest.EnumExample.class)).setRepetitions(100000).runTests();
    }

    private static class NoCodecExample2 {
        @SuppressWarnings("unused")
        private final BufferedInputStream noCodec = new BufferedInputStream(null);
    }

    private static class NoCodecExample1 {
        @SuppressWarnings("unused")
        private final DynamicCodecTest.NoCodecExample2 noCodec = new DynamicCodecTest.NoCodecExample2();
    }

    @Test
    public void testNoCodecExample() throws Exception {
        ObjectCodecs codecs = new ObjectCodecs(AutoRegistry.get(), ImmutableMap.of());
        try {
            codecs.serializeMemoized(new DynamicCodecTest.NoCodecExample1());
            Assert.fail();
        } catch (SerializationException expected) {
            assertThat(expected).hasMessageThat().contains(("java.io.BufferedInputStream [" + (((("java.io.BufferedInputStream, " + "com.google.devtools.build.lib.skyframe.serialization.") + "DynamicCodecTest$NoCodecExample2, ") + "com.google.devtools.build.lib.skyframe.serialization.") + "DynamicCodecTest$NoCodecExample1]")));
        }
    }
}

