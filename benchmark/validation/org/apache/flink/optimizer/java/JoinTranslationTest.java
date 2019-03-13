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
package org.apache.flink.optimizer.java;


import DriverStrategy.HYBRIDHASH_BUILD_FIRST;
import DriverStrategy.HYBRIDHASH_BUILD_SECOND;
import DriverStrategy.INNER_MERGE;
import JoinHint.BROADCAST_HASH_FIRST;
import JoinHint.BROADCAST_HASH_SECOND;
import JoinHint.OPTIMIZER_CHOOSES;
import JoinHint.REPARTITION_HASH_FIRST;
import JoinHint.REPARTITION_HASH_SECOND;
import JoinHint.REPARTITION_SORT_MERGE;
import ShipStrategyType.BROADCAST;
import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.util.CompilerTestBase;
import org.apache.flink.runtime.operators.DriverStrategy;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class JoinTranslationTest extends CompilerTestBase {
    @Test
    public void testBroadcastHashFirstTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(BROADCAST_HASH_FIRST);
            Assert.assertEquals(BROADCAST, node.getInput1().getShipStrategy());
            Assert.assertEquals(FORWARD, node.getInput2().getShipStrategy());
            Assert.assertEquals(HYBRIDHASH_BUILD_FIRST, node.getDriverStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void testBroadcastHashSecondTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(BROADCAST_HASH_SECOND);
            Assert.assertEquals(FORWARD, node.getInput1().getShipStrategy());
            Assert.assertEquals(BROADCAST, node.getInput2().getShipStrategy());
            Assert.assertEquals(HYBRIDHASH_BUILD_SECOND, node.getDriverStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void testPartitionHashFirstTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(REPARTITION_HASH_FIRST);
            Assert.assertEquals(PARTITION_HASH, node.getInput1().getShipStrategy());
            Assert.assertEquals(PARTITION_HASH, node.getInput2().getShipStrategy());
            Assert.assertEquals(HYBRIDHASH_BUILD_FIRST, node.getDriverStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void testPartitionHashSecondTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(REPARTITION_HASH_SECOND);
            Assert.assertEquals(PARTITION_HASH, node.getInput1().getShipStrategy());
            Assert.assertEquals(PARTITION_HASH, node.getInput2().getShipStrategy());
            Assert.assertEquals(HYBRIDHASH_BUILD_SECOND, node.getDriverStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void testPartitionSortMergeTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(REPARTITION_SORT_MERGE);
            Assert.assertEquals(PARTITION_HASH, node.getInput1().getShipStrategy());
            Assert.assertEquals(PARTITION_HASH, node.getInput2().getShipStrategy());
            Assert.assertEquals(INNER_MERGE, node.getDriverStrategy());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void testOptimizerChoosesTest() {
        try {
            DualInputPlanNode node = createPlanAndGetJoinNode(OPTIMIZER_CHOOSES);
            Assert.assertEquals(PARTITION_HASH, node.getInput1().getShipStrategy());
            Assert.assertEquals(PARTITION_HASH, node.getInput2().getShipStrategy());
            Assert.assertTrue((((DriverStrategy.HYBRIDHASH_BUILD_FIRST) == (node.getDriverStrategy())) || ((DriverStrategy.HYBRIDHASH_BUILD_SECOND) == (node.getDriverStrategy()))));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail((((e.getClass().getSimpleName()) + ": ") + (e.getMessage())));
        }
    }

    private static final class IdentityKeySelector<T> implements KeySelector<T, T> {
        @Override
        public T getKey(T value) {
            return value;
        }
    }
}

