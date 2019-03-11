/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import LightWeightGSet.LinkedElement;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing {@link LightWeightCache}
 */
public class TestLightWeightCache {
    private static final long starttime = Time.now();

    private static final long seed = TestLightWeightCache.starttime;

    private static final Random ran = new Random(TestLightWeightCache.seed);

    static {
        TestLightWeightCache.println(((("Start time = " + (new Date(TestLightWeightCache.starttime))) + ", seed=") + (TestLightWeightCache.seed)));
    }

    @Test
    public void testLightWeightCache() {
        // test randomized creation expiration with zero access expiration
        {
            final long creationExpiration = (TestLightWeightCache.ran.nextInt(1024)) + 1;
            TestLightWeightCache.check(1, creationExpiration, 0L, (1 << 10), 65537);
            TestLightWeightCache.check(17, creationExpiration, 0L, (1 << 16), 17);
            TestLightWeightCache.check(255, creationExpiration, 0L, (1 << 16), 65537);
        }
        // test randomized creation/access expiration periods
        for (int i = 0; i < 3; i++) {
            final long creationExpiration = (TestLightWeightCache.ran.nextInt(1024)) + 1;
            final long accessExpiration = (TestLightWeightCache.ran.nextInt(1024)) + 1;
            TestLightWeightCache.check(1, creationExpiration, accessExpiration, (1 << 10), 65537);
            TestLightWeightCache.check(17, creationExpiration, accessExpiration, (1 << 16), 17);
            TestLightWeightCache.check(255, creationExpiration, accessExpiration, (1 << 16), 65537);
        }
        // test size limit
        final int dataSize = 1 << 16;
        for (int i = 0; i < 10; i++) {
            final int modulus = (TestLightWeightCache.ran.nextInt(1024)) + 1;
            final int sizeLimit = (TestLightWeightCache.ran.nextInt(modulus)) + 1;
            TestLightWeightCache.checkSizeLimit(sizeLimit, dataSize, modulus);
        }
    }

    /**
     * The test case contains two data structures, a cache and a hashMap.
     * The hashMap is used to verify the correctness of the cache.  Note that
     * no automatic eviction is performed in the hashMap.  Thus, we have
     * (1) If an entry exists in cache, it MUST exist in the hashMap.
     * (2) If an entry does not exist in the cache, it may or may not exist in the
     *     hashMap.  If it exists, it must be expired.
     */
    private static class LightWeightCacheTestCase implements GSet<TestLightWeightCache.IntEntry, TestLightWeightCache.IntEntry> {
        /**
         * hashMap will not evict entries automatically.
         */
        final GSet<TestLightWeightCache.IntEntry, TestLightWeightCache.IntEntry> hashMap = new GSetByHashMap<TestLightWeightCache.IntEntry, TestLightWeightCache.IntEntry>(1024, 0.75F);

        final LightWeightCache<TestLightWeightCache.IntEntry, TestLightWeightCache.IntEntry> cache;

        final TestLightWeightCache.IntData data;

        final String info;

        final long starttime = Time.now();

        /**
         * Determine the probability in {@link #check()}.
         */
        final int denominator;

        int iterate_count = 0;

        int contain_count = 0;

        private FakeTimer fakeTimer = new FakeTimer();

        LightWeightCacheTestCase(int tablelength, int sizeLimit, long creationExpirationPeriod, long accessExpirationPeriod, int datasize, int modulus) {
            denominator = Math.min(((datasize >> 7) + 1), (1 << 16));
            info = ((((((((((((((getClass().getSimpleName()) + "(") + (new Date(starttime))) + "): tablelength=") + tablelength) + ", creationExpirationPeriod=") + creationExpirationPeriod) + ", accessExpirationPeriod=") + accessExpirationPeriod) + ", datasize=") + datasize) + ", modulus=") + modulus) + ", denominator=") + (denominator);
            TestLightWeightCache.println(info);
            data = new TestLightWeightCache.IntData(datasize, modulus);
            cache = new LightWeightCache<TestLightWeightCache.IntEntry, TestLightWeightCache.IntEntry>(tablelength, sizeLimit, creationExpirationPeriod, 0, fakeTimer);
            Assert.assertEquals(0, cache.size());
        }

        private boolean containsTest(TestLightWeightCache.IntEntry key) {
            final boolean c = cache.contains(key);
            if (c) {
                Assert.assertTrue(hashMap.contains(key));
            } else {
                final TestLightWeightCache.IntEntry h = hashMap.remove(key);
                if (h != null) {
                    Assert.assertTrue(cache.isExpired(h, fakeTimer.monotonicNowNanos()));
                }
            }
            return c;
        }

        @Override
        public boolean contains(TestLightWeightCache.IntEntry key) {
            final boolean e = containsTest(key);
            check();
            return e;
        }

        private TestLightWeightCache.IntEntry getTest(TestLightWeightCache.IntEntry key) {
            final TestLightWeightCache.IntEntry c = cache.get(key);
            if (c != null) {
                Assert.assertEquals(hashMap.get(key).id, c.id);
            } else {
                final TestLightWeightCache.IntEntry h = hashMap.remove(key);
                if (h != null) {
                    Assert.assertTrue(cache.isExpired(h, fakeTimer.monotonicNowNanos()));
                }
            }
            return c;
        }

