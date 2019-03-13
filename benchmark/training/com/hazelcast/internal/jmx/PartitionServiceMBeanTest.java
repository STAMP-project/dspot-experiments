/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.jmx;


import com.hazelcast.monitor.impl.MemberPartitionStateImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionServiceMBeanTest extends HazelcastTestSupport {
    public static final String TYPE_NAME = "HazelcastInstance.PartitionServiceMBean";

    private MBeanDataHolder holder;

    private String hzName;

    @Test
    public void testPartitionCount() throws Exception {
        Assert.assertEquals(MemberPartitionStateImpl.DEFAULT_PARTITION_COUNT, ((int) (getIntAttribute("partitionCount"))));
    }

    @Test
    public void testActivePartitionCount() throws Exception {
        HazelcastTestSupport.warmUpPartitions(holder.getHz());
        Assert.assertEquals(MemberPartitionStateImpl.DEFAULT_PARTITION_COUNT, ((int) (getIntAttribute("activePartitionCount"))));
    }

    @Test
    public void testClusterSafe() throws Exception {
        Assert.assertTrue(((Boolean) (holder.getMBeanAttribute(PartitionServiceMBeanTest.TYPE_NAME, hzName, "isClusterSafe"))));
    }

    @Test
    public void testLocalMemberSafe() throws Exception {
        Assert.assertTrue(((Boolean) (holder.getMBeanAttribute(PartitionServiceMBeanTest.TYPE_NAME, hzName, "isLocalMemberSafe"))));
    }
}

