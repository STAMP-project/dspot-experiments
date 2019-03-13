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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests that blocks can be larger than 2GB
 */
public class TestLargeBlock {
    /**
     * {
     * GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
     * GenericTestUtils.setLogLevel(LeaseManager.LOG, Level.ALL);
     * GenericTestUtils.setLogLevel(FSNamesystem.LOG, Level.ALL);
     * GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);
     * GenericTestUtils.setLogLevel(TestLargeBlock.LOG, Level.ALL);
     * }
     */
    private static final Logger LOG = LoggerFactory.getLogger(TestLargeBlock.class);

    // should we verify the data read back from the file? (slow)
    static final boolean verifyData = true;

    static final byte[] pattern = new byte[]{ 'D', 'E', 'A', 'D', 'B', 'E', 'E', 'F' };

    static final int numDatanodes = 3;

    /**
     * Test for block size of 2GB + 512B. This test can take a rather long time to
     * complete on Windows (reading the file back can be slow) so we use a larger
     * timeout here.
     *
     * @throws IOException
     * 		in case of errors
     */
    @Test(timeout = 1800000)
    public void testLargeBlockSize() throws IOException {
        final long blockSize = (((2L * 1024L) * 1024L) * 1024L) + 512L;// 2GB + 512B

        runTest(blockSize);
    }
}

