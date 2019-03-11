package org.roaringbitmap.buffer;


import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RunContainer;
import org.roaringbitmap.ShortIterator;


public class TestRunContainer {
    @Test
    public void testToString() {
        MappeableRunContainer rc = new MappeableRunContainer(32200, 35000);
        rc.add(((short) (-1)));
        Assert.assertEquals("[32200,34999][65535,65535]", rc.toString());
    }

    @Test
    public void testRunOpti() {
        MutableRoaringBitmap mrb = new MutableRoaringBitmap();
        for (int r = 0; r < 100000; r += 3) {
            mrb.add(r);
        }
        mrb.add(1000000);
        for (int r = 2000000; r < 3000000; ++r) {
            mrb.add(r);
        }
        MutableRoaringBitmap m2 = mrb.clone();
        m2.runOptimize();
        IntIterator x = m2.getReverseIntIterator();
        int count = 0;
        while (x.hasNext()) {
            x.next();
            count++;
        } 
        Assert.assertTrue(((m2.getCardinality()) == count));
        Assert.assertTrue(((mrb.getCardinality()) == count));
        Assert.assertTrue(((m2.serializedSizeInBytes()) < (mrb.serializedSizeInBytes())));
        Assert.assertEquals(m2, mrb);
        Assert.assertEquals(TestRunContainer.toMapped(m2), mrb);
        Assert.assertEquals(TestRunContainer.toMapped(m2), TestRunContainer.toMapped(mrb));
        Assert.assertEquals(m2, TestRunContainer.toMapped(mrb));
    }

