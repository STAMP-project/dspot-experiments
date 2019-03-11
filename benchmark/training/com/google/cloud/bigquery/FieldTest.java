/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.bigquery;


import Field.Mode;
import LegacySQLTypeName.BOOLEAN;
import StandardSQLTypeName.INT64;
import StandardSQLTypeName.STRING;
import StandardSQLTypeName.STRUCT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;

import static LegacySQLTypeName.INTEGER;
import static LegacySQLTypeName.RECORD;
import static LegacySQLTypeName.STRING;


public class FieldTest {
    private static final String FIELD_NAME1 = "StringField";

    private static final String FIELD_NAME2 = "IntegerField";

    private static final String FIELD_NAME3 = "RecordField";

    private static final LegacySQLTypeName FIELD_TYPE1 = STRING;

    private static final LegacySQLTypeName FIELD_TYPE2 = INTEGER;

    private static final Mode FIELD_MODE1 = Mode.NULLABLE;

    private static final Mode FIELD_MODE2 = Mode.REPEATED;

    private static final Mode FIELD_MODE3 = Mode.REQUIRED;

    private static final String FIELD_DESCRIPTION1 = "FieldDescription1";

    private static final String FIELD_DESCRIPTION2 = "FieldDescription2";

    private static final String FIELD_DESCRIPTION3 = "FieldDescription3";

    private static final Field FIELD_SCHEMA1 = Field.newBuilder(FieldTest.FIELD_NAME1, FieldTest.FIELD_TYPE1).setMode(FieldTest.FIELD_MODE1).setDescription(FieldTest.FIELD_DESCRIPTION1).build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder(FieldTest.FIELD_NAME2, FieldTest.FIELD_TYPE2).setMode(FieldTest.FIELD_MODE2).setDescription(FieldTest.FIELD_DESCRIPTION2).build();

    private static final LegacySQLTypeName FIELD_TYPE3 = RECORD;

    private static final Field FIELD_SCHEMA3 = Field.newBuilder(FieldTest.FIELD_NAME3, FieldTest.FIELD_TYPE3, FieldTest.FIELD_SCHEMA1, FieldTest.FIELD_SCHEMA2).setMode(FieldTest.FIELD_MODE3).setDescription(FieldTest.FIELD_DESCRIPTION3).build();

    private static final Field STANDARD_FIELD_SCHEMA1 = Field.newBuilder(FieldTest.FIELD_NAME1, STRING).setMode(FieldTest.FIELD_MODE1).setDescription(FieldTest.FIELD_DESCRIPTION1).build();

    private static final Field STANDARD_FIELD_SCHEMA2 = Field.newBuilder(FieldTest.FIELD_NAME2, INT64).setMode(FieldTest.FIELD_MODE2).setDescription(FieldTest.FIELD_DESCRIPTION2).build();

    private static final Field STANDARD_FIELD_SCHEMA3 = Field.newBuilder(FieldTest.FIELD_NAME3, STRUCT, FieldTest.STANDARD_FIELD_SCHEMA1, FieldTest.STANDARD_FIELD_SCHEMA2).setMode(FieldTest.FIELD_MODE3).setDescription(FieldTest.FIELD_DESCRIPTION3).build();

    @Test
    public void testToBuilder() {
        compareFieldSchemas(FieldTest.FIELD_SCHEMA1, FieldTest.FIELD_SCHEMA1.toBuilder().build());
        compareFieldSchemas(FieldTest.FIELD_SCHEMA2, FieldTest.FIELD_SCHEMA2.toBuilder().build());
        compareFieldSchemas(FieldTest.FIELD_SCHEMA3, FieldTest.FIELD_SCHEMA3.toBuilder().build());
        Field field = FieldTest.FIELD_SCHEMA1.toBuilder().setDescription("New Description").build();
        Assert.assertEquals("New Description", field.getDescription());
        field = field.toBuilder().setDescription(FieldTest.FIELD_DESCRIPTION1).build();
        compareFieldSchemas(FieldTest.FIELD_SCHEMA1, field);
    }

