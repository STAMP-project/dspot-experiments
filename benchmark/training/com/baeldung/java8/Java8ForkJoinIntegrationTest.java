package com.baeldung.java8;


import com.baeldung.forkjoin.CustomRecursiveAction;
import com.baeldung.forkjoin.CustomRecursiveTask;
import com.baeldung.forkjoin.util.PoolUtil;
import java.util.concurrent.ForkJoinPool;
import org.junit.Assert;
import org.junit.Test;


public class Java8ForkJoinIntegrationTest {
    private int[] arr;

    private CustomRecursiveTask customRecursiveTask;

    @Test
    public void callPoolUtil_whenExistsAndExpectedType_thenCorrect() {
        ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
        ForkJoinPool forkJoinPoolTwo = PoolUtil.forkJoinPool;
        Assert.assertNotNull(forkJoinPool);
        Assert.assertEquals(2, forkJoinPool.getParallelism());
        Assert.assertEquals(forkJoinPool, forkJoinPoolTwo);
    }

    @Test
    public void callCommonPool_whenExistsAndExpectedType_thenCorrect() {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ForkJoinPool commonPoolTwo = ForkJoinPool.commonPool();
        Assert.assertNotNull(commonPool);
        Assert.assertEquals(commonPool, commonPoolTwo);
    }

    @Test
    public void executeRecursiveAction_whenExecuted_thenCorrect() {
        CustomRecursiveAction myRecursiveAction = new CustomRecursiveAction("ddddffffgggghhhh");
        ForkJoinPool.commonPool().invoke(myRecursiveAction);
        Assert.assertTrue(myRecursiveAction.isDone());
    }

    @Test
    public void executeRecursiveTask_whenExecuted_thenCorrect() {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.execute(customRecursiveTask);
        int result = customRecursiveTask.join();
        Assert.assertTrue(customRecursiveTask.isDone());
        forkJoinPool.submit(customRecursiveTask);
        int resultTwo = customRecursiveTask.join();
        Assert.assertTrue(customRecursiveTask.isDone());
    }

    @Test
    public void executeRecursiveTaskWithFJ_whenExecuted_thenCorrect() {
        CustomRecursiveTask customRecursiveTaskFirst = new CustomRecursiveTask(arr);
        CustomRecursiveTask customRecursiveTaskSecond = new CustomRecursiveTask(arr);
        CustomRecursiveTask customRecursiveTaskLast = new CustomRecursiveTask(arr);
        customRecursiveTaskFirst.fork();
        customRecursiveTaskSecond.fork();
        customRecursiveTaskLast.fork();
        int result = 0;
        result += customRecursiveTaskLast.join();
        result += customRecursiveTaskSecond.join();
        result += customRecursiveTaskFirst.join();
        Assert.assertTrue(customRecursiveTaskFirst.isDone());
        Assert.assertTrue(customRecursiveTaskSecond.isDone());
        Assert.assertTrue(customRecursiveTaskLast.isDone());
        Assert.assertTrue((result != 0));
    }
}

