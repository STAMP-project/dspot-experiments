/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.map;


import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Ints;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Predicate;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.set.Builder;
import net.openhft.chronicle.values.Values;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored" })
public class ChronicleMapTest {
    static final LongValue ONE = Values.newHeapInstance(LongValue.class);

    static long count = 0;

    static {
        ChronicleMapTest.ONE.setValue(1);
    }

    private StringBuilder sb = new StringBuilder();

    @Test
    public void testRemoveWithKey() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(10).averageKey("key1").averageValue("one").minSegments(2).create()) {
            Assert.assertFalse(map.containsKey("key3"));
            map.put("key1", "one");
            map.put("key2", "two");
            Assert.assertEquals(2, map.size());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertFalse(map.containsKey("key3"));
            Assert.assertEquals("one", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            final CharSequence result = map.remove("key1");
            Assert.assertEquals(1, map.size());
            Assert.assertEquals("one", result.toString());
            Assert.assertFalse(map.containsKey("key1"));
            Assert.assertEquals(null, map.get("key1"));
            Assert.assertEquals("two", map.get("key2").toString());
            Assert.assertFalse(map.containsKey("key3"));
            // lets add one more item for luck !
            map.put("key3", "three");
            Assert.assertEquals("three", map.get("key3").toString());
            Assert.assertTrue(map.containsKey("key3"));
            Assert.assertEquals(2, map.size());
            // and just for kicks we'll overwrite what we have
            map.put("key3", "overwritten");
            Assert.assertEquals("overwritten", map.get("key3").toString());
            Assert.assertTrue(map.containsKey("key3"));
            Assert.assertEquals(2, map.size());
        }
    }

    @Test
    public void testByteArrayPersistenceFileReuse() throws IOException {
        final File persistenceFile = Builder.getPersistenceFile();
        for (int i = 0; i < 3; i++) {
            try (ChronicleMap<byte[], byte[]> map = ChronicleMap.of(byte[].class, byte[].class).entries(1).averageKey("hello".getBytes()).averageValue("world".getBytes()).createPersistedTo(persistenceFile)) {
                byte[] o = map.get("hello".getBytes());
                System.out.println((o == null ? "null" : new String(o)));
                map.put("hello".getBytes(), "world".getBytes());
            }
        }
        persistenceFile.delete();
    }

    @Test
    public void testEqualsCharSequence() {
        ChronicleMapBuilder<CharSequence, CharSequence> builder = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(1).averageKey("hello").averageValue("world");
        try (final ChronicleMap<CharSequence, CharSequence> map1 = builder.create()) {
            map1.put("hello", "world");
            try (final ChronicleMap<CharSequence, CharSequence> map2 = builder.create()) {
                map2.put("hello", "world");
                Assert.assertEquals(map1, map2);
            }
        }
    }

    @Test
    public void testEqualsCharArray() {
        char[] value = new char[5];
        Arrays.fill(value, 'X');
        ChronicleMapBuilder<CharSequence, char[]> builder = ChronicleMapBuilder.of(CharSequence.class, char[].class).entries(1).averageKey("hello").averageValue(value);
        try (final ChronicleMap<CharSequence, char[]> map1 = builder.create()) {
            map1.put("hello", value);
            try (final ChronicleMap<CharSequence, char[]> map2 = builder.create()) {
                map2.put("hello", value);
                Assert.assertEquals(map1, map2);
            }
        }
    }

    @Test
    public void testEqualsByteArray() {
        byte[] value = new byte[5];
        Arrays.fill(value, ((byte) ('X')));
        ChronicleMapBuilder<CharSequence, byte[]> builder = ChronicleMapBuilder.of(CharSequence.class, byte[].class).entries(1).averageKey("hello").averageValue(value);
        try (final ChronicleMap<CharSequence, byte[]> map1 = builder.create()) {
            map1.put("hello", value);
            try (final ChronicleMap<CharSequence, byte[]> map2 = builder.create()) {
                map2.put("hello", value);
                Assert.assertEquals(map1, map2);
            }
        }
    }

    @Test
    public void testSize() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMap.of(CharSequence.class, CharSequence.class).averageKey("key-1024").averageValue("value").minSegments(1024).entries(1024).removeReturnsNull(true).create()) {
            for (int i = 1; i < 1024; i++) {
                map.put(("key" + i), "value");
                Assert.assertEquals(i, map.size());
            }
            for (int i = 1023; i >= 1;) {
                map.remove(("key" + i));
                i--;
                Assert.assertEquals(i, map.size());
            }
        }
    }

    @Test
    public void testRemoveInteger() throws IOException {
        int count = 300;
        try (final ChronicleMap<Object, Object> map = ChronicleMapBuilder.of(Object.class, Object.class).averageKey(1).averageValue(1).entries(count).minSegments(2).create()) {
            for (int i = 1; i < count; i++) {
                map.put(i, i);
                Assert.assertEquals(i, map.size());
            }
            for (int i = count - 1; i >= 1;) {
                Integer j = ((Integer) (map.put(i, i)));
                Assert.assertEquals(i, j.intValue());
                Integer j2 = ((Integer) (map.remove(i)));
                Assert.assertEquals(i, j2.intValue());
                i--;
                Assert.assertEquals(i, map.size());
            }
        }
    }

    @Test
    public void testRemoveWithKeyAndRemoveReturnsNull() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(10).averageKey("key1").averageValue("one").minSegments(2).removeReturnsNull(true).create()) {
            Assert.assertFalse(map.containsKey("key3"));
            map.put("key1", "one");
            map.put("key2", "two");
            Assert.assertEquals(2, map.size());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertFalse(map.containsKey("key3"));
            Assert.assertEquals("one", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            final CharSequence result = map.remove("key1");
            Assert.assertEquals(null, result);
            Assert.assertEquals(1, map.size());
            Assert.assertFalse(map.containsKey("key1"));
            Assert.assertEquals(null, map.get("key1"));
            Assert.assertEquals("two", map.get("key2").toString());
            Assert.assertFalse(map.containsKey("key3"));
            // lets add one more item for luck !
            map.put("key3", "three");
            Assert.assertEquals("three", map.get("key3").toString());
            Assert.assertTrue(map.containsKey("key3"));
            Assert.assertEquals(2, map.size());
            // and just for kicks we'll overwrite what we have
            map.put("key3", "overwritten");
            Assert.assertEquals("overwritten", map.get("key3").toString());
            Assert.assertTrue(map.containsKey("key3"));
            Assert.assertEquals(2, map.size());
        }
    }

    @Test
    public void testReplaceWithKey() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(10).averageKey("key1").averageValue("one").minSegments(2).create()) {
            map.put("key1", "one");
            map.put("key2", "two");
            Assert.assertEquals(2, map.size());
            Assert.assertEquals("one", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            final CharSequence result = map.replace("key1", "newValue");
            Assert.assertEquals("one", result.toString());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertEquals(2, map.size());
            Assert.assertEquals("newValue", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertFalse(map.containsKey("key3"));
            Assert.assertEquals(2, map.size());
            // let and one more item for luck !
            map.put("key3", "three");
            Assert.assertEquals(3, map.size());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertTrue(map.containsKey("key3"));
            Assert.assertEquals("three", map.get("key3").toString());
            // and just for kicks we'll overwrite what we have
            map.put("key3", "overwritten");
            Assert.assertEquals("overwritten", map.get("key3").toString());
            Assert.assertTrue(map.containsKey("key1"));
            Assert.assertTrue(map.containsKey("key2"));
            Assert.assertTrue(map.containsKey("key3"));
            final CharSequence result2 = map.replace("key2", "newValue");
            Assert.assertEquals("two", result2.toString());
            Assert.assertEquals("newValue", map.get("key2").toString());
            final CharSequence result3 = map.replace("rubbish", "newValue");
            Assert.assertEquals(null, result3);
            Assert.assertFalse(map.containsKey("rubbish"));
            Assert.assertEquals(3, map.size());
        }
    }

    @Test
    public void testReplaceWithKeyAnd2Params() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(10).averageKey("key1").averageValue("one").minSegments(2).create()) {
            map.put("key1", "one");
            map.put("key2", "two");
            Assert.assertEquals("one", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            final boolean result = map.replace("key1", "one", "newValue");
            Assert.assertEquals(true, result);
            Assert.assertEquals("newValue", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            // let and one more item for luck !
            map.put("key3", "three");
            Assert.assertEquals("three", map.get("key3").toString());
            // and just for kicks we'll overwrite what we have
            map.put("key3", "overwritten");
            Assert.assertEquals("overwritten", map.get("key3").toString());
            final boolean result2 = map.replace("key2", "two", "newValue2");
            Assert.assertEquals(true, result2);
            Assert.assertEquals("newValue2", map.get("key2").toString());
            final boolean result3 = map.replace("newKey", "", "newValue");
            Assert.assertEquals(false, result3);
            final boolean result4 = map.replace("key2", "newValue2", "newValue2");
            Assert.assertEquals(true, result4);
        }
    }

    // i7-3970X CPU @ 3.50GHz, hex core: -verbose:gc -Xmx64m
    // to tmpfs file system
    // 10M users, updated 12 times. Throughput 19.3 M ops/sec, no GC!
    // 50M users, updated 12 times. Throughput 19.8 M ops/sec, no GC!
    // 100M users, updated 12 times. Throughput 19.0M ops/sec, no GC!
    // 200M users, updated 12 times. Throughput 18.4 M ops/sec, no GC!
    // 400M users, updated 12 times. Throughput 18.4 M ops/sec, no GC!
    // to ext4 file system.
    // 10M users, updated 12 times. Throughput 17.7 M ops/sec, no GC!
    // 50M users, updated 12 times. Throughput 16.5 M ops/sec, no GC!
    // 100M users, updated 12 times. Throughput 15.9 M ops/sec, no GC!
    // 200M users, updated 12 times. Throughput 15.4 M ops/sec, no GC!
    // 400M users, updated 12 times. Throughput 7.8 M ops/sec, no GC!
    // 600M users, updated 12 times. Throughput 5.8 M ops/sec, no GC!
    // dual E5-2650v2 @ 2.6 GHz, 128 GB: -verbose:gc -Xmx32m
    // to tmpfs
    // TODO small GC on startup should be tidied up, [GC 9216K->1886K(31744K), 0.0036750 secs]
    // 10M users, updated 16 times. Throughput 33.0M ops/sec, VmPeak: 5373848 kB, VmRSS: 544252 kB
    // 50M users, updated 16 times. Throughput 31.2 M ops/sec, VmPeak: 9091804 kB, VmRSS: 3324732 kB
    // 250M users, updated 16 times. Throughput 30.0 M ops/sec, VmPeak:	24807836 kB, VmRSS: 14329112 kB
    // 1000M users, updated 16 times, Throughput 24.1 M ops/sec, VmPeak: 85312732 kB, VmRSS: 57165952 kB
    // 2500M users, updated 16 times, Throughput 23.5 M ops/sec, VmPeak: 189545308 kB, VmRSS: 126055868 kB
    // to ext4
    // 10M users, updated 16 times. Throughput 28.4 M ops/sec, VmPeak: 5438652 kB, VmRSS: 544624 kB
    // 50M users, updated 16 times. Throughput 28.2 M ops/sec, VmPeak: 9091804 kB, VmRSS: 9091804 kB
    // 250M users, updated 16 times. Throughput 26.1 M ops/sec, VmPeak:	24807836 kB, VmRSS: 24807836 kB
    // 1000M users, updated 16 times, Throughput 1.3 M ops/sec, TODO FIX this
    @Test
    public void testRemoveWithKeyAndValue() {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(10).averageKey("key1").averageValue("one").minSegments(2).create()) {
            map.put("key1", "one");
            map.put("key2", "two");
            Assert.assertEquals("one", map.get("key1").toString());
            Assert.assertEquals("two", map.get("key2").toString());
            // a false remove
            final boolean wasRemoved1 = map.remove("key1", "three");
            Assert.assertFalse(wasRemoved1);
            Assert.assertEquals(null, map.get("key1").toString(), "one");
            Assert.assertEquals("two", map.get("key2").toString(), "two");
            map.put("key1", "one");
            final boolean wasRemoved2 = map.remove("key1", "three");
            Assert.assertFalse(wasRemoved2);
            // lets add one more item for luck !
            map.put("key3", "three");
            Assert.assertEquals("three", map.get("key3").toString());
            // and just for kicks we'll overwrite what we have
            map.put("key3", "overwritten");
            Assert.assertEquals("overwritten", map.get("key3").toString());
        }
    }

    @Test
    public void testAcquireWithNullContainer() {
        try (ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).averageKey("key").entries(1000).entryAndValueOffsetAlignment(4).create()) {
            map.acquireUsing("key", Values.newNativeReference(LongValue.class));
            Assert.assertEquals(0, map.acquireUsing("key", null).getValue());
        }
    }

    // i7-3970X CPU @ 3.50GHz, hex core: -Xmx30g -verbose:gc
    // 10M users, updated 12 times. Throughput 16.2 M ops/sec, longest [Full GC 853669K->852546K(3239936K), 0.8255960 secs]
    // 50M users, updated 12 times. Throughput 13.3 M ops/sec,  longest [Full GC 5516214K->5511353K(13084544K), 3.5752970 secs]
    // 100M users, updated 12 times. Throughput 11.8 M ops/sec, longest [Full GC 11240703K->11233711K(19170432K), 5.8783010 secs]
    // 200M users, updated 12 times. Throughput 4.2 M ops/sec, longest [Full GC 25974721K->22897189K(27962048K), 21.7962600 secs]
    // dual E5-2650v2 @ 2.6 GHz, 128 GB: -verbose:gc -Xmx100g
    // 10M users, updated 16 times. Throughput 155.3 M ops/sec, VmPeak: 113291428 kB, VmRSS: 9272176 kB, [Full GC 1624336K->1616457K(7299072K), 2.5381610 secs]
    // 50M users, updated 16 times. Throughput 120.4 M ops/sec, VmPeak: 113291428 kB, VmRSS: 28436248 kB [Full GC 6545332K->6529639K(18179584K), 6.9053810 secs]
    // 250M users, updated 16 times. Throughput 114.1 M ops/sec, VmPeak: 113291428 kB, VmRSS: 76441464 kB  [Full GC 41349527K->41304543K(75585024K), 17.3217490 secs]
    // 1000M users, OutOfMemoryError.
    @Test
    public void testGetWithNullContainer() {
        try (ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).averageKey("key").entries(10).entryAndValueOffsetAlignment(4).create()) {
            map.acquireUsing("key", Values.newNativeReference(LongValue.class));
            Assert.assertEquals(0, map.getUsing("key", null).getValue());
        }
    }

    @Test
    public void testGetWithoutAcquireFirst() {
        try (ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).averageKey("key").entries(10).entryAndValueOffsetAlignment(4).create()) {
            Assert.assertNull(map.getUsing("key", Values.newNativeReference(LongValue.class)));
        }
    }

    @Test
    public void testAcquireAndGet() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        /* 00 * 1000 */
        int entries = 3;
        try (ChronicleMap<CharSequence, LongValue> map2 = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).minSegments(1).averageKeySize(10).entryAndValueOffsetAlignment(8).create()) {
            LongValue value4 = Values.newNativeReference(LongValue.class);
            LongValue value22 = Values.newNativeReference(LongValue.class);
            LongValue value32 = Values.newNativeReference(LongValue.class);
            for (int j2 = 1; j2 <= 3; j2++) {
                for (int i2 = 0; i2 < entries; i2++) {
                    CharSequence userCS2 = getUserCharSequence(i2);
                    if (j2 > 1) {
                        Assert.assertNotNull(userCS2.toString(), map2.getUsing(userCS2, value4));
                    } else {
                        map2.acquireUsing(userCS2, value4);
                    }
                    if (i2 >= 1)
                        Assert.assertTrue(userCS2.toString(), map2.containsKey(getUserCharSequence(1)));

                    Assert.assertEquals(userCS2.toString(), (j2 - 1), value4.getValue());
                    value4.addAtomicValue(1);
                    Assert.assertEquals(value22, map2.acquireUsing(userCS2, value22));
                    Assert.assertEquals(j2, value22.getValue());
                    Assert.assertEquals(value32, map2.getUsing(userCS2, value32));
                    Assert.assertEquals(j2, value32.getValue());
                }
            }
            try (ChronicleMap<CharSequence, LongValue> map1 = // .entryAndValueOffsetAlignment(8)
            // .minSegments(1)
            ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).averageKeySize(10).create()) {
                LongValue value1 = Values.newNativeReference(LongValue.class);
                LongValue value21 = Values.newNativeReference(LongValue.class);
                LongValue value31 = Values.newNativeReference(LongValue.class);
                for (int j1 = 1; j1 <= 3; j1++) {
                    for (int i1 = 0; i1 < entries; i1++) {
                        CharSequence userCS1 = getUserCharSequence(i1);
                        if (j1 > 1) {
                            Assert.assertNotNull(userCS1.toString(), map1.getUsing(userCS1, value1));
                        } else {
                            map1.acquireUsing(userCS1, value1);
                        }
                        if (i1 >= 1)
                            Assert.assertTrue(userCS1.toString(), map1.containsKey(getUserCharSequence(1)));

                        Assert.assertEquals(userCS1.toString(), (j1 - 1), value1.getValue());
                        value1.addAtomicValue(1);
                        Assert.assertEquals(value21, map1.acquireUsing(userCS1, value21));
                        Assert.assertEquals(j1, value21.getValue());
                        Assert.assertEquals(value31, map1.getUsing(userCS1, value31));
                        Assert.assertEquals(j1, value31.getValue());
                    }
                }
            }
            try (ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).minSegments(1).averageKeySize(10).entryAndValueOffsetAlignment(8).create()) {
                LongValue value = Values.newNativeReference(LongValue.class);
                LongValue value2 = Values.newNativeReference(LongValue.class);
                LongValue value3 = Values.newNativeReference(LongValue.class);
                for (int j = 1; j <= 3; j++) {
                    for (int i = 0; i < entries; i++) {
                        CharSequence userCS = getUserCharSequence(i);
                        if (j > 1) {
                            Assert.assertNotNull(userCS.toString(), map.getUsing(userCS, value));
                        } else {
                            map.acquireUsing(userCS, value);
                        }
                        if (i >= 1)
                            Assert.assertTrue(userCS.toString(), map.containsKey(getUserCharSequence(1)));

                        Assert.assertEquals(userCS.toString(), (j - 1), value.getValue());
                        value.addAtomicValue(1);
                        Assert.assertEquals(value2, map.acquireUsing(userCS, value2));
                        Assert.assertEquals(j, value2.getValue());
                        Assert.assertEquals(value3, map.getUsing(userCS, value3));
                        Assert.assertEquals(j, value3.getValue());
                    }
                }
            }
        }
    }

    @Test
    public void testAcquireFromMultipleThreads() throws InterruptedException {
        int entries = 1000 * 1000;
        try (ChronicleMap<CharSequence, LongValue> map2 = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).minSegments(128).averageKeySize(10).entryAndValueOffsetAlignment(1).create()) {
            CharSequence key2 = getUserCharSequence(0);
            map2.acquireUsing(key2, Values.newNativeReference(LongValue.class));
            int iterations2 = 10000;
            int noOfThreads2 = 10;
            CyclicBarrier barrier2 = new CyclicBarrier(noOfThreads2);
            Thread[] threads2 = new Thread[noOfThreads2];
            for (int t2 = 0; t2 < noOfThreads2; t2++) {
                threads2[t2] = new Thread(new ChronicleMapTest.IncrementRunnable(map2, key2, iterations2, barrier2));
                threads2[t2].start();
            }
            for (int t2 = 0; t2 < noOfThreads2; t2++) {
                threads2[t2].join();
            }
            Assert.assertEquals((noOfThreads2 * iterations2), map2.acquireUsing(key2, Values.newNativeReference(LongValue.class)).getValue());
            try (ChronicleMap<CharSequence, LongValue> map1 = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).minSegments(128).averageKeySize(10).entryAndValueOffsetAlignment(4).create()) {
                CharSequence key1 = getUserCharSequence(0);
                map1.acquireUsing(key1, Values.newNativeReference(LongValue.class));
                int iterations1 = 10000;
                int noOfThreads1 = 10;
                CyclicBarrier barrier1 = new CyclicBarrier(noOfThreads1);
                Thread[] threads1 = new Thread[noOfThreads1];
                for (int t1 = 0; t1 < noOfThreads1; t1++) {
                    threads1[t1] = new Thread(new ChronicleMapTest.IncrementRunnable(map1, key1, iterations1, barrier1));
                    threads1[t1].start();
                }
                for (int t1 = 0; t1 < noOfThreads1; t1++) {
                    threads1[t1].join();
                }
                Assert.assertEquals((noOfThreads1 * iterations1), map1.acquireUsing(key1, Values.newNativeReference(LongValue.class)).getValue());
                try (ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(((long) (entries))).minSegments(128).averageKeySize(10).entryAndValueOffsetAlignment(8).create()) {
                    CharSequence key = getUserCharSequence(0);
                    map.acquireUsing(key, Values.newNativeReference(LongValue.class));
                    int iterations = 10000;
                    int noOfThreads = 10;
                    CyclicBarrier barrier = new CyclicBarrier(noOfThreads);
                    Thread[] threads = new Thread[noOfThreads];
                    for (int t = 0; t < noOfThreads; t++) {
                        threads[t] = new Thread(new ChronicleMapTest.IncrementRunnable(map, key, iterations, barrier));
                        threads[t].start();
                    }
                    for (int t = 0; t < noOfThreads; t++) {
                        threads[t].join();
                    }
                    Assert.assertEquals((noOfThreads * iterations), map.acquireUsing(key, Values.newNativeReference(LongValue.class)).getValue());
                }
            }
        }
    }

    @Test
    public void testLargerEntries() {
        for (int segments : new int[]{ 128, 256, 512, 1024 }) {
            int entries = 100000;
            int entrySize = 512;
            ChronicleMapBuilder<CharSequence, CharSequence> builder = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(((entries * 11) / 10)).actualSegments(segments).averageKeySize(14).averageValueSize(((entrySize - 14) - 2));
            try (ChronicleMap<CharSequence, CharSequence> map = builder.create()) {
                StringBuilder sb = new StringBuilder();
                while ((sb.length()) < ((entrySize - 14) - 2))
                    sb.append('+');

                for (int i = 0; i < entries; i++) {
                    map.put(("us:" + i), sb);
                }
            }
        }
    }

    @Test
    public void testPutAndRemove() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        int entries = 100 * 1000;
        try (ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(entries).minSegments(16).averageKeySize((("user:".length()) + 6)).averageValueSize((("value:".length()) + 6)).putReturnsNull(true).removeReturnsNull(true).create()) {
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            StringBuilder value2 = new StringBuilder();
            for (int j = 1; j <= 3; j++) {
                for (int i = 0; i < entries; i++) {
                    key.setLength(0);
                    key.append("user:").append(i);
                    value.setLength(0);
                    value.append("value:").append(i);
                    // System.out.println(key);
                    Assert.assertNull(map.getUsing(key, value));
                    Assert.assertNull(map.put(key, value));
                    Assert.assertNotNull(map.getUsing(key, value2));
                    Assert.assertEquals(value.toString(), value2.toString());
                    Assert.assertNull(map.remove(key));
                    Assert.assertNull(map.getUsing(key, value));
                }
            }
        }
    }

    @Test
    public void mapRemoveReflectedInViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            map.remove(2);
            ChronicleMapTest.assertMap(map, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertEntrySet(map.entrySet(), new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1, 3 });
            ChronicleMapTest.assertKeySet(map.keySet(), new int[]{ 1, 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertValues(map.values(), new CharSequence[]{ "1", "3" });
        }
    }

    @Test
    public void mapPutReflectedInViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            map.put(4, "4");
            ChronicleMapTest.assertMap(map, new int[]{ 4, 2, 3, 1 }, new CharSequence[]{ "4", "2", "3", "1" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 4, 2, 3, 1 }, new CharSequence[]{ "4", "2", "3", "1" });
            ChronicleMapTest.assertEntrySet(map.entrySet(), new int[]{ 4, 2, 3, 1 }, new CharSequence[]{ "4", "2", "3", "1" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 4, 2, 3, 1 });
            ChronicleMapTest.assertKeySet(map.keySet(), new int[]{ 4, 2, 3, 1 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "2", "1", "4", "3" });
            ChronicleMapTest.assertValues(map.values(), new CharSequence[]{ "2", "1", "4", "3" });
        }
    }

    @Test
    public void entrySetRemoveReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            entrySet.remove(new AbstractMap.SimpleEntry<Integer, CharSequence>(2, "2"));
            ChronicleMapTest.assertMap(map, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1, 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "1", "3" });
        }
    }

    @Test
    public void keySetRemoveReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            keySet.remove(2);
            ChronicleMapTest.assertMap(map, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1, 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "1", "3" });
        }
    }

    @Test
    public void valuesRemoveReflectedInMap() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            values.removeIf("2"::contentEquals);
            ChronicleMapTest.assertMap(map, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1, 3 }, new CharSequence[]{ "1", "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1, 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "1", "3" });
        }
    }

    @Test
    public void entrySetIteratorRemoveReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Map<Integer, CharSequence> refMap = new HashMap(map);
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            Iterator<Map.Entry<Integer, CharSequence>> entryIterator = entrySet.iterator();
            entryIterator.next();
            refMap.remove(entryIterator.next().getKey());
            entryIterator.remove();
            int[] expectedKeys = Ints.toArray(refMap.keySet());
            CharSequence[] expectedValues = refMap.values().toArray(new CharSequence[0]);
            ChronicleMapTest.assertMap(map, expectedKeys, expectedValues);
            ChronicleMapTest.assertEntrySet(entrySet, expectedKeys, expectedValues);
            ChronicleMapTest.assertKeySet(keySet, expectedKeys);
            ChronicleMapTest.assertValues(values, expectedValues);
        }
    }

    @Test
    public void keySetIteratorRemoveReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Map<Integer, CharSequence> refMap = new HashMap(map);
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            Iterator<Integer> keyIterator = keySet.iterator();
            keyIterator.next();
            refMap.remove(keyIterator.next());
            keyIterator.remove();
            int[] expectedKeys = Ints.toArray(refMap.keySet());
            CharSequence[] expectedValues = refMap.values().toArray(new CharSequence[0]);
            ChronicleMapTest.assertMap(map, expectedKeys, expectedValues);
            ChronicleMapTest.assertEntrySet(entrySet, expectedKeys, expectedValues);
            ChronicleMapTest.assertKeySet(keySet, expectedKeys);
            ChronicleMapTest.assertValues(values, expectedValues);
        }
    }

    @Test
    public void valuesIteratorRemoveReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            HashBiMap<Integer, CharSequence> refMap = HashBiMap.create();
            map.forEach(( k, v) -> refMap.put(k, v.toString()));
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            Iterator<CharSequence> valueIterator = values.iterator();
            valueIterator.next();
            refMap.inverse().remove(valueIterator.next().toString());
            valueIterator.remove();
            int[] expectedKeys = Ints.toArray(refMap.keySet());
            CharSequence[] expectedValues = new CharSequence[expectedKeys.length];
            for (int i = 0; i < (expectedKeys.length); i++) {
                expectedValues[i] = refMap.get(expectedKeys[i]);
            }
            ChronicleMapTest.assertMap(map, expectedKeys, expectedValues);
            ChronicleMapTest.assertEntrySet(entrySet, expectedKeys, expectedValues);
            ChronicleMapTest.assertKeySet(keySet, expectedKeys);
            ChronicleMapTest.assertValues(values, expectedValues);
        }
    }

    @Test
    public void entrySetRemoveAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            entrySet.removeAll(Arrays.asList(new AbstractMap.SimpleEntry<Integer, CharSequence>(1, "1"), new AbstractMap.SimpleEntry<Integer, CharSequence>(2, "2")));
            ChronicleMapTest.assertMap(map, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "3" });
        }
    }

    @Test
    public void keySetRemoveAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            keySet.removeAll(Arrays.asList(1, 2));
            ChronicleMapTest.assertMap(map, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "3" });
        }
    }

    @Test
    public void valuesRemoveAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            values.removeIf(( e) -> ("1".contentEquals(e)) || ("2".contentEquals(e)));
            ChronicleMapTest.assertMap(map, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 3 }, new CharSequence[]{ "3" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 3 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "3" });
        }
    }

    @Test
    public void entrySetRetainAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            entrySet.removeIf(( e) -> (!((e.getKey().equals(1)) && ("1".contentEquals(e.getValue())))) && (!((e.getKey().equals(2)) && ("2".contentEquals(e.getValue())))));
            ChronicleMapTest.assertMap(map, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 2, 1 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "2", "1" });
        }
    }

    @Test
    public void keySetRetainAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            keySet.retainAll(Arrays.asList(1, 2));
            ChronicleMapTest.assertMap(map, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 2, 1 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "2", "1" });
        }
    }

    @Test
    public void valuesRetainAllReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            values.removeIf(( v) -> (!("1".contentEquals(v))) && (!("2".contentEquals(v))));
            ChronicleMapTest.assertMap(map, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 2, 1 }, new CharSequence[]{ "2", "1" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 2, 1 });
            ChronicleMapTest.assertValues(values, new CharSequence[]{ "2", "1" });
        }
    }

    @Test
    public void entrySetClearReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            entrySet.clear();
            Assert.assertTrue(map.isEmpty());
            Assert.assertTrue(entrySet.isEmpty());
            Assert.assertTrue(keySet.isEmpty());
            Assert.assertTrue(values.isEmpty());
        }
    }

    @Test
    public void keySetClearReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            keySet.clear();
            Assert.assertTrue(map.isEmpty());
            Assert.assertTrue(entrySet.isEmpty());
            Assert.assertTrue(keySet.isEmpty());
            Assert.assertTrue(values.isEmpty());
        }
    }

    @Test
    public void valuesClearReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(3)) {
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            values.clear();
            Assert.assertTrue(map.isEmpty());
            Assert.assertTrue(entrySet.isEmpty());
            Assert.assertTrue(keySet.isEmpty());
            Assert.assertTrue(values.isEmpty());
        }
    }

    @Test
    public void clearMapViaEntryIteratorRemoves() throws IOException {
        int noOfElements = 16 * 1024;
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(noOfElements)) {
            int sum = 0;
            for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                it.next();
                it.remove();
                ++sum;
            }
            Assert.assertEquals(noOfElements, sum);
        }
    }

    @Test
    public void clearMapViaKeyIteratorRemoves() throws IOException {
        int noOfElements = 16 * 1024;
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(noOfElements)) {
            Set<Integer> keys = new HashSet<Integer>();
            for (int i = 1; i <= noOfElements; i++) {
                keys.add(i);
            }
            int sum = 0;
            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                keys.remove(key);
                it.remove();
                ++sum;
            }
            Assert.assertEquals(noOfElements, sum);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWhenNextIsNotCalled() throws IOException {
        ChronicleMap<Integer, CharSequence> map = getViewTestMap(2);
        Iterator<Integer> iterator = map.keySet().iterator();
        iterator.remove();
    }

    @Test
    public void clearMapViaValueIteratorRemoves() throws IOException {
        int noOfElements = 16 * 1024;
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(noOfElements)) {
            int sum = 0;
            for (Iterator it = map.values().iterator(); it.hasNext();) {
                it.next();
                it.remove();
                ++sum;
            }
            Assert.assertEquals(noOfElements, sum);
        }
    }

    @Test
    public void entrySetValueReflectedInMapAndOtherViews() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = getViewTestMap(0)) {
            map.put(1, "A");
            Set<Map.Entry<Integer, CharSequence>> entrySet = map.entrySet();
            Set<Integer> keySet = map.keySet();
            Collection<CharSequence> values = map.values();
            ChronicleMapTest.assertMap(map, new int[]{ 1 }, new CharSequence[]{ "A" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1 }, new CharSequence[]{ "A" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1 });
            ChronicleMapTest.assertValues(values, new String[]{ "A" });
            entrySet.iterator().next().setValue("B");
            ChronicleMapTest.assertMap(map, new int[]{ 1 }, new CharSequence[]{ "B" });
            ChronicleMapTest.assertEntrySet(entrySet, new int[]{ 1 }, new CharSequence[]{ "B" });
            ChronicleMapTest.assertEntrySet(map.entrySet(), new int[]{ 1 }, new CharSequence[]{ "B" });
            ChronicleMapTest.assertKeySet(keySet, new int[]{ 1 });
            ChronicleMapTest.assertKeySet(map.keySet(), new int[]{ 1 });
            ChronicleMapTest.assertValues(values, new String[]{ "B" });
            ChronicleMapTest.assertValues(map.values(), new String[]{ "B" });
        }
    }

    @Test
    public void equalsTest() throws IOException {
        try (final ChronicleMap<Integer, String> map1 = ChronicleMap.of(Integer.class, String.class).averageValue("one").entries(2).create()) {
            map1.put(1, "one");
            map1.put(2, "two");
            try (ChronicleMap<Integer, String> map2 = ChronicleMap.of(Integer.class, String.class).averageValue("one").entries(2).create()) {
                map2.put(1, "one");
                map2.put(2, "two");
                Assert.assertEquals(map1, map2);
            }
        }
    }

    @Test
    public void testPutLongValue() throws IOException {
        final ChronicleMapBuilder<CharSequence, LongValue> builder = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(1000).averageKeySize("x".length());
        try (final ChronicleMap<CharSequence, LongValue> map = builder.create()) {
            LongValue value = ChronicleMapTest.nativeLongValue();
            try {
                map.put("x", value);
            } catch (IllegalStateException | NullPointerException e) {
                // ok
                return;
            }
            throw new AssertionError(("Should throw either IllegalStateException or " + "NullPointerException, but succeed"));
        }
    }

    @Test
    public void testOffheapAcquireUsingLocked() throws IOException {
        ChronicleMapBuilder<CharSequence, LongValue> builder = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(1000).averageKeySize("one".length());
        try (final ChronicleMap<CharSequence, LongValue> map = builder.create()) {
            LongValue value = ChronicleMapTest.nativeLongValue();
            // this will add the entry
            try (Closeable c = map.acquireContext("one", value)) {
                Assert.assertEquals(0, value.getValue());
                value.addValue(1);
            }
            // check that the entry was added
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                MapEntry<CharSequence, LongValue> entry = c.entry();
                Assert.assertNotNull(entry);
                LongValue v = entry.value().getUsing(value);
                assert v == value;
                Assert.assertEquals(1, v.getValue());
            }
            // this will remove the entry
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                c.updateLock().lock();
                c.remove(c.entry());
            }
            // check that the entry was removed
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                c.updateLock().lock();
                Assert.assertNotNull(c.absentEntry());
            }
            try (Closeable c = map.acquireContext("one", value)) {
                Assert.assertEquals(0, value.getValue());
            }
            try (Closeable c = map.acquireContext("one", value)) {
                value.addValue(1);
            }
            // check that the entry was removed
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                LongValue v = c.entry().value().getUsing(value);
                assert value == v;
                Assert.assertEquals(1, c.entry().value().get().getValue());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcquireUsingLockedWithString() throws IOException {
        ChronicleMapBuilder<CharSequence, String> builder = ChronicleMapBuilder.of(CharSequence.class, String.class).averageKey("one").averageValue("").entries(1000);
        try (final ChronicleMap<CharSequence, String> map = builder.create()) {
            // this will add the entry
            try (Closeable c = map.acquireContext("one", "")) {
                // do nothing
            }
        }
    }

    @Test
    public void testOnheapAcquireUsingLockedStringBuilder() throws IOException {
        try (final ChronicleMap<CharSequence, CharSequence> map = ChronicleMapBuilder.of(CharSequence.class, CharSequence.class).entries(1000).averageKeySize("one".length()).averageValueSize("Hello World".length()).create()) {
            StringBuilder value = new StringBuilder();
            try (Closeable c = map.acquireContext("one", value)) {
                value.append("Hello World");
            }
            Assert.assertEquals("Hello World", value.toString());
        }
    }

    @Test
    public void testOnheapAcquireUsingLocked() throws IOException {
        File tmpFile = File.createTempFile("testAcquireUsingLocked", ".deleteme");
        tmpFile.deleteOnExit();
        try (final ChronicleMap<CharSequence, LongValue> map = ChronicleMapBuilder.of(CharSequence.class, LongValue.class).entries(1000).averageKeySize("one".length()).createPersistedTo(tmpFile)) {
            LongValue value = Values.newNativeReference(LongValue.class);
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                Assert.assertNotNull(c.absentEntry());
            }
            try (Closeable c = map.acquireContext("one", value)) {
                value.setValue(10);
            }
            // this will add the entry
            try (Closeable c = map.acquireContext("one", value)) {
                value.addValue(1);
            }
            // check that the entry was added
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                MapEntry<CharSequence, LongValue> entry = c.entry();
                Assert.assertNotNull(entry);
                Assert.assertEquals(11, entry.value().get().getValue());
            }
            // this will remove the entry
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                c.updateLock().lock();
                c.remove(c.entry());
            }
            // check that the entry was removed
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                Assert.assertNotNull(c.absentEntry());
            }
            try (Closeable c = map.acquireContext("one", value)) {
                Assert.assertEquals(0, value.getValue());
            }
            value.setValue(1);
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                Assert.assertEquals(1, c.entry().value().get().getValue());
            }
            try (Closeable c = map.acquireContext("one", value)) {
                value.addValue(1);
            }
            // check that the entry was removed
            try (ExternalMapQueryContext<CharSequence, LongValue, ?> c = map.queryContext("one")) {
                LongValue value1 = c.entry().value().get();
                Assert.assertEquals(2, value1.getValue());
            }
        }
        tmpFile.delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBytesMarshallableMustBeConcreteValueType() {
        try (ChronicleMap<CharSequence, ChronicleMapTest.BMSUper> map = ChronicleMapBuilder.of(CharSequence.class, ChronicleMapTest.BMSUper.class).entries(1).averageKey("hello").averageValue(new ChronicleMapTest.BMClass()).create()) {
            map.put("hi", new ChronicleMapTest.BMClass());
        }
    }

    interface BMSUper {}

    static class BMClass implements BytesMarshallable , ChronicleMapTest.BMSUper {}

    private static final class IncrementRunnable implements Runnable {
        private final ChronicleMap<CharSequence, LongValue> map;

        private final CharSequence key;

        private final int iterations;

        private final CyclicBarrier barrier;

        private IncrementRunnable(ChronicleMap<CharSequence, LongValue> map, CharSequence key, int iterations, CyclicBarrier barrier) {
            this.map = map;
            this.key = key;
            this.iterations = iterations;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                LongValue value = Values.newNativeReference(LongValue.class);
                barrier.await();
                for (int i = 0; i < (iterations); i++) {
                    map.acquireUsing(key, value);
                    value.addAtomicValue(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

