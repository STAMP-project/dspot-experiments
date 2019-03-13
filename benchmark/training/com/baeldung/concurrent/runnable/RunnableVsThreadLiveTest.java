package com.baeldung.concurrent.runnable;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunnableVsThreadLiveTest {
    private static Logger log = LoggerFactory.getLogger(RunnableVsThreadLiveTest.class);

    private static ExecutorService executorService;

    @Test
    public void givenARunnable_whenRunIt_thenResult() throws Exception {
        Thread thread = new Thread(new SimpleRunnable("SimpleRunnable executed using Thread"));
        thread.start();
        thread.join();
    }

    @Test
    public void givenARunnable_whenSubmitToES_thenResult() throws Exception {
        RunnableVsThreadLiveTest.executorService.submit(new SimpleRunnable("SimpleRunnable executed using ExecutorService")).get();
    }

    @Test
    public void givenARunnableLambda_whenSubmitToES_thenResult() throws Exception {
        RunnableVsThreadLiveTest.executorService.submit(() -> RunnableVsThreadLiveTest.log.info("Lambda runnable executed!!!")).get();
    }

    @Test
    public void givenAThread_whenRunIt_thenResult() throws Exception {
        Thread thread = new SimpleThread("SimpleThread executed using Thread");
        thread.start();
        thread.join();
    }

    @Test
    public void givenAThread_whenSubmitToES_thenResult() throws Exception {
        RunnableVsThreadLiveTest.executorService.submit(new SimpleThread("SimpleThread executed using ExecutorService")).get();
    }

    @Test
    public void givenACallable_whenSubmitToES_thenResult() throws Exception {
        Future<Integer> future = RunnableVsThreadLiveTest.executorService.submit(new SimpleCallable());
        RunnableVsThreadLiveTest.log.info("Result from callable: {}", future.get());
    }

    @Test
    public void givenACallableAsLambda_whenSubmitToES_thenResult() throws Exception {
        Future<Integer> future = RunnableVsThreadLiveTest.executorService.submit(() -> RandomUtils.nextInt(0, 100));
        RunnableVsThreadLiveTest.log.info("Result from callable: {}", future.get());
    }
}

