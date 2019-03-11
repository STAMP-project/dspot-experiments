/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.typeutils.runtime;


import java.util.ArrayList;
import java.util.Random;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.util.StringUtils;
import org.junit.Test;


public abstract class AbstractGenericArraySerializerTest {
    private final Random rnd = new Random(349712539451944123L);

    @Test
    public void testString() {
        String[] arr1 = new String[]{ "abc", "", StringUtils.getRandomString(new Random(289347567856686223L), 10, 100), StringUtils.getRandomString(new Random(289347567856686223L), 15, 50), StringUtils.getRandomString(new Random(289347567856686223L), 30, 170), StringUtils.getRandomString(new Random(289347567856686223L), 14, 15), "" };
        String[] arr2 = new String[]{ "foo", "", StringUtils.getRandomString(new Random(289347567856686223L), 10, 100), StringUtils.getRandomString(new Random(289347567856686223L), 1000, 5000), StringUtils.getRandomString(new Random(289347567856686223L), 30000, 35000), StringUtils.getRandomString(new Random(289347567856686223L), (100 * 1024), (105 * 1024)), "bar" };
        // run tests with the string serializer as the component serializer
        runTests(String.class, new StringSerializer(), arr1, arr2);
        // run the tests with the generic serializer as the component serializer
        runTests(arr1, arr2);
    }

    @Test
    public void testSimpleTypesObjects() {
        AbstractGenericTypeSerializerTest.SimpleTypes a = new AbstractGenericTypeSerializerTest.SimpleTypes();
        AbstractGenericTypeSerializerTest.SimpleTypes b = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes c = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes d = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes e = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes f = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes g = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        runTests(new AbstractGenericTypeSerializerTest.SimpleTypes[]{ a, b, c }, new AbstractGenericTypeSerializerTest.SimpleTypes[]{ d, e, f, g });
    }

    @Test
    public void testCompositeObject() {
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(5626435);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(76923);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1((-1100));
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(0);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o5 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(44);
        runTests(new AbstractGenericTypeSerializerTest.ComplexNestedObject1[]{ o1, o2 }, new AbstractGenericTypeSerializerTest.ComplexNestedObject1[]{ o3 }, new AbstractGenericTypeSerializerTest.ComplexNestedObject1[]{ o4, o5 });
    }

    @Test
    public void testNestedObjects() {
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2();
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        runTests(new AbstractGenericTypeSerializerTest.ComplexNestedObject2[]{ o1, o2, o3 }, new AbstractGenericTypeSerializerTest.ComplexNestedObject2[]{  }, new AbstractGenericTypeSerializerTest.ComplexNestedObject2[]{  }, new AbstractGenericTypeSerializerTest.ComplexNestedObject2[]{ o4 }, new AbstractGenericTypeSerializerTest.ComplexNestedObject2[]{  });
    }

    @Test
    public void testBeanStyleObjects() {
        {
            AbstractGenericTypeSerializerTest.Book b1 = new AbstractGenericTypeSerializerTest.Book(976243875L, "The Serialization Odyssey", 42);
            AbstractGenericTypeSerializerTest.Book b2 = new AbstractGenericTypeSerializerTest.Book(0L, "Debugging byte streams", 1337);
            AbstractGenericTypeSerializerTest.Book b3 = new AbstractGenericTypeSerializerTest.Book((-1L), "Low level interfaces", 12648430);
            AbstractGenericTypeSerializerTest.Book b4 = new AbstractGenericTypeSerializerTest.Book(Long.MAX_VALUE, "The joy of bits and bytes", -559038737);
            AbstractGenericTypeSerializerTest.Book b5 = new AbstractGenericTypeSerializerTest.Book(Long.MIN_VALUE, "Winning a prize for creative test strings", 12246784);
            AbstractGenericTypeSerializerTest.Book b6 = new AbstractGenericTypeSerializerTest.Book((-2L), "Distributed Systems", -6066930334832433271L);
            runTests(new AbstractGenericTypeSerializerTest.Book[]{ b1, b2 }, new AbstractGenericTypeSerializerTest.Book[]{  }, new AbstractGenericTypeSerializerTest.Book[]{  }, new AbstractGenericTypeSerializerTest.Book[]{  }, new AbstractGenericTypeSerializerTest.Book[]{  }, new AbstractGenericTypeSerializerTest.Book[]{ b3, b4, b5, b6 });
        }
        // object with collection
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add("A");
            list.add("B");
            list.add("C");
            list.add("D");
            list.add("E");
            AbstractGenericTypeSerializerTest.BookAuthor b1 = new AbstractGenericTypeSerializerTest.BookAuthor(976243875L, list, "Arno Nym");
            ArrayList<String> list2 = new ArrayList<String>();
            AbstractGenericTypeSerializerTest.BookAuthor b2 = new AbstractGenericTypeSerializerTest.BookAuthor(987654321L, list2, "The Saurus");
            runTests(new AbstractGenericTypeSerializerTest.BookAuthor[]{ b1, b2 });
        }
    }
}

