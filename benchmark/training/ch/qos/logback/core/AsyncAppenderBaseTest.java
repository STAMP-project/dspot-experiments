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


import AppenderBase.ALLOWED_REPEATS;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.testUtil.DelayingListAppender;
import ch.qos.logback.core.testUtil.NPEAppender;
import ch.qos.logback.core.testUtil.StatusChecker;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Torsten Juergeleit
 */
public class AsyncAppenderBaseTest {
    Context context = new ContextBase();

    AsyncAppenderBase<Integer> asyncAppenderBase = new AsyncAppenderBase<Integer>();

    AsyncAppenderBaseTest.LossyAsyncAppender lossyAsyncAppender = new AsyncAppenderBaseTest.LossyAsyncAppender();

    DelayingListAppender<Integer> delayingListAppender = new DelayingListAppender<Integer>();

    ListAppender<Integer> listAppender = new ListAppender<Integer>();

    OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();

    StatusChecker statusChecker = new StatusChecker(context);

    @Test(timeout = 2000)
    public void smoke() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(0);
        asyncAppenderBase.stop();
        verify(listAppender, 1);
    }

    @Test
    public void exceptionsShouldNotCauseHalting() throws InterruptedException {
        NPEAppender<Integer> npeAppender = new NPEAppender<Integer>();
        npeAppender.setName("bad");
        npeAppender.setContext(context);
        npeAppender.start();
        asyncAppenderBase.addAppender(npeAppender);
        asyncAppenderBase.start();
        Assert.assertTrue(asyncAppenderBase.isStarted());
        for (int i = 0; i < 10; i++)
            asyncAppenderBase.append(i);

        asyncAppenderBase.stop();
        Assert.assertFalse(asyncAppenderBase.isStarted());
        Assert.assertEquals(ALLOWED_REPEATS, matchCount("Appender \\[bad\\] failed to append."));
    }

    @Test(timeout = 2000)
    public void emptyQueueShouldBeStoppable() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        verify(listAppender, 0);
    }

    @Test(timeout = 2000)
    public void workerShouldStopEvenIfInterruptExceptionConsumedWithinSubappender() {
        delayingListAppender.delay = 100;
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(0);
        asyncAppenderBase.stop();
        verify(delayingListAppender, 1);
        Assert.assertTrue(delayingListAppender.interrupted);
        Thread.interrupted();
    }

    @Test(timeout = 2000)
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

    @Test(timeout = 2000)
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

    @Test(timeout = 2000)
    public void lossyAppenderShouldOnlyLoseCertainEvents() {
        int bufferSize = 5;
        int loopLen = bufferSize * 2;
        addAppender(delayingListAppender);
        setQueueSize(bufferSize);
        setDiscardingThreshold(1);
        start();
        for (int i = 0; i < loopLen; i++) {
            doAppend(i);
        }
        stop();
        // events 0, 3, 6 and 9 are discardable. However, for events 0 and 3
        // the buffer is not not yet full. Thus, only events 6 and 9 will be
        // effectively discarded.
        verify(delayingListAppender, (loopLen - 2));
    }

    @Test(timeout = 2000)
    public void lossyAppenderShouldBeNonLossyIfDiscardingThresholdIsZero() {
        int bufferSize = 5;
        int loopLen = bufferSize * 2;
        addAppender(delayingListAppender);
        setQueueSize(bufferSize);
        setDiscardingThreshold(0);
        start();
        for (int i = 0; i < loopLen; i++) {
            doAppend(i);
        }
        stop();
        verify(delayingListAppender, loopLen);
    }

    @Test
    public void invalidQueueCapacityShouldResultInNonStartedAppender() {
        asyncAppenderBase.addAppender(new ch.qos.logback.core.helpers.NOPAppender<Integer>());
        asyncAppenderBase.setQueueSize(0);
        Assert.assertEquals(0, asyncAppenderBase.getQueueSize());
        asyncAppenderBase.start();
        Assert.assertFalse(asyncAppenderBase.isStarted());
        statusChecker.assertContainsMatch("Invalid queue size");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void workerThreadFlushesOnStop() {
        int loopLen = 5;
        int maxRuntime = (loopLen + 1) * (Math.max(1000, delayingListAppender.delay));
        ListAppender<Integer> la = delayingListAppender;
        asyncAppenderBase.addAppender(la);
        asyncAppenderBase.setDiscardingThreshold(0);
        asyncAppenderBase.setMaxFlushTime(maxRuntime);
        asyncAppenderBase.start();
        asyncAppenderBase.worker.suspend();
        for (int i = 0; i < loopLen; i++) {
            asyncAppenderBase.doAppend(i);
        }
        Assert.assertEquals(loopLen, asyncAppenderBase.getNumberOfElementsInQueue());
        Assert.assertEquals(0, la.list.size());
        asyncAppenderBase.worker.resume();
        asyncAppenderBase.stop();
        Assert.assertEquals(0, asyncAppenderBase.getNumberOfElementsInQueue());
        verify(la, loopLen);
    }

    // @SuppressWarnings("deprecation")
    @Test
    public void stopExitsWhenMaxRuntimeReached() throws InterruptedException {
        int maxFlushTime = 1;// runtime of 0 means wait forever, so use 1 ms instead

        int loopLen = 10;
        ListAppender<Integer> la = delayingListAppender;
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
    @Test
    public void verifyInterruptionIsNotSwallowed() {
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        Thread.currentThread().interrupt();
        asyncAppenderBase.doAppend(Integer.valueOf(0));
        Assert.assertTrue(Thread.currentThread().isInterrupted());
        // clear interrupt flag for subsequent tests
        Thread.interrupted();
    }

    // Interruption of current thread should not prevent logging.
    // See also http://jira.qos.ch/browse/LOGBACK-910
    // and https://jira.qos.ch/browse/LOGBACK-1247
    @Test
    public void verifyInterruptionDoesNotPreventLogging() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.doAppend(Integer.valueOf(0));
        Thread.currentThread().interrupt();
        asyncAppenderBase.doAppend(Integer.valueOf(1));
        asyncAppenderBase.doAppend(Integer.valueOf(1));
        Assert.assertTrue(Thread.currentThread().isInterrupted());
        // the interruption needs to be consumed
        Thread.interrupted();
        asyncAppenderBase.stop();
        verify(listAppender, 3);
    }

    @Test
    public void verifyInterruptionFlagWhenStopping_INTERUPPTED() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        Thread.currentThread().interrupt();
        asyncAppenderBase.stop();
        Assert.assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @Test
    public void verifyInterruptionFlagWhenStopping_NOT_INTERUPPTED() {
        asyncAppenderBase.addAppender(listAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        Assert.assertFalse(Thread.currentThread().isInterrupted());
    }

    @Test
    public void verifyInterruptionOfWorkerIsSwallowed() {
        asyncAppenderBase.addAppender(delayingListAppender);
        asyncAppenderBase.start();
        asyncAppenderBase.stop();
        Assert.assertFalse(asyncAppenderBase.worker.isInterrupted());
    }

    static class LossyAsyncAppender extends AsyncAppenderBase<Integer> {
        @Override
        protected boolean isDiscardable(Integer i) {
            return (i % 3) == 0;
        }
    }

    @Test
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
}

