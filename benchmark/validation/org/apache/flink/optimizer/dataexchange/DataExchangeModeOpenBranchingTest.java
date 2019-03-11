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


import DataExchangeMode.PIPELINED;
import ExecutionMode.BATCH;
import ExecutionMode.BATCH_FORCED;
import ExecutionMode.PIPELINED_FORCED;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.junit.Test;


/**
 * This test checks the correct assignment of the DataExchangeMode to
 * connections for programs that branch, but do not re-join the branches.
 *
 * <pre>
 *                      /---> (filter) -> (sink)
 *                     /
 *                    /
 * (source) -> (map) -----------------\
 *                    \               (join) -> (sink)
 *                     \   (source) --/
 *                      \
 *                       \
 *                        \-> (sink)
 * </pre>
 */
@SuppressWarnings({ "serial", "unchecked" })
public class DataExchangeModeOpenBranchingTest extends CompilerTestBase {
    @Test
    public void testPipelinedForced() {
        // PIPELINED_FORCED should result in pipelining all the way
        verifyBranchigPlan(PIPELINED_FORCED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED);
    }

    @Test
    public void testPipelined() {
        // PIPELINED should result in pipelining all the way
        verifyBranchigPlan(ExecutionMode.PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED, PIPELINED);
    }

    @Test
    public void testBatch() {
        // BATCH should result in batching the shuffle all the way
        verifyBranchigPlan(BATCH, PIPELINED, PIPELINED, PIPELINED, DataExchangeMode.BATCH, DataExchangeMode.BATCH, PIPELINED, PIPELINED);
    }

    @Test
    public void testBatchForced() {
        // BATCH_FORCED should result in batching all the way
        verifyBranchigPlan(BATCH_FORCED, DataExchangeMode.BATCH, DataExchangeMode.BATCH, DataExchangeMode.BATCH, DataExchangeMode.BATCH, DataExchangeMode.BATCH, DataExchangeMode.BATCH, DataExchangeMode.BATCH);
    }
}

