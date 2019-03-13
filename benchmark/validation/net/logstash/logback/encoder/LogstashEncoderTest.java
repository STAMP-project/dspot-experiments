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
import Level.ERROR;
import Level.INFO;
import LogstashCommonFieldNames.IGNORE_FIELD_INDICATOR;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import net.logstash.logback.Logback11Support;
import net.logstash.logback.decorate.JsonFactoryDecorator;
import net.logstash.logback.decorate.JsonGeneratorDecorator;
import net.logstash.logback.fieldnames.ShortenedFieldNames;
import net.logstash.logback.marker.Markers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES;


@RunWith(MockitoJUnitRunner.Silent.class)
public class LogstashEncoderTest {
    private static Logger LOG = LoggerFactory.getLogger(LogstashEncoderTest.class);

    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(ESCAPE_NON_ASCII);

    private static final ObjectMapper MAPPER = new ObjectMapper(LogstashEncoderTest.FACTORY);

    private LogstashEncoder encoder = new LogstashEncoder();

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Mock
    private Logback11Support logback11Support;

    @Test
    public void basicsAreIncluded_logback11() throws Exception {
        encoder.setLogback11Support(logback11Support);
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        encoder.init(outputStream);
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.start();
        encoder.doEncode(event);
        IOUtils.closeQuietly(outputStream);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(outputStream.toByteArray());
        verifyBasics(timestamp, node);
    }

    @Test
    public void basicsAreIncluded_logback12() throws Exception {
        encoder.setLogback11Support(logback11Support);
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        verifyBasics(timestamp, node);
    }

    @Test
    public void basicsAreIncludedWithShortenedNames() throws Exception {
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setFieldNames(new ShortenedFieldNames());
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").textValue()).isEqualTo(FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(timestamp));
        assertThat(node.get("@version").textValue()).isEqualTo("1");
        assertThat(node.get("logger").textValue()).isEqualTo("LoggerName");
        assertThat(node.get("thread").textValue()).isEqualTo("ThreadName");
        assertThat(node.get("message").textValue()).isEqualTo("My message");
        assertThat(node.get("level").textValue()).isEqualTo("ERROR");
        assertThat(node.get("levelVal").intValue()).isEqualTo(40000);
    }

    @Test
    public void customDecorators() throws Exception {
        encoder.stop();
        encoder.setJsonFactoryDecorator(new JsonFactoryDecorator() {
            @Override
            public MappingJsonFactory decorate(MappingJsonFactory factory) {
                return ((MappingJsonFactory) (factory.disable(QUOTE_FIELD_NAMES)));
            }
        });
        encoder.setJsonGeneratorDecorator(new JsonGeneratorDecorator() {
            @Override
            public JsonGenerator decorate(JsonGenerator generator) {
                return generator.useDefaultPrettyPrinter();
            }
        });
        encoder.start();
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        byte[] encoded = encoder.encode(event);
        String output = new String(encoded, "UTF-8");
        assertThat(output).isEqualTo(String.format((((((((((("{%n" + "  @timestamp : \"") + (FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(timestamp))) + "\",%n") + "  @version : \"1\",%n") + "  message : \"My message\",%n") + "  logger_name : \"LoggerName\",%n") + "  thread_name : \"ThreadName\",%n") + "  level : \"ERROR\",%n") + "  level_value : 40000%n") + "}%n")));
    }

