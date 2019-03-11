package com.baeldung.java11.httpclient.test;


import HttpClient.Redirect.ALWAYS;
import HttpClient.Version.HTTP_1_1;
import HttpRequest.BodyPublishers;
import HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class HttpClientTest {
    @Test
    public void shouldReturnSampleDataContentWhenConnectViaSystemProxy() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyPublishers.ofString("Sample body")).build();
        HttpResponse<String> response = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build().send(request, BodyHandlers.ofString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.body(), CoreMatchers.containsString("Sample body"));
    }

    @Test
    public void shouldNotFollowRedirectWhenSetToDefaultNever() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_1_1).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_MOVED_PERM));
        Assert.assertThat(response.body(), CoreMatchers.containsString("https://stackoverflow.com/"));
    }

    @Test
    public void shouldFollowRedirectWhenSetToAlways() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com")).version(HTTP_1_1).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(ALWAYS).build().send(request, BodyHandlers.ofString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response.request().uri().toString(), CoreMatchers.equalTo("https://stackoverflow.com/"));
    }

    @Test
    public void shouldReturnOKStatusForAuthenticatedAccess() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/basic-auth")).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("postman", "password".toCharArray());
            }
        }).build().send(request, BodyHandlers.ofString());
        Assert.assertThat(response.statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldSendRequestAsync() throws InterruptedException, URISyntaxException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post")).headers("Content-Type", "text/plain;charset=UTF-8").POST(BodyPublishers.ofString("Sample body")).build();
        CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder().build().sendAsync(request, BodyHandlers.ofString());
        Assert.assertThat(response.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldUseJustTwoThreadWhenProcessingSendAsyncRequest() throws InterruptedException, URISyntaxException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture<HttpResponse<String>> response1 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> response2 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> response3 = HttpClient.newBuilder().executor(executorService).build().sendAsync(request, BodyHandlers.ofString());
        CompletableFuture.allOf(response1, response2, response3).join();
        Assert.assertThat(response1.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response2.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
        Assert.assertThat(response3.get().statusCode(), CoreMatchers.equalTo(HttpURLConnection.HTTP_OK));
    }

    @Test
    public void shouldNotStoreCookieWhenPolicyAcceptNone() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_NONE)).build();
        httpClient.send(request, BodyHandlers.ofString());
        Assert.assertTrue(httpClient.cookieHandler().isPresent());
    }

    @Test
    public void shouldStoreCookieWhenPolicyAcceptAll() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();
        HttpClient httpClient = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        httpClient.send(request, BodyHandlers.ofString());
        Assert.assertTrue(httpClient.cookieHandler().isPresent());
    }

    @Test
    public void shouldProcessMultipleRequestViaStream() throws InterruptedException, URISyntaxException, ExecutionException {
        List<URI> targets = Arrays.asList(new URI("https://postman-echo.com/get?foo1=bar1"), new URI("https://postman-echo.com/get?foo2=bar2"));
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<String>> futures = targets.stream().map(( target) -> client.sendAsync(HttpRequest.newBuilder(target).GET().build(), BodyHandlers.ofString()).thenApply(( response) -> response.body())).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if (futures.get(0).get().contains("foo1")) {
            Assert.assertThat(futures.get(0).get(), CoreMatchers.containsString("bar1"));
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar2"));
        } else {
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar2"));
            Assert.assertThat(futures.get(1).get(), CoreMatchers.containsString("bar1"));
        }
    }

    @Test
    public void completeExceptionallyExample() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(String::toUpperCase, CompletableFuture.delayedExecutor(CompletableFuture, 1, TimeUnit.SECONDS));
        CompletableFuture<String> exceptionHandler = cf.handle(( s, th) -> {
            return th != null ? "message upon cancel" : "";
        });
        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
        Assert.assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        try {
            cf.join();
            Assert.fail("Should have thrown an exception");
        } catch (CompletionException ex) {
            // just for testing
            Assert.assertEquals("completed exceptionally", ex.getCause().getMessage());
        }
        Assert.assertEquals("message upon cancel", exceptionHandler.join());
    }
}

