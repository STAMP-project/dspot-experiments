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


import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SerializationTest.class })
public class PdxInstanceImplTest {
    private PdxType emptyPdxType;

    private PdxField nonExistentField;

    private PdxType pdxType;

    private PdxField nonIdentityField;

    private PdxField booleanField;

    private PdxField doubleField;

    private PdxField intField;

    private PdxField longField;

    private PdxField objectField;

    private PdxField stringField;

    private PdxField byteArrayField;

    private PdxField objectArrayField;

    private PdxField integerArrayField;

    private PdxField shortArrayField;

    private PdxField intArrayField;

    private PdxField longArrayField;

    private PdxField charArrayField;

    private PdxField floatArrayField;

    private PdxField doubleArrayField;

    private PdxField booleanArrayField;

    private TypeRegistry pdxRegistry;

    private PdxInstance instance;

    @Test
    public void testToStringForEmpty() {
        final PdxWriterImpl writer = new PdxWriterImpl(emptyPdxType, pdxRegistry, new PdxOutputStream());
        writer.completeByteStreamGeneration();
        final PdxInstance instance = writer.makePdxInstance();
        Assert.assertEquals(((emptyPdxType.getClassName()) + "]{}"), StringUtils.substringAfter(instance.toString(), ","));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(((((((((((((((((((((((((((((((((((((((((((((((((((pdxType.getClassName()) + "]{") + (booleanArrayField.getFieldName())) + "=[false, true]") + ", ") + (booleanField.getFieldName())) + "=true") + ", ") + (byteArrayField.getFieldName())) + "=DEADBEEF") + ", ") + (charArrayField.getFieldName())) + "=[o, k]") + ", ") + (doubleArrayField.getFieldName())) + "=[3.14159, 2.71828]") + ", ") + (doubleField.getFieldName())) + "=3.1415") + ", ") + (floatArrayField.getFieldName())) + "=[3.14159, 2.71828]") + ", ") + (intArrayField.getFieldName())) + "=[37, 42]") + ", ") + (intField.getFieldName())) + "=37") + ", ") + (integerArrayField.getFieldName())) + "=[37, 42]") + ", ") + (longArrayField.getFieldName())) + "=[37, 42]") + ", ") + (longField.getFieldName())) + "=42") + ", ") + (objectArrayField.getFieldName())) + "=[Dave, Stewart]") + ", ") + (objectField.getFieldName())) + "=Dave") + ", ") + (shortArrayField.getFieldName())) + "=[37, 42]") + ", ") + (stringField.getFieldName())) + "=MOOF!") + "}"), StringUtils.substringAfter(instance.toString(), ","));
    }

    @Test
    public void testGetField() {
        Assert.assertNull(instance.getField(nonExistentField.getFieldName()));
        Assert.assertEquals(Integer.class, instance.getField(nonIdentityField.getFieldName()).getClass());
        Assert.assertEquals(Boolean.class, instance.getField(booleanField.getFieldName()).getClass());
        Assert.assertEquals(Double.class, instance.getField(doubleField.getFieldName()).getClass());
        Assert.assertEquals(Integer.class, instance.getField(intField.getFieldName()).getClass());
        Assert.assertEquals(Long.class, instance.getField(longField.getFieldName()).getClass());
        Assert.assertEquals(PdxInstanceImplTest.SerializableObject.class, instance.getField(objectField.getFieldName()).getClass());
        Assert.assertEquals(String.class, instance.getField(stringField.getFieldName()).getClass());
        Assert.assertEquals(byte[].class, instance.getField(byteArrayField.getFieldName()).getClass());
        Assert.assertEquals(Object[].class, instance.getField(objectArrayField.getFieldName()).getClass());
        Assert.assertEquals(Integer[].class, instance.getField(integerArrayField.getFieldName()).getClass());
        Assert.assertEquals(short[].class, instance.getField(shortArrayField.getFieldName()).getClass());
        Assert.assertEquals(int[].class, instance.getField(intArrayField.getFieldName()).getClass());
        Assert.assertEquals(long[].class, instance.getField(longArrayField.getFieldName()).getClass());
        Assert.assertEquals(char[].class, instance.getField(charArrayField.getFieldName()).getClass());
        Assert.assertEquals(float[].class, instance.getField(floatArrayField.getFieldName()).getClass());
        Assert.assertEquals(double[].class, instance.getField(doubleArrayField.getFieldName()).getClass());
        Assert.assertEquals(boolean[].class, instance.getField(booleanArrayField.getFieldName()).getClass());
    }

    @Test
    public void testHasField() {
        Assert.assertEquals(false, instance.hasField(nonExistentField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(nonIdentityField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(booleanField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(doubleField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(intField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(longField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(objectField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(stringField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(byteArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(objectArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(integerArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(shortArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(intArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(longArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(charArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(floatArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(doubleArrayField.getFieldName()));
        Assert.assertEquals(true, instance.hasField(booleanArrayField.getFieldName()));
    }

    @Test
    public void testIsEnum() {
        Assert.assertFalse(instance.isEnum());
    }

    @Test
    public void testGetClassName() {
        Assert.assertEquals(pdxType.getClassName(), instance.getClassName());
    }

    @Test
    public void testGetFieldNames() {
        Assert.assertEquals(Arrays.asList(nonIdentityField.getFieldName(), booleanField.getFieldName(), doubleField.getFieldName(), intField.getFieldName(), longField.getFieldName(), objectField.getFieldName(), stringField.getFieldName(), byteArrayField.getFieldName(), objectArrayField.getFieldName(), integerArrayField.getFieldName(), shortArrayField.getFieldName(), intArrayField.getFieldName(), longArrayField.getFieldName(), charArrayField.getFieldName(), floatArrayField.getFieldName(), doubleArrayField.getFieldName(), booleanArrayField.getFieldName()), instance.getFieldNames());
    }

    @Test
    public void testIsIdentityField() {
        Assert.assertEquals(false, instance.isIdentityField(nonExistentField.getFieldName()));
        Assert.assertEquals(false, instance.isIdentityField(nonIdentityField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(booleanField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(doubleField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(intField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(longField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(objectField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(stringField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(byteArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(objectArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(integerArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(shortArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(intArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(longArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(charArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(floatArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(doubleArrayField.getFieldName()));
        Assert.assertEquals(true, instance.isIdentityField(booleanArrayField.getFieldName()));
    }

    private static class SerializableObject implements Serializable {
        String name;

        public SerializableObject() {
            // Do nothing.
        }

        SerializableObject(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return StringUtils.trimToEmpty(name);
        }
    }

    private static class TestLonerTypeRegistration extends LonerTypeRegistration {
        TypeRegistration testTypeRegistration;

        TestLonerTypeRegistration(InternalCache cache, TypeRegistration testTypeRegistration) {
            super(cache);
            this.testTypeRegistration = testTypeRegistration;
        }

        @Override
        protected TypeRegistration createTypeRegistration(boolean client) {
            return testTypeRegistration;
        }
    }
}

