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


import Mode.REPEATED;
import Schema.FieldType;
import Schema.FieldType.BOOLEAN;
import Schema.FieldType.DATETIME;
import Schema.FieldType.DOUBLE;
import Schema.FieldType.INT64;
import Schema.FieldType.STRING;
import StandardSQLTypeName.BOOL;
import StandardSQLTypeName.FLOAT64;
import StandardSQLTypeName.STRUCT;
import StandardSQLTypeName.TIMESTAMP;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.values.Row;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BigQueryUtils}.
 */
public class BigQueryUtilsTest {
    private static final Schema FLAT_TYPE = Schema.builder().addNullableField("id", INT64).addNullableField("value", DOUBLE).addNullableField("name", STRING).addNullableField("timestamp", DATETIME).addNullableField("valid", BOOLEAN).build();

    private static final Schema ARRAY_TYPE = Schema.builder().addArrayField("ids", INT64).build();

    private static final Schema ROW_TYPE = Schema.builder().addNullableField("row", FieldType.row(BigQueryUtilsTest.FLAT_TYPE)).build();

    private static final Schema ARRAY_ROW_TYPE = Schema.builder().addArrayField("rows", FieldType.row(BigQueryUtilsTest.FLAT_TYPE)).build();

    private static final TableFieldSchema ID = new TableFieldSchema().setName("id").setType(StandardSQLTypeName.INT64.toString());

    private static final TableFieldSchema VALUE = new TableFieldSchema().setName("value").setType(FLOAT64.toString());

    private static final TableFieldSchema NAME = new TableFieldSchema().setName("name").setType(StandardSQLTypeName.STRING.toString());

    private static final TableFieldSchema TIMESTAMP = new TableFieldSchema().setName("timestamp").setType(StandardSQLTypeName.TIMESTAMP.toString());

    private static final TableFieldSchema VALID = new TableFieldSchema().setName("valid").setType(BOOL.toString());

    private static final TableFieldSchema IDS = new TableFieldSchema().setName("ids").setType(StandardSQLTypeName.INT64.toString()).setMode(REPEATED.toString());

    private static final Row FLAT_ROW = Row.withSchema(BigQueryUtilsTest.FLAT_TYPE).addValues(123L, 123.456, "test", new DateTime(123456), false).build();

    private static final Row NULL_FLAT_ROW = Row.withSchema(BigQueryUtilsTest.FLAT_TYPE).addValues(null, null, null, null, null).build();

    private static final Row ARRAY_ROW = Row.withSchema(BigQueryUtilsTest.ARRAY_TYPE).addValues(((Object) (Arrays.asList(123L, 124L)))).build();

    private static final Row ROW_ROW = Row.withSchema(BigQueryUtilsTest.ROW_TYPE).addValues(BigQueryUtilsTest.FLAT_ROW).build();

    private static final Row ARRAY_ROW_ROW = Row.withSchema(BigQueryUtilsTest.ARRAY_ROW_TYPE).addValues(((Object) (Arrays.asList(BigQueryUtilsTest.FLAT_ROW)))).build();

    @Test
    public void testToTableSchema_flat() {
        TableSchema schema = BigQueryUtils.toTableSchema(BigQueryUtilsTest.FLAT_TYPE);
        Assert.assertThat(schema.getFields(), Matchers.containsInAnyOrder(BigQueryUtilsTest.ID, BigQueryUtilsTest.VALUE, BigQueryUtilsTest.NAME, BigQueryUtilsTest.TIMESTAMP, BigQueryUtilsTest.VALID));
    }

    @Test
    public void testToTableSchema_array() {
        TableSchema schema = BigQueryUtils.toTableSchema(BigQueryUtilsTest.ARRAY_TYPE);
        Assert.assertThat(schema.getFields(), Matchers.contains(BigQueryUtilsTest.IDS));
    }

