/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.remoting;


import java.io.File;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.TlsHelper;
import org.apache.rocketmq.remoting.netty.TlsSystemConfig;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TlsTest {
    private RemotingServer remotingServer;

    private RemotingClient remotingClient;

    @Rule
    public TestName name = new TestName();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Tests that a client and a server configured using two-way SSL auth can successfully
     * communicate with each other.
     */
    @Test
    public void basicClientServerIntegrationTest() throws Exception {
        requestThenAssertResponse();
    }

    @Test
    public void reloadSslContextForServer() throws Exception {
        requestThenAssertResponse();
        // Use new cert and private key
        TlsSystemConfig.tlsClientKeyPath = TlsTest.getCertsPath("badClient.key");
        TlsSystemConfig.tlsClientCertPath = TlsTest.getCertsPath("badClient.pem");
        loadSslContext();
        // Request Again
        requestThenAssertResponse();
        // Start another client
        NettyClientConfig clientConfig = new NettyClientConfig();
        clientConfig.setUseTLS(true);
        RemotingClient remotingClient = RemotingServerTest.createRemotingClient(clientConfig);
        requestThenAssertResponse(remotingClient);
    }

    @Test
    public void serverNotNeedClientAuth() throws Exception {
        requestThenAssertResponse();
    }

    @Test
    public void serverWantClientAuth_ButClientNoCert() throws Exception {
        requestThenAssertResponse();
    }

    @Test
    public void serverAcceptsUnAuthClient() throws Exception {
        requestThenAssertResponse();
    }

    @Test
    public void serverRejectsSSLClient() throws Exception {
        try {
            RemotingCommand response = remotingClient.invokeSync("localhost:8888", TlsTest.createRequest(), (1000 * 5));
            failBecauseExceptionWasNotThrown(RemotingSendRequestException.class);
        } catch (RemotingSendRequestException ignore) {
        }
    }

    /**
     * Tests that a server configured to require client authentication refuses to accept connections
     * from a client that has an untrusted certificate.
     */
    @Test
    public void serverRejectsUntrustedClientCert() throws Exception {
        try {
            RemotingCommand response = remotingClient.invokeSync("localhost:8888", TlsTest.createRequest(), (1000 * 5));
            failBecauseExceptionWasNotThrown(RemotingSendRequestException.class);
        } catch (RemotingSendRequestException ignore) {
        }
    }

    @Test
    public void serverAcceptsUntrustedClientCert() throws Exception {
        requestThenAssertResponse();
    }

    /**
     * Tests that a server configured to require client authentication actually does require client
     * authentication.
     */
    @Test
    public void noClientAuthFailure() throws Exception {
        try {
            RemotingCommand response = remotingClient.invokeSync("localhost:8888", TlsTest.createRequest(), (1000 * 3));
            failBecauseExceptionWasNotThrown(RemotingSendRequestException.class);
        } catch (RemotingSendRequestException ignore) {
        }
    }

    /**
     * Tests that a client configured using GrpcSslContexts refuses to talk to a server that has an
     * an untrusted certificate.
     */
    @Test
    public void clientRejectsUntrustedServerCert() throws Exception {
        try {
            RemotingCommand response = remotingClient.invokeSync("localhost:8888", TlsTest.createRequest(), (1000 * 3));
            failBecauseExceptionWasNotThrown(RemotingSendRequestException.class);
        } catch (RemotingSendRequestException ignore) {
        }
    }

    @Test
    public void clientAcceptsUntrustedServerCert() throws Exception {
        requestThenAssertResponse();
    }

    @Test
    public void testTlsConfigThroughFile() throws Exception {
        File file = tempFolder.newFile("tls.config");
        TlsSystemConfig.tlsTestModeEnable = true;
        TlsSystemConfig.tlsConfigFile = file.getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(((TlsSystemConfig.TLS_SERVER_NEED_CLIENT_AUTH) + "=require\n"));
        sb.append(((TlsSystemConfig.TLS_SERVER_KEYPATH) + "=/server.key\n"));
        sb.append(((TlsSystemConfig.TLS_SERVER_CERTPATH) + "=/server.pem\n"));
        sb.append(((TlsSystemConfig.TLS_SERVER_KEYPASSWORD) + "=2345\n"));
        sb.append(((TlsSystemConfig.TLS_SERVER_AUTHCLIENT) + "=true\n"));
        sb.append(((TlsSystemConfig.TLS_SERVER_TRUSTCERTPATH) + "=/ca.pem\n"));
        sb.append(((TlsSystemConfig.TLS_CLIENT_KEYPATH) + "=/client.key\n"));
        sb.append(((TlsSystemConfig.TLS_CLIENT_KEYPASSWORD) + "=1234\n"));
        sb.append(((TlsSystemConfig.TLS_CLIENT_CERTPATH) + "=/client.pem\n"));
        sb.append(((TlsSystemConfig.TLS_CLIENT_AUTHSERVER) + "=false\n"));
        sb.append(((TlsSystemConfig.TLS_CLIENT_TRUSTCERTPATH) + "=/ca.pem\n"));
        TlsTest.writeStringToFile(file.getAbsolutePath(), sb.toString());
        TlsHelper.buildSslContext(false);
        assertThat(TlsSystemConfig.tlsServerNeedClientAuth).isEqualTo("require");
        assertThat(TlsSystemConfig.tlsServerKeyPath).isEqualTo("/server.key");
        assertThat(TlsSystemConfig.tlsServerCertPath).isEqualTo("/server.pem");
        assertThat(TlsSystemConfig.tlsServerKeyPassword).isEqualTo("2345");
        assertThat(TlsSystemConfig.tlsServerAuthClient).isEqualTo(true);
        assertThat(TlsSystemConfig.tlsServerTrustCertPath).isEqualTo("/ca.pem");
        assertThat(TlsSystemConfig.tlsClientKeyPath).isEqualTo("/client.key");
        assertThat(TlsSystemConfig.tlsClientKeyPassword).isEqualTo("1234");
        assertThat(TlsSystemConfig.tlsClientCertPath).isEqualTo("/client.pem");
        assertThat(TlsSystemConfig.tlsClientAuthServer).isEqualTo(false);
        assertThat(TlsSystemConfig.tlsClientTrustCertPath).isEqualTo("/ca.pem");
        TlsSystemConfig.tlsConfigFile = "/notFound";
    }
}

