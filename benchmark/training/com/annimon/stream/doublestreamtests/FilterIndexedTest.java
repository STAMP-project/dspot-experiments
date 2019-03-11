package com.annimon.stream.doublestreamtests;


import IndexedDoublePredicate.Util;
import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import com.annimon.stream.function.IndexedDoublePredicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterIndexedTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testFilterIndexed() {
        DoubleStream.of(1, 12, 3, 8, 2).filterIndexed(new IndexedDoublePredicate() {
            @Override
            public boolean test(int index, double value) {
                return (index * value) > 10;
            }
        }).custom(assertElements(// (0 * 1)
        // (1 * 12)
        // (2 * 3)
        // (3 * 8)
        // (4 * 2)
        Matchers.arrayContaining(Matchers.closeTo(12, 0.001), Matchers.closeTo(8, 0.001))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilterIndexedWithStartAndStep() {
        DoubleStream.of(1, 12, 3, 8, 2).filterIndexed(4, (-2), new IndexedDoublePredicate() {
            @Override
            public boolean test(int index, double value) {
                return (Math.abs((index * value))) > 10;
            }
        }).custom(assertElements(// (4 * 1)
        // (2 * 12)
        // (0 * 3)
        // (-2 * 8)
        // (-4 * 2)
        Matchers.arrayContaining(Matchers.closeTo(12, 0.001), Matchers.closeTo(8, 0.001))));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIndexedIteratorNextOnEmpty() {
        DoubleStream.empty().filterIndexed(Util.wrap(Functions.greaterThan(Math.PI))).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilterIndexedIteratorRemove() {
        DoubleStream.of(1, 2, 3, 4, 5).filterIndexed(Util.wrap(Functions.greaterThan(Math.PI))).iterator().remove();
    }
}

