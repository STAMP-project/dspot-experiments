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
package org.apache.ambari.server.controller.internal;


import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.HOLDING;
import HostRoleStatus.HOLDING_FAILED;
import HostRoleStatus.HOLDING_TIMEDOUT;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.PENDING;
import HostRoleStatus.QUEUED;
import HostRoleStatus.SKIPPED_FAILED;
import HostRoleStatus.TIMEDOUT;
import Role.AMBARI_SERVER_ACTION;
import RoleCommand.START;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapperFactory;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.orm.dao.HostRoleCommandStatusSummaryDTO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * CalculatedStatus tests.
 */
@SuppressWarnings("unchecked")
public class CalculatedStatusTest {
    private Injector m_injector;

    @Inject
    HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    ExecutionCommandWrapperFactory ecwFactory;

    private static long taskId = 0L;

    private static long stageId = 0L;

    private static Field s_field;

    @Test
    public void testGetStatus() throws Exception {
        Collection<HostRoleCommandEntity> tasks = getTaskEntities(COMPLETED, COMPLETED, PENDING, PENDING, PENDING);
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(IN_PROGRESS, status.getStatus());
    }

    @Test
    public void testGetPercent() throws Exception {
        Collection<HostRoleCommandEntity> tasks = getTaskEntities(COMPLETED, COMPLETED, PENDING, PENDING, PENDING);
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(40.0, status.getPercent(), 0.1);
    }

