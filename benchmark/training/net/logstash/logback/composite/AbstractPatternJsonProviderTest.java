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
package net.logstash.logback.composite;


import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.logstash.logback.pattern.AbstractJsonPatternParser;
import net.logstash.logback.pattern.NodeWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * This test just verifies that AbstractPatternJsonProvider delegates all the work to Parser.
 *
 * @param <Event>
 * 		- type of the event (ILoggingEvent, IAccessEvent)
 * @author <a href="mailto:dimas@dataart.com">Dmitry Andrianov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractPatternJsonProviderTest<Event extends DeferredProcessingAware> {
    // What our TestNodeWriter generates when invoked
    public static final String TEST_NODEWRITER_RESULT = "generated string";

    @Mock
    private Event event;

    @Mock
    private JsonGenerator generator;

    @Mock
    private JsonFactory jsonFactory;

    private AbstractJsonPatternParser<Event> parser;

    @Mock
    private NodeWriter<Event> nodeWriter;

    private AbstractPatternJsonProvider<Event> provider;

    @Test
    public void shouldDelegateToParser() throws IOException {
        // pattern used does not matter because decorated "parser" will always generate TEST_NODEWRITER_RESULT
        final String pattern = "{\"key\":\"value\"}";
        provider.setPattern(pattern);
        provider.setJsonFactory(jsonFactory);
        provider.start();
        // should actually invoke parser with the pattern requested
        Mockito.verify(parser).parse(ArgumentMatchers.eq(pattern));
        provider.writeTo(generator, event);
        // and the end result should be what NodeWriter returned by the parser produces
        Mockito.verify(nodeWriter).write(generator, event);
    }
}

