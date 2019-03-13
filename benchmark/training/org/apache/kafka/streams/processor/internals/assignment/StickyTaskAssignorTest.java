/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals.assignment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.processor.TaskId;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsIterableContaining;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class StickyTaskAssignorTest {
    private final TaskId task00 = new TaskId(0, 0);

    private final TaskId task01 = new TaskId(0, 1);

    private final TaskId task02 = new TaskId(0, 2);

    private final TaskId task03 = new TaskId(0, 3);

    private final TaskId task04 = new TaskId(0, 4);

    private final TaskId task05 = new TaskId(0, 5);

    private final TaskId task10 = new TaskId(1, 0);

    private final TaskId task11 = new TaskId(1, 1);

    private final TaskId task12 = new TaskId(1, 2);

    private final TaskId task20 = new TaskId(2, 0);

    private final TaskId task21 = new TaskId(2, 1);

    private final TaskId task22 = new TaskId(2, 2);

    private final List<Integer> expectedTopicGroupIds = Arrays.asList(1, 2);

    private final Map<Integer, ClientState> clients = new TreeMap<>();

    private final Integer p1 = 1;

    private final Integer p2 = 2;

    private final Integer p3 = 3;

    private final Integer p4 = 4;

    @Test
    public void shouldAssignOneActiveTaskToEachProcessWhenTaskCountSameAsProcessCount() {
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p3, 1);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        for (final Integer processId : clients.keySet()) {
            MatcherAssert.assertThat(clients.get(processId).activeTaskCount(), CoreMatchers.equalTo(1));
        }
    }

    @Test
    public void shouldAssignTopicGroupIdEvenlyAcrossClientsWithNoStandByTasks() {
        createClient(p1, 2);
        createClient(p2, 2);
        createClient(p3, 2);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task10, task11, task22, task20, task21, task12);
        taskAssignor.assign(0);
        assertActiveTaskTopicGroupIdsEvenlyDistributed();
    }

    @Test
    public void shouldAssignTopicGroupIdEvenlyAcrossClientsWithStandByTasks() {
        createClient(p1, 2);
        createClient(p2, 2);
        createClient(p3, 2);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task20, task11, task12, task10, task21, task22);
        taskAssignor.assign(1);
        assertActiveTaskTopicGroupIdsEvenlyDistributed();
    }

    @Test
    public void shouldNotMigrateActiveTaskToOtherProcess() {
        createClientWithPreviousActiveTasks(p1, 1, task00);
        createClientWithPreviousActiveTasks(p2, 1, task01);
        final StickyTaskAssignor firstAssignor = createTaskAssignor(task00, task01, task02);
        firstAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), IsIterableContaining.hasItems(task00));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), IsIterableContaining.hasItems(task01));
        MatcherAssert.assertThat(allActiveTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
        clients.clear();
        // flip the previous active tasks assignment around.
        createClientWithPreviousActiveTasks(p1, 1, task01);
        createClientWithPreviousActiveTasks(p2, 1, task02);
        final StickyTaskAssignor secondAssignor = createTaskAssignor(task00, task01, task02);
        secondAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), IsIterableContaining.hasItems(task01));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), IsIterableContaining.hasItems(task02));
        MatcherAssert.assertThat(allActiveTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
    }

    @Test
    public void shouldMigrateActiveTasksToNewProcessWithoutChangingAllAssignments() {
        createClientWithPreviousActiveTasks(p1, 1, task00, task02);
        createClientWithPreviousActiveTasks(p2, 1, task01);
        createClient(p3, 1);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task01)));
        MatcherAssert.assertThat(clients.get(p1).activeTasks().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p3).activeTasks().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(allActiveTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
    }

    @Test
    public void shouldAssignBasedOnCapacity() {
        createClient(p1, 1);
        createClient(p2, 2);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p2).activeTasks().size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void shouldAssignTasksEvenlyWithUnequalTopicGroupSizes() {
        createClientWithPreviousActiveTasks(p1, 1, task00, task01, task02, task03, task04, task05, task10);
        createClient(p2, 1);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task10, task00, task01, task02, task03, task04, task05);
        final Set<TaskId> expectedClientITasks = new java.util.HashSet(Arrays.asList(task00, task01, task10, task05));
        final Set<TaskId> expectedClientIITasks = new java.util.HashSet(Arrays.asList(task02, task03, task04));
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), CoreMatchers.equalTo(expectedClientITasks));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(expectedClientIITasks));
    }

    @Test
    public void shouldKeepActiveTaskStickynessWhenMoreClientThanActiveTasks() {
        final int p5 = 5;
        createClientWithPreviousActiveTasks(p1, 1, task00);
        createClientWithPreviousActiveTasks(p2, 1, task02);
        createClientWithPreviousActiveTasks(p3, 1, task01);
        createClient(p4, 1);
        createClient(p5, 1);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task00)));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task02)));
        MatcherAssert.assertThat(clients.get(p3).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task01)));
        // change up the assignment and make sure it is still sticky
        clients.clear();
        createClient(p1, 1);
        createClientWithPreviousActiveTasks(p2, 1, task00);
        createClient(p3, 1);
        createClientWithPreviousActiveTasks(p4, 1, task02);
        createClientWithPreviousActiveTasks(p5, 1, task01);
        final StickyTaskAssignor secondAssignor = createTaskAssignor(task00, task01, task02);
        secondAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task00)));
        MatcherAssert.assertThat(clients.get(p4).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task02)));
        MatcherAssert.assertThat(clients.get(p5).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task01)));
    }

    @Test
    public void shouldAssignTasksToClientWithPreviousStandbyTasks() {
        final ClientState client1 = createClient(p1, 1);
        client1.addPreviousStandbyTasks(Utils.mkSet(task02));
        final ClientState client2 = createClient(p2, 1);
        client2.addPreviousStandbyTasks(Utils.mkSet(task01));
        final ClientState client3 = createClient(p3, 1);
        client3.addPreviousStandbyTasks(Utils.mkSet(task00));
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task02)));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task01)));
        MatcherAssert.assertThat(clients.get(p3).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task00)));
    }

    @Test
    public void shouldAssignBasedOnCapacityWhenMultipleClientHaveStandbyTasks() {
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task00);
        c1.addPreviousStandbyTasks(Utils.mkSet(task01));
        final ClientState c2 = createClientWithPreviousActiveTasks(p2, 2, task02);
        c2.addPreviousStandbyTasks(Utils.mkSet(task01));
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), CoreMatchers.equalTo(Collections.singleton(task00)));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task02, task01)));
    }

    @Test
    public void shouldAssignStandbyTasksToDifferentClientThanCorrespondingActiveTaskIsAssingedTo() {
        createClientWithPreviousActiveTasks(p1, 1, task00);
        createClientWithPreviousActiveTasks(p2, 1, task01);
        createClientWithPreviousActiveTasks(p3, 1, task02);
        createClientWithPreviousActiveTasks(p4, 1, task03);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02, task03);
        taskAssignor.assign(1);
        MatcherAssert.assertThat(clients.get(p1).standbyTasks(), IsNot.not(IsIterableContaining.hasItems(task00)));
        Assert.assertTrue(((clients.get(p1).standbyTasks().size()) <= 2));
        MatcherAssert.assertThat(clients.get(p2).standbyTasks(), IsNot.not(IsIterableContaining.hasItems(task01)));
        Assert.assertTrue(((clients.get(p2).standbyTasks().size()) <= 2));
        MatcherAssert.assertThat(clients.get(p3).standbyTasks(), IsNot.not(IsIterableContaining.hasItems(task02)));
        Assert.assertTrue(((clients.get(p3).standbyTasks().size()) <= 2));
        MatcherAssert.assertThat(clients.get(p4).standbyTasks(), IsNot.not(IsIterableContaining.hasItems(task03)));
        Assert.assertTrue(((clients.get(p4).standbyTasks().size()) <= 2));
        int nonEmptyStandbyTaskCount = 0;
        for (final Integer client : clients.keySet()) {
            nonEmptyStandbyTaskCount += (clients.get(client).standbyTasks().isEmpty()) ? 0 : 1;
        }
        Assert.assertTrue((nonEmptyStandbyTaskCount >= 3));
        MatcherAssert.assertThat(allStandbyTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02, task03)));
    }

    @Test
    public void shouldAssignMultipleReplicasOfStandbyTask() {
        createClientWithPreviousActiveTasks(p1, 1, task00);
        createClientWithPreviousActiveTasks(p2, 1, task01);
        createClientWithPreviousActiveTasks(p3, 1, task02);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(2);
        MatcherAssert.assertThat(clients.get(p1).standbyTasks(), CoreMatchers.equalTo(Utils.mkSet(task01, task02)));
        MatcherAssert.assertThat(clients.get(p2).standbyTasks(), CoreMatchers.equalTo(Utils.mkSet(task02, task00)));
        MatcherAssert.assertThat(clients.get(p3).standbyTasks(), CoreMatchers.equalTo(Utils.mkSet(task00, task01)));
    }

    @Test
    public void shouldNotAssignStandbyTaskReplicasWhenNoClientAvailableWithoutHavingTheTaskAssigned() {
        createClient(p1, 1);
        final StickyTaskAssignor taskAssignor = createTaskAssignor(task00);
        taskAssignor.assign(1);
        MatcherAssert.assertThat(clients.get(p1).standbyTasks().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void shouldAssignActiveAndStandbyTasks() {
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(1);
        MatcherAssert.assertThat(allActiveTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
        MatcherAssert.assertThat(allStandbyTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
    }

    @Test
    public void shouldAssignAtLeastOneTaskToEachClientIfPossible() {
        createClient(p1, 3);
        createClient(p2, 1);
        createClient(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p2).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p3).assignedTaskCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldAssignEachActiveTaskToOneClientWhenMoreClientsThanTasks() {
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p3, 1);
        createClient(p4, 1);
        createClient(5, 1);
        createClient(6, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(allActiveTasks(), CoreMatchers.equalTo(Arrays.asList(task00, task01, task02)));
    }

    @Test
    public void shouldBalanceActiveAndStandbyTasksAcrossAvailableClients() {
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p3, 1);
        createClient(p4, 1);
        createClient(5, 1);
        createClient(6, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02);
        taskAssignor.assign(1);
        for (final ClientState clientState : clients.values()) {
            MatcherAssert.assertThat(clientState.assignedTaskCount(), CoreMatchers.equalTo(1));
        }
    }

    @Test
    public void shouldAssignMoreTasksToClientWithMoreCapacity() {
        createClient(p2, 2);
        createClient(p1, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02, new TaskId(1, 0), new TaskId(1, 1), new TaskId(1, 2), new TaskId(2, 0), new TaskId(2, 1), new TaskId(2, 2), new TaskId(3, 0), new TaskId(3, 1), new TaskId(3, 2));
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p2).assignedTaskCount(), CoreMatchers.equalTo(8));
        MatcherAssert.assertThat(clients.get(p1).assignedTaskCount(), CoreMatchers.equalTo(4));
    }

    @Test
    public void shouldEvenlyDistributeByTaskIdAndPartition() {
        createClient(p1, 4);
        createClient(p2, 4);
        createClient(p3, 4);
        createClient(p4, 4);
        final List<TaskId> taskIds = new ArrayList<>();
        final TaskId[] taskIdArray = new TaskId[16];
        for (int i = 1; i <= 2; i++) {
            for (int j = 0; j < 8; j++) {
                taskIds.add(new TaskId(i, j));
            }
        }
        Collections.shuffle(taskIds);
        taskIds.toArray(taskIdArray);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(taskIdArray);
        taskAssignor.assign(0);
        Collections.sort(taskIds);
        final Set<TaskId> expectedClientOneAssignment = getExpectedTaskIdAssignment(taskIds, 0, 4, 8, 12);
        final Set<TaskId> expectedClientTwoAssignment = getExpectedTaskIdAssignment(taskIds, 1, 5, 9, 13);
        final Set<TaskId> expectedClientThreeAssignment = getExpectedTaskIdAssignment(taskIds, 2, 6, 10, 14);
        final Set<TaskId> expectedClientFourAssignment = getExpectedTaskIdAssignment(taskIds, 3, 7, 11, 15);
        final Map<Integer, Set<TaskId>> sortedAssignments = sortClientAssignments(clients);
        MatcherAssert.assertThat(sortedAssignments.get(p1), CoreMatchers.equalTo(expectedClientOneAssignment));
        MatcherAssert.assertThat(sortedAssignments.get(p2), CoreMatchers.equalTo(expectedClientTwoAssignment));
        MatcherAssert.assertThat(sortedAssignments.get(p3), CoreMatchers.equalTo(expectedClientThreeAssignment));
        MatcherAssert.assertThat(sortedAssignments.get(p4), CoreMatchers.equalTo(expectedClientFourAssignment));
    }

    @Test
    public void shouldNotHaveSameAssignmentOnAnyTwoHosts() {
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p3, 1);
        createClient(p4, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(1);
        for (int i = p1; i <= (p4); i++) {
            final Set<TaskId> taskIds = clients.get(i).assignedTasks();
            for (int j = p1; j <= (p4); j++) {
                if (j != i) {
                    MatcherAssert.assertThat("clients shouldn't have same task assignment", clients.get(j).assignedTasks(), IsNot.not(CoreMatchers.equalTo(taskIds)));
                }
            }
        }
    }

    @Test
    public void shouldNotHaveSameAssignmentOnAnyTwoHostsWhenThereArePreviousActiveTasks() {
        createClientWithPreviousActiveTasks(p1, 1, task01, task02);
        createClientWithPreviousActiveTasks(p2, 1, task03);
        createClientWithPreviousActiveTasks(p3, 1, task00);
        createClient(p4, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(1);
        for (int i = p1; i <= (p4); i++) {
            final Set<TaskId> taskIds = clients.get(i).assignedTasks();
            for (int j = p1; j <= (p4); j++) {
                if (j != i) {
                    MatcherAssert.assertThat("clients shouldn't have same task assignment", clients.get(j).assignedTasks(), IsNot.not(CoreMatchers.equalTo(taskIds)));
                }
            }
        }
    }

    @Test
    public void shouldNotHaveSameAssignmentOnAnyTwoHostsWhenThereArePreviousStandbyTasks() {
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task01, task02);
        c1.addPreviousStandbyTasks(Utils.mkSet(task03, task00));
        final ClientState c2 = createClientWithPreviousActiveTasks(p2, 1, task03, task00);
        c2.addPreviousStandbyTasks(Utils.mkSet(task01, task02));
        createClient(p3, 1);
        createClient(p4, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(1);
        for (int i = p1; i <= (p4); i++) {
            final Set<TaskId> taskIds = clients.get(i).assignedTasks();
            for (int j = p1; j <= (p4); j++) {
                if (j != i) {
                    MatcherAssert.assertThat("clients shouldn't have same task assignment", clients.get(j).assignedTasks(), IsNot.not(CoreMatchers.equalTo(taskIds)));
                }
            }
        }
    }

    @Test
    public void shouldReBalanceTasksAcrossAllClientsWhenCapacityAndTaskCountTheSame() {
        createClientWithPreviousActiveTasks(p3, 1, task00, task01, task02, task03);
        createClient(p1, 1);
        createClient(p2, 1);
        createClient(p4, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p2).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p3).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p4).assignedTaskCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldReBalanceTasksAcrossClientsWhenCapacityLessThanTaskCount() {
        createClientWithPreviousActiveTasks(p3, 1, task00, task01, task02, task03);
        createClient(p1, 1);
        createClient(p2, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p3).assignedTaskCount(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(clients.get(p1).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p2).assignedTaskCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldRebalanceTasksToClientsBasedOnCapacity() {
        createClientWithPreviousActiveTasks(p2, 1, task00, task03, task02);
        createClient(p3, 2);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task03);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p2).assignedTaskCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(clients.get(p3).assignedTaskCount(), CoreMatchers.equalTo(2));
    }

    @Test
    public void shouldMoveMinimalNumberOfTasksWhenPreviouslyAboveCapacityAndNewClientAdded() {
        final Set<TaskId> p1PrevTasks = Utils.mkSet(task00, task02);
        final Set<TaskId> p2PrevTasks = Utils.mkSet(task01, task03);
        createClientWithPreviousActiveTasks(p1, 1, task00, task02);
        createClientWithPreviousActiveTasks(p2, 1, task01, task03);
        createClientWithPreviousActiveTasks(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task02, task01, task03);
        taskAssignor.assign(0);
        final Set<TaskId> p3ActiveTasks = clients.get(p3).activeTasks();
        MatcherAssert.assertThat(p3ActiveTasks.size(), CoreMatchers.equalTo(1));
        if (p1PrevTasks.removeAll(p3ActiveTasks)) {
            MatcherAssert.assertThat(clients.get(p2).activeTasks(), CoreMatchers.equalTo(p2PrevTasks));
        } else {
            MatcherAssert.assertThat(clients.get(p1).activeTasks(), CoreMatchers.equalTo(p1PrevTasks));
        }
    }

    @Test
    public void shouldNotMoveAnyTasksWhenNewTasksAdded() {
        createClientWithPreviousActiveTasks(p1, 1, task00, task01);
        createClientWithPreviousActiveTasks(p2, 1, task02, task03);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task03, task01, task04, task02, task00, task05);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), IsIterableContaining.hasItems(task00, task01));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), IsIterableContaining.hasItems(task02, task03));
    }

    @Test
    public void shouldAssignNewTasksToNewClientWhenPreviousTasksAssignedToOldClients() {
        createClientWithPreviousActiveTasks(p1, 1, task02, task01);
        createClientWithPreviousActiveTasks(p2, 1, task00, task03);
        createClient(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task03, task01, task04, task02, task00, task05);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTasks(), IsIterableContaining.hasItems(task02, task01));
        MatcherAssert.assertThat(clients.get(p2).activeTasks(), IsIterableContaining.hasItems(task00, task03));
        MatcherAssert.assertThat(clients.get(p3).activeTasks(), IsIterableContaining.hasItems(task04, task05));
    }

    @Test
    public void shouldAssignTasksNotPreviouslyActiveToNewClient() {
        final TaskId task10 = new TaskId(0, 10);
        final TaskId task11 = new TaskId(0, 11);
        final TaskId task12 = new TaskId(1, 2);
        final TaskId task13 = new TaskId(1, 3);
        final TaskId task20 = new TaskId(2, 0);
        final TaskId task21 = new TaskId(2, 1);
        final TaskId task22 = new TaskId(2, 2);
        final TaskId task23 = new TaskId(2, 3);
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task01, task12, task13);
        c1.addPreviousStandbyTasks(Utils.mkSet(task00, task11, task20, task21, task23));
        final ClientState c2 = createClientWithPreviousActiveTasks(p2, 1, task00, task11, task22);
        c2.addPreviousStandbyTasks(Utils.mkSet(task01, task10, task02, task20, task03, task12, task21, task13, task23));
        final ClientState c3 = createClientWithPreviousActiveTasks(p3, 1, task20, task21, task23);
        c3.addPreviousStandbyTasks(Utils.mkSet(task02, task12));
        final ClientState newClient = createClient(p4, 1);
        newClient.addPreviousStandbyTasks(Utils.mkSet(task00, task10, task01, task02, task11, task20, task03, task12, task21, task13, task22, task23));
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task10, task01, task02, task11, task20, task03, task12, task21, task13, task22, task23);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(c1.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task01, task12, task13)));
        MatcherAssert.assertThat(c2.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task00, task11, task22)));
        MatcherAssert.assertThat(c3.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task20, task21, task23)));
        MatcherAssert.assertThat(newClient.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task02, task03, task10)));
    }

    @Test
    public void shouldAssignTasksNotPreviouslyActiveToMultipleNewClients() {
        final TaskId task10 = new TaskId(0, 10);
        final TaskId task11 = new TaskId(0, 11);
        final TaskId task12 = new TaskId(1, 2);
        final TaskId task13 = new TaskId(1, 3);
        final TaskId task20 = new TaskId(2, 0);
        final TaskId task21 = new TaskId(2, 1);
        final TaskId task22 = new TaskId(2, 2);
        final TaskId task23 = new TaskId(2, 3);
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task01, task12, task13);
        c1.addPreviousStandbyTasks(Utils.mkSet(task00, task11, task20, task21, task23));
        final ClientState c2 = createClientWithPreviousActiveTasks(p2, 1, task00, task11, task22);
        c2.addPreviousStandbyTasks(Utils.mkSet(task01, task10, task02, task20, task03, task12, task21, task13, task23));
        final ClientState bounce1 = createClient(p3, 1);
        bounce1.addPreviousStandbyTasks(Utils.mkSet(task20, task21, task23));
        final ClientState bounce2 = createClient(p4, 1);
        bounce2.addPreviousStandbyTasks(Utils.mkSet(task02, task03, task10));
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task10, task01, task02, task11, task20, task03, task12, task21, task13, task22, task23);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(c1.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task01, task12, task13)));
        MatcherAssert.assertThat(c2.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task00, task11, task22)));
        MatcherAssert.assertThat(bounce1.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task20, task21, task23)));
        MatcherAssert.assertThat(bounce2.activeTasks(), CoreMatchers.equalTo(Utils.mkSet(task02, task03, task10)));
    }

    @Test
    public void shouldAssignTasksToNewClient() {
        createClientWithPreviousActiveTasks(p1, 1, task01, task02);
        createClient(p2, 1);
        createTaskAssignor(task01, task02).assign(0);
        MatcherAssert.assertThat(clients.get(p1).activeTaskCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldAssignTasksToNewClientWithoutFlippingAssignmentBetweenExistingClients() {
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task00, task01, task02);
        final ClientState c2 = createClientWithPreviousActiveTasks(p2, 1, task03, task04, task05);
        final ClientState newClient = createClient(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02, task03, task04, task05);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task03)));
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task04)));
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task05)));
        MatcherAssert.assertThat(c1.activeTaskCount(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task00)));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task01)));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task02)));
        MatcherAssert.assertThat(c2.activeTaskCount(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(newClient.activeTaskCount(), CoreMatchers.equalTo(2));
    }

    @Test
    public void shouldAssignTasksToNewClientWithoutFlippingAssignmentBetweenExistingAndBouncedClients() {
        final TaskId task06 = new TaskId(0, 6);
        final ClientState c1 = createClientWithPreviousActiveTasks(p1, 1, task00, task01, task02, task06);
        final ClientState c2 = createClient(p2, 1);
        c2.addPreviousStandbyTasks(Utils.mkSet(task03, task04, task05));
        final ClientState newClient = createClient(p3, 1);
        final StickyTaskAssignor<Integer> taskAssignor = createTaskAssignor(task00, task01, task02, task03, task04, task05, task06);
        taskAssignor.assign(0);
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task03)));
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task04)));
        MatcherAssert.assertThat(c1.activeTasks(), IsNot.not(IsIterableContaining.hasItem(task05)));
        MatcherAssert.assertThat(c1.activeTaskCount(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task00)));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task01)));
        MatcherAssert.assertThat(c2.activeTasks(), IsNot.not(IsIterableContaining.hasItems(task02)));
        MatcherAssert.assertThat(c2.activeTaskCount(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(newClient.activeTaskCount(), CoreMatchers.equalTo(2));
    }
}

