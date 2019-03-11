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
package org.apache.hadoop.mapreduce.v2.app.metrics;


import TaskType.MAP;
import TaskType.REDUCE;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.junit.Test;
import org.mockito.Mockito;


public class TestMRAppMetrics {
    @Test
    public void testNames() {
        Job job = Mockito.mock(Job.class);
        Task mapTask = Mockito.mock(Task.class);
        Mockito.when(mapTask.getType()).thenReturn(MAP);
        Task reduceTask = Mockito.mock(Task.class);
        Mockito.when(reduceTask.getType()).thenReturn(REDUCE);
        MRAppMetrics metrics = MRAppMetrics.create();
        metrics.submittedJob(job);
        metrics.waitingTask(mapTask);
        metrics.waitingTask(reduceTask);
        metrics.preparingJob(job);
        metrics.submittedJob(job);
        metrics.waitingTask(mapTask);
        metrics.waitingTask(reduceTask);
        metrics.preparingJob(job);
        metrics.submittedJob(job);
        metrics.waitingTask(mapTask);
        metrics.waitingTask(reduceTask);
        metrics.preparingJob(job);
        metrics.endPreparingJob(job);
        metrics.endPreparingJob(job);
        metrics.endPreparingJob(job);
        metrics.runningJob(job);
        metrics.launchedTask(mapTask);
        metrics.runningTask(mapTask);
        metrics.failedTask(mapTask);
        metrics.endWaitingTask(reduceTask);
        metrics.endRunningTask(mapTask);
        metrics.endRunningJob(job);
        metrics.failedJob(job);
        metrics.runningJob(job);
        metrics.launchedTask(mapTask);
        metrics.runningTask(mapTask);
        metrics.killedTask(mapTask);
        metrics.endWaitingTask(reduceTask);
        metrics.endRunningTask(mapTask);
        metrics.endRunningJob(job);
        metrics.killedJob(job);
        metrics.runningJob(job);
        metrics.launchedTask(mapTask);
        metrics.runningTask(mapTask);
        metrics.completedTask(mapTask);
        metrics.endRunningTask(mapTask);
        metrics.launchedTask(reduceTask);
        metrics.runningTask(reduceTask);
        metrics.completedTask(reduceTask);
        metrics.endRunningTask(reduceTask);
        metrics.endRunningJob(job);
        metrics.completedJob(job);
        /* job */
        /* map */
        /* reduce */
        checkMetrics(3, 1, 1, 1, 0, 0, 3, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0);
    }
}

