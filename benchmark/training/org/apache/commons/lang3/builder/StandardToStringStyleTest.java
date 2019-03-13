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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.builder.ToStringStyle}.
 */
public class StandardToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    private final String baseStr = "Integer";

    private static final StandardToStringStyle STYLE = new StandardToStringStyle();

    static {
        StandardToStringStyleTest.STYLE.setUseShortClassName(true);
        StandardToStringStyleTest.STYLE.setUseIdentityHashCode(false);
        StandardToStringStyleTest.STYLE.setArrayStart("[");
        StandardToStringStyleTest.STYLE.setArraySeparator(", ");
        StandardToStringStyleTest.STYLE.setArrayEnd("]");
        StandardToStringStyleTest.STYLE.setNullText("%NULL%");
        StandardToStringStyleTest.STYLE.setSizeStartText("%SIZE=");
        StandardToStringStyleTest.STYLE.setSizeEndText("%");
        StandardToStringStyleTest.STYLE.setSummaryObjectStartText("%");
        StandardToStringStyleTest.STYLE.setSummaryObjectEndText("%");
    }

    // ----------------------------------------------------------------
    @Test
    public void testBlank() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).toString());
    }

    @Test
    public void testAppendSuper() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").toString());
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).appendSuper("Integer@8888[%NULL%]").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[%NULL%,a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[%NULL%]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=%NULL%]"), new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertEquals(((baseStr) + "[a=%Integer%]"), new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertEquals(((baseStr) + "[a=%SIZE=0%]"), new ToStringBuilder(base).append("a", new ArrayList<>(), false).toString());
        Assertions.assertEquals(((baseStr) + "[a=[]]"), new ToStringBuilder(base).append("a", new ArrayList<>(), true).toString());
        Assertions.assertEquals(((baseStr) + "[a=%SIZE=0%]"), new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertEquals(((baseStr) + "[a=%SIZE=0%]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals(((baseStr) + "[a=[]]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
    }

    @Test
    public void testPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Suzy Queue";
        p.age = 19;
        p.smoker = false;
        final String pBaseStr = "ToStringStyleTest.Person";
        Assertions.assertEquals((pBaseStr + "[name=Suzy Queue,age=19,smoker=false]"), new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString());
    }

    @Test
    public void testLong() {
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(3L).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", 3L).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", 3L).append("b", 4L).toString());
    }

    @Test
    public void testObjectArray() {
        Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals(((baseStr) + "[[%NULL%, 5, [3, 6]]]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[[%NULL%, 5, [3, 6]]]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArray() {
        long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((baseStr) + "[[1, 2, -3, 4]]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[[1, 2, -3, 4]]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[[[1, 2], %NULL%, [5]]]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[[[1, 2], %NULL%, [5]]]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[%NULL%]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testDefaultValueOfUseClassName() {
        Assertions.assertTrue(new StandardToStringStyle().isUseClassName());
    }

    @Test
    public void testDefaultValueOfUseFieldNames() {
        Assertions.assertTrue(new StandardToStringStyle().isUseFieldNames());
    }

    @Test
    public void testDefaultValueOfUseShortClassName() {
        Assertions.assertFalse(new StandardToStringStyle().isUseShortClassName());
    }

    @Test
    public void testDefaultValueOfUseIdentityHashCode() {
        Assertions.assertTrue(new StandardToStringStyle().isUseIdentityHashCode());
    }
}

