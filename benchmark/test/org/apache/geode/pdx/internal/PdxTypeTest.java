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
import java.util.Collection;
import java.util.List;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SerializationTest.class)
public class PdxTypeTest {
    static final String TYPE_NAME = "typeName";

    static final boolean EXPECT_DOMAIN_CLASS = true;

    static final PdxField FIELD_0 = new PdxField("field0", 0, 0, FieldType.INT, true);

    static final PdxField FIELD_1 = new PdxField("field1", 1, 1, FieldType.STRING, true);

    static final PdxField FIELD_2 = new PdxField("field2", 2, 0, FieldType.BOOLEAN, false);

    static final PdxField FIELD_3 = new PdxField("field3", 3, 0, FieldType.DOUBLE, false);

    static final PdxField FIELD_4 = new PdxField("field4", 4, 2, FieldType.OBJECT_ARRAY, false);

    static {
        PdxTypeTest.FIELD_3.setDeleted(true);
    }

    @Test
    public void testNoArgConstructor() {
        final PdxType type = new PdxType();
        Assert.assertEquals(0, type.getVariableLengthFieldCount());
        Assert.assertNull(type.getClassName());
        Assert.assertFalse(type.getNoDomainClass());
        Assert.assertEquals(0, type.getTypeId());
        Assert.assertEquals(0, type.getFieldCount());
        Assert.assertFalse(type.getHasDeletedField());
    }

