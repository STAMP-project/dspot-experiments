package com.codahale.metrics.httpasyncclient;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Timer;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InstrumentedHttpClientsTest extends HttpClientTestBase {
    private final MetricRegistry metricRegistry = new MetricRegistry();

    private HttpAsyncClient asyncHttpClient;

    @Mock
    private HttpClientMetricNameStrategy metricNameStrategy;

    @Mock
    private MetricRegistryListener registryListener;

    @Test
    public void registersExpectedMetricsGivenNameStrategy() throws Exception {
        HttpHost host = startServerWithGlobalRequestHandler(HttpClientTestBase.STATUS_OK);
        final HttpGet get = new HttpGet("/q=anything");
        final String metricName = MetricRegistry.name("some.made.up.metric.name");
        Mockito.when(metricNameStrategy.getNameFor(ArgumentMatchers.any(), ArgumentMatchers.any(HttpRequest.class))).thenReturn(metricName);
        asyncHttpClient.execute(host, get, null).get();
        Mockito.verify(registryListener).onTimerAdded(ArgumentMatchers.eq(metricName), ArgumentMatchers.any(Timer.class));
    }
}

