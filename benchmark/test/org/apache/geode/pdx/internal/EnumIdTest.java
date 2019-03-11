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
import org.apache.geode.internal.DataSerializableFixedID;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SerializationTest.class)
public class EnumIdTest {
    static final int ID = -559038737;

    @Test
    public void testNoArgConstructor() {
        final EnumId enumId = new EnumId();
        Assert.assertEquals(0, enumId.intValue());
    }

    @Test
    public void testSingleArgConstructor() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(EnumIdTest.ID, enumId.intValue());
    }

    @Test
    public void testGetDSFID() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(DataSerializableFixedID.ENUM_ID, enumId.getDSFID());
    }

    @Test
    public void testGetDSID() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(222, enumId.getDSId());
    }

    @Test
    public void testEnumNum() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(11386607, enumId.getEnumNum());
    }

    @Test
    public void testGetSerializationVersions() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertNull(enumId.getSerializationVersions());
    }

    @Test
    public void testHashCode() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(enumId.hashCode(), enumId.hashCode());
        final EnumId sameId = new EnumId(EnumIdTest.ID);
        Assert.assertEquals(enumId.hashCode(), sameId.hashCode());
        final EnumId differentId = new EnumId(((EnumIdTest.ID) + 1));
        Assert.assertNotEquals(enumId.hashCode(), differentId.hashCode());
    }

    @Test
    public void testEquals() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        Assert.assertTrue(enumId.equals(enumId));
        Assert.assertFalse(enumId.equals(null));
        Assert.assertFalse(enumId.equals(new Object()));
        final EnumId sameId = new EnumId(EnumIdTest.ID);
        Assert.assertTrue(enumId.equals(sameId));
        final EnumId differentId = new EnumId(((EnumIdTest.ID) + 1));
        Assert.assertFalse(enumId.equals(differentId));
    }

    @Test
    public void testToString() {
        final EnumId enumId = new EnumId(EnumIdTest.ID);
        final String str = enumId.toString();
        Assert.assertNotEquals((-1), str.indexOf(Integer.toString(222)));
        Assert.assertNotEquals((-1), str.indexOf(Integer.toString(11386607)));
    }

    @Test
    public void testToDataAndFromData() throws IOException, ClassNotFoundException {
        final EnumId before = new EnumId(EnumIdTest.ID);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        before.toData(dataOutputStream);
        dataOutputStream.close();
        final EnumId after = new EnumId();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        after.fromData(dataInputStream);
        Assert.assertEquals(before.intValue(), after.intValue());
    }
}

