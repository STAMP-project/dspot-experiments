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
import org.jctools.maps.NonBlockingHashMap;
import org.junit.Assert;
import org.junit.Test;


// Test NonBlockingHashMap via JUnit
// This test is a copy of the JCK test Hashtable2027, which is incorrect.
// The test requires a particular order of values to appear in the esa
// array - but this is not part of the spec.  A different implementation
// might put the same values into the array but in a different order.
// public void testToArray() {
// NonBlockingHashMap ht = new NonBlockingHashMap();
// 
// ht.put("Nine", new Integer(9));
// ht.put("Ten", new Integer(10));
// ht.put("Ten1", new Integer(100));
// 
// Collection es = ht.values();
// 
// Object [] esa = es.toArray();
// 
// ht.remove("Ten1");
// 
// assertEquals( "size check", es.size(), 2 );
// assertEquals( "iterator_order[0]", new Integer( 9), esa[0] );
// assertEquals( "iterator_order[1]", new Integer(10), esa[1] );
// }
public class NBHM_Tester2 {
    private static NonBlockingHashMap<String, String> _nbhm;

    // Test some basic stuff; add a few keys, remove a few keys
    @Test
    public void testBasic() {
        Assert.assertTrue(NBHM_Tester2._nbhm.isEmpty());
        Assert.assertThat(NBHM_Tester2._nbhm.putIfAbsent("k1", "v1"), CoreMatchers.nullValue());
        checkSizes(1);
        Assert.assertThat(NBHM_Tester2._nbhm.putIfAbsent("k2", "v2"), CoreMatchers.nullValue());
        checkSizes(2);
        Assert.assertTrue(NBHM_Tester2._nbhm.containsKey("k2"));
        Assert.assertThat(NBHM_Tester2._nbhm.put("k1", "v1a"), CoreMatchers.is("v1"));
        Assert.assertThat(NBHM_Tester2._nbhm.put("k2", "v2a"), CoreMatchers.is("v2"));
        checkSizes(2);
        Assert.assertThat(NBHM_Tester2._nbhm.putIfAbsent("k2", "v2b"), CoreMatchers.is("v2a"));
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k1"), CoreMatchers.is("v1a"));
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k1"));
        checkSizes(1);
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k1"), CoreMatchers.nullValue());
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k2"), CoreMatchers.is("v2a"));
        checkSizes(0);
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k2"), CoreMatchers.nullValue());
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k3"), CoreMatchers.nullValue());
        Assert.assertTrue(NBHM_Tester2._nbhm.isEmpty());
        Assert.assertThat(NBHM_Tester2._nbhm.put("k0", "v0"), CoreMatchers.nullValue());
        Assert.assertTrue(NBHM_Tester2._nbhm.containsKey("k0"));
        checkSizes(1);
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k0"), CoreMatchers.is("v0"));
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k0"));
        checkSizes(0);
        Assert.assertThat(NBHM_Tester2._nbhm.replace("k0", "v0"), CoreMatchers.nullValue());
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k0"));
        Assert.assertThat(NBHM_Tester2._nbhm.put("k0", "v0"), CoreMatchers.nullValue());
        Assert.assertEquals(NBHM_Tester2._nbhm.replace("k0", "v0a"), "v0");
        Assert.assertEquals(NBHM_Tester2._nbhm.get("k0"), "v0a");
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k0"), CoreMatchers.is("v0a"));
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k0"));
        checkSizes(0);
        Assert.assertThat(NBHM_Tester2._nbhm.replace("k1", "v1"), CoreMatchers.nullValue());
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k1"));
        Assert.assertThat(NBHM_Tester2._nbhm.put("k1", "v1"), CoreMatchers.nullValue());
        Assert.assertEquals(NBHM_Tester2._nbhm.replace("k1", "v1a"), "v1");
        Assert.assertEquals(NBHM_Tester2._nbhm.get("k1"), "v1a");
        Assert.assertThat(NBHM_Tester2._nbhm.remove("k1"), CoreMatchers.is("v1a"));
        Assert.assertFalse(NBHM_Tester2._nbhm.containsKey("k1"));
        checkSizes(0);
        // Insert & Remove KeyBonks until the table resizes and we start
        // finding Tombstone keys- and KeyBonk's equals-call with throw a
        // ClassCastException if it sees a non-KeyBonk.
        NonBlockingHashMap<NBHM_Tester2.KeyBonk, String> dumb = new NonBlockingHashMap();
        for (int i = 0; i < 10000; i++) {
            final NBHM_Tester2.KeyBonk happy1 = new NBHM_Tester2.KeyBonk(i);
            Assert.assertThat(dumb.put(happy1, "and"), CoreMatchers.nullValue());
            if ((i & 1) == 0) {
                dumb.remove(happy1);
            }
            final NBHM_Tester2.KeyBonk happy2 = new NBHM_Tester2.KeyBonk(i);// 'equals' but not '=='

            dumb.get(happy2);
        }
        // Simple insert of simple keys, with no reprobing on insert until the
        // table gets full exactly.  Then do a 'get' on the totally full table.
        NonBlockingHashMap<Integer, Object> map = new NonBlockingHashMap(32);
        for (int i = 1; i < 32; i++) {
            map.put(i, new Object());
        }
        map.get(33);// this returns null, but tested a crash edge case for expansion

    }

