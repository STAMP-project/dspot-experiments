package org.mockserver.integration.mocking;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.integration.ClientAndServer;


public class ConcurrencyResponseWebSocketMockingIntegrationTest {
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
            ConcurrencyResponseWebSocketMockingIntegrationTest.this.sendRequestAndVerifyResponse();
        }
    }
}

