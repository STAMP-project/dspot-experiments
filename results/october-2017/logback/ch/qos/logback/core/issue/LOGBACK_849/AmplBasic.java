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


public class AmplBasic {
    // InterruptIgnoring ===========================================
    static class InterruptIgnoring implements java.lang.Runnable {
        int delay;

        InterruptIgnoring(int delay) {
            this.delay = delay;
        }

        public void run() {
            long runUntil = (java.lang.System.currentTimeMillis()) + (delay);
            while (true) {
                try {
                    long sleep = runUntil - (java.lang.System.currentTimeMillis());
                    java.lang.System.out.println(("will sleep " + sleep));
                    if (sleep > 0) {
                        java.lang.Thread.currentThread().sleep(delay);
                    }else {
                        return ;
                    }
                } catch (java.lang.InterruptedException e) {
                    // ignore the exception
                }
            } 
        }
    }

    java.util.concurrent.ExecutorService executor = ch.qos.logback.core.util.ExecutorServiceUtil.newScheduledExecutorService();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    @org.junit.Test(timeout = 100)
    public void withNoSubmittedTasksShutdownNowShouldReturnImmediately() throws java.lang.InterruptedException {
        executor.shutdownNow();
        executor.awaitTermination(5000, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    @org.junit.Ignore
    @org.junit.Test
    public void withOneSlowTask() throws java.lang.InterruptedException {
        executor.execute(new ch.qos.logback.core.issue.LOGBACK_849.AmplBasic.InterruptIgnoring(1000));
        java.lang.Thread.sleep(100);
        ch.qos.logback.core.util.ExecutorServiceUtil.shutdown(executor);
    }
}

