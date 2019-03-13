package io.dropwizard.logging.json.layout;


import AccessAttribute.CONTENT_LENGTH;
import AccessAttribute.METHOD;
import AccessAttribute.PROTOCOL;
import AccessAttribute.REMOTE_ADDRESS;
import AccessAttribute.REMOTE_USER;
import AccessAttribute.REQUEST_TIME;
import AccessAttribute.REQUEST_URI;
import AccessAttribute.STATUS_CODE;
import AccessAttribute.TIMESTAMP;
import AccessAttribute.USER_AGENT;
import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.json.AccessAttribute;
import io.dropwizard.util.Maps;
import io.dropwizard.util.Sets;
import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class AccessJsonLayoutTest {
    private String remoteHost = "nw-4.us.crawl.io";

    private String serverName = "sw-2.us.api.example.io";

    private String timestamp = "2018-01-01T14:35:21.000+0000";

    private String uri = "/test/users";

    private String query = "?age=22&city=LA";

    private String pathQuery = (uri) + (query);

    private String url = "GET /test/users?age=22&city=LA HTTP/1.1";

    private String userAgent = "Mozilla/5.0";

    private Map<String, String> requestHeaders;

    private Map<String, String> responseHeaders;

    private String responseContent = "{\"message\":\"Hello, Crawler!\"}";

    private String remoteAddress = "192.168.52.15";

    private IAccessEvent event = Mockito.mock(IAccessEvent.class);

    private TimestampFormatter timestampFormatter = new TimestampFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSZ", ZoneId.of("UTC"));

    private ObjectMapper objectMapper = Jackson.newObjectMapper();

    private JsonFormatter jsonFormatter = new JsonFormatter(objectMapper, false, true);

    private Set<AccessAttribute> includes = EnumSet.of(REMOTE_ADDRESS, REMOTE_USER, REQUEST_TIME, REQUEST_URI, STATUS_CODE, METHOD, PROTOCOL, CONTENT_LENGTH, USER_AGENT, TIMESTAMP);

    private AccessJsonLayout accessJsonLayout = new AccessJsonLayout(jsonFormatter, timestampFormatter, includes, Collections.emptyMap(), Collections.emptyMap());

    @Test
    public void testProducesDefaultJsonMap() {
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress));
    }

    @Test
    public void testDisableRemoteAddress() {
        includes.remove(REMOTE_ADDRESS);
        accessJsonLayout.setIncludes(includes);
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent));
    }

    @Test
    public void testDisableTimestamp() {
        includes.remove(TIMESTAMP);
        accessJsonLayout.setIncludes(includes);
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress));
    }

    @Test
    public void testEnableSpecificResponseHeader() {
        accessJsonLayout.setResponseHeaders(Collections.singleton("transfer-encoding"));
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress), entry("responseHeaders", Collections.singletonMap("Transfer-Encoding", "chunked")));
    }

    @Test
    public void testEnableSpecificRequestHeader() {
        accessJsonLayout.setRequestHeaders(Collections.singleton("user-agent"));
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress), entry("headers", Collections.singletonMap("User-Agent", userAgent)));
    }

    @Test
    public void testEnableEverything() {
        accessJsonLayout.setIncludes(EnumSet.allOf(AccessAttribute.class));
        accessJsonLayout.setRequestHeaders(Sets.of("Host", "User-Agent"));
        accessJsonLayout.setResponseHeaders(Sets.of("Transfer-Encoding", "Content-Type"));
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress), entry("responseHeaders", this.responseHeaders), entry("responseContent", responseContent), entry("port", 8080), entry("requestContent", ""), entry("headers", this.requestHeaders), entry("remoteHost", remoteHost), entry("url", url), entry("serverName", serverName), entry("pathQuery", pathQuery));
    }

    @Test
    public void testAddAdditionalFields() {
        final Map<String, Object> additionalFields = Maps.of("serviceName", "user-service", "serviceVersion", "1.2.3");
        accessJsonLayout = new AccessJsonLayout(jsonFormatter, timestampFormatter, includes, Collections.emptyMap(), additionalFields);
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remoteUser", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("requestTime", 100L), entry("contentLength", 78L), entry("userAgent", userAgent), entry("remoteAddress", remoteAddress), entry("serviceName", "user-service"), entry("serviceVersion", "1.2.3"));
    }

    @Test
    public void testCustomFieldNames() {
        final Map<String, String> customFieldNames = Maps.of("remoteUser", "remote_user", "userAgent", "user_agent", "remoteAddress", "remote_address", "contentLength", "content_length", "requestTime", "request_time");
        accessJsonLayout = new AccessJsonLayout(jsonFormatter, timestampFormatter, includes, customFieldNames, Collections.emptyMap());
        assertThat(accessJsonLayout.toJsonMap(event)).containsOnly(entry("timestamp", timestamp), entry("remote_user", "john"), entry("method", "GET"), entry("uri", uri), entry("protocol", "HTTP/1.1"), entry("status", 200), entry("request_time", 100L), entry("content_length", 78L), entry("user_agent", userAgent), entry("remote_address", remoteAddress));
    }

    @Test
    public void testProducesCorrectJson() throws Exception {
        JsonNode json = objectMapper.readTree(accessJsonLayout.doLayout(event));
        assertThat(json).isNotNull();
        assertThat(json.get("timestamp").asText()).isEqualTo(timestamp);
        assertThat(json.get("remoteUser").asText()).isEqualTo("john");
        assertThat(json.get("method").asText()).isEqualTo("GET");
        assertThat(json.get("uri").asText()).isEqualTo(uri);
        assertThat(json.get("protocol").asText()).isEqualTo("HTTP/1.1");
        assertThat(json.get("status").asInt()).isEqualTo(200);
        assertThat(json.get("requestTime").asInt()).isEqualTo(100);
        assertThat(json.get("contentLength").asInt()).isEqualTo(78);
        assertThat(json.get("userAgent").asText()).isEqualTo(userAgent);
        assertThat(json.get("remoteAddress").asText()).isEqualTo(remoteAddress);
    }
}

