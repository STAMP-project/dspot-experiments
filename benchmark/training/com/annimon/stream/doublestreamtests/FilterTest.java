package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import com.annimon.stream.function.DoublePredicate;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterTest {
    @Test
    public void testFilter() {
        final DoublePredicate predicate = Functions.greaterThan(Math.PI);
        DoubleStream.of(0.012, 10.347, 3.039, 19.84, 100.0).filter(predicate).custom(assertElements(Matchers.arrayContaining(10.347, 19.84, 100.0)));
        DoubleStream.of(0.012, (-10)).filter(predicate).custom(assertIsEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFilterIteratorNextOnEmpty() {
        DoubleStream.empty().filter(Functions.greaterThan(Math.PI)).iterator().next();
    }
}

