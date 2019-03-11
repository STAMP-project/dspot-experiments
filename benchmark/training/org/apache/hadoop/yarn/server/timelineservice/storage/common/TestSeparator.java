/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.storage.common;


import Separator.QUALIFIERS;
import Separator.SPACE;
import Separator.TAB;
import Separator.VALUES;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;

import static Separator.VARIABLE_SIZE;


public class TestSeparator {
    private static String villain = "Dr. Heinz Doofenshmirtz";

    private static String special = ".   *   |   ?   +   \t   (   )   [   ]   {   }   ^   $  \\ \"  %";

    /**
     *
     */
    @Test
    public void testEncodeDecodeString() {
        for (Separator separator : Separator.values()) {
            testEncodeDecode(separator, "");
            testEncodeDecode(separator, " ");
            testEncodeDecode(separator, "!");
            testEncodeDecode(separator, "?");
            testEncodeDecode(separator, "&");
            testEncodeDecode(separator, "+");
            testEncodeDecode(separator, "\t");
            testEncodeDecode(separator, "Dr.");
            testEncodeDecode(separator, "Heinz");
            testEncodeDecode(separator, "Doofenshmirtz");
            testEncodeDecode(separator, TestSeparator.villain);
            testEncodeDecode(separator, TestSeparator.special);
            Assert.assertNull(separator.encode(null));
        }
    }

    @Test
    public void testEncodeDecode() {
        TestSeparator.testEncodeDecode("Dr.", QUALIFIERS);
        TestSeparator.testEncodeDecode("Heinz", QUALIFIERS, QUALIFIERS);
        TestSeparator.testEncodeDecode("Doofenshmirtz", QUALIFIERS, null, QUALIFIERS);
        TestSeparator.testEncodeDecode("&Perry", QUALIFIERS, VALUES, null);
        TestSeparator.testEncodeDecode("the ", QUALIFIERS, SPACE);
        TestSeparator.testEncodeDecode("Platypus...", ((Separator) (null)));
        TestSeparator.testEncodeDecode("The what now ?!?", QUALIFIERS, VALUES, SPACE);
    }

    @Test
    public void testEncodedValues() {
        TestSeparator.testEncodeDecode(("Double-escape %2$ and %9$ or %%2$ or %%3$, nor  %%%2$" + "= no problem!"), QUALIFIERS, VALUES, SPACE, TAB);
    }

