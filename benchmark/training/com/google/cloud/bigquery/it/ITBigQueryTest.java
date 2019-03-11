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
package com.google.cloud.bigquery.it;


import BigQuery.TableField.TIME_PARTITIONING;
import DatasetField.CREATION_TIME;
import DatasetField.DESCRIPTION;
import DatasetField.LABELS;
import Field.Mode.NULLABLE;
import Field.Mode.REPEATED;
import Field.Mode.REQUIRED;
import FieldValue.Attribute.PRIMITIVE;
import JobField.ETAG;
import JobField.USER_EMAIL;
import JobInfo.CreateDisposition.CREATE_IF_NEEDED;
import JobStatistics.QueryStatistics;
import LegacySQLTypeName.BOOLEAN;
import LegacySQLTypeName.BYTES;
import LegacySQLTypeName.FLOAT;
import LegacySQLTypeName.GEOGRAPHY;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.NUMERIC;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import LegacySQLTypeName.TIMESTAMP;
import TableDefinition.Type.TABLE;
import Type.DAY;
import com.google.api.gax.paging.Page;
import com.google.cloud.RetryOption;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQuery.DatasetDeleteOption;
import com.google.cloud.bigquery.BigQuery.DatasetOption;
import com.google.cloud.bigquery.BigQuery.JobListOption;
import com.google.cloud.bigquery.BigQuery.JobOption;
import com.google.cloud.bigquery.BigQuery.TableOption;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.Clustering;
import com.google.cloud.bigquery.CopyJobConfiguration;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.ExternalTableDefinition;
import com.google.cloud.bigquery.ExtractJobConfiguration;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatistics;
import com.google.cloud.bigquery.JobStatistics.LoadStatistics;
import com.google.cloud.bigquery.LoadJobConfiguration;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.TimePartitioning;
import com.google.cloud.bigquery.ViewDefinition;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.threeten.bp.Duration;


public class ITBigQueryTest {
    private static final byte[] BYTES = new byte[]{ 13, 14, 10, 13 };

    private static final String BYTES_BASE64 = BaseEncoding.base64().encode(ITBigQueryTest.BYTES);

    private static final Logger LOG = Logger.getLogger(ITBigQueryTest.class.getName());

    private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final String DESCRIPTION = "Test dataset";

    private static final String OTHER_DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final Map<String, String> LABELS = ImmutableMap.of("example-label1", "example-value1", "example-label2", "example-value2");

    private static final Field TIMESTAMP_FIELD_SCHEMA = Field.newBuilder("TimestampField", TIMESTAMP).setMode(NULLABLE).setDescription("TimestampDescription").build();

    private static final Field STRING_FIELD_SCHEMA = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("StringDescription").build();

    private static final Field INTEGER_ARRAY_FIELD_SCHEMA = Field.newBuilder("IntegerArrayField", INTEGER).setMode(REPEATED).setDescription("IntegerArrayDescription").build();

    private static final Field BOOLEAN_FIELD_SCHEMA = Field.newBuilder("BooleanField", BOOLEAN).setMode(NULLABLE).setDescription("BooleanDescription").build();

    private static final Field BYTES_FIELD_SCHEMA = Field.newBuilder("BytesField", LegacySQLTypeName.BYTES).setMode(NULLABLE).setDescription("BytesDescription").build();

    private static final Field RECORD_FIELD_SCHEMA = Field.newBuilder("RecordField", RECORD, ITBigQueryTest.TIMESTAMP_FIELD_SCHEMA, ITBigQueryTest.STRING_FIELD_SCHEMA, ITBigQueryTest.INTEGER_ARRAY_FIELD_SCHEMA, ITBigQueryTest.BOOLEAN_FIELD_SCHEMA, ITBigQueryTest.BYTES_FIELD_SCHEMA).setMode(REQUIRED).setDescription("RecordDescription").build();

    private static final Field INTEGER_FIELD_SCHEMA = Field.newBuilder("IntegerField", INTEGER).setMode(NULLABLE).setDescription("IntegerDescription").build();

    private static final Field FLOAT_FIELD_SCHEMA = Field.newBuilder("FloatField", FLOAT).setMode(NULLABLE).setDescription("FloatDescription").build();

    private static final Field GEOGRAPHY_FIELD_SCHEMA = Field.newBuilder("GeographyField", GEOGRAPHY).setMode(NULLABLE).setDescription("GeographyDescription").build();

    private static final Field NUMERIC_FIELD_SCHEMA = Field.newBuilder("NumericField", NUMERIC).setMode(NULLABLE).setDescription("NumericDescription").build();

    private static final Schema TABLE_SCHEMA = Schema.of(ITBigQueryTest.TIMESTAMP_FIELD_SCHEMA, ITBigQueryTest.STRING_FIELD_SCHEMA, ITBigQueryTest.INTEGER_ARRAY_FIELD_SCHEMA, ITBigQueryTest.BOOLEAN_FIELD_SCHEMA, ITBigQueryTest.BYTES_FIELD_SCHEMA, ITBigQueryTest.RECORD_FIELD_SCHEMA, ITBigQueryTest.INTEGER_FIELD_SCHEMA, ITBigQueryTest.FLOAT_FIELD_SCHEMA, ITBigQueryTest.GEOGRAPHY_FIELD_SCHEMA, ITBigQueryTest.NUMERIC_FIELD_SCHEMA);

    private static final Schema SIMPLE_SCHEMA = Schema.of(ITBigQueryTest.STRING_FIELD_SCHEMA);

    private static final Schema QUERY_RESULT_SCHEMA = Schema.of(Field.newBuilder("TimestampField", TIMESTAMP).setMode(NULLABLE).build(), Field.newBuilder("StringField", STRING).setMode(NULLABLE).build(), Field.newBuilder("BooleanField", BOOLEAN).setMode(NULLABLE).build());

    private static final String LOAD_FILE = "load.csv";

    private static final String JSON_LOAD_FILE = "load.json";

    private static final String EXTRACT_FILE = "extract.csv";

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final TableId TABLE_ID = TableId.of(ITBigQueryTest.DATASET, "testing_table");

    private static final String CSV_CONTENT = "StringValue1\nStringValue2\n";

