package com.codahale.metrics.httpasyncclient;


import Timer.Context;
import com.codahale.metrics.MetricRegistry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@Ignore("The tests are flaky")
public class InstrumentedHttpClientsTimerTest extends HttpClientTestBase {
    private HttpAsyncClient asyncHttpClient;

    @Mock
    private Context context;

    @Mock
    private MetricRegistry metricRegistry;

    @Test
    public void timerIsStoppedCorrectly() throws Exception {
        HttpHost host = startServerWithGlobalRequestHandler(HttpClientTestBase.STATUS_OK);
        HttpGet get = new HttpGet("/?q=anything");
        // Timer hasn't been stopped prior to executing the request
        Mockito.verify(context, Mockito.never()).stop();
        Future<HttpResponse> responseFuture = asyncHttpClient.execute(host, get, null);
        // Timer should still be running
        Mockito.verify(context, Mockito.never()).stop();
        responseFuture.get(20, TimeUnit.SECONDS);
        // After the computation is complete timer must be stopped
        // Materialzing the future and calling the future callback is not an atomic operation so
        // we need to wait for callback to succeed
        Mockito.verify(context, Mockito.timeout(200).times(1)).stop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void timerIsStoppedCorrectlyWithProvidedFutureCallbackCompleted() throws Exception {
        HttpHost host = startServerWithGlobalRequestHandler(HttpClientTestBase.STATUS_OK);
        HttpGet get = new HttpGet("/?q=something");
        FutureCallback<HttpResponse> futureCallback = Mockito.mock(FutureCallback.class);
        // Timer hasn't been stopped prior to executing the request
        Mockito.verify(context, Mockito.never()).stop();
        Future<HttpResponse> responseFuture = asyncHttpClient.execute(host, get, futureCallback);
        // Timer should still be running
        Mockito.verify(context, Mockito.never()).stop();
        responseFuture.get(20, TimeUnit.SECONDS);
        // Callback must have been called
        assertThat(responseFuture.isDone()).isTrue();
        // After the computation is complete timer must be stopped
        // Materialzing the future and calling the future callback is not an atomic operation so
        // we need to wait for callback to succeed
        Mockito.verify(futureCallback, Mockito.timeout(200).times(1)).completed(ArgumentMatchers.any(HttpResponse.class));
        Mockito.verify(context, Mockito.timeout(200).times(1)).stop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void timerIsStoppedCorrectlyWithProvidedFutureCallbackFailed() throws Exception {
        // There should be nothing listening on this port
        HttpHost host = create(String.format("http://127.0.0.1:%d", HttpClientTestBase.findAvailableLocalPort()));
        HttpGet get = new HttpGet("/?q=something");
        FutureCallback<HttpResponse> futureCallback = Mockito.mock(FutureCallback.class);
        // Timer hasn't been stopped prior to executing the request
        Mockito.verify(context, Mockito.never()).stop();
        Future<HttpResponse> responseFuture = asyncHttpClient.execute(host, get, futureCallback);
        // Timer should still be running
        Mockito.verify(context, Mockito.never()).stop();
        try {
            responseFuture.get(20, TimeUnit.SECONDS);
            Assert.fail("This should fail as the client should not be able to connect");
        } catch (Exception e) {
            // Ignore
        }
        // After the computation is complete timer must be stopped
        // Materialzing the future and calling the future callback is not an atomic operation so
        // we need to wait for callback to succeed
        Mockito.verify(futureCallback, Mockito.timeout(200).times(1)).failed(ArgumentMatchers.any(Exception.class));
        Mockito.verify(context, Mockito.timeout(200).times(1)).stop();
    }
}

