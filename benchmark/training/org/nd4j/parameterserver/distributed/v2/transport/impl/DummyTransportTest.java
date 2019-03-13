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
package org.nd4j.parameterserver.distributed.v2.transport.impl;


import PropagationMode.BOTH_WAYS;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.v2.messages.impl.GradientsUpdateMessage;
import org.nd4j.parameterserver.distributed.v2.messages.pairs.handshake.HandshakeRequest;
import org.nd4j.parameterserver.distributed.v2.messages.pairs.handshake.HandshakeResponse;


@Slf4j
public class DummyTransportTest {
    @Test
    public void testBasicConnection_1() throws Exception {
        val counter = new AtomicInteger(0);
        val connector = new DummyTransport.Connector();
        val transportA = new DummyTransport("alpha", connector);
        val transportB = new DummyTransport("beta", connector);
        connector.register(transportA, transportB);
        transportB.addInterceptor(HandshakeRequest.class, ( message) -> {
            // just increment
            counter.incrementAndGet();
        });
        transportA.sendMessage(new HandshakeRequest(), "beta");
        // we expect that message was delivered, and connector works
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testHandshake_1() throws Exception {
        val counter = new AtomicInteger(0);
        val connector = new DummyTransport.Connector();
        val transportA = new DummyTransport("alpha", connector);
        val transportB = new DummyTransport("beta", connector);
        connector.register(transportA, transportB);
        transportB.addInterceptor(HandshakeResponse.class, (HandshakeResponse message) -> {
            // just increment
            assertNotNull(message);
            assertNotNull(message.getMesh());
            counter.incrementAndGet();
        });
        transportB.sendMessage(new HandshakeRequest(), "alpha");
        // we expect that message was delivered, and connector works
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testMeshPropagation_1() throws Exception {
        val counter = new AtomicInteger(0);
        val connector = new DummyTransport.Connector();
        val transportA = new DummyTransport("alpha", connector);
        val transportB = new DummyTransport("beta", connector);
        val transportG = new DummyTransport("gamma", connector);
        val transportD = new DummyTransport("delta", connector);
        connector.register(transportA, transportB, transportG, transportD);
        transportB.sendMessage(new HandshakeRequest(), "alpha");
        transportG.sendMessage(new HandshakeRequest(), "alpha");
        transportD.sendMessage(new HandshakeRequest(), "alpha");
        val meshA = transportA.getMesh();
        val meshB = transportB.getMesh();
        val meshG = transportG.getMesh();
        val meshD = transportD.getMesh();
        // versions should be equal
        Assert.assertEquals(meshA.getVersion(), meshB.getVersion());
        Assert.assertEquals(meshA.getVersion(), meshG.getVersion());
        Assert.assertEquals(meshA.getVersion(), meshD.getVersion());
        // and meshs in general too
        Assert.assertEquals(meshA, meshB);
        Assert.assertEquals(meshA, meshG);
        Assert.assertEquals(meshA, meshD);
        Assert.assertTrue(meshA.isKnownNode("alpha"));
        Assert.assertTrue(meshA.isKnownNode("beta"));
        Assert.assertTrue(meshA.isKnownNode("gamma"));
        Assert.assertTrue(meshA.isKnownNode("delta"));
        val node = meshB.getNodeById("alpha");
        Assert.assertTrue(node.isRootNode());
    }

    @Test
    public void testUpdatesPropagation_1() throws Exception {
        val counter = new AtomicInteger(0);
        val connector = new DummyTransport.Connector();
        val transportA = new DummyTransport("alpha", connector);
        val transportB = new DummyTransport("beta", connector);
        val transportG = new DummyTransport("gamma", connector);
        val transportD = new DummyTransport("delta", connector);
        connector.register(transportA, transportB, transportG, transportD);
        transportB.sendMessage(new HandshakeRequest(), "alpha");
        transportG.sendMessage(new HandshakeRequest(), "alpha");
        transportD.sendMessage(new HandshakeRequest(), "alpha");
        val f = new org.nd4j.parameterserver.distributed.v2.transport.MessageCallable<GradientsUpdateMessage>() {
            @Override
            public void apply(GradientsUpdateMessage message) {
                val update = message.getPayload();
                counter.addAndGet(update.sumNumber().intValue());
            }
        };
        transportA.addPrecursor(GradientsUpdateMessage.class, f);
        transportB.addPrecursor(GradientsUpdateMessage.class, f);
        transportG.addPrecursor(GradientsUpdateMessage.class, f);
        transportD.addPrecursor(GradientsUpdateMessage.class, f);
        val array = Nd4j.ones(10, 10);
        val msg = new GradientsUpdateMessage("message", array);
        msg.setOriginatorId("beta");
        transportB.propagateMessage(msg, BOTH_WAYS);
        // we expect that each of the nodes gets this message
        Assert.assertEquals(400, counter.get());
    }

    @Test
    public void testReconnectAfterFailure_1() throws Exception {
        val counter = new AtomicInteger(0);
        val connector = new DummyTransport.Connector();
        val transportA = new DummyTransport("alpha", connector);
        val transportB = new DummyTransport("beta", connector);
        val transportG = new DummyTransport("gamma", connector);
        val transportD = new DummyTransport("delta", connector);
        val transportE = new DummyTransport("epsilon", connector);
        val transportZ = new DummyTransport("zeta", connector);
        val transportT = new DummyTransport("theta", connector);
        connector.register(transportA, transportB, transportG, transportD, transportE, transportZ, transportT);
        transportB.sendMessage(new HandshakeRequest(), "alpha");
        transportG.sendMessage(new HandshakeRequest(), "alpha");
        transportD.sendMessage(new HandshakeRequest(), "alpha");
        transportE.sendMessage(new HandshakeRequest(), "alpha");
        transportZ.sendMessage(new HandshakeRequest(), "alpha");
        transportT.sendMessage(new HandshakeRequest(), "alpha");
        val originalMeshA = transportA.getMesh();
        val originalMeshZ = transportZ.getMesh();
        Assert.assertEquals(originalMeshA, originalMeshZ);
        val version = originalMeshA.getVersion();
        val upstream = originalMeshZ.getUpstreamForNode("zeta");
        val restarted = new AtomicBoolean(false);
        val f = new org.nd4j.parameterserver.distributed.v2.transport.MessageCallable<HandshakeResponse>() {
            @Override
            public void apply(HandshakeResponse message) {
                Assert.assertTrue(message.isRestart());
                restarted.set(true);
            }
        };
        transportZ.addPrecursor(HandshakeResponse.class, f);
        // this message basically says that Z is restarting
        transportZ.sendMessage(new HandshakeRequest(), "alpha");
        val newMesh = transportZ.getMesh();
        val newUpstream = newMesh.getUpstreamForNode("zeta");
        Assert.assertNotEquals(version, newMesh.getVersion());
        Assert.assertTrue(restarted.get());
    }
}

