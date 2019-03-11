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


import TagsJsonProvider.FIELD_TAGS;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.logstash.logback.fieldnames.LogstashFieldNames;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Marker;


public class TagsJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TagsJsonProvider provider = new TagsJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Mock
    private Marker marker1;

    @Mock
    private LogstashMarker marker2;

    @Mock
    private Marker marker3;

    @Mock
    private Marker marker4;

    @Test
    public void testDefaultName() throws IOException {
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeArrayFieldStart(FIELD_TAGS);
        inOrder.verify(generator).writeString("marker1");
        inOrder.verify(generator).writeString("marker4");
        inOrder.verify(generator).writeEndArray();
    }

    @Test
    public void testFieldName() throws IOException {
        provider.setFieldName("newFieldName");
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeArrayFieldStart("newFieldName");
        inOrder.verify(generator).writeString("marker1");
        inOrder.verify(generator).writeString("marker4");
        inOrder.verify(generator).writeEndArray();
    }

    @Test
    public void testFieldNames() throws IOException {
        LogstashFieldNames fieldNames = new LogstashFieldNames();
        fieldNames.setTags("newFieldName");
        provider.setFieldNames(fieldNames);
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeArrayFieldStart("newFieldName");
        inOrder.verify(generator).writeString("marker1");
        inOrder.verify(generator).writeString("marker4");
        inOrder.verify(generator).writeEndArray();
    }
}

