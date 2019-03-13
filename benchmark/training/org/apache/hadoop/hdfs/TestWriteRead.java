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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestWriteRead {
    // Junit test settings.
    private static final int WR_NTIMES = 350;

    private static final int WR_CHUNK_SIZE = 10000;

    private static final int BUFFER_SIZE = 8192 * 100;

    private static final String ROOT_DIR = "/tmp/";

    private static final long blockSize = 1024 * 100;

    // command-line options. Different defaults for unit test vs real cluster
    String filenameOption = (TestWriteRead.ROOT_DIR) + "fileX1";

    int chunkSizeOption = 10000;

    int loopOption = 10;

    private MiniDFSCluster cluster;

    private Configuration conf;// = new HdfsConfiguration();


    private FileSystem mfs;// = cluster.getFileSystem();


    private FileContext mfc;// = FileContext.getFileContext();


    // configuration
    private boolean useFCOption = false;// use either FileSystem or FileContext


    private boolean verboseOption = true;

    private boolean positionReadOption = false;

    private boolean truncateOption = false;

    private final boolean abortTestOnFailure = true;

    private static final Logger LOG = LoggerFactory.getLogger(TestWriteRead.class);

    /**
     * Junit Test reading while writing.
     */
    @Test
    public void testWriteReadSeq() throws IOException {
        useFCOption = false;
        positionReadOption = false;
        String fname = filenameOption;
        long rdBeginPos = 0;
        // need to run long enough to fail: takes 25 to 35 seec on Mac
        int stat = testWriteAndRead(fname, TestWriteRead.WR_NTIMES, TestWriteRead.WR_CHUNK_SIZE, rdBeginPos);
        TestWriteRead.LOG.info(("Summary status from test1: status= " + stat));
        Assert.assertEquals(0, stat);
    }

    /**
     * Junit Test position read while writing.
     */
    @Test
    public void testWriteReadPos() throws IOException {
        String fname = filenameOption;
        positionReadOption = true;// position read

        long rdBeginPos = 0;
        int stat = testWriteAndRead(fname, TestWriteRead.WR_NTIMES, TestWriteRead.WR_CHUNK_SIZE, rdBeginPos);
        Assert.assertEquals(0, stat);
    }

    /**
     * Junit Test position read of the current block being written.
     */
    @Test
    public void testReadPosCurrentBlock() throws IOException {
        String fname = filenameOption;
        positionReadOption = true;// position read

        int wrChunkSize = ((int) (TestWriteRead.blockSize)) + ((int) ((TestWriteRead.blockSize) / 2));
        long rdBeginPos = (TestWriteRead.blockSize) + 1;
        int numTimes = 5;
        int stat = testWriteAndRead(fname, numTimes, wrChunkSize, rdBeginPos);
        Assert.assertEquals(0, stat);
    }
}

