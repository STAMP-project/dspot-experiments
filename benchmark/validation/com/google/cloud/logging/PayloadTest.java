/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import NullValue.NULL_VALUE;
import Type.JSON;
import Type.PROTO;
import Type.STRING;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.Payload.ProtoPayload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PayloadTest {
    private static final String STRING_DATA = "string";

    private static final Value NULL_VALUE = Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();

    private static final Double NUMBER = 42.0;

    private static final Value NUMBER_VALUE = Value.newBuilder().setNumberValue(PayloadTest.NUMBER).build();

    private static final String STRING = "string";

    private static final Value STRING_VALUE = Value.newBuilder().setStringValue(PayloadTest.STRING).build();

    private static final Boolean BOOLEAN = true;

    private static final Value BOOLEAN_VALUE = Value.newBuilder().setBoolValue(PayloadTest.BOOLEAN).build();

    private static final List<Object> LIST = ImmutableList.<Object>of(PayloadTest.NUMBER, PayloadTest.STRING, PayloadTest.BOOLEAN);

    private static final Value VALUE_LIST = Value.newBuilder().setListValue(ListValue.newBuilder().addValues(PayloadTest.NUMBER_VALUE).addValues(PayloadTest.STRING_VALUE).addValues(PayloadTest.BOOLEAN_VALUE).build()).build();

    private static final Map<String, Object> INNER_MAP = new HashMap<>();

    private static final Map<String, Object> JSON_DATA = new HashMap<>();

    private static final Struct INNER_STRUCT = Struct.newBuilder().putAllFields(ImmutableMap.of("null", PayloadTest.NULL_VALUE, "number", PayloadTest.NUMBER_VALUE, "string", PayloadTest.STRING_VALUE, "boolean", PayloadTest.BOOLEAN_VALUE, "list", PayloadTest.VALUE_LIST)).build();

    private static final Struct STRUCT_DATA = Struct.newBuilder().putAllFields(ImmutableMap.<String, Value>builder().put("null", PayloadTest.NULL_VALUE).put("number", PayloadTest.NUMBER_VALUE).put("string", PayloadTest.STRING_VALUE).put("boolean", PayloadTest.BOOLEAN_VALUE).put("list", PayloadTest.VALUE_LIST).put("struct", Value.newBuilder().setStructValue(PayloadTest.INNER_STRUCT).build()).build()).build();

    static {
        PayloadTest.INNER_MAP.put("null", null);
        PayloadTest.INNER_MAP.put("number", PayloadTest.NUMBER);
        PayloadTest.INNER_MAP.put("string", PayloadTest.STRING);
        PayloadTest.INNER_MAP.put("boolean", PayloadTest.BOOLEAN);
        PayloadTest.INNER_MAP.put("list", PayloadTest.LIST);
        PayloadTest.JSON_DATA.put("null", null);
        PayloadTest.JSON_DATA.put("number", PayloadTest.NUMBER);
        PayloadTest.JSON_DATA.put("string", PayloadTest.STRING);
        PayloadTest.JSON_DATA.put("boolean", PayloadTest.BOOLEAN);
        PayloadTest.JSON_DATA.put("list", PayloadTest.LIST);
        PayloadTest.JSON_DATA.put("struct", PayloadTest.INNER_MAP);
    }

    private static final Any PROTO_DATA = Any.getDefaultInstance();

    private static final StringPayload STRING_PAYLOAD = StringPayload.of(PayloadTest.STRING_DATA);

    private static final JsonPayload JSON_PAYLOAD = JsonPayload.of(PayloadTest.JSON_DATA);

    private static final ProtoPayload PROTO_PAYLOAD = ProtoPayload.of(PayloadTest.PROTO_DATA);

    @Test
    public void testOf() {
        Assert.assertEquals(Type.STRING, PayloadTest.STRING_PAYLOAD.getType());
        Assert.assertEquals(PayloadTest.STRING_DATA, PayloadTest.STRING_PAYLOAD.getData());
        Assert.assertEquals(JSON, PayloadTest.JSON_PAYLOAD.getType());
        Assert.assertEquals(PayloadTest.STRUCT_DATA, PayloadTest.JSON_PAYLOAD.getData());
        Assert.assertEquals(PayloadTest.JSON_DATA, PayloadTest.JSON_PAYLOAD.getDataAsMap());
        Assert.assertEquals(PROTO, PayloadTest.PROTO_PAYLOAD.getType());
        Assert.assertEquals(PayloadTest.PROTO_DATA, PayloadTest.PROTO_PAYLOAD.getData());
        JsonPayload jsonPayload = JsonPayload.of(PayloadTest.STRUCT_DATA);
        Assert.assertEquals(JSON, jsonPayload.getType());
        Assert.assertEquals(PayloadTest.STRUCT_DATA, jsonPayload.getData());
        Assert.assertEquals(PayloadTest.JSON_DATA, jsonPayload.getDataAsMap());
    }

    @Test
    public void testToAndFromPb() {
        Payload<?> payload = Payload.fromPb(PayloadTest.STRING_PAYLOAD.toPb().build());
        Assert.assertTrue((payload instanceof StringPayload));
        comparePayload(PayloadTest.STRING_PAYLOAD, payload);
        payload = Payload.fromPb(PayloadTest.JSON_PAYLOAD.toPb().build());
        Assert.assertTrue((payload instanceof JsonPayload));
        comparePayload(PayloadTest.JSON_PAYLOAD, payload);
        payload = ProtoPayload.fromPb(PayloadTest.PROTO_PAYLOAD.toPb().build());
        Assert.assertTrue((payload instanceof ProtoPayload));
        comparePayload(PayloadTest.PROTO_PAYLOAD, payload);
    }
}

