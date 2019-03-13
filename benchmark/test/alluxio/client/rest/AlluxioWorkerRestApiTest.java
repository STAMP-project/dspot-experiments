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
package alluxio.client.rest;


import PropertyKey.METRICS_CONF_FILE;
import PropertyKey.WORKER_MEMORY_SIZE;
import RuntimeConstants.VERSION;
import alluxio.conf.ServerConfiguration;
import alluxio.metrics.MetricsSystem;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.Capacity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link AlluxioWorkerRestServiceHandler}.
 */
public final class AlluxioWorkerRestApiTest extends RestApiTest {
    @Test
    public void getCapacity() throws Exception {
        long total = ServerConfiguration.getBytes(WORKER_MEMORY_SIZE);
        Capacity capacity = getInfo().getCapacity();
        Assert.assertEquals(total, capacity.getTotal());
        Assert.assertEquals(0, capacity.getUsed());
    }

    @Test
    public void getConfiguration() throws Exception {
        ServerConfiguration.set(METRICS_CONF_FILE, "abc");
        Assert.assertEquals("abc", getInfo().getConfiguration().get(METRICS_CONF_FILE.toString()));
    }

    @Test
    public void getMetrics() throws Exception {
        Assert.assertEquals(Long.valueOf(0), getInfo().getMetrics().get(MetricsSystem.getMetricName("CompleteFileOps")));
    }

    @Test
    public void getRpcAddress() throws Exception {
        Assert.assertTrue(((NetworkAddressUtils.parseInetSocketAddress(getInfo().getRpcAddress()).getPort()) > 0));
    }

    @Test
    public void getStartTimeMs() throws Exception {
        Assert.assertTrue(((getInfo().getStartTimeMs()) > 0));
    }

    @Test
    public void getTierCapacity() throws Exception {
        long total = ServerConfiguration.getBytes(WORKER_MEMORY_SIZE);
        Capacity capacity = getInfo().getTierCapacity().get("MEM");
        Assert.assertEquals(total, capacity.getTotal());
        Assert.assertEquals(0, capacity.getUsed());
    }

    @Test
    public void getTierPaths() throws Exception {
        Assert.assertTrue(getInfo().getTierPaths().containsKey("MEM"));
    }

    @Test
    public void getUptimeMs() throws Exception {
        Assert.assertTrue(((getInfo().getUptimeMs()) > 0));
    }

    @Test
    public void getVersion() throws Exception {
        Assert.assertEquals(VERSION, getInfo().getVersion());
    }
}

