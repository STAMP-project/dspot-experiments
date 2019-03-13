package com.thinkaurelius.titan.util.datastructures;


import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.cursors.LongObjectCursor;
import com.google.common.collect.Iterables;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class RelationCacheTest {
    private static final Random random = new Random();

    @Test
    public void testMap() {
        int len = 100;
        LongObjectHashMap<Object> map = new LongObjectHashMap<Object>();
        for (int i = 1; i <= len; i++) {
            map.put((i * 1000), ("TestValue " + i));
        }
        Map<Long, Object> copy1 = new HashMap<Long, Object>();
        for (LongObjectCursor<Object> entry : map) {
            copy1.put(entry.key, entry.value);
        }
        Map<Long, Object> copy2 = new HashMap<Long, Object>();
        for (LongObjectCursor<Object> entry : map) {
            copy2.put(entry.key, entry.value);
        }
        Assert.assertEquals(len, map.size());
        Assert.assertEquals(len, copy1.size());
        Assert.assertEquals(len, copy2.size());
        for (int i = 1; i <= len; i++) {
            Assert.assertEquals(("TestValue " + i), map.get((i * 1000)));
            Assert.assertEquals(("TestValue " + i), copy1.get((i * 1000L)));
            Assert.assertEquals(("TestValue " + i), copy2.get((i * 1000L)));
        }
    }

    @Test
    public void testEmpty() {
        LongObjectHashMap<Object> map = new LongObjectHashMap<Object>();
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, Iterables.size(map));
    }

    @Test
    public void testPerformance() {
        int trials = 10;
        int iterations = 100000;
        for (int k = 0; k < iterations; k++) {
            int len = RelationCacheTest.random.nextInt(10);
            LongObjectHashMap<Object> map = new LongObjectHashMap<Object>();
            for (int i = 1; i <= len; i++) {
                map.put((i * 1000), ("TestValue " + i));
            }
            for (int t = 0; t < trials; t++) {
                for (int i = 1; i <= len; i++) {
                    Assert.assertEquals(("TestValue " + i), map.get((i * 1000)));
                }
                Assert.assertEquals(len, map.size());
                for (LongObjectCursor<Object> entry : map) {
                }
            }
        }
    }
}

