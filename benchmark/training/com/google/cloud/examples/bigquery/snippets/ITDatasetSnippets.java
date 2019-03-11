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


import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.Dataset.Builder;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class ITDatasetSnippets {
    private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final String NON_EXISTING_DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static final String FRIENDLY_NAME = "some_friendly_name";

    private static BigQuery bigquery;

    private static Dataset nonExistingDataset;

    private static Dataset dataset;

    private static DatasetSnippets datasetSnippets;

    private static DatasetSnippets nonExistingDatasetSnippets;

    @Test
    public void testExistsNonExistingDataset() {
        Assert.assertFalse(ITDatasetSnippets.nonExistingDatasetSnippets.doesDatasetExist());
    }

    @Test
    public void testExists() {
        Assert.assertTrue(ITDatasetSnippets.datasetSnippets.doesDatasetExist());
    }

    @Test
    public void testReloadNonExistingDataset() {
        Assert.assertNull(ITDatasetSnippets.nonExistingDatasetSnippets.reloadDataset());
    }

    @Test
    public void testReload() {
        Assert.assertNull(ITDatasetSnippets.dataset.getFriendlyName());
        Builder builder = ITDatasetSnippets.dataset.toBuilder();
        builder.setFriendlyName(ITDatasetSnippets.FRIENDLY_NAME);
        builder.build().update();
        Dataset reloadedDataset = ITDatasetSnippets.datasetSnippets.reloadDataset();
        Assert.assertEquals(ITDatasetSnippets.FRIENDLY_NAME, reloadedDataset.getFriendlyName());
    }

    @Test
    public void testDeleteNonExistingDataset() {
        Assert.assertFalse(ITDatasetSnippets.nonExistingDatasetSnippets.deleteDataset());
    }

    @Test
    public void testDelete() {
        String datasetName = RemoteBigQueryHelper.generateDatasetName();
        DatasetInfo dataset = DatasetInfo.newBuilder(datasetName).build();
        DatasetSnippets datasetSnippets = new DatasetSnippets(ITDatasetSnippets.bigquery.create(dataset));
        Assert.assertTrue(datasetSnippets.deleteDataset());
    }

    @Test
    public void testListTablesEmpty() {
        Page<Table> tables = ITDatasetSnippets.datasetSnippets.list();
        Assert.assertFalse(tables.iterateAll().iterator().hasNext());
    }

    @Test
    public void testListTablesNotEmpty() {
        String expectedTableName = "test_table";
        ITDatasetSnippets.dataset.create(expectedTableName, StandardTableDefinition.newBuilder().build());
        Page<Table> tables = ITDatasetSnippets.datasetSnippets.list();
        Iterator<Table> iterator = tables.iterateAll().iterator();
        Assert.assertTrue(iterator.hasNext());
        Table actualTable = iterator.next();
        Assert.assertEquals(expectedTableName, actualTable.getTableId().getTable());
        Assert.assertFalse(iterator.hasNext());
        ITDatasetSnippets.bigquery.delete(ITDatasetSnippets.DATASET, expectedTableName);
    }

    @Test
    public void testGetTable() {
        String expectedTableName = "test_table";
        ITDatasetSnippets.dataset.create(expectedTableName, StandardTableDefinition.newBuilder().build());
        Table actualTable = ITDatasetSnippets.datasetSnippets.getTable(expectedTableName);
        Assert.assertNotNull(actualTable);
        Assert.assertEquals(expectedTableName, actualTable.getTableId().getTable());
        ITDatasetSnippets.bigquery.delete(ITDatasetSnippets.DATASET, expectedTableName);
    }

    @Test
    public void testCreateTable() {
        String expectedTableName = "test_table";
        String expectedFieldName = "test_field";
        Table actualTable = ITDatasetSnippets.datasetSnippets.createTable(expectedTableName, expectedFieldName);
        Assert.assertNotNull(actualTable);
        Assert.assertEquals(expectedTableName, actualTable.getTableId().getTable());
        Assert.assertEquals(1, actualTable.getDefinition().getSchema().getFields().size());
        Field actualField = actualTable.getDefinition().getSchema().getFields().get(0);
        Assert.assertEquals(expectedFieldName, actualField.getName());
        ITDatasetSnippets.bigquery.delete(ITDatasetSnippets.DATASET, expectedTableName);
    }
}

