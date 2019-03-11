package com.annimon.stream.longstreamtests;


import IndexedLongPredicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import com.annimon.stream.function.IndexedLongPredicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterIndexedTest {
    @Test
    public void testFilterIndexed() {
        LongStream.rangeClosed(4, 8).filterIndexed(new IndexedLongPredicate() {
            @Override
            public boolean test(int index, long value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.arrayContaining(4L, 6L, 8L)));
    }

    @Test
    public void testFilterIndexedWithStartAndStep() {
        LongStream.rangeClosed(4, 8).filterIndexed(20, (-5), new IndexedLongPredicate() {
            @Override
            public boolean test(int index, long value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.arrayContaining(4L, 6L, 8L)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIndexedIteratorNextOnEmpty() {
        LongStream.empty().filterIndexed(Util.wrap(Functions.remainderLong(2))).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilterIndexedIteratorRemove() {
        LongStream.range(0, 10).filterIndexed(Util.wrap(Functions.remainderLong(2))).iterator().remove();
    }
}

