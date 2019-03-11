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
import RelFieldCollation.Direction.ASCENDING;
import RelFieldCollation.NullDirection.FIRST;
import java.util.List;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.config.ComplexToJson;
import org.apache.drill.exec.physical.config.ExternalSort;
import org.apache.drill.exec.physical.config.Filter;
import org.apache.drill.exec.physical.config.HashAggregate;
import org.apache.drill.exec.physical.config.HashJoinPOP;
import org.apache.drill.exec.physical.config.MergeJoinPOP;
import org.apache.drill.exec.physical.config.Project;
import org.apache.drill.exec.physical.config.StreamingAggregate;
import org.apache.drill.exec.physical.config.TopN;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.LegacyOperatorTestBuilder;
import org.apache.drill.test.PhysicalOpUnitTestBase;
import org.apache.drill.test.TestBuilder;
import org.junit.Test;


public class BasicPhysicalOpUnitTest extends PhysicalOpUnitTestBase {
    @Test
    public void testSimpleProject() {
        Project projectConf = new Project(parseExprs("x+5", "x"), null);
        List<String> jsonBatches = Lists.newArrayList("[{\"x\": 5 },{\"x\": 10 }]", "[{\"x\": 20 },{\"x\": 30 },{\"x\": 40 }]");
        legacyOpTestBuilder().physicalOperator(projectConf).inputDataStreamJson(jsonBatches).baselineColumns("x").baselineValues(10L).baselineValues(15L).baselineValues(25L).baselineValues(35L).baselineValues(45L).go();
    }

    @Test
    public void testProjectComplexOutput() {
        Project projectConf = new Project(parseExprs("convert_from(json_col, 'JSON')", "complex_col"), null);
        List<String> jsonBatches = Lists.newArrayList("[{\"json_col\": \"{ \\\"a\\\" : 1 }\"}]", "[{\"json_col\": \"{ \\\"a\\\" : 5 }\"}]");
        legacyOpTestBuilder().physicalOperator(projectConf).inputDataStreamJson(jsonBatches).baselineColumns("complex_col").baselineValues(TestBuilder.mapOf("a", 1L)).baselineValues(TestBuilder.mapOf("a", 5L)).go();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleHashJoin() {
        HashJoinPOP joinConf = new HashJoinPOP(null, null, Lists.newArrayList(joinCond("x", "EQUALS", "x1")), JoinRelType.LEFT, null);
        // TODO - figure out where to add validation, column names must be unique, even between the two batches,
        // for all columns, not just the one in the join condition
        // TODO - if any are common between the two, it is failing in the generated setup method in HashJoinProbeGen
        List<String> leftJsonBatches = Lists.newArrayList("[{\"x\": 5, \"a\" : \"a string\"}]", "[{\"x\": 5, \"a\" : \"a different string\"},{\"x\": 5, \"a\" : \"meh\"}]");
        List<String> rightJsonBatches = Lists.newArrayList("[{\"x1\": 5, \"a2\" : \"asdf\"}]", "[{\"x1\": 6, \"a2\" : \"qwerty\"},{\"x1\": 5, \"a2\" : \"12345\"}]");
        legacyOpTestBuilder().physicalOperator(joinConf).inputDataStreamsJson(Lists.newArrayList(leftJsonBatches, rightJsonBatches)).baselineColumns("x", "a", "a2", "x1").baselineValues(5L, "a string", "asdf", 5L).baselineValues(5L, "a string", "12345", 5L).baselineValues(5L, "a different string", "asdf", 5L).baselineValues(5L, "a different string", "12345", 5L).baselineValues(5L, "meh", "asdf", 5L).baselineValues(5L, "meh", "12345", 5L).go();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleMergeJoin() {
        MergeJoinPOP joinConf = new MergeJoinPOP(null, null, Lists.newArrayList(joinCond("x", "EQUALS", "x1")), JoinRelType.LEFT);
        // TODO - figure out where to add validation, column names must be unique, even between the two batches,
        // for all columns, not just the one in the join condition
        List<String> leftJsonBatches = Lists.newArrayList("[{\"x\": 5, \"a\" : \"a string\"}]", "[{\"x\": 5, \"a\" : \"a different string\"},{\"x\": 5, \"a\" : \"meh\"}]");
        List<String> rightJsonBatches = Lists.newArrayList("[{\"x1\": 5, \"a2\" : \"asdf\"}]", "[{\"x1\": 5, \"a2\" : \"12345\"}, {\"x1\": 6, \"a2\" : \"qwerty\"}]");
        legacyOpTestBuilder().physicalOperator(joinConf).inputDataStreamsJson(Lists.newArrayList(leftJsonBatches, rightJsonBatches)).baselineColumns("x", "a", "a2", "x1").baselineValues(5L, "a string", "asdf", 5L).baselineValues(5L, "a string", "12345", 5L).baselineValues(5L, "a different string", "asdf", 5L).baselineValues(5L, "a different string", "12345", 5L).baselineValues(5L, "meh", "asdf", 5L).baselineValues(5L, "meh", "12345", 5L).go();
    }

    @Test
    public void testSimpleHashAgg() {
        HashAggregate aggConf = new HashAggregate(null, OperatorPhase.PHASE_1of1, parseExprs("a", "a"), parseExprs("sum(b)", "b_sum"), 1.0F);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]");
        legacyOpTestBuilder().physicalOperator(aggConf).inputDataStreamJson(inputJsonBatches).baselineColumns("b_sum", "a").baselineValues(6L, 5L).baselineValues(8L, 3L).go();
    }

    @Test
    public void testSimpleStreamAgg() {
        StreamingAggregate aggConf = new StreamingAggregate(null, parseExprs("a", "a"), parseExprs("sum(b)", "b_sum"), 1.0F);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]");
        legacyOpTestBuilder().physicalOperator(aggConf).inputDataStreamJson(inputJsonBatches).baselineColumns("b_sum", "a").baselineValues(6L, 5L).baselineValues(8L, 3L).go();
    }

