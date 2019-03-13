/**
 * Copyright 2014 Alexey Andreev.
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


import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class LinkedListTest {
    @Test
    public void emptyListCreated() {
        LinkedList<String> list = new LinkedList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());
        Assert.assertFalse(list.iterator().hasNext());
    }

    @Test
    public void elementAdded() {
        LinkedList<String> list = new LinkedList<>();
        list.add("foo");
        Iterator<String> iter = list.iterator();
        Assert.assertEquals("foo", iter.next());
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void elementRetrievedByIndex() {
        LinkedList<String> list = new LinkedList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        Assert.assertEquals("foo", list.get(0));
        Assert.assertEquals("bar", list.get(1));
        Assert.assertEquals("baz", list.get(2));
    }

    @Test
    public void listIteratorPositioned() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(1);
        Assert.assertEquals(1, iter.nextIndex());
        Assert.assertEquals(0, iter.previousIndex());
        Assert.assertTrue(iter.hasNext());
        Assert.assertTrue(iter.hasPrevious());
        Assert.assertEquals("2", iter.next());
    }

    @Test
    public void listIteratorMoved() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(1);
        Assert.assertEquals("2", iter.next());
        Assert.assertEquals("3", iter.next());
        Assert.assertEquals("a", iter.next());
        Assert.assertEquals("a", iter.previous());
        Assert.assertEquals("3", iter.previous());
        Assert.assertEquals(2, iter.nextIndex());
        Assert.assertEquals(1, iter.previousIndex());
    }

    @Test(expected = NoSuchElementException.class)
    public void listInteratorCantMoveBeyondLowerBound() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(1);
        Assert.assertEquals("1", iter.previous());
        iter.previous();
    }

    @Test(expected = NoSuchElementException.class)
    public void listInteratorCantMoveBeyondUpperBound() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(4);
        Assert.assertEquals("b", iter.next());
        iter.next();
    }

    @Test
    public void listIteratorRemovesItem() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(2);
        Assert.assertEquals("3", iter.next());
        iter.remove();
        Assert.assertEquals(2, iter.nextIndex());
        Assert.assertEquals("a", iter.next());
        Assert.assertArrayEquals(new String[]{ "1", "2", "a", "b" }, list.toArray(new String[0]));
        Assert.assertEquals(4, list.size());
    }

    @Test
    public void listIteratorAddsItem() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(2);
        iter.add("*");
        Assert.assertEquals("3", iter.next());
        Assert.assertArrayEquals(new String[]{ "1", "2", "*", "3", "a", "b" }, list.toArray(new String[0]));
    }

    @Test
    public void listIteratorReplacesItem() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(2);
        iter.next();
        iter.set("*");
        Assert.assertEquals("a", iter.next());
        Assert.assertArrayEquals(new String[]{ "1", "2", "*", "a", "b" }, list.toArray(new String[0]));
    }

    @Test
    public void listIteratorRemovesPreviousItem() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(2);
        iter.previous();
        iter.remove();
        Assert.assertEquals(1, iter.nextIndex());
        Assert.assertEquals("3", iter.next());
        Assert.assertEquals(4, list.size());
    }

    @Test(expected = IllegalStateException.class)
    public void freshListIteratorWithOffsetDoesNotAllowRemoval() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        ListIterator<String> iter = list.listIterator(2);
        iter.remove();
    }

    @Test
    public void addsToTail() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        list.addLast("*");
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "a", "b", "*" }, list.toArray(new String[0]));
    }

    @Test
    public void addsToHead() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        list.addFirst("*");
        Assert.assertArrayEquals(new String[]{ "*", "1", "2", "3", "a", "b" }, list.toArray(new String[0]));
    }

    @Test
    public void removesFromTail() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        Assert.assertEquals("b", list.removeLast());
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("a", list.getLast());
        Iterator<String> iter = list.iterator();
        Assert.assertEquals("1", iter.next());
        iter.next();
        iter.next();
        Assert.assertEquals("a", iter.next());
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void removesFromHead() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "a", "b"));
        Assert.assertEquals("1", list.removeFirst());
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("2", list.getFirst());
        Iterator<String> iter = list.descendingIterator();
        Assert.assertEquals("b", iter.next());
        iter.next();
        iter.next();
        Assert.assertEquals("2", iter.next());
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void removesFirstOccurrence() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "1", "2"));
        Assert.assertFalse(list.removeFirstOccurrence("*"));
        Assert.assertTrue(list.removeFirstOccurrence("2"));
        Assert.assertEquals(4, list.size());
        Assert.assertArrayEquals(new String[]{ "1", "3", "1", "2" }, list.toArray(new String[0]));
    }

    @Test
    public void removesLastOccurrence() {
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList("1", "2", "3", "1", "2"));
        Assert.assertFalse(list.removeLastOccurrence("*"));
        Assert.assertTrue(list.removeLastOccurrence("2"));
        Assert.assertEquals(4, list.size());
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "1" }, list.toArray(new String[0]));
    }

    @Test
    public void pushes() {
        LinkedList<String> list = new LinkedList<>();
        list.push("foo");
        Assert.assertEquals("foo", list.peek());
        list.push("bar");
        Assert.assertEquals("bar", list.peek());
    }
}

