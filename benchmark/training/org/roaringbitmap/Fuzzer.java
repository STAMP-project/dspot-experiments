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


public class Fuzzer {
    @FunctionalInterface
    interface IntBitmapPredicate {
        boolean test(int index, RoaringBitmap bitmap);
    }

    @FunctionalInterface
    interface RangeBitmapPredicate {
        boolean test(long min, long max, RoaringBitmap bitmap);
    }

    @Test
    public void rankSelectInvariance() {
        Fuzzer.verifyInvariance("rankSelectInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( i, rb) -> (rb.rank(rb.select(i))) == (i + 1));
    }

    @Test
    public void selectContainsInvariance() {
        Fuzzer.verifyInvariance("rankSelectInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( i, rb) -> rb.contains(rb.select(i)));
    }

    @Test
    public void firstSelect0Invariance() {
        Fuzzer.verifyInvariance("rankSelectInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( bitmap) -> bitmap.first(), ( bitmap) -> bitmap.select(0));
    }

    @Test
    public void lastSelectCardinalityInvariance() {
        Fuzzer.verifyInvariance("rankSelectInvariance", ( bitmap) -> !(bitmap.isEmpty()), ( bitmap) -> bitmap.last(), ( bitmap) -> bitmap.select(((bitmap.getCardinality()) - 1)));
    }

    @Test
    public void andCardinalityInvariance() {
        Fuzzer.verifyInvariance("andCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> RoaringBitmap.and(l, r).getCardinality(), ( l, r) -> RoaringBitmap.andCardinality(l, r));
    }

    @Test
    public void orCardinalityInvariance() {
        Fuzzer.verifyInvariance("orCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> RoaringBitmap.or(l, r).getCardinality(), ( l, r) -> RoaringBitmap.orCardinality(l, r));
    }

    @Test
    public void xorCardinalityInvariance() {
        Fuzzer.verifyInvariance("xorCardinalityInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> RoaringBitmap.xor(l, r).getCardinality(), ( l, r) -> RoaringBitmap.xorCardinality(l, r));
    }

    @Test
    public void containsContainsInvariance() {
        Fuzzer.verifyInvariance("containsContainsInvariance", ( l, r) -> (l.contains(r)) && (!(r.equals(l))), ( l, r) -> false, ( l, r) -> !(r.contains(l)));
    }

    @Test
    public void containsAndInvariance() {
        Fuzzer.verifyInvariance("containsAndInvariance", ( l, r) -> l.contains(r), ( l, r) -> RoaringBitmap.and(l, r).equals(r));
    }

    @Test
    public void limitCardinalityEqualsSelf() {
        Fuzzer.verifyInvariance("limitCardinalityEqualsSelf", true, ( rb) -> rb.equals(rb.limit(rb.getCardinality())));
    }

    @Test
    public void limitCardinalityXorCardinalityInvariance() {
        Fuzzer.verifyInvariance("limitCardinalityXorCardinalityInvariance", ( rb) -> true, ( rb) -> rb.getCardinality(), ( rb) -> ((rb.getCardinality()) / 2) + (RoaringBitmap.xorCardinality(rb, rb.limit(((rb.getCardinality()) / 2)))));
    }

    @Test
    public void containsRangeFirstLastInvariance() {
        Fuzzer.verifyInvariance("containsRangeFirstLastInvariance", true, ( rb) -> RoaringBitmap.add(rb.clone(), Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())).contains(Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())));
    }

    @Test
    public void intersectsRangeFirstLastInvariance() {
        Fuzzer.verifyInvariance("intersectsRangeFirstLastInvariance", true, ( rb) -> rb.intersects(Util.toUnsignedLong(rb.first()), Util.toUnsignedLong(rb.last())));
    }

    @Test
    public void containsSelf() {
        Fuzzer.verifyInvariance("containsSelf", true, ( rb) -> rb.contains(rb.clone()));
    }

    @Test
    public void containsSubset() {
        Fuzzer.verifyInvariance("containsSubset", true, ( rb) -> rb.contains(rb.limit(((rb.getCardinality()) / 2))));
    }

    @Test
    public void andCardinalityContainsInvariance() {
        Fuzzer.verifyInvariance("andCardinalityContainsInvariance", ( l, r) -> (RoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> false, ( l, r) -> (l.contains(r)) || (r.contains(l)));
    }

    @Test
    public void sizeOfUnionOfDisjointSetsEqualsSumOfSizes() {
        Fuzzer.verifyInvariance("sizeOfUnionOfDisjointSetsEqualsSumOfSizes", ( l, r) -> (RoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> (l.getCardinality()) + (r.getCardinality()), ( l, r) -> RoaringBitmap.orCardinality(l, r));
    }

    @Test
    public void sizeOfDifferenceOfDisjointSetsEqualsSumOfSizes() {
        Fuzzer.verifyInvariance("sizeOfDifferenceOfDisjointSetsEqualsSumOfSizes", ( l, r) -> (RoaringBitmap.andCardinality(l, r)) == 0, ( l, r) -> (l.getCardinality()) + (r.getCardinality()), ( l, r) -> RoaringBitmap.xorCardinality(l, r));
    }

    @Test
    public void equalsSymmetryInvariance() {
        Fuzzer.verifyInvariance("equalsSymmetryInvariance", ( l, r) -> l.equals(r), ( l, r) -> r.equals(l));
    }

    @Test
    public void orOfDisjunction() {
        Fuzzer.verifyInvariance("orOfDisjunction", RandomisedTestData.ITERATIONS, (1 << 8), ( l, r) -> l, ( l, r) -> RoaringBitmap.or(l, RoaringBitmap.and(l, r)));
    }

    @Test
    public void orCoversXor() {
        Fuzzer.verifyInvariance("orCoversXor", RandomisedTestData.ITERATIONS, (1 << 8), ( l, r) -> RoaringBitmap.or(l, r), ( l, r) -> RoaringBitmap.or(l, RoaringBitmap.xor(l, r)));
    }

    @Test
    public void xorInvariance() {
        Fuzzer.verifyInvariance("xorInvariance", RandomisedTestData.ITERATIONS, (1 << 9), ( l, r) -> RoaringBitmap.xor(l, r), ( l, r) -> RoaringBitmap.andNot(RoaringBitmap.or(l, r), RoaringBitmap.and(l, r)));
    }

    @Test
    public void rangeCardinalityVsMaterialisedRange() {
        Fuzzer.verifyInvariance("rangeCardinalityVsMaterialisedRange", (1 << 9), ( min, max, bitmap) -> {
            RoaringBitmap range = new RoaringBitmap();
            range.add(min, max);
            return (bitmap.rangeCardinality(min, max)) == (RoaringBitmap.andCardinality(range, bitmap));
        });
    }

    @Test
    public void absentValuesConsistentWithBitSet() {
        List<Integer> offsets = Arrays.asList(0, 1, (-1), 10, (-10), 100, (-100));
        // Size limit to avoid out of memory errors; r.last() > 0 to avoid bitmaps with last > Integer.MAX_VALUE
        Fuzzer.verifyInvariance(( r) -> (r.isEmpty()) || (((r.last()) > 0) && ((r.last()) < (1 << 30))), ( bitmap) -> {
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

