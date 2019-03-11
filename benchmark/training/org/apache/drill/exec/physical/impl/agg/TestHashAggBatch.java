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
package org.apache.drill.exec.physical.impl.agg;


import AggPrelBase.OperatorPhase.PHASE_1of1;
import AggPrelBase.OperatorPhase.PHASE_1of2;
import AggPrelBase.OperatorPhase.PHASE_2of2;
import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.VARCHAR;
import java.util.List;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.test.PhysicalOpUnitTestBase;
import org.junit.Test;


public class TestHashAggBatch extends PhysicalOpUnitTestBase {
    public static final String FIRST_NAME_COL = "firstname";

    public static final String LAST_NAME_COL = "lastname";

    public static final String STUFF_COL = "stuff";

    public static final String TOTAL_STUFF_COL = "totalstuff";

    public static final List<String> FIRST_NAMES = ImmutableList.of("Strawberry", "Banana", "Mango", "Grape");

    public static final List<String> LAST_NAMES = ImmutableList.of("Red", "Green", "Blue", "Purple");

    public static final TupleMetadata INT_OUTPUT_SCHEMA = new SchemaBuilder().add(TestHashAggBatch.FIRST_NAME_COL, VARCHAR, REQUIRED).add(TestHashAggBatch.LAST_NAME_COL, VARCHAR, REQUIRED).add(TestHashAggBatch.TOTAL_STUFF_COL, BIGINT, OPTIONAL).buildSchema();

    @Test
    public void simpleSingleBatchSumTestPhase1of2() throws Exception {
        batchSumTest(100, Integer.MAX_VALUE, PHASE_1of2);
    }

    @Test
    public void simpleMultiBatchSumTestPhase1of2() throws Exception {
        batchSumTest(100, 100, PHASE_1of2);
    }

    @Test
    public void simpleSingleBatchSumTestPhase1of1() throws Exception {
        batchSumTest(100, Integer.MAX_VALUE, PHASE_1of1);
    }

    @Test
    public void simpleMultiBatchSumTestPhase1of1() throws Exception {
        batchSumTest(100, 100, PHASE_1of1);
    }

    @Test
    public void simpleSingleBatchSumTestPhase2of2() throws Exception {
        batchSumTest(100, Integer.MAX_VALUE, PHASE_2of2);
    }

    @Test
    public void simpleMultiBatchSumTestPhase2of2() throws Exception {
        batchSumTest(100, 100, PHASE_2of2);
    }
}

