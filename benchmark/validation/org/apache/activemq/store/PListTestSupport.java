/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store;


import PList.PListIterator;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.IOHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class PListTestSupport {
    static final Logger LOG = LoggerFactory.getLogger(PListTestSupport.class);

    protected PListStore store;

    private PList plist;

    final ByteSequence payload = new ByteSequence(new byte[400]);

    final String idSeed = new String(("Seed" + new byte[1024]));

    final Vector<Throwable> exceptions = new Vector<Throwable>();

    ExecutorService executor;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testAddLast() throws Exception {
        final int COUNT = 1000;
        LinkedList<ByteSequence> list = new LinkedList<ByteSequence>();
        for (int i = 0; i < COUNT; i++) {
            String test = new String(("test" + i));
            ByteSequence bs = new ByteSequence(test.getBytes());
            list.addLast(bs);
            plist.addLast(test, bs);
        }
        Assert.assertEquals(plist.size(), COUNT);
        PList.PListIterator actual = plist.iterator();
        Iterator<ByteSequence> expected = list.iterator();
        while (expected.hasNext()) {
            ByteSequence bs = expected.next();
            Assert.assertTrue(actual.hasNext());
            PListEntry entry = actual.next();
            String origStr = new String(bs.getData(), bs.getOffset(), bs.getLength());
            String plistString = new String(entry.getByteSequence().getData(), entry.getByteSequence().getOffset(), entry.getByteSequence().getLength());
            Assert.assertEquals(origStr, plistString);
        } 
        Assert.assertFalse(actual.hasNext());
    }

    @Test
    public void testAddFirst() throws Exception {
        final int COUNT = 1000;
        LinkedList<ByteSequence> list = new LinkedList<ByteSequence>();
        for (int i = 0; i < COUNT; i++) {
            String test = new String(("test" + i));
            ByteSequence bs = new ByteSequence(test.getBytes());
            list.addFirst(bs);
            plist.addFirst(test, bs);
        }
        Assert.assertEquals(plist.size(), COUNT);
        PList.PListIterator actual = plist.iterator();
        Iterator<ByteSequence> expected = list.iterator();
        while (expected.hasNext()) {
            ByteSequence bs = expected.next();
            Assert.assertTrue(actual.hasNext());
            PListEntry entry = actual.next();
            String origStr = new String(bs.getData(), bs.getOffset(), bs.getLength());
            String plistString = new String(entry.getByteSequence().getData(), entry.getByteSequence().getOffset(), entry.getByteSequence().getLength());
            Assert.assertEquals(origStr, plistString);
        } 
        Assert.assertFalse(actual.hasNext());
    }

    @Test
    public void testRemove() throws IOException {
        doTestRemove(2000);
    }

    @Test
    public void testDestroy() throws Exception {
        doTestRemove(1);
        plist.destroy();
        Assert.assertEquals(0, plist.size());
    }

    @Test
    public void testDestroyNonEmpty() throws Exception {
        final int COUNT = 1000;
        Map<String, ByteSequence> map = new LinkedHashMap<String, ByteSequence>();
        for (int i = 0; i < COUNT; i++) {
            String test = new String(("test" + i));
            ByteSequence bs = new ByteSequence(test.getBytes());
            map.put(test, bs);
            plist.addLast(test, bs);
        }
        plist.destroy();
        Assert.assertEquals(0, plist.size());
    }

    @Test
    public void testRemoveSecond() throws Exception {
        Object first = plist.addLast("First", new ByteSequence("A".getBytes()));
        Object second = plist.addLast("Second", new ByteSequence("B".getBytes()));
        Assert.assertTrue(plist.remove(second));
        Assert.assertTrue(plist.remove(first));
        Assert.assertFalse(plist.remove(first));
    }

    @Test
    public void testRemoveSingleEntry() throws Exception {
        plist.addLast("First", new ByteSequence("A".getBytes()));
        Iterator<PListEntry> iterator = plist.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        } 
    }

    @Test
    public void testRemoveSecondPosition() throws Exception {
        Object first = plist.addLast("First", new ByteSequence("A".getBytes()));
        Object second = plist.addLast("Second", new ByteSequence("B".getBytes()));
        Assert.assertTrue(plist.remove(second));
        Assert.assertTrue(plist.remove(first));
        Assert.assertFalse(plist.remove(first));
    }

    @Test
    public void testConcurrentAddRemove() throws Exception {
        File directory = store.getDirectory();
        store.stop();
        IOHelper.mkdirs(directory);
        IOHelper.deleteChildren(directory);
        store = createConcurrentAddRemovePListStore();
        store.setDirectory(directory);
        store.start();
        final ByteSequence payload = new ByteSequence(new byte[1024 * 2]);
        final Vector<Throwable> exceptions = new Vector<Throwable>();
        final int iterations = 1000;
        final int numLists = 10;
        final PList[] lists = new PList[numLists];
        String threadName = Thread.currentThread().getName();
        for (int i = 0; i < numLists; i++) {
            Thread.currentThread().setName(("C:" + (String.valueOf(i))));
            lists[i] = store.getPList(String.valueOf(i));
        }
        Thread.currentThread().setName(threadName);
        executor = Executors.newFixedThreadPool(100);
        class A implements Runnable {
            @Override
            public void run() {
                final String threadName = Thread.currentThread().getName();
                try {
                    for (int i = 0; i < iterations; i++) {
                        PList candidate = lists[(i % numLists)];
                        Thread.currentThread().setName(("ALRF:" + (candidate.getName())));
                        synchronized(plistLocks(candidate)) {
                            Object last = candidate.addLast(String.valueOf(i), payload);
                            getFirst(candidate);
                            Assert.assertTrue(candidate.remove(last));
                        }
                    }
                } catch (Exception error) {
                    PListTestSupport.LOG.error("Unexpcted ex", error);
                    error.printStackTrace();
                    exceptions.add(error);
                } finally {
                    Thread.currentThread().setName(threadName);
                }
            }
        }
        class B implements Runnable {
            @Override
            public void run() {
                final String threadName = Thread.currentThread().getName();
                try {
                    for (int i = 0; i < iterations; i++) {
                        PList candidate = lists[(i % numLists)];
                        Thread.currentThread().setName(("ALRF:" + (candidate.getName())));
                        synchronized(plistLocks(candidate)) {
                            Object last = candidate.addLast(String.valueOf(i), payload);
                            getFirst(candidate);
                            Assert.assertTrue(candidate.remove(last));
                        }
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    exceptions.add(error);
                } finally {
                    Thread.currentThread().setName(threadName);
                }
            }
        }
        executor.execute(new A());
        executor.execute(new A());
        executor.execute(new A());
        executor.execute(new B());
        executor.execute(new B());
        executor.execute(new B());
        executor.shutdown();
        boolean finishedInTime = executor.awaitTermination(5, TimeUnit.MINUTES);
        PListTestSupport.LOG.info("Tested completion finished in time? -> {}", (finishedInTime ? "YES" : "NO"));
        Assert.assertTrue("no exceptions", exceptions.isEmpty());
        Assert.assertTrue("finished ok", finishedInTime);
    }

    @Test
    public void testConcurrentAddLast() throws Exception {
        File directory = store.getDirectory();
        store.stop();
        IOHelper.mkdirs(directory);
        IOHelper.deleteChildren(directory);
        store = createPListStore();
        store.setDirectory(directory);
        store.start();
        final int numThreads = 20;
        final int iterations = 1000;
        executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < numThreads; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.ADD, iterations).run();
        }
        for (int i = 0; i < numThreads; i++) {
            executor.execute(new PListTestSupport.Job(i, PListTestSupport.TaskType.ITERATE, iterations));
        }
        for (int i = 0; i < 100; i++) {
            executor.execute(new PListTestSupport.Job((i + 20), PListTestSupport.TaskType.ADD, 100));
        }
        executor.shutdown();
        boolean finishedInTime = executor.awaitTermination((60 * 5), TimeUnit.SECONDS);
        Assert.assertTrue("finished ok", finishedInTime);
    }

    @Test
    public void testOverFlow() throws Exception {
        File directory = store.getDirectory();
        store.stop();
        IOHelper.mkdirs(directory);
        IOHelper.deleteChildren(directory);
        store = createPListStore();
        store.setDirectory(directory);
        store.start();
        for (int i = 0; i < 2000; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.ADD, 5).run();
        }
        // LOG.info("After Load index file: " + store.pageFile.getFile().length());
        // LOG.info("After remove index file: " + store.pageFile.getFile().length());
    }

    @Test
    public void testConcurrentAddRemoveWithPreload() throws Exception {
        File directory = store.getDirectory();
        store.stop();
        IOHelper.mkdirs(directory);
        IOHelper.deleteChildren(directory);
        store = createConcurrentAddRemoveWithPreloadPListStore();
        store.setDirectory(directory);
        store.start();
        final int iterations = 500;
        final int numLists = 10;
        // prime the store
        // create/delete
        PListTestSupport.LOG.info("create");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.CREATE, iterations).run();
        }
        PListTestSupport.LOG.info("delete");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.DELETE, iterations).run();
        }
        PListTestSupport.LOG.info("fill");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.ADD, iterations).run();
        }
        PListTestSupport.LOG.info("remove");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.REMOVE, iterations).run();
        }
        PListTestSupport.LOG.info("check empty");
        for (int i = 0; i < numLists; i++) {
            Assert.assertEquals(("empty " + i), 0, store.getPList(("List-" + i)).size());
        }
        PListTestSupport.LOG.info("delete again");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.DELETE, iterations).run();
        }
        PListTestSupport.LOG.info("fill again");
        for (int i = 0; i < numLists; i++) {
            new PListTestSupport.Job(i, PListTestSupport.TaskType.ADD, iterations).run();
        }
        PListTestSupport.LOG.info("parallel add and remove");
        executor = Executors.newFixedThreadPool((numLists * 2));
        for (int i = 0; i < (numLists * 2); i++) {
            executor.execute(new PListTestSupport.Job(i, (i >= numLists ? PListTestSupport.TaskType.ADD : PListTestSupport.TaskType.REMOVE), iterations));
        }
        executor.shutdown();
        PListTestSupport.LOG.info("wait for parallel work to complete");
        boolean finishedInTime = executor.awaitTermination((60 * 5), TimeUnit.SECONDS);
        Assert.assertTrue("no exceptions", exceptions.isEmpty());
        Assert.assertTrue("finished ok", finishedInTime);
    }

    // for non determinant issues, increasing this may help diagnose
    final int numRepeats = 1;

    @Test
    public void testRepeatStressWithCache() throws Exception {
        for (int i = 0; i < (numRepeats); i++) {
            do_testConcurrentAddIterateRemove(true);
        }
    }

    @Test
    public void testRepeatStressWithOutCache() throws Exception {
        for (int i = 0; i < (numRepeats); i++) {
            do_testConcurrentAddIterateRemove(false);
        }
    }

    enum TaskType {

        CREATE,
        DELETE,
        ADD,
        REMOVE,
        ITERATE,
        ITERATE_REMOVE;}

    ConcurrentMap<String, Object> entries = new ConcurrentHashMap<String, Object>();

    class Job implements Runnable {
        int id;

        PListTestSupport.TaskType task;

        int iterations;

        public Job(int id, PListTestSupport.TaskType t, int iterations) {
            this.id = id;
            this.task = t;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            final String threadName = Thread.currentThread().getName();
            try {
                PList plist = null;
                switch (task) {
                    case CREATE :
                        Thread.currentThread().setName(("C:" + (id)));
                        plist = store.getPList(String.valueOf(id));
                        PListTestSupport.LOG.info((("Job-" + (id)) + ", CREATE"));
                        break;
                    case DELETE :
                        Thread.currentThread().setName(("D:" + (id)));
                        store.removePList(String.valueOf(id));
                        break;
                    case ADD :
                        Thread.currentThread().setName(("A:" + (id)));
                        plist = store.getPList(String.valueOf(id));
                        for (int j = 0; j < (iterations); j++) {
                            synchronized(plistLocks(plist)) {
                                if (exceptions.isEmpty()) {
                                    String key = ((("PL>" + (id)) + (idSeed)) + "-") + j;
                                    entries.put(key, plist.addLast(key, payload));
                                } else {
                                    break;
                                }
                            }
                        }
                        if (exceptions.isEmpty()) {
                            PListTestSupport.LOG.info(((("Job-" + (id)) + ", Add, done: ") + (iterations)));
                        }
                        break;
                    case REMOVE :
                        Thread.currentThread().setName(("R:" + (id)));
                        plist = store.getPList(String.valueOf(id));
                        synchronized(plistLocks(plist)) {
                            for (int j = (iterations) - 1; j >= 0; j--) {
                                String key = ((("PL>" + (id)) + (idSeed)) + "-") + j;
                                Object position = entries.remove(key);
                                if (position != null) {
                                    plist.remove(position);
                                }
                                if ((j > 0) && ((j % ((iterations) / 2)) == 0)) {
                                    PListTestSupport.LOG.info(((("Job-" + (id)) + " Done remove: ") + j));
                                }
                            }
                        }
                        break;
                    case ITERATE :
                        Thread.currentThread().setName(("I:" + (id)));
                        plist = store.getPList(String.valueOf(id));
                        int iterateCount = 0;
                        synchronized(plistLocks(plist)) {
                            if (exceptions.isEmpty()) {
                                Iterator<PListEntry> iterator = plist.iterator();
                                while ((iterator.hasNext()) && (exceptions.isEmpty())) {
                                    iterator.next();
                                    iterateCount++;
                                } 
                                // LOG.info("Job-" + id + " Done iterate: it=" + iterator + ", count:" + iterateCount + ", size:" + plist.size());
                                if ((plist.size()) != iterateCount) {
                                    System.err.println(("Count Wrong: " + iterator));
                                }
                                Assert.assertEquals(((("iterate got all " + (id)) + " iterator:") + iterator), plist.size(), iterateCount);
                            }
                        }
                        break;
                    case ITERATE_REMOVE :
                        Thread.currentThread().setName(("IRM:" + (id)));
                        plist = store.getPList(String.valueOf(id));
                        int removeCount = 0;
                        synchronized(plistLocks(plist)) {
                            Iterator<PListEntry> removeIterator = plist.iterator();
                            while (removeIterator.hasNext()) {
                                removeIterator.next();
                                removeIterator.remove();
                                if ((removeCount++) > (iterations)) {
                                    break;
                                }
                            } 
                        }
                        PListTestSupport.LOG.info(((("Job-" + (id)) + " Done remove: ") + removeCount));
                        break;
                    default :
                }
            } catch (Exception e) {
                PListTestSupport.LOG.warn(((("Job[" + (id)) + "] caught exception: ") + (e.getMessage())));
                e.printStackTrace();
                exceptions.add(e);
                if ((executor) != null) {
                    executor.shutdownNow();
                }
            } finally {
                Thread.currentThread().setName(threadName);
            }
        }
    }

    Map<PList, Object> locks = new HashMap<PList, Object>();
}

