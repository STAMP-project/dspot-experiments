package io.dropwizard.logging.json;


import AccessAttribute.METHOD;
import AccessAttribute.PATH_QUERY;
import AccessAttribute.REMOTE_HOST;
import AccessAttribute.REMOTE_USER;
import AccessAttribute.REQUEST_CONTENT;
import AccessAttribute.REQUEST_PARAMETERS;
import AccessAttribute.REQUEST_URL;
import AccessAttribute.STATUS_CODE;
import AccessAttribute.USER_AGENT;
import ConsoleAppenderFactory.ConsoleStream.STDERR;
import EventAttribute.CALLER_DATA;
import EventAttribute.EXCEPTION;
import EventAttribute.LEVEL;
import EventAttribute.LOGGER_NAME;
import EventAttribute.MDC;
import EventAttribute.MESSAGE;
import EventAttribute.THREAD_NAME;
import EventAttribute.TIMESTAMP;
import Level.INFO;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.logging.json.layout.ExceptionFormat;
import io.dropwizard.logging.layout.DiscoverableLayoutFactory;
import io.dropwizard.request.logging.LogbackAccessRequestLogFactory;
import io.dropwizard.validation.BaseValidator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;


public class LayoutIntegrationTests {
    static {
        BootstrapLogging.bootstrap(INFO, new EventJsonLayoutBaseFactory());
    }

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    @SuppressWarnings("rawtypes")
    private final YamlConfigurationFactory<ConsoleAppenderFactory> yamlFactory = new YamlConfigurationFactory(ConsoleAppenderFactory.class, BaseValidator.newValidator(), objectMapper, "dw-json-log");

    @Test
    public void testDeserializeJson() throws Exception {
        ConsoleAppenderFactory<ILoggingEvent> appenderFactory = getAppenderFactory("yaml/json-log.yml");
        DiscoverableLayoutFactory<?> layout = Objects.requireNonNull(appenderFactory.getLayout());
        assertThat(layout).isInstanceOf(EventJsonLayoutBaseFactory.class);
        EventJsonLayoutBaseFactory factory = ((EventJsonLayoutBaseFactory) (layout));
        assertThat(factory).isNotNull();
        assertThat(factory.getTimestampFormat()).isEqualTo("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        assertThat(factory.isPrettyPrint()).isFalse();
        assertThat(factory.isAppendLineSeparator()).isTrue();
        assertThat(factory.getIncludes()).contains(LEVEL, MDC, MESSAGE, LOGGER_NAME, EXCEPTION, TIMESTAMP, CALLER_DATA);
        assertThat(factory.isFlattenMdc()).isTrue();
        assertThat(factory.getCustomFieldNames()).containsOnly(entry("timestamp", "@timestamp"));
        assertThat(factory.getAdditionalFields()).containsOnly(entry("service-name", "user-service"), entry("service-build", 218));
        assertThat(factory.getIncludesMdcKeys()).containsOnly("userId");
        ExceptionFormat exceptionFormat = Objects.requireNonNull(factory.getExceptionFormat());
        assertThat(exceptionFormat.getDepth()).isEqualTo("10");
        assertThat(exceptionFormat.isRootFirst()).isFalse();
        assertThat(exceptionFormat.getEvaluators()).contains("io.dropwizard");
    }

    @Test
    public void testDeserializeAccessJson() throws Exception {
        ConsoleAppenderFactory<IAccessEvent> appenderFactory = getAppenderFactory("yaml/json-access-log.yml");
        DiscoverableLayoutFactory<?> layout = Objects.requireNonNull(appenderFactory.getLayout());
        assertThat(layout).isInstanceOf(AccessJsonLayoutBaseFactory.class);
        AccessJsonLayoutBaseFactory factory = ((AccessJsonLayoutBaseFactory) (layout));
        assertThat(factory.getTimestampFormat()).isEqualTo("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assertThat(factory.isPrettyPrint()).isFalse();
        assertThat(factory.isAppendLineSeparator()).isTrue();
        assertThat(factory.getIncludes()).contains(AccessAttribute.TIMESTAMP, REMOTE_USER, STATUS_CODE, METHOD, REQUEST_URL, REMOTE_HOST, REQUEST_PARAMETERS, REQUEST_CONTENT, AccessAttribute.TIMESTAMP, USER_AGENT, PATH_QUERY);
        assertThat(factory.getResponseHeaders()).containsOnly("X-Request-Id");
        assertThat(factory.getRequestHeaders()).containsOnly("User-Agent", "X-Request-Id");
        assertThat(factory.getCustomFieldNames()).containsOnly(entry("statusCode", "status_code"), entry("userAgent", "user_agent"));
        assertThat(factory.getAdditionalFields()).containsOnly(entry("service-name", "shipping-service"), entry("service-version", "1.2.3"));
    }

    @Test
    public void testLogJsonToConsole() throws Exception {
        ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory = getAppenderFactory("yaml/json-log-default.yml");
        DefaultLoggingFactory defaultLoggingFactory = new DefaultLoggingFactory();
        defaultLoggingFactory.setAppenders(Collections.singletonList(consoleAppenderFactory));
        DiscoverableLayoutFactory<?> layout = Objects.requireNonNull(consoleAppenderFactory.getLayout());
        assertThat(layout).isInstanceOf(EventJsonLayoutBaseFactory.class);
        EventJsonLayoutBaseFactory factory = ((EventJsonLayoutBaseFactory) (layout));
        assertThat(factory).isNotNull();
        assertThat(factory.getIncludes()).contains(LEVEL, THREAD_NAME, MDC, LOGGER_NAME, MESSAGE, EXCEPTION, TIMESTAMP);
        assertThat(factory.isFlattenMdc()).isFalse();
        assertThat(factory.getIncludesMdcKeys()).isEmpty();
        assertThat(factory.getExceptionFormat()).isNull();
        PrintStream old = System.out;
        ByteArrayOutputStream redirectedStream = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(redirectedStream));
            defaultLoggingFactory.configure(new MetricRegistry(), "json-log-test");
            LoggerFactory.getLogger("com.example.app").info("Application log");
            Thread.sleep(100);// Need to wait, because the logger is async

            JsonNode jsonNode = objectMapper.readTree(redirectedStream.toString());
            assertThat(jsonNode).isNotNull();
            assertThat(jsonNode.get("timestamp").isTextual()).isTrue();
            assertThat(jsonNode.get("level").asText()).isEqualTo("INFO");
            assertThat(jsonNode.get("logger").asText()).isEqualTo("com.example.app");
            assertThat(jsonNode.get("message").asText()).isEqualTo("Application log");
        } finally {
            System.setOut(old);
        }
    }

