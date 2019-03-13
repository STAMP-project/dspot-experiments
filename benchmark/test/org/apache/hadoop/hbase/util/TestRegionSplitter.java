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
package org.apache.hadoop.hbase.util;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.RegionSplitter.DecimalStringSplit;
import org.apache.hadoop.hbase.util.RegionSplitter.HexStringSplit;
import org.apache.hadoop.hbase.util.RegionSplitter.SplitAlgorithm;
import org.apache.hadoop.hbase.util.RegionSplitter.UniformSplit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link RegionSplitter}, which can create a pre-split table or do a
 * rolling split of an existing table.
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestRegionSplitter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionSplitter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionSplitter.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final String CF_NAME = "SPLIT_TEST_CF";

    private static final byte xFF = ((byte) (255));

    @Rule
    public TestName name = new TestName();

    /**
     * Test creating a pre-split table using the HexStringSplit algorithm.
     */
    @Test
    public void testCreatePresplitTableHex() throws Exception {
        final List<byte[]> expectedBounds = new ArrayList<>(17);
        expectedBounds.add(ArrayUtils.EMPTY_BYTE_ARRAY);
        expectedBounds.add(Bytes.toBytes("10000000"));
        expectedBounds.add(Bytes.toBytes("20000000"));
        expectedBounds.add(Bytes.toBytes("30000000"));
        expectedBounds.add(Bytes.toBytes("40000000"));
        expectedBounds.add(Bytes.toBytes("50000000"));
        expectedBounds.add(Bytes.toBytes("60000000"));
        expectedBounds.add(Bytes.toBytes("70000000"));
        expectedBounds.add(Bytes.toBytes("80000000"));
        expectedBounds.add(Bytes.toBytes("90000000"));
        expectedBounds.add(Bytes.toBytes("a0000000"));
        expectedBounds.add(Bytes.toBytes("b0000000"));
        expectedBounds.add(Bytes.toBytes("c0000000"));
        expectedBounds.add(Bytes.toBytes("d0000000"));
        expectedBounds.add(Bytes.toBytes("e0000000"));
        expectedBounds.add(Bytes.toBytes("f0000000"));
        expectedBounds.add(ArrayUtils.EMPTY_BYTE_ARRAY);
        // Do table creation/pre-splitting and verification of region boundaries
        preSplitTableAndVerify(expectedBounds, HexStringSplit.class.getSimpleName(), TableName.valueOf(name.getMethodName()));
    }

    /**
     * Test creating a pre-split table using the UniformSplit algorithm.
     */
    @Test
    public void testCreatePresplitTableUniform() throws Exception {
        List<byte[]> expectedBounds = new ArrayList<>(17);
        expectedBounds.add(ArrayUtils.EMPTY_BYTE_ARRAY);
        expectedBounds.add(new byte[]{ 16, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 32, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 48, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 64, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 80, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 96, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ 112, 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (128)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (144)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (160)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (176)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (192)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (208)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (224)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(new byte[]{ ((byte) (240)), 0, 0, 0, 0, 0, 0, 0 });
        expectedBounds.add(ArrayUtils.EMPTY_BYTE_ARRAY);
        // Do table creation/pre-splitting and verification of region boundaries
        preSplitTableAndVerify(expectedBounds, UniformSplit.class.getSimpleName(), TableName.valueOf(name.getMethodName()));
    }

    /**
     * Unit tests for the HexStringSplit algorithm. Makes sure it divides up the
     * space of keys in the way that we expect.
     */
    @Test
    public void unitTestHexStringSplit() {
        HexStringSplit splitter = new HexStringSplit();
        // Check splitting while starting from scratch
        byte[][] twoRegionsSplits = splitter.split(2);
        Assert.assertEquals(1, twoRegionsSplits.length);
        Assert.assertArrayEquals(Bytes.toBytes("80000000"), twoRegionsSplits[0]);
        byte[][] threeRegionsSplits = splitter.split(3);
        Assert.assertEquals(2, threeRegionsSplits.length);
        byte[] expectedSplit0 = Bytes.toBytes("55555555");
        Assert.assertArrayEquals(expectedSplit0, threeRegionsSplits[0]);
        byte[] expectedSplit1 = Bytes.toBytes("aaaaaaaa");
        Assert.assertArrayEquals(expectedSplit1, threeRegionsSplits[1]);
        // Check splitting existing regions that have start and end points
        byte[] splitPoint = splitter.split(Bytes.toBytes("10000000"), Bytes.toBytes("30000000"));
        Assert.assertArrayEquals(Bytes.toBytes("20000000"), splitPoint);
        byte[] lastRow = Bytes.toBytes("ffffffff");
        Assert.assertArrayEquals(lastRow, splitter.lastRow());
        byte[] firstRow = Bytes.toBytes("00000000");
        Assert.assertArrayEquals(firstRow, splitter.firstRow());
        // Halfway between 00... and 20... should be 10...
        splitPoint = splitter.split(firstRow, Bytes.toBytes("20000000"));
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splitPoint);
        // Halfway between df... and ff... should be ef....
        splitPoint = splitter.split(Bytes.toBytes("dfffffff"), lastRow);
        Assert.assertArrayEquals(Bytes.toBytes("efffffff"), splitPoint);
        // Check splitting region with multiple mappers per region
        byte[][] splits = splitter.split(Bytes.toBytes("00000000"), Bytes.toBytes("30000000"), 3, false);
        Assert.assertEquals(2, splits.length);
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splits[0]);
        Assert.assertArrayEquals(Bytes.toBytes("20000000"), splits[1]);
        splits = splitter.split(Bytes.toBytes("00000000"), Bytes.toBytes("20000000"), 2, true);
        Assert.assertEquals(3, splits.length);
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splits[1]);
    }

    /**
     * Unit tests for the DecimalStringSplit algorithm. Makes sure it divides up the
     * space of keys in the way that we expect.
     */
    @Test
    public void unitTestDecimalStringSplit() {
        DecimalStringSplit splitter = new DecimalStringSplit();
        // Check splitting while starting from scratch
        byte[][] twoRegionsSplits = splitter.split(2);
        Assert.assertEquals(1, twoRegionsSplits.length);
        Assert.assertArrayEquals(Bytes.toBytes("50000000"), twoRegionsSplits[0]);
        byte[][] threeRegionsSplits = splitter.split(3);
        Assert.assertEquals(2, threeRegionsSplits.length);
        byte[] expectedSplit0 = Bytes.toBytes("33333333");
        Assert.assertArrayEquals(expectedSplit0, threeRegionsSplits[0]);
        byte[] expectedSplit1 = Bytes.toBytes("66666666");
        Assert.assertArrayEquals(expectedSplit1, threeRegionsSplits[1]);
        // Check splitting existing regions that have start and end points
        byte[] splitPoint = splitter.split(Bytes.toBytes("10000000"), Bytes.toBytes("30000000"));
        Assert.assertArrayEquals(Bytes.toBytes("20000000"), splitPoint);
        byte[] lastRow = Bytes.toBytes("99999999");
        Assert.assertArrayEquals(lastRow, splitter.lastRow());
        byte[] firstRow = Bytes.toBytes("00000000");
        Assert.assertArrayEquals(firstRow, splitter.firstRow());
        // Halfway between 00... and 20... should be 10...
        splitPoint = splitter.split(firstRow, Bytes.toBytes("20000000"));
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splitPoint);
        // Halfway between 00... and 19... should be 09...
        splitPoint = splitter.split(firstRow, Bytes.toBytes("19999999"));
        Assert.assertArrayEquals(Bytes.toBytes("09999999"), splitPoint);
        // Halfway between 79... and 99... should be 89....
        splitPoint = splitter.split(Bytes.toBytes("79999999"), lastRow);
        Assert.assertArrayEquals(Bytes.toBytes("89999999"), splitPoint);
        // Check splitting region with multiple mappers per region
        byte[][] splits = splitter.split(Bytes.toBytes("00000000"), Bytes.toBytes("30000000"), 3, false);
        Assert.assertEquals(2, splits.length);
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splits[0]);
        Assert.assertArrayEquals(Bytes.toBytes("20000000"), splits[1]);
        splits = splitter.split(Bytes.toBytes("00000000"), Bytes.toBytes("20000000"), 2, true);
        Assert.assertEquals(3, splits.length);
        Assert.assertArrayEquals(Bytes.toBytes("10000000"), splits[1]);
    }

    /**
     * Unit tests for the UniformSplit algorithm. Makes sure it divides up the space of
     * keys in the way that we expect.
     */
    @Test
    public void unitTestUniformSplit() {
        UniformSplit splitter = new UniformSplit();
        // Check splitting while starting from scratch
        try {
            splitter.split(1);
            throw new AssertionError("Splitting into <2 regions should have thrown exception");
        } catch (IllegalArgumentException e) {
        }
        byte[][] twoRegionsSplits = splitter.split(2);
        Assert.assertEquals(1, twoRegionsSplits.length);
        Assert.assertArrayEquals(twoRegionsSplits[0], new byte[]{ ((byte) (128)), 0, 0, 0, 0, 0, 0, 0 });
        byte[][] threeRegionsSplits = splitter.split(3);
        Assert.assertEquals(2, threeRegionsSplits.length);
        byte[] expectedSplit0 = new byte[]{ 85, 85, 85, 85, 85, 85, 85, 85 };
        Assert.assertArrayEquals(expectedSplit0, threeRegionsSplits[0]);
        byte[] expectedSplit1 = new byte[]{ ((byte) (170)), ((byte) (170)), ((byte) (170)), ((byte) (170)), ((byte) (170)), ((byte) (170)), ((byte) (170)), ((byte) (170)) };
        Assert.assertArrayEquals(expectedSplit1, threeRegionsSplits[1]);
        // Check splitting existing regions that have start and end points
        byte[] splitPoint = splitter.split(new byte[]{ 16 }, new byte[]{ 48 });
        Assert.assertArrayEquals(new byte[]{ 32 }, splitPoint);
        byte[] lastRow = new byte[]{ TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF };
        Assert.assertArrayEquals(lastRow, splitter.lastRow());
        byte[] firstRow = ArrayUtils.EMPTY_BYTE_ARRAY;
        Assert.assertArrayEquals(firstRow, splitter.firstRow());
        splitPoint = splitter.split(firstRow, new byte[]{ 32 });
        Assert.assertArrayEquals(splitPoint, new byte[]{ 16 });
        splitPoint = splitter.split(new byte[]{ ((byte) (223)), TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF }, lastRow);
        Assert.assertArrayEquals(splitPoint, new byte[]{ ((byte) (239)), TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF, TestRegionSplitter.xFF });
        splitPoint = splitter.split(new byte[]{ 'a', 'a', 'a' }, new byte[]{ 'a', 'a', 'b' });
        Assert.assertArrayEquals(splitPoint, new byte[]{ 'a', 'a', 'a', ((byte) (128)) });
        // Check splitting region with multiple mappers per region
        byte[][] splits = splitter.split(new byte[]{ 'a', 'a', 'a' }, new byte[]{ 'a', 'a', 'd' }, 3, false);
        Assert.assertEquals(2, splits.length);
        Assert.assertArrayEquals(splits[0], new byte[]{ 'a', 'a', 'b' });
        Assert.assertArrayEquals(splits[1], new byte[]{ 'a', 'a', 'c' });
        splits = splitter.split(new byte[]{ 'a', 'a', 'a' }, new byte[]{ 'a', 'a', 'e' }, 2, true);
        Assert.assertEquals(3, splits.length);
        Assert.assertArrayEquals(splits[1], new byte[]{ 'a', 'a', 'c' });
    }

    @Test
    public void testUserInput() {
        SplitAlgorithm algo = new HexStringSplit();
        Assert.assertFalse(splitFailsPrecondition(algo));// default settings are fine

        Assert.assertFalse(splitFailsPrecondition(algo, "00", "AA"));// custom is fine

        Assert.assertTrue(splitFailsPrecondition(algo, "AA", "00"));// range error

        Assert.assertTrue(splitFailsPrecondition(algo, "AA", "AA"));// range error

        Assert.assertFalse(splitFailsPrecondition(algo, "0", "2", 3));// should be fine

        Assert.assertFalse(splitFailsPrecondition(algo, "0", "A", 11));// should be fine

        Assert.assertTrue(splitFailsPrecondition(algo, "0", "A", 12));// too granular

        algo = new DecimalStringSplit();
        Assert.assertFalse(splitFailsPrecondition(algo));// default settings are fine

        Assert.assertFalse(splitFailsPrecondition(algo, "00", "99"));// custom is fine

        Assert.assertTrue(splitFailsPrecondition(algo, "99", "00"));// range error

        Assert.assertTrue(splitFailsPrecondition(algo, "99", "99"));// range error

        Assert.assertFalse(splitFailsPrecondition(algo, "0", "2", 3));// should be fine

        Assert.assertFalse(splitFailsPrecondition(algo, "0", "9", 10));// should be fine

        Assert.assertTrue(splitFailsPrecondition(algo, "0", "9", 11));// too granular

        algo = new UniformSplit();
        Assert.assertFalse(splitFailsPrecondition(algo));// default settings are fine

        Assert.assertFalse(splitFailsPrecondition(algo, "\\x00", "\\xAA"));// custom is fine

        Assert.assertTrue(splitFailsPrecondition(algo, "\\xAA", "\\x00"));// range error

        Assert.assertTrue(splitFailsPrecondition(algo, "\\xAA", "\\xAA"));// range error

        Assert.assertFalse(splitFailsPrecondition(algo, "\\x00", "\\x02", 3));// should be fine

        Assert.assertFalse(splitFailsPrecondition(algo, "\\x00", "\\x0A", 11));// should be fine

        Assert.assertFalse(splitFailsPrecondition(algo, "\\x00", "\\x0A", 12));// should be fine

    }

    @Test
    public void noopRollingSplit() throws Exception {
        final List<byte[]> expectedBounds = new ArrayList<>(1);
        expectedBounds.add(ArrayUtils.EMPTY_BYTE_ARRAY);
        rollingSplitAndVerify(TableName.valueOf(TestRegionSplitter.class.getSimpleName()), "UniformSplit", expectedBounds);
    }
}

