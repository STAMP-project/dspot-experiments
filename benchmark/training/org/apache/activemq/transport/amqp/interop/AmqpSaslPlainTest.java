/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp.interop;


import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.junit.Test;


/**
 * Test broker behaviour when creating AMQP connections with SASL PLAIN mechanism.
 */
public class AmqpSaslPlainTest extends AmqpClientTestSupport {
    private static final String ADMIN = "admin";

    private static final String USER = "user";

    private static final String USER_PASSWORD = "password";

    @Test(timeout = 30000)
    public void testSaslPlainWithValidUsernameAndPassword() throws Exception {
        AmqpClient client = createAmqpClient(AmqpSaslPlainTest.USER, AmqpSaslPlainTest.USER_PASSWORD);
        doSucessfullConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithValidUsernameAndPasswordAndAuthzidAsUser() throws Exception {
        AmqpClient client = createAmqpClient(AmqpSaslPlainTest.USER, AmqpSaslPlainTest.USER_PASSWORD);
        client.setAuthzid(AmqpSaslPlainTest.USER);
        doSucessfullConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithValidUsernameAndPasswordAndAuthzidAsUnkown() throws Exception {
        AmqpClient client = createAmqpClient(AmqpSaslPlainTest.USER, AmqpSaslPlainTest.USER_PASSWORD);
        client.setAuthzid("unknown");
        doSucessfullConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithInvalidUsername() throws Exception {
        AmqpClient client = createAmqpClient("not-user", AmqpSaslPlainTest.USER_PASSWORD);
        doFailedConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithInvalidPassword() throws Exception {
        AmqpClient client = createAmqpClient(AmqpSaslPlainTest.USER, "not-user-password");
        doFailedConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithInvalidUsernameAndAuthzid() throws Exception {
        AmqpClient client = createAmqpClient("not-user", AmqpSaslPlainTest.USER_PASSWORD);
        client.setAuthzid(AmqpSaslPlainTest.USER);
        doFailedConnectionTestImpl(client);
    }

    @Test(timeout = 30000)
    public void testSaslPlainWithInvalidPasswordAndAuthzid() throws Exception {
        AmqpClient client = createAmqpClient(AmqpSaslPlainTest.USER, "not-user-password");
        client.setAuthzid(AmqpSaslPlainTest.USER);
        doFailedConnectionTestImpl(client);
    }
}

