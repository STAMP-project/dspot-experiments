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
import JobConfiguration.Type.QUERY;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import SchemaUpdateOption.ALLOW_FIELD_RELAXATION;
import Type.DAY;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.SchemaUpdateOption;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.QueryJobConfiguration.Priority;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class QueryJobConfigurationTest {
    private static final String TEST_PROJECT_ID = "test-project-id";

    private static final String QUERY = "BigQuery SQL";

    private static final DatasetId DATASET_ID = DatasetId.of("dataset");

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final List<String> SOURCE_URIS = ImmutableList.of("uri1", "uri2");

    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, QueryJobConfigurationTest.FIELD_SCHEMA1, QueryJobConfigurationTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final Schema TABLE_SCHEMA = Schema.of(QueryJobConfigurationTest.FIELD_SCHEMA1, QueryJobConfigurationTest.FIELD_SCHEMA2, QueryJobConfigurationTest.FIELD_SCHEMA3);

    private static final Integer MAX_BAD_RECORDS = 42;

    private static final Boolean IGNORE_UNKNOWN_VALUES = true;

    private static final String COMPRESSION = "GZIP";

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().build();

    private static final ExternalTableDefinition TABLE_CONFIGURATION = ExternalTableDefinition.newBuilder(QueryJobConfigurationTest.SOURCE_URIS, QueryJobConfigurationTest.TABLE_SCHEMA, QueryJobConfigurationTest.CSV_OPTIONS).setCompression(QueryJobConfigurationTest.COMPRESSION).setIgnoreUnknownValues(QueryJobConfigurationTest.IGNORE_UNKNOWN_VALUES).setMaxBadRecords(QueryJobConfigurationTest.MAX_BAD_RECORDS).build();

    private static final Map<String, ExternalTableDefinition> TABLE_DEFINITIONS = ImmutableMap.of("tableName", QueryJobConfigurationTest.TABLE_CONFIGURATION);

    private static final CreateDisposition CREATE_DISPOSITION = CreateDisposition.CREATE_IF_NEEDED;

    private static final WriteDisposition WRITE_DISPOSITION = WriteDisposition.WRITE_APPEND;

    private static final Priority PRIORITY = Priority.BATCH;

    private static final boolean ALLOW_LARGE_RESULTS = true;

    private static final boolean USE_QUERY_CACHE = false;

    private static final boolean FLATTEN_RESULTS = true;

    private static final boolean USE_LEGACY_SQL = true;

    private static final Integer MAX_BILLING_TIER = 123;

    private static final List<SchemaUpdateOption> SCHEMA_UPDATE_OPTIONS = ImmutableList.of(ALLOW_FIELD_RELAXATION);

    private static final List<UserDefinedFunction> USER_DEFINED_FUNCTIONS = ImmutableList.of(UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI"));

    private static final EncryptionConfiguration JOB_ENCRYPTION_CONFIGURATION = EncryptionConfiguration.newBuilder().setKmsKeyName("KMS_KEY_1").build();

    private static final TimePartitioning TIME_PARTITIONING = TimePartitioning.of(DAY);

    private static final Clustering CLUSTERING = Clustering.newBuilder().setFields(ImmutableList.of("Foo", "Bar")).build();

    private static final QueryJobConfiguration QUERY_JOB_CONFIGURATION = QueryJobConfiguration.newBuilder(QueryJobConfigurationTest.QUERY).setUseQueryCache(QueryJobConfigurationTest.USE_QUERY_CACHE).setTableDefinitions(QueryJobConfigurationTest.TABLE_DEFINITIONS).setAllowLargeResults(QueryJobConfigurationTest.ALLOW_LARGE_RESULTS).setCreateDisposition(QueryJobConfigurationTest.CREATE_DISPOSITION).setDefaultDataset(QueryJobConfigurationTest.DATASET_ID).setDestinationTable(QueryJobConfigurationTest.TABLE_ID).setWriteDisposition(QueryJobConfigurationTest.WRITE_DISPOSITION).setPriority(QueryJobConfigurationTest.PRIORITY).setFlattenResults(QueryJobConfigurationTest.FLATTEN_RESULTS).setUserDefinedFunctions(QueryJobConfigurationTest.USER_DEFINED_FUNCTIONS).setDryRun(true).setUseLegacySql(QueryJobConfigurationTest.USE_LEGACY_SQL).setMaximumBillingTier(QueryJobConfigurationTest.MAX_BILLING_TIER).setSchemaUpdateOptions(QueryJobConfigurationTest.SCHEMA_UPDATE_OPTIONS).setDestinationEncryptionConfiguration(QueryJobConfigurationTest.JOB_ENCRYPTION_CONFIGURATION).setTimePartitioning(QueryJobConfigurationTest.TIME_PARTITIONING).setClustering(QueryJobConfigurationTest.CLUSTERING).build();

    @Test
    public void testToBuilder() {
        compareQueryJobConfiguration(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION, QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toBuilder().build());
        QueryJobConfiguration job = QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toBuilder().setQuery("New BigQuery SQL").build();
        Assert.assertEquals("New BigQuery SQL", job.getQuery());
        job = job.toBuilder().setQuery(QueryJobConfigurationTest.QUERY).build();
        compareQueryJobConfiguration(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION, job);
    }

    @Test
    public void testOf() {
        QueryJobConfiguration job = QueryJobConfiguration.of(QueryJobConfigurationTest.QUERY);
        Assert.assertEquals(QueryJobConfigurationTest.QUERY, job.getQuery());
    }

    @Test
    public void testToBuilderIncomplete() {
        QueryJobConfiguration job = QueryJobConfiguration.of(QueryJobConfigurationTest.QUERY);
        compareQueryJobConfiguration(job, job.toBuilder().build());
    }

    @Test
    public void testToPbAndFromPb() {
        Assert.assertNotNull(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toPb().getQuery());
        Assert.assertNull(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toPb().getExtract());
        Assert.assertNull(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toPb().getCopy());
        Assert.assertNull(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toPb().getLoad());
        compareQueryJobConfiguration(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION, QueryJobConfiguration.fromPb(QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toPb()));
        QueryJobConfiguration job = QueryJobConfiguration.of(QueryJobConfigurationTest.QUERY);
        compareQueryJobConfiguration(job, QueryJobConfiguration.fromPb(job.toPb()));
    }

    @Test
    public void testSetProjectId() {
        QueryJobConfiguration configuration = QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.setProjectId(QueryJobConfigurationTest.TEST_PROJECT_ID);
        Assert.assertEquals(QueryJobConfigurationTest.TEST_PROJECT_ID, configuration.getDefaultDataset().getProject());
        Assert.assertEquals(QueryJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        QueryJobConfiguration configuration = QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.toBuilder().setDestinationTable(QueryJobConfigurationTest.TABLE_ID.setProjectId(QueryJobConfigurationTest.TEST_PROJECT_ID)).build().setProjectId("update-only-on-dataset");
        Assert.assertEquals("update-only-on-dataset", configuration.getDefaultDataset().getProject());
        Assert.assertEquals(QueryJobConfigurationTest.TEST_PROJECT_ID, configuration.getDestinationTable().getProject());
    }

    @Test
    public void testGetType() {
        Assert.assertEquals(JobConfiguration.Type.QUERY, QueryJobConfigurationTest.QUERY_JOB_CONFIGURATION.getType());
    }
}

