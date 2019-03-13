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
package org.apache.beam.sdk.extensions.sql.impl.rel;


import Schema.FieldType.DOUBLE;
import Schema.FieldType.INT32;
import Schema.FieldType.INT64;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestBoundedTable;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Test for {@code BeamSortRel}.
 */
public class BeamSortRelTest extends BaseRelTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOrderBy_basic() throws Exception {
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id, price)  SELECT " + ((" order_id, site_id, price " + "FROM ORDER_DETAILS ") + "ORDER BY order_id asc, site_id desc limit 4");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(INT64, "order_id", INT32, "site_id", DOUBLE, "price").addRows(1L, 2, 1.0, 1L, 1, 2.0, 2L, 4, 3.0, 2L, 1, 4.0).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_timestamp() throws Exception {
        String sql = "SELECT order_id, site_id, price, order_time " + ("FROM ORDER_DETAILS " + "ORDER BY order_time desc limit 4");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(INT64, "order_id", INT32, "site_id", DOUBLE, "price", Schema.FieldType.DATETIME, "order_time").addRows(7L, 7, 7.0, new DateTime(6), 8L, 8888, 8.0, new DateTime(7), 8L, 999, 9.0, new DateTime(8), 10L, 100, 10.0, new DateTime(9)).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_nullsFirst() throws Exception {
        Schema schema = Schema.builder().addField("order_id", INT64).addNullableField("site_id", INT32).addField("price", DOUBLE).build();
        BaseRelTest.registerTable("ORDER_DETAILS", TestBoundedTable.of(schema).addRows(1L, 2, 1.0, 1L, null, 2.0, 2L, 1, 3.0, 2L, null, 4.0, 5L, 5, 5.0));
        BaseRelTest.registerTable("SUB_ORDER_RAM", TestBoundedTable.of(schema));
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id, price)  SELECT " + ((" order_id, site_id, price " + "FROM ORDER_DETAILS ") + "ORDER BY order_id asc, site_id desc NULLS FIRST limit 4");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(schema).addRows(1L, null, 2.0, 1L, 2, 1.0, 2L, null, 4.0, 2L, 1, 3.0).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_nullsLast() throws Exception {
        Schema schema = Schema.builder().addField("order_id", INT64).addNullableField("site_id", INT32).addField("price", DOUBLE).build();
        BaseRelTest.registerTable("ORDER_DETAILS", TestBoundedTable.of(schema).addRows(1L, 2, 1.0, 1L, null, 2.0, 2L, 1, 3.0, 2L, null, 4.0, 5L, 5, 5.0));
        BaseRelTest.registerTable("SUB_ORDER_RAM", TestBoundedTable.of(schema));
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id, price)  SELECT " + ((" order_id, site_id, price " + "FROM ORDER_DETAILS ") + "ORDER BY order_id asc, site_id desc NULLS LAST limit 4");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(schema).addRows(1L, 2, 1.0, 1L, null, 2.0, 2L, 1, 3.0, 2L, null, 4.0).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_with_offset() throws Exception {
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id, price)  SELECT " + ((" order_id, site_id, price " + "FROM ORDER_DETAILS ") + "ORDER BY order_id asc, site_id desc limit 4 offset 4");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(INT64, "order_id", INT32, "site_id", DOUBLE, "price").addRows(5L, 5, 5.0, 6L, 6, 6.0, 7L, 7, 7.0, 8L, 8888, 8.0).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_bigFetch() throws Exception {
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id, price)  SELECT " + ((" order_id, site_id, price " + "FROM ORDER_DETAILS ") + "ORDER BY order_id asc, site_id desc limit 11");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(INT64, "order_id", INT32, "site_id", DOUBLE, "price").addRows(1L, 2, 1.0, 1L, 1, 2.0, 2L, 4, 3.0, 2L, 1, 4.0, 5L, 5, 5.0, 6L, 6, 6.0, 7L, 7, 7.0, 8L, 8888, 8.0, 8L, 999, 9.0, 10L, 100, 10.0).getRows());
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testOrderBy_exception() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("`ORDER BY` is only supported for GlobalWindows");
        String sql = "INSERT INTO SUB_ORDER_RAM(order_id, site_id)  SELECT " + (((" order_id, COUNT(*) " + "FROM ORDER_DETAILS ") + "GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)") + "ORDER BY order_id asc limit 11");
        TestPipeline pipeline = TestPipeline.create();
        BaseRelTest.compilePipeline(sql, pipeline);
    }
}

