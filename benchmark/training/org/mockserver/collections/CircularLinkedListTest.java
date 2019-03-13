package org.mockserver.collections;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class CircularLinkedListTest {
    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAdd() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.add("1");
        circularLinkedList.add("2");
        circularLinkedList.add("3");
        circularLinkedList.add("4");
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("1"));
        Assert.assertTrue(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("3"));
        Assert.assertTrue(circularLinkedList.contains("4"));
        Assert.assertEquals(Arrays.asList("2", "3", "4"), circularLinkedList);
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddFirst() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.addFirst("1");
        circularLinkedList.addFirst("2");
        circularLinkedList.addFirst("3");
        circularLinkedList.addFirst("4");
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("3"));
        Assert.assertTrue(circularLinkedList.contains("4"));
        Assert.assertTrue(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("1"));
        Assert.assertEquals(Arrays.asList("4", "2", "1"), circularLinkedList);
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddLast() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.addLast("1");
        circularLinkedList.addLast("2");
        circularLinkedList.addLast("3");
        circularLinkedList.addLast("4");
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("1"));
        Assert.assertTrue(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("3"));
        Assert.assertTrue(circularLinkedList.contains("4"));
        Assert.assertEquals(Arrays.asList("2", "3", "4"), circularLinkedList);
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddAll() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.addAll(Arrays.asList("1", "2", "3", "4"));
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("1"));
        Assert.assertTrue(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("3"));
        Assert.assertTrue(circularLinkedList.contains("4"));
        Assert.assertEquals(Arrays.asList("2", "3", "4"), circularLinkedList);
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddAllWithIndex() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.add("1");
        circularLinkedList.add("2");
        circularLinkedList.add("3");
        circularLinkedList.addAll(2, Arrays.asList("a", "b"));
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("1"));
        Assert.assertFalse(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("a"));
        Assert.assertTrue(circularLinkedList.contains("b"));
        Assert.assertTrue(circularLinkedList.contains("3"));
        Assert.assertEquals(Arrays.asList("a", "b", "3"), circularLinkedList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowInsertingListInIndexGreaterThenMaximumNumberOfEntries() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.add("1");
        circularLinkedList.add("2");
        circularLinkedList.add("3");
        circularLinkedList.addAll(3, Arrays.asList("a", "b"));
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddWithIndex() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.add("1");
        circularLinkedList.add("2");
        circularLinkedList.add("3");
        circularLinkedList.add(1, "4");
        // then
        Assert.assertEquals(3, circularLinkedList.size());
        Assert.assertFalse(circularLinkedList.contains("1"));
        Assert.assertTrue(circularLinkedList.contains("4"));
        Assert.assertTrue(circularLinkedList.contains("2"));
        Assert.assertTrue(circularLinkedList.contains("3"));
        Assert.assertEquals(Arrays.asList("4", "2", "3"), circularLinkedList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowInsertingItemInIndexGreaterThenMaximumNumberOfEntries() {
        // given
        CircularLinkedList<String> circularLinkedList = new CircularLinkedList<String>(3);
        // when
        circularLinkedList.add("1");
        circularLinkedList.add("2");
        circularLinkedList.add("3");
        circularLinkedList.add(3, "4");
    }
}

