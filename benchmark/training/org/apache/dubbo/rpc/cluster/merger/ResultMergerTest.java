/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster.merger;


import ArrayMerger.INSTANCE;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ResultMergerTest {
    /**
     * MergerFactory test
     */
    @Test
    public void testMergerFactoryIllegalArgumentException() {
        try {
            MergerFactory.getMerger(null);
            Assertions.fail("expected IllegalArgumentException for null argument");
        } catch (IllegalArgumentException exception) {
            Assertions.assertEquals("returnType is null", exception.getMessage());
        }
    }

    /**
     * ArrayMerger test
     */
    @Test
    public void testArrayMergerIllegalArgumentException() {
        String[] stringArray = new String[]{ "1", "2", "3" };
        Integer[] integerArray = new Integer[]{ 3, 4, 5 };
        try {
            Object result = INSTANCE.merge(stringArray, null, integerArray);
            Assertions.fail("expected IllegalArgumentException for different arguments' types");
        } catch (IllegalArgumentException exception) {
            Assertions.assertEquals("Arguments' types are different", exception.getMessage());
        }
    }

    /**
     * ArrayMerger test
     */
    @Test
    public void testArrayMerger() {
        String[] stringArray1 = new String[]{ "1", "2", "3" };
        String[] stringArray2 = new String[]{ "4", "5", "6" };
        String[] stringArray3 = new String[]{  };
        Object result = INSTANCE.merge(stringArray1, stringArray2, stringArray3, null);
        Assertions.assertTrue(result.getClass().isArray());
        Assertions.assertEquals(6, Array.getLength(result));
        Assertions.assertTrue(String.class.isInstance(Array.get(result, 0)));
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(String.valueOf((i + 1)), Array.get(result, i));
        }
        Integer[] intArray1 = new Integer[]{ 1, 2, 3 };
        Integer[] intArray2 = new Integer[]{ 4, 5, 6 };
        Integer[] intArray3 = new Integer[]{ 7 };
        // trigger ArrayMerger
        result = MergerFactory.getMerger(Integer[].class).merge(intArray1, intArray2, intArray3, null);
        Assertions.assertTrue(result.getClass().isArray());
        Assertions.assertEquals(7, Array.getLength(result));
        Assertions.assertTrue(((Integer.class) == (result.getClass().getComponentType())));
        for (int i = 0; i < 7; i++) {
            Assertions.assertEquals((i + 1), Array.get(result, i));
        }
        result = INSTANCE.merge(null);
        Assertions.assertEquals(0, Array.getLength(result));
        result = INSTANCE.merge(null, null);
        Assertions.assertEquals(0, Array.getLength(result));
        result = INSTANCE.merge(null, new Object[0]);
        Assertions.assertEquals(0, Array.getLength(result));
    }

    /**
     * BooleanArrayMerger test
     */
    @Test
    public void testBooleanArrayMerger() {
        boolean[] arrayOne = new boolean[]{ true, false };
        boolean[] arrayTwo = new boolean[]{ false };
        boolean[] result = MergerFactory.getMerger(boolean[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(3, result.length);
        boolean[] mergedResult = new boolean[]{ true, false, false };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertEquals(mergedResult[i], result[i]);
        }
        result = MergerFactory.getMerger(boolean[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(boolean[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * ByteArrayMerger test
     */
    @Test
    public void testByteArrayMerger() {
        byte[] arrayOne = new byte[]{ 1, 2 };
        byte[] arrayTwo = new byte[]{ 1, 32 };
        byte[] result = MergerFactory.getMerger(byte[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        byte[] mergedResult = new byte[]{ 1, 2, 1, 32 };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertEquals(mergedResult[i], result[i]);
        }
        result = MergerFactory.getMerger(byte[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(byte[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * CharArrayMerger test
     */
    @Test
    public void testCharArrayMerger() {
        char[] arrayOne = "hello".toCharArray();
        char[] arrayTwo = "world".toCharArray();
        char[] result = MergerFactory.getMerger(char[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(10, result.length);
        char[] mergedResult = "helloworld".toCharArray();
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertEquals(mergedResult[i], result[i]);
        }
        result = MergerFactory.getMerger(char[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(char[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * DoubleArrayMerger test
     */
    @Test
    public void testDoubleArrayMerger() {
        double[] arrayOne = new double[]{ 1.2, 3.5 };
        double[] arrayTwo = new double[]{ 2.0, 34.0 };
        double[] result = MergerFactory.getMerger(double[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        double[] mergedResult = new double[]{ 1.2, 3.5, 2.0, 34.0 };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertTrue(((mergedResult[i]) == (result[i])));
        }
        result = MergerFactory.getMerger(double[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(double[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * FloatArrayMerger test
     */
    @Test
    public void testFloatArrayMerger() {
        float[] arrayOne = new float[]{ 1.2F, 3.5F };
        float[] arrayTwo = new float[]{ 2.0F, 34.0F };
        float[] result = MergerFactory.getMerger(float[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        double[] mergedResult = new double[]{ 1.2F, 3.5F, 2.0F, 34.0F };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertTrue(((mergedResult[i]) == (result[i])));
        }
        result = MergerFactory.getMerger(float[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(float[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * IntArrayMerger test
     */
    @Test
    public void testIntArrayMerger() {
        int[] arrayOne = new int[]{ 1, 2 };
        int[] arrayTwo = new int[]{ 2, 34 };
        int[] result = MergerFactory.getMerger(int[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        double[] mergedResult = new double[]{ 1, 2, 2, 34 };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertTrue(((mergedResult[i]) == (result[i])));
        }
        result = MergerFactory.getMerger(int[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(int[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * ListMerger test
     */
    @Test
    public void testListMerger() {
        List<Object> list1 = new ArrayList<Object>() {
            {
                add(null);
                add("1");
                add("2");
            }
        };
        List<Object> list2 = new ArrayList<Object>() {
            {
                add("3");
                add("4");
            }
        };
        List result = MergerFactory.getMerger(List.class).merge(list1, list2, null);
        Assertions.assertEquals(5, result.size());
        ArrayList<String> expected = new ArrayList<String>() {
            {
                add(null);
                add("1");
                add("2");
                add("3");
                add("4");
            }
        };
        Assertions.assertEquals(expected, result);
        result = MergerFactory.getMerger(List.class).merge(null);
        Assertions.assertEquals(0, result.size());
        result = MergerFactory.getMerger(List.class).merge(null, null);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * LongArrayMerger test
     */
    @Test
    public void testMapArrayMerger() {
        Map<Object, Object> mapOne = new HashMap<Object, Object>() {
            {
                put("11", 222);
                put("223", 11);
            }
        };
        Map<Object, Object> mapTwo = new HashMap<Object, Object>() {
            {
                put("3333", 3232);
                put("444", 2323);
            }
        };
        Map<Object, Object> result = MergerFactory.getMerger(Map.class).merge(mapOne, mapTwo, null);
        Assertions.assertEquals(4, result.size());
        Map<String, Integer> mergedResult = new HashMap<String, Integer>() {
            {
                put("11", 222);
                put("223", 11);
                put("3333", 3232);
                put("444", 2323);
            }
        };
        Assertions.assertEquals(mergedResult, result);
        result = MergerFactory.getMerger(Map.class).merge(null);
        Assertions.assertEquals(0, result.size());
        result = MergerFactory.getMerger(Map.class).merge(null, null);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * LongArrayMerger test
     */
    @Test
    public void testLongArrayMerger() {
        long[] arrayOne = new long[]{ 1L, 2L };
        long[] arrayTwo = new long[]{ 2L, 34L };
        long[] result = MergerFactory.getMerger(long[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        double[] mergedResult = new double[]{ 1L, 2L, 2L, 34L };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertTrue(((mergedResult[i]) == (result[i])));
        }
        result = MergerFactory.getMerger(long[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(long[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }

    /**
     * SetMerger test
     */
    @Test
    public void testSetMerger() {
        Set<Object> set1 = new HashSet<Object>() {
            {
                add(null);
                add("1");
                add("2");
            }
        };
        Set<Object> set2 = new HashSet<Object>() {
            {
                add("2");
                add("3");
            }
        };
        Set result = MergerFactory.getMerger(Set.class).merge(set1, set2, null);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(new HashSet<String>() {
            {
                add(null);
                add("1");
                add("2");
                add("3");
            }
        }, result);
        result = MergerFactory.getMerger(Set.class).merge(null);
        Assertions.assertEquals(0, result.size());
        result = MergerFactory.getMerger(Set.class).merge(null, null);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * ShortArrayMerger test
     */
    @Test
    public void testShortArrayMerger() {
        short[] arrayOne = new short[]{ 1, 2 };
        short[] arrayTwo = new short[]{ 2, 34 };
        short[] result = MergerFactory.getMerger(short[].class).merge(arrayOne, arrayTwo, null);
        Assertions.assertEquals(4, result.length);
        double[] mergedResult = new double[]{ 1, 2, 2, 34 };
        for (int i = 0; i < (mergedResult.length); i++) {
            Assertions.assertTrue(((mergedResult[i]) == (result[i])));
        }
        result = MergerFactory.getMerger(short[].class).merge(null);
        Assertions.assertEquals(0, result.length);
        result = MergerFactory.getMerger(short[].class).merge(null, null);
        Assertions.assertEquals(0, result.length);
    }
}

