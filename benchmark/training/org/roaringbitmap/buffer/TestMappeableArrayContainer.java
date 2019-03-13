package org.roaringbitmap.buffer;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ShortBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.IntConsumer;


public class TestMappeableArrayContainer {
    @Test
    public void addEmptyRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.iadd(1, 1);
        Assert.assertEquals(0, ac.getCardinality());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addInvalidRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac.add(13, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac.iadd(13, 1);
    }

    @Test
    public void iaddSanityTest() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.iadd(10, 20);
        // insert disjoint at end
        ac = ac.iadd(30, 70);
        // insert disjoint between
        ac = ac.iadd(25, 26);
        // insert disjoint at start
        ac = ac.iadd(1, 2);
        // insert overlap at end
        ac = ac.iadd(60, 80);
        // insert overlap between
        ac = ac.iadd(10, 30);
        // insert overlap at start
        ac = ac.iadd(1, 20);
        Assert.assertEquals(79, ac.getCardinality());
    }

    @Test
    public void remove() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.iadd(1, 3);
        ac = ac.remove(((short) (2)));
        Assert.assertEquals(1, ac.getCardinality());
        Assert.assertTrue(ac.contains(((short) (1))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac.remove(13, 1);
    }

    @Test
    public void iremoveEmptyRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac.remove(1, 1);
        Assert.assertEquals(0, ac.getCardinality());
    }

    @Test(expected = IllegalArgumentException.class)
    public void iremoveInvalidRange() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac.iremove(13, 1);
    }

    @Test
    public void constructorWithRun() {
        MappeableContainer ac = new MappeableArrayContainer(1, 13);
        Assert.assertEquals(12, ac.getCardinality());
        for (int i = 1; i <= 12; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void add() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 2, 3, 5);
        ac = ac.add(((short) (4)));
        Assert.assertEquals(5, ac.getCardinality());
        for (int i = 1; i <= 5; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void add2() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 5000);
        ac = ac.add(((short) (7000)));
        Assert.assertEquals(5000, ac.getCardinality());
        for (int i = 1; i < 5000; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
        Assert.assertTrue(ac.contains(((short) (7000))));
    }

    @Test
    public void flip() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 2, 3, 5);
        ac = ac.flip(((short) (4)));
        Assert.assertEquals(5, ac.getCardinality());
        for (int i = 1; i <= 5; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void flip2() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 2, 3, 4, 5);
        ac = ac.flip(((short) (5)));
        Assert.assertEquals(4, ac.getCardinality());
        for (int i = 1; i <= 4; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void flip3() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 5000);
        ac = ac.flip(((short) (7000)));
        Assert.assertEquals(5000, ac.getCardinality());
        for (int i = 1; i < 5000; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
        Assert.assertTrue(ac.contains(((short) (7000))));
    }

    @Test
    public void iand() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.add(10, 20);
        MappeableContainer bc = new MappeableBitmapContainer();
        bc = bc.add(15, 25);
        ac.iand(bc);
        Assert.assertEquals(5, ac.getCardinality());
        for (int i = 15; i < 20; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void iandNotArray() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.add(10, 20);
        MappeableContainer ac2 = TestMappeableArrayContainer.newArrayContainer(15, 25);
        ac.iandNot(ac2);
        Assert.assertEquals(5, ac.getCardinality());
        for (int i = 10; i < 15; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void iandNotBitmap() {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.add(10, 20);
        MappeableContainer bc = new MappeableBitmapContainer();
        bc = bc.add(15, 25);
        ac.iandNot(bc);
        Assert.assertEquals(5, ac.getCardinality());
        for (int i = 10; i < 15; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void intersects() {
        MappeableContainer ac1 = new MappeableArrayContainer();
        ac1 = ac1.add(10, 20);
        MappeableContainer ac2 = new MappeableArrayContainer();
        ac2 = ac2.add(15, 25);
        Assert.assertTrue(ac1.intersects(ac2));
    }

    @Test
    public void numberOfRuns() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(1, 13);
        Assert.assertEquals(1, ac.numberOfRuns());
    }

    @Test
    public void roundtrip() throws Exception {
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.add(1, 5);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oo = new ObjectOutputStream(bos)) {
            ac.writeExternal(oo);
        }
        MappeableContainer ac2 = new MappeableArrayContainer();
        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ac2.readExternal(new ObjectInputStream(bis));
        Assert.assertEquals(4, ac2.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(ac2.contains(((short) (i))));
        }
    }

    @Test
    public void orArray() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(0, 8192);
        MappeableContainer ac2 = TestMappeableArrayContainer.newArrayContainer(15, 25);
        ac = ac.or(ac2);
        Assert.assertEquals(8192, ac.getCardinality());
        for (int i = 0; i < 8192; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void xorArray() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(0, 8192);
        MappeableContainer ac2 = TestMappeableArrayContainer.newArrayContainer(15, 25);
        ac = ac.xor(ac2);
        Assert.assertEquals(8182, ac.getCardinality());
        for (int i = 0; i < 15; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
        for (int i = 25; i < 8192; i++) {
            Assert.assertTrue(ac.contains(((short) (i))));
        }
    }

    @Test
    public void foreach() {
        MappeableContainer ac = TestMappeableArrayContainer.newArrayContainer(0, 64);
        ac.forEach(((short) (0)), new IntConsumer() {
            int expected = 0;

            @Override
            public void accept(int value) {
                Assert.assertEquals(value, ((expected)++));
            }
        });
    }

    @Test
    public void orFullToRunContainer() {
        MappeableArrayContainer ac = new MappeableArrayContainer(0, (1 << 12));
        MappeableBitmapContainer half = new MappeableBitmapContainer((1 << 12), (1 << 16));
        MappeableContainer result = ac.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertThat(result, CoreMatchers.instanceOf(MappeableRunContainer.class));
    }

    @Test
    public void orFullToRunContainer2() {
        MappeableArrayContainer ac = new MappeableArrayContainer(0, (1 << 15));
        MappeableArrayContainer half = new MappeableArrayContainer((1 << 15), (1 << 16));
        MappeableContainer result = ac.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertThat(result, CoreMatchers.instanceOf(MappeableRunContainer.class));
    }

    @Test
    public void testLazyORFull() {
        MappeableArrayContainer ac = new MappeableArrayContainer(0, (1 << 15));
        MappeableArrayContainer ac2 = new MappeableArrayContainer((1 << 15), (1 << 16));
        MappeableContainer rbc = ac.lazyor(ac2);
        Assert.assertEquals((-1), rbc.getCardinality());
        MappeableContainer repaired = rbc.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertThat(repaired, CoreMatchers.instanceOf(MappeableRunContainer.class));
    }

    @Test
    public void testToString() {
        MappeableArrayContainer ac1 = new MappeableArrayContainer(5, 15);
        ac1.add(((short) (-3)));
        ac1.add(((short) (-17)));
        Assert.assertEquals("{5,6,7,8,9,10,11,12,13,14,65519,65533}", ac1.toString());
    }

    @Test
    public void iorNotIncreaseCapacity() {
        MappeableArrayContainer ac1 = new MappeableArrayContainer();
        MappeableArrayContainer ac2 = new MappeableArrayContainer();
        ac1.add(((short) (128)));
        ac1.add(((short) (256)));
        ac2.add(((short) (1024)));
        ac1.ior(ac2);
        Assert.assertTrue(ac1.contains(((short) (128))));
        Assert.assertTrue(ac1.contains(((short) (256))));
        Assert.assertTrue(ac1.contains(((short) (1024))));
    }

    @Test
    public void iorIncreaseCapacity() {
        MappeableArrayContainer ac1 = new MappeableArrayContainer();
        MappeableArrayContainer ac2 = new MappeableArrayContainer();
        ac1.add(((short) (128)));
        ac1.add(((short) (256)));
        ac1.add(((short) (512)));
        ac1.add(((short) (513)));
        ac2.add(((short) (1024)));
        ac1.ior(ac2);
        Assert.assertTrue(ac1.contains(((short) (128))));
        Assert.assertTrue(ac1.contains(((short) (256))));
        Assert.assertTrue(ac1.contains(((short) (512))));
        Assert.assertTrue(ac1.contains(((short) (513))));
        Assert.assertTrue(ac1.contains(((short) (1024))));
    }

    @Test
    public void iorSanityCheck() {
        MappeableContainer ac = new MappeableArrayContainer().add(0, 10);
        MappeableContainer disjoint = new MappeableArrayContainer().add(20, 40);
        ac.ior(disjoint);
        Assert.assertTrue(ac.contains(disjoint));
    }

    @Test
    public void testIntersectsWithRange() {
        MappeableContainer container = new MappeableArrayContainer().add(0, 10);
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, TestMappeableArrayContainer.lower16Bits((-1))));
        Assert.assertFalse(container.intersects(11, TestMappeableArrayContainer.lower16Bits((-1))));
    }

    @Test
    public void testIntersectsWithRange2() {
        MappeableContainer container = new MappeableArrayContainer().add(TestMappeableArrayContainer.lower16Bits((-50)), TestMappeableArrayContainer.lower16Bits((-10)));
        Assert.assertFalse(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, TestMappeableArrayContainer.lower16Bits((-40))));
        Assert.assertFalse(container.intersects(TestMappeableArrayContainer.lower16Bits((-100)), TestMappeableArrayContainer.lower16Bits((-55))));
        Assert.assertFalse(container.intersects(TestMappeableArrayContainer.lower16Bits((-9)), TestMappeableArrayContainer.lower16Bits((-1))));
        Assert.assertTrue(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testIntersectsWithRange3() {
        MappeableContainer container = new MappeableArrayContainer().add(((short) (1))).add(((short) (300))).add(((short) (1024)));
        Assert.assertTrue(container.intersects(0, 300));
        Assert.assertTrue(container.intersects(1, 300));
        Assert.assertFalse(container.intersects(2, 300));
        Assert.assertFalse(container.intersects(2, 299));
        Assert.assertTrue(container.intersects(0, TestMappeableArrayContainer.lower16Bits((-1))));
        Assert.assertFalse(container.intersects(1025, (1 << 16)));
    }

    @Test
    public void testContainsRange() {
        MappeableContainer ac = new MappeableArrayContainer().add(20, 100);
        Assert.assertFalse(ac.contains(1, 21));
        Assert.assertFalse(ac.contains(1, 19));
        Assert.assertTrue(ac.contains(20, 100));
        Assert.assertTrue(ac.contains(20, 99));
        Assert.assertTrue(ac.contains(21, 100));
        Assert.assertFalse(ac.contains(21, 101));
        Assert.assertFalse(ac.contains(19, 99));
        Assert.assertFalse(ac.contains(190, 9999));
    }

    @Test
    public void testContainsRange2() {
        MappeableContainer ac = new MappeableArrayContainer().add(((short) (1))).add(((short) (10))).add(20, 100);
        Assert.assertFalse(ac.contains(1, 21));
        Assert.assertFalse(ac.contains(1, 20));
        Assert.assertTrue(ac.contains(1, 2));
    }

    @Test
    public void testContainsRangeUnsigned() {
        MappeableContainer ac = new MappeableArrayContainer().add((1 << 15), ((1 << 8) | (1 << 15)));
        Assert.assertTrue(ac.contains((1 << 15), ((1 << 8) | (1 << 15))));
        Assert.assertTrue(ac.contains((1 + (1 << 15)), (((1 << 8) | (1 << 15)) - 1)));
        Assert.assertFalse(ac.contains((1 + (1 << 15)), (((1 << 8) | (1 << 15)) + 1)));
        Assert.assertFalse(ac.contains(((1 << 15) - 1), (((1 << 8) | (1 << 15)) - 1)));
        Assert.assertFalse(ac.contains(0, (1 << 15)));
        Assert.assertFalse(ac.contains((((1 << 8) | (1 << 15)) | 1), (1 << 16)));
    }

    @Test
    public void testNextValueBeforeStart() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals(10, container.nextValue(((short) (5))));
    }

    @Test
    public void testNextValue() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals(10, container.nextValue(((short) (10))));
        Assert.assertEquals(20, container.nextValue(((short) (11))));
        Assert.assertEquals(30, container.nextValue(((short) (30))));
    }

    @Test
    public void testNextValueAfterEnd() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals((-1), container.nextValue(((short) (31))));
    }

    @Test
    public void testNextValue2() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129);
        Assert.assertTrue((container instanceof MappeableArrayContainer));
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals((-1), container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (5000))));
    }

    @Test
    public void testNextValueBetweenRuns() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129).iadd(256, 321);
        Assert.assertTrue((container instanceof MappeableArrayContainer));
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals(256, container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (512))));
    }

    @Test
    public void testNextValue3() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
        Assert.assertTrue((container instanceof MappeableArrayContainer));
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (63))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals(200, container.nextValue(((short) (129))));
        Assert.assertEquals(200, container.nextValue(((short) (199))));
        Assert.assertEquals(200, container.nextValue(((short) (200))));
        Assert.assertEquals(250, container.nextValue(((short) (250))));
        Assert.assertEquals(5000, container.nextValue(((short) (2500))));
        Assert.assertEquals(5000, container.nextValue(((short) (5000))));
        Assert.assertEquals(5200, container.nextValue(((short) (5200))));
        Assert.assertEquals((-1), container.nextValue(((short) (5201))));
    }

    @Test
    public void testPreviousValue1() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129);
        Assert.assertTrue((container instanceof MappeableArrayContainer));
        Assert.assertEquals((-1), container.previousValue(((short) (0))));
        Assert.assertEquals((-1), container.previousValue(((short) (63))));
        Assert.assertEquals(64, container.previousValue(((short) (64))));
        Assert.assertEquals(65, container.previousValue(((short) (65))));
        Assert.assertEquals(128, container.previousValue(((short) (128))));
        Assert.assertEquals(128, container.previousValue(((short) (129))));
    }

    @Test
    public void testPreviousValue2() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
        Assert.assertTrue((container instanceof MappeableArrayContainer));
        Assert.assertEquals((-1), container.previousValue(((short) (0))));
        Assert.assertEquals((-1), container.previousValue(((short) (63))));
        Assert.assertEquals(64, container.previousValue(((short) (64))));
        Assert.assertEquals(65, container.previousValue(((short) (65))));
        Assert.assertEquals(128, container.previousValue(((short) (128))));
        Assert.assertEquals(128, container.previousValue(((short) (129))));
        Assert.assertEquals(128, container.previousValue(((short) (199))));
        Assert.assertEquals(200, container.previousValue(((short) (200))));
        Assert.assertEquals(250, container.previousValue(((short) (250))));
        Assert.assertEquals(500, container.previousValue(((short) (2500))));
        Assert.assertEquals(5000, container.previousValue(((short) (5000))));
        Assert.assertEquals(5200, container.previousValue(((short) (5200))));
    }

    @Test
    public void testPreviousValueBeforeStart() {
        MappeableContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals((-1), container.previousValue(((short) (5))));
    }

    @Test
    public void testPreviousValueSparse() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals((-1), container.previousValue(((short) (9))));
        Assert.assertEquals(10, container.previousValue(((short) (10))));
        Assert.assertEquals(10, container.previousValue(((short) (11))));
        Assert.assertEquals(20, container.previousValue(((short) (21))));
        Assert.assertEquals(30, container.previousValue(((short) (30))));
    }

    @Test
    public void testPreviousValueUnsigned() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }), 2);
        Assert.assertEquals((-1), container.previousValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextValueUnsigned() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }), 2);
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals((-1), container.nextValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testPreviousValueAfterEnd() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals(30, container.previousValue(((short) (31))));
    }

    @Test
    public void testPreviousAbsentValue1() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129);
        Assert.assertEquals(0, container.previousAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (63))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (64))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (65))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.previousAbsentValue(((short) (129))));
    }

    @Test
    public void testPreviousAbsentValue2() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
        Assert.assertEquals(0, container.previousAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (63))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (64))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (65))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.previousAbsentValue(((short) (129))));
        Assert.assertEquals(199, container.previousAbsentValue(((short) (199))));
        Assert.assertEquals(199, container.previousAbsentValue(((short) (200))));
        Assert.assertEquals(199, container.previousAbsentValue(((short) (250))));
        Assert.assertEquals(2500, container.previousAbsentValue(((short) (2500))));
        Assert.assertEquals(4999, container.previousAbsentValue(((short) (5000))));
        Assert.assertEquals(4999, container.previousAbsentValue(((short) (5200))));
    }

    @Test
    public void testPreviousAbsentValueEmpty() {
        MappeableArrayContainer container = new MappeableArrayContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.previousAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testPreviousAbsentValueSparse() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals(9, container.previousAbsentValue(((short) (9))));
        Assert.assertEquals(9, container.previousAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.previousAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.previousAbsentValue(((short) (21))));
        Assert.assertEquals(29, container.previousAbsentValue(((short) (30))));
    }

    @Test
    public void testPreviousAbsentValueUnsigned() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }), 2);
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.previousAbsentValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextAbsentValue1() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129);
        Assert.assertEquals(0, container.nextAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.nextAbsentValue(((short) (63))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (64))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (65))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (129))));
    }

    @Test
    public void testNextAbsentValue2() {
        MappeableContainer container = new MappeableArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
        Assert.assertEquals(0, container.nextAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.nextAbsentValue(((short) (63))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (64))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (65))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (129))));
        Assert.assertEquals(199, container.nextAbsentValue(((short) (199))));
        Assert.assertEquals(501, container.nextAbsentValue(((short) (200))));
        Assert.assertEquals(501, container.nextAbsentValue(((short) (250))));
        Assert.assertEquals(2500, container.nextAbsentValue(((short) (2500))));
        Assert.assertEquals(5201, container.nextAbsentValue(((short) (5000))));
        Assert.assertEquals(5201, container.nextAbsentValue(((short) (5200))));
    }

    @Test
    public void testNextAbsentValueEmpty() {
        MappeableArrayContainer container = new MappeableArrayContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.nextAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testNextAbsentValueSparse() {
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 10, 20, 30 }), 3);
        Assert.assertEquals(9, container.nextAbsentValue(((short) (9))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.nextAbsentValue(((short) (21))));
        Assert.assertEquals(31, container.nextAbsentValue(((short) (30))));
    }

    @Test
    public void testNextAbsentValueUnsigned() {
        short[] array = new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) };
        MappeableArrayContainer container = new MappeableArrayContainer(ShortBuffer.wrap(array), 2);
        Assert.assertEquals(((1 << 15) | 4), container.nextAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 8))));
    }
}

