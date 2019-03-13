/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.functions.windowing.delta.extractor;


import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ConcatenatedExtract}.
 */
public class ConcatenatedExtractTest {
    private String[] testStringArray1 = new String[]{ "1", "2", "3" };

    private int[] testIntArray1 = new int[]{ 1, 2, 3 };

    private String[] testStringArray2 = new String[]{ "4", "5", "6" };

    private int[] testIntArray2 = new int[]{ 4, 5, 6 };

    private String[] testStringArray3 = new String[]{ "7", "8", "9" };

    private int[] testIntArray3 = new int[]{ 7, 8, 9 };

    private Tuple2<String[], int[]>[] testTuple2Array;

    private Tuple2<String[], int[]> testTuple2;

    private Tuple2<Tuple2<String[], int[]>, Tuple2<String[], int[]>[]> testData;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test1() {
        Extractor ext = new ConcatenatedExtract(new FieldFromTuple(0), new FieldFromTuple(1)).add(new FieldsFromArray(Integer.class, 2, 1, 0));
        int[] expected = new int[]{ testIntArray3[2], testIntArray3[1], testIntArray3[0] };
        Assert.assertEquals(new Integer(expected[0]), ((Integer[]) (ext.extract(testData)))[0]);
        Assert.assertEquals(new Integer(expected[1]), ((Integer[]) (ext.extract(testData)))[1]);
        Assert.assertEquals(new Integer(expected[2]), ((Integer[]) (ext.extract(testData)))[2]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test2() {
        Extractor ext = // String[]
        // Object[] (Containing String[])
        // Tuple2<String[],int[]>
        // Tuple2<String[],int[]>[]
        // Tuple2<String[],int[]>[]
        new ConcatenatedExtract(new FieldFromTuple(1), new FieldsFromArray(Tuple2.class, 1)).add(new FieldFromArray(0)).add(new ArrayFromTuple(0)).add(new FieldFromArray(0)).add(new FieldFromArray(1));// String

        String expected2 = testStringArray2[1];
        Assert.assertEquals(expected2, ext.extract(testData));
    }
}

