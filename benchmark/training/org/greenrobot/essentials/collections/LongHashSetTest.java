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


import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class LongHashSetTest {
    Random random;

    private String traceName;

    private long start;

    public LongHashSetTest() {
        this.random = new Random();
    }

    @Test
    public void testLongHashSetSimple() {
        LongHashSet set = new LongHashSet();
        set.add((1L << 33));
        Assert.assertFalse(set.contains(0));
        Assert.assertTrue(set.contains((1L << 33)));
        long keyLong = 2147483647L << (33L + 14);
        Assert.assertFalse(set.remove(keyLong));
        set.add(keyLong);
        Assert.assertTrue(set.contains(keyLong));
        Assert.assertTrue(set.remove(keyLong));
        Assert.assertFalse(set.remove(keyLong));
        keyLong = Long.MAX_VALUE;
        set.add(keyLong);
        Assert.assertTrue(set.contains(keyLong));
        keyLong = 8064216579113853113L;
        set.add(keyLong);
        Assert.assertTrue(set.contains(keyLong));
    }

    @Test
    public void testLongHashMapRandom() {
        LongHashSet set = new LongHashSet();
        for (int i = 0; i < 5000; i++) {
            long key = random.nextLong();
            set.add(key);
            Assert.assertTrue(set.contains(key));
            int keyInt = ((int) (key));
            set.add(keyInt);
            Assert.assertTrue(set.contains(keyInt));
            Assert.assertTrue(set.remove(key));
            if (key != keyInt) {
                Assert.assertTrue(set.remove(keyInt));
            }
            Assert.assertFalse(set.remove(key));
            Assert.assertFalse(set.remove(keyInt));
        }
    }

    @Test
    public void testKeys() {
        LongHashSet set = new LongHashSet();
        set.add(0);
        set.add((-98));
        set.add(666);
        set.add(Long.MAX_VALUE);
        set.remove(666);
        long[] keys = set.keys();
        Assert.assertEquals(3, keys.length);
        Arrays.sort(keys);
        Assert.assertEquals((-98), keys[0]);
        Assert.assertEquals(0, keys[1]);
        Assert.assertEquals(Long.MAX_VALUE, keys[2]);
    }
}

