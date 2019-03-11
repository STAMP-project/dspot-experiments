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
package org.apache.hadoop.hdfs;


import CreateFlag.APPEND;
import CreateFlag.NEW_BLOCK;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests append on erasure coded file.
 */
public class TestStripedFileAppend {
    public static final Log LOG = LogFactory.getLog(TestStripedFileAppend.class);

    static {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
    }

    private static final int NUM_DATA_BLOCKS = StripedFileTestUtil.getDefaultECPolicy().getNumDataUnits();

    private static final int CELL_SIZE = StripedFileTestUtil.getDefaultECPolicy().getCellSize();

    private static final int NUM_DN = 9;

    private static final int STRIPES_PER_BLOCK = 4;

    private static final int BLOCK_SIZE = (TestStripedFileAppend.CELL_SIZE) * (TestStripedFileAppend.STRIPES_PER_BLOCK);

    private static final int BLOCK_GROUP_SIZE = (TestStripedFileAppend.BLOCK_SIZE) * (TestStripedFileAppend.NUM_DATA_BLOCKS);

    private static final Random RANDOM = new Random();

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    private Path dir = new Path("/TestFileAppendStriped");

    private HdfsConfiguration conf = new HdfsConfiguration();

    /**
     * test simple append to a closed striped file, with NEW_BLOCK flag enabled.
     */
    @Test
    public void testAppendToNewBlock() throws IOException {
        int fileLength = 0;
        int totalSplit = 6;
        byte[] expected = StripedFileTestUtil.generateBytes(((TestStripedFileAppend.BLOCK_GROUP_SIZE) * totalSplit));
        Path file = new Path(dir, "testAppendToNewBlock");
        FSDataOutputStream out;
        for (int split = 0; split < totalSplit; split++) {
            if (split == 0) {
                out = dfs.create(file);
            } else {
                out = dfs.append(file, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
            }
            int splitLength = TestStripedFileAppend.RANDOM.nextInt(TestStripedFileAppend.BLOCK_GROUP_SIZE);
            out.write(expected, fileLength, splitLength);
            fileLength += splitLength;
            out.close();
        }
        expected = Arrays.copyOf(expected, fileLength);
        LocatedBlocks lbs = dfs.getClient().getLocatedBlocks(file.toString(), 0L, Long.MAX_VALUE);
        Assert.assertEquals(totalSplit, lbs.getLocatedBlocks().size());
        StripedFileTestUtil.verifyStatefulRead(dfs, file, fileLength, expected, new byte[4096]);
        StripedFileTestUtil.verifySeek(dfs, file, fileLength, StripedFileTestUtil.getDefaultECPolicy(), totalSplit);
    }
}

