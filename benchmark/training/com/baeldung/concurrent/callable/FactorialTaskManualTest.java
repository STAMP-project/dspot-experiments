package com.baeldung.concurrent.callable;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import junit.framework.Assert;
import org.junit.Test;


public class FactorialTaskManualTest {
    private ExecutorService executorService;

    @Test
    public void whenTaskSubmitted_ThenFutureResultObtained() throws InterruptedException, ExecutionException {
        FactorialTask task = new FactorialTask(5);
        Future<Integer> future = executorService.submit(task);
        Assert.assertEquals(120, future.get().intValue());
    }

    @Test(expected = ExecutionException.class)
    public void whenException_ThenCallableThrowsIt() throws InterruptedException, ExecutionException {
        FactorialTask task = new FactorialTask((-5));
        Future<Integer> future = executorService.submit(task);
        Integer result = future.get().intValue();
    }

    @Test
    public void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled() {
        FactorialTask task = new FactorialTask((-5));
        Future<Integer> future = executorService.submit(task);
        Assert.assertEquals(false, future.isDone());
    }
}

