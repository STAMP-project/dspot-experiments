/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.logstash.logback.appender;


import Status.ERROR;
import Status.WARN;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.lmax.disruptor.EventHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.logstash.logback.appender.AsyncDisruptorAppender.LogEvent;
import net.logstash.logback.appender.listener.AppenderListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class AsyncDisruptorAppenderTest {
    private static final int VERIFICATION_TIMEOUT = 1000 * 30;

    @InjectMocks
    private AsyncDisruptorAppender<ILoggingEvent, AppenderListener<ILoggingEvent>> appender = new AsyncDisruptorAppender<ILoggingEvent, AppenderListener<ILoggingEvent>>() {};

    @Mock
    private EventHandler<LogEvent<ILoggingEvent>> eventHandler;

    @Mock
    private Context context;

    @Mock
    private StatusManager statusManager;

    @Mock
    private ILoggingEvent event1;

    @Mock
    private ILoggingEvent event2;

    @Mock
    private AppenderListener<ILoggingEvent> listener;

    @SuppressWarnings("unchecked")
    @Test
    public void testEventHandlerCalled() throws Exception {
        final AtomicReference<Object> capturedEvent = new AtomicReference<Object>();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                capturedEvent.set(invocation.<LogEvent>getArgument(0).event);
                return null;
            }
        }).when(eventHandler).onEvent(ArgumentMatchers.any(LogEvent.class), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        appender.start();
        Mockito.verify(listener).appenderStarted(appender);
        appender.append(event1);
        Mockito.verify(listener).eventAppended(ArgumentMatchers.eq(appender), ArgumentMatchers.eq(event1), ArgumentMatchers.anyLong());
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        Mockito.verify(eventHandler, Mockito.timeout(AsyncDisruptorAppenderTest.VERIFICATION_TIMEOUT)).onEvent(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        // When eventHandler is invoked, the event should be event1
        Assert.assertEquals(event1, capturedEvent.get());
        // The event should be set back to null after invocation
        Assert.assertNull(captor.getValue().event);
        Mockito.verify(event1).prepareForDeferredProcessing();
        appender.stop();
        Mockito.verify(listener).appenderStopped(appender);
    }

    @Test
    public void testThreadDaemon() throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        appender.setDaemon(true);
        assertThat(appender.getThreadFactory().newThread(runnable).isDaemon()).isTrue();
        appender.setDaemon(false);
        assertThat(appender.getThreadFactory().newThread(runnable).isDaemon()).isFalse();
    }

    @Test
    public void testThreadName() throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        appender.setThreadNameFormat("threadNamePrefix");
        assertThat(appender.getThreadFactory().newThread(runnable).getName()).startsWith("threadNamePrefix");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEventDroppedWhenFull() throws Exception {
        appender.setRingBufferSize(1);
        appender.start();
        final CountDownLatch eventHandlerWaiter = new CountDownLatch(1);
        final CountDownLatch mainWaiter = new CountDownLatch(1);
        /* Cause the first event handling to block until we're done with the test. */
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mainWaiter.countDown();
                eventHandlerWaiter.await();
                return null;
            }
        }).when(eventHandler).onEvent(ArgumentMatchers.any(LogEvent.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        /* This one will block during event handling */
        appender.append(event1);
        mainWaiter.await(AsyncDisruptorAppenderTest.VERIFICATION_TIMEOUT, TimeUnit.MILLISECONDS);
        /* This one should be dropped */
        appender.append(event2);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(statusManager, Mockito.timeout(AsyncDisruptorAppenderTest.VERIFICATION_TIMEOUT)).add(statusCaptor.capture());
        Assert.assertEquals(WARN, statusCaptor.getValue().getLevel());
        Assert.assertTrue(statusCaptor.getValue().getMessage().startsWith("Dropped"));
        eventHandlerWaiter.countDown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEventHandlerThrowsException() throws Exception {
        appender.start();
        final Throwable throwable = new RuntimeException("message");
        Mockito.doThrow(throwable).when(eventHandler).onEvent(ArgumentMatchers.any(LogEvent.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        appender.append(event1);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(statusManager, Mockito.timeout(AsyncDisruptorAppenderTest.VERIFICATION_TIMEOUT)).add(statusCaptor.capture());
        Assert.assertEquals(ERROR, statusCaptor.getValue().getLevel());
        Assert.assertTrue(statusCaptor.getValue().getMessage().startsWith("Unable to process event"));
    }
}

