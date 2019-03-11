/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.pdx.internal;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SerializationTest.class)
public class PdxFieldTest {
    static final String FIELD_NAME = "fieldName";

    static final int FIELD_INDEX = 13;

    static final int VAR_LEN_FIELD_SEQ_ID = 37;

    static final FieldType FIELD_TYPE = FieldType.OBJECT;

    static final boolean IDENTITY_FIELD = true;

    @Test
    public void testNoArgConstructor() {
        final PdxField emptyField = new PdxField();
        Assert.assertNull(emptyField.getFieldName());
        Assert.assertEquals(0, emptyField.getFieldIndex());
        Assert.assertEquals(0, emptyField.getVarLenFieldSeqId());
        Assert.assertNull(emptyField.getFieldType());
        Assert.assertFalse(emptyField.isIdentityField());
        try {
            Assert.assertEquals(false, emptyField.isVariableLengthType());
            Assert.fail();
        } catch (NullPointerException npe) {
            // Pass.
        }
        Assert.assertEquals(0, emptyField.getRelativeOffset());
        Assert.assertEquals(0, emptyField.getVlfOffsetIndex());
        Assert.assertEquals(false, emptyField.isDeleted());
        try {
            Assert.assertNull(emptyField.getTypeIdString());
            Assert.fail();
        } catch (NullPointerException npe) {
            // Pass.
        }
    }

    @Test
    public void testSomeArgConstructor() {
        final PdxField nonEmptyField = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        Assert.assertEquals(PdxFieldTest.FIELD_NAME, nonEmptyField.getFieldName());
        Assert.assertEquals(PdxFieldTest.FIELD_INDEX, nonEmptyField.getFieldIndex());
        Assert.assertEquals(PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, nonEmptyField.getVarLenFieldSeqId());
        Assert.assertEquals(PdxFieldTest.FIELD_TYPE, nonEmptyField.getFieldType());
        Assert.assertEquals(PdxFieldTest.IDENTITY_FIELD, nonEmptyField.isIdentityField());
        Assert.assertEquals((!(PdxFieldTest.FIELD_TYPE.isFixedWidth())), nonEmptyField.isVariableLengthType());
        Assert.assertEquals(0, nonEmptyField.getRelativeOffset());
        Assert.assertEquals(0, nonEmptyField.getVlfOffsetIndex());
        Assert.assertEquals(false, nonEmptyField.isDeleted());
        Assert.assertEquals(PdxFieldTest.FIELD_TYPE.toString(), nonEmptyField.getTypeIdString());
    }

    @Test
    public void testCompareTo() {
        final PdxField field = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        Assert.assertEquals(0, field.compareTo(field));
        final PdxField sameFieldNameOnly = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.getAnotherFieldType(PdxFieldTest.FIELD_TYPE), (!(PdxFieldTest.IDENTITY_FIELD)));
        Assert.assertEquals(0, field.compareTo(sameFieldNameOnly));
        final PdxField differentFieldNameOnly = new PdxField(("Not " + (PdxFieldTest.FIELD_NAME)), PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        Assert.assertNotEquals(0, field.compareTo(differentFieldNameOnly));
    }

    @Test
    public void testHashCode() {
        final PdxField field = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        final PdxField sameFieldNameAndFieldType = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.FIELD_TYPE, (!(PdxFieldTest.IDENTITY_FIELD)));
        Assert.assertEquals(field.hashCode(), sameFieldNameAndFieldType.hashCode());
        final PdxField differentFieldName = new PdxField(("Not " + (PdxFieldTest.FIELD_NAME)), ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.FIELD_TYPE, (!(PdxFieldTest.IDENTITY_FIELD)));
        Assert.assertNotEquals(field.hashCode(), differentFieldName.hashCode());
        final PdxField differentFieldType = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.getAnotherFieldType(PdxFieldTest.FIELD_TYPE), (!(PdxFieldTest.IDENTITY_FIELD)));
        Assert.assertNotEquals(field.hashCode(), differentFieldType.hashCode());
    }

    @Test
    public void testEquals() {
        final PdxField field = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        field.setDeleted(true);
        Assert.assertTrue(field.equals(field));
        Assert.assertFalse(field.equals(null));
        Assert.assertFalse(field.equals(new Object()));
        final PdxField sameFieldNameFieldTypeAndDeleted = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.FIELD_TYPE, (!(PdxFieldTest.IDENTITY_FIELD)));
        sameFieldNameFieldTypeAndDeleted.setDeleted(true);
        Assert.assertTrue(field.equals(sameFieldNameFieldTypeAndDeleted));
        final PdxField differentFieldName = new PdxField(("Not " + (PdxFieldTest.FIELD_NAME)), ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.FIELD_TYPE, (!(PdxFieldTest.IDENTITY_FIELD)));
        differentFieldName.setDeleted(true);
        Assert.assertFalse(field.equals(differentFieldName));
        final PdxField differentFieldType = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.getAnotherFieldType(PdxFieldTest.FIELD_TYPE), (!(PdxFieldTest.IDENTITY_FIELD)));
        differentFieldType.setDeleted(true);
        Assert.assertFalse(field.equals(differentFieldType));
        final PdxField differentDeleted = new PdxField(PdxFieldTest.FIELD_NAME, ((PdxFieldTest.FIELD_INDEX) + 1), ((PdxFieldTest.VAR_LEN_FIELD_SEQ_ID) + 1), PdxFieldTest.FIELD_TYPE, (!(PdxFieldTest.IDENTITY_FIELD)));
        differentDeleted.setDeleted(false);
        Assert.assertFalse(field.equals(differentDeleted));
    }

    @Test
    public void testToString() {
        final PdxField field = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        Assert.assertEquals(0, field.toString().indexOf(PdxFieldTest.FIELD_NAME));
    }

    @Test
    public void testToStream() {
        final PdxField field = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        field.toStream(new PrintStream(byteArrayOutputStream));
        Assert.assertNotEquals((-1), byteArrayOutputStream.toString().indexOf(PdxFieldTest.FIELD_NAME));
    }

    @Test
    public void testToDataAndFromData() throws IOException, ClassNotFoundException {
        final PdxField before = new PdxField(PdxFieldTest.FIELD_NAME, PdxFieldTest.FIELD_INDEX, PdxFieldTest.VAR_LEN_FIELD_SEQ_ID, PdxFieldTest.FIELD_TYPE, PdxFieldTest.IDENTITY_FIELD);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        before.toData(dataOutputStream);
        dataOutputStream.close();
        final PdxField after = new PdxField();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        after.fromData(dataInputStream);
        Assert.assertEquals(before.getFieldName(), after.getFieldName());
        Assert.assertEquals(before.getFieldIndex(), after.getFieldIndex());
        Assert.assertEquals(before.getVarLenFieldSeqId(), after.getVarLenFieldSeqId());
        Assert.assertEquals(before.getFieldType(), after.getFieldType());
        Assert.assertEquals(before.isIdentityField(), after.isIdentityField());
        Assert.assertEquals(before.isVariableLengthType(), after.isVariableLengthType());
        Assert.assertEquals(before.getRelativeOffset(), after.getRelativeOffset());
        Assert.assertEquals(before.getVlfOffsetIndex(), after.getVlfOffsetIndex());
        Assert.assertEquals(before.isDeleted(), after.isDeleted());
        Assert.assertEquals(before.getTypeIdString(), after.getTypeIdString());
    }
}

