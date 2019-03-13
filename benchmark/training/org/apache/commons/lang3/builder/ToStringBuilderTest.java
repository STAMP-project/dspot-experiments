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
package org.apache.commons.lang3.builder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for {@link org.apache.commons.lang3.builder.ToStringBuilder}.
 */
public class ToStringBuilderTest {
    // See LANG-1337 for more.
    private static final int ARRAYLIST_INITIAL_CAPACITY = 10;

    private final Integer base = Integer.valueOf(5);

    private final String baseStr = ((base.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(base)));

    // -----------------------------------------------------------------------
    @Test
    public void testConstructorEx1() {
        Assertions.assertEquals("<null>", new ToStringBuilder(null).toString());
    }

    @Test
    public void testConstructorEx2() {
        Assertions.assertEquals("<null>", new ToStringBuilder(null, null).toString());
        new ToStringBuilder(this.base, null).toString();
    }

    @Test
    public void testConstructorEx3() {
        Assertions.assertEquals("<null>", new ToStringBuilder(null, null, null).toString());
        new ToStringBuilder(this.base, null, null).toString();
        new ToStringBuilder(this.base, ToStringStyle.DEFAULT_STYLE, null).toString();
    }

    @Test
    public void testGetSetDefault() {
        try {
            ToStringBuilder.setDefaultStyle(ToStringStyle.NO_FIELD_NAMES_STYLE);
            Assertions.assertSame(ToStringStyle.NO_FIELD_NAMES_STYLE, ToStringBuilder.getDefaultStyle());
        } finally {
            // reset for other tests
            ToStringBuilder.setDefaultStyle(ToStringStyle.DEFAULT_STYLE);
        }
    }

