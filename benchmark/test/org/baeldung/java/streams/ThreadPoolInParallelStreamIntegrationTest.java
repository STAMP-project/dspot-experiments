package org.baeldung.java.streams;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class ThreadPoolInParallelStreamIntegrationTest {
    @Test
    public void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() throws InterruptedException, ExecutionException {
        long firstNum = 1;
        long lastNum = 1000000;
        List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed().collect(Collectors.toList());
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        long actualTotal = customThreadPool.submit(() -> aList.parallelStream().reduce(0L, Long::sum)).get();
        Assert.assertEquals((((lastNum + firstNum) * lastNum) / 2), actualTotal);
    }

    @Test
    public void givenList_whenCallingParallelStream_shouldBeParallelStream() {
        List<Long> aList = new ArrayList<>();
        Stream<Long> parallelStream = aList.parallelStream();
        Assert.assertTrue(parallelStream.isParallel());
    }
}