    @Test
    public void testToBuilderWithStandardSQLTypeName() {
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA1, FieldTest.STANDARD_FIELD_SCHEMA1.toBuilder().build());
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA2, FieldTest.STANDARD_FIELD_SCHEMA2.toBuilder().build());
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA3, FieldTest.STANDARD_FIELD_SCHEMA3.toBuilder().build());
        Field field = FieldTest.STANDARD_FIELD_SCHEMA1.toBuilder().setDescription("New Description").build();
        Assert.assertEquals("New Description", field.getDescription());
        field = field.toBuilder().setDescription(FieldTest.FIELD_DESCRIPTION1).build();
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA1, field);
    }

    @Test
    public void testToBuilderIncomplete() {
        Field field = Field.of(FieldTest.FIELD_NAME1, FieldTest.FIELD_TYPE1);
        compareFieldSchemas(field, field.toBuilder().build());
        field = Field.of(FieldTest.FIELD_NAME2, FieldTest.FIELD_TYPE3, FieldTest.FIELD_SCHEMA1, FieldTest.FIELD_SCHEMA2);
        compareFieldSchemas(field, field.toBuilder().build());
    }

    @Test
    public void testToBuilderIncompleteWithStandardSQLTypeName() {
        Field field = Field.of(FieldTest.FIELD_NAME1, FieldTest.FIELD_TYPE1);
        compareFieldSchemas(field, field.toBuilder().build());
        field = Field.of(FieldTest.FIELD_NAME2, FieldTest.FIELD_TYPE3, FieldTest.STANDARD_FIELD_SCHEMA1, FieldTest.STANDARD_FIELD_SCHEMA2);
        compareFieldSchemas(field, field.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(FieldTest.FIELD_NAME1, FieldTest.FIELD_SCHEMA1.getName());
        Assert.assertEquals(FieldTest.FIELD_TYPE1, FieldTest.FIELD_SCHEMA1.getType());
        Assert.assertEquals(FieldTest.FIELD_MODE1, FieldTest.FIELD_SCHEMA1.getMode());
        Assert.assertEquals(FieldTest.FIELD_DESCRIPTION1, FieldTest.FIELD_SCHEMA1.getDescription());
        Assert.assertEquals(null, FieldTest.FIELD_SCHEMA1.getSubFields());
        Assert.assertEquals(FieldTest.FIELD_NAME3, FieldTest.FIELD_SCHEMA3.getName());
        Assert.assertEquals(FieldTest.FIELD_TYPE3, FieldTest.FIELD_SCHEMA3.getType());
        Assert.assertEquals(FieldTest.FIELD_MODE3, FieldTest.FIELD_SCHEMA3.getMode());
        Assert.assertEquals(FieldTest.FIELD_DESCRIPTION3, FieldTest.FIELD_SCHEMA3.getDescription());
        Assert.assertEquals(FieldList.of(FieldTest.FIELD_SCHEMA1, FieldTest.FIELD_SCHEMA2), FieldTest.FIELD_SCHEMA3.getSubFields());
    }

    @Test
    public void testBuilderWithStandardSQLTypeName() {
        Assert.assertEquals(FieldTest.FIELD_NAME1, FieldTest.STANDARD_FIELD_SCHEMA1.getName());
        Assert.assertEquals(FieldTest.FIELD_TYPE1, FieldTest.STANDARD_FIELD_SCHEMA1.getType());
        Assert.assertEquals(FieldTest.FIELD_MODE1, FieldTest.STANDARD_FIELD_SCHEMA1.getMode());
        Assert.assertEquals(FieldTest.FIELD_DESCRIPTION1, FieldTest.STANDARD_FIELD_SCHEMA1.getDescription());
        Assert.assertEquals(null, FieldTest.STANDARD_FIELD_SCHEMA1.getSubFields());
        Assert.assertEquals(FieldTest.FIELD_NAME3, FieldTest.STANDARD_FIELD_SCHEMA3.getName());
        Assert.assertEquals(FieldTest.FIELD_TYPE3, FieldTest.STANDARD_FIELD_SCHEMA3.getType());
        Assert.assertEquals(FieldTest.FIELD_MODE3, FieldTest.STANDARD_FIELD_SCHEMA3.getMode());
        Assert.assertEquals(FieldTest.FIELD_DESCRIPTION3, FieldTest.STANDARD_FIELD_SCHEMA3.getDescription());
        Assert.assertEquals(FieldList.of(FieldTest.STANDARD_FIELD_SCHEMA1, FieldTest.STANDARD_FIELD_SCHEMA2), FieldTest.STANDARD_FIELD_SCHEMA3.getSubFields());
    }

    @Test
    public void testToAndFromPb() {
        compareFieldSchemas(FieldTest.FIELD_SCHEMA1, Field.fromPb(FieldTest.FIELD_SCHEMA1.toPb()));
        compareFieldSchemas(FieldTest.FIELD_SCHEMA2, Field.fromPb(FieldTest.FIELD_SCHEMA2.toPb()));
        compareFieldSchemas(FieldTest.FIELD_SCHEMA3, Field.fromPb(FieldTest.FIELD_SCHEMA3.toPb()));
        Field field = Field.newBuilder(FieldTest.FIELD_NAME1, FieldTest.FIELD_TYPE1).build();
        compareFieldSchemas(field, Field.fromPb(field.toPb()));
    }

    @Test
    public void testToAndFromPbWithStandardSQLTypeName() {
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA1, Field.fromPb(FieldTest.STANDARD_FIELD_SCHEMA1.toPb()));
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA2, Field.fromPb(FieldTest.STANDARD_FIELD_SCHEMA2.toPb()));
        compareFieldSchemas(FieldTest.STANDARD_FIELD_SCHEMA3, Field.fromPb(FieldTest.STANDARD_FIELD_SCHEMA3.toPb()));
        Field field = Field.newBuilder(FieldTest.FIELD_NAME1, FieldTest.FIELD_TYPE1).build();
        compareFieldSchemas(field, Field.fromPb(field.toPb()));
    }

    @Test
    public void testSubFieldWithClonedType() throws Exception {
        LegacySQLTypeName record = RECORD;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(record);
        oos.flush();
        oos.close();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(is);
        LegacySQLTypeName clonedRecord = ((LegacySQLTypeName) (ois.readObject()));
        ois.close();
        Field.of("field", clonedRecord, Field.of("subfield", BOOLEAN));
    }
}

