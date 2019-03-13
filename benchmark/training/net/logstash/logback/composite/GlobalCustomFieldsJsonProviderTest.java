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


import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class GlobalCustomFieldsJsonProviderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private GlobalCustomFieldsJsonProvider<ILoggingEvent> provider = new GlobalCustomFieldsJsonProvider<ILoggingEvent>();

    @Mock
    private ILoggingEvent event;

    private JsonFactory factory = new MappingJsonFactory();

    private StringWriter writer = new StringWriter();

    private JsonGenerator generator;

    @Test
    public void test() throws IOException {
        generator.writeStartObject();
        provider.writeTo(generator, event);
        generator.writeEndObject();
        generator.flush();
        assertThat(writer.toString()).isEqualTo("{\"name\":\"value\"}");
    }
}

