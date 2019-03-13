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


import Goal.START;
import com.google.common.collect.ImmutableMap;
import com.spotify.helios.common.Clock;
import com.spotify.helios.common.descriptors.Deployment;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.JobStatus;
import com.spotify.helios.master.MasterModel;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class ExpiredJobReaperTest {
    @Mock
    private MasterModel masterModel;

    @Mock
    private Clock mockClock;

    private static final JobId NON_EXPIRING_JOB_ID = JobId.fromString("non_expiring");

    private static final Job NON_EXPIRING_JOB = Job.newBuilder().setCommand(Arrays.asList("foo", "foo")).setImage("foo:4711").setName("foo").setVersion("17").build();

    private static final JobId EXPIRING_JOB_ID = JobId.fromString("expiring");

    private static final long EXPIRED_TS = 0;

    private static final long CURRENT_TS = 1;

    private static final long FUTURE_TS = 2;

    private static final Job EXPIRING_JOB = Job.newBuilder().setCommand(Arrays.asList("foo", "foo")).setImage("foo:4711").setName("foo").setVersion("17").setExpires(new Date(ExpiredJobReaperTest.EXPIRED_TS)).build();

    private static final JobId FAR_FUTURE_EXPIRING_JOB_ID = JobId.fromString("far_future_expiring");

    private static final Job FAR_FUTURE_EXPIRING_JOB = Job.newBuilder().setCommand(Arrays.asList("foo", "foo")).setImage("foo:4711").setName("foo").setVersion("17").setExpires(new Date(ExpiredJobReaperTest.FUTURE_TS)).build();

    private static final Map<JobId, Job> JOBS = ImmutableMap.of(ExpiredJobReaperTest.NON_EXPIRING_JOB_ID, ExpiredJobReaperTest.NON_EXPIRING_JOB, ExpiredJobReaperTest.EXPIRING_JOB_ID, ExpiredJobReaperTest.EXPIRING_JOB, ExpiredJobReaperTest.FAR_FUTURE_EXPIRING_JOB_ID, ExpiredJobReaperTest.FAR_FUTURE_EXPIRING_JOB);

    @Test
    public void testExpiredJobReaper() throws Exception {
        Mockito.when(mockClock.now()).thenReturn(new Instant(ExpiredJobReaperTest.CURRENT_TS));
        Mockito.when(masterModel.getJobs()).thenReturn(ExpiredJobReaperTest.JOBS);
        Mockito.when(masterModel.getJobStatus(ArgumentMatchers.any(JobId.class))).then(new Answer<JobStatus>() {
            @Override
            public JobStatus answer(final InvocationOnMock invocation) throws Throwable {
                final JobId jobId = ((JobId) (invocation.getArguments()[0]));
                final Map<String, Deployment> deployments = ImmutableMap.of("hostA", Deployment.of(jobId, START), "hostB", Deployment.of(jobId, START));
                return JobStatus.newBuilder().setJob(ExpiredJobReaperTest.JOBS.get(jobId)).setDeployments(deployments).build();
            }
        });
        ExpiredJobReaper.newBuilder().setClock(mockClock).setMasterModel(masterModel).build().runOneIteration();
        // Make sure that the expiring job was removed, but that the non-expiring job
        // and the job that expires far in the future were not.
        Mockito.verify(masterModel).undeployJob(ArgumentMatchers.eq("hostA"), ArgumentMatchers.eq(ExpiredJobReaperTest.EXPIRING_JOB_ID), ArgumentMatchers.eq(""));
        Mockito.verify(masterModel).undeployJob(ArgumentMatchers.eq("hostB"), ArgumentMatchers.eq(ExpiredJobReaperTest.EXPIRING_JOB_ID), ArgumentMatchers.eq(""));
        Mockito.verify(masterModel).removeJob(ArgumentMatchers.eq(ExpiredJobReaperTest.EXPIRING_JOB_ID), ArgumentMatchers.eq(""));
        Mockito.verifyNoMoreInteractions(Mockito.ignoreStubs(masterModel));
    }
}