    @Test
    public void testSomeArgsConstructor() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        Assert.assertEquals(0, type.getVariableLengthFieldCount());
        Assert.assertEquals(PdxTypeTest.TYPE_NAME, type.getClassName());
        Assert.assertEquals((!(PdxTypeTest.EXPECT_DOMAIN_CLASS)), type.getNoDomainClass());
        Assert.assertEquals(0, type.getTypeId());
        Assert.assertEquals(0, type.getFieldCount());
        Assert.assertFalse(type.getHasDeletedField());
    }

    @Test
    public void testGetDSId() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setTypeId(-559038737);
        Assert.assertEquals(222, type.getDSId());
    }

    @Test
    public void testGetTypeNum() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setTypeId(-559038737);
        Assert.assertEquals(11386607, type.getTypeNum());
    }

    @Test
    public void testAddField() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        Assert.assertEquals(0, type.getFieldCount());
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertEquals(3, type.getFieldCount());
        final List<PdxField> fields = type.getFields();
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_0));
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_1));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_2));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_3));
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_4));
    }

    @Test
    public void testGetPdxField() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertSame(PdxTypeTest.FIELD_0, type.getPdxField(PdxTypeTest.FIELD_0.getFieldName()));
        Assert.assertSame(PdxTypeTest.FIELD_1, type.getPdxField(PdxTypeTest.FIELD_1.getFieldName()));
        Assert.assertNull(type.getPdxField(PdxTypeTest.FIELD_2.getFieldName()));
        Assert.assertNull(type.getPdxField(PdxTypeTest.FIELD_3.getFieldName()));
        Assert.assertSame(PdxTypeTest.FIELD_4, type.getPdxField(PdxTypeTest.FIELD_4.getFieldName()));
    }

    @Test
    public void testGetPdxFieldByIndex() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertSame(PdxTypeTest.FIELD_0, type.getPdxFieldByIndex(1));
        Assert.assertSame(PdxTypeTest.FIELD_1, type.getPdxFieldByIndex(2));
        Assert.assertSame(PdxTypeTest.FIELD_4, type.getPdxFieldByIndex(0));
    }

    @Test
    public void testGetUndeletedFieldCount() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setHasDeletedField(true);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_3);
        type.addField(PdxTypeTest.FIELD_2);
        Assert.assertEquals(2, type.getUndeletedFieldCount());
    }

    @Test
    public void testGetFieldNames() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        final List<String> fieldNames = type.getFieldNames();
        Assert.assertEquals(3, fieldNames.size());
        Assert.assertTrue(fieldNames.contains(PdxTypeTest.FIELD_0.getFieldName()));
        Assert.assertTrue(fieldNames.contains(PdxTypeTest.FIELD_1.getFieldName()));
        Assert.assertFalse(fieldNames.contains(PdxTypeTest.FIELD_2.getFieldName()));
        Assert.assertFalse(fieldNames.contains(PdxTypeTest.FIELD_3.getFieldName()));
        Assert.assertTrue(fieldNames.contains(PdxTypeTest.FIELD_4.getFieldName()));
    }

    @Test
    public void testSortedFields() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setHasDeletedField(true);
        type.addField(PdxTypeTest.FIELD_3);
        type.addField(PdxTypeTest.FIELD_2);
        type.addField(PdxTypeTest.FIELD_1);
        final Collection<PdxField> fields = type.getSortedFields();
        Assert.assertEquals(2, fields.size());
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_0));
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_1));
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_2));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_3));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_4));
    }

    @Test
    public void testSortedIdentityFields() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setHasDeletedField(true);
        type.addField(PdxTypeTest.FIELD_3);
        type.addField(PdxTypeTest.FIELD_2);
        type.addField(PdxTypeTest.FIELD_1);
        final Collection<PdxField> fields = type.getSortedIdentityFields();
        Assert.assertEquals(1, fields.size());
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_0));
        Assert.assertTrue(fields.contains(PdxTypeTest.FIELD_1));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_2));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_3));
        Assert.assertFalse(fields.contains(PdxTypeTest.FIELD_4));
    }

    @Test
    public void testHasExtraFields() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.setHasDeletedField(true);
        type.addField(PdxTypeTest.FIELD_3);
        type.addField(PdxTypeTest.FIELD_2);
        type.addField(PdxTypeTest.FIELD_1);
        type.addField(PdxTypeTest.FIELD_0);
        Assert.assertFalse(type.hasExtraFields(type));
        final PdxType noDeletedField = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        noDeletedField.addField(PdxTypeTest.FIELD_2);
        noDeletedField.addField(PdxTypeTest.FIELD_1);
        noDeletedField.addField(PdxTypeTest.FIELD_0);
        Assert.assertFalse(type.hasExtraFields(noDeletedField));
        final PdxType fewerFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        fewerFields.addField(PdxTypeTest.FIELD_2);
        fewerFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.hasExtraFields(fewerFields));
        final PdxType moreFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        moreFields.addField(PdxTypeTest.FIELD_4);
        moreFields.addField(PdxTypeTest.FIELD_2);
        moreFields.addField(PdxTypeTest.FIELD_1);
        moreFields.addField(PdxTypeTest.FIELD_0);
        Assert.assertFalse(type.hasExtraFields(moreFields));
    }

    @Test
    public void testCompatible() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.compatible(type));
        Assert.assertFalse(type.compatible(null));
        final PdxType sameTypeNameAndFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.compatible(sameTypeNameAndFields));
        final PdxType sameTypeNameAndFieldsDifferentDomain = new PdxType(PdxTypeTest.TYPE_NAME, (!(PdxTypeTest.EXPECT_DOMAIN_CLASS)));
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.compatible(sameTypeNameAndFieldsDifferentDomain));
        final PdxType sameTypeNameAndDifferentFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_3);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_2);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertFalse(type.compatible(sameTypeNameAndDifferentFields));
        final PdxType sameTypeNameAndFieldsInDifferentOrder = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndFieldsInDifferentOrder.addField(PdxTypeTest.FIELD_1);
        sameTypeNameAndFieldsInDifferentOrder.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFieldsInDifferentOrder.addField(PdxTypeTest.FIELD_4);
        Assert.assertTrue(type.compatible(sameTypeNameAndFieldsInDifferentOrder));
    }

    @Test
    public void testHashCode() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertEquals(type.hashCode(), type.hashCode());
        final PdxType sameTypeNameAndFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertEquals(type.hashCode(), sameTypeNameAndFields.hashCode());
        final PdxType sameTypeNameAndFieldsDifferentDomain = new PdxType(PdxTypeTest.TYPE_NAME, (!(PdxTypeTest.EXPECT_DOMAIN_CLASS)));
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_1);
        Assert.assertEquals(type.hashCode(), sameTypeNameAndFieldsDifferentDomain.hashCode());
        final PdxType differentTypeNameAndSameFields = new PdxType(("Not " + (PdxTypeTest.TYPE_NAME)), PdxTypeTest.EXPECT_DOMAIN_CLASS);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_4);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_0);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertNotEquals(type.hashCode(), differentTypeNameAndSameFields.hashCode());
        final PdxType sameTypeNameAndDifferentFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_3);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_2);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertNotEquals(type.hashCode(), sameTypeNameAndDifferentFields.hashCode());
    }

    @Test
    public void testEquals() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.equals(type));
        Assert.assertFalse(type.equals(null));
        Assert.assertFalse(type.equals(new Object()));
        final PdxType sameTypeNameAndFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertTrue(type.equals(sameTypeNameAndFields));
        final PdxType sameTypeNameAndFieldsDifferentDomain = new PdxType(PdxTypeTest.TYPE_NAME, (!(PdxTypeTest.EXPECT_DOMAIN_CLASS)));
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_4);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_0);
        sameTypeNameAndFieldsDifferentDomain.addField(PdxTypeTest.FIELD_1);
        Assert.assertFalse(type.equals(sameTypeNameAndFieldsDifferentDomain));
        final PdxType differentTypeNameAndSameFields = new PdxType(("Not " + (PdxTypeTest.TYPE_NAME)), PdxTypeTest.EXPECT_DOMAIN_CLASS);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_4);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_0);
        differentTypeNameAndSameFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertFalse(type.equals(differentTypeNameAndSameFields));
        final PdxType sameTypeNameAndDifferentFields = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_3);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_2);
        sameTypeNameAndDifferentFields.addField(PdxTypeTest.FIELD_1);
        Assert.assertFalse(type.equals(sameTypeNameAndDifferentFields));
    }

    @Test
    public void testToFormattedString() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        final String str = type.toFormattedString();
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.TYPE_NAME));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_0.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_1.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_4.getFieldName()));
    }

    @Test
    public void testToString() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        final String str = type.toString();
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.TYPE_NAME));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_0.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_1.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_4.getFieldName()));
    }

    @Test
    public void testToStream() {
        final PdxType type = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        type.addField(PdxTypeTest.FIELD_4);
        type.addField(PdxTypeTest.FIELD_0);
        type.addField(PdxTypeTest.FIELD_1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        type.toStream(new PrintStream(byteArrayOutputStream), true);
        final String str = byteArrayOutputStream.toString();
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.TYPE_NAME));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_0.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_1.getFieldName()));
        Assert.assertNotEquals((-1), str.indexOf(PdxTypeTest.FIELD_4.getFieldName()));
    }

    @Test
    public void testToDataAndFromData() throws IOException, ClassNotFoundException {
        final PdxType before = new PdxType(PdxTypeTest.TYPE_NAME, PdxTypeTest.EXPECT_DOMAIN_CLASS);
        before.setHasDeletedField(true);
        before.addField(PdxTypeTest.FIELD_4);
        before.addField(PdxTypeTest.FIELD_3);
        before.addField(PdxTypeTest.FIELD_2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        before.toData(dataOutputStream);
        dataOutputStream.close();
        final PdxType after = new PdxType();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        after.fromData(dataInputStream);
        Assert.assertEquals(before.getVariableLengthFieldCount(), after.getVariableLengthFieldCount());
        Assert.assertEquals(before.getClassName(), after.getClassName());
        Assert.assertEquals(before.getNoDomainClass(), after.getNoDomainClass());
        Assert.assertEquals(before.getTypeId(), after.getTypeId());
        Assert.assertEquals(before.getFieldCount(), after.getFieldCount());
        Assert.assertEquals(before.getHasDeletedField(), after.getHasDeletedField());
    }
}

