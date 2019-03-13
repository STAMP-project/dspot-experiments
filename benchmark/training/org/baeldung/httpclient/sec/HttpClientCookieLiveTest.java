package org.baeldung.httpclient.sec;


import HttpClientContext.COOKIE_STORE;
import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientCookieLiveTest {
    private CloseableHttpClient instance;

    private CloseableHttpResponse response;

    // tests
    @Test
    public final void whenSettingCookiesOnARequest_thenCorrect() throws IOException {
        instance = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet("http://www.github.com");
        request.setHeader("Cookie", "JSESSIONID=1234");
        response = instance.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenUsingDeprecatedApi_whenSettingCookiesOnTheHttpClient_thenCorrect() throws IOException {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        final HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        final HttpGet request = new HttpGet("http://www.github.com");
        response = ((CloseableHttpResponse) (client.execute(request)));
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void whenSettingCookiesOnTheHttpClient_thenCookieSentCorrectly() throws IOException {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        instance = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        final HttpGet request = new HttpGet("http://www.github.com");
        response = instance.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void whenSettingCookiesOnTheRequest_thenCookieSentCorrectly() throws IOException {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        instance = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet("http://www.github.com");
        final HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(COOKIE_STORE, cookieStore);
        // localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore); // before 4.3
        response = instance.execute(request, localContext);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }
}

