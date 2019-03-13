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
package org.apache.nifi.serialization;


import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import java.util.ArrayList;
import java.util.List;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestSimpleRecordSchema {
    @Test
    public void testPreventsTwoFieldsWithSameAlias() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("hello", STRING.getDataType(), null, set("foo", "bar")));
        fields.add(new RecordField("goodbye", STRING.getDataType(), null, set("baz", "bar")));
        try {
            new SimpleRecordSchema(fields);
            Assert.fail("Was able to create two fields with same alias");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testPreventsTwoFieldsWithSameName() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("hello", STRING.getDataType(), null, set("foo", "bar")));
        fields.add(new RecordField("hello", STRING.getDataType()));
        try {
            new SimpleRecordSchema(fields);
            Assert.fail("Was able to create two fields with same name");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testPreventsTwoFieldsWithConflictingNamesAliases() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("hello", STRING.getDataType(), null, set("foo", "bar")));
        fields.add(new RecordField("bar", STRING.getDataType()));
        try {
            new SimpleRecordSchema(fields);
            Assert.fail("Was able to create two fields with conflicting names/aliases");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testHashCodeAndEqualsWithSelfReferencingSchema() {
        final SimpleRecordSchema schema = new SimpleRecordSchema(SchemaIdentifier.EMPTY);
        final List<RecordField> personFields = new ArrayList<>();
        personFields.add(new RecordField("name", STRING.getDataType()));
        personFields.add(new RecordField("sibling", RECORD.getRecordDataType(schema)));
        schema.setFields(personFields);
        schema.hashCode();
        Assert.assertTrue(schema.equals(schema));
        final SimpleRecordSchema secondSchema = new SimpleRecordSchema(SchemaIdentifier.EMPTY);
        secondSchema.setFields(personFields);
        Assert.assertTrue(schema.equals(secondSchema));
        Assert.assertTrue(secondSchema.equals(schema));
    }
}

