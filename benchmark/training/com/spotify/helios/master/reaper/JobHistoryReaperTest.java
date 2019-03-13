/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.master.reaper;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.master.MasterModel;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JobHistoryReaperTest {
    private static class Datapoint {
        private final String jobName;

        private final JobId jobId;

        private final Job job;

        private final boolean expectReap;

        private Datapoint(final String jobName, final Job job, final boolean expectReap) {
            this.jobName = jobName;
            this.jobId = JobId.fromString(jobName);
            this.job = job;
            this.expectReap = expectReap;
        }

        String getJobName() {
            return jobName;
        }

        Job getJob() {
            return job;
        }

        JobId getJobId() {
            return jobId;
        }
    }

    @Test
    public void testJobHistoryReaper() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        final List<JobHistoryReaperTest.Datapoint> datapoints = // A job history with a corresponding job should NOT BE reaped.
        // A job history without a corresponding job should BE reaped.
        Lists.newArrayList(new JobHistoryReaperTest.Datapoint("job1", Job.newBuilder().setName("job1").build(), false), new JobHistoryReaperTest.Datapoint("job2", null, true));
        for (final JobHistoryReaperTest.Datapoint dp : datapoints) {
            Mockito.when(masterModel.getJob(ArgumentMatchers.argThat(matchesName(dp.getJobName())))).thenReturn(dp.getJob());
        }
        final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);
        final List<String> jobHistories = ImmutableList.of("job1", "job2");
        Mockito.when(client.getChildren(Paths.historyJobs())).thenReturn(jobHistories);
        final JobHistoryReaper reaper = new JobHistoryReaper(masterModel, client, 100, 0);
        reaper.startAsync().awaitRunning();
        for (final JobHistoryReaperTest.Datapoint datapoint : datapoints) {
            if (datapoint.expectReap) {
                Mockito.verify(client, Mockito.timeout(500)).deleteRecursive(Paths.historyJob(datapoint.getJobId()));
            } else {
                Mockito.verify(client, Mockito.never()).deleteRecursive(Paths.historyJob(datapoint.getJobId()));
            }
        }
    }
}

