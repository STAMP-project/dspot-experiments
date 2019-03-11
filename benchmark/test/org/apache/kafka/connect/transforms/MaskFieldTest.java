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
package org.apache.kafka.connect.transforms;


import Schema.BOOLEAN_SCHEMA;
import Schema.FLOAT32_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.STRING_SCHEMA;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Date.SCHEMA;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;


public class MaskFieldTest {
    @Test
    public void schemaless() {
        final Map<String, Object> value = new HashMap<>();
        value.put("magic", 42);
        value.put("bool", true);
        value.put("byte", ((byte) (42)));
        value.put("short", ((short) (42)));
        value.put("int", 42);
        value.put("long", 42L);
        value.put("float", 42.0F);
        value.put("double", 42.0);
        value.put("string", "blabla");
        value.put("date", new Date());
        value.put("bigint", new BigInteger("42"));
        value.put("bigdec", new BigDecimal("42.0"));
        value.put("list", Collections.singletonList(42));
        value.put("map", Collections.singletonMap("key", "value"));
        final List<String> maskFields = new ArrayList<>(value.keySet());
        maskFields.remove("magic");
        @SuppressWarnings("unchecked")
        final Map<String, Object> updatedValue = ((Map) (MaskFieldTest.transform(maskFields).apply(MaskFieldTest.record(null, value)).value()));
        Assert.assertEquals(42, updatedValue.get("magic"));
        Assert.assertEquals(false, updatedValue.get("bool"));
        Assert.assertEquals(((byte) (0)), updatedValue.get("byte"));
        Assert.assertEquals(((short) (0)), updatedValue.get("short"));
        Assert.assertEquals(0, updatedValue.get("int"));
        Assert.assertEquals(0L, updatedValue.get("long"));
        Assert.assertEquals(0.0F, updatedValue.get("float"));
        Assert.assertEquals(0.0, updatedValue.get("double"));
        Assert.assertEquals("", updatedValue.get("string"));
        Assert.assertEquals(new Date(0), updatedValue.get("date"));
        Assert.assertEquals(BigInteger.ZERO, updatedValue.get("bigint"));
        Assert.assertEquals(BigDecimal.ZERO, updatedValue.get("bigdec"));
        Assert.assertEquals(Collections.emptyList(), updatedValue.get("list"));
        Assert.assertEquals(Collections.emptyMap(), updatedValue.get("map"));
    }

    @Test
    public void withSchema() {
        Schema schema = SchemaBuilder.struct().field("magic", INT32_SCHEMA).field("bool", BOOLEAN_SCHEMA).field("byte", INT8_SCHEMA).field("short", INT16_SCHEMA).field("int", INT32_SCHEMA).field("long", INT64_SCHEMA).field("float", FLOAT32_SCHEMA).field("double", FLOAT64_SCHEMA).field("string", STRING_SCHEMA).field("date", SCHEMA).field("time", Time.SCHEMA).field("timestamp", Timestamp.SCHEMA).field("decimal", Decimal.schema(0)).field("array", SchemaBuilder.array(INT32_SCHEMA)).field("map", SchemaBuilder.map(STRING_SCHEMA, STRING_SCHEMA)).build();
        final Struct value = new Struct(schema);
        value.put("magic", 42);
        value.put("bool", true);
        value.put("byte", ((byte) (42)));
        value.put("short", ((short) (42)));
        value.put("int", 42);
        value.put("long", 42L);
        value.put("float", 42.0F);
        value.put("double", 42.0);
        value.put("string", "hmm");
        value.put("date", new Date());
        value.put("time", new Date());
        value.put("timestamp", new Date());
        value.put("decimal", new BigDecimal(42));
        value.put("array", Arrays.asList(1, 2, 3));
        value.put("map", Collections.singletonMap("what", "what"));
        final List<String> maskFields = new ArrayList(schema.fields().size());
        for (Field field : schema.fields()) {
            if (!(field.name().equals("magic"))) {
                maskFields.add(field.name());
            }
        }
        final Struct updatedValue = ((Struct) (MaskFieldTest.transform(maskFields).apply(MaskFieldTest.record(schema, value)).value()));
        Assert.assertEquals(42, updatedValue.get("magic"));
        Assert.assertEquals(false, updatedValue.get("bool"));
        Assert.assertEquals(((byte) (0)), updatedValue.get("byte"));
        Assert.assertEquals(((short) (0)), updatedValue.get("short"));
        Assert.assertEquals(0, updatedValue.get("int"));
        Assert.assertEquals(0L, updatedValue.get("long"));
        Assert.assertEquals(0.0F, updatedValue.get("float"));
        Assert.assertEquals(0.0, updatedValue.get("double"));
        Assert.assertEquals("", updatedValue.get("string"));
        Assert.assertEquals(new Date(0), updatedValue.get("date"));
        Assert.assertEquals(new Date(0), updatedValue.get("time"));
        Assert.assertEquals(new Date(0), updatedValue.get("timestamp"));
        Assert.assertEquals(BigDecimal.ZERO, updatedValue.get("decimal"));
        Assert.assertEquals(Collections.emptyList(), updatedValue.get("array"));
        Assert.assertEquals(Collections.emptyMap(), updatedValue.get("map"));
    }
}

