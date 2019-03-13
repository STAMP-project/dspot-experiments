/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker;


import ContainerExecutor.Signal.KILL;
import ContainerExecutor.Signal.QUIT;
import ContainerExecutor.Signal.TERM;
import ContainerRuntimeContext.Builder;
import DockerContainerStatus.CREATED;
import DockerContainerStatus.DEAD;
import DockerContainerStatus.EXITED;
import DockerContainerStatus.NONEXISTENT;
import DockerContainerStatus.REMOVING;
import DockerContainerStatus.RESTARTING;
import DockerContainerStatus.RUNNING;
import DockerContainerStatus.STOPPED;
import DockerContainerStatus.UNKNOWN;
import PrivilegedOperation.OperationType.INSPECT_DOCKER_CONTAINER;
import PrivilegedOperation.OperationType.REMOVE_DOCKER_CONTAINER;
import PrivilegedOperation.OperationType.RUN_DOCKER_CMD;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.MockPrivilegedOperationCaptor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.CGroupsHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.DockerLinuxContainerRuntime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test common docker commands.
 */
public class TestDockerCommandExecutor {
    private static final String MOCK_CONTAINER_ID = "container_e11_1861047502093_13763105_01_000001";

    private static final String MOCK_LOCAL_IMAGE_NAME = "local_image_name";

    private static final String MOCK_IMAGE_NAME = "image_name";

    private static final String MOCK_CGROUP_HIERARCHY = "hadoop-yarn";

    private PrivilegedOperationExecutor mockExecutor;

    private CGroupsHandler mockCGroupsHandler;

    private Configuration configuration;

    private Builder builder;

    private DockerLinuxContainerRuntime runtime;

    private Container container;

    private ContainerId cId;

    private ContainerLaunchContext context;

    private HashMap<String, String> env;

    private Context nmContext;

    private ApplicationAttemptId appAttemptId;

    @Test
    public void testExecuteDockerCommand() throws Exception {
        DockerStopCommand dockerStopCommand = new DockerStopCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID);
        DockerCommandExecutor.executeDockerCommand(dockerStopCommand, cId.toString(), env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
    }

