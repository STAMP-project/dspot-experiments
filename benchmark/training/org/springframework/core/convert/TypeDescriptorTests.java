/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.convert;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.util.MultiValueMap;


/**
 * Tests for {@link TypeDescriptor}.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @author Phillip Webb
 * @author Sam Brannen
 * @author Nathan Piper
 */
@SuppressWarnings("rawtypes")
public class TypeDescriptorTests {
    @Test
    public void parameterPrimitive() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(new MethodParameter(getClass().getMethod("testParameterPrimitive", int.class), 0));
        Assert.assertEquals(int.class, desc.getType());
        Assert.assertEquals(Integer.class, desc.getObjectType());
        Assert.assertEquals("int", desc.getName());
        Assert.assertEquals("int", desc.toString());
        Assert.assertTrue(desc.isPrimitive());
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void parameterScalar() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(new MethodParameter(getClass().getMethod("testParameterScalar", String.class), 0));
        Assert.assertEquals(String.class, desc.getType());
        Assert.assertEquals(String.class, desc.getObjectType());
        Assert.assertEquals("java.lang.String", desc.getName());
        Assert.assertEquals("java.lang.String", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void parameterList() throws Exception {
        MethodParameter methodParameter = new MethodParameter(getClass().getMethod("testParameterList", List.class), 0);
        TypeDescriptor desc = new TypeDescriptor(methodParameter);
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(List.class, desc.getObjectType());
        Assert.assertEquals("java.util.List", desc.getName());
        Assert.assertEquals("java.util.List<java.util.List<java.util.Map<java.lang.Integer, java.lang.Enum<?>>>>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertTrue(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertEquals(List.class, desc.getElementTypeDescriptor().getType());
        Assert.assertEquals(TypeDescriptor.nested(methodParameter, 1), desc.getElementTypeDescriptor());
        Assert.assertEquals(TypeDescriptor.nested(methodParameter, 2), desc.getElementTypeDescriptor().getElementTypeDescriptor());
        Assert.assertEquals(TypeDescriptor.nested(methodParameter, 3), desc.getElementTypeDescriptor().getElementTypeDescriptor().getMapValueTypeDescriptor());
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getElementTypeDescriptor().getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(Enum.class, desc.getElementTypeDescriptor().getElementTypeDescriptor().getMapValueTypeDescriptor().getType());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void parameterListNoParamTypes() throws Exception {
        MethodParameter methodParameter = new MethodParameter(getClass().getMethod("testParameterListNoParamTypes", List.class), 0);
        TypeDescriptor desc = new TypeDescriptor(methodParameter);
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(List.class, desc.getObjectType());
        Assert.assertEquals("java.util.List", desc.getName());
        Assert.assertEquals("java.util.List<?>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertTrue(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertNull(desc.getElementTypeDescriptor());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void parameterArray() throws Exception {
        MethodParameter methodParameter = new MethodParameter(getClass().getMethod("testParameterArray", Integer[].class), 0);
        TypeDescriptor desc = new TypeDescriptor(methodParameter);
        Assert.assertEquals(Integer[].class, desc.getType());
        Assert.assertEquals(Integer[].class, desc.getObjectType());
        Assert.assertEquals("java.lang.Integer[]", desc.getName());
        Assert.assertEquals("java.lang.Integer[]", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertTrue(desc.isArray());
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertEquals(TypeDescriptor.valueOf(Integer.class), desc.getElementTypeDescriptor());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void parameterMap() throws Exception {
        MethodParameter methodParameter = new MethodParameter(getClass().getMethod("testParameterMap", Map.class), 0);
        TypeDescriptor desc = new TypeDescriptor(methodParameter);
        Assert.assertEquals(Map.class, desc.getType());
        Assert.assertEquals(Map.class, desc.getObjectType());
        Assert.assertEquals("java.util.Map", desc.getName());
        Assert.assertEquals("java.util.Map<java.lang.Integer, java.util.List<java.lang.String>>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertTrue(desc.isMap());
        Assert.assertEquals(TypeDescriptor.nested(methodParameter, 1), desc.getMapValueTypeDescriptor());
        Assert.assertEquals(TypeDescriptor.nested(methodParameter, 2), desc.getMapValueTypeDescriptor().getElementTypeDescriptor());
        Assert.assertEquals(Integer.class, desc.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(List.class, desc.getMapValueTypeDescriptor().getType());
        Assert.assertEquals(String.class, desc.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
    }

    @Test
    public void parameterAnnotated() throws Exception {
        TypeDescriptor t1 = new TypeDescriptor(new MethodParameter(getClass().getMethod("testAnnotatedMethod", String.class), 0));
        Assert.assertEquals(String.class, t1.getType());
        Assert.assertEquals(1, t1.getAnnotations().length);
        Assert.assertNotNull(t1.getAnnotation(TypeDescriptorTests.ParameterAnnotation.class));
        Assert.assertTrue(t1.hasAnnotation(TypeDescriptorTests.ParameterAnnotation.class));
        Assert.assertEquals(123, t1.getAnnotation(TypeDescriptorTests.ParameterAnnotation.class).value());
    }

    @Test
    public void propertyComplex() throws Exception {
        Property property = new Property(getClass(), getClass().getMethod("getComplexProperty"), getClass().getMethod("setComplexProperty", Map.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(String.class, desc.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, desc.getMapValueTypeDescriptor().getElementTypeDescriptor().getElementTypeDescriptor().getType());
    }

    @Test
    public void propertyGenericType() throws Exception {
        TypeDescriptorTests.GenericType<Integer> genericBean = new TypeDescriptorTests.IntegerType();
        Property property = new Property(getClass(), genericBean.getClass().getMethod("getProperty"), genericBean.getClass().getMethod("setProperty", Integer.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void propertyTypeCovariance() throws Exception {
        TypeDescriptorTests.GenericType<Number> genericBean = new TypeDescriptorTests.NumberType();
        Property property = new Property(getClass(), genericBean.getClass().getMethod("getProperty"), genericBean.getClass().getMethod("setProperty", Number.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void propertyGenericTypeList() throws Exception {
        TypeDescriptorTests.GenericType<Integer> genericBean = new TypeDescriptorTests.IntegerType();
        Property property = new Property(getClass(), genericBean.getClass().getMethod("getListProperty"), genericBean.getClass().getMethod("setListProperty", List.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
    }

    @Test
    public void propertyGenericClassList() throws Exception {
        TypeDescriptorTests.IntegerClass genericBean = new TypeDescriptorTests.IntegerClass();
        Property property = new Property(genericBean.getClass(), genericBean.getClass().getMethod("getListProperty"), genericBean.getClass().getMethod("setListProperty", List.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.MethodAnnotation1.class));
        Assert.assertTrue(desc.hasAnnotation(TypeDescriptorTests.MethodAnnotation1.class));
    }

    @Test
    public void property() throws Exception {
        Property property = new Property(getClass(), getClass().getMethod("getProperty"), getClass().getMethod("setProperty", Map.class));
        TypeDescriptor desc = new TypeDescriptor(property);
        Assert.assertEquals(Map.class, desc.getType());
        Assert.assertEquals(Integer.class, desc.getMapKeyTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals(Long.class, desc.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.MethodAnnotation1.class));
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.MethodAnnotation2.class));
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.MethodAnnotation3.class));
    }

    @Test
    public void getAnnotationOnMethodThatIsLocallyAnnotated() throws Exception {
        assertAnnotationFoundOnMethod(TypeDescriptorTests.MethodAnnotation1.class, "methodWithLocalAnnotation");
    }

    @Test
    public void getAnnotationOnMethodThatIsMetaAnnotated() throws Exception {
        assertAnnotationFoundOnMethod(TypeDescriptorTests.MethodAnnotation1.class, "methodWithComposedAnnotation");
    }

    @Test
    public void getAnnotationOnMethodThatIsMetaMetaAnnotated() throws Exception {
        assertAnnotationFoundOnMethod(TypeDescriptorTests.MethodAnnotation1.class, "methodWithComposedComposedAnnotation");
    }

    @Test
    public void fieldScalar() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(getClass().getField("fieldScalar"));
        Assert.assertFalse(typeDescriptor.isPrimitive());
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertFalse(typeDescriptor.isCollection());
        Assert.assertFalse(typeDescriptor.isMap());
        Assert.assertEquals(Integer.class, typeDescriptor.getType());
        Assert.assertEquals(Integer.class, typeDescriptor.getObjectType());
    }

    @Test
    public void fieldList() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("listOfString"));
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertEquals(List.class, typeDescriptor.getType());
        Assert.assertEquals(String.class, typeDescriptor.getElementTypeDescriptor().getType());
        Assert.assertEquals("java.util.List<java.lang.String>", typeDescriptor.toString());
    }

    @Test
    public void fieldListOfListOfString() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("listOfListOfString"));
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertEquals(List.class, typeDescriptor.getType());
        Assert.assertEquals(List.class, typeDescriptor.getElementTypeDescriptor().getType());
        Assert.assertEquals(String.class, typeDescriptor.getElementTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.String>>", typeDescriptor.toString());
    }

    @Test
    public void fieldListOfListUnknown() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("listOfListOfUnknown"));
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertEquals(List.class, typeDescriptor.getType());
        Assert.assertEquals(List.class, typeDescriptor.getElementTypeDescriptor().getType());
        Assert.assertNull(typeDescriptor.getElementTypeDescriptor().getElementTypeDescriptor());
        Assert.assertEquals("java.util.List<java.util.List<?>>", typeDescriptor.toString());
    }

    @Test
    public void fieldArray() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("intArray"));
        Assert.assertTrue(typeDescriptor.isArray());
        Assert.assertEquals(Integer.TYPE, typeDescriptor.getElementTypeDescriptor().getType());
        Assert.assertEquals("int[]", typeDescriptor.toString());
    }

    @Test
    public void fieldComplexTypeDescriptor() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("arrayOfListOfString"));
        Assert.assertTrue(typeDescriptor.isArray());
        Assert.assertEquals(List.class, typeDescriptor.getElementTypeDescriptor().getType());
        Assert.assertEquals(String.class, typeDescriptor.getElementTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals("java.util.List<java.lang.String>[]", typeDescriptor.toString());
    }

    @Test
    public void fieldComplexTypeDescriptor2() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(TypeDescriptorTests.class.getDeclaredField("nestedMapField"));
        Assert.assertTrue(typeDescriptor.isMap());
        Assert.assertEquals(String.class, typeDescriptor.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(List.class, typeDescriptor.getMapValueTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, typeDescriptor.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals("java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>", typeDescriptor.toString());
    }

    @Test
    public void fieldMap() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(TypeDescriptorTests.class.getField("fieldMap"));
        Assert.assertTrue(desc.isMap());
        Assert.assertEquals(Integer.class, desc.getMapKeyTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals(Long.class, desc.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
    }

    @Test
    public void fieldAnnotated() throws Exception {
        TypeDescriptor typeDescriptor = new TypeDescriptor(getClass().getField("fieldAnnotated"));
        Assert.assertEquals(1, typeDescriptor.getAnnotations().length);
        Assert.assertNotNull(typeDescriptor.getAnnotation(TypeDescriptorTests.FieldAnnotation.class));
    }

    @Test
    public void valueOfScalar() {
        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(Integer.class);
        Assert.assertFalse(typeDescriptor.isPrimitive());
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertFalse(typeDescriptor.isCollection());
        Assert.assertFalse(typeDescriptor.isMap());
        Assert.assertEquals(Integer.class, typeDescriptor.getType());
        Assert.assertEquals(Integer.class, typeDescriptor.getObjectType());
    }

    @Test
    public void valueOfPrimitive() {
        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(int.class);
        Assert.assertTrue(typeDescriptor.isPrimitive());
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertFalse(typeDescriptor.isCollection());
        Assert.assertFalse(typeDescriptor.isMap());
        Assert.assertEquals(Integer.TYPE, typeDescriptor.getType());
        Assert.assertEquals(Integer.class, typeDescriptor.getObjectType());
    }

    @Test
    public void valueOfArray() throws Exception {
        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(int[].class);
        Assert.assertTrue(typeDescriptor.isArray());
        Assert.assertFalse(typeDescriptor.isCollection());
        Assert.assertFalse(typeDescriptor.isMap());
        Assert.assertEquals(Integer.TYPE, typeDescriptor.getElementTypeDescriptor().getType());
    }

    @Test
    public void valueOfCollection() throws Exception {
        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(Collection.class);
        Assert.assertTrue(typeDescriptor.isCollection());
        Assert.assertFalse(typeDescriptor.isArray());
        Assert.assertFalse(typeDescriptor.isMap());
        Assert.assertNull(typeDescriptor.getElementTypeDescriptor());
    }

    @Test
    public void forObject() {
        TypeDescriptor desc = TypeDescriptor.forObject("3");
        Assert.assertEquals(String.class, desc.getType());
    }

    @Test
    public void forObjectNullTypeDescriptor() {
        TypeDescriptor desc = TypeDescriptor.forObject(null);
        Assert.assertNull(desc);
    }

    @Test
    public void nestedMethodParameterType2Levels() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test2", List.class), 0), 2);
        Assert.assertEquals(String.class, t1.getType());
    }

    @Test
    public void nestedMethodParameterTypeMap() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test3", Map.class), 0), 1);
        Assert.assertEquals(String.class, t1.getType());
    }

    @Test
    public void nestedMethodParameterTypeMapTwoLevels() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test4", List.class), 0), 2);
        Assert.assertEquals(String.class, t1.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedMethodParameterNot1NestedLevel() throws Exception {
        TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test4", List.class), 0, 2), 2);
    }

    @Test
    public void nestedTooManyLevels() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test4", List.class), 0), 3);
        Assert.assertNull(t1);
    }

    @Test
    public void nestedMethodParameterTypeNotNestable() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test5", String.class), 0), 2);
        Assert.assertNull(t1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedMethodParameterTypeInvalidNestingLevel() throws Exception {
        TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test5", String.class), 0, 2), 2);
    }

    @Test
    public void nestedNotParameterized() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test6", List.class), 0), 1);
        Assert.assertEquals(List.class, t1.getType());
        Assert.assertEquals("java.util.List<?>", t1.toString());
        TypeDescriptor t2 = TypeDescriptor.nested(new MethodParameter(getClass().getMethod("test6", List.class), 0), 2);
        Assert.assertNull(t2);
    }

    @Test
    public void nestedFieldTypeMapTwoLevels() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.nested(getClass().getField("test4"), 2);
        Assert.assertEquals(String.class, t1.getType());
    }

    @Test
    public void nestedPropertyTypeMapTwoLevels() throws Exception {
        Property property = new Property(getClass(), getClass().getMethod("getTest4"), getClass().getMethod("setTest4", List.class));
        TypeDescriptor t1 = TypeDescriptor.nested(property, 2);
        Assert.assertEquals(String.class, t1.getType());
    }

    @Test
    public void collection() {
        TypeDescriptor desc = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Integer.class));
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(List.class, desc.getObjectType());
        Assert.assertEquals("java.util.List", desc.getName());
        Assert.assertEquals("java.util.List<java.lang.Integer>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertTrue(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertEquals(TypeDescriptor.valueOf(Integer.class), desc.getElementTypeDescriptor());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void collectionNested() {
        TypeDescriptor desc = TypeDescriptor.collection(List.class, TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Integer.class)));
        Assert.assertEquals(List.class, desc.getType());
        Assert.assertEquals(List.class, desc.getObjectType());
        Assert.assertEquals("java.util.List", desc.getName());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.Integer>>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertTrue(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertEquals(List.class, desc.getElementTypeDescriptor().getType());
        Assert.assertEquals(TypeDescriptor.valueOf(Integer.class), desc.getElementTypeDescriptor().getElementTypeDescriptor());
        Assert.assertFalse(desc.isMap());
    }

    @Test
    public void map() {
        TypeDescriptor desc = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class));
        Assert.assertEquals(Map.class, desc.getType());
        Assert.assertEquals(Map.class, desc.getObjectType());
        Assert.assertEquals("java.util.Map", desc.getName());
        Assert.assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertTrue(desc.isMap());
        Assert.assertEquals(String.class, desc.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, desc.getMapValueTypeDescriptor().getType());
    }

    @Test
    public void mapNested() {
        TypeDescriptor desc = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
        Assert.assertEquals(Map.class, desc.getType());
        Assert.assertEquals(Map.class, desc.getObjectType());
        Assert.assertEquals("java.util.Map", desc.getName());
        Assert.assertEquals("java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Integer>>", desc.toString());
        Assert.assertTrue((!(desc.isPrimitive())));
        Assert.assertEquals(0, desc.getAnnotations().length);
        Assert.assertFalse(desc.isCollection());
        Assert.assertFalse(desc.isArray());
        Assert.assertTrue(desc.isMap());
        Assert.assertEquals(String.class, desc.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(String.class, desc.getMapValueTypeDescriptor().getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, desc.getMapValueTypeDescriptor().getMapValueTypeDescriptor().getType());
    }

    @Test
    public void narrow() {
        TypeDescriptor desc = TypeDescriptor.valueOf(Number.class);
        Integer value = Integer.valueOf(3);
        desc = desc.narrow(value);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void elementType() {
        TypeDescriptor desc = TypeDescriptor.valueOf(List.class);
        Integer value = Integer.valueOf(3);
        desc = desc.elementTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void elementTypePreserveContext() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(getClass().getField("listPreserveContext"));
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getElementTypeDescriptor().getType());
        List<Integer> value = new ArrayList<>(3);
        desc = desc.elementTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.FieldAnnotation.class));
    }

