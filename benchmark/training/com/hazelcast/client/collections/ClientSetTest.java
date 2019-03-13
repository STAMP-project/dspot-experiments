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
package com.hazelcast.client.collections;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientSetTest extends HazelcastTestSupport {
    private TestHazelcastFactory hazelcastFactory;

    private ISet<String> set;

    @Test
    public void testAddAll() {
        List<String> l = new ArrayList<String>();
        l.add("item1");
        l.add("item2");
        Assert.assertTrue(set.addAll(l));
        Assert.assertEquals(2, set.size());
        Assert.assertFalse(set.addAll(l));
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void testAddRemove() {
        Assert.assertTrue(set.add("item1"));
        Assert.assertTrue(set.add("item2"));
        Assert.assertTrue(set.add("item3"));
        Assert.assertEquals(3, set.size());
        Assert.assertFalse(set.add("item3"));
        Assert.assertEquals(3, set.size());
        Assert.assertFalse(set.remove("item4"));
        Assert.assertTrue(set.remove("item3"));
    }

    @Test
    public void testIterator() {
        Assert.assertTrue(set.add("item1"));
        Assert.assertTrue(set.add("item2"));
        Assert.assertTrue(set.add("item3"));
        Assert.assertTrue(set.add("item4"));
        Iterator iter = set.iterator();
        Assert.assertTrue(((String) (iter.next())).startsWith("item"));
        Assert.assertTrue(((String) (iter.next())).startsWith("item"));
        Assert.assertTrue(((String) (iter.next())).startsWith("item"));
        Assert.assertTrue(((String) (iter.next())).startsWith("item"));
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testContains() {
        Assert.assertTrue(set.add("item1"));
        Assert.assertTrue(set.add("item2"));
        Assert.assertTrue(set.add("item3"));
        Assert.assertTrue(set.add("item4"));
        assertNotContains(set, "item5");
        assertContains(set, "item2");
        List<String> l = new ArrayList<String>();
        l.add("item6");
        l.add("item3");
        Assert.assertFalse(set.containsAll(l));
        Assert.assertTrue(set.add("item6"));
        assertContainsAll(set, l);
    }

    @Test
    public void removeRetainAll() {
        Assert.assertTrue(set.add("item1"));
        Assert.assertTrue(set.add("item2"));
        Assert.assertTrue(set.add("item3"));
        Assert.assertTrue(set.add("item4"));
        List<String> l = new ArrayList<String>();
        l.add("item4");
        l.add("item3");
        Assert.assertTrue(set.removeAll(l));
        Assert.assertEquals(2, set.size());
        Assert.assertFalse(set.removeAll(l));
        Assert.assertEquals(2, set.size());
        l.clear();
        l.add("item1");
        l.add("item2");
        Assert.assertFalse(set.retainAll(l));
        Assert.assertEquals(2, set.size());
        l.clear();
        Assert.assertTrue(set.retainAll(l));
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(6);
        ItemListener<String> listener = new ItemListener<String>() {
            public void itemAdded(ItemEvent<String> itemEvent) {
                latch.countDown();
            }

            public void itemRemoved(ItemEvent<String> item) {
            }
        };
        String registrationId = set.addItemListener(listener, true);
        new Thread() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    set.add(("item" + i));
                }
                set.add("done");
            }
        }.start();
        Assert.assertTrue(latch.await(20, TimeUnit.SECONDS));
        set.removeItemListener(registrationId);
    }

    @Test
    public void testIsEmpty_whenEmpty() {
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testIsEmpty_whenNotEmpty() {
        set.add("item");
        Assert.assertFalse(set.isEmpty());
        Assert.assertEquals(1, set.size());
    }
}

