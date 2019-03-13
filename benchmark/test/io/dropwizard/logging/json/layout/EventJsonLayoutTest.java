package io.dropwizard.logging.json.layout;


import EventAttribute.CALLER_DATA;
import EventAttribute.EXCEPTION;
import EventAttribute.LEVEL;
import EventAttribute.LOGGER_NAME;
import EventAttribute.MDC;
import EventAttribute.MESSAGE;
import EventAttribute.THREAD_NAME;
import EventAttribute.TIMESTAMP;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyVO;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.json.EventAttribute;
import io.dropwizard.util.Maps;
import io.dropwizard.util.Sets;
import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class EventJsonLayoutTest {
    private static final String timestamp = "2018-01-02T15:19:21.000+0000";

    private static final String logger = "com.example.user.service";

    private static final String message = "User[18] has been registered";

    private static final Map<String, String> mdc = Maps.of("userId", "18", "serviceId", "19", "orderId", "24");

    private static final Set<EventAttribute> DEFAULT_EVENT_ATTRIBUTES = Collections.unmodifiableSet(EnumSet.of(LEVEL, THREAD_NAME, MDC, LOGGER_NAME, MESSAGE, EXCEPTION, TIMESTAMP, CALLER_DATA));

    private final TimestampFormatter timestampFormatter = new TimestampFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSZ", ZoneId.of("UTC"));

    private final JsonFormatter jsonFormatter = new JsonFormatter(Jackson.newObjectMapper(), false, true);

    private ThrowableProxyConverter throwableProxyConverter = Mockito.mock(ThrowableProxyConverter.class);

    private ILoggingEvent event = Mockito.mock(ILoggingEvent.class);

    private Map<String, Object> defaultExpectedFields;

    private EventJsonLayout eventJsonLayout;

    @Test
    public void testProducesDefaultMap() {
        Map<String, Object> map = eventJsonLayout.toJsonMap(event);
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        assertThat(map).isEqualTo(expectedFields);
    }

    @Test
    public void testLogsAnException() {
        Mockito.when(event.getThrowableProxy()).thenReturn(new ThrowableProxyVO());
        Mockito.when(throwableProxyConverter.convert(event)).thenReturn("Boom!");
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.put("exception", "Boom!");
        assertThat(eventJsonLayout.toJsonMap(event)).isEqualTo(expectedFields);
    }

    @Test
    public void testDisableTimestamp() {
        EnumSet<EventAttribute> eventAttributes = EnumSet.copyOf(EventJsonLayoutTest.DEFAULT_EVENT_ATTRIBUTES);
        eventAttributes.remove(TIMESTAMP);
        eventJsonLayout.setIncludes(eventAttributes);
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.remove("timestamp");
        assertThat(eventJsonLayout.toJsonMap(event)).isEqualTo(expectedFields);
    }

    @Test
    public void testLogVersion() {
        eventJsonLayout.setJsonProtocolVersion("1.2");
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.put("version", "1.2");
        assertThat(eventJsonLayout.toJsonMap(event)).isEqualTo(expectedFields);
    }

    @Test
    public void testReplaceFieldName() {
        final Map<String, String> customFieldNames = Maps.of("timestamp", "@timestamp", "message", "@message");
        Map<String, Object> map = new EventJsonLayout(jsonFormatter, timestampFormatter, throwableProxyConverter, EventJsonLayoutTest.DEFAULT_EVENT_ATTRIBUTES, customFieldNames, Collections.emptyMap(), Collections.emptySet(), false).toJsonMap(event);
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.put("@timestamp", EventJsonLayoutTest.timestamp);
        expectedFields.put("@message", EventJsonLayoutTest.message);
        expectedFields.remove("timestamp");
        expectedFields.remove("message");
        assertThat(map).isEqualTo(expectedFields);
    }

    @Test
    public void testAddNewField() {
        final Map<String, Object> additionalFields = Maps.of("serviceName", "userService", "serviceBuild", 207);
        Map<String, Object> map = new EventJsonLayout(jsonFormatter, timestampFormatter, throwableProxyConverter, EventJsonLayoutTest.DEFAULT_EVENT_ATTRIBUTES, Collections.emptyMap(), additionalFields, Collections.emptySet(), false).toJsonMap(event);
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.put("serviceName", "userService");
        expectedFields.put("serviceBuild", 207);
        assertThat(map).isEqualTo(expectedFields);
    }

    @Test
    public void testFilterMdc() {
        final Set<String> includesMdcKeys = Sets.of("userId", "orderId");
        Map<String, Object> map = new EventJsonLayout(jsonFormatter, timestampFormatter, throwableProxyConverter, EventJsonLayoutTest.DEFAULT_EVENT_ATTRIBUTES, Collections.emptyMap(), Collections.emptyMap(), includesMdcKeys, false).toJsonMap(event);
        final Map<String, String> expectedMdc = Maps.of("userId", "18", "orderId", "24");
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.put("mdc", expectedMdc);
        assertThat(map).isEqualTo(expectedFields);
    }

    @Test
    public void testFlattensMdcMap() {
        Map<String, Object> map = new EventJsonLayout(jsonFormatter, timestampFormatter, throwableProxyConverter, EventJsonLayoutTest.DEFAULT_EVENT_ATTRIBUTES, Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet(), true).toJsonMap(event);
        final HashMap<String, Object> expectedFields = new HashMap<>(defaultExpectedFields);
        expectedFields.putAll(EventJsonLayoutTest.mdc);
        expectedFields.remove("mdc");
        assertThat(map).isEqualTo(expectedFields);
    }

    @Test
    public void testStartThrowableConverter() {
        eventJsonLayout.start();
        Mockito.verify(throwableProxyConverter).start();
    }

    @Test
    public void testStopThrowableConverter() {
        eventJsonLayout.stop();
        Mockito.verify(throwableProxyConverter).stop();
    }
}

