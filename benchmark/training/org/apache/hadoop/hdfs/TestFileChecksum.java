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


import HdfsClientConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test serves a prototype to demo the idea proposed so far. It creates two
 * files using the same data, one is in replica mode, the other is in stripped
 * layout. For simple, it assumes 6 data blocks in both files and the block size
 * are the same.
 */
public class TestFileChecksum {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileChecksum.class);

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private int dataBlocks = ecPolicy.getNumDataUnits();

    private int parityBlocks = ecPolicy.getNumParityUnits();

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private Configuration conf;

    private DFSClient client;

    private int cellSize = ecPolicy.getCellSize();

    private int stripesPerBlock = 6;

    private int blockSize = (cellSize) * (stripesPerBlock);

    private int numBlockGroups = 10;

    private int stripSize = (cellSize) * (dataBlocks);

    private int blockGroupSize = (stripesPerBlock) * (stripSize);

    private int fileSize = (numBlockGroups) * (blockGroupSize);

    private int bytesPerCRC;

    private String ecDir = "/striped";

    private String stripedFile1 = (ecDir) + "/stripedFileChecksum1";

    private String stripedFile2 = (ecDir) + "/stripedFileChecksum2";

    private String replicatedFile = "/replicatedFileChecksum";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 90000)
    public void testStripedFileChecksum1() throws Exception {
        int length = 0;
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length + 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum2() throws Exception {
        int length = (stripSize) - 1;
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length - 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum3() throws Exception {
        int length = stripSize;
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length - 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum4() throws Exception {
        int length = (stripSize) + ((cellSize) * 2);
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length - 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum5() throws Exception {
        int length = blockGroupSize;
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length - 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum6() throws Exception {
        int length = (blockGroupSize) + (blockSize);
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, (length - 10));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksum7() throws Exception {
        int length = -1;// whole file

        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        testStripedFileChecksum(length, fileSize);
    }

    @Test(timeout = 90000)
    public void testStripedAndReplicatedFileChecksum() throws Exception {
        prepareTestFiles(fileSize, new String[]{ stripedFile1, replicatedFile });
        FileChecksum stripedFileChecksum1 = getFileChecksum(stripedFile1, 10, false);
        FileChecksum replicatedFileChecksum = getFileChecksum(replicatedFile, 10, false);
        if (expectComparableStripedAndReplicatedFiles()) {
            Assert.assertEquals(stripedFileChecksum1, replicatedFileChecksum);
        } else {
            Assert.assertNotEquals(stripedFileChecksum1, replicatedFileChecksum);
        }
    }

    @Test(timeout = 90000)
    public void testDifferentBlockSizeReplicatedFileChecksum() throws Exception {
        byte[] fileData = StripedFileTestUtil.generateBytes(fileSize);
        String replicatedFile1 = "/replicatedFile1";
        String replicatedFile2 = "/replicatedFile2";
        DFSTestUtil.writeFile(fs, new Path(replicatedFile1), fileData, blockSize);
        DFSTestUtil.writeFile(fs, new Path(replicatedFile2), fileData, ((blockSize) / 2));
        FileChecksum checksum1 = getFileChecksum(replicatedFile1, (-1), false);
        FileChecksum checksum2 = getFileChecksum(replicatedFile2, (-1), false);
        if (expectComparableDifferentBlockSizeReplicatedFiles()) {
            Assert.assertEquals(checksum1, checksum2);
        } else {
            Assert.assertNotEquals(checksum1, checksum2);
        }
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocks1() throws Exception {
        prepareTestFiles(fileSize, new String[]{ stripedFile1 });
        FileChecksum stripedFileChecksum1 = getFileChecksum(stripedFile1, fileSize, false);
        FileChecksum stripedFileChecksumRecon = getFileChecksum(stripedFile1, fileSize, true);
        TestFileChecksum.LOG.info(("stripedFileChecksum1:" + stripedFileChecksum1));
        TestFileChecksum.LOG.info(("stripedFileChecksumRecon:" + stripedFileChecksumRecon));
        Assert.assertTrue("Checksum mismatches!", stripedFileChecksum1.equals(stripedFileChecksumRecon));
    }

    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocks2() throws Exception {
        prepareTestFiles(fileSize, new String[]{ stripedFile1, stripedFile2 });
        FileChecksum stripedFileChecksum1 = getFileChecksum(stripedFile1, (-1), false);
        FileChecksum stripedFileChecksum2 = getFileChecksum(stripedFile2, (-1), false);
        FileChecksum stripedFileChecksum2Recon = getFileChecksum(stripedFile2, (-1), true);
        TestFileChecksum.LOG.info(("stripedFileChecksum1:" + stripedFileChecksum1));
        TestFileChecksum.LOG.info(("stripedFileChecksum2:" + stripedFileChecksum1));
        TestFileChecksum.LOG.info(("stripedFileChecksum2Recon:" + stripedFileChecksum2Recon));
        Assert.assertTrue("Checksum mismatches!", stripedFileChecksum1.equals(stripedFileChecksum2));
        Assert.assertTrue("Checksum mismatches!", stripedFileChecksum1.equals(stripedFileChecksum2Recon));
        Assert.assertTrue("Checksum mismatches!", stripedFileChecksum2.equals(stripedFileChecksum2Recon));
    }

    /**
     * Test to verify that the checksum can be computed for a small file less than
     * bytesPerCRC size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery1() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, 1);
    }

    /**
     * Test to verify that the checksum can be computed for a small file less than
     * bytesPerCRC size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery2() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, 10);
    }

    /**
     * Test to verify that the checksum can be computed by giving bytesPerCRC
     * length of file range for checksum calculation. 512 is the value of
     * bytesPerCRC.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery3() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, bytesPerCRC);
    }

    /**
     * Test to verify that the checksum can be computed by giving 'cellsize'
     * length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery4() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, cellSize);
    }

    /**
     * Test to verify that the checksum can be computed by giving less than
     * cellsize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery5() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((cellSize) - 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving greater than
     * cellsize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery6() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((cellSize) + 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving two times
     * cellsize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery7() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((cellSize) * 2));
    }

    /**
     * Test to verify that the checksum can be computed by giving stripSize
     * length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery8() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, stripSize);
    }

    /**
     * Test to verify that the checksum can be computed by giving less than
     * stripSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery9() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((stripSize) - 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving greater than
     * stripSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery10() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((stripSize) + 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving less than
     * blockGroupSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery11() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((blockGroupSize) - 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving greaterthan
     * blockGroupSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery12() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((blockGroupSize) + 1));
    }

    /**
     * Test to verify that the checksum can be computed by giving greater than
     * blockGroupSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery13() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, (((blockGroupSize) * (numBlockGroups)) / 2));
    }

    /**
     * Test to verify that the checksum can be computed by giving lessthan
     * fileSize length of file range for checksum calculation.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery14() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((fileSize) - 1));
    }

    /**
     * Test to verify that the checksum can be computed for a length greater than
     * file size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery15() throws Exception {
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile1, ((fileSize) * 2));
    }

    /**
     * Test to verify that the checksum can be computed for a small file less than
     * bytesPerCRC size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery16() throws Exception {
        int fileLength = 100;
        String stripedFile3 = (ecDir) + "/stripedFileChecksum3";
        prepareTestFiles(fileLength, new String[]{ stripedFile3 });
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile3, (fileLength - 1));
    }

    /**
     * Test to verify that the checksum can be computed for a small file less than
     * bytesPerCRC size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery17() throws Exception {
        int fileLength = 100;
        String stripedFile3 = (ecDir) + "/stripedFileChecksum3";
        prepareTestFiles(fileLength, new String[]{ stripedFile3 });
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile3, 1);
    }

    /**
     * Test to verify that the checksum can be computed for a small file less than
     * bytesPerCRC size.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery18() throws Exception {
        int fileLength = 100;
        String stripedFile3 = (ecDir) + "/stripedFileChecksum3";
        prepareTestFiles(fileLength, new String[]{ stripedFile3 });
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile3, 10);
    }

    /**
     * Test to verify that the checksum can be computed with greater than file
     * length.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery19() throws Exception {
        int fileLength = 100;
        String stripedFile3 = (ecDir) + "/stripedFileChecksum3";
        prepareTestFiles(fileLength, new String[]{ stripedFile3 });
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile3, (fileLength * 2));
    }

    /**
     * Test to verify that the checksum can be computed for small file with less
     * than file length.
     */
    @Test(timeout = 90000)
    public void testStripedFileChecksumWithMissedDataBlocksRangeQuery20() throws Exception {
        int fileLength = bytesPerCRC;
        String stripedFile3 = (ecDir) + "/stripedFileChecksum3";
        prepareTestFiles(fileLength, new String[]{ stripedFile3 });
        testStripedFileChecksumWithMissedDataBlocksRangeQuery(stripedFile3, ((bytesPerCRC) - 1));
    }

    @Test(timeout = 90000)
    public void testMixedBytesPerChecksum() throws Exception {
        int fileLength = (bytesPerCRC) * 3;
        byte[] fileData = StripedFileTestUtil.generateBytes(fileLength);
        String replicatedFile1 = "/replicatedFile1";
        // Split file into two parts.
        byte[] fileDataPart1 = new byte[(bytesPerCRC) * 2];
        System.arraycopy(fileData, 0, fileDataPart1, 0, fileDataPart1.length);
        byte[] fileDataPart2 = new byte[(fileData.length) - (fileDataPart1.length)];
        System.arraycopy(fileData, fileDataPart1.length, fileDataPart2, 0, fileDataPart2.length);
        DFSTestUtil.writeFile(fs, new Path(replicatedFile1), fileDataPart1);
        // Modify bytesPerCRC for second part that we append as separate block.
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, ((bytesPerCRC) / 2));
        DFSTestUtil.appendFileNewBlock(((DistributedFileSystem) (FileSystem.newInstance(conf))), new Path(replicatedFile1), fileDataPart2);
        if (expectSupportForSingleFileMixedBytesPerChecksum()) {
            String replicatedFile2 = "/replicatedFile2";
            DFSTestUtil.writeFile(fs, new Path(replicatedFile2), fileData);
            FileChecksum checksum1 = getFileChecksum(replicatedFile1, (-1), false);
            FileChecksum checksum2 = getFileChecksum(replicatedFile2, (-1), false);
            Assert.assertEquals(checksum1, checksum2);
        } else {
            exception.expect(IOException.class);
            FileChecksum checksum = getFileChecksum(replicatedFile1, (-1), false);
        }
    }
}

