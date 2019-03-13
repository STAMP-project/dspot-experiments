/**
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
package org.jctools.maps.nbhm_test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.hamcrest.CoreMatchers;
import org.jctools.maps.NonBlockingHashMapLong;
import org.junit.Assert;
import org.junit.Test;


// Test NonBlockingHashMapLong via JUnit
public class NBHML_Tester2 {
    private static NonBlockingHashMapLong<String> _nbhml;

    // Test some basic stuff; add a few keys, remove a few keys
    @Test
    public void testBasic() {
        Assert.assertTrue(NBHML_Tester2._nbhml.isEmpty());
        Assert.assertThat(NBHML_Tester2._nbhml.put(1, "v1"), CoreMatchers.nullValue());
        checkSizes(1);
        Assert.assertThat(NBHML_Tester2._nbhml.putIfAbsent(2, "v2"), CoreMatchers.nullValue());
        checkSizes(2);
        Assert.assertTrue(NBHML_Tester2._nbhml.containsKey(2));
        Assert.assertThat(NBHML_Tester2._nbhml.put(1, "v1a"), CoreMatchers.is("v1"));
        Assert.assertThat(NBHML_Tester2._nbhml.put(2, "v2a"), CoreMatchers.is("v2"));
        checkSizes(2);
        Assert.assertThat(NBHML_Tester2._nbhml.putIfAbsent(2, "v2b"), CoreMatchers.is("v2a"));
        Assert.assertThat(NBHML_Tester2._nbhml.remove(1), CoreMatchers.is("v1a"));
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(1));
        checkSizes(1);
        Assert.assertThat(NBHML_Tester2._nbhml.remove(1), CoreMatchers.nullValue());
        Assert.assertThat(NBHML_Tester2._nbhml.remove(2), CoreMatchers.is("v2a"));
        checkSizes(0);
        Assert.assertThat(NBHML_Tester2._nbhml.remove(2), CoreMatchers.nullValue());
        Assert.assertThat(NBHML_Tester2._nbhml.remove("k3"), CoreMatchers.nullValue());
        Assert.assertTrue(NBHML_Tester2._nbhml.isEmpty());
        Assert.assertThat(NBHML_Tester2._nbhml.put(0, "v0"), CoreMatchers.nullValue());
        Assert.assertTrue(NBHML_Tester2._nbhml.containsKey(0));
        checkSizes(1);
        Assert.assertThat(NBHML_Tester2._nbhml.remove(0), CoreMatchers.is("v0"));
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(0));
        checkSizes(0);
        Assert.assertThat(NBHML_Tester2._nbhml.replace(0, "v0"), CoreMatchers.nullValue());
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(0));
        Assert.assertThat(NBHML_Tester2._nbhml.put(0, "v0"), CoreMatchers.nullValue());
        Assert.assertEquals(NBHML_Tester2._nbhml.replace(0, "v0a"), "v0");
        Assert.assertEquals(NBHML_Tester2._nbhml.get(0), "v0a");
        Assert.assertThat(NBHML_Tester2._nbhml.remove(0), CoreMatchers.is("v0a"));
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(0));
        checkSizes(0);
        Assert.assertThat(NBHML_Tester2._nbhml.replace(1, "v1"), CoreMatchers.nullValue());
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(1));
        Assert.assertThat(NBHML_Tester2._nbhml.put(1, "v1"), CoreMatchers.nullValue());
        Assert.assertEquals(NBHML_Tester2._nbhml.replace(1, "v1a"), "v1");
        Assert.assertEquals(NBHML_Tester2._nbhml.get(1), "v1a");
        Assert.assertThat(NBHML_Tester2._nbhml.remove(1), CoreMatchers.is("v1a"));
        Assert.assertFalse(NBHML_Tester2._nbhml.containsKey(1));
        checkSizes(0);
        // Simple insert of simple keys, with no reprobing on insert until the
        // table gets full exactly.  Then do a 'get' on the totally full table.
        NonBlockingHashMapLong<Object> map = new NonBlockingHashMapLong<Object>(32);
        for (int i = 1; i < 32; i++) {
            map.put(i, new Object());
        }
        map.get(33);// this causes a NPE

    }

    @Test
    public void testIterationBig2() {
        final int CNT = 10000;
        Assert.assertThat(NBHML_Tester2._nbhml.size(), CoreMatchers.is(0));
        final String v = "v";
        for (int i = 0; i < CNT; i++) {
            NBHML_Tester2._nbhml.put(i, v);
            String s = NBHML_Tester2._nbhml.get(i);
            Assert.assertThat(s, CoreMatchers.is(v));
        }
        Assert.assertThat(NBHML_Tester2._nbhml.size(), CoreMatchers.is(CNT));
        NBHML_Tester2._nbhml.clear();
    }

    @Test
    public void testIteration() {
        Assert.assertTrue(NBHML_Tester2._nbhml.isEmpty());
        Assert.assertThat(NBHML_Tester2._nbhml.put(1, "v1"), CoreMatchers.nullValue());
        Assert.assertThat(NBHML_Tester2._nbhml.put(2, "v2"), CoreMatchers.nullValue());
        String str1 = "";
        for (Map.Entry<Long, String> e : NBHML_Tester2._nbhml.entrySet()) {
            str1 += e.getKey();
        }
        Assert.assertThat("found all entries", str1, CoreMatchers.anyOf(CoreMatchers.is("12"), CoreMatchers.is("21")));
        String str2 = "";
        for (Long key : NBHML_Tester2._nbhml.keySet()) {
            str2 += key;
        }
        Assert.assertThat("found all keys", str2, CoreMatchers.anyOf(CoreMatchers.is("12"), CoreMatchers.is("21")));
        String str3 = "";
        for (String val : NBHML_Tester2._nbhml.values()) {
            str3 += val;
        }
        Assert.assertThat("found all vals", str3, CoreMatchers.anyOf(CoreMatchers.is("v1v2"), CoreMatchers.is("v2v1")));
        Assert.assertThat("toString works", NBHML_Tester2._nbhml.toString(), CoreMatchers.anyOf(CoreMatchers.is("{1=v1, 2=v2}"), CoreMatchers.is("{2=v2, 1=v1}")));
        NBHML_Tester2._nbhml.clear();
    }

    @Test
    public void testSerial() {
        Assert.assertTrue(NBHML_Tester2._nbhml.isEmpty());
        Assert.assertThat(NBHML_Tester2._nbhml.put(305419896L, "v1"), CoreMatchers.nullValue());
        Assert.assertThat(NBHML_Tester2._nbhml.put(2271560481L, "v2"), CoreMatchers.nullValue());
        // Serialize it out
        try {
            FileOutputStream fos = new FileOutputStream("NBHML_test.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(NBHML_Tester2._nbhml);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Read it back
        try {
            File f = new File("NBHML_test.txt");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            NonBlockingHashMapLong nbhml = ((NonBlockingHashMapLong) (in.readObject()));
            in.close();
            Assert.assertEquals(NBHML_Tester2._nbhml.toString(), nbhml.toString());
            if (!(f.delete())) {
                throw new IOException("delete failed");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testIterationBig() {
        final int CNT = 10000;
        Assert.assertThat(NBHML_Tester2._nbhml.size(), CoreMatchers.is(0));
        for (int i = 0; i < CNT; i++) {
            NBHML_Tester2._nbhml.put(i, ("v" + i));
        }
        Assert.assertThat(NBHML_Tester2._nbhml.size(), CoreMatchers.is(CNT));
        int sz = 0;
        int sum = 0;
        for (long x : NBHML_Tester2._nbhml.keySet()) {
            sz++;
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= (CNT - 1))));
        }
        Assert.assertThat("Found 10000 ints", sz, CoreMatchers.is(CNT));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is(((CNT * (CNT - 1)) / 2)));
        Assert.assertThat("can remove 3", NBHML_Tester2._nbhml.remove(3), CoreMatchers.is("v3"));
        Assert.assertThat("can remove 4", NBHML_Tester2._nbhml.remove(4), CoreMatchers.is("v4"));
        sz = 0;
        sum = 0;
        for (long x : NBHML_Tester2._nbhml.keySet()) {
            sz++;
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= (CNT - 1))));
            String v = NBHML_Tester2._nbhml.get(x);
            Assert.assertThat("", v.charAt(0), CoreMatchers.is('v'));
            Assert.assertThat("", x, CoreMatchers.is(Long.parseLong(v.substring(1))));
        }
        Assert.assertThat((("Found " + (CNT - 2)) + " ints"), sz, CoreMatchers.is((CNT - 2)));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is((((CNT * (CNT - 1)) / 2) - (3 + 4))));
        NBHML_Tester2._nbhml.clear();
    }

    // Do some simple concurrent testing
    @Test
    public void testConcurrentSimple() throws InterruptedException {
        final NonBlockingHashMapLong<String> nbhml = new NonBlockingHashMapLong();
        // In 2 threads, add & remove even & odd elements concurrently
        final int num_thrds = 2;
        Thread[] ts = new Thread[num_thrds];
        for (int i = 1; i < num_thrds; i++) {
            final int x = i;
            ts[i] = new Thread() {
                public void run() {
                    work_helper(nbhml, x, num_thrds);
                }
            };
        }
        for (int i = 1; i < num_thrds; i++) {
            ts[i].start();
        }
        work_helper(nbhml, 0, num_thrds);
        for (int i = 1; i < num_thrds; i++) {
            ts[i].join();
        }
        // In the end, all members should be removed
        StringBuilder buf = new StringBuilder();
        buf.append("Should be emptyset but has these elements: {");
        boolean found = false;
        for (long x : nbhml.keySet()) {
            buf.append(" ").append(x);
            found = true;
        }
        if (found) {
            System.out.println((buf + " }"));
        }
        Assert.assertThat("concurrent size=0", nbhml.size(), CoreMatchers.is(0));
        Assert.assertThat("keySet size==0", nbhml.keySet().size(), CoreMatchers.is(0));
    }

    // --- Customer Test Case 1 ------------------------------------------------
    @Test
    public final void testNonBlockingHashMapSize() {
        NonBlockingHashMapLong<String> items = new NonBlockingHashMapLong();
        items.put(100L, "100");
        items.put(101L, "101");
        Assert.assertEquals("keySet().size()", 2, items.keySet().size());
        Assert.assertTrue("keySet().contains(100)", items.keySet().contains(100L));
        Assert.assertTrue("keySet().contains(101)", items.keySet().contains(101L));
        Assert.assertEquals("values().size()", 2, items.values().size());
        Assert.assertTrue("values().contains(\"100\")", items.values().contains("100"));
        Assert.assertTrue("values().contains(\"101\")", items.values().contains("101"));
        Assert.assertEquals("entrySet().size()", 2, items.entrySet().size());
        boolean found100 = false;
        boolean found101 = false;
        for (Map.Entry<Long, String> entry : items.entrySet()) {
            if (entry.getKey().equals(100L)) {
                Assert.assertEquals("entry[100].getValue()==\"100\"", "100", entry.getValue());
                found100 = true;
            } else
                if (entry.getKey().equals(101L)) {
                    Assert.assertEquals("entry[101].getValue()==\"101\"", "101", entry.getValue());
                    found101 = true;
                }

        }
        Assert.assertTrue("entrySet().contains([100])", found100);
        Assert.assertTrue("entrySet().contains([101])", found101);
    }

    // --- Customer Test Case 2 ------------------------------------------------
    // Concurrent insertion & then iterator test.
    @Test
    public void testNonBlockingHashMapIterator() throws InterruptedException {
        final int ITEM_COUNT1 = 1000;
        final int THREAD_COUNT = 5;
        final int PER_CNT = ITEM_COUNT1 / THREAD_COUNT;
        final int ITEM_COUNT = PER_CNT * THREAD_COUNT;// fix roundoff for odd thread counts

        NonBlockingHashMapLong<NBHML_Tester2.TestKey> nbhml = new NonBlockingHashMapLong();
        // use a barrier to open the gate for all threads at once to avoid rolling
        // start and no actual concurrency
        final CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
        final ExecutorService ex = Executors.newFixedThreadPool(THREAD_COUNT);
        final CompletionService<Object> co = new ExecutorCompletionService<>(ex);
        for (int i = 0; i < THREAD_COUNT; i++) {
            co.submit(new NBHML_Tester2.NBHMLFeeder(nbhml, PER_CNT, barrier, (i * PER_CNT)));
        }
        for (int retCount = 0; retCount < THREAD_COUNT; retCount++) {
            co.take();
        }
        ex.shutdown();
        Assert.assertEquals("values().size()", ITEM_COUNT, nbhml.values().size());
        Assert.assertEquals("entrySet().size()", ITEM_COUNT, nbhml.entrySet().size());
        int itemCount = 0;
        for (NBHML_Tester2.TestKey K : nbhml.values()) {
            itemCount++;
        }
        Assert.assertEquals("values().iterator() count", ITEM_COUNT, itemCount);
    }

    // ---
    @Test
    public void testNonBlockingHashMapIteratorMultithreaded() throws InterruptedException, ExecutionException {
        NBHML_Tester2.TestKeyFeeder feeder = getTestKeyFeeder();
        final int itemCount = feeder.size();
        // validate results
        final NonBlockingHashMapLong<NBHML_Tester2.TestKey> items = feeder.getMapMultithreaded();
        Assert.assertEquals("size()", itemCount, items.size());
        Assert.assertEquals("values().size()", itemCount, items.values().size());
        Assert.assertEquals("entrySet().size()", itemCount, items.entrySet().size());
        int iteratorCount = 0;
        for (NBHML_Tester2.TestKey m : items.values()) {
            iteratorCount++;
        }
        // sometimes a different result comes back the second time
        int iteratorCount2 = 0;
        for (NBHML_Tester2.TestKey m2 : items.values()) {
            iteratorCount2++;
        }
        Assert.assertEquals("iterator counts differ", iteratorCount, iteratorCount2);
        Assert.assertEquals("values().iterator() count", itemCount, iteratorCount);
    }

    // --- NBHMLFeeder ---
    // Class to be called from another thread, to get concurrent installs into
    // the table.
    private static class NBHMLFeeder implements Callable<Object> {
        private static final Random _rand = new Random(System.currentTimeMillis());

        private final NonBlockingHashMapLong<NBHML_Tester2.TestKey> _map;

        private final int _count;

        private final CyclicBarrier _barrier;

        private final long _offset;

        public NBHMLFeeder(final NonBlockingHashMapLong<NBHML_Tester2.TestKey> map, final int count, final CyclicBarrier barrier, final long offset) {
            _map = map;
            _count = count;
            _barrier = barrier;
            _offset = offset;
        }

        public Object call() throws Exception {
            _barrier.await();// barrier, to force racing start

            for (long j = 0; j < (_count); j++) {
                _map.put((j + (_offset)), new NBHML_Tester2.TestKey(NBHML_Tester2.NBHMLFeeder._rand.nextLong(), NBHML_Tester2.NBHMLFeeder._rand.nextInt(), ((short) (NBHML_Tester2.NBHMLFeeder._rand.nextInt(Short.MAX_VALUE)))));
            }
            return null;
        }
    }

    // --- TestKey ---
    // Funny key tests all sorts of things, has a pre-wired hashCode & equals.
    private static final class TestKey {
        public final int _type;

        public final long _id;

        public final int _hash;

        public TestKey(final long id, final int type, int hash) {
            _id = id;
            _type = type;
            _hash = hash;
        }

        public int hashCode() {
            return _hash;
        }

        public boolean equals(Object object) {
            if (null == object) {
                return false;
            }
            if (object == (this)) {
                return true;
            }
            if ((object.getClass()) != (this.getClass())) {
                return false;
            }
            final NBHML_Tester2.TestKey other = ((NBHML_Tester2.TestKey) (object));
            return ((this._type) == (other._type)) && ((this._id) == (other._id));
        }

        public String toString() {
            return String.format("%s:%d,%d,%d", getClass().getSimpleName(), _id, _type, _hash);
        }
    }

    // ---
    private static class TestKeyFeeder {
        private final Hashtable<Integer, List<NBHML_Tester2.TestKey>> _items = new Hashtable<>();

        private int _size = 0;

        public int size() {
            return _size;
        }

        // Put items into the hashtable, sorted by 'type' into LinkedLists.
        public void checkedPut(final long id, final int type, final int hash) {
            (_size)++;
            final NBHML_Tester2.TestKey item = new NBHML_Tester2.TestKey(id, type, hash);
            if (!(_items.containsKey(type))) {
                _items.put(type, new LinkedList<>());
            }
            _items.get(type).add(item);
        }

        public NonBlockingHashMapLong<NBHML_Tester2.TestKey> getMapMultithreaded() throws InterruptedException, ExecutionException {
            final int threadCount = _items.keySet().size();
            final NonBlockingHashMapLong<NBHML_Tester2.TestKey> map = new NonBlockingHashMapLong();
            // use a barrier to open the gate for all threads at once to avoid rolling start and no actual concurrency
            final CyclicBarrier barrier = new CyclicBarrier(threadCount);
            final ExecutorService ex = Executors.newFixedThreadPool(threadCount);
            final CompletionService<Integer> co = new ExecutorCompletionService<>(ex);
            for (Integer type : _items.keySet()) {
                // A linked-list of things to insert
                List<NBHML_Tester2.TestKey> items = _items.get(type);
                NBHML_Tester2.TestKeyFeederThread feeder = new NBHML_Tester2.TestKeyFeederThread(items, map, barrier);
                co.submit(feeder);
            }
            // wait for all threads to return
            int itemCount = 0;
            for (int retCount = 0; retCount < threadCount; retCount++) {
                final Future<Integer> result = co.take();
                itemCount += result.get();
            }
            ex.shutdown();
            return map;
        }
    }

    // --- TestKeyFeederThread
    private static class TestKeyFeederThread implements Callable<Integer> {
        private final NonBlockingHashMapLong<NBHML_Tester2.TestKey> _map;

        private final List<NBHML_Tester2.TestKey> _items;

        private final CyclicBarrier _barrier;

        public TestKeyFeederThread(final List<NBHML_Tester2.TestKey> items, final NonBlockingHashMapLong<NBHML_Tester2.TestKey> map, final CyclicBarrier barrier) {
            _map = map;
            _items = items;
            _barrier = barrier;
        }

        public Integer call() throws Exception {
            _barrier.await();
            int count = 0;
            for (NBHML_Tester2.TestKey item : _items) {
                if (_map.contains(item._id)) {
                    System.err.printf("COLLISION DETECTED: %s exists\n", item.toString());
                }
                final NBHML_Tester2.TestKey exists = _map.putIfAbsent(item._id, item);
                if (exists == null) {
                    count++;
                } else {
                    System.err.printf("COLLISION DETECTED: %s exists as %s\n", item.toString(), exists.toString());
                }
            }
            return count;
        }
    }
}

