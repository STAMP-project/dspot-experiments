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


import AggPrelBase.OperatorPhase;
import BatchSchema.SelectionVectorMode.NONE;
import RelFieldCollation.Direction.ASCENDING;
import RelFieldCollation.NullDirection.FIRST;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.util.Collections;
import java.util.List;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.config.Limit;
import org.apache.drill.exec.physical.config.UnionAll;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.store.dfs.DrillFileSystem;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


public class TestNullInputMiniPlan extends MiniPlanUnitTestBase {
    protected static DrillFileSystem fs;

    public final String SINGLE_EMPTY_JSON = "/scan/emptyInput/emptyJson/empty.json";

    public final String SINGLE_EMPTY_JSON2 = "/scan/emptyInput/emptyJson/empty2.json";

    public final String SINGLE_JSON = "/scan/jsonTbl/1990/1.json";// {id: 100, name : "John"}


    public final String SINGLE_JSON2 = "/scan/jsonTbl/1991/2.json";// {id: 1000, name : "Joe"}


    /**
     * Test ScanBatch with a single empty json file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyJsonInput() throws Exception {
        RecordBatch scanBatch = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectNullBatch(true).go();
    }

    /**
     * Test ScanBatch with mixed json files.
     * input is empty, data_file, empty, data_file
     */
    @Test
    public void testJsonInputMixedWithEmptyFiles1() throws Exception {
        RecordBatch scanBatch = createScanBatchFromJson(SINGLE_EMPTY_JSON, SINGLE_JSON, SINGLE_EMPTY_JSON2, SINGLE_JSON2);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("id", BIGINT).addNullable("name", VARCHAR).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(100L, "John").baselineValues(1000L, "Joe").expectBatchNum(2).go();
    }

    /**
     * Test ScanBatch with mixed json files.
     * input is empty, empty, data_file, data_file
     */
    @Test
    public void testJsonInputMixedWithEmptyFiles2() throws Exception {
        RecordBatch scanBatch = createScanBatchFromJson(SINGLE_EMPTY_JSON, SINGLE_EMPTY_JSON2, SINGLE_JSON, SINGLE_JSON2);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("id", BIGINT).addNullable("name", VARCHAR).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(100L, "John").baselineValues(1000L, "Joe").expectBatchNum(2).go();
    }

    /**
     * Test ScanBatch with mixed json files.
     * input is empty, data_file, data_file, empty
     */
    @Test
    public void testJsonInputMixedWithEmptyFiles3() throws Exception {
        RecordBatch scanBatch = createScanBatchFromJson(SINGLE_EMPTY_JSON, SINGLE_JSON, SINGLE_JSON2, SINGLE_EMPTY_JSON2);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("id", BIGINT).addNullable("name", VARCHAR).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(100L, "John").baselineValues(1000L, "Joe").expectBatchNum(2).go();
    }

    /**
     * Test ScanBatch with mixed json files.
     * input is data_file, data_file, empty, empty
     */
    @Test
    public void testJsonInputMixedWithEmptyFiles4() throws Exception {
        RecordBatch scanBatch = createScanBatchFromJson(SINGLE_JSON, SINGLE_JSON2, SINGLE_EMPTY_JSON2, SINGLE_EMPTY_JSON2);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("id", BIGINT).addNullable("name", VARCHAR).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(scanBatch).expectSchema(expectedSchema).baselineValues(100L, "John").baselineValues(1000L, "Joe").expectBatchNum(2).go();
    }

    @Test
    public void testProjectEmpty() throws Exception {
        final PhysicalOperator project = new org.apache.drill.exec.physical.config.Project(parseExprs("x+5", "x"), null);
        testSingleInputNullBatchHandling(project);
    }

    @Test
    public void testFilterEmpty() throws Exception {
        final PhysicalOperator filter = new org.apache.drill.exec.physical.config.Filter(null, parseExpr("a=5"), 1.0F);
        testSingleInputNullBatchHandling(filter);
    }

