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
package org.apache.flink.hdfstests;


import JobSchedulingStatus.DONE;
import JobSchedulingStatus.PENDING;
import JobSchedulingStatus.RUNNING;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.highavailability.FsNegativeRunningJobsRegistry;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link FsNegativeRunningJobsRegistry} on HDFS.
 */
public class FsNegativeRunningJobsRegistryTest {
    @ClassRule
    public static final TemporaryFolder TEMP_DIR = new TemporaryFolder();

    private static MiniDFSCluster hdfsCluster;

    private static Path hdfsRootPath;

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    @Test
    public void testCreateAndSetFinished() throws Exception {
        final Path workDir = new Path(FsNegativeRunningJobsRegistryTest.hdfsRootPath, "test-work-dir");
        final JobID jid = new JobID();
        FsNegativeRunningJobsRegistry registry = new FsNegativeRunningJobsRegistry(workDir);
        // another registry should pick this up
        FsNegativeRunningJobsRegistry otherRegistry = new FsNegativeRunningJobsRegistry(workDir);
        // initially, without any call, the job is pending
        Assert.assertEquals(PENDING, registry.getJobSchedulingStatus(jid));
        Assert.assertEquals(PENDING, otherRegistry.getJobSchedulingStatus(jid));
        // after set running, the job is running
        registry.setJobRunning(jid);
        Assert.assertEquals(RUNNING, registry.getJobSchedulingStatus(jid));
        Assert.assertEquals(RUNNING, otherRegistry.getJobSchedulingStatus(jid));
        // set the job to finished and validate
        registry.setJobFinished(jid);
        Assert.assertEquals(DONE, registry.getJobSchedulingStatus(jid));
        Assert.assertEquals(DONE, otherRegistry.getJobSchedulingStatus(jid));
    }

    @Test
    public void testSetFinishedAndRunning() throws Exception {
        final Path workDir = new Path(FsNegativeRunningJobsRegistryTest.hdfsRootPath, "?nother_w?rk_direct?r?");
        final JobID jid = new JobID();
        FsNegativeRunningJobsRegistry registry = new FsNegativeRunningJobsRegistry(workDir);
        // set the job to finished and validate
        registry.setJobFinished(jid);
        Assert.assertEquals(DONE, registry.getJobSchedulingStatus(jid));
        // set the job to running does not overwrite the finished status
        registry.setJobRunning(jid);
        Assert.assertEquals(DONE, registry.getJobSchedulingStatus(jid));
        // another registry should pick this up
        FsNegativeRunningJobsRegistry otherRegistry = new FsNegativeRunningJobsRegistry(workDir);
        Assert.assertEquals(DONE, otherRegistry.getJobSchedulingStatus(jid));
        // clear the running and finished marker, it will be pending
        otherRegistry.clearJob(jid);
        Assert.assertEquals(PENDING, registry.getJobSchedulingStatus(jid));
        Assert.assertEquals(PENDING, otherRegistry.getJobSchedulingStatus(jid));
    }
}

