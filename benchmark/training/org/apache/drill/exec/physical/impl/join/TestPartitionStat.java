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
package org.apache.drill.exec.physical.impl.join;


import org.junit.Assert;
import org.junit.Test;


public class TestPartitionStat {
    @Test
    public void simpleAddBatchTest() {
        final PartitionStatImpl partitionStat = new PartitionStatImpl();
        TestPartitionStat.comparePartitionStat(partitionStat, true, 0L, 0, 0L);
        partitionStat.add(new HashJoinMemoryCalculator.BatchStat(1, 2));
        TestPartitionStat.comparePartitionStat(partitionStat, false, 2, 1, 1);
        partitionStat.add(new HashJoinMemoryCalculator.BatchStat(2, 3));
        TestPartitionStat.comparePartitionStat(partitionStat, false, 5, 2, 3);
    }

    @Test
    public void simpleSpillTest() {
        final PartitionStatImpl partitionStat = new PartitionStatImpl();
        Assert.assertFalse(partitionStat.isSpilled());
        partitionStat.add(new HashJoinMemoryCalculator.BatchStat(1, 2));
        Assert.assertFalse(partitionStat.isSpilled());
        partitionStat.spill();
        TestPartitionStat.comparePartitionStat(partitionStat, true, 0, 0, 0);
    }
}

