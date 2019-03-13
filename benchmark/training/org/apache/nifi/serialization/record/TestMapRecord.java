/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.serialization.record;


import RecordFieldType.INT;
import RecordFieldType.STRING;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestMapRecord {
    @Test
    public void testDefaultValue() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("noDefault", STRING.getDataType()));
        fields.add(new RecordField("defaultOfHello", STRING.getDataType(), "hello"));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        final Record record = new MapRecord(schema, values);
        Assert.assertNull(record.getValue("noDefault"));
        Assert.assertEquals("hello", record.getValue("defaultOfHello"));
    }

    @Test
    public void testDefaultValueInGivenField() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("noDefault", STRING.getDataType()));
        fields.add(new RecordField("defaultOfHello", STRING.getDataType(), "hello"));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        final Record record = new MapRecord(schema, values);
        Assert.assertNull(record.getValue("noDefault"));
        Assert.assertEquals("hello", record.getValue("defaultOfHello"));
        final RecordField newField = new RecordField("noDefault", STRING.getDataType(), "new");
        Assert.assertEquals("new", record.getValue(newField));
    }

    @Test
    public void testIllegalDefaultValue() {
        new RecordField("hello", STRING.getDataType(), 84);
        new RecordField("hello", STRING.getDataType(), ((Object) (null)));
        new RecordField("hello", INT.getDataType(), 84);
        new RecordField("hello", INT.getDataType(), ((Object) (null)));
        try {
            new RecordField("hello", INT.getDataType(), "foo");
            Assert.fail("Was able to set a default value of \"foo\" for INT type");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void testAliasOneValue() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), null, set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("bar", 1);
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals(1, record.getValue("foo"));
        Assert.assertEquals(1, record.getValue("bar"));
        Assert.assertEquals(1, record.getValue("baz"));
    }

    @Test
    public void testAliasConflictingValues() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), null, set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("bar", 1);
        values.put("foo", null);
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals(1, record.getValue("foo"));
        Assert.assertEquals(1, record.getValue("bar"));
        Assert.assertEquals(1, record.getValue("baz"));
    }

    @Test
    public void testAliasConflictingAliasValues() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), null, set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("baz", 1);
        values.put("bar", 33);
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals(33, record.getValue("foo"));
        Assert.assertEquals(33, record.getValue("bar"));
        Assert.assertEquals(33, record.getValue("baz"));
    }

    @Test
    public void testAliasInGivenField() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), null, set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("bar", 33);
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals(33, record.getValue("foo"));
        Assert.assertEquals(33, record.getValue("bar"));
        Assert.assertEquals(33, record.getValue("baz"));
        final RecordField noAlias = new RecordField("hello", STRING.getDataType());
        Assert.assertNull(record.getValue(noAlias));
        final RecordField withAlias = new RecordField("hello", STRING.getDataType(), null, set("baz"));
        Assert.assertEquals(33, record.getValue(withAlias));
        Assert.assertEquals("33", record.getAsString(withAlias, withAlias.getDataType().getFormat()));
    }

    @Test
    public void testDefaultValueWithAliasValue() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), "hello", set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("baz", 1);
        values.put("bar", 33);
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals(33, record.getValue("foo"));
        Assert.assertEquals(33, record.getValue("bar"));
        Assert.assertEquals(33, record.getValue("baz"));
    }

    @Test
    public void testDefaultValueWithAliasesDefined() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("foo", STRING.getDataType(), "hello", set("bar", "baz")));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        final Record record = new MapRecord(schema, values);
        Assert.assertEquals("hello", record.getValue("foo"));
        Assert.assertEquals("hello", record.getValue("bar"));
        Assert.assertEquals("hello", record.getValue("baz"));
    }
}

