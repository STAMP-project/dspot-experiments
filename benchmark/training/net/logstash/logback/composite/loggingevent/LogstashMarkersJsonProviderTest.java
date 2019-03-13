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
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Marker;


public class LogstashMarkersJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private LogstashMarkersJsonProvider provider = new LogstashMarkersJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Mock
    private LogstashMarker outerMarker;

    @Mock
    private LogstashMarker innerMarker;

    @Test
    public void test() throws IOException {
        Mockito.when(outerMarker.hasReferences()).thenReturn(true);
        Mockito.when(outerMarker.iterator()).thenReturn(Collections.<Marker>singleton(innerMarker).iterator());
        Mockito.when(event.getMarker()).thenReturn(outerMarker);
        provider.writeTo(generator, event);
        Mockito.verify(outerMarker).writeTo(generator);
        Mockito.verify(innerMarker).writeTo(generator);
    }
}

