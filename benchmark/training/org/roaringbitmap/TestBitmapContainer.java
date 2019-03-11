/**
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package org.roaringbitmap;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBitmapContainer {
    @Test
    public void testToString() {
        BitmapContainer bc2 = new BitmapContainer(5, 15);
        bc2.add(((short) (-19)));
        bc2.add(((short) (-3)));
        Assert.assertEquals("{5,6,7,8,9,10,11,12,13,14,65517,65533}", bc2.toString());
    }

    @Test
    public void testXOR() {
        BitmapContainer bc = new BitmapContainer(100, 10000);
        BitmapContainer bc2 = new BitmapContainer();
        BitmapContainer bc3 = new BitmapContainer();
        for (int i = 100; i < 10000; ++i) {
            if ((i % 2) == 0)
                bc2 = ((BitmapContainer) (bc2.add(((short) (i)))));
            else
                bc3 = ((BitmapContainer) (bc3.add(((short) (i)))));

        }
        bc = ((BitmapContainer) (bc.ixor(bc2)));
        Assert.assertTrue(((bc.ixor(bc3).getCardinality()) == 0));
    }

    @Test
    public void testANDNOT() {
        BitmapContainer bc = new BitmapContainer(100, 10000);
        BitmapContainer bc2 = new BitmapContainer();
        BitmapContainer bc3 = new BitmapContainer();
        for (int i = 100; i < 10000; ++i) {
            if ((i % 2) == 0)
                bc2 = ((BitmapContainer) (bc2.add(((short) (i)))));
            else
                bc3 = ((BitmapContainer) (bc3.add(((short) (i)))));

        }
        RunContainer rc = new RunContainer();
        rc.iadd(0, (1 << 16));
        bc = ((BitmapContainer) (bc.iand(rc)));
        bc = ((BitmapContainer) (bc.iandNot(bc2)));
        Assert.assertTrue(bc.equals(bc3));
        Assert.assertTrue(((bc.hashCode()) == (bc3.hashCode())));
        Assert.assertTrue(((bc.iandNot(bc3).getCardinality()) == 0));
        bc3.clear();
        Assert.assertTrue(((bc3.getCardinality()) == 0));
    }

    @Test
    public void testAND() {
        BitmapContainer bc = new BitmapContainer(100, 10000);
        BitmapContainer bc2 = new BitmapContainer();
        BitmapContainer bc3 = new BitmapContainer();
        for (int i = 100; i < 10000; ++i) {
            if ((i % 2) == 0)
                bc2 = ((BitmapContainer) (bc2.add(((short) (i)))));
            else
                bc3 = ((BitmapContainer) (bc3.add(((short) (i)))));

        }
        bc = ((BitmapContainer) (bc.iand(bc2)));
        Assert.assertTrue(bc.equals(bc2));
        Assert.assertTrue(((bc.iand(bc3).getCardinality()) == 0));
    }

    @Test
    public void testOR() {
        BitmapContainer bc = new BitmapContainer(100, 10000);
        BitmapContainer bc2 = new BitmapContainer();
        BitmapContainer bc3 = new BitmapContainer();
        for (int i = 100; i < 10000; ++i) {
            if ((i % 2) == 0)
                bc2 = ((BitmapContainer) (bc2.add(((short) (i)))));
            else
                bc3 = ((BitmapContainer) (bc3.add(((short) (i)))));

        }
        bc2 = ((BitmapContainer) (bc2.ior(bc3)));
        Assert.assertTrue(bc.equals(bc2));
        bc2 = ((BitmapContainer) (bc2.ior(bc)));
        Assert.assertTrue(bc.equals(bc2));
        RunContainer rc = new RunContainer();
        rc.iadd(0, (1 << 16));
        Assert.assertEquals(0, bc.iandNot(rc).getCardinality());
    }

    @Test
    public void testLazyORFull() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        BitmapContainer bc2 = new BitmapContainer(3210, (1 << 16));
        Container result = bc.lazyor(bc2);
        Container iresult = bc.ilazyor(bc2);
        Assert.assertEquals((-1), result.getCardinality());
        Assert.assertEquals((-1), iresult.getCardinality());
        Container repaired = result.repairAfterLazy();
        Container irepaired = iresult.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertEquals((1 << 16), irepaired.getCardinality());
        Assert.assertThat(repaired, CoreMatchers.instanceOf(RunContainer.class));
        Assert.assertThat(irepaired, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void testLazyORFull2() {
        BitmapContainer bc = new BitmapContainer(((1 << 10) - 200), (1 << 16));
        ArrayContainer ac = new ArrayContainer(0, (1 << 10));
        Container result = bc.lazyor(ac);
        Container iresult = bc.ilazyor(ac);
        Assert.assertEquals((-1), result.getCardinality());
        Assert.assertEquals((-1), iresult.getCardinality());
        Container repaired = result.repairAfterLazy();
        Container irepaired = iresult.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertEquals((1 << 16), irepaired.getCardinality());
        Assert.assertThat(repaired, CoreMatchers.instanceOf(RunContainer.class));
        Assert.assertThat(irepaired, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void testLazyORFull3() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        Container rc = Container.rangeOfOnes((1 << 15), (1 << 16));
        Container result = bc.lazyor(((RunContainer) (rc)));
        Container iresult = bc.ilazyor(((RunContainer) (rc)));
        Assert.assertEquals((-1), result.getCardinality());
        Assert.assertEquals((-1), iresult.getCardinality());
        Container repaired = result.repairAfterLazy();
        Container irepaired = iresult.repairAfterLazy();
        Assert.assertEquals((1 << 16), repaired.getCardinality());
        Assert.assertEquals((1 << 16), irepaired.getCardinality());
        Assert.assertThat(repaired, CoreMatchers.instanceOf(RunContainer.class));
        Assert.assertThat(irepaired, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void runConstructorForBitmap() {
        System.out.println("runConstructorForBitmap");
        for (int start = 0; start <= (1 << 16); start += 4096) {
            for (int end = start; end <= (1 << 16); end += 4096) {
                BitmapContainer bc = new BitmapContainer(start, end);
                BitmapContainer bc2 = new BitmapContainer();
                BitmapContainer bc3 = ((BitmapContainer) (bc2.add(start, end)));
                bc2 = ((BitmapContainer) (bc2.iadd(start, end)));
                Assert.assertEquals(bc.getCardinality(), (end - start));
                Assert.assertEquals(bc2.getCardinality(), (end - start));
                Assert.assertEquals(bc, bc2);
                Assert.assertEquals(bc, bc3);
                Assert.assertEquals(0, bc2.remove(start, end).getCardinality());
                Assert.assertEquals(bc2.getCardinality(), (end - start));
                Assert.assertEquals(0, bc2.not(start, end).getCardinality());
            }
        }
    }

    @Test
    public void runConstructorForBitmap2() {
        System.out.println("runConstructorForBitmap2");
        for (int start = 0; start <= (1 << 16); start += 63) {
            for (int end = start; end <= (1 << 16); end += 63) {
                BitmapContainer bc = new BitmapContainer(start, end);
                BitmapContainer bc2 = new BitmapContainer();
                BitmapContainer bc3 = ((BitmapContainer) (bc2.add(start, end)));
                bc2 = ((BitmapContainer) (bc2.iadd(start, end)));
                Assert.assertEquals(bc.getCardinality(), (end - start));
                Assert.assertEquals(bc2.getCardinality(), (end - start));
                Assert.assertEquals(bc, bc2);
                Assert.assertEquals(bc, bc3);
                Assert.assertEquals(0, bc2.remove(start, end).getCardinality());
                Assert.assertEquals(bc2.getCardinality(), (end - start));
                Assert.assertEquals(0, bc2.not(start, end).getCardinality());
            }
        }
    }

    @Test
    public void testRangeCardinality() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        bc = ((BitmapContainer) (bc.add(200, 2000)));
        Assert.assertEquals(8280, bc.cardinality);
    }

    @Test
    public void testRangeCardinality2() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        bc.iadd(200, 2000);
        Assert.assertEquals(8280, bc.cardinality);
    }

    @Test
    public void testRangeCardinality3() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        bc.ior(rc);
        Assert.assertEquals(8677, bc.cardinality);
    }

    @Test
    public void testRangeCardinality4() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        bc = ((BitmapContainer) (bc.andNot(rc)));
        Assert.assertEquals(5274, bc.cardinality);
    }

    @Test
    public void testRangeCardinality5() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        bc.iandNot(rc);
        Assert.assertEquals(5274, bc.cardinality);
    }

    @Test
    public void testRangeCardinality6() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 5200 }, 3);
        bc = ((BitmapContainer) (bc.iand(rc)));
        Assert.assertEquals(5046, bc.cardinality);
    }

    @Test
    public void testRangeCardinality7() {
        BitmapContainer bc = TestBitmapContainer.generateContainer(((short) (100)), ((short) (10000)), 5);
        RunContainer rc = new RunContainer(new short[]{ 7, 300, 400, 900, 1400, 2200 }, 3);
        bc.ixor(rc);
        Assert.assertEquals(6031, bc.cardinality);
    }

    @Test
    public void numberOfRunsLowerBound1() {
        System.out.println("numberOfRunsLowerBound1");
        Random r = new Random(12345);
        for (double density = 0.001; density < 0.8; density *= 2) {
            ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i = 0; i < 65536; ++i) {
                if ((r.nextDouble()) < density) {
                    values.add(i);
                }
            }
            Integer[] positions = values.toArray(new Integer[0]);
            BitmapContainer bc = new BitmapContainer();
            for (int position : positions) {
                bc.add(((short) (position)));
            }
            Assert.assertTrue(((bc.numberOfRunsLowerBound(1)) > 1));
            Assert.assertTrue(((bc.numberOfRunsLowerBound(100)) <= (bc.numberOfRuns())));
            // a big parameter like 100000 ensures that the full lower bound
            // is taken
            Assert.assertTrue(((bc.numberOfRunsLowerBound(100000)) <= (bc.numberOfRuns())));
            Assert.assertEquals(bc.numberOfRuns(), ((bc.numberOfRunsLowerBound(100000)) + (bc.numberOfRunsAdjustment())));
            /* the unrolled guys are commented out, did not help performance and slated for removal
            soon...

            assertTrue(bc.numberOfRunsLowerBoundUnrolled2(1) > 1);
            assertTrue(bc.numberOfRunsLowerBoundUnrolled2(100) <= bc.numberOfRuns());

            assertEquals(bc.numberOfRunsLowerBound(100000),
            bc.numberOfRunsLowerBoundUnrolled2(100000));
             */
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testNextTooLarge() {
        TestBitmapContainer.emptyContainer().nextSetBit(((Short.MAX_VALUE) + 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testNextTooSmall() {
        TestBitmapContainer.emptyContainer().nextSetBit((-1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPreviousTooLarge() {
        TestBitmapContainer.emptyContainer().prevSetBit(((Short.MAX_VALUE) + 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPreviousTooSmall() {
        TestBitmapContainer.emptyContainer().prevSetBit((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addInvalidRange() {
        Container bc = new BitmapContainer();
        bc.add(13, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void iaddInvalidRange() {
        Container bc = new BitmapContainer();
        bc.iadd(13, 1);
    }

    @Test
    public void roundtrip() throws Exception {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 5);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oo = new ObjectOutputStream(bos)) {
            bc.writeExternal(oo);
        }
        Container bc2 = new BitmapContainer();
        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        bc2.readExternal(new ObjectInputStream(bis));
        Assert.assertEquals(4, bc2.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(bc2.contains(((short) (i))));
        }
    }

    @Test
    public void iorRun() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 5);
        Container rc = new RunContainer();
        rc = rc.add(4, 10);
        bc.ior(rc);
        Assert.assertEquals(9, bc.getCardinality());
        for (int i = 1; i < 10; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test
    public void orFullToRunContainer() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        BitmapContainer half = new BitmapContainer((1 << 15), (1 << 16));
        Container result = bc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertThat(result, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void orFullToRunContainer2() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        ArrayContainer half = new ArrayContainer((1 << 15), (1 << 16));
        Container result = bc.or(half);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertThat(result, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void orFullToRunContainer3() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        BitmapContainer bc2 = new BitmapContainer(3210, (1 << 16));
        Container result = bc.or(bc2);
        Container iresult = bc.ior(bc2);
        Assert.assertEquals((1 << 16), result.getCardinality());
        Assert.assertEquals((1 << 16), iresult.getCardinality());
        Assert.assertThat(result, CoreMatchers.instanceOf(RunContainer.class));
        Assert.assertThat(iresult, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void orFullToRunContainer4() {
        BitmapContainer bc = new BitmapContainer(0, (1 << 15));
        Container bc2 = Container.rangeOfOnes(3210, (1 << 16));
        Container iresult = bc.ior(bc2);
        Assert.assertEquals((1 << 16), iresult.getCardinality());
        Assert.assertThat(iresult, CoreMatchers.instanceOf(RunContainer.class));
    }

    @Test
    public void iremoveEmptyRange() {
        Container bc = new BitmapContainer();
        bc = bc.iremove(1, 1);
        Assert.assertEquals(0, bc.getCardinality());
    }

    @Test(expected = IllegalArgumentException.class)
    public void iremoveInvalidRange() {
        Container ac = new BitmapContainer();
        ac.iremove(13, 1);
    }

    @Test
    public void iremove() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 10);
        bc = bc.iremove(5, 10);
        Assert.assertEquals(4, bc.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test
    public void iremove2() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 8092);
        bc = bc.iremove(1, 10);
        Assert.assertEquals(8082, bc.getCardinality());
        for (int i = 10; i < 8092; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test
    public void ixorRun() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 10);
        Container rc = new RunContainer();
        rc = rc.add(5, 15);
        bc = bc.ixor(rc);
        Assert.assertEquals(9, bc.getCardinality());
        for (int i = 1; i < 5; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
        for (int i = 10; i < 15; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test
    public void ixorRun2() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 8092);
        Container rc = new RunContainer();
        rc = rc.add(1, 10);
        bc = bc.ixor(rc);
        Assert.assertEquals(8082, bc.getCardinality());
        for (int i = 10; i < 8092; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectInvalidPosition() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 13);
        bc.select(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeInvalidRange() {
        Container ac = new BitmapContainer();
        ac.remove(13, 1);
    }

    @Test
    public void remove() {
        Container bc = new BitmapContainer();
        bc = bc.add(1, 8092);
        bc = bc.remove(1, 10);
        Assert.assertEquals(8082, bc.getCardinality());
        for (int i = 10; i < 8092; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test
    public void iandRun() {
        Container bc = new BitmapContainer();
        bc = bc.add(0, 8092);
        Container rc = new RunContainer();
        rc = rc.add(1, 10);
        bc = bc.iand(rc);
        Assert.assertEquals(9, bc.getCardinality());
        for (int i = 1; i < 10; i++) {
            Assert.assertTrue(bc.contains(((short) (i))));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirst_Empty() {
        new BitmapContainer().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLast_Empty() {
        new BitmapContainer().last();
    }

    @Test
    public void testFirstLast() {
        Container rc = new ArrayContainer();
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
    public void testContainsBitmapContainer_EmptyContainsEmpty() {
        Container bc = new BitmapContainer();
        Container subset = new BitmapContainer();
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeProperSubset() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new BitmapContainer().add(0, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeSelf() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new BitmapContainer().add(0, 10);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeSuperSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container superset = new BitmapContainer().add(0, 20);
        Assert.assertFalse(bc.contains(superset));
    }

    @Test
    public void testContainsBitmapContainer_IncludeProperSubsetDifferentStart() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new RunContainer().add(2, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeShiftedSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container shifted = new BitmapContainer().add(2, 12);
        Assert.assertFalse(bc.contains(shifted));
    }

    @Test
    public void testContainsBitmapContainer_ExcludeDisJointSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container disjoint = new BitmapContainer().add(20, 40);
        Assert.assertFalse(bc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(bc));
    }

    @Test
    public void testContainsRunContainer_EmptyContainsEmpty() {
        Container bc = new BitmapContainer();
        Container subset = new BitmapContainer();
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_IncludeProperSubset() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new RunContainer().add(0, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_IncludeSelf() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new RunContainer().add(0, 10);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_ExcludeSuperSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container superset = new RunContainer().add(0, 20);
        Assert.assertFalse(bc.contains(superset));
    }

    @Test
    public void testContainsRunContainer_IncludeProperSubsetDifferentStart() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new RunContainer().add(2, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsRunContainer_ExcludeShiftedSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container shifted = new RunContainer().add(2, 12);
        Assert.assertFalse(bc.contains(shifted));
    }

    @Test
    public void testContainsRunContainer_ExcludeDisJointSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container disjoint = new RunContainer().add(20, 40);
        Assert.assertFalse(bc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(bc));
    }

    @Test
    public void testContainsArrayContainer_EmptyContainsEmpty() {
        Container bc = new BitmapContainer();
        Container subset = new ArrayContainer();
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_IncludeProperSubset() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new ArrayContainer().add(0, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_IncludeSelf() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new ArrayContainer().add(0, 10);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_ExcludeSuperSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container superset = new ArrayContainer().add(0, 20);
        Assert.assertFalse(bc.contains(superset));
    }

    @Test
    public void testContainsArrayContainer_IncludeProperSubsetDifferentStart() {
        Container bc = new BitmapContainer().add(0, 10);
        Container subset = new ArrayContainer().add(2, 9);
        Assert.assertTrue(bc.contains(subset));
    }

    @Test
    public void testContainsArrayContainer_ExcludeShiftedSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container shifted = new ArrayContainer().add(2, 12);
        Assert.assertFalse(bc.contains(shifted));
    }

    @Test
    public void testContainsArrayContainer_ExcludeDisJointSet() {
        Container bc = new BitmapContainer().add(0, 10);
        Container disjoint = new ArrayContainer().add(20, 40);
        Assert.assertFalse(bc.contains(disjoint));
        Assert.assertFalse(disjoint.contains(bc));
    }

    @Test
    public void testIntersectsWithRange() {
        Container container = new BitmapContainer().add(0, 10);
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, (1 << 16)));
        Assert.assertFalse(container.intersects(11, TestBitmapContainer.lower16Bits((-1))));
    }

    @Test
    public void testIntersectsWithRangeHitScan() {
        Container container = new BitmapContainer().add(0, 10).add(500, 512).add(TestBitmapContainer.lower16Bits((-50)), TestBitmapContainer.lower16Bits((-10)));
        Assert.assertTrue(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, 101));
        Assert.assertTrue(container.intersects(0, (1 << 16)));
        Assert.assertTrue(container.intersects(11, (1 << 16)));
        Assert.assertTrue(container.intersects(501, 511));
    }

    @Test
    public void testIntersectsWithRangeUnsigned() {
        Container container = new BitmapContainer().add(TestBitmapContainer.lower16Bits((-50)), TestBitmapContainer.lower16Bits((-10)));
        Assert.assertFalse(container.intersects(0, 1));
        Assert.assertTrue(container.intersects(0, TestBitmapContainer.lower16Bits((-40))));
        Assert.assertFalse(container.intersects(TestBitmapContainer.lower16Bits((-100)), TestBitmapContainer.lower16Bits((-55))));
        Assert.assertFalse(container.intersects(TestBitmapContainer.lower16Bits((-9)), TestBitmapContainer.lower16Bits((-1))));
        Assert.assertTrue(container.intersects(11, ((short) (-1))));
    }

    @Test
    public void testIntersectsAtEndWord() {
        Container container = new BitmapContainer().add(TestBitmapContainer.lower16Bits((-500)), TestBitmapContainer.lower16Bits((-10)));
        Assert.assertTrue(container.intersects(TestBitmapContainer.lower16Bits((-50)), TestBitmapContainer.lower16Bits((-10))));
        Assert.assertTrue(container.intersects(TestBitmapContainer.lower16Bits((-400)), TestBitmapContainer.lower16Bits((-11))));
        Assert.assertTrue(container.intersects(TestBitmapContainer.lower16Bits((-11)), TestBitmapContainer.lower16Bits((-1))));
        Assert.assertFalse(container.intersects(TestBitmapContainer.lower16Bits((-10)), TestBitmapContainer.lower16Bits((-1))));
    }

    @Test
    public void testIntersectsAtEndWord2() {
        Container container = new BitmapContainer().add(TestBitmapContainer.lower16Bits(500), TestBitmapContainer.lower16Bits((-500)));
        Assert.assertTrue(container.intersects(TestBitmapContainer.lower16Bits((-650)), TestBitmapContainer.lower16Bits((-500))));
        Assert.assertTrue(container.intersects(TestBitmapContainer.lower16Bits((-501)), TestBitmapContainer.lower16Bits((-1))));
        Assert.assertFalse(container.intersects(TestBitmapContainer.lower16Bits((-500)), TestBitmapContainer.lower16Bits((-1))));
        Assert.assertFalse(container.intersects(TestBitmapContainer.lower16Bits((-499)), (1 << 16)));
    }

    @Test
    public void testContainsRangeSingleWord() {
        long[] bitmap = TestBitmapContainer.evenBits();
        bitmap[10] = -1L;
        int cardinality = (32 + 1) << 15;
        BitmapContainer container = new BitmapContainer(bitmap, cardinality);
        Assert.assertTrue(container.contains(0, 1));
        Assert.assertTrue(container.contains((64 * 10), (64 * 11)));
        Assert.assertFalse(container.contains((64 * 10), (2 + (64 * 11))));
        Assert.assertTrue(container.contains((1 + (64 * 10)), ((64 * 11) - 1)));
    }

    @Test
    public void testContainsRangeMultiWord() {
        long[] bitmap = TestBitmapContainer.evenBits();
        bitmap[10] = -1L;
        bitmap[11] = -1L;
        bitmap[12] |= (1L << 32) - 1;
        int cardinality = (((32 + 32) + 16) + 1) << 15;
        BitmapContainer container = new BitmapContainer(bitmap, cardinality);
        Assert.assertTrue(container.contains(0, 1));
        Assert.assertFalse(container.contains((64 * 10), ((64 * 13) - 30)));
        Assert.assertTrue(container.contains((64 * 10), ((64 * 13) - 31)));
        Assert.assertTrue(container.contains((1 + (64 * 10)), ((64 * 13) - 32)));
        Assert.assertTrue(container.contains((64 * 10), (64 * 12)));
        Assert.assertFalse(container.contains((64 * 10), (2 + (64 * 13))));
    }

    @Test
    public void testContainsRangeSubWord() {
        long[] bitmap = TestBitmapContainer.evenBits();
        bitmap[((bitmap.length) - 1)] = ~((1L << 63) | 1L);
        int cardinality = (((32 + 32) + 16) + 1) << 15;
        BitmapContainer container = new BitmapContainer(bitmap, cardinality);
        Assert.assertFalse(container.contains((64 * 1023), (64 * 1024)));
        Assert.assertFalse(container.contains((64 * 1023), ((64 * 1024) - 1)));
        Assert.assertTrue(container.contains((1 + (64 * 1023)), ((64 * 1024) - 1)));
        Assert.assertTrue(container.contains((1 + (64 * 1023)), ((64 * 1024) - 2)));
        Assert.assertFalse(container.contains((64 * 1023), ((64 * 1023) + 2)));
        Assert.assertTrue(container.contains(((64 * 1023) + 1), ((64 * 1023) + 2)));
    }

    @Test
    public void testNextSetBit() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        Assert.assertEquals(0, container.nextSetBit(0));
        Assert.assertEquals(2, container.nextSetBit(1));
        Assert.assertEquals(2, container.nextSetBit(2));
        Assert.assertEquals(4, container.nextSetBit(3));
    }

    @Test
    public void testNextSetBitAfterEnd() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        container.bitmap[1023] = 0L;
        container.cardinality -= 32;
        Assert.assertEquals((-1), container.nextSetBit(((64 * 1023) + 5)));
    }

    @Test
    public void testNextSetBitBeforeStart() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        container.bitmap[0] = 0L;
        container.cardinality -= 32;
        Assert.assertEquals(64, container.nextSetBit(1));
    }

    @Test
    public void testNextValue() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals(10, container.nextValue(((short) (10))));
        Assert.assertEquals(20, container.nextValue(((short) (11))));
        Assert.assertEquals(30, container.nextValue(((short) (30))));
    }

    @Test
    public void testNextValueAfterEnd() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals((-1), container.nextValue(((short) (31))));
    }

    @Test
    public void testNextValue2() {
        BitmapContainer container = new BitmapContainer().iadd(64, 129).toBitmapContainer();
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals((-1), container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (5000))));
    }

    @Test
    public void testNextValueBetweenRuns() {
        BitmapContainer container = new BitmapContainer().iadd(64, 129).iadd(256, 321).toBitmapContainer();
        Assert.assertEquals(64, container.nextValue(((short) (0))));
        Assert.assertEquals(64, container.nextValue(((short) (64))));
        Assert.assertEquals(65, container.nextValue(((short) (65))));
        Assert.assertEquals(128, container.nextValue(((short) (128))));
        Assert.assertEquals(256, container.nextValue(((short) (129))));
        Assert.assertEquals((-1), container.nextValue(((short) (512))));
    }

    @Test
    public void testNextValue3() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201).toBitmapContainer();
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
    public void testNextValueUnsigned() {
        BitmapContainer container = new ArrayContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }).toBitmapContainer();
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.nextValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.nextValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals((-1), container.nextValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testPreviousValue1() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).toBitmapContainer();
        Assert.assertEquals((-1), container.previousValue(((short) (0))));
        Assert.assertEquals((-1), container.previousValue(((short) (63))));
        Assert.assertEquals(64, container.previousValue(((short) (64))));
        Assert.assertEquals(65, container.previousValue(((short) (65))));
        Assert.assertEquals(128, container.previousValue(((short) (128))));
        Assert.assertEquals(128, container.previousValue(((short) (129))));
    }

    @Test
    public void testPreviousValue2() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201).toBitmapContainer();
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
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals((-1), container.previousValue(((short) (5))));
    }

    @Test
    public void testPreviousValueSparse() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals((-1), container.previousValue(((short) (9))));
        Assert.assertEquals(10, container.previousValue(((short) (10))));
        Assert.assertEquals(10, container.previousValue(((short) (11))));
        Assert.assertEquals(20, container.previousValue(((short) (21))));
        Assert.assertEquals(30, container.previousValue(((short) (30))));
    }

    @Test
    public void testPreviousValueAfterEnd() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals(30, container.previousValue(((short) (31))));
    }

    @Test
    public void testPreviousEvenBits() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        Assert.assertEquals(0, container.previousValue(((short) (0))));
        Assert.assertEquals(0, container.previousValue(((short) (1))));
        Assert.assertEquals(2, container.previousValue(((short) (2))));
        Assert.assertEquals(2, container.previousValue(((short) (3))));
    }

    @Test
    public void testPreviousValueUnsigned() {
        BitmapContainer container = new ArrayContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }).toBitmapContainer();
        Assert.assertEquals((-1), container.previousValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 5), container.previousValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 7), container.previousValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testPreviousAbsentValue1() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).toBitmapContainer();
        Assert.assertEquals(0, container.previousAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (63))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (64))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (65))));
        Assert.assertEquals(63, container.previousAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.previousAbsentValue(((short) (129))));
    }

    @Test
    public void testPreviousAbsentValue2() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201).toBitmapContainer();
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
        BitmapContainer container = new ArrayContainer().toBitmapContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.previousAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testPreviousAbsentValueSparse() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals(9, container.previousAbsentValue(((short) (9))));
        Assert.assertEquals(9, container.previousAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.previousAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.previousAbsentValue(((short) (21))));
        Assert.assertEquals(29, container.previousAbsentValue(((short) (30))));
    }

    @Test
    public void testPreviousAbsentEvenBits() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i - 1), container.previousAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.previousAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testPreviousAbsentValueUnsigned() {
        BitmapContainer container = new ArrayContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }).toBitmapContainer();
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 4), container.previousAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 6), container.previousAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.previousAbsentValue(((short) ((1 << 15) | 8))));
    }

    @Test
    public void testNextAbsentValue1() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).toBitmapContainer();
        Assert.assertEquals(0, container.nextAbsentValue(((short) (0))));
        Assert.assertEquals(63, container.nextAbsentValue(((short) (63))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (64))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (65))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (128))));
        Assert.assertEquals(129, container.nextAbsentValue(((short) (129))));
    }

    @Test
    public void testNextAbsentValue2() {
        BitmapContainer container = new ArrayContainer().iadd(64, 129).iadd(200, 501).iadd(5000, 5201).toBitmapContainer();
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
        BitmapContainer container = new ArrayContainer().toBitmapContainer();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(i, container.nextAbsentValue(((short) (i))));
        }
    }

    @Test
    public void testNextAbsentValueSparse() {
        BitmapContainer container = new ArrayContainer(new short[]{ 10, 20, 30 }).toBitmapContainer();
        Assert.assertEquals(9, container.nextAbsentValue(((short) (9))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (10))));
        Assert.assertEquals(11, container.nextAbsentValue(((short) (11))));
        Assert.assertEquals(21, container.nextAbsentValue(((short) (21))));
        Assert.assertEquals(31, container.nextAbsentValue(((short) (30))));
    }

    @Test
    public void testNextAbsentEvenBits() {
        BitmapContainer container = new BitmapContainer(TestBitmapContainer.evenBits(), (1 << 15));
        for (int i = 0; i < (1 << 10); i += 2) {
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i))));
            Assert.assertEquals((i + 1), container.nextAbsentValue(((short) (i + 1))));
        }
    }

    @Test
    public void testNextAbsentValueUnsigned() {
        BitmapContainer container = new ArrayContainer(new short[]{ ((short) ((1 << 15) | 5)), ((short) ((1 << 15) | 7)) }).toBitmapContainer();
        Assert.assertEquals(((1 << 15) | 4), container.nextAbsentValue(((short) ((1 << 15) | 4))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 5))));
        Assert.assertEquals(((1 << 15) | 6), container.nextAbsentValue(((short) ((1 << 15) | 6))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 7))));
        Assert.assertEquals(((1 << 15) | 8), container.nextAbsentValue(((short) ((1 << 15) | 8))));
    }
}