    @Test
    public void mapKeyType() {
        TypeDescriptor desc = TypeDescriptor.valueOf(Map.class);
        Integer value = Integer.valueOf(3);
        desc = desc.getMapKeyTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void mapKeyTypePreserveContext() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(getClass().getField("mapPreserveContext"));
        Assert.assertEquals(Integer.class, desc.getMapKeyTypeDescriptor().getElementTypeDescriptor().getType());
        List<Integer> value = new ArrayList<>(3);
        desc = desc.getMapKeyTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.FieldAnnotation.class));
    }

    @Test
    public void mapValueType() {
        TypeDescriptor desc = TypeDescriptor.valueOf(Map.class);
        Integer value = Integer.valueOf(3);
        desc = desc.getMapValueTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getType());
    }

    @Test
    public void mapValueTypePreserveContext() throws Exception {
        TypeDescriptor desc = new TypeDescriptor(getClass().getField("mapPreserveContext"));
        Assert.assertEquals(Integer.class, desc.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
        List<Integer> value = new ArrayList<>(3);
        desc = desc.getMapValueTypeDescriptor(value);
        Assert.assertEquals(Integer.class, desc.getElementTypeDescriptor().getType());
        Assert.assertNotNull(desc.getAnnotation(TypeDescriptorTests.FieldAnnotation.class));
    }

    @Test
    public void equality() throws Exception {
        TypeDescriptor t1 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor t2 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor t3 = TypeDescriptor.valueOf(Date.class);
        TypeDescriptor t4 = TypeDescriptor.valueOf(Date.class);
        TypeDescriptor t5 = TypeDescriptor.valueOf(List.class);
        TypeDescriptor t6 = TypeDescriptor.valueOf(List.class);
        TypeDescriptor t7 = TypeDescriptor.valueOf(Map.class);
        TypeDescriptor t8 = TypeDescriptor.valueOf(Map.class);
        Assert.assertEquals(t1, t2);
        Assert.assertEquals(t3, t4);
        Assert.assertEquals(t5, t6);
        Assert.assertEquals(t7, t8);
        TypeDescriptor t9 = new TypeDescriptor(getClass().getField("listField"));
        TypeDescriptor t10 = new TypeDescriptor(getClass().getField("listField"));
        Assert.assertEquals(t9, t10);
        TypeDescriptor t11 = new TypeDescriptor(getClass().getField("mapField"));
        TypeDescriptor t12 = new TypeDescriptor(getClass().getField("mapField"));
        Assert.assertEquals(t11, t12);
        MethodParameter testAnnotatedMethod = new MethodParameter(getClass().getMethod("testAnnotatedMethod", String.class), 0);
        TypeDescriptor t13 = new TypeDescriptor(testAnnotatedMethod);
        TypeDescriptor t14 = new TypeDescriptor(testAnnotatedMethod);
        Assert.assertEquals(t13, t14);
        TypeDescriptor t15 = new TypeDescriptor(testAnnotatedMethod);
        TypeDescriptor t16 = new TypeDescriptor(new MethodParameter(getClass().getMethod("testAnnotatedMethodDifferentAnnotationValue", String.class), 0));
        Assert.assertNotEquals(t15, t16);
        TypeDescriptor t17 = new TypeDescriptor(testAnnotatedMethod);
        TypeDescriptor t18 = new TypeDescriptor(new MethodParameter(getClass().getMethod("test5", String.class), 0));
        Assert.assertNotEquals(t17, t18);
    }

    @Test
    public void isAssignableTypes() {
        Assert.assertTrue(TypeDescriptor.valueOf(Integer.class).isAssignableTo(TypeDescriptor.valueOf(Number.class)));
        Assert.assertFalse(TypeDescriptor.valueOf(Number.class).isAssignableTo(TypeDescriptor.valueOf(Integer.class)));
        Assert.assertFalse(TypeDescriptor.valueOf(String.class).isAssignableTo(TypeDescriptor.valueOf(String[].class)));
    }

    @Test
    public void isAssignableElementTypes() throws Exception {
        Assert.assertTrue(new TypeDescriptor(getClass().getField("listField")).isAssignableTo(new TypeDescriptor(getClass().getField("listField"))));
        Assert.assertTrue(new TypeDescriptor(getClass().getField("notGenericList")).isAssignableTo(new TypeDescriptor(getClass().getField("listField"))));
        Assert.assertTrue(new TypeDescriptor(getClass().getField("listField")).isAssignableTo(new TypeDescriptor(getClass().getField("notGenericList"))));
        Assert.assertFalse(new TypeDescriptor(getClass().getField("isAssignableElementTypes")).isAssignableTo(new TypeDescriptor(getClass().getField("listField"))));
        Assert.assertTrue(TypeDescriptor.valueOf(List.class).isAssignableTo(new TypeDescriptor(getClass().getField("listField"))));
    }

    @Test
    public void isAssignableMapKeyValueTypes() throws Exception {
        Assert.assertTrue(new TypeDescriptor(getClass().getField("mapField")).isAssignableTo(new TypeDescriptor(getClass().getField("mapField"))));
        Assert.assertTrue(new TypeDescriptor(getClass().getField("notGenericMap")).isAssignableTo(new TypeDescriptor(getClass().getField("mapField"))));
        Assert.assertTrue(new TypeDescriptor(getClass().getField("mapField")).isAssignableTo(new TypeDescriptor(getClass().getField("notGenericMap"))));
        Assert.assertFalse(new TypeDescriptor(getClass().getField("isAssignableMapKeyValueTypes")).isAssignableTo(new TypeDescriptor(getClass().getField("mapField"))));
        Assert.assertTrue(TypeDescriptor.valueOf(Map.class).isAssignableTo(new TypeDescriptor(getClass().getField("mapField"))));
    }

    @Test
    public void multiValueMap() throws Exception {
        TypeDescriptor td = new TypeDescriptor(getClass().getField("multiValueMap"));
        Assert.assertTrue(td.isMap());
        Assert.assertEquals(String.class, td.getMapKeyTypeDescriptor().getType());
        Assert.assertEquals(List.class, td.getMapValueTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, td.getMapValueTypeDescriptor().getElementTypeDescriptor().getType());
    }

    @Test
    public void passDownGeneric() throws Exception {
        TypeDescriptor td = new TypeDescriptor(getClass().getField("passDownGeneric"));
        Assert.assertEquals(List.class, td.getElementTypeDescriptor().getType());
        Assert.assertEquals(Set.class, td.getElementTypeDescriptor().getElementTypeDescriptor().getType());
        Assert.assertEquals(Integer.class, td.getElementTypeDescriptor().getElementTypeDescriptor().getElementTypeDescriptor().getType());
    }

    @Test
    public void testUpCast() throws Exception {
        Property property = new Property(getClass(), getClass().getMethod("getProperty"), getClass().getMethod("setProperty", Map.class));
        TypeDescriptor typeDescriptor = new TypeDescriptor(property);
        TypeDescriptor upCast = typeDescriptor.upcast(Object.class);
        Assert.assertTrue(((upCast.getAnnotation(TypeDescriptorTests.MethodAnnotation1.class)) != null));
    }

    @Test
    public void testUpCastNotSuper() throws Exception {
        Property property = new Property(getClass(), getClass().getMethod("getProperty"), getClass().getMethod("setProperty", Map.class));
        TypeDescriptor typeDescriptor = new TypeDescriptor(property);
        try {
            typeDescriptor.upcast(Collection.class);
            Assert.fail("Did not throw");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("interface java.util.Map is not assignable to interface java.util.Collection", ex.getMessage());
        }
    }

    @Test
    public void elementTypeForCollectionSubclass() throws Exception {
        @SuppressWarnings("serial")
        class CustomSet extends HashSet<String> {}
        Assert.assertEquals(TypeDescriptor.valueOf(CustomSet.class).getElementTypeDescriptor(), TypeDescriptor.valueOf(String.class));
        Assert.assertEquals(TypeDescriptor.forObject(new CustomSet()).getElementTypeDescriptor(), TypeDescriptor.valueOf(String.class));
    }

    @Test
    public void elementTypeForMapSubclass() throws Exception {
        @SuppressWarnings("serial")
        class CustomMap extends HashMap<String, Integer> {}
        Assert.assertEquals(TypeDescriptor.valueOf(CustomMap.class).getMapKeyTypeDescriptor(), TypeDescriptor.valueOf(String.class));
        Assert.assertEquals(TypeDescriptor.valueOf(CustomMap.class).getMapValueTypeDescriptor(), TypeDescriptor.valueOf(Integer.class));
        Assert.assertEquals(TypeDescriptor.forObject(new CustomMap()).getMapKeyTypeDescriptor(), TypeDescriptor.valueOf(String.class));
        Assert.assertEquals(TypeDescriptor.forObject(new CustomMap()).getMapValueTypeDescriptor(), TypeDescriptor.valueOf(Integer.class));
    }

    @Test
    public void createMapArray() throws Exception {
        TypeDescriptor mapType = TypeDescriptor.map(LinkedHashMap.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class));
        TypeDescriptor arrayType = TypeDescriptor.array(mapType);
        Assert.assertEquals(arrayType.getType(), LinkedHashMap[].class);
        Assert.assertEquals(arrayType.getElementTypeDescriptor(), mapType);
    }

    @Test
    public void createStringArray() throws Exception {
        TypeDescriptor arrayType = TypeDescriptor.array(TypeDescriptor.valueOf(String.class));
        Assert.assertEquals(arrayType, TypeDescriptor.valueOf(String[].class));
    }

    @Test
    public void createNullArray() throws Exception {
        Assert.assertNull(TypeDescriptor.array(null));
    }

    @Test
    public void serializable() throws Exception {
        TypeDescriptor typeDescriptor = TypeDescriptor.forObject("");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(typeDescriptor);
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        TypeDescriptor readObject = ((TypeDescriptor) (inputStream.readObject()));
        Assert.assertThat(readObject, equalTo(typeDescriptor));
    }

    @Test
    public void createCollectionWithNullElement() throws Exception {
        TypeDescriptor typeDescriptor = TypeDescriptor.collection(List.class, null);
        Assert.assertThat(typeDescriptor.getElementTypeDescriptor(), nullValue());
    }

    @Test
    public void createMapWithNullElements() throws Exception {
        TypeDescriptor typeDescriptor = TypeDescriptor.map(LinkedHashMap.class, null, null);
        Assert.assertThat(typeDescriptor.getMapKeyTypeDescriptor(), nullValue());
        Assert.assertThat(typeDescriptor.getMapValueTypeDescriptor(), nullValue());
    }

    @Test
    public void getSource() throws Exception {
        Field field = getClass().getField("fieldScalar");
        MethodParameter methodParameter = new MethodParameter(getClass().getMethod("testParameterPrimitive", int.class), 0);
        Assert.assertThat(new TypeDescriptor(field).getSource(), equalTo(((Object) (field))));
        Assert.assertThat(new TypeDescriptor(methodParameter).getSource(), equalTo(((Object) (methodParameter))));
        Assert.assertThat(TypeDescriptor.valueOf(Integer.class).getSource(), equalTo(((Object) (Integer.class))));
    }

    // Fields designed for test introspection
    public Integer fieldScalar;

    public List<String> listOfString;

    public List<List<String>> listOfListOfString = new ArrayList<>();

    public List<List> listOfListOfUnknown = new ArrayList<>();

    public int[] intArray;

    public List<String>[] arrayOfListOfString;

    public List<Integer> listField = new ArrayList<>();

    public Map<String, Integer> mapField = new HashMap<>();

    public Map<String, List<Integer>> nestedMapField = new HashMap<>();

    public Map<List<Integer>, List<Long>> fieldMap;

    public List<Map<Integer, String>> test4;

    @TypeDescriptorTests.FieldAnnotation
    public List<String> fieldAnnotated;

    @TypeDescriptorTests.FieldAnnotation
    public List<List<Integer>> listPreserveContext;

    @TypeDescriptorTests.FieldAnnotation
    public Map<List<Integer>, List<Integer>> mapPreserveContext;

    @TypeDescriptorTests.MethodAnnotation3
    private Map<List<Integer>, List<Long>> property;

    public List notGenericList;

    public List<Number> isAssignableElementTypes;

    public Map notGenericMap;

    public Map<CharSequence, Number> isAssignableMapKeyValueTypes;

    public MultiValueMap<String, Integer> multiValueMap = new org.springframework.util.LinkedMultiValueMap();

    public TypeDescriptorTests.PassDownGeneric<Integer> passDownGeneric = new TypeDescriptorTests.PassDownGeneric<>();

    // Classes designed for test introspection
    @SuppressWarnings("serial")
    public static class PassDownGeneric<T> extends ArrayList<List<Set<T>>> {}

    public static class GenericClass<T> {
        public T getProperty() {
            return null;
        }

        public void setProperty(T t) {
        }

        @TypeDescriptorTests.MethodAnnotation1
        public List<T> getListProperty() {
            return null;
        }

        public void setListProperty(List<T> t) {
        }
    }

    public static class IntegerClass extends TypeDescriptorTests.GenericClass<Integer> {}

    public interface GenericType<T> {
        T getProperty();

        void setProperty(T t);

        List<T> getListProperty();

        void setListProperty(List<T> t);
    }

    public class IntegerType implements TypeDescriptorTests.GenericType<Integer> {
        @Override
        public Integer getProperty() {
            return null;
        }

        @Override
        public void setProperty(Integer t) {
        }

        @Override
        public List<Integer> getListProperty() {
            return null;
        }

        @Override
        public void setListProperty(List<Integer> t) {
        }
    }

    public class NumberType implements TypeDescriptorTests.GenericType<Number> {
        @Override
        public Integer getProperty() {
            return null;
        }

        @Override
        public void setProperty(Number t) {
        }

        @Override
        public List<Number> getListProperty() {
            return null;
        }

        @Override
        public void setListProperty(List<Number> t) {
        }
    }

    // Annotations used on tested elements
    @Target({ ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParameterAnnotation {
        int value();
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldAnnotation {}

    @Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation1 {}

    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation2 {}

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation3 {}

    @TypeDescriptorTests.MethodAnnotation1
    @Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ComposedMethodAnnotation1 {}

    @TypeDescriptorTests.ComposedMethodAnnotation1
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ComposedComposedMethodAnnotation1 {}
}

