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
import Schema.INT32_SCHEMA;
import Schema.STRING_SCHEMA;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceFieldTest {
    private ReplaceField<SinkRecord> xform = new ReplaceField.Value<>();

    @Test
    public void schemaless() {
        final Map<String, String> props = new HashMap<>();
        props.put("blacklist", "dont");
        props.put("renames", "abc:xyz,foo:bar");
        xform.configure(props);
        final Map<String, Object> value = new HashMap<>();
        value.put("dont", "whatever");
        value.put("abc", 42);
        value.put("foo", true);
        value.put("etc", "etc");
        final SinkRecord record = new SinkRecord("test", 0, null, null, null, value, 0);
        final SinkRecord transformedRecord = xform.apply(record);
        final Map updatedValue = ((Map) (transformedRecord.value()));
        Assert.assertEquals(3, updatedValue.size());
        Assert.assertEquals(42, updatedValue.get("xyz"));
        Assert.assertEquals(true, updatedValue.get("bar"));
        Assert.assertEquals("etc", updatedValue.get("etc"));
    }

    @Test
    public void withSchema() {
        final Map<String, String> props = new HashMap<>();
        props.put("whitelist", "abc,foo");
        props.put("renames", "abc:xyz,foo:bar");
        xform.configure(props);
        final Schema schema = SchemaBuilder.struct().field("dont", STRING_SCHEMA).field("abc", INT32_SCHEMA).field("foo", BOOLEAN_SCHEMA).field("etc", STRING_SCHEMA).build();
        final Struct value = new Struct(schema);
        value.put("dont", "whatever");
        value.put("abc", 42);
        value.put("foo", true);
        value.put("etc", "etc");
        final SinkRecord record = new SinkRecord("test", 0, null, null, schema, value, 0);
        final SinkRecord transformedRecord = xform.apply(record);
        final Struct updatedValue = ((Struct) (transformedRecord.value()));
        Assert.assertEquals(2, updatedValue.schema().fields().size());
        Assert.assertEquals(new Integer(42), updatedValue.getInt32("xyz"));
        Assert.assertEquals(true, updatedValue.getBoolean("bar"));
    }
}

