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
 * Unit tests {@link org.apache.commons.lang3.builder.MultiLineToStringStyleTest}.
 */
public class MultiLineToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    private final String baseStr = ((base.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(base)));

    // ----------------------------------------------------------------
    @Test
    public void testBlank() {
        Assertions.assertEquals(((((baseStr) + "[") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).toString());
    }

    @Test
    public void testAppendSuper() {
        Assertions.assertEquals(((((baseStr) + "[") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]")).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=hello") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).append("a", "hello").toString());
        Assertions.assertEquals(((((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "  a=hello") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]")).append("a", "hello").toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=hello") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  3") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=<null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=3") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals(((((((((baseStr) + "[") + (System.lineSeparator())) + "  a=3") + (System.lineSeparator())) + "  b=4") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=<Integer>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=<size=0>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", new ArrayList<>(), false).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=[]") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", new ArrayList<>(), true).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=<size=0>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a={}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=<size=0>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a={}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
    }

    @Test
    public void testPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        p.age = 25;
        p.smoker = true;
        final String pBaseStr = ((p.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(p)));
        Assertions.assertEquals((((((((((pBaseStr + "[") + (System.lineSeparator())) + "  name=Jane Doe") + (System.lineSeparator())) + "  age=25") + (System.lineSeparator())) + "  smoker=true") + (System.lineSeparator())) + "]"), new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString());
    }

    @Test
    public void testLong() {
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  3") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(3L).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  a=3") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", 3L).toString());
        Assertions.assertEquals(((((((((baseStr) + "[") + (System.lineSeparator())) + "  a=3") + (System.lineSeparator())) + "  b=4") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append("a", 3L).append("b", 4L).toString());
    }

    @Test
    public void testObjectArray() {
        Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {<null>,5,{3,6}}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {<null>,5,{3,6}}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArray() {
        long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {1,2,-3,4}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {1,2,-3,4}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {{1,2},<null>,{5}}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  {{1,2},<null>,{5}}") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((((((baseStr) + "[") + (System.lineSeparator())) + "  <null>") + (System.lineSeparator())) + "]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }
}

