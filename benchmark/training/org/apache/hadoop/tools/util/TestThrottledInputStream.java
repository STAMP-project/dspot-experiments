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
package org.apache.hadoop.tools.util;


import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestThrottledInputStream {
    private static final Logger LOG = LoggerFactory.getLogger(TestThrottledInputStream.class);

    private static final int BUFF_SIZE = 1024;

    private enum CB {

        ONE_C,
        BUFFER,
        BUFF_OFFSET;}

    @Test
    public void testRead() {
        File tmpFile;
        File outFile;
        try {
            tmpFile = createFile(1024);
            outFile = createFile();
            tmpFile.deleteOnExit();
            outFile.deleteOnExit();
            long maxBandwidth = copyAndAssert(tmpFile, outFile, 0, 1, (-1), TestThrottledInputStream.CB.BUFFER);
            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, TestThrottledInputStream.CB.BUFFER);
            /* copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.BUFFER);
            copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.BUFFER);
             */
            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, TestThrottledInputStream.CB.BUFF_OFFSET);
            /* copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.BUFF_OFFSET);
            copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.BUFF_OFFSET);
             */
            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, TestThrottledInputStream.CB.ONE_C);
            /* copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.ONE_C);
            copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.ONE_C);
             */
        } catch (IOException e) {
            TestThrottledInputStream.LOG.error("Exception encountered ", e);
        }
    }
}

