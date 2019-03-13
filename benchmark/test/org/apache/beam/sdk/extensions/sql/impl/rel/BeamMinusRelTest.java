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


import java.math.BigDecimal;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.junit.Rule;
import org.junit.Test;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Test for {@code BeamMinusRel}.
 */
public class BeamMinusRelTest extends BaseRelTest {
    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testExcept() throws Exception {
        String sql = "";
        sql += "SELECT order_id, site_id, price " + ((("FROM ORDER_DETAILS1 " + " EXCEPT ") + "SELECT order_id, site_id, price ") + "FROM ORDER_DETAILS2 ");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).containsInAnyOrder(of(Schema.FieldType.INT64, "order_id", Schema.FieldType.INT32, "site_id", Schema.FieldType.DECIMAL, "price").addRows(1L, 1, new BigDecimal(1.0), 4L, 4, new BigDecimal(4.0)).getRows());
        pipeline.run();
    }

    @Test
    public void testExceptAll() throws Exception {
        String sql = "";
        sql += "SELECT order_id, site_id, price " + ((("FROM ORDER_DETAILS1 " + " EXCEPT ALL ") + "SELECT order_id, site_id, price ") + "FROM ORDER_DETAILS2 ");
        PCollection<Row> rows = BaseRelTest.compilePipeline(sql, pipeline);
        PAssert.that(rows).satisfies(new CheckSize(3));
        PAssert.that(rows).containsInAnyOrder(of(Schema.FieldType.INT64, "order_id", Schema.FieldType.INT32, "site_id", Schema.FieldType.DECIMAL, "price").addRows(1L, 1, new BigDecimal(1.0), 4L, 4, new BigDecimal(4.0), 4L, 4, new BigDecimal(4.0)).getRows());
        pipeline.run();
    }
}

