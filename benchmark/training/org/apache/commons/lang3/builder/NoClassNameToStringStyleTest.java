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
 * Unit tests {@link ToStringStyle#NO_CLASS_NAME_STYLE}.
 */
public class NoClassNameToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    // ----------------------------------------------------------------
    @Test
    public void testBlank() {
        Assertions.assertEquals("[]", new ToStringBuilder(base).toString());
    }

    @Test
    public void testAppendSuper() {
        Assertions.assertEquals("[]", new ToStringBuilder(base).appendSuper("Integer@8888[]").toString());
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").toString());
        Assertions.assertEquals("[a=hello]", new ToStringBuilder(base).appendSuper("Integer@8888[]").append("a", "hello").toString());
        Assertions.assertEquals("[<null>,a=hello]", new ToStringBuilder(base).appendSuper("Integer@8888[<null>]").append("a", "hello").toString());
        Assertions.assertEquals("[a=hello]", new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString());
    }

    @Test
    public void testObject() {
        final Integer i3 = Integer.valueOf(3);
        final Integer i4 = Integer.valueOf(4);
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(((Object) (null))).toString());
        Assertions.assertEquals("[3]", new ToStringBuilder(base).append(i3).toString());
        Assertions.assertEquals("[a=<null>]", new ToStringBuilder(base).append("a", ((Object) (null))).toString());
        Assertions.assertEquals("[a=3]", new ToStringBuilder(base).append("a", i3).toString());
        Assertions.assertEquals("[a=3,b=4]", new ToStringBuilder(base).append("a", i3).append("b", i4).toString());
        Assertions.assertEquals("[a=<Integer>]", new ToStringBuilder(base).append("a", i3, false).toString());
        Assertions.assertEquals("[a=<size=0>]", new ToStringBuilder(base).append("a", new ArrayList<>(), false).toString());
        Assertions.assertEquals("[a=[]]", new ToStringBuilder(base).append("a", new ArrayList<>(), true).toString());
        Assertions.assertEquals("[a=<size=0>]", new ToStringBuilder(base).append("a", new HashMap<>(), false).toString());
        Assertions.assertEquals("[a={}]", new ToStringBuilder(base).append("a", new HashMap<>(), true).toString());
        Assertions.assertEquals("[a=<size=0>]", new ToStringBuilder(base).append("a", ((Object) (new String[0])), false).toString());
        Assertions.assertEquals("[a={}]", new ToStringBuilder(base).append("a", ((Object) (new String[0])), true).toString());
    }

    @Test
    public void testPerson() {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "John Q. Public";
        p.age = 45;
        p.smoker = true;
        Assertions.assertEquals("[name=John Q. Public,age=45,smoker=true]", new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString());
    }

    @Test
    public void testLong() {
        Assertions.assertEquals("[3]", new ToStringBuilder(base).append(3L).toString());
        Assertions.assertEquals("[a=3]", new ToStringBuilder(base).append("a", 3L).toString());
        Assertions.assertEquals("[a=3,b=4]", new ToStringBuilder(base).append("a", 3L).append("b", 4L).toString());
    }

    @Test
    public void testObjectArray() {
        Object[] array = new Object[]{ null, base, new int[]{ 3, 6 } };
        Assertions.assertEquals("[{<null>,5,{3,6}}]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[{<null>,5,{3,6}}]", new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArray() {
        long[] array = new long[]{ 1, 2, -3, 4 };
        Assertions.assertEquals("[{1,2,-3,4}]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[{1,2,-3,4}]", new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(((Object) (array))).toString());
    }

    @Test
    public void testLongArrayArray() {
        long[][] array = new long[][]{ new long[]{ 1, 2 }, null, new long[]{ 5 } };
        Assertions.assertEquals("[{{1,2},<null>,{5}}]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[{{1,2},<null>,{5}}]", new ToStringBuilder(base).append(((Object) (array))).toString());
        array = null;
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(array).toString());
        Assertions.assertEquals("[<null>]", new ToStringBuilder(base).append(((Object) (array))).toString());
    }
}

