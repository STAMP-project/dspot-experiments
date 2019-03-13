/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.container.common.statemachine.commandhandler;


import ContainerProtos.ContainerDataProto.State.CLOSED;
import ContainerProtos.ContainerDataProto.State.QUASI_CLOSED;
import java.io.File;
import java.util.Random;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.pipeline.PipelineID;
import org.apache.hadoop.ozone.container.common.interfaces.Container;
import org.apache.hadoop.ozone.container.common.statemachine.StateContext;
import org.apache.hadoop.ozone.container.ozoneimpl.OzoneContainer;
import org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test cases to verify CloseContainerCommandHandler in datanode.
 */
public class TestCloseContainerCommandHandler {
    private final StateContext context = Mockito.mock(StateContext.class);

    private final Random random = new Random();

    private static File testDir;

    @Test
    public void testCloseContainerViaRatis() throws Exception {
        final OzoneConfiguration conf = new OzoneConfiguration();
        final DatanodeDetails datanodeDetails = TestCloseContainerCommandHandler.randomDatanodeDetails();
        final OzoneContainer ozoneContainer = getOzoneContainer(conf, datanodeDetails);
        ozoneContainer.start();
        try {
            final Container container = createContainer(conf, datanodeDetails, ozoneContainer);
            Mockito.verify(context.getParent(), Mockito.times(1)).triggerHeartbeat();
            final long containerId = container.getContainerData().getContainerID();
            final PipelineID pipelineId = PipelineID.valueOf(UUID.fromString(container.getContainerData().getOriginPipelineId()));
            // We have created a container via ratis.
            // Now close the container on ratis.
            final CloseContainerCommandHandler closeHandler = new CloseContainerCommandHandler();
            final CloseContainerCommand command = new CloseContainerCommand(containerId, pipelineId);
            closeHandler.handle(command, ozoneContainer, context, null);
            Assert.assertEquals(CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            Mockito.verify(context.getParent(), Mockito.times(3)).triggerHeartbeat();
        } finally {
            ozoneContainer.stop();
        }
    }

    @Test
    public void testCloseContainerViaStandalone() throws Exception {
        final OzoneConfiguration conf = new OzoneConfiguration();
        final DatanodeDetails datanodeDetails = TestCloseContainerCommandHandler.randomDatanodeDetails();
        final OzoneContainer ozoneContainer = getOzoneContainer(conf, datanodeDetails);
        ozoneContainer.start();
        try {
            final Container container = createContainer(conf, datanodeDetails, ozoneContainer);
            Mockito.verify(context.getParent(), Mockito.times(1)).triggerHeartbeat();
            final long containerId = container.getContainerData().getContainerID();
            // To quasi close specify a pipeline which doesn't exist in the datanode.
            final PipelineID pipelineId = PipelineID.randomId();
            // We have created a container via ratis. Now quasi close it.
            final CloseContainerCommandHandler closeHandler = new CloseContainerCommandHandler();
            final CloseContainerCommand command = new CloseContainerCommand(containerId, pipelineId);
            closeHandler.handle(command, ozoneContainer, context, null);
            Assert.assertEquals(QUASI_CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            Mockito.verify(context.getParent(), Mockito.times(3)).triggerHeartbeat();
        } finally {
            ozoneContainer.stop();
        }
    }

    @Test
    public void testQuasiCloseToClose() throws Exception {
        final OzoneConfiguration conf = new OzoneConfiguration();
        final DatanodeDetails datanodeDetails = TestCloseContainerCommandHandler.randomDatanodeDetails();
        final OzoneContainer ozoneContainer = getOzoneContainer(conf, datanodeDetails);
        ozoneContainer.start();
        try {
            final Container container = createContainer(conf, datanodeDetails, ozoneContainer);
            Mockito.verify(context.getParent(), Mockito.times(1)).triggerHeartbeat();
            final long containerId = container.getContainerData().getContainerID();
            // A pipeline which doesn't exist in the datanode.
            final PipelineID pipelineId = PipelineID.randomId();
            // We have created a container via ratis. Now quasi close it.
            final CloseContainerCommandHandler closeHandler = new CloseContainerCommandHandler();
            final CloseContainerCommand command = new CloseContainerCommand(containerId, pipelineId);
            closeHandler.handle(command, ozoneContainer, context, null);
            Assert.assertEquals(QUASI_CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            Mockito.verify(context.getParent(), Mockito.times(3)).triggerHeartbeat();
            // The container is quasi closed. Force close the container now.
            final CloseContainerCommand closeCommand = new CloseContainerCommand(containerId, pipelineId, true);
            closeHandler.handle(closeCommand, ozoneContainer, context, null);
            Assert.assertEquals(CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            Mockito.verify(context.getParent(), Mockito.times(4)).triggerHeartbeat();
        } finally {
            ozoneContainer.stop();
        }
    }

    @Test
    public void testForceCloseOpenContainer() throws Exception {
        final OzoneConfiguration conf = new OzoneConfiguration();
        final DatanodeDetails datanodeDetails = TestCloseContainerCommandHandler.randomDatanodeDetails();
        final OzoneContainer ozoneContainer = getOzoneContainer(conf, datanodeDetails);
        ozoneContainer.start();
        try {
            final Container container = createContainer(conf, datanodeDetails, ozoneContainer);
            Mockito.verify(context.getParent(), Mockito.times(1)).triggerHeartbeat();
            final long containerId = container.getContainerData().getContainerID();
            // A pipeline which doesn't exist in the datanode.
            final PipelineID pipelineId = PipelineID.randomId();
            final CloseContainerCommandHandler closeHandler = new CloseContainerCommandHandler();
            final CloseContainerCommand closeCommand = new CloseContainerCommand(containerId, pipelineId, true);
            closeHandler.handle(closeCommand, ozoneContainer, context, null);
            Assert.assertEquals(CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            Mockito.verify(context.getParent(), Mockito.times(3)).triggerHeartbeat();
        } finally {
            ozoneContainer.stop();
        }
    }

    @Test
    public void testQuasiCloseClosedContainer() throws Exception {
        final OzoneConfiguration conf = new OzoneConfiguration();
        final DatanodeDetails datanodeDetails = TestCloseContainerCommandHandler.randomDatanodeDetails();
        final OzoneContainer ozoneContainer = getOzoneContainer(conf, datanodeDetails);
        ozoneContainer.start();
        try {
            final Container container = createContainer(conf, datanodeDetails, ozoneContainer);
            Mockito.verify(context.getParent(), Mockito.times(1)).triggerHeartbeat();
            final long containerId = container.getContainerData().getContainerID();
            final PipelineID pipelineId = PipelineID.valueOf(UUID.fromString(container.getContainerData().getOriginPipelineId()));
            final CloseContainerCommandHandler closeHandler = new CloseContainerCommandHandler();
            final CloseContainerCommand closeCommand = new CloseContainerCommand(containerId, pipelineId);
            closeHandler.handle(closeCommand, ozoneContainer, context, null);
            Assert.assertEquals(CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
            // The container is closed, now we send close command with
            // pipeline id which doesn't exist.
            // This should cause the datanode to trigger quasi close, since the
            // container is already closed, this should do nothing.
            // The command should not fail either.
            final PipelineID randomPipeline = PipelineID.randomId();
            final CloseContainerCommand quasiCloseCommand = new CloseContainerCommand(containerId, randomPipeline);
            closeHandler.handle(quasiCloseCommand, ozoneContainer, context, null);
            Assert.assertEquals(CLOSED, ozoneContainer.getContainerSet().getContainer(containerId).getContainerState());
        } finally {
            ozoneContainer.stop();
        }
    }
}

