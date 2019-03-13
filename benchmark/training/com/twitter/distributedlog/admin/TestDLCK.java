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
package com.twitter.distributedlog.admin;


import CreateMode.PERSISTENT;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.twitter.distributedlog.BookKeeperClient;
import com.twitter.distributedlog.DLMTestUtil;
import com.twitter.distributedlog.DLSN;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.DistributedLogManager;
import com.twitter.distributedlog.DistributedLogManagerFactory;
import com.twitter.distributedlog.LogSegmentMetadata;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.metadata.LogSegmentMetadataStoreUpdater;
import com.twitter.distributedlog.util.SchedulerUtils;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDLCK extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestDLCK.class);

    protected static DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(10).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");

    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    @SuppressWarnings("deprecation")
    public void testCheckAndRepairDLNamespace() throws Exception {
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDLCK.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        confLocal.setLogSegmentSequenceNumberValidationEnabled(false);
        URI uri = createDLMURI("/check-and-repair-dl-namespace");
        zkc.get().create(uri.getPath(), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        com.twitter.distributedlog.DistributedLogManagerFactory factory = new com.twitter.distributedlog.DistributedLogManagerFactory(confLocal, uri);
        ExecutorService executorService = Executors.newCachedThreadPool();
        String streamName = "check-and-repair-dl-namespace";
        // Create completed log segments
        DistributedLogManager dlm = factory.createDistributedLogManagerWithSharedClients(streamName);
        DLMTestUtil.injectLogSegmentWithLastDLSN(dlm, confLocal, 1L, 1L, 10, false);
        DLMTestUtil.injectLogSegmentWithLastDLSN(dlm, confLocal, 2L, 11L, 10, true);
        DLMTestUtil.injectLogSegmentWithLastDLSN(dlm, confLocal, 3L, 21L, 10, false);
        DLMTestUtil.injectLogSegmentWithLastDLSN(dlm, confLocal, 4L, 31L, 10, true);
        // dryrun
        BookKeeperClient bkc = getBookKeeperClient(factory);
        DistributedLogAdmin.checkAndRepairDLNamespace(uri, factory, new com.twitter.distributedlog.metadata.DryrunLogSegmentMetadataStoreUpdater(confLocal, getLogSegmentMetadataStore(factory)), executorService, bkc, confLocal.getBKDigestPW(), false, false);
        Map<Long, LogSegmentMetadata> segments = TestDLCK.getLogSegments(dlm);
        TestDLCK.LOG.info("segments after drynrun {}", segments);
        TestDLCK.verifyLogSegment(segments, new DLSN(1L, 18L, 0L), 1L, 10, 10L);
        TestDLCK.verifyLogSegment(segments, new DLSN(2L, 16L, 0L), 2L, 9, 19L);
        TestDLCK.verifyLogSegment(segments, new DLSN(3L, 18L, 0L), 3L, 10, 30L);
        TestDLCK.verifyLogSegment(segments, new DLSN(4L, 16L, 0L), 4L, 9, 39L);
        // check and repair
        bkc = getBookKeeperClient(factory);
        DistributedLogAdmin.checkAndRepairDLNamespace(uri, factory, LogSegmentMetadataStoreUpdater.createMetadataUpdater(confLocal, getLogSegmentMetadataStore(factory)), executorService, bkc, confLocal.getBKDigestPW(), false, false);
        segments = TestDLCK.getLogSegments(dlm);
        TestDLCK.LOG.info("segments after repair {}", segments);
        TestDLCK.verifyLogSegment(segments, new DLSN(1L, 18L, 0L), 1L, 10, 10L);
        TestDLCK.verifyLogSegment(segments, new DLSN(2L, 18L, 0L), 2L, 10, 20L);
        TestDLCK.verifyLogSegment(segments, new DLSN(3L, 18L, 0L), 3L, 10, 30L);
        TestDLCK.verifyLogSegment(segments, new DLSN(4L, 18L, 0L), 4L, 10, 40L);
        dlm.close();
        SchedulerUtils.shutdownScheduler(executorService, 5, TimeUnit.MINUTES);
        factory.close();
    }
}

