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
package com.taobao.metamorphosis.client.consumer.storage;


import com.taobao.metamorphosis.client.consumer.TopicPartitionRegInfo;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class LocalOffsetStorageUnitTest {
    private OffsetStorage offsetStorage;

    private File file;

    @Test
    public void testCommitLoad() throws Exception {
        final String group = "test-grp";
        final Partition partition = new Partition("0-1");
        Collection<TopicPartitionRegInfo> infoList = new ArrayList<TopicPartitionRegInfo>();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            infoList.add(new TopicPartitionRegInfo(topic, partition, i));
        }
        this.offsetStorage.commitOffset(group, infoList);
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = this.offsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
        OffsetStorage newOffsetStorage = new LocalOffsetStorage();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = newOffsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
        newOffsetStorage.close();
        Assert.assertTrue(this.file.exists());
        String content = this.readFile(this.file);
        Assert.assertFalse(content.contains("autoAck"));
        Assert.assertFalse(content.contains("acked"));
        Assert.assertFalse(content.contains("rollback"));
    }

    @Test
    public void testCommitCloseLoad() throws Exception {
        final String group = "test-grp";
        final Partition partition = new Partition("0-1");
        Collection<TopicPartitionRegInfo> infoList = new ArrayList<TopicPartitionRegInfo>();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            infoList.add(new TopicPartitionRegInfo(topic, partition, i));
        }
        this.offsetStorage.commitOffset(group, infoList);
        this.offsetStorage.close();
        this.offsetStorage = new LocalOffsetStorage();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = this.offsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
    }

    @Test
    public void testCommitLoadEmpty() throws Exception {
        final String group = "test-grp";
        final Partition partition = new Partition("0-1");
        Collection<TopicPartitionRegInfo> infoList = new ArrayList<TopicPartitionRegInfo>();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            infoList.add(new TopicPartitionRegInfo(topic, partition, i));
        }
        this.offsetStorage.commitOffset(group, infoList);
        this.offsetStorage.commitOffset(group, null);
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = this.offsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
        OffsetStorage newOffsetStorage = new LocalOffsetStorage();
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = newOffsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
        this.offsetStorage.commitOffset(group, Collections.EMPTY_LIST);
        for (int i = 0; i < 3; i++) {
            final String topic = "test" + (i + 1);
            TopicPartitionRegInfo info = newOffsetStorage.load(topic, "test-grp", partition);
            Assert.assertEquals(topic, info.getTopic());
            Assert.assertEquals(partition, info.getPartition());
            Assert.assertEquals(i, info.getOffset().get());
            info.getOffset().set(i);
            infoList.add(info);
        }
        newOffsetStorage.close();
    }

    @Test
    public void testBackwardCompatibility() throws IOException {
        OffsetStorage offsetStorage = new LocalOffsetStorage(ResourceUtils.getResourceAsFile("oldVersion_meta_offsets").getAbsolutePath());
        final String group = "test-grp";
        final String topic = "test-topic";
        Partition partition1 = new Partition("100-0");
        Partition partition2 = new Partition("101-0");
        TopicPartitionRegInfo info = offsetStorage.load(topic, group, partition1);
        Assert.assertEquals(topic, info.getTopic());
        Assert.assertEquals(partition1, info.getPartition());
        Assert.assertEquals(0, info.getOffset().get());
        // ???????????????????????,???????????
        Assert.assertEquals(true, info.getPartition().isAutoAck());
        Assert.assertEquals(true, info.getPartition().isAcked());
        Assert.assertEquals(false, info.getPartition().isRollback());
        TopicPartitionRegInfo info2 = offsetStorage.load(topic, group, partition2);
        Assert.assertEquals(topic, info2.getTopic());
        Assert.assertEquals(partition2, info2.getPartition());
        Assert.assertEquals(130835445, info2.getOffset().get());
        Assert.assertEquals(true, info2.getPartition().isAutoAck());
        Assert.assertEquals(true, info2.getPartition().isAcked());
        Assert.assertEquals(false, info2.getPartition().isRollback());
    }
}