    @Test
    public void testToTableSchema_row() {
        TableSchema schema = BigQueryUtils.toTableSchema(BigQueryUtilsTest.ROW_TYPE);
        Assert.assertThat(schema.getFields().size(), Matchers.equalTo(1));
        TableFieldSchema field = schema.getFields().get(0);
        Assert.assertThat(field.getName(), Matchers.equalTo("row"));
        Assert.assertThat(field.getType(), Matchers.equalTo(STRUCT.toString()));
        Assert.assertThat(field.getMode(), Matchers.nullValue());
        Assert.assertThat(field.getFields(), Matchers.containsInAnyOrder(BigQueryUtilsTest.ID, BigQueryUtilsTest.VALUE, BigQueryUtilsTest.NAME, BigQueryUtilsTest.TIMESTAMP, BigQueryUtilsTest.VALID));
    }

    @Test
    public void testToTableSchema_array_row() {
        TableSchema schema = BigQueryUtils.toTableSchema(BigQueryUtilsTest.ARRAY_ROW_TYPE);
        Assert.assertThat(schema.getFields().size(), Matchers.equalTo(1));
        TableFieldSchema field = schema.getFields().get(0);
        Assert.assertThat(field.getName(), Matchers.equalTo("rows"));
        Assert.assertThat(field.getType(), Matchers.equalTo(STRUCT.toString()));
        Assert.assertThat(field.getMode(), Matchers.equalTo(REPEATED.toString()));
        Assert.assertThat(field.getFields(), Matchers.containsInAnyOrder(BigQueryUtilsTest.ID, BigQueryUtilsTest.VALUE, BigQueryUtilsTest.NAME, BigQueryUtilsTest.TIMESTAMP, BigQueryUtilsTest.VALID));
    }

    @Test
    public void testToTableRow_flat() {
        TableRow row = BigQueryUtils.toTableRow().apply(BigQueryUtilsTest.FLAT_ROW);
        Assert.assertThat(row.size(), Matchers.equalTo(5));
        Assert.assertThat(row, hasEntry("id", 123L));
        Assert.assertThat(row, hasEntry("value", 123.456));
        Assert.assertThat(row, hasEntry("name", "test"));
        Assert.assertThat(row, hasEntry("valid", false));
    }

    @Test
    public void testToTableRow_array() {
        TableRow row = BigQueryUtils.toTableRow().apply(BigQueryUtilsTest.ARRAY_ROW);
        Assert.assertThat(row, hasEntry("ids", Arrays.asList(123L, 124L)));
        Assert.assertThat(row.size(), Matchers.equalTo(1));
    }

    @Test
    public void testToTableRow_row() {
        TableRow row = BigQueryUtils.toTableRow().apply(BigQueryUtilsTest.ROW_ROW);
        Assert.assertThat(row.size(), Matchers.equalTo(1));
        row = ((TableRow) (row.get("row")));
        Assert.assertThat(row.size(), Matchers.equalTo(5));
        Assert.assertThat(row, hasEntry("id", 123L));
        Assert.assertThat(row, hasEntry("value", 123.456));
        Assert.assertThat(row, hasEntry("name", "test"));
        Assert.assertThat(row, hasEntry("valid", false));
    }

    @Test
    public void testToTableRow_array_row() {
        TableRow row = BigQueryUtils.toTableRow().apply(BigQueryUtilsTest.ARRAY_ROW_ROW);
        Assert.assertThat(row.size(), Matchers.equalTo(1));
        row = ((List<TableRow>) (row.get("rows"))).get(0);
        Assert.assertThat(row.size(), Matchers.equalTo(5));
        Assert.assertThat(row, hasEntry("id", 123L));
        Assert.assertThat(row, hasEntry("value", 123.456));
        Assert.assertThat(row, hasEntry("name", "test"));
        Assert.assertThat(row, hasEntry("valid", false));
    }

    @Test
    public void testToTableRow_null_row() {
        TableRow row = BigQueryUtils.toTableRow().apply(BigQueryUtilsTest.NULL_FLAT_ROW);
        Assert.assertThat(row.size(), Matchers.equalTo(5));
        Assert.assertThat(row, hasEntry("id", null));
        Assert.assertThat(row, hasEntry("value", null));
        Assert.assertThat(row, hasEntry("name", null));
        Assert.assertThat(row, hasEntry("timestamp", null));
        Assert.assertThat(row, hasEntry("valid", null));
    }
}

