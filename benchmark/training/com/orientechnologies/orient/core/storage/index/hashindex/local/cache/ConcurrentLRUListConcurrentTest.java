package com.orientechnologies.orient.core.storage.index.hashindex.local.cache;


import com.orientechnologies.orient.core.storage.cache.OCacheEntry;
import com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl;
import com.orientechnologies.orient.core.storage.cache.local.twoq.ConcurrentLRUList;
import com.orientechnologies.orient.core.storage.cache.local.twoq.LRUList;
import com.orientechnologies.orient.test.ConcurrentTestHelper;
import com.orientechnologies.orient.test.TestFactory;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import org.junit.Test;


/**
 * Concurrent test for {@link ConcurrentLRUList}.
 *
 * @author Artem Orobets (enisher-at-gmail.com)
 */
public class ConcurrentLRUListConcurrentTest {
    private static final int AMOUNT_OF_OPERATIONS = 100000;

    private static final int THREAD_COUNT = 8;

    private LRUList list = new ConcurrentLRUList();

    private volatile long c = 47;

    @Test
    public void testConcurrentAdd() throws Exception {
        ConcurrentTestHelper.test(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.AdderFactory());
        int expectedSize = (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS) * (ConcurrentLRUListConcurrentTest.THREAD_COUNT);
        assertListConsistency(expectedSize);
    }

    @Test
    public void testConcurrentAddAndRemove() throws Exception {
        Collection<Integer> res = ConcurrentTestHelper.<Integer>build().add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.AdderFactory()).add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.RemoveLRUFactory()).go();
        int expectedSize = 0;
        for (Integer r : res) {
            expectedSize += r;
        }
        assertListConsistency(expectedSize);
    }

    @Test
    public void testAddRemoveSameEntries() throws Exception {
        ConcurrentTestHelper.<Integer>build().add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.AddSameFactory()).add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.RemoveLRUFactory()).go();
        assertListConsistency();
    }

    @Test
    public void testAllOperationsRandomEntries() throws Exception {
        ConcurrentTestHelper.<Integer>build().add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.RandomAdderFactory()).add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.RandomRemoveFactory()).add(ConcurrentLRUListConcurrentTest.THREAD_COUNT, new ConcurrentLRUListConcurrentTest.RemoveLRUFactory()).go();
        assertListConsistency();
    }

    private class AdderFactory implements TestFactory<Integer> {
        private int j = 0;

        @Override
        public Callable<Integer> createWorker() {
            return new Callable<Integer>() {
                private int threadNumber = ++(j);

                @Override
                public Integer call() throws Exception {
                    for (int i = 0; i < (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS); i++) {
                        list.putToMRU(new OCacheEntryImpl(threadNumber, i, null));
                    }
                    return ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS;
                }
            };
        }
    }

    private class RemoveLRUFactory implements TestFactory<Integer> {
        @Override
        public Callable<Integer> createWorker() {
            return new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int actualRemoves = 0;
                    consumeCPU(1000);
                    for (int i = 0; i < (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS); i++) {
                        OCacheEntry e = list.removeLRU();
                        if (e != null) {
                            actualRemoves++;
                        }
                        consumeCPU(1000);
                    }
                    return -actualRemoves;
                }
            };
        }
    }

    private class RandomAdderFactory implements TestFactory<Integer> {
        @Override
        public Callable<Integer> createWorker() {
            return new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Random r = new Random();
                    for (int i = 0; i < (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS); i++) {
                        list.putToMRU(new OCacheEntryImpl(0, r.nextInt(200), null));
                        consumeCPU(((r.nextInt(500)) + 1000));
                    }
                    return ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS;
                }
            };
        }
    }

    private class AddSameFactory implements TestFactory<Integer> {
        @Override
        public Callable<Integer> createWorker() {
            return new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Random r = new Random();
                    for (int i = 0; i < (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS); i++) {
                        list.putToMRU(new OCacheEntryImpl(0, 0, null));
                        consumeCPU(((r.nextInt(500)) + 1000));
                    }
                    return ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS;
                }
            };
        }
    }

    private class RandomRemoveFactory implements TestFactory<Integer> {
        @Override
        public Callable<Integer> createWorker() {
            return new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Random r = new Random();
                    int actualRemoves = 0;
                    for (int i = 0; i < (ConcurrentLRUListConcurrentTest.AMOUNT_OF_OPERATIONS); i++) {
                        OCacheEntry e = list.remove(0, r.nextInt(100));
                        if (e != null) {
                            actualRemoves++;
                        }
                        consumeCPU(((r.nextInt(1000)) + 1000));
                    }
                    return -actualRemoves;
                }
            };
        }
    }
}

