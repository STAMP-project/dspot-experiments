package com.orientechnologies.orient.core.storage.index.sbtree.singlevalue;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class OCellBTreeSingleValueTestIT {
    private OCellBTreeSingleValue<String> singleValueTree;

    private OrientDB orientDB;

    private String dbName;

    @Test
    public void testKeyPut() throws Exception {
        final int keysCount = 1000000;
        String lastKey = null;
        for (int i = 0; i < keysCount; i++) {
            final String key = Integer.toString(i);
            singleValueTree.put(key, new ORecordId((i % 32000), i));
            if ((i % 100000) == 0) {
                System.out.printf("%d items loaded out of %d%n", i, keysCount);
            }
            if (lastKey == null) {
                lastKey = key;
            } else
                if ((key.compareTo(lastKey)) > 0) {
                    lastKey = key;
                }

            Assert.assertEquals("0", singleValueTree.firstKey());
            Assert.assertEquals(lastKey, singleValueTree.lastKey());
        }
        for (int i = 0; i < keysCount; i++) {
            Assert.assertEquals((i + " key is absent"), singleValueTree.get(Integer.toString(i)), new ORecordId((i % 32000), i));
            if ((i % 100000) == 0) {
                System.out.printf("%d items tested out of %d%n", i, keysCount);
            }
        }
        for (int i = keysCount; i < (2 * keysCount); i++) {
            Assert.assertNull(singleValueTree.get(Integer.toString(i)));
        }
    }

    @Test
    public void testKeyPutRandomUniform() throws Exception {
        final NavigableSet<String> keys = new TreeSet<>();
        final Random random = new Random();
        final int keysCount = 1000000;
        while ((keys.size()) < keysCount) {
            int val = random.nextInt(Integer.MAX_VALUE);
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keys.add(key);
            Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
        } 
        Assert.assertEquals(singleValueTree.firstKey(), keys.first());
        Assert.assertEquals(singleValueTree.lastKey(), keys.last());
        for (String key : keys) {
            final int val = Integer.parseInt(key);
            Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
        }
    }

    @Test
    public void testKeyPutRandomGaussian() throws Exception {
        NavigableSet<String> keys = new TreeSet<>();
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyPutRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        final int keysCount = 1000000;
        while ((keys.size()) < keysCount) {
            int val = ((int) ((((random.nextGaussian()) * (Integer.MAX_VALUE)) / 2) + (Integer.MAX_VALUE)));
            if (val < 0) {
                continue;
            }
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keys.add(key);
            Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
        } 
        Assert.assertEquals(singleValueTree.firstKey(), keys.first());
        Assert.assertEquals(singleValueTree.lastKey(), keys.last());
        for (String key : keys) {
            int val = Integer.parseInt(key);
            Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
        }
    }

    @Test
    public void testKeyDeleteRandomUniform() throws Exception {
        final int keysCount = 1000000;
        NavigableSet<String> keys = new TreeSet<>();
        for (int i = 0; i < keysCount; i++) {
            String key = Integer.toString(i);
            singleValueTree.put(key, new ORecordId((i % 32000), i));
            keys.add(key);
        }
        Iterator<String> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (((Integer.parseInt(key)) % 3) == 0) {
                singleValueTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(singleValueTree.firstKey(), keys.first());
        Assert.assertEquals(singleValueTree.lastKey(), keys.last());
        for (String key : keys) {
            int val = Integer.parseInt(key);
            if ((val % 3) == 0) {
                Assert.assertNull(singleValueTree.get(key));
            } else {
                Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
            }
        }
    }

    @Test
    public void testKeyDeleteRandomGaussian() throws Exception {
        NavigableSet<String> keys = new TreeSet<>();
        final int keysCount = 1000000;
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyDeleteRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        while ((keys.size()) < keysCount) {
            int val = ((int) ((((random.nextGaussian()) * (Integer.MAX_VALUE)) / 2) + (Integer.MAX_VALUE)));
            if (val < 0) {
                continue;
            }
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keys.add(key);
            Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
        } 
        Iterator<String> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (((Integer.parseInt(key)) % 3) == 0) {
                singleValueTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(singleValueTree.firstKey(), keys.first());
        Assert.assertEquals(singleValueTree.lastKey(), keys.last());
        for (String key : keys) {
            int val = Integer.parseInt(key);
            if ((val % 3) == 0) {
                Assert.assertNull(singleValueTree.get(key));
            } else {
                Assert.assertEquals(singleValueTree.get(key), new ORecordId((val % 32000), val));
            }
        }
    }

    @Test
    public void testKeyDelete() throws Exception {
        final int keysCount = 1000000;
        for (int i = 0; i < keysCount; i++) {
            singleValueTree.put(Integer.toString(i), new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < keysCount; i++) {
            if ((i % 3) == 0) {
                Assert.assertEquals(singleValueTree.remove(Integer.toString(i)), new ORecordId((i % 32000), i));
            }
        }
        for (int i = 0; i < keysCount; i++) {
            if ((i % 3) == 0) {
                Assert.assertNull(singleValueTree.get(Integer.toString(i)));
            } else {
                Assert.assertEquals(singleValueTree.get(Integer.toString(i)), new ORecordId((i % 32000), i));
            }
        }
    }

    @Test
    public void testKeyAddDelete() throws Exception {
        final int keysCount = 1000000;
        for (int i = 0; i < keysCount; i++) {
            singleValueTree.put(Integer.toString(i), new ORecordId((i % 32000), i));
            Assert.assertEquals(singleValueTree.get(Integer.toString(i)), new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < keysCount; i++) {
            if ((i % 3) == 0) {
                Assert.assertEquals(singleValueTree.remove(Integer.toString(i)), new ORecordId((i % 32000), i));
            }
            if ((i % 2) == 0) {
                singleValueTree.put(Integer.toString((keysCount + i)), new ORecordId(((keysCount + i) % 32000), (keysCount + i)));
            }
        }
        for (int i = 0; i < keysCount; i++) {
            if ((i % 3) == 0) {
                Assert.assertNull(singleValueTree.get(Integer.toString(i)));
            } else {
                Assert.assertEquals(singleValueTree.get(Integer.toString(i)), new ORecordId((i % 32000), i));
            }
            if ((i % 2) == 0) {
                Assert.assertEquals(singleValueTree.get(Integer.toString((keysCount + i))), new ORecordId(((keysCount + i) % 32000), (keysCount + i)));
            }
        }
    }

    @Test
    public void testKeyCursor() throws Exception {
        final int keysCount = 1000000;
        NavigableMap<String, ORID> keyValues = new TreeMap<>();
        final long seed = System.nanoTime();
        System.out.println(("testKeyCursor: " + seed));
        Random random = new Random(seed);
        while ((keyValues.size()) < keysCount) {
            int val = random.nextInt(Integer.MAX_VALUE);
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keyValues.put(key, new ORecordId((val % 32000), val));
        } 
        Assert.assertEquals(singleValueTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(singleValueTree.lastKey(), keyValues.lastKey());
        final OCellBTreeSingleValue.OSBTreeKeyCursor<String> cursor = singleValueTree.keyCursor();
        for (String entryKey : keyValues.keySet()) {
            final String indexKey = cursor.next((-1));
            Assert.assertEquals(entryKey, indexKey);
        }
    }

    @Test
    public void testIterateEntriesMajor() throws Exception {
        final int keysCount = 1000000;
        NavigableMap<String, ORID> keyValues = new TreeMap<>();
        final long seed = System.nanoTime();
        System.out.println(("testIterateEntriesMajor: " + seed));
        Random random = new Random(seed);
        while ((keyValues.size()) < keysCount) {
            int val = random.nextInt(Integer.MAX_VALUE);
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keyValues.put(key, new ORecordId((val % 32000), val));
        } 
        assertIterateMajorEntries(keyValues, random, true, true);
        assertIterateMajorEntries(keyValues, random, false, true);
        assertIterateMajorEntries(keyValues, random, true, false);
        assertIterateMajorEntries(keyValues, random, false, false);
        Assert.assertEquals(singleValueTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(singleValueTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testIterateEntriesMinor() throws Exception {
        final int keysCount = 1000000;
        NavigableMap<String, ORID> keyValues = new TreeMap<>();
        final long seed = System.nanoTime();
        System.out.println(("testIterateEntriesMinor: " + seed));
        Random random = new Random(seed);
        while ((keyValues.size()) < keysCount) {
            int val = random.nextInt(Integer.MAX_VALUE);
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keyValues.put(key, new ORecordId((val % 32000), val));
        } 
        assertIterateMinorEntries(keyValues, random, true, true);
        assertIterateMinorEntries(keyValues, random, false, true);
        assertIterateMinorEntries(keyValues, random, true, false);
        assertIterateMinorEntries(keyValues, random, false, false);
        Assert.assertEquals(singleValueTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(singleValueTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testIterateEntriesBetween() throws Exception {
        final int keysCount = 1000000;
        NavigableMap<String, ORID> keyValues = new TreeMap<>();
        Random random = new Random();
        while ((keyValues.size()) < keysCount) {
            int val = random.nextInt(Integer.MAX_VALUE);
            String key = Integer.toString(val);
            singleValueTree.put(key, new ORecordId((val % 32000), val));
            keyValues.put(key, new ORecordId((val % 32000), val));
        } 
        assertIterateBetweenEntries(keyValues, random, true, true, true);
        assertIterateBetweenEntries(keyValues, random, true, false, true);
        assertIterateBetweenEntries(keyValues, random, false, true, true);
        assertIterateBetweenEntries(keyValues, random, false, false, true);
        assertIterateBetweenEntries(keyValues, random, true, true, false);
        assertIterateBetweenEntries(keyValues, random, true, false, false);
        assertIterateBetweenEntries(keyValues, random, false, true, false);
        assertIterateBetweenEntries(keyValues, random, false, false, false);
        Assert.assertEquals(singleValueTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(singleValueTree.lastKey(), keyValues.lastKey());
    }
}

