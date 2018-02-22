/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.issue.LOGBACK_849;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplBasic {
    // InterruptIgnoring ===========================================
    static class InterruptIgnoring implements Runnable {
        int delay;

        InterruptIgnoring(int delay) {
            this.delay = delay;
        }

        public void run() {
            long runUntil = (System.currentTimeMillis()) + (delay);
            while (true) {
                try {
                    long sleep = runUntil - (System.currentTimeMillis());
                    System.out.println(("will sleep " + sleep));
                    if (sleep > 0) {
                        Thread.currentThread().sleep(delay);
                    }else {
                        return ;
                    }
                } catch (InterruptedException e) {
                    // ignore the exception
                }
            } 
        }
    }

    ExecutorService executor = ExecutorServiceUtil.newScheduledExecutorService();

    Context context = new ContextBase();

    @Ignore
    @Test
    public void withOneSlowTask() throws InterruptedException {
        executor.execute(new AmplBasic.InterruptIgnoring(1000));
        Thread.sleep(100);
        ExecutorServiceUtil.shutdown(executor);
    }

    @Test(timeout = 10000)
    public void withNoSubmittedTasksShutdownNowShouldReturnImmediately() throws InterruptedException {
        // AssertGenerator create local variable with return value of invocation
        List<Runnable> o_withNoSubmittedTasksShutdownNowShouldReturnImmediately__1 = executor.shutdownNow();
        // AssertGenerator add assertion
        Assert.assertTrue(o_withNoSubmittedTasksShutdownNowShouldReturnImmediately__1.isEmpty());
        // AssertGenerator create local variable with return value of invocation
        boolean o_withNoSubmittedTasksShutdownNowShouldReturnImmediately__2 = this.executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        // AssertGenerator add assertion
        Assert.assertTrue(o_withNoSubmittedTasksShutdownNowShouldReturnImmediately__2);
        // AssertGenerator add assertion
        Assert.assertTrue(o_withNoSubmittedTasksShutdownNowShouldReturnImmediately__1.isEmpty());
    }
}

