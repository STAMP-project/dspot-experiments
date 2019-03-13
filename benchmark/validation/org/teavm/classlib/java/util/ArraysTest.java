/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ArraysTest {
    @Test
    public void arraySorted() {
        Integer[] array = new Integer[]{ 2, 5, 7, 3, 5, 6 };
        Arrays.sort(array);
        Assert.assertEquals(Integer.valueOf(2), array[0]);
        Assert.assertEquals(Integer.valueOf(3), array[1]);
        Assert.assertEquals(Integer.valueOf(5), array[2]);
        Assert.assertEquals(Integer.valueOf(5), array[3]);
        Assert.assertEquals(Integer.valueOf(6), array[4]);
        Assert.assertEquals(Integer.valueOf(7), array[5]);
    }

    @Test
    public void binarySearchWorks() {
        Integer[] array = new Integer[]{ 2, 4, 6, 8, 10, 12, 14, 16 };
        Assert.assertEquals(3, Arrays.binarySearch(array, 8));
        Assert.assertEquals(7, Arrays.binarySearch(array, 16));
        Assert.assertEquals(0, Arrays.binarySearch(array, 2));
        Assert.assertEquals((-1), Arrays.binarySearch(array, 1));
        Assert.assertEquals((-2), Arrays.binarySearch(array, 3));
        Assert.assertEquals((-3), Arrays.binarySearch(array, 5));
        Assert.assertEquals((-8), Arrays.binarySearch(array, 15));
        Assert.assertEquals((-9), Arrays.binarySearch(array, 17));
    }

    @Test
    public void arrayExposedAsList() {
        Integer[] array = new Integer[]{ 2, 3, 4 };
        List<Integer> list = Arrays.asList(array);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Integer.valueOf(4), list.get(2));
    }

    @Test
    public void arrayExposedAsString() {
        Object[] array = new Object[]{ 1, 2, null, null, "foo" };
        array[3] = array;
        Assert.assertEquals("[1, 2, null, [...], foo]", Arrays.deepToString(array));
    }

    @Test
    public void objectStream() {
        String[] array = new String[]{ "foo", "bar", "baz" };
        String result = Arrays.stream(array).collect(Collectors.joining(","));
        Assert.assertEquals("foo,bar,baz", result);
        result = Arrays.stream(array, 1, 3).collect(Collectors.joining(","));
        Assert.assertEquals("bar,baz", result);
        result = Arrays.stream(array, 0, 2).collect(Collectors.joining(","));
        Assert.assertEquals("foo,bar", result);
    }

    @Test
    public void intStream() {
        int[] array = new int[]{ 23, 42, 55 };
        String result = Arrays.stream(array).mapToObj(Integer::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23,42,55", result);
        result = Arrays.stream(array, 1, 3).mapToObj(Integer::toString).collect(Collectors.joining(","));
        Assert.assertEquals("42,55", result);
        result = Arrays.stream(array, 0, 2).mapToObj(Integer::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23,42", result);
    }

    @Test
    public void longStream() {
        long[] array = new long[]{ 23, 42, 55 };
        String result = Arrays.stream(array).mapToObj(Long::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23,42,55", result);
        result = Arrays.stream(array, 1, 3).mapToObj(Long::toString).collect(Collectors.joining(","));
        Assert.assertEquals("42,55", result);
        result = Arrays.stream(array, 0, 2).mapToObj(Long::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23,42", result);
    }

    @Test
    public void doubleStream() {
        double[] array = new double[]{ 23, 42, 55 };
        String result = Arrays.stream(array).mapToObj(Double::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23.0,42.0,55.0", result);
        result = Arrays.stream(array, 1, 3).mapToObj(Double::toString).collect(Collectors.joining(","));
        Assert.assertEquals("42.0,55.0", result);
        result = Arrays.stream(array, 0, 2).mapToObj(Double::toString).collect(Collectors.joining(","));
        Assert.assertEquals("23.0,42.0", result);
    }
}

