/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.checker;


import DFSConfigKeys.DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY;
import VolumeCheckResult.HEALTHY;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.FakeTimer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test a few more conditions not covered by TestDatasetVolumeChecker.
 */
public class TestDatasetVolumeCheckerFailures {
    public static final Logger LOG = LoggerFactory.getLogger(TestDatasetVolumeCheckerFailures.class);

    private FakeTimer timer;

    private Configuration conf;

    private static final long MIN_DISK_CHECK_GAP_MS = 1000;// 1 second.


    /**
     * Test timeout in {@link DatasetVolumeChecker#checkAllVolumes}.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testTimeout() throws Exception {
        // Add a volume whose check routine hangs forever.
        final List<FsVolumeSpi> volumes = Collections.singletonList(TestDatasetVolumeCheckerFailures.makeHungVolume());
        final FsDatasetSpi<FsVolumeSpi> dataset = TestDatasetVolumeChecker.makeDataset(volumes);
        // Create a disk checker with a very low timeout.
        conf.setTimeDuration(DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY, 1, TimeUnit.SECONDS);
        final DatasetVolumeChecker checker = new DatasetVolumeChecker(conf, new FakeTimer());
        // Ensure that the hung volume is detected as failed.
        Set<FsVolumeSpi> failedVolumes = checker.checkAllVolumes(dataset);
        Assert.assertThat(failedVolumes.size(), Is.is(1));
    }

    /**
     * Test checking a closed volume i.e. one which cannot be referenced.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testCheckingClosedVolume() throws Exception {
        // Add a volume that cannot be referenced.
        final List<FsVolumeSpi> volumes = Collections.singletonList(TestDatasetVolumeCheckerFailures.makeClosedVolume());
        final FsDatasetSpi<FsVolumeSpi> dataset = TestDatasetVolumeChecker.makeDataset(volumes);
        DatasetVolumeChecker checker = new DatasetVolumeChecker(conf, timer);
        Set<FsVolumeSpi> failedVolumes = checker.checkAllVolumes(dataset);
        Assert.assertThat(failedVolumes.size(), Is.is(0));
        Assert.assertThat(checker.getNumSyncDatasetChecks(), Is.is(0L));
        // The closed volume should not have been checked as it cannot
        // be referenced.
        Mockito.verify(volumes.get(0), Mockito.times(0)).check(ArgumentMatchers.any());
    }

    @Test(timeout = 60000)
    public void testMinGapIsEnforcedForSyncChecks() throws Exception {
        final List<FsVolumeSpi> volumes = TestDatasetVolumeChecker.makeVolumes(1, HEALTHY);
        final FsDatasetSpi<FsVolumeSpi> dataset = TestDatasetVolumeChecker.makeDataset(volumes);
        final DatasetVolumeChecker checker = new DatasetVolumeChecker(conf, timer);
        checker.checkAllVolumes(dataset);
        Assert.assertThat(checker.getNumSyncDatasetChecks(), Is.is(1L));
        // Re-check without advancing the timer. Ensure the check is skipped.
        checker.checkAllVolumes(dataset);
        Assert.assertThat(checker.getNumSyncDatasetChecks(), Is.is(1L));
        Assert.assertThat(checker.getNumSkippedChecks(), Is.is(1L));
        // Re-check after advancing the timer. Ensure the check is performed.
        timer.advance(TestDatasetVolumeCheckerFailures.MIN_DISK_CHECK_GAP_MS);
        checker.checkAllVolumes(dataset);
        Assert.assertThat(checker.getNumSyncDatasetChecks(), Is.is(2L));
        Assert.assertThat(checker.getNumSkippedChecks(), Is.is(1L));
    }
}

