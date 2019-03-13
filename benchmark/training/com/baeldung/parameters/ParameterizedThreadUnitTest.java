package com.baeldung.parameters;


import com.baeldung.concurrent.parameter.AverageCalculator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class ParameterizedThreadUnitTest {
    @Test
    public void whenSendingParameterToCallable_thenSuccessful() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Double> result = executorService.submit(new AverageCalculator(1, 2, 3));
        try {
            Assert.assertEquals(Double.valueOf(2.0), result.get());
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void whenParametersToThreadWithLamda_thenParametersPassedCorrectly() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        int[] numbers = new int[]{ 4, 5, 6 };
        try {
            Future<Integer> sumResult = executorService.submit(() -> IntStream.of(numbers).sum());
            Future<Double> averageResult = executorService.submit(() -> IntStream.of(numbers).average().orElse(0.0));
            Assert.assertEquals(Integer.valueOf(15), sumResult.get());
            Assert.assertEquals(Double.valueOf(5.0), averageResult.get());
        } finally {
            executorService.shutdown();
        }
    }
}