    @Test
    public void addAndCompress() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (99)));
        container.add(((short) (98)));
        Assert.assertEquals(12, container.getSizeInBytes());
    }

    @Test
    public void addOutOfOrder() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (1)));
        Assert.assertEquals(4, container.getCardinality());
        Assert.assertTrue(container.contains(((short) (0))));
        Assert.assertTrue(container.contains(((short) (1))));
        Assert.assertTrue(container.contains(((short) (2))));
        Assert.assertTrue(container.contains(((short) (55))));
    }

    @Test
    public void addRange() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                for (int k = 0; k < 50; ++k) {
                    BitSet bs = new BitSet();
                    MappeableRunContainer container = new MappeableRunContainer();
                    for (int p = 0; p < i; ++p) {
                        container.add(((short) (p)));
                        bs.set(p);
                    }
                    for (int p = 0; p < j; ++p) {
                        container.add(((short) (99 - p)));
                        bs.set((99 - p));
                    }
                    MappeableContainer newContainer = container.add((49 - k), (50 + k));
                    bs.set((49 - k), (50 + k));
                    Assert.assertNotSame(container, newContainer);
                    Assert.assertEquals(bs.cardinality(), newContainer.getCardinality());
                    int nb_runs = 1;
                    int lastIndex = bs.nextSetBit(0);
                    for (int p = bs.nextSetBit(0); p >= 0; p = bs.nextSetBit((p + 1))) {
                        if ((p - lastIndex) > 1) {
                            nb_runs++;
                        }
                        lastIndex = p;
                        Assert.assertTrue(newContainer.contains(((short) (p))));
                    }
                    Assert.assertEquals(((nb_runs * 4) + 4), newContainer.getSizeInBytes());
                }
            }
        }
    }

    @Test
    public void addRangeAndFuseWithNextValueLength() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (short i = 10; i < 20; ++i) {
            container.add(i);
        }
        for (short i = 21; i < 30; ++i) {
            container.add(i);
        }
        MappeableContainer newContainer = container.add(15, 21);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(20, newContainer.getCardinality());
        for (short i = 10; i < 30; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
        Assert.assertEquals(8, newContainer.getSizeInBytes());
    }

    @Test
    public void addRangeAndFuseWithPreviousValueLength() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (short i = 10; i < 20; ++i) {
            container.add(i);
        }
        MappeableContainer newContainer = container.add(20, 30);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(20, newContainer.getCardinality());
        for (short i = 10; i < 30; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
        Assert.assertEquals(8, newContainer.getSizeInBytes());
    }

    @Test
    public void addRangeOnEmptyContainer() {
        MappeableRunContainer container = new MappeableRunContainer();
        MappeableContainer newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(90, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeOnNonEmptyContainer() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (1)));
        container.add(((short) (256)));
        MappeableContainer newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(92, newContainer.getCardinality());
        Assert.assertTrue(newContainer.contains(((short) (1))));
        Assert.assertTrue(newContainer.contains(((short) (256))));
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeOnNonEmptyContainerAndFuse() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (short i = 1; i < 20; ++i) {
            container.add(i);
        }
        for (short i = 90; i < 120; ++i) {
            container.add(i);
        }
        MappeableContainer newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(119, newContainer.getCardinality());
        for (short i = 1; i < 120; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeWithinSetBounds() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (10)));
        container.add(((short) (99)));
        MappeableContainer newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(90, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeWithinSetBoundsAndFuse() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (1)));
        container.add(((short) (10)));
        container.add(((short) (55)));
        container.add(((short) (99)));
        container.add(((short) (150)));
        MappeableContainer newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(92, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void andNot() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer result = rc.andNot(bc);
        Assert.assertEquals(rc, result);
    }

    @Test
    public void andNot1() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        MappeableContainer result = rc.andNot(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void andNot2() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        bc.add(((short) (1)));
        MappeableContainer result = rc.andNot(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void andNotTest1() {
        // this test uses a bitmap container that will be too sparse- okay?
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer intersectionNOT = rc.andNot(bc);
        Assert.assertEquals(100, intersectionNOT.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue((" missing k=" + k), intersectionNOT.contains(((short) ((k * 10) + 5))));
        }
        Assert.assertEquals(200, bc.getCardinality());
        Assert.assertEquals(200, rc.getCardinality());
    }

    @Test
    public void andNotTest2() {
        System.out.println("andNotTest2");
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            ac = ac.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer intersectionNOT = rc.andNot(ac);
        Assert.assertEquals(100, intersectionNOT.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue((" missing k=" + k), intersectionNOT.contains(((short) ((k * 10) + 5))));
        }
        Assert.assertEquals(200, ac.getCardinality());
        Assert.assertEquals(200, rc.getCardinality());
    }

    @Test
    public void basic2() {
        MappeableContainer x = new MappeableRunContainer();
        int a = 33;
        int b = 50000;
        for (int k = a; k < b; ++k) {
            x = x.add(((short) (k)));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            if (x.contains(((short) (k)))) {
                MappeableRunContainer copy = ((MappeableRunContainer) (x.clone()));
                copy = ((MappeableRunContainer) (copy.remove(((short) (k)))));
                copy = ((MappeableRunContainer) (copy.add(((short) (k)))));
                Assert.assertEquals(copy.getCardinality(), x.getCardinality());
                Assert.assertTrue(copy.equals(x));
                Assert.assertTrue(x.equals(copy));
                x.trim();
                Assert.assertTrue(copy.equals(x));
                Assert.assertTrue(x.equals(copy));
            } else {
                MappeableRunContainer copy = ((MappeableRunContainer) (x.clone()));
                copy = ((MappeableRunContainer) (copy.add(((short) (k)))));
                Assert.assertEquals(copy.getCardinality(), ((x.getCardinality()) + 1));
            }
        }
    }

    @Test
    public void clear() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        Assert.assertEquals(1, rc.getCardinality());
        rc.clear();
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void equalTest1() {
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer ar = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            ar = ar.add(((short) (k * 10)));
        }
        Assert.assertEquals(ac, ar);
    }

    @Test
    public void equalTest2() {
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer ar = new MappeableRunContainer();
        for (int k = 0; k < 10000; ++k) {
            ac = ac.add(((short) (k)));
            ar = ar.add(((short) (k)));
        }
        Assert.assertEquals(ac, ar);
    }

    @Test
    public void fillLeastSignificantBits() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        rc.add(((short) (3)));
        rc.add(((short) (12)));
        int[] array = new int[4];
        rc.fillLeastSignificant16bits(array, 1, 0);
        Assert.assertEquals(0, array[0]);
        Assert.assertEquals(1, array[1]);
        Assert.assertEquals(3, array[2]);
        Assert.assertEquals(12, array[3]);
    }

    @Test
    public void flip() {
        MappeableRunContainer rc = new MappeableRunContainer();
        rc.flip(((short) (1)));
        Assert.assertTrue(rc.contains(((short) (1))));
        rc.flip(((short) (1)));
        Assert.assertFalse(rc.contains(((short) (1))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange1() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(10, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange2() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, (1 << 20));
    }

    @Test
    public void iaddRange() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                for (int k = 0; k < 50; ++k) {
                    BitSet bs = new BitSet();
                    MappeableRunContainer container = new MappeableRunContainer();
                    for (int p = 0; p < i; ++p) {
                        container.add(((short) (p)));
                        bs.set(p);
                    }
                    for (int p = 0; p < j; ++p) {
                        container.add(((short) (99 - p)));
                        bs.set((99 - p));
                    }
                    container.iadd((49 - k), (50 + k));
                    bs.set((49 - k), (50 + k));
                    Assert.assertEquals(bs.cardinality(), container.getCardinality());
                    int nb_runs = 1;
                    int lastIndex = bs.nextSetBit(0);
                    for (int p = bs.nextSetBit(0); p >= 0; p = bs.nextSetBit((p + 1))) {
                        if ((p - lastIndex) > 1) {
                            nb_runs++;
                        }
                        lastIndex = p;
                        Assert.assertTrue(container.contains(((short) (p))));
                    }
                    Assert.assertEquals(((nb_runs * 4) + 4), container.getSizeInBytes());
                }
            }
        }
    }

    @Test
    public void iaddRange1() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(5, 21);
        Assert.assertEquals(40, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange10() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        rc.iadd(15, 35);
        Assert.assertEquals(30, rc.getCardinality());
        for (short k = 0; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 15; k < 35; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange11() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 5; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        rc.iadd(0, 20);
        Assert.assertEquals(30, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange12() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 5; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        rc.iadd(0, 35);
        Assert.assertEquals(35, rc.getCardinality());
        for (short k = 0; k < 35; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange2() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(0, 26);
        Assert.assertEquals(40, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange3() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(0, 20);
        Assert.assertEquals(40, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange4() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(10, 21);
        Assert.assertEquals(40, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange5() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(15, 21);
        Assert.assertEquals(35, rc.getCardinality());
        for (short k = 0; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 15; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(16, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange6() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 5; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(0, 21);
        Assert.assertEquals(40, rc.getCardinality());
        for (short k = 0; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange7() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(15, 25);
        Assert.assertEquals(35, rc.getCardinality());
        for (short k = 0; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 15; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(16, rc.getSizeInBytes());
    }

    @Test
    public void iaddRange8() {
        MappeableContainer rc = new MappeableRunContainer();
        for (short k = 0; k < 10; ++k) {
            rc.add(k);
        }
        for (short k = 20; k < 30; ++k) {
            rc.add(k);
        }
        for (short k = 40; k < 50; ++k) {
            rc.add(k);
        }
        rc.iadd(15, 40);
        Assert.assertEquals(45, rc.getCardinality());
        for (short k = 0; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 15; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iaddRangeAndFuseWithPreviousValueLength() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (short i = 10; i < 20; ++i) {
            container.add(i);
        }
        container.iadd(20, 30);
        Assert.assertEquals(20, container.getCardinality());
        for (short i = 10; i < 30; ++i) {
            Assert.assertTrue(container.contains(i));
        }
        Assert.assertEquals(8, container.getSizeInBytes());
    }

    @Test
    public void iaddRangeOnNonEmptyContainerAndFuse() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (short i = 1; i < 20; ++i) {
            container.add(i);
        }
        for (short i = 90; i < 120; ++i) {
            container.add(i);
        }
        container.iadd(10, 100);
        Assert.assertEquals(119, container.getCardinality());
        for (short i = 1; i < 120; ++i) {
            Assert.assertTrue(container.contains(i));
        }
    }

    @Test
    public void iaddRangeWithinSetBounds() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (10)));
        container.add(((short) (99)));
        container.iadd(10, 100);
        Assert.assertEquals(90, container.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(container.contains(i));
        }
    }

    @Test
    public void intersectionTest1() {
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            rc = rc.add(((short) (k * 10)));
        }
        Assert.assertEquals(ac, ac.and(rc));
        Assert.assertEquals(ac, rc.and(ac));
    }

    @Test
    public void intersectionTest2() {
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 10000; ++k) {
            ac = ac.add(((short) (k)));
            rc = rc.add(((short) (k)));
        }
        Assert.assertEquals(ac, ac.and(rc));
        Assert.assertEquals(ac, rc.and(ac));
    }

    @Test
    public void intersectionTest3() {
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k)));
            rc = rc.add(((short) (k + 100)));
        }
        Assert.assertEquals(0, rc.and(ac).getCardinality());
    }

    @Test
    public void intersectionTest4() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer intersection = rc.and(bc);
        Assert.assertEquals(100, intersection.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue(intersection.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals(200, bc.getCardinality());
        Assert.assertEquals(200, rc.getCardinality());
    }

    @Test
    public void ior() {
        MappeableContainer rc1 = new MappeableRunContainer();
        MappeableContainer rc2 = new MappeableRunContainer();
        rc1.iadd(0, 128);
        rc2.iadd(128, 256);
        rc1.ior(rc2);
        Assert.assertEquals(256, rc1.getCardinality());
    }

    @Test
    public void iremove1() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        rc.iremove(1, 2);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove10() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(5, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 25);
        Assert.assertEquals(5, rc.getCardinality());
        for (short k = 25; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove11() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(5, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 35);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove12() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (0)));
        rc.add(((short) (10)));
        rc.iremove(0, 11);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove13() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(5, 25);
        Assert.assertEquals(10, rc.getCardinality());
        for (short k = 0; k < 5; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 25; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iremove14() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(5, 31);
        Assert.assertEquals(5, rc.getCardinality());
        for (short k = 0; k < 5; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove15() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 5);
        rc.iadd(20, 30);
        rc.iremove(5, 25);
        Assert.assertEquals(10, rc.getCardinality());
        for (short k = 0; k < 5; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 25; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iremove16() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 5);
        rc.iadd(20, 30);
        rc.iremove(5, 31);
        Assert.assertEquals(5, rc.getCardinality());
        for (short k = 0; k < 5; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove17() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(37543, 65536);
        rc.iremove(9795, 65536);
        Assert.assertEquals(rc.getCardinality(), 0);
    }

    @Test
    public void iremove2() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 21);
        Assert.assertEquals(9, rc.getCardinality());
        for (short k = 21; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove3() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iadd(40, 50);
        rc.iremove(0, 21);
        Assert.assertEquals(19, rc.getCardinality());
        for (short k = 21; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 40; k < 50; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iremove4() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iremove(0, 5);
        Assert.assertEquals(5, rc.getCardinality());
        for (short k = 5; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove5() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 31);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove6() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 25);
        Assert.assertEquals(5, rc.getCardinality());
        for (short k = 25; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(8, rc.getSizeInBytes());
    }

    @Test
    public void iremove7() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iremove(0, 15);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove8() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(5, 21);
        Assert.assertEquals(14, rc.getCardinality());
        for (short k = 0; k < 5; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 21; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test
    public void iremove9() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(15, 21);
        Assert.assertEquals(19, rc.getCardinality());
        for (short k = 0; k < 10; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        for (short k = 21; k < 30; ++k) {
            Assert.assertTrue(rc.contains(k));
        }
        Assert.assertEquals(12, rc.getSizeInBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void iremoveInvalidRange1() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.iremove(10, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iremoveInvalidRange2() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.remove(0, (1 << 20));
    }

    @Test
    public void iremoveRange() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                for (int k = 0; k < 50; ++k) {
                    BitSet bs = new BitSet();
                    MappeableRunContainer container = new MappeableRunContainer();
                    for (int p = 0; p < i; ++p) {
                        container.add(((short) (p)));
                        bs.set(p);
                    }
                    for (int p = 0; p < j; ++p) {
                        container.add(((short) (99 - p)));
                        bs.set((99 - p));
                    }
                    container.iremove((49 - k), (50 + k));
                    bs.clear((49 - k), (50 + k));
                    Assert.assertEquals(bs.cardinality(), container.getCardinality());
                    int nb_runs = (bs.isEmpty()) ? 0 : 1;
                    int lastIndex = bs.nextSetBit(0);
                    for (int p = bs.nextSetBit(0); p >= 0; p = bs.nextSetBit((p + 1))) {
                        if ((p - lastIndex) > 1) {
                            nb_runs++;
                        }
                        lastIndex = p;
                        Assert.assertTrue(container.contains(((short) (p))));
                    }
                    Assert.assertEquals(((nb_runs * 4) + 4), container.getSizeInBytes());
                }
            }
        }
    }

    @Test
    public void iterator() {
        MappeableContainer x = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            for (int j = 0; j < k; ++j) {
                x = x.add(((short) ((k * 100) + j)));
            }
        }
        ShortIterator i = x.getShortIterator();
        for (int k = 0; k < 100; ++k) {
            for (int j = 0; j < k; ++j) {
                Assert.assertTrue(i.hasNext());
                Assert.assertEquals(i.next(), ((short) ((k * 100) + j)));
            }
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void limit() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        MappeableContainer limit = container.limit(1024);
        Assert.assertNotSame(container, limit);
        Assert.assertEquals(container, limit);
        limit = container.limit(3);
        Assert.assertNotSame(container, limit);
        Assert.assertEquals(3, limit.getCardinality());
        Assert.assertTrue(limit.contains(((short) (0))));
        Assert.assertTrue(limit.contains(((short) (2))));
        Assert.assertTrue(limit.contains(((short) (55))));
    }

    @Test
    public void longbacksimpleIterator() {
        MappeableContainer x = new MappeableRunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = x.add(((short) (k)));
        }
        ShortIterator i = x.getReverseShortIterator();
        for (int k = (1 << 16) - 1; k >= 0; --k) {
            Assert.assertTrue(i.hasNext());
            Assert.assertEquals(i.next(), ((short) (k)));
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void longcsimpleIterator() {
        MappeableContainer x = new MappeableRunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = x.add(((short) (k)));
        }
        Iterator<Short> i = x.iterator();
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(i.hasNext());
            Assert.assertEquals(i.next().shortValue(), ((short) (k)));
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void longsimpleIterator() {
        MappeableContainer x = new MappeableRunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = x.add(((short) (k)));
        }
        ShortIterator i = x.getShortIterator();
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(i.hasNext());
            Assert.assertEquals(i.next(), ((short) (k)));
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void MappeableRunContainerArg_ArrayAND() {
        boolean atLeastOneArray = false;
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer thisContainer = setb.get(k);
                if (thisContainer instanceof MappeableBitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                MappeableContainer c1 = thisContainer.and(set.get(l));
                MappeableContainer c2 = setb.get(k).and(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void MappeableRunContainerArg_ArrayANDNOT() {
        boolean atLeastOneArray = false;
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer thisContainer = setb.get(k);
                if (thisContainer instanceof MappeableBitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                MappeableContainer c1 = thisContainer.andNot(set.get(l));
                MappeableContainer c2 = setb.get(k).andNot(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void RunContainerArg_ArrayANDNOT2() {
        MappeableArrayContainer ac = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 0, 2, 4, 8, 10, 15, 16, 48, 50, 61, 80, -2 }), 12);
        MappeableContainer rc = new MappeableRunContainer(ShortBuffer.wrap(new short[]{ 7, 3, 17, 2, 20, 3, 30, 3, 36, 6, 60, 5, -3, 2 }), 7);
        Assert.assertEquals(new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 0, 2, 4, 15, 16, 48, 50, 80 }), 8), ac.andNot(rc));
    }

    @Test
    public void FullRunContainerArg_ArrayANDNOT2() {
        MappeableArrayContainer ac = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 3 }), 1);
        MappeableContainer rc = MappeableRunContainer.full();
        Assert.assertEquals(new MappeableArrayContainer(), ac.andNot(rc));
    }

    @Test
    public void RunContainerArg_ArrayANDNOT3() {
        MappeableArrayContainer ac = new MappeableArrayContainer(ShortBuffer.wrap(new short[]{ 5 }), 1);
        MappeableContainer rc = new MappeableRunContainer(ShortBuffer.wrap(new short[]{ 3, 10 }), 1);
        Assert.assertEquals(new MappeableArrayContainer(), ac.andNot(rc));
    }

    @Test
    public void MappeableRunContainerArg_ArrayOR() {
        boolean atLeastOneArray = false;
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer thisContainer = setb.get(k);
                // MappeableBitmapContainers are tested separately, but why not test some more?
                if (thisContainer instanceof MappeableBitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                MappeableContainer c1 = thisContainer.or(set.get(l));
                MappeableContainer c2 = setb.get(k).or(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void MappeableRunContainerArg_ArrayXOR() {
        boolean atLeastOneArray = false;
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer thisContainer = setb.get(k);
                if (thisContainer instanceof MappeableBitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                MappeableContainer c1 = thisContainer.xor(set.get(l));
                MappeableContainer c2 = setb.get(k).xor(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void MappeableRunContainerVSMappeableRunContainerAND() {
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer c1 = set.get(k).and(set.get(l));
                MappeableContainer c2 = setb.get(k).and(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void MappeableRunContainerVSMappeableRunContainerANDNOT() {
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer c1 = set.get(k).andNot(set.get(l));
                MappeableContainer c2 = setb.get(k).andNot(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void MappeableRunContainerVSMappeableRunContainerOR() {
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer c1 = set.get(k).or(set.get(l));
                MappeableContainer c2 = setb.get(k).or(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void MappeableRunContainerVSMappeableRunContainerXOR() {
        ArrayList<MappeableRunContainer> set = new ArrayList<MappeableRunContainer>();
        ArrayList<MappeableContainer> setb = new ArrayList<MappeableContainer>();
        TestRunContainer.getSetOfMappeableRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                MappeableContainer c1 = set.get(k).xor(set.get(l));
                MappeableContainer c2 = setb.get(k).xor(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void not1() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        MappeableContainer result = container.not(64, 64);// empty range

        Assert.assertNotSame(container, result);
        Assert.assertEquals(container, result);
    }

    @Test
    public void not10() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run begins inside the range but extends outside
        MappeableContainer result = container.not(498, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    /* @Test public void safeSerialization() throws Exception { MappeableRunContainer container = new
    MappeableRunContainer(); container.add((short) 0); container.add((short) 2);
    container.add((short) 55); container.add((short) 64); container.add((short) 256);

    ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new
    ObjectOutputStream(bos); out.writeObject(container);

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); ObjectInputStream in =
    new ObjectInputStream(bis); MappeableRunContainer newContainer = (MappeableRunContainer)
    in.readObject(); assertEquals(container, newContainer);
    assertEquals(container.serializedSizeInBytes(), newContainer.serializedSizeInBytes()); }
     */
    @Test
    public void not11() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        // second run entirely inside range, third run entirely inside range, 4th run entirely outside
        MappeableContainer result = container.not(498, 507);
        Assert.assertEquals(7, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 503, 505, 506, 510 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not12() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        container.add(((short) (511)));
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        MappeableContainer result = container.not(501, 511);
        Assert.assertEquals(9, result.getCardinality());
        for (short i : new short[]{ 300, 500, 503, 505, 506, 507, 508, 509, 511 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not12A() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (300)));
        container.add(((short) (301)));
        // first run crosses into range
        MappeableContainer result = container.not(301, 303);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 300, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not13() {
        MappeableRunContainer container = new MappeableRunContainer();
        // check for off-by-1 errors that might affect length 1 runs
        for (int i = 100; i < 120; i += 3) {
            container.add(((short) (i)));
        }
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        MappeableContainer result = container.not(110, 115);
        Assert.assertEquals(10, result.getCardinality());
        for (short i : new short[]{ 100, 103, 106, 109, 110, 111, 113, 114, 115, 118 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not15() {
        MappeableRunContainer container = new MappeableRunContainer();
        for (int i = 0; i < 20000; ++i) {
            container.add(((short) (i)));
        }
        for (int i = 40000; i < 60000; ++i) {
            container.add(((short) (i)));
        }
        MappeableContainer result = container.not(15000, 25000);
        // this result should stay as a run container.
        Assert.assertTrue((result instanceof MappeableRunContainer));
    }

    @Test
    public void not2() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        MappeableContainer result = container.not(64, 66);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 65, 256 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not3() {
        MappeableRunContainer container = new MappeableRunContainer();
        // applied to a run-less container
        MappeableContainer result = container.not(64, 68);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 64, 65, 66, 67 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not4() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        // all runs are before the range
        MappeableContainer result = container.not(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 64, 256, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not5() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (500)));
        container.add(((short) (502)));
        container.add(((short) (555)));
        container.add(((short) (564)));
        container.add(((short) (756)));
        // all runs are after the range
        MappeableContainer result = container.not(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 500, 502, 555, 564, 756, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not6() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        // one run is strictly within the range
        MappeableContainer result = container.not(499, 505);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 499, 504 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not7() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // one run, spans the range
        MappeableContainer result = container.not(502, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not8() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run, spans the range
        MappeableContainer result = container.not(502, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not9() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // first run, begins inside the range but extends outside
        MappeableContainer result = container.not(498, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void randomFun() {
        final int bitsetperword1 = 32;
        final int bitsetperword2 = 63;
        MappeableContainer rc1;
        MappeableContainer rc2;
        MappeableContainer ac1;
        MappeableContainer ac2;
        Random rand = new Random(0);
        final int max = 1 << 16;
        final int howmanywords = (1 << 16) / 64;
        int[] values1 = TestRunContainer.generateUniformHash(rand, (bitsetperword1 * howmanywords), max);
        int[] values2 = TestRunContainer.generateUniformHash(rand, (bitsetperword2 * howmanywords), max);
        rc1 = new MappeableRunContainer();
        rc1 = TestRunContainer.fillMeUp(rc1, values1);
        rc2 = new MappeableRunContainer();
        rc2 = TestRunContainer.fillMeUp(rc2, values2);
        ac1 = new MappeableArrayContainer();
        ac1 = TestRunContainer.fillMeUp(ac1, values1);
        ac2 = new MappeableArrayContainer();
        ac2 = TestRunContainer.fillMeUp(ac2, values2);
        if (!(rc1.equals(ac1))) {
            throw new RuntimeException("first containers do not match");
        }
        if (!(rc2.equals(ac2))) {
            throw new RuntimeException("second containers do not match");
        }
        if (!(rc1.or(rc2).equals(ac1.or(ac2)))) {
            throw new RuntimeException("ors do not match");
        }
        if (!(rc1.and(rc2).equals(ac1.and(ac2)))) {
            throw new RuntimeException("ands do not match");
        }
        if (!(rc1.andNot(rc2).equals(ac1.andNot(ac2)))) {
            throw new RuntimeException("andnots do not match");
        }
        if (!(rc2.andNot(rc1).equals(ac2.andNot(ac1)))) {
            throw new RuntimeException("second andnots do not match");
        }
        if (!(rc1.xor(rc2).equals(ac1.xor(ac2)))) {
            throw new RuntimeException("xors do not match");
        }
    }

    @Test
    public void rank() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Assert.assertEquals(1, container.rank(((short) (0))));
        Assert.assertEquals(2, container.rank(((short) (10))));
        Assert.assertEquals(4, container.rank(((short) (128))));
        Assert.assertEquals(5, container.rank(((short) (1024))));
    }

    @Test
    public void shortRangeRank() {
        MappeableContainer container = new MappeableRunContainer();
        container = container.add(16, 32);
        Assert.assertThat(container, CoreMatchers.instanceOf(MappeableRunContainer.class));
        // results in correct value: 16
        // assertEquals(16, container.toBitmapContainer().rank((short) 32));
        Assert.assertEquals(16, container.rank(((short) (32))));
    }

    @Test
    public void remove() {
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        MappeableContainer newContainer = rc.remove(1, 2);
        Assert.assertEquals(0, newContainer.getCardinality());
    }

    @Test
    public void safeor() {
        MappeableContainer rc1 = new MappeableRunContainer();
        MappeableContainer rc2 = new MappeableRunContainer();
        for (int i = 0; i < 100; ++i) {
            rc1 = rc1.iadd((i * 4), (((i + 1) * 4) - 1));
            rc2 = rc2.iadd(((i * 4) + 10000), ((((i + 1) * 4) - 1) + 10000));
        }
        MappeableContainer x = rc1.or(rc2);
        rc1.ior(rc2);
        if (!(rc1.equals(x))) {
            throw new RuntimeException("bug");
        }
    }

    @Test
    public void safeSerialization() throws Exception {
        System.out.println("write safeSerialization");/**
         * ***************************
         */

    }

    @Test
    public void select() {
        MappeableRunContainer container = new MappeableRunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Assert.assertEquals(0, container.select(0));
        Assert.assertEquals(2, container.select(1));
        Assert.assertEquals(55, container.select(2));
        Assert.assertEquals(64, container.select(3));
        Assert.assertEquals(256, container.select(4));
    }

    @Test
    public void simpleIterator() {
        MappeableContainer x = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            x = x.add(((short) (k)));
        }
        ShortIterator i = x.getShortIterator();
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue(i.hasNext());
            Assert.assertEquals(i.next(), ((short) (k)));
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void testAndNot() {
        int[] array1 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179, 39180, 39181, 39182, 39183, 39184, 39185, 39186, 39187, 39188 };
        int[] array2 = new int[]{ 14205 };
        MutableRoaringBitmap rb1 = MutableRoaringBitmap.bitmapOf(array1);
        rb1.runOptimize();
        MutableRoaringBitmap rb2 = MutableRoaringBitmap.bitmapOf(array2);
        MutableRoaringBitmap answer = MutableRoaringBitmap.andNot(rb1, rb2);
        Assert.assertEquals(answer.getCardinality(), array1.length);
    }

    @Test
    public void testRoaringWithOptimize() {
        // create the same bitmap over and over again, with optimizing it
        final Set<MutableRoaringBitmap> setWithOptimize = new HashSet<MutableRoaringBitmap>();
        final int max = 1000;
        for (int i = 0; i < max; i++) {
            final MutableRoaringBitmap bitmapWithOptimize = new MutableRoaringBitmap();
            bitmapWithOptimize.add(1);
            bitmapWithOptimize.add(2);
            bitmapWithOptimize.add(3);
            bitmapWithOptimize.add(4);
            bitmapWithOptimize.runOptimize();
            setWithOptimize.add(bitmapWithOptimize);
        }
        Assert.assertEquals(1, setWithOptimize.size());
    }

    @Test
    public void testRoaringWithoutOptimize() {
        // create the same bitmap over and over again, without optimizing it
        final Set<MutableRoaringBitmap> setWithoutOptimize = new HashSet<MutableRoaringBitmap>();
        final int max = 1000;
        for (int i = 0; i < max; i++) {
            final MutableRoaringBitmap bitmapWithoutOptimize = new MutableRoaringBitmap();
            bitmapWithoutOptimize.add(1);
            bitmapWithoutOptimize.add(2);
            bitmapWithoutOptimize.add(3);
            bitmapWithoutOptimize.add(4);
            setWithoutOptimize.add(bitmapWithoutOptimize);
        }
        Assert.assertEquals(1, setWithoutOptimize.size());
    }

    @Test
    public void toBitmapOrArrayContainer() {
        MappeableRunContainer rc = new MappeableRunContainer();
        rc.iadd(0, ((MappeableArrayContainer.DEFAULT_MAX_SIZE) / 2));
        MappeableContainer ac = rc.toBitmapOrArrayContainer(rc.getCardinality());
        Assert.assertTrue((ac instanceof MappeableArrayContainer));
        Assert.assertEquals(((MappeableArrayContainer.DEFAULT_MAX_SIZE) / 2), ac.getCardinality());
        for (short k = 0; k < ((MappeableArrayContainer.DEFAULT_MAX_SIZE) / 2); ++k) {
            Assert.assertTrue(ac.contains(k));
        }
        rc.iadd(((MappeableArrayContainer.DEFAULT_MAX_SIZE) / 2), (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)));
        MappeableContainer bc = rc.toBitmapOrArrayContainer(rc.getCardinality());
        Assert.assertTrue((bc instanceof MappeableBitmapContainer));
        Assert.assertEquals((2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        for (short k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(bc.contains(k));
        }
    }

    @Test
    public void union() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer union = rc.or(bc);
        Assert.assertEquals(200, union.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue(union.contains(((short) (k * 10))));
            Assert.assertTrue(union.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals(100, bc.getCardinality());
        Assert.assertEquals(100, rc.getCardinality());
    }

    @Test
    public void union2() {
        System.out.println("union2");
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        int N = 1;
        for (int k = 0; k < N; ++k) {
            ac = ac.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        System.out.println(("run=" + rc));
        System.out.println(("array=" + ac));
        MappeableContainer union = rc.or(ac);
        System.out.println(union);
        Assert.assertEquals((2 * N), union.getCardinality());
        for (int k = 0; k < N; ++k) {
            Assert.assertTrue(union.contains(((short) (k * 10))));
            Assert.assertTrue(union.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals(N, ac.getCardinality());
        Assert.assertEquals(N, rc.getCardinality());
    }

    @Test
    public void xor() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), result.getCardinality());
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), rc.getCardinality());
    }

    @Test
    public void xor_array() {
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), result.getCardinality());
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        Assert.assertEquals((4 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)), rc.getCardinality());
    }

    @Test
    public void xor1() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor1a() {
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor2() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        bc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor2a() {
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        bc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor3() {
        MappeableContainer bc = new MappeableBitmapContainer();
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        bc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void xor3a() {
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        rc.add(((short) (1)));
        bc.add(((short) (1)));
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void xor4() {
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer answer = new MappeableArrayContainer();
        answer = answer.add(28203, 28214);
        rc = rc.add(28203, 28214);
        int[] data = new int[]{ 17739, 17740, 17945, 19077, 19278, 19407 };
        for (int x : data) {
            answer = answer.add(((short) (x)));
            bc = bc.add(((short) (x)));
        }
        MappeableContainer result = rc.xor(bc);
        Assert.assertEquals(answer, result);
    }

    @Test
    public void iorArray() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer ac = new MappeableArrayContainer();
        ac = ac.add(0, 1);
        rc = rc.ior(ac);
        Assert.assertEquals(1, rc.getCardinality());
        rc = new MappeableRunContainer();
        rc = rc.add(0, 13);
        rc = rc.ior(ac);
        Assert.assertEquals(13, rc.getCardinality());
        rc = new MappeableRunContainer();
        rc = rc.add(0, (1 << 16));
        rc = rc.ior(ac);
        Assert.assertEquals((1 << 16), rc.getCardinality());
    }

    @Test
    public void iorBitmap() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer bc = new MappeableBitmapContainer();
        bc = bc.add(0, 1);
        rc = rc.ior(bc);
        Assert.assertEquals(1, rc.getCardinality());
        rc = new MappeableRunContainer();
        rc = rc.add(0, (1 << 16));
        rc = rc.ior(bc);
        Assert.assertEquals((1 << 16), rc.getCardinality());
    }

    @Test
    public void iorRun() {
        MappeableContainer rc1 = new MappeableRunContainer();
        MappeableContainer rc2 = new MappeableRunContainer();
        rc2 = rc2.add(0, 1);
        rc1 = rc1.ior(rc2);
        Assert.assertEquals(1, rc1.getCardinality());
        rc1 = new MappeableRunContainer();
        rc1 = rc1.add(0, 13);
        rc1 = rc1.ior(rc2);
        Assert.assertEquals(13, rc1.getCardinality());
        rc1 = new MappeableRunContainer();
        rc1 = rc1.add(0, (1 << 16));
        rc1 = rc1.ior(rc2);
        Assert.assertEquals((1 << 16), rc1.getCardinality());
    }

    @Test
    public void intersectsRun() {
        MappeableContainer rc1 = new MappeableRunContainer();
        MappeableContainer rc2 = new MappeableRunContainer();
        rc1 = rc1.add(1, 13);
        rc2 = rc2.add(19, 54);
        Assert.assertFalse(rc1.intersects(rc2));
        rc1 = rc1.add(15, 17);
        Assert.assertFalse(rc1.intersects(rc2));
        Assert.assertFalse(rc2.intersects(rc1));
        rc1 = rc1.add(25, 27);
        Assert.assertTrue(rc1.intersects(rc2));
    }

    @Test
    public void intersectsArray() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer ac = new MappeableArrayContainer();
        rc = rc.add(1, 13);
        ac = ac.add(19, 54);
        Assert.assertFalse(rc.intersects(ac));
        rc = rc.add(15, 17);
        Assert.assertFalse(rc.intersects(ac));
        rc = rc.add(25, 27);
        Assert.assertTrue(rc.intersects(ac));
    }

    @Test
    public void testFirstLast() {
        MappeableContainer rc = new MappeableRunContainer();
        final int firstInclusive = 1;
        int lastExclusive = firstInclusive;
        for (int i = 0; i < (1 << (16 - 10)); ++i) {
            int newLastExclusive = lastExclusive + 10;
            rc = rc.add(lastExclusive, newLastExclusive);
            Assert.assertEquals(firstInclusive, rc.first());
            Assert.assertEquals((newLastExclusive - 1), rc.last());
            lastExclusive = newLastExclusive;
        }
    }

    @Test
    public void testFirstUnsigned() {
        MutableRoaringBitmap roaringWithRun = new MutableRoaringBitmap();
        roaringWithRun.add(32768L, 65536);// (1 << 15) to (1 << 16).

        Assert.assertEquals(roaringWithRun.first(), 32768);
    }

    @Test
    public void testContainsMappeableBitmapContainer_EmptyContainsEmpty() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer subset = new MappeableBitmapContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_IncludeProperSubset() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableBitmapContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_IncludeProperSubsetDifferentStart() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableBitmapContainer().add(1, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_ExcludeShiftedSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableBitmapContainer().add(2, 12);
        Assert.assertFalse(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_IncludeSelf() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableBitmapContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_ExcludeSuperSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer superset = new MappeableBitmapContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsMappeableBitmapContainer_ExcludeDisJointSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer disjoint = new MappeableBitmapContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testContainsMappeableRunContainer_EmptyContainsEmpty() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer subset = new MappeableRunContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableRunContainer_IncludeProperSubset() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableRunContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableRunContainer_IncludeSelf() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableRunContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableRunContainer_ExcludeSuperSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer superset = new MappeableRunContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsMappeableRunContainer_IncludeProperSubsetDifferentStart() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableRunContainer().add(1, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableRunContainer_ExcludeShiftedSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableRunContainer().add(2, 12);
        Assert.assertFalse(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableRunContainer_ExcludeDisJointSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer disjoint = new MappeableRunContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testContainsMappeableArrayContainer_EmptyContainsEmpty() {
        MappeableContainer rc = new MappeableRunContainer();
        MappeableContainer subset = new MappeableArrayContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableArrayContainer_IncludeProperSubset() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableArrayContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableArrayContainer_IncludeProperSubsetDifferentStart() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableArrayContainer().add(2, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableArrayContainer_ExcludeShiftedSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer shifted = new MappeableArrayContainer().add(2, 12);
        Assert.assertFalse(rc.contains(shifted));
    }

    @Test
    public void testContainsMappeableArrayContainer_IncludeSelf() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer subset = new MappeableArrayContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsMappeableArrayContainer_ExcludeSuperSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer superset = new MappeableArrayContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsMappeableArrayContainer_ExcludeDisJointSet() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer disjoint = new MappeableArrayContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testEqualsMappeableArrayContainer_Equal() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 10);
        Assert.assertTrue(rc.equals(ac));
        Assert.assertTrue(ac.equals(rc));
    }

    @Test
    public void testEqualsMappeableArrayContainer_NotEqual_ArrayLarger() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsMappeableArrayContainer_NotEqual_ArraySmaller() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 9);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsMappeableArrayContainer_NotEqual_ArrayShifted() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(1, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsMappeableArrayContainer_NotEqual_ArrayDiscontiguous() {
        MappeableContainer rc = new MappeableRunContainer().add(0, 10);
        MappeableContainer ac = new MappeableArrayContainer().add(0, 11);
        ac.flip(((short) (9)));
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEquals_FullRunContainerWithArrayContainer() {
        MappeableContainer full = new MappeableRunContainer().add(0, (1 << 16));
        Assert.assertNotEquals(full, new MappeableArrayContainer().add(0, 10));
    }

    @Test
    public void testFullConstructor() {
        Assert.assertTrue(MappeableRunContainer.full().isFull());
    }

    @Test
    public void testRangeConstructor() {
        MappeableRunContainer full = new MappeableRunContainer(0, (1 << 16));
        Assert.assertTrue(full.isFull());
        Assert.assertEquals(65536, full.getCardinality());
    }

    @Test
    public void testRangeConstructor2() {
        MappeableRunContainer c = new MappeableRunContainer(17, 1000);
        Assert.assertEquals(983, c.getCardinality());
    }

    @Test
    public void testRangeConstructor3() {
        MappeableRunContainer a = new MappeableRunContainer(17, 45679);
        MappeableRunContainer b = new MappeableRunContainer();
        b.iadd(17, 45679);
        Assert.assertEquals(a, b);
    }

    @Test
    public void testRangeConstructor4() {
        MappeableRunContainer c = new MappeableRunContainer(0, 45679);
        Assert.assertEquals(45679, c.getCardinality());
    }

    @Test
    public void testSimpleCardinality() {
        MappeableRunContainer c = new MappeableRunContainer();
        c.add(((short) (1)));
        c.add(((short) (17)));
        Assert.assertEquals(2, c.getCardinality());
    }

    @Test
    public void testIntersectsWithRange() {
        MappeableContainer container = new MappeableRunContainer().add(0, 10);
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, (1 << 16)));
        Assert.assertFalse(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testIntersectsWithRangeUnsigned() {
        MappeableContainer container = new MappeableRunContainer().add(TestRunContainer.lower16Bits((-50)), TestRunContainer.lower16Bits((-10)));
        Assert.assertFalse(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, TestRunContainer.lower16Bits((-40))));
        Assert.assertFalse(container.intersects(TestRunContainer.lower16Bits((-100)), TestRunContainer.lower16Bits((-55))));
        Assert.assertFalse(container.intersects((-9), (1 << 16)));
        Assert.assertTrue(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testIntersectsWithRangeManyRuns() {
        MappeableContainer container = new MappeableRunContainer().add(0, 10).add(TestRunContainer.lower16Bits((-50)), TestRunContainer.lower16Bits((-10)));
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, TestRunContainer.lower16Bits((-1))));
        Assert.assertTrue(container.intersects(11, TestRunContainer.lower16Bits((-1))));
        Assert.assertTrue(container.intersects(0, TestRunContainer.lower16Bits((-40))));
        Assert.assertFalse(container.intersects(TestRunContainer.lower16Bits((-100)), TestRunContainer.lower16Bits((-55))));
        Assert.assertFalse(container.intersects(TestRunContainer.lower16Bits((-9)), TestRunContainer.lower16Bits((-1))));
        Assert.assertTrue(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testContainsFull() {
        Assert.assertTrue(MappeableRunContainer.full().contains(0, (1 << 16)));
        Assert.assertFalse(MappeableRunContainer.full().flip(((short) (1 << 15))).contains(0, (1 << 16)));
    }

    @Test
    public void testContainsRange() {
        MappeableContainer rc = new MappeableRunContainer().add(1, 100).add(5000, 10000);
        Assert.assertFalse(rc.contains(0, 100));
        Assert.assertFalse(rc.contains(0, 100000));
        Assert.assertTrue(rc.contains(1, 100));
        Assert.assertTrue(rc.contains(1, 99));
        Assert.assertTrue(rc.contains(2, 100));
        Assert.assertTrue(rc.contains(5000, 10000));
        Assert.assertTrue(rc.contains(5000, 9999));
        Assert.assertTrue(rc.contains(5001, 9999));
        Assert.assertTrue(rc.contains(5001, 10000));
        Assert.assertFalse(rc.contains(100, 5000));
        Assert.assertFalse(rc.contains(50, 5000));
        Assert.assertFalse(rc.contains(4000, 6000));
        Assert.assertFalse(rc.contains(10001, 20000));
    }

    @Test
    public void testContainsRange2() {
        MappeableContainer rc = new MappeableRunContainer().add(1, 100).add(300, 400).add(5000, 10000);
        Assert.assertFalse(rc.contains(0, 100));
        Assert.assertFalse(rc.contains(0, 100000));
        Assert.assertTrue(rc.contains(1, 100));
        Assert.assertTrue(rc.contains(1, 99));
        Assert.assertTrue(rc.contains(300, 400));
        Assert.assertTrue(rc.contains(2, 100));
        Assert.assertTrue(rc.contains(5000, 10000));
        Assert.assertTrue(rc.contains(5000, 9999));
        Assert.assertTrue(rc.contains(5001, 9999));
        Assert.assertTrue(rc.contains(5001, 10000));
        Assert.assertFalse(rc.contains(100, 5000));
        Assert.assertFalse(rc.contains(50, 5000));
        Assert.assertFalse(rc.contains(4000, 6000));
        Assert.assertFalse(rc.contains(10001, 20000));
    }

    @Test
    public void testContainsRange3() {
        MappeableContainer rc = new MappeableRunContainer().add(1, 100).add(300, 300).add(400, 500).add(502, 600).add(700, 10000);
        Assert.assertFalse(rc.contains(0, 100));
        Assert.assertFalse(rc.contains(500, 600));
        Assert.assertFalse(rc.contains(501, 600));
        Assert.assertTrue(rc.contains(502, 600));
        Assert.assertFalse(rc.contains(600, 700));
        Assert.assertTrue(rc.contains(9999, 10000));
        Assert.assertFalse(rc.contains(9999, 10001));
    }

    @Test
    public void testNextValue() {
        MappeableContainer container = new RunContainer(new short[]{ 64, 64 }, 1).toMappeableContainer();
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals((-1), container.nextValue(((short) (129))));
    }

    @Test
    public void testNextValueBetweenRuns() {
        MappeableContainer container = new RunContainer(new short[]{ 64, 64, 256, 64 }, 2).toMappeableContainer();
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals(256, container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (512))));
    }

    @Test
    public void testNextValue2() {
        MappeableContainer container = new RunContainer(new short[]{ 64, 64, 200, 300, 5000, 200 }, 3).toMappeableContainer();
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
        MappeableContainer container = new RunContainer(new short[]{ 64, 64 }, 1).toMappeableContainer();
        Assert.assertEquals((-1), container.previousValue(((short) (0))));
        Assert.assertEquals((-1), container.previousValue(((short) (63))));
        Assert.assertEquals(64, container.previousValue(((short) (64))));
        Assert.assertEquals(65, container.previousValue(((short) (65))));
        Assert.assertEquals(128, container.previousValue(((short) (128))));
        Assert.assertEquals(128, container.previousValue(((short) (129))));
    }

    @Test
    public void testPreviousValue2() {
        MappeableContainer container = new RunContainer(new short[]{ 64, 64, 200, 300, 5000, 200 }, 3).toMappeableContainer();
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
    public void testPreviousValueUnsigned() {
        MappeableContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) (0)), ((short) ((1 << 15) | 7)), ((short) (0)) }, 2).toMappeableContainer();
        Assert.assertEquals((-1), container.previousValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextValueUnsigned() {
        MappeableContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) (0)), ((short) ((1 << 15) | 7)), ((short) (0)) }, 2).toMappeableContainer();
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals((-1), container.nextValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testPreviousAbsentValue1() {
        MappeableContainer container = new MappeableRunContainer().iadd(64, 129);
        Assert.assertEquals(0, container.previousAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (63))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (64))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (65))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.previousAbsentValue(((short) (129))));
    }

    @Test
    public void testPreviousAbsentValue2() {
        MappeableContainer container = new MappeableRunContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
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
        MappeableRunContainer container = new MappeableRunContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.previousAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testPreviousAbsentValueSparse() {
        MappeableRunContainer container = new MappeableRunContainer(ShortBuffer.wrap(new short[]{ 10, 0, 20, 0, 30, 0 }), 3);
        Assert.assertEquals(9, container.previousAbsentValue(((short) (9))));
        Assert.assertEquals(9, container.previousAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.previousAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.previousAbsentValue(((short) (21))));
        Assert.assertEquals(29, container.previousAbsentValue(((short) (30))));
    }

    @Test
    public void testPreviousAbsentEvenBits() {
        short[] evenBits = new short[1 << 15];
        for (int i = 0; i < (1 << 15); i += 2) {
            evenBits[i] = ((short) (i));
            evenBits[(i + 1)] = 0;
        }
        MappeableRunContainer container = new MappeableRunContainer(ShortBuffer.wrap(evenBits), (1 << 14));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i - 1), container.previousAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.previousAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testPreviousAbsentValueUnsigned() {
        short[] array = new short[]{ ((short) ((1 << 15) | 5)), 0, ((short) ((1 << 15) | 7)), 0 };
        MappeableRunContainer container = new MappeableRunContainer(ShortBuffer.wrap(array), 2);
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.previousAbsentValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextAbsentValue1() {
        MappeableContainer container = new MappeableRunContainer().iadd(64, 129);
        Assert.assertEquals(0, container.nextAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.nextAbsentValue(((short) (63))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (64))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (65))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (129))));
    }

    @Test
    public void testNextAbsentValue2() {
        MappeableContainer container = new MappeableRunContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
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
        MappeableRunContainer container = new MappeableRunContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.nextAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testNextAbsentValueSparse() {
        MappeableContainer container = new MappeableRunContainer(ShortBuffer.wrap(new short[]{ 10, 0, 20, 0, 30, 0 }), 3);
        Assert.assertEquals(9, container.nextAbsentValue(((short) (9))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.nextAbsentValue(((short) (21))));
        Assert.assertEquals(31, container.nextAbsentValue(((short) (30))));
    }

    @Test
    public void testNextAbsentEvenBits() {
        short[] evenBits = new short[1 << 15];
        for (int i = 0; i < (1 << 15); i += 2) {
            evenBits[i] = ((short) (i));
            evenBits[(i + 1)] = 0;
        }
        MappeableRunContainer container = new MappeableRunContainer(ShortBuffer.wrap(evenBits), (1 << 14));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testNextAbsentValueUnsigned() {
        short[] array = new short[]{ ((short) ((1 << 15) | 5)), 0, ((short) ((1 << 15) | 7)), 0 };
        MappeableRunContainer container = new MappeableRunContainer(ShortBuffer.wrap(array), 2);
        Assert.assertEquals(((1 << 15) | 4), container.nextAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 8))));
    }
}

