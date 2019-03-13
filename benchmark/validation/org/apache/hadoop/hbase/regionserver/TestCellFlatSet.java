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
package org.apache.hadoop.hbase.regionserver;


import MemStoreLAB.POOL_INITIAL_SIZE_DEFAULT;
import MemStoreLABImpl.CHUNK_SIZE_DEFAULT;
import java.lang.management.ManagementFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.util.MemorySizeUtil;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Category({ RegionServerTests.class, SmallTests.class })
@RunWith(Parameterized.class)
public class TestCellFlatSet {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellFlatSet.class);

    private static final int NUM_OF_CELLS = 4;

    private static final int SMALL_CHUNK_SIZE = 64;

    private Cell[] ascCells;

    private CellArrayMap ascCbOnHeap;

    private Cell[] descCells;

    private CellArrayMap descCbOnHeap;

    private static final Configuration CONF = new Configuration();

    private KeyValue lowerOuterCell;

    private KeyValue upperOuterCell;

    private CellChunkMap ascCCM;// for testing ascending CellChunkMap with one chunk in array


    private CellChunkMap descCCM;// for testing descending CellChunkMap with one chunk in array


    private final boolean smallChunks;

    private static ChunkCreator chunkCreator;

    public TestCellFlatSet(String chunkType) {
        long globalMemStoreLimit = ((long) ((ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()) * (MemorySizeUtil.getGlobalMemStoreHeapPercent(TestCellFlatSet.CONF, false))));
        if (chunkType.equals("NORMAL_CHUNKS")) {
            TestCellFlatSet.chunkCreator = ChunkCreator.initialize(CHUNK_SIZE_DEFAULT, false, globalMemStoreLimit, 0.2F, POOL_INITIAL_SIZE_DEFAULT, null);
            Assert.assertNotNull(TestCellFlatSet.chunkCreator);
            smallChunks = false;
        } else {
            // chunkCreator with smaller chunk size, so only 3 cell-representations can accommodate a chunk
            TestCellFlatSet.chunkCreator = ChunkCreator.initialize(TestCellFlatSet.SMALL_CHUNK_SIZE, false, globalMemStoreLimit, 0.2F, POOL_INITIAL_SIZE_DEFAULT, null);
            Assert.assertNotNull(TestCellFlatSet.chunkCreator);
            smallChunks = true;
        }
    }

    /* Create and test ascending CellSet based on CellArrayMap */
    @Test
    public void testCellArrayMapAsc() throws Exception {
        CellSet cs = new CellSet(ascCbOnHeap);
        testCellBlocks(cs);
        testIterators(cs);
    }

    /* Create and test ascending and descending CellSet based on CellChunkMap */
    @Test
    public void testCellChunkMap() throws Exception {
        CellSet cs = new CellSet(ascCCM);
        testCellBlocks(cs);
        testIterators(cs);
        testSubSet(cs);
        cs = new CellSet(descCCM);
        testSubSet(cs);
        // cs = new CellSet(ascMultCCM);
        // testCellBlocks(cs);
        // testSubSet(cs);
        // cs = new CellSet(descMultCCM);
        // testSubSet(cs);
    }

    @Test
    public void testAsc() throws Exception {
        CellSet ascCs = new CellSet(ascCbOnHeap);
        Assert.assertEquals(TestCellFlatSet.NUM_OF_CELLS, ascCs.size());
        testSubSet(ascCs);
    }

    @Test
    public void testDesc() throws Exception {
        CellSet descCs = new CellSet(descCbOnHeap);
        Assert.assertEquals(TestCellFlatSet.NUM_OF_CELLS, descCs.size());
        testSubSet(descCs);
    }
}

