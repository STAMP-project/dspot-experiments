package com.orientechnologies.common.collection.closabledictionary;


import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class OClosableLinkedContainerTest {
    @Test
    public void testSingleItemAddRemove() throws Exception {
        final OClosableItem closableItem = new OClosableLinkedContainerTest.CItem(10);
        final OClosableLinkedContainer<Long, OClosableItem> dictionary = new OClosableLinkedContainer<Long, OClosableItem>(10);
        dictionary.add(1L, closableItem);
        OClosableEntry<Long, OClosableItem> entry = dictionary.acquire(0L);
        Assert.assertNull(entry);
        entry = dictionary.acquire(1L);
        Assert.assertNotNull(entry);
        dictionary.release(entry);
        Assert.assertTrue(dictionary.checkAllLRUListItemsInMap());
        Assert.assertTrue(dictionary.checkAllOpenItemsInLRUList());
    }

    @Test
    public void testCloseHalfOfTheItems() throws Exception {
        final OClosableLinkedContainer<Long, OClosableItem> dictionary = new OClosableLinkedContainer<Long, OClosableItem>(10);
        for (int i = 0; i < 10; i++) {
            final OClosableItem closableItem = new OClosableLinkedContainerTest.CItem(i);
            dictionary.add(((long) (i)), closableItem);
        }
        OClosableEntry<Long, OClosableItem> entry = dictionary.acquire(10L);
        Assert.assertNull(entry);
        for (int i = 0; i < 5; i++) {
            entry = dictionary.acquire(((long) (i)));
            dictionary.release(entry);
        }
        dictionary.emptyBuffers();
        Assert.assertTrue(dictionary.checkAllLRUListItemsInMap());
        Assert.assertTrue(dictionary.checkAllOpenItemsInLRUList());
        for (int i = 0; i < 5; i++) {
            dictionary.add((10L + i), new OClosableLinkedContainerTest.CItem((10 + i)));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(dictionary.get(((long) (i))).isOpen());
        }
        for (int i = 5; i < 10; i++) {
            Assert.assertTrue((!(dictionary.get(((long) (i))).isOpen())));
        }
        for (int i = 10; i < 15; i++) {
            Assert.assertTrue(dictionary.get(((long) (i))).isOpen());
        }
        Assert.assertTrue(dictionary.checkAllLRUListItemsInMap());
        Assert.assertTrue(dictionary.checkAllOpenItemsInLRUList());
    }

    private class Adder implements Callable<Void> {
        private final OClosableLinkedContainer<Long, OClosableLinkedContainerTest.CItem> dictionary;

        private final CountDownLatch latch;

        private final int from;

        private final int to;

        public Adder(OClosableLinkedContainer<Long, OClosableLinkedContainerTest.CItem> dictionary, CountDownLatch latch, int from, int to) {
            this.dictionary = dictionary;
            this.latch = latch;
            this.from = from;
            this.to = to;
        }

        @Override
        public Void call() throws Exception {
            latch.await();
            try {
                for (int i = from; i < (to); i++) {
                    dictionary.add(((long) (i)), new OClosableLinkedContainerTest.CItem(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            System.out.println((((("Add from " + (from)) + " to ") + (to)) + " completed"));
            return null;
        }
    }

    private class Acquier implements Callable<Void> {
        private final OClosableLinkedContainer<Long, OClosableLinkedContainerTest.CItem> dictionary;

        private final CountDownLatch latch;

        private final int limit;

        private final AtomicBoolean stop;

        public Acquier(OClosableLinkedContainer<Long, OClosableLinkedContainerTest.CItem> dictionary, CountDownLatch latch, int limit, AtomicBoolean stop) {
            this.dictionary = dictionary;
            this.latch = latch;
            this.limit = limit;
            this.stop = stop;
        }

        @Override
        public Void call() throws Exception {
            latch.await();
            long counter = 0;
            long start = System.nanoTime();
            try {
                Random random = new Random();
                while (!(stop.get())) {
                    int index = random.nextInt(limit);
                    final OClosableEntry<Long, OClosableLinkedContainerTest.CItem> entry = dictionary.acquire(((long) (index)));
                    if (entry != null) {
                        Assert.assertTrue(entry.get().isOpen());
                        counter++;
                        dictionary.release(entry);
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            long end = System.nanoTime();
            System.out.println(((("Files processed " + counter) + " nanos per item ") + ((end - start) / counter)));
            return null;
        }
    }

    private static class CItem implements OClosableItem {
        public static AtomicInteger openFiles = new AtomicInteger();

        public static AtomicInteger maxDeltaLimit = new AtomicInteger();

        private volatile boolean open = true;

        private final int openLimit;

        public CItem(int openLimit) {
            this.openLimit = openLimit;
            countOpenFiles();
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() {
            open = false;
            int count = OClosableLinkedContainerTest.CItem.openFiles.decrementAndGet();
            if ((count - (openLimit)) > 0) {
                while (true) {
                    int max = OClosableLinkedContainerTest.CItem.maxDeltaLimit.get();
                    if ((count - (openLimit)) > max) {
                        if (OClosableLinkedContainerTest.CItem.maxDeltaLimit.compareAndSet(max, (count - (openLimit))))
                            break;

                    } else {
                        break;
                    }
                } 
            }
        }

        public void open() {
            open = true;
            countOpenFiles();
        }

        private void countOpenFiles() {
            int count = OClosableLinkedContainerTest.CItem.openFiles.incrementAndGet();
            if ((count - (openLimit)) > 0) {
                while (true) {
                    int max = OClosableLinkedContainerTest.CItem.maxDeltaLimit.get();
                    if ((count - (openLimit)) > max) {
                        if (OClosableLinkedContainerTest.CItem.maxDeltaLimit.compareAndSet(max, (count - (openLimit))))
                            break;

                    } else {
                        break;
                    }
                } 
            }
        }
    }
}

