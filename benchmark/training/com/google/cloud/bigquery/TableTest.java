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


import BigQuery.TableDataListOption;
import BigQuery.TableOption;
import FieldValue.Attribute.PRIMITIVE;
import LegacySQLTypeName.STRING;
import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TableTest {
    private static final String ETAG = "etag";

    private static final String GENERATED_ID = "project:dataset:table1";

    private static final String SELF_LINK = "selfLink";

    private static final String FRIENDLY_NAME = "friendlyName";

    private static final String DESCRIPTION = "description";

    private static final Long CREATION_TIME = 10L;

    private static final Long EXPIRATION_TIME = 100L;

    private static final Long LAST_MODIFIED_TIME = 20L;

    private static final TableId TABLE_ID1 = TableId.of("dataset", "table1");

    private static final TableId TABLE_ID2 = TableId.of("dataset", "table2");

    private static final CopyJobConfiguration COPY_JOB_CONFIGURATION = CopyJobConfiguration.of(TableTest.TABLE_ID2, TableTest.TABLE_ID1);

    private static final JobInfo COPY_JOB_INFO = JobInfo.of(TableTest.COPY_JOB_CONFIGURATION);

    private static final JobInfo LOAD_JOB_INFO = JobInfo.of(LoadJobConfiguration.of(TableTest.TABLE_ID1, ImmutableList.of("URI"), FormatOptions.json()));

    private static final JobInfo EXTRACT_JOB_INFO = JobInfo.of(ExtractJobConfiguration.of(TableTest.TABLE_ID1, ImmutableList.of("URI"), "CSV"));

    private static final Field FIELD = Field.of("FieldName", STRING);

    private static final Schema SCHEMA = Schema.of(TableTest.FIELD);

    private static final TableDefinition TABLE_DEFINITION = StandardTableDefinition.of(TableTest.SCHEMA);

    private static final TableInfo TABLE_INFO = TableInfo.of(TableTest.TABLE_ID1, TableTest.TABLE_DEFINITION);

    private static final List<RowToInsert> ROWS_TO_INSERT = ImmutableList.of(RowToInsert.of("id1", ImmutableMap.<String, Object>of("key", "val1")), RowToInsert.of("id2", ImmutableMap.<String, Object>of("key", "val2")));

    private static final InsertAllRequest INSERT_ALL_REQUEST = InsertAllRequest.of(TableTest.TABLE_ID1, TableTest.ROWS_TO_INSERT);

    private static final InsertAllRequest INSERT_ALL_REQUEST_COMPLETE = InsertAllRequest.newBuilder(TableTest.TABLE_ID1, TableTest.ROWS_TO_INSERT).setSkipInvalidRows(true).setIgnoreUnknownValues(true).build();

    private static final InsertAllResponse EMPTY_INSERT_ALL_RESPONSE = new InsertAllResponse(ImmutableMap.<Long, List<BigQueryError>>of());

    private static final FieldValue FIELD_VALUE1 = FieldValue.of(PRIMITIVE, "val1");

    private static final FieldValue FIELD_VALUE2 = FieldValue.of(PRIMITIVE, "val1");

    private static final List<FieldValueList> ROWS = ImmutableList.of(FieldValueList.of(ImmutableList.of(TableTest.FIELD_VALUE1)), FieldValueList.of(ImmutableList.of(TableTest.FIELD_VALUE2)));

    private static final List<FieldValueList> ROWS_WITH_SCHEMA = ImmutableList.of(FieldValueList.of(ImmutableList.of(TableTest.FIELD_VALUE1)).withSchema(TableTest.SCHEMA.getFields()), FieldValueList.of(ImmutableList.of(TableTest.FIELD_VALUE2)).withSchema(TableTest.SCHEMA.getFields()));

    private BigQuery serviceMockReturnsOptions = createStrictMock(BigQuery.class);

    private BigQueryOptions mockOptions = createMock(BigQueryOptions.class);

    private BigQuery bigquery;

    private Table expectedTable;

    private Table table;

    @Test
    public void testBuilder() {
        initializeExpectedTable(2);
        replay(bigquery);
        Table builtTable = setCreationTime(TableTest.CREATION_TIME).setDescription(TableTest.DESCRIPTION).setEtag(TableTest.ETAG).setExpirationTime(TableTest.EXPIRATION_TIME).setFriendlyName(TableTest.FRIENDLY_NAME).setGeneratedId(TableTest.GENERATED_ID).setLastModifiedTime(TableTest.LAST_MODIFIED_TIME).setSelfLink(TableTest.SELF_LINK).build();
        Assert.assertEquals(TableTest.TABLE_ID1, builtTable.getTableId());
        Assert.assertEquals(TableTest.CREATION_TIME, builtTable.getCreationTime());
        Assert.assertEquals(TableTest.DESCRIPTION, builtTable.getDescription());
        Assert.assertEquals(TableTest.ETAG, builtTable.getEtag());
        Assert.assertEquals(TableTest.EXPIRATION_TIME, builtTable.getExpirationTime());
        Assert.assertEquals(TableTest.FRIENDLY_NAME, builtTable.getFriendlyName());
        Assert.assertEquals(TableTest.GENERATED_ID, builtTable.getGeneratedId());
        Assert.assertEquals(TableTest.LAST_MODIFIED_TIME, builtTable.getLastModifiedTime());
        Assert.assertEquals(TableTest.TABLE_DEFINITION, builtTable.getDefinition());
        Assert.assertEquals(TableTest.SELF_LINK, builtTable.getSelfLink());
        Assert.assertSame(serviceMockReturnsOptions, builtTable.getBigQuery());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedTable(4);
        replay(bigquery);
        compareTable(expectedTable, expectedTable.toBuilder().build());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedTable(1);
        BigQuery[] expectedOptions = new TableOption[]{ TableOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(TableTest.TABLE_INFO.getTableId(), expectedOptions)).andReturn(expectedTable);
        replay(bigquery);
        initializeTable();
        Assert.assertTrue(table.exists());
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedTable(1);
        BigQuery[] expectedOptions = new TableOption[]{ TableOption.fields() };
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(TableTest.TABLE_INFO.getTableId(), expectedOptions)).andReturn(null);
        replay(bigquery);
        initializeTable();
        Assert.assertFalse(table.exists());
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedTable(4);
        TableInfo updatedInfo = TableTest.TABLE_INFO.toBuilder().setDescription("Description").build();
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(TableTest.TABLE_INFO.getTableId())).andReturn(expectedTable);
        replay(bigquery);
        initializeTable();
        Table updatedTable = table.reload();
        compareTable(expectedTable, updatedTable);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(TableTest.TABLE_INFO.getTableId())).andReturn(null);
        replay(bigquery);
        initializeTable();
        Assert.assertNull(table.reload());
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedTable(4);
        TableInfo updatedInfo = TableTest.TABLE_INFO.toBuilder().setDescription("Description").build();
        Table expectedTable = new Table(serviceMockReturnsOptions, new TableInfo.BuilderImpl(updatedInfo));
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.getTable(TableTest.TABLE_INFO.getTableId(), TableOption.fields())).andReturn(expectedTable);
        replay(bigquery);
        initializeTable();
        Table updatedTable = table.reload(TableOption.fields());
        compareTable(expectedTable, updatedTable);
    }

    @Test
    public void testUpdate() {
        initializeExpectedTable(4);
        Table expectedUpdatedTable = expectedTable.toBuilder().setDescription("Description").build();
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.update(eq(expectedTable))).andReturn(expectedUpdatedTable);
        replay(bigquery);
        initializeTable();
        Table actualUpdatedTable = table.update();
        compareTable(expectedUpdatedTable, actualUpdatedTable);
    }

    @Test
    public void testUpdateWithOptions() {
        initializeExpectedTable(4);
        Table expectedUpdatedTable = expectedTable.toBuilder().setDescription("Description").build();
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.update(eq(expectedTable), eq(TableOption.fields()))).andReturn(expectedUpdatedTable);
        replay(bigquery);
        initializeTable();
        Table actualUpdatedTable = table.update(TableOption.fields());
        compareTable(expectedUpdatedTable, actualUpdatedTable);
    }

    @Test
    public void testDeleteTrue() {
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.delete(TableTest.TABLE_INFO.getTableId())).andReturn(true);
        replay(bigquery);
        initializeTable();
        Assert.assertTrue(table.delete());
    }

    @Test
    public void testDeleteFalse() {
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.delete(TableTest.TABLE_INFO.getTableId())).andReturn(false);
        replay(bigquery);
        initializeTable();
        Assert.assertFalse(table.delete());
    }

    @Test
    public void testInsert() throws Exception {
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.insertAll(TableTest.INSERT_ALL_REQUEST)).andReturn(TableTest.EMPTY_INSERT_ALL_RESPONSE);
        replay(bigquery);
        initializeTable();
        InsertAllResponse response = table.insert(TableTest.ROWS_TO_INSERT);
        Assert.assertSame(TableTest.EMPTY_INSERT_ALL_RESPONSE, response);
    }

    @Test
    public void testInsertComplete() throws Exception {
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.insertAll(TableTest.INSERT_ALL_REQUEST_COMPLETE)).andReturn(TableTest.EMPTY_INSERT_ALL_RESPONSE);
        replay(bigquery);
        initializeTable();
        InsertAllResponse response = table.insert(TableTest.ROWS_TO_INSERT, true, true);
        Assert.assertSame(TableTest.EMPTY_INSERT_ALL_RESPONSE, response);
    }

    @Test
    public void testList() throws Exception {
        Page<FieldValueList> page = new com.google.cloud.PageImpl(null, "c", TableTest.ROWS);
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.listTableData(TableTest.TABLE_ID1)).andReturn(new TableResult(null, TableTest.ROWS.size(), page));
        expect(bigquery.listTableData(TableTest.TABLE_ID1, TableTest.SCHEMA)).andReturn(new TableResult(TableTest.SCHEMA, TableTest.ROWS.size(), page));
        replay(bigquery);
        initializeTable();
        Page<FieldValueList> dataPage = table.list();
        assertThat(dataPage.getValues()).containsExactlyElementsIn(TableTest.ROWS).inOrder();
        dataPage = table.list(TableTest.SCHEMA);
        assertThat(dataPage.getValues()).containsExactlyElementsIn(TableTest.ROWS_WITH_SCHEMA).inOrder();
    }

    @Test
    public void testListWithOptions() throws Exception {
        Page<FieldValueList> page = new com.google.cloud.PageImpl(null, "c", TableTest.ROWS);
        initializeExpectedTable(1);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        expect(bigquery.listTableData(TableTest.TABLE_ID1, TableDataListOption.pageSize(10L))).andReturn(new TableResult(null, TableTest.ROWS.size(), page));
        expect(bigquery.listTableData(TableTest.TABLE_ID1, TableTest.SCHEMA, TableDataListOption.pageSize(10L))).andReturn(new TableResult(TableTest.SCHEMA, TableTest.ROWS.size(), page));
        replay(bigquery);
        initializeTable();
        Page<FieldValueList> dataPage = table.list(TableDataListOption.pageSize(10L));
        assertThat(dataPage.getValues()).containsExactlyElementsIn(TableTest.ROWS).inOrder();
        dataPage = table.list(TableTest.SCHEMA, TableDataListOption.pageSize(10L));
        assertThat(dataPage.getValues()).containsExactlyElementsIn(TableTest.ROWS_WITH_SCHEMA).inOrder();
    }

    @Test
    public void testCopyFromString() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.COPY_JOB_INFO));
        expect(bigquery.create(TableTest.COPY_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.copy(TableTest.TABLE_ID2.getDataset(), TableTest.TABLE_ID2.getTable());
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testCopyFromId() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.COPY_JOB_INFO));
        expect(bigquery.create(TableTest.COPY_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.copy(TableTest.TABLE_ID2.getDataset(), TableTest.TABLE_ID2.getTable());
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testLoadDataUri() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.LOAD_JOB_INFO));
        expect(bigquery.create(TableTest.LOAD_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.load(FormatOptions.json(), "URI");
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testLoadDataUris() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.LOAD_JOB_INFO));
        expect(bigquery.create(TableTest.LOAD_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.load(FormatOptions.json(), ImmutableList.of("URI"));
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testExtractDataUri() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.EXTRACT_JOB_INFO));
        expect(bigquery.create(TableTest.EXTRACT_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.extract("CSV", "URI");
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testExtractDataUris() throws Exception {
        initializeExpectedTable(2);
        expect(bigquery.getOptions()).andReturn(mockOptions);
        Job expectedJob = new Job(serviceMockReturnsOptions, new JobInfo.BuilderImpl(TableTest.EXTRACT_JOB_INFO));
        expect(bigquery.create(TableTest.EXTRACT_JOB_INFO)).andReturn(expectedJob);
        replay(bigquery);
        initializeTable();
        Job job = table.extract("CSV", ImmutableList.of("URI"));
        Assert.assertSame(expectedJob, job);
    }

    @Test
    public void testBigQuery() {
        initializeExpectedTable(1);
        replay(bigquery);
        Assert.assertSame(serviceMockReturnsOptions, expectedTable.getBigQuery());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedTable(4);
        replay(bigquery);
        compareTable(expectedTable, Table.fromPb(serviceMockReturnsOptions, expectedTable.toPb()));
    }
}

