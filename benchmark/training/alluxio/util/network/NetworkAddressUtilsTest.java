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
package alluxio.util.network;


import CommonUtils.PROCESS_TYPE;
import ProcessType.CLIENT;
import ProcessType.MASTER;
import ProcessType.WORKER;
import PropertyKey.NETWORK_HOST_RESOLUTION_TIMEOUT_MS;
import PropertyKey.USER_HOSTNAME;
import alluxio.ConfigurationRule;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.util.network.NetworkAddressUtils.ServiceType;
import alluxio.wire.WorkerNetAddress;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link NetworkAddressUtils} class.
 */
public class NetworkAddressUtilsTest {
    private InstancedConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    /**
     * Tests the
     * {@link NetworkAddressUtils#getConnectAddress(ServiceType, alluxio.conf.AlluxioConfiguration)}
     * method.
     */
    @Test
    public void testGetConnectAddress() throws Exception {
        for (ServiceType service : ServiceType.values()) {
            if ((service == (ServiceType.JOB_MASTER_RAFT)) || (service == (ServiceType.MASTER_RAFT))) {
                // Skip the raft services, which don't support separate bind and connect ports.
                continue;
            }
            getConnectAddress(service);
        }
    }

    /**
     * Tests the
     * {@link NetworkAddressUtils#getBindAddress(ServiceType, alluxio.conf.AlluxioConfiguration)}
     * method.
     */
    @Test
    public void testGetBindAddress() throws Exception {
        for (ServiceType service : ServiceType.values()) {
            if ((service == (ServiceType.JOB_MASTER_RAFT)) || (service == (ServiceType.MASTER_RAFT))) {
                // Skip the raft services, which don't support separate bind and connect ports.
                continue;
            }
            getBindAddress(service);
        }
    }

    @Test
    public void getLocalNodeNameClient() throws Exception {
        PROCESS_TYPE.set(CLIENT);
        try (Closeable c = new ConfigurationRule(PropertyKey.USER_HOSTNAME, "client", mConfiguration).toResource()) {
            Assert.assertEquals("client", NetworkAddressUtils.getLocalNodeName(mConfiguration));
        }
    }

    @Test
    public void getLocalNodeNameWorker() throws Exception {
        PROCESS_TYPE.set(WORKER);
        try (Closeable c = new ConfigurationRule(PropertyKey.WORKER_HOSTNAME, "worker", mConfiguration).toResource()) {
            Assert.assertEquals("worker", NetworkAddressUtils.getLocalNodeName(mConfiguration));
        }
    }

    @Test
    public void getLocalNodeNameMaster() throws Exception {
        PROCESS_TYPE.set(MASTER);
        try (Closeable c = new ConfigurationRule(PropertyKey.MASTER_HOSTNAME, "master", mConfiguration).toResource()) {
            Assert.assertEquals("master", NetworkAddressUtils.getLocalNodeName(mConfiguration));
        }
    }

    @Test
    public void getLocalNodeNameLookup() throws Exception {
        int resolveTimeout = ((int) (mConfiguration.getMs(NETWORK_HOST_RESOLUTION_TIMEOUT_MS)));
        Assert.assertEquals(NetworkAddressUtils.getLocalHostName(resolveTimeout), NetworkAddressUtils.getLocalNodeName(mConfiguration));
    }

    /**
     * Tests the {@link NetworkAddressUtils#resolveHostName(String)} method.
     */
    @Test
    public void resolveHostName() throws UnknownHostException {
        Assert.assertEquals(NetworkAddressUtils.resolveHostName(""), null);
        Assert.assertEquals(NetworkAddressUtils.resolveHostName(null), null);
        Assert.assertEquals(NetworkAddressUtils.resolveHostName("localhost"), "localhost");
    }

    /**
     * Tests the {@link NetworkAddressUtils#resolveIpAddress(String)} method.
     */
    @Test
    public void resolveIpAddress() throws UnknownHostException {
        Assert.assertEquals(NetworkAddressUtils.resolveIpAddress("localhost"), "127.0.0.1");
        Assert.assertEquals(NetworkAddressUtils.resolveIpAddress("127.0.0.1"), "127.0.0.1");
    }

    /**
     * Tests the {@link NetworkAddressUtils#resolveIpAddress(String)} method.
     */
    @Test(expected = NullPointerException.class)
    public void resolveNullIpAddress() throws UnknownHostException {
        NetworkAddressUtils.resolveIpAddress(null);
    }

    /**
     * Tests the {@link NetworkAddressUtils#resolveIpAddress(String)} method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void resolveEmptyIpAddress() throws UnknownHostException {
        NetworkAddressUtils.resolveIpAddress("");
    }

    /**
     * Tests the {@link NetworkAddressUtils#getFqdnHost(InetSocketAddress)} and
     * {@link NetworkAddressUtils#getFqdnHost(WorkerNetAddress)} methods.
     */
    @Test
    public void getFqdnHost() throws UnknownHostException {
        Assert.assertEquals(NetworkAddressUtils.getFqdnHost(new InetSocketAddress("localhost", 0)), "localhost");
        Assert.assertEquals(NetworkAddressUtils.getFqdnHost(new WorkerNetAddress().setHost("localhost")), "localhost");
    }

    @Test
    public void getConfiguredClientHostname() {
        mConfiguration.set(USER_HOSTNAME, "clienthost");
        Assert.assertEquals("clienthost", NetworkAddressUtils.getClientHostName(mConfiguration));
    }

    @Test
    public void getDefaultClientHostname() {
        int resolveTimeout = ((int) (mConfiguration.getMs(NETWORK_HOST_RESOLUTION_TIMEOUT_MS)));
        Assert.assertEquals(NetworkAddressUtils.getLocalHostName(resolveTimeout), NetworkAddressUtils.getClientHostName(mConfiguration));
    }
}

