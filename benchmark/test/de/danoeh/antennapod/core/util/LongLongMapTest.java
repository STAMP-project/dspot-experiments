package de.danoeh.antennapod.core.util;


import org.junit.Assert;
import org.junit.Test;


public class LongLongMapTest {
    @Test
    public void testEmptyMap() {
        LongIntMap map = new LongIntMap();
        Assert.assertEquals(0, map.size());
        Assert.assertEquals("LongLongMap{}", map.toString());
        Assert.assertEquals(0, map.get(42));
        Assert.assertEquals((-1), map.get(42, (-1)));
        Assert.assertEquals(false, map.delete(42));
        Assert.assertEquals((-1), map.indexOfKey(42));
        Assert.assertEquals((-1), map.indexOfValue(42));
        Assert.assertEquals(1, map.hashCode());
    }

    @Test
    public void testSingleElement() {
        LongIntMap map = new LongIntMap();
        map.put(17, 42);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("LongLongMap{17=42}", map.toString());
        Assert.assertEquals(42, map.get(17));
        Assert.assertEquals(42, map.get(17, (-1)));
        Assert.assertEquals(0, map.indexOfKey(17));
        Assert.assertEquals(0, map.indexOfValue(42));
        Assert.assertEquals(true, map.delete(17));
    }

    @Test
    public void testAddAndDelete() {
        LongIntMap map = new LongIntMap();
        for (int i = 0; i < 100; i++) {
            map.put((i * 17), (i * 42));
        }
        Assert.assertEquals(100, map.size());
        Assert.assertEquals(0, map.get(0));
        Assert.assertEquals(42, map.get(17));
        Assert.assertEquals(42, map.get(17, (-1)));
        Assert.assertEquals(1, map.indexOfKey(17));
        Assert.assertEquals(1, map.indexOfValue(42));
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(true, map.delete((i * 17)));
        }
    }

    @Test
    public void testOverwrite() {
        LongIntMap map = new LongIntMap();
        map.put(17, 42);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("LongLongMap{17=42}", map.toString());
        Assert.assertEquals(42, map.get(17));
        map.put(17, 23);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("LongLongMap{17=23}", map.toString());
        Assert.assertEquals(23, map.get(17));
    }
}

