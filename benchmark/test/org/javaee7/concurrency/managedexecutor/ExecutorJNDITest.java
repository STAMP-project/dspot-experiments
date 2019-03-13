package org.javaee7.concurrency.managedexecutor;


import TestStatus.latch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.ManagedExecutorService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static TestStatus.latch;


/**
 *
 *
 * @author Arun Gupta
 */
// @Test
// public void testSubmitWithRunnableFromWebXML() throws Exception {
// executorFromWebXml.submit(new MyRunnableTask(1));
// Thread.sleep(2000);
// assertTrue(TestStatus.invokedRunnable);
// }
// 
// @Test
// public void testSubmitWithCallableFromWebXML() throws Exception {
// Future<Product> future = executorFromWebXml.submit(callableTask);
// assertEquals(1, future.get().getId());
// }
// 
// @Test
// public void testInvokeAllWithCallableFromWebXML() throws Exception {
// List<Future<Product>> results = executorFromWebXml.invokeAll(callableTasks);
// int count = 0;
// for (Future<Product> f : results) {
// assertEquals(count++, f.get().getId());
// }
// }
// 
// @Test
// public void testInvokeAnyWithCallableFromWebXML() throws Exception {
// Product results = executorFromWebXml.invokeAny(callableTasks);
// assertTrue(results.getId() >= 0);
// assertTrue(results.getId() <= 5);
// }
@RunWith(Arquillian.class)
public class ExecutorJNDITest {
    ManagedExecutorService defaultExecutor;

    ManagedExecutorService executorFromWebXml;

    Runnable runnableTask;

    Callable<Product> callableTask;

    Collection<Callable<Product>> callableTasks = new ArrayList<>();

    @Test
    public void testSubmitWithRunnableDefault() throws Exception {
        latch = new CountDownLatch(1);
        defaultExecutor.submit(runnableTask);
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSubmitWithCallableDefault() throws Exception {
        latch = new CountDownLatch(1);
        Future<Product> future = defaultExecutor.submit(callableTask);
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, future.get().getId());
    }

    @Test
    public void testInvokeAllWithCallableDefault() throws Exception {
        List<Future<Product>> results = defaultExecutor.invokeAll(callableTasks);
        int count = 0;
        for (Future<Product> f : results) {
            Assert.assertEquals((count++), f.get().getId());
        }
    }

    @Test
    public void testInvokeAnyWithCallableDefault() throws Exception {
        Product results = defaultExecutor.invokeAny(callableTasks);
        Assert.assertTrue(((results.getId()) >= 0));
        Assert.assertTrue(((results.getId()) <= 5));
    }
}

