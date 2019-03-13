package com.baeldung.java9.httpclient;


import HttpClient.Redirect.ALWAYS;
import HttpClient.Version.HTTP_1_1;
import HttpResponse.BodyHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by adam.
 */
public class HttpResponseTest {
    @Test
    public void shouldReturnStatusOKWhenSendGetRequest() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), IsNot.not(Matchers.isEmptyString()));
    }

    @Test
    public void shouldResponseURIDifferentThanRequestUIRWhenRedirect() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_1_1).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(ALWAYS).build().send(request, BodyHandler.asString());
        Assert.assertThat(request.uri().toString(), CoreMatchers.equalTo("http://stackoverflow.com"));
        Assert.assertThat(response.uri().toString(), CoreMatchers.equalTo("https://stackoverflow.com/"));
    }
}

