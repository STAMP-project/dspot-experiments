package com.baeldung.java.concurrentmap;


import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentNavigableMapManualTest {
    @Test
    public void givenSkipListMap_whenAccessInMultiThreads_thenOrderingStable() throws InterruptedException {
        NavigableMap<Integer, String> skipListMap = new ConcurrentSkipListMap<>();
        updateMapConcurrently(skipListMap);
        Iterator<Integer> skipListIter = skipListMap.keySet().iterator();
        int previous = skipListIter.next();
        while (skipListIter.hasNext()) {
            int current = skipListIter.next();
            Assert.assertTrue((previous < current));
        } 
    }

    @Test
    public void givenSkipListMap_whenNavConcurrently_thenCountCorrect() throws InterruptedException {
        NavigableMap<Integer, Integer> skipListMap = new ConcurrentSkipListMap<>();
        int count = countMapElementByPollingFirstEntry(skipListMap);
        Assert.assertEquals((10000 * 4), count);
    }

    @Test
    public void givenTreeMap_whenNavConcurrently_thenCountError() throws InterruptedException {
        NavigableMap<Integer, Integer> treeMap = new TreeMap<>();
        int count = countMapElementByPollingFirstEntry(treeMap);
        Assert.assertNotEquals((10000 * 4), count);
    }
}

