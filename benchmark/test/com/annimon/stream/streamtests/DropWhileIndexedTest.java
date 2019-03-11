package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class DropWhileIndexedTest {
    @Test
    public void testDropWhileIndexed() {
        Stream.of(1, 2, 3, 4, 0, 1, 2).dropWhileIndexed(new com.annimon.stream.function.IndexedPredicate<Integer>() {
            @Override
            public boolean test(int index, Integer value) {
                return (index + value) < 5;
            }
        }).custom(assertElements(Matchers.contains(3, 4, 0, 1, 2)));
    }

    @Test
    public void testDropWhileIndexedWithStartAndStep() {
        Stream.of(1, 2, 3, 4, (-5), (-6), (-7)).dropWhileIndexed(2, 2, new com.annimon.stream.function.IndexedPredicate<Integer>() {
            @Override
            public boolean test(int index, Integer value) {
                return (index + value) < 10;
            }
        }).custom(assertElements(Matchers.contains(4, (-5), (-6), (-7))));
    }
}

