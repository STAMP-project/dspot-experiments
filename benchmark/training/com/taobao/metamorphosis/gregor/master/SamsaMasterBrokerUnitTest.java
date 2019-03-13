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
package com.taobao.metamorphosis.gregor.master;


import com.taobao.gecko.service.RemotingServer;
import com.taobao.metamorphosis.gregor.master.SamsaMasterBroker.OffsetInfo;
import com.taobao.metamorphosis.gregor.master.SamsaMasterBroker.RecoverPartition;
import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;
import com.taobao.metamorphosis.server.store.AppendCallback;
import com.taobao.metamorphosis.server.store.Location;
import com.taobao.metamorphosis.server.store.MessageStore;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.utils.IdWorker;
import com.taobao.metamorphosis.utils.ZkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TreeSet;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Assert;
import org.junit.Test;


public class SamsaMasterBrokerUnitTest {
    private SamsaMasterBroker broker;

    @Test
    public void testFork() {
        final List<RecoverPartition> parts = new ArrayList<RecoverPartition>();
        for (int i = 0; i < 10; i++) {
            final String topic = "test" + (i % 2);
            parts.add(new RecoverPartition(topic, i));
        }
        System.out.println(parts);
        final List<List<RecoverPartition>> forks = SamsaMasterBroker.fork(parts, 3);
        System.out.println(forks);
        Assert.assertEquals(3, forks.size());
        Assert.assertEquals(4, forks.get(0).size());
        Assert.assertEquals(3, forks.get(1).size());
        Assert.assertEquals(3, forks.get(2).size());
        final TreeSet<RecoverPartition> set1 = new TreeSet<SamsaMasterBroker.RecoverPartition>(parts);
        final TreeSet<RecoverPartition> set2 = new TreeSet<SamsaMasterBroker.RecoverPartition>();
        for (final List<RecoverPartition> list : forks) {
            set2.addAll(list);
        }
        Assert.assertEquals(set1, set2);
    }

    private RemotingServer mockSlave;

    static class MessageInfo {
        long msgId;

        long offset;

        int partition;

        public MessageInfo(final long msgId, final long offset, final int partition) {
            super();
            this.msgId = msgId;
            this.offset = offset;
            this.partition = partition;
        }

        @Override
        public String toString() {
            return ((((("MessageInfo [msgId=" + (this.msgId)) + ", offset=") + (this.offset)) + ", partition=") + (this.partition)) + "]";
        }
    }

    @Test
    public void testRecover() throws Exception {
        this.mockSlaveServer();
        final MetaConfig metaConfig = new MetaConfig();
        metaConfig.setDataPath(this.getDataPath());
        final String topic = "SamsaMasterBrokerUnitTest";
        metaConfig.getTopics().add(topic);
        metaConfig.setNumPartitions(5);
        metaConfig.setMaxSegmentSize((1024 * 1024));
        final MetaMorphosisBroker metaBroker = new MetaMorphosisBroker(metaConfig);
        final IdWorker idWorker = metaBroker.getIdWorker();
        final byte[] data = new byte[1024];
        // ?????recover??offset???
        final List<SamsaMasterBrokerUnitTest.MessageInfo> allMsgs = new ArrayList<SamsaMasterBrokerUnitTest.MessageInfo>();
        final Random random = new Random();
        // ????????
        for (int i = 0; i < 20000; i++) {
            // ??5?????????????
            final int partition = i % 4;
            final int step = i;
            final MessageStore store = metaBroker.getStoreManager().getOrCreateMessageStore(topic, partition);
            final long msgId = idWorker.nextId();
            store.append(msgId, new PutCommand(topic, partition, data, null, 0, 0), new AppendCallback() {
                @Override
                public void appendComplete(final Location location) {
                    // ???????1044?????location??offset??????????????
                    allMsgs.add(new SamsaMasterBrokerUnitTest.MessageInfo(msgId, ((location.getOffset()) + 1044), partition));
                }
            });
            store.flush();
        }
        // ?????????????????20??
        final List<SamsaMasterBrokerUnitTest.MessageInfo> offsetInfos = new ArrayList<SamsaMasterBrokerUnitTest.MessageInfo>();
        for (int i = 0; i < 20; i++) {
            offsetInfos.add(allMsgs.get(random.nextInt(allMsgs.size())));
        }
        // ???????????offset???????
        this.mockConsumersOffset(topic, metaBroker, offsetInfos);
        System.out.println("Add messages done");
        try {
            final Properties props = new Properties();
            props.setProperty("recoverOffset", "true");
            props.setProperty("slave", "localhost:8121");
            props.setProperty("recoverParallel", "false");
            Assert.assertTrue(metaBroker.getBrokerZooKeeper().getZkConfig().zkEnable);
            this.broker.init(metaBroker, props);
            // recover??????????????zk
            Assert.assertFalse(metaBroker.getBrokerZooKeeper().getZkConfig().zkEnable);
            // ??????meta broker
            metaBroker.start();
            // ???recover
            this.broker.start();
            // ???????????????
            final String consumerId = "SamsaMasterBrokerUnitTest";
            final int brokerId = metaBroker.getMetaConfig().getBrokerId();
            // ????consumer???????????recover
            final String consumersPath = metaBroker.getBrokerZooKeeper().getMetaZookeeper().consumersPath;
            final ZkClient zkClient = metaBroker.getBrokerZooKeeper().getZkClient();
            int consumerIdCounter = 0;
            for (final SamsaMasterBrokerUnitTest.MessageInfo msgInfo : offsetInfos) {
                final int consumerIndex = consumerIdCounter++;
                final String offsetPath = ((((((((consumersPath + "/") + consumerId) + consumerIndex) + "/offsets/") + topic) + "/") + brokerId) + "-") + (msgInfo.partition);
                Assert.assertTrue(zkClient.exists(offsetPath));
                final String dataStr = ZkUtils.readDataMaybeNull(zkClient, offsetPath);
                Assert.assertNotNull(dataStr);
                final OffsetInfo offsetInfo = SamsaMasterBroker.readOffsetInfo(offsetPath, dataStr);
                System.out.println(((msgInfo + "    ") + dataStr));
                Assert.assertEquals(msgInfo.msgId, offsetInfo.msgId);
                Assert.assertEquals(msgInfo.offset, offsetInfo.offset);
            }
            // ??????????????0
            final String offsetPath = (((((((consumersPath + "/") + consumerId) + "/offsets/") + topic) + "/") + brokerId) + "-") + 4;
            Assert.assertTrue(zkClient.exists(offsetPath));
            final String dataStr = ZkUtils.readDataMaybeNull(zkClient, offsetPath);
            Assert.assertNotNull(dataStr);
            final OffsetInfo offsetInfo = SamsaMasterBroker.readOffsetInfo(offsetPath, dataStr);
            Assert.assertEquals((-1), offsetInfo.msgId);
            Assert.assertEquals(0, offsetInfo.offset);
        } finally {
            if (metaBroker != null) {
                metaBroker.stop();
            }
            this.broker.stop();
            this.stopMockSlave();
        }
    }
}

