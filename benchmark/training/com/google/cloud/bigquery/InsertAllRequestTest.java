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


import InsertAllRequest.RowToInsert;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class InsertAllRequestTest {
    private static final Map<String, Object> CONTENT1 = ImmutableMap.<String, Object>of("key", "val1");

    private static final Map<String, Object> CONTENT2 = ImmutableMap.<String, Object>of("key", "val2");

    private static final List<InsertAllRequest.RowToInsert> ROWS = ImmutableList.of(RowToInsert.of(InsertAllRequestTest.CONTENT1), RowToInsert.of(InsertAllRequestTest.CONTENT2));

    private static final List<InsertAllRequest.RowToInsert> ROWS_WITH_ID = ImmutableList.of(RowToInsert.of("id1", InsertAllRequestTest.CONTENT1), RowToInsert.of("id2", InsertAllRequestTest.CONTENT2));

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final Schema TABLE_SCHEMA = Schema.of();

    private static final TableDefinition TABLE_DEFINITION = StandardTableDefinition.of(InsertAllRequestTest.TABLE_SCHEMA);

    private static final TableInfo TABLE_INFO = TableInfo.of(InsertAllRequestTest.TABLE_ID, InsertAllRequestTest.TABLE_DEFINITION);

    private static final boolean SKIP_INVALID_ROWS = true;

    private static final boolean IGNORE_UNKNOWN_VALUES = false;

    private static final String TEMPLATE_SUFFIX = "templateSuffix";

    private static final InsertAllRequest INSERT_ALL_REQUEST1 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID).addRow(InsertAllRequestTest.CONTENT1).addRow(InsertAllRequestTest.CONTENT2).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST2 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID).setRows(InsertAllRequestTest.ROWS).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST3 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable()).setRows(InsertAllRequestTest.ROWS_WITH_ID).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST4 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID, InsertAllRequestTest.ROWS).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST5 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable(), InsertAllRequestTest.ROWS_WITH_ID).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST6 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID, InsertAllRequestTest.ROWS.get(0), InsertAllRequestTest.ROWS.get(1)).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST7 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable(), InsertAllRequestTest.ROWS_WITH_ID.get(0), InsertAllRequestTest.ROWS_WITH_ID.get(1)).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST8 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable()).addRow("id1", InsertAllRequestTest.CONTENT1).addRow("id2", InsertAllRequestTest.CONTENT2).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST9 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_INFO).addRow("id1", InsertAllRequestTest.CONTENT1).addRow("id2", InsertAllRequestTest.CONTENT2).setIgnoreUnknownValues(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).setSkipInvalidRows(InsertAllRequestTest.SKIP_INVALID_ROWS).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST10 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_INFO).addRow("id1", InsertAllRequestTest.CONTENT1).addRow("id2", InsertAllRequestTest.CONTENT2).setIgnoreUnknownValues(true).setSkipInvalidRows(false).build();

    private static final InsertAllRequest INSERT_ALL_REQUEST11 = InsertAllRequest.newBuilder(InsertAllRequestTest.TABLE_INFO).addRow("id1", InsertAllRequestTest.CONTENT1).addRow("id2", InsertAllRequestTest.CONTENT2).setIgnoreUnknownValues(true).setSkipInvalidRows(false).setTemplateSuffix(InsertAllRequestTest.TEMPLATE_SUFFIX).build();

    @Test
    public void testBuilder() {
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST1.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST2.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST3.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST4.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST5.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST6.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST7.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST8.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST9.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST10.getTable());
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST11.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST1.getRows());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST2.getRows());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST4.getRows());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST6.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST3.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST5.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST7.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST8.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST9.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST10.getRows());
        assertThat(InsertAllRequestTest.ROWS_WITH_ID).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST11.getRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST1.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST2.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST3.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST4.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST5.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST6.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST7.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST8.skipInvalidRows());
        assertThat(InsertAllRequestTest.SKIP_INVALID_ROWS).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST9.skipInvalidRows());
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST10.skipInvalidRows()).isFalse();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST11.skipInvalidRows()).isFalse();
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST1.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST2.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST3.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST4.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST5.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST6.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST7.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST8.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.IGNORE_UNKNOWN_VALUES).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST9.ignoreUnknownValues());
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST10.ignoreUnknownValues()).isTrue();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST11.ignoreUnknownValues()).isTrue();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST1.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST2.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST3.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST4.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST5.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST6.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST7.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST8.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST9.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.INSERT_ALL_REQUEST10.getTemplateSuffix()).isNull();
        assertThat(InsertAllRequestTest.TEMPLATE_SUFFIX).isEqualTo(InsertAllRequestTest.INSERT_ALL_REQUEST11.getTemplateSuffix());
    }

    @Test
    public void testOf() {
        InsertAllRequest request = InsertAllRequest.of(InsertAllRequestTest.TABLE_ID, InsertAllRequestTest.ROWS);
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_INFO, InsertAllRequestTest.ROWS);
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable(), InsertAllRequestTest.ROWS);
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable(), InsertAllRequestTest.ROWS);
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_ID, InsertAllRequestTest.ROWS.get(0), InsertAllRequestTest.ROWS.get(1));
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_INFO, InsertAllRequestTest.ROWS.get(0), InsertAllRequestTest.ROWS.get(1));
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
        request = InsertAllRequest.of(InsertAllRequestTest.TABLE_ID.getDataset(), InsertAllRequestTest.TABLE_ID.getTable(), InsertAllRequestTest.ROWS.get(0), InsertAllRequestTest.ROWS.get(1));
        assertThat(InsertAllRequestTest.TABLE_ID).isEqualTo(request.getTable());
        assertThat(InsertAllRequestTest.ROWS).isEqualTo(request.getRows());
    }

    @Test
    public void testEquals() {
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST1, InsertAllRequestTest.INSERT_ALL_REQUEST2);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST2, InsertAllRequestTest.INSERT_ALL_REQUEST4);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST3, InsertAllRequestTest.INSERT_ALL_REQUEST5);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST4, InsertAllRequestTest.INSERT_ALL_REQUEST6);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST5, InsertAllRequestTest.INSERT_ALL_REQUEST7);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST7, InsertAllRequestTest.INSERT_ALL_REQUEST8);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST8, InsertAllRequestTest.INSERT_ALL_REQUEST9);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST10, InsertAllRequestTest.INSERT_ALL_REQUEST10);
        compareInsertAllRequest(InsertAllRequestTest.INSERT_ALL_REQUEST11, InsertAllRequestTest.INSERT_ALL_REQUEST11);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutable() {
        InsertAllRequest.RowToInsert row = RowToInsert.of(new HashMap<String, Object>());
        row.getContent().put("zip", "zap");
    }

    @Test
    public void testNullOK() {
        InsertAllRequest.RowToInsert row = RowToInsert.of(Collections.singletonMap("foo", null));
        assertThat(row.getContent()).containsExactly("foo", null);
    }
}

