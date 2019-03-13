/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.testing.integration;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for GRPC over HTTP2 using the Netty framework.
 */
@RunWith(JUnit4.class)
public class Http2NettyTest extends AbstractInteropTest {
    @Test
    public void remoteAddr() {
        InetSocketAddress isa = ((InetSocketAddress) (obtainRemoteClientAddr()));
        Assert.assertEquals(InetAddress.getLoopbackAddress(), isa.getAddress());
        // It should not be the same as the server
        Assert.assertNotEquals(((InetSocketAddress) (getListenAddress())).getPort(), isa.getPort());
    }

    @Test
    public void localAddr() throws Exception {
        InetSocketAddress isa = ((InetSocketAddress) (obtainLocalClientAddr()));
        Assert.assertEquals(InetAddress.getLoopbackAddress(), isa.getAddress());
        Assert.assertEquals(((InetSocketAddress) (getListenAddress())).getPort(), isa.getPort());
    }

    @Test
    public void tlsInfo() {
        assertX500SubjectDn("CN=testclient, O=Internet Widgits Pty Ltd, ST=Some-State, C=AU");
    }
}

