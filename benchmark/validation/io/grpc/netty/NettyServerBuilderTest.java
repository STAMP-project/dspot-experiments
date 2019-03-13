/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.netty;


import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import io.grpc.ServerStreamTracer.Factory;
import io.netty.handler.ssl.SslContext;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link NettyServerBuilder}.
 */
@RunWith(JUnit4.class)
public class NettyServerBuilderTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private NettyServerBuilder builder = NettyServerBuilder.forPort(8080);

    @Test
    public void createMultipleServers() {
        builder.addListenAddress(new InetSocketAddress(8081));
        List<NettyServer> servers = builder.buildTransportServers(ImmutableList.<Factory>of());
        Truth.assertThat(servers).hasSize(2);
    }

    @Test
    public void sslContextCanBeNull() {
        builder.sslContext(null);
    }

    @Test
    public void failIfSslContextIsNotServer() {
        SslContext sslContext = Mockito.mock(SslContext.class);
        Mockito.when(sslContext.isClient()).thenReturn(true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Client SSL context can not be used for server");
        builder.sslContext(sslContext);
    }

    @Test
    public void failIfKeepAliveTimeNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("keepalive time must be positive");
        builder.keepAliveTime((-10L), TimeUnit.HOURS);
    }

    @Test
    public void failIfKeepAliveTimeoutNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("keepalive timeout must be positive");
        builder.keepAliveTimeout((-10L), TimeUnit.HOURS);
    }

    @Test
    public void failIfMaxConcurrentCallsPerConnectionNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("max must be positive");
        builder.maxConcurrentCallsPerConnection(0);
    }

    @Test
    public void failIfMaxInboundMetadataSizeNonPositive() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("maxInboundMetadataSize must be > 0");
        builder.maxInboundMetadataSize(0);
    }

    @Test
    public void failIfMaxConnectionIdleNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("max connection idle must be positive");
        builder.maxConnectionIdle((-1), TimeUnit.HOURS);
    }

    @Test
    public void failIfMaxConnectionAgeNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("max connection age must be positive");
        builder.maxConnectionAge((-1), TimeUnit.HOURS);
    }

    @Test
    public void failIfMaxConnectionAgeGraceNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("max connection age grace must be non-negative");
        builder.maxConnectionAgeGrace((-1), TimeUnit.HOURS);
    }

    @Test
    public void failIfPermitKeepAliveTimeNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("permit keepalive time must be non-negative");
        builder.permitKeepAliveTime((-1), TimeUnit.HOURS);
    }
}

