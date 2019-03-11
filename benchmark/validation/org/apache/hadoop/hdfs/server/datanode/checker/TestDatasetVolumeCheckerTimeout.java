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
package org.apache.hadoop.hdfs.server.datanode.checker;


import DFSConfigKeys.DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.util.FakeTimer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that timeout is triggered during Disk Volume Checker.
 */
public class TestDatasetVolumeCheckerTimeout {
    public static final Logger LOG = LoggerFactory.getLogger(TestDatasetVolumeCheckerTimeout.class);

    @Rule
    public TestName testName = new TestName();

    static Configuration conf;

    private static final long DISK_CHECK_TIMEOUT = 10;

    private static final long DISK_CHECK_TIME = 100;

    static ReentrantLock lock = new ReentrantLock();

    static {
        TestDatasetVolumeCheckerTimeout.conf = new HdfsConfiguration();
        TestDatasetVolumeCheckerTimeout.conf.setTimeDuration(DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY, TestDatasetVolumeCheckerTimeout.DISK_CHECK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 300000)
    public void testDiskCheckTimeout() throws Exception {
        TestDatasetVolumeCheckerTimeout.LOG.info("Executing {}", testName.getMethodName());
        final FsVolumeSpi volume = TestDatasetVolumeCheckerTimeout.makeSlowVolume();
        final DatasetVolumeChecker checker = new DatasetVolumeChecker(TestDatasetVolumeCheckerTimeout.conf, new FakeTimer());
        final AtomicLong numCallbackInvocations = new AtomicLong(0);
        TestDatasetVolumeCheckerTimeout.lock.lock();
        /**
         * Request a check and ensure it triggered {@link FsVolumeSpi#check}.
         */
        boolean result = checker.checkVolume(volume, new DatasetVolumeChecker.Callback() {
            @Override
            public void call(Set<FsVolumeSpi> healthyVolumes, Set<FsVolumeSpi> failedVolumes) {
                numCallbackInvocations.incrementAndGet();
                // Assert that the disk check registers a failed volume due to
                // timeout
                Assert.assertThat(healthyVolumes.size(), CoreMatchers.is(0));
                Assert.assertThat(failedVolumes.size(), CoreMatchers.is(1));
            }
        });
        // Wait for the callback
        Thread.sleep(TestDatasetVolumeCheckerTimeout.DISK_CHECK_TIME);
        // Release lock
        TestDatasetVolumeCheckerTimeout.lock.unlock();
        // Ensure that the check was invoked only once.
        Mockito.verify(volume, Mockito.times(1)).check(ArgumentMatchers.any());
        Assert.assertThat(numCallbackInvocations.get(), CoreMatchers.is(1L));
    }
}

