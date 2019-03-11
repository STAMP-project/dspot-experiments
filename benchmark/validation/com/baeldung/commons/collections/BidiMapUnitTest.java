package com.baeldung.commons.collections;


import org.apache.commons.collections4.BidiMap;
import org.junit.Assert;
import org.junit.Test;


public class BidiMapUnitTest {
    @Test
    public void givenKeyValue_whenPut_thenAddEntryToMap() {
        BidiMap<String, String> map = new org.apache.commons.collections4.bidimap.DualHashBidiMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Assert.assertEquals(map.size(), 2);
    }

    @Test
    public void whenInverseBidiMap_thenInverseKeyValue() {
        BidiMap<String, String> map = new org.apache.commons.collections4.bidimap.DualHashBidiMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        BidiMap<String, String> rMap = map.inverseBidiMap();
        Assert.assertTrue(((rMap.containsKey("value1")) && (rMap.containsKey("value2"))));
    }

    @Test
    public void givenValue_whenRemoveValue_thenRemoveMatchingMapEntry() {
        BidiMap<String, String> map = new org.apache.commons.collections4.bidimap.DualHashBidiMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.removeValue("value2");
        Assert.assertFalse(map.containsKey("key2"));
    }

    @Test
    public void givenValue_whenGetKey_thenMappedKey() {
        BidiMap<String, String> map = new org.apache.commons.collections4.bidimap.DualHashBidiMap();
        map.put("key1", "value1");
        Assert.assertEquals(map.getKey("value1"), "key1");
    }

    @Test
    public void givenKeyValue_whenAddValue_thenReplaceFirstKey() {
        BidiMap<String, String> map = new org.apache.commons.collections4.bidimap.DualHashBidiMap();
        map.put("key1", "value1");
        map.put("key2", "value1");
        Assert.assertEquals(map.size(), 1);
        Assert.assertFalse(map.containsKey("key1"));
    }
}

