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
package com.taobao.metamorphosis.metaslave;


import com.taobao.metamorphosis.cluster.json.TopicBroker;
import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.utils.MetaZookeeper;
import com.taobao.metamorphosis.utils.ZkUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-7-1 ????05:08:33
 */
public class SubscribeHandlerTest {
    private MetaConfig metaConfig;

    private MetaMorphosisBroker broker;

    private SubscribeHandler subscribeHandler;

    private MetaZookeeper metaZookeeper;

    private final int brokerId = 999;

    @Test
    public void testStart_NoTopicsOfMasterInZk() {
        Assert.assertEquals(0, this.subscribeHandler.getSlaveZooKeeper().getPartitionsForTopicsFromMaster().size());
        this.subscribeHandler.start();
        Assert.assertFalse(this.subscribeHandler.isStarted());
    }

    @Test
    public void testStart_NoTopicsOfMasterInZk_thenMasterRegister() throws Exception {
        Assert.assertEquals(0, this.subscribeHandler.getSlaveZooKeeper().getPartitionsForTopicsFromMaster().size());
        this.subscribeHandler.start();
        Assert.assertFalse(this.subscribeHandler.isStarted());
        ZkUtils.createEphemeralPath(this.getZkClient(), this.metaZookeeper.brokerIdsPathOf(this.brokerId, (-1)), "meta://1.1.1.1:222");
        ZkUtils.createEphemeralPath(this.getZkClient(), this.metaZookeeper.brokerTopicsPathOf("topictest", this.brokerId, (-1)), "2");
        Thread.sleep(5000);
    }

    @Test
    public void testStart_MasterNoStarted() throws Exception {
        ZkUtils.deletePath(this.getZkClient(), this.metaZookeeper.brokerIdsPathOf(this.brokerId, (-1)));
        ZkUtils.deletePath(this.getZkClient(), this.metaZookeeper.brokerTopicsPathOf("topictest", false, this.brokerId, (-1)));
        ZkUtils.createEphemeralPath(this.getZkClient(), this.metaZookeeper.brokerIdsPathOf(this.brokerId, (-1)), "meta://1.1.1.1:222");
        ZkUtils.createEphemeralPath(this.getZkClient(), this.metaZookeeper.brokerTopicsPathOf("topictest", false, this.brokerId, (-1)), new TopicBroker(2, null).toJson());
        Assert.assertTrue(((this.subscribeHandler.getSlaveZooKeeper().getPartitionsForTopicsFromMaster().size()) > 0));
        this.subscribeHandler.start();
        Assert.assertFalse(this.subscribeHandler.isStarted());
    }
}

