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
package org.apache.kafka.connect.header;


import Schema.BOOLEAN_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;


public class ConnectHeaderTest {
    private String key;

    private ConnectHeader header;

    @Test
    public void shouldAllowNullValues() {
        withValue(OPTIONAL_STRING_SCHEMA, null);
    }

    @Test
    public void shouldAllowNullSchema() {
        withValue(null, null);
        Assert.assertNull(header.schema());
        Assert.assertNull(header.value());
        String value = "non-null value";
        withValue(null, value);
        Assert.assertNull(header.schema());
        Assert.assertSame(value, header.value());
    }

    @Test
    public void shouldAllowNonNullValue() {
        String value = "non-null value";
        withValue(STRING_SCHEMA, value);
        Assert.assertSame(STRING_SCHEMA, header.schema());
        Assert.assertEquals(value, header.value());
        withValue(BOOLEAN_SCHEMA, true);
        Assert.assertSame(BOOLEAN_SCHEMA, header.schema());
        Assert.assertEquals(true, header.value());
    }

    @Test
    public void shouldGetSchemaFromStruct() {
        Schema schema = SchemaBuilder.struct().field("foo", STRING_SCHEMA).field("bar", INT32_SCHEMA).build();
        Struct value = new Struct(schema);
        value.put("foo", "value");
        value.put("bar", 100);
        withValue(null, value);
        Assert.assertSame(schema, header.schema());
        Assert.assertSame(value, header.value());
    }

    @Test
    public void shouldSatisfyEquals() {
        String value = "non-null value";
        Header h1 = withValue(STRING_SCHEMA, value);
        Assert.assertSame(STRING_SCHEMA, header.schema());
        Assert.assertEquals(value, header.value());
        Header h2 = withValue(STRING_SCHEMA, value);
        Assert.assertEquals(h1, h2);
        Assert.assertEquals(h1.hashCode(), h2.hashCode());
        Header h3 = withValue(INT8_SCHEMA, 100);
        Assert.assertNotEquals(h3, h2);
    }
}

