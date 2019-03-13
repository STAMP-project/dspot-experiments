package org.roaringbitmap;


import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


public class BufferFuzzer {
    @FunctionalInterface
    interface IntBitmapPredicate {
        boolean test(int index, MutableRoaringBitmap bitmap);
    }

    @FunctionalInterface
    interface RangeBitmapPredicate {
        boolean test(long min, long max, ImmutableRoaringBitmap bitmap);
    }

    @Test
    public void rankSelectInvariance() {
        BufferFuzzer.verifyInvariance("rankSelectInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( i, rb) -> (rb.rank(rb.select(i))) == (i + 1));
    }

    @Test
    public void selectContainsInvariance() {
        BufferFuzzer.verifyInvariance("selectContainsInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( i, rb) -> rb.contains(rb.select(i)));
    }

    @Test
    public void firstSelect0Invariance() {
        BufferFuzzer.verifyInvariance("firstSelect0Invariance", ( bitmap) -> !(bitmap.isEmpty()), ( bitmap) -> bitmap.first(), ( bitmap) -> bitmap.select(0));
    }

    @Test
    public void lastSelectCardinalityInvariance() {
        BufferFuzzer.verifyInvariance("lastSelectCardinalityInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( bitmap) -> bitmap.last(), ( bitmap) -> bitmap.select(((bitmap.getCardinality()) - 1)));
    }

    @Test
    public void intersectsRangeFirstLastInvariance() {
        BufferFuzzer.verifyInvariance("intersectsRangeFirstLastInvariance", true, ( rb) -> rb.intersects(Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())));
    }

    @Test
    public void containsRangeFirstLastInvariance() {
        BufferFuzzer.verifyInvariance("containsRangeFirstLastInvariance", true, ( rb) -> MutableRoaringBitmap.add(rb.clone(), Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())).contains(Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())));
    }

    @Test
    public void andCardinalityInvariance() {
        BufferFuzzer.verifyInvariance("andCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> MutableRoaringBitmap.and(l, r).getCardinality(), ( l, r) -> MutableRoaringBitmap.andCardinality(l, r));
    }

    @Test
    public void orCardinalityInvariance() {
        BufferFuzzer.verifyInvariance("orCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> MutableRoaringBitmap.or(l, r).getCardinality(), ( l, r) -> MutableRoaringBitmap.orCardinality(l, r));
    }

    @Test
    public void xorCardinalityInvariance() {
        BufferFuzzer.verifyInvariance("xorCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> MutableRoaringBitmap.xor(l, r).getCardinality(), ( l, r) -> MutableRoaringBitmap.xorCardinality(l, r));
    }

    @Test
    public void containsContainsInvariance() {
        BufferFuzzer.verifyInvariance("containsContainsInvariance", ( l, r) -> (l.contains(r)) && (!(r.equals(l))), ( l, r) -> false, ( l, r) -> !(r.contains(l)));
    }

    @Test
    public void containsAndInvariance() {
        BufferFuzzer.verifyInvariance("containsAndInvariance", ( l, r) -> l.contains(r), ( l, r) -> MutableRoaringBitmap.and(l, r).equals(r));
    }

    @Test
    public void andCardinalityContainsInvariance() {
        BufferFuzzer.verifyInvariance("andCardinalityContainsInvariance", ( l, r) -> (MutableRoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> false, ( l, r) -> (l.contains(r)) || (r.contains(l)));
    }

    @Test
    public void sizeOfUnionOfDisjointSetsEqualsSumOfSizes() {
        BufferFuzzer.verifyInvariance("sizeOfUnionOfDisjointSetsEqualsSumOfSizes", ( l, r) -> (MutableRoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> (l.getCardinality()) + (r.getCardinality()), ( l, r) -> MutableRoaringBitmap.orCardinality(l, r));
    }

    @Test
    public void sizeOfDifferenceOfDisjointSetsEqualsSumOfSizes() {
        BufferFuzzer.verifyInvariance("sizeOfDifferenceOfDisjointSetsEqualsSumOfSizes", ( l, r) -> (MutableRoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> (l.getCardinality()) + (r.getCardinality()), ( l, r) -> MutableRoaringBitmap.xorCardinality(l, r));
    }

    @Test
    public void equalsSymmetryInvariance() {
        BufferFuzzer.verifyInvariance("equalsSymmetryInvariance", ( l, r) -> l.equals(r), ( l, r) -> r.equals(l));
    }

    @Test
    public void orOfDisjunction() {
        BufferFuzzer.verifyInvariance("orOfDisjunction", RandomisedTestData.ITERATIONS, (1 << 8), ( l, r) -> l, ( l, r) -> MutableRoaringBitmap.or(l, MutableRoaringBitmap.and(l, r)));
    }

    @Test
    public void orCoversXor() {
        BufferFuzzer.verifyInvariance("orCoversXor", RandomisedTestData.ITERATIONS, (1 << 8), ( l, r) -> MutableRoaringBitmap.or(l, r), ( l, r) -> MutableRoaringBitmap.or(l, MutableRoaringBitmap.xor(l, r)));
    }

    @Test
    public void xorInvariance() {
        BufferFuzzer.verifyInvariance("xorInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> MutableRoaringBitmap.xor(l, r), ( l, r) -> MutableRoaringBitmap.andNot(MutableRoaringBitmap.or(l, r), MutableRoaringBitmap.and(l, r)));
    }

    @Test
    public void rangeCardinalityVsMaterialisedRange() {
        BufferFuzzer.verifyInvariance("rangeCardinalityVsMaterialisedRange", (1 << 9), ( min, max, bitmap) -> {
            MutableRoaringBitmap range = new MutableRoaringBitmap();
            range.add(min, max);
            return (bitmap.rangeCardinality(min, max)) == (ImmutableRoaringBitmap.andCardinality(range, bitmap));
        });
    }

    @Test
    public void absentValuesConsistentWithBitSet() {
        List<Integer> offsets = Arrays.asList(0, 1, (-1), 10, (-10), 100, (-100));
        // Size limit to avoid out of memory errors; r.last() > 0 to avoid bitmaps with last > Integer.MAX_VALUE
        BufferFuzzer.verifyInvariance(( r) -> (r.isEmpty()) || (((r.last()) > 0) && ((r.last()) < (1 << 30))), ( bitmap) -> {
            BitSet reference = new BitSet();
            bitmap.iterator().forEachRemaining(reference::set);
            for (int next : bitmap) {
                for (int offset : offsets) {
                    int pos = next + offset;
                    if (pos >= 0) {
                        Assert.assertEquals(reference.nextClearBit(pos), bitmap.nextAbsentValue(pos));
                        Assert.assertEquals(reference.previousClearBit(pos), bitmap.previousAbsentValue(pos));
                    }
                }
            }
        });
    }
}

