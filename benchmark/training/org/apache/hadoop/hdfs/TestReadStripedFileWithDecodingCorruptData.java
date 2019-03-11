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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test online recovery with corrupt files. This test is parameterized.
 */
@RunWith(Parameterized.class)
public class TestReadStripedFileWithDecodingCorruptData {
    static final Logger LOG = LoggerFactory.getLogger(TestReadStripedFileWithDecodingCorruptData.class);

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    private int fileLength;

    private int dataDelNum;

    private int parityDelNum;

    public TestReadStripedFileWithDecodingCorruptData(int fileLength, int dataDelNum, int parityDelNum) {
        this.fileLength = fileLength;
        this.dataDelNum = dataDelNum;
        this.parityDelNum = parityDelNum;
    }

    /**
     * Corrupt tolerable number of block before reading.
     * Verify the decoding works correctly.
     */
    @Test
    public void testReadCorruptedData() throws IOException {
        String src = (("/corrupted_" + (dataDelNum)) + "_") + (parityDelNum);
        ReadStripedFileWithDecodingHelper.testReadWithBlockCorrupted(TestReadStripedFileWithDecodingCorruptData.cluster, TestReadStripedFileWithDecodingCorruptData.dfs, src, fileLength, dataDelNum, parityDelNum, false);
    }
}

