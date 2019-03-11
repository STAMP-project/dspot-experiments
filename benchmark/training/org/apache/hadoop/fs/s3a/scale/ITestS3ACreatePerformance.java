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
package org.apache.hadoop.fs.s3a.scale;


import java.io.OutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for create(): performance and/or load testing.
 */
public class ITestS3ACreatePerformance extends S3AScaleTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3ADirectoryPerformance.class);

    private Path basePath;

    private int basePathDepth;

    private static final int PATH_DEPTH = 10;

    /**
     * Test rate at which we can create deeply-nested files from a single thread.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeepSequentialCreate() throws Exception {
        long numOperations = getOperationCount();
        S3AFileSystem fs = getFileSystem();
        NanoTimer timer = new NanoTimer();
        for (int i = 0; i < numOperations; i++) {
            Path p = getPathIteration(i, ITestS3ACreatePerformance.PATH_DEPTH);
            OutputStream out = fs.create(p);
            out.write(40);// one byte file with some value 40

            out.close();
        }
        timer.end("Time to create %d files of depth %d", getOperationCount(), ITestS3ACreatePerformance.PATH_DEPTH);
        ITestS3ACreatePerformance.LOG.info("Time per create: {} msec", ((timer.nanosPerOperation(numOperations)) / 1000));
    }
}

