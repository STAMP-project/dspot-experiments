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
import com.hazelcast.core.IList;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientListTest extends HazelcastTestSupport {
    private TestHazelcastFactory hazelcastFactory;

    private IList<String> list;

    @Test
    public void testAddAll() {
        List<String> l = new ArrayList<String>();
        l.add("item1");
        l.add("item2");
        Assert.assertTrue(list.addAll(l));
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.addAll(1, l));
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("item1", list.get(0));
        Assert.assertEquals("item1", list.get(1));
        Assert.assertEquals("item2", list.get(2));
        Assert.assertEquals("item2", list.get(3));
    }

    @Test
    public void testAddSetRemove() {
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item2"));
        list.add(0, "item3");
        Assert.assertEquals(3, list.size());
        String element = list.set(2, "item4");
        Assert.assertEquals("item2", element);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("item3", list.get(0));
        Assert.assertEquals("item1", list.get(1));
        Assert.assertEquals("item4", list.get(2));
        Assert.assertFalse(list.remove("item2"));
        Assert.assertTrue(list.remove("item3"));
        element = list.remove(1);
        Assert.assertEquals("item4", element);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("item1", list.get(0));
    }

    @Test
    public void testIndexOf() {
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item2"));
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item4"));
        Assert.assertEquals((-1), list.indexOf("item5"));
        Assert.assertEquals(0, list.indexOf("item1"));
        Assert.assertEquals((-1), list.lastIndexOf("item6"));
        Assert.assertEquals(2, list.lastIndexOf("item1"));
    }

    @Test
    public void testIterator() {
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item2"));
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item4"));
        Iterator iter = list.iterator();
        Assert.assertEquals("item1", iter.next());
        Assert.assertEquals("item2", iter.next());
        Assert.assertEquals("item1", iter.next());
        Assert.assertEquals("item4", iter.next());
        Assert.assertFalse(iter.hasNext());
        ListIterator listIterator = list.listIterator(2);
        Assert.assertEquals("item1", listIterator.next());
        Assert.assertEquals("item4", listIterator.next());
        Assert.assertFalse(listIterator.hasNext());
        List l = list.subList(1, 3);
        Assert.assertEquals(2, l.size());
        Assert.assertEquals("item2", l.get(0));
        Assert.assertEquals("item1", l.get(1));
    }

    @Test
    public void testContains() {
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item2"));
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item4"));
        assertNotContains(list, "item3");
        assertContains(list, "item2");
        List<String> l = new ArrayList<String>();
        l.add("item4");
        l.add("item3");
        assertNotContainsAll(list, l);
        Assert.assertTrue(list.add("item3"));
        assertContainsAll(list, l);
    }

    @Test
    public void removeRetainAll() {
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item2"));
        Assert.assertTrue(list.add("item1"));
        Assert.assertTrue(list.add("item4"));
        List<String> l = new ArrayList<String>();
        l.add("item4");
        l.add("item3");
        Assert.assertTrue(list.removeAll(l));
        Assert.assertEquals(3, list.size());
        Assert.assertFalse(list.removeAll(l));
        Assert.assertEquals(3, list.size());
        l.clear();
        l.add("item1");
        l.add("item2");
        Assert.assertFalse(list.retainAll(l));
        Assert.assertEquals(3, list.size());
        l.clear();
        Assert.assertTrue(list.retainAll(l));
        Assert.assertEquals(0, list.size());
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
        String registrationId = list.addItemListener(listener, true);
        new Thread() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    list.add(("item" + i));
                }
                list.add("done");
            }
        }.start();
        Assert.assertTrue(latch.await(20, TimeUnit.SECONDS));
        list.removeItemListener(registrationId);
    }

    @Test
    public void testIsEmpty_whenEmpty() {
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testIsEmpty_whenNotEmpty() {
        list.add("item");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1, list.size());
    }
}

