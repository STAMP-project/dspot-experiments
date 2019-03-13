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
package org.apache.flink.optimizer.dataexchange;


import DataExchangeMode.BATCH;
import DataExchangeMode.PIPELINED;
import ExecutionMode.BATCH_FORCED;
import ExecutionMode.PIPELINED_FORCED;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Test;


/**
 * This test checks the correct assignment of the DataExchangeMode to
 * connections for programs that branch, and re-join those branches.
 *
 * <pre>
 *                                         /-> (sink)
 *                                        /
 *                         /-> (reduce) -+          /-> (flatmap) -> (sink)
 *                        /               \        /
 *     (source) -> (map) -                (join) -+-----\
 *                        \               /              \
 *                         \-> (filter) -+                \
 *                                       \                (co group) -> (sink)
 *                                        \                /
 *                                         \-> (reduce) - /
 * </pre>
 */
@SuppressWarnings("serial")
public class DataExchangeModeClosedBranchingTest extends CompilerTestBase {
    @Test
    public void testPipelinedForced() {
        // PIPELINED_FORCED should result in pipelining all the way
        verifyBranchingJoiningPlan(PIPELINED_FORCED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED);
    }

    @Test
    public void testPipelined() {
        // PIPELINED should result in pipelining all the way
        // to map
        // to combiner connections are pipelined
        // to reduce
        // to filter
        // to sink after reduce
        // to join (first input)
        // to join (second input)
        // combiner connections are pipelined
        // to other reducer
        // to flatMap
        // to sink after flatMap
        // to coGroup (first input)
        // to coGroup (second input)
        // to sink after coGroup
        verifyBranchingJoiningPlan(ExecutionMode.PIPELINED, PIPELINED, PIPELINED, BATCH, BATCH, PIPELINED, PIPELINED, BATCH, PIPELINED, BATCH, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED);
    }

    @Test
    public void testBatch() {
        // BATCH should result in batching the shuffle all the way
        // to map
        // to combiner connections are pipelined
        // to reduce
        // to filter
        // to sink after reduce
        // to join (first input)
        // to join (second input)
        // combiner connections are pipelined
        // to other reducer
        // to flatMap
        // to sink after flatMap
        // to coGroup (first input)
        // to coGroup (second input)
        // to sink after coGroup
        verifyBranchingJoiningPlan(ExecutionMode.BATCH, PIPELINED, PIPELINED, BATCH, BATCH, PIPELINED, BATCH, BATCH, PIPELINED, BATCH, PIPELINED, PIPELINED, BATCH, BATCH, PIPELINED);
    }

    @Test
    public void testBatchForced() {
        // BATCH_FORCED should result in batching all the way
        // to map
        // to combiner connections are pipelined
        // to reduce
        // to filter
        // to sink after reduce
        // to join (first input)
        // to join (second input)
        // combiner connections are pipelined
        // to other reducer
        // to flatMap
        // to sink after flatMap
        // to coGroup (first input)
        // to coGroup (second input)
        // to sink after coGroup
        verifyBranchingJoiningPlan(BATCH_FORCED, BATCH, PIPELINED, BATCH, BATCH, BATCH, BATCH, BATCH, PIPELINED, BATCH, BATCH, BATCH, BATCH, BATCH, BATCH);
    }
}

