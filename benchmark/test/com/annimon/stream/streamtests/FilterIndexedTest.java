package com.annimon.stream.streamtests;


import IndexedPredicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterIndexedTest {
    @Test
    public void testFilterIndexed() {
        Stream.rangeClosed(4, 8).filterIndexed(new com.annimon.stream.function.IndexedPredicate<Integer>() {
            @Override
            public boolean test(int index, Integer value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (0 * 4)
        // (1 * 5)
        // (2 * 6)
        // (3 * 7)
        // (4 * 8)
        Matchers.contains(4, 6, 8)));
    }

    @Test
    public void testFilterIndexedWithStartAndStep() {
        Stream.rangeClosed(4, 8).filterIndexed(20, (-5), new com.annimon.stream.function.IndexedPredicate<Integer>() {
            @Override
            public boolean test(int index, Integer value) {
                return ((index * value) % 2) == 0;
            }
        }).custom(assertElements(// (20 * 4)
        // (15 * 5)
        // (10 * 6)
        // (5  * 7)
        // (0  * 8)
        Matchers.contains(4, 6, 8)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIndexedIteratorNextOnEmpty() {
        Stream.<Integer>empty().filterIndexed(Util.wrap(Functions.remainder(2))).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilterIndexedIteratorRemove() {
        Stream.range(0, 10).filterIndexed(Util.wrap(Functions.remainder(2))).iterator().remove();
    }
}

