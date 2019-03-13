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
 * Unit tests {@link org.apache.commons.lang3.builder.RecursiveToStringStyleTest}.
 */
public class RecursiveToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    private final String baseStr = ((base.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(base)));

    // ----------------------------------------------------------------
    @Test
    public void testBlank() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).toString());
    }

    @Test
    public void testAppendSuper() {
        Assertions.assertEquals(((baseStr) + "[]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[<null>,a=hello]"), new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").append("a", "hello").toString());
        Assertions.assertEquals(((baseStr) + "[a=hello]"), new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        final ArrayList<Object> emptyList = new ArrayList<>();
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[3]"), new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=<null>]"), new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals(((baseStr) + "[a=3]"), new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals(((baseStr) + "[a=3,b=4]"), new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertEquals(((baseStr) + "[a=<Integer>]"), new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", emptyList, false).toString());
        Assertions.assertEquals(((((baseStr) + "[a=java.util.ArrayList@") + (Integer.toHexString(System.identityHashCode(emptyList)))) + "{}]"), new ToStringBuilder(base).append("a", emptyList, true).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertEquals(((baseStr) + "[a=<size=0>]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals(((baseStr) + "[a={}]"), new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
    }

    @Test
    public void testPerson() {
        final RecursiveToStringStyleTest.Person p = new RecursiveToStringStyleTest.Person();
        p.name = "John Doe";
        p.age = 33;
        p.smoker = false;
        p.job = new RecursiveToStringStyleTest.Job();
        p.job.title = "Manager";
        final String pBaseStr = ((p.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(p)));
        final String pJobStr = ((p.job.getClass().getName()) + "@") + (Integer.toHexString(System.identityHashCode(p.job)));
        Assertions.assertEquals((((pBaseStr + "[name=John Doe,age=33,smoker=false,job=") + pJobStr) + "[title=Manager]]"), new ReflectionToStringBuilder(p, new RecursiveToStringStyle()).toString());
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
    public void testLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[{{1,2},<null>,{5}}]"), new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals(((baseStr) + "[<null>]"), new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    static class Person {
        /**
         * Test String field.
         */
        String name;

        /**
         * Test integer field.
         */
        int age;

        /**
         * Test boolean field.
         */
        boolean smoker;

        /**
         * Test Object field.
         */
        RecursiveToStringStyleTest.Job job;
    }

    static class Job {
        /**
         * Test String field.
         */
        String title;
    }
}

