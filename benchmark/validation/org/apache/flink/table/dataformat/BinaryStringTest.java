/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.	See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.	You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.dataformat;


import BinaryString.EMPTY_UTF8;
import java.nio.charset.StandardCharsets;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.table.runtime.sort.SortUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test of {@link BinaryString}.
 *
 * <p>Caution that you must construct a string by {@link #fromString} to cover all the
 * test cases.
 */
@RunWith(Parameterized.class)
public class BinaryStringTest {
    private BinaryString empty = fromString("");

    private final BinaryStringTest.Mode mode;

    public BinaryStringTest(BinaryStringTest.Mode mode) {
        this.mode = mode;
    }

    private enum Mode {

        ONE_SEG,
        MULTI_SEGS,
        STRING,
        RANDOM;}

    @Test
    public void basicTest() {
        checkBasic("", 0);
        checkBasic(",", 1);
        checkBasic("hello", 5);
        checkBasic("hello world", 11);
        checkBasic("Flink????", 9);
        checkBasic("? ? ? ?", 7);
        checkBasic("?", 1);// 2 bytes char

        checkBasic("??", 2);// 2 * 2 bytes chars

        checkBasic("???", 3);// 3 * 3 bytes chars

        checkBasic("\ud83e\udd19", 1);// 4 bytes char

    }

    @Test
    public void emptyStringTest() {
        Assert.assertEquals(empty, fromString(""));
        Assert.assertEquals(empty, BinaryString.fromBytes(new byte[0]));
        // assertEquals(0, empty.numChars());
        Assert.assertEquals(0, empty.getSizeInBytes());
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, fromString("   ").compareTo(BinaryString.blankString(3)));
        Assert.assertTrue(((fromString("").compareTo(fromString("a"))) < 0));
        Assert.assertTrue(((fromString("abc").compareTo(fromString("ABC"))) > 0));
        Assert.assertTrue(((fromString("abc0").compareTo(fromString("abc"))) > 0));
        Assert.assertEquals(0, fromString("abcabcabc").compareTo(fromString("abcabcabc")));
        Assert.assertTrue(((fromString("aBcabcabc").compareTo(fromString("Abcabcabc"))) > 0));
        Assert.assertTrue(((fromString("Abcabcabc").compareTo(fromString("abcabcabC"))) < 0));
        Assert.assertTrue(((fromString("abcabcabc").compareTo(fromString("abcabcabC"))) > 0));
        Assert.assertTrue(((fromString("abc").compareTo(fromString("??"))) < 0));
        Assert.assertTrue(((fromString("??").compareTo(fromString("??"))) > 0));
        Assert.assertTrue(((fromString("??123").compareTo(fromString("??122"))) > 0));
        MemorySegment segment1 = MemorySegmentFactory.allocateUnpooledSegment(1024);
        MemorySegment segment2 = MemorySegmentFactory.allocateUnpooledSegment(1024);
        SortUtil.putBinaryStringNormalizedKey(fromString("abcabcabc"), segment1, 0, 9);
        SortUtil.putBinaryStringNormalizedKey(fromString("abcabcabC"), segment2, 0, 9);
        Assert.assertTrue(((segment1.compare(segment2, 0, 0, 9)) > 0));
        SortUtil.putBinaryStringNormalizedKey(fromString("abcab"), segment1, 0, 9);
        Assert.assertTrue(((segment1.compare(segment2, 0, 0, 9)) < 0));
    }

    @Test
    public void testMultiSegments() {
        // prepare
        MemorySegment[] segments1 = new MemorySegment[2];
        segments1[0] = MemorySegmentFactory.wrap(new byte[10]);
        segments1[1] = MemorySegmentFactory.wrap(new byte[10]);
        segments1[0].put(5, "abcde".getBytes(StandardCharsets.UTF_8), 0, 5);
        segments1[1].put(0, "aaaaa".getBytes(StandardCharsets.UTF_8), 0, 5);
        MemorySegment[] segments2 = new MemorySegment[2];
        segments2[0] = MemorySegmentFactory.wrap(new byte[5]);
        segments2[1] = MemorySegmentFactory.wrap(new byte[5]);
        segments2[0].put(0, "abcde".getBytes(StandardCharsets.UTF_8), 0, 5);
        segments2[1].put(0, "b".getBytes(StandardCharsets.UTF_8), 0, 1);
        // test go ahead both
        BinaryString binaryString1 = BinaryString.fromAddress(segments1, 5, 10);
        BinaryString binaryString2 = BinaryString.fromAddress(segments2, 0, 6);
        Assert.assertEquals("abcdeaaaaa", binaryString1.toString());
        Assert.assertEquals("abcdeb", binaryString2.toString());
        Assert.assertEquals((-1), binaryString1.compareTo(binaryString2));
        // test needCompare == len
        binaryString1 = BinaryString.fromAddress(segments1, 5, 5);
        binaryString2 = BinaryString.fromAddress(segments2, 0, 5);
        Assert.assertEquals("abcde", binaryString1.toString());
        Assert.assertEquals("abcde", binaryString2.toString());
        Assert.assertEquals(0, binaryString1.compareTo(binaryString2));
        // test find the first segment of this string
        binaryString1 = BinaryString.fromAddress(segments1, 10, 5);
        binaryString2 = BinaryString.fromAddress(segments2, 0, 5);
        Assert.assertEquals("aaaaa", binaryString1.toString());
        Assert.assertEquals("abcde", binaryString2.toString());
        Assert.assertEquals((-1), binaryString1.compareTo(binaryString2));
        Assert.assertEquals(1, binaryString2.compareTo(binaryString1));
        // test go ahead single
        segments2 = new MemorySegment[]{ MemorySegmentFactory.wrap(new byte[10]) };
        segments2[0].put(4, "abcdeb".getBytes(StandardCharsets.UTF_8), 0, 6);
        binaryString1 = BinaryString.fromAddress(segments1, 5, 10);
        binaryString2 = BinaryString.fromAddress(segments2, 4, 6);
        Assert.assertEquals("abcdeaaaaa", binaryString1.toString());
        Assert.assertEquals("abcdeb", binaryString2.toString());
        Assert.assertEquals((-1), binaryString1.compareTo(binaryString2));
        Assert.assertEquals(1, binaryString2.compareTo(binaryString1));
    }

    @Test
    public void testEmptyString() {
        BinaryString str2 = fromString("hahahahah");
        BinaryString str3 = new BinaryString(null);
        {
            MemorySegment[] segments = new MemorySegment[2];
            segments[0] = MemorySegmentFactory.wrap(new byte[10]);
            segments[1] = MemorySegmentFactory.wrap(new byte[10]);
            str3.pointTo(segments, 15, 0);
        }
        Assert.assertTrue(((EMPTY_UTF8.compareTo(str2)) < 0));
        Assert.assertTrue(((str2.compareTo(EMPTY_UTF8)) > 0));
        Assert.assertTrue(((EMPTY_UTF8.compareTo(str3)) == 0));
        Assert.assertTrue(((str3.compareTo(EMPTY_UTF8)) == 0));
        Assert.assertFalse(EMPTY_UTF8.equals(str2));
        Assert.assertFalse(str2.equals(EMPTY_UTF8));
        Assert.assertTrue(EMPTY_UTF8.equals(str3));
        Assert.assertTrue(str3.equals(EMPTY_UTF8));
    }

    @Test
    public void testLazy() {
        String javaStr = "haha";
        BinaryString str = BinaryString.fromString(javaStr);
        str.ensureMaterialized();
        // check reference same.
        Assert.assertTrue(((str.toString()) == javaStr));
    }
}

