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
package org.apache.flink.runtime.checkpoint.savepoint;


import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import org.apache.flink.runtime.checkpoint.MasterState;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.junit.Test;


/**
 * Various tests for the version 2 format serializer of a checkpoint.
 */
public class SavepointV2SerializerTest {
    @Test
    public void testCheckpointWithNoState() throws Exception {
        final Random rnd = new Random();
        for (int i = 0; i < 100; ++i) {
            final long checkpointId = (rnd.nextLong()) & 9223372036854775807L;
            final Collection<OperatorState> taskStates = Collections.emptyList();
            final Collection<MasterState> masterStates = Collections.emptyList();
            testCheckpointSerialization(checkpointId, taskStates, masterStates);
        }
    }

    @Test
    public void testCheckpointWithOnlyMasterState() throws Exception {
        final Random rnd = new Random();
        final int maxNumMasterStates = 5;
        for (int i = 0; i < 100; ++i) {
            final long checkpointId = (rnd.nextLong()) & 9223372036854775807L;
            final Collection<OperatorState> operatorStates = Collections.emptyList();
            final int numMasterStates = (rnd.nextInt(maxNumMasterStates)) + 1;
            final Collection<MasterState> masterStates = CheckpointTestUtils.createRandomMasterStates(rnd, numMasterStates);
            testCheckpointSerialization(checkpointId, operatorStates, masterStates);
        }
    }

    @Test
    public void testCheckpointWithOnlyTaskState() throws Exception {
        final Random rnd = new Random();
        final int maxTaskStates = 20;
        final int maxNumSubtasks = 20;
        for (int i = 0; i < 100; ++i) {
            final long checkpointId = (rnd.nextLong()) & 9223372036854775807L;
            final int numTasks = (rnd.nextInt(maxTaskStates)) + 1;
            final int numSubtasks = (rnd.nextInt(maxNumSubtasks)) + 1;
            final Collection<OperatorState> taskStates = CheckpointTestUtils.createOperatorStates(rnd, numTasks, numSubtasks);
            final Collection<MasterState> masterStates = Collections.emptyList();
            testCheckpointSerialization(checkpointId, taskStates, masterStates);
        }
    }

    @Test
    public void testCheckpointWithMasterAndTaskState() throws Exception {
        final Random rnd = new Random();
        final int maxNumMasterStates = 5;
        final int maxTaskStates = 20;
        final int maxNumSubtasks = 20;
        for (int i = 0; i < 100; ++i) {
            final long checkpointId = (rnd.nextLong()) & 9223372036854775807L;
            final int numTasks = (rnd.nextInt(maxTaskStates)) + 1;
            final int numSubtasks = (rnd.nextInt(maxNumSubtasks)) + 1;
            final Collection<OperatorState> taskStates = CheckpointTestUtils.createOperatorStates(rnd, numTasks, numSubtasks);
            final int numMasterStates = (rnd.nextInt(maxNumMasterStates)) + 1;
            final Collection<MasterState> masterStates = CheckpointTestUtils.createRandomMasterStates(rnd, numMasterStates);
            testCheckpointSerialization(checkpointId, taskStates, masterStates);
        }
    }
}