    @Test
    public void testHashAggEmpty() throws Exception {
        final PhysicalOperator hashAgg = new org.apache.drill.exec.physical.config.HashAggregate(null, OperatorPhase.PHASE_1of1, parseExprs("a", "a"), parseExprs("sum(b)", "b_sum"), 1.0F);
        testSingleInputNullBatchHandling(hashAgg);
    }

    @Test
    public void testStreamingAggEmpty() throws Exception {
        final PhysicalOperator hashAgg = new org.apache.drill.exec.physical.config.StreamingAggregate(null, parseExprs("a", "a"), parseExprs("sum(b)", "b_sum"), 1.0F);
        testSingleInputNullBatchHandling(hashAgg);
    }

    @Test
    public void testSortEmpty() throws Exception {
        final PhysicalOperator sort = new org.apache.drill.exec.physical.config.ExternalSort(null, Lists.newArrayList(ordering("b", ASCENDING, FIRST)), false);
        testSingleInputNullBatchHandling(sort);
    }

    @Test
    public void testLimitEmpty() throws Exception {
        final PhysicalOperator limit = new Limit(null, 10, 5);
        testSingleInputNullBatchHandling(limit);
    }

    @Test
    public void testFlattenEmpty() throws Exception {
        final PhysicalOperator flatten = new org.apache.drill.exec.physical.config.FlattenPOP(null, SchemaPath.getSimplePath("col1"));
        testSingleInputNullBatchHandling(flatten);
    }

    @Test
    public void testUnionEmptyBoth() throws Exception {
        final PhysicalOperator unionAll = new UnionAll(Collections.EMPTY_LIST);// Children list is provided through RecordBatch

        testTwoInputNullBatchHandling(unionAll);
    }

