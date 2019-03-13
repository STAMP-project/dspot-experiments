/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.essentials.collections;


import LongHashMap.Entry;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class LongHashMapTest {
    Random random;

    private String traceName;

    private long start;

    public LongHashMapTest() {
        this.random = new Random();
    }

    @Test
    public void testLongHashMapSimple() {
        LongHashMap<Object> map = new LongHashMap();
        map.put((1L << 33), "OK");
        Assert.assertNull(map.get(0));
        Assert.assertEquals("OK", map.get((1L << 33)));
        long keyLong = 2147483647L << (33L + 14);
        Assert.assertNull(map.remove(keyLong));
        map.put(keyLong, "OK");
        Assert.assertTrue(map.containsKey(keyLong));
        Assert.assertEquals("OK", map.remove(keyLong));
        keyLong = Long.MAX_VALUE;
        map.put(keyLong, "OK");
        Assert.assertTrue(map.containsKey(keyLong));
        keyLong = 8064216579113853113L;
        map.put(keyLong, "OK");
        Assert.assertTrue(map.containsKey(keyLong));
    }

    @Test
    public void testLongHashMapRandom() {
        LongHashMap<Object> map = new LongHashMap();
        testLongHashMapRandom(map);
    }

    @Test
    public void testLongHashMapRandom_Synchronized() {
        LongHashMap<Object> map = LongHashMap.createSynchronized();
        testLongHashMapRandom(map);
    }

    @Test
    public void testKeys() {
        LongHashMap map = new LongHashMap();
        map.put(0, "a");
        map.put((-98), "b");
        map.put(666, "c");
        map.put(Long.MAX_VALUE, "d");
        map.remove(666);
        long[] keys = map.keys();
        Assert.assertEquals(3, keys.length);
        Arrays.sort(keys);
        Assert.assertEquals((-98), keys[0]);
        Assert.assertEquals(0, keys[1]);
        Assert.assertEquals(Long.MAX_VALUE, keys[2]);
    }

    @Test
    public void testEntries() {
        LongHashMap map = new LongHashMap();
        map.put(0, "a");
        map.put((-98), "b");
        map.put(666, "c");
        map.put(Long.MAX_VALUE, "d");
        map.remove(666);
        LongHashMap[] entries = map.entries();
        Assert.assertEquals(3, entries.length);
        String all = "";
        for (LongHashMap.Entry entry : entries) {
            all += ((("(" + (entry.key)) + "=") + (entry.value)) + ")";
        }
        Assert.assertTrue(all, all.contains("(0=a)"));
        Assert.assertTrue(all, all.contains("(-98=b)"));
        Assert.assertTrue(all, all.contains((("(" + (Long.MAX_VALUE)) + "=d)")));
    }
}

