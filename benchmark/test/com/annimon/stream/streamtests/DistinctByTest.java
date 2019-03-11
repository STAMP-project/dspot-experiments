package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.StreamMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class DistinctByTest {
    @Test
    public void testDistinct() {
        Stream.of("a", "bc", "d", "ef", "ghij").distinctBy(stringLengthExtractor()).custom(assertElements(Matchers.contains("a", "bc", "ghij")));
    }

    @Test
    public void testDistinctEmpty() {
        Stream.<String>empty().distinctBy(stringLengthExtractor()).custom(StreamMatcher.<String>assertIsEmpty());
    }
}