    @Test
    public void testStatusFromTaskEntities() throws Exception {
        // Pending stage
        Collection<HostRoleCommandEntity> tasks = getTaskEntities(PENDING, PENDING, PENDING, PENDING);
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(PENDING, status.getStatus());
        Assert.assertEquals(0.0, status.getPercent(), 0.1);
        // failed stage
        tasks = getTaskEntities(COMPLETED, FAILED, ABORTED, ABORTED, ABORTED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(FAILED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // failed skippable stage
        tasks = getTaskEntities(COMPLETED, FAILED, FAILED, FAILED, FAILED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, true);
        Assert.assertEquals(COMPLETED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // timed out stage
        tasks = getTaskEntities(COMPLETED, TIMEDOUT, TIMEDOUT, TIMEDOUT, TIMEDOUT);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(TIMEDOUT, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // timed out skippable stage
        tasks = getTaskEntities(COMPLETED, TIMEDOUT, FAILED, TIMEDOUT, TIMEDOUT);
        status = CalculatedStatus.statusFromTaskEntities(tasks, true);
        Assert.assertEquals(COMPLETED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // aborted stage
        tasks = getTaskEntities(COMPLETED, ABORTED, ABORTED, ABORTED, ABORTED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // aborted skippable stage (same as non-skippable)
        tasks = getTaskEntities(COMPLETED, ABORTED, ABORTED, ABORTED, ABORTED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, true);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // in progress stage
        tasks = getTaskEntities(COMPLETED, COMPLETED, PENDING, PENDING, PENDING);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(IN_PROGRESS, status.getStatus());
        Assert.assertEquals(40.0, status.getPercent(), 0.1);
        // completed stage
        tasks = getTaskEntities(COMPLETED, COMPLETED, COMPLETED, COMPLETED, COMPLETED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(COMPLETED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // holding stage
        tasks = getTaskEntities(COMPLETED, COMPLETED, HOLDING, PENDING, PENDING);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(54.0, status.getPercent(), 0.1);
        // holding failed stage
        tasks = getTaskEntities(COMPLETED, COMPLETED, HOLDING_FAILED, PENDING, PENDING);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(HOLDING_FAILED, status.getStatus());
        Assert.assertEquals(54.0, status.getPercent(), 0.1);
        // holding timed out stage
        tasks = getTaskEntities(COMPLETED, COMPLETED, HOLDING_TIMEDOUT, PENDING, PENDING);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(HOLDING_TIMEDOUT, status.getStatus());
        Assert.assertEquals(54.0, status.getPercent(), 0.1);
    }

    /**
     * Tests that aborted states calculate correctly. This is needed for upgrades
     * where the upgrade can be ABORTED and must not be calculated as COMPLETED.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAbortedCalculation() throws Exception {
        // a single task that is aborted
        Collection<HostRoleCommandEntity> tasks = getTaskEntities(ABORTED);
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // skippable
        status = CalculatedStatus.statusFromTaskEntities(tasks, true);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // a completed task and an aborted one
        tasks = getTaskEntities(COMPLETED, ABORTED);
        status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // skippable
        status = CalculatedStatus.statusFromTaskEntities(tasks, true);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
    }

    @Test
    public void testStatusFromStageEntities() throws Exception {
        // completed request
        Collection<StageEntity> stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, COMPLETED));
        CalculatedStatus status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(COMPLETED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // in progress request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, IN_PROGRESS, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(IN_PROGRESS, status.getStatus());
        Assert.assertEquals(48.3, status.getPercent(), 0.1);
        // pending request
        stages = getStageEntities(getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(PENDING, status.getStatus());
        Assert.assertEquals(0.0, status.getPercent(), 0.1);
        // failed request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, FAILED, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(FAILED, status.getStatus());
        Assert.assertEquals(55.55, status.getPercent(), 0.1);
        // timed out request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, TIMEDOUT), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(TIMEDOUT, status.getStatus());
        Assert.assertEquals(66.66, status.getPercent(), 0.1);
        // holding request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
        // holding failed request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
        // holding timed out request
        stages = getStageEntities(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStageEntities(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
    }

    @Test
    public void testStatusFromStages() throws Exception {
        Collection<Stage> stages;
        CalculatedStatus status;
        // completed request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, COMPLETED));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(COMPLETED, status.getStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // in progress request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, IN_PROGRESS, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(IN_PROGRESS, status.getStatus());
        Assert.assertEquals(48.3, status.getPercent(), 0.1);
        // pending request
        stages = getStages(getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(PENDING, status.getStatus());
        Assert.assertEquals(0.0, status.getPercent(), 0.1);
        // failed request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, FAILED, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(FAILED, status.getStatus());
        Assert.assertEquals(55.55, status.getPercent(), 0.1);
        // timed out request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, TIMEDOUT), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(TIMEDOUT, status.getStatus());
        Assert.assertEquals(66.66, status.getPercent(), 0.1);
        // holding request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
        // holding failed request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
        // holding timed out request
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, HOLDING), getTaskEntities(PENDING, PENDING, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(HOLDING, status.getStatus());
        Assert.assertNull(status.getDisplayStatus());
        Assert.assertEquals(47.5, status.getPercent(), 0.1);
        // aborted
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, ABORTED), getTaskEntities(ABORTED, ABORTED, ABORTED), getTaskEntities(ABORTED, ABORTED, ABORTED));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(ABORTED, status.getStatus());
        Assert.assertNull(status.getDisplayStatus());
        Assert.assertEquals(100.0, status.getPercent(), 0.1);
        // in-progress even though there are aborted tasks in the middle
        stages = getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(COMPLETED, COMPLETED, COMPLETED), getTaskEntities(ABORTED, ABORTED, PENDING), getTaskEntities(PENDING, PENDING, PENDING));
        status = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(IN_PROGRESS, status.getStatus());
        Assert.assertNull(status.getDisplayStatus());
        Assert.assertEquals(66.6, status.getPercent(), 0.1);
    }

    @Test
    public void testCalculateStatusCounts() throws Exception {
        Collection<HostRoleStatus> hostRoleStatuses = new LinkedList<>();
        hostRoleStatuses.add(PENDING);
        hostRoleStatuses.add(QUEUED);
        hostRoleStatuses.add(HOLDING);
        hostRoleStatuses.add(HOLDING_FAILED);
        hostRoleStatuses.add(HOLDING_TIMEDOUT);
        hostRoleStatuses.add(IN_PROGRESS);
        hostRoleStatuses.add(IN_PROGRESS);
        hostRoleStatuses.add(COMPLETED);
        hostRoleStatuses.add(COMPLETED);
        hostRoleStatuses.add(COMPLETED);
        hostRoleStatuses.add(COMPLETED);
        hostRoleStatuses.add(FAILED);
        hostRoleStatuses.add(TIMEDOUT);
        hostRoleStatuses.add(ABORTED);
        hostRoleStatuses.add(SKIPPED_FAILED);
        Map<HostRoleStatus, Integer> counts = CalculatedStatus.calculateStatusCounts(hostRoleStatuses);
        Assert.assertEquals(1L, ((long) (counts.get(PENDING))));
        Assert.assertEquals(1L, ((long) (counts.get(QUEUED))));
        Assert.assertEquals(1L, ((long) (counts.get(HOLDING))));
        Assert.assertEquals(1L, ((long) (counts.get(HOLDING_FAILED))));
        Assert.assertEquals(1L, ((long) (counts.get(HOLDING_TIMEDOUT))));
        Assert.assertEquals(5L, ((long) (counts.get(IN_PROGRESS))));
        Assert.assertEquals(8L, ((long) (counts.get(COMPLETED))));
        Assert.assertEquals(1L, ((long) (counts.get(FAILED))));
        Assert.assertEquals(1L, ((long) (counts.get(TIMEDOUT))));
        Assert.assertEquals(1L, ((long) (counts.get(ABORTED))));
        Assert.assertEquals(1L, ((long) (counts.get(SKIPPED_FAILED))));
    }

    @Test
    public void testCountsWithRepeatHosts() throws Exception {
        List<Stage> stages = new ArrayList<>();
        stages.addAll(getStages(getTaskEntities(COMPLETED, COMPLETED, COMPLETED, COMPLETED)));
        // create 5th stage that is a repeat of an earlier one
        HostRoleCommandEntity entity = new HostRoleCommandEntity();
        entity.setTaskId(((CalculatedStatusTest.taskId)++));
        entity.setStatus(PENDING);
        stages.addAll(getStages(Collections.singleton(entity)));
        CalculatedStatus calc = CalculatedStatus.statusFromStages(stages);
        Assert.assertEquals(IN_PROGRESS, calc.getStatus());
        Assert.assertEquals(80.0, calc.getPercent(), 0.1);
    }

    /**
     * Tests that a SKIPPED_FAILED status means the stage has completed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSkippedFailed_Stage() throws Exception {
        Collection<HostRoleCommandEntity> tasks = getTaskEntities(SKIPPED_FAILED);
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(tasks, false);
        Assert.assertEquals(COMPLETED, status.getStatus());
    }

    /**
     * Tests that a SKIPPED_FAILED status of any task means the
     * summary display status for is SKIPPED_FAILED, but summary status is
     * still COMPLETED
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSkippedFailed_UpgradeGroup() throws Exception {
        final HostRoleCommandStatusSummaryDTO summary1 = createNiceMock(HostRoleCommandStatusSummaryDTO.class);
        ArrayList<HostRoleStatus> taskStatuses1 = new ArrayList<HostRoleStatus>() {
            {
                add(COMPLETED);
                add(COMPLETED);
                add(COMPLETED);
            }
        };
        final HostRoleCommandStatusSummaryDTO summary2 = createNiceMock(HostRoleCommandStatusSummaryDTO.class);
        ArrayList<HostRoleStatus> taskStatuses2 = new ArrayList<HostRoleStatus>() {
            {
                add(COMPLETED);
                add(SKIPPED_FAILED);
                add(COMPLETED);
            }
        };
        Map<Long, HostRoleCommandStatusSummaryDTO> stageDto = new HashMap<Long, HostRoleCommandStatusSummaryDTO>() {
            {
                put(1L, summary1);
                put(2L, summary2);
            }
        };
        Set<Long> stageIds = new HashSet<Long>() {
            {
                add(1L);
                add(2L);
            }
        };
        expect(summary1.getTaskTotal()).andReturn(taskStatuses1.size()).anyTimes();
        expect(summary2.getTaskTotal()).andReturn(taskStatuses2.size()).anyTimes();
        expect(summary1.isStageSkippable()).andReturn(true).anyTimes();
        expect(summary2.isStageSkippable()).andReturn(true).anyTimes();
        expect(summary1.getTaskStatuses()).andReturn(taskStatuses1).anyTimes();
        expect(summary2.getTaskStatuses()).andReturn(taskStatuses2).anyTimes();
        replay(summary1, summary2);
        CalculatedStatus calc = CalculatedStatus.statusFromStageSummary(stageDto, stageIds);
        Assert.assertEquals(SKIPPED_FAILED, calc.getDisplayStatus());
        Assert.assertEquals(COMPLETED, calc.getStatus());
    }

    /**
     * Tests that upgrade group will correctly show status according to all stages.
     *
     * Example:
     *
     * If first stage have status COMPLETED and second IN_PROGRESS, overall group status should be IN_PROGRESS
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSummaryStatus_UpgradeGroup() throws Exception {
        final HostRoleCommandStatusSummaryDTO summary1 = createNiceMock(HostRoleCommandStatusSummaryDTO.class);
        ArrayList<HostRoleStatus> taskStatuses1 = new ArrayList<HostRoleStatus>() {
            {
                add(COMPLETED);
                add(COMPLETED);
                add(COMPLETED);
            }
        };
        final HostRoleCommandStatusSummaryDTO summary2 = createNiceMock(HostRoleCommandStatusSummaryDTO.class);
        ArrayList<HostRoleStatus> taskStatuses2 = new ArrayList<HostRoleStatus>() {
            {
                add(IN_PROGRESS);
                add(COMPLETED);
                add(COMPLETED);
            }
        };
        Map<Long, HostRoleCommandStatusSummaryDTO> stageDto = new HashMap<Long, HostRoleCommandStatusSummaryDTO>() {
            {
                put(1L, summary1);
                put(2L, summary2);
            }
        };
        Set<Long> stageIds = new HashSet<Long>() {
            {
                add(1L);
                add(2L);
            }
        };
        expect(summary1.getTaskTotal()).andReturn(taskStatuses1.size()).anyTimes();
        expect(summary2.getTaskTotal()).andReturn(taskStatuses2.size()).anyTimes();
        expect(summary1.isStageSkippable()).andReturn(true).anyTimes();
        expect(summary2.isStageSkippable()).andReturn(true).anyTimes();
        expect(summary1.getTaskStatuses()).andReturn(taskStatuses1).anyTimes();
        expect(summary2.getTaskStatuses()).andReturn(taskStatuses2).anyTimes();
        replay(summary1, summary2);
        CalculatedStatus calc = CalculatedStatus.statusFromStageSummary(stageDto, stageIds);
        Assert.assertEquals(IN_PROGRESS, calc.getDisplayStatus());
        Assert.assertEquals(IN_PROGRESS, calc.getStatus());
    }

    /**
     * Tests that when there are no tasks and all counts are 0, that the returned
     * status is {@link HostRoleStatus#COMPLETED}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetCompletedStatusForNoTasks() throws Exception {
        // no status / no tasks
        CalculatedStatus status = CalculatedStatus.statusFromTaskEntities(new ArrayList(), false);
        Assert.assertEquals(COMPLETED, status.getStatus());
        // empty summaries
        status = CalculatedStatus.statusFromStageSummary(new HashMap(), new HashSet());
        Assert.assertEquals(COMPLETED, status.getStatus());
        // generate a map of 0's - COMPLETED=0, IN_PROGRESS=0, etc
        Map<HostRoleStatus, Integer> counts = CalculatedStatus.calculateStatusCounts(new ArrayList());
        Map<HostRoleStatus, Integer> displayCounts = CalculatedStatus.calculateStatusCounts(new ArrayList());
        HostRoleStatus hostRoleStatus = CalculatedStatus.calculateSummaryStatusOfUpgrade(counts, 0);
        HostRoleStatus hostRoleDisplayStatus = CalculatedStatus.calculateSummaryDisplayStatus(displayCounts, 0, false);
        Assert.assertEquals(COMPLETED, hostRoleStatus);
        Assert.assertEquals(COMPLETED, hostRoleDisplayStatus);
    }

    private class TestStage extends Stage {
        private final List<HostRoleCommand> hostRoleCommands = new LinkedList<>();

        private TestStage() {
            super(1L, "", "", 1L, "", "", "", hostRoleCommandFactory, ecwFactory);
        }

        void setHostRoleCommands(Collection<HostRoleCommandEntity> tasks) {
            for (HostRoleCommandEntity task : tasks) {
                HostRoleCommand command = CalculatedStatusTest.HostRoleCommandHelper.createWithTaskId(task.getHostName(), ((CalculatedStatusTest.taskId)++), hostRoleCommandFactory);
                command.setStatus(task.getStatus());
                hostRoleCommands.add(command);
            }
        }

        @Override
        public List<HostRoleCommand> getOrderedHostRoleCommands() {
            return hostRoleCommands;
        }
    }

    private static class HostRoleCommandHelper {
        public static HostRoleCommand createWithTaskId(String hostName, long taskId, HostRoleCommandFactory hostRoleCommandFactory1) {
            HostRoleCommand hrc = hostRoleCommandFactory1.create(hostName, AMBARI_SERVER_ACTION, null, START);
            try {
                CalculatedStatusTest.s_field.set(hrc, Long.valueOf(taskId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hrc;
        }
    }
}

