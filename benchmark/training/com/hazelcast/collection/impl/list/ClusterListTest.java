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
package com.hazelcast.collection.impl.list;


import com.hazelcast.config.Config;
import com.hazelcast.config.ListConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterListTest extends HazelcastTestSupport {
    @Test
    public void testAddRemove() {
        final String name = HazelcastTestSupport.randomString();
        final int count = 100;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance[] instances = factory.newInstances();
        IList<String> list1 = instances[0].getList(name);
        IList<String> list2 = instances[1].getList(name);
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(list1.add(("item" + i)));
            Assert.assertTrue(list2.add(("item" + i)));
        }
        HazelcastTestSupport.assertSizeEventually(200, list1);
        HazelcastTestSupport.assertSizeEventually(200, list2);
        Assert.assertEquals("item0", list1.get(0));
        Assert.assertEquals("item0", list2.get(0));
        Assert.assertEquals("item99", list1.get(199));
        Assert.assertEquals("item99", list2.get(199));
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(("item" + i), list1.remove(i));
        }
        HazelcastTestSupport.assertSizeEventually(100, list2);
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(list2.remove(("item" + i)));
        }
        HazelcastTestSupport.assertSizeEventually(0, list1);
        HazelcastTestSupport.assertSizeEventually(0, list2);
    }

    @Test
    public void testAddContainsRemoveRetainsAll() {
        final String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        IList<String> list1 = instance1.getList(name);
        IList<String> list2 = instance2.getList(name);
        List<String> listTest1 = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            listTest1.add(("item" + i));
        }
        Assert.assertTrue(list1.addAll(listTest1));
        HazelcastTestSupport.assertSizeEventually(100, list2);
        List<String> listTest2 = new ArrayList<String>();
        for (int i = 30; i < 40; i++) {
            listTest2.add(("item" + i));
        }
        HazelcastTestSupport.assertContainsAll(list2, listTest2);
        Assert.assertTrue(list2.retainAll(listTest2));
        HazelcastTestSupport.assertSizeEventually(10, list1);
        Assert.assertTrue(list1.removeAll(listTest2));
        HazelcastTestSupport.assertSizeEventually(0, list1);
    }

    @Test
    public void testShutdown() {
        final String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance[] instances = factory.newInstances();
        IList<String> list1 = instances[0].getList(name);
        IList<String> list2 = instances[1].getList(name);
        HazelcastTestSupport.warmUpPartitions(instances);
        for (int i = 0; i < 50; i++) {
            list1.add(("item" + i));
        }
        instances[0].shutdown();
        HazelcastTestSupport.assertSizeEventually(50, list2);
        for (int i = 50; i < 100; i++) {
            list2.add(("item" + i));
        }
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(("item" + i), list2.remove(0));
        }
    }

    @Test
    public void testMigration() {
        Config config = new Config();
        final String name = HazelcastTestSupport.randomString();
        config.addListConfig(new ListConfig().setName(name).setBackupCount(1));
        final int insCount = 4;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(insCount);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IList<String> list = instance1.getList(name);
        for (int i = 0; i < 100; i++) {
            list.add(("item" + i));
        }
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        Assert.assertEquals(100, instance2.getList(name).size());
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        Assert.assertEquals(100, instance3.getList(name).size());
        instance1.shutdown();
        Assert.assertEquals(100, instance3.getList(name).size());
        list = instance2.getList(name);
        for (int i = 0; i < 100; i++) {
            list.add(("item-" + i));
        }
        instance2.shutdown();
        Assert.assertEquals(200, instance3.getList(name).size());
        instance1 = factory.newHazelcastInstance(config);
        Assert.assertEquals(200, instance1.getList(name).size());
        instance3.shutdown();
        Assert.assertEquals(200, instance1.getList(name).size());
    }

    @Test
    public void testMaxSize() {
        Config config = new Config();
        final String name = "defList";
        config.addListConfig(new ListConfig().setName(name).setBackupCount(1).setMaxSize(100));
        final int insCount = 2;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(insCount);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IList<String> list = instance1.getList(name);
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(list.add(("item" + i)));
        }
        Assert.assertFalse(list.add("item"));
        Assert.assertNotNull(list.remove(0));
        Assert.assertTrue(list.add("item"));
    }
}

