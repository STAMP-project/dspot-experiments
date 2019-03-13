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
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.builder.JsonToStringStyleTest}.
 */
public class JsonToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    // ----------------------------------------------------------------
    @Test
    public void testNull() {
        Assertions.assertEquals("null", new ToStringBuilder(null).toString());
    }

    @Test
    public void testBlank() {
        Assertions.assertEquals("{}", new ToStringBuilder(base).toString());
    }

    @Test
    public void testAppendSuper() {
        Assertions.assertEquals("{}", new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).toString());
        Assertions.assertEquals("{}", new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).toString());
        Assertions.assertEquals("{\"a\":\"hello\"}", new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).append("a", "hello").toString());
        Assertions.assertEquals("{\"a\":\"hello\"}", new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).append("a", "hello").toString());
        Assertions.assertEquals("{\"a\":\"hello\"}", new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
        Assertions.assertEquals("{\"a\":\"hello\",\"b\":\"world\"}", new ToStringBuilder(base).appendSuper("{\"a\":\"hello\"}").append("b", "world").toString());
    }

    @Test
    public void testChar() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append('A').toString());
        Assertions.assertEquals("{\"a\":\"A\"}", new ToStringBuilder(base).append("a", 'A').toString());
        Assertions.assertEquals("{\"a\":\"A\",\"b\":\"B\"}", new ToStringBuilder(base).append("a", 'A').append("b", 'B').toString());
    }

    @Test
    public void testDate() {
        final Date now = new Date();
        final Date afterNow = new Date(((System.currentTimeMillis()) + 1));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append(now).toString());
        Assertions.assertEquals((("{\"now\":\"" + (now.toString())) + "\"}"), new ToStringBuilder(base).append("now", now).toString());
        Assertions.assertEquals((((("{\"now\":\"" + (now.toString())) + "\",\"after\":\"") + (afterNow.toString())) + "\"}"), new ToStringBuilder(base).append("now", now).append("after", afterNow).toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals("{\"a\":null}", new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals("{\"a\":3}", new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals("{\"a\":3,\"b\":4}", new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", new ArrayList<>(), false).toString());
        Assertions.assertEquals("{\"a\":[]}", new ToStringBuilder(base).append("a", new ArrayList<>(), true).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals("{\"a\":{}}", new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals("{\"a\":[]}", new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", ((Object) (new int[]{ 1, 2, 3 })), false).toString());
        Assertions.assertEquals("{\"a\":[1,2,3]}", new ToStringBuilder(base).append("a", ((Object) (new int[]{ 1, 2, 3 })), true).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append("a", ((Object) (new String[]{ "v", "x", "y", "z" })), false).toString());
        Assertions.assertEquals("{\"a\":[\"v\",\"x\",\"y\",\"z\"]}", new ToStringBuilder(base).append("a", ((Object) (new String[]{ "v", "x", "y", "z" })), true).toString());
    }

    @Test
    public void testPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        p.age = 25;
        p.smoker = true;
        Assertions.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}", new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString());
    }

    @Test
    public void testNestingPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("smoker", this.smoker).toString();
            }
        };
        p.name = "Jane Doe";
        p.age = 25;
        p.smoker = true;
        final JsonToStringStyleTest.NestingPerson nestP = new JsonToStringStyleTest.NestingPerson();
        nestP.pid = "#1@Jane";
        nestP.person = p;
        Assertions.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString());
    }

    @Test
    public void testLong() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new ToStringBuilder(base).append(3L).toString());
        Assertions.assertEquals("{\"a\":3}", new ToStringBuilder(base).append("a", 3L).toString());
        Assertions.assertEquals("{\"a\":3,\"b\":4}", new ToStringBuilder(base).append("a", 3L).append("b", 4L).toString());
    }

    @Test
    public void testObjectArray() {
        final Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"objectArray\":[null,5,[3,6]]}", toStringBuilder.append("objectArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testLongArray() {
        final long[] array = new long[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"longArray\":[1,2,-3,4]}", toStringBuilder.append("longArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testIntArray() {
        final int[] array = new int[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"intArray\":[1,2,-3,4]}", toStringBuilder.append("intArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testByteArray() {
        final byte[] array = new byte[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"byteArray\":[1,2,-3,4]}", toStringBuilder.append("byteArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testShortArray() {
        final short[] array = new short[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"shortArray\":[1,2,-3,4]}", toStringBuilder.append("shortArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testDoubleArray() {
        final double[] array = new double[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"doubleArray\":[1.0,2.0,-3.0,4.0]}", toStringBuilder.append("doubleArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testFloatArray() {
        final float[] array = new float[]{ 1, 2, -3, 4 };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"floatArray\":[1.0,2.0,-3.0,4.0]}", toStringBuilder.append("floatArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testCharArray() {
        final char[] array = new char[]{ '1', '2', '3', '4' };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"charArray\":[\"1\",\"2\",\"3\",\"4\"]}", toStringBuilder.append("charArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testBooleanArray() {
        final boolean[] array = new boolean[]{ true, false };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertEquals("{\"booleanArray\":[true,false]}", toStringBuilder.append("booleanArray", array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testLongArrayArray() {
        final long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        final ToStringBuilder toStringBuilder = new ToStringBuilder(base);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(array).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((long[][]) (null))).toString());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> toStringBuilder.append(((Object) (array))).toString());
    }

    @Test
    public void testArray() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        p.age = 25;
        p.smoker = true;
        Assertions.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString());
    }

    @Test
    public void testLANG1395() {
        Assertions.assertEquals("{\"name\":\"value\"}", new ToStringBuilder(base).append("name", "value").toString());
        Assertions.assertEquals("{\"name\":\"\"}", new ToStringBuilder(base).append("name", "").toString());
        Assertions.assertEquals("{\"name\":\"\\\"\"}", new ToStringBuilder(base).append("name", '"').toString());
        Assertions.assertEquals("{\"name\":\"\\\\\"}", new ToStringBuilder(base).append("name", '\\').toString());
        Assertions.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", new ToStringBuilder(base).append("name", "Let\'s \"quote\" this").toString());
    }

    @Test
    public void testLANG1396() {
        Assertions.assertEquals("{\"Let\'s \\\"quote\\\" this\":\"value\"}", new ToStringBuilder(base).append("Let\'s \"quote\" this", "value").toString());
    }

    /**
     * An object with nested object structures used to test {@link ToStringStyle.JsonToStringStyle}.
     */
    static class NestingPerson {
        /**
         * Test String field.
         */
        String pid;

        /**
         * Test nested object field.
         */
        ToStringStyleTest.Person person;
    }
}

