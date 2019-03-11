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


import CommonConfigCodec.POOL_SIZE_FIELD;
import ConfigCodec.InjectTuple;
import Consistency.EVENTUAL;
import Consistency.STRONG;
import java.nio.ByteBuffer;
import java.util.Collections;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.clustered.common.PoolAllocation;
import org.ehcache.clustered.common.ServerSideConfiguration;
import org.ehcache.clustered.common.internal.ServerStoreConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.runnel.EnumMapping;
import org.terracotta.runnel.Struct;
import org.terracotta.runnel.StructBuilder;
import org.terracotta.runnel.encoding.StructEncoder;


public class CommonConfigCodecTest {
    private static final CommonConfigCodec CODEC = new CommonConfigCodec();

    @Test
    public void testEncodeDecodeServerSideConfiguration() throws Exception {
        ServerSideConfiguration serverSideConfiguration = new ServerSideConfiguration("foo", Collections.singletonMap("bar", new ServerSideConfiguration.Pool(1)));
        Struct serverSideConfigurationStruct = CommonConfigCodecTest.CODEC.injectServerSideConfiguration(newStructBuilder(), 10).getUpdatedBuilder().build();
        StructEncoder<Void> encoder = serverSideConfigurationStruct.encoder();
        CommonConfigCodecTest.CODEC.encodeServerSideConfiguration(encoder, serverSideConfiguration);
        ByteBuffer byteBuffer = encoder.encode();
        byteBuffer.rewind();
        ServerSideConfiguration decodedServerSideConfiguration = CommonConfigCodecTest.CODEC.decodeServerSideConfiguration(serverSideConfigurationStruct.decoder(byteBuffer));
        Assert.assertThat(decodedServerSideConfiguration.getDefaultServerResource(), Matchers.is("foo"));
        Assert.assertThat(decodedServerSideConfiguration.getResourcePools(), Matchers.hasKey("bar"));
    }

    @Test
    public void testInjectServerStoreConfiguration() {
        PoolAllocation poolAllocation = Mockito.mock(PoolAllocation.class);
        ServerStoreConfiguration serverStoreConfiguration = new ServerStoreConfiguration(poolAllocation, "Long.class", "String.class", null, null, Consistency.EVENTUAL, false, false);
        ConfigCodec.InjectTuple injectTuple = CommonConfigCodecTest.CODEC.injectServerStoreConfiguration(newStructBuilder(), 10);
        Assert.assertThat(injectTuple.getLastIndex(), Matchers.is(40));
        Struct struct = injectTuple.getUpdatedBuilder().build();
        StructEncoder<Void> encoder = struct.encoder();
        CommonConfigCodecTest.CODEC.encodeServerStoreConfiguration(encoder, serverStoreConfiguration);
        encoder.int64(POOL_SIZE_FIELD, 20);
    }

    @Test
    public void testDecodeNonLoaderWriterServerStoreConfiguration() {
        EnumMapping<Consistency> consistencyEnumMapping = newEnumMappingBuilder(Consistency.class).mapping(EVENTUAL, 1).mapping(STRONG, 2).build();
        int index = 30;
        StructBuilder builder = newStructBuilder().string("identifier", 10).string(MessageCodecUtils.SERVER_STORE_NAME_FIELD, 20).string("keyType", index).string("keySerializerType", (index + 10)).string("valueType", (index + 11)).string("valueSerializerType", (index + 15)).enm("consistency", (index + 16), consistencyEnumMapping).int64("poolSize", (index + 20)).string("resourceName", (index + 30));
        Struct struct = builder.build();
        ByteBuffer encodedStoreConfig = struct.encoder().string("identifier", "test").string(MessageCodecUtils.SERVER_STORE_NAME_FIELD, "testStore").string("keyType", "Long").string("keySerializerType", "Long").string("valueType", "Long").string("valueSerializerType", "Long").enm("consistency", STRONG).int64("poolSize", 20).string("resourceName", "primary").encode();
        Struct newStruct = CommonConfigCodecTest.CODEC.injectServerStoreConfiguration(newStructBuilder().string("identifier", 10).string(MessageCodecUtils.SERVER_STORE_NAME_FIELD, 20), index).getUpdatedBuilder().build();
        encodedStoreConfig.flip();
        ServerStoreConfiguration serverStoreConfiguration = CommonConfigCodecTest.CODEC.decodeServerStoreConfiguration(newStruct.decoder(encodedStoreConfig));
        Assert.assertThat(serverStoreConfiguration.isLoaderWriterConfigured(), Matchers.is(false));
        Assert.assertThat(serverStoreConfiguration.isWriteBehindConfigured(), Matchers.is(false));
    }
}

