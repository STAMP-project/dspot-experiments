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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.flink.util.StringUtils;
import org.junit.Test;


/**
 * A test for the {@link AvroSerializer}.
 */
public abstract class AbstractGenericTypeSerializerTest {
    private final Random rnd = new Random(349712539451944123L);

    @Test
    public void testString() {
        runTests("abc", "", StringUtils.getRandomString(new Random(289347567856686223L), 10, 100), StringUtils.getRandomString(new Random(289347567856686223L), 1000, 5000), StringUtils.getRandomString(new Random(289347567856686223L), 30000, 35000), StringUtils.getRandomString(new Random(289347567856686223L), (100 * 1024), (105 * 1024)));
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
        runTests(a, b, c, d, e, f, g);
    }

    @Test
    public void testCompositeObject() {
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(5626435);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(76923);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1((-1100));
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(0);
        AbstractGenericTypeSerializerTest.ComplexNestedObject1 o5 = new AbstractGenericTypeSerializerTest.ComplexNestedObject1(44);
        runTests(o1, o2, o3, o4, o5);
    }

    @Test
    public void testNestedObjects() {
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o1 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o2 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2();
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o3 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        AbstractGenericTypeSerializerTest.ComplexNestedObject2 o4 = new AbstractGenericTypeSerializerTest.ComplexNestedObject2(rnd);
        runTests(o1, o2, o3, o4);
    }

    @Test
    public void testBeanStyleObjects() {
        {
            AbstractGenericTypeSerializerTest.Book b1 = new AbstractGenericTypeSerializerTest.Book(976243875L, "The Serialization Odyssey", 42);
            AbstractGenericTypeSerializerTest.Book b2 = new AbstractGenericTypeSerializerTest.Book(0L, "Debugging byte streams", 1337);
            AbstractGenericTypeSerializerTest.Book b3 = new AbstractGenericTypeSerializerTest.Book((-1L), "Low level interfaces", 12648430);
            runTests(b1, b2, b3);
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
            runTests(b1, b2);
        }
    }

    @Test
    public void testNestedInterfaces() {
        {
            AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces s1 = new AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces("Hello", 1);
            AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces s2 = new AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces("Ciao", 2);
            AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces s3 = new AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces("Foo", 3);
            runTests(s1, s2, s3);
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
            runTests(b1, b2);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Test Objects
    // --------------------------------------------------------------------------------------------
    public static final class SimpleWithNestedInterfaces {
        Map<String, Integer> map1;

        List<String> list;

        public SimpleWithNestedInterfaces() {
            map1 = new HashMap<String, Integer>();
            list = new ArrayList<String>();
        }

        public SimpleWithNestedInterfaces(String str, Integer i) {
            map1 = new HashMap<String, Integer>();
            map1.put(str, i);
            list = new ArrayList<String>();
            list.add(str);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces that = ((AbstractGenericTypeSerializerTest.SimpleWithNestedInterfaces) (o));
            if (!(list.equals(that.list))) {
                return false;
            }
            if (!(map1.equals(that.map1))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = map1.hashCode();
            result = (31 * result) + (list.hashCode());
            return result;
        }
    }

    public static final class SimpleTypes {
        private final int iVal;

        private final long lVal;

        private final byte bVal;

        private final String sVal;

        private final short rVal;

        private final double dVal;

        public SimpleTypes() {
            this(0, 0, ((byte) (0)), "", ((short) (0)), 0);
        }

        public SimpleTypes(int iVal, long lVal, byte bVal, String sVal, short rVal, double dVal) {
            this.iVal = iVal;
            this.lVal = lVal;
            this.bVal = bVal;
            this.sVal = sVal;
            this.rVal = rVal;
            this.dVal = dVal;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (AbstractGenericTypeSerializerTest.SimpleTypes.class)) {
                AbstractGenericTypeSerializerTest.SimpleTypes other = ((AbstractGenericTypeSerializerTest.SimpleTypes) (obj));
                return ((((((other.iVal) == (this.iVal)) && ((other.lVal) == (this.lVal))) && ((other.bVal) == (this.bVal))) && (other.sVal.equals(this.sVal))) && ((other.rVal) == (this.rVal))) && ((other.dVal) == (this.dVal));
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %d, %s, %d, %f)", iVal, lVal, bVal, sVal, rVal, dVal);
        }
    }

    public static class ComplexNestedObject1 {
        private double doubleValue;

        private List<String> stringList;

        public ComplexNestedObject1() {
        }

        public ComplexNestedObject1(int offInit) {
            this.doubleValue = 6293485.6723 + offInit;
            this.stringList = new ArrayList<String>();
            this.stringList.add(("A" + offInit));
            this.stringList.add(("somewhat" + offInit));
            this.stringList.add(("random" + offInit));
            this.stringList.add(("collection" + offInit));
            this.stringList.add(("of" + offInit));
            this.stringList.add(("strings" + offInit));
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (AbstractGenericTypeSerializerTest.ComplexNestedObject1.class)) {
                AbstractGenericTypeSerializerTest.ComplexNestedObject1 other = ((AbstractGenericTypeSerializerTest.ComplexNestedObject1) (obj));
                return ((other.doubleValue) == (this.doubleValue)) && (this.stringList.equals(other.stringList));
            } else {
                return false;
            }
        }
    }

    public static class ComplexNestedObject2 {
        private long longValue;

        private Map<String, AbstractGenericTypeSerializerTest.ComplexNestedObject1> theMap = new HashMap<String, AbstractGenericTypeSerializerTest.ComplexNestedObject1>();

        public ComplexNestedObject2() {
        }

        public ComplexNestedObject2(Random rnd) {
            this.longValue = rnd.nextLong();
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
            this.theMap.put(String.valueOf(rnd.nextLong()), new AbstractGenericTypeSerializerTest.ComplexNestedObject1(rnd.nextInt()));
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (AbstractGenericTypeSerializerTest.ComplexNestedObject2.class)) {
                AbstractGenericTypeSerializerTest.ComplexNestedObject2 other = ((AbstractGenericTypeSerializerTest.ComplexNestedObject2) (obj));
                return ((other.longValue) == (this.longValue)) && (this.theMap.equals(other.theMap));
            } else {
                return false;
            }
        }
    }

    public static class Book {
        private long bookId;

        private String title;

        private long authorId;

        public Book() {
        }

        public Book(long bookId, String title, long authorId) {
            this.bookId = bookId;
            this.title = title;
            this.authorId = authorId;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (AbstractGenericTypeSerializerTest.Book.class)) {
                AbstractGenericTypeSerializerTest.Book other = ((AbstractGenericTypeSerializerTest.Book) (obj));
                return (((other.bookId) == (this.bookId)) && ((other.authorId) == (this.authorId))) && (this.title.equals(other.title));
            } else {
                return false;
            }
        }
    }

    public static class BookAuthor {
        private long authorId;

        private List<String> bookTitles;

        private String authorName;

        public BookAuthor() {
        }

        public BookAuthor(long authorId, List<String> bookTitles, String authorName) {
            this.authorId = authorId;
            this.bookTitles = bookTitles;
            this.authorName = authorName;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (AbstractGenericTypeSerializerTest.BookAuthor.class)) {
                AbstractGenericTypeSerializerTest.BookAuthor other = ((AbstractGenericTypeSerializerTest.BookAuthor) (obj));
                return ((other.authorName.equals(this.authorName)) && ((other.authorId) == (this.authorId))) && (other.bookTitles.equals(this.bookTitles));
            } else {
                return false;
            }
        }
    }
}

