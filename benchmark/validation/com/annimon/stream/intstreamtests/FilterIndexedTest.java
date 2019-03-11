package com.annimon.stream.intstreamtests;


import IndexedIntPredicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.function.IndexedIntPredicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterIndexedTest {
    @Test
    public void testFilterIndexed() {
        IntStream.rangeClosed(4, 8).filterIndexed(new IndexedIntPredicate() {
            @Override
            public boolean test(int index, int value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.arrayContaining(4, 6, 8)));
    }

    @Test
    public void testFilterIndexedWithStartAndStep() {
        IntStream.rangeClosed(4, 8).filterIndexed(20, (-5), new IndexedIntPredicate() {
            @Override
            public boolean test(int index, int value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.arrayContaining(4, 6, 8)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIndexedIteratorNextOnEmpty() {
        IntStream.empty().filterIndexed(Util.wrap(Functions.remainderInt(2))).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilterIndexedIteratorRemove() {
        IntStream.range(0, 10).filterIndexed(Util.wrap(Functions.remainderInt(2))).iterator().remove();
    }
}

