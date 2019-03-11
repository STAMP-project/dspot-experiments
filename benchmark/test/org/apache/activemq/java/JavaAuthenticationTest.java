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
package org.apache.activemq.java;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.RuntimeConfigTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.junit.Assert;
import org.junit.Test;


public class JavaAuthenticationTest extends RuntimeConfigTestSupport {
    public static final int SLEEP = 2;// seconds


    private JavaRuntimeConfigurationBroker javaConfigBroker;

    private SimpleAuthenticationPlugin authenticationPlugin;

    @Test
    public void testMod() throws Exception {
        BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        assertAllowed("test_user_password", "USERS.A");
        assertDenied("another_test_user_password", "USERS.A");
        // anonymous
        assertDenied(null, "USERS.A");
        List<AuthenticationUser> users = new ArrayList<>();
        users.add(new AuthenticationUser("test_user_password", "test_user_password", "users"));
        users.add(new AuthenticationUser("another_test_user_password", "another_test_user_password", "users"));
        authenticationPlugin.setAnonymousGroup("users");
        authenticationPlugin.setUsers(users);
        authenticationPlugin.setAnonymousAccessAllowed(true);
        javaConfigBroker.updateSimpleAuthenticationPlugin(authenticationPlugin);
        TimeUnit.SECONDS.sleep(JavaAuthenticationTest.SLEEP);
        assertAllowed("test_user_password", "USERS.A");
        assertAllowed("another_test_user_password", "USERS.A");
        assertAllowed(null, "USERS.A");
    }
}

