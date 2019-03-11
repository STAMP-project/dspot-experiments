/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.server;


import org.ehcache.clustered.common.Consistency;
import org.ehcache.clustered.common.PoolAllocation;
import org.ehcache.clustered.common.PoolAllocation.Dedicated;
import org.ehcache.clustered.common.PoolAllocation.Shared;
import org.ehcache.clustered.common.PoolAllocation.Unknown;
import org.ehcache.clustered.common.internal.ServerStoreConfiguration;
import org.ehcache.clustered.common.internal.exceptions.InvalidServerStoreConfigurationException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author GGIB
 */
public class ServerStoreCompatibilityTest {
    private static final String ERROR_MESSAGE_BASE = "Existing ServerStore configuration is not compatible with the desired configuration: " + "\n\t";

    private static final PoolAllocation DEDICATED_POOL_ALLOCATION = new Dedicated("primary", 4);

    private static final PoolAllocation SHARED_POOL_ALLOCATION = new Shared("sharedPool");

    private static final PoolAllocation UNKNOWN_POOL_ALLOCATION = new Unknown();

    private static final String STORED_KEY_TYPE = Long.class.getName();

    private static final String STORED_VALUE_TYPE = String.class.getName();

    private static final String KEY_SERIALIZER_TYPE = Long.class.getName();

    private static final String VALUE_SERIALIZER_TYPE = String.class.getName();

    @Test
    public void testStoredKeyTypeMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, String.class.getName(), ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "storedKeyType existing: ") + (serverConfiguration.getStoredKeyType())) + ", desired: ") + (clientConfiguration.getStoredKeyType()))), Matchers.is(true));
        }
    }

    @Test
    public void testStoredValueTypeMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, Long.class.getName(), ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "storedValueType existing: ") + (serverConfiguration.getStoredValueType())) + ", desired: ") + (clientConfiguration.getStoredValueType()))), Matchers.is(true));
        }
    }

    @Test
    public void testKeySerializerTypeMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, Double.class.getName(), ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "keySerializerType existing: ") + (serverConfiguration.getKeySerializerType())) + ", desired: ") + (clientConfiguration.getKeySerializerType()))), Matchers.is(true));
        }
    }

    @Test
    public void testValueSerializerTypeMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, Double.class.getName(), Consistency.EVENTUAL, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "valueSerializerType existing: ") + (serverConfiguration.getValueSerializerType())) + ", desired: ") + (clientConfiguration.getValueSerializerType()))), Matchers.is(true));
        }
    }

    @Test
    public void testConsitencyMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "consistencyType existing: ") + (serverConfiguration.getConsistency())) + ", desired: ") + (clientConfiguration.getConsistency()))), Matchers.is(true));
        }
    }

    @Test
    public void testDedicatedPoolResourceTooBig() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(new Dedicated("primary", 8), ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }

    @Test
    public void testDedicatedPoolResourceTooSmall() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(new Dedicated("primary", 2), ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }

    @Test
    public void testDedicatedPoolResourceNameMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(new Dedicated("primaryBad", 4), ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }

    @Test
    public void testSharedPoolResourceNameMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.SHARED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(new Shared("sharedPoolBad"), ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }

    @Test
    public void testAllResourceParametersMatch() throws Exception {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.EVENTUAL, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
    }

    @Test
    public void testPoolResourceTypeMismatch() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.SHARED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }

    @Test
    public void testClientStoreConfigurationUnknownPoolResource() throws InvalidServerStoreConfigurationException {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.UNKNOWN_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
    }

    @Test
    public void testServerStoreConfigurationUnknownPoolResourceInvalidKeyType() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.UNKNOWN_POOL_ALLOCATION, String.class.getName(), ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat("test failed", e.getMessage().equals((((((ServerStoreCompatibilityTest.ERROR_MESSAGE_BASE) + "storedKeyType existing: ") + (serverConfiguration.getStoredKeyType())) + ", desired: ") + (clientConfiguration.getStoredKeyType()))), Matchers.is(true));
        }
    }

    @Test
    public void testServerStoreConfigurationExtendedPoolAllocationType() {
        ServerStoreConfiguration serverConfiguration = new ServerStoreConfiguration(ServerStoreCompatibilityTest.DEDICATED_POOL_ALLOCATION, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        PoolAllocation extendedPoolAllocation = new PoolAllocation.DedicatedPoolAllocation() {
            private static final long serialVersionUID = 1L;

            @Override
            public long getSize() {
                return 4;
            }

            @Override
            public String getResourceName() {
                return "primary";
            }

            @Override
            public boolean isCompatible(final PoolAllocation other) {
                return true;
            }
        };
        ServerStoreConfiguration clientConfiguration = new ServerStoreConfiguration(extendedPoolAllocation, ServerStoreCompatibilityTest.STORED_KEY_TYPE, ServerStoreCompatibilityTest.STORED_VALUE_TYPE, ServerStoreCompatibilityTest.KEY_SERIALIZER_TYPE, ServerStoreCompatibilityTest.VALUE_SERIALIZER_TYPE, Consistency.STRONG, false);
        ServerStoreCompatibility serverStoreCompatibility = new ServerStoreCompatibility();
        try {
            serverStoreCompatibility.verify(serverConfiguration, clientConfiguration);
            Assert.fail("Expected InvalidServerStoreConfigurationException");
        } catch (InvalidServerStoreConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("resourcePoolType"));
        }
    }
}