    @Test
    public void testSplits() {
        byte[] maxLongBytes = Bytes.toBytes(Long.MAX_VALUE);
        byte[] maxIntBytes = Bytes.toBytes(Integer.MAX_VALUE);
        for (Separator separator : Separator.values()) {
            String str1 = ("cl" + (separator.getValue())) + "us";
            String str2 = (separator.getValue()) + "rst";
            byte[] sepByteArr = Bytes.toBytes(separator.getValue());
            byte[] longVal1Arr = Bytes.add(sepByteArr, Bytes.copy(maxLongBytes, sepByteArr.length, ((Bytes.SIZEOF_LONG) - (sepByteArr.length))));
            byte[] intVal1Arr = Bytes.add(sepByteArr, Bytes.copy(maxIntBytes, sepByteArr.length, ((Bytes.SIZEOF_INT) - (sepByteArr.length))));
            byte[] arr = separator.join(Bytes.toBytes(separator.encode(str1)), longVal1Arr, Bytes.toBytes(separator.encode(str2)), intVal1Arr);
            int[] sizes = new int[]{ VARIABLE_SIZE, Bytes.SIZEOF_LONG, VARIABLE_SIZE, Bytes.SIZEOF_INT };
            byte[][] splits = separator.split(arr, sizes);
            Assert.assertEquals(4, splits.length);
            Assert.assertEquals(str1, separator.decode(Bytes.toString(splits[0])));
            Assert.assertEquals(Bytes.toLong(longVal1Arr), Bytes.toLong(splits[1]));
            Assert.assertEquals(str2, separator.decode(Bytes.toString(splits[2])));
            Assert.assertEquals(Bytes.toInt(intVal1Arr), Bytes.toInt(splits[3]));
            longVal1Arr = Bytes.add(Bytes.copy(maxLongBytes, 0, ((Bytes.SIZEOF_LONG) - (sepByteArr.length))), sepByteArr);
            intVal1Arr = Bytes.add(Bytes.copy(maxIntBytes, 0, ((Bytes.SIZEOF_INT) - (sepByteArr.length))), sepByteArr);
            arr = separator.join(Bytes.toBytes(separator.encode(str1)), longVal1Arr, Bytes.toBytes(separator.encode(str2)), intVal1Arr);
            splits = separator.split(arr, sizes);
            Assert.assertEquals(4, splits.length);
            Assert.assertEquals(str1, separator.decode(Bytes.toString(splits[0])));
            Assert.assertEquals(Bytes.toLong(longVal1Arr), Bytes.toLong(splits[1]));
            Assert.assertEquals(str2, separator.decode(Bytes.toString(splits[2])));
            Assert.assertEquals(Bytes.toInt(intVal1Arr), Bytes.toInt(splits[3]));
            longVal1Arr = Bytes.add(sepByteArr, Bytes.copy(maxLongBytes, sepByteArr.length, (4 - (sepByteArr.length))), sepByteArr);
            longVal1Arr = Bytes.add(longVal1Arr, Bytes.copy(maxLongBytes, 4, (3 - (sepByteArr.length))), sepByteArr);
            arr = separator.join(Bytes.toBytes(separator.encode(str1)), longVal1Arr, Bytes.toBytes(separator.encode(str2)), intVal1Arr);
            splits = separator.split(arr, sizes);
            Assert.assertEquals(4, splits.length);
            Assert.assertEquals(str1, separator.decode(Bytes.toString(splits[0])));
            Assert.assertEquals(Bytes.toLong(longVal1Arr), Bytes.toLong(splits[1]));
            Assert.assertEquals(str2, separator.decode(Bytes.toString(splits[2])));
            Assert.assertEquals(Bytes.toInt(intVal1Arr), Bytes.toInt(splits[3]));
            arr = separator.join(Bytes.toBytes(separator.encode(str1)), Bytes.toBytes(separator.encode(str2)), intVal1Arr, longVal1Arr);
            int[] sizes1 = new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, Bytes.SIZEOF_INT, Bytes.SIZEOF_LONG };
            splits = separator.split(arr, sizes1);
            Assert.assertEquals(4, splits.length);
            Assert.assertEquals(str1, separator.decode(Bytes.toString(splits[0])));
            Assert.assertEquals(str2, separator.decode(Bytes.toString(splits[1])));
            Assert.assertEquals(Bytes.toInt(intVal1Arr), Bytes.toInt(splits[2]));
            Assert.assertEquals(Bytes.toLong(longVal1Arr), Bytes.toLong(splits[3]));
            try {
                int[] sizes2 = new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, Bytes.SIZEOF_INT, 7 };
                splits = separator.split(arr, sizes2);
                Assert.fail("Exception should have been thrown.");
            } catch (IllegalArgumentException e) {
            }
            try {
                int[] sizes2 = new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, 2, Bytes.SIZEOF_LONG };
                splits = separator.split(arr, sizes2);
                Assert.fail("Exception should have been thrown.");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void testJoinStripped() {
        List<String> stringList = new ArrayList<String>(0);
        stringList.add("nothing");
        String joined = VALUES.joinEncoded(stringList);
        Iterable<String> split = VALUES.splitEncoded(joined);
        Assert.assertTrue(Iterables.elementsEqual(stringList, split));
        stringList = new ArrayList<String>(3);
        stringList.add("a");
        stringList.add("b?");
        stringList.add("c");
        joined = VALUES.joinEncoded(stringList);
        split = VALUES.splitEncoded(joined);
        Assert.assertTrue(Iterables.elementsEqual(stringList, split));
        String[] stringArray1 = new String[]{ "else" };
        joined = VALUES.joinEncoded(stringArray1);
        split = VALUES.splitEncoded(joined);
        Assert.assertTrue(Iterables.elementsEqual(Arrays.asList(stringArray1), split));
        String[] stringArray2 = new String[]{ "d", "e?", "f" };
        joined = VALUES.joinEncoded(stringArray2);
        split = VALUES.splitEncoded(joined);
        Assert.assertTrue(Iterables.elementsEqual(Arrays.asList(stringArray2), split));
        List<String> empty = new ArrayList<String>(0);
        split = VALUES.splitEncoded(null);
        Assert.assertTrue(Iterables.elementsEqual(empty, split));
    }
}