    @Test
    public void testIteration() {
        Assert.assertTrue(NBHM_Tester2._nbhm.isEmpty());
        Assert.assertThat(NBHM_Tester2._nbhm.put("k1", "v1"), CoreMatchers.nullValue());
        Assert.assertThat(NBHM_Tester2._nbhm.put("k2", "v2"), CoreMatchers.nullValue());
        String str1 = "";
        for (Map.Entry<String, String> e : NBHM_Tester2._nbhm.entrySet()) {
            str1 += e.getKey();
        }
        Assert.assertThat("found all entries", str1, CoreMatchers.anyOf(CoreMatchers.is("k1k2"), CoreMatchers.is("k2k1")));
        String str2 = "";
        for (String key : NBHM_Tester2._nbhm.keySet()) {
            str2 += key;
        }
        Assert.assertThat("found all keys", str2, CoreMatchers.anyOf(CoreMatchers.is("k1k2"), CoreMatchers.is("k2k1")));
        String str3 = "";
        for (String val : NBHM_Tester2._nbhm.values()) {
            str3 += val;
        }
        Assert.assertThat("found all vals", str3, CoreMatchers.anyOf(CoreMatchers.is("v1v2"), CoreMatchers.is("v2v1")));
        Assert.assertThat("toString works", NBHM_Tester2._nbhm.toString(), CoreMatchers.anyOf(CoreMatchers.is("{k1=v1, k2=v2}"), CoreMatchers.is("{k2=v2, k1=v1}")));
        NBHM_Tester2._nbhm.clear();
    }

