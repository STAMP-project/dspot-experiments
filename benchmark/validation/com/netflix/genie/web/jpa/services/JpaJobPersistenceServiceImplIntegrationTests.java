/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.jpa.services;


import JobStatus.CLAIMED;
import JobStatus.FAILED;
import JobStatus.INIT;
import JobStatus.RESOLVED;
import JobStatus.RUNNING;
import JobStatus.SUCCEEDED;
import RandomSuppliers.INT;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.ClusterCriteria;
import com.netflix.genie.common.dto.Job;
import com.netflix.genie.common.dto.JobExecution;
import com.netflix.genie.common.dto.JobMetadata;
import com.netflix.genie.common.dto.JobMetadata.Builder;
import com.netflix.genie.common.dto.JobRequest;
import com.netflix.genie.common.dto.JobStatus;
import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.common.exceptions.GenieNotFoundException;
import com.netflix.genie.common.internal.dto.v4.AgentClientMetadata;
import com.netflix.genie.common.internal.dto.v4.JobRequestMetadata;
import com.netflix.genie.common.internal.dto.v4.JobSpecification;
import com.netflix.genie.common.internal.exceptions.unchecked.GenieIdAlreadyExistsException;
import com.netflix.genie.common.internal.exceptions.unchecked.GenieInvalidStatusException;
import com.netflix.genie.test.categories.IntegrationTest;
import com.netflix.genie.web.jpa.entities.JobEntity;
import com.netflix.genie.web.jpa.entities.projections.JobMetadataProjection;
import com.netflix.genie.web.services.ApplicationPersistenceService;
import com.netflix.genie.web.services.ClusterPersistenceService;
import com.netflix.genie.web.services.CommandPersistenceService;
import com.netflix.genie.web.services.JobPersistenceService;
import com.netflix.genie.web.services.JobSearchService;
import java.io.IOException;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Integration tests for {@link JpaJobPersistenceServiceImpl}.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(IntegrationTest.class)
@DatabaseSetup("JpaJobPersistenceServiceImplIntegrationTests/init.xml")
@DatabaseTearDown("cleanup.xml")
public class JpaJobPersistenceServiceImplIntegrationTests extends DBIntegrationTestBase {
    private static final String JOB_1_ID = "job1";

    private static final String JOB_2_ID = "job2";

    private static final String JOB_3_ID = "job3";

    // Job Request fields
    private static final String UNIQUE_ID = UUID.randomUUID().toString();

    private static final String NAME = UUID.randomUUID().toString();

    private static final String USER = UUID.randomUUID().toString();

    private static final String VERSION = UUID.randomUUID().toString();

    private static final String DESCRIPTION = UUID.randomUUID().toString();

    private static final List<String> COMMAND_ARGS = Lists.newArrayList(UUID.randomUUID().toString());

    private static final String GROUP = UUID.randomUUID().toString();

    private static final String SETUP_FILE = UUID.randomUUID().toString();

    private static final String CLUSTER_TAG_1 = UUID.randomUUID().toString();

    private static final String CLUSTER_TAG_2 = UUID.randomUUID().toString();

    private static final String CLUSTER_TAG_3 = UUID.randomUUID().toString();

