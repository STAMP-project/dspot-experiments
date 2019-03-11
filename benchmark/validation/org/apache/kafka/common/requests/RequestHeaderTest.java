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
package org.apache.kafka.common.requests;


import ApiKeys.CONTROLLED_SHUTDOWN;
import ApiKeys.CONTROLLED_SHUTDOWN.id;
import java.nio.ByteBuffer;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class RequestHeaderTest {
    @Test
    public void testSerdeControlledShutdownV0() {
        // Verify that version 0 of controlled shutdown does not include the clientId field
        int correlationId = 2342;
        ByteBuffer rawBuffer = ByteBuffer.allocate(32);
        rawBuffer.putShort(id);
        rawBuffer.putShort(((short) (0)));
        rawBuffer.putInt(correlationId);
        rawBuffer.flip();
        RequestHeader deserialized = RequestHeader.parse(rawBuffer);
        Assert.assertEquals(CONTROLLED_SHUTDOWN, deserialized.apiKey());
        Assert.assertEquals(0, deserialized.apiVersion());
        Assert.assertEquals(correlationId, deserialized.correlationId());
        Assert.assertEquals("", deserialized.clientId());
        Struct serialized = deserialized.toStruct();
        ByteBuffer serializedBuffer = TestUtils.toBuffer(serialized);
        Assert.assertEquals(id, serializedBuffer.getShort(0));
        Assert.assertEquals(0, serializedBuffer.getShort(2));
        Assert.assertEquals(correlationId, serializedBuffer.getInt(4));
        Assert.assertEquals(8, serializedBuffer.limit());
    }

    @Test
    public void testRequestHeader() {
        RequestHeader header = new RequestHeader(ApiKeys.FIND_COORDINATOR, ((short) (1)), "", 10);
        ByteBuffer buffer = TestUtils.toBuffer(header.toStruct());
        RequestHeader deserialized = RequestHeader.parse(buffer);
        Assert.assertEquals(header, deserialized);
    }

    @Test
    public void testRequestHeaderWithNullClientId() {
        RequestHeader header = new RequestHeader(ApiKeys.FIND_COORDINATOR, ((short) (1)), null, 10);
        Struct headerStruct = header.toStruct();
        ByteBuffer buffer = TestUtils.toBuffer(headerStruct);
        RequestHeader deserialized = RequestHeader.parse(buffer);
        Assert.assertEquals(header.apiKey(), deserialized.apiKey());
        Assert.assertEquals(header.apiVersion(), deserialized.apiVersion());
        Assert.assertEquals(header.correlationId(), deserialized.correlationId());
        Assert.assertEquals("", deserialized.clientId());// null defaults to ""

    }
}

