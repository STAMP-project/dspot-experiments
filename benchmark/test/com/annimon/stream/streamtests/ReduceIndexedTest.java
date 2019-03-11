package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class ReduceIndexedTest {
    @Test
    public void testReduceIndexed() {
        int result = Stream.rangeClosed(1, 5).reduceIndexed(10, Functions.indexedAddition());
        Assert.assertEquals(35, result);
    }

    @Test
    public void testReduceIndexedWithStartAndStep() {
        int result = Stream.rangeClosed(1, 5).reduceIndexed(1, 2, 0, Functions.indexedAddition());
        Assert.assertEquals(40, result);
    }
}

