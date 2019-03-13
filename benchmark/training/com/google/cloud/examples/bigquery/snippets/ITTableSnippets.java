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
import LegacySQLTypeName.STRING;
import TableField.LAST_MODIFIED_TIME;
import TableField.NUM_ROWS;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Integration tests for {@link TableSnippets}.
 */
public class ITTableSnippets {
    private static final String BASE_TABLE_NAME = "my_table";

    private static final String DATASET_NAME = RemoteBigQueryHelper.generateDatasetName();

    private static final String COPY_DATASET_NAME = RemoteBigQueryHelper.generateDatasetName();

    private static final String BUCKET_NAME = RemoteStorageHelper.generateBucketName();

    private static final Schema SCHEMA = Schema.of(Field.of("stringField", STRING), Field.of("booleanField", BOOLEAN));

    private static final List<?> ROW1 = ImmutableList.of("value1", true);

    private static final List<?> ROW2 = ImmutableList.of("value2", false);

    private static final String DOOMED_TABLE_NAME = "doomed_table";

    private static final TableId DOOMED_TABLE_ID = TableId.of(ITTableSnippets.DATASET_NAME, ITTableSnippets.DOOMED_TABLE_NAME);

    private static BigQuery bigquery;

    private static Storage storage;

    private static int nextTableNumber;

    private Table table;

    private TableSnippets tableSnippets;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testExists() {
        Assert.assertTrue(tableSnippets.exists());
    }

    @Test
    public void testReloadTableWithFields() {
        Table latestTable = tableSnippets.reloadTableWithFields(LAST_MODIFIED_TIME, NUM_ROWS);
        Assert.assertNotNull(latestTable);
        Assert.assertNotNull(latestTable.getLastModifiedTime());
    }

    @Test
    public void testUpdate() {
        Table updatedTable = tableSnippets.update();
        Assert.assertEquals("new description", updatedTable.getDescription());
    }

    @Test
    public void testDelete() {
        Table doomedTable = ITTableSnippets.bigquery.create(TableInfo.of(ITTableSnippets.DOOMED_TABLE_ID, StandardTableDefinition.of(ITTableSnippets.SCHEMA)));
        TableSnippets doomedTableSnippets = new TableSnippets(doomedTable);
        Assert.assertTrue(doomedTableSnippets.delete());
    }

    @Test
    public void testInsert() throws InterruptedException {
        InsertAllResponse response = tableSnippets.insert("row1", "row2");
        Assert.assertFalse(response.hasErrors());
        verifyTestRows(table);
    }

    @Test
    public void testInsertParams() throws InterruptedException {
        InsertAllResponse response = tableSnippets.insertWithParams("row1", "row2");
        Assert.assertFalse(response.hasErrors());
        List<FieldValueList> rows = ImmutableList.copyOf(tableSnippets.list().getValues());
        while (rows.isEmpty()) {
            Thread.sleep(500);
            rows = ImmutableList.copyOf(tableSnippets.list().getValues());
        } 
        Set<List<?>> values = FluentIterable.from(rows).transform(new Function<FieldValueList, List<?>>() {
            @Override
            public List<?> apply(FieldValueList row) {
                return ImmutableList.of(row.get(0).getStringValue(), row.get(1).getBooleanValue());
            }
        }).toSet();
        Assert.assertEquals(ImmutableSet.of(ITTableSnippets.ROW2), values);
    }

    @Test
    public void testList() throws InterruptedException {
        List<FieldValueList> rows = ImmutableList.copyOf(tableSnippets.list().getValues());
        Assert.assertEquals(0, rows.size());
        InsertAllResponse response = tableSnippets.insert("row1", "row2");
        Assert.assertFalse(response.hasErrors());
        rows = ImmutableList.copyOf(tableSnippets.list().getValues());
        while (rows.isEmpty()) {
            Thread.sleep(500);
            rows = ImmutableList.copyOf(tableSnippets.list().getValues());
        } 
        Assert.assertEquals(2, rows.size());
    }

    @Test
    public void testCopy() {
        tableSnippets.copy(ITTableSnippets.COPY_DATASET_NAME, ITTableSnippets.BASE_TABLE_NAME);
    }

    @Test
    public void testCopyTableId() {
        Job copyJob = tableSnippets.copyTableId(ITTableSnippets.COPY_DATASET_NAME, getCopyTableName());
        assertSuccessful(copyJob);
    }

    @Test
    public void testExtractAndLoadList() {
        String gcsFile1 = ("gs://" + (ITTableSnippets.BUCKET_NAME)) + "/extractTestA_*.csv";
        String gcsFile2 = ("gs://" + (ITTableSnippets.BUCKET_NAME)) + "/extractTestB_*.csv";
        Job extractJob = tableSnippets.extractList("CSV", gcsFile1, gcsFile2);
        gcsFile1 = gcsFile1.replace("*", "000000000000");
        gcsFile2 = gcsFile2.replace("*", "000000000000");
        assertSuccessful(extractJob);
        Job loadJob = tableSnippets.loadList(gcsFile1, gcsFile2);
        assertSuccessful(loadJob);
    }

    @Test
    public void testExtractAndLoadSingle() {
        String gcsFile = ("gs://" + (ITTableSnippets.BUCKET_NAME)) + "/extractTest.csv";
        Job extractJob = tableSnippets.extractSingle("CSV", gcsFile);
        assertSuccessful(extractJob);
        Job loadJob = tableSnippets.loadSingle(gcsFile);
        assertSuccessful(loadJob);
    }
}

