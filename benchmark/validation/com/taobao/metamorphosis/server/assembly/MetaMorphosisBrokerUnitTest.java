/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.assembly;


import com.taobao.gecko.service.RemotingClient;
import com.taobao.gecko.service.RemotingFactory;
import com.taobao.gecko.service.config.ClientConfig;
import com.taobao.metamorphosis.network.MetamorphosisWireFormatType;
import com.taobao.metamorphosis.server.BrokerZooKeeper;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.server.utils.SlaveConfig;
import com.taobao.metamorphosis.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Assert;
import org.junit.Test;


public class MetaMorphosisBrokerUnitTest {
    private MetaMorphosisBroker broker;

    MetaConfig metaConfig;

    @Test
    public void testStartStop() throws Exception {
        this.broker.start();
        // start twice,no problem
        this.broker.start();
        // ???????zk???????
        final BrokerZooKeeper brokerZooKeeper = this.broker.getBrokerZooKeeper();
        final ZkClient client = brokerZooKeeper.getZkClient();
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/ids/" + (this.metaConfig.getBrokerId())) + "/master")));
        Assert.assertEquals("meta://localhost:8199", ZkUtils.readData(client, (("/meta/brokers/ids/" + (this.metaConfig.getBrokerId())) + "/master")));
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/topics/topic1/" + (this.metaConfig.getBrokerId())) + "-m")));
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/topics/topic2/" + (this.metaConfig.getBrokerId())) + "-m")));
        Assert.assertEquals("5", ZkUtils.readData(client, (("/meta/brokers/topics/topic2/" + (this.metaConfig.getBrokerId())) + "-m")));
        final String serverUrl = ZkUtils.readData(client, (("/meta/brokers/ids/" + (this.metaConfig.getBrokerId())) + "/master"));
        Assert.assertEquals(((("meta://" + (this.metaConfig.getHostName())) + ":") + (this.metaConfig.getServerPort())), serverUrl);
        Assert.assertEquals("1", ZkUtils.readData(client, (("/meta/brokers/topics/topic1/" + (this.metaConfig.getBrokerId())) + "-m")));
        Assert.assertEquals("5", ZkUtils.readData(client, (("/meta/brokers/topics/topic2/" + (this.metaConfig.getBrokerId())) + "-m")));
        // ??????????????
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setWireFormatType(new MetamorphosisWireFormatType());
        final RemotingClient remotingClient = RemotingFactory.connect(clientConfig);
        remotingClient.connect(serverUrl);
        remotingClient.awaitReadyInterrupt(serverUrl);
        Assert.assertTrue(remotingClient.isConnected(serverUrl));
        remotingClient.stop();
        this.broker.stop();
        // stop twice,no problem
        this.broker.stop();
    }

    @Test
    public void testStartStop_slave() throws Exception {
        this.metaConfig.setSlaveConfig(new SlaveConfig(0));
        this.broker = new MetaMorphosisBroker(this.metaConfig);
        this.broker.start();
        // start twice,no problem
        this.broker.start();
        // ???????zk???????
        final BrokerZooKeeper brokerZooKeeper = this.broker.getBrokerZooKeeper();
        final ZkClient client = brokerZooKeeper.getZkClient();
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/ids/" + (this.metaConfig.getBrokerId())) + "/slave0")));
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/topics/topic1/" + (this.metaConfig.getBrokerId())) + "-s0")));
        Assert.assertTrue(ZkUtils.pathExists(client, (("/meta/brokers/topics/topic2/" + (this.metaConfig.getBrokerId())) + "-s0")));
        final String serverUrl = ZkUtils.readData(client, (("/meta/brokers/ids/" + (this.metaConfig.getBrokerId())) + "/slave0"));
        Assert.assertEquals(((("meta://" + (this.metaConfig.getHostName())) + ":") + (this.metaConfig.getServerPort())), serverUrl);
        Assert.assertEquals("1", ZkUtils.readData(client, (("/meta/brokers/topics/topic1/" + (this.metaConfig.getBrokerId())) + "-s0")));
        Assert.assertEquals("5", ZkUtils.readData(client, (("/meta/brokers/topics/topic2/" + (this.metaConfig.getBrokerId())) + "-s0")));
        // ??????????????
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setWireFormatType(new MetamorphosisWireFormatType());
        final RemotingClient remotingClient = RemotingFactory.connect(clientConfig);
        remotingClient.connect(serverUrl);
        remotingClient.awaitReadyInterrupt(serverUrl);
        Assert.assertTrue(remotingClient.isConnected(serverUrl));
        remotingClient.stop();
        this.broker.stop();
        // stop twice,no problem
        this.broker.stop();
    }
}

