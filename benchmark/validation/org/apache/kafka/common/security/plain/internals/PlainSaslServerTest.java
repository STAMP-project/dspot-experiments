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
package org.apache.kafka.common.security.plain.internals;


import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.junit.Assert;
import org.junit.Test;


public class PlainSaslServerTest {
    private static final String USER_A = "userA";

    private static final String PASSWORD_A = "passwordA";

    private static final String USER_B = "userB";

    private static final String PASSWORD_B = "passwordB";

    private PlainSaslServer saslServer;

    @Test
    public void noAuthorizationIdSpecified() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(saslMessage("", PlainSaslServerTest.USER_A, PlainSaslServerTest.PASSWORD_A));
        Assert.assertEquals(0, nextChallenge.length);
    }

    @Test
    public void authorizatonIdEqualsAuthenticationId() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(saslMessage(PlainSaslServerTest.USER_A, PlainSaslServerTest.USER_A, PlainSaslServerTest.PASSWORD_A));
        Assert.assertEquals(0, nextChallenge.length);
    }

    @Test(expected = SaslAuthenticationException.class)
    public void authorizatonIdNotEqualsAuthenticationId() throws Exception {
        saslServer.evaluateResponse(saslMessage(PlainSaslServerTest.USER_B, PlainSaslServerTest.USER_A, PlainSaslServerTest.PASSWORD_A));
    }

    @Test
    public void emptyTokens() {
        Exception e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("", "", "")));
        Assert.assertEquals("Authentication failed: username not specified", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("", "", "p")));
        Assert.assertEquals("Authentication failed: username not specified", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("", "u", "")));
        Assert.assertEquals("Authentication failed: password not specified", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("a", "", "")));
        Assert.assertEquals("Authentication failed: username not specified", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("a", "", "p")));
        Assert.assertEquals("Authentication failed: username not specified", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(saslMessage("a", "u", "")));
        Assert.assertEquals("Authentication failed: password not specified", e.getMessage());
        String nul = "\u0000";
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(String.format("%s%s%s%s%s%s", "a", nul, "u", nul, "p", nul).getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals("Invalid SASL/PLAIN response: expected 3 tokens, got 4", e.getMessage());
        e = Assert.assertThrows(SaslAuthenticationException.class, () -> saslServer.evaluateResponse(String.format("%s%s%s", "", nul, "u").getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals("Invalid SASL/PLAIN response: expected 3 tokens, got 2", e.getMessage());
    }
}

