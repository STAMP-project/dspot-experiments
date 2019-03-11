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


import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils.NanoTimer;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests concurrent operations on a single S3AFileSystem instance.
 */
public class ITestS3AConcurrentOps extends S3AScaleTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3AConcurrentOps.class);

    private final int concurrentRenames = 10;

    private Path testRoot;

    private Path[] source = new Path[concurrentRenames];

    private Path[] target = new Path[concurrentRenames];

    private S3AFileSystem fs;

    private S3AFileSystem auxFs;

    /**
     * Attempts to trigger a deadlock that would happen if any bounded resource
     * pool became saturated with control tasks that depended on other tasks
     * that now can't enter the resource pool to get completed.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testParallelRename() throws IOException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRenames, new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, ("testParallelRename" + (count.getAndIncrement())));
            }
        });
        ((ThreadPoolExecutor) (executor)).prestartAllCoreThreads();
        Future<Boolean>[] futures = new Future[concurrentRenames];
        for (int i = 0; i < (concurrentRenames); i++) {
            final int index = i;
            futures[i] = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    NanoTimer timer = new NanoTimer();
                    boolean result = fs.rename(source[index], target[index]);
                    timer.end("parallel rename %d", index);
                    ITestS3AConcurrentOps.LOG.info("Rename {} ran from {} to {}", index, timer.getStartTime(), timer.getEndTime());
                    return result;
                }
            });
        }
        ITestS3AConcurrentOps.LOG.info("Waiting for tasks to complete...");
        ITestS3AConcurrentOps.LOG.info(("Deadlock may have occurred if nothing else is logged" + " or the test times out"));
        for (int i = 0; i < (concurrentRenames); i++) {
            assertTrue(("No future " + i), futures[i].get());
            assertPathExists("target path", target[i]);
            assertPathDoesNotExist("source path", source[i]);
        }
        ITestS3AConcurrentOps.LOG.info("All tasks have completed successfully");
    }
}

