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


import com.google.common.collect.Sets;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.http.conn.DnsResolver;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class EndpointIteratorTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final DnsResolver RESOLVER = Mockito.mock(DnsResolver.class);

    private static final InetAddress IP_A = InetAddresses.forString("1.2.3.4");

    private static final InetAddress IP_B = InetAddresses.forString("2.3.4.5");

    private static final InetAddress IP_C = InetAddresses.forString("3.4.5.6");

    private static final InetAddress IP_D = InetAddresses.forString("4.5.6.7");

    private static final InetAddress[] IPS_1 = new InetAddress[]{ EndpointIteratorTest.IP_A, EndpointIteratorTest.IP_B };

    private static final InetAddress[] IPS_2 = new InetAddress[]{ EndpointIteratorTest.IP_C, EndpointIteratorTest.IP_D };

    private static URI uri1;

    private static URI uri2;

    private static List<Endpoint> endpoints;

    @Test
    public void test() throws Exception {
        final Iterator<Endpoint> iterator = EndpointIterator.of(EndpointIteratorTest.endpoints);
        final Set<URI> uris = Sets.newHashSet();
        final Set<InetAddress> ips = Sets.newHashSet();
        // Iterate 10 times and check we only have 2 unique URIs and 4 unique IPs
        for (int i = 0; i < 10; i++) {
            final Endpoint e = iterator.next();
            uris.add(e.getUri());
            ips.add(e.getIp());
        }
        Assert.assertEquals(uris.size(), 2);
        Assert.assertEquals(ips.size(), 4);
        Assert.assertThat(uris, Matchers.containsInAnyOrder(EndpointIteratorTest.uri1, EndpointIteratorTest.uri2));
        Assert.assertThat(ips, Matchers.containsInAnyOrder(EndpointIteratorTest.IP_A, EndpointIteratorTest.IP_B, EndpointIteratorTest.IP_C, EndpointIteratorTest.IP_D));
    }

    @Test
    public void testEmptyIterator() throws Exception {
        final Iterator<Endpoint> iterator = EndpointIterator.of(Collections.<Endpoint>emptyList());
        Assert.assertFalse(iterator.hasNext());
        exception.expect(NoSuchElementException.class);
        iterator.next();
    }
}

