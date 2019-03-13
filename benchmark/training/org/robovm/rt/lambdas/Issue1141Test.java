package org.robovm.rt.lambdas;


import org.junit.Assert;
import org.junit.Test;


public class Issue1141Test {
    interface Function<T> {
        public T func(T a);
    }

    interface BinaryFunction<T> {
        public T sum(T a, T b);
    }

    @Test
    public void testIssue1141() {
        Assert.assertEquals((((0 + 1) + 3) + 4), Issue1141Test.method(0L, ( e) -> 1L, Issue1141Test::sum));
    }
}

