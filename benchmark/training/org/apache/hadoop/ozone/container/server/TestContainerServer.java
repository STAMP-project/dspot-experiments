/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.container.server;


import ContainerProtos.ContainerType;
import ContainerProtos.Result.SUCCESS;
import DatanodeDetails.Port.Name.STANDALONE;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos.ContainerCommandRequestProto;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos.ContainerCommandResponseProto;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.XceiverClientGrpc;
import org.apache.hadoop.hdds.scm.container.common.helpers.StorageContainerException;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.ozone.container.common.helpers.ContainerMetrics;
import org.apache.hadoop.ozone.container.common.impl.ContainerSet;
import org.apache.hadoop.ozone.container.common.impl.HddsDispatcher;
import org.apache.hadoop.ozone.container.common.interfaces.ContainerDispatcher;
import org.apache.hadoop.ozone.container.common.interfaces.Handler;
import org.apache.hadoop.ozone.container.common.statemachine.DatanodeStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.StateContext;
import org.apache.hadoop.ozone.container.common.transport.server.XceiverServerGrpc;
import org.apache.hadoop.ozone.container.common.transport.server.ratis.DispatcherContext;
import org.apache.hadoop.ozone.container.common.volume.VolumeSet;
import org.apache.hadoop.ozone.container.ozoneimpl.ContainerController;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test Containers.
 */
@Ignore("Takes too long to run this test. Ignoring for time being.")
public class TestContainerServer {
    static final String TEST_DIR = (GenericTestUtils.getTestDir("dfs").getAbsolutePath()) + (File.separator);

    private static final OzoneConfiguration CONF = new OzoneConfiguration();

    @Test
    public void testClientServer() throws Exception {
        DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
        ContainerSet containerSet = new ContainerSet();
        ContainerController controller = new ContainerController(containerSet, null);
        TestContainerServer.runTestClientServer(1, ( pipeline, conf) -> conf.setInt(OzoneConfigKeys.DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(DatanodeDetails.Port.Name.STANDALONE).getValue()), XceiverClientGrpc::new, ( dn, conf) -> new XceiverServerGrpc(datanodeDetails, conf, new org.apache.hadoop.ozone.container.server.TestContainerDispatcher(), createReplicationService(controller)), ( dn, p) -> {
        });
    }

    @FunctionalInterface
    interface CheckedBiFunction<LEFT, RIGHT, OUT, THROWABLE extends Throwable> {
        OUT apply(LEFT left, RIGHT right) throws THROWABLE;
    }

    @Test
    public void testClientServerRatisNetty() throws Exception {
        TestContainerServer.runTestClientServerRatis(NETTY, 1);
        TestContainerServer.runTestClientServerRatis(NETTY, 3);
    }

    @Test
    public void testClientServerRatisGrpc() throws Exception {
        TestContainerServer.runTestClientServerRatis(GRPC, 1);
        TestContainerServer.runTestClientServerRatis(GRPC, 3);
    }

    @Test
    public void testClientServerWithContainerDispatcher() throws Exception {
        XceiverServerGrpc server = null;
        XceiverClientGrpc client = null;
        UUID scmId = UUID.randomUUID();
        try {
            Pipeline pipeline = ContainerTestHelper.createSingleNodePipeline();
            OzoneConfiguration conf = new OzoneConfiguration();
            conf.setInt(DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(STANDALONE).getValue());
            ContainerSet containerSet = new ContainerSet();
            VolumeSet volumeSet = Mockito.mock(VolumeSet.class);
            ContainerMetrics metrics = ContainerMetrics.create(conf);
            Map<ContainerProtos.ContainerType, Handler> handlers = Maps.newHashMap();
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            DatanodeStateMachine stateMachine = Mockito.mock(DatanodeStateMachine.class);
            StateContext context = Mockito.mock(StateContext.class);
            Mockito.when(stateMachine.getDatanodeDetails()).thenReturn(datanodeDetails);
            Mockito.when(context.getParent()).thenReturn(stateMachine);
            for (ContainerProtos.ContainerType containerType : ContainerType.values()) {
                handlers.put(containerType, Handler.getHandlerForContainerType(containerType, conf, context, containerSet, volumeSet, metrics));
            }
            HddsDispatcher dispatcher = new HddsDispatcher(conf, containerSet, volumeSet, handlers, context, metrics);
            dispatcher.setScmId(scmId.toString());
            dispatcher.init();
            server = new XceiverServerGrpc(datanodeDetails, conf, dispatcher, createReplicationService(new ContainerController(containerSet, null)));
            client = new XceiverClientGrpc(pipeline, conf);
            server.start();
            client.connect();
            ContainerCommandRequestProto request = ContainerTestHelper.getCreateContainerRequest(ContainerTestHelper.getTestContainerID(), pipeline);
            ContainerCommandResponseProto response = client.sendCommand(request);
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
            Assert.assertEquals(SUCCESS, response.getResult());
        } finally {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.stop();
            }
        }
    }

    private static class TestContainerDispatcher implements ContainerDispatcher {
        /**
         * Dispatches commands to container layer.
         *
         * @param msg
         * 		- Command Request
         * @return Command Response
         */
        @Override
        public ContainerCommandResponseProto dispatch(ContainerCommandRequestProto msg, DispatcherContext context) {
            return ContainerTestHelper.getCreateContainerResponse(msg);
        }

        @Override
        public void init() {
        }

        @Override
        public void validateContainerCommand(ContainerCommandRequestProto msg) throws StorageContainerException {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public Handler getHandler(ContainerProtos.ContainerType containerType) {
            return null;
        }

        @Override
        public void setScmId(String scmId) {
        }

        @Override
        public void buildMissingContainerSet(Set<Long> createdContainerSet) {
        }
    }
}

