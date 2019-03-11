package com.thinkaurelius.titan.util.datastructures;


import org.junit.Assert;
import org.junit.Test;


public class BitMapTest {
    @Test
    public void testBitMap() {
        byte map = BitMap.setBitb(BitMap.createMapb(2), 4);
        Assert.assertTrue(BitMap.readBitb(map, 2));
        Assert.assertTrue(BitMap.readBitb(map, 4));
        map = BitMap.unsetBitb(map, 2);
        Assert.assertFalse(BitMap.readBitb(map, 2));
        Assert.assertFalse(BitMap.readBitb(map, 3));
        Assert.assertFalse(BitMap.readBitb(map, 7));
        map = BitMap.setBitb(map, 7);
        Assert.assertTrue(BitMap.readBitb(map, 7));
    }
}

