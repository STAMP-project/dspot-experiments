package com.baeldung.concurrent.synchronize;


import BaeldungSynchronizedMethods.staticSum;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class BaeldungSynchronizeMethodsUnitTest {
    @Test
    public void givenMultiThread_whenMethodSync() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        BaeldungSynchronizedMethods method = new BaeldungSynchronizedMethods();
        IntStream.range(0, 1000).forEach(( count) -> service.submit(method::synchronisedCalculate));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1000, method.getSyncSum());
    }

    @Test
    public void givenMultiThread_whenStaticSyncMethod() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        IntStream.range(0, 1000).forEach(( count) -> service.submit(BaeldungSynchronizedMethods::syncStaticCalculate));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1000, staticSum);
    }
}

