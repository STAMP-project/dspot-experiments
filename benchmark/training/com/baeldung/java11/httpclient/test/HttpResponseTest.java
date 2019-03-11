package com.baeldung.java11.httpclient.test;


import HttpClient.Redirect.NORMAL;
import HttpClient.Version.HTTP_2;
import HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpResponseTest {
    @Test
    public void shouldReturnStatusOKWhenSendGetRequest() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).version(HTTP_2).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(NORMAL).build().send(request, BodyHandlers.ofString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertNotNull(response.body());
    }

    @Test
    public void shouldResponseURIDifferentThanRequestUIRWhenRedirect() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_2).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(NORMAL).build().send(request, BodyHandlers.ofString());
        Assert.assertThat(request.uri().toString(), CoreMatchers.equalTo("http://stackoverflow.com"));
        Assert.assertThat(response.uri().toString(), CoreMatchers.equalTo("https://stackoverflow.com/"));
    }
}

