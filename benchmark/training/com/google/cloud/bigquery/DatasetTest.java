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


import Acl.Group;
import Acl.Role.READER;
import BigQuery.DatasetOption;
import BigQuery.TableListOption;
import BigQuery.TableOption;
import LegacySQLTypeName.INTEGER;
import com.google.api.gax.paging.Page;
import com.google.cloud.PageImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DatasetTest {
    private static final DatasetId DATASET_ID = DatasetId.of("dataset");

    private static final List<Acl> ACCESS_RULES = ImmutableList.of(Acl.of(Group.ofAllAuthenticatedUsers(), READER), Acl.of(new Acl.View(TableId.of("dataset", "table"))));

    private static final Map<String, String> LABELS = ImmutableMap.of("example-label1", "example-value1", "example-label2", "example-value2");

    private static final Long CREATION_TIME = System.currentTimeMillis();

    private static final Long DEFAULT_TABLE_EXPIRATION = (DatasetTest.CREATION_TIME) + 100;

    private static final String DESCRIPTION = "description";

    private static final String ETAG = "0xFF00";

    private static final String FRIENDLY_NAME = "friendlyDataset";

    private static final String GENERATED_ID = "P/D:1";

    private static final Long LAST_MODIFIED = (DatasetTest.CREATION_TIME) + 50;

    private static final String LOCATION = "";

    private static final String SELF_LINK = "http://bigquery/p/d";

    private static final DatasetInfo DATASET_INFO = DatasetInfo.newBuilder(DatasetTest.DATASET_ID).build();

    private static final Field FIELD = Field.of("FieldName", INTEGER);

    private static final StandardTableDefinition TABLE_DEFINITION = StandardTableDefinition.of(Schema.of(DatasetTest.FIELD));

    private static final ViewDefinition VIEW_DEFINITION = ViewDefinition.of("QUERY");

    private static final ExternalTableDefinition EXTERNAL_TABLE_DEFINITION = ExternalTableDefinition.of(ImmutableList.of("URI"), Schema.of(), FormatOptions.csv());

    private static final TableInfo TABLE_INFO1 = TableInfo.newBuilder(TableId.of("dataset", "table1"), DatasetTest.TABLE_DEFINITION).build();

    private static final TableInfo TABLE_INFO2 = TableInfo.newBuilder(TableId.of("dataset", "table2"), DatasetTest.VIEW_DEFINITION).build();

    private static final TableInfo TABLE_INFO3 = TableInfo.newBuilder(TableId.of("dataset", "table3"), DatasetTest.EXTERNAL_TABLE_DEFINITION).build();

    private BigQuery serviceMockReturnsOptions = createStrictMock(BigQuery.class);

    private BigQueryOptions mockOptions = createMock(BigQueryOptions.class);

    private BigQuery bigquery;

    private Dataset expectedDataset;

    private Dataset dataset;

    @Test
    public void testBuilder() {
        initializeExpectedDataset(2);
        replay(bigquery);
        Dataset builtDataset = new Dataset.Builder(serviceMockReturnsOptions, DatasetTest.DATASET_ID).setAcl(DatasetTest.ACCESS_RULES).setCreationTime(DatasetTest.CREATION_TIME).setDefaultTableLifetime(DatasetTest.DEFAULT_TABLE_EXPIRATION).setDescription(DatasetTest.DESCRIPTION).setEtag(DatasetTest.ETAG).setFriendlyName(DatasetTest.FRIENDLY_NAME).setGeneratedId(DatasetTest.GENERATED_ID).setLastModified(DatasetTest.LAST_MODIFIED).setLocation(DatasetTest.LOCATION).setSelfLink(DatasetTest.SELF_LINK).setLabels(DatasetTest.LABELS).build();
        Assert.assertEquals(DatasetTest.DATASET_ID, builtDataset.getDatasetId());
        Assert.assertEquals(DatasetTest.ACCESS_RULES, builtDataset.getAcl());
        Assert.assertEquals(DatasetTest.CREATION_TIME, builtDataset.getCreationTime());
        Assert.assertEquals(DatasetTest.DEFAULT_TABLE_EXPIRATION, builtDataset.getDefaultTableLifetime());
        Assert.assertEquals(DatasetTest.DESCRIPTION, builtDataset.getDescription());
        Assert.assertEquals(DatasetTest.ETAG, builtDataset.getEtag());
        Assert.assertEquals(DatasetTest.FRIENDLY_NAME, builtDataset.getFriendlyName());
        Assert.assertEquals(DatasetTest.GENERATED_ID, builtDataset.getGeneratedId());
        Assert.assertEquals(DatasetTest.LAST_MODIFIED, builtDataset.getLastModified());
        Assert.assertEquals(DatasetTest.LOCATION, builtDataset.getLocation());
        Assert.assertEquals(DatasetTest.SELF_LINK, builtDataset.getSelfLink());
        Assert.assertEquals(DatasetTest.LABELS, builtDataset.getLabels());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedDataset(4);
        replay(bigquery);
        compareDataset(expectedDataset, expectedDataset.toBuilder().build());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedDataset(1);
        BigQuery[] expectedOptions = new DatasetOption[]{ DatasetOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getDataset(DatasetTest.DATASET_INFO.getDatasetId(), expectedOptions)).andReturn(expectedDataset);
        replay(bigquery);
        initializeDataset();
        Assert.assertTrue(dataset.exists());
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedDataset(1);
        BigQuery[] expectedOptions = new DatasetOption[]{ DatasetOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getDataset(DatasetTest.DATASET_INFO.getDatasetId(), expectedOptions)).andReturn(null);
        replay(bigquery);
        initializeDataset();
        Assert.assertFalse(dataset.exists());
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedDataset(4);
        DatasetInfo updatedInfo = DatasetTest.DATASET_INFO.toBuilder().setDescription("Description").build();
        Dataset expectedDataset = new Dataset(serviceMockReturnsOptions, new DatasetInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getDataset(DatasetTest.DATASET_INFO.getDatasetId().getDataset())).andReturn(expectedDataset);
        replay(bigquery);
        initializeDataset();
        Dataset updatedDataset = dataset.reload();
        compareDataset(expectedDataset, updatedDataset);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedDataset(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getDataset(DatasetTest.DATASET_INFO.getDatasetId().getDataset())).andReturn(null);
        replay(bigquery);
        initializeDataset();
        Assert.assertNull(dataset.reload());
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedDataset(4);
        DatasetInfo updatedInfo = DatasetTest.DATASET_INFO.toBuilder().setDescription("Description").build();
        Dataset expectedDataset = new Dataset(serviceMockReturnsOptions, new DatasetInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getDataset(DatasetTest.DATASET_INFO.getDatasetId().getDataset(), DatasetOption.fields())).andReturn(expectedDataset);
        replay(bigquery);
        initializeDataset();
        Dataset updatedDataset = dataset.reload(DatasetOption.fields());
        compareDataset(expectedDataset, updatedDataset);
    }

    @Test
    public void testUpdate() {
        initializeExpectedDataset(4);
        Dataset expectedUpdatedDataset = expectedDataset.toBuilder().setDescription("Description").build();
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.update(eq(expectedDataset))).andReturn(expectedUpdatedDataset);
        replay(bigquery);
        initializeDataset();
        Dataset actualUpdatedDataset = dataset.update();
        compareDataset(expectedUpdatedDataset, actualUpdatedDataset);
    }

    @Test
    public void testUpdateWithOptions() {
        initializeExpectedDataset(4);
        Dataset expectedUpdatedDataset = expectedDataset.toBuilder().setDescription("Description").build();
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.update(eq(expectedDataset), eq(DatasetOption.fields()))).andReturn(expectedUpdatedDataset);
        replay(bigquery);
        initializeDataset();
        Dataset actualUpdatedDataset = dataset.update(DatasetOption.fields());
        compareDataset(expectedUpdatedDataset, actualUpdatedDataset);
    }

    @Test
    public void testDeleteTrue() {
        initializeExpectedDataset(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.delete(DatasetTest.DATASET_INFO.getDatasetId())).andReturn(true);
        replay(bigquery);
        initializeDataset();
        Assert.assertTrue(dataset.delete());
    }

    @Test
    public void testDeleteFalse() {
        initializeExpectedDataset(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.delete(DatasetTest.DATASET_INFO.getDatasetId())).andReturn(false);
        replay(bigquery);
        initializeDataset();
        Assert.assertFalse(dataset.delete());
    }

    @Test
    public void testList() throws Exception {
        initializeExpectedDataset(4);
        List<Table> tableResults = ImmutableList.of(new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO1)), new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO2)), new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO3)));
        PageImpl<Table> expectedPage = new PageImpl(null, "c", tableResults);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.listTables(DatasetTest.DATASET_INFO.getDatasetId())).andReturn(expectedPage);
        replay(bigquery);
        initializeDataset();
        Page<Table> tablePage = dataset.list();
        Assert.assertArrayEquals(tableResults.toArray(), Iterables.toArray(tablePage.getValues(), Table.class));
        Assert.assertEquals(expectedPage.getNextPageToken(), tablePage.getNextPageToken());
    }

    @Test
    public void testListWithOptions() throws Exception {
        initializeExpectedDataset(4);
        List<Table> tableResults = ImmutableList.of(new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO1)), new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO2)), new Table(serviceMockReturnsOptions, new Table.BuilderImpl(DatasetTest.TABLE_INFO3)));
        PageImpl<Table> expectedPage = new PageImpl(null, "c", tableResults);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.listTables(DatasetTest.DATASET_INFO.getDatasetId(), TableListOption.pageSize(10L))).andReturn(expectedPage);
        replay(bigquery);
        initializeDataset();
        Page<Table> tablePage = dataset.list(TableListOption.pageSize(10L));
        Assert.assertArrayEquals(tableResults.toArray(), Iterables.toArray(tablePage.getValues(), Table.class));
        Assert.assertEquals(expectedPage.getNextPageToken(), tablePage.getNextPageToken());
    }

    @Test
    public void testGet() throws Exception {
        initializeExpectedDataset(2);
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(DatasetTest.TABLE_INFO1));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(DatasetTest.TABLE_INFO1.getTableId())).andReturn(expectedTable);
        replay(bigquery);
        initializeDataset();
        Table table = dataset.get(DatasetTest.TABLE_INFO1.getTableId().getTable());
        Assert.assertNotNull(table);
        Assert.assertEquals(expectedTable, table);
    }

    @Test
    public void testGetNull() throws Exception {
        initializeExpectedDataset(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(DatasetTest.TABLE_INFO1.getTableId())).andReturn(null);
        replay(bigquery);
        initializeDataset();
        Assert.assertNull(dataset.get(DatasetTest.TABLE_INFO1.getTableId().getTable()));
    }

    @Test
    public void testGetWithOptions() throws Exception {
        initializeExpectedDataset(2);
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(DatasetTest.TABLE_INFO1));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(DatasetTest.TABLE_INFO1.getTableId(), TableOption.fields())).andReturn(expectedTable);
        replay(bigquery);
        initializeDataset();
        Table table = dataset.get(DatasetTest.TABLE_INFO1.getTableId().getTable(), TableOption.fields());
        Assert.assertNotNull(table);
        Assert.assertEquals(expectedTable, table);
    }

    @Test
    public void testCreateTable() throws Exception {
        initializeExpectedDataset(2);
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(DatasetTest.TABLE_INFO1));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.create(DatasetTest.TABLE_INFO1)).andReturn(expectedTable);
        replay(bigquery);
        initializeDataset();
        Table table = dataset.create(DatasetTest.TABLE_INFO1.getTableId().getTable(), DatasetTest.TABLE_DEFINITION);
        Assert.assertEquals(expectedTable, table);
    }

    @Test
    public void testCreateTableWithOptions() throws Exception {
        initializeExpectedDataset(2);
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(DatasetTest.TABLE_INFO1));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.create(DatasetTest.TABLE_INFO1, TableOption.fields())).andReturn(expectedTable);
        replay(bigquery);
        initializeDataset();
        Table table = dataset.create(DatasetTest.TABLE_INFO1.getTableId().getTable(), DatasetTest.TABLE_DEFINITION, TableOption.fields());
        Assert.assertEquals(expectedTable, table);
    }

    @Test
    public void testBigQuery() {
        initializeExpectedDataset(1);
        replay(bigquery);
        Assert.assertSame(serviceMockReturnsOptions, expectedDataset.getBigQuery());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedDataset(4);
        replay(bigquery);
        compareDataset(expectedDataset, Dataset.fromPb(serviceMockReturnsOptions, expectedDataset.toPb()));
    }
}

