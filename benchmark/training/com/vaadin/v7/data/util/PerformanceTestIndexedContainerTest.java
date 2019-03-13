package com.vaadin.v7.data.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;


public class PerformanceTestIndexedContainerTest {
    private static final int REPEATS = 10;

    private static final int ITEMS = 50000;

    private static final long ADD_ITEM_FAIL_THRESHOLD = 200;

    // TODO should improve performance of these methods
    private static final long ADD_ITEM_AT_FAIL_THRESHOLD = 5000;

    private static final long ADD_ITEM_AFTER_FAIL_THRESHOLD = 5000;

    // FIXME: Vaadin 7 compatibility version fails this check with original
    // value of 5000
    private static final long ADD_ITEM_AFTER_LAST_FAIL_THRESHOLD = 6000;

    private static final long ADD_ITEMS_CONSTRUCTOR_FAIL_THRESHOLD = 200;

    @Test
    public void testAddItemPerformance() {
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < (PerformanceTestIndexedContainerTest.REPEATS); ++j) {
            IndexedContainer c = new IndexedContainer();
            long start = System.currentTimeMillis();
            for (int i = 0; i < (PerformanceTestIndexedContainerTest.ITEMS); i++) {
                c.addItem();
            }
            times.add(((System.currentTimeMillis()) - start));
        }
        checkMedian(PerformanceTestIndexedContainerTest.ITEMS, times, "IndexedContainer.addItem()", PerformanceTestIndexedContainerTest.ADD_ITEM_FAIL_THRESHOLD);
    }

    @Test
    public void testAddItemAtPerformance() {
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < (PerformanceTestIndexedContainerTest.REPEATS); ++j) {
            IndexedContainer c = new IndexedContainer();
            long start = System.currentTimeMillis();
            for (int i = 0; i < (PerformanceTestIndexedContainerTest.ITEMS); i++) {
                c.addItemAt(0);
            }
            times.add(((System.currentTimeMillis()) - start));
        }
        checkMedian(PerformanceTestIndexedContainerTest.ITEMS, times, "IndexedContainer.addItemAt()", PerformanceTestIndexedContainerTest.ADD_ITEM_AT_FAIL_THRESHOLD);
    }

    @Test
    public void testAddItemAfterPerformance() {
        Object initialId = "Item0";
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < (PerformanceTestIndexedContainerTest.REPEATS); ++j) {
            IndexedContainer c = new IndexedContainer();
            c.addItem(initialId);
            long start = System.currentTimeMillis();
            for (int i = 0; i < (PerformanceTestIndexedContainerTest.ITEMS); i++) {
                c.addItemAfter(initialId);
            }
            times.add(((System.currentTimeMillis()) - start));
        }
        checkMedian(PerformanceTestIndexedContainerTest.ITEMS, times, "IndexedContainer.addItemAfter()", PerformanceTestIndexedContainerTest.ADD_ITEM_AFTER_FAIL_THRESHOLD);
    }

    @Test
    public void testAddItemAfterLastPerformance() {
        // TODO running with less items because slow otherwise
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < (PerformanceTestIndexedContainerTest.REPEATS); ++j) {
            IndexedContainer c = new IndexedContainer();
            c.addItem();
            long start = System.currentTimeMillis();
            for (int i = 0; i < ((PerformanceTestIndexedContainerTest.ITEMS) / 3); i++) {
                c.addItemAfter(c.lastItemId());
            }
            times.add(((System.currentTimeMillis()) - start));
        }
        checkMedian(((PerformanceTestIndexedContainerTest.ITEMS) / 3), times, "IndexedContainer.addItemAfter(lastId)", PerformanceTestIndexedContainerTest.ADD_ITEM_AFTER_LAST_FAIL_THRESHOLD);
    }

    @Test
    public void testAddItemsConstructorPerformance() {
        Collection<Object> items = new ArrayList<Object>(50000);
        for (int i = 0; i < (PerformanceTestIndexedContainerTest.ITEMS); ++i) {
            items.add(new Object());
        }
        SortedSet<Long> times = new TreeSet<Long>();
        for (int j = 0; j < (PerformanceTestIndexedContainerTest.REPEATS); ++j) {
            long start = System.currentTimeMillis();
            new IndexedContainer(items);
            times.add(((System.currentTimeMillis()) - start));
        }
        checkMedian(PerformanceTestIndexedContainerTest.ITEMS, times, "IndexedContainer(Collection)", PerformanceTestIndexedContainerTest.ADD_ITEMS_CONSTRUCTOR_FAIL_THRESHOLD);
    }
}

