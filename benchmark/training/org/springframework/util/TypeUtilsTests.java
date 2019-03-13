/**
 * Copyright 2002-2010 the original author or authors.
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
package org.springframework.util;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link TypeUtils}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class TypeUtilsTests {
    public static Object object;

    public static String string;

    public static Integer number;

    public static List<Object> objects;

    public static List<String> strings;

    public static List<? extends Object> openObjects;

    public static List<? extends Number> openNumbers;

    public static List<? super Object> storableObjectList;

    public static List<Number>[] array;

    public static List<? extends Number>[] openArray;

    @Test
    public void withClasses() {
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, Object.class));
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, String.class));
        Assert.assertFalse(TypeUtils.isAssignable(String.class, Object.class));
        Assert.assertTrue(TypeUtils.isAssignable(List.class, List.class));
        Assert.assertTrue(TypeUtils.isAssignable(List.class, LinkedList.class));
        Assert.assertFalse(TypeUtils.isAssignable(List.class, Collection.class));
        Assert.assertFalse(TypeUtils.isAssignable(List.class, HashSet.class));
    }

    @Test
    public void withParameterizedTypes() throws Exception {
        Type objectsType = getClass().getField("objects").getGenericType();
        Type openObjectsType = getClass().getField("openObjects").getGenericType();
        Type stringsType = getClass().getField("strings").getGenericType();
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, objectsType));
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, openObjectsType));
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, stringsType));
        Assert.assertTrue(TypeUtils.isAssignable(List.class, objectsType));
        Assert.assertTrue(TypeUtils.isAssignable(List.class, openObjectsType));
        Assert.assertTrue(TypeUtils.isAssignable(List.class, stringsType));
        Assert.assertTrue(TypeUtils.isAssignable(objectsType, List.class));
        Assert.assertTrue(TypeUtils.isAssignable(openObjectsType, List.class));
        Assert.assertTrue(TypeUtils.isAssignable(stringsType, List.class));
        Assert.assertTrue(TypeUtils.isAssignable(objectsType, objectsType));
        Assert.assertTrue(TypeUtils.isAssignable(openObjectsType, openObjectsType));
        Assert.assertTrue(TypeUtils.isAssignable(stringsType, stringsType));
        Assert.assertTrue(TypeUtils.isAssignable(openObjectsType, objectsType));
        Assert.assertTrue(TypeUtils.isAssignable(openObjectsType, stringsType));
        Assert.assertFalse(TypeUtils.isAssignable(stringsType, objectsType));
        Assert.assertFalse(TypeUtils.isAssignable(objectsType, stringsType));
    }

    @Test
    public void withWildcardTypes() throws Exception {
        ParameterizedType openObjectsType = ((ParameterizedType) (getClass().getField("openObjects").getGenericType()));
        ParameterizedType openNumbersType = ((ParameterizedType) (getClass().getField("openNumbers").getGenericType()));
        Type storableObjectListType = getClass().getField("storableObjectList").getGenericType();
        Type objectType = getClass().getField("object").getGenericType();
        Type numberType = getClass().getField("number").getGenericType();
        Type stringType = getClass().getField("string").getGenericType();
        Type openWildcard = openObjectsType.getActualTypeArguments()[0];// '?'

        Type openNumbersWildcard = openNumbersType.getActualTypeArguments()[0];// '? extends number'

        Assert.assertTrue(TypeUtils.isAssignable(openWildcard, objectType));
        Assert.assertTrue(TypeUtils.isAssignable(openNumbersWildcard, numberType));
        Assert.assertFalse(TypeUtils.isAssignable(openNumbersWildcard, stringType));
        Assert.assertFalse(TypeUtils.isAssignable(storableObjectListType, openObjectsType));
    }

    @Test
    public void withGenericArrayTypes() throws Exception {
        Type arrayType = getClass().getField("array").getGenericType();
        Type openArrayType = getClass().getField("openArray").getGenericType();
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, arrayType));
        Assert.assertTrue(TypeUtils.isAssignable(Object.class, openArrayType));
        Assert.assertTrue(TypeUtils.isAssignable(List[].class, arrayType));
        Assert.assertTrue(TypeUtils.isAssignable(List[].class, openArrayType));
        Assert.assertTrue(TypeUtils.isAssignable(arrayType, List[].class));
        Assert.assertTrue(TypeUtils.isAssignable(openArrayType, List[].class));
        Assert.assertTrue(TypeUtils.isAssignable(arrayType, arrayType));
        Assert.assertTrue(TypeUtils.isAssignable(openArrayType, openArrayType));
        Assert.assertTrue(TypeUtils.isAssignable(openArrayType, arrayType));
    }
}