    @Test
    public void testExecuteDockerRm() throws Exception {
        DockerRmCommand dockerCommand = new DockerRmCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID, null);
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        PrivilegedOperation privOp = ops.get(0);
        List<String> args = privOp.getArguments();
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(REMOVE_DOCKER_CONTAINER.name(), privOp.getOperationType().name());
        Assert.assertEquals(1, args.size());
        Assert.assertEquals(TestDockerCommandExecutor.MOCK_CONTAINER_ID, args.get(0));
    }

    @Test
    public void testExecuteDockerRmWithCgroup() throws Exception {
        DockerRmCommand dockerCommand = new DockerRmCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID, TestDockerCommandExecutor.MOCK_CGROUP_HIERARCHY);
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        PrivilegedOperation privOp = ops.get(0);
        List<String> args = privOp.getArguments();
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(REMOVE_DOCKER_CONTAINER.name(), privOp.getOperationType().name());
        Assert.assertEquals(2, args.size());
        Assert.assertEquals(TestDockerCommandExecutor.MOCK_CGROUP_HIERARCHY, args.get(0));
        Assert.assertEquals(TestDockerCommandExecutor.MOCK_CONTAINER_ID, args.get(1));
    }

    @Test
    public void testExecuteDockerStop() throws Exception {
        DockerStopCommand dockerCommand = new DockerStopCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID);
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(3, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=stop", dockerCommands.get(1));
        Assert.assertEquals(("  name=" + (TestDockerCommandExecutor.MOCK_CONTAINER_ID)), dockerCommands.get(2));
    }

    @Test
    public void testExecuteDockerInspectStatus() throws Exception {
        DockerInspectCommand dockerCommand = new DockerInspectCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID).getContainerStatus();
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        PrivilegedOperation privOp = ops.get(0);
        List<String> args = privOp.getArguments();
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(INSPECT_DOCKER_CONTAINER.name(), privOp.getOperationType().name());
        Assert.assertEquals(2, args.size());
        Assert.assertEquals("--format={{.State.Status}}", args.get(0));
        Assert.assertEquals(TestDockerCommandExecutor.MOCK_CONTAINER_ID, args.get(1));
    }

    @Test
    public void testExecuteDockerPull() throws Exception {
        DockerPullCommand dockerCommand = new DockerPullCommand(TestDockerCommandExecutor.MOCK_IMAGE_NAME);
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(3, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=pull", dockerCommands.get(1));
        Assert.assertEquals(("  image=" + (TestDockerCommandExecutor.MOCK_IMAGE_NAME)), dockerCommands.get(2));
    }

    @Test
    public void testExecuteDockerLoad() throws Exception {
        DockerLoadCommand dockerCommand = new DockerLoadCommand(TestDockerCommandExecutor.MOCK_LOCAL_IMAGE_NAME);
        DockerCommandExecutor.executeDockerCommand(dockerCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(3, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=load", dockerCommands.get(1));
        Assert.assertEquals(("  image=" + (TestDockerCommandExecutor.MOCK_LOCAL_IMAGE_NAME)), dockerCommands.get(2));
    }

    @Test
    public void testGetContainerStatus() throws Exception {
        for (DockerCommandExecutor.DockerContainerStatus status : DockerCommandExecutor.DockerContainerStatus.values()) {
            Mockito.when(mockExecutor.executePrivilegedOperation(ArgumentMatchers.eq(null), ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false))).thenReturn(status.getName());
            Assert.assertEquals(status, DockerCommandExecutor.getContainerStatus(TestDockerCommandExecutor.MOCK_CONTAINER_ID, mockExecutor, nmContext));
        }
    }

    @Test
    public void testExecuteDockerKillSIGQUIT() throws Exception {
        DockerKillCommand dockerKillCommand = new DockerKillCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID).setSignal(QUIT.name());
        DockerCommandExecutor.executeDockerCommand(dockerKillCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(4, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=kill", dockerCommands.get(1));
        Assert.assertEquals(("  name=" + (TestDockerCommandExecutor.MOCK_CONTAINER_ID)), dockerCommands.get(2));
        Assert.assertEquals(("  signal=" + (QUIT.name())), dockerCommands.get(3));
    }

    @Test
    public void testExecuteDockerKillSIGKILL() throws Exception {
        DockerKillCommand dockerKillCommand = new DockerKillCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID).setSignal(KILL.name());
        DockerCommandExecutor.executeDockerCommand(dockerKillCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(4, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=kill", dockerCommands.get(1));
        Assert.assertEquals(("  name=" + (TestDockerCommandExecutor.MOCK_CONTAINER_ID)), dockerCommands.get(2));
        Assert.assertEquals(("  signal=" + (KILL.name())), dockerCommands.get(3));
    }

    @Test
    public void testExecuteDockerKillSIGTERM() throws Exception {
        DockerKillCommand dockerKillCommand = new DockerKillCommand(TestDockerCommandExecutor.MOCK_CONTAINER_ID).setSignal(TERM.name());
        DockerCommandExecutor.executeDockerCommand(dockerKillCommand, TestDockerCommandExecutor.MOCK_CONTAINER_ID, env, mockExecutor, false, nmContext);
        List<PrivilegedOperation> ops = MockPrivilegedOperationCaptor.capturePrivilegedOperations(mockExecutor, 1, true);
        List<String> dockerCommands = getValidatedDockerCommands(ops);
        Assert.assertEquals(1, ops.size());
        Assert.assertEquals(RUN_DOCKER_CMD.name(), ops.get(0).getOperationType().name());
        Assert.assertEquals(4, dockerCommands.size());
        Assert.assertEquals("[docker-command-execution]", dockerCommands.get(0));
        Assert.assertEquals("  docker-command=kill", dockerCommands.get(1));
        Assert.assertEquals(("  name=" + (TestDockerCommandExecutor.MOCK_CONTAINER_ID)), dockerCommands.get(2));
        Assert.assertEquals(("  signal=" + (TERM.name())), dockerCommands.get(3));
    }

    @Test
    public void testIsStoppable() {
        Assert.assertTrue(DockerCommandExecutor.isStoppable(RUNNING));
        Assert.assertTrue(DockerCommandExecutor.isStoppable(RESTARTING));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(EXITED));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(CREATED));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(DEAD));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(NONEXISTENT));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(REMOVING));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(STOPPED));
        Assert.assertFalse(DockerCommandExecutor.isStoppable(UNKNOWN));
    }

    @Test
    public void testIsKIllable() {
        Assert.assertTrue(DockerCommandExecutor.isKillable(RUNNING));
        Assert.assertTrue(DockerCommandExecutor.isKillable(RESTARTING));
        Assert.assertFalse(DockerCommandExecutor.isKillable(EXITED));
        Assert.assertFalse(DockerCommandExecutor.isKillable(CREATED));
        Assert.assertFalse(DockerCommandExecutor.isKillable(DEAD));
        Assert.assertFalse(DockerCommandExecutor.isKillable(NONEXISTENT));
        Assert.assertFalse(DockerCommandExecutor.isKillable(REMOVING));
        Assert.assertFalse(DockerCommandExecutor.isKillable(STOPPED));
        Assert.assertFalse(DockerCommandExecutor.isKillable(UNKNOWN));
    }

    @Test
    public void testIsRemovable() {
        Assert.assertTrue(DockerCommandExecutor.isRemovable(STOPPED));
        Assert.assertTrue(DockerCommandExecutor.isRemovable(RESTARTING));
        Assert.assertTrue(DockerCommandExecutor.isRemovable(EXITED));
        Assert.assertTrue(DockerCommandExecutor.isRemovable(CREATED));
        Assert.assertTrue(DockerCommandExecutor.isRemovable(DEAD));
        Assert.assertFalse(DockerCommandExecutor.isRemovable(NONEXISTENT));
        Assert.assertFalse(DockerCommandExecutor.isRemovable(REMOVING));
        Assert.assertFalse(DockerCommandExecutor.isRemovable(UNKNOWN));
        Assert.assertFalse(DockerCommandExecutor.isRemovable(RUNNING));
    }
}

