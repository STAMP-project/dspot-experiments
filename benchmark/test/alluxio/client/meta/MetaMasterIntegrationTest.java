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
package alluxio.client.meta;


import MasterInfoField.WEB_PORT;
import alluxio.ClientContext;
import alluxio.client.MetaMasterClient;
import alluxio.client.MetaMasterConfigClient;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.ConfigProperty;
import alluxio.grpc.MasterInfo;
import alluxio.master.MasterClientContext;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for the meta master.
 */
public final class MetaMasterIntegrationTest extends BaseIntegrationTest {
    @Rule
    public LocalAlluxioClusterResource mResource = new LocalAlluxioClusterResource.Builder().build();

    private int mWebPort;

    @Test
    public void getInfoAllFields() throws Exception {
        try (MetaMasterClient client = new alluxio.client.RetryHandlingMetaMasterClient(MasterClientContext.newBuilder(ClientContext.create(ServerConfiguration.global())).build())) {
            MasterInfo info = client.getMasterInfo(Collections.emptySet());
            Assert.assertEquals(mWebPort, info.getWebPort());
        }
    }

    @Test
    public void getMasterInfoWebPort() throws Exception {
        try (MetaMasterClient client = new alluxio.client.RetryHandlingMetaMasterClient(MasterClientContext.newBuilder(ClientContext.create(ServerConfiguration.global())).build())) {
            MasterInfo info = client.getMasterInfo(new java.util.HashSet(Arrays.asList(WEB_PORT)));
            Assert.assertEquals(mWebPort, info.getWebPort());
        }
    }

    @Test
    public void getConfigurationWebPort() throws Exception {
        try (MetaMasterConfigClient client = new alluxio.client.RetryHandlingMetaMasterConfigClient(MasterClientContext.newBuilder(ClientContext.create(ServerConfiguration.global())).build())) {
            List<ConfigProperty> configList = client.getConfiguration();
            int configWebPort = -1;
            for (ConfigProperty info : configList) {
                if (info.getName().equals("alluxio.master.web.port")) {
                    configWebPort = Integer.valueOf(info.getValue());
                }
            }
            Assert.assertEquals(mWebPort, configWebPort);
        }
    }
}

