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


import DFSOutputStream.LOG;
import StreamCapability.HFLUSH;
import StreamCapability.HSYNC;
import SyncFlag.UPDATE_LENGTH;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDFSStripedOutputStream {
    public static final Logger LOG = LoggerFactory.getLogger(TestDFSStripedOutputStream.class);

    static {
        GenericTestUtils.setLogLevel(DFSOutputStream.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(DataStreamer.LOG, Level.ALL);
    }

    private ErasureCodingPolicy ecPolicy;

    private int dataBlocks;

    private int parityBlocks;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private Configuration conf;

    private int cellSize;

    private final int stripesPerBlock = 4;

    private int blockSize;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testFileEmpty() throws Exception {
        testOneFile("/EmptyFile", 0);
    }

    @Test
    public void testFileSmallerThanOneCell1() throws Exception {
        testOneFile("/SmallerThanOneCell", 1);
    }

    @Test
    public void testFileSmallerThanOneCell2() throws Exception {
        testOneFile("/SmallerThanOneCell", ((cellSize) - 1));
    }

    @Test
    public void testFileEqualsWithOneCell() throws Exception {
        testOneFile("/EqualsWithOneCell", cellSize);
    }

    @Test
    public void testFileSmallerThanOneStripe1() throws Exception {
        testOneFile("/SmallerThanOneStripe", (((cellSize) * (dataBlocks)) - 1));
    }

    @Test
    public void testFileSmallerThanOneStripe2() throws Exception {
        testOneFile("/SmallerThanOneStripe", ((cellSize) + 123));
    }

    @Test
    public void testFileEqualsWithOneStripe() throws Exception {
        testOneFile("/EqualsWithOneStripe", ((cellSize) * (dataBlocks)));
    }

    @Test
    public void testFileMoreThanOneStripe1() throws Exception {
        testOneFile("/MoreThanOneStripe1", (((cellSize) * (dataBlocks)) + 123));
    }

    @Test
    public void testFileMoreThanOneStripe2() throws Exception {
        testOneFile("/MoreThanOneStripe2", ((((cellSize) * (dataBlocks)) + ((cellSize) * (dataBlocks))) + 123));
    }

    @Test
    public void testFileLessThanFullBlockGroup() throws Exception {
        testOneFile("/LessThanFullBlockGroup", ((((cellSize) * (dataBlocks)) * ((stripesPerBlock) - 1)) + (cellSize)));
    }

    @Test
    public void testFileFullBlockGroup() throws Exception {
        testOneFile("/FullBlockGroup", ((blockSize) * (dataBlocks)));
    }

    @Test
    public void testFileMoreThanABlockGroup1() throws Exception {
        testOneFile("/MoreThanABlockGroup1", (((blockSize) * (dataBlocks)) + 123));
    }

    @Test
    public void testFileMoreThanABlockGroup2() throws Exception {
        testOneFile("/MoreThanABlockGroup2", ((((blockSize) * (dataBlocks)) + (cellSize)) + 123));
    }

    @Test
    public void testFileMoreThanABlockGroup3() throws Exception {
        testOneFile("/MoreThanABlockGroup3", ((((((blockSize) * (dataBlocks)) * 3) + ((cellSize) * (dataBlocks))) + (cellSize)) + 123));
    }

    /**
     * {@link DFSStripedOutputStream} doesn't support hflush() or hsync() yet.
     * This test is to make sure that DFSStripedOutputStream doesn't throw any
     * {@link UnsupportedOperationException} on hflush() or hsync() so as to
     * comply with output stream spec.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStreamFlush() throws Exception {
        final byte[] bytes = StripedFileTestUtil.generateBytes(((((((blockSize) * (dataBlocks)) * 3) + ((cellSize) * (dataBlocks))) + (cellSize)) + 123));
        try (FSDataOutputStream os = fs.create(new Path("/ec-file-1"))) {
            Assert.assertFalse("DFSStripedOutputStream should not have hflush() capability yet!", os.hasCapability(HFLUSH.getValue()));
            Assert.assertFalse("DFSStripedOutputStream should not have hsync() capability yet!", os.hasCapability(HSYNC.getValue()));
            try (InputStream is = new ByteArrayInputStream(bytes)) {
                IOUtils.copyBytes(is, os, bytes.length);
                os.hflush();
                IOUtils.copyBytes(is, os, bytes.length);
                os.hsync();
                IOUtils.copyBytes(is, os, bytes.length);
            }
            Assert.assertTrue("stream is not a DFSStripedOutputStream", ((os.getWrappedStream()) instanceof DFSStripedOutputStream));
            final DFSStripedOutputStream dfssos = ((DFSStripedOutputStream) (os.getWrappedStream()));
            dfssos.hsync(EnumSet.of(UPDATE_LENGTH));
        }
    }

    @Test
    public void testFileBlockSizeSmallerThanCellSize() throws Exception {
        final Path path = new Path("testFileBlockSizeSmallerThanCellSize");
        final byte[] bytes = StripedFileTestUtil.generateBytes(((cellSize) * 2));
        try {
            DFSTestUtil.writeFile(fs, path, bytes, ((cellSize) / 2));
            Assert.fail(("Creating a file with block size smaller than " + "ec policy's cell size should fail"));
        } catch (IOException expected) {
            TestDFSStripedOutputStream.LOG.info("Caught expected exception", expected);
            GenericTestUtils.assertExceptionContains("less than the cell size", expected);
        }
    }
}

