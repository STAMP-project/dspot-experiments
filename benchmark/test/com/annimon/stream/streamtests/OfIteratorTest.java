package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class OfIteratorTest {
    @Test
    public void testStreamOfIterator() {
        Stream.of(Functions.counterIterator()).limit(5).custom(assertElements(Matchers.contains(0, 1, 2, 3, 4)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfIteratorNull() {
        Stream.of(((Iterator<?>) (null)));
    }
}

