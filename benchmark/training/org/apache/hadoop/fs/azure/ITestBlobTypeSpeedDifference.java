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
package org.apache.hadoop.fs.azure;


import org.junit.Test;


/**
 * A simple benchmark to find out the difference in speed between block
 * and page blobs.
 */
public class ITestBlobTypeSpeedDifference extends AbstractWasbTestBase {
    private static class TestResult {
        final long timeTakenInMs;

        final long totalNumberOfRequests;

        TestResult(long timeTakenInMs, long totalNumberOfRequests) {
            this.timeTakenInMs = timeTakenInMs;
            this.totalNumberOfRequests = totalNumberOfRequests;
        }
    }

    /**
     * Runs the benchmark over a small 10 KB file, flushing every 500 bytes.
     */
    @Test
    public void testTenKbFileFrequentFlush() throws Exception {
        ITestBlobTypeSpeedDifference.testForSizeAndFlushInterval(getFileSystem(), (10 * 1000), 500);
    }
}