    @Test
    public void testSerial() {
        Assert.assertTrue(NBHM_Tester2._nbhm.isEmpty());
        Assert.assertThat(NBHM_Tester2._nbhm.put("k1", "v1"), CoreMatchers.nullValue());
        Assert.assertThat(NBHM_Tester2._nbhm.put("k2", "v2"), CoreMatchers.nullValue());
        // Serialize it out
        try {
            FileOutputStream fos = new FileOutputStream("NBHM_test.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(NBHM_Tester2._nbhm);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Read it back
        try {
            File f = new File("NBHM_test.txt");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            NonBlockingHashMap nbhm = ((NonBlockingHashMap) (in.readObject()));
            in.close();
            Assert.assertEquals(NBHM_Tester2._nbhm.toString(), nbhm.toString());
            if (!(f.delete())) {
                throw new IOException("delete failed");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testIterationBig2() {
        final int CNT = 10000;
        NonBlockingHashMap<Integer, String> nbhm = new NonBlockingHashMap();
        final String v = "v";
        for (int i = 0; i < CNT; i++) {
            final Integer z = i;
            String s0 = nbhm.get(z);
            Assert.assertThat(s0, CoreMatchers.nullValue());
            nbhm.put(z, v);
            String s1 = nbhm.get(z);
            Assert.assertThat(s1, CoreMatchers.is(v));
        }
        Assert.assertThat(nbhm.size(), CoreMatchers.is(CNT));
    }

    @Test
    public void testIterationBig() {
        final int CNT = 10000;
        Assert.assertThat(NBHM_Tester2._nbhm.size(), CoreMatchers.is(0));
        for (int i = 0; i < CNT; i++) {
            NBHM_Tester2._nbhm.put(("k" + i), ("v" + i));
        }
        Assert.assertThat(NBHM_Tester2._nbhm.size(), CoreMatchers.is(CNT));
        int sz = 0;
        int sum = 0;
        for (String s : NBHM_Tester2._nbhm.keySet()) {
            sz++;
            Assert.assertThat("", s.charAt(0), CoreMatchers.is('k'));
            int x = Integer.parseInt(s.substring(1));
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= (CNT - 1))));
        }
        Assert.assertThat("Found 10000 ints", sz, CoreMatchers.is(CNT));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is(((CNT * (CNT - 1)) / 2)));
        Assert.assertThat("can remove 3", NBHM_Tester2._nbhm.remove("k3"), CoreMatchers.is("v3"));
        Assert.assertThat("can remove 4", NBHM_Tester2._nbhm.remove("k4"), CoreMatchers.is("v4"));
        sz = 0;
        sum = 0;
        for (String s : NBHM_Tester2._nbhm.keySet()) {
            sz++;
            Assert.assertThat("", s.charAt(0), CoreMatchers.is('k'));
            int x = Integer.parseInt(s.substring(1));
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= (CNT - 1))));
            String v = NBHM_Tester2._nbhm.get(s);
            Assert.assertThat("", v.charAt(0), CoreMatchers.is('v'));
            Assert.assertThat("", s.substring(1), CoreMatchers.is(v.substring(1)));
        }
        Assert.assertThat((("Found " + (CNT - 2)) + " ints"), sz, CoreMatchers.is((CNT - 2)));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is((((CNT * (CNT - 1)) / 2) - (3 + 4))));
        NBHM_Tester2._nbhm.clear();
    }

    // Do some simple concurrent testing
    @Test
    public void testConcurrentSimple() throws InterruptedException {
        final NonBlockingHashMap<String, String> nbhm = new NonBlockingHashMap();
        // In 2 threads, add & remove even & odd elements concurrently
        Thread t1 = new Thread() {
            public void run() {
                work_helper(nbhm, "T1", 1);
            }
        };
        t1.start();
        work_helper(nbhm, "T0", 0);
        t1.join();
        // In the end, all members should be removed
        StringBuilder buf = new StringBuilder();
        buf.append("Should be emptyset but has these elements: {");
        boolean found = false;
        for (String x : nbhm.keySet()) {
            buf.append(" ").append(x);
            found = true;
        }
        if (found) {
            System.out.println((buf + " }"));
        }
        Assert.assertThat("concurrent size=0", nbhm.size(), CoreMatchers.is(0));
        Assert.assertThat("keyset size=0", nbhm.keySet().size(), CoreMatchers.is(0));
    }

    @Test
    public final void testNonBlockingHashMapSize() {
        NonBlockingHashMap<Long, String> items = new NonBlockingHashMap();
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

    // Concurrent insertion & then iterator test.
    @Test
    public void testNonBlockingHashMapIterator() throws InterruptedException {
        final int ITEM_COUNT1 = 1000;
        final int THREAD_COUNT = 5;
        final int PER_CNT = ITEM_COUNT1 / THREAD_COUNT;
        final int ITEM_COUNT = PER_CNT * THREAD_COUNT;// fix roundoff for odd thread counts

        NonBlockingHashMap<Long, NBHM_Tester2.TestKey> nbhml = new NonBlockingHashMap();
        // use a barrier to open the gate for all threads at once to avoid rolling
        // start and no actual concurrency
        final CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
        final ExecutorService ex = Executors.newFixedThreadPool(THREAD_COUNT);
        final CompletionService<Object> co = new ExecutorCompletionService<>(ex);
        for (int i = 0; i < THREAD_COUNT; i++) {
            co.submit(new NBHM_Tester2.NBHMLFeeder(nbhml, PER_CNT, barrier, (i * PER_CNT)));
        }
        for (int retCount = 0; retCount < THREAD_COUNT; retCount++) {
            co.take();
        }
        ex.shutdown();
        Assert.assertEquals("values().size()", ITEM_COUNT, nbhml.values().size());
        Assert.assertEquals("entrySet().size()", ITEM_COUNT, nbhml.entrySet().size());
        int itemCount = 0;
        for (NBHM_Tester2.TestKey K : nbhml.values()) {
            itemCount++;
        }
        Assert.assertEquals("values().iterator() count", ITEM_COUNT, itemCount);
    }

    // ---
    @Test
    public void testNonBlockingHashMapIteratorMultithreaded() throws InterruptedException, ExecutionException {
        NBHM_Tester2.TestKeyFeeder feeder = getTestKeyFeeder();
        final int itemCount = feeder.size();
        // validate results
        final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> items = feeder.getMapMultithreaded();
        Assert.assertEquals("size()", itemCount, items.size());
        Assert.assertEquals("values().size()", itemCount, items.values().size());
        Assert.assertEquals("entrySet().size()", itemCount, items.entrySet().size());
        int iteratorCount = 0;
        for (NBHM_Tester2.TestKey m : items.values()) {
            iteratorCount++;
        }
        // sometimes a different result comes back the second time
        int iteratorCount2 = 0;
        for (NBHM_Tester2.TestKey m2 : items.values()) {
            iteratorCount2++;
        }
        Assert.assertEquals("iterator counts differ", iteratorCount, iteratorCount2);
        Assert.assertEquals("values().iterator() count", itemCount, iteratorCount);
    }

    // --- Tests on equality of values
    @Test
    public void replaceResultIsBasedOnEquality() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap();
        Integer initialValue = new Integer(10);
        map.put(1, initialValue);
        Assert.assertTrue(map.replace(1, initialValue, 20));
        Assert.assertTrue(map.replace(1, new Integer(20), 30));
    }

    @Test
    public void removeResultIsBasedOnEquality() {
        NonBlockingHashMap<Integer, Integer> map = new NonBlockingHashMap();
        Integer initialValue = new Integer(10);
        map.put(1, initialValue);
        Assert.assertTrue(map.remove(1, initialValue));
        map.put(1, initialValue);
        Assert.assertTrue(map.remove(1, new Integer(10)));
    }

    // Throw a ClassCastException if I see a tombstone during key-compares
    private static class KeyBonk {
        final int _x;

        KeyBonk(int i) {
            _x = i;
        }

        public int hashCode() {
            return (_x) >> 2;
        }

        public boolean equals(Object o) {
            return (o != null) && ((((NBHM_Tester2.KeyBonk) (o))._x)// Throw CCE here
             == (this._x));
        }

        public String toString() {
            return "Bonk_" + (Integer.toString(_x));
        }
    }

    // --- NBHMLFeeder ---
    // Class to be called from another thread, to get concurrent installs into
    // the table.
    private static class NBHMLFeeder implements Callable<Object> {
        private static final Random _rand = new Random(System.currentTimeMillis());

        private final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> _map;

        private final int _count;

        private final CyclicBarrier _barrier;

        private final long _offset;

        public NBHMLFeeder(final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> map, final int count, final CyclicBarrier barrier, final long offset) {
            _map = map;
            _count = count;
            _barrier = barrier;
            _offset = offset;
        }

        public Object call() throws Exception {
            _barrier.await();// barrier, to force racing start

            for (long j = 0; j < (_count); j++) {
                _map.put((j + (_offset)), new NBHM_Tester2.TestKey(NBHM_Tester2.NBHMLFeeder._rand.nextLong(), NBHM_Tester2.NBHMLFeeder._rand.nextInt(), ((short) (NBHM_Tester2.NBHMLFeeder._rand.nextInt(Short.MAX_VALUE)))));
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
            final NBHM_Tester2.TestKey other = ((NBHM_Tester2.TestKey) (object));
            return ((this._type) == (other._type)) && ((this._id) == (other._id));
        }

        public String toString() {
            return String.format("%s:%d,%d,%d", getClass().getSimpleName(), _id, _type, _hash);
        }
    }

    // ---
    private static class TestKeyFeeder {
        private final Hashtable<Integer, List<NBHM_Tester2.TestKey>> _items = new Hashtable<>();

        private int _size = 0;

        public int size() {
            return _size;
        }

        // Put items into the hashtable, sorted by 'type' into LinkedLists.
        public void checkedPut(final long id, final int type, final int hash) {
            (_size)++;
            final NBHM_Tester2.TestKey item = new NBHM_Tester2.TestKey(id, type, hash);
            if (!(_items.containsKey(type))) {
                _items.put(type, new LinkedList<>());
            }
            _items.get(type).add(item);
        }

        public NonBlockingHashMap<Long, NBHM_Tester2.TestKey> getMapMultithreaded() throws InterruptedException, ExecutionException {
            final int threadCount = _items.keySet().size();
            final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> map = new NonBlockingHashMap();
            // use a barrier to open the gate for all threads at once to avoid rolling start and no actual concurrency
            final CyclicBarrier barrier = new CyclicBarrier(threadCount);
            final ExecutorService ex = Executors.newFixedThreadPool(threadCount);
            final CompletionService<Integer> co = new ExecutorCompletionService<>(ex);
            for (Integer type : _items.keySet()) {
                // A linked-list of things to insert
                List<NBHM_Tester2.TestKey> items = _items.get(type);
                NBHM_Tester2.TestKeyFeederThread feeder = new NBHM_Tester2.TestKeyFeederThread(items, map, barrier);
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
        private final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> _map;

        private final List<NBHM_Tester2.TestKey> _items;

        private final CyclicBarrier _barrier;

        public TestKeyFeederThread(final List<NBHM_Tester2.TestKey> items, final NonBlockingHashMap<Long, NBHM_Tester2.TestKey> map, final CyclicBarrier barrier) {
            _map = map;
            _items = items;
            _barrier = barrier;
        }

        public Integer call() throws Exception {
            _barrier.await();
            int count = 0;
            for (NBHM_Tester2.TestKey item : _items) {
                if (_map.contains(item._id)) {
                    System.err.printf("COLLISION DETECTED: %s exists\n", item.toString());
                }
                final NBHM_Tester2.TestKey exists = _map.putIfAbsent(item._id, item);
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

