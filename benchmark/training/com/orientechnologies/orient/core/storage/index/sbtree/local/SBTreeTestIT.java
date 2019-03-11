package com.orientechnologies.orient.core.storage.index.sbtree.local;


import OIntegerSerializer.INSTANCE;
import OSBTree.OSBTreeCursor;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 12.08.13
 */
public class SBTreeTestIT {
    private static final int KEYS_COUNT = 500000;

    OSBTree<Integer, OIdentifiable> sbTree;

    protected ODatabaseSession databaseDocumentTx;

    protected String buildDirectory;

    protected OrientDB orientDB;

    String dbName;

    @Test
    public void testKeyPut() throws Exception {
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            Assert.assertEquals((i + " key is absent"), sbTree.get(i), new ORecordId((i % 32000), i));
        }
        Assert.assertEquals(0, ((int) (sbTree.firstKey())));
        Assert.assertEquals(((SBTreeTestIT.KEYS_COUNT) - 1), ((int) (sbTree.lastKey())));
        for (int i = SBTreeTestIT.KEYS_COUNT; i < (2 * (SBTreeTestIT.KEYS_COUNT)); i++) {
            Assert.assertNull(sbTree.get(i));
        }
    }

    @Test
    public void testKeyPutRandomUniform() throws Exception {
        final NavigableSet<Integer> keys = new TreeSet<>();
        final Random random = new Random();
        while ((keys.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Assert.assertEquals(sbTree.firstKey(), keys.first());
        Assert.assertEquals(sbTree.lastKey(), keys.last());
        for (int key : keys) {
            Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
        }
    }

    @Test
    public void testKeyPutRandomGaussian() throws Exception {
        NavigableSet<Integer> keys = new TreeSet<>();
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyPutRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        while ((keys.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = ((int) ((((random.nextGaussian()) * (Integer.MAX_VALUE)) / 2) + (Integer.MAX_VALUE)));
            if (key < 0)
                continue;

            sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Assert.assertEquals(sbTree.firstKey(), keys.first());
        Assert.assertEquals(sbTree.lastKey(), keys.last());
        for (int key : keys) {
            Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
        }
    }

    @Test
    public void testKeyDeleteRandomUniform() throws Exception {
        NavigableSet<Integer> keys = new TreeSet<>();
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
            keys.add(i);
        }
        Iterator<Integer> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            int key = keysIterator.next();
            if ((key % 3) == 0) {
                sbTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(sbTree.firstKey(), keys.first());
        Assert.assertEquals(sbTree.lastKey(), keys.last());
        for (int key : keys) {
            if ((key % 3) == 0) {
                Assert.assertNull(sbTree.get(key));
            } else {
                Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
            }
        }
    }

    @Test
    public void testKeyDeleteRandomGaussian() throws Exception {
        NavigableSet<Integer> keys = new TreeSet<>();
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyDeleteRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        while ((keys.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = ((int) ((((random.nextGaussian()) * (Integer.MAX_VALUE)) / 2) + (Integer.MAX_VALUE)));
            if (key < 0)
                continue;

            sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Iterator<Integer> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            int key = keysIterator.next();
            if ((key % 3) == 0) {
                sbTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(sbTree.firstKey(), keys.first());
        Assert.assertEquals(sbTree.lastKey(), keys.last());
        for (int key : keys) {
            if ((key % 3) == 0) {
                Assert.assertNull(sbTree.get(key));
            } else {
                Assert.assertEquals(sbTree.get(key), new ORecordId((key % 32000), key));
            }
        }
    }

    @Test
    public void testKeyDelete() throws Exception {
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertEquals(sbTree.remove(i), new ORecordId((i % 32000), i));

        }
        Assert.assertEquals(((int) (sbTree.firstKey())), 1);
        // noinspection ConstantConditions
        Assert.assertEquals(((int) (sbTree.lastKey())), ((((SBTreeTestIT.KEYS_COUNT) - 1) % 3) == 0 ? (SBTreeTestIT.KEYS_COUNT) - 2 : (SBTreeTestIT.KEYS_COUNT) - 1));
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertNull(sbTree.get(i));
            else
                Assert.assertEquals(sbTree.get(i), new ORecordId((i % 32000), i));

        }
    }

    @Test
    public void testKeyAddDelete() throws Exception {
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
            Assert.assertEquals(sbTree.get(i), new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertEquals(sbTree.remove(i), new ORecordId((i % 32000), i));

            if ((i % 2) == 0)
                sbTree.put(((SBTreeTestIT.KEYS_COUNT) + i), new ORecordId((((SBTreeTestIT.KEYS_COUNT) + i) % 32000), ((SBTreeTestIT.KEYS_COUNT) + i)));

        }
        Assert.assertEquals(((int) (sbTree.firstKey())), 1);
        Assert.assertEquals(((int) (sbTree.lastKey())), ((2 * (SBTreeTestIT.KEYS_COUNT)) - 2));
        for (int i = 0; i < (SBTreeTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertNull(sbTree.get(i));
            else
                Assert.assertEquals(sbTree.get(i), new ORecordId((i % 32000), i));

            if ((i % 2) == 0)
                Assert.assertEquals(sbTree.get(((SBTreeTestIT.KEYS_COUNT) + i)), new ORecordId((((SBTreeTestIT.KEYS_COUNT) + i) % 32000), ((SBTreeTestIT.KEYS_COUNT) + i)));

        }
    }

    @Test
    public void testIterateEntriesMajor() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<>();
        Random random = new Random();
        while ((keyValues.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertIterateMajorEntries(keyValues, random, true, true);
        assertIterateMajorEntries(keyValues, random, false, true);
        assertIterateMajorEntries(keyValues, random, true, false);
        assertIterateMajorEntries(keyValues, random, false, false);
        Assert.assertEquals(sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testIterateEntriesMinor() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<>();
        Random random = new Random();
        while ((keyValues.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertIterateMinorEntries(keyValues, random, true, true);
        assertIterateMinorEntries(keyValues, random, false, true);
        assertIterateMinorEntries(keyValues, random, true, false);
        assertIterateMinorEntries(keyValues, random, false, false);
        Assert.assertEquals(sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testIterateEntriesBetween() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<>();
        Random random = new Random();
        while ((keyValues.size()) < (SBTreeTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertIterateBetweenEntries(keyValues, random, true, true, true);
        assertIterateBetweenEntries(keyValues, random, true, false, true);
        assertIterateBetweenEntries(keyValues, random, false, true, true);
        assertIterateBetweenEntries(keyValues, random, false, false, true);
        assertIterateBetweenEntries(keyValues, random, true, true, false);
        assertIterateBetweenEntries(keyValues, random, true, false, false);
        assertIterateBetweenEntries(keyValues, random, false, true, false);
        assertIterateBetweenEntries(keyValues, random, false, false, false);
        Assert.assertEquals(sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testAddKeyValuesInTwoBucketsAndMakeFirstEmpty() throws Exception {
        for (int i = 0; i < 5167; i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < 3500; i++) {
            sbTree.remove(i);
        }
        Assert.assertEquals(((int) (sbTree.firstKey())), 3500);
        for (int i = 0; i < 3500; i++) {
            Assert.assertNull(sbTree.get(i));
        }
        for (int i = 3500; i < 5167; i++) {
            Assert.assertEquals(sbTree.get(i), new ORecordId((i % 32000), i));
        }
    }

    @Test
    public void testAddKeyValuesInTwoBucketsAndMakeLastEmpty() throws Exception {
        for (int i = 0; i < 5167; i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 5166; i > 1700; i--) {
            sbTree.remove(i);
        }
        Assert.assertEquals(((int) (sbTree.lastKey())), 1700);
        for (int i = 5166; i > 1700; i--) {
            Assert.assertNull(sbTree.get(i));
        }
        for (int i = 1700; i >= 0; i--) {
            Assert.assertEquals(sbTree.get(i), new ORecordId((i % 32000), i));
        }
    }

    @Test
    public void testAddKeyValuesAndRemoveFirstMiddleAndLastPages() throws Exception {
        for (int i = 0; i < 12055; i++) {
            sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < 1730; i++) {
            sbTree.remove(i);
        }
        for (int i = 3440; i < 6900; i++) {
            sbTree.remove(i);
        }
        for (int i = 8600; i < 12055; i++)
            sbTree.remove(i);

        Assert.assertEquals(((int) (sbTree.firstKey())), 1730);
        Assert.assertEquals(((int) (sbTree.lastKey())), 8599);
        Set<OIdentifiable> identifiables = new HashSet<>();
        OSBTreeCursor<Integer, OIdentifiable> cursor = sbTree.iterateEntriesMinor(7200, true, true);
        cursorToSet(identifiables, cursor);
        for (int i = 7200; i >= 6900; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 3439; i >= 1730; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        cursor = sbTree.iterateEntriesMinor(7200, true, false);
        cursorToSet(identifiables, cursor);
        for (int i = 7200; i >= 6900; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 3439; i >= 1730; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        cursor = sbTree.iterateEntriesMajor(1740, true, true);
        cursorToSet(identifiables, cursor);
        for (int i = 1740; i < 3440; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 6900; i < 8600; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        cursor = sbTree.iterateEntriesMajor(1740, true, false);
        cursorToSet(identifiables, cursor);
        for (int i = 1740; i < 3440; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 6900; i < 8600; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        cursor = sbTree.iterateEntriesBetween(1740, true, 7200, true, true);
        cursorToSet(identifiables, cursor);
        for (int i = 1740; i < 3440; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 6900; i <= 7200; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        cursor = sbTree.iterateEntriesBetween(1740, true, 7200, true, false);
        cursorToSet(identifiables, cursor);
        for (int i = 1740; i < 3440; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 6900; i <= 7200; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
    }

    @Test
    public void testNullKeysInSBTree() throws Exception {
        final OSBTree<Integer, OIdentifiable> nullSBTree = new OSBTree("nullSBTree", ".sbt", ".nbt", ((OAbstractPaginatedStorage) (getStorage())));
        nullSBTree.create(INSTANCE, OLinkSerializer.INSTANCE, null, 1, true, null);
        try {
            for (int i = 0; i < 10; i++)
                nullSBTree.put(i, new ORecordId(3, i));

            OIdentifiable identifiable = nullSBTree.get(null);
            Assert.assertNull(identifiable);
            nullSBTree.put(null, new ORecordId(10, 1000));
            identifiable = nullSBTree.get(null);
            Assert.assertEquals(identifiable, new ORecordId(10, 1000));
            OIdentifiable removed = nullSBTree.remove(5);
            Assert.assertEquals(removed, new ORecordId(3, 5));
            removed = nullSBTree.remove(null);
            Assert.assertEquals(removed, new ORecordId(10, 1000));
            removed = nullSBTree.remove(null);
            Assert.assertNull(removed);
            identifiable = nullSBTree.get(null);
            Assert.assertNull(identifiable);
        } finally {
            nullSBTree.delete();
        }
    }
}

