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
 * Unit tests {@link org.apache.commons.lang3.builder.ToStringStyle#SHORT_PREFIX_STYLE}.
 */
public class ShortPrefixToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    private final String baseStr = "Integer";

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
    public void testPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "John Q. Public";
        p.age = 45;
        p.smoker = true;
        final String pBaseStr = "ToStringStyleTest.Person";
        Assertions.assertEquals((pBaseStr + "[name=John Q. Public,age=45,smoker=true]"), new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString());
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
}

