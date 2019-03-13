/**
 * The MIT License
 *
 * Copyright (c) 2012, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.model.lazy;


import Direction.ASC;
import Direction.DESC;
import Direction.EXACT;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import jenkins.util.Timer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class AbstractLazyLoadRunMapTest {
    // A=1, B=3, C=5
    @Rule
    public FakeMapBuilder aBuilder = new FakeMapBuilder();

    private FakeMap a;

    // empty map
    @Rule
    public FakeMapBuilder bBuilder = new FakeMapBuilder();

    private FakeMap b;

    @Rule
    public FakeMapBuilder localBuilder = new FakeMapBuilder();

    @Rule
    public FakeMapBuilder localExpiredBuilder = new FakeMapBuilder() {
        @Override
        public FakeMap make() {
            assert (getDir()) != null;
            return new FakeMap(getDir()) {
                @Override
                protected BuildReference<Build> createReference(Build r) {
                    return /* pretend referent expired */
                    new BuildReference<Build>(Integer.toString(r.n), null);
                }
            };
        }
    };

    private final Map<Integer, Semaphore> slowBuilderStartSemaphores = new HashMap<>();

    private final Map<Integer, Semaphore> slowBuilderEndSemaphores = new HashMap<>();

    private final Map<Integer, AtomicInteger> slowBuilderLoadCount = new HashMap<>();

    @Rule
    public FakeMapBuilder slowBuilder = new FakeMapBuilder() {
        @Override
        public FakeMap make() {
            return new FakeMap(getDir()) {
                @Override
                protected Build retrieve(File dir) throws IOException {
                    Build b = super.retrieve(dir);
                    slowBuilderStartSemaphores.get(b.n).release();
                    try {
                        slowBuilderEndSemaphores.get(b.n).acquire();
                    } catch (InterruptedException x) {
                        throw new IOException(x);
                    }
                    slowBuilderLoadCount.get(b.n).incrementAndGet();
                    return b;
                }
            };
        }
    };

    @Test
    public void lookup() {
        Assert.assertNull(a.get(0));
        a.get(1).asserts(1);
        Assert.assertNull(a.get(2));
        a.get(3).asserts(3);
        Assert.assertNull(a.get(4));
        a.get(5).asserts(5);
        Assert.assertNull(a.get(6));
        Assert.assertNull(b.get(1));
        Assert.assertNull(b.get(3));
        Assert.assertNull(b.get(5));
    }

    @Test
    public void lookup2() {
        Assert.assertNull(a.get(6));
    }

    @Test
    public void idempotentLookup() {
        for (int i = 0; i < 5; i++) {
            a.get(1).asserts(1);
            a.get(((Object) (1))).asserts(1);
        }
    }

    @Test
    public void lookupWithBogusKeyType() {
        Assert.assertNull(a.get(null));
        Assert.assertNull(a.get("foo"));
        Assert.assertNull(get(this));
    }

    @Test
    public void firstKey() {
        Assert.assertEquals(5, a.firstKey().intValue());
        try {
            b.firstKey();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // as expected
        }
    }

    @Issue("JENKINS-26690")
    @Test
    public void headMap() {
        Assert.assertEquals("[]", a.headMap(Integer.MAX_VALUE).keySet().toString());
        Assert.assertEquals("[]", a.headMap(6).keySet().toString());
        Assert.assertEquals("[]", a.headMap(5).keySet().toString());
        Assert.assertEquals("[5]", a.headMap(4).keySet().toString());
        Assert.assertEquals("[5]", a.headMap(3).keySet().toString());
        Assert.assertEquals("[5, 3]", a.headMap(2).keySet().toString());
        Assert.assertEquals("[5, 3]", a.headMap(1).keySet().toString());
        Assert.assertEquals("[5, 3, 1]", a.headMap(0).keySet().toString());
        Assert.assertEquals("[5, 3, 1]", a.headMap((-1)).keySet().toString());
        Assert.assertEquals("[5, 3, 1]", a.headMap((-2)).keySet().toString());// this failed

        Assert.assertEquals("[5, 3, 1]", a.headMap(Integer.MIN_VALUE).keySet().toString());
    }

    @Test
    public void lastKey() {
        Assert.assertEquals(1, a.lastKey().intValue());
        try {
            b.lastKey();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // as expected
        }
    }

    @Test
    public void search() {
        // searching toward non-existent direction
        Assert.assertNull(a.search(99, ASC));
        Assert.assertNull(a.search((-99), DESC));
    }

    @Issue("JENKINS-19418")
    @Test
    public void searchExactWhenIndexedButSoftReferenceExpired() throws IOException {
        final FakeMap m = localExpiredBuilder.add(1).add(2).make();
        // force index creation
        entrySet();
        m.search(1, EXACT).asserts(1);
        Assert.assertNull(m.search(3, EXACT));
        Assert.assertNull(m.search(0, EXACT));
    }

    @Issue("JENKINS-22681")
    @Test
    public void exactSearchShouldNotReload() throws Exception {
        FakeMap m = localBuilder.add(1).add(2).make();
        Assert.assertNull(m.search(0, EXACT));
        Build a = m.search(1, EXACT);
        a.asserts(1);
        Build b = m.search(2, EXACT);
        b.asserts(2);
        Assert.assertNull(m.search(0, EXACT));
        Assert.assertSame(a, m.search(1, EXACT));
        Assert.assertSame(b, m.search(2, EXACT));
        Assert.assertNull(m.search(3, EXACT));
        Assert.assertNull(m.search(0, EXACT));
        Assert.assertSame(a, m.search(1, EXACT));
        Assert.assertSame("#2 should not have been reloaded by searching for #3", b, m.search(2, EXACT));
        Assert.assertNull(m.search(3, EXACT));
    }

    /**
     * If load fails, search needs to gracefully handle it
     */
    @Test
    public void unloadableData() throws IOException {
        FakeMap m = localBuilder.add(1).addUnloadable(3).add(5).make();
        Assert.assertNull(m.search(3, EXACT));
        m.search(3, DESC).asserts(1);
        m.search(3, ASC).asserts(5);
    }

    @Test
    public void eagerLoading() throws IOException {
        Map.Entry[] b = entrySet().toArray(new Map.Entry[3]);
        ((Build) (b[0].getValue())).asserts(5);
        ((Build) (b[1].getValue())).asserts(3);
        ((Build) (b[2].getValue())).asserts(1);
    }

    @Test
    public void fastSubMap() throws Exception {
        SortedMap<Integer, Build> m = subMap(99, 2);
        Assert.assertEquals(2, m.size());
        Build[] b = m.values().toArray(new Build[2]);
        Assert.assertEquals(2, b.length);
        b[0].asserts(5);
        b[1].asserts(3);
    }

    @Test
    public void identity() {
        Assert.assertTrue(a.equals(a));
        Assert.assertTrue((!(a.equals(b))));
        a.hashCode();
        b.hashCode();
    }

    @Issue("JENKINS-15439")
    @Test
    public void indexOutOfBounds() throws Exception {
        FakeMapBuilder f = localBuilder;
        f.add(100).addUnloadable(150).addUnloadable(151).addUnloadable(152).addUnloadable(153).addUnloadable(154).addUnloadable(155).add(200).add(201);
        FakeMap map = f.make();
        Build x = map.search(Integer.MAX_VALUE, DESC);
        assert (x.n) == 201;
    }

    @Issue("JENKINS-18065")
    @Test
    public void all() throws Exception {
        Assert.assertEquals("[]", getLoadedBuilds().keySet().toString());
        Set<Map.Entry<Integer, Build>> entries = a.entrySet();
        Assert.assertEquals("[]", getLoadedBuilds().keySet().toString());
        Assert.assertFalse(entries.isEmpty());
        Assert.assertEquals("5 since it is the latest", "[5]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(5, getById("5").n);
        Assert.assertEquals("[5]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(1, getByNumber(1).n);
        Assert.assertEquals("[5, 1]", getLoadedBuilds().keySet().toString());
        purgeCache();
        Assert.assertEquals("[]", getLoadedBuilds().keySet().toString());
        Iterator<Map.Entry<Integer, Build>> iterator = entries.iterator();
        Assert.assertEquals("[5]", getLoadedBuilds().keySet().toString());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("[5]", getLoadedBuilds().keySet().toString());
        Map.Entry<Integer, Build> entry = iterator.next();
        Assert.assertEquals("[5, 3]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(5, entry.getKey().intValue());
        Assert.assertEquals("[5, 3]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(5, entry.getValue().n);
        Assert.assertEquals("[5, 3]", getLoadedBuilds().keySet().toString());
        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals(3, entry.getKey().intValue());
        Assert.assertEquals(".next() precomputes the one after that too", "[5, 3, 1]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(3, entry.getValue().n);
        Assert.assertEquals("[5, 3, 1]", getLoadedBuilds().keySet().toString());
        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals(1, entry.getKey().intValue());
        Assert.assertEquals("[5, 3, 1]", getLoadedBuilds().keySet().toString());
        Assert.assertEquals(1, entry.getValue().n);
        Assert.assertEquals("[5, 3, 1]", getLoadedBuilds().keySet().toString());
        Assert.assertFalse(iterator.hasNext());
    }

    @Issue("JENKINS-18065")
    @Test
    public void entrySetIterator() {
        Iterator<Map.Entry<Integer, Build>> itr = entrySet().iterator();
        // iterator, when created fresh, shouldn't force loading everything
        // this involves binary searching, so it can load several.
        Assert.assertTrue(((getLoadedBuilds().size()) < 3));
        // check if the first entry is legit
        Assert.assertTrue(itr.hasNext());
        Map.Entry<Integer, Build> e = itr.next();
        Assert.assertEquals(((Integer) (5)), e.getKey());
        e.getValue().asserts(5);
        // now that the first entry is returned, we expect there to be two loaded
        Assert.assertTrue(((getLoadedBuilds().size()) < 3));
        // check if the second entry is legit
        Assert.assertTrue(itr.hasNext());
        e = itr.next();
        Assert.assertEquals(((Integer) (3)), e.getKey());
        e.getValue().asserts(3);
        // repeat the process for the third one
        Assert.assertTrue(((getLoadedBuilds().size()) <= 3));
        // check if the third entry is legit
        Assert.assertTrue(itr.hasNext());
        e = itr.next();
        Assert.assertEquals(((Integer) (1)), e.getKey());
        e.getValue().asserts(1);
        Assert.assertFalse(itr.hasNext());
        Assert.assertEquals(3, getLoadedBuilds().size());
    }

    @Issue("JENKINS-18065")
    @Test
    public void entrySetEmpty() {
        // entrySet().isEmpty() shouldn't cause full data load
        Assert.assertFalse(entrySet().isEmpty());
        Assert.assertTrue(((getLoadedBuilds().size()) < 3));
    }

    @Issue("JENKINS-18065")
    @Test
    public void entrySetSize() {
        Assert.assertEquals(3, entrySet().size());
        Assert.assertEquals(0, entrySet().size());
    }

    @Issue("JENKINS-25655")
    @Test
    public void entrySetChanges() {
        Assert.assertEquals(3, entrySet().size());
        put(new Build(7));
        Assert.assertEquals(4, entrySet().size());
    }

    @Issue("JENKINS-18065")
    @Test
    public void entrySetContains() {
        for (Map.Entry<Integer, Build> e : entrySet()) {
            Assert.assertTrue(entrySet().contains(e));
        }
    }

    @Issue("JENKINS-22767")
    @Test
    public void slowRetrieve() throws Exception {
        for (int i = 1; i <= 3; i++) {
            slowBuilder.add(i);
            slowBuilderStartSemaphores.put(i, new Semaphore(0));
            slowBuilderEndSemaphores.put(i, new Semaphore(0));
            slowBuilderLoadCount.put(i, new AtomicInteger());
        }
        final FakeMap m = slowBuilder.make();
        Future<Build> firstLoad = Timer.get().submit(new Callable<Build>() {
            @Override
            public Build call() throws Exception {
                return m.getByNumber(2);
            }
        });
        Future<Build> secondLoad = Timer.get().submit(new Callable<Build>() {
            @Override
            public Build call() throws Exception {
                return m.getByNumber(2);
            }
        });
        slowBuilderStartSemaphores.get(2).acquire(1);
        // now one of them is inside retrieve(?); the other is waiting for the lock
        slowBuilderEndSemaphores.get(2).release(2);// allow both to proceed

        Build first = firstLoad.get();
        Build second = secondLoad.get();
        Assert.assertEquals(1, slowBuilderLoadCount.get(2).get());
        Assert.assertSame(second, first);
    }
}

