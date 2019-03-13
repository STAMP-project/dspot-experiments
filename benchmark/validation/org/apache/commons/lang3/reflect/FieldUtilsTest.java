/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.reflect;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.testbed.Ambig;
import org.apache.commons.lang3.reflect.testbed.Annotated;
import org.apache.commons.lang3.reflect.testbed.Foo;
import org.apache.commons.lang3.reflect.testbed.PrivatelyShadowedChild;
import org.apache.commons.lang3.reflect.testbed.PublicChild;
import org.apache.commons.lang3.reflect.testbed.PubliclyShadowedChild;
import org.apache.commons.lang3.reflect.testbed.StaticContainer;
import org.apache.commons.lang3.reflect.testbed.StaticContainerChild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests FieldUtils
 */
public class FieldUtilsTest {
    private static final String JACOCO_DATA_FIELD_NAME = "$jacocoData";

    static final Integer I0 = Integer.valueOf(0);

    static final Integer I1 = Integer.valueOf(1);

    static final Double D0 = Double.valueOf(0.0);

    static final Double D1 = Double.valueOf(1.0);

    @Annotated
    private PublicChild publicChild;

    private PubliclyShadowedChild publiclyShadowedChild;

    @Annotated
    private PrivatelyShadowedChild privatelyShadowedChild;

