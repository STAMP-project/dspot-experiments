/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class EnumSetTest {
    @Test
    public void emptyCreated() {
        EnumSet<EnumSetTest.L> set = EnumSet.noneOf(EnumSetTest.L.class);
        Assert.assertEquals("Size", 0, set.size());
        Assert.assertFalse("Iterator.hasNext must return false", set.iterator().hasNext());
        Assert.assertFalse("Does not contain E1", set.contains(EnumSetTest.L.E1));
        Assert.assertFalse("Does not contain E36", set.contains(EnumSetTest.L.E36));
        try {
            set.iterator().next();
            Assert.fail("Iterator expected to throw exception");
        } catch (NoSuchElementException e) {
            // OK
        }
    }

    @Test
    public void allItemsCreated() {
        EnumSet<EnumSetTest.L> set = EnumSet.allOf(EnumSetTest.L.class);
        Assert.assertEquals("Size", 36, set.size());
        Assert.assertTrue("Iterator.hasNext must return true", set.iterator().hasNext());
        Assert.assertEquals("Iterator.next must return E1", EnumSetTest.L.E1, set.iterator().next());
        Assert.assertTrue("Contains E1", set.contains(EnumSetTest.L.E1));
        Assert.assertTrue("Contains E36", set.contains(EnumSetTest.L.E36));
    }

    @Test
    public void itemAdded() {
        EnumSet<EnumSetTest.L> set = EnumSet.noneOf(EnumSetTest.L.class);
        Assert.assertTrue("Adding absent E2 must return true", set.add(EnumSetTest.L.E2));
        Assert.assertEquals("Iterator must return E2", EnumSetTest.L.E2, set.iterator().next());
        Assert.assertTrue("Set must contain E2", set.contains(EnumSetTest.L.E2));
        Assert.assertEquals("Size must be 1 after first addition", 1, set.size());
        Assert.assertFalse("Adding existing E2 must return false", set.add(EnumSetTest.L.E2));
        Assert.assertEquals("Iterator must return E2 after repeated addition", EnumSetTest.L.E2, set.iterator().next());
        Assert.assertTrue("Set must contain E2 after repeated addition", set.contains(EnumSetTest.L.E2));
        Assert.assertEquals("Size must be 1 after repeated addition", 1, set.size());
        Assert.assertTrue("Adding absent E4 must return true", set.add(EnumSetTest.L.E4));
        Assert.assertTrue("Set must contain E4", set.contains(EnumSetTest.L.E4));
        Assert.assertEquals("Size must be 2", 2, set.size());
        Assert.assertTrue("Adding absent E33 must return true", set.add(EnumSetTest.L.E33));
        Assert.assertTrue("Set must contain E4", set.contains(EnumSetTest.L.E33));
        Assert.assertEquals("Size must be 3", 3, set.size());
    }

    @Test
    public void iteratorWorks() {
        EnumSet<EnumSetTest.L> set = EnumSet.noneOf(EnumSetTest.L.class);
        set.add(EnumSetTest.L.E1);
        set.add(EnumSetTest.L.E4);
        set.add(EnumSetTest.L.E33);
        set.add(EnumSetTest.L.E2);
        List<EnumSetTest.L> items = new ArrayList<>();
        Iterator<EnumSetTest.L> iter = set.iterator();
        while (iter.hasNext()) {
            items.add(iter.next());
        } 
        try {
            iter.next();
            Assert.fail("Can't call Iterator.next after entire collection got iterated");
        } catch (NoSuchElementException e) {
            // OK
        }
        Assert.assertEquals(Arrays.asList(EnumSetTest.L.E1, EnumSetTest.L.E2, EnumSetTest.L.E4, EnumSetTest.L.E33), items);
        try {
            set.iterator().remove();
            Assert.fail("Can't call Iterator.remove right after initialization");
        } catch (IllegalStateException e) {
            // OK
        }
        iter = EnumSet.copyOf(set).iterator();
        iter.next();
        iter.remove();
        try {
            iter.remove();
            Assert.fail("Can't call Iterator.remove right after previous removal");
        } catch (IllegalStateException e) {
            // OK
        }
        iter = set.iterator();
        iter.next();
        iter.remove();
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4, EnumSetTest.L.E33), set);
    }

    @Test
    public void removeAll() {
        EnumSet<EnumSetTest.L> original = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E5, EnumSetTest.L.E8, EnumSetTest.L.E32);
        EnumSet<EnumSetTest.L> set = original.clone();
        Assert.assertTrue(set.removeAll(EnumSet.of(EnumSetTest.L.E3, EnumSetTest.L.E10, EnumSetTest.L.E32)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E5, EnumSetTest.L.E8), set);
        set = original.clone();
        Assert.assertFalse(set.removeAll(EnumSet.of(EnumSetTest.L.E4, EnumSetTest.L.E33)));
        Assert.assertEquals(original, set);
    }

    @Test
    public void contains() {
        EnumSet<EnumSetTest.L> set = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E5, EnumSetTest.L.E8, EnumSetTest.L.E32);
        Assert.assertFalse(set.contains(EnumSetTest.L.E1));
        Assert.assertTrue(set.contains(EnumSetTest.L.E2));
        Assert.assertTrue(set.contains(EnumSetTest.L.E3));
        Assert.assertFalse(set.contains(EnumSetTest.L.E4));
        Assert.assertTrue(set.contains(EnumSetTest.L.E5));
        Assert.assertTrue(set.contains(EnumSetTest.L.E8));
        Assert.assertFalse(set.contains(EnumSetTest.L.E31));
        Assert.assertTrue(set.contains(EnumSetTest.L.E32));
        Assert.assertFalse(set.contains(EnumSetTest.L.E33));
    }

    @Test
    public void add() {
        EnumSet<EnumSetTest.L> set = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4);
        Assert.assertFalse(set.add(EnumSetTest.L.E2));
        Assert.assertTrue(set.add(EnumSetTest.L.E3));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E4), set);
    }

    @Test
    public void containsAll() {
        EnumSet<EnumSetTest.L> set = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E5, EnumSetTest.L.E8, EnumSetTest.L.E32);
        Assert.assertFalse(set.containsAll(EnumSet.of(EnumSetTest.L.E1)));
        Assert.assertFalse(set.containsAll(EnumSet.of(EnumSetTest.L.E1, EnumSetTest.L.E4)));
        Assert.assertTrue(set.containsAll(EnumSet.of(EnumSetTest.L.E2)));
        Assert.assertTrue(set.containsAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E5)));
        Assert.assertFalse(set.containsAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4)));
    }

    @Test
    public void addAll() {
        EnumSet<EnumSetTest.L> set = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4);
        Assert.assertTrue(set.addAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E4), set);
        Assert.assertFalse(set.addAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E4), set);
        Assert.assertTrue(set.addAll(EnumSet.of(EnumSetTest.L.E5, EnumSetTest.L.E6)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E3, EnumSetTest.L.E4, EnumSetTest.L.E5, EnumSetTest.L.E6), set);
    }

    @Test
    public void retainAll() {
        EnumSet<EnumSetTest.L> original = EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4, EnumSetTest.L.E5);
        EnumSet<EnumSetTest.L> set = original.clone();
        Assert.assertTrue(set.retainAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4), set);
        set = original.clone();
        Assert.assertTrue(set.retainAll(EnumSet.of(EnumSetTest.L.E1, EnumSetTest.L.E2)));
        Assert.assertEquals(EnumSet.of(EnumSetTest.L.E2), set);
        set = original.clone();
        Assert.assertTrue(set.retainAll(EnumSet.of(EnumSetTest.L.E1)));
        Assert.assertEquals(EnumSet.noneOf(EnumSetTest.L.class), set);
        set = original.clone();
        Assert.assertFalse(set.retainAll(EnumSet.of(EnumSetTest.L.E2, EnumSetTest.L.E4, EnumSetTest.L.E5, EnumSetTest.L.E6)));
        Assert.assertEquals(original, set);
    }

    enum L {

        E1,
        E2,
        E3,
        E4,
        E5,
        E6,
        E7,
        E8,
        E9,
        E10,
        E11,
        E12,
        E13,
        E14,
        E15,
        E16,
        E17,
        E18,
        E19,
        E20,
        E21,
        E22,
        E23,
        E24,
        E25,
        E26,
        E27,
        E28,
        E29,
        E30,
        E31,
        E32,
        E33,
        E34,
        E35,
        E36;}
}

