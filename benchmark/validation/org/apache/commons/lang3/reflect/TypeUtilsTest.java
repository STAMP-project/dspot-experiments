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


import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.lang3.reflect.testbed.Foo;
import org.apache.commons.lang3.reflect.testbed.GenericParent;
import org.apache.commons.lang3.reflect.testbed.GenericTypeHolder;
import org.apache.commons.lang3.reflect.testbed.StringParameterizedChild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test TypeUtils
 */
// raw types, where used, are used purposely
@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
public class TypeUtilsTest<B> {
    public interface This<K, V> {}

    public class That<K, V> implements TypeUtilsTest.This<K, V> {}

    public interface And<K, V> extends TypeUtilsTest.This<Number, Number> {}

    public class The<K, V> extends TypeUtilsTest<B>.That<Number, Number> implements TypeUtilsTest.And<String, String> {}

    public class Other<T> implements TypeUtilsTest.This<String, T> {}

    public class Thing<Q> extends TypeUtilsTest<B>.Other<B> {}

    public class Tester implements TypeUtilsTest.This<String, B> {}

    public TypeUtilsTest.This<String, String> dis;

    public TypeUtilsTest<B>.That<String, String> dat;

    public TypeUtilsTest<B>.The<String, String> da;

    public TypeUtilsTest<B>.Other<String> uhder;

    public TypeUtilsTest.Thing ding;

    public TypeUtilsTest<String>.Tester tester;

    public TypeUtilsTest<B>.Tester tester2;

    public TypeUtilsTest<String>.That<String, String> dat2;

    public TypeUtilsTest<Number>.That<String, String> dat3;

    public Comparable<? extends Integer>[] intWildcardComparable;

    public static Comparable<String> stringComparable;

    public static Comparable<URI> uriComparable;

    public static Comparable<Integer> intComparable;

    public static Comparable<Long> longComparable;

    public static Comparable<?> wildcardComparable;

    public static URI uri;

    public static List<String>[] stringListArray;

