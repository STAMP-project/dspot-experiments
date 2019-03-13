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


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.SerializerTestInstance;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.junit.Test;


/**
 * A serialization test for multidimensional arrays.
 */
public class MultidimensionalArraySerializerTest {
    @Test
    public void testStringArray() {
        String[][] array = new String[][]{ new String[]{ null, "b" }, new String[]{ "c", "d" }, new String[]{ "e", "f" }, new String[]{ "g", "h" }, null };
        TypeInformation<String[][]> ti = TypeExtractor.getForClass(String[][].class);
        SerializerTestInstance<String[][]> testInstance = new SerializerTestInstance<String[][]>(ti.createSerializer(new ExecutionConfig()), String[][].class, (-1), array);
        testInstance.testAll();
    }

    @Test
    public void testPrimitiveArray() {
        int[][] array = new int[][]{ new int[]{ 12, 1 }, new int[]{ 48, 42 }, new int[]{ 23, 80 }, new int[]{ 484, 849 }, new int[]{ 987, 4 } };
        TypeInformation<int[][]> ti = TypeExtractor.getForClass(int[][].class);
        SerializerTestInstance<int[][]> testInstance = new SerializerTestInstance<int[][]>(ti.createSerializer(new ExecutionConfig()), int[][].class, (-1), array);
        testInstance.testAll();
    }

    public static class MyPojo {
        public String field1;

        public int field2;

        public MyPojo(String field1, int field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArraySerializerTest.MyPojo)) {
                return false;
            }
            MultidimensionalArraySerializerTest.MyPojo other = ((MultidimensionalArraySerializerTest.MyPojo) (obj));
            return ((((field1) == null) && ((other.field1) == null)) || (((field1) != null) && (field1.equals(other.field1)))) && ((field2) == (other.field2));
        }
    }

    @Test
    public void testObjectArrays() {
        Integer[][] array = new Integer[][]{ new Integer[]{ 0, 1 }, null, new Integer[]{ null, 42 } };
        TypeInformation<Integer[][]> ti = TypeExtractor.getForClass(Integer[][].class);
        SerializerTestInstance<Integer[][]> testInstance = new SerializerTestInstance<Integer[][]>(ti.createSerializer(new ExecutionConfig()), Integer[][].class, (-1), array);
        testInstance.testAll();
        MultidimensionalArraySerializerTest.MyPojo[][] array2 = new MultidimensionalArraySerializerTest.MyPojo[][]{ new MultidimensionalArraySerializerTest.MyPojo[]{ new MultidimensionalArraySerializerTest.MyPojo(null, 42), new MultidimensionalArraySerializerTest.MyPojo("test2", (-1)) }, new MultidimensionalArraySerializerTest.MyPojo[]{ null, null }, null };
        TypeInformation<MultidimensionalArraySerializerTest.MyPojo[][]> ti2 = TypeExtractor.getForClass(MultidimensionalArraySerializerTest.MyPojo[][].class);
        SerializerTestInstance<MultidimensionalArraySerializerTest.MyPojo[][]> testInstance2 = new SerializerTestInstance<MultidimensionalArraySerializerTest.MyPojo[][]>(ti2.createSerializer(new ExecutionConfig()), MultidimensionalArraySerializerTest.MyPojo[][].class, (-1), array2);
        testInstance2.testAll();
    }

    public static class MyGenericPojo<T> {
        public T[][] field;

        public MyGenericPojo() {
            // nothing to do
        }

        public MyGenericPojo(T[][] field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArraySerializerTest.MyGenericPojo)) {
                return false;
            }
            MultidimensionalArraySerializerTest.MyGenericPojo<?> other = ((MultidimensionalArraySerializerTest.MyGenericPojo<?>) (obj));
            return (((field) == null) && ((other.field) == null)) || (((field) != null) && ((field.length) == (other.field.length)));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericObjectArrays() {
        MultidimensionalArraySerializerTest.MyGenericPojo<String>[][] array = ((MultidimensionalArraySerializerTest.MyGenericPojo<String>[][]) (new MultidimensionalArraySerializerTest.MyGenericPojo[][]{ new MultidimensionalArraySerializerTest.MyGenericPojo[]{ new MultidimensionalArraySerializerTest.MyGenericPojo<String>(new String[][]{ new String[]{ "a", "b" }, new String[]{ "c", "d" } }), null } }));
        TypeInformation<MultidimensionalArraySerializerTest.MyGenericPojo<String>[][]> ti = TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<MultidimensionalArraySerializerTest.MyGenericPojo<String>[][]>() {});
        SerializerTestInstance testInstance = new SerializerTestInstance(ti.createSerializer(new ExecutionConfig()), MultidimensionalArraySerializerTest.MyGenericPojo[][].class, (-1), ((Object) (array)));
        testInstance.testAll();
    }
}

