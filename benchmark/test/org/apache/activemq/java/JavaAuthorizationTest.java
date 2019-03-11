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


import java.util.concurrent.TimeUnit;
import org.apache.activemq.AbstractAuthorizationTest;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.security.DefaultAuthorizationMap;
import org.junit.Assert;
import org.junit.Test;


public class JavaAuthorizationTest extends AbstractAuthorizationTest {
    public static final int SLEEP = 2;// seconds


    String configurationSeed = "authorizationTest";

    private JavaRuntimeConfigurationBroker javaConfigBroker;

    @Test
    public void testMod() throws Exception {
        DefaultAuthorizationMap authorizationMap = buildUsersMap();
        BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        javaConfigBroker.updateAuthorizationMap(authorizationMap);
        assertAllowed("user", "USERS.A");
        assertDenied("user", "GUESTS.A");
        assertDeniedTemp("guest");
        // applyNewConfig(brokerConfig, configurationSeed + "-users-guests", SLEEP);
        authorizationMap = buildUsersGuestsMap();
        javaConfigBroker.updateAuthorizationMap(authorizationMap);
        TimeUnit.SECONDS.sleep(JavaAuthorizationTest.SLEEP);
        assertAllowed("user", "USERS.A");
        assertAllowed("guest", "GUESTS.A");
        assertDenied("user", "GUESTS.A");
        assertAllowedTemp("guest");
    }

    @Test
    public void testModRm() throws Exception {
        DefaultAuthorizationMap authorizationMap = buildUsersGuestsMap();
        BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        javaConfigBroker.updateAuthorizationMap(authorizationMap);
        TimeUnit.SECONDS.sleep(JavaAuthorizationTest.SLEEP);
        assertAllowed("user", "USERS.A");
        assertAllowed("guest", "GUESTS.A");
        assertDenied("user", "GUESTS.A");
        assertAllowedTemp("guest");
        authorizationMap = buildUsersMap();
        javaConfigBroker.updateAuthorizationMap(authorizationMap);
        TimeUnit.SECONDS.sleep(JavaAuthorizationTest.SLEEP);
        assertAllowed("user", "USERS.A");
        assertDenied("user", "GUESTS.A");
        assertDeniedTemp("guest");
    }

    @Test
    public void testWildcard() throws Exception {
        DefaultAuthorizationMap authorizationMap = buildWildcardUsersGuestsMap();
        BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        javaConfigBroker.updateAuthorizationMap(authorizationMap);
        TimeUnit.SECONDS.sleep(JavaAuthorizationTest.SLEEP);
        final String ALL_USERS = "ALL.USERS.>";
        final String ALL_GUESTS = "ALL.GUESTS.>";
        assertAllowed("user", ALL_USERS);
        assertAllowed("guest", ALL_GUESTS);
        assertDenied("user", ((ALL_USERS + ",") + ALL_GUESTS));
        assertDenied("guest", ((ALL_GUESTS + ",") + ALL_USERS));
        final String ALL_PREFIX = "ALL.>";
        assertDenied("user", ALL_PREFIX);
        assertDenied("guest", ALL_PREFIX);
        assertAllowed("user", "ALL.USERS.A");
        assertAllowed("user", "ALL.USERS.A,ALL.USERS.B");
        assertAllowed("guest", "ALL.GUESTS.A");
        assertAllowed("guest", "ALL.GUESTS.A,ALL.GUESTS.B");
        assertDenied("user", "USERS.>");
        assertDenied("guest", "GUESTS.>");
        assertAllowedTemp("guest");
    }
}

