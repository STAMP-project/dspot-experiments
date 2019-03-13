package com.baeldung.okhttp;


import com.baeldung.client.Consts;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OkHttpMiscLiveTest {
    private static final String BASE_URL = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-rest";

    private static Logger logger = LoggerFactory.getLogger(OkHttpMiscLiveTest.class);

    OkHttpClient client;

    @Test(expected = SocketTimeoutException.class)
    public void whenSetRequestTimeout_thenFail() throws IOException {
        final OkHttpClient clientWithTimeout = new OkHttpClient.Builder().readTimeout(1, TimeUnit.SECONDS).build();
        final Request request = // This URL is served with a 2 second delay.
        new Request.Builder().url(((OkHttpMiscLiveTest.BASE_URL) + "/delay/2")).build();
        final Call call = clientWithTimeout.newCall(request);
        final Response response = call.execute();
        response.close();
    }

    @Test(expected = IOException.class)
    public void whenCancelRequest_thenCorrect() throws IOException {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        final Request request = // This URL is served with a 2 second delay.
        new Request.Builder().url(((OkHttpMiscLiveTest.BASE_URL) + "/delay/2")).build();
        final int seconds = 1;
        final long startNanos = System.nanoTime();
        final Call call = client.newCall(request);
        // Schedule a job to cancel the call in 1 second.
        executor.schedule(() -> {
            OkHttpMiscLiveTest.logger.debug(("Canceling call: " + (((System.nanoTime()) - startNanos) / 1.0E9F)));
            call.cancel();
            OkHttpMiscLiveTest.logger.debug(("Canceled call: " + (((System.nanoTime()) - startNanos) / 1.0E9F)));
        }, seconds, TimeUnit.SECONDS);
        OkHttpMiscLiveTest.logger.debug(("Executing call: " + (((System.nanoTime()) - startNanos) / 1.0E9F)));
        final Response response = call.execute();
        OkHttpMiscLiveTest.logger.debug(("Call completed: " + (((System.nanoTime()) - startNanos) / 1.0E9F)), response);
    }

    @Test
    public void whenSetResponseCache_thenCorrect() throws IOException {
        final int cacheSize = (10 * 1024) * 1024;// 10 MiB

        final File cacheDirectory = new File("src/test/resources/cache");
        final Cache cache = new Cache(cacheDirectory, cacheSize);
        final OkHttpClient clientCached = new OkHttpClient.Builder().cache(cache).build();
        final Request request = new Request.Builder().url("http://publicobject.com/helloworld.txt").build();
        final Response response1 = clientCached.newCall(request).execute();
        logResponse(response1);
        final Response response2 = clientCached.newCall(request).execute();
        logResponse(response2);
    }
}

