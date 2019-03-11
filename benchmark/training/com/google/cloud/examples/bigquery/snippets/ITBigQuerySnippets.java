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
package com.google.cloud.examples.bigquery.snippets;


import LegacySQLTypeName.BOOLEAN;
import LegacySQLTypeName.BYTES;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITBigQuerySnippets {
    private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final String OTHER_DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final String QUERY = "SELECT unique(corpus) FROM [bigquery-public-data:samples.shakespeare]";

    private static final Function<Job, JobId> TO_JOB_ID_FUNCTION = new Function<Job, JobId>() {
        @Override
        public JobId apply(Job job) {
            return job.getJobId();
        }
    };

    private static final Function<Table, TableId> TO_TABLE_ID_FUNCTION = new Function<Table, TableId>() {
        @Override
        public TableId apply(Table table) {
            return table.getTableId();
        }
    };

    private static final Function<Dataset, DatasetId> TO_DATASET_ID_FUNCTION = new Function<Dataset, DatasetId>() {
        @Override
        public DatasetId apply(Dataset dataset) {
            return dataset.getDatasetId();
        }
    };

    private static BigQuery bigquery;

    private static BigQuerySnippets bigquerySnippets;

    private static ByteArrayOutputStream bout;

    private static PrintStream out;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testCreateGetAndDeleteTable() throws InterruptedException {
        String tableName = "test_create_get_delete";
        String fieldName = "aField";
        Table table = ITBigQuerySnippets.bigquerySnippets.createTable(ITBigQuerySnippets.DATASET, tableName, fieldName);
        Assert.assertNotNull(table);
        TableId tableId = TableId.of(ITBigQuerySnippets.bigquery.getOptions().getProjectId(), ITBigQuerySnippets.DATASET, tableName);
        Assert.assertEquals(tableId, ITBigQuerySnippets.bigquerySnippets.getTable(tableId.getDataset(), tableId.getTable()).getTableId());
        Assert.assertNotNull(ITBigQuerySnippets.bigquerySnippets.updateTableDescription(ITBigQuerySnippets.DATASET, tableName, "new description"));
        table = ITBigQuerySnippets.bigquerySnippets.updateTableExpiration(ITBigQuerySnippets.DATASET, tableName);
        Assert.assertNotNull(table.getExpirationTime());
        Assert.assertEquals("new description", ITBigQuerySnippets.bigquerySnippets.getTableFromId(tableId.getProject(), tableId.getDataset(), tableId.getTable()).getDescription());
        Set<TableId> tables = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listTables(ITBigQuerySnippets.DATASET).iterateAll().iterator(), ITBigQuerySnippets.TO_TABLE_ID_FUNCTION));
        while (!(tables.contains(tableId))) {
            Thread.sleep(500);
            tables = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listTables(ITBigQuerySnippets.DATASET).iterateAll().iterator(), ITBigQuerySnippets.TO_TABLE_ID_FUNCTION));
        } 
        tables = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listTablesFromId(tableId.getProject(), ITBigQuerySnippets.DATASET).iterateAll().iterator(), ITBigQuerySnippets.TO_TABLE_ID_FUNCTION));
        while (!(tables.contains(tableId))) {
            Thread.sleep(500);
            tables = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listTablesFromId(tableId.getProject(), ITBigQuerySnippets.DATASET).iterateAll().iterator(), ITBigQuerySnippets.TO_TABLE_ID_FUNCTION));
        } 
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.deleteTable(ITBigQuerySnippets.DATASET, tableName));
        Assert.assertFalse(ITBigQuerySnippets.bigquerySnippets.deleteTableFromId(tableId.getProject(), ITBigQuerySnippets.DATASET, tableName));
    }

    @Test
    public void testCreateGetAndDeleteDataset() throws InterruptedException {
        DatasetId datasetId = DatasetId.of(ITBigQuerySnippets.bigquery.getOptions().getProjectId(), ITBigQuerySnippets.OTHER_DATASET);
        Dataset dataset = ITBigQuerySnippets.bigquerySnippets.createDataset(ITBigQuerySnippets.OTHER_DATASET);
        Assert.assertNotNull(dataset);
        Assert.assertEquals(datasetId, ITBigQuerySnippets.bigquerySnippets.getDataset(ITBigQuerySnippets.OTHER_DATASET).getDatasetId());
        Assert.assertNotNull(ITBigQuerySnippets.bigquerySnippets.updateDataset(ITBigQuerySnippets.OTHER_DATASET, "new description"));
        Assert.assertEquals("new description", ITBigQuerySnippets.bigquerySnippets.getDatasetFromId(datasetId.getProject(), ITBigQuerySnippets.OTHER_DATASET).getDescription());
        Set<DatasetId> datasets = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listDatasets().iterateAll().iterator(), ITBigQuerySnippets.TO_DATASET_ID_FUNCTION));
        while (!(datasets.contains(datasetId))) {
            Thread.sleep(500);
            datasets = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listDatasets().iterateAll().iterator(), ITBigQuerySnippets.TO_DATASET_ID_FUNCTION));
        } 
        datasets = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listDatasets(datasetId.getProject()).iterateAll().iterator(), ITBigQuerySnippets.TO_DATASET_ID_FUNCTION));
        while (!(datasets.contains(datasetId))) {
            Thread.sleep(500);
            datasets = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listDatasets(datasetId.getProject()).iterateAll().iterator(), ITBigQuerySnippets.TO_DATASET_ID_FUNCTION));
        } 
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.deleteDataset(ITBigQuerySnippets.OTHER_DATASET));
        Assert.assertFalse(ITBigQuerySnippets.bigquerySnippets.deleteDatasetFromId(datasetId.getProject(), ITBigQuerySnippets.OTHER_DATASET));
    }

    @Test
    public void testWriteAndListTableData() throws IOException, InterruptedException, URISyntaxException, TimeoutException {
        // Create table
        String tableName = "test_write_and_list_table_data";
        String fieldName = "string_field";
        Assert.assertNotNull(ITBigQuerySnippets.bigquerySnippets.createTable(ITBigQuerySnippets.DATASET, tableName, fieldName));
        // Add rows from string
        long outputRows = ITBigQuerySnippets.bigquerySnippets.writeToTable(ITBigQuerySnippets.DATASET, tableName, "StringValue1\nStringValue2\n");
        Assert.assertEquals(2L, outputRows);
        // Add rows from file
        Path csvPath = Paths.get(Resources.getResource("bigquery/test_write_and_list_table_data.csv").toURI());
        outputRows = ITBigQuerySnippets.bigquerySnippets.writeFileToTable(ITBigQuerySnippets.DATASET, tableName, csvPath, "us");
        Assert.assertEquals(2L, outputRows);
        // List all rows
        TableResult tableData = ITBigQuerySnippets.bigquerySnippets.listTableData(ITBigQuerySnippets.DATASET, tableName);
        String tableDataString = tableData.toString();
        Assert.assertTrue(tableDataString.contains("StringValue1"));
        Assert.assertTrue(tableDataString.contains("StringValue2"));
        Assert.assertTrue(tableDataString.contains("StringValue3"));
        Assert.assertTrue(tableDataString.contains("StringValue4"));
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.deleteTable(ITBigQuerySnippets.DATASET, tableName));
    }

    @Test
    public void testWriteRemoteJsonToTable() throws InterruptedException {
        String datasetName = "test_dataset";
        String tableName = "us_states";
        Table table = ITBigQuerySnippets.bigquery.getTable(datasetName, tableName);
        Assert.assertNull(table);
        Long result = ITBigQuerySnippets.bigquerySnippets.writeRemoteFileToTable(datasetName, tableName);
        table = ITBigQuerySnippets.bigquery.getTable(datasetName, tableName);
        Assert.assertNotNull(table);
        ArrayList<String> tableFieldNames = new ArrayList<>();
        for (Field field : table.getDefinition().getSchema().getFields()) {
            tableFieldNames.add(field.getName());
        }
        ITBigQuerySnippets.bigquery.delete(table.getTableId());
        Assert.assertEquals(Long.valueOf(50), result);
    }

    @Test
    public void testInsertAllAndListTableData() throws IOException, InterruptedException {
        String tableName = "test_insert_all_and_list_table_data";
        String fieldName1 = "booleanField";
        String fieldName2 = "bytesField";
        String fieldName3 = "recordField";
        String fieldName4 = "stringField";
        TableId tableId = TableId.of(ITBigQuerySnippets.DATASET, tableName);
        Schema schema = Schema.of(Field.of(fieldName1, BOOLEAN), Field.of(fieldName2, BYTES), Field.of(fieldName3, RECORD, Field.of(fieldName4, STRING)));
        TableInfo table = TableInfo.of(tableId, StandardTableDefinition.of(schema));
        Assert.assertNotNull(ITBigQuerySnippets.bigquery.create(table));
        InsertAllResponse response = ITBigQuerySnippets.bigquerySnippets.insertAll(ITBigQuerySnippets.DATASET, tableName);
        Assert.assertFalse(response.hasErrors());
        Assert.assertTrue(response.getInsertErrors().isEmpty());
        Page<FieldValueList> listPage = ITBigQuerySnippets.bigquerySnippets.listTableDataFromId(ITBigQuerySnippets.DATASET, tableName);
        while ((Iterators.size(listPage.iterateAll().iterator())) < 1) {
            Thread.sleep(500);
            listPage = ITBigQuerySnippets.bigquerySnippets.listTableDataFromId(ITBigQuerySnippets.DATASET, tableName);
        } 
        FieldValueList row = listPage.getValues().iterator().next();
        Assert.assertEquals(true, row.get(0).getBooleanValue());
        Assert.assertArrayEquals(new byte[]{ 10, 13, 13, 14, 13 }, row.get(1).getBytesValue());
        Assert.assertEquals("Hello, World!", row.get(2).getRecordValue().get(0).getStringValue());
        listPage = ITBigQuerySnippets.bigquerySnippets.listTableDataSchema(ITBigQuerySnippets.DATASET, tableName, schema, fieldName1);
        row = listPage.getValues().iterator().next();
        Assert.assertNotNull(row.get(fieldName1));
        Assert.assertArrayEquals(new byte[]{ 10, 13, 13, 14, 13 }, row.get(fieldName2).getBytesValue());
        ITBigQuerySnippets.bigquerySnippets.listTableDataSchemaId();
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.deleteTable(ITBigQuerySnippets.DATASET, tableName));
    }

    @Test
    public void testJob() throws InterruptedException, ExecutionException {
        Job job1 = ITBigQuerySnippets.bigquerySnippets.createJob(ITBigQuerySnippets.QUERY);
        Job job2 = ITBigQuerySnippets.bigquerySnippets.createJob(ITBigQuerySnippets.QUERY);
        Assert.assertNotNull(job1);
        Assert.assertNotNull(job2);
        Assert.assertEquals(job1.getJobId(), ITBigQuerySnippets.bigquerySnippets.getJob(job1.getJobId().getJob()).getJobId());
        Assert.assertEquals(job2.getJobId(), ITBigQuerySnippets.bigquerySnippets.getJobFromId(job2.getJobId().getJob()).getJobId());
        Set<JobId> jobs = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listJobs().iterateAll().iterator(), ITBigQuerySnippets.TO_JOB_ID_FUNCTION));
        while ((!(jobs.contains(job1.getJobId()))) || (!(jobs.contains(job2.getJobId())))) {
            Thread.sleep(500);
            jobs = Sets.newHashSet(Iterators.transform(ITBigQuerySnippets.bigquerySnippets.listJobs().iterateAll().iterator(), ITBigQuerySnippets.TO_JOB_ID_FUNCTION));
        } 
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.cancelJob(job1.getJobId().getJob()));
        Assert.assertTrue(ITBigQuerySnippets.bigquerySnippets.cancelJobFromId(job2.getJobId().getJob()));
    }

    @Test
    public void testRunQuery() throws InterruptedException {
        ITBigQuerySnippets.bigquerySnippets.runQuery();
        String got = ITBigQuerySnippets.bout.toString();
        Assert.assertTrue(got.contains("romeoandjuliet"));
    }
}

