/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.it.client.retry;


import HelloService.Iface;
import TApplicationException.INTERNAL_ERROR;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.client.retry.Backoff;
import com.linecorp.armeria.client.retry.RetryStrategyWithContent;
import com.linecorp.armeria.client.retry.RetryingRpcClient;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.thrift.ThriftSerializationFormats;
import com.linecorp.armeria.common.util.EventLoopGroups;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.service.test.thrift.main.DevNullService;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.thrift.TApplicationException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RetryingRpcClientTest {
    private static final RetryStrategyWithContent<RpcResponse> retryAlways = ( ctx, response) -> CompletableFuture.completedFuture(Backoff.fixed(500));

    private static final RetryStrategyWithContent<RpcResponse> retryOnException = ( ctx, response) -> response.completionFuture().handle(( unused, cause) -> {
        if (cause != null) {
            return Backoff.withoutDelay();
        }
        return null;
    });

    private final Iface serviceHandler = Mockito.mock(Iface.class);

    private final DevNullService.Iface devNullServiceHandler = Mockito.mock(DevNullService.Iface.class);

    @Rule
    public final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            final AtomicInteger retryCount = new AtomicInteger();
            sb.service("/thrift", THttpService.of(serviceHandler).decorate(( delegate, ctx, req) -> {
                final int count = retryCount.getAndIncrement();
                if (count != 0) {
                    assertThat(count).isEqualTo(req.headers().getInt(ARMERIA_RETRY_COUNT));
                }
                return delegate.serve(ctx, req);
            }));
            sb.service("/thrift-devnull", THttpService.of(devNullServiceHandler));
        }
    };

    @Test
    public void execute() throws Exception {
        final HelloService.Iface client = helloClient(RetryingRpcClientTest.retryOnException, 100);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenReturn("world");
        assertThat(client.hello("hello")).isEqualTo("world");
        Mockito.verify(serviceHandler, Mockito.only()).hello("hello");
    }

    @Test
    public void execute_retry() throws Exception {
        final HelloService.Iface client = helloClient(RetryingRpcClientTest.retryOnException, 100);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenThrow(new IllegalArgumentException()).thenThrow(new IllegalArgumentException()).thenReturn("world");
        assertThat(client.hello("hello")).isEqualTo("world");
        Mockito.verify(serviceHandler, Mockito.times(3)).hello("hello");
    }

    @Test
    public void execute_reachedMaxAttempts() throws Exception {
        final HelloService.Iface client = helloClient(RetryingRpcClientTest.retryAlways, 2);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenThrow(new IllegalArgumentException());
        final Throwable thrown = catchThrowable(() -> client.hello("hello"));
        assertThat(thrown).isInstanceOf(TApplicationException.class);
        assertThat(getType()).isEqualTo(INTERNAL_ERROR);
        Mockito.verify(serviceHandler, Mockito.times(2)).hello("hello");
    }

    @Test
    public void propagateLastResponseWhenNextRetryIsAfterTimeout() throws Exception {
        final RetryStrategyWithContent<RpcResponse> strategy = ( ctx, response) -> CompletableFuture.completedFuture(Backoff.fixed(10000000));
        final HelloService.Iface client = helloClient(strategy, 100);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenThrow(new IllegalArgumentException());
        final Throwable thrown = catchThrowable(() -> client.hello("hello"));
        assertThat(thrown).isInstanceOf(TApplicationException.class);
        assertThat(getType()).isEqualTo(INTERNAL_ERROR);
        Mockito.verify(serviceHandler, Mockito.only()).hello("hello");
    }

    @Test
    public void execute_void() throws Exception {
        final DevNullService.Iface client = new com.linecorp.armeria.client.ClientBuilder(server.uri(ThriftSerializationFormats.BINARY, "/thrift-devnull")).rpcDecorator(RetryingRpcClient.newDecorator(RetryingRpcClientTest.retryOnException, 10)).build(DevNullService.Iface.class);
        Mockito.doThrow(new IllegalArgumentException()).doThrow(new IllegalArgumentException()).doNothing().when(devNullServiceHandler).consume(ArgumentMatchers.anyString());
        client.consume("hello");
        Mockito.verify(devNullServiceHandler, Mockito.times(3)).consume("hello");
    }

    @Test
    public void shouldGetExceptionWhenFactoryIsClosed() throws Exception {
        final ClientFactory factory = new ClientFactoryBuilder().workerGroup(EventLoopGroups.newEventLoopGroup(2), true).build();
        final RetryStrategyWithContent<RpcResponse> strategy = ( ctx, response) -> {
            // Retry after 8000 which is slightly less than responseTimeoutMillis(10000).
            return CompletableFuture.completedFuture(Backoff.fixed(8000));
        };
        final HelloService.Iface client = defaultResponseTimeoutMillis(10000).factory(factory).rpcDecorator(newDecorator()).build(Iface.class);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenThrow(new IllegalArgumentException());
        // There's no way to notice that the RetryingClient has scheduled the next retry.
        // The next retry will be after 8 seconds so closing the factory after 3 seconds should work.
        Executors.newSingleThreadScheduledExecutor().schedule(factory::close, 3, TimeUnit.SECONDS);
        assertThatThrownBy(() -> client.hello("hello")).isInstanceOf(IllegalStateException.class).satisfies(( cause) -> assertThat(cause.getMessage()).matches("(?i).*(factory has been closed|not accepting a task).*"));
    }

    @Test
    public void doNotRetryWhenResponseIsCancelled() throws Exception {
        final HelloService.Iface client = new com.linecorp.armeria.client.ClientBuilder(server.uri(ThriftSerializationFormats.BINARY, "/thrift")).rpcDecorator(newDecorator()).rpcDecorator(( delegate, ctx, req) -> {
            final RpcResponse res = delegate.execute(ctx, req);
            res.cancel(true);
            return res;
        }).build(Iface.class);
        Mockito.when(serviceHandler.hello(ArgumentMatchers.anyString())).thenThrow(new IllegalArgumentException());
        assertThatThrownBy(() -> client.hello("hello")).isInstanceOf(CancellationException.class);
        await().untilAsserted(() -> verify(serviceHandler, only()).hello("hello"));
        // Sleep 1 second more to check if there was another retry.
        TimeUnit.SECONDS.sleep(1);
        Mockito.verify(serviceHandler, Mockito.only()).hello("hello");
    }
}

