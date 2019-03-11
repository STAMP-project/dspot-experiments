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
import java.util.Collections;
import java.util.Map;
import net.logstash.logback.fieldnames.LogstashFieldNames;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class MdcJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private MdcJsonProvider provider = new MdcJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    private Map<String, String> mdc;

    @Test
    public void testUnwrapped() throws IOException {
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeFieldName("name1");
        Mockito.verify(generator).writeObject("value1");
        Mockito.verify(generator).writeFieldName("name2");
        Mockito.verify(generator).writeObject("value2");
        Mockito.verify(generator).writeFieldName("name3");
        Mockito.verify(generator).writeObject("value3");
    }

    @Test
    public void testWrapped() throws IOException {
        provider.setFieldName("mdc");
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeObjectFieldStart("mdc");
        inOrder.verify(generator).writeFieldName("name1");
        inOrder.verify(generator).writeObject("value1");
        inOrder.verify(generator).writeFieldName("name2");
        inOrder.verify(generator).writeObject("value2");
        inOrder.verify(generator).writeFieldName("name3");
        inOrder.verify(generator).writeObject("value3");
        inOrder.verify(generator).writeEndObject();
    }

    @Test
    public void testWrappedUsingFieldNames() throws IOException {
        LogstashFieldNames fieldNames = new LogstashFieldNames();
        fieldNames.setMdc("mdc");
        provider.setFieldNames(fieldNames);
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeObjectFieldStart("mdc");
        inOrder.verify(generator).writeFieldName("name1");
        inOrder.verify(generator).writeObject("value1");
        inOrder.verify(generator).writeFieldName("name2");
        inOrder.verify(generator).writeObject("value2");
        inOrder.verify(generator).writeFieldName("name3");
        inOrder.verify(generator).writeObject("value3");
        inOrder.verify(generator).writeEndObject();
    }

    @Test
    public void testInclude() throws IOException {
        provider.setIncludeMdcKeyNames(Collections.singletonList("name1"));
        provider.writeTo(generator, event);
        Mockito.verify(generator).writeFieldName("name1");
        Mockito.verify(generator).writeObject("value1");
        Mockito.verify(generator, Mockito.never()).writeFieldName("name2");
        Mockito.verify(generator, Mockito.never()).writeObject("value2");
        Mockito.verify(generator, Mockito.never()).writeFieldName("name3");
        Mockito.verify(generator, Mockito.never()).writeObject("value3");
    }

    @Test
    public void testExclude() throws IOException {
        provider.setExcludeMdcKeyNames(Collections.singletonList("name1"));
        provider.writeTo(generator, event);
        Mockito.verify(generator, Mockito.never()).writeFieldName("name1");
        Mockito.verify(generator, Mockito.never()).writeObject("value1");
        Mockito.verify(generator).writeFieldName("name2");
        Mockito.verify(generator).writeObject("value2");
        Mockito.verify(generator).writeFieldName("name3");
        Mockito.verify(generator).writeObject("value3");
    }
}

