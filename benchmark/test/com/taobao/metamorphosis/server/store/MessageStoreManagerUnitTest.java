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
package com.taobao.metamorphosis.server.store;


import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.server.utils.TopicConfig;
import com.taobao.metamorphosis.utils.IdWorker;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class MessageStoreManagerUnitTest {
    private MessageStoreManager messageStoreManager;

    private MetaConfig metaConfig;

    @Test
    public void testGetOrCreateMessageStore() throws Exception {
        final String topic = "MessageStoreManagerUnitTest";
        final int partition = 0;
        final File dir = new File((((((this.metaConfig.getDataPath()) + (File.separator)) + topic) + "-") + partition));
        Assert.assertFalse(dir.exists());
        Assert.assertNull(this.messageStoreManager.getMessageStore(topic, partition));
        final MessageStore store1 = this.messageStoreManager.getOrCreateMessageStore(topic, partition);
        Assert.assertTrue(dir.exists());
        final MessageStore store2 = this.messageStoreManager.getMessageStore(topic, partition);
        Assert.assertSame(store1, store2);
    }

    @Test
    public void testGetOrCreateMessageStore_withOffset() throws Exception {
        final String topic = "MessageStoreManagerUnitTest";
        final int partition = 0;
        final File dir = new File((((((this.metaConfig.getDataPath()) + (File.separator)) + topic) + "-") + partition));
        Assert.assertFalse(dir.exists());
        Assert.assertNull(this.messageStoreManager.getMessageStore(topic, partition));
        final MessageStore store1 = this.messageStoreManager.getOrCreateMessageStore(topic, partition, 2048);
        Assert.assertTrue(dir.exists());
        final MessageStore store2 = this.messageStoreManager.getMessageStore(topic, partition);
        Assert.assertSame(store1, store2);
        Assert.assertEquals(2048, store2.getMinOffset());
        final IdWorker idWorker = new IdWorker(0);
        final PutCommand cmd1 = new PutCommand(topic, partition, "hello".getBytes(), null, 0, 0);
        final PutCommand cmd2 = new PutCommand(topic, partition, "world".getBytes(), null, 0, 0);
        store1.append(idWorker.nextId(), cmd1, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(2048, location.getOffset());
            }
        });
        store1.flush();// flush???

        final long size = store1.getSegments().last().size();
        store1.append(idWorker.nextId(), cmd2, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals((2048 + size), location.getOffset());
            }
        });
        store1.flush();
        Assert.assertEquals(1, dir.listFiles().length);
        Assert.assertTrue(dir.listFiles()[0].exists());
        Assert.assertEquals(store1.nameFromOffset(2048), dir.listFiles()[0].getName());
        store1.close();
        for (final File file : dir.listFiles()) {
            file.delete();
        }
    }

    @Test
    public void testChooseRandomPartition() {
        Assert.assertEquals(0, this.messageStoreManager.chooseRandomPartition("MessageStoreManagerUnitTest"));
        Assert.assertEquals(0, this.messageStoreManager.chooseRandomPartition("MessageStoreManagerUnitTest"));
        Assert.assertEquals(0, this.messageStoreManager.chooseRandomPartition("MessageStoreManagerUnitTest"));
        this.metaConfig.setNumPartitions(10);
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(((this.messageStoreManager.chooseRandomPartition("MessageStoreManagerUnitTest")) < 10));
        }
    }

    @Test
    public void testGetNumPartitions() {
        Assert.assertEquals(1, this.messageStoreManager.getNumPartitions("MessageStoreManagerUnitTest"));
        Assert.assertEquals(1, this.messageStoreManager.getNumPartitions("MessageStoreManagerUnitTest"));
        final TopicConfig topicConfig = new TopicConfig("MessageStoreManagerUnitTest", this.metaConfig);
        topicConfig.setNumPartitions(9999);
        this.metaConfig.getTopicConfigMap().put("MessageStoreManagerUnitTest", topicConfig);
        Assert.assertEquals(9999, this.messageStoreManager.getNumPartitions("MessageStoreManagerUnitTest"));
        Assert.assertEquals(9999, this.messageStoreManager.getNumPartitions("MessageStoreManagerUnitTest"));
    }

    @Test
    public void testInit() throws Exception {
        this.testInit0(false);
    }

    @Test
    public void testInitInParallel() throws Exception {
        this.testInit0(true);
    }

    @Test
    public void testRunDeletePolicy() throws Exception {
        this.metaConfig.setMaxSegmentSize(1024);
        this.messageStoreManager.init();
        final String topic = "MessageStoreManagerUnitTest";
        final int partition = 0;
        final File dir = new File((((((this.metaConfig.getDataPath()) + (File.separator)) + topic) + "-") + partition));
        Assert.assertFalse(dir.exists());
        Assert.assertNull(this.messageStoreManager.getMessageStore(topic, partition));
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore(topic, partition);
        final IdWorker idWorker = new IdWorker(0);
        final byte[] data = new byte[1024];
        final PutCommand cmd1 = new PutCommand(topic, partition, data, null, 0, 0);
        final PutCommand cmd2 = new PutCommand(topic, partition, data, null, 0, 0);
        store.append(idWorker.nextId(), cmd1, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(0, location.getOffset());
            }
        });
        store.flush();// flush???

        store.append(idWorker.nextId(), cmd2, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(1044, location.getOffset());
            }
        });
        store.flush();
        System.out.println(store.getSegmentInfos().size());
        Assert.assertFalse(store.getSegmentInfos().isEmpty());
        Assert.assertEquals(3, store.getSegmentInfos().size());
        // Wait for 10 seconds
        Thread.sleep(15000);
        System.out.println(store.getSegmentInfos().size());
        Assert.assertEquals(1, store.getSegmentInfos().size());
    }

    @Test
    public void testIsLegalTopicWildChar() throws Exception {
        this.metaConfig.getTopics().add("TBCTU-*");
        this.tearDown();
        this.messageStoreManager = new MessageStoreManager(this.metaConfig, null);
        Assert.assertTrue(this.messageStoreManager.isLegalTopic("TBCTU-test"));
        Assert.assertTrue(this.messageStoreManager.isLegalTopic("TBCTU-2343"));
        Assert.assertTrue(this.messageStoreManager.isLegalTopic("TBCTU-TBCTU"));
        Assert.assertTrue(this.messageStoreManager.isLegalTopic("TBCTU-"));
        Assert.assertFalse(this.messageStoreManager.isLegalTopic("a-TBCTU-"));
        Assert.assertFalse(this.messageStoreManager.isLegalTopic("TCCTU-test"));
    }
}

