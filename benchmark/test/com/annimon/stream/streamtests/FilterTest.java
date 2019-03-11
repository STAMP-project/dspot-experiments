package com.annimon.stream.streamtests;


import Predicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterTest {
    @Test
    public void testFilter() {
        Stream.range(0, 10).filter(Functions.remainder(2)).custom(assertElements(Matchers.contains(0, 2, 4, 6, 8)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIteratorNextOnEmpty() {
        Stream.<Integer>empty().filter(Functions.remainder(2)).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilterIteratorRemove() {
        Stream.range(0, 10).filter(Functions.remainder(2)).iterator().remove();
    }

    @Test
    public void testFilterWithOrPredicate() {
        Predicate<Integer> predicate = Util.or(Functions.remainder(2), Functions.remainder(3));
        Stream.range(0, 10).filter(predicate).custom(assertElements(Matchers.contains(0, 2, 3, 4, 6, 8, 9)));
    }

    @Test
    public void testFilterWithAndPredicate() {
        Predicate<Integer> predicate = Util.and(Functions.remainder(2), Functions.remainder(3));
        Stream.range(0, 10).filter(predicate).custom(assertElements(Matchers.contains(0, 6)));
    }

    @Test
    public void testFilterWithXorPredicate() {
        Predicate<Integer> predicate = Util.xor(Functions.remainder(2), Functions.remainder(3));
        Stream.range(0, 10).filter(predicate).custom(assertElements(Matchers.contains(2, 3, 4, 8, 9)));
    }
}

