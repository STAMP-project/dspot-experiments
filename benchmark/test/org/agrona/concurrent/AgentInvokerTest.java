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


import java.nio.channels.ClosedByInterruptException;
import org.agrona.ErrorHandler;
import org.agrona.LangUtil;
import org.agrona.concurrent.status.AtomicCounter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AgentInvokerTest {
    private final ErrorHandler mockErrorHandler = Mockito.mock(ErrorHandler.class);

    private final AtomicCounter mockAtomicCounter = Mockito.mock(AtomicCounter.class);

    private final Agent mockAgent = Mockito.mock(Agent.class);

    private final AgentInvoker invoker = new AgentInvoker(mockErrorHandler, mockAtomicCounter, mockAgent);

    @Test
    public void shouldFollowLifecycle() throws Exception {
        invoker.start();
        invoker.start();
        Mockito.verify(mockAgent, Mockito.times(1)).onStart();
        Mockito.verifyNoMoreInteractions(mockAgent);
        invoker.invoke();
        invoker.invoke();
        Mockito.verify(mockAgent, Mockito.times(2)).doWork();
        Mockito.verifyNoMoreInteractions(mockAgent);
        invoker.close();
        invoker.close();
        Mockito.verify(mockAgent, Mockito.times(1)).onClose();
        Mockito.verifyNoMoreInteractions(mockAgent);
    }

    @Test
    public void shouldReturnAgent() {
        Assert.assertThat(invoker.agent(), Matchers.is(mockAgent));
    }

    @Test
    public void shouldNotDoWorkOnClosedRunnerButCallOnClose() throws Exception {
        invoker.close();
        invoker.invoke();
        Mockito.verify(mockAgent, Mockito.never()).onStart();
        Mockito.verify(mockAgent, Mockito.never()).doWork();
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(mockAtomicCounter, Mockito.never()).increment();
        Mockito.verify(mockAgent).onClose();
    }

    @Test
    public void shouldReportExceptionThrownByAgent() throws Exception {
        final RuntimeException expectedException = new RuntimeException();
        Mockito.when(mockAgent.doWork()).thenThrow(expectedException);
        invoker.start();
        invoker.invoke();
        Mockito.verify(mockAgent).doWork();
        Mockito.verify(mockErrorHandler).onError(expectedException);
        Mockito.verify(mockAtomicCounter).increment();
        Mockito.verify(mockAgent, Mockito.never()).onClose();
        Mockito.reset(mockAgent);
        invoker.invoke();
        Mockito.verify(mockAgent).doWork();
        Mockito.reset(mockAgent);
        invoker.close();
        Mockito.verify(mockAgent, Mockito.never()).doWork();
        Mockito.verify(mockAgent).onClose();
    }

    @Test
    public void shouldReportExceptionThrownOnStart() throws Exception {
        final RuntimeException expectedException = new RuntimeException();
        Mockito.doThrow(expectedException).when(mockAgent).onStart();
        invoker.start();
        invoker.invoke();
        Mockito.verify(mockAgent, Mockito.never()).doWork();
        Mockito.verify(mockErrorHandler).onError(expectedException);
        Mockito.verify(mockAgent).onClose();
        Assert.assertTrue(invoker.isStarted());
        Assert.assertFalse(invoker.isRunning());
        Assert.assertTrue(invoker.isClosed());
    }

    @Test
    public void shouldHandleAgentTerminationExceptionThrownByAgent() throws Exception {
        final RuntimeException expectedException = new AgentTerminationException();
        Mockito.when(mockAgent.doWork()).thenThrow(expectedException);
        invoker.start();
        invoker.invoke();
        Mockito.verify(mockAgent).doWork();
        Mockito.verify(mockErrorHandler).onError(expectedException);
        Mockito.verify(mockAtomicCounter).increment();
        Mockito.verify(mockAgent).onClose();
        Assert.assertTrue(invoker.isClosed());
        Mockito.reset(mockAgent);
        invoker.invoke();
        Mockito.verify(mockAgent, Mockito.never()).doWork();
        Assert.assertTrue(invoker.isClosed());
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
}

