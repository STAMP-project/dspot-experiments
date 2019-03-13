/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.marshalling.spi.net;


import DefaultExternalizer.INET4_ADDRESS;
import DefaultExternalizer.INET6_ADDRESS;
import DefaultExternalizer.INET_ADDRESS;
import DefaultExternalizer.INET_SOCKET_ADDRESS;
import DefaultExternalizer.URI;
import DefaultExternalizer.URL;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.Test;

import static java.net.URI.create;


/**
 * Unit test for {@link URIExternalizer}.
 *
 * @author Paul Ferraro
 */
public class NetExternalizerTestCase {
    @Test
    public void test() throws IOException, ClassNotFoundException {
        new org.wildfly.clustering.marshalling.ExternalizerTester(URI.cast(java.net.URI.class)).test(create("http://wildfly.org/news/"));
        new org.wildfly.clustering.marshalling.ExternalizerTester(URL.cast(java.net.URL.class)).test(new java.net.URL("http://wildfly.org/news/"));
        new org.wildfly.clustering.marshalling.ExternalizerTester(INET_ADDRESS.cast(InetAddress.class)).test(InetAddress.getLoopbackAddress());
        new org.wildfly.clustering.marshalling.ExternalizerTester(INET4_ADDRESS.cast(InetAddress.class)).test(InetAddress.getByName("127.0.0.1"));
        new org.wildfly.clustering.marshalling.ExternalizerTester(INET6_ADDRESS.cast(InetAddress.class)).test(InetAddress.getByName("::1"));
        new org.wildfly.clustering.marshalling.ExternalizerTester(INET_SOCKET_ADDRESS.cast(InetSocketAddress.class)).test(InetSocketAddress.createUnresolved("hostname", 0));
        new org.wildfly.clustering.marshalling.ExternalizerTester(INET_SOCKET_ADDRESS.cast(InetSocketAddress.class)).test(new InetSocketAddress(InetAddress.getLoopbackAddress(), Short.MAX_VALUE));
    }
}

