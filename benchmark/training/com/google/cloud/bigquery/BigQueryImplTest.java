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
import Acl.Role.WRITER;
import BigQuery.DatasetDeleteOption;
import BigQuery.DatasetField.ACCESS;
import BigQuery.DatasetField.ETAG;
import BigQuery.DatasetListOption;
import BigQuery.DatasetOption;
import BigQuery.JobField.STATISTICS;
import BigQuery.JobField.USER_EMAIL;
import BigQuery.JobListOption;
import BigQuery.JobOption;
import BigQuery.TableDataListOption;
import BigQuery.TableField.SCHEMA;
import BigQuery.TableListOption;
import BigQuery.TableOption;
import BigQueryRpc.Option;
import BigQueryRpc.Option.ALL_DATASETS;
import BigQueryRpc.Option.ALL_USERS;
import BigQueryRpc.Option.DELETE_CONTENTS;
import BigQueryRpc.Option.MAX_RESULTS;
import BigQueryRpc.Option.PAGE_TOKEN;
import BigQueryRpc.Option.START_INDEX;
import BigQueryRpc.Option.STATE_FILTER;
import BigQueryRpc.Option.TIMEOUT;
import DatasetInfo.TO_PB_FUNCTION;
import Field.Mode.NULLABLE;
import Job.DEFAULT_QUERY_WAIT_OPTIONS;
import JobStatus.State.DONE;
import JobStatus.State.PENDING;
import LegacySQLTypeName.BOOLEAN;
import LegacySQLTypeName.INTEGER;
import TableDataInsertAllRequest.Rows;
import com.google.api.gax.paging.Page;
import com.google.api.services.bigquery.model.ErrorProto;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobStatus;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableDataList;
import com.google.api.services.bigquery.model.TableRow;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Tuple;
import com.google.cloud.bigquery.BigQuery.QueryResultsOption;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;
import com.google.cloud.bigquery.spi.BigQueryRpcFactory;
import com.google.cloud.bigquery.spi.v2.BigQueryRpc;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BigQueryImplTest {
    private static final String PROJECT = "project";

    private static final String LOCATION = "US";

    private static final String OTHER_PROJECT = "otherProject";

    private static final String DATASET = "dataset";

    private static final String TABLE = "table";

    private static final String JOB = "job";

    private static final String OTHER_TABLE = "otherTable";

    private static final String OTHER_DATASET = "otherDataset";

    private static final List<Acl> ACCESS_RULES = ImmutableList.of(Acl.of(Group.ofAllAuthenticatedUsers(), READER), Acl.of(new Acl.View(TableId.of("dataset", "table")), WRITER));

    private static final List<Acl> ACCESS_RULES_WITH_PROJECT = ImmutableList.of(Acl.of(Group.ofAllAuthenticatedUsers(), READER), Acl.of(new Acl.View(TableId.of(BigQueryImplTest.PROJECT, "dataset", "table"))));

    private static final DatasetInfo DATASET_INFO = DatasetInfo.newBuilder(BigQueryImplTest.DATASET).setAcl(BigQueryImplTest.ACCESS_RULES).setDescription("description").build();

    private static final DatasetInfo DATASET_INFO_WITH_PROJECT = DatasetInfo.newBuilder(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET).setAcl(BigQueryImplTest.ACCESS_RULES_WITH_PROJECT).setDescription("description").build();

    private static final DatasetInfo OTHER_DATASET_INFO = DatasetInfo.newBuilder(BigQueryImplTest.PROJECT, BigQueryImplTest.OTHER_DATASET).setAcl(BigQueryImplTest.ACCESS_RULES).setDescription("other description").build();

    private static final TableId TABLE_ID = TableId.of(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);

    private static final TableId OTHER_TABLE_ID = TableId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.OTHER_TABLE);

    private static final TableId TABLE_ID_WITH_PROJECT = TableId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);

    private static final Field FIELD_SCHEMA1 = Field.newBuilder("BooleanField", BOOLEAN).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(NULLABLE).setDescription("FieldDescription2").build();

    private static final Schema TABLE_SCHEMA = Schema.of(BigQueryImplTest.FIELD_SCHEMA1, BigQueryImplTest.FIELD_SCHEMA2);

    private static final StandardTableDefinition TABLE_DEFINITION = StandardTableDefinition.of(BigQueryImplTest.TABLE_SCHEMA);

    private static final ModelTableDefinition MODEL_TABLE_DEFINITION = ModelTableDefinition.newBuilder().build();

    private static final TableInfo TABLE_INFO = TableInfo.of(BigQueryImplTest.TABLE_ID, BigQueryImplTest.TABLE_DEFINITION);

    private static final TableInfo OTHER_TABLE_INFO = TableInfo.of(BigQueryImplTest.OTHER_TABLE_ID, BigQueryImplTest.TABLE_DEFINITION);

    private static final TableInfo TABLE_INFO_WITH_PROJECT = TableInfo.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, BigQueryImplTest.TABLE_DEFINITION);

    private static final TableInfo MODEL_TABLE_INFO_WITH_PROJECT = TableInfo.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, BigQueryImplTest.MODEL_TABLE_DEFINITION);

    private static final LoadJobConfiguration LOAD_JOB_CONFIGURATION = LoadJobConfiguration.of(BigQueryImplTest.TABLE_ID, "URI");

    private static final LoadJobConfiguration LOAD_JOB_CONFIGURATION_WITH_PROJECT = LoadJobConfiguration.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, "URI");

    private static final JobInfo LOAD_JOB = JobInfo.of(BigQueryImplTest.LOAD_JOB_CONFIGURATION);

    private static final JobInfo COMPLETE_LOAD_JOB = JobInfo.of(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB), BigQueryImplTest.LOAD_JOB_CONFIGURATION_WITH_PROJECT);

    private static final CopyJobConfiguration COPY_JOB_CONFIGURATION = CopyJobConfiguration.of(BigQueryImplTest.TABLE_ID, ImmutableList.of(BigQueryImplTest.TABLE_ID, BigQueryImplTest.TABLE_ID));

    private static final CopyJobConfiguration COPY_JOB_CONFIGURATION_WITH_PROJECT = CopyJobConfiguration.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, ImmutableList.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, BigQueryImplTest.TABLE_ID_WITH_PROJECT));

    private static final JobInfo COPY_JOB = JobInfo.of(BigQueryImplTest.COPY_JOB_CONFIGURATION);

    private static final JobInfo COMPLETE_COPY_JOB = JobInfo.of(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB), BigQueryImplTest.COPY_JOB_CONFIGURATION_WITH_PROJECT);

    private static final QueryJobConfiguration QUERY_JOB_CONFIGURATION = QueryJobConfiguration.newBuilder("SQL").setDefaultDataset(DatasetId.of(BigQueryImplTest.DATASET)).setDestinationTable(BigQueryImplTest.TABLE_ID).build();

    private static final QueryJobConfiguration QUERY_JOB_CONFIGURATION_WITH_PROJECT = QueryJobConfiguration.newBuilder("SQL").setDefaultDataset(DatasetId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET)).setDestinationTable(BigQueryImplTest.TABLE_ID_WITH_PROJECT).build();

    private static final JobInfo QUERY_JOB = JobInfo.of(BigQueryImplTest.QUERY_JOB_CONFIGURATION);

    private static final JobInfo COMPLETE_QUERY_JOB = JobInfo.of(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB), BigQueryImplTest.QUERY_JOB_CONFIGURATION_WITH_PROJECT);

    private static final ExtractJobConfiguration EXTRACT_JOB_CONFIGURATION = ExtractJobConfiguration.of(BigQueryImplTest.TABLE_ID, "URI");

    private static final ExtractJobConfiguration EXTRACT_JOB_CONFIGURATION_WITH_PROJECT = ExtractJobConfiguration.of(BigQueryImplTest.TABLE_ID_WITH_PROJECT, "URI");

    private static final JobInfo EXTRACT_JOB = JobInfo.of(BigQueryImplTest.EXTRACT_JOB_CONFIGURATION);

    private static final JobInfo COMPLETE_EXTRACT_JOB = JobInfo.of(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB), BigQueryImplTest.EXTRACT_JOB_CONFIGURATION_WITH_PROJECT);

    private static final TableCell BOOLEAN_FIELD = new TableCell().setV("false");

    private static final TableCell INTEGER_FIELD = new TableCell().setV("1");

    private static final TableRow TABLE_ROW = new TableRow().setF(ImmutableList.of(BigQueryImplTest.BOOLEAN_FIELD, BigQueryImplTest.INTEGER_FIELD));

    private static final QueryJobConfiguration QUERY_JOB_CONFIGURATION_FOR_QUERY = QueryJobConfiguration.newBuilder("SQL").setDefaultDataset(DatasetId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET)).setUseQueryCache(false).build();

    private static final JobInfo JOB_INFO = JobInfo.newBuilder(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY).setJobId(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB)).build();

    private static final String CURSOR = "cursor";

    private static final TableCell CELL_PB1 = new TableCell().setV("Value1");

    private static final TableCell CELL_PB2 = new TableCell().setV("Value2");

    private static final ImmutableList<FieldValueList> TABLE_DATA = ImmutableList.of(FieldValueList.of(ImmutableList.of(FieldValue.fromPb(BigQueryImplTest.CELL_PB1))), FieldValueList.of(ImmutableList.of(FieldValue.fromPb(BigQueryImplTest.CELL_PB2))));

    private static final TableDataList TABLE_DATA_PB = new TableDataList().setPageToken(BigQueryImplTest.CURSOR).setTotalRows(3L).setRows(ImmutableList.of(new TableRow().setF(ImmutableList.of(new TableCell().setV("Value1"))), new TableRow().setF(ImmutableList.of(new TableCell().setV("Value2")))));

    // Empty BigQueryRpc options
    private static final Map<BigQueryRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    // Dataset options
    private static final DatasetOption DATASET_OPTION_FIELDS = DatasetOption.fields(ACCESS, ETAG);

    // Dataset list options
    private static final DatasetListOption DATASET_LIST_ALL = DatasetListOption.all();

    private static final DatasetListOption DATASET_LIST_PAGE_TOKEN = DatasetListOption.pageToken(BigQueryImplTest.CURSOR);

    private static final DatasetListOption DATASET_LIST_PAGE_SIZE = DatasetListOption.pageSize(42L);

    private static final Map<BigQueryRpc.Option, ?> DATASET_LIST_OPTIONS = ImmutableMap.of(ALL_DATASETS, true, PAGE_TOKEN, BigQueryImplTest.CURSOR, MAX_RESULTS, 42L);

    // Dataset delete options
    private static final DatasetDeleteOption DATASET_DELETE_CONTENTS = DatasetDeleteOption.deleteContents();

    private static final Map<BigQueryRpc.Option, ?> DATASET_DELETE_OPTIONS = ImmutableMap.of(DELETE_CONTENTS, true);

    // Table options
    private static final TableOption TABLE_OPTION_FIELDS = TableOption.fields(SCHEMA, BigQuery.TableField.ETAG);

    // Table list options
    private static final TableListOption TABLE_LIST_PAGE_SIZE = TableListOption.pageSize(42L);

    private static final TableListOption TABLE_LIST_PAGE_TOKEN = TableListOption.pageToken(BigQueryImplTest.CURSOR);

    private static final Map<BigQueryRpc.Option, ?> TABLE_LIST_OPTIONS = ImmutableMap.of(MAX_RESULTS, 42L, PAGE_TOKEN, BigQueryImplTest.CURSOR);

    // TableData list options
    private static final TableDataListOption TABLE_DATA_LIST_PAGE_SIZE = TableDataListOption.pageSize(42L);

    private static final TableDataListOption TABLE_DATA_LIST_PAGE_TOKEN = TableDataListOption.pageToken(BigQueryImplTest.CURSOR);

    private static final TableDataListOption TABLE_DATA_LIST_START_INDEX = TableDataListOption.startIndex(0L);

    private static final Map<BigQueryRpc.Option, ?> TABLE_DATA_LIST_OPTIONS = ImmutableMap.of(MAX_RESULTS, 42L, PAGE_TOKEN, BigQueryImplTest.CURSOR, START_INDEX, 0L);

    // Job options
    private static final JobOption JOB_OPTION_FIELDS = JobOption.fields(USER_EMAIL);

    // Job list options
    private static final JobListOption JOB_LIST_OPTION_FIELD = JobListOption.fields(STATISTICS);

    private static final JobListOption JOB_LIST_ALL_USERS = JobListOption.allUsers();

    private static final JobListOption JOB_LIST_STATE_FILTER = JobListOption.stateFilter(DONE, PENDING);

    private static final JobListOption JOB_LIST_PAGE_TOKEN = JobListOption.pageToken(BigQueryImplTest.CURSOR);

    private static final JobListOption JOB_LIST_PAGE_SIZE = JobListOption.pageSize(42L);

    private static final Map<BigQueryRpc.Option, ?> JOB_LIST_OPTIONS = ImmutableMap.of(ALL_USERS, true, STATE_FILTER, ImmutableList.of("done", "pending"), PAGE_TOKEN, BigQueryImplTest.CURSOR, MAX_RESULTS, 42L);

    // Query Results options
    private static final QueryResultsOption QUERY_RESULTS_OPTION_TIME = BigQuery.QueryResultsOption.maxWaitTime(42L);

    private static final QueryResultsOption QUERY_RESULTS_OPTION_INDEX = BigQuery.QueryResultsOption.startIndex(1024L);

    private static final QueryResultsOption QUERY_RESULTS_OPTION_PAGE_TOKEN = BigQuery.QueryResultsOption.pageToken(BigQueryImplTest.CURSOR);

    private static final QueryResultsOption QUERY_RESULTS_OPTION_PAGE_SIZE = BigQuery.QueryResultsOption.pageSize(0L);

    private static final Map<BigQueryRpc.Option, ?> QUERY_RESULTS_OPTIONS = ImmutableMap.of(TIMEOUT, 42L, START_INDEX, 1024L, PAGE_TOKEN, BigQueryImplTest.CURSOR, MAX_RESULTS, 0L);

    private BigQueryOptions options;

    private BigQueryRpcFactory rpcFactoryMock;

    private BigQueryRpc bigqueryRpcMock;

    private BigQuery bigquery;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertSame(options, bigquery.getOptions());
    }

    @Test
    public void testCreateDataset() {
        DatasetInfo datasetInfo = BigQueryImplTest.DATASET_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.create(datasetInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(datasetInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.create(datasetInfo);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(datasetInfo)), dataset);
    }

    @Test
    public void testCreateDatasetWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.create(eq(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb()), capture(capturedOptions))).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.create(BigQueryImplTest.DATASET_INFO, BigQueryImplTest.DATASET_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.DATASET_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("datasetReference"));
        Assert.assertTrue(selector.contains("access"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(28, selector.length());
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testGetDataset() {
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.getDataset(BigQueryImplTest.DATASET);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testGetDatasetNotFoundWhenThrowIsDisabled() {
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(false);
        bigquery = options.getService();
        Dataset dataset = bigquery.getDataset(BigQueryImplTest.DATASET);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testGetDatasetNotFoundWhenThrowIsEnabled() {
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, "dataset-not-found", BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new BigQueryException(404, "Dataset not found"));
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(true);
        bigquery = options.getService();
        thrown.expect(BigQueryException.class);
        bigquery.getDataset("dataset-not-found");
    }

    @Test
    public void testGetDatasetFromDatasetId() {
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.getDataset(DatasetId.of(BigQueryImplTest.DATASET));
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testGetDatasetFromDatasetIdWithProject() {
        DatasetInfo datasetInfo = BigQueryImplTest.DATASET_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        DatasetId datasetId = DatasetId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET);
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(datasetInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.getDataset(datasetId);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(datasetInfo)), dataset);
    }

    @Test
    public void testGetDatasetWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.getDataset(eq(BigQueryImplTest.PROJECT), eq(BigQueryImplTest.DATASET), capture(capturedOptions))).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.getDataset(BigQueryImplTest.DATASET, BigQueryImplTest.DATASET_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.DATASET_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("datasetReference"));
        Assert.assertTrue(selector.contains("access"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(28, selector.length());
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testListDatasets() {
        bigquery = options.getService();
        ImmutableList<Dataset> datasetList = ImmutableList.of(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.OTHER_DATASET_INFO)));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Dataset>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(datasetList, TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listDatasets(BigQueryImplTest.PROJECT, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Dataset> page = bigquery.listDatasets();
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(datasetList.toArray(), Iterables.toArray(page.getValues(), DatasetInfo.class));
    }

    @Test
    public void testListDatasetsWithProjects() {
        bigquery = options.getService();
        ImmutableList<Dataset> datasetList = ImmutableList.of(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT))));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Dataset>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(datasetList, TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listDatasets(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Dataset> page = bigquery.listDatasets(BigQueryImplTest.OTHER_PROJECT);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(datasetList.toArray(), Iterables.toArray(page.getValues(), DatasetInfo.class));
    }

    @Test
    public void testListEmptyDatasets() {
        ImmutableList<com.google.api.services.bigquery.model.Dataset> datasets = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Dataset>> result = Tuple.<String, Iterable<com.google.api.services.bigquery.model.Dataset>>of(null, datasets);
        EasyMock.expect(bigqueryRpcMock.listDatasets(BigQueryImplTest.PROJECT, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Page<Dataset> page = bigquery.listDatasets();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(ImmutableList.of().toArray(), Iterables.toArray(page.getValues(), Dataset.class));
    }

    @Test
    public void testListDatasetsWithOptions() {
        bigquery = options.getService();
        ImmutableList<Dataset> datasetList = ImmutableList.of(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.OTHER_DATASET_INFO)));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Dataset>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(datasetList, TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listDatasets(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Dataset> page = bigquery.listDatasets(BigQueryImplTest.DATASET_LIST_ALL, BigQueryImplTest.DATASET_LIST_PAGE_TOKEN, BigQueryImplTest.DATASET_LIST_PAGE_SIZE);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(datasetList.toArray(), Iterables.toArray(page.getValues(), DatasetInfo.class));
    }

    @Test
    public void testDeleteDataset() {
        EasyMock.expect(bigqueryRpcMock.deleteDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(BigQueryImplTest.DATASET));
    }

    @Test
    public void testDeleteDatasetFromDatasetId() {
        EasyMock.expect(bigqueryRpcMock.deleteDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(DatasetId.of(BigQueryImplTest.DATASET)));
    }

    @Test
    public void testDeleteDatasetFromDatasetIdWithProject() {
        DatasetId datasetId = DatasetId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET);
        EasyMock.expect(bigqueryRpcMock.deleteDataset(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(datasetId));
    }

    @Test
    public void testDeleteDatasetWithOptions() {
        EasyMock.expect(bigqueryRpcMock.deleteDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.DATASET_DELETE_OPTIONS)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(BigQueryImplTest.DATASET, BigQueryImplTest.DATASET_DELETE_CONTENTS));
    }

    @Test
    public void testUpdateDataset() {
        DatasetInfo updatedDatasetInfo = BigQueryImplTest.DATASET_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT).toBuilder().setDescription("newDescription").build();
        EasyMock.expect(bigqueryRpcMock.patch(updatedDatasetInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(updatedDatasetInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.update(updatedDatasetInfo);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(updatedDatasetInfo)), dataset);
    }

    @Test
    public void testUpdateDatasetWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        DatasetInfo updatedDatasetInfo = BigQueryImplTest.DATASET_INFO.toBuilder().setDescription("newDescription").build();
        DatasetInfo updatedDatasetInfoWithProject = BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toBuilder().setDescription("newDescription").build();
        EasyMock.expect(bigqueryRpcMock.patch(eq(updatedDatasetInfoWithProject.toPb()), capture(capturedOptions))).andReturn(updatedDatasetInfoWithProject.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Dataset dataset = bigquery.update(updatedDatasetInfo, BigQueryImplTest.DATASET_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.DATASET_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("datasetReference"));
        Assert.assertTrue(selector.contains("access"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(28, selector.length());
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(updatedDatasetInfoWithProject)), dataset);
    }

    @Test
    public void testCreateTable() {
        TableInfo tableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.create(tableInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(tableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.create(tableInfo);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(tableInfo)), table);
    }

    @Test
    public void testCreateTableWithoutProject() {
        TableInfo tableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.PROJECT);
        TableId tableId = TableId.of("", BigQueryImplTest.TABLE_ID.getDataset(), BigQueryImplTest.TABLE_ID.getTable());
        tableInfo.toBuilder().setTableId(tableId);
        EasyMock.expect(bigqueryRpcMock.create(tableInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(tableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.create(tableInfo);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(tableInfo)), table);
    }

    @Test
    public void testCreateTableWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.create(eq(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb()), capture(capturedOptions))).andReturn(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Table table = bigquery.create(BigQueryImplTest.TABLE_INFO, BigQueryImplTest.TABLE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.TABLE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("tableReference"));
        Assert.assertTrue(selector.contains("schema"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(31, selector.length());
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), table);
    }

    @Test
    public void testGetTable() {
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Table table = bigquery.getTable(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), table);
    }

    @Test
    public void testGetTableNotFoundWhenThrowIsDisabled() {
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(false);
        bigquery = options.getService();
        Table table = bigquery.getTable(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), table);
    }

    @Test
    public void testGetTableNotFoundWhenThrowIsEnabled() {
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, "table-not-found", BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new BigQueryException(404, "Table not found"));
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(true);
        bigquery = options.getService();
        thrown.expect(BigQueryException.class);
        bigquery.getTable(BigQueryImplTest.DATASET, "table-not-found");
    }

    @Test
    public void testGetTableFromTableId() {
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Table table = bigquery.getTable(BigQueryImplTest.TABLE_ID);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), table);
    }

    @Test
    public void testGetTableFromTableIdWithProject() {
        TableInfo tableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        TableId tableId = BigQueryImplTest.TABLE_ID.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(tableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.getTable(tableId);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(tableInfo)), table);
    }

    @Test
    public void testGetTableFromTableIdWithoutProject() {
        TableInfo tableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.PROJECT);
        TableId tableId = TableId.of("", BigQueryImplTest.TABLE_ID.getDataset(), BigQueryImplTest.TABLE_ID.getTable());
        EasyMock.expect(bigqueryRpcMock.getTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(tableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.getTable(tableId);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(tableInfo)), table);
    }

    @Test
    public void testGetTableWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.getTable(eq(BigQueryImplTest.PROJECT), eq(BigQueryImplTest.DATASET), eq(BigQueryImplTest.TABLE), capture(capturedOptions))).andReturn(BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Table table = bigquery.getTable(BigQueryImplTest.TABLE_ID, BigQueryImplTest.TABLE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.TABLE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("tableReference"));
        Assert.assertTrue(selector.contains("schema"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(31, selector.length());
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), table);
    }

    @Test
    public void testListTables() {
        bigquery = options.getService();
        ImmutableList<Table> tableList = ImmutableList.of(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.OTHER_TABLE_INFO)), new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.MODEL_TABLE_INFO_WITH_PROJECT)));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Table>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(tableList, TableInfo.TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listTables(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Table> page = bigquery.listTables(BigQueryImplTest.DATASET);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(tableList.toArray(), Iterables.toArray(page.getValues(), Table.class));
    }

    @Test
    public void testListTablesFromDatasetId() {
        bigquery = options.getService();
        ImmutableList<Table> tableList = ImmutableList.of(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.OTHER_TABLE_INFO)));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Table>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(tableList, TableInfo.TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listTables(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Table> page = bigquery.listTables(DatasetId.of(BigQueryImplTest.DATASET));
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(tableList.toArray(), Iterables.toArray(page.getValues(), Table.class));
    }

    @Test
    public void testListTablesFromDatasetIdWithProject() {
        bigquery = options.getService();
        ImmutableList<Table> tableList = ImmutableList.of(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT))));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Table>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(tableList, TableInfo.TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listTables(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Table> page = bigquery.listTables(DatasetId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET));
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(tableList.toArray(), Iterables.toArray(page.getValues(), Table.class));
    }

    @Test
    public void testListTablesWithOptions() {
        bigquery = options.getService();
        ImmutableList<Table> tableList = ImmutableList.of(new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.TABLE_INFO_WITH_PROJECT)), new Table(bigquery, new TableInfo.BuilderImpl(BigQueryImplTest.OTHER_TABLE_INFO)));
        Tuple<String, Iterable<com.google.api.services.bigquery.model.Table>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(tableList, TableInfo.TO_PB_FUNCTION));
        EasyMock.expect(bigqueryRpcMock.listTables(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Table> page = bigquery.listTables(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE_LIST_PAGE_SIZE, BigQueryImplTest.TABLE_LIST_PAGE_TOKEN);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(tableList.toArray(), Iterables.toArray(page.getValues(), Table.class));
    }

    @Test
    public void testDeleteTable() {
        EasyMock.expect(bigqueryRpcMock.deleteTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE));
    }

    @Test
    public void testDeleteTableFromTableId() {
        EasyMock.expect(bigqueryRpcMock.deleteTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.delete(BigQueryImplTest.TABLE_ID));
    }

    @Test
    public void testDeleteTableFromTableIdWithProject() {
        TableId tableId = BigQueryImplTest.TABLE_ID.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.deleteTable(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Assert.assertTrue(bigquery.delete(tableId));
    }

    @Test
    public void testDeleteTableFromTableIdWithoutProject() {
        TableId tableId = TableId.of("", BigQueryImplTest.TABLE_ID.getDataset(), BigQueryImplTest.TABLE_ID.getTable());
        EasyMock.expect(bigqueryRpcMock.deleteTable(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Assert.assertTrue(bigquery.delete(tableId));
    }

    @Test
    public void testUpdateTable() {
        TableInfo updatedTableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.OTHER_PROJECT).toBuilder().setDescription("newDescription").build();
        EasyMock.expect(bigqueryRpcMock.patch(updatedTableInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(updatedTableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.update(updatedTableInfo);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(updatedTableInfo)), table);
    }

    @Test
    public void testUpdateTableWithoutProject() {
        TableInfo tableInfo = BigQueryImplTest.TABLE_INFO.setProjectId(BigQueryImplTest.PROJECT);
        TableId tableId = TableId.of("", BigQueryImplTest.TABLE_ID.getDataset(), BigQueryImplTest.TABLE_ID.getTable());
        tableInfo.toBuilder().setTableId(tableId);
        EasyMock.expect(bigqueryRpcMock.patch(tableInfo.toPb(), BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(tableInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Table table = bigquery.update(tableInfo);
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(tableInfo)), table);
    }

    @Test
    public void testUpdateTableWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        TableInfo updatedTableInfo = BigQueryImplTest.TABLE_INFO.toBuilder().setDescription("newDescription").build();
        TableInfo updatedTableInfoWithProject = BigQueryImplTest.TABLE_INFO_WITH_PROJECT.toBuilder().setDescription("newDescription").build();
        EasyMock.expect(bigqueryRpcMock.patch(eq(updatedTableInfoWithProject.toPb()), capture(capturedOptions))).andReturn(updatedTableInfoWithProject.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Table table = bigquery.update(updatedTableInfo, BigQueryImplTest.TABLE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.TABLE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("tableReference"));
        Assert.assertTrue(selector.contains("schema"));
        Assert.assertTrue(selector.contains("etag"));
        Assert.assertEquals(31, selector.length());
        Assert.assertEquals(new Table(bigquery, new TableInfo.BuilderImpl(updatedTableInfoWithProject)), table);
    }

    @Test
    public void testInsertAllWithRowIdShouldRetry() {
        Map<String, Object> row1 = ImmutableMap.<String, Object>of("field", "value1");
        Map<String, Object> row2 = ImmutableMap.<String, Object>of("field", "value2");
        List<RowToInsert> rows = ImmutableList.of(new RowToInsert("row1", row1), new RowToInsert("row2", row2));
        InsertAllRequest request = InsertAllRequest.newBuilder(BigQueryImplTest.TABLE_ID).setRows(rows).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix").build();
        TableDataInsertAllRequest requestPb = new TableDataInsertAllRequest().setRows(Lists.transform(rows, new Function<RowToInsert, TableDataInsertAllRequest.Rows>() {
            @Override
            public Rows apply(RowToInsert rowToInsert) {
                return new TableDataInsertAllRequest.Rows().setInsertId(rowToInsert.getId()).setJson(rowToInsert.getContent());
            }
        })).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix");
        TableDataInsertAllResponse responsePb = new TableDataInsertAllResponse().setInsertErrors(ImmutableList.of(new TableDataInsertAllResponse.InsertErrors().setIndex(0L).setErrors(ImmutableList.of(new ErrorProto().setMessage("ErrorMessage")))));
        EasyMock.expect(bigqueryRpcMock.insertAll(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, requestPb)).andThrow(new BigQueryException(500, "InternalError"));
        EasyMock.expect(bigqueryRpcMock.insertAll(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, requestPb)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        InsertAllResponse response = bigquery.insertAll(request);
        Assert.assertNotNull(response.getErrorsFor(0L));
        Assert.assertNull(response.getErrorsFor(1L));
        Assert.assertEquals(1, response.getErrorsFor(0L).size());
        Assert.assertEquals("ErrorMessage", response.getErrorsFor(0L).get(0).getMessage());
    }

    @Test
    public void testInsertAllWithoutRowIdShouldNotRetry() {
        Map<String, Object> row1 = ImmutableMap.<String, Object>of("field", "value1");
        Map<String, Object> row2 = ImmutableMap.<String, Object>of("field", "value2");
        List<RowToInsert> rows = ImmutableList.of(RowToInsert.of(row1), RowToInsert.of(row2));
        InsertAllRequest request = InsertAllRequest.newBuilder(BigQueryImplTest.TABLE_ID).setRows(rows).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix").build();
        TableDataInsertAllRequest requestPb = new TableDataInsertAllRequest().setRows(Lists.transform(rows, new Function<RowToInsert, TableDataInsertAllRequest.Rows>() {
            @Override
            public Rows apply(RowToInsert rowToInsert) {
                return new TableDataInsertAllRequest.Rows().setInsertId(rowToInsert.getId()).setJson(rowToInsert.getContent());
            }
        })).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix");
        EasyMock.expect(bigqueryRpcMock.insertAll(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, requestPb)).andThrow(new BigQueryException(500, "InternalError"));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(BigQueryException.class);
        bigquery.insertAll(request);
    }

    @Test
    public void testInsertAllWithProject() {
        Map<String, Object> row1 = ImmutableMap.<String, Object>of("field", "value1");
        Map<String, Object> row2 = ImmutableMap.<String, Object>of("field", "value2");
        List<RowToInsert> rows = ImmutableList.of(new RowToInsert("row1", row1), new RowToInsert("row2", row2));
        TableId tableId = TableId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);
        InsertAllRequest request = InsertAllRequest.newBuilder(tableId).setRows(rows).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix").build();
        TableDataInsertAllRequest requestPb = new TableDataInsertAllRequest().setRows(Lists.transform(rows, new Function<RowToInsert, TableDataInsertAllRequest.Rows>() {
            @Override
            public Rows apply(RowToInsert rowToInsert) {
                return new TableDataInsertAllRequest.Rows().setInsertId(rowToInsert.getId()).setJson(rowToInsert.getContent());
            }
        })).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix");
        TableDataInsertAllResponse responsePb = new TableDataInsertAllResponse().setInsertErrors(ImmutableList.of(new TableDataInsertAllResponse.InsertErrors().setIndex(0L).setErrors(ImmutableList.of(new ErrorProto().setMessage("ErrorMessage")))));
        EasyMock.expect(bigqueryRpcMock.insertAll(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, requestPb)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        InsertAllResponse response = bigquery.insertAll(request);
        Assert.assertNotNull(response.getErrorsFor(0L));
        Assert.assertNull(response.getErrorsFor(1L));
        Assert.assertEquals(1, response.getErrorsFor(0L).size());
        Assert.assertEquals("ErrorMessage", response.getErrorsFor(0L).get(0).getMessage());
    }

    @Test
    public void testInsertAllWithProjectInTable() {
        Map<String, Object> row1 = ImmutableMap.<String, Object>of("field", "value1");
        Map<String, Object> row2 = ImmutableMap.<String, Object>of("field", "value2");
        List<RowToInsert> rows = ImmutableList.of(new RowToInsert("row1", row1), new RowToInsert("row2", row2));
        TableId tableId = TableId.of("project-different-from-option", BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);
        InsertAllRequest request = InsertAllRequest.newBuilder(tableId).setRows(rows).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix").build();
        TableDataInsertAllRequest requestPb = new TableDataInsertAllRequest().setRows(Lists.transform(rows, new Function<RowToInsert, TableDataInsertAllRequest.Rows>() {
            @Override
            public Rows apply(RowToInsert rowToInsert) {
                return new TableDataInsertAllRequest.Rows().setInsertId(rowToInsert.getId()).setJson(rowToInsert.getContent());
            }
        })).setSkipInvalidRows(false).setIgnoreUnknownValues(true).setTemplateSuffix("suffix");
        TableDataInsertAllResponse responsePb = new TableDataInsertAllResponse().setInsertErrors(ImmutableList.of(new TableDataInsertAllResponse.InsertErrors().setIndex(0L).setErrors(ImmutableList.of(new ErrorProto().setMessage("ErrorMessage")))));
        EasyMock.expect(bigqueryRpcMock.insertAll("project-different-from-option", BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, requestPb)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        InsertAllResponse response = bigquery.insertAll(request);
        Assert.assertNotNull(response.getErrorsFor(0L));
        Assert.assertNull(response.getErrorsFor(1L));
        Assert.assertEquals(1, response.getErrorsFor(0L).size());
        Assert.assertEquals("ErrorMessage", response.getErrorsFor(0L).get(0).getMessage());
    }

    @Test
    public void testListTableData() {
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_DATA_PB);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Page<FieldValueList> page = bigquery.listTableData(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(BigQueryImplTest.TABLE_DATA.toArray(), Iterables.toArray(page.getValues(), List.class));
    }

    @Test
    public void testListTableDataFromTableId() {
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_DATA_PB);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Page<FieldValueList> page = bigquery.listTableData(TableId.of(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE));
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(BigQueryImplTest.TABLE_DATA.toArray(), Iterables.toArray(page.getValues(), List.class));
    }

    @Test
    public void testListTableDataFromTableIdWithProject() {
        TableId tableId = BigQueryImplTest.TABLE_ID.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.TABLE_DATA_PB);
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Page<FieldValueList> page = bigquery.listTableData(tableId);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(BigQueryImplTest.TABLE_DATA.toArray(), Iterables.toArray(page.getValues(), List.class));
    }

    @Test
    public void testListTableDataWithOptions() {
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.TABLE_DATA_LIST_OPTIONS)).andReturn(BigQueryImplTest.TABLE_DATA_PB);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Page<FieldValueList> page = bigquery.listTableData(BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, BigQueryImplTest.TABLE_DATA_LIST_PAGE_SIZE, BigQueryImplTest.TABLE_DATA_LIST_PAGE_TOKEN, BigQueryImplTest.TABLE_DATA_LIST_START_INDEX);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(BigQueryImplTest.TABLE_DATA.toArray(), Iterables.toArray(page.getValues(), List.class));
    }

    @Test
    public void testCreateJobSuccess() {
        String id = "testCreateJobSuccess-id";
        JobId jobId = JobId.of(id);
        String query = "SELECT * in FOO";
        Capture<Job> jobCapture = EasyMock.newCapture();
        EasyMock.expect(bigqueryRpcMock.create(EasyMock.capture(jobCapture), EasyMock.eq(BigQueryImplTest.EMPTY_RPC_OPTIONS))).andReturn(BigQueryImplTest.newJobPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        assertThat(bigquery.create(JobInfo.of(jobId, QueryJobConfiguration.of(query)))).isNotNull();
        assertThat(jobCapture.getValue().getJobReference().getJobId()).isEqualTo(id);
    }

    @Test
    public void testCreateJobWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.create(EasyMock.anyObject(Job.class), EasyMock.capture(capturedOptions))).andReturn(BigQueryImplTest.newJobPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQuery.JobOption jobOptions = JobOption.fields(USER_EMAIL);
        bigquery = options.getService();
        bigquery.create(JobInfo.of(QueryJobConfiguration.of("SOME QUERY")), jobOptions);
        String selector = ((String) (capturedOptions.getValue().get(jobOptions.getRpcOption())));
        // jobReference and configuration are always sent; the RPC call won't succeed otherwise.
        assertThat(selector.split(",")).asList().containsExactly("jobReference", "configuration", "user_email");
    }

    @Test
    public void testCreateJobNoGet() {
        String id = "testCreateJobNoGet-id";
        JobId jobId = JobId.of(id);
        String query = "SELECT * in FOO";
        Capture<Job> jobCapture = EasyMock.newCapture();
        EasyMock.expect(bigqueryRpcMock.create(EasyMock.capture(jobCapture), EasyMock.eq(BigQueryImplTest.EMPTY_RPC_OPTIONS))).andThrow(new BigQueryException(409, "already exists, for some reason"));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        try {
            bigquery.create(JobInfo.of(jobId, QueryJobConfiguration.of(query)));
            Assert.fail("should throw");
        } catch (BigQueryException e) {
            assertThat(jobCapture.getValue().getJobReference().getJobId()).isEqualTo(id);
        }
    }

    @Test
    public void testCreateJobTryGet() {
        final String id = "testCreateJobTryGet-id";
        String query = "SELECT * in FOO";
        Supplier<JobId> idProvider = new Supplier<JobId>() {
            @Override
            public JobId get() {
                return JobId.of(id);
            }
        };
        Capture<Job> jobCapture = EasyMock.newCapture();
        EasyMock.expect(bigqueryRpcMock.create(EasyMock.capture(jobCapture), EasyMock.eq(BigQueryImplTest.EMPTY_RPC_OPTIONS))).andThrow(new BigQueryException(409, "already exists, for some reason"));
        EasyMock.expect(bigqueryRpcMock.getJob(anyString(), EasyMock.eq(id), EasyMock.eq(((String) (null))), EasyMock.eq(BigQueryImplTest.EMPTY_RPC_OPTIONS))).andReturn(BigQueryImplTest.newJobPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        ((BigQueryImpl) (bigquery)).create(JobInfo.of(QueryJobConfiguration.of(query)), idProvider);
        assertThat(jobCapture.getValue().getJobReference().getJobId()).isEqualTo(id);
    }

    @Test
    public void testCreateJobWithProjectId() {
        JobInfo jobInfo = JobInfo.newBuilder(BigQueryImplTest.QUERY_JOB_CONFIGURATION.setProjectId(BigQueryImplTest.OTHER_PROJECT)).setJobId(JobId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB)).build();
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(bigqueryRpcMock.create(eq(jobInfo.toPb()), capture(capturedOptions))).andReturn(jobInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions bigQueryOptions = createBigQueryOptionsForProject(BigQueryImplTest.OTHER_PROJECT, rpcFactoryMock);
        bigquery = bigQueryOptions.getService();
        Job job = bigquery.create(jobInfo, BigQueryImplTest.JOB_OPTION_FIELDS);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(jobInfo)), job);
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.JOB_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("jobReference"));
        Assert.assertTrue(selector.contains("configuration"));
        Assert.assertTrue(selector.contains("user_email"));
        Assert.assertEquals(37, selector.length());
    }

    @Test
    public void testGetJob() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.COMPLETE_COPY_JOB.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(BigQueryImplTest.JOB);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_COPY_JOB)), job);
    }

    @Test
    public void testGetJobWithLocation() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, BigQueryImplTest.LOCATION, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.COMPLETE_COPY_JOB.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions options = createBigQueryOptionsForProjectWithLocation(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(BigQueryImplTest.JOB);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_COPY_JOB)), job);
    }

    @Test
    public void testGetJobNotFoundWhenThrowIsDisabled() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.COMPLETE_COPY_JOB.toPb());
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(false);
        bigquery = options.getService();
        Job job = bigquery.getJob(BigQueryImplTest.JOB);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_COPY_JOB)), job);
    }

    @Test
    public void testGetJobNotFoundWhenThrowIsEnabled() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, "job-not-found", null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new BigQueryException(404, "Job not found"));
        EasyMock.replay(bigqueryRpcMock);
        options.setThrowNotFound(true);
        bigquery = options.getService();
        thrown.expect(BigQueryException.class);
        bigquery.getJob("job-not-found");
    }

    @Test
    public void testGetJobFromJobId() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.COMPLETE_COPY_JOB.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(JobId.of(BigQueryImplTest.JOB));
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_COPY_JOB)), job);
    }

    @Test
    public void testGetJobFromJobIdWithLocation() {
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, BigQueryImplTest.LOCATION, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(BigQueryImplTest.COMPLETE_COPY_JOB.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions options = createBigQueryOptionsForProjectWithLocation(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(JobId.of(BigQueryImplTest.JOB));
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_COPY_JOB)), job);
    }

    @Test
    public void testGetJobFromJobIdWithProject() {
        JobId jobId = JobId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB);
        JobInfo jobInfo = BigQueryImplTest.COPY_JOB.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(jobInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(jobId);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(jobInfo)), job);
    }

    @Test
    public void testGetJobFromJobIdWithProjectWithLocation() {
        JobId jobId = JobId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB);
        JobInfo jobInfo = BigQueryImplTest.COPY_JOB.setProjectId(BigQueryImplTest.OTHER_PROJECT);
        EasyMock.expect(bigqueryRpcMock.getJob(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB, BigQueryImplTest.LOCATION, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(jobInfo.toPb());
        EasyMock.replay(bigqueryRpcMock);
        BigQueryOptions options = createBigQueryOptionsForProjectWithLocation(BigQueryImplTest.PROJECT, rpcFactoryMock);
        bigquery = options.getService();
        Job job = bigquery.getJob(jobId);
        Assert.assertEquals(new Job(bigquery, new JobInfo.BuilderImpl(jobInfo)), job);
    }

    @Test
    public void testListJobs() {
        bigquery = options.getService();
        ImmutableList<Job> jobList = ImmutableList.of(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_QUERY_JOB)), new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_LOAD_JOB)));
        Tuple<String, Iterable<Job>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(jobList, new Function<Job, Job>() {
            @Override
            public Job apply(Job job) {
                return job.toPb();
            }
        }));
        EasyMock.expect(bigqueryRpcMock.listJobs(BigQueryImplTest.PROJECT, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Job> page = bigquery.listJobs();
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(jobList.toArray(), Iterables.toArray(page.getValues(), Job.class));
    }

    @Test
    public void testListJobsWithOptions() {
        bigquery = options.getService();
        ImmutableList<Job> jobList = ImmutableList.of(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_QUERY_JOB)), new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_LOAD_JOB)));
        Tuple<String, Iterable<Job>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(jobList, new Function<Job, Job>() {
            @Override
            public Job apply(Job job) {
                return job.toPb();
            }
        }));
        EasyMock.expect(bigqueryRpcMock.listJobs(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Job> page = bigquery.listJobs(BigQueryImplTest.JOB_LIST_ALL_USERS, BigQueryImplTest.JOB_LIST_STATE_FILTER, BigQueryImplTest.JOB_LIST_PAGE_TOKEN, BigQueryImplTest.JOB_LIST_PAGE_SIZE);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(jobList.toArray(), Iterables.toArray(page.getValues(), Job.class));
    }

    @Test
    public void testListJobsWithSelectedFields() {
        Capture<Map<BigQueryRpc.Option, Object>> capturedOptions = Capture.newInstance();
        bigquery = options.getService();
        ImmutableList<Job> jobList = ImmutableList.of(new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_QUERY_JOB)), new Job(bigquery, new JobInfo.BuilderImpl(BigQueryImplTest.COMPLETE_LOAD_JOB)));
        Tuple<String, Iterable<Job>> result = Tuple.of(BigQueryImplTest.CURSOR, Iterables.transform(jobList, new Function<Job, Job>() {
            @Override
            public Job apply(Job job) {
                return job.toPb();
            }
        }));
        EasyMock.expect(bigqueryRpcMock.listJobs(eq(BigQueryImplTest.PROJECT), capture(capturedOptions))).andReturn(result);
        EasyMock.replay(bigqueryRpcMock);
        Page<Job> page = bigquery.listJobs(BigQueryImplTest.JOB_LIST_OPTION_FIELD);
        Assert.assertEquals(BigQueryImplTest.CURSOR, page.getNextPageToken());
        Assert.assertArrayEquals(jobList.toArray(), Iterables.toArray(page.getValues(), Job.class));
        String selector = ((String) (capturedOptions.getValue().get(BigQueryImplTest.JOB_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("nextPageToken,jobs("));
        Assert.assertTrue(selector.contains("configuration"));
        Assert.assertTrue(selector.contains("jobReference"));
        Assert.assertTrue(selector.contains("statistics"));
        Assert.assertTrue(selector.contains("state"));
        Assert.assertTrue(selector.contains("errorResult"));
        Assert.assertTrue(selector.contains(")"));
        Assert.assertEquals(75, selector.length());
    }

    @Test
    public void testCancelJob() {
        EasyMock.expect(bigqueryRpcMock.cancel(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.cancel(BigQueryImplTest.JOB));
    }

    @Test
    public void testCancelJobFromJobId() {
        EasyMock.expect(bigqueryRpcMock.cancel(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.cancel(JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB)));
    }

    @Test
    public void testCancelJobFromJobIdWithProject() {
        JobId jobId = JobId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB);
        EasyMock.expect(bigqueryRpcMock.cancel(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB, null)).andReturn(true);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Assert.assertTrue(bigquery.cancel(jobId));
    }

    @Test
    public void testQueryRequestCompleted() throws InterruptedException {
        JobId queryJob = JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB);
        Job jobResponsePb = new Job().setConfiguration(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY.toPb()).setJobReference(queryJob.toPb()).setId(BigQueryImplTest.JOB).setStatus(new JobStatus().setState("DONE"));
        jobResponsePb.getConfiguration().getQuery().setDestinationTable(BigQueryImplTest.TABLE_ID.toPb());
        GetQueryResultsResponse responsePb = new GetQueryResultsResponse().setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L)).setSchema(BigQueryImplTest.TABLE_SCHEMA.toPb());
        EasyMock.expect(bigqueryRpcMock.create(BigQueryImplTest.JOB_INFO.toPb(), Collections.<BigQueryRpc.Option, Object>emptyMap())).andReturn(jobResponsePb);
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImpl.optionMap(DEFAULT_QUERY_WAIT_OPTIONS))).andReturn(responsePb);
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, Collections.<BigQueryRpc.Option, Object>emptyMap())).andReturn(new TableDataList().setPageToken("").setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setTotalRows(1L));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        TableResult result = bigquery.query(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY, queryJob);
        assertThat(result.getSchema()).isEqualTo(BigQueryImplTest.TABLE_SCHEMA);
        assertThat(result.getTotalRows()).isEqualTo(1);
        for (FieldValueList row : result.getValues()) {
            assertThat(row.get(0).getBooleanValue()).isFalse();
            assertThat(row.get(1).getLongValue()).isEqualTo(1);
        }
    }

    @Test
    public void testQueryRequestCompletedOptions() throws InterruptedException {
        JobId queryJob = JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB);
        Job jobResponsePb = new Job().setConfiguration(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY.toPb()).setJobReference(queryJob.toPb()).setId(BigQueryImplTest.JOB).setStatus(new JobStatus().setState("DONE"));
        jobResponsePb.getConfiguration().getQuery().setDestinationTable(BigQueryImplTest.TABLE_ID.toPb());
        GetQueryResultsResponse responsePb = new GetQueryResultsResponse().setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L)).setSchema(BigQueryImplTest.TABLE_SCHEMA.toPb());
        EasyMock.expect(bigqueryRpcMock.create(BigQueryImplTest.JOB_INFO.toPb(), Collections.<BigQueryRpc.Option, Object>emptyMap())).andReturn(jobResponsePb);
        Map<BigQueryRpc.Option, Object> optionMap = Maps.newEnumMap(Option.class);
        QueryResultsOption pageSizeOption = QueryResultsOption.pageSize(42L);
        optionMap.put(pageSizeOption.getRpcOption(), pageSizeOption.getValue());
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImpl.optionMap(DEFAULT_QUERY_WAIT_OPTIONS))).andReturn(responsePb);
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, optionMap)).andReturn(new TableDataList().setPageToken("").setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setTotalRows(1L));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        Job job = bigquery.create(JobInfo.of(queryJob, BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY));
        TableResult result = job.getQueryResults(pageSizeOption);
        assertThat(result.getSchema()).isEqualTo(BigQueryImplTest.TABLE_SCHEMA);
        assertThat(result.getTotalRows()).isEqualTo(1);
        for (FieldValueList row : result.getValues()) {
            assertThat(row.get(0).getBooleanValue()).isFalse();
            assertThat(row.get(1).getLongValue()).isEqualTo(1);
        }
    }

    @Test
    public void testQueryRequestCompletedOnSecondAttempt() throws InterruptedException {
        JobId queryJob = JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB);
        Job jobResponsePb1 = new Job().setConfiguration(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY.toPb()).setJobReference(queryJob.toPb()).setId(BigQueryImplTest.JOB);
        jobResponsePb1.getConfiguration().getQuery().setDestinationTable(BigQueryImplTest.TABLE_ID.toPb());
        GetQueryResultsResponse responsePb1 = new GetQueryResultsResponse().setJobReference(queryJob.toPb()).setJobComplete(false);
        GetQueryResultsResponse responsePb2 = new GetQueryResultsResponse().setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L)).setSchema(BigQueryImplTest.TABLE_SCHEMA.toPb());
        EasyMock.expect(bigqueryRpcMock.create(BigQueryImplTest.JOB_INFO.toPb(), Collections.<BigQueryRpc.Option, Object>emptyMap())).andReturn(jobResponsePb1);
        EasyMock.expect(bigqueryRpcMock.getJob(eq(BigQueryImplTest.PROJECT), eq(BigQueryImplTest.JOB), anyString(), anyObject(Map.class))).andReturn(jobResponsePb1);
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImpl.optionMap(DEFAULT_QUERY_WAIT_OPTIONS))).andReturn(responsePb1);
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImpl.optionMap(DEFAULT_QUERY_WAIT_OPTIONS))).andReturn(responsePb2);
        EasyMock.expect(bigqueryRpcMock.listTableData(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.TABLE, Collections.<BigQueryRpc.Option, Object>emptyMap())).andReturn(new TableDataList().setPageToken("").setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setTotalRows(1L));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        TableResult result = bigquery.query(BigQueryImplTest.QUERY_JOB_CONFIGURATION_FOR_QUERY, queryJob);
        assertThat(result.getSchema()).isEqualTo(BigQueryImplTest.TABLE_SCHEMA);
        assertThat(result.getTotalRows()).isEqualTo(1);
        for (FieldValueList row : result.getValues()) {
            assertThat(row.get(0).getBooleanValue()).isFalse();
            assertThat(row.get(1).getLongValue()).isEqualTo(1);
        }
    }

    @Test
    public void testGetQueryResults() {
        JobId queryJob = JobId.of(BigQueryImplTest.JOB);
        GetQueryResultsResponse responsePb = new GetQueryResultsResponse().setEtag("etag").setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L));
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        QueryResponse response = bigquery.getQueryResults(queryJob);
        Assert.assertEquals(true, response.getCompleted());
        Assert.assertEquals(null, response.getSchema());
    }

    @Test
    public void testGetQueryResultsWithProject() {
        JobId queryJob = JobId.of(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB);
        GetQueryResultsResponse responsePb = new GetQueryResultsResponse().setEtag("etag").setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L));
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.OTHER_PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        QueryResponse response = bigquery.getQueryResults(queryJob);
        Assert.assertTrue(response.getCompleted());
        Assert.assertEquals(null, response.getSchema());
    }

    @Test
    public void testGetQueryResultsWithOptions() {
        JobId queryJob = JobId.of(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB);
        GetQueryResultsResponse responsePb = new GetQueryResultsResponse().setJobReference(queryJob.toPb()).setRows(ImmutableList.of(BigQueryImplTest.TABLE_ROW)).setJobComplete(true).setCacheHit(false).setPageToken(BigQueryImplTest.CURSOR).setTotalBytesProcessed(42L).setTotalRows(BigInteger.valueOf(1L));
        EasyMock.expect(bigqueryRpcMock.getQueryResults(BigQueryImplTest.PROJECT, BigQueryImplTest.JOB, null, BigQueryImplTest.QUERY_RESULTS_OPTIONS)).andReturn(responsePb);
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.getService();
        QueryResponse response = bigquery.getQueryResults(queryJob, BigQueryImplTest.QUERY_RESULTS_OPTION_TIME, BigQueryImplTest.QUERY_RESULTS_OPTION_INDEX, BigQueryImplTest.QUERY_RESULTS_OPTION_PAGE_SIZE, BigQueryImplTest.QUERY_RESULTS_OPTION_PAGE_TOKEN);
        Assert.assertEquals(true, response.getCompleted());
        Assert.assertEquals(null, response.getSchema());
    }

    @Test
    public void testRetryableException() {
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new BigQueryException(500, "InternalError")).andReturn(BigQueryImplTest.DATASET_INFO_WITH_PROJECT.toPb());
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        Dataset dataset = bigquery.getDataset(BigQueryImplTest.DATASET);
        Assert.assertEquals(new Dataset(bigquery, new DatasetInfo.BuilderImpl(BigQueryImplTest.DATASET_INFO_WITH_PROJECT)), dataset);
    }

    @Test
    public void testNonRetryableException() {
        String exceptionMessage = "Not Implemented";
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new BigQueryException(501, exceptionMessage));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(BigQueryException.class);
        thrown.expectMessage(exceptionMessage);
        bigquery.getDataset(DatasetId.of(BigQueryImplTest.DATASET));
    }

    @Test
    public void testRuntimeException() {
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(bigqueryRpcMock.getDataset(BigQueryImplTest.PROJECT, BigQueryImplTest.DATASET, BigQueryImplTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(bigqueryRpcMock);
        bigquery = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(BigQueryException.class);
        thrown.expectMessage(exceptionMessage);
        bigquery.getDataset(BigQueryImplTest.DATASET);
    }

    @Test
    public void testQueryDryRun() throws Exception {
        // https://github.com/googleapis/google-cloud-java/issues/2479
        EasyMock.replay(bigqueryRpcMock);
        thrown.expect(UnsupportedOperationException.class);
        options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService().query(QueryJobConfiguration.newBuilder("foo").setDryRun(true).build());
    }
}

