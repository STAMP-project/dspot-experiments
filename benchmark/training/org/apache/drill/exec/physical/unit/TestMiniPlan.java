/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.unit;


import BatchSchema.SelectionVectorMode.NONE;
import TypeProtos.MinorType.BIGINT;
import java.util.Collections;
import java.util.List;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.store.dfs.DrillFileSystem;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * This class contains examples to show how to use MiniPlanTestBuilder to test a
 * specific plan fragment (MiniPlan). Each testcase requires 1) a RecordBatch,
 * built from PopBuilder/ScanBuilder, 2)an expected schema and base line values,
 * or 3) indicating no batch is expected.
 */
@Category(PlannerTest.class)
public class TestMiniPlan extends MiniPlanUnitTestBase {
    protected static DrillFileSystem fs;

    @Test
    public void testSimpleParquetScan() throws Exception {
        String file = DrillFileUtils.getResourceAsFile("/tpchmulti/region/01.parquet").toURI().toString();
        List<Path> filePath = Collections.singletonList(new Path(file));
        RecordBatch scanBatch = new MiniPlanUnitTestBase.ParquetScanBuilder().fileSystem(TestMiniPlan.fs).columnsToRead("R_REGIONKEY").inputPaths(filePath).build();
        BatchSchema expectedSchema = new SchemaBuilder().add("R_REGIONKEY", BIGINT).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(0L).baselineValues(1L).go();
    }

    @Test
    public void testSimpleJson() throws Exception {
        List<String> jsonBatches = Lists.newArrayList("{\"a\":100}");
        RecordBatch scanBatch = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(jsonBatches).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(100L).go();
    }

    @Test
    public void testUnionFilter() throws Exception {
        List<String> leftJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]", "[{\"a\": 40, \"b\" : 3},{\"a\": 13, \"b\" : 100}]");
        List<String> rightJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 10 }]", "[{\"a\": 50, \"b\" : 100}]");
        RecordBatch batch = // Children list is provided through RecordBatch
        new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.UnionAll(Collections.<PhysicalOperator>emptyList())).addInputAsChild().physicalOperator(new org.apache.drill.exec.physical.config.Filter(null, parseExpr("a=5"), 1.0F)).addJsonScanAsChild().jsonBatches(leftJsonBatches).columnsToRead("a", "b").buildAddAsInput().buildAddAsInput().addInputAsChild().physicalOperator(new org.apache.drill.exec.physical.config.Filter(null, parseExpr("a=50"), 1.0F)).addJsonScanAsChild().jsonBatches(rightJsonBatches).columnsToRead("a", "b").buildAddAsInput().buildAddAsInput().build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", BIGINT).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(batch).expectSchema(expectedSchema).baselineValues(5L, 1L).baselineValues(5L, 5L).baselineValues(50L, 100L).go();
    }
}

