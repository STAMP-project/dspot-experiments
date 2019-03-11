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
import org.apache.flink.api.java.tuple.Tuple0;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.util.StringUtils;
import org.junit.Test;


public class TupleSerializerTest {
    @Test
    public void testTuple0() {
        Tuple0[] testTuples = new Tuple0[]{ Tuple0.INSTANCE, Tuple0.INSTANCE, Tuple0.INSTANCE };
        runTests(1, testTuples);
    }

    @Test
    public void testTuple1Int() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Tuple1<Integer>[] testTuples = new Tuple1[]{ new Tuple1<Integer>(42), new Tuple1<Integer>(1), new Tuple1<Integer>(0), new Tuple1<Integer>((-1)), new Tuple1<Integer>(Integer.MAX_VALUE), new Tuple1<Integer>(Integer.MIN_VALUE) };
        runTests(4, testTuples);
    }

    @Test
    public void testTuple1String() {
        Random rnd = new Random(68761564135413L);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Tuple1<String>[] testTuples = new Tuple1[]{ new Tuple1<String>(StringUtils.getRandomString(rnd, 10, 100)), new Tuple1<String>("abc"), new Tuple1<String>(""), new Tuple1<String>(StringUtils.getRandomString(rnd, 30, 170)), new Tuple1<String>(StringUtils.getRandomString(rnd, 15, 50)), new Tuple1<String>("") };
        runTests((-1), testTuples);
    }

    @Test
    public void testTuple1StringArray() {
        Random rnd = new Random(289347567856686223L);
        String[] arr1 = new String[]{ "abc", "", StringUtils.getRandomString(rnd, 10, 100), StringUtils.getRandomString(rnd, 15, 50), StringUtils.getRandomString(rnd, 30, 170), StringUtils.getRandomString(rnd, 14, 15), "" };
        String[] arr2 = new String[]{ "foo", "", StringUtils.getRandomString(rnd, 10, 100), StringUtils.getRandomString(rnd, 1000, 5000), StringUtils.getRandomString(rnd, 30000, 35000), StringUtils.getRandomString(rnd, (100 * 1024), (105 * 1024)), "bar" };
        @SuppressWarnings("unchecked")
        Tuple1<String[]>[] testTuples = new Tuple1[]{ new Tuple1<String[]>(arr1), new Tuple1<String[]>(arr2) };
        runTests((-1), testTuples);
    }

    @Test
    public void testTuple2StringDouble() {
        Random rnd = new Random(807346528946L);
        @SuppressWarnings("unchecked")
        Tuple2<String, Double>[] testTuples = new Tuple2[]{ new Tuple2<String, Double>(StringUtils.getRandomString(rnd, 10, 100), rnd.nextDouble()), new Tuple2<String, Double>(StringUtils.getRandomString(rnd, 10, 100), rnd.nextDouble()), new Tuple2<String, Double>(StringUtils.getRandomString(rnd, 10, 100), rnd.nextDouble()), new Tuple2<String, Double>("", rnd.nextDouble()), new Tuple2<String, Double>(StringUtils.getRandomString(rnd, 10, 100), rnd.nextDouble()), new Tuple2<String, Double>(StringUtils.getRandomString(rnd, 10, 100), rnd.nextDouble()) };
        runTests((-1), testTuples);
    }

    @Test
    public void testTuple2StringStringArray() {
        Random rnd = new Random(289347567856686223L);
        String[] arr1 = new String[]{ "abc", "", StringUtils.getRandomString(rnd, 10, 100), StringUtils.getRandomString(rnd, 15, 50), StringUtils.getRandomString(rnd, 30, 170), StringUtils.getRandomString(rnd, 14, 15), "" };
        String[] arr2 = new String[]{ "foo", "", StringUtils.getRandomString(rnd, 10, 100), StringUtils.getRandomString(rnd, 1000, 5000), StringUtils.getRandomString(rnd, 30000, 35000), StringUtils.getRandomString(rnd, (100 * 1024), (105 * 1024)), "bar" };
        @SuppressWarnings("unchecked")
        Tuple2<String, String[]>[] testTuples = new Tuple2[]{ new Tuple2<String, String[]>(StringUtils.getRandomString(rnd, 30, 170), arr1), new Tuple2<String, String[]>(StringUtils.getRandomString(rnd, 30, 170), arr2), new Tuple2<String, String[]>(StringUtils.getRandomString(rnd, 30, 170), arr1), new Tuple2<String, String[]>(StringUtils.getRandomString(rnd, 30, 170), arr2), new Tuple2<String, String[]>(StringUtils.getRandomString(rnd, 30, 170), arr2) };
        runTests((-1), testTuples);
    }

    @Test
    public void testTuple5CustomObjects() {
        Random rnd = new Random(807346528946L);
        AbstractGenericTypeSerializerTest.SimpleTypes a = new AbstractGenericTypeSerializerTest.SimpleTypes();
        AbstractGenericTypeSerializerTest.SimpleTypes b = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes c = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes d = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes e = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes f = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.SimpleTypes g = new AbstractGenericTypeSerializerTest.SimpleTypes(rnd.nextInt(), rnd.nextLong(), ((byte) (rnd.nextInt())), StringUtils.getRandomString(rnd, 10, 100), ((short) (rnd.nextInt())), rnd.nextDouble());
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(5626435);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(76923);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1((-1100));
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(0);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o5 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(44);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 co1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 co2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2();
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 co3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 co4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.Book b1 = new AbstractGenericTypeSerializerTest.Book(976243875L, "The Serialization Odysse", 42);
        AbstractGenericTypeSerializerTest.Book b2 = new AbstractGenericTypeSerializerTest.Book(0L, "Debugging byte streams", 1337);
        AbstractGenericTypeSerializerTest.Book b3 = new AbstractGenericTypeSerializerTest.Book((-1L), "Low level interfaces", 12648430);
        AbstractGenericTypeSerializerTest.Book b4 = new AbstractGenericTypeSerializerTest.Book(Long.MAX_VALUE, "The joy of bits and bytes", -559038737);
        AbstractGenericTypeSerializerTest.Book b5 = new AbstractGenericTypeSerializerTest.Book(Long.MIN_VALUE, "Winnign a prize for creative test strings", 12246784);
        AbstractGenericTypeSerializerTest.Book b6 = new AbstractGenericTypeSerializerTest.Book((-2L), "Distributed Systems", -6066930334832433271L);
        ArrayList<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        AbstractGenericTypeSerializerTest.BookAuthor ba1 = new AbstractGenericTypeSerializerTest.BookAuthor(976243875L, list, "Arno Nym");
        ArrayList<String> list2 = new ArrayList<String>();
        AbstractGenericTypeSerializerTest.BookAuthor ba2 = new AbstractGenericTypeSerializerTest.BookAuthor(987654321L, list2, "The Saurus");
        @SuppressWarnings("unchecked")
        Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>[] testTuples = new Tuple5[]{ new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(a, b1, o1, ba1, co1), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(b, b2, o2, ba2, co2), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(c, b3, o3, ba1, co3), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(d, b2, o4, ba1, co4), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(e, b4, o5, ba2, co4), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(f, b5, o1, ba2, co4), new Tuple5<AbstractGenericTypeSerializerTest.SimpleTypes, AbstractGenericTypeSerializerTest.Book, AbstractGenericTypeSerializerTest.ComplexNestedObject1, AbstractGenericTypeSerializerTest.BookAuthor, AbstractGenericTypeSerializerTest.ComplexNestedObject2>(g, b6, o4, ba1, co2) };
        runTests((-1), testTuples);
    }
}

