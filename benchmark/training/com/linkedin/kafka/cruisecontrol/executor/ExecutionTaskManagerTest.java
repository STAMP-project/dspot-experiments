/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.executor;


import com.codahale.metrics.MetricRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.SystemTime;
import org.junit.Assert;
import org.junit.Test;


public class ExecutionTaskManagerTest {
    @Test
    public void testStateChangeSequences() {
        TopicPartition tp = new TopicPartition("topic", 0);
        ExecutionTaskManager taskManager = new ExecutionTaskManager(1, 1, null, new MetricRegistry(), new SystemTime());
        List<List<ExecutionTask.State>> testSequences = new ArrayList<>();
        // Completed successfully.
        testSequences.add(Arrays.asList(IN_PROGRESS, COMPLETED));
        // Rollback succeeded.
        testSequences.add(Arrays.asList(IN_PROGRESS, ABORTING, ABORTED));
        // Rollback failed.
        testSequences.add(Arrays.asList(IN_PROGRESS, ABORTING, DEAD));
        // Cannot rollback.
        testSequences.add(Arrays.asList(IN_PROGRESS, DEAD));
        for (List<ExecutionTask.State> sequence : testSequences) {
            taskManager.clear();
            // Make sure the proposal does not involve leader movement.
            ExecutionProposal proposal = new ExecutionProposal(tp, 0, 2, Arrays.asList(0, 2), Arrays.asList(2, 1));
            taskManager.setExecutionModeForTaskTracker(false);
            taskManager.addExecutionProposals(Collections.singletonList(proposal), Collections.emptySet(), generateExpectedCluster(proposal, tp), null);
            taskManager.setRequestedPartitionMovementConcurrency(null);
            taskManager.setRequestedLeadershipMovementConcurrency(null);
            List<ExecutionTask> tasks = taskManager.getReplicaMovementTasks();
            Assert.assertEquals(1, tasks.size());
            ExecutionTask task = tasks.get(0);
            verifyStateChangeSequence(sequence, task, taskManager);
        }
    }
}

