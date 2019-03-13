/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.client;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.http.conn.DnsResolver;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class EndpointsTest {
    private static final InetAddress IP_A = InetAddresses.forString("1.2.3.4");

    private static final InetAddress IP_B = InetAddresses.forString("2.3.4.5");

    private static final InetAddress IP_C = InetAddresses.forString("3.4.5.6");

    private static final InetAddress IP_D = InetAddresses.forString("4.5.6.7");

    private static final InetAddress[] IPS_1 = new InetAddress[]{ EndpointsTest.IP_A, EndpointsTest.IP_B };

    private static final InetAddress[] IPS_2 = new InetAddress[]{ EndpointsTest.IP_C, EndpointsTest.IP_D };

    private static URI uri1;

    private static URI uri2;

    private static List<URI> uris;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testSupplierFactory() throws Exception {
        final DnsResolver resolver = Mockito.mock(DnsResolver.class);
        Mockito.when(resolver.resolve("example.com")).thenReturn(EndpointsTest.IPS_1);
        Mockito.when(resolver.resolve("example.net")).thenReturn(EndpointsTest.IPS_2);
        final Supplier<List<URI>> uriSupplier = Suppliers.ofInstance(EndpointsTest.uris);
        final Supplier<List<Endpoint>> endpointSupplier = Endpoints.of(uriSupplier, resolver);
        final List<Endpoint> endpoints = endpointSupplier.get();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(endpoints.get(0).getUri(), CoreMatchers.equalTo(EndpointsTest.uri1));
        Assert.assertThat(endpoints.get(0).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_A));
        Assert.assertThat(endpoints.get(1).getUri(), CoreMatchers.equalTo(EndpointsTest.uri1));
        Assert.assertThat(endpoints.get(1).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_B));
        Assert.assertThat(endpoints.get(2).getUri(), CoreMatchers.equalTo(EndpointsTest.uri2));
        Assert.assertThat(endpoints.get(2).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_C));
        Assert.assertThat(endpoints.get(3).getUri(), CoreMatchers.equalTo(EndpointsTest.uri2));
        Assert.assertThat(endpoints.get(3).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_D));
    }

    @Test
    public void testFactory() throws Exception {
        final DnsResolver resolver = Mockito.mock(DnsResolver.class);
        Mockito.when(resolver.resolve("example.com")).thenReturn(EndpointsTest.IPS_1);
        Mockito.when(resolver.resolve("example.net")).thenReturn(EndpointsTest.IPS_2);
        final List<Endpoint> endpoints = Endpoints.of(EndpointsTest.uris, resolver);
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(endpoints.get(0).getUri(), CoreMatchers.equalTo(EndpointsTest.uri1));
        Assert.assertThat(endpoints.get(0).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_A));
        Assert.assertThat(endpoints.get(1).getUri(), CoreMatchers.equalTo(EndpointsTest.uri1));
        Assert.assertThat(endpoints.get(1).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_B));
        Assert.assertThat(endpoints.get(2).getUri(), CoreMatchers.equalTo(EndpointsTest.uri2));
        Assert.assertThat(endpoints.get(2).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_C));
        Assert.assertThat(endpoints.get(3).getUri(), CoreMatchers.equalTo(EndpointsTest.uri2));
        Assert.assertThat(endpoints.get(3).getIp(), CoreMatchers.equalTo(EndpointsTest.IP_D));
    }

    @Test
    public void testUnableToResolve() throws Exception {
        final DnsResolver resolver = Mockito.mock(DnsResolver.class);
        Mockito.when(resolver.resolve("example.com")).thenThrow(new UnknownHostException());
        Mockito.when(resolver.resolve("example.net")).thenThrow(new UnknownHostException());
        final List<Endpoint> endpoints = Endpoints.of(EndpointsTest.uris, resolver);
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void testInvalidUri_NoScheme() throws Exception {
        final DnsResolver resolver = Mockito.mock(DnsResolver.class);
        Mockito.when(resolver.resolve("example.com")).thenReturn(EndpointsTest.IPS_1);
        exception.expect(IllegalArgumentException.class);
        Endpoints.of(ImmutableList.of(new URI(null, "example.com", null, null)), resolver);
    }

    @Test
    public void testInvalidUri_NoPort() throws Exception {
        final DnsResolver resolver = Mockito.mock(DnsResolver.class);
        Mockito.when(resolver.resolve("example.com")).thenReturn(EndpointsTest.IPS_1);
        exception.expect(IllegalArgumentException.class);
        Endpoints.of(ImmutableList.of(new URI("http", "example.com", null, null)), resolver);
    }
}

