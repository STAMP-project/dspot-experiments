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


package ch.qos.logback.core;


/**
 * @author Ceki G&uuml;lc&uuml;
 * @author Torsten Juergeleit
 */
public class AmplAsyncAppenderBaseTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.AsyncAppenderBase<java.lang.Integer> asyncAppenderBase = new ch.qos.logback.core.AsyncAppenderBase<java.lang.Integer>();

    ch.qos.logback.core.AmplAsyncAppenderBaseTest.LossyAsyncAppender lossyAsyncAppender = new ch.qos.logback.core.AmplAsyncAppenderBaseTest.LossyAsyncAppender();

    ch.qos.logback.core.testUtil.DelayingListAppender<java.lang.Integer> delayingListAppender = new ch.qos.logback.core.testUtil.DelayingListAppender<java.lang.Integer>();

    ch.qos.logback.core.read.ListAppender<java.lang.Integer> listAppender = new ch.qos.logback.core.read.ListAppender<java.lang.Integer>();

    ch.qos.logback.core.status.OnConsoleStatusListener onConsoleStatusListener = new ch.qos.logback.core.status.OnConsoleStatusListener();

    ch.qos.logback.core.status.StatusChecker statusChecker = new ch.qos.logback.core.status.StatusChecker(context);

    @org.junit.Before
    public void setUp() {
        onConsoleStatusListener.setContext(context);
        context.getStatusManager().add(onConsoleStatusListener);
        onConsoleStatusListener.start();
        asyncAppenderBase.setContext(context);
        lossyAsyncAppender.setContext(context);
        listAppender.setContext(context);
        listAppender.setName("list");
        listAppender.start();
        delayingListAppender.setContext(context);
        delayingListAppender.setName("list");
        delayingListAppender.start();
    }

    @org.junit.Test(timeout = 2000)
    public void smoke() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(0);
        asyncAppenderBase.stop();
        verify(listAppender, 1);
    }

    @org.junit.Test
    public void exceptionsShouldNotCauseHalting() throws java.lang.InterruptedException {
        ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
        npeAppender.setName("bad");
        npeAppender.setContext(context);
        npeAppender.start();
        asyncAppenderBase.addAppender(npeAppender);
        asyncAppenderBase.start();
        org.junit.Assert.assertTrue(asyncAppenderBase.isStarted());
        for (int i = 0; i < 10; i++)
            asyncAppenderBase.append(i);
        
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        org.junit.Assert.assertEquals(ch.qos.logback.core.AppenderBase.ALLOWED_REPEATS, statusChecker.matchCount("Appender \\[bad\\] failed to append."));
    }

    @org.junit.Test(timeout = 2000)
    public void emptyQueueShouldBeStoppable() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        verify(listAppender, 0);
    }

    @org.junit.Test(timeout = 2000)
    public void workerShouldStopEvenIfInterruptExceptionConsumedWithinSubappender() {
        delayingListAppender.delay = 100;
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(0);
        asyncAppenderBase.stop();
        verify(delayingListAppender, 1);
        org.junit.Assert.assertTrue(delayingListAppender.interrupted);
        java.lang.Thread.interrupted();
    }

    @org.junit.Test(timeout = 2000)
    public void noEventLoss() {
        int bufferSize = 10;
        int loopLen = bufferSize * 2;
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.setQueueSize(bufferSize);
        asyncAppenderBase.start();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        asyncAppenderBase.stop();
        verify(delayingListAppender, loopLen);
    }

    @org.junit.Test(timeout = 2000)
    public void eventLossIfNeverBlock() {
        int bufferSize = 10;
        int loopLen = bufferSize * 200;
        delayingListAppender.setDelay(5);// (loopLen*delay) much bigger than test timeout
        
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.setQueueSize(bufferSize);
        asyncAppenderBase.setNeverBlock(true);
        asyncAppenderBase.start();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        asyncAppenderBase.stop();
        // ListAppender size isn't a reliable test here, so just make sure we didn't
        // have any errors, and that we could complete the test in time.
        statusChecker.assertIsErrorFree();
    }

    @org.junit.Test(timeout = 2000)
    public void lossyAppenderShouldOnlyLoseCertainEvents() {
        int bufferSize = 5;
        int loopLen = bufferSize * 2;
        lossyAsyncAppender.addAppender(delayingListAppender);
        lossyAsyncAppender.setQueueSize(bufferSize);
        lossyAsyncAppender.setDiscardingThreshold(1);
        lossyAsyncAppender.start();
        for (int i = 0; i < loopLen; i++) {
            lossyAsyncAppender.doAppend(i);
        }
        lossyAsyncAppender.stop();
        // events 0, 3, 6 and 9 are discardable. However, for events 0 and 3
        // the buffer is not not yet full. Thus, only events 6 and 9 will be
        // effectively discarded.
        verify(delayingListAppender, (loopLen - 2));
    }

    @org.junit.Test(timeout = 2000)
    public void lossyAppenderShouldBeNonLossyIfDiscardingThresholdIsZero() {
        int bufferSize = 5;
        int loopLen = bufferSize * 2;
        lossyAsyncAppender.addAppender(delayingListAppender);
        lossyAsyncAppender.setQueueSize(bufferSize);
        lossyAsyncAppender.setDiscardingThreshold(0);
        lossyAsyncAppender.start();
        for (int i = 0; i < loopLen; i++) {
            lossyAsyncAppender.doAppend(i);
        }
        lossyAsyncAppender.stop();
        verify(delayingListAppender, loopLen);
    }

    @org.junit.Test
    public void invalidQueueCapacityShouldResultInNonStartedAppender() {
        asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
        asyncAppenderBase.setQueueSize(0);
        org.junit.Assert.assertEquals(0, asyncAppenderBase.getQueueSize());
        asyncAppenderBase.start();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        statusChecker.assertContainsMatch("Invalid queue size");
    }

    @java.lang.SuppressWarnings(value = "deprecation")
    @org.junit.Test
    public void workerThreadFlushesOnStop() {
        int loopLen = 5;
        int maxRuntime = (loopLen + 1) * (java.lang.Math.max(1000, delayingListAppender.delay));
        ch.qos.logback.core.read.ListAppender<java.lang.Integer> la = delayingListAppender;
        asyncAppenderBase.addAppender(la);
        asyncAppenderBase.setDiscardingThreshold(0);
        asyncAppenderBase.setMaxFlushTime(maxRuntime);
        asyncAppenderBase.start();
        asyncAppenderBase.worker.suspend();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        org.junit.Assert.assertEquals(loopLen, asyncAppenderBase.getNumberOfElementsInQueue());
        org.junit.Assert.assertEquals(0, la.list.size());
        asyncAppenderBase.worker.resume();
        asyncAppenderBase.stop();
        org.junit.Assert.assertEquals(0, asyncAppenderBase.getNumberOfElementsInQueue());
        verify(la, loopLen);
    }

    // @SuppressWarnings("deprecation")
    @org.junit.Test
    public void stopExitsWhenMaxRuntimeReached() throws java.lang.InterruptedException {
        int maxFlushTime = 1;// runtime of 0 means wait forever, so use 1 ms instead
        
        int loopLen = 10;
        ch.qos.logback.core.read.ListAppender<java.lang.Integer> la = delayingListAppender;
        asyncAppenderBase.addAppender(la);
        asyncAppenderBase.setMaxFlushTime(maxFlushTime);
        asyncAppenderBase.start();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        asyncAppenderBase.stop();
        // confirms that stop exited when runtime reached
        statusChecker.assertContainsMatch((("Max queue flush timeout \\(" + maxFlushTime) + " ms\\) exceeded."));
        asyncAppenderBase.worker.join();
        // confirms that all entries do end up being flushed if we wait long enough
        verify(la, loopLen);
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    @org.junit.Test
    public void verifyInterruptionIsNotSwallowed() {
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        java.lang.Thread.currentThread().interrupt();
        asyncAppenderBase.doAppend(new java.lang.Integer(0));
        org.junit.Assert.assertTrue(java.lang.Thread.currentThread().isInterrupted());
        // clear interrupt flag for subsequent tests
        java.lang.Thread.interrupted();
    }

    // Interruption of current thread should not prevent logging.
    // See also http://jira.qos.ch/browse/LOGBACK-910
    // and https://jira.qos.ch/browse/LOGBACK-1247
    @org.junit.Test
    public void verifyInterruptionDoesNotPreventLogging() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(new java.lang.Integer(0));
        java.lang.Thread.currentThread().interrupt();
        asyncAppenderBase.doAppend(new java.lang.Integer(1));
        asyncAppenderBase.doAppend(new java.lang.Integer(1));
        org.junit.Assert.assertTrue(java.lang.Thread.currentThread().isInterrupted());
        // the interruption needs to be consumed
        java.lang.Thread.interrupted();
        asyncAppenderBase.stop();
        verify(listAppender, 3);
    }

    @org.junit.Test
    public void verifyInterruptionFlagWhenStopping_INTERUPPTED() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        java.lang.Thread.currentThread().interrupt();
        asyncAppenderBase.stop();
        org.junit.Assert.assertTrue(java.lang.Thread.currentThread().isInterrupted());
        java.lang.Thread.interrupted();
    }

    @org.junit.Test
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(java.lang.Thread.currentThread().isInterrupted());
    }

    @org.junit.Test
    public void verifyInterruptionOfWorkerIsSwallowed() {
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.worker.isInterrupted());
    }

    private void verify(ch.qos.logback.core.read.ListAppender<java.lang.Integer> la, int atLeast) {
        org.junit.Assert.assertFalse(la.isStarted());
        org.junit.Assert.assertTrue(((atLeast + " <= ") + (la.list.size())), (atLeast <= (la.list.size())));
        statusChecker.assertIsErrorFree();
        statusChecker.assertContainsMatch("Worker thread will flush remaining events before exiting.");
    }

    static class LossyAsyncAppender extends ch.qos.logback.core.AsyncAppenderBase<java.lang.Integer> {
        @java.lang.Override
        protected boolean isDiscardable(java.lang.Integer i) {
            return (i % 3) == 0;
        }
    }

    @org.junit.Test
    public void checkThatStartMethodIsIdempotent() {
        asyncAppenderBase.addAppender(lossyAsyncAppender);
        asyncAppenderBase.start();
        // we don't need mockito for this test, but if we did here is how it would look
        // AsyncAppenderBase<Integer> spied = Mockito.spy(asyncAppenderBase);
        // Mockito.doThrow(new IllegalStateException("non idempotent start")).when((UnsynchronizedAppenderBase<Integer>)
        // spied).start();
        // a second invocation of start will cause a IllegalThreadStateException thrown by the asyncAppenderBase.worker
        // thread
        asyncAppenderBase.start();
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#checkThatStartMethodIsIdempotent */
    @org.junit.Test(timeout = 10000)
    public void checkThatStartMethodIsIdempotent_add1() {
        // MethodCallAdder
        asyncAppenderBase.addAppender(lossyAsyncAppender);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1921554173 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1921554173, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(lossyAsyncAppender);
        asyncAppenderBase.start();
        // we don't need mockito for this test, but if we did here is how it would look
        // AsyncAppenderBase<Integer> spied = Mockito.spy(asyncAppenderBase);
        // Mockito.doThrow(new IllegalStateException("non idempotent start")).when((UnsynchronizedAppenderBase<Integer>)
        // spied).start();
        // a second invocation of start will cause a IllegalThreadStateException thrown by the asyncAppenderBase.worker
        // thread
        asyncAppenderBase.start();
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#checkThatStartMethodIsIdempotent */
    @org.junit.Test(timeout = 10000)
    public void checkThatStartMethodIsIdempotent_add2() {
        asyncAppenderBase.addAppender(lossyAsyncAppender);
        // MethodCallAdder
        asyncAppenderBase.start();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1450588858 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1450588858, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.start();
        // we don't need mockito for this test, but if we did here is how it would look
        // AsyncAppenderBase<Integer> spied = Mockito.spy(asyncAppenderBase);
        // Mockito.doThrow(new IllegalStateException("non idempotent start")).when((UnsynchronizedAppenderBase<Integer>)
        // spied).start();
        // a second invocation of start will cause a IllegalThreadStateException thrown by the asyncAppenderBase.worker
        // thread
        asyncAppenderBase.start();
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#checkThatStartMethodIsIdempotent */
    @org.junit.Test(timeout = 10000)
    public void checkThatStartMethodIsIdempotent_add1_cf15_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_9_1 = 256;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 1000;
            // MethodCallAdder
            asyncAppenderBase.addAppender(lossyAsyncAppender);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1921554173 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1921554173, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_9 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_6 = (ch.qos.logback.core.Appender)null;
            // StatementAdderMethod cloned existing statement
            vc_6.setName(vc_9);
            // MethodAssertGenerator build local variable
            Object o_21_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.addAppender(lossyAsyncAppender);
            asyncAppenderBase.start();
            // we don't need mockito for this test, but if we did here is how it would look
            // AsyncAppenderBase<Integer> spied = Mockito.spy(asyncAppenderBase);
            // Mockito.doThrow(new IllegalStateException("non idempotent start")).when((UnsynchronizedAppenderBase<Integer>)
            // spied).start();
            // a second invocation of start will cause a IllegalThreadStateException thrown by the asyncAppenderBase.worker
            // thread
            asyncAppenderBase.start();
            org.junit.Assert.fail("checkThatStartMethodIsIdempotent_add1_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#eventLossIfNeverBlock */
    @org.junit.Test(timeout = 10000)
    public void eventLossIfNeverBlock_add894() {
        int bufferSize = 10;
        int loopLen = bufferSize * 200;
        delayingListAppender.setDelay(5);// (loopLen*delay) much bigger than test timeout
        
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.setQueueSize(bufferSize);
        asyncAppenderBase.setNeverBlock(true);
        // MethodCallAdder
        asyncAppenderBase.start();
        // AssertGenerator add assertion
        java.util.ArrayList collection_967827115 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_967827115, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.start();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        asyncAppenderBase.stop();
        // ListAppender size isn't a reliable test here, so just make sure we didn't
        // have any errors, and that we could complete the test in time.
        statusChecker.assertIsErrorFree();
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#eventLossIfNeverBlock */
    @org.junit.Test(timeout = 10000)
    public void eventLossIfNeverBlock_add891() {
        int bufferSize = 10;
        int loopLen = bufferSize * 200;
        delayingListAppender.setDelay(5);// (loopLen*delay) much bigger than test timeout
        
        // MethodCallAdder
        asyncAppenderBase.addAppender(delayingListAppender);
        // AssertGenerator add assertion
        java.util.ArrayList collection_480428493 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_480428493, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.setQueueSize(bufferSize);
        asyncAppenderBase.setNeverBlock(true);
        asyncAppenderBase.start();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        asyncAppenderBase.stop();
        // ListAppender size isn't a reliable test here, so just make sure we didn't
        // have any errors, and that we could complete the test in time.
        statusChecker.assertIsErrorFree();
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#eventLossIfNeverBlock */
    @org.junit.Test(timeout = 10000)
    public void eventLossIfNeverBlock_add892_cf993_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_18_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_14_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1000;
            int bufferSize = 10;
            int loopLen = bufferSize * 200;
            delayingListAppender.setDelay(5);// (loopLen*delay) much bigger than test timeout
            
            asyncAppenderBase.addAppender(delayingListAppender);
            // MethodCallAdder
            asyncAppenderBase.setQueueSize(bufferSize);
            // AssertGenerator add assertion
            java.util.ArrayList collection_2003784348 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2003784348, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_49 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_46 = (ch.qos.logback.core.Appender)null;
            // StatementAdderMethod cloned existing statement
            vc_46.setName(vc_49);
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.setQueueSize(bufferSize);
            asyncAppenderBase.setNeverBlock(true);
            asyncAppenderBase.start();
            for (int i = 0; i < loopLen; i++) {
                asyncAppenderBase.doAppend(i);
            }
            asyncAppenderBase.stop();
            // ListAppender size isn't a reliable test here, so just make sure we didn't
            // have any errors, and that we could complete the test in time.
            statusChecker.assertIsErrorFree();
            org.junit.Assert.fail("eventLossIfNeverBlock_add892_cf993 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#exceptionsShouldNotCauseHalting */
    @org.junit.Test(timeout = 10000)
    public void exceptionsShouldNotCauseHalting_add2149() throws java.lang.InterruptedException {
        ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
        npeAppender.setName("bad");
        npeAppender.setContext(context);
        npeAppender.start();
        asyncAppenderBase.addAppender(npeAppender);
        asyncAppenderBase.start();
        org.junit.Assert.assertTrue(asyncAppenderBase.isStarted());
        for (int i = 0; i < 10; i++) {
            // MethodCallAdder
            asyncAppenderBase.append(i);
            // AssertGenerator add assertion
            java.util.ArrayList collection_550423457 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_550423457, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.append(i);
        }
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        org.junit.Assert.assertEquals(ch.qos.logback.core.AppenderBase.ALLOWED_REPEATS, statusChecker.matchCount("Appender \\[bad\\] failed to append."));
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#exceptionsShouldNotCauseHalting */
    @org.junit.Test(timeout = 10000)
    public void exceptionsShouldNotCauseHalting_add2147() throws java.lang.InterruptedException {
        ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
        npeAppender.setName("bad");
        npeAppender.setContext(context);
        npeAppender.start();
        // MethodCallAdder
        asyncAppenderBase.addAppender(npeAppender);
        // AssertGenerator add assertion
        java.util.ArrayList collection_243195747 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_243195747, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(npeAppender);
        asyncAppenderBase.start();
        org.junit.Assert.assertTrue(asyncAppenderBase.isStarted());
        for (int i = 0; i < 10; i++)
            asyncAppenderBase.append(i);
        
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        org.junit.Assert.assertEquals(ch.qos.logback.core.AppenderBase.ALLOWED_REPEATS, statusChecker.matchCount("Appender \\[bad\\] failed to append."));
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#exceptionsShouldNotCauseHalting */
    @org.junit.Test(timeout = 10000)
    public void exceptionsShouldNotCauseHalting_add2150() throws java.lang.InterruptedException {
        ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
        npeAppender.setName("bad");
        npeAppender.setContext(context);
        npeAppender.start();
        asyncAppenderBase.addAppender(npeAppender);
        asyncAppenderBase.start();
        org.junit.Assert.assertTrue(asyncAppenderBase.isStarted());
        for (int i = 0; i < 10; i++)
            asyncAppenderBase.append(i);
        
        // MethodCallAdder
        asyncAppenderBase.stop();
        // AssertGenerator add assertion
        java.util.ArrayList collection_802531044 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_802531044, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        org.junit.Assert.assertEquals(ch.qos.logback.core.AppenderBase.ALLOWED_REPEATS, statusChecker.matchCount("Appender \\[bad\\] failed to append."));
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#exceptionsShouldNotCauseHalting */
    @org.junit.Test(timeout = 10000)
    public void exceptionsShouldNotCauseHalting_add2147_literalMutation2206_failAssert30() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_18_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_14_1 = 256;
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1000;
            ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
            npeAppender.setName("bad");
            npeAppender.setContext(context);
            npeAppender.start();
            // MethodCallAdder
            asyncAppenderBase.addAppender(npeAppender);
            // AssertGenerator add assertion
            java.util.ArrayList collection_243195747 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_243195747, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.addAppender(npeAppender);
            asyncAppenderBase.start();
            // MethodAssertGenerator build local variable
            Object o_24_0 = asyncAppenderBase.isStarted();
            for (int i = 0; i < 10; i++)
                asyncAppenderBase.append(i);
            
            asyncAppenderBase.stop();
            // MethodAssertGenerator build local variable
            Object o_32_0 = asyncAppenderBase.isStarted();
            // MethodAssertGenerator build local variable
            Object o_34_0 = statusChecker.matchCount("ZG5qmr`1s$.raz`39dL#}_3(,uc,zul%aS");
            org.junit.Assert.fail("exceptionsShouldNotCauseHalting_add2147_literalMutation2206 should have thrown PatternSyntaxException");
        } catch (java.util.regex.PatternSyntaxException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#exceptionsShouldNotCauseHalting */
    @org.junit.Test(timeout = 10000)
    public void exceptionsShouldNotCauseHalting_add2147_cf2216_failAssert24() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_18_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_14_1 = 256;
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1000;
            ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer> npeAppender = new ch.qos.logback.core.testUtil.NPEAppender<java.lang.Integer>();
            npeAppender.setName("bad");
            npeAppender.setContext(context);
            npeAppender.start();
            // MethodCallAdder
            asyncAppenderBase.addAppender(npeAppender);
            // AssertGenerator add assertion
            java.util.ArrayList collection_243195747 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_243195747, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.addAppender(npeAppender);
            asyncAppenderBase.start();
            // MethodAssertGenerator build local variable
            Object o_24_0 = asyncAppenderBase.isStarted();
            for (int i = 0; i < 10; i++)
                asyncAppenderBase.append(i);
            
            asyncAppenderBase.stop();
            // MethodAssertGenerator build local variable
            Object o_32_0 = asyncAppenderBase.isStarted();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1 = "Appender \\[bad\\] failed to append.";
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_206 = (ch.qos.logback.core.Appender)null;
            // StatementAdderMethod cloned existing statement
            vc_206.setName(String_vc_1);
            // MethodAssertGenerator build local variable
            Object o_40_0 = statusChecker.matchCount("Appender \\[bad\\] failed to append.");
            org.junit.Assert.fail("exceptionsShouldNotCauseHalting_add2147_cf2216 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#invalidQueueCapacityShouldResultInNonStartedAppender */
    @org.junit.Test(timeout = 10000)
    public void invalidQueueCapacityShouldResultInNonStartedAppender_add2942() {
        // MethodCallAdder
        asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2049653888 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2049653888, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
        asyncAppenderBase.setQueueSize(0);
        org.junit.Assert.assertEquals(0, asyncAppenderBase.getQueueSize());
        asyncAppenderBase.start();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        statusChecker.assertContainsMatch("Invalid queue size");
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#invalidQueueCapacityShouldResultInNonStartedAppender */
    @org.junit.Test(timeout = 10000)
    public void invalidQueueCapacityShouldResultInNonStartedAppender_add2944() {
        asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
        asyncAppenderBase.setQueueSize(0);
        org.junit.Assert.assertEquals(0, asyncAppenderBase.getQueueSize());
        // MethodCallAdder
        asyncAppenderBase.start();
        // AssertGenerator add assertion
        java.util.ArrayList collection_1634782256 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1634782256, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.start();
        org.junit.Assert.assertFalse(asyncAppenderBase.isStarted());
        statusChecker.assertContainsMatch("Invalid queue size");
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#invalidQueueCapacityShouldResultInNonStartedAppender */
    @org.junit.Test(timeout = 10000)
    public void invalidQueueCapacityShouldResultInNonStartedAppender_cf2963_failAssert10_literalMutation3115() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
            asyncAppenderBase.setQueueSize(-1);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1337929299 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1337929299, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), -1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            // MethodAssertGenerator build local variable
            Object o_4_0 = asyncAppenderBase.getQueueSize();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, -1);
            asyncAppenderBase.start();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_249 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_249, "");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_246 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_246);
            // StatementAdderMethod cloned existing statement
            vc_246.setName(vc_249);
            // MethodAssertGenerator build local variable
            Object o_13_0 = asyncAppenderBase.isStarted();
            statusChecker.assertContainsMatch("Invalid queue size");
            org.junit.Assert.fail("invalidQueueCapacityShouldResultInNonStartedAppender_cf2963 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#invalidQueueCapacityShouldResultInNonStartedAppender */
    @org.junit.Test(timeout = 10000)
    public void invalidQueueCapacityShouldResultInNonStartedAppender_add2943_literalMutation3009_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_11_1 = 0;
            // MethodAssertGenerator build local variable
            Object o_7_1 = 1000;
            asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
            // MethodCallAdder
            asyncAppenderBase.setQueueSize(0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1736048681 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1736048681, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_15_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // MethodAssertGenerator build local variable
            Object o_17_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.setQueueSize(0);
            // MethodAssertGenerator build local variable
            Object o_20_0 = asyncAppenderBase.getQueueSize();
            asyncAppenderBase.start();
            // MethodAssertGenerator build local variable
            Object o_23_0 = asyncAppenderBase.isStarted();
            statusChecker.assertContainsMatch("Inv{alid queue size");
            org.junit.Assert.fail("invalidQueueCapacityShouldResultInNonStartedAppender_add2943_literalMutation3009 should have thrown PatternSyntaxException");
        } catch (java.util.regex.PatternSyntaxException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#invalidQueueCapacityShouldResultInNonStartedAppender */
    @org.junit.Test(timeout = 10000)
    public void invalidQueueCapacityShouldResultInNonStartedAppender_cf2961_failAssert8_add3076() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
            // AssertGenerator add assertion
            java.util.ArrayList collection_55701354 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_55701354, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<java.lang.Integer>());
            asyncAppenderBase.setQueueSize(0);
            // MethodAssertGenerator build local variable
            Object o_4_0 = asyncAppenderBase.getQueueSize();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 0);
            asyncAppenderBase.start();
            // StatementAdderOnAssert create null value
            java.lang.String vc_248 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_248);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_246 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_246);
            // StatementAdderMethod cloned existing statement
            vc_246.setName(vc_248);
            // MethodAssertGenerator build local variable
            Object o_13_0 = asyncAppenderBase.isStarted();
            statusChecker.assertContainsMatch("Invalid queue size");
            org.junit.Assert.fail("invalidQueueCapacityShouldResultInNonStartedAppender_cf2961 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_INTERUPPTED_cf27102_failAssert0_add27113() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(listAppender);
            // MethodCallAdder
            asyncAppenderBase.start();
            // AssertGenerator add assertion
            java.util.ArrayList collection_2101721527 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2101721527, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_900 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_900);
            // StatementAdderMethod cloned existing statement
            vc_900.getName();
            // MethodAssertGenerator build local variable
            Object o_10_0 = java.lang.Thread.currentThread().isInterrupted();
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionFlagWhenStopping_INTERUPPTED_cf27102 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_add27582() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        // MethodCallAdder
        asyncAppenderBase.stop();
        // AssertGenerator add assertion
        java.util.ArrayList collection_764831036 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_764831036, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(java.lang.Thread.currentThread().isInterrupted());
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_add27580() {
        // MethodCallAdder
        asyncAppenderBase.addAppender(listAppender);
        // AssertGenerator add assertion
        java.util.ArrayList collection_73501703 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_73501703, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(java.lang.Thread.currentThread().isInterrupted());
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_cf27583_failAssert0_add27635() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            asyncAppenderBase.addAppender(listAppender);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1361580535 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1361580535, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.addAppender(listAppender);
            asyncAppenderBase.start();
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1090 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1090);
            // StatementAdderMethod cloned existing statement
            vc_1090.getName();
            // MethodAssertGenerator build local variable
            Object o_8_0 = java.lang.Thread.currentThread().isInterrupted();
            org.junit.Assert.fail("verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_cf27583 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_cf27583_failAssert0_add27637() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(listAppender);
            asyncAppenderBase.start();
            // MethodCallAdder
            asyncAppenderBase.stop();
            // AssertGenerator add assertion
            java.util.ArrayList collection_627800674 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_627800674, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1090 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1090);
            // StatementAdderMethod cloned existing statement
            vc_1090.getName();
            // MethodAssertGenerator build local variable
            Object o_8_0 = java.lang.Thread.currentThread().isInterrupted();
            org.junit.Assert.fail("verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_cf27583 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_add27580_cf27604_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_9_1 = 256;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 1000;
            // MethodCallAdder
            asyncAppenderBase.addAppender(listAppender);
            // AssertGenerator add assertion
            java.util.ArrayList collection_73501703 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_73501703, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // MethodAssertGenerator build local variable
            Object o_15_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.addAppender(listAppender);
            asyncAppenderBase.start();
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1109 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1106 = (ch.qos.logback.core.Appender)null;
            // StatementAdderMethod cloned existing statement
            vc_1106.setName(vc_1109);
            // MethodAssertGenerator build local variable
            Object o_26_0 = java.lang.Thread.currentThread().isInterrupted();
            org.junit.Assert.fail("verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED_add27580_cf27604 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27984_failAssert2_literalMutation28016() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.doAppend(new java.lang.Integer(2));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1311722747 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1311722747, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1259 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1259, "");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1259);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27984 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27983_failAssert1_add28000() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            // MethodCallAdder
            asyncAppenderBase.doAppend(new java.lang.Integer(0));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1032910486 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1032910486, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 255);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.doAppend(new java.lang.Integer(0));
            // StatementAdderOnAssert create null value
            java.lang.String vc_1258 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1258);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1258);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27983 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27983_failAssert1_literalMutation28006() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.doAppend(new java.lang.Integer(2));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1794982144 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1794982144, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            // StatementAdderOnAssert create null value
            java.lang.String vc_1258 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1258);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1258);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27983 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27983_failAssert1_add27998() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            // MethodCallAdder
            asyncAppenderBase.start();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1061859635 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1061859635, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.doAppend(new java.lang.Integer(0));
            // StatementAdderOnAssert create null value
            java.lang.String vc_1258 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1258);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1258);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27983 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27983_failAssert1_literalMutation28004() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.doAppend(new java.lang.Integer(-1));
            // AssertGenerator add assertion
            java.util.ArrayList collection_1918446283 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1918446283, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            // StatementAdderOnAssert create null value
            java.lang.String vc_1258 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1258);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1258);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27983 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // Interruption of current thread when in doAppend method should not be
    // consumed by async appender. See also http://jira.qos.ch/browse/LOGBACK-910
    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionIsNotSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionIsNotSwallowed_cf27983_failAssert1_literalMutation28003() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            java.lang.Thread.currentThread().interrupt();
            asyncAppenderBase.doAppend(new java.lang.Integer(1));
            // AssertGenerator add assertion
            java.util.ArrayList collection_476480008 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_476480008, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 255);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            // StatementAdderOnAssert create null value
            java.lang.String vc_1258 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1258);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1256 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1256);
            // StatementAdderMethod cloned existing statement
            vc_1256.setName(vc_1258);
            // MethodAssertGenerator build local variable
            Object o_13_0 = java.lang.Thread.currentThread().isInterrupted();
            // clear interrupt flag for subsequent tests
            java.lang.Thread.interrupted();
            org.junit.Assert.fail("verifyInterruptionIsNotSwallowed_cf27983 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionOfWorkerIsSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionOfWorkerIsSwallowed_add28683() {
        // MethodCallAdder
        asyncAppenderBase.addAppender(delayingListAppender);
        // AssertGenerator add assertion
        java.util.ArrayList collection_929755521 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_929755521, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.worker.isInterrupted());
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionOfWorkerIsSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionOfWorkerIsSwallowed_add28684() {
        asyncAppenderBase.addAppender(delayingListAppender);
        // MethodCallAdder
        asyncAppenderBase.start();
        // AssertGenerator add assertion
        java.util.ArrayList collection_280312647 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_280312647, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        org.junit.Assert.assertFalse(asyncAppenderBase.worker.isInterrupted());
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionOfWorkerIsSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionOfWorkerIsSwallowed_cf28686_failAssert0_add28740() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            // MethodCallAdder
            asyncAppenderBase.stop();
            // AssertGenerator add assertion
            java.util.ArrayList collection_463162095 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_463162095, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime(), 1000);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize(), 256);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold(), 51);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName());
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1490 = (ch.qos.logback.core.Appender)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1490);
            // StatementAdderMethod cloned existing statement
            vc_1490.getName();
            // MethodAssertGenerator build local variable
            Object o_8_0 = asyncAppenderBase.worker.isInterrupted();
            org.junit.Assert.fail("verifyInterruptionOfWorkerIsSwallowed_cf28686 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.AsyncAppenderBaseTest#verifyInterruptionOfWorkerIsSwallowed */
    @org.junit.Test(timeout = 10000)
    public void verifyInterruptionOfWorkerIsSwallowed_add28685_cf28734_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = 0;
            // MethodAssertGenerator build local variable
            Object o_17_1 = 51;
            // MethodAssertGenerator build local variable
            Object o_13_1 = 256;
            // MethodAssertGenerator build local variable
            Object o_9_1 = 1000;
            // MethodAssertGenerator build local variable
            Object o_7_1 = 256;
            asyncAppenderBase.addAppender(delayingListAppender);
            asyncAppenderBase.start();
            // MethodCallAdder
            asyncAppenderBase.stop();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1205502970 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1205502970, ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getCopyOfAttachedFiltersList());;
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getRemainingCapacity();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getMaxFlushTime();
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isStarted();
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getQueueSize();
            // MethodAssertGenerator build local variable
            Object o_15_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).isNeverBlock();
            // MethodAssertGenerator build local variable
            Object o_17_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getDiscardingThreshold();
            // MethodAssertGenerator build local variable
            Object o_19_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getNumberOfElementsInQueue();
            // MethodAssertGenerator build local variable
            Object o_21_0 = ((ch.qos.logback.core.AsyncAppenderBase)asyncAppenderBase).getName();
            asyncAppenderBase.stop();
            // StatementAdderOnAssert create null value
            java.lang.String vc_1528 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.Appender vc_1526 = (ch.qos.logback.core.Appender)null;
            // StatementAdderMethod cloned existing statement
            vc_1526.setName(vc_1528);
            // MethodAssertGenerator build local variable
            Object o_30_0 = asyncAppenderBase.worker.isInterrupted();
            org.junit.Assert.fail("verifyInterruptionOfWorkerIsSwallowed_add28685_cf28734 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

