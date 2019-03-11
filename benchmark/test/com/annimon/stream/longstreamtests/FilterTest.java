package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongPredicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterTest {
    @Test
    public void testFilter() {
        final LongPredicate predicate = Functions.remainderLong(111);
        LongStream.of(322, 555, 666, 1984, 1998).filter(predicate).custom(assertElements(Matchers.arrayContaining(555L, 666L, 1998L)));
        LongStream.of(12, (-10)).filter(predicate).custom(assertIsEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIteratorNextOnEmpty() {
        LongStream.empty().filter(Functions.remainderLong(2)).iterator().next();
    }
}

