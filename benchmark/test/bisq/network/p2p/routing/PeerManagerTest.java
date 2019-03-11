/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.network.p2p.routing;


import bisq.network.p2p.DummySeedNode;
import bisq.network.p2p.NodeAddress;
import bisq.network.p2p.P2PService;
import bisq.network.p2p.P2PServiceListener;
import bisq.network.p2p.network.LocalhostNetworkNode;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TorNode created. Took 6 sec.
// Hidden service created. Took 40-50 sec.
// Connection establishment takes about 4 sec.
// need to define seed node addresses first before using tor version
// TODO P2P network tests are outdated
@SuppressWarnings({ "UnusedAssignment", "EmptyMethod" })
@Ignore
public class PeerManagerTest {
    private static final Logger log = LoggerFactory.getLogger(PeerManagerTest.class);

    private static final int MAX_CONNECTIONS = 100;

    final boolean useLocalhostForP2P = true;

    private CountDownLatch latch;

    private Set<NodeAddress> seedNodes;

    private int sleepTime;

    private DummySeedNode seedNode1;

    private DummySeedNode seedNode2;

    private DummySeedNode seedNode3;

    @Test
    public void test2SeedNodes() throws InterruptedException {
        LocalhostNetworkNode.setSimulateTorDelayTorNode(0);
        LocalhostNetworkNode.setSimulateTorDelayHiddenService(0);
        seedNodes = new HashSet();
        NodeAddress nodeAddress1 = new NodeAddress("localhost:8001");
        seedNodes.add(nodeAddress1);
        NodeAddress nodeAddress2 = new NodeAddress("localhost:8002");
        seedNodes.add(nodeAddress2);
        latch = new CountDownLatch(6);
        seedNode1 = new DummySeedNode("test_dummy_dir");
        seedNode1.createAndStartP2PService(nodeAddress1, PeerManagerTest.MAX_CONNECTIONS, useLocalhostForP2P, 2, true, seedNodes, new P2PServiceListener() {
            @Override
            public void onDataReceived() {
                latch.countDown();
            }

            @Override
            public void onNoSeedNodeAvailable() {
            }

            @Override
            public void onTorNodeReady() {
            }

            @Override
            public void onNoPeersAvailable() {
            }

            @Override
            public void onUpdatedDataReceived() {
                latch.countDown();
            }

            @Override
            public void onHiddenServicePublished() {
                latch.countDown();
            }

            @Override
            public void onSetupFailed(Throwable throwable) {
            }

            @Override
            public void onRequestCustomBridges() {
            }
        });
        P2PService p2PService1 = seedNode1.getSeedNodeP2PService();
        Thread.sleep(500);
        seedNode2 = new DummySeedNode("test_dummy_dir");
        seedNode2.createAndStartP2PService(nodeAddress2, PeerManagerTest.MAX_CONNECTIONS, useLocalhostForP2P, 2, true, seedNodes, new P2PServiceListener() {
            @Override
            public void onDataReceived() {
                latch.countDown();
            }

            @Override
            public void onNoSeedNodeAvailable() {
            }

            @Override
            public void onTorNodeReady() {
            }

            @Override
            public void onNoPeersAvailable() {
            }

            @Override
            public void onUpdatedDataReceived() {
                latch.countDown();
            }

            @Override
            public void onHiddenServicePublished() {
                latch.countDown();
            }

            @Override
            public void onSetupFailed(Throwable throwable) {
            }

            @Override
            public void onRequestCustomBridges() {
            }
        });
        P2PService p2PService2 = seedNode2.getSeedNodeP2PService();
        latch.await();
        // Assert.assertEquals(1, p2PService1.getPeerManager().getAuthenticatedAndReportedPeers().size());
        // Assert.assertEquals(1, p2PService2.getPeerManager().getAuthenticatedAndReportedPeers().size());
    }
}

