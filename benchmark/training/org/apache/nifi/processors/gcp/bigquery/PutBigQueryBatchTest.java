/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.bigquery;


import JobInfo.CreateDisposition.CREATE_IF_NEEDED;
import JobInfo.WriteDisposition.WRITE_EMPTY;
import PutBigQueryBatch.REL_FAILURE;
import PutBigQueryBatch.REL_SUCCESS;
import com.google.cloud.RetryOption;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatistics;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link PutBigQueryBatch}.
 */
public class PutBigQueryBatchTest extends AbstractBQTest {
    private static final String TABLENAME = "test_table";

    private static final String TABLE_SCHEMA = "[{ \"mode\": \"NULLABLE\", \"name\": \"data\", \"type\": \"STRING\" }]";

    private static final String SOURCE_TYPE = FormatOptions.json().getType();

    private static final String CREATE_DISPOSITION = CREATE_IF_NEEDED.name();

    private static final String WRITE_DISPOSITION = WRITE_EMPTY.name();

    private static final String MAXBAD_RECORDS = "0";

    private static final String IGNORE_UNKNOWN = "true";

    private static final String READ_TIMEOUT = "5 minutes";

    @Mock
    BigQuery bq;

    @Mock
    Table table;

    @Mock
    Job job;

    @Mock
    JobStatus jobStatus;

    @Mock
    JobStatistics stats;

    @Mock
    TableDataWriteChannel tableDataWriteChannel;

    @Test
    public void testSuccessfulLoad() throws Exception {
        Mockito.when(table.exists()).thenReturn(Boolean.TRUE);
        Mockito.when(bq.create(ArgumentMatchers.isA(JobInfo.class))).thenReturn(job);
        Mockito.when(bq.writer(ArgumentMatchers.isA(WriteChannelConfiguration.class))).thenReturn(tableDataWriteChannel);
        Mockito.when(tableDataWriteChannel.getJob()).thenReturn(job);
        Mockito.when(job.waitFor(ArgumentMatchers.isA(RetryOption.class))).thenReturn(job);
        Mockito.when(job.getStatus()).thenReturn(jobStatus);
        Mockito.when(job.getStatistics()).thenReturn(stats);
        Mockito.when(stats.getCreationTime()).thenReturn(0L);
        Mockito.when(stats.getStartTime()).thenReturn(1L);
        Mockito.when(stats.getEndTime()).thenReturn(2L);
        final TestRunner runner = AbstractBQTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        runner.enqueue("{ \"data\": \"datavalue\" }");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
    }

    @Test
    public void testFailedLoad() throws Exception {
        Mockito.when(table.exists()).thenReturn(Boolean.TRUE);
        Mockito.when(bq.create(ArgumentMatchers.isA(JobInfo.class))).thenReturn(job);
        Mockito.when(bq.writer(ArgumentMatchers.isA(WriteChannelConfiguration.class))).thenReturn(tableDataWriteChannel);
        Mockito.when(tableDataWriteChannel.getJob()).thenReturn(job);
        Mockito.when(job.waitFor(ArgumentMatchers.isA(RetryOption.class))).thenThrow(BigQueryException.class);
        Mockito.when(job.getStatus()).thenReturn(jobStatus);
        Mockito.when(job.getStatistics()).thenReturn(stats);
        Mockito.when(stats.getCreationTime()).thenReturn(0L);
        Mockito.when(stats.getStartTime()).thenReturn(1L);
        Mockito.when(stats.getEndTime()).thenReturn(2L);
        final TestRunner runner = AbstractBQTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        runner.enqueue("{ \"data\": \"datavalue\" }");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }
}

