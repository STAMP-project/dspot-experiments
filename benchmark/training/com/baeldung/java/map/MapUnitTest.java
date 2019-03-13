package com.baeldung.java.map;


import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MapUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(MapUnitTest.class);

    @Test
    public void givenHashMap_whenRetrievesKeyset_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "baeldung");
        map.put("type", "blog");
        Set<String> keys = map.keySet();
        Assert.assertEquals(2, keys.size());
        Assert.assertTrue(keys.contains("name"));
        Assert.assertTrue(keys.contains("type"));
    }

    @Test
    public void givenHashMap_whenRetrievesValues_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "baeldung");
        map.put("type", "blog");
        Collection<String> values = map.values();
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(values.contains("baeldung"));
        Assert.assertTrue(values.contains("blog"));
    }

    @Test
    public void givenHashMap_whenRetrievesEntries_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "baeldung");
        map.put("type", "blog");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Assert.assertEquals(2, entries.size());
        for (Map.Entry<String, String> e : entries) {
            String key = e.getKey();
            String val = e.getValue();
            Assert.assertTrue(((key.equals("name")) || (key.equals("type"))));
            Assert.assertTrue(((val.equals("baeldung")) || (val.equals("blog"))));
        }
    }

    @Test
    public void givenKeySet_whenChangeReflectsInMap_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "baeldung");
        map.put("type", "blog");
        Assert.assertEquals(2, map.size());
        Set<String> keys = map.keySet();
        keys.remove("name");
        Assert.assertEquals(1, map.size());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void givenIterator_whenFailsFastOnModification_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "baeldung");
        map.put("type", "blog");
        Set<String> keys = map.keySet();
        Iterator<String> it = keys.iterator();
        map.remove("type");
        while (it.hasNext()) {
            String key = it.next();
        } 
    }

    @Test
    public void whenHashCodeIsCalledOnPut_thenCorrect() {
        MyKey key = new MyKey(1, "name");
        Map<MyKey, String> map = new HashMap<>();
        map.put(key, "val");
    }

    @Test
    public void whenHashCodeIsCalledOnGet_thenCorrect() {
        MyKey key = new MyKey(1, "name");
        Map<MyKey, String> map = new HashMap<>();
        map.put(key, "val");
        map.get(key);
    }

    @Test
    public void whenGetWorks_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "val");
        String val = map.get("key");
        Assert.assertEquals("val", val);
    }

    @Test
    public void givenNewKey_whenPutReturnsNull_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        String rtnVal = map.put("key1", "val1");
        Assert.assertNull(rtnVal);
    }

    @Test
    public void givenUnmappedKey_whenGetReturnsNull_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        String rtnVal = map.get("key1");
        Assert.assertNull(rtnVal);
    }

    @Test
    public void givenNullVal_whenPutReturnsNull_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        String rtnVal = map.put("key1", null);
        Assert.assertNull(rtnVal);
    }

    @Test
    public void givenNullKeyAndVal_whenAccepts_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put(null, null);
    }

    @Test
    public void givenNullVal_whenRetrieves_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("key", null);
        String val = map.get("key");
        Assert.assertNull(val);
    }

    @Test
    public void whenContainsDistinguishesNullValues_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        String val1 = map.get("key");
        boolean valPresent = map.containsKey("key");
        Assert.assertNull(val1);
        Assert.assertFalse(valPresent);
        map.put("key", null);
        String val = map.get("key");
        valPresent = map.containsKey("key");
        Assert.assertNull(val);
        Assert.assertTrue(valPresent);
    }

    @Test
    public void whenPutReturnsPrevValue_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        String rtnVal = map.put("key1", "val2");
        Assert.assertEquals("val1", rtnVal);
    }

    @Test
    public void whenCallsEqualsOnCollision_thenCorrect() {
        HashMap<MyKey, String> map = new HashMap<>();
        MyKey k1 = new MyKey(1, "firstKey");
        MyKey k2 = new MyKey(2, "secondKey");
        MyKey k3 = new MyKey(2, "thirdKey");
        MapUnitTest.LOG.debug("storing value for k1");
        map.put(k1, "firstValue");
        MapUnitTest.LOG.debug("storing value for k2");
        map.put(k2, "secondValue");
        MapUnitTest.LOG.debug("storing value for k3");
        map.put(k3, "thirdValue");
        MapUnitTest.LOG.debug("retrieving value for k1");
        String v1 = map.get(k1);
        MapUnitTest.LOG.debug("retrieving value for k2");
        String v2 = map.get(k2);
        MapUnitTest.LOG.debug("retrieving value for k3");
        String v3 = map.get(k3);
        Assert.assertEquals("firstValue", v1);
        Assert.assertEquals("secondValue", v2);
        Assert.assertEquals("thirdValue", v3);
    }

    @Test
    public void givenLinkedHashMap_whenGetsOrderedKeyset_thenCorrect() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(1, null);
        map.put(2, null);
        map.put(3, null);
        map.put(4, null);
        map.put(5, null);
        Set<Integer> keys = map.keySet();
        Integer[] arr = keys.toArray(new Integer[0]);
        for (int i = 0; i < (arr.length); i++) {
            Assert.assertEquals(new Integer((i + 1)), arr[i]);
        }
    }

    @Test
    public void givenLinkedHashMap_whenAccessOrderWorks_thenCorrect() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>(16, 0.75F, true);
        map.put(1, null);
        map.put(2, null);
        map.put(3, null);
        map.put(4, null);
        map.put(5, null);
        Set<Integer> keys = map.keySet();
        Assert.assertEquals("[1, 2, 3, 4, 5]", keys.toString());
        map.get(4);
        Assert.assertEquals("[1, 2, 3, 5, 4]", keys.toString());
        map.get(1);
        Assert.assertEquals("[2, 3, 5, 4, 1]", keys.toString());
        map.get(3);
        Assert.assertEquals("[2, 5, 4, 1, 3]", keys.toString());
    }

    @Test
    public void givenLinkedHashMap_whenRemovesEldestEntry_thenCorrect() {
        LinkedHashMap<Integer, String> map = new MyLinkedHashMap(16, 0.75F, true);
        map.put(1, null);
        map.put(2, null);
        map.put(3, null);
        map.put(4, null);
        map.put(5, null);
        Set<Integer> keys = map.keySet();
        Assert.assertEquals("[1, 2, 3, 4, 5]", keys.toString());
        map.put(6, null);
        Assert.assertEquals("[2, 3, 4, 5, 6]", keys.toString());
        map.put(7, null);
        Assert.assertEquals("[3, 4, 5, 6, 7]", keys.toString());
        map.put(8, null);
        Assert.assertEquals("[4, 5, 6, 7, 8]", keys.toString());
    }

    @Test
    public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect() {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(3, "val");
        map.put(2, "val");
        map.put(1, "val");
        map.put(5, "val");
        map.put(4, "val");
        Assert.assertEquals("[1, 2, 3, 4, 5]", map.keySet().toString());
    }

    @Test
    public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect2() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("c", "val");
        map.put("b", "val");
        map.put("a", "val");
        map.put("e", "val");
        map.put("d", "val");
        Assert.assertEquals("[a, b, c, d, e]", map.keySet().toString());
    }

    @Test
    public void givenTreeMap_whenOrdersEntriesByComparator_thenCorrect() {
        TreeMap<Integer, String> map = new TreeMap<>(Comparator.reverseOrder());
        map.put(3, "val");
        map.put(2, "val");
        map.put(1, "val");
        map.put(5, "val");
        map.put(4, "val");
        Assert.assertEquals("[5, 4, 3, 2, 1]", map.keySet().toString());
    }

    @Test
    public void givenTreeMap_whenPerformsQueries_thenCorrect() {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(3, "val");
        map.put(2, "val");
        map.put(1, "val");
        map.put(5, "val");
        map.put(4, "val");
        Integer highestKey = map.lastKey();
        Integer lowestKey = map.firstKey();
        Set<Integer> keysLessThan3 = map.headMap(3).keySet();
        Set<Integer> keysGreaterThanEqTo3 = map.tailMap(3).keySet();
        Assert.assertEquals(new Integer(5), highestKey);
        Assert.assertEquals(new Integer(1), lowestKey);
        Assert.assertEquals("[1, 2]", keysLessThan3.toString());
        Assert.assertEquals("[3, 4, 5]", keysGreaterThanEqTo3.toString());
    }
}