    @Test
    public void testLogAccessJsonToConsole() throws Exception {
        ConsoleAppenderFactory<IAccessEvent> consoleAppenderFactory = getAppenderFactory("yaml/json-access-log-default.yml");
        // Use sys.err, because there are some other log configuration messages in std.out
        consoleAppenderFactory.setTarget(STDERR);
        final LogbackAccessRequestLogFactory requestLogHandler = new LogbackAccessRequestLogFactory();
        requestLogHandler.setAppenders(Collections.singletonList(consoleAppenderFactory));
        PrintStream old = System.err;
        ByteArrayOutputStream redirectedStream = new ByteArrayOutputStream();
        try {
            System.setErr(new PrintStream(redirectedStream));
            RequestLog requestLog = requestLogHandler.build("json-access-log-test");
            Request request = Mockito.mock(Request.class);
            Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
            Mockito.when(request.getTimeStamp()).thenReturn(TimeUnit.SECONDS.toMillis(1353042047));
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(request.getRequestURI()).thenReturn("/test/users");
            Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
            Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("age", "city")));
            Mockito.when(request.getParameterValues("age")).thenReturn(new String[]{ "22" });
            Mockito.when(request.getParameterValues("city")).thenReturn(new String[]{ "LA" });
            Mockito.when(request.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
            Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("Connection", "User-Agent")));
            Mockito.when(request.getHeader("Connection")).thenReturn("keep-alive");
            Mockito.when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
            Response response = Mockito.mock(Response.class);
            Mockito.when(response.getStatus()).thenReturn(200);
            Mockito.when(response.getContentCount()).thenReturn(8290L);
            HttpFields httpFields = new HttpFields();
            httpFields.add("Date", "Mon, 16 Nov 2012 05:00:48 GMT");
            httpFields.add("Server", "Apache/2.4.12");
            Mockito.when(response.getHttpFields()).thenReturn(httpFields);
            Mockito.when(response.getHeaderNames()).thenReturn(Arrays.asList("Date", "Server"));
            Mockito.when(response.getHeader("Date")).thenReturn("Mon, 16 Nov 2012 05:00:48 GMT");
            Mockito.when(response.getHeader("Server")).thenReturn("Apache/2.4.12");
            requestLog.log(request, response);
            Thread.sleep(100);// Need to wait, because the logger is async

            JsonNode jsonNode = objectMapper.readTree(redirectedStream.toString());
            assertThat(jsonNode).isNotNull();
            assertThat(jsonNode.get("timestamp").isNumber()).isTrue();
            assertThat(jsonNode.get("requestTime").isNumber()).isTrue();
            assertThat(jsonNode.get("remoteAddress").asText()).isEqualTo("10.0.0.1");
            assertThat(jsonNode.get("status").asInt()).isEqualTo(200);
            assertThat(jsonNode.get("method").asText()).isEqualTo("GET");
            assertThat(jsonNode.get("uri").asText()).isEqualTo("/test/users");
            assertThat(jsonNode.get("protocol").asText()).isEqualTo("HTTP/1.1");
            assertThat(jsonNode.get("userAgent").asText()).isEqualTo("Mozilla/5.0");
            assertThat(jsonNode.get("contentLength").asInt()).isEqualTo(8290);
        } finally {
            System.setErr(old);
        }
    }
}

