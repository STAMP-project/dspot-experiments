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


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.appender.listener.AppenderListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DelegatingAsyncDisruptorAppenderTest {
    private static final int VERIFICATION_TIMEOUT = 1000 * 30;

    @InjectMocks
    private DelegatingAsyncDisruptorAppender<ILoggingEvent, AppenderListener<ILoggingEvent>> appender = new DelegatingAsyncDisruptorAppender<ILoggingEvent, AppenderListener<ILoggingEvent>>() {};

    @Mock
    private ILoggingEvent event;

    @Mock
    private Appender<ILoggingEvent> delegate;

    @Test
    public void testEventHandlerCalled() throws Exception {
        appender.start();
        Mockito.verify(delegate).start();
        appender.append(event);
        Mockito.verify(delegate, Mockito.timeout(DelegatingAsyncDisruptorAppenderTest.VERIFICATION_TIMEOUT)).doAppend(event);
    }
}

