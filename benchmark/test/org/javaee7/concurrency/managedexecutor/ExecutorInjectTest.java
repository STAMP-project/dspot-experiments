package org.javaee7.concurrency.managedexecutor;


import TestStatus.foundTransactionScopedBean;
import TestStatus.latch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
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
@RunWith(Arquillian.class)
public class ExecutorInjectTest {
    @Resource(name = "DefaultManagedExecutorService")
    ManagedExecutorService defaultExecutor;

    @Resource(name = "concurrent/myExecutor")
    ManagedExecutorService executorFromWebXml;

    @Resource
    ManagedExecutorService executorNoName;

    @EJB
    TestBean ejb;

    Callable<Product> callableTask;

    Runnable runnableTask;

    MyTaskWithListener taskWithListener;

    // Inject so we have a managed bean to handle the TX
    @Inject
    MyTaskWithTransaction taskWithTransaction;

    Collection<Callable<Product>> callableTasks = new ArrayList<>();

    private static CountDownLatch latch;

    @Test
    public void testSubmitWithRunnableDefault() throws Exception {
        latch = new CountDownLatch(1);
        defaultExecutor.submit(runnableTask);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSubmitWithCallableDefault() throws Exception {
        latch = new CountDownLatch(1);
        Future<Product> future = defaultExecutor.submit(callableTask);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
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

    @Test
    public void testSubmitWithRunnableNoName() throws Exception {
        latch = new CountDownLatch(1);
        executorNoName.submit(runnableTask);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSubmitWithCallableNoName() throws Exception {
        latch = new CountDownLatch(1);
        Future<Product> future = executorNoName.submit(callableTask);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, future.get().getId());
    }

    @Test
    public void testInvokeAllWithCallableNoName() throws Exception {
        List<Future<Product>> results = executorNoName.invokeAll(callableTasks);
        int count = 0;
        for (Future<Product> f : results) {
            Assert.assertEquals((count++), f.get().getId());
        }
    }

    @Test
    public void testInvokeAnyWithCallableNoName() throws Exception {
        Product results = executorNoName.invokeAny(callableTasks);
        Assert.assertTrue(((results.getId()) >= 0));
        Assert.assertTrue(((results.getId()) <= 5));
    }

    @Test
    public void testSubmitWithRunnableFromWebXML() throws Exception {
        latch = new CountDownLatch(1);
        executorFromWebXml.submit(new MyRunnableTask());
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSubmitWithCallableFromWebXML() throws Exception {
        latch = new CountDownLatch(1);
        Future<Product> future = executorFromWebXml.submit(new MyCallableTask(1));
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, future.get().getId());
    }

    @Test
    public void testInvokeAllWithCallableFromWebXML() throws Exception {
        List<Future<Product>> results = executorFromWebXml.invokeAll(callableTasks);
        int count = 0;
        for (Future<Product> f : results) {
            Assert.assertEquals((count++), f.get().getId());
        }
    }

    @Test
    public void testInvokeAnyWithCallableFromWebXML() throws Exception {
        Product results = executorFromWebXml.invokeAny(callableTasks);
        Assert.assertTrue(((results.getId()) >= 0));
        Assert.assertTrue(((results.getId()) <= 5));
    }

    @Test
    public void testSubmitWithListener() throws Exception {
        latch = new CountDownLatch(1);
        defaultExecutor.submit(taskWithListener);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSubmitWithTransaction() throws Exception {
        latch = new CountDownLatch(1);
        defaultExecutor.submit(taskWithTransaction);
        Assert.assertTrue(TestStatus.latch.await(2000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(foundTransactionScopedBean);
    }

    @Test
    public void testSubmitWithEJB() throws Exception {
        Assert.assertTrue(ejb.doSomething());
    }
}

