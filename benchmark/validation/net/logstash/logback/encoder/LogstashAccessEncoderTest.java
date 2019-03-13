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
package net.logstash.logback.encoder;


import FormattedTimestampJsonProvider.UNIX_TIMESTAMP_AS_NUMBER;
import FormattedTimestampJsonProvider.UNIX_TIMESTAMP_AS_STRING;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.Logback11Support;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LogstashAccessEncoderTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private LogstashAccessEncoder encoder = new LogstashAccessEncoder();

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Mock
    private Logback11Support logback11Support;

    @Test
    public void basicsAreIncluded_logback11() throws Exception {
        encoder.setLogback11Support(logback11Support);
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        encoder.init(outputStream);
        final long timestamp = System.currentTimeMillis();
        IAccessEvent event = mockBasicILoggingEvent();
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.getFieldNames().setTimestamp("timestamp");
        encoder.start();
        encoder.doEncode(event);
        IOUtils.closeQuietly(outputStream);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(outputStream.toByteArray());
        verifyBasics(timestamp, event, node);
    }

    @Test
    public void basicsAreIncluded_logback12OrLater() throws Exception {
        final long timestamp = System.currentTimeMillis();
        IAccessEvent event = mockBasicILoggingEvent();
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.getFieldNames().setTimestamp("timestamp");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(encoded);
        verifyBasics(timestamp, event, node);
    }

    @Test
    public void closePutsSeparatorAtTheEnd() throws Exception {
        encoder.setLogback11Support(logback11Support);
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        encoder.init(outputStream);
        IAccessEvent event = mockBasicILoggingEvent();
        encoder.start();
        encoder.doEncode(event);
        encoder.close();
        IOUtils.closeQuietly(outputStream);
        assertThat(outputStream.toString()).endsWith(IOUtils.LINE_SEPARATOR);
    }

    @Test
    public void propertiesInContextAreIncluded() throws Exception {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("thing_one", "One");
        propertyMap.put("thing_two", "Three");
        final Context context = Mockito.mock(Context.class);
        Mockito.when(context.getCopyOfPropertyMap()).thenReturn(propertyMap);
        IAccessEvent event = mockBasicILoggingEvent();
        encoder.setContext(context);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("thing_two").textValue()).isEqualTo("Three");
    }

    @Test
    public void requestAndResponseHeadersAreIncluded() throws Exception {
        IAccessEvent event = mockBasicILoggingEvent();
        encoder.getFieldNames().setRequestHeaders("request_headers");
        encoder.getFieldNames().setResponseHeaders("response_headers");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("request_headers").size()).isEqualTo(2);
        assertThat(node.get("request_headers").get("user-agent").textValue()).isEqualTo("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        assertThat(node.get("request_headers").get("accept").textValue()).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        assertThat(node.get("request_headers").get("unknown")).isNull();
        assertThat(node.get("response_headers").size()).isEqualTo(2);
        assertThat(node.get("response_headers").get("content-type").textValue()).isEqualTo("text/html; charset=UTF-8");
        assertThat(node.get("response_headers").get("content-length").textValue()).isEqualTo("42");
        assertThat(node.get("response_headers").get("unknown")).isNull();
    }

    @Test
    public void immediateFlushIsSane() {
        encoder.setImmediateFlush(true);
        assertThat(encoder.isImmediateFlush()).isEqualTo(true);
        encoder.setImmediateFlush(false);
        assertThat(encoder.isImmediateFlush()).isEqualTo(false);
    }

    @Test
    public void testCustomFields() throws Exception {
        String customFields = "{\"foo\":\"bar\"}";
        encoder.setCustomFields(customFields);
        assertThat(encoder.getCustomFields()).isEqualTo(customFields);
    }

    @Test
    public void unixTimestampAsNumber() throws Exception {
        final long timestamp = System.currentTimeMillis();
        IAccessEvent event = mockBasicILoggingEvent();
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setTimestampPattern(UNIX_TIMESTAMP_AS_NUMBER);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").numberValue()).isEqualTo(timestamp);
    }

    @Test
    public void unixTimestampAsString() throws Exception {
        final long timestamp = System.currentTimeMillis();
        IAccessEvent event = mockBasicILoggingEvent();
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setTimestampPattern(UNIX_TIMESTAMP_AS_STRING);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashAccessEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").textValue()).isEqualTo(Long.toString(timestamp));
    }
}

