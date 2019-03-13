/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static ImplicitLinkedHashSet.INVALID_INDEX;


/**
 * A unit test for ImplicitLinkedHashSet.
 */
public class ImplicitLinkedHashSetTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    static final class TestElement implements ImplicitLinkedHashSet.Element {
        private int prev = INVALID_INDEX;

        private int next = INVALID_INDEX;

        private final int val;

        TestElement(int val) {
            this.val = val;
        }

        @Override
        public int prev() {
            return prev;
        }

        @Override
        public void setPrev(int prev) {
            this.prev = prev;
        }

        @Override
        public int next() {
            return next;
        }

        @Override
        public void setNext(int next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((o.getClass()) != (ImplicitLinkedHashSetTest.TestElement.class)))
                return false;

            ImplicitLinkedHashSetTest.TestElement that = ((ImplicitLinkedHashSetTest.TestElement) (o));
            return (val) == (that.val);
        }

        @Override
        public String toString() {
            return ("TestElement(" + (val)) + ")";
        }

        @Override
        public int hashCode() {
            return val;
        }
    }

    @Test
    public void testNullForbidden() {
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet();
        Assert.assertFalse(multiSet.add(null));
    }

    @Test
    public void testInsertDelete() {
        ImplicitLinkedHashSet<ImplicitLinkedHashSetTest.TestElement> set = new ImplicitLinkedHashSet(100);
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(1)));
        ImplicitLinkedHashSetTest.TestElement second = new ImplicitLinkedHashSetTest.TestElement(2);
        Assert.assertTrue(set.add(second));
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(3)));
        Assert.assertFalse(set.add(new ImplicitLinkedHashSetTest.TestElement(3)));
        Assert.assertEquals(3, set.size());
        Assert.assertTrue(set.contains(new ImplicitLinkedHashSetTest.TestElement(1)));
        Assert.assertFalse(set.contains(new ImplicitLinkedHashSetTest.TestElement(4)));
        ImplicitLinkedHashSetTest.TestElement secondAgain = set.find(new ImplicitLinkedHashSetTest.TestElement(2));
        Assert.assertTrue((second == secondAgain));
        Assert.assertTrue(set.remove(new ImplicitLinkedHashSetTest.TestElement(1)));
        Assert.assertFalse(set.remove(new ImplicitLinkedHashSetTest.TestElement(1)));
        Assert.assertEquals(2, set.size());
        set.clear();
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testTraversal() {
        ImplicitLinkedHashSet<ImplicitLinkedHashSetTest.TestElement> set = new ImplicitLinkedHashSet();
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator());
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(2)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2);
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(1)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2, 1);
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(100)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2, 1, 100);
        Assert.assertTrue(set.remove(new ImplicitLinkedHashSetTest.TestElement(1)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2, 100);
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(1)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2, 100, 1);
        Iterator<ImplicitLinkedHashSetTest.TestElement> iter = set.iterator();
        iter.next();
        iter.next();
        iter.remove();
        iter.next();
        Assert.assertFalse(iter.hasNext());
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 2, 1);
        List<ImplicitLinkedHashSetTest.TestElement> list = new ArrayList<>();
        list.add(new ImplicitLinkedHashSetTest.TestElement(1));
        list.add(new ImplicitLinkedHashSetTest.TestElement(2));
        Assert.assertTrue(set.removeAll(list));
        Assert.assertFalse(set.removeAll(list));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator());
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testCollisions() {
        ImplicitLinkedHashSet<ImplicitLinkedHashSetTest.TestElement> set = new ImplicitLinkedHashSet(5);
        Assert.assertEquals(11, set.numSlots());
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(11)));
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(0)));
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(22)));
        Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(33)));
        Assert.assertEquals(11, set.numSlots());
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 11, 0, 22, 33);
        Assert.assertTrue(set.remove(new ImplicitLinkedHashSetTest.TestElement(22)));
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 11, 0, 33);
        Assert.assertEquals(3, set.size());
        Assert.assertFalse(set.isEmpty());
    }

    @Test
    public void testEnlargement() {
        ImplicitLinkedHashSet<ImplicitLinkedHashSetTest.TestElement> set = new ImplicitLinkedHashSet(5);
        Assert.assertEquals(11, set.numSlots());
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(set.add(new ImplicitLinkedHashSetTest.TestElement(i)));
        }
        Assert.assertEquals(23, set.numSlots());
        Assert.assertEquals(6, set.size());
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 0, 1, 2, 3, 4, 5);
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(("Failed to find element " + i), set.contains(new ImplicitLinkedHashSetTest.TestElement(i)));
        }
        set.remove(new ImplicitLinkedHashSetTest.TestElement(3));
        Assert.assertEquals(23, set.numSlots());
        Assert.assertEquals(5, set.size());
        ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), 0, 1, 2, 4, 5);
    }

    @Test
    public void testManyInsertsAndDeletes() {
        Random random = new Random(123);
        LinkedHashSet<Integer> existing = new LinkedHashSet<>();
        ImplicitLinkedHashSet<ImplicitLinkedHashSetTest.TestElement> set = new ImplicitLinkedHashSet();
        for (int i = 0; i < 100; i++) {
            addRandomElement(random, existing, set);
            addRandomElement(random, existing, set);
            addRandomElement(random, existing, set);
            removeRandomElement(random, existing, set);
            ImplicitLinkedHashSetTest.expectTraversal(set.iterator(), existing.iterator());
        }
    }
}

