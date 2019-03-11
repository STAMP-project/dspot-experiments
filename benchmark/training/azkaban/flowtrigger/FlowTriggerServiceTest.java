/**
 * Copyright 2018 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.flowtrigger;


import CancellationCause.CASCADING;
import CancellationCause.FAILURE;
import CancellationCause.MANUAL;
import Status.CANCELLED;
import azkaban.executor.ExecutorManager;
import azkaban.flowtrigger.database.FlowTriggerInstanceLoader;
import azkaban.flowtrigger.testplugin.TestDependencyCheck;
import azkaban.flowtrigger.util.TestUtil;
import azkaban.project.FlowTrigger;
import azkaban.project.FlowTriggerDependency;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;


public class FlowTriggerServiceTest {
    private static final FlowTriggerInstanceLoader flowTriggerInstanceLoader = new MockFlowTriggerInstanceLoader();

    private static TestDependencyCheck testDepCheck;

    private static FlowTriggerService flowTriggerService;

    private static ExecutorManager executorManager;

    @Test
    public void testStartTriggerCancelledManually() throws InterruptedException {
        final List<FlowTriggerDependency> deps = new ArrayList<>();
        deps.add(TestUtil.createTestDependency("2secs", 2, false));
        deps.add(TestUtil.createTestDependency("8secs", 8, false));
        deps.add(TestUtil.createTestDependency("9secs", 9, false));
        final FlowTrigger flowTrigger = TestUtil.createTestFlowTrigger(deps, Duration.ofSeconds(5));
        for (int i = 0; i < 30; i++) {
            FlowTriggerServiceTest.flowTriggerService.startTrigger(flowTrigger, "testflow", 1, "test", createProject());
        }
        Thread.sleep(Duration.ofMillis(500).toMillis());
        for (final TriggerInstance runningTrigger : FlowTriggerServiceTest.flowTriggerService.getRunningTriggers()) {
            FlowTriggerServiceTest.flowTriggerService.cancelTriggerInstance(runningTrigger, MANUAL);
        }
        Thread.sleep(Duration.ofMillis(500).toMillis());
        assertThat(FlowTriggerServiceTest.flowTriggerService.getRunningTriggers()).isEmpty();
        final Collection<TriggerInstance> triggerInstances = FlowTriggerServiceTest.flowTriggerService.getRecentlyFinished();
        assertThat(triggerInstances).hasSize(30);
        for (final TriggerInstance inst : triggerInstances) {
            assertThat(inst.getStatus()).isEqualTo(CANCELLED);
            for (final DependencyInstance depInst : inst.getDepInstances()) {
                assertThat(depInst.getStatus()).isEqualTo(CANCELLED);
                assertThat(depInst.getCancellationCause()).isEqualTo(MANUAL);
            }
        }
    }

    @Test
    public void testStartTriggerCancelledByFailure() throws InterruptedException {
        final List<FlowTriggerDependency> deps = new ArrayList<>();
        deps.add(TestUtil.createTestDependency("2secs", 2, true));
        deps.add(TestUtil.createTestDependency("8secs", 8, false));
        deps.add(TestUtil.createTestDependency("9secs", 9, false));
        final FlowTrigger flowTrigger = TestUtil.createTestFlowTrigger(deps, Duration.ofSeconds(10));
        for (int i = 0; i < 30; i++) {
            FlowTriggerServiceTest.flowTriggerService.startTrigger(flowTrigger, "testflow", 1, "test", createProject());
        }
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        assertThat(FlowTriggerServiceTest.flowTriggerService.getRunningTriggers()).isEmpty();
        final Collection<TriggerInstance> triggerInstances = FlowTriggerServiceTest.flowTriggerService.getRecentlyFinished();
        assertThat(triggerInstances).hasSize(30);
        for (final TriggerInstance inst : triggerInstances) {
            assertThat(inst.getStatus()).isEqualTo(CANCELLED);
            for (final DependencyInstance depInst : inst.getDepInstances()) {
                if (depInst.getDepName().equals("2secs")) {
                    assertThat(depInst.getStatus()).isEqualTo(CANCELLED);
                    assertThat(depInst.getCancellationCause()).isEqualTo(FAILURE);
                } else {
                    assertThat(depInst.getStatus()).isEqualTo(CANCELLED);
                    assertThat(depInst.getCancellationCause()).isEqualTo(CASCADING);
                }
            }
        }
    }
}

