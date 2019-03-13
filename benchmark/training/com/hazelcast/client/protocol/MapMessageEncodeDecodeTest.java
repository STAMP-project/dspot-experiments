/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.protocol;


import ClientMessage.BEGIN_AND_END_FLAGS;
import MapPutCodec.REQUEST_TYPE;
import MapPutCodec.RequestParameters;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Encode Decode Tests
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MapMessageEncodeDecodeTest {
    private static final SerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    private static final String NAME = "name";

    private static final Data DATA = MapMessageEncodeDecodeTest.serializationService.toData("The Test");

    private static final long THE_LONG = 65535L;

    private ClientProtocolBuffer byteBuffer;

    @Test
    public void shouldEncodeDecodeCorrectly_PUT() {
        final int calculatedSize = RequestParameters.calculateDataSize(MapMessageEncodeDecodeTest.NAME, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG);
        ClientMessage cmEncode = MapPutCodec.encodeRequest(MapMessageEncodeDecodeTest.NAME, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG);
        cmEncode.setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(Long.MAX_VALUE).setPartitionId(77);
        Assert.assertTrue((calculatedSize > (cmEncode.getFrameLength())));
        byteBuffer = cmEncode.buffer();
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, 0);
        MapPutCodec.RequestParameters decodeParams = MapPutCodec.decodeRequest(cmDecode);
        Assert.assertEquals(REQUEST_TYPE.id(), cmDecode.getMessageType());
        Assert.assertEquals(3, cmDecode.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode.getFlags());
        Assert.assertEquals(Long.MAX_VALUE, cmDecode.getCorrelationId());
        Assert.assertEquals(77, cmDecode.getPartitionId());
        Assert.assertEquals(MapMessageEncodeDecodeTest.NAME, decodeParams.name);
        Assert.assertEquals(MapMessageEncodeDecodeTest.DATA, decodeParams.key);
        Assert.assertEquals(MapMessageEncodeDecodeTest.DATA, decodeParams.value);
        Assert.assertEquals(MapMessageEncodeDecodeTest.THE_LONG, decodeParams.threadId);
        Assert.assertEquals(MapMessageEncodeDecodeTest.THE_LONG, decodeParams.ttl);
    }

    @Test
    public void shouldEncodeDecodeCorrectly_PUT_withMaxIdle() {
        final int calculatedSize = MapPutWithMaxIdleCodec.RequestParameters.calculateDataSize(MapMessageEncodeDecodeTest.NAME, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG);
        ClientMessage cmEncode = MapPutWithMaxIdleCodec.encodeRequest(MapMessageEncodeDecodeTest.NAME, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.DATA, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG, MapMessageEncodeDecodeTest.THE_LONG);
        cmEncode.setVersion(((short) (3))).addFlag(BEGIN_AND_END_FLAGS).setCorrelationId(Long.MAX_VALUE).setPartitionId(77);
        Assert.assertTrue((calculatedSize > (cmEncode.getFrameLength())));
        byteBuffer = cmEncode.buffer();
        ClientMessage cmDecode = ClientMessage.createForDecode(byteBuffer, 0);
        MapPutWithMaxIdleCodec.RequestParameters decodeParams = MapPutWithMaxIdleCodec.decodeRequest(cmDecode);
        Assert.assertEquals(MapPutWithMaxIdleCodec.REQUEST_TYPE.id(), cmDecode.getMessageType());
        Assert.assertEquals(3, cmDecode.getVersion());
        Assert.assertEquals(BEGIN_AND_END_FLAGS, cmDecode.getFlags());
        Assert.assertEquals(Long.MAX_VALUE, cmDecode.getCorrelationId());
        Assert.assertEquals(77, cmDecode.getPartitionId());
        Assert.assertEquals(MapMessageEncodeDecodeTest.NAME, decodeParams.name);
        Assert.assertEquals(MapMessageEncodeDecodeTest.DATA, decodeParams.key);
        Assert.assertEquals(MapMessageEncodeDecodeTest.DATA, decodeParams.value);
        Assert.assertEquals(MapMessageEncodeDecodeTest.THE_LONG, decodeParams.threadId);
        Assert.assertEquals(MapMessageEncodeDecodeTest.THE_LONG, decodeParams.ttl);
        Assert.assertEquals(MapMessageEncodeDecodeTest.THE_LONG, decodeParams.maxIdle);
    }
}

