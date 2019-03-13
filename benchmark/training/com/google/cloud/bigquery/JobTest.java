/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import BigQuery.JobField.STATUS;
import BigQuery.JobOption;
import Job.DEFAULT_QUERY_WAIT_OPTIONS;
import JobStatus.State;
import JobStatus.State.DONE;
import JobStatus.State.RUNNING;
import LegacySQLTypeName.BOOLEAN;
import LegacySQLTypeName.INTEGER;
import com.google.api.core.ApiClock;
import com.google.api.core.CurrentMillisClock;
import com.google.api.gax.paging.Page;
import com.google.cloud.RetryOption;
import com.google.cloud.bigquery.JobStatistics.CopyStatistics;
import com.google.cloud.bigquery.JobStatistics.QueryStatistics;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.threeten.bp.Duration;


public class JobTest {
    private static final JobId JOB_ID = JobId.of("project", "job");

    private static final TableId TABLE_ID1 = TableId.of("dataset", "table1");

    private static final TableId TABLE_ID2 = TableId.of("dataset", "table2");

    private static final String ETAG = "etag";

    private static final String GENERATED_ID = "id";

    private static final String SELF_LINK = "selfLink";

    private static final String EMAIL = "email";

    private static final JobStatus JOB_STATUS = new JobStatus(State.DONE);

