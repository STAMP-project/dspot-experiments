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
package org.apache.flink.runtime.jobmaster;


import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.testutils.MiniClusterResource;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.util.FlinkRuntimeException;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests for the recovery of task failures.
 */
public class JobRecoveryITCase extends TestLogger {
    private static final int NUM_TMS = 1;

    private static final int SLOTS_PER_TM = 11;

    private static final int PARALLELISM = (JobRecoveryITCase.NUM_TMS) * (JobRecoveryITCase.SLOTS_PER_TM);

    @ClassRule
    public static final MiniClusterResource MINI_CLUSTER_RESOURCE = new MiniClusterResource(new MiniClusterResourceConfiguration.Builder().setNumberTaskManagers(JobRecoveryITCase.NUM_TMS).setNumberSlotsPerTaskManager(JobRecoveryITCase.SLOTS_PER_TM).build());

    @Test
    public void testTaskFailureRecovery() throws Exception {
        runTaskFailureRecoveryTest(createjobGraph(false));
    }

    @Test
    public void testTaskFailureWithSlotSharingRecovery() throws Exception {
        runTaskFailureRecoveryTest(createjobGraph(true));
    }

    /**
     * Receiver which fails once before successfully completing.
     */
    public static final class FailingOnceReceiver extends TestingAbstractInvokables.Receiver {
        private static volatile boolean failed = false;

        public FailingOnceReceiver(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            if ((!(JobRecoveryITCase.FailingOnceReceiver.failed)) && ((getEnvironment().getTaskInfo().getIndexOfThisSubtask()) == 0)) {
                JobRecoveryITCase.FailingOnceReceiver.failed = true;
                throw new FlinkRuntimeException(getClass().getSimpleName());
            } else {
                super.invoke();
            }
        }

        private static void reset() {
            JobRecoveryITCase.FailingOnceReceiver.failed = false;
        }
    }
}

