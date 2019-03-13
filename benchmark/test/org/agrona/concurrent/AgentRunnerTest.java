/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.concurrent;


import AgentRunner.RETRY_CLOSE_TIMEOUT_MS;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.agrona.ErrorHandler;
import org.agrona.LangUtil;
import org.agrona.collections.MutableBoolean;
import org.agrona.concurrent.status.AtomicCounter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class AgentRunnerTest {
    private final AtomicCounter mockAtomicCounter = Mockito.mock(AtomicCounter.class);

    private final ErrorHandler mockErrorHandler = Mockito.mock(ErrorHandler.class);

    private final Agent mockAgent = Mockito.mock(Agent.class);

    private final IdleStrategy idleStrategy = new NoOpIdleStrategy();

    private final AgentRunner runner = new AgentRunner(idleStrategy, mockErrorHandler, mockAtomicCounter, mockAgent);

    @Test
    public void shouldReturnAgent() {
        Assert.assertThat(runner.agent(), Matchers.is(mockAgent));
    }

    @Test
    public void shouldNotDoWorkOnClosedRunner() throws Exception {
        runner.close();
        runner.run();
        Mockito.verify(mockAgent, Mockito.never()).onStart();
        Mockito.verify(mockAgent, Mockito.never()).doWork();
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(mockAtomicCounter, Mockito.never()).increment();
        Mockito.verify(mockAgent, Mockito.times(1)).onClose();
        Assert.assertTrue(runner.isClosed());
    }

    @Test
    public void shouldHandleAgentTerminationExceptionThrownByAgent() throws Exception {
        final RuntimeException expectedException = new AgentTerminationException();
        Mockito.when(mockAgent.doWork()).thenThrow(expectedException);
        runner.run();
        Mockito.verify(mockAgent).doWork();
        Mockito.verify(mockErrorHandler).onError(expectedException);
        Mockito.verify(mockAtomicCounter).increment();
        Mockito.verify(mockAgent, Mockito.times(1)).onClose();
        Assert.assertTrue(runner.isClosed());
    }

    @Test
    public void shouldReportExceptionThrownByAgent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final RuntimeException expectedException = new RuntimeException();
        Mockito.when(mockAgent.doWork()).thenThrow(expectedException);
        Mockito.doAnswer(( invocation) -> {
            latch.countDown();
            return null;
        }).when(mockErrorHandler).onError(expectedException);
        new Thread(runner).start();
        if (!(latch.await(3, TimeUnit.SECONDS))) {
            Assert.fail("Should have called error handler");
        }
        Mockito.verify(mockAgent, Mockito.times(1)).onStart();
        Mockito.verify(mockAgent, Mockito.atLeastOnce()).doWork();
        Mockito.verify(mockErrorHandler, Mockito.atLeastOnce()).onError(expectedException);
        Mockito.verify(mockAtomicCounter, Mockito.atLeastOnce()).increment();
        runner.close();
    }

    @Test
    public void shouldNotReportClosedByInterruptException() throws Exception {
        Mockito.when(mockAgent.doWork()).thenThrow(new ClosedByInterruptException());
        assertExceptionNotReported();
    }

    @Test
    public void shouldNotReportRethrownClosedByInterruptException() throws Exception {
        Mockito.when(mockAgent.doWork()).thenAnswer(( inv) -> {
            try {
                throw new ClosedByInterruptException();
            } catch (final  ex) {
                LangUtil.rethrowUnchecked(ex);
            }
            return null;
        });
        assertExceptionNotReported();
    }

    @Test
    public void shouldRaiseInterruptFlagByClose() throws Exception {
        Mockito.when(mockAgent.doWork()).then(AdditionalAnswers.answersWithDelay(500, Mockito.RETURNS_DEFAULTS));
        new Thread(runner).start();
        while ((runner.thread()) == null) {
            Thread.yield();
        } 
        Thread.currentThread().interrupt();
        final MutableBoolean actionCalled = new MutableBoolean();
        final Consumer<Thread> failedCloseAction = ( ignore) -> actionCalled.set(true);
        runner.close(RETRY_CLOSE_TIMEOUT_MS, failedCloseAction);
        Assert.assertTrue(Thread.interrupted());
        Assert.assertTrue(actionCalled.get());
    }

    @Test
    public void shouldInvokeActionOnRetryCloseTimeout() throws Exception {
        Mockito.when(mockAgent.doWork()).then(AdditionalAnswers.answersWithDelay(500, Mockito.RETURNS_DEFAULTS));
        final Thread agentRunnerThread = new Thread(runner);
        agentRunnerThread.start();
        Thread.sleep(100);
        final AtomicInteger closeTimeoutCalls = new AtomicInteger();
        runner.close(1, ( t) -> {
            closeTimeoutCalls.incrementAndGet();
            assertSame(t, agentRunnerThread);
        });
        Assert.assertThat(closeTimeoutCalls.get(), Matchers.greaterThan(0));
    }
}

