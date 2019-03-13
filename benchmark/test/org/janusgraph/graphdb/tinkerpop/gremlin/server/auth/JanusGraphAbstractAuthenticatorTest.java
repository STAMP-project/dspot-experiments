/**
 * Copyright 2019 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.tinkerpop.gremlin.server.auth;


import Cardinality.SINGLE;
import CredentialGraphTokens.PROPERTY_USERNAME;
import JanusGraphSimpleAuthenticator.USERNAME_INDEX_NAME;
import SchemaStatus.ENABLED;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.easymock.EasyMockSupport;
import org.janusgraph.StorageSetup;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public abstract class JanusGraphAbstractAuthenticatorTest extends EasyMockSupport {
    @Test
    public void testSetupNullConfig() {
        final JanusGraphAbstractAuthenticator authenticator = createAuthenticator();
        Assertions.assertThrows(IllegalArgumentException.class, () -> authenticator.setup(null));
    }

    @Test
    public void testSetupNoCredDb() {
        final JanusGraphAbstractAuthenticator authenticator = createAuthenticator();
        Assertions.assertThrows(IllegalStateException.class, () -> authenticator.setup(new HashMap()));
    }

    @Test
    public void testSetupNoUserDefault() {
        final JanusGraphAbstractAuthenticator authenticator = createAuthenticator();
        final Map<String, Object> configMap = configBuilder().defaultPassword("pass").create();
        Assertions.assertThrows(IllegalStateException.class, () -> authenticator.setup(configMap));
    }

    @Test
    public void testSetupNoPassDefault() {
        final JanusGraphAbstractAuthenticator authenticator = createAuthenticator();
        final Map<String, Object> configMap = configBuilder().defaultUser("user").create();
        Assertions.assertThrows(IllegalStateException.class, () -> authenticator.setup(configMap));
    }

    @Test
    public void testSetupEmptyCredentialsGraphCreateDefaultUser() {
        final String defaultUser = "user";
        final String defaultPassword = "pass";
        final Map<String, Object> configMap = configBuilder().defaultUser(defaultUser).defaultPassword(defaultPassword).create();
        final JanusGraph graph = StorageSetup.getInMemoryGraph();
        final JanusGraphAbstractAuthenticator authenticator = createInitializedAuthenticator(configMap, graph);
        authenticator.setup(configMap);
        CredentialTraversalSource credentialSource = graph.traversal(CredentialTraversalSource.class);
        List<Vertex> users = credentialSource.users(defaultUser).toList();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    public void testSetupEmptyCredentialsGraphCreateUsernamePropertyKey() {
        final Map<String, Object> configMap = createConfig();
        final JanusGraph graph = StorageSetup.getInMemoryGraph();
        final JanusGraphAbstractAuthenticator authenticator = createInitializedAuthenticator(configMap, graph);
        authenticator.setup(configMap);
        final ManagementSystem mgmt = ((ManagementSystem) (graph.openManagement()));
        final PropertyKey username = mgmt.getPropertyKey(PROPERTY_USERNAME);
        Assertions.assertEquals(String.class, username.dataType());
        Assertions.assertEquals(SINGLE, username.cardinality());
    }

    @Test
    public void testSetupEmptyCredentialsGraphCreateUsernameIndex() {
        final Map<String, Object> configMap = createConfig();
        final JanusGraph graph = StorageSetup.getInMemoryGraph();
        final JanusGraphAbstractAuthenticator authenticator = createInitializedAuthenticator(configMap, graph);
        authenticator.setup(configMap);
        final ManagementSystem mgmt = ((ManagementSystem) (graph.openManagement()));
        Assertions.assertTrue(mgmt.containsGraphIndex(USERNAME_INDEX_NAME));
        final JanusGraphIndex index = mgmt.getGraphIndex(USERNAME_INDEX_NAME);
        final PropertyKey username = mgmt.getPropertyKey(PROPERTY_USERNAME);
        Assertions.assertTrue(index.getIndexStatus(username).equals(ENABLED));
    }

    @Test
    public void testSetupCredentialsGraphWithDefaultUserDontCreateUserAgain() {
        final String defaultUser = "user";
        final String defaultPassword = "pass";
        final Map<String, Object> configMap = configBuilder().defaultUser(defaultUser).defaultPassword(defaultPassword).create();
        final JanusGraph graph = StorageSetup.getInMemoryGraph();
        JanusGraphAbstractAuthenticator authenticator = createInitializedAuthenticator(configMap, graph);
        authenticator.setup(configMap);
        authenticator = createInitializedAuthenticator(configMap, graph);
        // set up again:
        authenticator.setup(configMap);
        CredentialTraversalSource credentialSource = graph.traversal(CredentialTraversalSource.class);
        List<Vertex> users = credentialSource.users(defaultUser).toList();
        Assertions.assertEquals(1, users.size());
    }
}

