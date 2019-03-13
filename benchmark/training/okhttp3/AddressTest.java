/**
 * Copyright (C) 2014 Square, Inc.
 *
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
 */
package okhttp3;


import ConnectionSpec.MODERN_TLS;
import Protocol.HTTP_1_1;
import java.net.Proxy;
import java.util.List;
import javax.net.SocketFactory;
import okhttp3.internal.Util;
import okhttp3.internal.http.RecordingProxySelector;
import org.junit.Assert;
import org.junit.Test;

import static Authenticator.NONE;
import static Dns.SYSTEM;


public final class AddressTest {
    private Dns dns = SYSTEM;

    private SocketFactory socketFactory = SocketFactory.getDefault();

    private Authenticator authenticator = NONE;

    private List<Protocol> protocols = Util.immutableList(HTTP_1_1);

    private List<ConnectionSpec> connectionSpecs = Util.immutableList(MODERN_TLS);

    private RecordingProxySelector proxySelector = new RecordingProxySelector();

    @Test
    public void equalsAndHashcode() throws Exception {
        Address a = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, proxySelector);
        Address b = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, proxySelector);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void differentProxySelectorsAreDifferent() throws Exception {
        Address a = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, new RecordingProxySelector());
        Address b = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, new RecordingProxySelector());
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void addressToString() throws Exception {
        Address address = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, proxySelector);
        Assert.assertEquals("Address{square.com:80, proxySelector=RecordingProxySelector}", address.toString());
    }

    @Test
    public void addressWithProxyToString() throws Exception {
        Address address = new Address("square.com", 80, dns, socketFactory, null, null, null, authenticator, Proxy.NO_PROXY, protocols, connectionSpecs, proxySelector);
        Assert.assertEquals((("Address{square.com:80, proxy=" + (Proxy.NO_PROXY)) + "}"), address.toString());
    }
}

