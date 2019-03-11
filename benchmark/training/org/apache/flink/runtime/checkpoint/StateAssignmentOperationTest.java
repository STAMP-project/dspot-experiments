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
package org.apache.flink.runtime.checkpoint;


import OperatorStateHandle.Mode;
import OperatorStateHandle.StateMetaInfo;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.OperatorStateHandle;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests to verify state assignment operation.
 */
public class StateAssignmentOperationTest extends TestLogger {
    @Test
    public void testRepartitionSplitDistributeStates() {
        OperatorID operatorID = new OperatorID();
        OperatorState operatorState = new OperatorState(operatorID, 2, 4);
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap1 = new HashMap<>(1);
        metaInfoMap1.put("t-1", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 10 }, Mode.SPLIT_DISTRIBUTE));
        OperatorStateHandle osh1 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap1, new ByteStreamStateHandle("test1", new byte[30]));
        operatorState.putState(0, new OperatorSubtaskState(osh1, null, null, null));
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap2 = new HashMap<>(1);
        metaInfoMap2.put("t-2", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 15 }, Mode.SPLIT_DISTRIBUTE));
        OperatorStateHandle osh2 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap2, new ByteStreamStateHandle("test2", new byte[40]));
        operatorState.putState(1, new OperatorSubtaskState(osh2, null, null, null));
        verifyOneKindPartitionableStateRescale(operatorState, operatorID);
    }

    @Test
    public void testRepartitionUnionState() {
        OperatorID operatorID = new OperatorID();
        OperatorState operatorState = new OperatorState(operatorID, 2, 4);
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap1 = new HashMap<>(2);
        metaInfoMap1.put("t-3", new OperatorStateHandle.StateMetaInfo(new long[]{ 0 }, Mode.UNION));
        metaInfoMap1.put("t-4", new OperatorStateHandle.StateMetaInfo(new long[]{ 22, 44 }, Mode.UNION));
        OperatorStateHandle osh1 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap1, new ByteStreamStateHandle("test1", new byte[50]));
        operatorState.putState(0, new OperatorSubtaskState(osh1, null, null, null));
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap2 = new HashMap<>(1);
        metaInfoMap2.put("t-3", new OperatorStateHandle.StateMetaInfo(new long[]{ 0 }, Mode.UNION));
        OperatorStateHandle osh2 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap2, new ByteStreamStateHandle("test2", new byte[20]));
        operatorState.putState(1, new OperatorSubtaskState(osh2, null, null, null));
        verifyOneKindPartitionableStateRescale(operatorState, operatorID);
    }

    @Test
    public void testRepartitionBroadcastState() {
        OperatorID operatorID = new OperatorID();
        OperatorState operatorState = new OperatorState(operatorID, 2, 4);
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap1 = new HashMap<>(2);
        metaInfoMap1.put("t-5", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 10, 20 }, Mode.BROADCAST));
        metaInfoMap1.put("t-6", new OperatorStateHandle.StateMetaInfo(new long[]{ 30, 40, 50 }, Mode.BROADCAST));
        OperatorStateHandle osh1 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap1, new ByteStreamStateHandle("test1", new byte[60]));
        operatorState.putState(0, new OperatorSubtaskState(osh1, null, null, null));
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap2 = new HashMap<>(2);
        metaInfoMap2.put("t-5", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 10, 20 }, Mode.BROADCAST));
        metaInfoMap2.put("t-6", new OperatorStateHandle.StateMetaInfo(new long[]{ 30, 40, 50 }, Mode.BROADCAST));
        OperatorStateHandle osh2 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap2, new ByteStreamStateHandle("test2", new byte[60]));
        operatorState.putState(1, new OperatorSubtaskState(osh2, null, null, null));
        verifyOneKindPartitionableStateRescale(operatorState, operatorID);
    }

    /**
     * Verify repartition logic on partitionable states with all modes.
     */
    @Test
    public void testReDistributeCombinedPartitionableStates() {
        OperatorID operatorID = new OperatorID();
        OperatorState operatorState = new OperatorState(operatorID, 2, 4);
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap1 = new HashMap<>(6);
        metaInfoMap1.put("t-1", new OperatorStateHandle.StateMetaInfo(new long[]{ 0 }, Mode.UNION));
        metaInfoMap1.put("t-2", new OperatorStateHandle.StateMetaInfo(new long[]{ 22, 44 }, Mode.UNION));
        metaInfoMap1.put("t-3", new OperatorStateHandle.StateMetaInfo(new long[]{ 52, 63 }, Mode.SPLIT_DISTRIBUTE));
        metaInfoMap1.put("t-4", new OperatorStateHandle.StateMetaInfo(new long[]{ 67, 74, 75 }, Mode.BROADCAST));
        metaInfoMap1.put("t-5", new OperatorStateHandle.StateMetaInfo(new long[]{ 77, 88, 92 }, Mode.BROADCAST));
        metaInfoMap1.put("t-6", new OperatorStateHandle.StateMetaInfo(new long[]{ 101, 123, 127 }, Mode.BROADCAST));
        OperatorStateHandle osh1 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap1, new ByteStreamStateHandle("test1", new byte[130]));
        operatorState.putState(0, new OperatorSubtaskState(osh1, null, null, null));
        Map<String, OperatorStateHandle.StateMetaInfo> metaInfoMap2 = new HashMap<>(3);
        metaInfoMap2.put("t-1", new OperatorStateHandle.StateMetaInfo(new long[]{ 0 }, Mode.UNION));
        metaInfoMap2.put("t-4", new OperatorStateHandle.StateMetaInfo(new long[]{ 20, 27, 28 }, Mode.BROADCAST));
        metaInfoMap2.put("t-5", new OperatorStateHandle.StateMetaInfo(new long[]{ 30, 44, 48 }, Mode.BROADCAST));
        metaInfoMap2.put("t-6", new OperatorStateHandle.StateMetaInfo(new long[]{ 57, 79, 83 }, Mode.BROADCAST));
        OperatorStateHandle osh2 = new org.apache.flink.runtime.state.OperatorStreamStateHandle(metaInfoMap2, new ByteStreamStateHandle("test2", new byte[86]));
        operatorState.putState(1, new OperatorSubtaskState(osh2, null, null, null));
        // rescale up case, parallelism 2 --> 3
        verifyCombinedPartitionableStateRescale(operatorState, operatorID, 2, 3);
        // rescale down case, parallelism 2 --> 1
        verifyCombinedPartitionableStateRescale(operatorState, operatorID, 2, 1);
        // not rescale
        verifyCombinedPartitionableStateRescale(operatorState, operatorID, 2, 2);
    }
}

