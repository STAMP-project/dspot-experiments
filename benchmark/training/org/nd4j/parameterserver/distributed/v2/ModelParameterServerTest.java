/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.parameterserver.distributed.v2;


import MeshBuildMode.MESH;
import MeshBuildMode.PLAIN;
import MeshOrganizer.Node;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.AtomicBoolean;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.v2.messages.pairs.params.ModelParametersRequest;
import org.nd4j.parameterserver.distributed.v2.messages.pairs.params.UpdaterParametersRequest;
import org.nd4j.parameterserver.distributed.v2.transport.UpdaterParametersProvider;
import org.nd4j.parameterserver.distributed.v2.transport.impl.DummyTransport;
import org.nd4j.parameterserver.distributed.v2.util.AbstractUpdatesHandler;
import org.nd4j.parameterserver.distributed.v2.util.MeshOrganizer;


@Slf4j
public class ModelParameterServerTest {
    private static final String rootId = "ROOT_NODE";

    @Test(timeout = 20000L)
    public void testBasicInitialization_1() throws Exception {
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector);
        connector.register(rootTransport);
        val rootServer = new ModelParameterServer(rootTransport, true);
        rootServer.launch();
        Assert.assertEquals(ModelParameterServerTest.rootId, rootTransport.getUpstreamId());
        rootServer.shutdown();
    }

    @Test(timeout = 20000L)
    public void testBasicInitialization_2() throws Exception {
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector);
        val clientTransportA = new DummyTransport("123", connector, ModelParameterServerTest.rootId);
        val clientTransportB = new DummyTransport("1234", connector, ModelParameterServerTest.rootId);
        connector.register(rootTransport, clientTransportA, clientTransportB);
        val rootServer = new ModelParameterServer(rootTransport, true);
        val clientServerA = new ModelParameterServer(clientTransportA, false);
        val clientServerB = new ModelParameterServer(clientTransportB, false);
        rootServer.launch();
        clientServerA.launch();
        clientServerB.launch();
        val meshR = rootTransport.getMesh();
        val meshA = clientTransportA.getMesh();
        val meshB = clientTransportB.getMesh();
        Assert.assertEquals(3, meshA.totalNodes());
        Assert.assertEquals(meshR, meshA);
        Assert.assertEquals(meshA, meshB);
    }

    @Test
    public void testUpdatesPropagation_1() throws Exception {
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector);
        val clientTransportA = new DummyTransport("412334", connector, ModelParameterServerTest.rootId);
        val clientTransportB = new DummyTransport("123441", connector, ModelParameterServerTest.rootId);
        connector.register(rootTransport, clientTransportA, clientTransportB);
        val rootServer = new ModelParameterServer(rootTransport, true);
        val clientServerA = new ModelParameterServer(clientTransportA, false);
        val clientServerB = new ModelParameterServer(clientTransportB, false);
        rootServer.launch();
        clientServerA.launch();
        clientServerB.launch();
        val array = Nd4j.ones(10, 10);
        clientServerA.sendUpdate(array);
        val updatesR = rootServer.getUpdates();
        val updatesA = clientServerA.getUpdates();
        val updatesB = clientServerB.getUpdates();
        Assert.assertEquals(1, updatesR.size());
        Assert.assertEquals(1, updatesB.size());
        // we should NOT get this message back to A
        Assert.assertEquals(0, updatesA.size());
    }

    // (timeout = 30000L)
    @Test
    public void testReconnectPropagation_1() throws Exception {
        val config = VoidConfiguration.builder().meshBuildMode(MESH).build();
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector, ModelParameterServerTest.rootId, config);
        connector.register(rootTransport);
        val rootServer = new ModelParameterServer(config, rootTransport, true);
        rootServer.addUpdatesSubscriber(new AbstractUpdatesHandler() {
            @Override
            public INDArray getParametersArray() {
                return Nd4j.create(10, 10);
            }

            @Override
            public void onNext(INDArray array) {
            }
        });
        rootServer.launch();
        val servers = new ArrayList<ModelParameterServer>();
        val transports = new ArrayList<DummyTransport>();
        for (int e = 0; e < 128; e++) {
            val clientTransport = new DummyTransport(UUID.randomUUID().toString(), connector, ModelParameterServerTest.rootId, config);
            val clientServer = new ModelParameterServer(config, clientTransport, false);
            servers.add(clientServer);
            transports.add(clientTransport);
            connector.register(clientTransport);
            clientServer.launch();
            // log.info("Client [{}] started", e );
        }
        // at this point we should have 2048 nodes within
        val rootMesh = rootTransport.getMesh();
        val originalVersion = rootMesh.getVersion();
        Assert.assertEquals(128, rootMesh.getVersion());
        // all mesh structures should be equal
        for (val t : transports)
            Assert.assertEquals(rootMesh, t.getMesh());

        // now we're picking one server that'll play bad role
        val badServer = servers.get(23);
        val badTransport = transports.get(23);
        val badId = badTransport.id();
        val badNode = rootMesh.getNodeById(badId);
        val upstreamId = badNode.getUpstreamNode().getId();
        log.info("Upstream: [{}]; Number of downstreams: [{}]", upstreamId, badNode.numberOfDownstreams());
        connector.dropConnection(badId);
        val clientTransport = new DummyTransport(badId, connector, ModelParameterServerTest.rootId);
        val clientServer = new ModelParameterServer(clientTransport, false);
        connector.register(clientTransport);
        clientServer.launch();
        // at this point we have re-registered node
        Assert.assertNotEquals(originalVersion, rootMesh.getVersion());
        val newNode = rootMesh.getNodeById(badId);
        val newUpstream = newNode.getUpstreamNode().getId();
        // after reconnect node should have 0 downstreams and new upstream
        Assert.assertNotEquals(upstreamId, newUpstream);
        Assert.assertEquals(0, newNode.numberOfDownstreams());
    }

    @Test
    public void testModelAndUpdaterParamsUpdate_1() throws Exception {
        val config = VoidConfiguration.builder().meshBuildMode(PLAIN).build();
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector, ModelParameterServerTest.rootId, config);
        rootTransport.addRequestConsumer(ModelParametersRequest.class, new io.reactivex.functions.Consumer<ModelParametersRequest>() {
            @Override
            public void accept(ModelParametersRequest modelParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.ModelParametersMessage("123", Nd4j.create(10));
                msg.setRequestId(modelParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, modelParametersRequest.getOriginatorId());
            }
        });
        rootTransport.addRequestConsumer(UpdaterParametersRequest.class, new io.reactivex.functions.Consumer<UpdaterParametersRequest>() {
            @Override
            public void accept(UpdaterParametersRequest updatersParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.UpdaterParametersMessage("123", Nd4j.create(10));
                msg.setRequestId(updatersParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, updatersParametersRequest.getOriginatorId());
            }
        });
        val updatedModel = new AtomicBoolean(false);
        val updatedUpdater = new AtomicBoolean(false);
        val gotGradients = new AtomicBoolean(false);
        connector.register(rootTransport);
        val servers = new ArrayList<ModelParameterServer>();
        val transports = new ArrayList<DummyTransport>();
        for (int e = 0; e < 128; e++) {
            val clientTransport = new DummyTransport(UUID.randomUUID().toString(), connector, ModelParameterServerTest.rootId, config);
            val clientServer = new ModelParameterServer(config, clientTransport, false);
            servers.add(clientServer);
            transports.add(clientTransport);
            connector.register(clientTransport);
            clientServer.launch();
            log.info("Client [{}] started", e);
        }
        Thread.sleep(100);
        val rootMesh = rootTransport.getMesh();
        // now we're picking one server that'll play bad role
        val badServer = servers.get(23);
        val badTransport = transports.get(23);
        val badId = badTransport.id();
        val badNode = rootMesh.getNodeById(badId);
        val upstreamId = badNode.getUpstreamNode().getId();
        log.info("Upstream: [{}]; Number of downstreams: [{}]", upstreamId, badNode.numberOfDownstreams());
        connector.dropConnection(badId);
        val clientTransport = new DummyTransport(badId, connector, ModelParameterServerTest.rootId);
        val clientServer = new ModelParameterServer(clientTransport, false);
        clientServer.addUpdaterParamsSubscriber(new org.nd4j.parameterserver.distributed.v2.util.AbstractSubscriber<INDArray>() {
            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                updatedUpdater.set(true);
            }
        });
        clientServer.addModelParamsSubscriber(new org.nd4j.parameterserver.distributed.v2.util.AbstractSubscriber<INDArray>() {
            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                updatedModel.set(true);
            }
        });
        clientServer.addUpdatesSubscriber(new AbstractUpdatesHandler() {
            @Override
            public INDArray getParametersArray() {
                return null;
            }

            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                Assert.assertEquals(Nd4j.linspace(1, 10, 100).reshape(10, 10), array);
                gotGradients.set(true);
            }
        });
        connector.register(clientTransport);
        clientServer.launch();
        connector.blockUntilFinished();
        // getting any server
        val serv = servers.get(96);
        serv.sendUpdate(Nd4j.linspace(1, 10, 100).reshape(10, 10));
        connector.blockUntilFinished();
        Assert.assertTrue(updatedModel.get());
        Assert.assertTrue(updatedUpdater.get());
        Assert.assertTrue(gotGradients.get());
    }

    @Test
    public void testModelAndUpdaterParamsUpdate_2() throws Exception {
        Nd4j.create(1);
        val config = VoidConfiguration.builder().meshBuildMode(MESH).build();
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector, ModelParameterServerTest.rootId, config);
        val rootServer = new ModelParameterServer(config, rootTransport, true);
        val rootUpdatesCounter = new AtomicInteger(0);
        rootTransport.addRequestConsumer(ModelParametersRequest.class, new io.reactivex.functions.Consumer<ModelParametersRequest>() {
            @Override
            public void accept(ModelParametersRequest modelParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.ModelParametersMessage(UUID.randomUUID().toString(), Nd4j.create(10));
                msg.setRequestId(modelParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, modelParametersRequest.getOriginatorId());
            }
        });
        rootTransport.addRequestConsumer(UpdaterParametersRequest.class, new io.reactivex.functions.Consumer<UpdaterParametersRequest>() {
            @Override
            public void accept(UpdaterParametersRequest updatersParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.UpdaterParametersMessage(UUID.randomUUID().toString(), Nd4j.create(10));
                msg.setRequestId(updatersParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, updatersParametersRequest.getOriginatorId());
            }
        });
        rootServer.addUpdatesSubscriber(new AbstractUpdatesHandler() {
            @Override
            public INDArray getParametersArray() {
                return Nd4j.create(10, 10);
            }

            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                rootUpdatesCounter.incrementAndGet();
            }
        });
        connector.register(rootTransport);
        rootServer.launch();
        val updatedModel = new AtomicBoolean(false);
        val updatedUpdater = new AtomicBoolean(false);
        val gotGradients = new AtomicBoolean(false);
        val servers = new ArrayList<ModelParameterServer>();
        val transports = new ArrayList<DummyTransport>();
        for (int e = 0; e < 32; e++) {
            val clientTransport = new DummyTransport(String.valueOf(e), connector, ModelParameterServerTest.rootId, config);
            val clientServer = new ModelParameterServer(config, clientTransport, false);
            clientServer.configure(config, clientTransport, new UpdaterParametersProvider() {
                @Override
                public INDArray getUpdaterParameters() {
                    return Nd4j.create(10, 10);
                }
            });
            servers.add(clientServer);
            transports.add(clientTransport);
            connector.register(clientTransport);
            clientServer.launch();
            log.info("Client [{}] started", e);
        }
        Thread.sleep(100);
        val rootMesh = rootTransport.getMesh();
        // now we're picking one server that'll play bad role
        val badServer = servers.get(23);
        val badTransport = transports.get(23);
        val badId = badTransport.id();
        val badNode = rootMesh.getNodeById(badId);
        val upstreamId = badNode.getUpstreamNode().getId();
        log.info("Upstream: [{}]; Number of downstreams: [{}]", upstreamId, badNode.numberOfDownstreams());
        connector.dropConnection(badId);
        val clientTransport = new DummyTransport(badId, connector, ModelParameterServerTest.rootId);
        val clientServer = new ModelParameterServer(clientTransport, false);
        clientServer.addUpdaterParamsSubscriber(new org.nd4j.parameterserver.distributed.v2.util.AbstractSubscriber<INDArray>() {
            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                updatedUpdater.set(true);
            }
        });
        clientServer.addModelParamsSubscriber(new org.nd4j.parameterserver.distributed.v2.util.AbstractSubscriber<INDArray>() {
            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                updatedModel.set(true);
            }
        });
        clientServer.addUpdatesSubscriber(new AbstractUpdatesHandler() {
            @Override
            public INDArray getParametersArray() {
                return null;
            }

            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                Assert.assertEquals(Nd4j.linspace(1, 10, 100).reshape(10, 10), array);
                gotGradients.set(true);
            }
        });
        connector.register(clientTransport);
        clientServer.launch();
        connector.blockUntilFinished();
        log.info("New upstream: {}", clientTransport.getMesh().getRootNode().getId());
        // getting any server
        val serv = servers.get(27);
        serv.sendUpdate(Nd4j.linspace(1, 10, 100).reshape(10, 10));
        connector.blockUntilFinished();
        int failedCnt = 0;
        for (int e = 0; e < 32; e++) {
            // we're skipping node 23 since it was reconnected, and has different MPS instance
            // and node 96, since it sends update
            if ((e != 23) && (e != 27))
                if ((servers.get(e).getUpdates().size()) == 0)
                    failedCnt++;


        }
        Assert.assertEquals("Some nodes got no updates:", 0, failedCnt);
        Assert.assertTrue(updatedModel.get());
        Assert.assertTrue(gotGradients.get());
        Assert.assertTrue(updatedUpdater.get());
    }

    @Test
    public void testLinearPropagation_1() throws Exception {
        Nd4j.create(1);
        val config = VoidConfiguration.builder().meshBuildMode(MESH).build();
        val connector = new DummyTransport.Connector();
        val rootTransport = new DummyTransport(ModelParameterServerTest.rootId, connector, ModelParameterServerTest.rootId, config);
        val rootServer = new ModelParameterServer(config, rootTransport, true);
        val rootUpdatesCounter = new AtomicInteger(0);
        rootTransport.addRequestConsumer(ModelParametersRequest.class, new io.reactivex.functions.Consumer<ModelParametersRequest>() {
            @Override
            public void accept(ModelParametersRequest modelParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.ModelParametersMessage("123", Nd4j.create(10));
                msg.setRequestId(modelParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, modelParametersRequest.getOriginatorId());
            }
        });
        rootTransport.addRequestConsumer(UpdaterParametersRequest.class, new io.reactivex.functions.Consumer<UpdaterParametersRequest>() {
            @Override
            public void accept(UpdaterParametersRequest updatersParametersRequest) throws Exception {
                val msg = new org.nd4j.parameterserver.distributed.v2.messages.pairs.params.UpdaterParametersMessage("123", Nd4j.create(10));
                msg.setRequestId(updatersParametersRequest.getRequestId());
                rootTransport.sendMessage(msg, updatersParametersRequest.getOriginatorId());
            }
        });
        rootServer.addUpdatesSubscriber(new AbstractUpdatesHandler() {
            @Override
            public INDArray getParametersArray() {
                return null;
            }

            @Override
            public void onNext(INDArray array) {
                Assert.assertNotNull(array);
                rootUpdatesCounter.incrementAndGet();
            }
        });
        connector.register(rootTransport);
        rootServer.launch();
        val servers = new ArrayList<ModelParameterServer>();
        val transports = new ArrayList<DummyTransport>();
        for (int e = 0; e < 7; e++) {
            val clientTransport = new DummyTransport(String.valueOf(e), connector, ModelParameterServerTest.rootId, config);
            val clientServer = new ModelParameterServer(config, clientTransport, false);
            servers.add(clientServer);
            transports.add(clientTransport);
            connector.register(clientTransport);
            clientServer.launch();
            log.info("Client [{}] started", e);
        }
        val mesh = rootTransport.getMesh();
        val rootNode = mesh.getRootNode();
        val nodesForRemap = new LinkedTransferQueue<MeshOrganizer.Node>();
        MeshOrganizer.Node lastNode = null;
        int cnt = 0;
        for (val d : rootNode.getDownstreamNodes()) {
            Assert.assertEquals(0, d.numberOfDownstreams());
            Assert.assertEquals(0, d.numberOfDownstreams());
            if ((cnt++) > 0) {
                rootNode.removeFromDownstreams(d);
                lastNode.addDownstreamNode(d);
                lastNode = d;
            } else
                lastNode = d;

        }
        Assert.assertEquals(1, rootNode.numberOfDownstreams());
        // now we want to ensure that all nodes have only 1 downstream, and last node has 0 downstreams
        val nodes = new ArrayList<MeshOrganizer.Node>(mesh.flatNodes());
        for (val n : nodes) {
            if (!(n.getId().equals("6")))
                Assert.assertEquals(1, n.numberOfDownstreams());
            else
                Assert.assertEquals(0, n.numberOfDownstreams());

        }
        // update all mesh copies, just to be sure
        for (int e = 0; e < 7; e++) {
            val t = transports.get(e);
            t.setMesh(mesh);
        }
        val middleTransport = transports.get(3);
        log.info("Upstream ID: [{}]", middleTransport.getUpstreamId());
        val middleServer = servers.get(3);
        val update = Nd4j.create(10, 10);
        middleServer.sendUpdate(update);
        connector.blockUntilFinished();
        // checking how many nodes got update
        int failCnt = 0;
        for (int e = 0; e < 7; e++) {
            val s = servers.get(e);
            if (e != 3)
                if (1 != (s.getUpdates().size())) {
                    log.info("Node [{}] have no updates", e);
                    failCnt++;
                } else
                    Assert.assertEquals(0, s.getUpdates().size());


        }
        Assert.assertEquals(0, failCnt);
        // now we're checking if root server got update
        Assert.assertEquals(1, rootUpdatesCounter.get());
    }
}

