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
package com.hazelcast.collection.impl.set;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ISet;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public abstract class SetAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected IAtomicLong atomicLong;

    private ISet<String> set;

    private Config config;

    private HazelcastInstance local;

    private HazelcastInstance target;

    // ======================== isEmpty test ============================
    @Test
    public void testIsEmpty_whenEmpty() {
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testIsEmpty_whenNotEmpty() {
        set.add("item1");
        Assert.assertFalse(set.isEmpty());
    }

    // ======================== add - addAll test =======================
    @Test
    public void testAdd() {
        for (int i = 1; i <= 10; i++) {
            Assert.assertTrue(set.add(("item" + i)));
        }
        Assert.assertEquals(10, set.size());
    }

    @Test
    public void testAdd_withMaxCapacity() {
        String name = HazelcastTestSupport.randomNameOwnedBy(target, "testAdd_withMaxCapacity");
        set = local.getSet(name);
        set.add("item");
        for (int i = 1; i <= 10; i++) {
            Assert.assertFalse(set.add(("item" + i)));
        }
        Assert.assertEquals(1, set.size());
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        set.add(null);
    }

    @Test
    public void testAddAll_Basic() {
        Set<String> added = new HashSet<String>();
        added.add("item1");
        added.add("item2");
        set.addAll(added);
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void testAddAll_whenAllElementsSame() {
        Set<String> added = new HashSet<String>();
        for (int i = 1; i <= 10; i++) {
            added.add("item");
        }
        set.addAll(added);
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testAddAll_whenCollectionContainsNull() {
        Set<String> added = new HashSet<String>();
        added.add("item1");
        added.add(null);
        try {
            Assert.assertFalse(set.addAll(added));
        } catch (NullPointerException e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(0, set.size());
    }

    // ======================== remove  - removeAll ==========================
    @Test
    public void testRemoveBasic() {
        set.add("item1");
        Assert.assertTrue(set.remove("item1"));
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testRemove_whenElementNotExist() {
        set.add("item1");
        Assert.assertFalse(set.remove("notExist"));
        Assert.assertEquals(1, set.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_whenArgumentNull() {
        set.remove(null);
    }

    @Test
    public void testRemoveAll() {
        Set<String> removed = new HashSet<String>();
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
            removed.add(("item" + i));
        }
        set.removeAll(removed);
        Assert.assertEquals(0, set.size());
    }

    // ======================== iterator ==========================
    @Test
    public void testIterator() {
        set.add("item");
        Iterator iterator = set.iterator();
        Assert.assertEquals("item", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveThrowsUnsupportedOperationException() {
        set.add("item");
        Iterator iterator = set.iterator();
        iterator.next();
        iterator.remove();
    }

    // ======================== clear ==========================
    @Test
    public void testClear() {
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
        }
        Assert.assertEquals(10, set.size());
        set.clear();
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testClear_whenSetEmpty() {
        set.clear();
        Assert.assertEquals(0, set.size());
    }

    // ======================== retainAll ==========================
    @Test
    public void testRetainAll_whenArgumentEmptyCollection() {
        Set<String> retained = Collections.emptySet();
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
        }
        set.retainAll(retained);
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testRetainAll_whenArgumentHasSameElements() {
        Set<String> retained = new HashSet<String>();
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
            retained.add(("item" + i));
        }
        set.retainAll(retained);
        Assert.assertEquals(10, set.size());
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void testRetainAll_whenCollectionNull() {
        set.retainAll(null);
    }

    // ======================== contains - containsAll ==========================
    @Test
    public void testContains() {
        set.add("item1");
        HazelcastTestSupport.assertContains(set, "item1");
    }

    @Test
    public void testContains_whenEmpty() {
        HazelcastTestSupport.assertNotContains(set, "notExist");
    }

    @Test
    public void testContains_whenNotContains() {
        set.add("item1");
        HazelcastTestSupport.assertNotContains(set, "notExist");
    }

    @Test
    public void testContainsAll() {
        Set<String> contains = new HashSet<String>();
        contains.add("item1");
        contains.add("item2");
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
            contains.add(("item" + i));
        }
        HazelcastTestSupport.assertContainsAll(set, contains);
    }

    @Test
    public void testContainsAll_whenSetNotContains() {
        Set<String> contains = new HashSet<String>();
        contains.add("item1");
        contains.add("item100");
        for (int i = 1; i <= 10; i++) {
            set.add(("item" + i));
            contains.add(("item" + i));
        }
        HazelcastTestSupport.assertNotContainsAll(set, contains);
    }

    @Test
    public void testNameBasedAffinity() {
        // creates more instances to increase a chance 'foo' will not be owned by
        // the same member as 'foo@1'
        newInstances(config);
        newInstances(config);
        int numberOfSets = 100;
        ISet[] localSets = new ISet[numberOfSets];
        ISet[] targetSets = new ISet[numberOfSets];
        for (int i = 0; i < numberOfSets; i++) {
            String name = ((HazelcastTestSupport.randomName()) + "@") + i;
            localSets[i] = local.getSet(name);
            targetSets[i] = target.getSet(name);
        }
        for (final ISet set : localSets) {
            TransactionalTask task = new TransactionalTask() {
                @Override
                public Object execute(TransactionalTaskContext context) throws TransactionException {
                    TransactionalSet<String> txSet = context.getSet(set.getName());
                    txSet.add("Hello");
                    return null;
                }
            };
            local.executeTransaction(task);
        }
        for (ISet set : localSets) {
            Assert.assertEquals(1, set.size());
        }
    }
}

