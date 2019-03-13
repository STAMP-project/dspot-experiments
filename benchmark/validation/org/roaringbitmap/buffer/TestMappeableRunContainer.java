package org.roaringbitmap.buffer;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.LongBuffer;
import org.junit.Assert;
import org.junit.Test;

import static MappeableArrayContainer.DEFAULT_MAX_SIZE;


public class TestMappeableRunContainer {
    @Test
    public void constructorArray() {
        MappeableArrayContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 100);
        MappeableRunContainer rc = new MappeableRunContainer(ac, 1);
        Assert.assertEquals(99, rc.getCardinality());
        for (int i = 1; i < 100; i++) {
            Assert.assertTrue(rc.contains(((short) (i))));
        }
    }

    @Test
    public void constructorBitmap() {
        LongBuffer buffer = LongBuffer.allocate(((MappeableBitmapContainer.MAX_CAPACITY) / 64));
        buffer.put((~0L));
        MappeableBitmapContainer bc = new MappeableBitmapContainer(buffer.asReadOnlyBuffer(), 64);
        MappeableRunContainer rc = new MappeableRunContainer(bc, 1);
        Assert.assertEquals(64, rc.getCardinality());
        for (int i = 0; i < 64; i++) {
            Assert.assertTrue(rc.contains(((short) (i))));
        }
    }

    @Test
    public void not() {
        MappeableRunContainer rc = TestMappeableRunContainer.newRunContainer(1, 13);
        MappeableContainer result = rc.not(5, 8);
        Assert.assertEquals(9, result.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(rc.contains(((short) (i))));
        }
        for (int i = 8; i < 13; i++) {
            Assert.assertTrue(rc.contains(((short) (i))));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectInvalidPosition() {
        MappeableContainer bc = new MappeableRunContainer();
        bc = bc.add(1, 13);
        bc.select(100);
    }

    @Test
    public void trim() {
        MappeableContainer rc = new MappeableRunContainer(10);
        rc = rc.add(1, 5);
        rc.trim();
        Assert.assertEquals(4, rc.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(rc.contains(((short) (i))));
        }
    }

    @Test
    public void roundtrip() throws Exception {
        MappeableContainer rc = new MappeableRunContainer();
        rc = rc.add(1, 5);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oo = new ObjectOutputStream(bos)) {
            rc.writeExternal(oo);
        }
        MappeableContainer rc2 = new MappeableRunContainer();
        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        rc2.readExternal(new ObjectInputStream(bis));
        Assert.assertEquals(4, rc2.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(rc2.contains(((short) (i))));
        }
    }

    @Test
    public void andCardinalityArray() {
        MappeableRunContainer rc = new MappeableRunContainer();
        MappeableArrayContainer ac = new MappeableArrayContainer();
        ac.iadd(15, 25);
        Assert.assertEquals(0, rc.andCardinality(ac));
        rc.iadd(10, 20);
        Assert.assertEquals(5, rc.andCardinality(ac));
        rc.iadd(24, 30);
        Assert.assertEquals(6, rc.andCardinality(ac));
    }

    @Test
    public void andCardinalityBitmap() {
        MappeableRunContainer rc = new MappeableRunContainer();
        MappeableBitmapContainer ac = new MappeableBitmapContainer();
        ac.iadd(15, 25);
        Assert.assertEquals(0, rc.andCardinality(ac));
        rc.iadd(10, 20);
        Assert.assertEquals(5, rc.andCardinality(ac));
        rc.iadd(24, 30);
        Assert.assertEquals(6, rc.andCardinality(ac));
    }

    @Test
    public void andCardinalityRun() {
        MappeableRunContainer rc1 = new MappeableRunContainer();
        MappeableRunContainer rc2 = new MappeableRunContainer();
        rc2.iadd(15, 25);
        Assert.assertEquals(0, rc1.andCardinality(rc2));
        Assert.assertEquals(0, rc2.andCardinality(rc1));
        rc1.iadd(10, 20);
        Assert.assertEquals(5, rc1.andCardinality(rc2));
        Assert.assertEquals(5, rc2.andCardinality(rc1));
        rc1.iadd(24, 30);
        Assert.assertEquals(6, rc1.andCardinality(rc2));
        Assert.assertEquals(6, rc2.andCardinality(rc1));
        rc1.iadd(55, 66);
        rc2.iadd(100, 110);
        Assert.assertEquals(6, rc1.andCardinality(rc2));
        Assert.assertEquals(6, rc2.andCardinality(rc1));
        rc1.iadd(100, 110);
        rc1.iadd(120, 130);
        rc2.iadd(130, 140);
        Assert.assertEquals(16, rc1.andCardinality(rc2));
        Assert.assertEquals(16, rc2.andCardinality(rc1));
    }

    @Test
    public void orFullToRunContainer() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(0, (1 << 15));
        MappeableBitmapContainer half = new MappeableBitmapContainer((1 << 15), (1 << 16));
        Assert.assertTrue((rc instanceof MappeableRunContainer));
        MappeableContainer result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof MappeableRunContainer));
    }

    @Test
    public void orFullToRunContainer2() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(0, (1 << 15));
        MappeableArrayContainer half = new MappeableArrayContainer((1 << 15), (1 << 16));
        Assert.assertTrue((rc instanceof MappeableRunContainer));
        MappeableContainer result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof MappeableRunContainer));
    }

    @Test
    public void orFullToRunContainer3() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(0, (1 << 15));
        MappeableContainer half = MappeableContainer.rangeOfOnes((1 << 15), (1 << 16));
        Assert.assertTrue((rc instanceof MappeableRunContainer));
        MappeableContainer result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof MappeableRunContainer));
    }

    @Test
    public void testLazyORFull() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(0, (1 << 15));
        MappeableBitmapContainer bc2 = new MappeableBitmapContainer(3210, (1 << 16));
        MappeableContainer rbc = rc.lazyOR(bc2);
        Assert.assertEquals((-1), rbc.getCardinality());
        MappeableContainer repaired = rbc.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertTrue((repaired instanceof MappeableRunContainer));
    }

    @Test
    public void testLazyORFull2() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(((1 << 10) - 200), (1 << 16));
        MappeableArrayContainer ac = new MappeableArrayContainer(0, (1 << 10));
        MappeableContainer rbc = rc.lazyOR(ac);
        Assert.assertEquals((1 << 16), rbc.getCardinality());
        Assert.assertTrue((rbc instanceof MappeableRunContainer));
    }

    @Test
    public void testLazyORFull3() {
        MappeableContainer rc = MappeableContainer.rangeOfOnes(0, (1 << 15));
        MappeableContainer rc2 = MappeableContainer.rangeOfOnes((1 << 15), (1 << 16));
        MappeableContainer result = rc.lazyOR(rc2);
        MappeableContainer iresult = rc.lazyIOR(rc2);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertEquals((1 << 16), iresult.getCardinality());
        Assert.assertTrue((result instanceof MappeableRunContainer));
        Assert.assertTrue((iresult instanceof MappeableRunContainer));
    }

    @Test
    public void testRangeCardinality() {
        MappeableBitmapContainer bc = TestMappeableBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        MappeableRunContainer rc = TestMappeableRunContainer.generateContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        MappeableContainer result = rc.or(bc);
        Assert.assertEquals(8677, result.getCardinality());
    }

    @Test
    public void testRangeCardinality2() {
        MappeableBitmapContainer bc = TestMappeableBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        bc.add(((short) (22345)));// important case to have greater element than run container

        bc.add(Short.MAX_VALUE);
        MappeableRunContainer rc = TestMappeableRunContainer.generateContainer(new short[]{ 7, 300, 400, 900, 1400, 18000 }, 3);
        Assert.assertTrue(((rc.getCardinality()) > (DEFAULT_MAX_SIZE)));
        MappeableContainer result = rc.andNot(bc);
        Assert.assertEquals(11437, result.getCardinality());
    }

    @Test
    public void testRangeCardinality3() {
        MappeableBitmapContainer bc = TestMappeableBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        MappeableRunContainer rc = TestMappeableRunContainer.generateContainer(new short[]{ 7, 300, 400, 900, 1400, 5200 }, 3);
        MappeableBitmapContainer result = ((MappeableBitmapContainer) (rc.and(bc)));
        Assert.assertEquals(5046, result.getCardinality());
    }

    @Test
    public void testRangeCardinality4() {
        MappeableBitmapContainer bc = TestMappeableBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        MappeableRunContainer rc = TestMappeableRunContainer.generateContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        MappeableBitmapContainer result = ((MappeableBitmapContainer) (rc.xor(bc)));
        Assert.assertEquals(6031, result.getCardinality());
    }

    @Test
    public void testEqualsArrayContainer_Equal() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 10);
        Assert.assertTrue(rc.equals(ac));
        Assert.assertTrue(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayLarger() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArraySmaller() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 9);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayShifted() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(1, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayDiscontiguous() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 11);
        ac.flip(((short) (9)));
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }
}

