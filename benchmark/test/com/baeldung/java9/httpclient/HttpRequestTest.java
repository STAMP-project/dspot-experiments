package com.baeldung.java9.httpclient;


import HttpClient.Version.HTTP_1_1;
import HttpClient.Version.HTTP_2;
import HttpRequest.BodyProcessor;
import HttpResponse.BodyHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by adam.
 */
public class HttpRequestTest {
    @Test
    public void shouldReturnStatusOKWhenSendGetRequest() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldUseHttp2WhenWebsiteUsesHttp2() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://stackoverflow.com")).version(HTTP_2).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.version(), CoreMatchers.equalTo(HTTP_2));
    }

    @Test
    public void shouldFallbackToHttp1_1WhenWebsiteDoesNotUseHttp2() throws IOException, InterruptedException, URISyntaxException, NoSuchAlgorithmException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).version(HTTP_2).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.version(), CoreMatchers.equalTo(HTTP_1_1));
    }

    @Test
    public void shouldReturnStatusOKWhenSendGetRequestWithDummyHeaders() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).headers("key1", "value1", "key2", "value2").GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldReturnStatusOKWhenSendGetRequestTimeoutSet() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).timeout(Duration.of(10, ChronoUnit.SECONDS)).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldReturnNoContentWhenPostWithNoBody() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).POST(HttpRequest.noBody()).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldReturnSampleDataContentWhenPostWithBodyText() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromString("Sample request body")).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample request body"));
    }

    @Test
    public void shouldReturnSampleDataContentWhenPostWithInputStream() throws IOException, InterruptedException, URISyntaxException {
        byte[] sampleData = "Sample request body".getBytes();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromInputStream(() -> new ByteArrayInputStream(sampleData))).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample request body"));
    }

    @Test
    public void shouldReturnSampleDataContentWhenPostWithByteArrayProcessorStream() throws IOException, InterruptedException, URISyntaxException {
        byte[] sampleData = "Sample request body".getBytes();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromByteArray(sampleData)).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample request body"));
    }

    @Test
    public void shouldReturnSampleDataContentWhenPostWithFileProcessorStream() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromFile(Paths.get("src/test/resources/sample.txt"))).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample file content"));
    }
}

