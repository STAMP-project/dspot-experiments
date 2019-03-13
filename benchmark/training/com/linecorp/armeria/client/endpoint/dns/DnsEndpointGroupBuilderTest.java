/**
 * Copyright 2018 LINE Corporation
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
package com.linecorp.armeria.client.endpoint.dns;


import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import java.net.InetSocketAddress;
import org.junit.Test;


public class DnsEndpointGroupBuilderTest {
    @Test
    public void hostname() {
        assertThat(new DnsEndpointGroupBuilderTest.Builder("my-host.com").hostname()).isEqualTo("my-host.com");
        assertThat(new DnsEndpointGroupBuilderTest.Builder("MY-HOST.COM").hostname()).isEqualTo("my-host.com");
        // IDN
        assertThat(new DnsEndpointGroupBuilderTest.Builder("?????").hostname()).isEqualTo("xn--2w2b2dxu436ada");
    }

    @Test
    public void eventLoop() {
        assertThat(DnsEndpointGroupBuilderTest.builder().eventLoop()).isNotNull();
        final EventLoop loop = new NioEventLoopGroup().next();
        assertThat(DnsEndpointGroupBuilderTest.builder().eventLoop(loop).eventLoop()).isSameAs(loop);
        assertThatThrownBy(() -> builder().eventLoop(new io.netty.channel.DefaultEventLoop())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("unsupported");
    }

    @Test
    public void ttl() {
        assertThat(minTtl()).isOne();
        assertThat(maxTtl()).isEqualTo(Integer.MAX_VALUE);
        final DnsEndpointGroupBuilderTest.Builder builderWithCustomTtl = DnsEndpointGroupBuilderTest.builder().ttl(10, 20);
        assertThat(minTtl()).isEqualTo(10);
        assertThat(maxTtl()).isEqualTo(20);
        assertThatThrownBy(() -> builder().ttl(0, 10)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder().ttl(20, 10)).isInstanceOf(IllegalArgumentException.class);
        final DnsEndpointGroupBuilderTest.Builder builderWithSameCustomTtl = DnsEndpointGroupBuilderTest.builder().ttl(1, 1);
        assertThat(minTtl()).isOne();
        assertThat(maxTtl()).isOne();
    }

    @Test
    public void serverAddresses() {
        // Should be set by default.
        assertThat(serverAddressStreamProvider()).isNotNull();
        // Should use the sequential stream when set by a user.
        final DnsServerAddressStreamProvider provider = DnsEndpointGroupBuilderTest.builder().serverAddresses(new InetSocketAddress("1.1.1.1", 53), new InetSocketAddress("1.0.0.1", 53)).serverAddressStreamProvider();
        final DnsServerAddressStream stream = provider.nameServerAddressStream("foo.com");
        assertThat(stream.size()).isEqualTo(2);
        assertThat(stream.next()).isEqualTo(new InetSocketAddress("1.1.1.1", 53));
        assertThat(stream.next()).isEqualTo(new InetSocketAddress("1.0.0.1", 53));
        assertThat(stream.next()).isEqualTo(new InetSocketAddress("1.1.1.1", 53));
        assertThat(stream.next()).isEqualTo(new InetSocketAddress("1.0.0.1", 53));
    }

    private static final class Builder extends DnsEndpointGroupBuilder<DnsEndpointGroupBuilderTest.Builder> {
        Builder(String hostname) {
            super(hostname);
        }
    }
}