    private static final String JSON_CONTENT = (((((((((((((((((((((((((((((((((((((("{" + (((("  \"TimestampField\": \"2014-08-19 07:41:35.220 -05:00\"," + "  \"StringField\": \"stringValue\",") + "  \"IntegerArrayField\": [\"0\", \"1\"],") + "  \"BooleanField\": \"false\",") + "  \"BytesField\": \"")) + (ITBigQueryTest.BYTES_BASE64)) + "\",") + "  \"RecordField\": {") + "    \"TimestampField\": \"1969-07-20 20:18:04 UTC\",") + "    \"StringField\": null,") + "    \"IntegerArrayField\": [\"1\",\"0\"],") + "    \"BooleanField\": \"true\",") + "    \"BytesField\": \"") + (ITBigQueryTest.BYTES_BASE64)) + "\"") + "  },") + "  \"IntegerField\": \"3\",") + "  \"FloatField\": \"1.2\",") + "  \"GeographyField\": \"POINT(-122.35022 47.649154)\",") + "  \"NumericField\": \"123456.789012345\"") + "}\n") + "{") + "  \"TimestampField\": \"2014-08-19 07:41:35.220 -05:00\",") + "  \"StringField\": \"stringValue\",") + "  \"IntegerArrayField\": [\"0\", \"1\"],") + "  \"BooleanField\": \"false\",") + "  \"BytesField\": \"") + (ITBigQueryTest.BYTES_BASE64)) + "\",") + "  \"RecordField\": {") + "    \"TimestampField\": \"1969-07-20 20:18:04 UTC\",") + "    \"StringField\": null,") + "    \"IntegerArrayField\": [\"1\",\"0\"],") + "    \"BooleanField\": \"true\",") + "    \"BytesField\": \"") + (ITBigQueryTest.BYTES_BASE64)) + "\"") + "  },") + "  \"IntegerField\": \"3\",") + "  \"FloatField\": \"1.2\",") + "  \"GeographyField\": \"POINT(-122.35022 47.649154)\",") + "  \"NumericField\": \"123456.789012345\"") + "}";

    private static final Set<String> PUBLIC_DATASETS = ImmutableSet.of("github_repos", "hacker_news", "noaa_gsod", "samples", "usa_names");

    private static BigQuery bigquery;

    private static Storage storage;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testListDatasets() {
        Page<Dataset> datasets = ITBigQueryTest.bigquery.listDatasets("bigquery-public-data");
        Iterator<Dataset> iterator = datasets.iterateAll().iterator();
        Set<String> datasetNames = new HashSet<>();
        while (iterator.hasNext()) {
            datasetNames.add(iterator.next().getDatasetId().getDataset());
        } 
        for (String type : ITBigQueryTest.PUBLIC_DATASETS) {
            Assert.assertTrue(datasetNames.contains(type));
        }
    }

    @Test
    public void testGetDataset() {
        Dataset dataset = ITBigQueryTest.bigquery.getDataset(ITBigQueryTest.DATASET);
        Assert.assertEquals(ITBigQueryTest.bigquery.getOptions().getProjectId(), dataset.getDatasetId().getProject());
        Assert.assertEquals(ITBigQueryTest.DATASET, dataset.getDatasetId().getDataset());
        Assert.assertEquals(ITBigQueryTest.DESCRIPTION, dataset.getDescription());
        Assert.assertEquals(ITBigQueryTest.LABELS, dataset.getLabels());
        Assert.assertNotNull(dataset.getAcl());
        Assert.assertNotNull(dataset.getEtag());
        Assert.assertNotNull(dataset.getGeneratedId());
        Assert.assertNotNull(dataset.getLastModified());
        Assert.assertNotNull(dataset.getSelfLink());
    }

    @Test
    public void testGetDatasetWithSelectedFields() {
        Dataset dataset = ITBigQueryTest.bigquery.getDataset(ITBigQueryTest.DATASET, DatasetOption.fields(CREATION_TIME, DatasetField.LABELS));
        Assert.assertEquals(ITBigQueryTest.bigquery.getOptions().getProjectId(), dataset.getDatasetId().getProject());
        Assert.assertEquals(ITBigQueryTest.DATASET, dataset.getDatasetId().getDataset());
        Assert.assertEquals(ITBigQueryTest.LABELS, dataset.getLabels());
        Assert.assertNotNull(dataset.getCreationTime());
        Assert.assertNull(dataset.getDescription());
        Assert.assertNull(dataset.getDefaultTableLifetime());
        Assert.assertNull(dataset.getAcl());
        Assert.assertNull(dataset.getEtag());
        Assert.assertNull(dataset.getFriendlyName());
        Assert.assertNull(dataset.getGeneratedId());
        Assert.assertNull(dataset.getLastModified());
        Assert.assertNull(dataset.getLocation());
        Assert.assertNull(dataset.getSelfLink());
    }

    @Test
    public void testUpdateDataset() {
        Dataset dataset = ITBigQueryTest.bigquery.create(DatasetInfo.newBuilder(ITBigQueryTest.OTHER_DATASET).setDescription("Some Description").setLabels(Collections.singletonMap("a", "b")).build());
        assertThat(dataset).isNotNull();
        assertThat(dataset.getDatasetId().getProject()).isEqualTo(ITBigQueryTest.bigquery.getOptions().getProjectId());
        assertThat(dataset.getDatasetId().getDataset()).isEqualTo(ITBigQueryTest.OTHER_DATASET);
        assertThat(dataset.getDescription()).isEqualTo("Some Description");
        assertThat(dataset.getLabels()).containsExactly("a", "b");
        Map<String, String> updateLabels = new HashMap<>();
        updateLabels.put("x", "y");
        updateLabels.put("a", null);
        Dataset updatedDataset = ITBigQueryTest.bigquery.update(dataset.toBuilder().setDescription("Updated Description").setLabels(updateLabels).build());
        assertThat(updatedDataset.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedDataset.getLabels()).containsExactly("x", "y");
        updatedDataset = ITBigQueryTest.bigquery.update(updatedDataset.toBuilder().setLabels(null).build());
        assertThat(updatedDataset.getLabels()).isEmpty();
        assertThat(dataset.delete()).isTrue();
    }

