/**
 * Copyright 2017 Google LLC
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
import org.junit.Assert;
import org.junit.Test;

import static LegacySQLTypeName.INTEGER;
import static LegacySQLTypeName.RECORD;
import static LegacySQLTypeName.STRING;


public class FieldListTest {
    private static final String FIELD_NAME1 = "StringField";

    private static final String FIELD_NAME2 = "IntegerField";

    private static final String FIELD_NAME3 = "RecordField";

    private static final String FIELD_NAME4 = "NonExistentField";

    private static final LegacySQLTypeName FIELD_TYPE1 = STRING;

    private static final LegacySQLTypeName FIELD_TYPE2 = INTEGER;

    private static final LegacySQLTypeName FIELD_TYPE3 = RECORD;

    private static final Mode FIELD_MODE1 = Mode.NULLABLE;

    private static final Mode FIELD_MODE2 = Mode.REPEATED;

    private static final Mode FIELD_MODE3 = Mode.REQUIRED;

    private static final String FIELD_DESCRIPTION1 = "FieldDescription1";

    private static final String FIELD_DESCRIPTION2 = "FieldDescription2";

    private static final String FIELD_DESCRIPTION3 = "FieldDescription3";

    private final Field fieldSchema1 = Field.newBuilder(FieldListTest.FIELD_NAME1, FieldListTest.FIELD_TYPE1).setMode(FieldListTest.FIELD_MODE1).setDescription(FieldListTest.FIELD_DESCRIPTION1).build();

    private final Field fieldSchema2 = Field.newBuilder(FieldListTest.FIELD_NAME2, FieldListTest.FIELD_TYPE2).setMode(FieldListTest.FIELD_MODE2).setDescription(FieldListTest.FIELD_DESCRIPTION2).build();

    private final Field fieldSchema3 = Field.newBuilder(FieldListTest.FIELD_NAME3, FieldListTest.FIELD_TYPE3, fieldSchema1, fieldSchema2).setMode(FieldListTest.FIELD_MODE3).setDescription(FieldListTest.FIELD_DESCRIPTION3).build();

    private final FieldList fieldsSchema = FieldList.of(fieldSchema1, fieldSchema2, fieldSchema3);

    @Test
    public void testGetByName() {
        Assert.assertEquals(fieldSchema1, fieldsSchema.get(FieldListTest.FIELD_NAME1));
        Assert.assertEquals(fieldSchema2, fieldsSchema.get(FieldListTest.FIELD_NAME2));
        Assert.assertEquals(fieldSchema3, fieldsSchema.get(FieldListTest.FIELD_NAME3));
        Assert.assertEquals(3, fieldsSchema.size());
        IllegalArgumentException exception = null;
        try {
            fieldsSchema.get(FieldListTest.FIELD_NAME4);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    public void testGetByIndex() {
        Assert.assertEquals(fieldSchema1, fieldsSchema.get(0));
        Assert.assertEquals(fieldSchema2, fieldsSchema.get(1));
        Assert.assertEquals(fieldSchema3, fieldsSchema.get(2));
        Assert.assertEquals(3, fieldsSchema.size());
        IndexOutOfBoundsException exception = null;
        try {
            fieldsSchema.get(4);
        } catch (IndexOutOfBoundsException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    public void testGetRecordSchema() {
        Assert.assertEquals(2, fieldSchema3.getSubFields().size());
        Assert.assertEquals(fieldSchema1, fieldSchema3.getSubFields().get(FieldListTest.FIELD_NAME1));
        Assert.assertEquals(fieldSchema2, fieldSchema3.getSubFields().get(FieldListTest.FIELD_NAME2));
        Assert.assertEquals(0, fieldSchema3.getSubFields().getIndex(FieldListTest.FIELD_NAME1));
        Assert.assertEquals(1, fieldSchema3.getSubFields().getIndex(FieldListTest.FIELD_NAME2));
        Assert.assertEquals(fieldSchema1, fieldSchema3.getSubFields().get(0));
        Assert.assertEquals(fieldSchema2, fieldSchema3.getSubFields().get(1));
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(fieldsSchema, FieldList.of(fieldSchema1, fieldSchema2, fieldSchema3));
        Assert.assertNotEquals(fieldsSchema, FieldList.of(fieldSchema1, fieldSchema3));
        Assert.assertEquals(fieldsSchema, FieldList.fromPb(fieldsSchema.toPb()));
    }
}

