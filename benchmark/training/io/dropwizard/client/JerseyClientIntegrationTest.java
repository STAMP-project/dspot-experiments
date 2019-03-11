package io.dropwizard.client;


import HttpHeaders.ACCEPT_ENCODING;
import HttpHeaders.CONTENT_ENCODING;
import HttpHeaders.CONTENT_LENGTH;
import HttpHeaders.CONTENT_TYPE;
import HttpHeaders.USER_AGENT;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.dropwizard.jackson.Jackson;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.Test;


/**
 * Integration test of {@link org.glassfish.jersey.client.JerseyClient}
 * with {@link io.dropwizard.client.DropwizardApacheConnector}
 */
public class JerseyClientIntegrationTest {
    private static final String TRANSFER_ENCODING = "Transfer-Encoding";

    private static final String CHUNKED = "chunked";

    private static final String GZIP = "gzip";

    private static final ObjectMapper JSON_MAPPER = Jackson.newObjectMapper();

    private static final String GZIP_DEFLATE = "gzip,deflate";

    private static final String JSON_TOKEN = JerseyClientIntegrationTest.JSON_MAPPER.createObjectNode().put("id", 214).put("token", "a23f78bc31cc5de821ad9412e").toString();

    private HttpServer httpServer;

    @Test
    public void testChunkedGzipPost() throws Exception {
        httpServer.createContext("/register", ( httpExchange) -> {
            try {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                assertThat(requestHeaders.get(JerseyClientIntegrationTest.TRANSFER_ENCODING)).containsExactly(JerseyClientIntegrationTest.CHUNKED);
                assertThat(requestHeaders.get(CONTENT_LENGTH)).isNull();
                assertThat(requestHeaders.get(CONTENT_ENCODING)).containsExactly(JerseyClientIntegrationTest.GZIP);
                assertThat(requestHeaders.get(ACCEPT_ENCODING)).containsExactly(JerseyClientIntegrationTest.GZIP_DEFLATE);
                checkBody(httpExchange, true);
                postResponse(httpExchange);
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        postRequest(new JerseyClientConfiguration());
    }

    @Test
    public void testBufferedGzipPost() {
        httpServer.createContext("/register", ( httpExchange) -> {
            try {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                assertThat(requestHeaders.get(CONTENT_LENGTH)).containsExactly("58");
                assertThat(requestHeaders.get(JerseyClientIntegrationTest.TRANSFER_ENCODING)).isNull();
                assertThat(requestHeaders.get(CONTENT_ENCODING)).containsExactly(JerseyClientIntegrationTest.GZIP);
                assertThat(requestHeaders.get(ACCEPT_ENCODING)).containsExactly(JerseyClientIntegrationTest.GZIP_DEFLATE);
                checkBody(httpExchange, true);
                postResponse(httpExchange);
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setChunkedEncodingEnabled(false);
        postRequest(configuration);
    }

    @Test
    public void testChunkedPost() throws Exception {
        httpServer.createContext("/register", ( httpExchange) -> {
            try {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                assertThat(requestHeaders.get(JerseyClientIntegrationTest.TRANSFER_ENCODING)).containsExactly(JerseyClientIntegrationTest.CHUNKED);
                assertThat(requestHeaders.get(CONTENT_LENGTH)).isNull();
                assertThat(requestHeaders.get(CONTENT_ENCODING)).isNull();
                assertThat(requestHeaders.get(ACCEPT_ENCODING)).containsExactly(JerseyClientIntegrationTest.GZIP_DEFLATE);
                checkBody(httpExchange, false);
                postResponse(httpExchange);
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setGzipEnabledForRequests(false);
        postRequest(configuration);
    }

    @Test
    public void testChunkedPostWithoutGzip() throws Exception {
        httpServer.createContext("/register", ( httpExchange) -> {
            try {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                assertThat(requestHeaders.get(JerseyClientIntegrationTest.TRANSFER_ENCODING)).containsExactly(JerseyClientIntegrationTest.CHUNKED);
                assertThat(requestHeaders.get(CONTENT_LENGTH)).isNull();
                assertThat(requestHeaders.get(CONTENT_ENCODING)).isNull();
                assertThat(requestHeaders.get(ACCEPT_ENCODING)).isNull();
                checkBody(httpExchange, false);
                httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().write(JerseyClientIntegrationTest.JSON_TOKEN.getBytes(StandardCharsets.UTF_8));
                httpExchange.getResponseBody().close();
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setGzipEnabled(false);
        configuration.setGzipEnabledForRequests(false);
        postRequest(configuration);
    }

    @Test
    public void testRetryHandler() throws Exception {
        httpServer.createContext("/register", ( httpExchange) -> {
            try {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                assertThat(requestHeaders.get(JerseyClientIntegrationTest.TRANSFER_ENCODING)).containsExactly(JerseyClientIntegrationTest.CHUNKED);
                assertThat(requestHeaders.get(CONTENT_LENGTH)).isNull();
                assertThat(requestHeaders.get(CONTENT_ENCODING)).isNull();
                assertThat(requestHeaders.get(ACCEPT_ENCODING)).isNull();
                checkBody(httpExchange, false);
                httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().write(JerseyClientIntegrationTest.JSON_TOKEN.getBytes(StandardCharsets.UTF_8));
                httpExchange.getResponseBody().close();
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setGzipEnabled(false);
        configuration.setGzipEnabledForRequests(false);
        postRequest(configuration);
    }

    @Test
    public void testGet() {
        httpServer.createContext("/player", ( httpExchange) -> {
            try {
                assertThat(httpExchange.getRequestURI().getQuery()).isEqualTo("id=21");
                httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().write(JerseyClientIntegrationTest.JSON_MAPPER.createObjectNode().put("email", "john@doe.me").put("name", "John Doe").toString().getBytes(StandardCharsets.UTF_8));
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Client jersey = using(executor, JerseyClientIntegrationTest.JSON_MAPPER).using(new JerseyClientConfiguration()).build("jersey-test");
        Response response = jersey.target((("http://127.0.0.1:" + (httpServer.getAddress().getPort())) + "/player?id=21")).request().buildGet().invoke();
        assertThat(response.getHeaderString(CONTENT_TYPE)).isEqualTo(APPLICATION_JSON);
        assertThat(response.getHeaderString(JerseyClientIntegrationTest.TRANSFER_ENCODING)).isEqualTo(JerseyClientIntegrationTest.CHUNKED);
        JerseyClientIntegrationTest.Person person = response.readEntity(JerseyClientIntegrationTest.Person.class);
        assertThat(person.email).isEqualTo("john@doe.me");
        assertThat(person.name).isEqualTo("John Doe");
        executor.shutdown();
        jersey.close();
    }

    @Test
    public void testSetUserAgent() {
        httpServer.createContext("/test", ( httpExchange) -> {
            try {
                assertThat(httpExchange.getRequestHeaders().get(USER_AGENT)).containsExactly("Custom user-agent");
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().write("Hello World!".getBytes(StandardCharsets.UTF_8));
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setUserAgent(Optional.of("Custom user-agent"));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Client jersey = using(executor, JerseyClientIntegrationTest.JSON_MAPPER).using(configuration).build("jersey-test");
        String text = jersey.target((("http://127.0.0.1:" + (httpServer.getAddress().getPort())) + "/test")).request().buildGet().invoke().readEntity(String.class);
        assertThat(text).isEqualTo("Hello World!");
        executor.shutdown();
        jersey.close();
    }

    /**
     * Test for ConnectorProvider idempotency
     */
    @Test
    public void testFilterOnAWebTarget() {
        httpServer.createContext("/test", ( httpExchange) -> {
            try {
                httpExchange.getResponseHeaders().add(CONTENT_TYPE, TEXT_PLAIN);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().write("Hello World!".getBytes(StandardCharsets.UTF_8));
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Client jersey = using(executor, JerseyClientIntegrationTest.JSON_MAPPER).build("test-jersey-client");
        String uri = ("http://127.0.0.1:" + (httpServer.getAddress().getPort())) + "/test";
        WebTarget target = jersey.target(uri);
        target.register(new LoggingFeature());
        String firstResponse = target.request().buildGet().invoke().readEntity(String.class);
        assertThat(firstResponse).isEqualTo("Hello World!");
        String secondResponse = jersey.target(uri).request().buildGet().invoke().readEntity(String.class);
        assertThat(secondResponse).isEqualTo("Hello World!");
        executor.shutdown();
        jersey.close();
    }

    @Test
    public void testAsyncWithCustomized() throws Exception {
        httpServer.createContext("/test", ( httpExchange) -> {
            try {
                httpExchange.getResponseHeaders().add(CONTENT_TYPE, TEXT_PLAIN);
                byte[] body = "Hello World!".getBytes(StandardCharsets.UTF_8);
                httpExchange.sendResponseHeaders(200, body.length);
                httpExchange.getResponseBody().write(body);
            } finally {
                httpExchange.close();
            }
        });
        httpServer.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Client jersey = using(executor, JerseyClientIntegrationTest.JSON_MAPPER).build("test-jersey-client");
        String uri = ("http://127.0.0.1:" + (httpServer.getAddress().getPort())) + "/test";
        final List<CompletableFuture<String>> requests = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            requests.add(jersey.target(uri).register(HttpAuthenticationFeature.basic("scott", "t1ger")).request().rx().get(String.class).toCompletableFuture());
        }
        final CompletableFuture<Void> allDone = CompletableFuture.allOf(requests.toArray(new CompletableFuture[0]));
        final CompletableFuture<List<String>> futures = allDone.thenApply(( x) -> requests.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        final List<String> responses = futures.get(5, TimeUnit.SECONDS);
        assertThat(futures).isCompleted();
        assertThat(responses).hasSize(25).allMatch(( x) -> x.equals("Hello World!"));
        executor.shutdown();
        jersey.close();
    }

    static class Person {
        @JsonProperty("email")
        final String email;

        @JsonProperty("name")
        final String name;

        Person(@JsonProperty("email")
        String email, @JsonProperty("name")
        String name) {
            this.email = email;
            this.name = name;
        }
    }

    static class Credentials {
        @JsonProperty("id")
        final long id;

        @JsonProperty("token")
        final String token;

        Credentials(@JsonProperty("id")
        long id, @JsonProperty("token")
        String token) {
            this.id = id;
            this.token = token;
        }
    }
}