        @Override
        public TestLightWeightCache.IntEntry get(TestLightWeightCache.IntEntry key) {
            final TestLightWeightCache.IntEntry e = getTest(key);
            check();
            return e;
        }

        private TestLightWeightCache.IntEntry putTest(TestLightWeightCache.IntEntry entry) {
            final TestLightWeightCache.IntEntry c = cache.put(entry);
            if (c != null) {
                Assert.assertEquals(hashMap.put(entry).id, c.id);
            } else {
                final TestLightWeightCache.IntEntry h = hashMap.put(entry);
                if ((h != null) && (h != entry)) {
                    // if h == entry, its expiration time is already updated
                    Assert.assertTrue(cache.isExpired(h, fakeTimer.monotonicNowNanos()));
                }
            }
            return c;
        }

        @Override
        public TestLightWeightCache.IntEntry put(TestLightWeightCache.IntEntry entry) {
            final TestLightWeightCache.IntEntry e = putTest(entry);
            check();
            return e;
        }

        private TestLightWeightCache.IntEntry removeTest(TestLightWeightCache.IntEntry key) {
            final TestLightWeightCache.IntEntry c = cache.remove(key);
            if (c != null) {
                Assert.assertEquals(c.id, hashMap.remove(key).id);
            } else {
                final TestLightWeightCache.IntEntry h = hashMap.remove(key);
                if (h != null) {
                    Assert.assertTrue(cache.isExpired(h, fakeTimer.monotonicNowNanos()));
                }
            }
            return c;
        }

        @Override
        public TestLightWeightCache.IntEntry remove(TestLightWeightCache.IntEntry key) {
            final TestLightWeightCache.IntEntry e = removeTest(key);
            check();
            return e;
        }

        private int sizeTest() {
            final int c = cache.size();
            Assert.assertTrue(((hashMap.size()) >= c));
            return c;
        }

        @Override
        public int size() {
            final int s = sizeTest();
            check();
            return s;
        }

        @Override
        public Iterator<TestLightWeightCache.IntEntry> iterator() {
            throw new UnsupportedOperationException();
        }

        boolean tossCoin() {
            return (TestLightWeightCache.ran.nextInt(denominator)) == 0;
        }

        void check() {
            fakeTimer.advanceNanos(((TestLightWeightCache.ran.nextInt()) & 3));
            // test size
            sizeTest();
            if (tossCoin()) {
                // test get(..), check content and test iterator
                (iterate_count)++;
                for (TestLightWeightCache.IntEntry i : cache) {
                    getTest(i);
                }
            }
            if (tossCoin()) {
                // test contains(..)
                (contain_count)++;
                final int count = Math.min(data.size(), 1000);
                if (count == (data.size())) {
                    for (TestLightWeightCache.IntEntry i : data.integers) {
                        containsTest(i);
                    }
                } else {
                    for (int j = 0; j < count; j++) {
                        containsTest(data.get(TestLightWeightCache.ran.nextInt(data.size())));
                    }
                }
            }
        }

        String stat() {
            final long t = (Time.now()) - (starttime);
            return String.format(" iterate=%5d, contain=%5d, time elapsed=%5d.%03ds", iterate_count, contain_count, (t / 1000), (t % 1000));
        }

        @Override
        public void clear() {
            hashMap.clear();
            cache.clear();
            Assert.assertEquals(0, size());
        }

        @Override
        public Collection<TestLightWeightCache.IntEntry> values() {
            throw new UnsupportedOperationException();
        }
    }

    private static class IntData {
        final TestLightWeightCache.IntEntry[] integers;

        IntData(int size, int modulus) {
            integers = new TestLightWeightCache.IntEntry[size];
            for (int i = 0; i < (integers.length); i++) {
                integers[i] = new TestLightWeightCache.IntEntry(i, TestLightWeightCache.ran.nextInt(modulus));
            }
        }

        TestLightWeightCache.IntEntry get(int i) {
            return integers[i];
        }

        int size() {
            return integers.length;
        }
    }

    /**
     * Entries of {@link LightWeightCache} in this test
     */
    private static class IntEntry implements Comparable<TestLightWeightCache.IntEntry> , LightWeightCache.Entry {
        private LinkedElement next;

        final int id;

        final int value;

        private long expirationTime = 0;

        IntEntry(int id, int value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj != null) && (obj instanceof TestLightWeightCache.IntEntry)) && ((value) == (((TestLightWeightCache.IntEntry) (obj)).value));
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public int compareTo(TestLightWeightCache.IntEntry that) {
            return (value) - (that.value);
        }

        @Override
        public String toString() {
            return ((((id) + "#") + (value)) + ",expirationTime=") + (expirationTime);
        }

        @Override
        public LinkedElement getNext() {
            return next;
        }

        @Override
        public void setNext(LightWeightGSet.LinkedElement e) {
            next = e;
        }

        @Override
        public void setExpirationTime(long timeNano) {
            this.expirationTime = timeNano;
        }

        @Override
        public long getExpirationTime() {
            return expirationTime;
        }
    }
}

