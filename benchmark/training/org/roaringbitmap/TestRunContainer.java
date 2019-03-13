package org.roaringbitmap;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static ArrayContainer.DEFAULT_MAX_SIZE;


public class TestRunContainer {
    @Test
    public void testRunOpti() {
        RoaringBitmap mrb = new RoaringBitmap();
        for (int r = 0; r < 100000; r += 3) {
            mrb.add(r);
        }
        mrb.add(1000000);
        for (int r = 2000000; r < 3000000; ++r) {
            mrb.add(r);
        }
        RoaringBitmap m2 = mrb.clone();
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
    }

    @Test
    public void addAndCompress() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (99)));
        container.add(((short) (98)));
        Assert.assertEquals(12, container.getSizeInBytes());
    }

    @Test
    public void addOutOfOrder() {
        RunContainer container = new RunContainer();
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
                    RunContainer container = new RunContainer();
                    for (int p = 0; p < i; ++p) {
                        container.add(((short) (p)));
                        bs.set(p);
                    }
                    for (int p = 0; p < j; ++p) {
                        container.add(((short) (99 - p)));
                        bs.set((99 - p));
                    }
                    Container newContainer = container.add((49 - k), (50 + k));
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
        RunContainer container = new RunContainer();
        for (short i = 10; i < 20; ++i) {
            container.add(i);
        }
        for (short i = 21; i < 30; ++i) {
            container.add(i);
        }
        Container newContainer = container.add(15, 21);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(20, newContainer.getCardinality());
        for (short i = 10; i < 30; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
        Assert.assertEquals(8, newContainer.getSizeInBytes());
    }

    @Test
    public void addRangeAndFuseWithPreviousValueLength() {
        RunContainer container = new RunContainer();
        for (short i = 10; i < 20; ++i) {
            container.add(i);
        }
        Container newContainer = container.add(20, 30);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(20, newContainer.getCardinality());
        for (short i = 10; i < 30; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
        Assert.assertEquals(8, newContainer.getSizeInBytes());
    }

    @Test
    public void addRangeOnEmptyContainer() {
        RunContainer container = new RunContainer();
        Container newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(90, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeOnNonEmptyContainer() {
        RunContainer container = new RunContainer();
        container.add(((short) (1)));
        container.add(((short) (256)));
        Container newContainer = container.add(10, 100);
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
        RunContainer container = new RunContainer();
        for (short i = 1; i < 20; ++i) {
            container.add(i);
        }
        for (short i = 90; i < 120; ++i) {
            container.add(i);
        }
        Container newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(119, newContainer.getCardinality());
        for (short i = 1; i < 120; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeWithinSetBounds() {
        RunContainer container = new RunContainer();
        container.add(((short) (10)));
        container.add(((short) (99)));
        Container newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(90, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void addRangeWithinSetBoundsAndFuse() {
        RunContainer container = new RunContainer();
        container.add(((short) (1)));
        container.add(((short) (10)));
        container.add(((short) (55)));
        container.add(((short) (99)));
        container.add(((short) (150)));
        Container newContainer = container.add(10, 100);
        Assert.assertNotSame(container, newContainer);
        Assert.assertEquals(92, newContainer.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(newContainer.contains(i));
        }
    }

    @Test
    public void andNot() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container result = rc.andNot(bc);
        Assert.assertEquals(rc, result);
    }

    @Test
    public void andNot1() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        Container result = rc.andNot(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void andNot2() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        bc.add(((short) (1)));
        Container result = rc.andNot(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void andNotTest1() {
        // this test uses a bitmap container that will be too sparse- okay?
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container intersectionNOT = rc.andNot(bc);
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
        Container ac = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            ac = ac.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container intersectionNOT = rc.andNot(ac);
        Assert.assertEquals(100, intersectionNOT.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue((" missing k=" + k), intersectionNOT.contains(((short) ((k * 10) + 5))));
        }
        Assert.assertEquals(200, ac.getCardinality());
        Assert.assertEquals(200, rc.getCardinality());
    }

    @Test
    public void basic() {
        RunContainer x = new RunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(x.contains(((short) (k))));
            x = ((RunContainer) (x.remove(((short) (k)))));
            Assert.assertFalse(x.contains(((short) (k))));
            Assert.assertEquals((k + 1), ((1 << 16) - (x.getCardinality())));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = (1 << 16) - 1; k >= 0; --k) {
            Assert.assertTrue(x.contains(((short) (k))));
            x = ((RunContainer) (x.remove(((short) (k)))));
            Assert.assertFalse(x.contains(((short) (k))));
            Assert.assertEquals(k, x.getCardinality());
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            RunContainer copy = ((RunContainer) (x.clone()));
            copy = ((RunContainer) (copy.remove(((short) (k)))));
            Assert.assertEquals(((copy.getCardinality()) + 1), x.getCardinality());
            copy = ((RunContainer) (copy.add(((short) (k)))));
            Assert.assertEquals(copy.getCardinality(), x.getCardinality());
            Assert.assertTrue(copy.equals(x));
            Assert.assertTrue(x.equals(copy));
            copy.trim();
            Assert.assertTrue(copy.equals(x));
            Assert.assertTrue(x.equals(copy));
        }
    }

    @Test
    public void basic2() {
        RunContainer x = new RunContainer();
        int a = 33;
        int b = 50000;
        for (int k = a; k < b; ++k) {
            x = ((RunContainer) (x.add(((short) (k)))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            if (x.contains(((short) (k)))) {
                RunContainer copy = ((RunContainer) (x.clone()));
                copy = ((RunContainer) (copy.remove(((short) (k)))));
                copy = ((RunContainer) (copy.add(((short) (k)))));
                Assert.assertEquals(copy.getCardinality(), x.getCardinality());
                Assert.assertTrue(copy.equals(x));
                Assert.assertTrue(x.equals(copy));
                x.trim();
                Assert.assertTrue(copy.equals(x));
                Assert.assertTrue(x.equals(copy));
            } else {
                RunContainer copy = ((RunContainer) (x.clone()));
                copy = ((RunContainer) (copy.add(((short) (k)))));
                Assert.assertEquals(copy.getCardinality(), ((x.getCardinality()) + 1));
            }
        }
    }

    @Test
    public void basictri() {
        RunContainer x = new RunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            x.trim();
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(x.contains(((short) (k))));
            x = ((RunContainer) (x.remove(((short) (k)))));
            x.trim();
            Assert.assertFalse(x.contains(((short) (k))));
            Assert.assertEquals((k + 1), ((1 << 16) - (x.getCardinality())));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            x.trim();
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = (1 << 16) - 1; k >= 0; --k) {
            Assert.assertTrue(x.contains(((short) (k))));
            x = ((RunContainer) (x.remove(((short) (k)))));
            x.trim();
            Assert.assertFalse(x.contains(((short) (k))));
            Assert.assertEquals(k, x.getCardinality());
        }
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertFalse(x.contains(((short) (k))));
            x = ((RunContainer) (x.add(((short) (k)))));
            x.trim();
            Assert.assertEquals((k + 1), x.getCardinality());
            Assert.assertTrue(x.contains(((short) (k))));
        }
        for (int k = 0; k < (1 << 16); ++k) {
            RunContainer copy = ((RunContainer) (x.clone()));
            copy.trim();
            copy = ((RunContainer) (copy.remove(((short) (k)))));
            copy = ((RunContainer) (copy.add(((short) (k)))));
            Assert.assertEquals(copy.getCardinality(), x.getCardinality());
            Assert.assertTrue(copy.equals(x));
            Assert.assertTrue(x.equals(copy));
            copy.trim();
            Assert.assertTrue(copy.equals(x));
            Assert.assertTrue(x.equals(copy));
        }
    }

    @Test
    public void clear() {
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        Assert.assertEquals(1, rc.getCardinality());
        rc.clear();
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void equalTest1() {
        Container ac = new ArrayContainer();
        Container ar = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            ar = ar.add(((short) (k * 10)));
        }
        Assert.assertEquals(ac, ar);
    }

    @Test
    public void equalTest2() {
        Container ac = new ArrayContainer();
        Container ar = new RunContainer();
        for (int k = 0; k < 10000; ++k) {
            ac = ac.add(((short) (k)));
            ar = ar.add(((short) (k)));
        }
        Assert.assertEquals(ac, ar);
    }

    @Test
    public void fillLeastSignificantBits() {
        Container rc = new RunContainer();
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
        RunContainer rc = new RunContainer();
        rc.flip(((short) (1)));
        Assert.assertTrue(rc.contains(((short) (1))));
        rc.flip(((short) (1)));
        Assert.assertFalse(rc.contains(((short) (1))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange1() {
        Container rc = new RunContainer();
        rc.iadd(10, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange2() {
        Container rc = new RunContainer();
        rc.iadd(0, (1 << 20));
    }

    @Test
    public void iaddRange() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                for (int k = 0; k < 50; ++k) {
                    BitSet bs = new BitSet();
                    RunContainer container = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        RunContainer container = new RunContainer();
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
        RunContainer container = new RunContainer();
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
        RunContainer container = new RunContainer();
        container.add(((short) (10)));
        container.add(((short) (99)));
        container.iadd(10, 100);
        Assert.assertEquals(90, container.getCardinality());
        for (short i = 10; i < 100; ++i) {
            Assert.assertTrue(container.contains(i));
        }
    }

    @Test
    public void inot1() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Container result = container.inot(64, 64);// empty range

        Assert.assertSame(container, result);
        Assert.assertEquals(5, container.getCardinality());
    }

    @Test
    public void inot10() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run begins inside the range but extends outside
        Container result = container.inot(498, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot11() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        // second run entirely inside range, third run entirely inside range, 4th run entirely outside
        Container result = container.inot(498, 507);
        Assert.assertEquals(7, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 503, 505, 506, 510 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot12() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        container.add(((short) (511)));
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        Container result = container.inot(501, 511);
        Assert.assertEquals(9, result.getCardinality());
        for (short i : new short[]{ 300, 500, 503, 505, 506, 507, 508, 509, 511 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot12A() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (301)));
        // first run crosses into range
        Container result = container.inot(301, 303);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 300, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot13() {
        RunContainer container = new RunContainer();
        // check for off-by-1 errors that might affect length 1 runs
        for (int i = 100; i < 120; i += 3) {
            container.add(((short) (i)));
        }
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        Container result = container.inot(110, 115);
        Assert.assertEquals(10, result.getCardinality());
        for (short i : new short[]{ 100, 103, 106, 109, 110, 111, 113, 114, 115, 118 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot14() {
        inot14once(10, 1);
        inot14once(10, 10);
        inot14once(1000, 100);
        for (int i = 1; i <= 100; ++i) {
            if ((i % 10) == 0) {
                System.out.println(("inot 14 attempt " + i));
            }
            inot14once(50000, 100);
        }
    }

    @Test
    public void inot15() {
        RunContainer container = new RunContainer();
        for (int i = 0; i < 20000; ++i) {
            container.add(((short) (i)));
        }
        for (int i = 40000; i < 60000; ++i) {
            container.add(((short) (i)));
        }
        Container result = container.inot(15000, 25000);
        // this result should stay as a run container (same one)
        Assert.assertSame(container, result);
    }

    @Test
    public void inot2() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Container result = container.inot(64, 66);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 65, 256 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot3() {
        RunContainer container = new RunContainer();
        // applied to a run-less container
        Container result = container.inot(64, 68);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 64, 65, 66, 67 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot4() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        // all runs are before the range
        Container result = container.inot(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 64, 256, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot5() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (502)));
        container.add(((short) (555)));
        container.add(((short) (564)));
        container.add(((short) (756)));
        // all runs are after the range
        Container result = container.inot(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 500, 502, 555, 564, 756, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot6() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        // one run is strictly within the range
        Container result = container.inot(499, 505);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 499, 504 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot7() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // one run, spans the range
        Container result = container.inot(502, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot8() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run, spans the range
        Container result = container.inot(502, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void inot9() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // first run, begins inside the range but extends outside
        Container result = container.inot(498, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void intersectionTest1() {
        Container ac = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k * 10)));
            rc = rc.add(((short) (k * 10)));
        }
        Assert.assertEquals(ac, ac.and(rc));
        Assert.assertEquals(ac, rc.and(ac));
    }

    @Test
    public void intersectionTest2() {
        Container ac = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 10000; ++k) {
            ac = ac.add(((short) (k)));
            rc = rc.add(((short) (k)));
        }
        Assert.assertEquals(ac, ac.and(rc));
        Assert.assertEquals(ac, rc.and(ac));
    }

    @Test
    public void intersectionTest3() {
        Container ac = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ac.add(((short) (k)));
            rc = rc.add(((short) (k + 100)));
        }
        Assert.assertEquals(0, rc.and(ac).getCardinality());
    }

    @Test
    public void intersectionTest4() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 5)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container intersection = rc.and(bc);
        Assert.assertEquals(100, intersection.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue(intersection.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals(200, bc.getCardinality());
        Assert.assertEquals(200, rc.getCardinality());
    }

    @Test
    public void ior() {
        Container rc1 = new RunContainer();
        Container rc2 = new RunContainer();
        rc1.iadd(0, 128);
        rc2.iadd(128, 256);
        rc1.ior(rc2);
        Assert.assertEquals(256, rc1.getCardinality());
    }

    @Test
    public void ior2() {
        Container rc = new RunContainer();
        Container ac = new ArrayContainer();
        rc.iadd(0, 128);
        rc.iadd(256, 512);
        ac.iadd(128, 256);
        rc.ior(ac);
        Assert.assertEquals(512, rc.getCardinality());
    }

    @Test
    public void iremove1() {
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        rc.iremove(1, 2);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove10() {
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
        rc.iadd(5, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 35);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove12() {
        Container rc = new RunContainer();
        rc.add(((short) (0)));
        rc.add(((short) (10)));
        rc.iremove(0, 11);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove13() {
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
        rc.iadd(37543, 65536);
        rc.iremove(9795, 65536);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove18() {
        Container rc = new RunContainer();
        rc.iadd(100, 200);
        rc.iadd(300, 400);
        rc.iadd(37543, 65000);
        rc.iremove(300, 65000);// start at beginning of run, end at end of another run

        Assert.assertEquals(100, rc.getCardinality());
    }

    @Test
    public void iremove19() {
        Container rc = new RunContainer();
        rc.iadd(100, 200);
        rc.iadd(300, 400);
        rc.iadd(64000, 65000);
        rc.iremove(350, 64000);// start midway through run, end at the start of next

        // got 100..199, 300..349, 64000..64999
        Assert.assertEquals(1150, rc.getCardinality());
    }

    @Test
    public void iremove2() {
        Container rc = new RunContainer();
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
    public void iremove20() {
        Container rc = new RunContainer();
        rc.iadd(100, 200);
        rc.iadd(300, 400);
        rc.iadd(64000, 65000);
        rc.iremove(350, 64001);// start midway through run, end at the start of next

        // got 100..199, 300..349, 64001..64999
        Assert.assertEquals(1149, rc.getCardinality());
    }

    @Test
    public void iremove3() {
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
        rc.iadd(0, 10);
        rc.iadd(20, 30);
        rc.iremove(0, 31);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove6() {
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
        rc.iadd(0, 10);
        rc.iremove(0, 15);
        Assert.assertEquals(0, rc.getCardinality());
    }

    @Test
    public void iremove8() {
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
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
        Container rc = new RunContainer();
        rc.iremove(10, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iremoveInvalidRange2() {
        Container rc = new RunContainer();
        rc.remove(0, (1 << 20));
    }

    @Test
    public void iremoveRange() {
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                for (int k = 0; k < 50; ++k) {
                    BitSet bs = new BitSet();
                    RunContainer container = new RunContainer();
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
    public void iremoveEmptyRange() {
        RunContainer container = new RunContainer();
        Assert.assertEquals(0, container.getCardinality());
        container.iremove(0, 0);
        Assert.assertEquals(0, container.getCardinality());
    }

    @Test
    public void iterator() {
        RunContainer x = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            for (int j = 0; j < k; ++j) {
                x = ((RunContainer) (x.add(((short) ((k * 100) + j)))));
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
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Container limit = container.limit(1024);
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
        RunContainer x = new RunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = ((RunContainer) (x.add(((short) (k)))));
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
        RunContainer x = new RunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = ((RunContainer) (x.add(((short) (k)))));
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
        RunContainer x = new RunContainer();
        for (int k = 0; k < (1 << 16); ++k) {
            x = ((RunContainer) (x.add(((short) (k)))));
        }
        ShortIterator i = x.getShortIterator();
        for (int k = 0; k < (1 << 16); ++k) {
            Assert.assertTrue(i.hasNext());
            Assert.assertEquals(i.next(), ((short) (k)));
        }
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void not1() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Container result = container.not(64, 64);// empty range

        Assert.assertNotSame(container, result);
        Assert.assertEquals(container, result);
    }

    @Test
    public void not10() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run begins inside the range but extends outside
        Container result = container.not(498, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not11() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        // second run entirely inside range, third run entirely inside range, 4th run entirely outside
        Container result = container.not(498, 507);
        Assert.assertEquals(7, result.getCardinality());
        for (short i : new short[]{ 300, 498, 499, 503, 505, 506, 510 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not12() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (504)));
        container.add(((short) (510)));
        container.add(((short) (511)));
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        Container result = container.not(501, 511);
        Assert.assertEquals(9, result.getCardinality());
        for (short i : new short[]{ 300, 500, 503, 505, 506, 507, 508, 509, 511 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not12A() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (301)));
        // first run crosses into range
        Container result = container.not(301, 303);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 300, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not13() {
        RunContainer container = new RunContainer();
        // check for off-by-1 errors that might affect length 1 runs
        for (int i = 100; i < 120; i += 3) {
            container.add(((short) (i)));
        }
        // second run crosses into range, third run entirely inside range, 4th crosses outside
        Container result = container.not(110, 115);
        Assert.assertEquals(10, result.getCardinality());
        for (short i : new short[]{ 100, 103, 106, 109, 110, 111, 113, 114, 115, 118 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not14() {
        not14once(10, 1);
        not14once(10, 10);
        not14once(1000, 100);
        for (int i = 1; i <= 100; ++i) {
            if ((i % 10) == 0) {
                System.out.println(("not 14 attempt " + i));
            }
            not14once(50000, 100);
        }
    }

    @Test
    public void not15() {
        RunContainer container = new RunContainer();
        for (int i = 0; i < 20000; ++i) {
            container.add(((short) (i)));
        }
        for (int i = 40000; i < 60000; ++i) {
            container.add(((short) (i)));
        }
        Container result = container.not(15000, 25000);
        // this result should stay as a run container.
        Assert.assertTrue((result instanceof RunContainer));
    }

    @Test
    public void not2() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        Container result = container.not(64, 66);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 65, 256 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not3() {
        RunContainer container = new RunContainer();
        // applied to a run-less container
        Container result = container.not(64, 68);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 64, 65, 66, 67 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not4() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        // all runs are before the range
        Container result = container.not(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 0, 2, 55, 64, 256, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not5() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (502)));
        container.add(((short) (555)));
        container.add(((short) (564)));
        container.add(((short) (756)));
        // all runs are after the range
        Container result = container.not(300, 303);
        Assert.assertEquals(8, result.getCardinality());
        for (short i : new short[]{ 500, 502, 555, 564, 756, 300, 301, 302 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not6() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        // one run is strictly within the range
        Container result = container.not(499, 505);
        Assert.assertEquals(2, result.getCardinality());
        for (short i : new short[]{ 499, 504 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not7() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // one run, spans the range
        Container result = container.not(502, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not8() {
        RunContainer container = new RunContainer();
        container.add(((short) (300)));
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // second run, spans the range
        Container result = container.not(502, 504);
        Assert.assertEquals(5, result.getCardinality());
        for (short i : new short[]{ 300, 500, 501, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void not9() {
        RunContainer container = new RunContainer();
        container.add(((short) (500)));
        container.add(((short) (501)));
        container.add(((short) (502)));
        container.add(((short) (503)));
        container.add(((short) (504)));
        container.add(((short) (505)));
        // first run, begins inside the range but extends outside
        Container result = container.not(498, 504);
        Assert.assertEquals(4, result.getCardinality());
        for (short i : new short[]{ 498, 499, 504, 505 }) {
            Assert.assertTrue(result.contains(i));
        }
    }

    @Test
    public void randomFun() {
        final int bitsetperword1 = 32;
        final int bitsetperword2 = 63;
        Container rc1;
        Container rc2;
        Container ac1;
        Container ac2;
        Random rand = new Random(0);
        final int max = 1 << 16;
        final int howmanywords = (1 << 16) / 64;
        int[] values1 = TestRunContainer.generateUniformHash(rand, (bitsetperword1 * howmanywords), max);
        int[] values2 = TestRunContainer.generateUniformHash(rand, (bitsetperword2 * howmanywords), max);
        rc1 = new RunContainer();
        rc1 = TestRunContainer.fillMeUp(rc1, values1);
        rc2 = new RunContainer();
        rc2 = TestRunContainer.fillMeUp(rc2, values2);
        ac1 = new ArrayContainer();
        ac1 = TestRunContainer.fillMeUp(ac1, values1);
        ac2 = new ArrayContainer();
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
            throw new RuntimeException("andnots do not match");
        }
        if (!(rc1.xor(rc2).equals(ac1.xor(ac2)))) {
            throw new RuntimeException("xors do not match");
        }
    }

    @Test
    public void rank() {
        RunContainer container = new RunContainer();
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
        Container container = new RunContainer();
        container = container.add(16, 32);
        Assert.assertTrue((container instanceof RunContainer));
        // results in correct value: 16
        // assertEquals(16, container.toBitmapContainer().rank((short) 32));
        Assert.assertEquals(16, container.rank(((short) (32))));
    }

    @Test
    public void remove() {
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        Container newContainer = rc.remove(1, 2);
        Assert.assertEquals(0, newContainer.getCardinality());
    }

    @Test
    public void RunContainerArg_ArrayAND() {
        boolean atLeastOneArray = false;
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container thisContainer = setb.get(k);
                if (thisContainer instanceof BitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                Container c1 = thisContainer.and(set.get(l));
                Container c2 = setb.get(k).and(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void RunContainerArg_ArrayANDNOT() {
        boolean atLeastOneArray = false;
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container thisContainer = setb.get(k);
                if (thisContainer instanceof BitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                Container c1 = thisContainer.andNot(set.get(l));
                Container c2 = setb.get(k).andNot(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void RunContainerArg_ArrayANDNOT2() {
        ArrayContainer ac = new ArrayContainer(12, new short[]{ 0, 2, 4, 8, 10, 15, 16, 48, 50, 61, 80, -2 });
        RunContainer rc = new RunContainer(new short[]{ 7, 3, 17, 2, 20, 3, 30, 3, 36, 6, 60, 5, -3, 2 }, 7);
        Assert.assertEquals(new ArrayContainer(8, new short[]{ 0, 2, 4, 15, 16, 48, 50, 80 }), ac.andNot(rc));
    }

    @Test
    public void FullRunContainerArg_ArrayANDNOT2() {
        ArrayContainer ac = new ArrayContainer(1, new short[]{ 3 });
        Container rc = RunContainer.full();
        Assert.assertEquals(new ArrayContainer(), ac.andNot(rc));
    }

    @Test
    public void RunContainerArg_ArrayANDNOT3() {
        ArrayContainer ac = new ArrayContainer(1, new short[]{ 5 });
        Container rc = new RunContainer(new short[]{ 3, 10 }, 1);
        Assert.assertEquals(new ArrayContainer(), ac.andNot(rc));
    }

    @Test
    public void RunContainerArg_ArrayOR() {
        boolean atLeastOneArray = false;
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container thisContainer = setb.get(k);
                // BitmapContainers are tested separately, but why not test some more?
                if (thisContainer instanceof BitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                Container c1 = thisContainer.or(set.get(l));
                Container c2 = setb.get(k).or(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void RunContainerArg_ArrayXOR() {
        boolean atLeastOneArray = false;
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container thisContainer = setb.get(k);
                if (thisContainer instanceof BitmapContainer) {
                    // continue;
                } else {
                    atLeastOneArray = true;
                }
                Container c1 = thisContainer.xor(set.get(l));
                Container c2 = setb.get(k).xor(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
        Assert.assertTrue(atLeastOneArray);
    }

    @Test
    public void RunContainerFromBitmap() {
        Container rc = new RunContainer();
        Container bc = new BitmapContainer();
        rc = rc.add(((short) (2)));
        bc = bc.add(((short) (2)));
        rc = rc.add(((short) (3)));
        bc = bc.add(((short) (3)));
        rc = rc.add(((short) (4)));
        bc = bc.add(((short) (4)));
        rc = rc.add(((short) (17)));
        bc = bc.add(((short) (17)));
        for (int i = 192; i < 500; ++i) {
            rc = rc.add(((short) (i)));
            bc = bc.add(((short) (i)));
        }
        rc = rc.add(((short) (1700)));
        bc = bc.add(((short) (1700)));
        rc = rc.add(((short) (1701)));
        bc = bc.add(((short) (1701)));
        // cases depending on whether we have largest item.
        // this test: no, we don't get near largest word
        RunContainer rc2 = new RunContainer(((BitmapContainer) (bc)), ((RunContainer) (rc)).nbrruns);
        Assert.assertEquals(rc, rc2);
    }

    @Test
    public void RunContainerFromBitmap1() {
        Container rc = new RunContainer();
        Container bc = new BitmapContainer();
        rc = rc.add(((short) (2)));
        bc = bc.add(((short) (2)));
        rc = rc.add(((short) (3)));
        bc = bc.add(((short) (3)));
        rc = rc.add(((short) (4)));
        bc = bc.add(((short) (4)));
        rc = rc.add(((short) (17)));
        bc = bc.add(((short) (17)));
        for (int i = 192; i < 500; ++i) {
            rc = rc.add(((short) (i)));
            bc = bc.add(((short) (i)));
        }
        rc = rc.add(((short) (1700)));
        bc = bc.add(((short) (1700)));
        rc = rc.add(((short) (1701)));
        bc = bc.add(((short) (1701)));
        // cases depending on whether we have largest item.
        // this test: we have a 1 in the largest word but not at end
        rc = rc.add(((short) (65530)));
        bc = bc.add(((short) (65530)));
        RunContainer rc2 = new RunContainer(((BitmapContainer) (bc)), ((RunContainer) (rc)).nbrruns);
        Assert.assertEquals(rc, rc2);
    }

    @Test
    public void RunContainerFromBitmap2() {
        Container rc = new RunContainer();
        Container bc = new BitmapContainer();
        rc = rc.add(((short) (2)));
        bc = bc.add(((short) (2)));
        rc = rc.add(((short) (3)));
        bc = bc.add(((short) (3)));
        rc = rc.add(((short) (4)));
        bc = bc.add(((short) (4)));
        rc = rc.add(((short) (17)));
        bc = bc.add(((short) (17)));
        for (int i = 192; i < 500; ++i) {
            rc = rc.add(((short) (i)));
            bc = bc.add(((short) (i)));
        }
        rc = rc.add(((short) (1700)));
        bc = bc.add(((short) (1700)));
        rc = rc.add(((short) (1701)));
        bc = bc.add(((short) (1701)));
        // cases depending on whether we have largest item.
        // this test: we have a 1 in the largest word and at end
        rc = rc.add(((short) (65530)));
        bc = bc.add(((short) (65530)));
        rc = rc.add(((short) (65535)));
        bc = bc.add(((short) (65535)));
        RunContainer rc2 = new RunContainer(((BitmapContainer) (bc)), ((RunContainer) (rc)).nbrruns);
        Assert.assertEquals(rc, rc2);
    }

    @Test
    public void RunContainerFromBitmap3() {
        Container rc = new RunContainer();
        Container bc = new BitmapContainer();
        rc = rc.add(((short) (2)));
        bc = bc.add(((short) (2)));
        rc = rc.add(((short) (3)));
        bc = bc.add(((short) (3)));
        rc = rc.add(((short) (4)));
        bc = bc.add(((short) (4)));
        rc = rc.add(((short) (17)));
        bc = bc.add(((short) (17)));
        for (int i = 192; i < 500; ++i) {
            rc = rc.add(((short) (i)));
            bc = bc.add(((short) (i)));
        }
        rc = rc.add(((short) (1700)));
        bc = bc.add(((short) (1700)));
        rc = rc.add(((short) (1701)));
        bc = bc.add(((short) (1701)));
        // cases depending on whether we have largest item.
        // this test: we have a lot of 1s in a run at the end
        for (int i = 65000; i < 65535; ++i) {
            rc = rc.add(((short) (i)));
            bc = bc.add(((short) (i)));
        }
        RunContainer rc2 = new RunContainer(((BitmapContainer) (bc)), ((RunContainer) (rc)).nbrruns);
        Assert.assertEquals(rc, rc2);
    }

    @Test
    public void RunContainerVSRunContainerAND() {
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container c1 = set.get(k).and(set.get(l));
                Container c2 = setb.get(k).and(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void RunContainerVSRunContainerANDNOT() {
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container c1 = set.get(k).andNot(set.get(l));
                Container c2 = setb.get(k).andNot(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void RunContainerVSRunContainerOR() {
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container c1 = set.get(k).or(set.get(l));
                Container c2 = setb.get(k).or(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void RunContainerVSRunContainerXOR() {
        ArrayList<RunContainer> set = new ArrayList<RunContainer>();
        ArrayList<Container> setb = new ArrayList<Container>();
        TestRunContainer.getSetOfRunContainers(set, setb);
        for (int k = 0; k < (set.size()); ++k) {
            for (int l = 0; l < (set.size()); ++l) {
                Assert.assertTrue(set.get(k).equals(setb.get(k)));
                Assert.assertTrue(set.get(l).equals(setb.get(l)));
                Container c1 = set.get(k).xor(set.get(l));
                Container c2 = setb.get(k).xor(setb.get(l));
                Assert.assertTrue(c1.equals(c2));
            }
        }
    }

    @Test
    public void safeor() {
        Container rc1 = new RunContainer();
        Container rc2 = new RunContainer();
        for (int i = 0; i < 100; ++i) {
            rc1 = rc1.iadd((i * 4), (((i + 1) * 4) - 1));
            rc2 = rc2.iadd(((i * 4) + 10000), ((((i + 1) * 4) - 1) + 10000));
        }
        Container x = rc1.or(rc2);
        rc1.ior(rc2);
        if (!(rc1.equals(x))) {
            throw new RuntimeException("bug");
        }
    }

    @Test
    public void orFullToRunContainer() {
        Container rc = Container.rangeOfOnes(0, (1 << 15));
        Container half = new BitmapContainer((1 << 15), (1 << 16));
        Assert.assertTrue((rc instanceof RunContainer));
        Container result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof RunContainer));
    }

    @Test
    public void orFullToRunContainer2() {
        Container rc = Container.rangeOfOnes(((1 << 10) - 200), (1 << 16));
        Container half = new ArrayContainer(0, (1 << 10));
        Assert.assertTrue((rc instanceof RunContainer));
        Container result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof RunContainer));
    }

    @Test
    public void orFullToRunContainer3() {
        Container rc = Container.rangeOfOnes(0, (1 << 15));
        Container half = Container.rangeOfOnes(((1 << 15) - 200), (1 << 16));
        Assert.assertTrue((rc instanceof RunContainer));
        Container result = rc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertTrue((result instanceof RunContainer));
    }

    @Test
    public void safeSerialization() throws Exception {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (2)));
        container.add(((short) (55)));
        container.add(((short) (64)));
        container.add(((short) (256)));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(container);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        RunContainer newContainer = ((RunContainer) (in.readObject()));
        Assert.assertEquals(container, newContainer);
        Assert.assertEquals(container.serializedSizeInBytes(), newContainer.serializedSizeInBytes());
    }

    @Test
    public void select() {
        RunContainer container = new RunContainer();
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

    @Test(expected = IllegalArgumentException.class)
    public void select2() {
        RunContainer container = new RunContainer();
        container.add(((short) (0)));
        container.add(((short) (3)));
        container.add(((short) (118)));
        container.select(666);
    }

    @Test
    public void simpleIterator() {
        RunContainer x = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            x = ((RunContainer) (x.add(((short) (k)))));
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
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(array1);
        rb1.runOptimize();
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(array2);
        RoaringBitmap answer = RoaringBitmap.andNot(rb1, rb2);
        Assert.assertEquals(answer.getCardinality(), array1.length);
    }

    @Test
    public void testRoaringWithOptimize() {
        // create the same bitmap over and over again, with optimizing it
        final Set<RoaringBitmap> setWithOptimize = new HashSet<RoaringBitmap>();
        final int max = 1000;
        for (int i = 0; i < max; i++) {
            final RoaringBitmap bitmapWithOptimize = new RoaringBitmap();
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
        final Set<RoaringBitmap> setWithoutOptimize = new HashSet<RoaringBitmap>();
        final int max = 1000;
        for (int i = 0; i < max; i++) {
            final RoaringBitmap bitmapWithoutOptimize = new RoaringBitmap();
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
        RunContainer rc = new RunContainer();
        rc.iadd(0, ((ArrayContainer.DEFAULT_MAX_SIZE) / 2));
        Container ac = rc.toBitmapOrArrayContainer(rc.getCardinality());
        Assert.assertTrue((ac instanceof ArrayContainer));
        Assert.assertEquals(((ArrayContainer.DEFAULT_MAX_SIZE) / 2), ac.getCardinality());
        for (short k = 0; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 2); ++k) {
            Assert.assertTrue(ac.contains(k));
        }
        rc.iadd(((ArrayContainer.DEFAULT_MAX_SIZE) / 2), (2 * (ArrayContainer.DEFAULT_MAX_SIZE)));
        Container bc = rc.toBitmapOrArrayContainer(rc.getCardinality());
        Assert.assertTrue((bc instanceof BitmapContainer));
        Assert.assertEquals((2 * (ArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        for (short k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(bc.contains(k));
        }
    }

    @Test
    public void union() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            bc = bc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container union = rc.or(bc);
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
        ArrayContainer ac = new ArrayContainer();
        RunContainer rc = new RunContainer();
        for (int k = 0; k < 100; ++k) {
            ac = ((ArrayContainer) (ac.add(((short) (k * 10)))));
            rc = ((RunContainer) (rc.add(((short) ((k * 10) + 3)))));
        }
        Container union = rc.or(ac);
        Assert.assertEquals(200, union.getCardinality());
        for (int k = 0; k < 100; ++k) {
            Assert.assertTrue(union.contains(((short) (k * 10))));
            Assert.assertTrue(union.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals(100, ac.getCardinality());
        Assert.assertEquals(100, rc.getCardinality());
    }

    @Test
    public void xor() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container result = rc.xor(bc);
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), result.getCardinality());
        for (int k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), rc.getCardinality());
    }

    @Test
    public void xor_array() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            bc = bc.add(((short) (k * 10)));
            bc = bc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) (k * 10)));
            rc = rc.add(((short) ((k * 10) + 3)));
        }
        Container result = rc.xor(bc);
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), result.getCardinality());
        for (int k = 0; k < (2 * (ArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 3))));
        }
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), bc.getCardinality());
        Assert.assertEquals((4 * (ArrayContainer.DEFAULT_MAX_SIZE)), rc.getCardinality());
    }

    @Test
    public void xor_array_largecase_runcontainer_best() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < 60; ++k) {
            for (int j = 0; j < 99; ++j) {
                rc = rc.add(((short) ((k * 100) + j)));// most efficiently stored as runs

                bc = bc.add(((short) ((k * 100) + 98))).add(((short) ((k * 100) + 99)));
            }
        }
        // size ordering preference for rc: run, bitmap, array
        Assert.assertTrue((bc instanceof ArrayContainer));
        Assert.assertTrue((rc instanceof RunContainer));
        int rcSize = rc.getCardinality();
        int bcSize = bc.getCardinality();
        Container result = rc.xor(bc);
        // input containers should not change (just check card)
        Assert.assertEquals(rcSize, rc.getCardinality());
        Assert.assertEquals(bcSize, bc.getCardinality());
        // each group of 60, we gain the missing 99th value but lose the 98th. Net wash
        Assert.assertEquals(rcSize, result.getCardinality());
        // a runcontainer would be, space-wise, best
        // but the code may (and does) opt to produce a bitmap
        // assertTrue( result instanceof RunContainer);
        for (int k = 0; k < 60; ++k) {
            for (int j = 0; j < 98; ++j) {
                Assert.assertTrue(result.contains(((short) ((k * 100) + j))));
            }
            Assert.assertTrue(result.contains(((short) ((k * 100) + 99))));
        }
    }

    @Test
    public void xor_array_mediumcase() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 6); ++k) {
            rc = rc.add(((short) (k * 10)));// most efficiently stored as runs

            rc = rc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) ((k * 10) + 2)));
        }
        for (int k = 0; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 12); ++k) {
            bc = bc.add(((short) (k * 10)));
        }
        // size ordering preference for rc: run, array, bitmap
        Assert.assertTrue((bc instanceof ArrayContainer));
        Assert.assertTrue((rc instanceof RunContainer));
        int rcSize = rc.getCardinality();
        int bcSize = bc.getCardinality();
        Container result = rc.xor(bc);
        // input containers should not change (just check card)
        Assert.assertEquals(rcSize, rc.getCardinality());
        Assert.assertEquals(bcSize, bc.getCardinality());
        Assert.assertEquals((rcSize - bcSize), result.getCardinality());
        // The result really ought to be a runcontainer, by its size
        // however, as of test writing, the implementation
        // will have converted the result to an array container.
        // This is suboptimal, storagewise, but arguably not an error
        // assertTrue( result instanceof RunContainer);
        for (int k = 0; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 12); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 2))));
        }
        for (int k = (ArrayContainer.DEFAULT_MAX_SIZE) / 12; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 6); ++k) {
            Assert.assertTrue(result.contains(((short) ((k * 10) + 1))));
            Assert.assertTrue(result.contains(((short) ((k * 10) + 2))));
        }
    }

    @Test
    public void xor_array_smallcase() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        for (int k = 0; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 3); ++k) {
            rc = rc.add(((short) (k * 10)));// most efficiently stored as runs

            rc = rc.add(((short) ((k * 10) + 1)));
            rc = rc.add(((short) ((k * 10) + 2)));
            rc = rc.add(((short) ((k * 10) + 3)));
            rc = rc.add(((short) ((k * 10) + 4)));
        }
        // very small array.
        bc = bc.add(((short) (1))).add(((short) (2))).add(((short) (3))).add(((short) (4))).add(((short) (5)));
        Assert.assertTrue((bc instanceof ArrayContainer));
        Assert.assertTrue((rc instanceof RunContainer));
        int rcSize = rc.getCardinality();
        int bcSize = bc.getCardinality();
        Container result = rc.xor(bc);
        // input containers should not change (just check card)
        Assert.assertEquals(rcSize, rc.getCardinality());
        Assert.assertEquals(bcSize, bc.getCardinality());
        Assert.assertEquals((rcSize - 3), result.getCardinality());
        Assert.assertTrue(result.contains(((short) (5))));
        Assert.assertTrue(result.contains(((short) (0))));
        for (int k = 1; k < ((ArrayContainer.DEFAULT_MAX_SIZE) / 3); ++k) {
            for (int i = 0; i < 5; ++i) {
                Assert.assertTrue(result.contains(((short) ((k * 10) + i))));
            }
        }
    }

    @Test
    public void xor1() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor1a() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor2() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        bc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor2a() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        bc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(1, result.getCardinality());
        Assert.assertTrue(result.contains(((short) (1))));
    }

    @Test
    public void xor3() {
        Container bc = new BitmapContainer();
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        bc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void xor3a() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        rc.add(((short) (1)));
        bc.add(((short) (1)));
        Container result = rc.xor(bc);
        Assert.assertEquals(0, result.getCardinality());
    }

    @Test
    public void xor4() {
        Container bc = new ArrayContainer();
        Container rc = new RunContainer();
        Container answer = new ArrayContainer();
        answer = answer.add(28203, 28214);
        rc = rc.add(28203, 28214);
        int[] data = new int[]{ 17739, 17740, 17945, 19077, 19278, 19407 };
        for (int x : data) {
            answer = answer.add(((short) (x)));
            bc = bc.add(((short) (x)));
        }
        Container result = rc.xor(bc);
        Assert.assertEquals(answer, result);
    }

    @Test
    public void xor5() {
        Container rc1 = new RunContainer();
        Container rc2 = new RunContainer();
        rc2.iadd(1, 13);
        Assert.assertEquals(rc2, rc1.xor(rc2));
        Assert.assertEquals(rc2, rc2.xor(rc1));
    }

    @Test
    public void intersects1() {
        Container ac = new ArrayContainer();
        ac = ac.add(((short) (1)));
        ac = ac.add(((short) (7)));
        ac = ac.add(((short) (13)));
        ac = ac.add(((short) (666)));
        Container rc = new RunContainer();
        Assert.assertFalse(rc.intersects(ac));
        Assert.assertFalse(ac.intersects(rc));
        rc = rc.add(((short) (1000)));
        Assert.assertFalse(rc.intersects(ac));
        Assert.assertFalse(ac.intersects(rc));
        rc = rc.remove(((short) (1000)));
        rc = rc.add(100, 200);
        rc = rc.add(300, 500);
        Assert.assertFalse(rc.intersects(ac));
        Assert.assertFalse(ac.intersects(rc));
        rc = rc.add(500, 1000);
        Assert.assertTrue(rc.intersects(ac));
        Assert.assertTrue(ac.intersects(rc));
    }

    @Test
    public void intersects2() {
        Container rc1 = new RunContainer();
        Container rc2 = new RunContainer();
        Assert.assertFalse(rc1.intersects(rc2));
        rc1 = rc1.add(10, 50);
        rc2 = rc2.add(100, 500);
        Assert.assertFalse(rc1.intersects(rc2));
        rc1 = rc1.add(60, 70);
        Assert.assertFalse(rc1.intersects(rc2));
        rc1 = rc1.add(600, 700);
        rc2 = rc2.add(800, 900);
        Assert.assertFalse(rc1.intersects(rc2));
        rc2 = rc2.add(30, 40);
        Assert.assertTrue(rc1.intersects(rc2));
    }

    @Test
    public void intersects3() {
        Container rc = new RunContainer();
        Container bc = new BitmapContainer();
        rc = rc.add(10, 50);
        bc = bc.add(100, 500);
        Assert.assertFalse(rc.intersects(bc));
    }

    @Test(expected = RuntimeException.class)
    public void constructor1() {
        new RunContainer(new short[]{ 1, 2, 10, 3 }, 5);
    }

    @Test
    public void ensureCapacity() {
        RunContainer rc = new RunContainer();
        rc.add(((short) (13)));
        Assert.assertTrue(rc.contains(((short) (13))));
        rc.ensureCapacity(10);
        Assert.assertTrue(rc.contains(((short) (13))));
    }

    @Test
    public void testToString() {
        Container rc = new RunContainer(32200, 35000);
        rc.add(((short) (-1)));
        Assert.assertEquals("[32200,34999][65535,65535]", rc.toString());
    }

    @Test
    public void lazyIOR() {
        Container rc = new RunContainer();
        Container ac = new ArrayContainer();
        ac = ac.add(0, 1);
        rc = rc.lazyIOR(ac);
        Assert.assertEquals(1, rc.getCardinality());
        rc = new RunContainer();
        rc = rc.add(0, 13);
        rc = rc.lazyIOR(ac);
        Assert.assertEquals(13, rc.getCardinality());
        rc = new RunContainer();
        rc = rc.add(0, (1 << 16));
        rc = rc.lazyIOR(ac);
        Assert.assertEquals((1 << 16), rc.getCardinality());
    }

    @Test
    public void lazyOR() {
        Container rc = new RunContainer();
        Container ac = new ArrayContainer();
        ac = ac.add(0, 1);
        rc = rc.lazyOR(ac);
        Assert.assertEquals(1, rc.getCardinality());
        rc = new RunContainer();
        rc = rc.add(0, 13);
        rc = rc.lazyOR(ac);
        Assert.assertEquals(13, rc.getCardinality());
        rc = new RunContainer();
        rc = rc.add(0, (1 << 16));
        rc = rc.lazyOR(ac);
        Assert.assertEquals((1 << 16), rc.getCardinality());
    }

    @Test
    public void testLazyORFull() {
        Container rc = Container.rangeOfOnes(0, (1 << 15));
        BitmapContainer bc2 = new BitmapContainer(3210, (1 << 16));
        Container rbc = rc.lazyOR(bc2);
        Assert.assertEquals((-1), rbc.getCardinality());
        Container repaired = rbc.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertTrue((repaired instanceof RunContainer));
    }

    @Test
    public void testLazyORFull2() {
        Container rc = Container.rangeOfOnes(((1 << 10) - 200), (1 << 16));
        ArrayContainer ac = new ArrayContainer(0, (1 << 10));
        Container rbc = rc.lazyOR(ac);
        Assert.assertEquals((1 << 16), rbc.getCardinality());
        Assert.assertTrue((rbc instanceof RunContainer));
    }

    @Test
    public void testLazyORFull3() {
        Container rc = Container.rangeOfOnes(0, (1 << 15));
        Container rc2 = Container.rangeOfOnes((1 << 15), (1 << 16));
        Container result = rc.lazyOR(rc2);
        Container iresult = rc.lazyIOR(rc2);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertEquals((1 << 16), iresult.getCardinality());
        Assert.assertTrue((result instanceof RunContainer));
        Assert.assertTrue((iresult instanceof RunContainer));
    }

    @Test
    public void testRangeCardinality() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        Container result = rc.or(bc);
        Assert.assertEquals(8677, result.getCardinality());
    }

    @Test
    public void testRangeCardinality2() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        bc.add(((short) (22345)));// important case to have greater element than run container

        bc.add(Short.MAX_VALUE);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 18000 }, 3);
        Assert.assertTrue(((rc.getCardinality()) > (DEFAULT_MAX_SIZE)));
        Container result = rc.andNot(bc);
        Assert.assertEquals(11437, result.getCardinality());
    }

    @Test
    public void testRangeCardinality3() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 5200 }, 3);
        BitmapContainer result = ((BitmapContainer) (rc.and(bc)));
        Assert.assertEquals(5046, result.getCardinality());
    }

    @Test
    public void testRangeCardinality4() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        BitmapContainer result = ((BitmapContainer) (rc.xor(bc)));
        Assert.assertEquals(6031, result.getCardinality());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirst_Empty() {
        new RunContainer().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLast_Empty() {
        new RunContainer().last();
    }

    @Test
    public void testFirstLast() {
        Container rc = new RunContainer();
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
        RoaringBitmap roaringWithRun = new RoaringBitmap();
        roaringWithRun.add(32768L, 65536);// (1 << 15) to (1 << 16).

        Assert.assertEquals(roaringWithRun.first(), 32768);
    }

    @Test
    public void testContainsBitmapContainer_EmptyContainsEmpty() {
        Container rc = new RunContainer();
        Container subset = new BitmapContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeProperSubset() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new BitmapContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeProperSubsetDifferentStart() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new BitmapContainer().add(1, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeShiftedSet() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new BitmapContainer().add(2, 12);
        Assert.assertFalse(rc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeSelf() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new BitmapContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeSuperSet() {
        Container rc = new RunContainer().add(0, 10);
        Container superset = new BitmapContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeDisJointSet() {
        Container rc = new RunContainer().add(0, 10);
        Container disjoint = new BitmapContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testContainsRunContainer_EmptyContainsEmpty() {
        Container rc = new RunContainer();
        Container subset = new RunContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_IncludeProperSubset() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new RunContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_IncludeSelf() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new RunContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_ExcludeSuperSet() {
        Container rc = new RunContainer().add(0, 10);
        Container superset = new RunContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsRunContainer_IncludeProperSubsetDifferentStart() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new RunContainer().add(1, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_ExcludeShiftedSet() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new RunContainer().add(2, 12);
        Assert.assertFalse(rc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_ExcludeDisJointSet() {
        Container rc = new RunContainer().add(0, 10);
        Container disjoint = new RunContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testContainsArrayContainer_EmptyContainsEmpty() {
        Container rc = new RunContainer();
        Container subset = new ArrayContainer();
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_IncludeProperSubset() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new ArrayContainer().add(0, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_IncludeProperSubsetDifferentStart() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new ArrayContainer().add(2, 9);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_ExcludeShiftedSet() {
        Container rc = new RunContainer().add(0, 10);
        Container shifted = new ArrayContainer().add(2, 12);
        Assert.assertFalse(rc.contains(shifted));
    }

    @Test
    public void testContainsArrayContainer_IncludeSelf() {
        Container rc = new RunContainer().add(0, 10);
        Container subset = new ArrayContainer().add(0, 10);
        Assert.assertTrue(rc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_ExcludeSuperSet() {
        Container rc = new RunContainer().add(0, 10);
        Container superset = new ArrayContainer().add(0, 20);
        Assert.assertFalse(rc.contains(superset));
    }

    @Test
    public void testContainsArrayContainer_ExcludeDisJointSet() {
        Container rc = new RunContainer().add(0, 10);
        Container disjoint = new ArrayContainer().add(20, 40);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
        disjoint = new ArrayContainer().add(((short) (512)));
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
        rc = rc.add(12, 14).add(16, 18).add(20, 22);
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
        rc.trim();
        Assert.assertFalse(rc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(rc));
    }

    @Test
    public void testEqualsArrayContainer_Equal() {
        Container rc = new RunContainer().add(0, 10);
        Container ac = new ArrayContainer().add(0, 10);
        Assert.assertTrue(rc.equals(ac));
        Assert.assertTrue(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayLarger() {
        Container rc = new RunContainer().add(0, 10);
        Container ac = new ArrayContainer().add(0, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArraySmaller() {
        Container rc = new RunContainer().add(0, 10);
        Container ac = new ArrayContainer().add(0, 9);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayShifted() {
        Container rc = new RunContainer().add(0, 10);
        Container ac = new ArrayContainer().add(1, 11);
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEqualsArrayContainer_NotEqual_ArrayDiscontiguous() {
        Container rc = new RunContainer().add(0, 10);
        Container ac = new ArrayContainer().add(0, 11);
        ac.flip(((short) (9)));
        Assert.assertFalse(rc.equals(ac));
        Assert.assertFalse(ac.equals(rc));
    }

    @Test
    public void testEquals_FullRunContainerWithArrayContainer() {
        Container full = new RunContainer().add(0, (1 << 16));
        Assert.assertNotEquals(full, new ArrayContainer().add(0, 10));
    }

    @Test
    public void testFullConstructor() {
        Assert.assertTrue(RunContainer.full().isFull());
    }

    @Test
    public void testRangeConstructor() {
        RunContainer c = new RunContainer(0, (1 << 16));
        Assert.assertTrue(c.isFull());
        Assert.assertEquals(65536, c.getCardinality());
    }

    @Test
    public void testRangeConstructor2() {
        RunContainer c = new RunContainer(17, 1000);
        Assert.assertEquals(983, c.getCardinality());
    }

    @Test
    public void testRangeConstructor3() {
        RunContainer a = new RunContainer(17, 45679);
        RunContainer b = new RunContainer();
        b.iadd(17, 45679);
        Assert.assertEquals(a, b);
    }

    @Test
    public void testRangeConstructor4() {
        RunContainer c = new RunContainer(0, 45679);
        Assert.assertEquals(45679, c.getCardinality());
    }

    @Test
    public void testSimpleCardinality() {
        RunContainer c = new RunContainer();
        c.add(((short) (1)));
        c.add(((short) (17)));
        Assert.assertEquals(2, c.getCardinality());
    }

    @Test
    public void testIntersectsWithRange() {
        Container container = new RunContainer().add(0, 10);
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, (1 << 16)));
        Assert.assertFalse(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testIntersectsWithRangeUnsigned() {
        Container container = new RunContainer().add(TestRunContainer.lower16Bits((-50)), TestRunContainer.lower16Bits((-10)));
        Assert.assertFalse(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, TestRunContainer.lower16Bits((-40))));
        Assert.assertFalse(container.intersects(TestRunContainer.lower16Bits((-100)), TestRunContainer.lower16Bits((-55))));
        Assert.assertFalse(container.intersects((-9), (1 << 16)));
        Assert.assertTrue(container.intersects(11, (1 << 16)));
    }

    @Test
    public void testIntersectsWithRangeManyRuns() {
        Container container = new RunContainer().add(0, 10).add(TestRunContainer.lower16Bits((-50)), TestRunContainer.lower16Bits((-10)));
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
        Assert.assertTrue(RunContainer.full().contains(0, (1 << 16)));
        Assert.assertFalse(RunContainer.full().flip(((short) (1 << 15))).contains(0, (1 << 16)));
    }

    @Test
    public void testContainsRange() {
        Container rc = new RunContainer().add(1, 100).add(5000, 10000);
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
    public void testContainsRange3() {
        Container rc = new RunContainer().add(1, 100).add(300, 300).add(400, 500).add(502, 600).add(700, 10000);
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
        RunContainer container = new RunContainer(new short[]{ 64, 64 }, 1);
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals((-1), container.nextValue(((short) (129))));
    }

    @Test
    public void testNextValueBetweenRuns() {
        RunContainer container = new RunContainer(new short[]{ 64, 64, 256, 64 }, 2);
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals(256, container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (512))));
    }

    @Test
    public void testNextValue2() {
        RunContainer container = new RunContainer(new short[]{ 64, 64, 200, 300, 5000, 200 }, 3);
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
        RunContainer container = new RunContainer(new short[]{ 64, 64 }, 1);
        Assert.assertEquals((-1), container.previousValue(((short) (0))));
        Assert.assertEquals((-1), container.previousValue(((short) (63))));
        Assert.assertEquals(64, container.previousValue(((short) (64))));
        Assert.assertEquals(65, container.previousValue(((short) (65))));
        Assert.assertEquals(128, container.previousValue(((short) (128))));
        Assert.assertEquals(128, container.previousValue(((short) (129))));
    }

    @Test
    public void testPreviousValue2() {
        RunContainer container = new RunContainer(new short[]{ 64, 64, 200, 300, 5000, 200 }, 3);
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
        RunContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) (0)), ((short) ((1 << 15) | 7)), ((short) (0)) }, 2);
        Assert.assertEquals((-1), container.previousValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextValueUnsigned() {
        RunContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) (0)), ((short) ((1 << 15) | 7)), ((short) (0)) }, 2);
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals((-1), container.nextValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testPreviousAbsentValue1() {
        Container container = new RunContainer().iadd(64, 129);
        Assert.assertEquals(0, container.previousAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (63))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (64))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (65))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.previousAbsentValue(((short) (129))));
    }

    @Test
    public void testPreviousAbsentValue2() {
        Container container = new RunContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
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
        RunContainer container = new RunContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.previousAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testPreviousAbsentValueSparse() {
        RunContainer container = new RunContainer(new short[]{ 10, 0, 20, 0, 30, 0 }, 3);
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
        RunContainer container = new RunContainer(evenBits, (1 << 14));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i - 1), container.previousAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.previousAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testPreviousAbsentValueUnsigned() {
        RunContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), 0, ((short) ((1 << 15) | 7)), 0 }, 2);
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.previousAbsentValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextAbsentValue1() {
        Container container = new RunContainer().iadd(64, 129);
        Assert.assertEquals(0, container.nextAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.nextAbsentValue(((short) (63))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (64))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (65))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (129))));
    }

    @Test
    public void testNextAbsentValue2() {
        Container container = new RunContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201);
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
        RunContainer container = new RunContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.nextAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testNextAbsentValueSparse() {
        Container container = new RunContainer(new short[]{ 10, 0, 20, 0, 30, 0 }, 3);
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
        RunContainer container = new RunContainer(evenBits, (1 << 14));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testNextAbsentValueUnsigned() {
        RunContainer container = new RunContainer(new short[]{ ((short) ((1 << 15) | 5)), 0, ((short) ((1 << 15) | 7)), 0 }, 2);
        Assert.assertEquals(((1 << 15) | 4), container.nextAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 8))));
    }
}

