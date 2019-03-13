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


import CallerDataJsonProvider.FIELD_CALLER_CLASS_NAME;
import CallerDataJsonProvider.FIELD_CALLER_FILE_NAME;
import CallerDataJsonProvider.FIELD_CALLER_LINE_NUMBER;
import CallerDataJsonProvider.FIELD_CALLER_METHOD_NAME;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.logstash.logback.fieldnames.LogstashFieldNames;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class CallerDataJsonProviderTest {
    private static final StackTraceElement CALLER_DATA = new StackTraceElement("declaringClass", "methodName", "fileName", 100);

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CallerDataJsonProvider provider = new CallerDataJsonProvider();

    @Mock
    private JsonGenerator generator;

    @Mock
    private ILoggingEvent event;

    @Test
    public void testUnwrapped() throws IOException {
        setupCallerData();
        provider.writeTo(generator, event);
        Mockito.verify(generator, Mockito.never()).writeStartObject();
        Mockito.verify(generator, Mockito.never()).writeEndObject();
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeStringField(FIELD_CALLER_CLASS_NAME, CallerDataJsonProviderTest.CALLER_DATA.getClassName());
        inOrder.verify(generator).writeStringField(FIELD_CALLER_METHOD_NAME, CallerDataJsonProviderTest.CALLER_DATA.getMethodName());
        inOrder.verify(generator).writeStringField(FIELD_CALLER_FILE_NAME, CallerDataJsonProviderTest.CALLER_DATA.getFileName());
        inOrder.verify(generator).writeNumberField(FIELD_CALLER_LINE_NUMBER, CallerDataJsonProviderTest.CALLER_DATA.getLineNumber());
    }

    @Test
    public void testWrapped() throws IOException {
        setupCallerData();
        provider.setFieldName("caller");
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeObjectFieldStart("caller");
        inOrder.verify(generator).writeStringField(FIELD_CALLER_CLASS_NAME, CallerDataJsonProviderTest.CALLER_DATA.getClassName());
        inOrder.verify(generator).writeStringField(FIELD_CALLER_METHOD_NAME, CallerDataJsonProviderTest.CALLER_DATA.getMethodName());
        inOrder.verify(generator).writeStringField(FIELD_CALLER_FILE_NAME, CallerDataJsonProviderTest.CALLER_DATA.getFileName());
        inOrder.verify(generator).writeNumberField(FIELD_CALLER_LINE_NUMBER, CallerDataJsonProviderTest.CALLER_DATA.getLineNumber());
        inOrder.verify(generator).writeEndObject();
    }

    @Test
    public void testNoCallerData() throws IOException {
        provider.writeTo(generator, event);
        Mockito.verifyNoMoreInteractions(generator);
    }

    @Test
    public void testFieldName() throws IOException {
        setupCallerData();
        provider.setFieldName("caller");
        provider.setClassFieldName("class");
        provider.setMethodFieldName("method");
        provider.setFileFieldName("file");
        provider.setLineFieldName("line");
        Mockito.when(event.getLoggerName()).thenReturn(getClass().getName());
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeObjectFieldStart("caller");
        inOrder.verify(generator).writeStringField("class", CallerDataJsonProviderTest.CALLER_DATA.getClassName());
        inOrder.verify(generator).writeStringField("method", CallerDataJsonProviderTest.CALLER_DATA.getMethodName());
        inOrder.verify(generator).writeStringField("file", CallerDataJsonProviderTest.CALLER_DATA.getFileName());
        inOrder.verify(generator).writeNumberField("line", CallerDataJsonProviderTest.CALLER_DATA.getLineNumber());
        inOrder.verify(generator).writeEndObject();
    }

    @Test
    public void testFieldNames() throws IOException {
        setupCallerData();
        LogstashFieldNames fieldNames = new LogstashFieldNames();
        fieldNames.setCaller("caller");
        fieldNames.setCallerClass("class");
        fieldNames.setCallerMethod("method");
        fieldNames.setCallerFile("file");
        fieldNames.setCallerLine("line");
        provider.setFieldNames(fieldNames);
        provider.writeTo(generator, event);
        InOrder inOrder = Mockito.inOrder(generator);
        inOrder.verify(generator).writeObjectFieldStart("caller");
        inOrder.verify(generator).writeStringField("class", CallerDataJsonProviderTest.CALLER_DATA.getClassName());
        inOrder.verify(generator).writeStringField("method", CallerDataJsonProviderTest.CALLER_DATA.getMethodName());
        inOrder.verify(generator).writeStringField("file", CallerDataJsonProviderTest.CALLER_DATA.getFileName());
        inOrder.verify(generator).writeNumberField("line", CallerDataJsonProviderTest.CALLER_DATA.getLineNumber());
        inOrder.verify(generator).writeEndObject();
    }

    @Test
    public void testPrepareForDeferredProcessing() throws IOException {
        provider.prepareForDeferredProcessing(event);
        Mockito.verify(event).getCallerData();
    }
}

