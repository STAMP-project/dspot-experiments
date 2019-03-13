/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ssl;


import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.SSLException;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SniClientTest {
    private final SslProvider serverProvider;

    private final SslProvider clientProvider;

    public SniClientTest(SslProvider serverProvider, SslProvider clientProvider) {
        this.serverProvider = serverProvider;
        this.clientProvider = clientProvider;
    }

    @Test(timeout = 30000)
    public void testSniClient() throws Exception {
        SniClientTest.testSniClient(serverProvider, clientProvider);
    }

    @Test(timeout = 30000)
    public void testSniSNIMatcherMatchesClient() throws Exception {
        Assume.assumeTrue(((PlatformDependent.javaVersion()) >= 8));
        SniClientJava8TestUtil.testSniClient(serverProvider, clientProvider, true);
    }

    @Test(timeout = 30000, expected = SSLException.class)
    public void testSniSNIMatcherDoesNotMatchClient() throws Exception {
        Assume.assumeTrue(((PlatformDependent.javaVersion()) >= 8));
        SniClientJava8TestUtil.testSniClient(serverProvider, clientProvider, false);
    }
}

