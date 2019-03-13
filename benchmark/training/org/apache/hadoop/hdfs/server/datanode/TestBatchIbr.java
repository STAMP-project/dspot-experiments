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
package org.apache.hadoop.hdfs.server.datanode;


import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This test verifies that incremental block reports are sent in batch mode
 * and the namenode allows closing a file with COMMITTED blocks.
 */
public class TestBatchIbr {
    public static final Logger LOG = LoggerFactory.getLogger(TestBatchIbr.class);

    private static final short NUM_DATANODES = 4;

    private static final int BLOCK_SIZE = 1024;

    private static final int MAX_BLOCK_NUM = 8;

    private static final int NUM_FILES = 1000;

    private static final int NUM_THREADS = 128;

    private static final TestBatchIbr.ThreadLocalBuffer IO_BUF = new TestBatchIbr.ThreadLocalBuffer();

    private static final TestBatchIbr.ThreadLocalBuffer VERIFY_BUF = new TestBatchIbr.ThreadLocalBuffer();

    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(IncrementalBlockReportManager.class), Level.TRACE);
    }

    static class ThreadLocalBuffer extends ThreadLocal<byte[]> {
        @Override
        protected byte[] initialValue() {
            return new byte[TestBatchIbr.BLOCK_SIZE];
        }
    }

    @Test
    public void testIbr() throws Exception {
        TestBatchIbr.runIbrTest(0L);
        TestBatchIbr.runIbrTest(100L);
    }
}