    @Test
    public void testHashJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.INNER, null);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testLeftHashJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.LEFT, null);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testRightHashJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.RIGHT, null);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testFullHashJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.FULL, null);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testMergeJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.MergeJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.INNER);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testLeftMergeJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.MergeJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.LEFT);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testRightMergeJoinEmptyBoth() throws Exception {
        final PhysicalOperator join = new org.apache.drill.exec.physical.config.MergeJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "b")), JoinRelType.RIGHT);
        testTwoInputNullBatchHandling(join);
    }

    @Test
    public void testUnionLeftEmtpy() throws Exception {
        final PhysicalOperator unionAll = new UnionAll(Collections.EMPTY_LIST);// Children list is provided through RecordBatch

        RecordBatch left = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        String file = DrillFileUtils.getResourceAsFile("/tpchmulti/region/01.parquet").toURI().toString();
        List<Path> filePath = Collections.singletonList(new Path(file));
        RecordBatch scanBatch = new MiniPlanUnitTestBase.ParquetScanBuilder().fileSystem(TestNullInputMiniPlan.fs).columnsToRead("R_REGIONKEY").inputPaths(filePath).build();
        RecordBatch projectBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.Project(parseExprs("R_REGIONKEY+10", "regionkey"), null)).addInput(scanBatch).build();
        RecordBatch unionBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(unionAll).addInput(left).addInput(projectBatch).build();
        BatchSchema expectedSchema = new SchemaBuilder().add("regionkey", BIGINT).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(unionBatch).expectSchema(expectedSchema).baselineValues(10L).baselineValues(11L).go();
    }

    @Test
    public void testHashJoinLeftEmpty() throws Exception {
        RecordBatch left = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        List<String> rightJsonBatches = Lists.newArrayList("[{\"a\": 50, \"b\" : 10 }]");
        RecordBatch rightScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(rightJsonBatches).columnsToRead("a", "b").build();
        RecordBatch joinBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a2", "EQUALS", "a")), JoinRelType.INNER, null)).addInput(left).addInput(rightScan).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", BIGINT).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(joinBatch).expectSchema(expectedSchema).expectZeroRow(true).go();
    }

    @Test
    public void testHashJoinRightEmpty() throws Exception {
        List<String> leftJsonBatches = Lists.newArrayList("[{\"a\": 50, \"b\" : 10 }]");
        RecordBatch leftScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(leftJsonBatches).columnsToRead("a", "b").build();
        RecordBatch right = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        RecordBatch joinBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "a2")), JoinRelType.INNER, null)).addInput(leftScan).addInput(right).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", BIGINT).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(joinBatch).expectSchema(expectedSchema).expectZeroRow(true).go();
    }

    @Test
    public void testLeftHashJoinLeftEmpty() throws Exception {
        RecordBatch left = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        List<String> rightJsonBatches = Lists.newArrayList("[{\"a\": 50, \"b\" : 10 }]");
        RecordBatch rightScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(rightJsonBatches).columnsToRead("a", "b").build();
        RecordBatch joinBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a2", "EQUALS", "a")), JoinRelType.LEFT, null)).addInput(left).addInput(rightScan).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", BIGINT).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(joinBatch).expectSchema(expectedSchema).expectZeroRow(true).go();
    }

    @Test
    public void testLeftHashJoinRightEmpty() throws Exception {
        List<String> leftJsonBatches = Lists.newArrayList("[{\"a\": 50, \"b\" : 10 }]");
        RecordBatch leftScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(leftJsonBatches).columnsToRead("a", "b").build();
        RecordBatch right = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        RecordBatch joinBatch = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.HashJoinPOP(null, null, Lists.newArrayList(joinCond("a", "EQUALS", "a2")), JoinRelType.LEFT, null)).addInput(leftScan).addInput(right).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", BIGINT).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(joinBatch).expectSchema(expectedSchema).baselineValues(50L, 10L).go();
    }

    @Test
    public void testUnionFilterAll() throws Exception {
        List<String> leftJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : \"name1\" }]");
        List<String> rightJsonBatches = Lists.newArrayList("[{\"a\": 50, \"b\" : \"name2\" }]");
        RecordBatch leftScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(leftJsonBatches).columnsToRead("a", "b").build();
        RecordBatch leftFilter = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.Filter(null, parseExpr("a < 0"), 1.0F)).addInput(leftScan).build();
        RecordBatch rightScan = new MiniPlanUnitTestBase.JsonScanBuilder().jsonBatches(rightJsonBatches).columnsToRead("a", "b").build();
        RecordBatch rightFilter = new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new org.apache.drill.exec.physical.config.Filter(null, parseExpr("a < 0"), 1.0F)).addInput(rightScan).build();
        RecordBatch batch = // Children list is provided through RecordBatch
        new MiniPlanUnitTestBase.PopBuilder().physicalOperator(new UnionAll(Collections.EMPTY_LIST)).addInput(leftFilter).addInput(rightFilter).build();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).withSVMode(NONE).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(batch).expectSchema(expectedSchema).expectZeroRow(true).go();
    }

    @Test
    public void testOutputProjectEmpty() throws Exception {
        final PhysicalOperator project = new org.apache.drill.exec.physical.config.Project(parseExprs("x", "col1", "x + 100", "col2", "100.0", "col3", "cast(nonExist as varchar(100))", "col4"), null, true);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("col1", INT).addNullable("col2", INT).add("col3", FLOAT8).addNullable("col4", VARCHAR, 100).withSVMode(NONE).build();
        final RecordBatch input = createScanBatchFromJson(SINGLE_EMPTY_JSON);
        RecordBatch batch = // Children list is provided through RecordBatch
        new MiniPlanUnitTestBase.PopBuilder().physicalOperator(project).addInput(input).build();
        new MiniPlanUnitTestBase.MiniPlanTestBuilder().root(batch).expectSchema(expectedSchema).expectZeroRow(true).go();
    }
}

