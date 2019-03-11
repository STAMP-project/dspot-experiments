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
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestBoundedTable;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.junit.Rule;
import org.junit.Test;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Bounded + Bounded Test for {@code BeamJoinRel}.
 */
public class BeamJoinRelBoundedVsBoundedTest extends BaseRelTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    public static final TestBoundedTable ORDER_DETAILS1 = TestBoundedTable.of(INT32, "order_id", INT32, "site_id", INT32, "price").addRows(1, 2, 3, 2, 3, 3, 3, 4, 5);

    public static final TestBoundedTable ORDER_DETAILS2 = TestBoundedTable.of(INT32, "order_id", INT32, "site_id", INT32, "price").addRows(1, 2, 3, 2, 3, 3, 3, 4, 5);

    @Test
    public void testInnerJoin() throws Exception {
        String sql = "SELECT *  " + ((("FROM ORDER_DETAILS1 o1" + " JOIN ORDER_DETAILS2 o2") + " on ") + " o1.order_id=o2.site_id AND o2.price=o1.site_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(Schema.builder().addField("order_id", INT32).addField("site_id", INT32).addField("price", INT32).addField("order_id0", INT32).addField("site_id0", INT32).addField("price0", INT32).build()).addRows(2, 3, 3, 1, 2, 3).getRows());
        pipeline.run();
    }

    @Test
    public void testLeftOuterJoin() throws Exception {
        String sql = "SELECT *  " + ((("FROM ORDER_DETAILS1 o1" + " LEFT OUTER JOIN ORDER_DETAILS2 o2") + " on ") + " o1.order_id=o2.site_id AND o2.price=o1.site_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.enableAbandonedNodeEnforcement(false);
        PAssert.that(rows).containsInAnyOrder(of(Schema.builder().addField("order_id", INT32).addField("site_id", INT32).addField("price", INT32).addNullableField("order_id0", INT32).addNullableField("site_id0", INT32).addNullableField("price0", INT32).build()).addRows(1, 2, 3, null, null, null, 2, 3, 3, 1, 2, 3, 3, 4, 5, null, null, null).getRows());
        pipeline.run();
    }

    @Test
    public void testRightOuterJoin() throws Exception {
        String sql = "SELECT *  " + ((("FROM ORDER_DETAILS1 o1" + " RIGHT OUTER JOIN ORDER_DETAILS2 o2") + " on ") + " o1.order_id=o2.site_id AND o2.price=o1.site_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(Schema.builder().addNullableField("order_id", INT32).addNullableField("site_id", INT32).addNullableField("price", INT32).addField("order_id0", INT32).addField("site_id0", INT32).addField("price0", INT32).build()).addRows(2, 3, 3, 1, 2, 3, null, null, null, 2, 3, 3, null, null, null, 3, 4, 5).getRows());
        pipeline.run();
    }

    @Test
    public void testFullOuterJoin() throws Exception {
        String sql = "SELECT *  " + ((("FROM ORDER_DETAILS1 o1" + " FULL OUTER JOIN ORDER_DETAILS2 o2") + " on ") + " o1.order_id=o2.site_id AND o2.price=o1.site_id");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(Schema.builder().addNullableField("order_id", INT32).addNullableField("site_id", INT32).addNullableField("price", INT32).addNullableField("order_id0", INT32).addNullableField("site_id0", INT32).addNullableField("price0", INT32).build()).addRows(2, 3, 3, 1, 2, 3, 1, 2, 3, null, null, null, 3, 4, 5, null, null, null, null, null, null, 2, 3, 3, null, null, null, 3, 4, 5).getRows());
        pipeline.run();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testException_nonEqualJoin() throws Exception {
        String sql = "SELECT *  " + ((("FROM ORDER_DETAILS1 o1" + " JOIN ORDER_DETAILS2 o2") + " on ") + " o1.order_id>o2.site_id");
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testException_crossJoin() throws Exception {
        String sql = "SELECT *  " + "FROM ORDER_DETAILS1 o1, ORDER_DETAILS2 o2";
        pipeline.enableAbandonedNodeEnforcement(false);
        BaseRelTest.compilePipeline(sql, pipeline);
        pipeline.run();
    }
}