    @Test
    public void testComplexToJson() {
        ComplexToJson complexToJson = new ComplexToJson(null);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": {\"b\" : 1 }}]", "[{\"a\": {\"b\" : 5}},{\"a\": {\"b\" : 8}}]");
        legacyOpTestBuilder().physicalOperator(complexToJson).inputDataStreamJson(inputJsonBatches).baselineColumns("a").baselineValues("{\n  \"b\" : 1\n}").baselineValues("{\n  \"b\" : 5\n}").baselineValues("{\n  \"b\" : 8\n}").go();
    }

    @Test
    public void testFilter() {
        Filter filterConf = new Filter(null, parseExpr("a=5"), 1.0F);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]", "[{\"a\": 40, \"b\" : 3},{\"a\": 13, \"b\" : 100}]");
        legacyOpTestBuilder().physicalOperator(filterConf).inputDataStreamJson(inputJsonBatches).baselineColumns("a", "b").baselineValues(5L, 1L).baselineValues(5L, 5L).go();
    }

    @Test
    public void testFlatten() {
        final PhysicalOperator flatten = new org.apache.drill.exec.physical.config.FlattenPOP(null, SchemaPath.getSimplePath("b"));
        List<String> inputJsonBatches = Lists.newArrayList();
        StringBuilder batchString = new StringBuilder();
        for (int j = 0; j < 1; j++) {
            batchString.append("[");
            for (int i = 0; i < 1; i++) {
                batchString.append("{\"a\": 5, \"b\" : [5, 6, 7]}");
            }
            batchString.append("]");
            inputJsonBatches.add(batchString.toString());
        }
        LegacyOperatorTestBuilder opTestBuilder = legacyOpTestBuilder().physicalOperator(flatten).inputDataStreamJson(inputJsonBatches).baselineColumns("a", "b").baselineValues(5L, 5L).baselineValues(5L, 6L).baselineValues(5L, 7L);
        opTestBuilder.go();
    }

    @Test
    public void testExternalSort() {
        ExternalSort sortConf = new ExternalSort(null, Lists.newArrayList(ordering("b", ASCENDING, FIRST)), false);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]", "[{\"a\": 40, \"b\" : 3},{\"a\": 13, \"b\" : 100}]");
        legacyOpTestBuilder().physicalOperator(sortConf).maxAllocation(15000000L).inputDataStreamJson(inputJsonBatches).baselineColumns("a", "b").baselineValues(5L, 1L).baselineValues(40L, 3L).baselineValues(5L, 5L).baselineValues(3L, 8L).baselineValues(13L, 100L).go();
    }

    @Test
    public void testTopN() {
        TopN sortConf = new TopN(null, Lists.newArrayList(ordering("b", ASCENDING, FIRST)), false, 3);
        List<String> inputJsonBatches = Lists.newArrayList("[{\"a\": 5, \"b\" : 1 }]", "[{\"a\": 5, \"b\" : 5},{\"a\": 3, \"b\" : 8}]", "[{\"a\": 40, \"b\" : 3},{\"a\": 13, \"b\" : 100}]");
        legacyOpTestBuilder().physicalOperator(sortConf).inputDataStreamJson(inputJsonBatches).baselineColumns("a", "b").baselineValues(5L, 1L).baselineValues(40L, 3L).baselineValues(5L, 5L).go();
    }
}

