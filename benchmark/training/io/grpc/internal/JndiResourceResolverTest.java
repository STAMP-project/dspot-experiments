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


import GrpcAttributes.ATTR_LB_ADDR_AUTHORITY;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.internal.DnsNameResolver.AddressResolver;
import io.grpc.internal.JndiResourceResolverFactory.JndiRecordFetcher;
import io.grpc.internal.JndiResourceResolverFactory.JndiResourceResolver;
import io.grpc.internal.JndiResourceResolverFactory.RecordFetcher;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link JndiResourceResolverFactory}.
 */
@RunWith(JUnit4.class)
public class JndiResourceResolverTest {
    @Test
    public void normalizeDataRemovesJndiFormattingForTxtRecords() {
        Assert.assertEquals("blah", JndiResourceResolver.unquote("blah"));
        Assert.assertEquals("", JndiResourceResolver.unquote("\"\""));
        Assert.assertEquals("blahblah", JndiResourceResolver.unquote("blah blah"));
        Assert.assertEquals("blahfoo blah", JndiResourceResolver.unquote("blah \"foo blah\""));
        Assert.assertEquals("blah blah", JndiResourceResolver.unquote("\"blah blah\""));
        Assert.assertEquals("blah\"blah", JndiResourceResolver.unquote("\"blah\\\"blah\""));
        Assert.assertEquals("blah\\blah", JndiResourceResolver.unquote("\"blah\\\\blah\""));
    }

    @Test
    public void jndiResolverWorks() throws Exception {
        Assume.assumeNoException(new JndiResourceResolverFactory().unavailabilityCause());
        RecordFetcher recordFetcher = new JndiRecordFetcher();
        try {
            recordFetcher.getAllRecords("SRV", "dns:///localhost");
        } catch (CommunicationException e) {
            Assume.assumeNoException(e);
        } catch (NameNotFoundException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void txtRecordLookup() throws Exception {
        RecordFetcher recordFetcher = Mockito.mock(RecordFetcher.class);
        Mockito.when(recordFetcher.getAllRecords("TXT", "dns:///service.example.com")).thenReturn(Arrays.asList("foo", "\"bar\""));
        List<String> golden = Arrays.asList("foo", "bar");
        JndiResourceResolver resolver = new JndiResourceResolver(recordFetcher);
        assertThat(resolver.resolveTxt("service.example.com")).isEqualTo(golden);
    }

    @Test
    public void srvRecordLookup() throws Exception {
        AddressResolver addressResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(addressResolver.resolveAddress("foo.example.com.")).thenReturn(Arrays.asList(InetAddress.getByName("127.1.2.3")));
        Mockito.when(addressResolver.resolveAddress("bar.example.com.")).thenReturn(Arrays.asList(InetAddress.getByName("127.3.2.1"), InetAddress.getByName("::1")));
        Mockito.when(addressResolver.resolveAddress("unknown.example.com.")).thenThrow(new UnknownHostException("unknown.example.com."));
        RecordFetcher recordFetcher = Mockito.mock(RecordFetcher.class);
        Mockito.when(recordFetcher.getAllRecords("SRV", "dns:///service.example.com")).thenReturn(Arrays.asList("0 0 314 foo.example.com.", "0 0 42 bar.example.com.", "0 0 1 unknown.example.com."));
        List<EquivalentAddressGroup> golden = Arrays.asList(new EquivalentAddressGroup(Arrays.<SocketAddress>asList(new InetSocketAddress("127.1.2.3", 314)), Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "foo.example.com").build()), new EquivalentAddressGroup(Arrays.<SocketAddress>asList(new InetSocketAddress("127.3.2.1", 42), new InetSocketAddress("::1", 42)), Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "bar.example.com").build()));
        JndiResourceResolver resolver = new JndiResourceResolver(recordFetcher);
        assertThat(resolver.resolveSrv(addressResolver, "service.example.com")).isEqualTo(golden);
    }
}

