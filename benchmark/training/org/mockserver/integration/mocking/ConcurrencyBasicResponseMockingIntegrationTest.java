package org.mockserver.integration.mocking;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


public class ConcurrencyBasicResponseMockingIntegrationTest {
    private ClientAndServer clientAndServer;

    private NettyHttpClient httpClient;

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    @Test
    public void sendMultipleRequestsSingleThreaded() throws InterruptedException, ExecutionException, TimeoutException {
        scheduleTasksAndWaitForResponses(1);
    }

    @Test
    public void sendMultipleRequestsMultiThreaded() throws InterruptedException, ExecutionException, TimeoutException {
        scheduleTasksAndWaitForResponses(100);
    }

    public class Task implements Runnable {
        @Override
        public void run() {
            ConcurrencyBasicResponseMockingIntegrationTest.this.sendRequestAndVerifyResponse();
        }
    }

    public static class ClassCallback implements ExpectationResponseCallback {
        @Override
        public HttpResponse handle(HttpRequest request) {
            return response().withHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(request.getBodyAsString().length())).withBody(request.getBodyAsString());
        }
    }
}

