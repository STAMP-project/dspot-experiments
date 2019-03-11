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


import ContextNameJsonProvider.FIELD_CONTEXT_NAME;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ContextNameJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ContextNameJsonProvider provider = new ContextNameJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Mock
    private LoggerContextVO loggerContext;

    @Test
    public void testDefaultName() throws IOException {
        Mockito.when(loggerContext.getName()).thenReturn("testcontext");
        Mockito.when(event.getLoggerContextVO()).thenReturn(loggerContext);
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField(FIELD_CONTEXT_NAME, "testcontext");
    }

    @Test
    public void testFieldName() throws IOException {
        provider.setFieldName("newFieldName");
        Mockito.when(loggerContext.getName()).thenReturn("testcontext");
        Mockito.when(event.getLoggerContextVO()).thenReturn(loggerContext);
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeStringField("newFieldName", "testcontext");
    }
}

