/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.service.concurrent;


import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.common.function.ExceptionRunnable;
import org.wildfly.common.function.ExceptionSupplier;


/**
 *
 *
 * @author Paul Ferraro
 */
public class StampedLockServiceExecutorTestCase {
    @Test
    public void testExecuteRunnable() {
        ServiceExecutor executor = new StampedLockServiceExecutor();
        Runnable executeTask = Mockito.mock(Runnable.class);
        executor.execute(executeTask);
        // Task should run
        Mockito.verify(executeTask).run();
        Mockito.reset(executeTask);
        Runnable closeTask = Mockito.mock(Runnable.class);
        executor.close(closeTask);
        Mockito.verify(closeTask).run();
        Mockito.reset(closeTask);
        executor.close(closeTask);
        // Close task should only run once
        Mockito.verify(closeTask, Mockito.never()).run();
        executor.execute(executeTask);
        // Task should no longer run since service is closed
        Mockito.verify(executeTask, Mockito.never()).run();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteExceptionRunnable() throws Exception {
        ServiceExecutor executor = new StampedLockServiceExecutor();
        ExceptionRunnable<Exception> executeTask = Mockito.mock(ExceptionRunnable.class);
        executor.execute(executeTask);
        // Task should run
        Mockito.verify(executeTask).run();
        Mockito.reset(executeTask);
        Mockito.doThrow(new Exception()).when(executeTask).run();
        try {
            executor.execute(executeTask);
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        Mockito.reset(executeTask);
        Runnable closeTask = Mockito.mock(Runnable.class);
        executor.close(closeTask);
        Mockito.verify(closeTask).run();
        Mockito.reset(closeTask);
        executor.close(closeTask);
        // Close task should only run once
        Mockito.verify(closeTask, Mockito.never()).run();
        executor.execute(executeTask);
        // Task should no longer run since service is closed
        Mockito.verify(executeTask, Mockito.never()).run();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteSupplier() {
        ServiceExecutor executor = new StampedLockServiceExecutor();
        Object expected = new Object();
        Supplier<Object> executeTask = Mockito.mock(Supplier.class);
        Mockito.when(executeTask.get()).thenReturn(expected);
        Optional<Object> result = executor.execute(executeTask);
        // Task should run
        Assert.assertTrue(result.isPresent());
        Assert.assertSame(expected, result.get());
        Mockito.reset(executeTask);
        Runnable closeTask = Mockito.mock(Runnable.class);
        executor.close(closeTask);
        Mockito.verify(closeTask).run();
        Mockito.reset(closeTask);
        executor.close(closeTask);
        // Close task should only run once
        Mockito.verify(closeTask, Mockito.never()).run();
        result = executor.execute(executeTask);
        // Task should no longer run since service is closed
        Assert.assertFalse(result.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteExceptionSupplier() throws Exception {
        ServiceExecutor executor = new StampedLockServiceExecutor();
        Object expected = new Object();
        ExceptionSupplier<Object, Exception> executeTask = Mockito.mock(ExceptionSupplier.class);
        Mockito.when(executeTask.get()).thenReturn(expected);
        Optional<Object> result = executor.execute(executeTask);
        // Task should run
        Assert.assertTrue(result.isPresent());
        Assert.assertSame(expected, result.get());
        Mockito.reset(executeTask);
        Mockito.doThrow(new Exception()).when(executeTask).get();
        try {
            executor.execute(executeTask);
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        Mockito.reset(executeTask);
        Runnable closeTask = Mockito.mock(Runnable.class);
        executor.close(closeTask);
        Mockito.verify(closeTask).run();
        Mockito.reset(closeTask);
        executor.close(closeTask);
        // Close task should only run once
        Mockito.verify(closeTask, Mockito.never()).run();
        result = executor.execute(executeTask);
        // Task should no longer run since service is closed
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void concurrent() throws InterruptedException, ExecutionException {
        ServiceExecutor executor = new StampedLockServiceExecutor();
        ExecutorService service = Executors.newFixedThreadPool(2);
        try {
            CountDownLatch executeLatch = new CountDownLatch(1);
            CountDownLatch stopLatch = new CountDownLatch(1);
            Runnable executeTask = () -> {
                try {
                    executeLatch.countDown();
                    stopLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Future<?> executeFuture = service.submit(() -> executor.execute(executeTask));
            executeLatch.await();
            Runnable closeTask = Mockito.mock(Runnable.class);
            Future<?> closeFuture = service.submit(() -> executor.close(closeTask));
            Thread.yield();
            // Verify that stop is blocked
            Mockito.verify(closeTask, Mockito.never()).run();
            stopLatch.countDown();
            executeFuture.get();
            closeFuture.get();
            // Verify close task was invoked, now that execute task is complete
            Mockito.verify(closeTask).run();
        } finally {
            service.shutdownNow();
        }
    }
}

