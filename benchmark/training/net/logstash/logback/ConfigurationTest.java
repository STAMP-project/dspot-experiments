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
package net.logstash.logback;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import net.logstash.logback.appender.AsyncDisruptorAppender;
import net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender;
import net.logstash.logback.appender.listener.AppenderListener;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class ConfigurationTest {
    private static final Logger LOGGER = ((Logger) (LoggerFactory.getLogger(ConfigurationTest.class)));

    private final ListAppender<ILoggingEvent> listAppender = ((ListAppender<ILoggingEvent>) (ConfigurationTest.LOGGER.getAppender("listAppender")));

    private final JsonFactory jsonFactory = new MappingJsonFactory();

    @Test
    public void testLogstashEncoderAppender() throws IOException {
        LoggingEventCompositeJsonEncoder encoder = getEncoder("logstashEncoderAppender");
        List<JsonProvider<ILoggingEvent>> providers = encoder.getProviders().getProviders();
        Assert.assertEquals(21, providers.size());
        verifyCommonProviders(providers);
        verifyOutput(encoder);
    }

    @Test
    public void testLoggingEventCompositeJsonEncoderAppender() throws IOException {
        LoggingEventCompositeJsonEncoder encoder = getEncoder("loggingEventCompositeJsonEncoderAppender");
        List<JsonProvider<ILoggingEvent>> providers = encoder.getProviders().getProviders();
        Assert.assertEquals(25, providers.size());
        verifyCommonProviders(providers);
        Assert.assertNotNull(getInstance(providers, TestJsonProvider.class));
        verifyOutput(encoder);
    }

    @Test
    public void testAppenderHasListener() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        LoggingEventAsyncDisruptorAppender appender = getAppender("asyncAppender");
        Field listenersField = AsyncDisruptorAppender.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        List<AppenderListener<ILoggingEvent>> listeners = ((List<AppenderListener<ILoggingEvent>>) (listenersField.get(appender)));
        Assert.assertEquals(1, listeners.size());
    }
}

