/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.internal;


import Attributes.EMPTY;
import Attributes.Key;
import com.google.common.testing.EqualsTester;
import io.grpc.Attributes;
import io.grpc.HttpConnectProxiedSocketAddress;
import io.grpc.internal.ClientTransportFactory.ClientTransportOptions;
import java.net.InetSocketAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ClientTransportFactoryTest {
    private String authority = "testing123";

    private Attributes eagAttributes = Attributes.newBuilder().set(Key.create("fake key"), "fake value").build();

    private String userAgent = "best-ua/3.14";

    private HttpConnectProxiedSocketAddress proxySocketAddress = HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(InetSocketAddress.createUnresolved("localhost", 100)).setProxyAddress(new InetSocketAddress(0)).build();

    @Test
    public void clientTransportOptions_init_checkNotNulls() {
        ClientTransportOptions cto = new ClientTransportOptions();
        assertThat(cto.getAuthority()).isNotNull();
        assertThat(cto.getEagAttributes()).isEqualTo(EMPTY);
    }

    @Test
    public void clientTransportOptions_getsMatchSets() {
        ClientTransportOptions cto = new ClientTransportOptions().setAuthority(authority).setEagAttributes(eagAttributes).setUserAgent(userAgent).setHttpConnectProxiedSocketAddress(proxySocketAddress);
        assertThat(cto.getAuthority()).isEqualTo(authority);
        assertThat(cto.getEagAttributes()).isEqualTo(eagAttributes);
        assertThat(cto.getUserAgent()).isEqualTo(userAgent);
        assertThat(cto.getHttpConnectProxiedSocketAddress()).isSameAs(proxySocketAddress);
    }

    @Test
    public void clientTransportOptions_equals() {
        new EqualsTester().addEqualityGroup(new ClientTransportOptions()).addEqualityGroup(new ClientTransportOptions().setAuthority(authority), new ClientTransportOptions().setAuthority(authority).setEagAttributes(EMPTY)).addEqualityGroup(new ClientTransportOptions().setAuthority(authority).setEagAttributes(eagAttributes)).addEqualityGroup(new ClientTransportOptions().setAuthority(authority).setEagAttributes(eagAttributes).setUserAgent(userAgent)).addEqualityGroup(new ClientTransportOptions().setAuthority(authority).setEagAttributes(eagAttributes).setUserAgent(userAgent).setHttpConnectProxiedSocketAddress(proxySocketAddress), new ClientTransportOptions().setAuthority(authority).setEagAttributes(eagAttributes).setUserAgent(userAgent).setHttpConnectProxiedSocketAddress(proxySocketAddress)).testEquals();
    }
}

