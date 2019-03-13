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


import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class LoggingEventNestedJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private LoggingEventNestedJsonProvider provider;

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Mock
    private LoggingEventJsonProviders providers;

    @Test
    public void testWrite() throws IOException {
        provider.setFieldName("newFieldName");
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator, providers);
        inOrder.verify(generator).writeFieldName("newFieldName");
        inOrder.verify(generator).writeStartObject();
        inOrder.verify(providers).writeTo(generator, event);
        inOrder.verify(generator).writeEndObject();
    }
}

