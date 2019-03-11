/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.cache;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RobolectricTestRunner.class)
public class CountingLruMapTest {
    private CountingLruMap<String, Integer> mCountingLruMap;

    @Test
    public void testInitialState() {
        Assert.assertEquals(0, mCountingLruMap.getCount());
        Assert.assertEquals(0, mCountingLruMap.getSizeInBytes());
    }

    @Test
    public void testPut() {
        // last inserted element should be last in the queue
        mCountingLruMap.put("key1", 110);
        Assert.assertEquals(1, mCountingLruMap.getCount());
        Assert.assertEquals(110, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1");
        assertValueOrder(110);
        mCountingLruMap.put("key2", 120);
        Assert.assertEquals(2, mCountingLruMap.getCount());
        Assert.assertEquals(230, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2");
        assertValueOrder(110, 120);
        mCountingLruMap.put("key3", 130);
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
    }

    @Test
    public void testPut_SameKeyTwice() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        // last inserted element should be last in the queue
        mCountingLruMap.put("key2", 150);
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(390, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key3", "key2");
        assertValueOrder(110, 130, 150);
    }

    @Test
    public void testGet() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        // get shouldn't affect the ordering, nor the size
        Assert.assertEquals(120, ((Object) (mCountingLruMap.get("key2"))));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertEquals(110, ((Object) (mCountingLruMap.get("key1"))));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertEquals(null, ((Object) (mCountingLruMap.get("key4"))));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertEquals(130, ((Object) (mCountingLruMap.get("key3"))));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
    }

    @Test
    public void testContains() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        // contains shouldn't affect the ordering, nor the size
        Assert.assertTrue(mCountingLruMap.contains("key2"));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertTrue(mCountingLruMap.contains("key1"));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertFalse(mCountingLruMap.contains("key4"));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertTrue(mCountingLruMap.contains("key3"));
        Assert.assertEquals(3, mCountingLruMap.getCount());
        Assert.assertEquals(360, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
    }

    @Test
    public void testRemove() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        Assert.assertEquals(120, ((Object) (mCountingLruMap.remove("key2"))));
        Assert.assertEquals(2, mCountingLruMap.getCount());
        Assert.assertEquals(240, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key3");
        assertValueOrder(110, 130);
        Assert.assertEquals(130, ((Object) (mCountingLruMap.remove("key3"))));
        Assert.assertEquals(1, mCountingLruMap.getCount());
        Assert.assertEquals(110, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1");
        assertValueOrder(110);
        Assert.assertEquals(null, ((Object) (mCountingLruMap.remove("key4"))));
        Assert.assertEquals(1, mCountingLruMap.getCount());
        Assert.assertEquals(110, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1");
        assertValueOrder(110);
        Assert.assertEquals(110, ((Object) (mCountingLruMap.remove("key1"))));
        Assert.assertEquals(0, mCountingLruMap.getCount());
        Assert.assertEquals(0, mCountingLruMap.getSizeInBytes());
        assertKeyOrder();
        assertValueOrder();
    }

    @Test
    public void testRemoveAll() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        mCountingLruMap.put("key4", 140);
        mCountingLruMap.removeAll(new com.facebook.common.internal.Predicate<String>() {
            @Override
            public boolean apply(String key) {
                return (key.equals("key2")) || (key.equals("key3"));
            }
        });
        Assert.assertEquals(2, mCountingLruMap.getCount());
        Assert.assertEquals(250, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key4");
        assertValueOrder(110, 140);
    }

    @Test
    public void testClear() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        mCountingLruMap.put("key4", 140);
        mCountingLruMap.clear();
        Assert.assertEquals(0, mCountingLruMap.getCount());
        Assert.assertEquals(0, mCountingLruMap.getSizeInBytes());
        assertKeyOrder();
        assertValueOrder();
    }

    @Test
    public void testGetMatchingEntries() {
        mCountingLruMap.put("key1", 110);
        mCountingLruMap.put("key2", 120);
        mCountingLruMap.put("key3", 130);
        mCountingLruMap.put("key4", 140);
        List<LinkedHashMap.Entry<String, Integer>> entries = mCountingLruMap.getMatchingEntries(new com.facebook.common.internal.Predicate<String>() {
            @Override
            public boolean apply(String key) {
                return (key.equals("key2")) || (key.equals("key3"));
            }
        });
        Assert.assertNotNull(entries);
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals("key2", entries.get(0).getKey());
        Assert.assertEquals(120, ((int) (entries.get(0).getValue())));
        Assert.assertEquals("key3", entries.get(1).getKey());
        Assert.assertEquals(130, ((int) (entries.get(1).getValue())));
        // getting entries should not affect the order nor the size
        Assert.assertEquals(4, mCountingLruMap.getCount());
        Assert.assertEquals(500, mCountingLruMap.getSizeInBytes());
        assertKeyOrder("key1", "key2", "key3", "key4");
        assertValueOrder(110, 120, 130, 140);
    }

    @Test
    public void testGetFirstKey() {
        mCountingLruMap.put("key1", 110);
        assertKeyOrder("key1");
        assertValueOrder(110);
        Assert.assertEquals("key1", mCountingLruMap.getFirstKey());
        mCountingLruMap.put("key2", 120);
        assertKeyOrder("key1", "key2");
        assertValueOrder(110, 120);
        Assert.assertEquals("key1", mCountingLruMap.getFirstKey());
        mCountingLruMap.put("key3", 130);
        assertKeyOrder("key1", "key2", "key3");
        assertValueOrder(110, 120, 130);
        Assert.assertEquals("key1", mCountingLruMap.getFirstKey());
        mCountingLruMap.put("key1", 140);
        assertKeyOrder("key2", "key3", "key1");
        assertValueOrder(120, 130, 140);
        Assert.assertEquals("key2", mCountingLruMap.getFirstKey());
        mCountingLruMap.remove("key3");
        assertKeyOrder("key2", "key1");
        assertValueOrder(120, 140);
        Assert.assertEquals("key2", mCountingLruMap.getFirstKey());
        mCountingLruMap.remove("key2");
        assertKeyOrder("key1");
        assertValueOrder(140);
        Assert.assertEquals("key1", mCountingLruMap.getFirstKey());
        mCountingLruMap.remove("key1");
        assertKeyOrder();
        assertValueOrder();
        Assert.assertEquals(null, mCountingLruMap.getFirstKey());
    }
}

