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
package org.apache.flink.runtime.deployment;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.blob.PermanentBlobKey;
import org.apache.flink.runtime.checkpoint.JobManagerTaskRestore;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.JobInformation;
import org.apache.flink.runtime.executiongraph.TaskInformation;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.operators.BatchTask;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TaskDeploymentDescriptor}.
 */
public class TaskDeploymentDescriptorTest extends TestLogger {
    private static final JobID jobID = new JobID();

    private static final JobVertexID vertexID = new JobVertexID();

    private static final ExecutionAttemptID execId = new ExecutionAttemptID();

    private static final AllocationID allocationId = new AllocationID();

    private static final String jobName = "job name";

    private static final String taskName = "task name";

    private static final int numberOfKeyGroups = 1;

    private static final int indexInSubtaskGroup = 0;

    private static final int currentNumberOfSubtasks = 1;

    private static final int attemptNumber = 0;

    private static final Configuration jobConfiguration = new Configuration();

    private static final Configuration taskConfiguration = new Configuration();

    private static final Class<? extends AbstractInvokable> invokableClass = BatchTask.class;

    private static final List<ResultPartitionDeploymentDescriptor> producedResults = new ArrayList<ResultPartitionDeploymentDescriptor>(0);

    private static final List<InputGateDeploymentDescriptor> inputGates = new ArrayList<InputGateDeploymentDescriptor>(0);

    private static final List<PermanentBlobKey> requiredJars = new ArrayList<>(0);

    private static final List<URL> requiredClasspaths = new ArrayList<>(0);

    private static final int targetSlotNumber = 47;

    private static final TaskStateSnapshot taskStateHandles = new TaskStateSnapshot();

    private static final JobManagerTaskRestore taskRestore = new JobManagerTaskRestore(1L, TaskDeploymentDescriptorTest.taskStateHandles);

    private final SerializedValue<ExecutionConfig> executionConfig = new SerializedValue(new ExecutionConfig());

    private final SerializedValue<JobInformation> serializedJobInformation = new SerializedValue(new JobInformation(TaskDeploymentDescriptorTest.jobID, TaskDeploymentDescriptorTest.jobName, executionConfig, TaskDeploymentDescriptorTest.jobConfiguration, TaskDeploymentDescriptorTest.requiredJars, TaskDeploymentDescriptorTest.requiredClasspaths));

    private final SerializedValue<TaskInformation> serializedJobVertexInformation = new SerializedValue(new TaskInformation(TaskDeploymentDescriptorTest.vertexID, TaskDeploymentDescriptorTest.taskName, TaskDeploymentDescriptorTest.currentNumberOfSubtasks, TaskDeploymentDescriptorTest.numberOfKeyGroups, TaskDeploymentDescriptorTest.invokableClass.getName(), TaskDeploymentDescriptorTest.taskConfiguration));

    public TaskDeploymentDescriptorTest() throws IOException {
    }

    @Test
    public void testSerialization() throws Exception {
        final TaskDeploymentDescriptor orig = createTaskDeploymentDescriptor(new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobInformation), new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobVertexInformation));
        final TaskDeploymentDescriptor copy = CommonTestUtils.createCopySerializable(orig);
        Assert.assertFalse(((orig.getSerializedJobInformation()) == (copy.getSerializedJobInformation())));
        Assert.assertFalse(((orig.getSerializedTaskInformation()) == (copy.getSerializedTaskInformation())));
        Assert.assertFalse(((orig.getExecutionAttemptId()) == (copy.getExecutionAttemptId())));
        Assert.assertFalse(((orig.getTaskRestore()) == (copy.getTaskRestore())));
        Assert.assertFalse(((orig.getProducedPartitions()) == (copy.getProducedPartitions())));
        Assert.assertFalse(((orig.getInputGates()) == (copy.getInputGates())));
        Assert.assertEquals(orig.getSerializedJobInformation(), copy.getSerializedJobInformation());
        Assert.assertEquals(orig.getSerializedTaskInformation(), copy.getSerializedTaskInformation());
        Assert.assertEquals(orig.getExecutionAttemptId(), copy.getExecutionAttemptId());
        Assert.assertEquals(orig.getAllocationId(), copy.getAllocationId());
        Assert.assertEquals(orig.getSubtaskIndex(), copy.getSubtaskIndex());
        Assert.assertEquals(orig.getAttemptNumber(), copy.getAttemptNumber());
        Assert.assertEquals(orig.getTargetSlotNumber(), copy.getTargetSlotNumber());
        Assert.assertEquals(orig.getTaskRestore().getRestoreCheckpointId(), copy.getTaskRestore().getRestoreCheckpointId());
        Assert.assertEquals(orig.getTaskRestore().getTaskStateSnapshot(), copy.getTaskRestore().getTaskStateSnapshot());
        Assert.assertEquals(orig.getProducedPartitions(), copy.getProducedPartitions());
        Assert.assertEquals(orig.getInputGates(), copy.getInputGates());
    }

    @Test
    public void testOffLoadedAndNonOffLoadedPayload() {
        final TaskDeploymentDescriptor taskDeploymentDescriptor = createTaskDeploymentDescriptor(new TaskDeploymentDescriptor.NonOffloaded<>(serializedJobInformation), new TaskDeploymentDescriptor.Offloaded<>(new PermanentBlobKey()));
        SerializedValue<JobInformation> actualSerializedJobInformation = taskDeploymentDescriptor.getSerializedJobInformation();
        Assert.assertThat(actualSerializedJobInformation, Matchers.is(serializedJobInformation));
        try {
            taskDeploymentDescriptor.getSerializedTaskInformation();
            Assert.fail("Expected to fail since the task information should be offloaded.");
        } catch (IllegalStateException expected) {
            // expected
        }
    }
}

