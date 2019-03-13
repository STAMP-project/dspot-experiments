/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.authenticator;


import java.util.Arrays;
import java.util.Map;
import org.apache.kafka.common.network.CertStores;
import org.apache.kafka.common.network.ChannelBuilder;
import org.apache.kafka.common.network.NetworkTestUtils;
import org.apache.kafka.common.network.NioEchoServer;
import org.apache.kafka.common.network.Selector;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SaslAuthenticatorFailureDelayTest {
    private static final int BUFFER_SIZE = 4 * 1024;

    private final MockTime time = new MockTime(10);

    private NioEchoServer server;

    private Selector selector;

    private ChannelBuilder channelBuilder;

    private CertStores serverCertStores;

    private CertStores clientCertStores;

    private Map<String, Object> saslClientConfigs;

    private Map<String, Object> saslServerConfigs;

    private CredentialCache credentialCache;

    private long startTimeMs;

    private final int failedAuthenticationDelayMs;

    public SaslAuthenticatorFailureDelayTest(int failedAuthenticationDelayMs) {
        this.failedAuthenticationDelayMs = failedAuthenticationDelayMs;
    }

    /**
     * Tests that SASL/PLAIN clients with invalid password fail authentication.
     */
    @Test
    public void testInvalidPasswordSaslPlain() throws Exception {
        String node = "0";
        SecurityProtocol securityProtocol = SecurityProtocol.SASL_SSL;
        TestJaasConfig jaasConfig = configureMechanisms("PLAIN", Arrays.asList("PLAIN"));
        jaasConfig.setClientOptions("PLAIN", TestJaasConfig.USERNAME, "invalidpassword");
        server = createEchoServer(securityProtocol);
        createAndCheckClientAuthenticationFailure(securityProtocol, node, "PLAIN", "Authentication failed: Invalid username or password");
        server.verifyAuthenticationMetrics(0, 1);
    }

    /**
     * Tests client connection close before response for authentication failure is sent.
     */
    @Test
    public void testClientConnectionClose() throws Exception {
        String node = "0";
        SecurityProtocol securityProtocol = SecurityProtocol.SASL_SSL;
        TestJaasConfig jaasConfig = configureMechanisms("PLAIN", Arrays.asList("PLAIN"));
        jaasConfig.setClientOptions("PLAIN", TestJaasConfig.USERNAME, "invalidpassword");
        server = createEchoServer(securityProtocol);
        createClientConnection(securityProtocol, node);
        Map<?, ?> delayedClosingChannels = NetworkTestUtils.delayedClosingChannels(server.selector());
        // Wait until server has established connection with client and has processed the auth failure
        TestUtils.waitForCondition(() -> {
            poll(selector);
            return !(server.selector().channels().isEmpty());
        }, "Timeout waiting for connection");
        TestUtils.waitForCondition(() -> {
            poll(selector);
            return ((failedAuthenticationDelayMs) == 0) || (!(delayedClosingChannels.isEmpty()));
        }, "Timeout waiting for auth failure");
        selector.close();
        selector = null;
        // Now that client connection is closed, wait until server notices the disconnection and removes it from the
        // list of connected channels and from delayed response for auth failure
        TestUtils.waitForCondition(() -> ((failedAuthenticationDelayMs) == 0) || (delayedClosingChannels.isEmpty()), "Timeout waiting for delayed response remove");
        TestUtils.waitForCondition(() -> server.selector().channels().isEmpty(), "Timeout waiting for connection close");
        // Try forcing completion of delayed channel close
        TestUtils.waitForCondition(() -> (time.milliseconds()) > (((startTimeMs) + (failedAuthenticationDelayMs)) + 1), "Timeout when waiting for auth failure response timeout to elapse");
        NetworkTestUtils.completeDelayedChannelClose(server.selector(), time.nanoseconds());
    }
}

