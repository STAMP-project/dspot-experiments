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
package alluxio.util;


import PropertyKey.JOB_MASTER_EMBEDDED_JOURNAL_ADDRESSES;
import PropertyKey.JOB_MASTER_HOSTNAME;
import PropertyKey.JOB_MASTER_RPC_ADDRESSES;
import PropertyKey.JOB_MASTER_RPC_PORT;
import PropertyKey.MASTER_EMBEDDED_JOURNAL_ADDRESSES;
import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.MASTER_RPC_ADDRESSES;
import PropertyKey.MASTER_RPC_PORT;
import alluxio.Constants;
import alluxio.conf.AlluxioConfiguration;
import alluxio.util.network.NetworkAddressUtils;
import com.google.common.collect.ImmutableMap;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ConfigurationUtils}.
 */
public final class ConfigurationUtilsTest {
    @Test
    public void getSingleMasterRpcAddress() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(MASTER_HOSTNAME, "testhost", MASTER_RPC_PORT, "1000"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("testhost", 1000)), ConfigurationUtils.getMasterRpcAddresses(conf));
    }

    @Test
    public void getMasterRpcAddresses() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(MASTER_RPC_ADDRESSES, "host1:99,host2:100"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("host1", 99), new InetSocketAddress("host2", 100)), ConfigurationUtils.getMasterRpcAddresses(conf));
    }

    @Test
    public void getMasterRpcAddressesFallback() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(MASTER_EMBEDDED_JOURNAL_ADDRESSES, "host1:99,host2:100", MASTER_RPC_PORT, "50"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("host1", 50), new InetSocketAddress("host2", 50)), ConfigurationUtils.getMasterRpcAddresses(conf));
    }

    @Test
    public void getMasterRpcAddressesDefault() {
        AlluxioConfiguration conf = createConf(Collections.emptyMap());
        String host = NetworkAddressUtils.getLocalHostName((5 * (Constants.SECOND_MS)));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress(host, 19998)), ConfigurationUtils.getMasterRpcAddresses(conf));
    }

    @Test
    public void getSingleJobMasterRpcAddress() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(JOB_MASTER_HOSTNAME, "testhost", JOB_MASTER_RPC_PORT, "1000"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("testhost", 1000)), ConfigurationUtils.getJobMasterRpcAddresses(conf));
    }

    @Test
    public void getJobMasterRpcAddresses() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(JOB_MASTER_RPC_ADDRESSES, "host1:99,host2:100"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("host1", 99), new InetSocketAddress("host2", 100)), ConfigurationUtils.getJobMasterRpcAddresses(conf));
    }

    @Test
    public void getJobMasterRpcAddressesMasterRpcFallback() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(MASTER_RPC_ADDRESSES, "host1:99,host2:100", JOB_MASTER_RPC_PORT, "50"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("host1", 50), new InetSocketAddress("host2", 50)), ConfigurationUtils.getJobMasterRpcAddresses(conf));
    }

    @Test
    public void getJobMasterRpcAddressesServerFallback() {
        AlluxioConfiguration conf = createConf(ImmutableMap.of(JOB_MASTER_EMBEDDED_JOURNAL_ADDRESSES, "host1:99,host2:100", JOB_MASTER_RPC_PORT, "50"));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress("host1", 50), new InetSocketAddress("host2", 50)), ConfigurationUtils.getJobMasterRpcAddresses(conf));
    }

    @Test
    public void getJobMasterRpcAddressesDefault() {
        AlluxioConfiguration conf = createConf(Collections.emptyMap());
        String host = NetworkAddressUtils.getLocalHostName((5 * (Constants.SECOND_MS)));
        Assert.assertEquals(Arrays.asList(new InetSocketAddress(host, 20001)), ConfigurationUtils.getJobMasterRpcAddresses(conf));
    }
}