    // deliberately used here
    @SuppressWarnings("boxing")
    @Test
    public void testIsAssignable() throws NoSuchFieldException, NoSuchMethodException, SecurityException {
        List list0 = null;
        List<Object> list1 = null;
        List<?> list2 = null;
        List<? super Object> list3 = null;
        List<String> list4 = null;
        List<? extends String> list5 = null;
        List<? super String> list6 = null;
        List[] list7 = null;
        List<Object>[] list8 = null;
        List<?>[] list9 = null;
        List<? super Object>[] list10 = null;
        List<String>[] list11 = null;
        List<? extends String>[] list12 = null;
        List<? super String>[] list13;
        final Class<?> clazz = getClass();
        final Method method = clazz.getMethod("dummyMethod", List.class, List.class, List.class, List.class, List.class, List.class, List.class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class);
        final Type[] types = method.getGenericParameterTypes();
        // list0 = list0;
        delegateBooleanAssertion(types, 0, 0, true);
        list1 = list0;
        delegateBooleanAssertion(types, 0, 1, true);
        list0 = list1;
        delegateBooleanAssertion(types, 1, 0, true);
        list2 = list0;
        delegateBooleanAssertion(types, 0, 2, true);
        list0 = list2;
        delegateBooleanAssertion(types, 2, 0, true);
        list3 = list0;
        delegateBooleanAssertion(types, 0, 3, true);
        list0 = list3;
        delegateBooleanAssertion(types, 3, 0, true);
        list4 = list0;
        delegateBooleanAssertion(types, 0, 4, true);
        list0 = list4;
        delegateBooleanAssertion(types, 4, 0, true);
        list5 = list0;
        delegateBooleanAssertion(types, 0, 5, true);
        list0 = list5;
        delegateBooleanAssertion(types, 5, 0, true);
        list6 = list0;
        delegateBooleanAssertion(types, 0, 6, true);
        list0 = list6;
        delegateBooleanAssertion(types, 6, 0, true);
        // list1 = list1;
        delegateBooleanAssertion(types, 1, 1, true);
        list2 = list1;
        delegateBooleanAssertion(types, 1, 2, true);
        list1 = ((List<Object>) (list2));
        delegateBooleanAssertion(types, 2, 1, false);
        list3 = list1;
        delegateBooleanAssertion(types, 1, 3, true);
        list1 = ((List<Object>) (list3));
        delegateBooleanAssertion(types, 3, 1, false);
        // list4 = list1;
        delegateBooleanAssertion(types, 1, 4, false);
        // list1 = list4;
        delegateBooleanAssertion(types, 4, 1, false);
        // list5 = list1;
        delegateBooleanAssertion(types, 1, 5, false);
        // list1 = list5;
        delegateBooleanAssertion(types, 5, 1, false);
        list6 = list1;
        delegateBooleanAssertion(types, 1, 6, true);
        list1 = ((List<Object>) (list6));
        delegateBooleanAssertion(types, 6, 1, false);
        // list2 = list2;
        delegateBooleanAssertion(types, 2, 2, true);
        list2 = list3;
        delegateBooleanAssertion(types, 2, 3, false);
        list2 = list4;
        delegateBooleanAssertion(types, 3, 2, true);
        list3 = ((List<? super Object>) (list2));
        delegateBooleanAssertion(types, 2, 4, false);
        list2 = list5;
        delegateBooleanAssertion(types, 4, 2, true);
        list4 = ((List<String>) (list2));
        delegateBooleanAssertion(types, 2, 5, false);
        list2 = list6;
        delegateBooleanAssertion(types, 5, 2, true);
        list5 = ((List<? extends String>) (list2));
        delegateBooleanAssertion(types, 2, 6, false);
        // list3 = list3;
        delegateBooleanAssertion(types, 6, 2, true);
        list6 = ((List<? super String>) (list2));
        delegateBooleanAssertion(types, 3, 3, true);
        // list4 = list3;
        delegateBooleanAssertion(types, 3, 4, false);
        // list3 = list4;
        delegateBooleanAssertion(types, 4, 3, false);
        // list5 = list3;
        delegateBooleanAssertion(types, 3, 5, false);
        // list3 = list5;
        delegateBooleanAssertion(types, 5, 3, false);
        list6 = list3;
        delegateBooleanAssertion(types, 3, 6, true);
        list3 = ((List<? super Object>) (list6));
        delegateBooleanAssertion(types, 6, 3, false);
        // list4 = list4;
        delegateBooleanAssertion(types, 4, 4, true);
        list5 = list4;
        delegateBooleanAssertion(types, 4, 5, true);
        list4 = ((List<String>) (list5));
        delegateBooleanAssertion(types, 5, 4, false);
        list6 = list4;
        delegateBooleanAssertion(types, 4, 6, true);
        list4 = ((List<String>) (list6));
        delegateBooleanAssertion(types, 6, 4, false);
        // list5 = list5;
        delegateBooleanAssertion(types, 5, 5, true);
        list6 = ((List<? super String>) (list5));
        delegateBooleanAssertion(types, 5, 6, false);
        list5 = ((List<? extends String>) (list6));
        delegateBooleanAssertion(types, 6, 5, false);
        // list6 = list6;
        delegateBooleanAssertion(types, 6, 6, true);
        // list7 = list7;
        delegateBooleanAssertion(types, 7, 7, true);
        list8 = list7;
        delegateBooleanAssertion(types, 7, 8, true);
        list7 = list8;
        delegateBooleanAssertion(types, 8, 7, true);
        list9 = list7;
        delegateBooleanAssertion(types, 7, 9, true);
        list7 = list9;
        delegateBooleanAssertion(types, 9, 7, true);
        list10 = list7;
        delegateBooleanAssertion(types, 7, 10, true);
        list7 = list10;
        delegateBooleanAssertion(types, 10, 7, true);
        list11 = list7;
        delegateBooleanAssertion(types, 7, 11, true);
        list7 = list11;
        delegateBooleanAssertion(types, 11, 7, true);
        list12 = list7;
        delegateBooleanAssertion(types, 7, 12, true);
        list7 = list12;
        delegateBooleanAssertion(types, 12, 7, true);
        list13 = list7;
        delegateBooleanAssertion(types, 7, 13, true);
        list7 = list13;
        delegateBooleanAssertion(types, 13, 7, true);
        // list8 = list8;
        delegateBooleanAssertion(types, 8, 8, true);
        list9 = list8;
        delegateBooleanAssertion(types, 8, 9, true);
        list8 = ((List<Object>[]) (list9));
        delegateBooleanAssertion(types, 9, 8, false);
        list10 = list8;
        delegateBooleanAssertion(types, 8, 10, true);
        list8 = ((List<Object>[]) (list10));// NOTE cast is required by Sun Java, but not by Eclipse

        delegateBooleanAssertion(types, 10, 8, false);
        // list11 = list8;
        delegateBooleanAssertion(types, 8, 11, false);
        // list8 = list11;
        delegateBooleanAssertion(types, 11, 8, false);
        // list12 = list8;
        delegateBooleanAssertion(types, 8, 12, false);
        // list8 = list12;
        delegateBooleanAssertion(types, 12, 8, false);
        list13 = list8;
        delegateBooleanAssertion(types, 8, 13, true);
        list8 = ((List<Object>[]) (list13));
        delegateBooleanAssertion(types, 13, 8, false);
        // list9 = list9;
        delegateBooleanAssertion(types, 9, 9, true);
        list10 = ((List<? super Object>[]) (list9));
        delegateBooleanAssertion(types, 9, 10, false);
        list9 = list10;
        delegateBooleanAssertion(types, 10, 9, true);
        list11 = ((List<String>[]) (list9));
        delegateBooleanAssertion(types, 9, 11, false);
        list9 = list11;
        delegateBooleanAssertion(types, 11, 9, true);
        list12 = ((List<? extends String>[]) (list9));
        delegateBooleanAssertion(types, 9, 12, false);
        list9 = list12;
        delegateBooleanAssertion(types, 12, 9, true);
        list13 = ((List<? super String>[]) (list9));
        delegateBooleanAssertion(types, 9, 13, false);
        list9 = list13;
        delegateBooleanAssertion(types, 13, 9, true);
        // list10 = list10;
        delegateBooleanAssertion(types, 10, 10, true);
        // list11 = list10;
        delegateBooleanAssertion(types, 10, 11, false);
        // list10 = list11;
        delegateBooleanAssertion(types, 11, 10, false);
        // list12 = list10;
        delegateBooleanAssertion(types, 10, 12, false);
        // list10 = list12;
        delegateBooleanAssertion(types, 12, 10, false);
        list13 = list10;
        delegateBooleanAssertion(types, 10, 13, true);
        list10 = ((List<? super Object>[]) (list13));
        delegateBooleanAssertion(types, 13, 10, false);
        // list11 = list11;
        delegateBooleanAssertion(types, 11, 11, true);
        list12 = list11;
        delegateBooleanAssertion(types, 11, 12, true);
        list11 = ((List<String>[]) (list12));
        delegateBooleanAssertion(types, 12, 11, false);
        list13 = list11;
        delegateBooleanAssertion(types, 11, 13, true);
        list11 = ((List<String>[]) (list13));
        delegateBooleanAssertion(types, 13, 11, false);
        // list12 = list12;
        delegateBooleanAssertion(types, 12, 12, true);
        list13 = ((List<? super String>[]) (list12));
        delegateBooleanAssertion(types, 12, 13, false);
        list12 = ((List<? extends String>[]) (list13));
        delegateBooleanAssertion(types, 13, 12, false);
        // list13 = list13;
        delegateBooleanAssertion(types, 13, 13, true);
        final Type disType = getClass().getField("dis").getGenericType();
        // Reporter.log( ( ( ParameterizedType ) disType
        // ).getOwnerType().getClass().toString() );
        final Type datType = getClass().getField("dat").getGenericType();
        final Type daType = getClass().getField("da").getGenericType();
        final Type uhderType = getClass().getField("uhder").getGenericType();
        final Type dingType = getClass().getField("ding").getGenericType();
        final Type testerType = getClass().getField("tester").getGenericType();
        final Type tester2Type = getClass().getField("tester2").getGenericType();
        final Type dat2Type = getClass().getField("dat2").getGenericType();
        final Type dat3Type = getClass().getField("dat3").getGenericType();
        dis = dat;
        Assertions.assertTrue(TypeUtils.isAssignable(datType, disType));
        // dis = da;
        Assertions.assertFalse(TypeUtils.isAssignable(daType, disType));
        dis = uhder;
        Assertions.assertTrue(TypeUtils.isAssignable(uhderType, disType));
        dis = ding;
        Assertions.assertFalse(TypeUtils.isAssignable(dingType, disType), String.format("type %s not assignable to %s!", dingType, disType));
        dis = tester;
        Assertions.assertTrue(TypeUtils.isAssignable(testerType, disType));
        // dis = tester2;
        Assertions.assertFalse(TypeUtils.isAssignable(tester2Type, disType));
        // dat = dat2;
        Assertions.assertFalse(TypeUtils.isAssignable(dat2Type, datType));
        // dat2 = dat;
        Assertions.assertFalse(TypeUtils.isAssignable(datType, dat2Type));
        // dat = dat3;
        Assertions.assertFalse(TypeUtils.isAssignable(dat3Type, datType));
        final char ch = 0;
        final boolean bo = false;
        final byte by = 0;
        final short sh = 0;
        int in = 0;
        long lo = 0;
        final float fl = 0;
        double du = 0;
        du = ch;
        Assertions.assertTrue(TypeUtils.isAssignable(char.class, double.class));
        du = by;
        Assertions.assertTrue(TypeUtils.isAssignable(byte.class, double.class));
        du = sh;
        Assertions.assertTrue(TypeUtils.isAssignable(short.class, double.class));
        du = in;
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, double.class));
        du = lo;
        Assertions.assertTrue(TypeUtils.isAssignable(long.class, double.class));
        du = fl;
        Assertions.assertTrue(TypeUtils.isAssignable(float.class, double.class));
        lo = in;
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, long.class));
        lo = Integer.valueOf(0);
        Assertions.assertTrue(TypeUtils.isAssignable(Integer.class, long.class));
        // Long lngW = 1;
        Assertions.assertFalse(TypeUtils.isAssignable(int.class, Long.class));
        // lngW = Integer.valueOf( 0 );
        Assertions.assertFalse(TypeUtils.isAssignable(Integer.class, Long.class));
        in = Integer.valueOf(0);
        Assertions.assertTrue(TypeUtils.isAssignable(Integer.class, int.class));
        final Integer inte = in;
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, Integer.class));
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, Number.class));
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, Object.class));
        final Type intComparableType = getClass().getField("intComparable").getGenericType();
        TypeUtilsTest.intComparable = 1;
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, intComparableType));
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, Comparable.class));
        final Serializable ser = 1;
        Assertions.assertTrue(TypeUtils.isAssignable(int.class, Serializable.class));
        final Type longComparableType = getClass().getField("longComparable").getGenericType();
        // longComparable = 1;
        Assertions.assertFalse(TypeUtils.isAssignable(int.class, longComparableType));
        // longComparable = Integer.valueOf( 0 );
        Assertions.assertFalse(TypeUtils.isAssignable(Integer.class, longComparableType));
        // int[] ia;
        // long[] la = ia;
        Assertions.assertFalse(TypeUtils.isAssignable(int[].class, long[].class));
        final Integer[] ia = null;
        final Type caType = getClass().getField("intWildcardComparable").getGenericType();
        intWildcardComparable = ia;
        Assertions.assertTrue(TypeUtils.isAssignable(Integer[].class, caType));
        // int[] ina = ia;
        Assertions.assertFalse(TypeUtils.isAssignable(Integer[].class, int[].class));
        final int[] ina = null;
        Object[] oa;
        // oa = ina;
        Assertions.assertFalse(TypeUtils.isAssignable(int[].class, Object[].class));
        oa = new Integer[0];
        Assertions.assertTrue(TypeUtils.isAssignable(Integer[].class, Object[].class));
        final Type bClassType = AClass.class.getField("bClass").getGenericType();
        final Type cClassType = AClass.class.getField("cClass").getGenericType();
        final Type dClassType = AClass.class.getField("dClass").getGenericType();
        final Type eClassType = AClass.class.getField("eClass").getGenericType();
        final Type fClassType = AClass.class.getField("fClass").getGenericType();
        final AClass aClass = new AClass(new AAClass<>());
        aClass.bClass = aClass.cClass;
        Assertions.assertTrue(TypeUtils.isAssignable(cClassType, bClassType));
        aClass.bClass = aClass.dClass;
        Assertions.assertTrue(TypeUtils.isAssignable(dClassType, bClassType));
        aClass.bClass = aClass.eClass;
        Assertions.assertTrue(TypeUtils.isAssignable(eClassType, bClassType));
        aClass.bClass = aClass.fClass;
        Assertions.assertTrue(TypeUtils.isAssignable(fClassType, bClassType));
        aClass.cClass = aClass.dClass;
        Assertions.assertTrue(TypeUtils.isAssignable(dClassType, cClassType));
        aClass.cClass = aClass.eClass;
        Assertions.assertTrue(TypeUtils.isAssignable(eClassType, cClassType));
        aClass.cClass = aClass.fClass;
        Assertions.assertTrue(TypeUtils.isAssignable(fClassType, cClassType));
        aClass.dClass = aClass.eClass;
        Assertions.assertTrue(TypeUtils.isAssignable(eClassType, dClassType));
        aClass.dClass = aClass.fClass;
        Assertions.assertTrue(TypeUtils.isAssignable(fClassType, dClassType));
        aClass.eClass = aClass.fClass;
        Assertions.assertTrue(TypeUtils.isAssignable(fClassType, eClassType));
    }

    // boxing is deliberate here
    @SuppressWarnings("boxing")
    @Test
    public void testIsInstance() throws NoSuchFieldException, SecurityException {
        final Type intComparableType = getClass().getField("intComparable").getGenericType();
        final Type uriComparableType = getClass().getField("uriComparable").getGenericType();
        TypeUtilsTest.intComparable = 1;
        Assertions.assertTrue(TypeUtils.isInstance(1, intComparableType));
        // uriComparable = 1;
        Assertions.assertFalse(TypeUtils.isInstance(1, uriComparableType));
    }

    @Test
    public void testGetTypeArguments() {
        Map<TypeVariable<?>, Type> typeVarAssigns;
        TypeVariable<?> treeSetTypeVar;
        Type typeArg;
        typeVarAssigns = TypeUtils.getTypeArguments(Integer.class, Comparable.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        Assertions.assertTrue(typeVarAssigns.containsKey(treeSetTypeVar), ("Type var assigns for Comparable from Integer: " + typeVarAssigns));
        typeArg = typeVarAssigns.get(treeSetTypeVar);
        Assertions.assertEquals(Integer.class, typeVarAssigns.get(treeSetTypeVar), ("Type argument of Comparable from Integer: " + typeArg));
        typeVarAssigns = TypeUtils.getTypeArguments(int.class, Comparable.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        Assertions.assertTrue(typeVarAssigns.containsKey(treeSetTypeVar), ("Type var assigns for Comparable from int: " + typeVarAssigns));
        typeArg = typeVarAssigns.get(treeSetTypeVar);
        Assertions.assertEquals(Integer.class, typeVarAssigns.get(treeSetTypeVar), ("Type argument of Comparable from int: " + typeArg));
        final Collection<Integer> col = Arrays.asList(new Integer[0]);
        typeVarAssigns = TypeUtils.getTypeArguments(List.class, Collection.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        Assertions.assertFalse(typeVarAssigns.containsKey(treeSetTypeVar), ("Type var assigns for Collection from List: " + typeVarAssigns));
        typeVarAssigns = TypeUtils.getTypeArguments(AAAClass.BBBClass.class, AAClass.BBClass.class);
        Assertions.assertEquals(2, typeVarAssigns.size());
        Assertions.assertEquals(String.class, typeVarAssigns.get(AAClass.class.getTypeParameters()[0]));
        Assertions.assertEquals(String.class, typeVarAssigns.get(AAClass.BBClass.class.getTypeParameters()[0]));
        typeVarAssigns = TypeUtils.getTypeArguments(TypeUtilsTest.Other.class, TypeUtilsTest.This.class);
        Assertions.assertEquals(2, typeVarAssigns.size());
        Assertions.assertEquals(String.class, typeVarAssigns.get(TypeUtilsTest.This.class.getTypeParameters()[0]));
        Assertions.assertEquals(TypeUtilsTest.Other.class.getTypeParameters()[0], typeVarAssigns.get(TypeUtilsTest.This.class.getTypeParameters()[1]));
        typeVarAssigns = TypeUtils.getTypeArguments(TypeUtilsTest.And.class, TypeUtilsTest.This.class);
        Assertions.assertEquals(2, typeVarAssigns.size());
        Assertions.assertEquals(Number.class, typeVarAssigns.get(TypeUtilsTest.This.class.getTypeParameters()[0]));
        Assertions.assertEquals(Number.class, typeVarAssigns.get(TypeUtilsTest.This.class.getTypeParameters()[1]));
        typeVarAssigns = TypeUtils.getTypeArguments(TypeUtilsTest.Thing.class, TypeUtilsTest.Other.class);
        Assertions.assertEquals(2, typeVarAssigns.size());
        Assertions.assertEquals(getClass().getTypeParameters()[0], typeVarAssigns.get(getClass().getTypeParameters()[0]));
        Assertions.assertEquals(getClass().getTypeParameters()[0], typeVarAssigns.get(TypeUtilsTest.Other.class.getTypeParameters()[0]));
    }

    @Test
    public void testTypesSatisfyVariables() throws NoSuchMethodException, SecurityException {
        final Map<TypeVariable<?>, Type> typeVarAssigns = new HashMap<>();
        final Integer max = TypeUtilsTest.<Integer>stub();
        typeVarAssigns.put(getClass().getMethod("stub").getTypeParameters()[0], Integer.class);
        Assertions.assertTrue(TypeUtils.typesSatisfyVariables(typeVarAssigns));
        typeVarAssigns.clear();
        typeVarAssigns.put(getClass().getMethod("stub2").getTypeParameters()[0], Integer.class);
        Assertions.assertTrue(TypeUtils.typesSatisfyVariables(typeVarAssigns));
        typeVarAssigns.clear();
        typeVarAssigns.put(getClass().getMethod("stub3").getTypeParameters()[0], Integer.class);
        Assertions.assertTrue(TypeUtils.typesSatisfyVariables(typeVarAssigns));
    }

    @Test
    public void testDetermineTypeVariableAssignments() throws NoSuchFieldException, SecurityException {
        final ParameterizedType iterableType = ((ParameterizedType) (getClass().getField("iterable").getGenericType()));
        final Map<TypeVariable<?>, Type> typeVarAssigns = TypeUtils.determineTypeArguments(TreeSet.class, iterableType);
        final TypeVariable<?> treeSetTypeVar = TreeSet.class.getTypeParameters()[0];
        Assertions.assertTrue(typeVarAssigns.containsKey(treeSetTypeVar));
        Assertions.assertEquals(iterableType.getActualTypeArguments()[0], typeVarAssigns.get(treeSetTypeVar));
    }

    @Test
    public void testGetRawType() throws NoSuchFieldException, SecurityException {
        final Type stringParentFieldType = GenericTypeHolder.class.getDeclaredField("stringParent").getGenericType();
        final Type integerParentFieldType = GenericTypeHolder.class.getDeclaredField("integerParent").getGenericType();
        final Type foosFieldType = GenericTypeHolder.class.getDeclaredField("foos").getGenericType();
        final Type genericParentT = GenericParent.class.getTypeParameters()[0];
        Assertions.assertEquals(GenericParent.class, TypeUtils.getRawType(stringParentFieldType, null));
        Assertions.assertEquals(GenericParent.class, TypeUtils.getRawType(integerParentFieldType, null));
        Assertions.assertEquals(List.class, TypeUtils.getRawType(foosFieldType, null));
        Assertions.assertEquals(String.class, TypeUtils.getRawType(genericParentT, StringParameterizedChild.class));
        Assertions.assertEquals(String.class, TypeUtils.getRawType(genericParentT, stringParentFieldType));
        Assertions.assertEquals(Foo.class, TypeUtils.getRawType(Iterable.class.getTypeParameters()[0], foosFieldType));
        Assertions.assertEquals(Foo.class, TypeUtils.getRawType(List.class.getTypeParameters()[0], foosFieldType));
        Assertions.assertNull(TypeUtils.getRawType(genericParentT, GenericParent.class));
        Assertions.assertEquals(GenericParent[].class, TypeUtils.getRawType(GenericTypeHolder.class.getDeclaredField("barParents").getGenericType(), null));
    }

    @Test
    public void testIsArrayTypeClasses() {
        Assertions.assertTrue(TypeUtils.isArrayType(boolean[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(byte[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(short[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(int[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(char[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(long[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(float[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(double[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(Object[].class));
        Assertions.assertTrue(TypeUtils.isArrayType(String[].class));
        Assertions.assertFalse(TypeUtils.isArrayType(boolean.class));
        Assertions.assertFalse(TypeUtils.isArrayType(byte.class));
        Assertions.assertFalse(TypeUtils.isArrayType(short.class));
        Assertions.assertFalse(TypeUtils.isArrayType(int.class));
        Assertions.assertFalse(TypeUtils.isArrayType(char.class));
        Assertions.assertFalse(TypeUtils.isArrayType(long.class));
        Assertions.assertFalse(TypeUtils.isArrayType(float.class));
        Assertions.assertFalse(TypeUtils.isArrayType(double.class));
        Assertions.assertFalse(TypeUtils.isArrayType(Object.class));
        Assertions.assertFalse(TypeUtils.isArrayType(String.class));
    }

    @Test
    public void testIsArrayGenericTypes() throws Exception {
        final Method method = getClass().getMethod("dummyMethod", List.class, List.class, List.class, List.class, List.class, List.class, List.class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class);
        final Type[] types = method.getGenericParameterTypes();
        Assertions.assertFalse(TypeUtils.isArrayType(types[0]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[1]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[2]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[3]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[4]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[5]));
        Assertions.assertFalse(TypeUtils.isArrayType(types[6]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[7]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[8]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[9]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[10]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[11]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[12]));
        Assertions.assertTrue(TypeUtils.isArrayType(types[13]));
    }

    @Test
    public void testGetPrimitiveArrayComponentType() {
        Assertions.assertEquals(boolean.class, TypeUtils.getArrayComponentType(boolean[].class));
        Assertions.assertEquals(byte.class, TypeUtils.getArrayComponentType(byte[].class));
        Assertions.assertEquals(short.class, TypeUtils.getArrayComponentType(short[].class));
        Assertions.assertEquals(int.class, TypeUtils.getArrayComponentType(int[].class));
        Assertions.assertEquals(char.class, TypeUtils.getArrayComponentType(char[].class));
        Assertions.assertEquals(long.class, TypeUtils.getArrayComponentType(long[].class));
        Assertions.assertEquals(float.class, TypeUtils.getArrayComponentType(float[].class));
        Assertions.assertEquals(double.class, TypeUtils.getArrayComponentType(double[].class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(boolean.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(byte.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(short.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(int.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(char.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(long.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(float.class));
        Assertions.assertNull(TypeUtils.getArrayComponentType(double.class));
    }

    @Test
    public void testGetArrayComponentType() throws Exception {
        final Method method = getClass().getMethod("dummyMethod", List.class, List.class, List.class, List.class, List.class, List.class, List.class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class, List[].class);
        final Type[] types = method.getGenericParameterTypes();
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[0]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[1]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[2]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[3]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[4]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[5]));
        Assertions.assertNull(TypeUtils.getArrayComponentType(types[6]));
        Assertions.assertEquals(types[0], TypeUtils.getArrayComponentType(types[7]));
        Assertions.assertEquals(types[1], TypeUtils.getArrayComponentType(types[8]));
        Assertions.assertEquals(types[2], TypeUtils.getArrayComponentType(types[9]));
        Assertions.assertEquals(types[3], TypeUtils.getArrayComponentType(types[10]));
        Assertions.assertEquals(types[4], TypeUtils.getArrayComponentType(types[11]));
        Assertions.assertEquals(types[5], TypeUtils.getArrayComponentType(types[12]));
        Assertions.assertEquals(types[6], TypeUtils.getArrayComponentType(types[13]));
    }

    @Test
    public void testLang820() {
        final Type[] typeArray = new Type[]{ String.class, String.class };
        final Type[] expectedArray = new Type[]{ String.class };
        Assertions.assertArrayEquals(expectedArray, TypeUtils.normalizeUpperBounds(typeArray));
    }

    @Test
    public void testParameterize() throws Exception {
        final ParameterizedType stringComparableType = TypeUtils.parameterize(Comparable.class, String.class);
        Assertions.assertTrue(TypeUtils.equals(getClass().getField("stringComparable").getGenericType(), stringComparableType));
        Assertions.assertEquals("java.lang.Comparable<java.lang.String>", stringComparableType.toString());
    }

    @Test
    public void testParameterizeNarrowerTypeArray() {
        final TypeVariable<?>[] variables = ArrayList.class.getTypeParameters();
        final ParameterizedType parameterizedType = TypeUtils.parameterize(ArrayList.class, variables);
        final Map<TypeVariable<?>, Type> mapping = Collections.<TypeVariable<?>, Type>singletonMap(variables[0], String.class);
        final Type unrolled = TypeUtils.unrollVariables(mapping, parameterizedType);
        Assertions.assertEquals(TypeUtils.parameterize(ArrayList.class, String.class), unrolled);
    }

    @Test
    public void testParameterizeWithOwner() throws Exception {
        final Type owner = TypeUtils.parameterize(TypeUtilsTest.class, String.class);
        final ParameterizedType dat2Type = TypeUtils.parameterizeWithOwner(owner, TypeUtilsTest.That.class, String.class, String.class);
        Assertions.assertTrue(TypeUtils.equals(getClass().getField("dat2").getGenericType(), dat2Type));
    }

    @Test
    public void testWildcardType() throws Exception {
        final WildcardType simpleWildcard = TypeUtils.wildcardType().withUpperBounds(String.class).build();
        final Field cClass = AClass.class.getField("cClass");
        Assertions.assertTrue(TypeUtils.equals(((ParameterizedType) (cClass.getGenericType())).getActualTypeArguments()[0], simpleWildcard));
        Assertions.assertEquals(String.format("? extends %s", String.class.getName()), TypeUtils.toString(simpleWildcard));
        Assertions.assertEquals(String.format("? extends %s", String.class.getName()), simpleWildcard.toString());
    }

    @Test
    public void testUnboundedWildcardType() {
        final WildcardType unbounded = TypeUtils.wildcardType().withLowerBounds(((Type) (null))).withUpperBounds().build();
        Assertions.assertTrue(TypeUtils.equals(TypeUtils.WILDCARD_ALL, unbounded));
        Assertions.assertArrayEquals(new Type[]{ Object.class }, TypeUtils.getImplicitUpperBounds(unbounded));
        Assertions.assertArrayEquals(new Type[]{ null }, TypeUtils.getImplicitLowerBounds(unbounded));
        Assertions.assertEquals("?", TypeUtils.toString(unbounded));
        Assertions.assertEquals("?", unbounded.toString());
    }

    @Test
    public void testLowerBoundedWildcardType() {
        final WildcardType lowerBounded = TypeUtils.wildcardType().withLowerBounds(Date.class).build();
        Assertions.assertEquals(String.format("? super %s", Date.class.getName()), TypeUtils.toString(lowerBounded));
        Assertions.assertEquals(String.format("? super %s", Date.class.getName()), lowerBounded.toString());
        final TypeVariable<Class<Iterable>> iterableT0 = Iterable.class.getTypeParameters()[0];
        final WildcardType lowerTypeVariable = TypeUtils.wildcardType().withLowerBounds(iterableT0).build();
        Assertions.assertEquals(String.format("? super %s", iterableT0.getName()), TypeUtils.toString(lowerTypeVariable));
        Assertions.assertEquals(String.format("? super %s", iterableT0.getName()), lowerTypeVariable.toString());
    }

    @Test
    public void testLang1114() throws Exception {
        final Type nonWildcardType = getClass().getDeclaredField("wildcardComparable").getGenericType();
        final Type wildcardType = ((ParameterizedType) (nonWildcardType)).getActualTypeArguments()[0];
        Assertions.assertFalse(TypeUtils.equals(wildcardType, nonWildcardType));
        Assertions.assertFalse(TypeUtils.equals(nonWildcardType, wildcardType));
    }

    @Test
    public void testGenericArrayType() throws Exception {
        final Type expected = getClass().getField("intWildcardComparable").getGenericType();
        final GenericArrayType actual = TypeUtils.genericArrayType(TypeUtils.parameterize(Comparable.class, TypeUtils.wildcardType().withUpperBounds(Integer.class).build()));
        Assertions.assertTrue(TypeUtils.equals(expected, actual));
        Assertions.assertEquals("java.lang.Comparable<? extends java.lang.Integer>[]", actual.toString());
    }

    @Test
    public void testToStringLang1311() {
        Assertions.assertEquals("int[]", TypeUtils.toString(int[].class));
        Assertions.assertEquals("java.lang.Integer[]", TypeUtils.toString(Integer[].class));
        final Field stringListField = FieldUtils.getDeclaredField(getClass(), "stringListArray");
        Assertions.assertEquals("java.util.List<java.lang.String>[]", TypeUtils.toString(stringListField.getGenericType()));
    }

    @Test
    public void testToLongString() {
        Assertions.assertEquals(((getClass().getName()) + ":B"), TypeUtils.toLongString(getClass().getTypeParameters()[0]));
    }

    @Test
    public void testWrap() {
        final Type t = getClass().getTypeParameters()[0];
        Assertions.assertTrue(TypeUtils.equals(t, TypeUtils.wrap(t).getType()));
        Assertions.assertEquals(String.class, TypeUtils.wrap(String.class).getType());
    }

    public static class ClassWithSuperClassWithGenericType extends ArrayList<Object> {
        private static final long serialVersionUID = 1L;

        public static <U> Iterable<U> methodWithGenericReturnType() {
            return null;
        }
    }

    @Test
    public void testLANG1190() throws Exception {
        final Type fromType = TypeUtilsTest.ClassWithSuperClassWithGenericType.class.getDeclaredMethod("methodWithGenericReturnType").getGenericReturnType();
        final Type failingToType = TypeUtils.wildcardType().withLowerBounds(TypeUtilsTest.ClassWithSuperClassWithGenericType.class).build();
        Assertions.assertTrue(TypeUtils.isAssignable(fromType, failingToType));
    }

    @Test
    public void testLANG1348() throws Exception {
        final Method method = Enum.class.getMethod("valueOf", Class.class, String.class);
        Assertions.assertEquals("T extends java.lang.Enum<T>", TypeUtils.toString(method.getGenericReturnType()));
    }

    public Iterable<? extends Map<Integer, ? extends Collection<?>>> iterable;
}

