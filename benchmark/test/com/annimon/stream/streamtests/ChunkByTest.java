package com.annimon.stream.streamtests;


import UnaryOperator.Util;
import com.annimon.stream.Stream;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class ChunkByTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testChunkBy() {
        Stream.of(1, 1, 2, 2, 2, 3, 1).chunkBy(Util.<Integer>identity()).custom(assertElements(Matchers.contains(Arrays.asList(1, 1), Arrays.asList(2, 2, 2), Arrays.asList(3), Arrays.asList(1))));
    }
}

