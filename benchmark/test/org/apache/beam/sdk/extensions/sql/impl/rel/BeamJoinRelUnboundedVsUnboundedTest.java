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


import Schema.FieldType.INT32;
import org.apache.beam.sdk.extensions.sql.TestUtils;
import org.apache.beam.sdk.extensions.sql.impl.transform.BeamSqlOutputToConsoleFn;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Unbounded + Unbounded Test for {@code BeamJoinRel}.
 */
public class BeamJoinRelUnboundedVsUnboundedTest extends BaseRelTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    public static final DateTime FIRST_DATE = new DateTime(1);

    public static final DateTime SECOND_DATE = new DateTime((1 + (3600 * 1000)));

    private static final Duration WINDOW_SIZE = Duration.standardHours(1);

    @Test
    public void testInnerJoin() throws Exception {
        String sql = "SELECT * FROM " + (((((("(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o2 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addField("order_id1", INT32).addField("sum_site_id", INT32).addField("order_id", INT32).addField("sum_site_id0", INT32).build()).addRows(1, 3, 1, 3, 2, 5, 2, 5).getStringRows());
        pipeline.run();
    }

    @Test
    public void testLeftOuterJoin() throws Exception {
        String sql = "SELECT * FROM " + (((((("(select site_id as order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY site_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " LEFT OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o2 ") + " on ") + " o1.order_id=o2.order_id");
        // 1, 1 | 1, 3
        // 2, 2 | NULL, NULL
        // ---- | -----
        // 2, 2 | 2, 5
        // 3, 3 | NULL, NULL
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addField("order_id1", INT32).addField("sum_site_id", INT32).addNullableField("order_id", INT32).addNullableField("sum_site_id0", INT32).build()).addRows(1, 1, 1, 3, 2, 2, null, null, 2, 2, 2, 5, 3, 3, null, null).getStringRows());
        pipeline.run();
    }

    @Test
    public void testRightOuterJoin() throws Exception {
        String sql = "SELECT * FROM " + (((((("(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " RIGHT OUTER JOIN ") + "(select site_id as order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY site_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o2 ") + " on ") + " o1.order_id=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addNullableField("order_id1", INT32).addNullableField("sum_site_id", INT32).addField("order_id", INT32).addField("sum_site_id0", INT32).build()).addRows(1, 3, 1, 1, null, null, 2, 2, 2, 5, 2, 2, null, null, 3, 3).getStringRows());
        pipeline.run();
    }

    @Test
    public void testFullOuterJoin() throws Exception {
        String sql = "SELECT * FROM " + (((((("(select price as order_id1, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY price, TUMBLE(order_time, INTERVAL '1' HOUR)) o1 ") + " FULL OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id , TUMBLE(order_time, INTERVAL '1' HOUR)) o2 ") + " on ") + " o1.order_id1=o2.order_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        rows.apply(ParDo.of(new BeamSqlOutputToConsoleFn("hello")));
        PAssert.that(rows.apply(ParDo.of(new TestUtils.BeamSqlRow2StringDoFn()))).containsInAnyOrder(of(Schema.builder().addNullableField("order_id1", INT32).addNullableField("sum_site_id", INT32).addNullableField("order_id", INT32).addNullableField("sum_site_id0", INT32).build()).addRows(1, 1, 1, 3, 6, 2, null, null, 7, 2, null, null, 8, 3, null, null, null, null, 2, 5).getStringRows());
        pipeline.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWindowsMismatch() throws Exception {
        String sql = "SELECT * FROM " + (((((("(select site_id as order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS " + "          GROUP BY site_id, TUMBLE(order_time, INTERVAL '2' HOUR)) o1 ") + " LEFT OUTER JOIN ") + "(select order_id, sum(site_id) as sum_site_id FROM ORDER_DETAILS ") + "          GROUP BY order_id, TUMBLE(order_time, INTERVAL '1' HOUR)) o2 ") + " on ") + " o1.order_id=o2.order_id");
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }
}