    @Test
    public void testSetDefaultEx() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ToStringBuilder.setDefaultStyle(null));
    }

    @Test
    public void testBlank() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).toString());
    }

    /**
     * Test wrapper for int primitive.
     */
    @Test
    public void testReflectionInteger() {
        Assertions.assertEquals(((baseStr) + "[value=5]"), ToStringBuilder.reflectionToString(base));
    }

    /**
     * Test wrapper for char primitive.
     */
    @Test
    public void testReflectionCharacter() {
        final Character c = 'A';
        Assertions.assertEquals(((this.toBaseString(c)) + "[value=A]"), ToStringBuilder.reflectionToString(c));
    }

    /**
     * Test wrapper for char boolean.
     */
    @Test
    public void testReflectionBoolean() {
        Boolean b;
        b = Boolean.TRUE;
        Assertions.assertEquals(((this.toBaseString(b)) + "[value=true]"), ToStringBuilder.reflectionToString(b));
        b = Boolean.FALSE;
        Assertions.assertEquals(((this.toBaseString(b)) + "[value=false]"), ToStringBuilder.reflectionToString(b));
    }

    @Test
    public void testReflectionObjectArray() {
        Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{<null>,5,{3,6}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionLongArray() {
        long[] array = new long[]{ 1, 2, -3, 4 };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1,2,-3,4}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionIntArray() {
        int[] array = new int[]{ 1, 2, -3, 4 };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1,2,-3,4}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionShortArray() {
        short[] array = new short[]{ 1, 2, -3, 4 };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1,2,-3,4}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionyteArray() {
        byte[] array = new byte[]{ 1, 2, -3, 4 };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1,2,-3,4}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionCharArray() {
        char[] array = new char[]{ 'A', '2', '_', 'D' };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{A,2,_,D}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionDoubleArray() {
        double[] array = new double[]{ 1.0, 2.9876, -3.00001, 4.3 };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1.0,2.9876,-3.00001,4.3}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionFloatArray() {
        float[] array = new float[]{ 1.0F, 2.9876F, -3.00001F, 4.3F };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{1.0,2.9876,-3.00001,4.3}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionBooleanArray() {
        boolean[] array = new boolean[]{ true, false, false };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{true,false,false}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    // Reflection Array Array tests
    @Test
    public void testReflectionFloatArrayArray() {
        float[][] array = new float[][]{ new float[]{ 1.0F, 2.29686F }, null, new float[]{ Float.NaN } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1.0,2.29686},<null>,{NaN}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1,2},<null>,{5}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionIntArrayArray() {
        int[][] array = new int[][]{ new int[]{ 1, 2 }, null, new int[]{ 5 } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1,2},<null>,{5}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionhortArrayArray() {
        short[][] array = new short[][]{ new short[]{ 1, 2 }, null, new short[]{ 5 } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1,2},<null>,{5}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionByteArrayArray() {
        byte[][] array = new byte[][]{ new byte[]{ 1, 2 }, null, new byte[]{ 5 } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1,2},<null>,{5}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionCharArrayArray() {
        char[][] array = new char[][]{ new char[]{ 'A', 'B' }, null, new char[]{ 'p' } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{A,B},<null>,{p}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionDoubleArrayArray() {
        double[][] array = new double[][]{ new double[]{ 1.0, 2.29686 }, null, new double[]{ Double.NaN } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{1.0,2.29686},<null>,{NaN}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    @Test
    public void testReflectionBooleanArrayArray() {
        boolean[][] array = new boolean[][]{ new boolean[]{ true, false }, null, new boolean[]{ false } };
        final String baseString = this.toBaseString(array);
        Assertions.assertEquals((baseString + "[{{true,false},<null>,{false}}]"), ToStringBuilder.reflectionToString(array));
        Assertions.assertEquals((baseString + "[{{true,false},<null>,{false}}]"), ToStringBuilder.reflectionToString(array));
        array = null;
        assertReflectionArray("<null>", array);
    }

    // Reflection hierarchy tests
    @Test
    public void testReflectionHierarchyArrayList() {
        // LANG-1337 without this, the generated string can differ depending on the JVM version/vendor
        final List<Object> list = new ArrayList<>(ToStringBuilderTest.ARRAYLIST_INITIAL_CAPACITY);
        final String baseString = this.toBaseString(list);
        final String expectedWithTransients = baseString + "[elementData={<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>},size=0,modCount=0]";
        final String toStringWithTransients = ToStringBuilder.reflectionToString(list, null, true);
        if (!(expectedWithTransients.equals(toStringWithTransients))) {
            Assertions.assertEquals(expectedWithTransients, toStringWithTransients);
        }
        final String expectedWithoutTransients = baseString + "[size=0]";
        final String toStringWithoutTransients = ToStringBuilder.reflectionToString(list, null, false);
        if (!(expectedWithoutTransients.equals(toStringWithoutTransients))) {
            Assertions.assertEquals(expectedWithoutTransients, toStringWithoutTransients);
        }
    }

    @Test
    public void testReflectionHierarchy() {
        final ToStringBuilderTest.ReflectionTestFixtureA baseA = new ToStringBuilderTest.ReflectionTestFixtureA();
        String baseString = this.toBaseString(baseA);
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA));
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA, null));
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA, null, false));
        Assertions.assertEquals((baseString + "[a=a,transientA=t]"), ToStringBuilder.reflectionToString(baseA, null, true));
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA, null, false, null));
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA, null, false, Object.class));
        Assertions.assertEquals((baseString + "[a=a]"), ToStringBuilder.reflectionToString(baseA, null, false, ToStringBuilderTest.ReflectionTestFixtureA.class));
        final ToStringBuilderTest.ReflectionTestFixtureB baseB = new ToStringBuilderTest.ReflectionTestFixtureB();
        baseString = this.toBaseString(baseB);
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB, null));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB, null, false));
        Assertions.assertEquals((baseString + "[b=b,transientB=t,a=a,transientA=t]"), ToStringBuilder.reflectionToString(baseB, null, true));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB, null, false, null));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB, null, false, Object.class));
        Assertions.assertEquals((baseString + "[b=b,a=a]"), ToStringBuilder.reflectionToString(baseB, null, false, ToStringBuilderTest.ReflectionTestFixtureA.class));
        Assertions.assertEquals((baseString + "[b=b]"), ToStringBuilder.reflectionToString(baseB, null, false, ToStringBuilderTest.ReflectionTestFixtureB.class));
    }

    static class ReflectionTestFixtureA {
        @SuppressWarnings("unused")
        private final char a = 'a';

        @SuppressWarnings("unused")
        private transient char transientA = 't';
    }

    static class ReflectionTestFixtureB extends ToStringBuilderTest.ReflectionTestFixtureA {
        @SuppressWarnings("unused")
        private final char b = 'b';

        @SuppressWarnings("unused")
        private transient char transientB = 't';
    }

    @Test
    public void testInnerClassReflection() {
        final ToStringBuilderTest.Outer outer = new ToStringBuilderTest.Outer();
        Assertions.assertEquals(((((toBaseString(outer)) + "[inner=") + (toBaseString(outer.inner))) + "[]]"), outer.toString());
    }

    static class Outer {
        ToStringBuilderTest.Outer.Inner inner = new ToStringBuilderTest.Outer.Inner();

        class Inner {
            @Override
            public String toString() {
                return ToStringBuilder.reflectionToString(this);
            }
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    // Reflection cycle tests
    /**
     * Test an array element pointing to its container.
     */
    @Test
    public void testReflectionArrayCycle() {
        final Object[] objects = new Object[1];
        objects[0] = objects;
        Assertions.assertEquals(((((this.toBaseString(objects)) + "[{") + (this.toBaseString(objects))) + "}]"), ToStringBuilder.reflectionToString(objects));
    }

    /**
     * Test an array element pointing to its container.
     */
    @Test
    public void testReflectionArrayCycleLevel2() {
        final Object[] objects = new Object[1];
        final Object[] objectsLevel2 = new Object[1];
        objects[0] = objectsLevel2;
        objectsLevel2[0] = objects;
        Assertions.assertEquals(((((this.toBaseString(objects)) + "[{{") + (this.toBaseString(objects))) + "}}]"), ToStringBuilder.reflectionToString(objects));
        Assertions.assertEquals(((((this.toBaseString(objectsLevel2)) + "[{{") + (this.toBaseString(objectsLevel2))) + "}}]"), ToStringBuilder.reflectionToString(objectsLevel2));
    }

    @Test
    public void testReflectionArrayArrayCycle() {
        final Object[][] objects = new Object[2][2];
        objects[0][0] = objects;
        objects[0][1] = objects;
        objects[1][0] = objects;
        objects[1][1] = objects;
        final String basicToString = this.toBaseString(objects);
        Assertions.assertEquals((((((((((basicToString + "[{{") + basicToString) + ",") + basicToString) + "},{") + basicToString) + ",") + basicToString) + "}}]"), ToStringBuilder.reflectionToString(objects));
    }

    /**
     * A reflection test fixture.
     */
    static class ReflectionTestCycleA {
        ToStringBuilderTest.ReflectionTestCycleB b;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * A reflection test fixture.
     */
    static class ReflectionTestCycleB {
        ToStringBuilderTest.ReflectionTestCycleA a;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * A reflection test fixture.
     */
    static class SimpleReflectionTestFixture {
        Object o;

        SimpleReflectionTestFixture() {
        }

        SimpleReflectionTestFixture(final Object o) {
            this.o = o;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private static class SelfInstanceVarReflectionTestFixture {
        @SuppressWarnings("unused")
        private final ToStringBuilderTest.SelfInstanceVarReflectionTestFixture typeIsSelf;

        SelfInstanceVarReflectionTestFixture() {
            this.typeIsSelf = this;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private static class SelfInstanceTwoVarsReflectionTestFixture {
        @SuppressWarnings("unused")
        private final ToStringBuilderTest.SelfInstanceTwoVarsReflectionTestFixture typeIsSelf;

        private final String otherType = "The Other Type";

        SelfInstanceTwoVarsReflectionTestFixture() {
            this.typeIsSelf = this;
        }

        public String getOtherType() {
            return this.otherType;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * Test an Object pointing to itself, the simplest test.
     */
    @Test
    public void testSimpleReflectionObjectCycle() {
        final ToStringBuilderTest.SimpleReflectionTestFixture simple = new ToStringBuilderTest.SimpleReflectionTestFixture();
        simple.o = simple;
        Assertions.assertEquals(((((this.toBaseString(simple)) + "[o=") + (this.toBaseString(simple))) + "]"), simple.toString());
    }

    /**
     * Test a class that defines an ivar pointing to itself.
     */
    @Test
    public void testSelfInstanceVarReflectionObjectCycle() {
        final ToStringBuilderTest.SelfInstanceVarReflectionTestFixture test = new ToStringBuilderTest.SelfInstanceVarReflectionTestFixture();
        Assertions.assertEquals(((((this.toBaseString(test)) + "[typeIsSelf=") + (this.toBaseString(test))) + "]"), test.toString());
    }

    /**
     * Test a class that defines an ivar pointing to itself.  This test was
     * created to show that handling cyclical object resulted in a missing endFieldSeparator call.
     */
    @Test
    public void testSelfInstanceTwoVarsReflectionObjectCycle() {
        final ToStringBuilderTest.SelfInstanceTwoVarsReflectionTestFixture test = new ToStringBuilderTest.SelfInstanceTwoVarsReflectionTestFixture();
        Assertions.assertEquals(((((((this.toBaseString(test)) + "[typeIsSelf=") + (this.toBaseString(test))) + ",otherType=") + (test.getOtherType().toString())) + "]"), test.toString());
    }

    /**
     * Test Objects pointing to each other.
     */
    @Test
    public void testReflectionObjectCycle() {
        final ToStringBuilderTest.ReflectionTestCycleA a = new ToStringBuilderTest.ReflectionTestCycleA();
        final ToStringBuilderTest.ReflectionTestCycleB b = new ToStringBuilderTest.ReflectionTestCycleB();
        a.b = b;
        b.a = a;
        Assertions.assertEquals(((((((this.toBaseString(a)) + "[b=") + (this.toBaseString(b))) + "[a=") + (this.toBaseString(a))) + "]]"), a.toString());
    }

    /**
     * Test a nasty combination of arrays and Objects pointing to each other.
     * objects[0] -&gt; SimpleReflectionTestFixture[ o -&gt; objects ]
     */
    @Test
    public void testReflectionArrayAndObjectCycle() {
        final Object[] objects = new Object[1];
        final ToStringBuilderTest.SimpleReflectionTestFixture simple = new ToStringBuilderTest.SimpleReflectionTestFixture(objects);
        objects[0] = simple;
        Assertions.assertEquals((((((((this.toBaseString(objects)) + "[{") + (this.toBaseString(simple))) + "[o=") + (this.toBaseString(objects))) + "]") + "}]"), ToStringBuilder.reflectionToString(objects));
        Assertions.assertEquals(((((this.toBaseString(simple)) + "[o={") + (this.toBaseString(simple))) + "}]"), ToStringBuilder.reflectionToString(simple));
    }

    // End: Reflection cycle tests
    @Test
    public void testAppendSuper() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[<null>,a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
    }

    @Test
    public void testAppendToString() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).appendToString("Integer@8888[]").toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).appendToString("Integer@8888[<null>]").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendToString("Integer@8888[]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[<null>,a=hello]"), new ToStringBuilder(base).appendToString("Integer@8888[<null>]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendToString(null).append("a", "hello").toString());
    }

    @Test
    public void testAppendAsObjectToString() {
        final String objectToAppend1 = "";
        final Boolean objectToAppend2 = Boolean.TRUE;
        final Object objectToAppend3 = new Object();
        Assertions.assertEquals(((((baseStr) + "[") + (toBaseString(objectToAppend1))) + "]"), new ToStringBuilder(base).appendAsObjectToString(objectToAppend1).toString());
        Assertions.assertEquals(((((baseStr) + "[") + (toBaseString(objectToAppend2))) + "]"), new ToStringBuilder(base).appendAsObjectToString(objectToAppend2).toString());
        Assertions.assertEquals(((((baseStr) + "[") + (toBaseString(objectToAppend3))) + "]"), new ToStringBuilder(base).appendAsObjectToString(objectToAppend3).toString());
    }

    @Test
    public void testAppendBooleanArrayWithFieldName() {
        final boolean[] array = new boolean[]{ true, false, false };
        Assertions.assertEquals(((baseStr) + "[flags={true,false,false}]"), new ToStringBuilder(base).append("flags", array).toString());
        Assertions.assertEquals(((baseStr) + "[flags=<null>]"), new ToStringBuilder(base).append("flags", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{true,false,false}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendBooleanArrayWithFieldNameAndFullDetatil() {
        final boolean[] array = new boolean[]{ true, false, false };
        Assertions.assertEquals(((baseStr) + "[flags={true,false,false}]"), new ToStringBuilder(base).append("flags", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=3>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[flags=<null>]"), new ToStringBuilder(base).append("flags", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=3>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendCharArrayWithFieldName() {
        final char[] array = new char[]{ 'A', '2', '_', 'D' };
        Assertions.assertEquals(((baseStr) + "[chars={A,2,_,D}]"), new ToStringBuilder(base).append("chars", array).toString());
        Assertions.assertEquals(((baseStr) + "[letters={A,2,_,D}]"), new ToStringBuilder(base).append("letters", array).toString());
        Assertions.assertEquals(((baseStr) + "[flags=<null>]"), new ToStringBuilder(base).append("flags", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{A,2,_,D}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendCharArrayWithFieldNameAndFullDetatil() {
        final char[] array = new char[]{ 'A', '2', '_', 'D' };
        Assertions.assertEquals(((baseStr) + "[chars={A,2,_,D}]"), new ToStringBuilder(base).append("chars", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[letters=<size=4>]"), new ToStringBuilder(base).append("letters", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[flags=<null>]"), new ToStringBuilder(base).append("flags", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendDoubleArrayWithFieldName() {
        final double[] array = new double[]{ 1.0, 2.9876, -3.00001, 4.3 };
        Assertions.assertEquals(((baseStr) + "[values={1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendDoubleArrayWithFieldNameAndFullDetatil() {
        final double[] array = new double[]{ 1.0, 2.9876, -3.00001, 4.3 };
        Assertions.assertEquals(((baseStr) + "[values={1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendObjectArrayWithFieldName() {
        final Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals(((baseStr) + "[values={<null>,5,{3,6}}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{<null>,5,{3,6}}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendObjectArrayWithFieldNameAndFullDetatil() {
        final Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals(((baseStr) + "[values={<null>,5,{3,6}}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=3>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=3>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendLongArrayWithFieldName() {
        final long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendLongArrayWithFieldNameAndFullDetatil() {
        final long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendIntArrayWithFieldName() {
        final int[] array = new int[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendIntArrayWithFieldNameAndFullDetatil() {
        final int[] array = new int[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendShortArrayWithFieldName() {
        final short[] array = new short[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendShortArrayWithFieldNameAndFullDetatil() {
        final short[] array = new short[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendByteArrayWithFieldName() {
        final byte[] array = new byte[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendByteArrayWithFieldNameAndFullDetatil() {
        final byte[] array = new byte[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[values={1,2,-3,4}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testAppendFloatArrayWithFieldName() {
        final float[] array = new float[]{ 1.0F, 2.9876F, -3.00001F, 4.3F };
        Assertions.assertEquals(((baseStr) + "[values={1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append("values", array).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(null, array).toString());
    }

    @Test
    public void testAppendFloatArrayWithFieldNameAndFullDetatil() {
        final float[] array = new float[]{ 1.0F, 2.9876F, -3.00001F, 4.3F };
        Assertions.assertEquals(((baseStr) + "[values={1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append("values", array, true).toString());
        Assertions.assertEquals(((baseStr) + "[length=<size=4>]"), new ToStringBuilder(base).append("length", array, false).toString());
        Assertions.assertEquals(((baseStr) + "[values=<null>]"), new ToStringBuilder(base).append("values", ((boolean[]) (null)), true).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(null, ((boolean[]) (null)), false).toString());
        Assertions.assertEquals(((baseStr) + "[<size=4>]"), new ToStringBuilder(base).append(null, array, false).toString());
    }

    @Test
    public void testConstructToStringBuilder() {
        final ToStringBuilder stringBuilder1 = new ToStringBuilder(base, null, null);
        final ToStringBuilder stringBuilder2 = new ToStringBuilder(base, ToStringStyle.DEFAULT_STYLE, new StringBuffer(1024));
        Assertions.assertEquals(ToStringStyle.DEFAULT_STYLE, stringBuilder1.getStyle());
        Assertions.assertNotNull(stringBuilder1.getStringBuffer());
        Assertions.assertNotNull(stringBuilder1.toString());
        Assertions.assertEquals(ToStringStyle.DEFAULT_STYLE, stringBuilder2.getStyle());
        Assertions.assertNotNull(stringBuilder2.getStringBuffer());
        Assertions.assertNotNull(stringBuilder2.toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=<null>]"), new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertEquals(((baseStr) + "[a=<Integer>]"), new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", new ArrayList<>(), false).toString());
        Assertions.assertEquals(((baseStr) + "[a=[]]"), new ToStringBuilder(base).append("a", new ArrayList<>(), true).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
    }

    @Test
    public void testObjectBuild() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (null))).build());
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(i3).build());
        Assertions.assertEquals(((baseStr) + "[a=<null>]"), new ToStringBuilder(base).append("a", ((Object) (null))).build());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", i3).build());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", i3).append("b", i4).build());
        Assertions.assertEquals(((baseStr) + "[a=<Integer>]"), new ToStringBuilder(base).append("a", i3, false).build());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", new ArrayList<>(), false).build());
        Assertions.assertEquals(((baseStr) + "[a=[]]"), new ToStringBuilder(base).append("a", new ArrayList<>(), true).build());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", new HashMap<>(), false).build());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", new HashMap<>(), true).build());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).build());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).build());
    }

    @Test
    public void testLong() {
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(3L).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", 3L).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", 3L).append("b", 4L).toString());
    }

    // cast is not really needed, keep for consistency
    @SuppressWarnings("cast")
    @Test
    public void testInt() {
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(3).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", 3).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", 3).append("b", 4).toString());
    }

    @Test
    public void testShort() {
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(((short) (3))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", ((short) (3))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", ((short) (3))).append("b", ((short) (4))).toString());
    }

    @Test
    public void testChar() {
        Assertions.assertEquals(((baseStr) + "[A]"), new ToStringBuilder(base).append(((char) (65))).toString());
        Assertions.assertEquals(((baseStr) + "[a=A]"), new ToStringBuilder(base).append("a", ((char) (65))).toString());
        Assertions.assertEquals(((baseStr) + "[a=A,b=B]"), new ToStringBuilder(base).append("a", ((char) (65))).append("b", ((char) (66))).toString());
    }

    @Test
    public void testByte() {
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(((byte) (3))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", ((byte) (3))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", ((byte) (3))).append("b", ((byte) (4))).toString());
    }

    @SuppressWarnings("cast")
    @Test
    public void testDouble() {
        Assertions.assertEquals(((baseStr) + "[3.2]"), new ToStringBuilder(base).append(3.2).toString());
        Assertions.assertEquals(((baseStr) + "[a=3.2]"), new ToStringBuilder(base).append("a", 3.2).toString());
        Assertions.assertEquals(((baseStr) + "[a=3.2,b=4.3]"), new ToStringBuilder(base).append("a", 3.2).append("b", 4.3).toString());
    }

    @Test
    public void testFloat() {
        Assertions.assertEquals(((baseStr) + "[3.2]"), new ToStringBuilder(base).append(((float) (3.2))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3.2]"), new ToStringBuilder(base).append("a", ((float) (3.2))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3.2,b=4.3]"), new ToStringBuilder(base).append("a", ((float) (3.2))).append("b", ((float) (4.3))).toString());
    }

    @Test
    public void testBoolean() {
        Assertions.assertEquals(((baseStr) + "[true]"), new ToStringBuilder(base).append(true).toString());
        Assertions.assertEquals(((baseStr) + "[a=true]"), new ToStringBuilder(base).append("a", true).toString());
        Assertions.assertEquals(((baseStr) + "[a=true,b=false]"), new ToStringBuilder(base).append("a", true).append("b", false).toString());
    }

    @Test
    public void testObjectArray() {
        Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals(((baseStr) + "[{<null>,5,{3,6}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{<null>,5,{3,6}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArray() {
        long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testIntArray() {
        int[] array = new int[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testShortArray() {
        short[] array = new short[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testByteArray() {
        byte[] array = new byte[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1,2,-3,4}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testCharArray() {
        char[] array = new char[]{ 'A', '2', '_', 'D' };
        Assertions.assertEquals(((baseStr) + "[{A,2,_,D}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{A,2,_,D}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testDoubleArray() {
        double[] array = new double[]{ 1.0, 2.9876, -3.00001, 4.3 };
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testFloatArray() {
        float[] array = new float[]{ 1.0F, 2.9876F, -3.00001F, 4.3F };
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{1.0,2.9876,-3.00001,4.3}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testBooleanArray() {
        boolean[] array = new boolean[]{ true, false, false };
        Assertions.assertEquals(((baseStr) + "[{true,false,false}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{true,false,false}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testIntArrayArray() {
        int[][] array = new int[][]{ new int[]{ 1, 2 }, null, new int[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testShortArrayArray() {
        short[][] array = new short[][]{ new short[]{ 1, 2 }, null, new short[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testByteArrayArray() {
        byte[][] array = new byte[][]{ new byte[]{ 1, 2 }, null, new byte[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testCharArrayArray() {
        char[][] array = new char[][]{ new char[]{ 'A', 'B' }, null, new char[]{ 'p' } };
        Assertions.assertEquals(((baseStr) + "[{{A,B},<null>,{p}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{A,B},<null>,{p}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testDoubleArrayArray() {
        double[][] array = new double[][]{ new double[]{ 1.0, 2.29686 }, null, new double[]{ Double.NaN } };
        Assertions.assertEquals(((baseStr) + "[{{1.0,2.29686},<null>,{NaN}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1.0,2.29686},<null>,{NaN}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testFloatArrayArray() {
        float[][] array = new float[][]{ new float[]{ 1.0F, 2.29686F }, null, new float[]{ Float.NaN } };
        Assertions.assertEquals(((baseStr) + "[{{1.0,2.29686},<null>,{NaN}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1.0,2.29686},<null>,{NaN}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testBooleanArrayArray() {
        boolean[][] array = new boolean[][]{ new boolean[]{ true, false }, null, new boolean[]{ false } };
        Assertions.assertEquals(((baseStr) + "[{{true,false},<null>,{false}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{true,false},<null>,{false}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testObjectCycle() {
        final ToStringBuilderTest.ObjectCycle a = new ToStringBuilderTest.ObjectCycle();
        final ToStringBuilderTest.ObjectCycle b = new ToStringBuilderTest.ObjectCycle();
        a.obj = b;
        b.obj = a;
        final String expected = (((((toBaseString(a)) + "[") + (toBaseString(b))) + "[") + (toBaseString(a))) + "]]";
        Assertions.assertEquals(expected, a.toString());
    }

    static class ObjectCycle {
        Object obj;

        @Override
        public String toString() {
            return new ToStringBuilder(this).append(obj).toString();
        }
    }

    @Test
    public void testSimpleReflectionStatics() {
        final ToStringBuilderTest.SimpleReflectionStaticFieldsFixture instance1 = new ToStringBuilderTest.SimpleReflectionStaticFieldsFixture();
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345]"), ReflectionToStringBuilder.toString(instance1, null, false, true, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345]"), ReflectionToStringBuilder.toString(instance1, null, true, true, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
    }

    /**
     * Tests ReflectionToStringBuilder.toString() for statics.
     */
    @Test
    public void testReflectionStatics() {
        final ToStringBuilderTest.ReflectionStaticFieldsFixture instance1 = new ToStringBuilderTest.ReflectionStaticFieldsFixture();
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345,instanceString=instanceString,instanceInt=67890]"), ReflectionToStringBuilder.toString(instance1, null, false, true, ToStringBuilderTest.ReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345,staticTransientString=staticTransientString,staticTransientInt=54321,instanceString=instanceString,instanceInt=67890,transientString=transientString,transientInt=98765]"), ReflectionToStringBuilder.toString(instance1, null, true, true, ToStringBuilderTest.ReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345,instanceString=instanceString,instanceInt=67890]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.ReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString=staticString,staticInt=12345,instanceString=instanceString,instanceInt=67890]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.ReflectionStaticFieldsFixture.class));
    }

    /**
     * Tests ReflectionToStringBuilder.toString() for statics.
     */
    @Test
    public void testInheritedReflectionStatics() {
        final ToStringBuilderTest.InheritedReflectionStaticFieldsFixture instance1 = new ToStringBuilderTest.InheritedReflectionStaticFieldsFixture();
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString2=staticString2,staticInt2=67890]"), ReflectionToStringBuilder.toString(instance1, null, false, true, ToStringBuilderTest.InheritedReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString2=staticString2,staticInt2=67890,staticString=staticString,staticInt=12345]"), ReflectionToStringBuilder.toString(instance1, null, false, true, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString2=staticString2,staticInt2=67890,staticString=staticString,staticInt=12345]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
        Assertions.assertEquals(((this.toBaseString(instance1)) + "[staticString2=staticString2,staticInt2=67890,staticString=staticString,staticInt=12345]"), this.toStringWithStatics(instance1, null, ToStringBuilderTest.SimpleReflectionStaticFieldsFixture.class));
    }

    /**
     * Tests ReflectionToStringBuilder setUpToClass().
     */
    @Test
    public void test_setUpToClass_valid() {
        final Integer val = Integer.valueOf(5);
        final ReflectionToStringBuilder test = new ReflectionToStringBuilder(val);
        test.setUpToClass(Number.class);
        test.toString();
    }

    /**
     * Tests ReflectionToStringBuilder setUpToClass().
     */
    @Test
    public void test_setUpToClass_invalid() {
        final Integer val = Integer.valueOf(5);
        final ReflectionToStringBuilder test = new ReflectionToStringBuilder(val);
        Assertions.assertThrows(IllegalArgumentException.class, () -> test.setUpToClass(String.class));
        test.toString();
    }

    /**
     * Tests ReflectionToStringBuilder.toString() for statics.
     */
    class ReflectionStaticFieldsFixture {
        static final String staticString = "staticString";

        static final int staticInt = 12345;

        static final transient String staticTransientString = "staticTransientString";

        static final transient int staticTransientInt = 54321;

        String instanceString = "instanceString";

        int instanceInt = 67890;

        transient String transientString = "transientString";

        transient int transientInt = 98765;
    }

    /**
     * Test fixture for ReflectionToStringBuilder.toString() for statics.
     */
    class SimpleReflectionStaticFieldsFixture {
        static final String staticString = "staticString";

        static final int staticInt = 12345;
    }

    /**
     * Test fixture for ReflectionToStringBuilder.toString() for statics.
     */
    @SuppressWarnings("unused")
    class InheritedReflectionStaticFieldsFixture extends ToStringBuilderTest.SimpleReflectionStaticFieldsFixture {
        static final String staticString2 = "staticString2";

        static final int staticInt2 = 67890;
    }

    @Test
    public void testReflectionNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ReflectionToStringBuilder.toString(null));
    }

    /**
     * Points out failure to print anything from appendToString methods using MULTI_LINE_STYLE.
     * See issue LANG-372.
     */
    class MultiLineTestObject {
        Integer i = Integer.valueOf(31337);

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("testInt", i).toString();
        }
    }

    @Test
    public void testAppendToStringUsingMultiLineStyle() {
        final ToStringBuilderTest.MultiLineTestObject obj = new ToStringBuilderTest.MultiLineTestObject();
        final ToStringBuilder testBuilder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).appendToString(obj.toString());
        Assertions.assertEquals((-1), testBuilder.toString().indexOf("testInt=31337"));
    }
}

