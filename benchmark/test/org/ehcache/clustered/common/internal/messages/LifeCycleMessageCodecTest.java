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
package org.ehcache.clustered.common.internal.messages;


import EhcacheMessageType.VALIDATE;
import EhcacheMessageType.VALIDATE_SERVER_STORE;
import LifecycleMessage.ValidateServerStore;
import LifecycleMessage.ValidateStoreManager;
import PoolAllocation.Dedicated;
import PoolAllocation.Shared;
import PoolAllocation.Unknown;
import java.nio.ByteBuffer;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.clustered.common.PoolAllocation;
import org.ehcache.clustered.common.ServerSideConfiguration;
import org.ehcache.clustered.common.internal.ServerStoreConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * LifeCycleMessageCodecTest
 */
public class LifeCycleMessageCodecTest {
    private static final long MESSAGE_ID = 42L;

    private final LifeCycleMessageFactory factory = new LifeCycleMessageFactory();

    private final LifeCycleMessageCodec codec = new LifeCycleMessageCodec(new CommonConfigCodec());

    @Test
    public void testValidateStoreManager() throws Exception {
        ServerSideConfiguration configuration = getServerSideConfiguration();
        LifecycleMessage message = factory.validateStoreManager(configuration);
        byte[] encoded = codec.encode(message);
        LifecycleMessage.ValidateStoreManager decodedMessage = ((LifecycleMessage.ValidateStoreManager) (codec.decode(message.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMessage.getMessageType(), Matchers.is(VALIDATE));
        Assert.assertThat(decodedMessage.getConfiguration().getDefaultServerResource(), Matchers.is(configuration.getDefaultServerResource()));
        Assert.assertThat(decodedMessage.getConfiguration().getResourcePools(), Matchers.is(configuration.getResourcePools()));
    }

    @Test
    public void testValidateServerStoreDedicated() throws Exception {
        PoolAllocation.Dedicated dedicated = new PoolAllocation.Dedicated("dedicate", 420000L);
        ServerStoreConfiguration configuration = new ServerStoreConfiguration(dedicated, "java.lang.Long", "java.lang.String", "org.ehcache.impl.serialization.LongSerializer", "org.ehcache.impl.serialization.StringSerializer", Consistency.STRONG, false);
        LifecycleMessage message = factory.validateServerStore("store1", configuration);
        byte[] encoded = codec.encode(message);
        LifecycleMessage.ValidateServerStore decodedMessage = ((LifecycleMessage.ValidateServerStore) (codec.decode(message.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMessage.getMessageType(), Matchers.is(VALIDATE_SERVER_STORE));
        validateCommonServerStoreConfig(decodedMessage, configuration);
        PoolAllocation.Dedicated decodedPoolAllocation = ((PoolAllocation.Dedicated) (decodedMessage.getStoreConfiguration().getPoolAllocation()));
        Assert.assertThat(decodedPoolAllocation.getResourceName(), Matchers.is(dedicated.getResourceName()));
        Assert.assertThat(decodedPoolAllocation.getSize(), Matchers.is(dedicated.getSize()));
        Assert.assertThat(decodedMessage.getStoreConfiguration().isLoaderWriterConfigured(), Matchers.is(false));
    }

    @Test
    public void testValidateServerStoreShared() throws Exception {
        PoolAllocation.Shared shared = new PoolAllocation.Shared("shared");
        ServerStoreConfiguration configuration = new ServerStoreConfiguration(shared, "java.lang.Long", "java.lang.String", "org.ehcache.impl.serialization.LongSerializer", "org.ehcache.impl.serialization.StringSerializer", Consistency.STRONG, false);
        LifecycleMessage message = factory.validateServerStore("store1", configuration);
        byte[] encoded = codec.encode(message);
        LifecycleMessage.ValidateServerStore decodedMessage = ((LifecycleMessage.ValidateServerStore) (codec.decode(message.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMessage.getMessageType(), Matchers.is(VALIDATE_SERVER_STORE));
        validateCommonServerStoreConfig(decodedMessage, configuration);
        PoolAllocation.Shared decodedPoolAllocation = ((PoolAllocation.Shared) (decodedMessage.getStoreConfiguration().getPoolAllocation()));
        Assert.assertThat(decodedPoolAllocation.getResourcePoolName(), Matchers.is(shared.getResourcePoolName()));
        Assert.assertThat(decodedMessage.getStoreConfiguration().isLoaderWriterConfigured(), Matchers.is(false));
    }

    @Test
    public void testValidateServerStoreUnknown() throws Exception {
        PoolAllocation.Unknown unknown = new PoolAllocation.Unknown();
        ServerStoreConfiguration configuration = new ServerStoreConfiguration(unknown, "java.lang.Long", "java.lang.String", "org.ehcache.impl.serialization.LongSerializer", "org.ehcache.impl.serialization.StringSerializer", Consistency.STRONG, false);
        LifecycleMessage message = factory.validateServerStore("store1", configuration);
        byte[] encoded = codec.encode(message);
        LifecycleMessage.ValidateServerStore decodedMessage = ((LifecycleMessage.ValidateServerStore) (codec.decode(message.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMessage.getMessageType(), Matchers.is(VALIDATE_SERVER_STORE));
        validateCommonServerStoreConfig(decodedMessage, configuration);
        Assert.assertThat(decodedMessage.getStoreConfiguration().getPoolAllocation(), Matchers.instanceOf(Unknown.class));
        Assert.assertThat(decodedMessage.getStoreConfiguration().isLoaderWriterConfigured(), Matchers.is(false));
    }
}

