package org.baeldung.httpclient;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.junit.Test;


public class HttpClientHeadersLiveTest {
    private static final String SAMPLE_URL = "http://www.github.com";

    private CloseableHttpClient client;

    private CloseableHttpResponse response;

    // tests - headers - deprecated
    @Test
    public final void givenNewApi_whenClientUsesCustomUserAgent_thenCorrect() throws IOException, ClientProtocolException {
        client = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").build();
        final HttpGet request = new HttpGet(HttpClientHeadersLiveTest.SAMPLE_URL);
        response = client.execute(request);
    }

    // tests - headers - user agent
    @Test
    public final void givenConfigOnRequest_whenRequestHasCustomUserAgent_thenCorrect() throws IOException, ClientProtocolException {
        client = HttpClients.custom().build();
        final HttpGet request = new HttpGet(HttpClientHeadersLiveTest.SAMPLE_URL);
        request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
        response = client.execute(request);
    }

    @Test
    public final void givenConfigOnClient_whenRequestHasCustomUserAgent_thenCorrect() throws IOException, ClientProtocolException {
        client = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").build();
        response = client.execute(new HttpGet(HttpClientHeadersLiveTest.SAMPLE_URL));
    }

    // tests - headers - content type
    @Test
    public final void givenUsingNewApi_whenRequestHasCustomContentType_thenCorrect() throws IOException, ClientProtocolException {
        client = HttpClients.custom().build();
        final HttpGet request = new HttpGet(HttpClientHeadersLiveTest.SAMPLE_URL);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response = client.execute(request);
    }

    @Test
    public final void givenRequestBuildWithBuilderWithNewApi_whenRequestHasCustomContentType_thenCorrect() throws IOException, ClientProtocolException {
        final CloseableHttpClient client2 = HttpClients.custom().build();
        final HttpGet request = new HttpGet(HttpClientHeadersLiveTest.SAMPLE_URL);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response = client2.execute(request);
    }

    @Test
    public final void givenRequestBuildWithBuilder_whenRequestHasCustomContentType_thenCorrect() throws IOException, ClientProtocolException {
        client = HttpClients.custom().build();
        final HttpUriRequest request = RequestBuilder.get().setUri(HttpClientHeadersLiveTest.SAMPLE_URL).setHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
        response = client.execute(request);
    }

    @Test
    public final void givenConfigOnClient_whenRequestHasCustomContentType_thenCorrect() throws IOException, ClientProtocolException {
        final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        final List<Header> headers = Lists.newArrayList(header);
        client = HttpClients.custom().setDefaultHeaders(headers).build();
        final HttpUriRequest request = RequestBuilder.get().setUri(HttpClientHeadersLiveTest.SAMPLE_URL).build();
        response = client.execute(request);
    }
}

