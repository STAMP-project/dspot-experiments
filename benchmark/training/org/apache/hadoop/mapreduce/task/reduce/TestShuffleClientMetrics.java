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
package org.apache.hadoop.mapreduce.task.reduce;


import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.metrics2.MetricsTag;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link TestShuffleClientMetrics}.
 */
public class TestShuffleClientMetrics {
    private static final String TEST_JOB_NAME = "Test job name";

    private static final String TEST_JOB_ID = "Test job id";

    private static final String TEST_TASK_ID = "Test task id";

    private static final String TEST_USER_NAME = "Test user name";

    @Test
    public void testShuffleMetricsTags() {
        // Set up
        JobID jobID = Mockito.mock(JobID.class);
        Mockito.when(jobID.toString()).thenReturn(TestShuffleClientMetrics.TEST_JOB_ID);
        TaskAttemptID reduceId = Mockito.mock(TaskAttemptID.class);
        Mockito.when(reduceId.getJobID()).thenReturn(jobID);
        Mockito.when(reduceId.toString()).thenReturn(TestShuffleClientMetrics.TEST_TASK_ID);
        JobConf jobConf = Mockito.mock(JobConf.class);
        Mockito.when(jobConf.getUser()).thenReturn(TestShuffleClientMetrics.TEST_USER_NAME);
        Mockito.when(jobConf.getJobName()).thenReturn(TestShuffleClientMetrics.TEST_JOB_NAME);
        // Act
        ShuffleClientMetrics shuffleClientMetrics = ShuffleClientMetrics.create(reduceId, jobConf);
        // Assert
        MetricsTag userMetrics = shuffleClientMetrics.getMetricsRegistry().getTag("user");
        Assert.assertEquals(TestShuffleClientMetrics.TEST_USER_NAME, userMetrics.value());
        MetricsTag jobNameMetrics = shuffleClientMetrics.getMetricsRegistry().getTag("jobName");
        Assert.assertEquals(TestShuffleClientMetrics.TEST_JOB_NAME, jobNameMetrics.value());
        MetricsTag jobIdMetrics = shuffleClientMetrics.getMetricsRegistry().getTag("jobId");
        Assert.assertEquals(TestShuffleClientMetrics.TEST_JOB_ID, jobIdMetrics.value());
        MetricsTag taskIdMetrics = shuffleClientMetrics.getMetricsRegistry().getTag("taskId");
        Assert.assertEquals(TestShuffleClientMetrics.TEST_TASK_ID, taskIdMetrics.value());
    }
}