    @Test
    public void loggerNameIsShortenedProperly() throws Exception {
        final long timestamp = System.currentTimeMillis();
        final int length = 36;
        final String shortenedLoggerName = new TargetLengthBasedClassNameAbbreviator(length).abbreviate(FastDateFormat.class.getCanonicalName());
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getLoggerName()).thenReturn(FastDateFormat.class.getCanonicalName());
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setFieldNames(new ShortenedFieldNames());
        encoder.setShortenedLoggerNameLength(length);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").textValue()).isEqualTo(FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(timestamp));
        assertThat(node.get("@version").textValue()).isEqualTo("1");
        assertThat(node.get("logger").textValue()).isEqualTo(shortenedLoggerName);
        assertThat(node.get("thread").textValue()).isEqualTo("ThreadName");
        assertThat(node.get("message").textValue()).isEqualTo("My message");
        assertThat(node.get("level").textValue()).isEqualTo("ERROR");
        assertThat(node.get("levelVal").intValue()).isEqualTo(40000);
    }

    @Test
    public void closePutsSeparatorAtTheEnd_logback11() throws Exception {
        encoder.setLogback11Support(logback11Support);
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        encoder.init(outputStream);
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        encoder.start();
        encoder.doEncode(event);
        encoder.close();
        IOUtils.closeQuietly(outputStream);
        assertThat(outputStream.toString()).endsWith(IOUtils.LINE_SEPARATOR);
    }

    @Test
    public void includingThrowableProxyIncludesStackTrace() throws Exception {
        IThrowableProxy throwableProxy = new ThrowableProxy(new Exception("My goodness"));
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getThrowableProxy()).thenReturn(throwableProxy);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("stack_trace").textValue()).isEqualTo(ThrowableProxyUtil.asString(throwableProxy));
    }

    @Test
    public void mdcAllIncluded() throws Exception {
        Map<String, String> mdcMap = new HashMap<String, String>();
        mdcMap.put("thing_one", "One");
        mdcMap.put("thing_two", "Three");
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("thing_two").textValue()).isEqualTo("Three");
    }

    @Test
    public void mdcSomeIncluded() throws Exception {
        Map<String, String> mdcMap = new HashMap<String, String>();
        mdcMap.put("thing_one", "One");
        mdcMap.put("thing_two", "Three");
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        encoder.addIncludeMdcKeyName("thing_one");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("thing_two")).isNull();
    }

    @Test
    public void mdcSomeExcluded() throws Exception {
        Map<String, String> mdcMap = new HashMap<String, String>();
        mdcMap.put("thing_one", "One");
        mdcMap.put("thing_two", "Three");
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        encoder.addExcludeMdcKeyName("thing_two");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("thing_two")).isNull();
    }

    @Test
    public void mdcNoneIncluded() throws Exception {
        Map<String, String> mdcMap = new HashMap<String, String>();
        mdcMap.put("thing_one", "One");
        mdcMap.put("thing_two", "Three");
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        encoder.setIncludeMdc(false);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one")).isNull();
        assertThat(node.get("thing_two")).isNull();
    }

    @Test
    public void propertiesInMDCAreIncludedInSubObject() throws Exception {
        Map<String, String> mdcMap = new HashMap<String, String>();
        mdcMap.put("thing_one", "One");
        mdcMap.put("thing_two", "Three");
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        encoder.getFieldNames().setMdc("mdc");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("mdc").get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("mdc").get("thing_two").textValue()).isEqualTo("Three");
    }

    @Test
    public void nullMDCDoesNotCauseEverythingToBlowUp() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(null);
        encoder.start();
        encoder.encode(event);
    }

    @Test
    public void callerDataIsIncluded() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(Collections.<String, String>emptyMap());
        final StackTraceElement[] stackTraceElements = new StackTraceElement[]{ new StackTraceElement("caller_class", "method_name", "file_name", 12345) };
        Mockito.when(event.getCallerData()).thenReturn(stackTraceElements);
        encoder.setIncludeCallerInfo(true);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("caller_class_name").textValue()).isEqualTo(stackTraceElements[0].getClassName());
        assertThat(node.get("caller_method_name").textValue()).isEqualTo(stackTraceElements[0].getMethodName());
        assertThat(node.get("caller_file_name").textValue()).isEqualTo(stackTraceElements[0].getFileName());
        assertThat(node.get("caller_line_number").intValue()).isEqualTo(stackTraceElements[0].getLineNumber());
    }

    @Test
    public void callerDataIsIncludedInSubObject() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(Collections.<String, String>emptyMap());
        final StackTraceElement[] stackTraceElements = new StackTraceElement[]{ new StackTraceElement("caller_class", "method_name", "file_name", 12345) };
        Mockito.when(event.getCallerData()).thenReturn(stackTraceElements);
        encoder.setIncludeCallerInfo(true);
        encoder.getFieldNames().setCaller("caller");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("caller").get("caller_class_name").textValue()).isEqualTo(stackTraceElements[0].getClassName());
        assertThat(node.get("caller").get("caller_method_name").textValue()).isEqualTo(stackTraceElements[0].getMethodName());
        assertThat(node.get("caller").get("caller_file_name").textValue()).isEqualTo(stackTraceElements[0].getFileName());
        assertThat(node.get("caller").get("caller_line_number").intValue()).isEqualTo(stackTraceElements[0].getLineNumber());
    }

    @Test
    public void callerDataIsNotIncludedIfSwitchedOff() throws Exception {
        ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
        Mockito.when(event.getLoggerName()).thenReturn("LoggerName");
        Mockito.when(event.getThreadName()).thenReturn("ThreadName");
        Mockito.when(event.getFormattedMessage()).thenReturn("My message");
        Mockito.when(event.getLevel()).thenReturn(ERROR);
        Mockito.when(event.getMDCPropertyMap()).thenReturn(Collections.<String, String>emptyMap());
        final StackTraceElement[] stackTraceElements = new StackTraceElement[]{ new StackTraceElement("caller_class", "method_name", "file_name", 12345) };
        Mockito.when(event.getCallerData()).thenReturn(stackTraceElements);
        encoder.setIncludeCallerInfo(false);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("caller_class_name")).isNull();
        assertThat(node.get("caller_method_name")).isNull();
        assertThat(node.get("caller_file_name")).isNull();
        assertThat(node.get("caller_line_number")).isNull();
    }

    @Test
    public void propertiesInContextAreIncluded() throws Exception {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("thing_one", "One");
        propertyMap.put("thing_two", "Three");
        final Context context = Mockito.mock(Context.class);
        Mockito.when(context.getCopyOfPropertyMap()).thenReturn(propertyMap);
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        encoder.setContext(context);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("thing_two").textValue()).isEqualTo("Three");
    }

    @Test
    public void propertiesInContextAreNotIncludedIfSwitchedOff() throws Exception {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("thing_one", "One");
        propertyMap.put("thing_two", "Three");
        final Context context = Mockito.mock(Context.class);
        Mockito.when(context.getCopyOfPropertyMap()).thenReturn(propertyMap);
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        encoder.setIncludeContext(false);
        encoder.setContext(context);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("thing_one")).isNull();
        assertThat(node.get("thing_two")).isNull();
    }

    @Test
    public void propertiesInContextAreIncludedInSubObject() throws Exception {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("thing_one", "One");
        propertyMap.put("thing_two", "Three");
        final Context context = Mockito.mock(Context.class);
        Mockito.when(context.getCopyOfPropertyMap()).thenReturn(propertyMap);
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        encoder.getFieldNames().setContext("context");
        encoder.setContext(context);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("context").get("thing_one").textValue()).isEqualTo("One");
        assertThat(node.get("context").get("thing_two").textValue()).isEqualTo("Three");
    }

    @Test
    public void markerIncludesItselfAsTag() throws Exception {
        Marker marker = MarkerFactory.getMarker("hoosh");
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(marker);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertJsonArray(node.findValue("tags"), "hoosh");
    }

    @Test
    public void markerReferencesAreIncludedAsTags() throws Exception {
        Marker marker = MarkerFactory.getMarker("bees");
        marker.add(MarkerFactory.getMarker("knees"));
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(marker);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertJsonArray(node.findValue("tags"), "bees", "knees");
    }

    @Test
    public void nullMarkerIsIgnored() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(null);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.findValue("tags")).isNull();
    }

    @Test
    public void markerNoneIncluded() throws Exception {
        Marker marker = MarkerFactory.getMarker("bees");
        marker.add(MarkerFactory.getMarker("knees"));
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(marker);
        encoder.setIncludeTags(false);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.findValue("tags")).isNull();
    }

    /**
     * Tests the old way of appending a json_message to the event.
     *
     * @deprecated See {@link #testAppendJsonMessage()} for the new way of doing this.
     */
    @Test
    @Deprecated
    public void markerIsJSON() throws Exception {
        Object[] argArray = new Object[]{ 1, Collections.singletonMap("hello", Collections.singletonMap("hello", "world")) };
        Marker marker = MarkerFactory.getMarker("JSON");
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(marker);
        Mockito.when(event.getArgumentArray()).thenReturn(argArray);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(LogstashEncoderTest.MAPPER.convertValue(argArray, JsonNode.class).equals(node.get("json_message"))).isEqualTo(true);
    }

    @Test
    public void testAppendJsonMessage() throws Exception {
        Object[] argArray = new Object[]{ 1, Collections.singletonMap("hello", Collections.singletonMap("hello", "world")) };
        Marker marker = Markers.append("json_message", argArray);
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getMarker()).thenReturn(marker);
        Mockito.when(event.getArgumentArray()).thenReturn(argArray);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(LogstashEncoderTest.MAPPER.convertValue(argArray, JsonNode.class).equals(node.get("json_message"))).isEqualTo(true);
    }

    @Test
    public void immediateFlushIsSane() {
        encoder.setImmediateFlush(true);
        assertThat(encoder.isImmediateFlush()).isEqualTo(true);
        encoder.setImmediateFlush(false);
        assertThat(encoder.isImmediateFlush()).isEqualTo(false);
    }

    @Test
    public void includeJsonChunk() throws Exception {
        String customFields = "{\"appname\":\"damnGodWebservice\",\"roles\":[\"customerorder\", \"auth\"], \"buildinfo\": { \"version\" : \"Version 0.1.0-SNAPSHOT\", \"lastcommit\" : \"75473700d5befa953c45f630c6d9105413c16fe1\"} }";
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        encoder.setCustomFields(customFields);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("appname").textValue()).isEqualTo("damnGodWebservice");
        Assert.assertTrue(node.get("roles").equals(parse("[\"customerorder\", \"auth\"]")));
        Assert.assertTrue(node.get("buildinfo").equals(parse("{ \"version\" : \"Version 0.1.0-SNAPSHOT\", \"lastcommit\" : \"75473700d5befa953c45f630c6d9105413c16fe1\"}")));
    }

    @Test
    public void customTimeZone() throws Exception {
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setTimeZone("UTC");
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").textValue()).isEqualTo(FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", TimeZone.getTimeZone("UTC")).format(timestamp));
        assertThat(node.get("@version").textValue()).isEqualTo("1");
        assertThat(node.get("logger_name").textValue()).isEqualTo("LoggerName");
        assertThat(node.get("thread_name").textValue()).isEqualTo("ThreadName");
        assertThat(node.get("message").textValue()).isEqualTo("My message");
        assertThat(node.get("level").textValue()).isEqualTo("ERROR");
        assertThat(node.get("level_value").intValue()).isEqualTo(40000);
    }

    @Test
    @Deprecated
    public void testContextMapWithNoArguments() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Mockito.when(event.getArgumentArray()).thenReturn(null);
        encoder.setEnableContextMap(true);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("message").textValue()).isEqualTo("My message");
    }

    /**
     * Tests the old way of embedding a map in the json event.
     *
     * @deprecated See {@link #testAppendEntries()} for the new way of doing this.
     */
    @Test
    @Deprecated
    public void testContextMap() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("duration", 1200);
        contextMap.put("remoteResponse", "OK");
        contextMap.put("extra", Collections.singletonMap("extraEntry", "extraValue"));
        Object[] argList = new Object[]{ "firstParamThatShouldBeIgnored", Collections.singletonMap("ignoredMapEntry", "whatever"), contextMap };
        Mockito.when(event.getArgumentArray()).thenReturn(argList);
        encoder.setEnableContextMap(true);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("duration")).isNotNull();
        assertThat(node.get("duration").intValue()).isEqualTo(1200);
        assertThat(node.get("remoteResponse")).isNotNull();
        assertThat(node.get("remoteResponse").textValue()).isEqualTo("OK");
        assertThat(node.get("extra")).isNotNull();
        assertThat(node.get("extra").get("extraEntry")).isNotNull();
        assertThat(node.get("extra").get("extraEntry").textValue()).isEqualTo("extraValue");
        assertThat(node.get("ignoredMapEntry")).as("The second map from the end should be ignored").isNull();
    }

    @Test
    public void testAppendEntries() throws Exception {
        ILoggingEvent event = mockBasicILoggingEvent(INFO);
        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("duration", 1200);
        contextMap.put("remoteResponse", "OK");
        contextMap.put("extra", Collections.singletonMap("extraEntry", "extraValue"));
        Object[] argList = new Object[]{ "firstParamThatShouldBeIgnored", Collections.singletonMap("ignoredMapEntry", "whatever") };
        Mockito.when(event.getArgumentArray()).thenReturn(argList);
        Marker marker = Markers.appendEntries(contextMap);
        Mockito.when(event.getMarker()).thenReturn(marker);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("duration")).isNotNull();
        assertThat(node.get("duration").intValue()).isEqualTo(1200);
        assertThat(node.get("remoteResponse")).isNotNull();
        assertThat(node.get("remoteResponse").textValue()).isEqualTo("OK");
        assertThat(node.get("extra")).isNotNull();
        assertThat(node.get("extra").get("extraEntry")).isNotNull();
        assertThat(node.get("extra").get("extraEntry").textValue()).isEqualTo("extraValue");
        assertThat(node.get("ignoredMapEntry")).as("The second map from the end should be ignored").isNull();
    }

    @Test
    public void testEncoderConfiguration() throws Exception {
        // Empty the log file
        PrintWriter writer = new PrintWriter(((System.getProperty("java.io.tmpdir")) + "/test.log"));
        writer.print("");
        writer.close();
        MDC.put("myMdcKey", "myMdcValue");
        LogstashEncoderTest.LOG.info(Markers.append("appendedName", "appendedValue").and(Markers.append("n1", 2)), "Testing info logging.");
        MDC.remove("myMdcKey");
        InputStream is = new FileInputStream(((System.getProperty("java.io.tmpdir")) + "/test.log"));
        List<String> lines = IOUtils.readLines(is);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(lines.get(0).getBytes("UTF-8"));
        /* The configuration suppresses the version field,
        make sure it doesn't appear.
         */
        assertThat(node.get("@version")).isNull();
        assertThat(node.get(IGNORE_FIELD_INDICATOR)).isNull();
        assertThat(node.get("appname").textValue()).isEqualTo("damnGodWebservice");
        assertThat(node.get("appendedName").textValue()).isEqualTo("appendedValue");
        assertThat(node.get("myMdcKey")).isNull();
        assertThat(node.get("logger").textValue()).isEqualTo(LogstashEncoderTest.class.getName());
        Assert.assertTrue(node.get("roles").equals(parse("[\"customerorder\", \"auth\"]")));
        Assert.assertTrue(node.get("buildinfo").equals(parse("{ \"version\" : \"Version 0.1.0-SNAPSHOT\", \"lastcommit\" : \"75473700d5befa953c45f630c6d9105413c16fe1\"}")));
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
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setTimestampPattern(UNIX_TIMESTAMP_AS_NUMBER);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").numberValue()).isEqualTo(timestamp);
    }

    @Test
    public void unixTimestampAsString() throws Exception {
        final long timestamp = System.currentTimeMillis();
        ILoggingEvent event = mockBasicILoggingEvent(ERROR);
        Mockito.when(event.getTimeStamp()).thenReturn(timestamp);
        encoder.setTimestampPattern(UNIX_TIMESTAMP_AS_STRING);
        encoder.start();
        byte[] encoded = encoder.encode(event);
        JsonNode node = LogstashEncoderTest.MAPPER.readTree(encoded);
        assertThat(node.get("@timestamp").textValue()).isEqualTo(Long.toString(timestamp));
    }
}

