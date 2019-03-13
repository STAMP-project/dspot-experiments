/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.file.policy;


import Constants.GB;
import Constants.MB;
import PropertyKey.NETWORK_HOST_RESOLUTION_TIMEOUT_MS;
import alluxio.ConfigurationTestUtils;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.conf.InstancedConfiguration;
import alluxio.network.TieredIdentityFactory;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.WorkerNetAddress;
import com.google.common.testing.EqualsTester;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link LocalFirstPolicy}.
 */
public final class LocalFirstPolicyTest {
    private static InstancedConfiguration sConf = ConfigurationTestUtils.defaults();

    private static int sResolutionTimeout = ((int) (LocalFirstPolicyTest.sConf.getMs(NETWORK_HOST_RESOLUTION_TIMEOUT_MS)));

    /**
     * Tests that the local host is returned first.
     */
    @Test
    public void getLocalFirst() {
        String localhostName = NetworkAddressUtils.getLocalHostName(LocalFirstPolicyTest.sResolutionTimeout);
        LocalFirstPolicy policy = new LocalFirstPolicy(LocalFirstPolicyTest.sConf);
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, "worker1", ""));
        workers.add(worker(GB, localhostName, ""));
        Assert.assertEquals(localhostName, policy.getWorkerForNextBlock(workers, MB).getHost());
    }

    /**
     * Tests that another worker is picked in case the local host does not have enough capacity.
     */
    @Test
    public void getOthersWhenNotEnoughCapacityOnLocal() {
        String localhostName = NetworkAddressUtils.getLocalHostName(LocalFirstPolicyTest.sResolutionTimeout);
        LocalFirstPolicy policy = new LocalFirstPolicy(LocalFirstPolicyTest.sConf);
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, "worker1", ""));
        workers.add(worker(MB, localhostName, ""));
        Assert.assertEquals("worker1", policy.getWorkerForNextBlock(workers, GB).getHost());
    }

    /**
     * Tests that non-local workers are randomly selected.
     */
    @Test
    public void getOthersRandomly() {
        LocalFirstPolicy policy = new LocalFirstPolicy(LocalFirstPolicyTest.sConf);
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, "worker1", ""));
        workers.add(worker(GB, "worker2", ""));
        boolean success = false;
        for (int i = 0; i < 100; i++) {
            String host = policy.getWorkerForNextBlock(workers, GB).getHost();
            if (!(host.equals(policy.getWorkerForNextBlock(workers, GB).getHost()))) {
                success = true;
                break;
            }
        }
        Assert.assertTrue(success);
    }

    @Test
    public void chooseClosestTier() throws Exception {
        List<BlockWorkerInfo> workers = new ArrayList<>();
        workers.add(worker(GB, "node2", "rack3"));
        workers.add(worker(GB, "node3", "rack2"));
        workers.add(worker(GB, "node4", "rack3"));
        LocalFirstPolicy policy;
        WorkerNetAddress chosen;
        // local rack
        policy = LocalFirstPolicy.create(TieredIdentityFactory.fromString("node=node1,rack=rack2", LocalFirstPolicyTest.sConf), LocalFirstPolicyTest.sConf);
        chosen = policy.getWorkerForNextBlock(workers, GB);
        Assert.assertEquals("rack2", chosen.getTieredIdentity().getTier(1).getValue());
        // local node
        policy = LocalFirstPolicy.create(TieredIdentityFactory.fromString("node=node4,rack=rack3", LocalFirstPolicyTest.sConf), LocalFirstPolicyTest.sConf);
        chosen = policy.getWorkerForNextBlock(workers, GB);
        Assert.assertEquals("node4", chosen.getTieredIdentity().getTier(0).getValue());
    }

    @Test
    public void tieredLocalityEnoughSpace() throws Exception {
        List<BlockWorkerInfo> workers = new ArrayList<>();
        // Local node doesn't have enough space
        workers.add(worker(MB, "node2", "rack3"));
        workers.add(worker(GB, "node3", "rack2"));
        // Local rack has enough space
        workers.add(worker(GB, "node4", "rack3"));
        LocalFirstPolicy policy = LocalFirstPolicy.create(TieredIdentityFactory.fromString("node=node2,rack=rack3", LocalFirstPolicyTest.sConf), LocalFirstPolicyTest.sConf);
        WorkerNetAddress chosen = policy.getWorkerForNextBlock(workers, GB);
        Assert.assertEquals(workers.get(2).getNetAddress(), chosen);
    }

    @Test
    public void equalsTest() throws Exception {
        new EqualsTester().addEqualityGroup(LocalFirstPolicy.create(TieredIdentityFactory.fromString("node=x,rack=y", LocalFirstPolicyTest.sConf), LocalFirstPolicyTest.sConf)).addEqualityGroup(LocalFirstPolicy.create(TieredIdentityFactory.fromString("node=x,rack=z", LocalFirstPolicyTest.sConf), LocalFirstPolicyTest.sConf)).testEquals();
    }
}

