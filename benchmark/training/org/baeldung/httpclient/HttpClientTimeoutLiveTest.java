package org.baeldung.httpclient;


import java.io.IOException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientTimeoutLiveTest {
    private CloseableHttpResponse response;

    // tests
    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaRequestConfig_thenCorrect() throws IOException {
        final int timeout = 2;
        final RequestConfig config = RequestConfig.custom().setConnectTimeout((timeout * 1000)).setConnectionRequestTimeout((timeout * 1000)).setSocketTimeout((timeout * 1000)).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        final HttpGet request = new HttpGet("http://www.github.com");
        // httpParams.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(timeout * 1000)); // https://issues.apache.org/jira/browse/HTTPCLIENT-1418
        response = client.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaSocketConfig_thenCorrect() throws IOException {
        final int timeout = 2;
        final SocketConfig config = SocketConfig.custom().setSoTimeout((timeout * 1000)).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultSocketConfig(config).build();
        final HttpGet request = new HttpGet("http://www.github.com");
        response = client.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaHighLevelApi_thenCorrect() throws IOException {
        final int timeout = 5;
        final RequestConfig config = RequestConfig.custom().setConnectTimeout((timeout * 1000)).setConnectionRequestTimeout((timeout * 1000)).setSocketTimeout((timeout * 1000)).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        final HttpGet request = new HttpGet("http://www.github.com");
        response = client.execute(request);
        Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
    }

    /**
     * This simulates a timeout against a domain with multiple routes/IPs to it (not a single raw IP)
     */
    @Test(expected = HttpHostConnectException.class)
    public final void givenTimeoutIsConfigured_whenTimingOut_thenTimeoutException() throws IOException {
        final int timeout = 3;
        final RequestConfig config = RequestConfig.custom().setConnectTimeout((timeout * 1000)).setConnectionRequestTimeout((timeout * 1000)).setSocketTimeout((timeout * 1000)).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        final HttpGet request = new HttpGet("http://www.google.com:81");
        client.execute(request);
    }
}

