/**
 * -
 * -\-\-
 * Helios Testing Library
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
package com.spotify.helios.testing;


import com.google.common.base.Optional;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.JobStatus;
import java.util.Date;
import java.util.Map;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class JobNamePrefixTest extends TemporaryJobsTestBase {
    private static JobPrefixFile jobPrefixFile;

    @Test
    public void testJobNamePrefix() throws Exception {
        // Create four jobs which represent these use cases:
        // job1 - Created, deployed, locked. Simulates a job being used by another process. The
        // job should not get undeployed or deleted since it is in use.
        // job2 - Created, not deployed, locked. Simulates a job being used by another process. The
        // job should not get deleted since it is in use.
        // job3 - Created, deployed, not locked. Simulates an old job no longer in use, which should
        // be undeployed and deleted.
        // job4 - Created, not deployed, not locked. Simulates an old job no longer in use, which
        // should be deleted.
        // job1 - create and deploy
        final JobId jobId1 = createJob(((testJobName) + "_1"), testJobVersion, BUSYBOX, IDLE_COMMAND);
        deployJob(jobId1, TemporaryJobsTestBase.testHost1);
        // job2 - create
        final JobId jobId2 = createJob(((testJobName) + "_2"), testJobVersion, BUSYBOX, IDLE_COMMAND);
        // job3 - create and deploy
        final JobId jobId3 = createJob(((testJobName) + "_3"), testJobVersion, BUSYBOX, IDLE_COMMAND);
        deployJob(jobId3, TemporaryJobsTestBase.testHost1);
        // job4 - create
        final JobId jobId4 = createJob(((testJobName) + "_4"), testJobVersion, BUSYBOX, IDLE_COMMAND);
        // Create prefix files for all four jobs. They will be locked by default.
        try (JobPrefixFile file1 = JobPrefixFile.create(jobId1.getName(), TemporaryJobsTestBase.prefixDirectory);JobPrefixFile file2 = JobPrefixFile.create(jobId2.getName(), TemporaryJobsTestBase.prefixDirectory);JobPrefixFile file3 = JobPrefixFile.create(jobId3.getName(), TemporaryJobsTestBase.prefixDirectory);JobPrefixFile file4 = JobPrefixFile.create(jobId4.getName(), TemporaryJobsTestBase.prefixDirectory)) {
            // Release the locks of jobs 3 and 4 so they can be cleaned up
            file3.release();
            file4.release();
            Assert.assertThat(PrintableResult.testResult(JobNamePrefixTest.JobNamePrefixTestImpl.class), ResultMatchers.isSuccessful());
            final Map<JobId, Job> jobs = TemporaryJobsTestBase.client.jobs().get();
            // Verify job1 is still deployed and the prefix file has not been deleted.
            Assert.assertThat(jobs, Matchers.hasKey(jobId1));
            final JobStatus status1 = TemporaryJobsTestBase.client.jobStatus(jobId1).get();
            Assert.assertThat(status1.getDeployments().size(), Matchers.is(1));
            Assert.assertTrue(JobNamePrefixTest.fileExists(TemporaryJobsTestBase.prefixDirectory, jobId1.getName()));
            // Verify job2 still exists, is not deployed, and the prefix file is still there.
            Assert.assertThat(jobs, Matchers.hasKey(jobId2));
            final JobStatus status2 = TemporaryJobsTestBase.client.jobStatus(jobId2).get();
            Assert.assertThat(status2.getDeployments().size(), Matchers.is(0));
            Assert.assertTrue(JobNamePrefixTest.fileExists(TemporaryJobsTestBase.prefixDirectory, jobId2.getName()));
            // Verify that job3 has been deleted (which means it has also been undeployed), and
            // the prefix file has been deleted.
            Assert.assertThat(jobs, Matchers.not(Matchers.hasKey(jobId3)));
            Assert.assertFalse(JobNamePrefixTest.fileExists(TemporaryJobsTestBase.prefixDirectory, jobId3.getName()));
            // Verify that job4 and its prefix file have been deleted.
            Assert.assertThat(jobs, Matchers.not(Matchers.hasKey(jobId4)));
            Assert.assertFalse(JobNamePrefixTest.fileExists(TemporaryJobsTestBase.prefixDirectory, jobId4.getName()));
            // Verify the prefix file created during the run of JobNamePrefixTest was deleted
            Assert.assertFalse(JobNamePrefixTest.fileExists(TemporaryJobsTestBase.prefixDirectory, JobNamePrefixTest.jobPrefixFile.prefix()));
        }
    }

    public static class JobNamePrefixTestImpl {
        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobsTestBase.temporaryJobsBuilder().client(TemporaryJobsTestBase.client).prober(new TemporaryJobsTestBase.TestProber()).prefixDirectory(TemporaryJobsTestBase.prefixDirectory.toString()).jobPrefix(Optional.of(TemporaryJobsTestBase.testTag).get()).build();

        private final Date expires = new DateTime().plusHours(1).toDate();

        private TemporaryJob job1;

        private TemporaryJob job2;

        @Before
        public void setup() {
            job1 = temporaryJobs.job().command(IDLE_COMMAND).deploy(TemporaryJobsTestBase.testHost1);
            job2 = temporaryJobs.job().command(IDLE_COMMAND).expires(expires).deploy(TemporaryJobsTestBase.testHost1);
        }

        @Test
        public void testJobPrefixFile() throws Exception {
            // Verify a default expires values was set on job1
            Assert.assertThat(job1.job().getExpires(), Matchers.is(Matchers.notNullValue()));
            // Verify expires was set correctly on job2
            Assert.assertThat(job2.job().getExpires(), Matchers.is(Matchers.equalTo(expires)));
            // Get all jobs from master to make sure values are set correctly there
            final Map<JobId, Job> jobs = TemporaryJobsTestBase.client.jobs().get();
            // Verify job1 was set correctly on master
            final Job remoteJob1 = jobs.get(job1.job().getId());
            Assert.assertThat(remoteJob1, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(remoteJob1.getExpires(), Matchers.is(Matchers.equalTo(job1.job().getExpires())));
            // Verify job2 was set correctly on master
            final Job remoteJob2 = jobs.get(job2.job().getId());
            Assert.assertThat(remoteJob2, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(remoteJob2.getExpires(), Matchers.equalTo(expires));
            // Set jobPrefixFile so we can verify it was deleted after test completed
            JobNamePrefixTest.jobPrefixFile = temporaryJobs.jobPrefixFile();
        }
    }
}

