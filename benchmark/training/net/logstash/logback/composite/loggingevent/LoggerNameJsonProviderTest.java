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
package net.logstash.logback.composite.loggingevent;


import LoggerNameJsonProvider.FIELD_LOGGER_NAME;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.logstash.logback.fieldnames.LogstashFieldNames;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class LoggerNameJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private LoggerNameJsonProvider provider = new LoggerNameJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Test
    public void testFullName() throws IOException {
        Mockito.when(event.getLoggerName()).thenReturn(getClass().getName());
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField(FIELD_LOGGER_NAME, getClass().getName());
    }

    @Test
    public void testFieldName() throws IOException {
        provider.setFieldName("newFieldName");
        Mockito.when(event.getLoggerName()).thenReturn(getClass().getName());
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField("newFieldName", getClass().getName());
    }

    @Test
    public void testFieldNames() throws IOException {
        LogstashFieldNames fieldNames = new LogstashFieldNames();
        fieldNames.setLogger("newFieldName");
        provider.setFieldNames(fieldNames);
        Mockito.when(event.getLoggerName()).thenReturn(getClass().getName());
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField("newFieldName", getClass().getName());
    }

    @Test
    public void testShortName() throws IOException {
        provider.setShortenedLoggerNameLength(5);
        Mockito.when(event.getLoggerName()).thenReturn(getClass().getName());
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField(FIELD_LOGGER_NAME, "n.l.l.c.l.LoggerNameJsonProviderTest");
    }
}

