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
package alluxio.master;


import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.MASTER_JOURNAL_TYPE;
import PropertyKey.MASTER_RPC_PORT;
import PropertyKey.ZOOKEEPER_ADDRESS;
import PropertyKey.ZOOKEEPER_LEADER_PATH;
import ServiceType.MASTER_RPC;
import alluxio.ConfigurationRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.master.MasterInquireClient.ConnectDetails;
import alluxio.master.SingleMasterInquireClient.SingleMasterConnectDetails;
import alluxio.master.ZkMasterInquireClient.ZkMasterConnectDetails;
import alluxio.util.network.NetworkAddressUtils;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for functionality in {@link MasterInquireClient}.
 */
public final class MasterInquireClientTest {
    private InstancedConfiguration mConfiguration;

    @Test
    public void defaultConnectString() {
        ConnectDetails cs = new SingleMasterConnectDetails(NetworkAddressUtils.getConnectAddress(MASTER_RPC, mConfiguration));
        assertCurrentConnectString(cs);
        Assert.assertEquals((((NetworkAddressUtils.getConnectHost(MASTER_RPC, mConfiguration)) + ":") + (NetworkAddressUtils.getPort(MASTER_RPC, mConfiguration))), cs.toString());
    }

    @Test
    public void singleMasterConnectString() throws Exception {
        String host = "testhost";
        int port = 123;
        try (Closeable c = new ConfigurationRule(new HashMap<PropertyKey, String>() {
            {
                put(MASTER_HOSTNAME, host);
                put(MASTER_RPC_PORT, Integer.toString(port));
            }
        }, mConfiguration).toResource()) {
            ConnectDetails cs = new SingleMasterConnectDetails(new InetSocketAddress(host, port));
            assertCurrentConnectString(cs);
            Assert.assertEquals("testhost:123", cs.toString());
        }
    }

    @Test
    public void zkConnectString() throws Exception {
        String zkAddr = "zkAddr:1234";
        String leaderPath = "/my/leader/path";
        try (Closeable c = new ConfigurationRule(new HashMap<PropertyKey, String>() {
            {
                put(MASTER_JOURNAL_TYPE, "UFS");
                put(ZOOKEEPER_ADDRESS, zkAddr);
                put(ZOOKEEPER_LEADER_PATH, leaderPath);
            }
        }, mConfiguration).toResource()) {
            ConnectDetails singleConnect = new SingleMasterConnectDetails(NetworkAddressUtils.getConnectAddress(MASTER_RPC, mConfiguration));
            assertCurrentConnectString(singleConnect);
            try (Closeable c2 = new ConfigurationRule(PropertyKey.ZOOKEEPER_ENABLED, "true", mConfiguration).toResource()) {
                ConnectDetails zkConnect = new ZkMasterConnectDetails(zkAddr, leaderPath);
                assertCurrentConnectString(zkConnect);
                Assert.assertEquals("zk@zkAddr:1234/my/leader/path", zkConnect.toString());
            }
        }
    }
}