    private final Class<? super PublicChild> parentClass = PublicChild.class.getSuperclass();

    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new FieldUtils());
        final Constructor<?>[] cons = FieldUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(FieldUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(FieldUtils.class.getModifiers()));
    }

    @Test
    public void testGetField() {
        Assertions.assertEquals(Foo.class, FieldUtils.getField(PublicChild.class, "VALUE").getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PublicChild.class, "s").getDeclaringClass());
        Assertions.assertNull(FieldUtils.getField(PublicChild.class, "b"));
        Assertions.assertNull(FieldUtils.getField(PublicChild.class, "i"));
        Assertions.assertNull(FieldUtils.getField(PublicChild.class, "d"));
        Assertions.assertEquals(Foo.class, FieldUtils.getField(PubliclyShadowedChild.class, "VALUE").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "s").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "b").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "i").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "d").getDeclaringClass());
        Assertions.assertEquals(Foo.class, FieldUtils.getField(PrivatelyShadowedChild.class, "VALUE").getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PrivatelyShadowedChild.class, "s").getDeclaringClass());
        Assertions.assertNull(FieldUtils.getField(PrivatelyShadowedChild.class, "b"));
        Assertions.assertNull(FieldUtils.getField(PrivatelyShadowedChild.class, "i"));
        Assertions.assertNull(FieldUtils.getField(PrivatelyShadowedChild.class, "d"));
    }

    @Test
    public void testGetFieldIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(null, "none"));
    }

    @Test
    public void testGetFieldIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, null));
    }

    @Test
    public void testGetFieldIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, ""));
    }

    @Test
    public void testGetFieldIllegalArgumentException4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, " "));
    }

    @Test
    public void testGetFieldForceAccess() {
        Assertions.assertEquals(PublicChild.class, FieldUtils.getField(PublicChild.class, "VALUE", true).getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PublicChild.class, "s", true).getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PublicChild.class, "b", true).getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PublicChild.class, "i", true).getDeclaringClass());
        Assertions.assertEquals(parentClass, FieldUtils.getField(PublicChild.class, "d", true).getDeclaringClass());
        Assertions.assertEquals(Foo.class, FieldUtils.getField(PubliclyShadowedChild.class, "VALUE", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "s", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "b", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "i", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getField(PubliclyShadowedChild.class, "d", true).getDeclaringClass());
        Assertions.assertEquals(Foo.class, FieldUtils.getField(PrivatelyShadowedChild.class, "VALUE", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getField(PrivatelyShadowedChild.class, "s", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getField(PrivatelyShadowedChild.class, "b", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getField(PrivatelyShadowedChild.class, "i", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getField(PrivatelyShadowedChild.class, "d", true).getDeclaringClass());
    }

    @Test
    public void testGetFieldForceAccessIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(null, "none", true));
    }

    @Test
    public void testGetFieldForceAccessIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, null, true));
    }

    @Test
    public void testGetFieldForceAccessIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, "", true));
    }

    @Test
    public void testGetFieldForceAccessIllegalArgumentException4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(PublicChild.class, " ", true));
    }

    @Test
    public void testGetAllFields() {
        Assertions.assertArrayEquals(new Field[0], FieldUtils.getAllFields(Object.class));
        final Field[] fieldsNumber = Number.class.getDeclaredFields();
        Assertions.assertArrayEquals(fieldsNumber, FieldUtils.getAllFields(Number.class));
        final Field[] fieldsInteger = Integer.class.getDeclaredFields();
        Assertions.assertArrayEquals(ArrayUtils.addAll(fieldsInteger, fieldsNumber), FieldUtils.getAllFields(Integer.class));
        final Field[] allFields = FieldUtils.getAllFields(PublicChild.class);
        // Under Jacoco,0.8.1 and Java 10, the field count is 7.
        int expected = 5;
        for (Field field : allFields) {
            if (field.getName().equals(FieldUtilsTest.JACOCO_DATA_FIELD_NAME)) {
                expected++;
            }
        }
        Assertions.assertEquals(expected, allFields.length, Arrays.toString(allFields));
    }

    @Test
    public void testGetAllFieldsList() {
        Assertions.assertEquals(0, FieldUtils.getAllFieldsList(Object.class).size());
        final List<Field> fieldsNumber = Arrays.asList(Number.class.getDeclaredFields());
        Assertions.assertEquals(fieldsNumber, FieldUtils.getAllFieldsList(Number.class));
        final List<Field> fieldsInteger = Arrays.asList(Integer.class.getDeclaredFields());
        final List<Field> allFieldsInteger = new ArrayList<>(fieldsInteger);
        allFieldsInteger.addAll(fieldsNumber);
        Assertions.assertEquals(allFieldsInteger, FieldUtils.getAllFieldsList(Integer.class));
        final List<Field> allFields = FieldUtils.getAllFieldsList(PublicChild.class);
        // Under Jacoco,0.8.1 and Java 10, the field count is 7.
        int expected = 5;
        for (Field field : allFields) {
            if (field.getName().equals(FieldUtilsTest.JACOCO_DATA_FIELD_NAME)) {
                expected++;
            }
        }
        Assertions.assertEquals(expected, allFields.size(), allFields.toString());
    }

    @Test
    public void testGetFieldsWithAnnotation() throws NoSuchFieldException {
        Assertions.assertArrayEquals(new Field[0], FieldUtils.getFieldsWithAnnotation(Object.class, Annotated.class));
        final Field[] annotatedFields = new Field[]{ FieldUtilsTest.class.getDeclaredField("publicChild"), FieldUtilsTest.class.getDeclaredField("privatelyShadowedChild") };
        Assertions.assertArrayEquals(annotatedFields, FieldUtils.getFieldsWithAnnotation(FieldUtilsTest.class, Annotated.class));
    }

    @Test
    public void testGetFieldsWithAnnotationIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsWithAnnotation(FieldUtilsTest.class, null));
    }

    @Test
    public void testGetFieldsWithAnnotationIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsWithAnnotation(null, Annotated.class));
    }

    @Test
    public void testGetFieldsWithAnnotationIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsWithAnnotation(null, null));
    }

    @Test
    public void testGetFieldsListWithAnnotation() throws NoSuchFieldException {
        Assertions.assertEquals(0, FieldUtils.getFieldsListWithAnnotation(Object.class, Annotated.class).size());
        final List<Field> annotatedFields = Arrays.asList(FieldUtilsTest.class.getDeclaredField("publicChild"), FieldUtilsTest.class.getDeclaredField("privatelyShadowedChild"));
        final List<Field> fieldUtilsTestAnnotatedFields = FieldUtils.getFieldsListWithAnnotation(FieldUtilsTest.class, Annotated.class);
        Assertions.assertEquals(annotatedFields.size(), fieldUtilsTestAnnotatedFields.size());
        Assertions.assertTrue(fieldUtilsTestAnnotatedFields.contains(annotatedFields.get(0)));
        Assertions.assertTrue(fieldUtilsTestAnnotatedFields.contains(annotatedFields.get(1)));
    }

    @Test
    public void testGetFieldsListWithAnnotationIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsListWithAnnotation(FieldUtilsTest.class, null));
    }

    @Test
    public void testGetFieldsListWithAnnotationIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsListWithAnnotation(null, Annotated.class));
    }

    @Test
    public void testGetFieldsListWithAnnotationIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getFieldsListWithAnnotation(null, null));
    }

    @Test
    public void testGetDeclaredField() {
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "VALUE"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "s"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "b"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "i"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "d"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "VALUE"));
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "s").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "b").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "i").getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "d").getDeclaringClass());
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "VALUE"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "s"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "b"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "i"));
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "d"));
    }

    @Test
    public void testGetDeclaredFieldAccessIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(null, "none"));
    }

    @Test
    public void testGetDeclaredFieldAccessIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, null));
    }

    @Test
    public void testGetDeclaredFieldAccessIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, ""));
    }

    @Test
    public void testGetDeclaredFieldAccessIllegalArgumentException4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, " "));
    }

    @Test
    public void testGetDeclaredFieldForceAccess() {
        Assertions.assertEquals(PublicChild.class, FieldUtils.getDeclaredField(PublicChild.class, "VALUE", true).getDeclaringClass());
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "s", true));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "b", true));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "i", true));
        Assertions.assertNull(FieldUtils.getDeclaredField(PublicChild.class, "d", true));
        Assertions.assertNull(FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "VALUE", true));
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "s", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "b", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "i", true).getDeclaringClass());
        Assertions.assertEquals(PubliclyShadowedChild.class, FieldUtils.getDeclaredField(PubliclyShadowedChild.class, "d", true).getDeclaringClass());
        Assertions.assertNull(FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "VALUE", true));
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "s", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "b", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "i", true).getDeclaringClass());
        Assertions.assertEquals(PrivatelyShadowedChild.class, FieldUtils.getDeclaredField(PrivatelyShadowedChild.class, "d", true).getDeclaringClass());
    }

    @Test
    public void testGetDeclaredFieldForceAccessIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(null, "none", true));
    }

    @Test
    public void testGetDeclaredFieldForceAccessIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, null, true));
    }

    @Test
    public void testGetDeclaredFieldForceAccessIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, "", true));
    }

    @Test
    public void testGetDeclaredFieldForceAccessIllegalArgumentException4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getDeclaredField(PublicChild.class, " ", true));
    }

    @Test
    public void testReadStaticField() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(FieldUtils.getField(Foo.class, "VALUE")));
    }

    @Test
    public void testReadStaticFieldIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(null));
    }

    @Test
    public void testReadStaticFieldIllegalArgumentException2() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(FieldUtils.getField(Foo.class, "VALUE")));
        final Field nonStaticField = FieldUtils.getField(PublicChild.class, "s");
        Assumptions.assumeTrue((nonStaticField != null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(nonStaticField));
    }

    @Test
    public void testReadStaticFieldForceAccess() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(FieldUtils.getField(Foo.class, "VALUE")));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(FieldUtils.getField(PublicChild.class, "VALUE")));
    }

    @Test
    public void testReadStaticFieldForceAccessIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(null, true));
    }

    @Test
    public void testReadStaticFieldForceAccessIllegalArgumentException2() {
        final Field nonStaticField = FieldUtils.getField(PublicChild.class, "s", true);
        Assumptions.assumeTrue((nonStaticField != null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(nonStaticField));
    }

    @Test
    public void testReadNamedStaticField() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(Foo.class, "VALUE"));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(PubliclyShadowedChild.class, "VALUE"));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(PrivatelyShadowedChild.class, "VALUE"));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(PublicChild.class, "VALUE"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(null, "none"), "null class should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, null), "null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, ""), "empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, " "), "blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, "does_not_exist"), "a field that doesn't exist should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(PublicChild.class, "s"), "non-static field should cause an IllegalArgumentException");
    }

    @Test
    public void testReadNamedStaticFieldForceAccess() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(Foo.class, "VALUE", true));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(PubliclyShadowedChild.class, "VALUE", true));
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readStaticField(PrivatelyShadowedChild.class, "VALUE", true));
        Assertions.assertEquals("child", FieldUtils.readStaticField(PublicChild.class, "VALUE", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(null, "none", true), "null class should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, null, true), "null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, "", true), "empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, " ", true), "blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(Foo.class, "does_not_exist", true), "a field that doesn't exist should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readStaticField(PublicChild.class, "s", false), "non-static field should cause an IllegalArgumentException");
    }

    @Test
    public void testReadDeclaredNamedStaticField() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readDeclaredStaticField(Foo.class, "VALUE"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredStaticField(PublicChild.class, "VALUE"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredStaticField(PubliclyShadowedChild.class, "VALUE"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredStaticField(PrivatelyShadowedChild.class, "VALUE"));
    }

    @Test
    public void testReadDeclaredNamedStaticFieldForceAccess() throws Exception {
        Assertions.assertEquals(Foo.VALUE, FieldUtils.readDeclaredStaticField(Foo.class, "VALUE", true));
        Assertions.assertEquals("child", FieldUtils.readDeclaredStaticField(PublicChild.class, "VALUE", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredStaticField(PubliclyShadowedChild.class, "VALUE", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredStaticField(PrivatelyShadowedChild.class, "VALUE", true));
    }

    @Test
    public void testReadField() throws Exception {
        final Field parentS = FieldUtils.getDeclaredField(parentClass, "s");
        Assertions.assertEquals("s", FieldUtils.readField(parentS, publicChild));
        Assertions.assertEquals("s", FieldUtils.readField(parentS, publiclyShadowedChild));
        Assertions.assertEquals("s", FieldUtils.readField(parentS, privatelyShadowedChild));
        final Field parentB = FieldUtils.getDeclaredField(parentClass, "b", true);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, publicChild));
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, publiclyShadowedChild));
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, privatelyShadowedChild));
        final Field parentI = FieldUtils.getDeclaredField(parentClass, "i", true);
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, publicChild));
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, publiclyShadowedChild));
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, privatelyShadowedChild));
        final Field parentD = FieldUtils.getDeclaredField(parentClass, "d", true);
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, publicChild));
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, publiclyShadowedChild));
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, privatelyShadowedChild));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(null, publicChild), "a null field should cause an IllegalArgumentException");
    }

    @Test
    public void testReadFieldForceAccess() throws Exception {
        final Field parentS = FieldUtils.getDeclaredField(parentClass, "s");
        parentS.setAccessible(false);
        Assertions.assertEquals("s", FieldUtils.readField(parentS, publicChild, true));
        Assertions.assertEquals("s", FieldUtils.readField(parentS, publiclyShadowedChild, true));
        Assertions.assertEquals("s", FieldUtils.readField(parentS, privatelyShadowedChild, true));
        final Field parentB = FieldUtils.getDeclaredField(parentClass, "b", true);
        parentB.setAccessible(false);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, publicChild, true));
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, publiclyShadowedChild, true));
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(parentB, privatelyShadowedChild, true));
        final Field parentI = FieldUtils.getDeclaredField(parentClass, "i", true);
        parentI.setAccessible(false);
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, publicChild, true));
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, publiclyShadowedChild, true));
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(parentI, privatelyShadowedChild, true));
        final Field parentD = FieldUtils.getDeclaredField(parentClass, "d", true);
        parentD.setAccessible(false);
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, publicChild, true));
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, publiclyShadowedChild, true));
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(parentD, privatelyShadowedChild, true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(null, publicChild, true), "a null field should cause an IllegalArgumentException");
    }

    @Test
    public void testReadNamedField() throws Exception {
        Assertions.assertEquals("s", FieldUtils.readField(publicChild, "s"));
        Assertions.assertEquals("ss", FieldUtils.readField(publiclyShadowedChild, "s"));
        Assertions.assertEquals("s", FieldUtils.readField(privatelyShadowedChild, "s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, null), "a null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, ""), "an empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, " "), "a blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(((Object) (null)), "none"), "a null target should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, "b"));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readField(publiclyShadowedChild, "b"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(privatelyShadowedChild, "b"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, "i"));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readField(publiclyShadowedChild, "i"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(privatelyShadowedChild, "i"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, "d"));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readField(publiclyShadowedChild, "d"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(privatelyShadowedChild, "d"));
    }

    @Test
    public void testReadNamedFieldForceAccess() throws Exception {
        Assertions.assertEquals("s", FieldUtils.readField(publicChild, "s", true));
        Assertions.assertEquals("ss", FieldUtils.readField(publiclyShadowedChild, "s", true));
        Assertions.assertEquals("ss", FieldUtils.readField(privatelyShadowedChild, "s", true));
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(publicChild, "b", true));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readField(publiclyShadowedChild, "b", true));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readField(privatelyShadowedChild, "b", true));
        Assertions.assertEquals(FieldUtilsTest.I0, FieldUtils.readField(publicChild, "i", true));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readField(publiclyShadowedChild, "i", true));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readField(privatelyShadowedChild, "i", true));
        Assertions.assertEquals(FieldUtilsTest.D0, FieldUtils.readField(publicChild, "d", true));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readField(publiclyShadowedChild, "d", true));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readField(privatelyShadowedChild, "d", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, null, true), "a null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, "", true), "an empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(publicChild, " ", true), "a blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readField(((Object) (null)), "none", true), "a null target should cause an IllegalArgumentException");
    }

    @Test
    public void testReadDeclaredNamedField() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, null), "a null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, ""), "an empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, " "), "a blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(null, "none"), "a null target should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "s"));
        Assertions.assertEquals("ss", FieldUtils.readDeclaredField(publiclyShadowedChild, "s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(privatelyShadowedChild, "s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "b"));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readDeclaredField(publiclyShadowedChild, "b"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(privatelyShadowedChild, "b"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "i"));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readDeclaredField(publiclyShadowedChild, "i"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(privatelyShadowedChild, "i"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "d"));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readDeclaredField(publiclyShadowedChild, "d"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(privatelyShadowedChild, "d"));
    }

    @Test
    public void testReadDeclaredNamedFieldForceAccess() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, null, true), "a null field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "", true), "an empty field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, " ", true), "a blank field name should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(null, "none", true), "a null target should cause an IllegalArgumentException");
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "s", true));
        Assertions.assertEquals("ss", FieldUtils.readDeclaredField(publiclyShadowedChild, "s", true));
        Assertions.assertEquals("ss", FieldUtils.readDeclaredField(privatelyShadowedChild, "s", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "b", true));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readDeclaredField(publiclyShadowedChild, "b", true));
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readDeclaredField(privatelyShadowedChild, "b", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "i", true));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readDeclaredField(publiclyShadowedChild, "i", true));
        Assertions.assertEquals(FieldUtilsTest.I1, FieldUtils.readDeclaredField(privatelyShadowedChild, "i", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.readDeclaredField(publicChild, "d", true));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readDeclaredField(publiclyShadowedChild, "d", true));
        Assertions.assertEquals(FieldUtilsTest.D1, FieldUtils.readDeclaredField(privatelyShadowedChild, "d", true));
    }

    @Test
    public void testWriteStaticField() throws Exception {
        Field field = StaticContainer.class.getDeclaredField("mutablePublic");
        FieldUtils.writeStaticField(field, "new");
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("mutableProtected"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("mutablePackage"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("mutablePrivate"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PUBLIC"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PROTECTED"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PACKAGE"), "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PRIVATE"), "new"));
    }

    @Test
    public void testWriteStaticFieldForceAccess() throws Exception {
        Field field = StaticContainer.class.getDeclaredField("mutablePublic");
        FieldUtils.writeStaticField(field, "new", true);
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        field = StaticContainer.class.getDeclaredField("mutableProtected");
        FieldUtils.writeStaticField(field, "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutableProtected());
        field = StaticContainer.class.getDeclaredField("mutablePackage");
        FieldUtils.writeStaticField(field, "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePackage());
        field = StaticContainer.class.getDeclaredField("mutablePrivate");
        FieldUtils.writeStaticField(field, "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePrivate());
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PUBLIC"), "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PROTECTED"), "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PACKAGE"), "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainer.class.getDeclaredField("IMMUTABLE_PRIVATE"), "new", true));
    }

    @Test
    public void testWriteNamedStaticField() throws Exception {
        FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePublic", "new");
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "mutableProtected", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePackage", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePrivate", "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PUBLIC", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PROTECTED", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PACKAGE", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PRIVATE", "new"));
    }

    @Test
    public void testWriteNamedStaticFieldForceAccess() throws Exception {
        FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePublic", "new", true);
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        FieldUtils.writeStaticField(StaticContainerChild.class, "mutableProtected", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutableProtected());
        FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePackage", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePackage());
        FieldUtils.writeStaticField(StaticContainerChild.class, "mutablePrivate", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePrivate());
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PUBLIC", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PROTECTED", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PACKAGE", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeStaticField(StaticContainerChild.class, "IMMUTABLE_PRIVATE", "new", true));
    }

    @Test
    public void testWriteDeclaredNamedStaticField() throws Exception {
        FieldUtils.writeStaticField(StaticContainer.class, "mutablePublic", "new");
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutableProtected", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutablePackage", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutablePrivate", "new"));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PUBLIC", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PROTECTED", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PACKAGE", "new"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PRIVATE", "new"));
    }

    @Test
    public void testWriteDeclaredNamedStaticFieldForceAccess() throws Exception {
        FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutablePublic", "new", true);
        Assertions.assertEquals("new", StaticContainer.mutablePublic);
        FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutableProtected", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutableProtected());
        FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutablePackage", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePackage());
        FieldUtils.writeDeclaredStaticField(StaticContainer.class, "mutablePrivate", "new", true);
        Assertions.assertEquals("new", StaticContainer.getMutablePrivate());
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PUBLIC", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PROTECTED", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PACKAGE", "new", true));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeDeclaredStaticField(StaticContainer.class, "IMMUTABLE_PRIVATE", "new", true));
    }

    @Test
    public void testWriteField() throws Exception {
        Field field = parentClass.getDeclaredField("s");
        FieldUtils.writeField(field, publicChild, "S");
        Assertions.assertEquals("S", field.get(publicChild));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeField(parentClass.getDeclaredField("b"), publicChild, Boolean.TRUE));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeField(parentClass.getDeclaredField("i"), publicChild, Integer.valueOf(Integer.MAX_VALUE)));
        Assertions.assertThrows(IllegalAccessException.class, () -> FieldUtils.writeField(parentClass.getDeclaredField("d"), publicChild, Double.valueOf(Double.MAX_VALUE)));
    }

    @Test
    public void testWriteFieldForceAccess() throws Exception {
        Field field = parentClass.getDeclaredField("s");
        FieldUtils.writeField(field, publicChild, "S", true);
        Assertions.assertEquals("S", field.get(publicChild));
        field = parentClass.getDeclaredField("b");
        FieldUtils.writeField(field, publicChild, Boolean.TRUE, true);
        Assertions.assertEquals(Boolean.TRUE, field.get(publicChild));
        field = parentClass.getDeclaredField("i");
        FieldUtils.writeField(field, publicChild, Integer.valueOf(Integer.MAX_VALUE), true);
        Assertions.assertEquals(Integer.valueOf(Integer.MAX_VALUE), field.get(publicChild));
        field = parentClass.getDeclaredField("d");
        FieldUtils.writeField(field, publicChild, Double.valueOf(Double.MAX_VALUE), true);
        Assertions.assertEquals(Double.valueOf(Double.MAX_VALUE), field.get(publicChild));
    }

    @Test
    public void testWriteNamedField() throws Exception {
        FieldUtils.writeField(publicChild, "s", "S");
        Assertions.assertEquals("S", FieldUtils.readField(publicChild, "s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(publicChild, "b", Boolean.TRUE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(publicChild, "i", Integer.valueOf(1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(publicChild, "d", Double.valueOf(1.0)));
        FieldUtils.writeField(publiclyShadowedChild, "s", "S");
        Assertions.assertEquals("S", FieldUtils.readField(publiclyShadowedChild, "s"));
        FieldUtils.writeField(publiclyShadowedChild, "b", Boolean.FALSE);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(publiclyShadowedChild, "b"));
        FieldUtils.writeField(publiclyShadowedChild, "i", Integer.valueOf(0));
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readField(publiclyShadowedChild, "i"));
        FieldUtils.writeField(publiclyShadowedChild, "d", Double.valueOf(0.0));
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readField(publiclyShadowedChild, "d"));
        FieldUtils.writeField(privatelyShadowedChild, "s", "S");
        Assertions.assertEquals("S", FieldUtils.readField(privatelyShadowedChild, "s"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(privatelyShadowedChild, "b", Boolean.TRUE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(privatelyShadowedChild, "i", Integer.valueOf(1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeField(privatelyShadowedChild, "d", Double.valueOf(1.0)));
    }

    @Test
    public void testWriteNamedFieldForceAccess() throws Exception {
        FieldUtils.writeField(publicChild, "s", "S", true);
        Assertions.assertEquals("S", FieldUtils.readField(publicChild, "s", true));
        FieldUtils.writeField(publicChild, "b", Boolean.TRUE, true);
        Assertions.assertEquals(Boolean.TRUE, FieldUtils.readField(publicChild, "b", true));
        FieldUtils.writeField(publicChild, "i", Integer.valueOf(1), true);
        Assertions.assertEquals(Integer.valueOf(1), FieldUtils.readField(publicChild, "i", true));
        FieldUtils.writeField(publicChild, "d", Double.valueOf(1.0), true);
        Assertions.assertEquals(Double.valueOf(1.0), FieldUtils.readField(publicChild, "d", true));
        FieldUtils.writeField(publiclyShadowedChild, "s", "S", true);
        Assertions.assertEquals("S", FieldUtils.readField(publiclyShadowedChild, "s", true));
        FieldUtils.writeField(publiclyShadowedChild, "b", Boolean.FALSE, true);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(publiclyShadowedChild, "b", true));
        FieldUtils.writeField(publiclyShadowedChild, "i", Integer.valueOf(0), true);
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readField(publiclyShadowedChild, "i", true));
        FieldUtils.writeField(publiclyShadowedChild, "d", Double.valueOf(0.0), true);
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readField(publiclyShadowedChild, "d", true));
        FieldUtils.writeField(privatelyShadowedChild, "s", "S", true);
        Assertions.assertEquals("S", FieldUtils.readField(privatelyShadowedChild, "s", true));
        FieldUtils.writeField(privatelyShadowedChild, "b", Boolean.FALSE, true);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readField(privatelyShadowedChild, "b", true));
        FieldUtils.writeField(privatelyShadowedChild, "i", Integer.valueOf(0), true);
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readField(privatelyShadowedChild, "i", true));
        FieldUtils.writeField(privatelyShadowedChild, "d", Double.valueOf(0.0), true);
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readField(privatelyShadowedChild, "d", true));
    }

    @Test
    public void testWriteDeclaredNamedField() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "s", "S"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "b", Boolean.TRUE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "i", Integer.valueOf(1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "d", Double.valueOf(1.0)));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "s", "S");
        Assertions.assertEquals("S", FieldUtils.readDeclaredField(publiclyShadowedChild, "s"));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "b", Boolean.FALSE);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readDeclaredField(publiclyShadowedChild, "b"));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "i", Integer.valueOf(0));
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readDeclaredField(publiclyShadowedChild, "i"));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "d", Double.valueOf(0.0));
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readDeclaredField(publiclyShadowedChild, "d"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(privatelyShadowedChild, "s", "S"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(privatelyShadowedChild, "b", Boolean.TRUE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(privatelyShadowedChild, "i", Integer.valueOf(1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(privatelyShadowedChild, "d", Double.valueOf(1.0)));
    }

    @Test
    public void testWriteDeclaredNamedFieldForceAccess() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "s", "S", true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "b", Boolean.TRUE, true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "i", Integer.valueOf(1), true));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.writeDeclaredField(publicChild, "d", Double.valueOf(1.0), true));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "s", "S", true);
        Assertions.assertEquals("S", FieldUtils.readDeclaredField(publiclyShadowedChild, "s", true));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "b", Boolean.FALSE, true);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readDeclaredField(publiclyShadowedChild, "b", true));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "i", Integer.valueOf(0), true);
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readDeclaredField(publiclyShadowedChild, "i", true));
        FieldUtils.writeDeclaredField(publiclyShadowedChild, "d", Double.valueOf(0.0), true);
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readDeclaredField(publiclyShadowedChild, "d", true));
        FieldUtils.writeDeclaredField(privatelyShadowedChild, "s", "S", true);
        Assertions.assertEquals("S", FieldUtils.readDeclaredField(privatelyShadowedChild, "s", true));
        FieldUtils.writeDeclaredField(privatelyShadowedChild, "b", Boolean.FALSE, true);
        Assertions.assertEquals(Boolean.FALSE, FieldUtils.readDeclaredField(privatelyShadowedChild, "b", true));
        FieldUtils.writeDeclaredField(privatelyShadowedChild, "i", Integer.valueOf(0), true);
        Assertions.assertEquals(Integer.valueOf(0), FieldUtils.readDeclaredField(privatelyShadowedChild, "i", true));
        FieldUtils.writeDeclaredField(privatelyShadowedChild, "d", Double.valueOf(0.0), true);
        Assertions.assertEquals(Double.valueOf(0.0), FieldUtils.readDeclaredField(privatelyShadowedChild, "d", true));
    }

    @Test
    public void testAmbig() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(Ambig.class, "VALUE"));
    }

    @Test
    public void testRemoveFinalModifier() throws Exception {
        final Field field = StaticContainer.class.getDeclaredField("IMMUTABLE_PRIVATE_2");
        Assertions.assertFalse(field.isAccessible());
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        FieldUtils.removeFinalModifier(field);
        // The field is no longer final
        Assertions.assertFalse(Modifier.isFinal(field.getModifiers()));
        Assertions.assertFalse(field.isAccessible());
    }

    @Test
    public void testRemoveFinalModifierWithAccess() throws Exception {
        final Field field = StaticContainer.class.getDeclaredField("IMMUTABLE_PRIVATE_2");
        Assertions.assertFalse(field.isAccessible());
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        FieldUtils.removeFinalModifier(field, true);
        // The field is no longer final
        Assertions.assertFalse(Modifier.isFinal(field.getModifiers()));
        Assertions.assertFalse(field.isAccessible());
    }

    @Test
    public void testRemoveFinalModifierWithoutAccess() throws Exception {
        final Field field = StaticContainer.class.getDeclaredField("IMMUTABLE_PRIVATE_2");
        Assertions.assertFalse(field.isAccessible());
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        FieldUtils.removeFinalModifier(field, false);
        // The field is STILL final because we did not force access
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        Assertions.assertFalse(field.isAccessible());
    }

    @Test
    public void testRemoveFinalModifierAccessNotNeeded() throws Exception {
        final Field field = StaticContainer.class.getDeclaredField("IMMUTABLE_PACKAGE");
        Assertions.assertFalse(field.isAccessible());
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        FieldUtils.removeFinalModifier(field, false);
        // The field is no longer final AND we did not need to force access
        Assertions.assertTrue(Modifier.isFinal(field.getModifiers()));
        Assertions.assertFalse(field.isAccessible());
    }
}

