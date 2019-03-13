package com.baeldung.java9.httpclient;


import HttpClient.Redirect.ALWAYS;
import HttpClient.Version.HTTP_1_1;
import HttpRequest.BodyProcessor;
import HttpResponse.BodyHandler;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by adam.
 */
public class HttpClientTest {
    @Test
    public void shouldReturnSampleDataContentWhenConnectViaSystemProxy() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromString("Sample body")).build();
        HttpResponse<String> response = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample body"));
    }

    @Test
    public void shouldNotFollowRedirectWhenSetToDefaultNever() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_1_1).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_MOVED_PERM));
        Assert.assertThat(response.body(), CoreMatchers.containsString("https://stackoverflow.com/"));
    }

    @Test
    public void shouldFollowRedirectWhenSetToAlways() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_1_1).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(ALWAYS).build().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.finalRequest().uri().toString(), CoreMatchers.equalTo("https://stackoverflow.com/"));
    }

    @Test
    public void shouldReturnOKStatusForAuthenticatedAccess() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/basic-auth")).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("postman", "password".toCharArray());
            }
        }).build().send(request, BodyHandler.asString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldSendRequestAsync() throws InterruptedException, URISyntaxException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyProcessor.fromString("Sample body")).build();
        CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder().build().sendAsync(request, BodyHandler.asString());
        Assert.assertThat(response.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldUseJustTwoThreadWhenProcessingSendAsyncRequest() throws InterruptedException, URISyntaxException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture<HttpResponse<String>> response1 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandler.asString());
        CompletableFuture<HttpResponse<String>> response2 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandler.asString());
        CompletableFuture<HttpResponse<String>> response3 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandler.asString());
        CompletableFuture.allOf(response1, response2, response3).join();
        Assert.assertThat(response1.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response2.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response3.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldNotStoreCookieWhenPolicyAcceptNone() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpClient httpClient = HttpClient.newBuilder().cookieManager(new CookieManager(null, CookiePolicy.ACCEPT_NONE)).build();
        httpClient.send(request, BodyHandler.asString());
        Assert.assertThat(httpClient.cookieManager().get().getCookieStore().getCookies(), empty());
    }

    @Test
    public void shouldStoreCookieWhenPolicyAcceptAll() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpClient httpClient = HttpClient.newBuilder().cookieManager(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        httpClient.send(request, BodyHandler.asString());
        Assert.assertThat(httpClient.cookieManager().get().getCookieStore().getCookies(), IsNot.not(empty()));
    }

    @Test
    public void shouldProcessMultipleRequestViaStream() throws InterruptedException, URISyntaxException, ExecutionException {
        List<URI> targets = Arrays.asList(new URI("https://postman-echo.com/get?foo1=bar1"), new URI("https://postman-echo.com/get?foo2=bar2"));
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<String>> futures = targets.stream().map(( target) -> client.sendAsync(HttpRequest.newBuilder(target).GET().build(), BodyHandler.asString()).thenApply(( response) -> response.body())).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if (futures.get(0).get().contains("foo1")) {
            Assert.assertThat(futures.get(0).get(), CoreMatchers.containsString("bar1"));
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar2"));
        } else {
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar2"));
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar1"));
        }
    }
}

