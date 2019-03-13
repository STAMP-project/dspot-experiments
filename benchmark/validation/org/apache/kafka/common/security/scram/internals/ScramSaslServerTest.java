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
package org.apache.kafka.common.security.scram.internals;


import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.junit.Assert;
import org.junit.Test;


public class ScramSaslServerTest {
    private static final String USER_A = "userA";

    private static final String USER_B = "userB";

    private ScramMechanism mechanism;

    private ScramFormatter formatter;

    private ScramSaslServer saslServer;

    @Test
    public void noAuthorizationIdSpecified() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(clientFirstMessage(ScramSaslServerTest.USER_A, null));
        Assert.assertTrue("Next challenge is empty", ((nextChallenge.length) > 0));
    }

    @Test
    public void authorizatonIdEqualsAuthenticationId() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(clientFirstMessage(ScramSaslServerTest.USER_A, ScramSaslServerTest.USER_A));
        Assert.assertTrue("Next challenge is empty", ((nextChallenge.length) > 0));
    }

    @Test(expected = SaslAuthenticationException.class)
    public void authorizatonIdNotEqualsAuthenticationId() throws Exception {
        saslServer.evaluateResponse(clientFirstMessage(ScramSaslServerTest.USER_A, ScramSaslServerTest.USER_B));
    }
}

