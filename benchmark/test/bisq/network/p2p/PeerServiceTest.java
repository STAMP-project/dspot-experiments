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
package bisq.network.p2p;


import bisq.network.p2p.network.LocalhostNetworkNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TorNode created. Took 6 sec.
// Hidden service created. Took 40-50 sec.
// Connection establishment takes about 4 sec.
// Please Note: You need to edit seed node addresses first before using tor version.
// Run it once then lookup for onion address at: tor/hiddenservice/hostname and use that for the NodeAddress param.
// TODO deactivated because outdated
@SuppressWarnings({ "UnusedAssignment", "EmptyMethod" })
@Ignore
public class PeerServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PeerServiceTest.class);

    private static final int MAX_CONNECTIONS = 100;

    final boolean useLocalhostForP2P = true;

    private CountDownLatch latch;

    private int sleepTime;

    private DummySeedNode seedNode1;

    private DummySeedNode seedNode2;

    private DummySeedNode seedNode3;

    private final Set<NodeAddress> seedNodeAddresses = new HashSet<>();

    private final List<DummySeedNode> seedNodes = new ArrayList<>();

    private final String test_dummy_dir = "test_dummy_dir";

    @Test
    public void testSingleSeedNode() throws InterruptedException {
        LocalhostNetworkNode.setSimulateTorDelayTorNode(0);
        LocalhostNetworkNode.setSimulateTorDelayHiddenService(0);
        seedNodeAddresses.clear();
        for (int i = 0; i < 10; i++) {
            int port = 8000 + i;
            NodeAddress nodeAddress = new NodeAddress(("localhost:" + port));
            seedNodeAddresses.add(nodeAddress);
            DummySeedNode seedNode = new DummySeedNode(test_dummy_dir);
            seedNodes.add(seedNode);
            seedNode.createAndStartP2PService(true);
            seedNode.getSeedNodeP2PService().start(new P2PServiceListener() {
                @Override
                public void onDataReceived() {
                }

                @Override
                public void onNoSeedNodeAvailable() {
                }

                @Override
                public void onNoPeersAvailable() {
                }

                @Override
                public void onUpdatedDataReceived() {
                }

                @Override
                public void onTorNodeReady() {
                }

                @Override
                public void onHiddenServicePublished() {
                }

                @Override
                public void onSetupFailed(Throwable throwable) {
                }

                @Override
                public void onRequestCustomBridges() {
                }
            });
        }
        Thread.sleep(30000);
        /* latch = new CountDownLatch(2);

        seedNode.createAndStartP2PService(nodeAddress, MAX_CONNECTIONS, useLocalhostForP2P, 2, true,
        seedNodeAddresses, new P2PServiceListener() {
        @Override
        public void onRequestingDataCompleted() {
        latch.countDown();
        }

        @Override
        public void onTorNodeReady() {
        }

        @Override
        public void onNoSeedNodeAvailable() {
        }

        @Override
        public void onNoPeersAvailable() {
        }

        @Override
        public void onBootstrapComplete() {
        }

        @Override
        public void onHiddenServicePublished() {
        latch.countDown();
        }

        @Override
        public void onSetupFailed(Throwable throwable) {

        }
        });

        P2PService p2PService = seedNode.getSeedNodeP2PService();
        latch.await();
        Thread.sleep(500);
         */
        // Assert.assertEquals(0, p2PService1.getPeerManager().getAuthenticatedAndReportedPeers().size());
    }
}

