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


import LegacySQLTypeName.DATETIME;
import QueryStatistics.StatementType;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationExtract;
import com.google.api.services.bigquery.model.JobConfigurationLoad;
import com.google.api.services.bigquery.model.JobConfigurationQuery;
import com.google.api.services.bigquery.model.JobConfigurationTableCopy;
import com.google.api.services.bigquery.model.JobStatistics;
import com.google.cloud.bigquery.JobStatistics.CopyStatistics;
import com.google.cloud.bigquery.JobStatistics.ExtractStatistics;
import com.google.cloud.bigquery.JobStatistics.LoadStatistics;
import com.google.cloud.bigquery.JobStatistics.QueryStatistics;
import com.google.cloud.bigquery.QueryStage.QueryStep;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JobStatisticsTest {
    private static final Integer BILLING_TIER = 42;

    private static final Boolean CACHE_HIT = true;

    private static final String DDL_OPERATION_PERFORMED = "SKIP";

    private static final TableId DDL_TARGET_TABLE = TableId.of("foo", "bar", "baz");

    private static final Long ESTIMATE_BYTES_PROCESSED = 101L;

    private static final Long NUM_DML_AFFECTED_ROWS = 88L;

    private static final StatementType STATEMENT_TYPE = StatementType.SELECT;

    private static final Long TOTAL_BYTES_BILLED = 24L;

    private static final Long TOTAL_BYTES_PROCESSED = 42L;

    private static final Long TOTAL_PARTITION_PROCESSED = 63L;

    private static final Long TOTAL_SLOT_MS = 10202L;

    private static final Long INPUT_BYTES = 1L;

    private static final Long INPUT_FILES = 2L;

    private static final Long OUTPUT_BYTES = 3L;

    private static final Long OUTPUT_ROWS = 4L;

    private static final Long BAD_RECORDS = 1L;

    private static final List<TableId> REFERENCED_TABLES = ImmutableList.of(TableId.of("foo", "bar", "table1"), TableId.of("foo", "bar", "table2"));

    private static final List<Long> FILE_COUNT = ImmutableList.of(1L, 2L, 3L);

    private static final Long CREATION_TIME = 10L;

    private static final Long END_TIME = 20L;

    private static final Long START_TIME = 15L;

    private static final CopyStatistics COPY_STATISTICS = CopyStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).build();

    private static final ExtractStatistics EXTRACT_STATISTICS = ExtractStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).setDestinationUriFileCounts(JobStatisticsTest.FILE_COUNT).build();

    private static final LoadStatistics LOAD_STATISTICS = LoadStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).setInputBytes(JobStatisticsTest.INPUT_BYTES).setInputFiles(JobStatisticsTest.INPUT_FILES).setOutputBytes(JobStatisticsTest.OUTPUT_BYTES).setOutputRows(JobStatisticsTest.OUTPUT_ROWS).setBadRecords(JobStatisticsTest.BAD_RECORDS).build();

    private static final LoadStatistics LOAD_STATISTICS_INCOMPLETE = LoadStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).setInputBytes(JobStatisticsTest.INPUT_BYTES).setInputFiles(JobStatisticsTest.INPUT_FILES).setBadRecords(JobStatisticsTest.BAD_RECORDS).build();

    private static final List<String> SUBSTEPS1 = ImmutableList.of("substep1", "substep2");

    private static final List<String> SUBSTEPS2 = ImmutableList.of("substep3", "substep4");

    private static final QueryStep QUERY_STEP1 = new QueryStep("KIND", JobStatisticsTest.SUBSTEPS1);

    private static final QueryStep QUERY_STEP2 = new QueryStep("KIND", JobStatisticsTest.SUBSTEPS2);

    private static final QueryStage QUERY_STAGE = QueryStage.newBuilder().setComputeRatioAvg(1.1).setComputeRatioMax(2.2).setGeneratedId(42L).setName("stage").setReadRatioAvg(3.3).setReadRatioMax(4.4).setRecordsRead(5L).setRecordsWritten(6L).setSteps(ImmutableList.of(JobStatisticsTest.QUERY_STEP1, JobStatisticsTest.QUERY_STEP2)).setWaitRatioAvg(7.7).setWaitRatioMax(8.8).setWriteRatioAvg(9.9).setWriteRatioMax(10.1).build();

    private static final TimelineSample TIMELINE_SAMPLE1 = TimelineSample.newBuilder().setElapsedMs(1001L).setActiveUnits(100L).setCompletedUnits(200L).setPendingUnits(50L).setSlotMillis(12345L).build();

    private static final TimelineSample TIMELINE_SAMPLE2 = TimelineSample.newBuilder().setElapsedMs(2002L).setActiveUnits(48L).setCompletedUnits(302L).setPendingUnits(0L).setSlotMillis(23456L).build();

    private static final List<TimelineSample> TIMELINE = ImmutableList.of(JobStatisticsTest.TIMELINE_SAMPLE1, JobStatisticsTest.TIMELINE_SAMPLE2);

    private static final List<QueryStage> QUERY_PLAN = ImmutableList.of(JobStatisticsTest.QUERY_STAGE);

    private static final Schema SCHEMA = Schema.of(Field.of("column", DATETIME));

    private static final QueryStatistics QUERY_STATISTICS = QueryStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).setBillingTier(JobStatisticsTest.BILLING_TIER).setCacheHit(JobStatisticsTest.CACHE_HIT).setDDLOperationPerformed(JobStatisticsTest.DDL_OPERATION_PERFORMED).setDDLTargetTable(JobStatisticsTest.DDL_TARGET_TABLE).setEstimatedBytesProcessed(JobStatisticsTest.ESTIMATE_BYTES_PROCESSED).setNumDmlAffectedRows(JobStatisticsTest.NUM_DML_AFFECTED_ROWS).setReferenceTables(JobStatisticsTest.REFERENCED_TABLES).setStatementType(JobStatisticsTest.STATEMENT_TYPE).setTotalBytesBilled(JobStatisticsTest.TOTAL_BYTES_BILLED).setTotalBytesProcessed(JobStatisticsTest.TOTAL_BYTES_PROCESSED).setTotalPartitionsProcessed(JobStatisticsTest.TOTAL_PARTITION_PROCESSED).setTotalSlotMs(JobStatisticsTest.TOTAL_SLOT_MS).setQueryPlan(JobStatisticsTest.QUERY_PLAN).setTimeline(JobStatisticsTest.TIMELINE).setSchema(JobStatisticsTest.SCHEMA).build();

    private static final QueryStatistics QUERY_STATISTICS_INCOMPLETE = QueryStatistics.newBuilder().setCreationTimestamp(JobStatisticsTest.CREATION_TIME).setEndTime(JobStatisticsTest.END_TIME).setStartTime(JobStatisticsTest.START_TIME).setBillingTier(JobStatisticsTest.BILLING_TIER).setCacheHit(JobStatisticsTest.CACHE_HIT).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(JobStatisticsTest.CREATION_TIME, JobStatisticsTest.EXTRACT_STATISTICS.getCreationTime());
        Assert.assertEquals(JobStatisticsTest.START_TIME, JobStatisticsTest.EXTRACT_STATISTICS.getStartTime());
        Assert.assertEquals(JobStatisticsTest.END_TIME, JobStatisticsTest.EXTRACT_STATISTICS.getEndTime());
        Assert.assertEquals(JobStatisticsTest.FILE_COUNT, JobStatisticsTest.EXTRACT_STATISTICS.getDestinationUriFileCounts());
        Assert.assertEquals(JobStatisticsTest.CREATION_TIME, JobStatisticsTest.LOAD_STATISTICS.getCreationTime());
        Assert.assertEquals(JobStatisticsTest.START_TIME, JobStatisticsTest.LOAD_STATISTICS.getStartTime());
        Assert.assertEquals(JobStatisticsTest.END_TIME, JobStatisticsTest.LOAD_STATISTICS.getEndTime());
        Assert.assertEquals(JobStatisticsTest.INPUT_BYTES, JobStatisticsTest.LOAD_STATISTICS.getInputBytes());
        Assert.assertEquals(JobStatisticsTest.INPUT_FILES, JobStatisticsTest.LOAD_STATISTICS.getInputFiles());
        Assert.assertEquals(JobStatisticsTest.OUTPUT_BYTES, JobStatisticsTest.LOAD_STATISTICS.getOutputBytes());
        Assert.assertEquals(JobStatisticsTest.OUTPUT_ROWS, JobStatisticsTest.LOAD_STATISTICS.getOutputRows());
        Assert.assertEquals(JobStatisticsTest.BAD_RECORDS, JobStatisticsTest.LOAD_STATISTICS.getBadRecords());
        Assert.assertEquals(JobStatisticsTest.CREATION_TIME, JobStatisticsTest.QUERY_STATISTICS.getCreationTime());
        Assert.assertEquals(JobStatisticsTest.START_TIME, JobStatisticsTest.QUERY_STATISTICS.getStartTime());
        Assert.assertEquals(JobStatisticsTest.END_TIME, JobStatisticsTest.QUERY_STATISTICS.getEndTime());
        Assert.assertEquals(JobStatisticsTest.BILLING_TIER, JobStatisticsTest.QUERY_STATISTICS.getBillingTier());
        Assert.assertEquals(JobStatisticsTest.CACHE_HIT, JobStatisticsTest.QUERY_STATISTICS.getCacheHit());
        Assert.assertEquals(JobStatisticsTest.DDL_OPERATION_PERFORMED, JobStatisticsTest.QUERY_STATISTICS.getDdlOperationPerformed());
        Assert.assertEquals(JobStatisticsTest.DDL_TARGET_TABLE, JobStatisticsTest.QUERY_STATISTICS.getDdlTargetTable());
        Assert.assertEquals(JobStatisticsTest.ESTIMATE_BYTES_PROCESSED, JobStatisticsTest.QUERY_STATISTICS.getEstimatedBytesProcessed());
        Assert.assertEquals(JobStatisticsTest.NUM_DML_AFFECTED_ROWS, JobStatisticsTest.QUERY_STATISTICS.getNumDmlAffectedRows());
        Assert.assertEquals(JobStatisticsTest.REFERENCED_TABLES, JobStatisticsTest.QUERY_STATISTICS.getReferencedTables());
        Assert.assertEquals(JobStatisticsTest.STATEMENT_TYPE, JobStatisticsTest.QUERY_STATISTICS.getStatementType());
        Assert.assertEquals(JobStatisticsTest.TOTAL_BYTES_BILLED, JobStatisticsTest.QUERY_STATISTICS.getTotalBytesBilled());
        Assert.assertEquals(JobStatisticsTest.TOTAL_BYTES_PROCESSED, JobStatisticsTest.QUERY_STATISTICS.getTotalBytesProcessed());
        Assert.assertEquals(JobStatisticsTest.TOTAL_PARTITION_PROCESSED, JobStatisticsTest.QUERY_STATISTICS.getTotalPartitionsProcessed());
        Assert.assertEquals(JobStatisticsTest.TOTAL_SLOT_MS, JobStatisticsTest.QUERY_STATISTICS.getTotalSlotMs());
        Assert.assertEquals(JobStatisticsTest.QUERY_PLAN, JobStatisticsTest.QUERY_STATISTICS.getQueryPlan());
        Assert.assertEquals(JobStatisticsTest.TIMELINE, JobStatisticsTest.QUERY_STATISTICS.getTimeline());
        Assert.assertEquals(JobStatisticsTest.CREATION_TIME, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getCreationTime());
        Assert.assertEquals(JobStatisticsTest.START_TIME, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getStartTime());
        Assert.assertEquals(JobStatisticsTest.END_TIME, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getEndTime());
        Assert.assertEquals(JobStatisticsTest.INPUT_BYTES, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getInputBytes());
        Assert.assertEquals(JobStatisticsTest.INPUT_FILES, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getInputFiles());
        Assert.assertEquals(JobStatisticsTest.BAD_RECORDS, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getBadRecords());
        Assert.assertEquals(null, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getOutputBytes());
        Assert.assertEquals(null, JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.getOutputRows());
        Assert.assertEquals(JobStatisticsTest.CREATION_TIME, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getCreationTime());
        Assert.assertEquals(JobStatisticsTest.START_TIME, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getStartTime());
        Assert.assertEquals(JobStatisticsTest.END_TIME, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getEndTime());
        Assert.assertEquals(JobStatisticsTest.BILLING_TIER, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getBillingTier());
        Assert.assertEquals(JobStatisticsTest.CACHE_HIT, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getCacheHit());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getDdlOperationPerformed());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getDdlTargetTable());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getEstimatedBytesProcessed());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getNumDmlAffectedRows());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getTotalBytesBilled());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getTotalBytesProcessed());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getTotalPartitionsProcessed());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getTotalSlotMs());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getReferencedTables());
        Assert.assertEquals(null, JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.getQueryPlan());
    }

    @Test
    public void testToPbAndFromPb() {
        compareExtractStatistics(JobStatisticsTest.EXTRACT_STATISTICS, ExtractStatistics.fromPb(JobStatisticsTest.EXTRACT_STATISTICS.toPb()));
        compareLoadStatistics(JobStatisticsTest.LOAD_STATISTICS, LoadStatistics.fromPb(JobStatisticsTest.LOAD_STATISTICS.toPb()));
        compareQueryStatistics(JobStatisticsTest.QUERY_STATISTICS, QueryStatistics.fromPb(JobStatisticsTest.QUERY_STATISTICS.toPb()));
        compareStatistics(JobStatisticsTest.COPY_STATISTICS, CopyStatistics.fromPb(JobStatisticsTest.COPY_STATISTICS.toPb()));
        compareLoadStatistics(JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE, LoadStatistics.fromPb(JobStatisticsTest.LOAD_STATISTICS_INCOMPLETE.toPb()));
        compareQueryStatistics(JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE, QueryStatistics.fromPb(JobStatisticsTest.QUERY_STATISTICS_INCOMPLETE.toPb()));
    }

    @Test
    public void testIncomplete() {
        // https://github.com/googleapis/google-cloud-java/issues/2357
        Job job = new Job().setStatistics(new JobStatistics().setCreationTime(1234L).setStartTime(5678L));
        job.setConfiguration(new JobConfiguration().setCopy(new JobConfigurationTableCopy()));
        assertThat(JobStatistics.fromPb(job)).isInstanceOf(CopyStatistics.class);
        job.setConfiguration(new JobConfiguration().setLoad(new JobConfigurationLoad()));
        assertThat(JobStatistics.fromPb(job)).isInstanceOf(LoadStatistics.class);
        job.setConfiguration(new JobConfiguration().setExtract(new JobConfigurationExtract()));
        assertThat(JobStatistics.fromPb(job)).isInstanceOf(ExtractStatistics.class);
        job.setConfiguration(new JobConfiguration().setQuery(new JobConfigurationQuery()));
        assertThat(JobStatistics.fromPb(job)).isInstanceOf(QueryStatistics.class);
    }
}

