package com.orientechnologies.orient.core.storage.index.sbtreebonsai.local;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 12.08.13
 */
public class OSBTreeBonsaiLocalTestIT {
    private static final int KEYS_COUNT = 500000;

    protected static OSBTreeBonsaiLocal<Integer, OIdentifiable> sbTree;

    protected static ODatabaseDocumentTx databaseDocumentTx;

    @Test
    public void testGetFisrtKeyInEmptyTree() throws Exception {
        Integer result = OSBTreeBonsaiLocalTestIT.sbTree.firstKey();
        Assert.assertNull(result);
    }

    @Test
    public void testKeyPut() throws Exception {
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            // Assert.assertEquals(sbTree.get(i), new ORecordId(i % 32000, i), i + " key is absent");
            Assertions.assertThat(OSBTreeBonsaiLocalTestIT.sbTree.get(i)).isEqualTo(new ORecordId((i % 32000), i));
        }
        Assert.assertEquals(0, ((int) (OSBTreeBonsaiLocalTestIT.sbTree.firstKey())));
        Assert.assertEquals(((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) - 1), ((int) (OSBTreeBonsaiLocalTestIT.sbTree.lastKey())));
        for (int i = OSBTreeBonsaiLocalTestIT.KEYS_COUNT; i < (2 * (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)); i++)
            Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(i));

    }

    @Test
    public void testKeyPutRandomUniform() throws Exception {
        final NavigableSet<Integer> keys = new TreeSet<Integer>();
        final Random random = new Random();
        while ((keys.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keys.first());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keys.last());
        for (int key : keys)
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));

    }

    @Test
    public void testKeyPutRandomGaussian() throws Exception {
        final double mx = (Integer.MAX_VALUE) / 2.0;
        final double dx = (Integer.MAX_VALUE) / 8.0;
        NavigableSet<Integer> keys = new TreeSet<Integer>();
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyPutRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        while ((keys.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = generateGaussianKey(mx, dx, random);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keys.first());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keys.last());
        for (int key : keys)
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));

    }

    @Test
    public void testKeyDeleteRandomUniform() throws Exception {
        NavigableSet<Integer> keys = new TreeSet<Integer>();
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));
            keys.add(i);
        }
        Iterator<Integer> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            int key = keysIterator.next();
            if ((key % 3) == 0) {
                OSBTreeBonsaiLocalTestIT.sbTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keys.first());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keys.last());
        for (int key : keys) {
            if ((key % 3) == 0) {
                Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(key));
            } else {
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));
            }
        }
    }

    @Test
    public void testKeyDeleteRandomGaussian() throws Exception {
        final double mx = (Integer.MAX_VALUE) / 2.0;
        final double dx = (Integer.MAX_VALUE) / 8.0;
        NavigableSet<Integer> keys = new TreeSet<Integer>();
        long seed = System.currentTimeMillis();
        System.out.println(("testKeyDeleteRandomGaussian seed : " + seed));
        Random random = new Random(seed);
        while ((keys.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = generateGaussianKey(mx, dx, random);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keys.add(key);
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));
        } 
        Iterator<Integer> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            int key = keysIterator.next();
            if ((key % 3) == 0) {
                OSBTreeBonsaiLocalTestIT.sbTree.remove(key);
                keysIterator.remove();
            }
        } 
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keys.first());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keys.last());
        for (int key : keys) {
            if ((key % 3) == 0) {
                Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(key));
            } else {
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(key), new ORecordId((key % 32000), key));
            }
        }
    }

    @Test
    public void testKeyDelete() throws Exception {
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.remove(i), new ORecordId((i % 32000), i));

        }
        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.firstKey())), 1);
        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.lastKey())), ((((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) - 1) % 3) == 0 ? (OSBTreeBonsaiLocalTestIT.KEYS_COUNT) - 2 : (OSBTreeBonsaiLocalTestIT.KEYS_COUNT) - 1));
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(i));
            else
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(i), new ORecordId((i % 32000), i));

        }
    }

    @Test
    public void testKeyAddDelete() throws Exception {
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(i), new ORecordId((i % 32000), i));
        }
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.remove(i), new ORecordId((i % 32000), i));

            if ((i % 2) == 0)
                OSBTreeBonsaiLocalTestIT.sbTree.put(((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i), new ORecordId((((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i) % 32000), ((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i)));

        }
        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.firstKey())), 1);
        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.lastKey())), ((2 * (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) - 2));
        for (int i = 0; i < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT); i++) {
            if ((i % 3) == 0)
                Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(i));
            else
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(i), new ORecordId((i % 32000), i));

            if ((i % 2) == 0)
                Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i)), new ORecordId((((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i) % 32000), ((OSBTreeBonsaiLocalTestIT.KEYS_COUNT) + i)));

        }
    }

    @Test
    public void testValuesMajor() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<Integer, ORID>();
        Random random = new Random();
        while ((keyValues.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertMajorValues(keyValues, random, true);
        assertMajorValues(keyValues, random, false);
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testValuesMinor() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<Integer, ORID>();
        Random random = new Random();
        while ((keyValues.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertMinorValues(keyValues, random, true);
        assertMinorValues(keyValues, random, false);
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testValuesBetween() throws Exception {
        NavigableMap<Integer, ORID> keyValues = new TreeMap<Integer, ORID>();
        Random random = new Random();
        while ((keyValues.size()) < (OSBTreeBonsaiLocalTestIT.KEYS_COUNT)) {
            int key = random.nextInt(Integer.MAX_VALUE);
            OSBTreeBonsaiLocalTestIT.sbTree.put(key, new ORecordId((key % 32000), key));
            keyValues.put(key, new ORecordId((key % 32000), key));
        } 
        assertBetweenValues(keyValues, random, true, true);
        assertBetweenValues(keyValues, random, true, false);
        assertBetweenValues(keyValues, random, false, true);
        assertBetweenValues(keyValues, random, false, false);
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.firstKey(), keyValues.firstKey());
        Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.lastKey(), keyValues.lastKey());
    }

    @Test
    public void testAddKeyValuesInTwoBucketsAndMakeFirstEmpty() throws Exception {
        for (int i = 0; i < 110; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));

        for (int i = 0; i < 56; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.remove(i);

        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.firstKey())), 56);
        for (int i = 0; i < 56; i++)
            Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(i));

        for (int i = 56; i < 110; i++)
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(i), new ORecordId((i % 32000), i));

    }

    @Test
    public void testAddKeyValuesInTwoBucketsAndMakeLastEmpty() throws Exception {
        for (int i = 0; i < 110; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));

        for (int i = 110; i > 50; i--)
            OSBTreeBonsaiLocalTestIT.sbTree.remove(i);

        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.lastKey())), 50);
        for (int i = 110; i > 50; i--)
            Assert.assertNull(OSBTreeBonsaiLocalTestIT.sbTree.get(i));

        for (int i = 50; i >= 0; i--)
            Assert.assertEquals(OSBTreeBonsaiLocalTestIT.sbTree.get(i), new ORecordId((i % 32000), i));

    }

    @Test
    public void testAddKeyValuesAndRemoveFirstMiddleAndLastPages() throws Exception {
        for (int i = 0; i < 326; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.put(i, new ORecordId((i % 32000), i));

        for (int i = 0; i < 60; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.remove(i);

        for (int i = 100; i < 220; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.remove(i);

        for (int i = 260; i < 326; i++)
            OSBTreeBonsaiLocalTestIT.sbTree.remove(i);

        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.firstKey())), 60);
        Assert.assertEquals(((int) (OSBTreeBonsaiLocalTestIT.sbTree.lastKey())), 259);
        Collection<OIdentifiable> result = OSBTreeBonsaiLocalTestIT.sbTree.getValuesMinor(250, true, (-1));
        Set<OIdentifiable> identifiables = new HashSet<OIdentifiable>(result);
        for (int i = 250; i >= 220; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 99; i >= 60; i--) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        result = OSBTreeBonsaiLocalTestIT.sbTree.getValuesMajor(70, true, (-1));
        identifiables = new HashSet<OIdentifiable>(result);
        for (int i = 70; i < 100; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 220; i < 260; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
        result = OSBTreeBonsaiLocalTestIT.sbTree.getValuesBetween(70, true, 250, true, (-1));
        identifiables = new HashSet<OIdentifiable>(result);
        for (int i = 70; i < 100; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        for (int i = 220; i <= 250; i++) {
            boolean removed = identifiables.remove(new ORecordId((i % 32000), i));
            Assert.assertTrue(removed);
        }
        Assert.assertTrue(identifiables.isEmpty());
    }
}

