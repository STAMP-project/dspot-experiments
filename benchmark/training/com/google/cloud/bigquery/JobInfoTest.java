/**
 * Copyright 2016 Google LLC
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


import Field.Mode.NULLABLE;
import Field.Mode.REPEATED;
import Field.Mode.REQUIRED;
import JobStatus.State;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import QueryJobConfiguration.Priority;
import SchemaUpdateOption.ALLOW_FIELD_ADDITION;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.SchemaUpdateOption;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.JobStatistics.CopyStatistics;
import com.google.cloud.bigquery.JobStatistics.ExtractStatistics;
import com.google.cloud.bigquery.JobStatistics.LoadStatistics;
import com.google.cloud.bigquery.JobStatistics.QueryStatistics;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JobInfoTest {
    private static final String ETAG = "etag";

    private static final String GENERATED_ID = "id";

    private static final String SELF_LINK = "selfLink";

    private static final String EMAIL = "email";

    private static final JobId JOB_ID = JobId.of("job");

    private static final JobStatus JOB_STATUS = new JobStatus(State.DONE);

    private static final CopyStatistics COPY_JOB_STATISTICS = CopyStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).build();

    private static final ExtractStatistics EXTRACT_JOB_STATISTICS = ExtractStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).setDestinationUriFileCounts(ImmutableList.of(42L)).build();

    private static final LoadStatistics LOAD_JOB_STATISTICS = LoadStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).setInputFiles(42L).setOutputBytes(1024L).setInputBytes(2048L).setOutputRows(24L).build();

    private static final QueryStatistics QUERY_JOB_STATISTICS = QueryStatistics.newBuilder().setCreationTimestamp(1L).setEndTime(3L).setStartTime(2L).setTotalBytesProcessed(2048L).setTotalBytesBilled(1024L).setCacheHit(false).setBillingTier(42).build();

    private static final TableId SOURCE_TABLE = TableId.of("dataset", "sourceTable");

    private static final TableId DESTINATION_TABLE = TableId.of("dataset", "destinationTable");

    private static final CreateDisposition CREATE_DISPOSITION = CreateDisposition.CREATE_IF_NEEDED;

    private static final WriteDisposition WRITE_DISPOSITION = WriteDisposition.WRITE_APPEND;

    private static final CopyJobConfiguration COPY_CONFIGURATION = CopyJobConfiguration.newBuilder(JobInfoTest.DESTINATION_TABLE, JobInfoTest.SOURCE_TABLE).setCreateDisposition(JobInfoTest.CREATE_DISPOSITION).setWriteDisposition(JobInfoTest.WRITE_DISPOSITION).build();

    private static final List<String> DESTINATION_URIS = ImmutableList.of("uri1", "uri2");

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final DatasetId DATASET_ID = DatasetId.of("dataset");

    private static final List<String> SOURCE_URIS = ImmutableList.of("uri1", "uri2");

    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, JobInfoTest.FIELD_SCHEMA1, JobInfoTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final Schema TABLE_SCHEMA = Schema.of(JobInfoTest.FIELD_SCHEMA1, JobInfoTest.FIELD_SCHEMA2, JobInfoTest.FIELD_SCHEMA3);

    private static final String FIELD_DELIMITER = ",";

    private static final String FORMAT = "CSV";

    private static final Boolean PRINT_HEADER = true;

    private static final String COMPRESSION = "GZIP";

    private static final ExtractJobConfiguration EXTRACT_CONFIGURATION = ExtractJobConfiguration.newBuilder(JobInfoTest.TABLE_ID, JobInfoTest.DESTINATION_URIS).setPrintHeader(JobInfoTest.PRINT_HEADER).setFieldDelimiter(JobInfoTest.FIELD_DELIMITER).setCompression(JobInfoTest.COMPRESSION).setFormat(JobInfoTest.FORMAT).build();

    private static final List<String> PROJECTION_FIELDS = ImmutableList.of("field1", "field2");

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().build();

    private static final List<SchemaUpdateOption> SCHEMA_UPDATE_OPTIONS = ImmutableList.of(ALLOW_FIELD_ADDITION);

    private static final ExternalTableDefinition TABLE_CONFIGURATION = ExternalTableDefinition.newBuilder(JobInfoTest.SOURCE_URIS, JobInfoTest.TABLE_SCHEMA, JobInfoTest.CSV_OPTIONS).setCompression(JobInfoTest.COMPRESSION).setIgnoreUnknownValues(JobInfoTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(JobInfoTest.MAX_BAD_RECORDS).build();

    private static final LoadJobConfiguration LOAD_CONFIGURATION = LoadJobConfiguration.newBuilder(JobInfoTest.TABLE_ID, JobInfoTest.SOURCE_URIS).setCreateDisposition(JobInfoTest.CREATE_DISPOSITION).setWriteDisposition(JobInfoTest.WRITE_DISPOSITION).setFormatOptions(JobInfoTest.CSV_OPTIONS).setIgnoreUnknownValues(JobInfoTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(JobInfoTest.MAX_BAD_RECORDS).setSchema(JobInfoTest.TABLE_SCHEMA).setSchemaUpdateOptions(JobInfoTest.SCHEMA_UPDATE_OPTIONS).build();

    private static final String QUERY = "BigQuery SQL";

    private static final Map<String, ExternalTableDefinition> TABLE_DEFINITIONS = ImmutableMap.of("tableName", JobInfoTest.TABLE_CONFIGURATION);

    private static final Priority PRIORITY = Priority.BATCH;

    private static final boolean ALLOW_LARGE_RESULTS = true;

    private static final boolean USE_QUERY_CACHE = false;

    private static final boolean FLATTEN_RESULTS = true;

    private static final List<UserDefinedFunction> USER_DEFINED_FUNCTIONS = ImmutableList.of(UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI"));

    private static final QueryJobConfiguration QUERY_CONFIGURATION = QueryJobConfiguration.newBuilder(JobInfoTest.QUERY).setUseQueryCache(JobInfoTest.USE_QUERY_CACHE).setTableDefinitions(JobInfoTest.TABLE_DEFINITIONS).setAllowLargeResults(JobInfoTest.ALLOW_LARGE_RESULTS).setCreateDisposition(JobInfoTest.CREATE_DISPOSITION).setDefaultDataset(JobInfoTest.DATASET_ID).setDestinationTable(JobInfoTest.TABLE_ID).setWriteDisposition(JobInfoTest.WRITE_DISPOSITION).setPriority(JobInfoTest.PRIORITY).setFlattenResults(JobInfoTest.FLATTEN_RESULTS).setUserDefinedFunctions(JobInfoTest.USER_DEFINED_FUNCTIONS).setDryRun(true).setSchemaUpdateOptions(JobInfoTest.SCHEMA_UPDATE_OPTIONS).build();

    private static final JobInfo COPY_JOB = JobInfo.newBuilder(JobInfoTest.COPY_CONFIGURATION).setJobId(JobInfoTest.JOB_ID).setStatistics(JobInfoTest.COPY_JOB_STATISTICS).setJobId(JobInfoTest.JOB_ID).setEtag(JobInfoTest.ETAG).setGeneratedId(JobInfoTest.GENERATED_ID).setSelfLink(JobInfoTest.SELF_LINK).setUserEmail(JobInfoTest.EMAIL).setStatus(JobInfoTest.JOB_STATUS).build();

    private static final JobInfo EXTRACT_JOB = JobInfo.newBuilder(JobInfoTest.EXTRACT_CONFIGURATION).setJobId(JobInfoTest.JOB_ID).setStatistics(JobInfoTest.EXTRACT_JOB_STATISTICS).setJobId(JobInfoTest.JOB_ID).setEtag(JobInfoTest.ETAG).setGeneratedId(JobInfoTest.GENERATED_ID).setSelfLink(JobInfoTest.SELF_LINK).setUserEmail(JobInfoTest.EMAIL).setStatus(JobInfoTest.JOB_STATUS).build();

    private static final JobInfo LOAD_JOB = JobInfo.newBuilder(JobInfoTest.LOAD_CONFIGURATION).setJobId(JobInfoTest.JOB_ID).setStatistics(JobInfoTest.LOAD_JOB_STATISTICS).setJobId(JobInfoTest.JOB_ID).setEtag(JobInfoTest.ETAG).setGeneratedId(JobInfoTest.GENERATED_ID).setSelfLink(JobInfoTest.SELF_LINK).setUserEmail(JobInfoTest.EMAIL).setStatus(JobInfoTest.JOB_STATUS).build();

    private static final JobInfo QUERY_JOB = JobInfo.newBuilder(JobInfoTest.QUERY_CONFIGURATION).setJobId(JobInfoTest.JOB_ID).setStatistics(JobInfoTest.QUERY_JOB_STATISTICS).setJobId(JobInfoTest.JOB_ID).setEtag(JobInfoTest.ETAG).setGeneratedId(JobInfoTest.GENERATED_ID).setSelfLink(JobInfoTest.SELF_LINK).setUserEmail(JobInfoTest.EMAIL).setStatus(JobInfoTest.JOB_STATUS).build();

    @Test
    public void testToBuilder() {
        compareJobInfo(JobInfoTest.COPY_JOB, JobInfoTest.COPY_JOB.toBuilder().build());
        compareJobInfo(JobInfoTest.EXTRACT_JOB, JobInfoTest.EXTRACT_JOB.toBuilder().build());
        compareJobInfo(JobInfoTest.LOAD_JOB, JobInfoTest.LOAD_JOB.toBuilder().build());
        compareJobInfo(JobInfoTest.QUERY_JOB, JobInfoTest.QUERY_JOB.toBuilder().build());
        JobInfo job = JobInfoTest.COPY_JOB.toBuilder().setUserEmail("newEmail").build();
        Assert.assertEquals("newEmail", job.getUserEmail());
        job = job.toBuilder().setUserEmail(JobInfoTest.EMAIL).build();
        compareJobInfo(JobInfoTest.COPY_JOB, job);
        job = JobInfoTest.EXTRACT_JOB.toBuilder().setUserEmail("newEmail").build();
        Assert.assertEquals("newEmail", job.getUserEmail());
        job = job.toBuilder().setUserEmail(JobInfoTest.EMAIL).build();
        compareJobInfo(JobInfoTest.EXTRACT_JOB, job);
        job = JobInfoTest.LOAD_JOB.toBuilder().setUserEmail("newEmail").build();
        Assert.assertEquals("newEmail", job.getUserEmail());
        job = job.toBuilder().setUserEmail(JobInfoTest.EMAIL).build();
        compareJobInfo(JobInfoTest.LOAD_JOB, job);
        job = JobInfoTest.QUERY_JOB.toBuilder().setUserEmail("newEmail").build();
        Assert.assertEquals("newEmail", job.getUserEmail());
        job = job.toBuilder().setUserEmail(JobInfoTest.EMAIL).build();
        compareJobInfo(JobInfoTest.QUERY_JOB, job);
    }

    @Test
    public void testOf() {
        JobInfo job = JobInfo.of(JobInfoTest.COPY_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.COPY_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.EXTRACT_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.EXTRACT_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.LOAD_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.LOAD_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.QUERY_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.QUERY_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.JOB_ID, JobInfoTest.COPY_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.JOB_ID, job.getJobId());
        Assert.assertEquals(JobInfoTest.COPY_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.JOB_ID, JobInfoTest.EXTRACT_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.JOB_ID, job.getJobId());
        Assert.assertEquals(JobInfoTest.EXTRACT_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.JOB_ID, JobInfoTest.LOAD_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.JOB_ID, job.getJobId());
        Assert.assertEquals(JobInfoTest.LOAD_CONFIGURATION, job.getConfiguration());
        job = JobInfo.of(JobInfoTest.JOB_ID, JobInfoTest.QUERY_CONFIGURATION);
        Assert.assertEquals(JobInfoTest.JOB_ID, job.getJobId());
        Assert.assertEquals(JobInfoTest.QUERY_CONFIGURATION, job.getConfiguration());
    }

    @Test
    public void testToBuilderIncomplete() {
        JobInfo job = JobInfo.of(JobInfoTest.COPY_CONFIGURATION);
        compareJobInfo(job, job.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(JobInfoTest.ETAG, JobInfoTest.COPY_JOB.getEtag());
        Assert.assertEquals(JobInfoTest.GENERATED_ID, JobInfoTest.COPY_JOB.getGeneratedId());
        Assert.assertEquals(JobInfoTest.SELF_LINK, JobInfoTest.COPY_JOB.getSelfLink());
        Assert.assertEquals(JobInfoTest.EMAIL, JobInfoTest.COPY_JOB.getUserEmail());
        Assert.assertEquals(JobInfoTest.JOB_ID, JobInfoTest.COPY_JOB.getJobId());
        Assert.assertEquals(JobInfoTest.JOB_STATUS, JobInfoTest.COPY_JOB.getStatus());
        Assert.assertEquals(JobInfoTest.COPY_CONFIGURATION, JobInfoTest.COPY_JOB.getConfiguration());
        Assert.assertEquals(JobInfoTest.COPY_JOB_STATISTICS, JobInfoTest.COPY_JOB.getStatistics());
        Assert.assertEquals(JobInfoTest.ETAG, JobInfoTest.EXTRACT_JOB.getEtag());
        Assert.assertEquals(JobInfoTest.GENERATED_ID, JobInfoTest.EXTRACT_JOB.getGeneratedId());
        Assert.assertEquals(JobInfoTest.SELF_LINK, JobInfoTest.EXTRACT_JOB.getSelfLink());
        Assert.assertEquals(JobInfoTest.EMAIL, JobInfoTest.EXTRACT_JOB.getUserEmail());
        Assert.assertEquals(JobInfoTest.JOB_ID, JobInfoTest.EXTRACT_JOB.getJobId());
        Assert.assertEquals(JobInfoTest.JOB_STATUS, JobInfoTest.EXTRACT_JOB.getStatus());
        Assert.assertEquals(JobInfoTest.EXTRACT_CONFIGURATION, JobInfoTest.EXTRACT_JOB.getConfiguration());
        Assert.assertEquals(JobInfoTest.EXTRACT_JOB_STATISTICS, JobInfoTest.EXTRACT_JOB.getStatistics());
        Assert.assertEquals(JobInfoTest.ETAG, JobInfoTest.LOAD_JOB.getEtag());
        Assert.assertEquals(JobInfoTest.GENERATED_ID, JobInfoTest.LOAD_JOB.getGeneratedId());
        Assert.assertEquals(JobInfoTest.SELF_LINK, JobInfoTest.LOAD_JOB.getSelfLink());
        Assert.assertEquals(JobInfoTest.EMAIL, JobInfoTest.LOAD_JOB.getUserEmail());
        Assert.assertEquals(JobInfoTest.JOB_ID, JobInfoTest.LOAD_JOB.getJobId());
        Assert.assertEquals(JobInfoTest.JOB_STATUS, JobInfoTest.LOAD_JOB.getStatus());
        Assert.assertEquals(JobInfoTest.LOAD_CONFIGURATION, JobInfoTest.LOAD_JOB.getConfiguration());
        Assert.assertEquals(JobInfoTest.LOAD_JOB_STATISTICS, JobInfoTest.LOAD_JOB.getStatistics());
        Assert.assertEquals(JobInfoTest.ETAG, JobInfoTest.QUERY_JOB.getEtag());
        Assert.assertEquals(JobInfoTest.GENERATED_ID, JobInfoTest.QUERY_JOB.getGeneratedId());
        Assert.assertEquals(JobInfoTest.SELF_LINK, JobInfoTest.QUERY_JOB.getSelfLink());
        Assert.assertEquals(JobInfoTest.EMAIL, JobInfoTest.QUERY_JOB.getUserEmail());
        Assert.assertEquals(JobInfoTest.JOB_ID, JobInfoTest.QUERY_JOB.getJobId());
        Assert.assertEquals(JobInfoTest.JOB_STATUS, JobInfoTest.QUERY_JOB.getStatus());
        Assert.assertEquals(JobInfoTest.QUERY_CONFIGURATION, JobInfoTest.QUERY_JOB.getConfiguration());
        Assert.assertEquals(JobInfoTest.QUERY_JOB_STATISTICS, JobInfoTest.QUERY_JOB.getStatistics());
    }

    @Test
    public void testToPbAndFromPb() {
        Assert.assertNotNull(JobInfoTest.COPY_JOB.toPb().getConfiguration().getCopy());
        Assert.assertNull(JobInfoTest.COPY_JOB.toPb().getConfiguration().getExtract());
        Assert.assertNull(JobInfoTest.COPY_JOB.toPb().getConfiguration().getLoad());
        Assert.assertNull(JobInfoTest.COPY_JOB.toPb().getConfiguration().getQuery());
        Assert.assertEquals(JobInfoTest.COPY_JOB_STATISTICS, JobStatistics.fromPb(JobInfoTest.COPY_JOB.toPb()));
        compareJobInfo(JobInfoTest.COPY_JOB, JobInfo.fromPb(JobInfoTest.COPY_JOB.toPb()));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.COPY_JOB.toPb()).getConfiguration()) instanceof CopyJobConfiguration));
        Assert.assertNull(JobInfoTest.EXTRACT_JOB.toPb().getConfiguration().getCopy());
        Assert.assertNotNull(JobInfoTest.EXTRACT_JOB.toPb().getConfiguration().getExtract());
        Assert.assertNull(JobInfoTest.EXTRACT_JOB.toPb().getConfiguration().getLoad());
        Assert.assertNull(JobInfoTest.EXTRACT_JOB.toPb().getConfiguration().getQuery());
        Assert.assertEquals(JobInfoTest.EXTRACT_JOB_STATISTICS, JobStatistics.fromPb(JobInfoTest.EXTRACT_JOB.toPb()));
        compareJobInfo(JobInfoTest.EXTRACT_JOB, JobInfo.fromPb(JobInfoTest.EXTRACT_JOB.toPb()));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.EXTRACT_JOB.toPb()).getConfiguration()) instanceof ExtractJobConfiguration));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.EXTRACT_JOB.toPb()).getStatistics()) instanceof ExtractStatistics));
        Assert.assertNull(JobInfoTest.LOAD_JOB.toPb().getConfiguration().getCopy());
        Assert.assertNull(JobInfoTest.LOAD_JOB.toPb().getConfiguration().getExtract());
        Assert.assertNotNull(JobInfoTest.LOAD_JOB.toPb().getConfiguration().getLoad());
        Assert.assertNull(JobInfoTest.LOAD_JOB.toPb().getConfiguration().getQuery());
        Assert.assertEquals(JobInfoTest.LOAD_JOB_STATISTICS, JobStatistics.fromPb(JobInfoTest.LOAD_JOB.toPb()));
        compareJobInfo(JobInfoTest.LOAD_JOB, JobInfo.fromPb(JobInfoTest.LOAD_JOB.toPb()));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.LOAD_JOB.toPb()).getConfiguration()) instanceof LoadJobConfiguration));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.LOAD_JOB.toPb()).getStatistics()) instanceof LoadStatistics));
        Assert.assertNull(JobInfoTest.QUERY_JOB.toPb().getConfiguration().getCopy());
        Assert.assertNull(JobInfoTest.QUERY_JOB.toPb().getConfiguration().getExtract());
        Assert.assertNull(JobInfoTest.QUERY_JOB.toPb().getConfiguration().getLoad());
        Assert.assertNotNull(JobInfoTest.QUERY_JOB.toPb().getConfiguration().getQuery());
        Assert.assertEquals(JobInfoTest.QUERY_JOB_STATISTICS, JobStatistics.fromPb(JobInfoTest.QUERY_JOB.toPb()));
        compareJobInfo(JobInfoTest.QUERY_JOB, JobInfo.fromPb(JobInfoTest.QUERY_JOB.toPb()));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.QUERY_JOB.toPb()).getConfiguration()) instanceof QueryJobConfiguration));
        Assert.assertTrue(((JobInfo.fromPb(JobInfoTest.QUERY_JOB.toPb()).getStatistics()) instanceof QueryStatistics));
    }

    @Test
    public void testSetProjectId() {
        JobInfo jobInfo = JobInfoTest.COPY_JOB.setProjectId("p");
        Assert.assertEquals("p", jobInfo.getJobId().getProject());
        CopyJobConfiguration copyConfiguration = jobInfo.getConfiguration();
        Assert.assertEquals("p", copyConfiguration.getDestinationTable().getProject());
        for (TableId sourceTable : copyConfiguration.getSourceTables()) {
            Assert.assertEquals("p", sourceTable.getProject());
        }
        jobInfo = JobInfoTest.EXTRACT_JOB.setProjectId("p");
        Assert.assertEquals("p", jobInfo.getJobId().getProject());
        ExtractJobConfiguration extractConfiguration = jobInfo.getConfiguration();
        Assert.assertEquals("p", extractConfiguration.getSourceTable().getProject());
        jobInfo = JobInfoTest.LOAD_JOB.setProjectId("p");
        Assert.assertEquals("p", jobInfo.getJobId().getProject());
        LoadJobConfiguration loadConfiguration = jobInfo.getConfiguration();
        Assert.assertEquals("p", loadConfiguration.getDestinationTable().getProject());
        jobInfo = JobInfoTest.QUERY_JOB.setProjectId("p");
        Assert.assertEquals("p", jobInfo.getJobId().getProject());
        QueryJobConfiguration queryConfiguration = jobInfo.getConfiguration();
        Assert.assertEquals("p", queryConfiguration.getDefaultDataset().getProject());
        Assert.assertEquals("p", queryConfiguration.getDestinationTable().getProject());
    }
}