    private static final List<ClusterCriteria> CLUSTER_CRITERIA = Lists.newArrayList(new ClusterCriteria(Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.CLUSTER_TAG_1)), new ClusterCriteria(Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.CLUSTER_TAG_2)), new ClusterCriteria(Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.CLUSTER_TAG_3)));

    private static final String COMMAND_TAG_1 = UUID.randomUUID().toString();

    private static final String COMMAND_TAG_2 = UUID.randomUUID().toString();

    private static final Set<String> COMMAND_CRITERION = Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.COMMAND_TAG_1, JpaJobPersistenceServiceImplIntegrationTests.COMMAND_TAG_2);

    private static final String CONFIG_1 = UUID.randomUUID().toString();

    private static final String CONFIG_2 = UUID.randomUUID().toString();

    private static final Set<String> CONFIGS = Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.CONFIG_1, JpaJobPersistenceServiceImplIntegrationTests.CONFIG_2);

    private static final String DEPENDENCY = UUID.randomUUID().toString();

    private static final Set<String> DEPENDENCIES = Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.DEPENDENCY);

    private static final String EMAIL = (((UUID.randomUUID().toString()) + "@") + (UUID.randomUUID().toString())) + ".com";

    private static final String TAG_1 = UUID.randomUUID().toString();

    private static final String TAG_2 = UUID.randomUUID().toString();

    private static final String TAG_3 = UUID.randomUUID().toString();

    private static final Set<String> TAGS = Sets.newHashSet(JpaJobPersistenceServiceImplIntegrationTests.TAG_1, JpaJobPersistenceServiceImplIntegrationTests.TAG_2, JpaJobPersistenceServiceImplIntegrationTests.TAG_3);

    private static final int CPU_REQUESTED = 2;

    private static final int MEMORY_REQUESTED = 1024;

    private static final String APP_REQUESTED_1 = UUID.randomUUID().toString();

    private static final String APP_REQUESTED_2 = UUID.randomUUID().toString();

    private static final List<String> APPLICATIONS_REQUESTED = Lists.newArrayList(JpaJobPersistenceServiceImplIntegrationTests.APP_REQUESTED_1, JpaJobPersistenceServiceImplIntegrationTests.APP_REQUESTED_2);

    private static final int TIMEOUT_REQUESTED = 84500;

    private static final String GROUPING = UUID.randomUUID().toString();

    private static final String GROUPING_INSTANCE = UUID.randomUUID().toString();

    // Job Metadata fields
    private static final String CLIENT_HOST = UUID.randomUUID().toString();

    private static final String USER_AGENT = UUID.randomUUID().toString();

    private static final int NUM_ATTACHMENTS = 3;

    private static final long TOTAL_SIZE_ATTACHMENTS = 38023423L;

    private static final long STD_ERR_SIZE = 98025245L;

    private static final long STD_OUT_SIZE = 78723423L;

    // Job Execution fields
    private static final String HOSTNAME = UUID.randomUUID().toString();

    private static final int PROCESS_ID = 3203;

    private static final long CHECK_DELAY = 8728L;

    private static final Instant TIMEOUT = Instant.now();

    private static final int MEMORY = 2048;

    // Job fields
    private static final String ARCHIVE_LOCATION = UUID.randomUUID().toString();

    private static final Instant FINISHED = Instant.now();

    private static final Instant STARTED = Instant.now();

    private static final JobStatus STATUS = JobStatus.RUNNING;

    private static final String STATUS_MSG = UUID.randomUUID().toString();

    @Autowired
    private JobPersistenceService jobPersistenceService;

    @Autowired
    private JobSearchService jobSearchService;

    @Autowired
    private ClusterPersistenceService clusterPersistenceService;

    @Autowired
    private CommandPersistenceService commandPersistenceService;

    @Autowired
    private ApplicationPersistenceService applicationPersistenceService;

    /**
     * Make sure we can delete jobs that were created before a given date.
     */
    @Test
    public void canDeleteJobsCreatedBeforeDateWithMinTransactionAndPageSize() {
        final Instant cal = ZonedDateTime.of(2016, Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        final long deleted = this.jobPersistenceService.deleteBatchOfJobsCreatedBeforeDate(cal, 1, 1);
        Assert.assertThat(deleted, Matchers.is(1L));
        Assert.assertThat(this.jobRepository.count(), Matchers.is(2L));
        Assert.assertTrue(this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID).isPresent());
    }

    /**
     * Make sure we can delete jobs that were created before a given date.
     */
    @Test
    public void canDeleteJobsCreatedBeforeDateWithPageLargerThanMax() {
        final Instant cal = ZonedDateTime.of(2016, Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        final long deleted = this.jobPersistenceService.deleteBatchOfJobsCreatedBeforeDate(cal, 1, 10);
        Assert.assertThat(deleted, Matchers.is(2L));
        Assert.assertThat(this.jobRepository.count(), Matchers.is(1L));
        Assert.assertTrue(this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID).isPresent());
    }

    /**
     * Make sure we can delete jobs that were created before a given date.
     */
    @Test
    public void canDeleteJobsCreatedBeforeDateWithMaxLargerThanPage() {
        final Instant cal = ZonedDateTime.of(2016, Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        final long deleted = this.jobPersistenceService.deleteBatchOfJobsCreatedBeforeDate(cal, 10, 1);
        Assert.assertThat(deleted, Matchers.is(2L));
        Assert.assertThat(this.jobRepository.count(), Matchers.is(1L));
        Assert.assertTrue(this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID).isPresent());
    }

    /**
     * Make sure we can delete jobs that were created before a given date.
     */
    @Test
    public void canDeleteJobsCreatedBeforeDateWithLargeTransactionAndPageSize() {
        final Instant cal = ZonedDateTime.of(2016, Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        final long deleted = this.jobPersistenceService.deleteBatchOfJobsCreatedBeforeDate(cal, 10000, 1);
        Assert.assertThat(deleted, Matchers.is(2L));
        Assert.assertThat(this.jobRepository.count(), Matchers.is(1L));
        Assert.assertTrue(this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID).isPresent());
    }

    /**
     * Make sure a job can be saved AND criterion are saved properly.
     *
     * @throws GenieException
     * 		on error
     */
    @Test
    public void canPersistAndGetAJob() throws GenieException {
        // Set up some fields for comparison
        final JobRequest jobRequest = withId(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID).withDescription(JpaJobPersistenceServiceImplIntegrationTests.DESCRIPTION).withCommandArgs(JpaJobPersistenceServiceImplIntegrationTests.COMMAND_ARGS).withGroup(JpaJobPersistenceServiceImplIntegrationTests.GROUP).withSetupFile(JpaJobPersistenceServiceImplIntegrationTests.SETUP_FILE).withConfigs(JpaJobPersistenceServiceImplIntegrationTests.CONFIGS).withDependencies(JpaJobPersistenceServiceImplIntegrationTests.DEPENDENCIES).withDisableLogArchival(true).withEmail(JpaJobPersistenceServiceImplIntegrationTests.EMAIL).withTags(JpaJobPersistenceServiceImplIntegrationTests.TAGS).withCpu(JpaJobPersistenceServiceImplIntegrationTests.CPU_REQUESTED).withMemory(JpaJobPersistenceServiceImplIntegrationTests.MEMORY_REQUESTED).withTimeout(JpaJobPersistenceServiceImplIntegrationTests.TIMEOUT_REQUESTED).withApplications(JpaJobPersistenceServiceImplIntegrationTests.APPLICATIONS_REQUESTED).withGrouping(JpaJobPersistenceServiceImplIntegrationTests.GROUPING).withGroupingInstance(JpaJobPersistenceServiceImplIntegrationTests.GROUPING_INSTANCE).build();
        final JobMetadata jobMetadata = new Builder().withClientHost(JpaJobPersistenceServiceImplIntegrationTests.CLIENT_HOST).withUserAgent(JpaJobPersistenceServiceImplIntegrationTests.USER_AGENT).withNumAttachments(JpaJobPersistenceServiceImplIntegrationTests.NUM_ATTACHMENTS).withTotalSizeOfAttachments(JpaJobPersistenceServiceImplIntegrationTests.TOTAL_SIZE_ATTACHMENTS).withStdErrSize(JpaJobPersistenceServiceImplIntegrationTests.STD_ERR_SIZE).withStdOutSize(JpaJobPersistenceServiceImplIntegrationTests.STD_OUT_SIZE).build();
        final Job.Builder jobBuilder = new Job.Builder(JpaJobPersistenceServiceImplIntegrationTests.NAME, JpaJobPersistenceServiceImplIntegrationTests.USER, JpaJobPersistenceServiceImplIntegrationTests.VERSION).withId(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID).withDescription(JpaJobPersistenceServiceImplIntegrationTests.DESCRIPTION).withTags(JpaJobPersistenceServiceImplIntegrationTests.TAGS).withArchiveLocation(JpaJobPersistenceServiceImplIntegrationTests.ARCHIVE_LOCATION).withStarted(JpaJobPersistenceServiceImplIntegrationTests.STARTED).withFinished(JpaJobPersistenceServiceImplIntegrationTests.FINISHED).withStatus(JpaJobPersistenceServiceImplIntegrationTests.STATUS).withStatusMsg(JpaJobPersistenceServiceImplIntegrationTests.STATUS_MSG);
        jobBuilder.withCommandArgs(JpaJobPersistenceServiceImplIntegrationTests.COMMAND_ARGS);
        final Job job = jobBuilder.build();
        final JobExecution jobExecution = new JobExecution.Builder(JpaJobPersistenceServiceImplIntegrationTests.HOSTNAME).withId(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID).withCheckDelay(JpaJobPersistenceServiceImplIntegrationTests.CHECK_DELAY).withTimeout(JpaJobPersistenceServiceImplIntegrationTests.TIMEOUT).withMemory(JpaJobPersistenceServiceImplIntegrationTests.MEMORY).withProcessId(JpaJobPersistenceServiceImplIntegrationTests.PROCESS_ID).build();
        Assert.assertThat(this.jobRepository.count(), Matchers.is(3L));
        Assert.assertThat(this.tagRepository.count(), Matchers.is(17L));
        Assert.assertThat(this.fileRepository.count(), Matchers.is(11L));
        this.jobPersistenceService.createJob(jobRequest, jobMetadata, job, jobExecution);
        Assert.assertThat(this.jobRepository.count(), Matchers.is(4L));
        Assert.assertThat(this.tagRepository.count(), Matchers.is(25L));
        Assert.assertThat(this.fileRepository.count(), Matchers.is(15L));
        Assert.assertFalse(this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID).orElseThrow(IllegalArgumentException::new).isV4());
        this.validateJobRequest(this.jobSearchService.getJobRequest(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID));
        this.validateJob(this.jobSearchService.getJob(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID));
        this.validateJobExecution(this.jobSearchService.getJobExecution(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID));
        final Optional<JobMetadataProjection> metadataProjection = this.jobRepository.findByUniqueId(JpaJobPersistenceServiceImplIntegrationTests.UNIQUE_ID, JobMetadataProjection.class);
        Assert.assertTrue(metadataProjection.isPresent());
        this.validateJobMetadata(metadataProjection.get());
    }

    /**
     * Test the V4 {@link JobPersistenceService#saveJobRequest(JobRequest, JobRequestMetadata)} method.
     *
     * @throws GenieException
     * 		on error
     * @throws IOException
     * 		on JSON error
     */
    @Test
    public void canSaveAndRetrieveJobRequest() throws GenieException, IOException {
        final String job0Id = UUID.randomUUID().toString();
        final String job3Id = UUID.randomUUID().toString();
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest0 = this.createJobRequest(job0Id, UUID.randomUUID().toString());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest1 = this.createJobRequest(null, UUID.randomUUID().toString());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest2 = this.createJobRequest(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID, UUID.randomUUID().toString());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest3 = this.createJobRequest(job3Id, null);
        final JobRequestMetadata jobRequestMetadata = this.createJobRequestMetadata();
        String id = this.jobPersistenceService.saveJobRequest(jobRequest0, jobRequestMetadata);
        Assert.assertThat(id, Matchers.is(job0Id));
        this.validateSavedJobRequest(id, jobRequest0, jobRequestMetadata);
        id = this.jobPersistenceService.saveJobRequest(jobRequest1, jobRequestMetadata);
        Assert.assertThat(id, Matchers.notNullValue());
        this.validateSavedJobRequest(id, jobRequest1, jobRequestMetadata);
        try {
            this.jobPersistenceService.saveJobRequest(jobRequest2, jobRequestMetadata);
            Assert.fail();
        } catch (final GenieIdAlreadyExistsException e) {
            // Expected
        }
        id = this.jobPersistenceService.saveJobRequest(jobRequest3, jobRequestMetadata);
        Assert.assertThat(id, Matchers.is(job3Id));
        this.validateSavedJobRequest(id, jobRequest3, jobRequestMetadata);
    }

    /**
     * Make sure saving and retrieving a job specification works as expected.
     *
     * @throws GenieException
     * 		on error
     * @throws IOException
     * 		on json error
     */
    @Test
    public void canSaveAndRetrieveJobSpecification() throws GenieException, IOException {
        final String jobId = this.jobPersistenceService.saveJobRequest(this.createJobRequest(null, UUID.randomUUID().toString()), this.createJobRequestMetadata());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest = this.jobPersistenceService.getJobRequest(jobId).orElseThrow(IllegalArgumentException::new);
        final JobSpecification jobSpecification = this.createJobSpecification(jobId, jobRequest, UUID.randomUUID().toString());
        this.jobPersistenceService.saveJobSpecification(jobId, jobSpecification);
        Assert.assertThat(this.jobPersistenceService.getJobSpecification(jobId).orElse(null), Matchers.is(jobSpecification));
        final String jobId2 = this.jobPersistenceService.saveJobRequest(this.createJobRequest(null, null), this.createJobRequestMetadata());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest2 = this.jobPersistenceService.getJobRequest(jobId2).orElseThrow(IllegalArgumentException::new);
        final JobSpecification jobSpecification2 = this.createJobSpecification(jobId2, jobRequest2, null);
        this.jobPersistenceService.saveJobSpecification(jobId2, jobSpecification2);
        Assert.assertThat(this.jobPersistenceService.getJobSpecification(jobId2).orElse(null), Matchers.is(jobSpecification2));
    }

    /**
     * Make sure {@link JpaJobPersistenceServiceImpl#claimJob(String, AgentClientMetadata)} works as expected.
     *
     * @throws GenieException
     * 		on error
     * @throws IOException
     * 		on json error
     */
    @Test
    public void canClaimJobAndUpdateStatus() throws GenieException, IOException {
        final String jobId = this.jobPersistenceService.saveJobRequest(this.createJobRequest(null, UUID.randomUUID().toString()), this.createJobRequestMetadata());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest = this.jobPersistenceService.getJobRequest(jobId).orElseThrow(IllegalArgumentException::new);
        final JobSpecification jobSpecification = this.createJobSpecification(jobId, jobRequest, UUID.randomUUID().toString());
        this.jobPersistenceService.saveJobSpecification(jobId, jobSpecification);
        final JobEntity preClaimedJob = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(preClaimedJob.getStatus(), Matchers.is(RESOLVED));
        Assert.assertTrue(preClaimedJob.isResolved());
        Assert.assertFalse(preClaimedJob.isClaimed());
        Assert.assertThat(preClaimedJob.getAgentHostname(), Matchers.is(Optional.empty()));
        Assert.assertThat(preClaimedJob.getAgentVersion(), Matchers.is(Optional.empty()));
        Assert.assertThat(preClaimedJob.getAgentPid(), Matchers.is(Optional.empty()));
        final String agentHostname = UUID.randomUUID().toString();
        final String agentVersion = UUID.randomUUID().toString();
        final int agentPid = INT.get();
        final AgentClientMetadata agentClientMetadata = new AgentClientMetadata(agentHostname, agentVersion, agentPid);
        this.jobPersistenceService.claimJob(jobId, agentClientMetadata);
        final JobEntity postClaimedJob = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(postClaimedJob.getStatus(), Matchers.is(CLAIMED));
        Assert.assertTrue(postClaimedJob.isResolved());
        Assert.assertTrue(postClaimedJob.isClaimed());
        Assert.assertThat(postClaimedJob.getAgentHostname(), Matchers.is(Optional.of(agentHostname)));
        Assert.assertThat(postClaimedJob.getAgentVersion(), Matchers.is(Optional.of(agentVersion)));
        Assert.assertThat(postClaimedJob.getAgentPid(), Matchers.is(Optional.of(agentPid)));
    }

    /**
     * Test the {@link JpaJobPersistenceServiceImpl#updateJobStatus(String, JobStatus, JobStatus, String)} method.
     *
     * @throws GenieException
     * 		on error
     * @throws IOException
     * 		on error
     */
    @Test
    public void canUpdateJobStatus() throws GenieException, IOException {
        final String jobId = this.jobPersistenceService.saveJobRequest(this.createJobRequest(null, UUID.randomUUID().toString()), this.createJobRequestMetadata());
        final com.netflix.genie.common.internal.dto.v4.JobRequest jobRequest = this.jobPersistenceService.getJobRequest(jobId).orElseThrow(IllegalArgumentException::new);
        final JobSpecification jobSpecification = this.createJobSpecification(jobId, jobRequest, UUID.randomUUID().toString());
        this.jobPersistenceService.saveJobSpecification(jobId, jobSpecification);
        final String agentHostname = UUID.randomUUID().toString();
        final String agentVersion = UUID.randomUUID().toString();
        final int agentPid = INT.get();
        final AgentClientMetadata agentClientMetadata = new AgentClientMetadata(agentHostname, agentVersion, agentPid);
        this.jobPersistenceService.claimJob(jobId, agentClientMetadata);
        JobEntity jobEntity = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(jobEntity.getStatus(), Matchers.is(CLAIMED));
        try {
            this.jobPersistenceService.updateJobStatus(jobId, RUNNING, FAILED, null);
            Assert.fail();
        } catch (final GenieInvalidStatusException e) {
            // status won't match so it will throw exception
        }
        final String initStatusMessage = "Job is initializing";
        this.jobPersistenceService.updateJobStatus(jobId, CLAIMED, INIT, initStatusMessage);
        jobEntity = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(jobEntity.getStatus(), Matchers.is(INIT));
        Assert.assertThat(jobEntity.getStatusMsg(), Matchers.is(Optional.of(initStatusMessage)));
        Assert.assertFalse(jobEntity.getStarted().isPresent());
        Assert.assertFalse(jobEntity.getFinished().isPresent());
        final String runningStatusMessage = "Job is running";
        this.jobPersistenceService.updateJobStatus(jobId, INIT, RUNNING, runningStatusMessage);
        jobEntity = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(jobEntity.getStatus(), Matchers.is(RUNNING));
        Assert.assertThat(jobEntity.getStatusMsg(), Matchers.is(Optional.of(runningStatusMessage)));
        Assert.assertTrue(jobEntity.getStarted().isPresent());
        Assert.assertFalse(jobEntity.getFinished().isPresent());
        final String successStatusMessage = "Job completed successfully";
        this.jobPersistenceService.updateJobStatus(jobId, RUNNING, SUCCEEDED, successStatusMessage);
        jobEntity = this.jobRepository.findByUniqueId(jobId).orElseThrow(IllegalArgumentException::new);
        Assert.assertThat(jobEntity.getStatus(), Matchers.is(SUCCEEDED));
        Assert.assertThat(jobEntity.getStatusMsg(), Matchers.is(Optional.of(successStatusMessage)));
        Assert.assertTrue(jobEntity.getStarted().isPresent());
        Assert.assertTrue(jobEntity.getFinished().isPresent());
    }

    /**
     * Test {@link JpaJobPersistenceServiceImpl#getJobStatus(String)}.
     *
     * @throws GenieNotFoundException
     * 		when the job doesn't exist but should
     */
    @Test
    public void canGetJobStatus() throws GenieNotFoundException {
        try {
            this.jobPersistenceService.getJobStatus(UUID.randomUUID().toString());
            Assert.fail("Should have thrown GenieNotFoundException");
        } catch (final GenieNotFoundException e) {
            // expected
        }
        Assert.assertThat(this.jobPersistenceService.getJobStatus(JpaJobPersistenceServiceImplIntegrationTests.JOB_1_ID), Matchers.is(SUCCEEDED));
        Assert.assertThat(this.jobPersistenceService.getJobStatus(JpaJobPersistenceServiceImplIntegrationTests.JOB_2_ID), Matchers.is(RUNNING));
        Assert.assertThat(this.jobPersistenceService.getJobStatus(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID), Matchers.is(RUNNING));
    }

    /**
     * Test {@link JpaJobPersistenceServiceImpl#getJobArchiveLocation(String)}.
     *
     * @throws GenieNotFoundException
     * 		if the test is broken
     */
    @Test
    public void canGetJobArchiveLocation() throws GenieNotFoundException {
        try {
            this.jobPersistenceService.getJobArchiveLocation(UUID.randomUUID().toString());
            Assert.fail();
        } catch (final GenieNotFoundException gnfe) {
            // expected
        }
        Assert.assertFalse(this.jobPersistenceService.getJobArchiveLocation(JpaJobPersistenceServiceImplIntegrationTests.JOB_3_ID).isPresent());
        Assert.assertThat(this.jobPersistenceService.getJobArchiveLocation(JpaJobPersistenceServiceImplIntegrationTests.JOB_1_ID).orElseThrow(IllegalStateException::new), Matchers.is("s3://somebucket/genie/logs/1/"));
    }
}