    @Test
    public void testUpdateDatasetWithSelectedFields() {
        Dataset dataset = ITBigQueryTest.bigquery.create(DatasetInfo.newBuilder(ITBigQueryTest.OTHER_DATASET).setDescription("Some Description").build());
        Assert.assertNotNull(dataset);
        Assert.assertEquals(ITBigQueryTest.bigquery.getOptions().getProjectId(), dataset.getDatasetId().getProject());
        Assert.assertEquals(ITBigQueryTest.OTHER_DATASET, dataset.getDatasetId().getDataset());
        Assert.assertEquals("Some Description", dataset.getDescription());
        Dataset updatedDataset = ITBigQueryTest.bigquery.update(dataset.toBuilder().setDescription("Updated Description").build(), DatasetOption.fields(DatasetField.DESCRIPTION));
        Assert.assertEquals("Updated Description", updatedDataset.getDescription());
        Assert.assertNull(updatedDataset.getCreationTime());
        Assert.assertNull(updatedDataset.getDefaultTableLifetime());
        Assert.assertNull(updatedDataset.getAcl());
        Assert.assertNull(updatedDataset.getEtag());
        Assert.assertNull(updatedDataset.getFriendlyName());
        Assert.assertNull(updatedDataset.getGeneratedId());
        Assert.assertNull(updatedDataset.getLastModified());
        Assert.assertNull(updatedDataset.getLocation());
        Assert.assertNull(updatedDataset.getSelfLink());
        Assert.assertTrue(dataset.delete());
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, "test_get_non_existing_table"));
    }

    @Test
    public void testCreateAndGetTable() {
        String tableName = "test_create_and_get_table";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, tableName);
        TimePartitioning partitioning = TimePartitioning.of(DAY);
        Clustering clustering = Clustering.newBuilder().setFields(ImmutableList.of(ITBigQueryTest.STRING_FIELD_SCHEMA.getName())).build();
        StandardTableDefinition tableDefinition = StandardTableDefinition.newBuilder().setSchema(ITBigQueryTest.TABLE_SCHEMA).setTimePartitioning(partitioning).setClustering(clustering).build();
        Table createdTable = ITBigQueryTest.bigquery.create(TableInfo.of(tableId, tableDefinition));
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(tableName, createdTable.getTableId().getTable());
        Table remoteTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, tableName);
        Assert.assertNotNull(remoteTable);
        Assert.assertTrue(((remoteTable.getDefinition()) instanceof StandardTableDefinition));
        Assert.assertEquals(createdTable.getTableId(), remoteTable.getTableId());
        Assert.assertEquals(TABLE, remoteTable.getDefinition().getType());
        Assert.assertEquals(ITBigQueryTest.TABLE_SCHEMA, remoteTable.getDefinition().getSchema());
        Assert.assertNotNull(remoteTable.getCreationTime());
        Assert.assertNotNull(remoteTable.getLastModifiedTime());
        Assert.assertNotNull(remoteTable.<StandardTableDefinition>getDefinition().getNumBytes());
        Assert.assertNotNull(remoteTable.<StandardTableDefinition>getDefinition().getNumLongTermBytes());
        Assert.assertNotNull(remoteTable.<StandardTableDefinition>getDefinition().getNumRows());
        Assert.assertEquals(partitioning, getTimePartitioning());
        Assert.assertEquals(clustering, remoteTable.<StandardTableDefinition>getDefinition().getClustering());
        Assert.assertTrue(remoteTable.delete());
    }

    @Test
    public void testCreateAndGetTableWithSelectedField() {
        String tableName = "test_create_and_get_selected_fields_table";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, tableName);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        Table createdTable = ITBigQueryTest.bigquery.create(TableInfo.newBuilder(tableId, tableDefinition).setLabels(Collections.singletonMap("a", "b")).build());
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(tableName, createdTable.getTableId().getTable());
        Table remoteTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, tableName, TableOption.fields(TableField.CREATION_TIME, TableField.LABELS));
        Assert.assertNotNull(remoteTable);
        Assert.assertTrue(((remoteTable.getDefinition()) instanceof StandardTableDefinition));
        Assert.assertEquals(createdTable.getTableId(), remoteTable.getTableId());
        Assert.assertEquals(TABLE, remoteTable.getDefinition().getType());
        assertThat(remoteTable.getLabels()).containsExactly("a", "b");
        Assert.assertNotNull(remoteTable.getCreationTime());
        Assert.assertNull(remoteTable.getDefinition().getSchema());
        Assert.assertNull(remoteTable.getLastModifiedTime());
        Assert.assertNull(remoteTable.<StandardTableDefinition>getDefinition().getNumBytes());
        Assert.assertNull(remoteTable.<StandardTableDefinition>getDefinition().getNumLongTermBytes());
        Assert.assertNull(remoteTable.<StandardTableDefinition>getDefinition().getNumRows());
        Assert.assertNull(getTimePartitioning());
        Assert.assertNull(remoteTable.<StandardTableDefinition>getDefinition().getClustering());
        Assert.assertTrue(remoteTable.delete());
    }

    @Test
    public void testCreateExternalTable() throws InterruptedException {
        String tableName = "test_create_external_table";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, tableName);
        ExternalTableDefinition externalTableDefinition = ExternalTableDefinition.of(((("gs://" + (ITBigQueryTest.BUCKET)) + "/") + (ITBigQueryTest.JSON_LOAD_FILE)), ITBigQueryTest.TABLE_SCHEMA, FormatOptions.json());
        TableInfo tableInfo = TableInfo.of(tableId, externalTableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(tableName, createdTable.getTableId().getTable());
        Table remoteTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, tableName);
        Assert.assertNotNull(remoteTable);
        Assert.assertTrue(((remoteTable.getDefinition()) instanceof ExternalTableDefinition));
        Assert.assertEquals(createdTable.getTableId(), remoteTable.getTableId());
        Assert.assertEquals(ITBigQueryTest.TABLE_SCHEMA, remoteTable.getDefinition().getSchema());
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(((("SELECT TimestampField, StringField, IntegerArrayField, BooleanField FROM " + (ITBigQueryTest.DATASET)) + ".") + tableName)).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setUseLegacySql(true).build();
        TableResult result = ITBigQueryTest.bigquery.query(config);
        long integerValue = 0;
        int rowCount = 0;
        for (FieldValueList row : result.getValues()) {
            FieldValue timestampCell = row.get(0);
            Assert.assertEquals(timestampCell, row.get("TimestampField"));
            FieldValue stringCell = row.get(1);
            Assert.assertEquals(stringCell, row.get("StringField"));
            FieldValue integerCell = row.get(2);
            Assert.assertEquals(integerCell, row.get("IntegerArrayField"));
            FieldValue booleanCell = row.get(3);
            Assert.assertEquals(booleanCell, row.get("BooleanField"));
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, integerCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(integerValue, integerCell.getLongValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            integerValue = (~integerValue) & 1;
            rowCount++;
        }
        Assert.assertEquals(4, rowCount);
        Assert.assertTrue(remoteTable.delete());
    }

    @Test
    public void testCreateViewTable() throws InterruptedException {
        String tableName = "test_create_view_table";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, tableName);
        ViewDefinition viewDefinition = ViewDefinition.newBuilder(String.format("SELECT TimestampField, StringField, BooleanField FROM %s.%s", ITBigQueryTest.DATASET, ITBigQueryTest.TABLE_ID.getTable())).setUseLegacySql(true).build();
        TableInfo tableInfo = TableInfo.of(tableId, viewDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(tableName, createdTable.getTableId().getTable());
        Table remoteTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, tableName);
        Assert.assertNotNull(remoteTable);
        Assert.assertEquals(createdTable.getTableId(), remoteTable.getTableId());
        Assert.assertTrue(((remoteTable.getDefinition()) instanceof ViewDefinition));
        Schema expectedSchema = Schema.of(Field.newBuilder("TimestampField", TIMESTAMP).setMode(NULLABLE).build(), Field.newBuilder("StringField", STRING).setMode(NULLABLE).build(), Field.newBuilder("BooleanField", BOOLEAN).setMode(NULLABLE).build());
        Assert.assertEquals(expectedSchema, remoteTable.getDefinition().getSchema());
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(("SELECT * FROM " + tableName)).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setUseLegacySql(true).build();
        TableResult result = ITBigQueryTest.bigquery.query(config);
        int rowCount = 0;
        for (FieldValueList row : result.getValues()) {
            FieldValue timestampCell = row.get(0);
            Assert.assertEquals(timestampCell, row.get("TimestampField"));
            FieldValue stringCell = row.get(1);
            Assert.assertEquals(stringCell, row.get("StringField"));
            FieldValue booleanCell = row.get(2);
            Assert.assertEquals(booleanCell, row.get("BooleanField"));
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            rowCount++;
        }
        Assert.assertEquals(2, rowCount);
        Assert.assertTrue(remoteTable.delete());
    }

    @Test
    public void testListTables() {
        String tableName = "test_list_tables";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Page<Table> tables = ITBigQueryTest.bigquery.listTables(ITBigQueryTest.DATASET);
        boolean found = false;
        Iterator<Table> tableIterator = tables.getValues().iterator();
        while ((tableIterator.hasNext()) && (!found)) {
            if (tableIterator.next().getTableId().equals(createdTable.getTableId())) {
                found = true;
            }
        } 
        Assert.assertTrue(found);
        Assert.assertTrue(createdTable.delete());
    }

    @Test
    public void testUpdateTable() {
        String tableName = "test_update_table";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.newBuilder(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition).setDescription("Some Description").setLabels(Collections.singletonMap("a", "b")).build();
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        assertThat(createdTable.getDescription()).isEqualTo("Some Description");
        assertThat(createdTable.getLabels()).containsExactly("a", "b");
        Map<String, String> updateLabels = new HashMap<>();
        updateLabels.put("x", "y");
        updateLabels.put("a", null);
        Table updatedTable = ITBigQueryTest.bigquery.update(createdTable.toBuilder().setDescription("Updated Description").setLabels(updateLabels).build());
        assertThat(updatedTable.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTable.getLabels()).containsExactly("x", "y");
        updatedTable = ITBigQueryTest.bigquery.update(updatedTable.toBuilder().setLabels(null).build());
        assertThat(updatedTable.getLabels()).isEmpty();
        assertThat(createdTable.delete()).isTrue();
    }

    @Test
    public void testUpdateTimePartitioning() {
        String tableName = "testUpdateTimePartitioning";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, tableName);
        StandardTableDefinition tableDefinition = StandardTableDefinition.newBuilder().setSchema(ITBigQueryTest.TABLE_SCHEMA).setTimePartitioning(TimePartitioning.of(DAY)).build();
        Table table = ITBigQueryTest.bigquery.create(TableInfo.of(tableId, tableDefinition));
        assertThat(table.getDefinition()).isInstanceOf(StandardTableDefinition.class);
        assertThat(getTimePartitioning().getExpirationMs()).isNull();
        table = table.toBuilder().setDefinition(tableDefinition.toBuilder().setTimePartitioning(TimePartitioning.of(DAY, 42L)).build()).build().update(BigQuery.TableOption.fields(TIME_PARTITIONING));
        assertThat(getTimePartitioning().getExpirationMs()).isEqualTo(42L);
        table = table.toBuilder().setDefinition(tableDefinition.toBuilder().setTimePartitioning(TimePartitioning.of(DAY)).build()).build().update(BigQuery.TableOption.fields(TIME_PARTITIONING));
        assertThat(getTimePartitioning().getExpirationMs()).isNull();
        table.delete();
    }

    @Test
    public void testUpdateTableWithSelectedFields() {
        String tableName = "test_update_with_selected_fields_table";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Table updatedTable = ITBigQueryTest.bigquery.update(tableInfo.toBuilder().setDescription("newDescr").build(), TableOption.fields(TableField.DESCRIPTION));
        Assert.assertTrue(((updatedTable.getDefinition()) instanceof StandardTableDefinition));
        Assert.assertEquals(ITBigQueryTest.DATASET, updatedTable.getTableId().getDataset());
        Assert.assertEquals(tableName, updatedTable.getTableId().getTable());
        Assert.assertEquals("newDescr", updatedTable.getDescription());
        Assert.assertNull(updatedTable.getDefinition().getSchema());
        Assert.assertNull(updatedTable.getLastModifiedTime());
        Assert.assertNull(updatedTable.<StandardTableDefinition>getDefinition().getNumBytes());
        Assert.assertNull(updatedTable.<StandardTableDefinition>getDefinition().getNumLongTermBytes());
        Assert.assertNull(updatedTable.<StandardTableDefinition>getDefinition().getNumRows());
        Assert.assertTrue(createdTable.delete());
    }

    @Test
    public void testUpdateNonExistingTable() {
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, "test_update_non_existing_table"), StandardTableDefinition.of(ITBigQueryTest.SIMPLE_SCHEMA));
        try {
            ITBigQueryTest.bigquery.update(tableInfo);
            Assert.fail("BigQueryException was expected");
        } catch (BigQueryException e) {
            BigQueryError error = e.getError();
            Assert.assertNotNull(error);
            Assert.assertEquals("notFound", error.getReason());
            Assert.assertNotNull(error.getMessage());
        }
    }

    @Test
    public void testDeleteNonExistingTable() {
        Assert.assertFalse(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, "test_delete_non_existing_table"));
    }

    @Test
    public void testInsertAll() throws IOException {
        String tableName = "test_insert_all_table";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition);
        Assert.assertNotNull(ITBigQueryTest.bigquery.create(tableInfo));
        ImmutableMap.Builder<String, Object> builder1 = ImmutableMap.builder();
        builder1.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder1.put("StringField", "stringValue");
        builder1.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder1.put("BooleanField", false);
        builder1.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder1.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder1.put("IntegerField", 5);
        builder1.put("FloatField", 1.2);
        builder1.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder1.put("NumericField", new BigDecimal("123456789.123456789"));
        ImmutableMap.Builder<String, Object> builder2 = ImmutableMap.builder();
        builder2.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder2.put("StringField", "stringValue");
        builder2.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder2.put("BooleanField", false);
        builder2.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder2.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder2.put("IntegerField", 5);
        builder2.put("FloatField", 1.2);
        builder2.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder2.put("NumericField", new BigDecimal("123456789.123456789"));
        InsertAllRequest request = InsertAllRequest.newBuilder(tableInfo.getTableId()).addRow(builder1.build()).addRow(builder2.build()).build();
        InsertAllResponse response = ITBigQueryTest.bigquery.insertAll(request);
        Assert.assertFalse(response.hasErrors());
        Assert.assertEquals(0, response.getInsertErrors().size());
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(TableId.of(ITBigQueryTest.DATASET, tableName)));
    }

    @Test
    public void testInsertAllWithSuffix() throws InterruptedException {
        String tableName = "test_insert_all_with_suffix_table";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition);
        Assert.assertNotNull(ITBigQueryTest.bigquery.create(tableInfo));
        ImmutableMap.Builder<String, Object> builder1 = ImmutableMap.builder();
        builder1.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder1.put("StringField", "stringValue");
        builder1.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder1.put("BooleanField", false);
        builder1.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder1.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder1.put("IntegerField", 5);
        builder1.put("FloatField", 1.2);
        builder1.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder1.put("NumericField", new BigDecimal("123456789.123456789"));
        ImmutableMap.Builder<String, Object> builder2 = ImmutableMap.builder();
        builder2.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder2.put("StringField", "stringValue");
        builder2.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder2.put("BooleanField", false);
        builder2.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder2.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder2.put("IntegerField", 5);
        builder2.put("FloatField", 1.2);
        builder2.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder2.put("NumericField", new BigDecimal("123456789.123456789"));
        InsertAllRequest request = InsertAllRequest.newBuilder(tableInfo.getTableId()).addRow(builder1.build()).addRow(builder2.build()).setTemplateSuffix("_suffix").build();
        InsertAllResponse response = ITBigQueryTest.bigquery.insertAll(request);
        Assert.assertFalse(response.hasErrors());
        Assert.assertEquals(0, response.getInsertErrors().size());
        String newTableName = tableName + "_suffix";
        Table suffixTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, newTableName, TableOption.fields());
        // wait until the new table is created. If the table is never created the test will time-out
        while (suffixTable == null) {
            Thread.sleep(1000L);
            suffixTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, newTableName, TableOption.fields());
        } 
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(TableId.of(ITBigQueryTest.DATASET, tableName)));
        Assert.assertTrue(suffixTable.delete());
    }

    @Test
    public void testInsertAllWithErrors() {
        String tableName = "test_insert_all_with_errors_table";
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(TableId.of(ITBigQueryTest.DATASET, tableName), tableDefinition);
        Assert.assertNotNull(ITBigQueryTest.bigquery.create(tableInfo));
        ImmutableMap.Builder<String, Object> builder1 = ImmutableMap.builder();
        builder1.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder1.put("StringField", "stringValue");
        builder1.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder1.put("BooleanField", false);
        builder1.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder1.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder1.put("IntegerField", 5);
        builder1.put("FloatField", 1.2);
        builder1.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder1.put("NumericField", new BigDecimal("123456789.123456789"));
        ImmutableMap.Builder<String, Object> builder2 = ImmutableMap.builder();
        builder2.put("TimestampField", "invalidDate");
        builder2.put("StringField", "stringValue");
        builder2.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder2.put("BooleanField", false);
        builder2.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        builder2.put("RecordField", ImmutableMap.of("TimestampField", "1969-07-20 20:18:04 UTC", "IntegerArrayField", ImmutableList.of(1, 0), "BooleanField", true, "BytesField", ITBigQueryTest.BYTES_BASE64));
        builder2.put("IntegerField", 5);
        builder2.put("FloatField", 1.2);
        builder2.put("GeographyField", "POINT(-122.350220 47.649154)");
        builder2.put("NumericField", new BigDecimal("123456789.123456789"));
        ImmutableMap.Builder<String, Object> builder3 = ImmutableMap.builder();
        builder3.put("TimestampField", "2014-08-19 07:41:35.220 -05:00");
        builder3.put("StringField", "stringValue");
        builder3.put("IntegerArrayField", ImmutableList.of(0, 1));
        builder3.put("BooleanField", false);
        builder3.put("BytesField", ITBigQueryTest.BYTES_BASE64);
        InsertAllRequest request = InsertAllRequest.newBuilder(tableInfo.getTableId()).addRow(builder1.build()).addRow(builder2.build()).addRow(builder3.build()).setSkipInvalidRows(true).build();
        InsertAllResponse response = ITBigQueryTest.bigquery.insertAll(request);
        Assert.assertTrue(response.hasErrors());
        Assert.assertEquals(2, response.getInsertErrors().size());
        Assert.assertNotNull(response.getErrorsFor(1L));
        Assert.assertNotNull(response.getErrorsFor(2L));
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(TableId.of(ITBigQueryTest.DATASET, tableName)));
    }

    @Test
    public void testListAllTableData() {
        Page<FieldValueList> rows = ITBigQueryTest.bigquery.listTableData(ITBigQueryTest.TABLE_ID);
        int rowCount = 0;
        for (FieldValueList row : rows.getValues()) {
            FieldValue timestampCell = row.get(0);
            FieldValue stringCell = row.get(1);
            FieldValue integerArrayCell = row.get(2);
            FieldValue booleanCell = row.get(3);
            FieldValue bytesCell = row.get(4);
            FieldValue recordCell = row.get(5);
            FieldValue integerCell = row.get(6);
            FieldValue floatCell = row.get(7);
            FieldValue geographyCell = row.get(8);
            FieldValue numericCell = row.get(9);
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(FieldValue.Attribute.REPEATED, integerArrayCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, bytesCell.getAttribute());
            Assert.assertEquals(FieldValue.Attribute.RECORD, recordCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, integerCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, floatCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, geographyCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, numericCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(0, integerArrayCell.getRepeatedValue().get(0).getLongValue());
            Assert.assertEquals(1, integerArrayCell.getRepeatedValue().get(1).getLongValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            Assert.assertArrayEquals(ITBigQueryTest.BYTES, bytesCell.getBytesValue());
            Assert.assertEquals((-14182916000000L), recordCell.getRecordValue().get(0).getTimestampValue());
            Assert.assertTrue(recordCell.getRecordValue().get(1).isNull());
            Assert.assertEquals(1, recordCell.getRecordValue().get(2).getRepeatedValue().get(0).getLongValue());
            Assert.assertEquals(0, recordCell.getRecordValue().get(2).getRepeatedValue().get(1).getLongValue());
            Assert.assertEquals(true, recordCell.getRecordValue().get(3).getBooleanValue());
            Assert.assertEquals(3, integerCell.getLongValue());
            Assert.assertEquals(1.2, floatCell.getDoubleValue(), 1.0E-4);
            Assert.assertEquals("POINT(-122.35022 47.649154)", geographyCell.getStringValue());
            Assert.assertEquals(new BigDecimal("123456.789012345"), numericCell.getNumericValue());
            rowCount++;
        }
        Assert.assertEquals(2, rowCount);
    }

    @Test
    public void testQuery() throws InterruptedException {
        String query = "SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable());
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).build();
        Job job = ITBigQueryTest.bigquery.create(JobInfo.of(JobId.of(), config));
        TableResult result = job.getQueryResults();
        Assert.assertEquals(ITBigQueryTest.QUERY_RESULT_SCHEMA, result.getSchema());
        int rowCount = 0;
        for (FieldValueList row : result.getValues()) {
            FieldValue timestampCell = row.get(0);
            Assert.assertEquals(timestampCell, row.get("TimestampField"));
            FieldValue stringCell = row.get(1);
            Assert.assertEquals(stringCell, row.get("StringField"));
            FieldValue booleanCell = row.get(2);
            Assert.assertEquals(booleanCell, row.get("BooleanField"));
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            rowCount++;
        }
        Assert.assertEquals(2, rowCount);
        Job job2 = ITBigQueryTest.bigquery.getJob(job.getJobId());
        JobStatistics.QueryStatistics statistics = job2.getStatistics();
        Assert.assertNotNull(statistics.getQueryPlan());
    }

    @Test
    public void testPositionalQueryParameters() throws InterruptedException {
        String query = (((((("SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable())) + " WHERE StringField = ?") + " AND TimestampField > ?") + " AND IntegerField IN UNNEST(?)") + " AND IntegerField < ?") + " AND FloatField > ?") + " AND NumericField < ?";
        QueryParameterValue stringParameter = QueryParameterValue.string("stringValue");
        QueryParameterValue timestampParameter = QueryParameterValue.timestamp("2014-01-01 07:00:00.000000+00:00");
        QueryParameterValue intArrayParameter = QueryParameterValue.array(new Integer[]{ 3, 4 }, Integer.class);
        QueryParameterValue int64Parameter = QueryParameterValue.int64(5);
        QueryParameterValue float64Parameter = QueryParameterValue.float64(0.5);
        QueryParameterValue numericParameter = QueryParameterValue.numeric(new BigDecimal("234567890.123456"));
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setUseLegacySql(false).addPositionalParameter(stringParameter).addPositionalParameter(timestampParameter).addPositionalParameter(intArrayParameter).addPositionalParameter(int64Parameter).addPositionalParameter(float64Parameter).addPositionalParameter(numericParameter).build();
        TableResult result = ITBigQueryTest.bigquery.query(config);
        Assert.assertEquals(ITBigQueryTest.QUERY_RESULT_SCHEMA, result.getSchema());
        Assert.assertEquals(2, Iterables.size(result.getValues()));
    }

    @Test
    public void testNamedQueryParameters() throws InterruptedException {
        String query = (("SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable())) + " WHERE StringField = @stringParam") + " AND IntegerField IN UNNEST(@integerList)";
        QueryParameterValue stringParameter = QueryParameterValue.string("stringValue");
        QueryParameterValue intArrayParameter = QueryParameterValue.array(new Integer[]{ 3, 4 }, Integer.class);
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setUseLegacySql(false).addNamedParameter("stringParam", stringParameter).addNamedParameter("integerList", intArrayParameter).build();
        TableResult result = ITBigQueryTest.bigquery.query(config);
        Assert.assertEquals(ITBigQueryTest.QUERY_RESULT_SCHEMA, result.getSchema());
        Assert.assertEquals(2, Iterables.size(result.getValues()));
    }

    @Test
    public void testBytesParameter() throws Exception {
        String query = "SELECT BYTE_LENGTH(@p) AS length";
        QueryParameterValue bytesParameter = QueryParameterValue.bytes(new byte[]{ 1, 3 });
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setUseLegacySql(false).addNamedParameter("p", bytesParameter).build();
        TableResult result = ITBigQueryTest.bigquery.query(config);
        int rowCount = 0;
        for (FieldValueList row : result.getValues()) {
            rowCount++;
            Assert.assertEquals(2, row.get(0).getLongValue());
            Assert.assertEquals(2, row.get("length").getLongValue());
        }
        Assert.assertEquals(1, rowCount);
    }

    @Test
    public void testListJobs() {
        Page<Job> jobs = ITBigQueryTest.bigquery.listJobs();
        for (Job job : jobs.getValues()) {
            Assert.assertNotNull(job.getJobId());
            Assert.assertNotNull(job.getStatistics());
            Assert.assertNotNull(job.getStatus());
            Assert.assertNotNull(job.getUserEmail());
            Assert.assertNotNull(job.getGeneratedId());
        }
    }

    @Test
    public void testListJobsWithSelectedFields() {
        Page<Job> jobs = ITBigQueryTest.bigquery.listJobs(JobListOption.fields(USER_EMAIL));
        for (Job job : jobs.getValues()) {
            Assert.assertNotNull(job.getJobId());
            Assert.assertNotNull(job.getStatus());
            Assert.assertNotNull(job.getUserEmail());
            Assert.assertNull(job.getStatistics());
            Assert.assertNull(job.getGeneratedId());
        }
    }

    @Test
    public void testCreateAndGetJob() throws InterruptedException, TimeoutException {
        String sourceTableName = "test_create_and_get_job_source_table";
        String destinationTableName = "test_create_and_get_job_destination_table";
        TableId sourceTable = TableId.of(ITBigQueryTest.DATASET, sourceTableName);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(sourceTable, tableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(sourceTableName, createdTable.getTableId().getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, destinationTableName);
        CopyJobConfiguration copyJobConfiguration = CopyJobConfiguration.of(destinationTable, sourceTable);
        Job createdJob = ITBigQueryTest.bigquery.create(JobInfo.of(copyJobConfiguration));
        Job remoteJob = ITBigQueryTest.bigquery.getJob(createdJob.getJobId());
        Assert.assertEquals(createdJob.getJobId(), remoteJob.getJobId());
        CopyJobConfiguration createdConfiguration = createdJob.getConfiguration();
        CopyJobConfiguration remoteConfiguration = remoteJob.getConfiguration();
        Assert.assertEquals(createdConfiguration.getSourceTables(), remoteConfiguration.getSourceTables());
        Assert.assertEquals(createdConfiguration.getDestinationTable(), remoteConfiguration.getDestinationTable());
        Assert.assertEquals(createdConfiguration.getCreateDisposition(), remoteConfiguration.getCreateDisposition());
        Assert.assertEquals(createdConfiguration.getWriteDisposition(), remoteConfiguration.getWriteDisposition());
        Assert.assertNotNull(remoteJob.getEtag());
        Assert.assertNotNull(remoteJob.getStatistics());
        Assert.assertNotNull(remoteJob.getStatus());
        Assert.assertEquals(createdJob.getSelfLink(), remoteJob.getSelfLink());
        Assert.assertEquals(createdJob.getUserEmail(), remoteJob.getUserEmail());
        Assert.assertTrue(createdTable.delete());
        Job completedJob = remoteJob.waitFor(RetryOption.totalTimeout(Duration.ofMinutes(1)));
        Assert.assertNotNull(completedJob);
        Assert.assertNull(completedJob.getStatus().getError());
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, destinationTableName));
    }

    @Test
    public void testCreateAndGetJobWithSelectedFields() throws InterruptedException, TimeoutException {
        String sourceTableName = "test_create_and_get_job_with_selected_fields_source_table";
        String destinationTableName = "test_create_and_get_job_with_selected_fields_destination_table";
        TableId sourceTable = TableId.of(ITBigQueryTest.DATASET, sourceTableName);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(sourceTable, tableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(sourceTableName, createdTable.getTableId().getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, destinationTableName);
        CopyJobConfiguration configuration = CopyJobConfiguration.of(destinationTable, sourceTable);
        Job createdJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration), JobOption.fields(ETAG));
        CopyJobConfiguration createdConfiguration = createdJob.getConfiguration();
        Assert.assertNotNull(createdJob.getJobId());
        Assert.assertNotNull(createdConfiguration.getSourceTables());
        Assert.assertNotNull(createdConfiguration.getDestinationTable());
        Assert.assertNotNull(createdJob.getEtag());
        Assert.assertNull(createdJob.getStatistics());
        Assert.assertNull(createdJob.getStatus());
        Assert.assertNull(createdJob.getSelfLink());
        Assert.assertNull(createdJob.getUserEmail());
        Job remoteJob = ITBigQueryTest.bigquery.getJob(createdJob.getJobId(), JobOption.fields(ETAG));
        CopyJobConfiguration remoteConfiguration = remoteJob.getConfiguration();
        Assert.assertEquals(createdJob.getJobId(), remoteJob.getJobId());
        Assert.assertEquals(createdConfiguration.getSourceTables(), remoteConfiguration.getSourceTables());
        Assert.assertEquals(createdConfiguration.getDestinationTable(), remoteConfiguration.getDestinationTable());
        Assert.assertEquals(createdConfiguration.getCreateDisposition(), remoteConfiguration.getCreateDisposition());
        Assert.assertEquals(createdConfiguration.getWriteDisposition(), remoteConfiguration.getWriteDisposition());
        Assert.assertNotNull(remoteJob.getEtag());
        Assert.assertNull(remoteJob.getStatistics());
        Assert.assertNull(remoteJob.getStatus());
        Assert.assertNull(remoteJob.getSelfLink());
        Assert.assertNull(remoteJob.getUserEmail());
        Assert.assertTrue(createdTable.delete());
        Job completedJob = remoteJob.waitFor(RetryOption.initialRetryDelay(Duration.ofSeconds(1)), RetryOption.totalTimeout(Duration.ofMinutes(1)));
        Assert.assertNotNull(completedJob);
        Assert.assertNull(completedJob.getStatus().getError());
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, destinationTableName));
    }

    @Test
    public void testCopyJob() throws InterruptedException, TimeoutException {
        String sourceTableName = "test_copy_job_source_table";
        String destinationTableName = "test_copy_job_destination_table";
        TableId sourceTable = TableId.of(ITBigQueryTest.DATASET, sourceTableName);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(ITBigQueryTest.TABLE_SCHEMA);
        TableInfo tableInfo = TableInfo.of(sourceTable, tableDefinition);
        Table createdTable = ITBigQueryTest.bigquery.create(tableInfo);
        Assert.assertNotNull(createdTable);
        Assert.assertEquals(ITBigQueryTest.DATASET, createdTable.getTableId().getDataset());
        Assert.assertEquals(sourceTableName, createdTable.getTableId().getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, destinationTableName);
        CopyJobConfiguration configuration = CopyJobConfiguration.of(destinationTable, sourceTable);
        Job remoteJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration));
        remoteJob = remoteJob.waitFor();
        Assert.assertNull(remoteJob.getStatus().getError());
        Table remoteTable = ITBigQueryTest.bigquery.getTable(ITBigQueryTest.DATASET, destinationTableName);
        Assert.assertNotNull(remoteTable);
        Assert.assertEquals(destinationTable.getDataset(), remoteTable.getTableId().getDataset());
        Assert.assertEquals(destinationTableName, remoteTable.getTableId().getTable());
        Assert.assertEquals(ITBigQueryTest.TABLE_SCHEMA, remoteTable.getDefinition().getSchema());
        Assert.assertTrue(createdTable.delete());
        Assert.assertTrue(remoteTable.delete());
    }

    @Test
    public void testQueryJob() throws InterruptedException, TimeoutException {
        String tableName = "test_query_job_table";
        String query = "SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, tableName);
        QueryJobConfiguration configuration = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setDestinationTable(destinationTable).build();
        Job remoteJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration));
        remoteJob = remoteJob.waitFor();
        Assert.assertNull(remoteJob.getStatus().getError());
        TableResult result = remoteJob.getQueryResults();
        Assert.assertEquals(ITBigQueryTest.QUERY_RESULT_SCHEMA, result.getSchema());
        int rowCount = 0;
        for (FieldValueList row : result.getValues()) {
            FieldValue timestampCell = row.get(0);
            FieldValue stringCell = row.get(1);
            FieldValue booleanCell = row.get(2);
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            rowCount++;
        }
        Assert.assertEquals(2, rowCount);
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, tableName));
        Job queryJob = ITBigQueryTest.bigquery.getJob(remoteJob.getJobId());
        JobStatistics.QueryStatistics statistics = queryJob.getStatistics();
        Assert.assertNotNull(statistics.getQueryPlan());
    }

    @Test
    public void testQueryJobWithDryRun() throws InterruptedException, TimeoutException {
        String tableName = "test_query_job_table";
        String query = "SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, tableName);
        QueryJobConfiguration configuration = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setDestinationTable(destinationTable).setDryRun(true).build();
        Job remoteJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration));
        Assert.assertNull(remoteJob.getJobId().getJob());
        Assert.assertEquals(State.DONE, remoteJob.getStatus().getState());
        Assert.assertNotNull(remoteJob.getConfiguration());
    }

    @Test
    public void testExtractJob() throws InterruptedException, TimeoutException {
        String tableName = "test_export_job_table";
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, tableName);
        LoadJobConfiguration configuration = LoadJobConfiguration.newBuilder(destinationTable, ((("gs://" + (ITBigQueryTest.BUCKET)) + "/") + (ITBigQueryTest.LOAD_FILE))).setSchema(ITBigQueryTest.SIMPLE_SCHEMA).build();
        Job remoteLoadJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration));
        remoteLoadJob = remoteLoadJob.waitFor();
        Assert.assertNull(remoteLoadJob.getStatus().getError());
        ExtractJobConfiguration extractConfiguration = ExtractJobConfiguration.newBuilder(destinationTable, ((("gs://" + (ITBigQueryTest.BUCKET)) + "/") + (ITBigQueryTest.EXTRACT_FILE))).setPrintHeader(false).build();
        Job remoteExtractJob = ITBigQueryTest.bigquery.create(JobInfo.of(extractConfiguration));
        remoteExtractJob = remoteExtractJob.waitFor();
        Assert.assertNull(remoteExtractJob.getStatus().getError());
        String extractedCsv = new String(ITBigQueryTest.storage.readAllBytes(ITBigQueryTest.BUCKET, ITBigQueryTest.EXTRACT_FILE), StandardCharsets.UTF_8);
        Assert.assertEquals(Sets.newHashSet(ITBigQueryTest.CSV_CONTENT.split("\n")), Sets.newHashSet(extractedCsv.split("\n")));
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, tableName));
    }

    @Test
    public void testCancelJob() throws InterruptedException, TimeoutException {
        String destinationTableName = "test_cancel_query_job_table";
        String query = "SELECT TimestampField, StringField, BooleanField FROM " + (ITBigQueryTest.TABLE_ID.getTable());
        TableId destinationTable = TableId.of(ITBigQueryTest.DATASET, destinationTableName);
        QueryJobConfiguration configuration = QueryJobConfiguration.newBuilder(query).setDefaultDataset(DatasetId.of(ITBigQueryTest.DATASET)).setDestinationTable(destinationTable).build();
        Job remoteJob = ITBigQueryTest.bigquery.create(JobInfo.of(configuration));
        Assert.assertTrue(remoteJob.cancel());
        remoteJob = remoteJob.waitFor();
        Assert.assertNull(remoteJob.getStatus().getError());
    }

    @Test
    public void testCancelNonExistingJob() {
        Assert.assertFalse(ITBigQueryTest.bigquery.cancel("test_cancel_non_existing_job"));
    }

    @Test
    public void testInsertFromFile() throws IOException, InterruptedException, TimeoutException {
        String destinationTableName = "test_insert_from_file_table";
        TableId tableId = TableId.of(ITBigQueryTest.DATASET, destinationTableName);
        WriteChannelConfiguration configuration = WriteChannelConfiguration.newBuilder(tableId).setFormatOptions(FormatOptions.json()).setCreateDisposition(CREATE_IF_NEEDED).setSchema(ITBigQueryTest.TABLE_SCHEMA).build();
        TableDataWriteChannel channel = ITBigQueryTest.bigquery.writer(configuration);
        try {
            // A zero byte write should not throw an exception.
            Assert.assertEquals(0, channel.write(ByteBuffer.wrap("".getBytes(StandardCharsets.UTF_8))));
        } finally {
            // Force the channel to flush by calling `close`.
            channel.close();
        }
        channel = ITBigQueryTest.bigquery.writer(configuration);
        try {
            channel.write(ByteBuffer.wrap(ITBigQueryTest.JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
        } finally {
            channel.close();
        }
        Job job = channel.getJob().waitFor();
        LoadStatistics statistics = job.getStatistics();
        Assert.assertEquals(1L, statistics.getInputFiles().longValue());
        Assert.assertEquals(2L, statistics.getOutputRows().longValue());
        LoadJobConfiguration jobConfiguration = job.getConfiguration();
        Assert.assertEquals(ITBigQueryTest.TABLE_SCHEMA, jobConfiguration.getSchema());
        Assert.assertNull(jobConfiguration.getSourceUris());
        Assert.assertNull(job.getStatus().getError());
        Page<FieldValueList> rows = ITBigQueryTest.bigquery.listTableData(tableId);
        int rowCount = 0;
        for (FieldValueList row : rows.getValues()) {
            FieldValue timestampCell = row.get(0);
            FieldValue stringCell = row.get(1);
            FieldValue integerArrayCell = row.get(2);
            FieldValue booleanCell = row.get(3);
            FieldValue bytesCell = row.get(4);
            FieldValue recordCell = row.get(5);
            FieldValue integerCell = row.get(6);
            FieldValue floatCell = row.get(7);
            FieldValue geographyCell = row.get(8);
            FieldValue numericCell = row.get(9);
            Assert.assertEquals(PRIMITIVE, timestampCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, stringCell.getAttribute());
            Assert.assertEquals(FieldValue.Attribute.REPEATED, integerArrayCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, booleanCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, bytesCell.getAttribute());
            Assert.assertEquals(FieldValue.Attribute.RECORD, recordCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, integerCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, floatCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, geographyCell.getAttribute());
            Assert.assertEquals(PRIMITIVE, numericCell.getAttribute());
            Assert.assertEquals(1408452095220000L, timestampCell.getTimestampValue());
            Assert.assertEquals("stringValue", stringCell.getStringValue());
            Assert.assertEquals(0, integerArrayCell.getRepeatedValue().get(0).getLongValue());
            Assert.assertEquals(1, integerArrayCell.getRepeatedValue().get(1).getLongValue());
            Assert.assertEquals(false, booleanCell.getBooleanValue());
            Assert.assertArrayEquals(ITBigQueryTest.BYTES, bytesCell.getBytesValue());
            Assert.assertEquals((-14182916000000L), recordCell.getRecordValue().get(0).getTimestampValue());
            Assert.assertTrue(recordCell.getRecordValue().get(1).isNull());
            Assert.assertEquals(1, recordCell.getRecordValue().get(2).getRepeatedValue().get(0).getLongValue());
            Assert.assertEquals(0, recordCell.getRecordValue().get(2).getRepeatedValue().get(1).getLongValue());
            Assert.assertEquals(true, recordCell.getRecordValue().get(3).getBooleanValue());
            Assert.assertEquals(3, integerCell.getLongValue());
            Assert.assertEquals(1.2, floatCell.getDoubleValue(), 1.0E-4);
            Assert.assertEquals("POINT(-122.35022 47.649154)", geographyCell.getStringValue());
            Assert.assertEquals(new BigDecimal("123456.789012345"), numericCell.getNumericValue());
            rowCount++;
        }
        Assert.assertEquals(2, rowCount);
        Assert.assertTrue(ITBigQueryTest.bigquery.delete(ITBigQueryTest.DATASET, destinationTableName));
    }

    @Test
    public void testLocation() throws Exception {
        String location = "EU";
        String wrongLocation = "US";
        assertThat(location).isNotEqualTo(wrongLocation);
        Dataset dataset = ITBigQueryTest.bigquery.create(DatasetInfo.newBuilder(("locationset_" + (UUID.randomUUID().toString().replace("-", "_")))).setLocation(location).build());
        try {
            TableId tableId = TableId.of(dataset.getDatasetId().getDataset(), "sometable");
            Schema schema = Schema.of(Field.of("name", STRING));
            TableDefinition tableDef = StandardTableDefinition.of(schema);
            Table table = ITBigQueryTest.bigquery.create(TableInfo.newBuilder(tableId, tableDef).build());
            String query = String.format("SELECT * FROM `%s.%s.%s`", table.getTableId().getProject(), table.getTableId().getDataset(), table.getTableId().getTable());
            // Test create/get
            {
                Job job = ITBigQueryTest.bigquery.create(JobInfo.of(JobId.newBuilder().setLocation(location).build(), QueryJobConfiguration.of(query)));
                job = job.waitFor();
                assertThat(job.getStatus().getError()).isNull();
                assertThat(job.getJobId().getLocation()).isEqualTo(location);
                JobId jobId = job.getJobId();
                JobId wrongId = jobId.toBuilder().setLocation(wrongLocation).build();
                // Getting with location should work.
                assertThat(ITBigQueryTest.bigquery.getJob(jobId)).isNotNull();
                // Getting with wrong location shouldn't work.
                assertThat(ITBigQueryTest.bigquery.getJob(wrongId)).isNull();
                // Cancelling with location should work. (Cancelling already finished job is fine.)
                assertThat(ITBigQueryTest.bigquery.cancel(jobId)).isTrue();
                // Cancelling with wrong location shouldn't work.
                assertThat(ITBigQueryTest.bigquery.cancel(wrongId)).isFalse();
            }
            // Test query
            {
                assertThat(ITBigQueryTest.bigquery.query(QueryJobConfiguration.of(query), JobId.newBuilder().setLocation(location).build()).iterateAll()).isEmpty();
                try {
                    ITBigQueryTest.bigquery.query(QueryJobConfiguration.of(query), JobId.newBuilder().setLocation(wrongLocation).build()).iterateAll();
                    Assert.fail("querying a table with wrong location shouldn't work");
                } catch (BigQueryException e) {
                    // Nothing to do
                }
            }
            // Test write
            {
                WriteChannelConfiguration writeChannelConfiguration = WriteChannelConfiguration.newBuilder(tableId).setFormatOptions(FormatOptions.csv()).build();
                try (TableDataWriteChannel writer = ITBigQueryTest.bigquery.writer(JobId.newBuilder().setLocation(location).build(), writeChannelConfiguration)) {
                    writer.write(ByteBuffer.wrap("foo".getBytes()));
                }
                try {
                    ITBigQueryTest.bigquery.writer(JobId.newBuilder().setLocation(wrongLocation).build(), writeChannelConfiguration);
                    Assert.fail("writing to a table with wrong location shouldn't work");
                } catch (BigQueryException e) {
                    // Nothing to do
                }
            }
        } finally {
            ITBigQueryTest.bigquery.delete(dataset.getDatasetId(), DatasetDeleteOption.deleteContents());
        }
    }
}

