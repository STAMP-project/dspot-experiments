/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.common;


import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509ExtendedTrustManager;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Test;
import org.mockito.Mockito;


// We can only test calls to ZKTrustManager using Sockets (not SSLEngines). This can be fine since the logic is the same.
public class ZKTrustManagerTest extends ZKTestCase {
    private static KeyPair keyPair;

    private X509ExtendedTrustManager mockX509ExtendedTrustManager;

    private static final String IP_ADDRESS = "127.0.0.1";

    private static final String HOSTNAME = "localhost";

    private InetAddress mockInetAddress;

    private Socket mockSocket;

    @Test
    public void testServerHostnameVerificationWithHostnameVerificationDisabled() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, false, false);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(ZKTrustManagerTest.IP_ADDRESS, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkServerTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkServerTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testServerHostnameVerificationWithHostnameVerificationDisabledAndClientHostnameVerificationEnabled() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, false, true);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(ZKTrustManagerTest.IP_ADDRESS, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkServerTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkServerTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testServerHostnameVerificationWithIPAddress() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, true, false);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(ZKTrustManagerTest.IP_ADDRESS, null);
        zkTrustManager.checkServerTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkServerTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testServerHostnameVerificationWithHostname() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, true, false);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(null, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkServerTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkServerTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testClientHostnameVerificationWithHostnameVerificationDisabled() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, false, true);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(null, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkClientTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkClientTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testClientHostnameVerificationWithClientHostnameVerificationDisabled() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, true, false);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(null, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkClientTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkClientTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testClientHostnameVerificationWithIPAddress() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, true, true);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(ZKTrustManagerTest.IP_ADDRESS, null);
        zkTrustManager.checkClientTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(0)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkClientTrusted(certificateChain, null, mockSocket);
    }

    @Test
    public void testClientHostnameVerificationWithHostname() throws Exception {
        ZKTrustManager zkTrustManager = new ZKTrustManager(mockX509ExtendedTrustManager, true, true);
        X509Certificate[] certificateChain = createSelfSignedCertifcateChain(null, ZKTrustManagerTest.HOSTNAME);
        zkTrustManager.checkClientTrusted(certificateChain, null, mockSocket);
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostAddress();
        Mockito.verify(mockInetAddress, Mockito.times(1)).getHostName();
        Mockito.verify(mockX509ExtendedTrustManager, Mockito.times(1)).checkClientTrusted(certificateChain, null, mockSocket);
    }
}

