package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class OfIterableTest {
    @Test
    public void testStreamOfIterable() {
        Iterable<Integer> iterable = new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return Functions.counterIterator();
            }
        };
        Stream.of(iterable).limit(5).custom(assertElements(Matchers.contains(0, 1, 2, 3, 4)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfIterableNull() {
        Stream.of(((Iterable<?>) (null)));
    }
}