    private static final JobStatistics COPY_JOB_STATISTICS = CopyStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).build();

    private static final CopyJobConfiguration COPY_CONFIGURATION = CopyJobConfiguration.of(JobTest.TABLE_ID1, JobTest.TABLE_ID2);

    private static final JobInfo JOB_INFO = JobInfo.newBuilder(JobTest.COPY_CONFIGURATION).setJobId(JobTest.JOB_ID).setStatistics(JobTest.COPY_JOB_STATISTICS).setJobId(JobTest.JOB_ID).setEtag(JobTest.ETAG).setGeneratedId(JobTest.GENERATED_ID).setSelfLink(JobTest.SELF_LINK).setUserEmail(JobTest.EMAIL).setStatus(JobTest.JOB_STATUS).build();

    private static final RetryOption[] TEST_RETRY_OPTIONS = new RetryOption[]{ RetryOption.totalTimeout(Duration.ofSeconds(3)), RetryOption.initialRetryDelay(Duration.ofMillis(1L)), RetryOption.jittered(false), RetryOption.retryDelayMultiplier(1.0) };

    private BigQuery serviceMockReturnsOptions = createStrictMock(BigQuery.class);

    private BigQueryOptions mockOptions = createMock(BigQueryOptions.class);

    private BigQuery bigquery;

    private Job expectedJob;

    private Job job;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBuilder() {
        initializeExpectedJob(2);
        replay(bigquery);
        Job builtJob = new Job.Builder(serviceMockReturnsOptions, JobTest.COPY_CONFIGURATION).setJobId(JobTest.JOB_ID).setStatistics(JobTest.COPY_JOB_STATISTICS).setJobId(JobTest.JOB_ID).setEtag(JobTest.ETAG).setGeneratedId(JobTest.GENERATED_ID).setSelfLink(JobTest.SELF_LINK).setUserEmail(JobTest.EMAIL).setStatus(JobTest.JOB_STATUS).build();
        Assert.assertEquals(JobTest.ETAG, builtJob.getEtag());
        Assert.assertEquals(JobTest.GENERATED_ID, builtJob.getGeneratedId());
        Assert.assertEquals(JobTest.SELF_LINK, builtJob.getSelfLink());
        Assert.assertEquals(JobTest.EMAIL, builtJob.getUserEmail());
        Assert.assertEquals(JobTest.JOB_ID, builtJob.getJobId());
        Assert.assertEquals(JobTest.JOB_STATUS, builtJob.getStatus());
        Assert.assertEquals(JobTest.COPY_CONFIGURATION, builtJob.getConfiguration());
        Assert.assertEquals(JobTest.COPY_JOB_STATISTICS, builtJob.getStatistics());
        Assert.assertSame(serviceMockReturnsOptions, builtJob.getBigQuery());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedJob(4);
        replay(bigquery);
        compareJob(expectedJob, expectedJob.toBuilder().build());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedJob(1);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(expectedJob);
        replay(bigquery);
        initializeJob();
        Assert.assertTrue(job.exists());
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedJob(1);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(null);
        replay(bigquery);
        initializeJob();
        Assert.assertFalse(job.exists());
    }

    @Test
    public void testIsDone_True() throws Exception {
        initializeExpectedJob(2);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        JobStatus status = createStrictMock(JobStatus.class);
        expect(status.getState()).andReturn(DONE);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(expectedJob.toBuilder().setStatus(status).build());
        replay(status, bigquery);
        initializeJob();
        Assert.assertTrue(job.isDone());
        verify(status);
    }

    @Test
    public void testIsDone_False() throws Exception {
        initializeExpectedJob(2);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        JobStatus status = createStrictMock(JobStatus.class);
        expect(status.getState()).andReturn(RUNNING);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(expectedJob.toBuilder().setStatus(status).build());
        replay(status, bigquery);
        initializeJob();
        Assert.assertFalse(job.isDone());
        verify(status);
    }

    @Test
    public void testIsDone_NotExists() throws Exception {
        initializeExpectedJob(1);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(null);
        replay(bigquery);
        initializeJob();
        Assert.assertTrue(job.isDone());
    }

    @Test
    public void testWaitFor() throws InterruptedException {
        initializeExpectedJob(2);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        JobStatus status = createStrictMock(JobStatus.class);
        expect(status.getState()).andReturn(DONE);
        // caused by optimizing extra call to ResultRetryAlgorithm#shouldRetry()
        expect(status.getState()).andReturn(DONE);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        Job completedJob = expectedJob.toBuilder().setStatus(status).build();
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(completedJob);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(completedJob);
        replay(status, bigquery, mockOptions);
        initializeJob();
        Assert.assertSame(completedJob, job.waitFor(JobTest.TEST_RETRY_OPTIONS));
        verify(status, mockOptions);
    }

    @Test
    public void testWaitForAndGetQueryResultsEmpty() throws InterruptedException {
        QueryJobConfiguration jobConfig = QueryJobConfiguration.newBuilder("CREATE VIEW").setDestinationTable(JobTest.TABLE_ID1).build();
        QueryStatistics jobStatistics = QueryStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).build();
        JobInfo jobInfo = JobInfo.newBuilder(jobConfig).setJobId(JobTest.JOB_ID).setStatistics(jobStatistics).setJobId(JobTest.JOB_ID).setEtag(JobTest.ETAG).setGeneratedId(JobTest.GENERATED_ID).setSelfLink(JobTest.SELF_LINK).setUserEmail(JobTest.EMAIL).setStatus(JobTest.JOB_STATUS).build();
        initializeExpectedJob(2, jobInfo);
        JobStatus status = createStrictMock(JobStatus.class);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock()).times(2);
        Job completedJob = expectedJob.toBuilder().setStatus(status).build();
        // TODO(pongad): remove when we bump gax to 1.15.
        Page<FieldValueList> emptyPage = new Page<FieldValueList>() {
            @Override
            public boolean hasNextPage() {
                return false;
            }

            @Override
            public String getNextPageToken() {
                return "";
            }

            @Override
            public Page<FieldValueList> getNextPage() {
                return null;
            }

            @Override
            public Iterable<FieldValueList> iterateAll() {
                return Collections.emptyList();
            }

            @Override
            public Iterable<FieldValueList> getValues() {
                return Collections.emptyList();
            }
        };
        QueryResponse completedQuery = QueryResponse.newBuilder().setCompleted(true).setTotalRows(0).setSchema(Schema.of()).setErrors(ImmutableList.<BigQueryError>of()).build();
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(completedJob);
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        replay(status, bigquery, mockOptions);
        initializeJob(jobInfo);
        assertThat(job.waitFor(JobTest.TEST_RETRY_OPTIONS)).isSameAs(completedJob);
        assertThat(job.getQueryResults().iterateAll()).isEmpty();
        verify(status, mockOptions);
    }

    @Test
    public void testWaitForAndGetQueryResultsEmptyWithSchema() throws InterruptedException {
        QueryJobConfiguration jobConfig = QueryJobConfiguration.newBuilder("CREATE VIEW").setDestinationTable(JobTest.TABLE_ID1).build();
        QueryStatistics jobStatistics = QueryStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).build();
        JobInfo jobInfo = JobInfo.newBuilder(jobConfig).setJobId(JobTest.JOB_ID).setStatistics(jobStatistics).setJobId(JobTest.JOB_ID).setEtag(JobTest.ETAG).setGeneratedId(JobTest.GENERATED_ID).setSelfLink(JobTest.SELF_LINK).setUserEmail(JobTest.EMAIL).setStatus(JobTest.JOB_STATUS).build();
        initializeExpectedJob(2, jobInfo);
        JobStatus status = createStrictMock(JobStatus.class);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock()).times(2);
        Job completedJob = expectedJob.toBuilder().setStatus(status).build();
        QueryResponse completedQuery = QueryResponse.newBuilder().setCompleted(true).setTotalRows(0).setSchema(Schema.of(Field.of("field1", BOOLEAN))).setErrors(ImmutableList.<BigQueryError>of()).build();
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(completedJob);
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        replay(status, bigquery, mockOptions);
        initializeJob(jobInfo);
        assertThat(job.waitFor(JobTest.TEST_RETRY_OPTIONS)).isSameAs(completedJob);
        assertThat(job.getQueryResults().getSchema()).isEqualTo(Schema.of(Field.of("field1", BOOLEAN)));
        verify(status, mockOptions);
    }

    @Test
    public void testWaitForAndGetQueryResults() throws InterruptedException {
        QueryJobConfiguration jobConfig = QueryJobConfiguration.newBuilder("SELECT 1").setDestinationTable(JobTest.TABLE_ID1).build();
        QueryStatistics jobStatistics = QueryStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).build();
        JobInfo jobInfo = JobInfo.newBuilder(jobConfig).setJobId(JobTest.JOB_ID).setStatistics(jobStatistics).setJobId(JobTest.JOB_ID).setEtag(JobTest.ETAG).setGeneratedId(JobTest.GENERATED_ID).setSelfLink(JobTest.SELF_LINK).setUserEmail(JobTest.EMAIL).setStatus(JobTest.JOB_STATUS).build();
        initializeExpectedJob(2, jobInfo);
        JobStatus status = createStrictMock(JobStatus.class);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock()).times(2);
        Job completedJob = expectedJob.toBuilder().setStatus(status).build();
        // TODO(pongad): remove when we bump gax to 1.15.
        Page<FieldValueList> singlePage = new Page<FieldValueList>() {
            @Override
            public boolean hasNextPage() {
                return false;
            }

            @Override
            public String getNextPageToken() {
                return "";
            }

            @Override
            public Page<FieldValueList> getNextPage() {
                return null;
            }

            @Override
            public Iterable<FieldValueList> iterateAll() {
                return Collections.emptyList();
            }

            @Override
            public Iterable<FieldValueList> getValues() {
                return Collections.emptyList();
            }
        };
        TableResult result = new TableResult(Schema.of(), 1, singlePage);
        QueryResponse completedQuery = // Lies to force call of listTableData().
        QueryResponse.newBuilder().setCompleted(true).setTotalRows(1).setSchema(Schema.of(Field.of("_f0", INTEGER))).setErrors(ImmutableList.<BigQueryError>of()).build();
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(completedJob);
        expect(bigquery.getQueryResults(jobInfo.getJobId(), DEFAULT_QUERY_WAIT_OPTIONS)).andReturn(completedQuery);
        expect(bigquery.listTableData(eq(JobTest.TABLE_ID1), anyObject(Schema.class))).andReturn(result);
        replay(status, bigquery, mockOptions);
        initializeJob(jobInfo);
        assertThat(job.waitFor(JobTest.TEST_RETRY_OPTIONS)).isSameAs(completedJob);
        assertThat(job.getQueryResults().iterateAll()).hasSize(0);
        verify(status, mockOptions);
    }

    @Test
    public void testWaitForAndGetQueryResults_Unsupported() throws InterruptedException {
        initializeExpectedJob(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        replay(bigquery, mockOptions);
        initializeJob();
        thrown.expect(UnsupportedOperationException.class);
        job.getQueryResults();
    }

    @Test
    public void testWaitFor_Null() throws InterruptedException {
        initializeExpectedJob(1);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(null);
        replay(bigquery, mockOptions);
        initializeJob();
        Assert.assertNull(job.waitFor(JobTest.TEST_RETRY_OPTIONS));
        verify(mockOptions);
    }

    @Test
    public void testWaitForWithCheckingPeriod() throws InterruptedException {
        initializeExpectedJob(3);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        JobStatus status = createStrictMock(JobStatus.class);
        expect(status.getState()).andReturn(RUNNING);
        // caused by optimizing extra call to ResultRetryAlgorithm#shouldRetry()
        expect(status.getState()).andReturn(RUNNING);
        expect(status.getState()).andReturn(DONE);
        // caused by optimizing extra call to ResultRetryAlgorithm#shouldRetry()
        expect(status.getState()).andReturn(DONE);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        Job runningJob = expectedJob.toBuilder().setStatus(status).build();
        Job completedJob = expectedJob.toBuilder().setStatus(status).build();
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(runningJob);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(completedJob);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(completedJob);
        replay(status, bigquery, mockOptions);
        initializeJob();
        Assert.assertSame(completedJob, job.waitFor(JobTest.TEST_RETRY_OPTIONS));
        verify(status, mockOptions);
    }

    @Test
    public void testWaitForWithCheckingPeriod_Null() throws InterruptedException {
        initializeExpectedJob(2);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        Job runningJob = expectedJob.toBuilder().setStatus(new JobStatus(State.RUNNING)).build();
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(runningJob);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(null);
        replay(bigquery, mockOptions);
        initializeJob();
        Assert.assertNull(job.waitFor(JobTest.TEST_RETRY_OPTIONS));
        verify(bigquery, mockOptions);
    }

    @Test
    public void testWaitForWithTimeout() throws InterruptedException {
        initializeExpectedJob(2);
        BigQuery[] expectedOptions = new JobOption[]{ JobOption.fields(STATUS) };
        ApiClock clock = createStrictMock(ApiClock.class);
        expect(clock.nanoTime()).andReturn(0L);
        expect(clock.nanoTime()).andReturn(1000000L);
        expect(clock.nanoTime()).andReturn(3000000L);
        JobStatus status = createStrictMock(JobStatus.class);
        expect(status.getState()).andReturn(RUNNING);
        expect(status.getState()).andReturn(RUNNING);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(clock);
        Job runningJob = expectedJob.toBuilder().setStatus(status).build();
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(runningJob);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), expectedOptions)).andReturn(runningJob);
        replay(status, bigquery, clock, mockOptions);
        initializeJob();
        thrown.expect(BigQueryException.class);
        job.waitFor(ObjectArrays.concat(JobTest.TEST_RETRY_OPTIONS, RetryOption.totalTimeout(Duration.ofMillis(3))));
        verify(status, clock, mockOptions);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedJob(4);
        JobInfo updatedInfo = JobTest.JOB_INFO.toBuilder().setEtag("etag").build();
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(expectedJob);
        replay(bigquery);
        initializeJob();
        Job updatedJob = job.reload();
        compareJob(expectedJob, updatedJob);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedJob(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId())).andReturn(null);
        replay(bigquery);
        initializeJob();
        Assert.assertNull(job.reload());
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedJob(4);
        JobInfo updatedInfo = JobTest.JOB_INFO.toBuilder().setEtag("etag").build();
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getJob(JobTest.JOB_INFO.getJobId(), JobOption.fields())).andReturn(expectedJob);
        replay(bigquery);
        initializeJob();
        Job updatedJob = job.reload(JobOption.fields());
        compareJob(expectedJob, updatedJob);
    }

    @Test
    public void testCancel() throws Exception {
        initializeExpectedJob(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.cancel(JobTest.JOB_INFO.getJobId())).andReturn(true);
        replay(bigquery);
        initializeJob();
        Assert.assertTrue(job.cancel());
    }

    @Test
    public void testBigQuery() {
        initializeExpectedJob(1);
        replay(bigquery);
        Assert.assertSame(serviceMockReturnsOptions, expectedJob.getBigQuery());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedJob(4);
        replay(bigquery);
        compareJob(expectedJob, Job.fromPb(serviceMockReturnsOptions, expectedJob.toPb()));
    }

    @Test
    public void testToAndFromPbWithoutConfiguration() {
        JobInfo jobInfo = JobInfo.newBuilder(null).build();
        initializeExpectedJob(4, jobInfo);
        replay(bigquery);
        compareJob(expectedJob, Job.fromPb(serviceMockReturnsOptions, expectedJob.toPb()));
    }
}

