package org.baeldung.httpclient;


import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientRedirectLiveTest {
    private CloseableHttpClient instance;

    private CloseableHttpResponse response;

    // tests
    @Test
    public final void givenRedirectsAreDisabledViaNewApi_whenConsumingUrlWhichRedirects_thenNotRedirected() throws IOException {
        instance = HttpClients.custom().disableRedirectHandling().build();
        final HttpGet httpGet = new HttpGet("http://t.co/I5YYd9tddw");
        response = instance.execute(httpGet);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(301));
    }

    @Test
    public final void givenRedirectsAreDisabled_whenConsumingUrlWhichRedirects_thenNotRedirected() throws IOException {
        instance = HttpClientBuilder.create().disableRedirectHandling().build();
        response = instance.execute(new HttpGet("http://t.co/I5YYd9tddw"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(301));
    }

    // redirect with POST
    @Test
    public final void givenPostRequest_whenConsumingUrlWhichRedirects_thenNotRedirected() throws IOException {
        instance = HttpClientBuilder.create().build();
        response = instance.execute(new HttpPost("http://t.co/I5YYd9tddw"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(301));
    }

    @Test
    public final void givenRedirectingPOSTViaPost4_2Api_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() throws IOException {
        final CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new DefaultRedirectStrategy() {
            /**
             * Redirectable methods.
             */
            private final String[] REDIRECT_METHODS = new String[]{ HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpHead.METHOD_NAME };

            @Override
            protected boolean isRedirectable(final String method) {
                return Arrays.stream(REDIRECT_METHODS).anyMatch(( m) -> m.equalsIgnoreCase(method));
            }
        }).build();
        response = client.execute(new HttpPost("http://t.co/I5YYd9tddw"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenRedirectingPOSTVia4_2Api_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() throws IOException {
        final CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        response = client.execute(new HttpPost("http://t.co/I5YYd9tddw"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenRedirectingPOST_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() throws IOException {
        instance = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        response = instance.execute(new HttpPost("http://t.co/I5YYd9tddw"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }
}

