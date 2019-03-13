/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.security.authentication;


import TestDerbyConnector.DerbyConnectorRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.druid.metadata.MetadataStorageTablesConfig;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.security.basic.BasicAuthUtils;
import org.apache.druid.security.basic.BasicSecurityDBResourceException;
import org.apache.druid.security.basic.authentication.db.updater.CoordinatorBasicAuthenticatorMetadataStorageUpdater;
import org.apache.druid.security.basic.authentication.entity.BasicAuthenticatorCredentialUpdate;
import org.apache.druid.security.basic.authentication.entity.BasicAuthenticatorCredentials;
import org.apache.druid.security.basic.authentication.entity.BasicAuthenticatorUser;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest {
    private static final String AUTHENTICATOR_NAME = "test";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private TestDerbyConnector connector;

    private MetadataStorageTablesConfig tablesConfig;

    private CoordinatorBasicAuthenticatorMetadataStorageUpdater updater;

    private ObjectMapper objectMapper;

    @Test
    public void createUser() {
        updater.createUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
        Map<String, BasicAuthenticatorUser> expectedUserMap = ImmutableMap.of("druid", new BasicAuthenticatorUser("druid", null));
        Map<String, BasicAuthenticatorUser> actualUserMap = BasicAuthUtils.deserializeAuthenticatorUserMap(objectMapper, updater.getCurrentUserMapBytes(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME));
        Assert.assertEquals(expectedUserMap, actualUserMap);
        // create duplicate should fail
        expectedException.expect(BasicSecurityDBResourceException.class);
        expectedException.expectMessage("User [druid] already exists.");
        updater.createUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
    }

    @Test
    public void deleteUser() {
        updater.createUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
        updater.deleteUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
        Map<String, BasicAuthenticatorUser> expectedUserMap = ImmutableMap.of();
        Map<String, BasicAuthenticatorUser> actualUserMap = BasicAuthUtils.deserializeAuthenticatorUserMap(objectMapper, updater.getCurrentUserMapBytes(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME));
        Assert.assertEquals(expectedUserMap, actualUserMap);
        // delete non-existent user should fail
        expectedException.expect(BasicSecurityDBResourceException.class);
        expectedException.expectMessage("User [druid] does not exist.");
        updater.deleteUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
    }

    @Test
    public void setCredentials() {
        updater.createUser(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid");
        updater.setUserCredentials(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME, "druid", new BasicAuthenticatorCredentialUpdate("helloworld", null));
        Map<String, BasicAuthenticatorUser> userMap = BasicAuthUtils.deserializeAuthenticatorUserMap(objectMapper, updater.getCurrentUserMapBytes(CoordinatorBasicAuthenticatorMetadataStorageUpdaterTest.AUTHENTICATOR_NAME));
        BasicAuthenticatorCredentials credentials = userMap.get("druid").getCredentials();
        byte[] recalculatedHash = BasicAuthUtils.hashPassword("helloworld".toCharArray(), credentials.getSalt(), credentials.getIterations());
        Assert.assertArrayEquals(credentials.getHash(), recalculatedHash);
    }
}

