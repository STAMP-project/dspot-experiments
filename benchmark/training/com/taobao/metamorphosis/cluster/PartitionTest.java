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
package com.taobao.metamorphosis.cluster;


import Partition.RandomPartiton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-7-22 ????04:30:28
 */
public class PartitionTest {
    @Test
    public void testCompareTo() {
        Assert.assertTrue(((new Partition(2, 0).compareTo(new Partition(2, 0))) == 0));
        Assert.assertTrue(((new Partition(2, 1).compareTo(new Partition(2, 0))) == 1));
        Assert.assertTrue(((new Partition(2, 0).compareTo(new Partition(2, 1))) == (-1)));
        Assert.assertTrue(((new Partition(3, 0).compareTo(new Partition(2, 0))) == 1));
        Assert.assertTrue(((new Partition(2, 0).compareTo(new Partition(3, 0))) == (-1)));
        Assert.assertTrue(((new Partition(3, 0).compareTo(new Partition(2, 5))) == 1));
        Assert.assertTrue(((new Partition(2, 5).compareTo(new Partition(3, 0))) == (-1)));
        Assert.assertTrue(((new Partition(2, 5).compareTo(new Partition(10, 5))) == (-1)));
        Assert.assertTrue(((new Partition(10, 5).compareTo(new Partition(2, 5))) == 1));
        Assert.assertTrue(((new Partition(10, 0).compareTo(new Partition(2, 5))) == 1));
        Assert.assertTrue(((new Partition(2, 5).compareTo(new Partition(10, 0))) == (-1)));
        Assert.assertTrue(((new Partition(102, 15).compareTo(new Partition(203, 2))) == (-1)));
        Assert.assertTrue(((new Partition(203, 2).compareTo(new Partition(102, 15))) == 1));
    }

    @Test
    public void testCompareTo_order() {
        final List<Partition> partitions = new ArrayList<Partition>();
        for (int i = 21; i >= 0; i--) {
            for (int j = 14; j >= 0; j--) {
                partitions.add(new Partition(i, j));
            }
        }
        Collections.shuffle(partitions);
        System.out.println(partitions);
        Collections.sort(partitions);
        System.out.println(partitions);
        for (int i = 0; i < (partitions.size()); i++) {
            if (i == ((partitions.size()) - 1)) {
                return;
            }
            Assert.assertTrue(((partitions.get(i).compareTo(partitions.get((i + 1)))) == (-1)));
            Assert.assertTrue(((partitions.get(i).getBrokerId()) <= (partitions.get((i + 1)).getBrokerId())));
            if ((partitions.get(i).getBrokerId()) == (partitions.get((i + 1)).getBrokerId())) {
                Assert.assertTrue(((partitions.get(i).getPartition()) < (partitions.get((i + 1)).getPartition())));
            }
        }
    }

    @Test
    public void testNewRandomPartitonByString() {
        final Partition partition = new Partition("-1--1");
        Assert.assertEquals(RandomPartiton, partition);
        Assert.assertEquals(RandomPartiton.getBrokerId(), partition.getBrokerId());
        Assert.assertEquals(RandomPartiton.getPartition(), partition.getPartition());
    }

    @Test
    public void testAckRollbackReset() {
        final Partition partition = new Partition("0-0");
        Assert.assertFalse(partition.isRollback());
        Assert.assertTrue(partition.isAcked());
        Assert.assertTrue(partition.isAutoAck());
        partition.setAutoAck(false);
        Assert.assertFalse(partition.isRollback());
        Assert.assertFalse(partition.isAcked());
        Assert.assertFalse(partition.isAutoAck());
        partition.ack();
        Assert.assertFalse(partition.isRollback());
        Assert.assertTrue(partition.isAcked());
        Assert.assertFalse(partition.isAutoAck());
        try {
            partition.rollback();
            Assert.fail();
        } catch (final IllegalStateException e) {
            Assert.assertEquals("Could not rollback acked partition", e.getMessage());
        }
        partition.reset();
        Assert.assertFalse(partition.isRollback());
        Assert.assertFalse(partition.isAcked());
        Assert.assertFalse(partition.isAutoAck());
        partition.rollback();
        Assert.assertTrue(partition.isRollback());
        Assert.assertFalse(partition.isAcked());
        Assert.assertFalse(partition.isAutoAck());
        try {
            partition.ack();
            Assert.fail();
        } catch (final IllegalStateException e) {
            Assert.assertEquals("Could not ack rollbacked partition", e.getMessage());
        }
        partition.setAutoAck(true);
        Assert.assertFalse(partition.isRollback());
        Assert.assertTrue(partition.isAcked());
        Assert.assertTrue(partition.isAutoAck());
    }
}

