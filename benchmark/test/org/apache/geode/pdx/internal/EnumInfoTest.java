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
import org.apache.geode.internal.DataSerializableFixedID;
import org.junit.Assert;
import org.junit.Test;


public class EnumInfoTest {
    enum TestEnum {

        ZERO(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4);
        private int intValue;

        TestEnum(int intValue) {
            this.intValue = intValue;
        }

        public int intValue() {
            return intValue;
        }
    }

    @Test
    public void testNoArgConstructor() {
        final EnumInfo enumInfo = new EnumInfo();
        Assert.assertNull(enumInfo.getClassName());
        Assert.assertEquals(0, enumInfo.getOrdinal());
    }

    @Test
    public void testThreeArgConstructor() {
        final EnumInfo enumInfo = new EnumInfo("clazz", "name", 37);
        Assert.assertEquals("clazz", enumInfo.getClassName());
        Assert.assertEquals(37, enumInfo.getOrdinal());
    }

    @Test
    public void testOneArgConstructor() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        Assert.assertEquals("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", enumInfo.getClassName());
        Assert.assertEquals(1, enumInfo.getOrdinal());
    }

    @Test
    public void testGetDSFID() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        Assert.assertEquals(DataSerializableFixedID.ENUM_INFO, enumInfo.getDSFID());
    }

    @Test
    public void testGetSerializationVersions() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        Assert.assertNull(enumInfo.getSerializationVersions());
    }

    @Test
    public void testHashCode() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        Assert.assertEquals(enumInfo.hashCode(), enumInfo.hashCode());
        final EnumInfo sameClazzAndSameName = new EnumInfo("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", "ONE", 1);
        Assert.assertEquals(enumInfo.hashCode(), sameClazzAndSameName.hashCode());
        final EnumInfo differentClazzAndSameName = new EnumInfo(("Not " + "org.apache.geode.pdx.internal.EnumInfoTest$TestEnum"), "ONE", 1);
        Assert.assertNotEquals(enumInfo.hashCode(), differentClazzAndSameName.hashCode());
        final EnumInfo sameClazzAndDifferentName = new EnumInfo("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", ("Not " + "ONE"), 1);
        Assert.assertNotEquals(enumInfo.hashCode(), sameClazzAndDifferentName.hashCode());
    }

    @Test
    public void testEquals() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        Assert.assertTrue(enumInfo.equals(enumInfo));
        Assert.assertFalse(enumInfo.equals(null));
        Assert.assertFalse(enumInfo.equals(new Object()));
        final EnumInfo sameClazzSameNameAndSameOrdinal = new EnumInfo("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", "ONE", 1);
        Assert.assertTrue(enumInfo.equals(sameClazzSameNameAndSameOrdinal));
        final EnumInfo differentClazzSameNameAndSameOrdinal = new EnumInfo(("Not " + "org.apache.geode.pdx.internal.EnumInfoTest$TestEnum"), "ONE", 1);
        Assert.assertFalse(enumInfo.equals(differentClazzSameNameAndSameOrdinal));
        final EnumInfo sameClazzDifferentNameAndSameOrdinal = new EnumInfo("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", ("Not " + "ONE"), 1);
        Assert.assertFalse(enumInfo.equals(sameClazzDifferentNameAndSameOrdinal));
        final EnumInfo sameClazzDifferentNameAndDifferentOrdinal = new EnumInfo("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum", ("Not " + "ONE"), (1 + 1));
        Assert.assertFalse(enumInfo.equals(sameClazzDifferentNameAndDifferentOrdinal));
    }

    @Test
    public void testToFormattedString() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        final String str = enumInfo.toFormattedString();
        Assert.assertEquals(0, str.indexOf("EnumInfo"));
        Assert.assertNotEquals((-1), str.indexOf("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum"));
        Assert.assertNotEquals((-1), str.indexOf("ONE"));
    }

    @Test
    public void testToString() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        final String str = enumInfo.toString();
        Assert.assertNotEquals((-1), str.indexOf("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum"));
        Assert.assertNotEquals((-1), str.indexOf("ONE"));
    }

    @Test
    public void testToStream() {
        final EnumInfo enumInfo = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        enumInfo.toStream(new PrintStream(byteArrayOutputStream));
        final String str = byteArrayOutputStream.toString();
        Assert.assertNotEquals((-1), str.indexOf("org.apache.geode.pdx.internal.EnumInfoTest$TestEnum"));
        Assert.assertNotEquals((-1), str.indexOf("ONE"));
    }

    @Test
    public void testToDataAndFromData() throws IOException, ClassNotFoundException {
        final EnumInfo before = new EnumInfo(EnumInfoTest.TestEnum.ONE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        before.toData(dataOutputStream);
        dataOutputStream.close();
        final EnumInfo after = new EnumInfo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        after.fromData(dataInputStream);
        Assert.assertEquals(before.getClassName(), after.getClassName());
        Assert.assertEquals(before.getOrdinal(), after.getOrdinal());
    }
}

