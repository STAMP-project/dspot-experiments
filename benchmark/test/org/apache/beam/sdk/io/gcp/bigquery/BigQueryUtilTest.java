/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.gcp.bigquery;


import Bigquery.Tabledata;
import Bigquery.Tables;
import Bigquery.Tables.Get;
import GlobalWindow.INSTANCE;
import GlobalWindow.TIMESTAMP_MAX_VALUE;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableDataList;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryServicesImpl.DatasetServiceImpl;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.values.ValueInSingleWindow;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for util classes related to BigQuery.
 */
@RunWith(JUnit4.class)
public class BigQueryUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Bigquery mockClient;

    @Mock
    private Tables mockTables;

    @Mock
    private Get mockTablesGet;

    @Mock
    private Tabledata mockTabledata;

    @Mock
    private List mockTabledataList;

    private PipelineOptions options;

    @Test
    public void testTableGet() throws IOException, InterruptedException {
        onTableGet(basicTableSchema());
        TableDataList dataList = new TableDataList().setTotalRows(0L);
        onTableList(dataList);
        BigQueryServicesImpl.DatasetServiceImpl services = new BigQueryServicesImpl.DatasetServiceImpl(mockClient, options);
        services.getTable(new TableReference().setProjectId("project").setDatasetId("dataset").setTableId("table"));
        verifyTableGet();
    }

    @Test
    public void testInsertAll() throws Exception {
        // Build up a list of indices to fail on each invocation. This should result in
        // 5 calls to insertAll.
        List<List<Long>> errorsIndices = new ArrayList<>();
        errorsIndices.add(Arrays.asList(0L, 5L, 10L, 15L, 20L));
        errorsIndices.add(Arrays.asList(0L, 2L, 4L));
        errorsIndices.add(Arrays.asList(0L, 2L));
        errorsIndices.add(new ArrayList<>());
        onInsertAll(errorsIndices);
        TableReference ref = BigQueryHelpers.parseTableSpec("project:dataset.table");
        DatasetServiceImpl datasetService = new DatasetServiceImpl(mockClient, options, 5);
        List<ValueInSingleWindow<TableRow>> rows = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 25; ++i) {
            rows.add(ValueInSingleWindow.of(rawRow("foo", 1234), TIMESTAMP_MAX_VALUE, INSTANCE, ON_TIME_AND_ONLY_FIRING));
            ids.add("");
        }
        long totalBytes = 0;
        try {
            totalBytes = datasetService.insertAll(ref, rows, ids, InsertRetryPolicy.alwaysRetry(), null, null, false, false);
        } finally {
            verifyInsertAll(5);
            // Each of the 25 rows is 23 bytes: "{f=[{v=foo}, {v=1234}]}"
            Assert.assertEquals("Incorrect byte count", (25L * 23L), totalBytes);
        }
    }
}

