/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.typeutils;


import BasicTypeInfo.BOOLEAN_TYPE_INFO;
import BasicTypeInfo.BYTE_TYPE_INFO;
import BasicTypeInfo.CHAR_TYPE_INFO;
import BasicTypeInfo.INT_TYPE_INFO;
import BasicTypeInfo.LONG_TYPE_INFO;
import BasicTypeInfo.STRING_TYPE_INFO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.types.Value;
import org.junit.Assert;
import org.junit.Test;


/**
 * Pojo Type tests.
 *
 * <p>Pojo is a bean-style class with getters, setters and empty ctor
 * OR a class with all fields public (or for every private field, there has to be a public getter/setter)
 * everything else is a generic type (that can't be used for field selection)
 */
public class PojoTypeExtractionTest {
    /**
     * Simple test type that implements the {@link Value} interface.
     */
    public static class MyValue implements Value {
        private static final long serialVersionUID = 8607223484689147046L;

        @Override
        public void write(DataOutputView out) throws IOException {
        }

        @Override
        public void read(DataInputView in) throws IOException {
        }
    }

    public static class HasDuplicateField extends PojoTypeExtractionTest.WC {
        @SuppressWarnings("unused")
        private int count;// duplicate

    }

    @Test
    public void testDuplicateFieldException() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.HasDuplicateField.class);
        Assert.assertTrue((ti instanceof GenericTypeInfo<?>));
    }

    // test with correct pojo types
    public static class WC {
        // is a pojo
        public PojoTypeExtractionTest.ComplexNestedClass complex;// is a pojo


        private int count;// is a BasicType


        public WC() {
        }

        public int getCount() {
            return count;
        }

        public void setCount(int c) {
            this.count = c;
        }
    }

    public static class ComplexNestedClass {
        // pojo
        public static int ignoreStaticField;

        public transient int ignoreTransientField;

        public Date date;// generic type


        public Integer someNumberWith?nic?deN?me;// BasicType


        public float someFloat;// BasicType


        public Tuple3<Long, Long, String> word;// Tuple Type with three basic types


        public Object nothing;// generic type


        public PojoTypeExtractionTest.MyValue valueType;// writableType


        public List<String> collection;
    }

    // all public test
    public static class AllPublic extends PojoTypeExtractionTest.ComplexNestedClass {
        public ArrayList<String> somethingFancy;// generic type


        public PojoTypeExtractionTest.FancyCollectionSubtype<Integer> fancyIds;// generic type


        public String[] fancyArray;// generic type

    }

    public static class FancyCollectionSubtype<T> extends HashSet<T> {
        private static final long serialVersionUID = -3494469602638179921L;
    }

    public static class ParentSettingGenerics extends PojoTypeExtractionTest.PojoWithGenerics<Integer, Long> {
        public String field3;
    }

    public static class PojoWithGenerics<T1, T2> {
        public int key;

        public T1 field1;

        public T2 field2;
    }

    public static class ComplexHierarchyTop extends PojoTypeExtractionTest.ComplexHierarchy<Tuple1<String>> {}

    public static class ComplexHierarchy<T> extends PojoTypeExtractionTest.PojoWithGenerics<PojoTypeExtractionTest.FromTuple, T> {}

    // extends from Tuple and adds a field
    public static class FromTuple extends Tuple3<String, String, Long> {
        private static final long serialVersionUID = 1L;

        public int special;
    }

    // setter is missing (intentional)
    public static class IncorrectPojo {
        private int isPrivate;

        public int getIsPrivate() {
            return isPrivate;
        }
    }

    // correct pojo
    public static class BeanStylePojo {
        public String abc;

        private int field;

        public int getField() {
            return this.field;
        }

        public void setField(int f) {
            this.field = f;
        }
    }

    public static class WrongCtorPojo {
        public int a;

        public WrongCtorPojo(int a) {
            this.a = a;
        }
    }

    public static class PojoWithGenericFields {
        private Collection<String> users;

        private boolean favorited;

        public boolean isFavorited() {
            return favorited;
        }

        public void setFavorited(boolean favorited) {
            this.favorited = favorited;
        }

        public Collection<String> getUsers() {
            return users;
        }

        public void setUsers(Collection<String> users) {
            this.users = users;
        }
    }

    @Test
    public void testPojoWithGenericFields() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.PojoWithGenericFields.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
    }

    // in this test, the location of the getters and setters is mixed across the type hierarchy.
    public static class TypedPojoGetterSetterCheck extends PojoTypeExtractionTest.GenericPojoGetterSetterCheck<String> {
        public void setPackageProtected(String in) {
            this.packageProtected = in;
        }
    }

    public static class GenericPojoGetterSetterCheck<T> {
        T packageProtected;

        public T getPackageProtected() {
            return packageProtected;
        }
    }

    @Test
    public void testIncorrectPojos() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.IncorrectPojo.class);
        Assert.assertTrue((typeForClass instanceof GenericTypeInfo<?>));
        typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.WrongCtorPojo.class);
        Assert.assertTrue((typeForClass instanceof GenericTypeInfo<?>));
    }

    @Test
    public void testCorrectPojos() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.BeanStylePojo.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
        typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.TypedPojoGetterSetterCheck.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
    }

    @Test
    public void testPojoWC() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.WC.class);
        checkWCPojoAsserts(typeForClass);
        PojoTypeExtractionTest.WC t = new PojoTypeExtractionTest.WC();
        t.complex = new PojoTypeExtractionTest.ComplexNestedClass();
        TypeInformation<?> typeForObject = TypeExtractor.getForObject(t);
        checkWCPojoAsserts(typeForObject);
    }

    @Test
    public void testPojoAllPublic() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.AllPublic.class);
        checkAllPublicAsserts(typeForClass);
        TypeInformation<?> typeForObject = TypeExtractor.getForObject(new PojoTypeExtractionTest.AllPublic());
        checkAllPublicAsserts(typeForObject);
    }

    @Test
    public void testPojoExtendingTuple() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.FromTuple.class);
        checkFromTuplePojo(typeForClass);
        PojoTypeExtractionTest.FromTuple ft = new PojoTypeExtractionTest.FromTuple();
        ft.f0 = "";
        ft.f1 = "";
        ft.f2 = 0L;
        TypeInformation<?> typeForObject = TypeExtractor.getForObject(ft);
        checkFromTuplePojo(typeForObject);
    }

    @Test
    public void testPojoWithGenerics() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.ParentSettingGenerics.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
        PojoTypeInfo<?> pojoTypeForClass = ((PojoTypeInfo<?>) (typeForClass));
        for (int i = 0; i < (pojoTypeForClass.getArity()); i++) {
            PojoField field = pojoTypeForClass.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("field1")) {
                Assert.assertEquals(INT_TYPE_INFO, field.getTypeInformation());
            } else
                if (name.equals("field2")) {
                    Assert.assertEquals(LONG_TYPE_INFO, field.getTypeInformation());
                } else
                    if (name.equals("field3")) {
                        Assert.assertEquals(STRING_TYPE_INFO, field.getTypeInformation());
                    } else
                        if (name.equals("key")) {
                            Assert.assertEquals(INT_TYPE_INFO, field.getTypeInformation());
                        } else {
                            Assert.fail(("Unexpected field " + field));
                        }



        }
    }

    /**
     * Test if the TypeExtractor is accepting untyped generics,
     * making them GenericTypes
     */
    @Test
    public void testPojoWithGenericsSomeFieldsGeneric() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.PojoWithGenerics.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
        PojoTypeInfo<?> pojoTypeForClass = ((PojoTypeInfo<?>) (typeForClass));
        for (int i = 0; i < (pojoTypeForClass.getArity()); i++) {
            PojoField field = pojoTypeForClass.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("field1")) {
                Assert.assertEquals(new GenericTypeInfo<Object>(Object.class), field.getTypeInformation());
            } else
                if (name.equals("field2")) {
                    Assert.assertEquals(new GenericTypeInfo<Object>(Object.class), field.getTypeInformation());
                } else
                    if (name.equals("key")) {
                        Assert.assertEquals(INT_TYPE_INFO, field.getTypeInformation());
                    } else {
                        Assert.fail(("Unexpected field " + field));
                    }


        }
    }

    @Test
    public void testPojoWithComplexHierarchy() {
        TypeInformation<?> typeForClass = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.ComplexHierarchyTop.class);
        Assert.assertTrue((typeForClass instanceof PojoTypeInfo<?>));
        PojoTypeInfo<?> pojoTypeForClass = ((PojoTypeInfo<?>) (typeForClass));
        for (int i = 0; i < (pojoTypeForClass.getArity()); i++) {
            PojoField field = pojoTypeForClass.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("field1")) {
                Assert.assertTrue(((field.getTypeInformation()) instanceof PojoTypeInfo<?>));// From tuple is pojo (not tuple type!)

            } else
                if (name.equals("field2")) {
                    Assert.assertTrue(((field.getTypeInformation()) instanceof TupleTypeInfo<?>));
                    Assert.assertTrue(((TupleTypeInfo<?>) (field.getTypeInformation())).getTypeAt(0).equals(STRING_TYPE_INFO));
                } else
                    if (name.equals("key")) {
                        Assert.assertEquals(INT_TYPE_INFO, field.getTypeInformation());
                    } else {
                        Assert.fail(("Unexpected field " + field));
                    }


        }
    }

    public static class MyMapper<T> implements MapFunction<PojoTypeExtractionTest.PojoWithGenerics<Long, T>, PojoTypeExtractionTest.PojoWithGenerics<T, T>> {
        private static final long serialVersionUID = 1L;

        @Override
        public PojoTypeExtractionTest.PojoWithGenerics<T, T> map(PojoTypeExtractionTest.PojoWithGenerics<Long, T> value) throws Exception {
            return null;
        }
    }

    @Test
    public void testGenericPojoTypeInference1() {
        PojoTypeExtractionTest.MyMapper<String> function = new PojoTypeExtractionTest.MyMapper<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoWithGenerics<Long, String>>() {}));
        Assert.assertTrue((ti instanceof PojoTypeInfo<?>));
        PojoTypeInfo<?> pti = ((PojoTypeInfo<?>) (ti));
        for (int i = 0; i < (pti.getArity()); i++) {
            PojoField field = pti.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("field1")) {
                Assert.assertEquals(STRING_TYPE_INFO, field.getTypeInformation());
            } else
                if (name.equals("field2")) {
                    Assert.assertEquals(STRING_TYPE_INFO, field.getTypeInformation());
                } else
                    if (name.equals("key")) {
                        Assert.assertEquals(INT_TYPE_INFO, field.getTypeInformation());
                    } else {
                        Assert.fail(("Unexpected field " + field));
                    }


        }
    }

    public static class PojoTuple<A, B, C> extends Tuple3<B, C, Long> {
        private static final long serialVersionUID = 1L;

        public A extraField;
    }

    public static class MyMapper2<D, E> implements MapFunction<Tuple2<E, D>, PojoTypeExtractionTest.PojoTuple<E, D, D>> {
        private static final long serialVersionUID = 1L;

        @Override
        public PojoTypeExtractionTest.PojoTuple<E, D, D> map(Tuple2<E, D> value) throws Exception {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference2() {
        PojoTypeExtractionTest.MyMapper2<Boolean, Character> function = new PojoTypeExtractionTest.MyMapper2<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<Tuple2<Character, Boolean>>() {}));
        Assert.assertTrue((ti instanceof PojoTypeInfo<?>));
        PojoTypeInfo<?> pti = ((PojoTypeInfo<?>) (ti));
        for (int i = 0; i < (pti.getArity()); i++) {
            PojoField field = pti.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("extraField")) {
                Assert.assertEquals(CHAR_TYPE_INFO, field.getTypeInformation());
            } else
                if (name.equals("f0")) {
                    Assert.assertEquals(BOOLEAN_TYPE_INFO, field.getTypeInformation());
                } else
                    if (name.equals("f1")) {
                        Assert.assertEquals(BOOLEAN_TYPE_INFO, field.getTypeInformation());
                    } else
                        if (name.equals("f2")) {
                            Assert.assertEquals(LONG_TYPE_INFO, field.getTypeInformation());
                        } else {
                            Assert.fail(("Unexpected field " + field));
                        }



        }
    }

    public static class MyMapper3<D, E> implements MapFunction<PojoTypeExtractionTest.PojoTuple<E, D, D>, Tuple2<E, D>> {
        private static final long serialVersionUID = 1L;

        @Override
        public Tuple2<E, D> map(PojoTypeExtractionTest.PojoTuple<E, D, D> value) throws Exception {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference3() {
        PojoTypeExtractionTest.MyMapper3<Boolean, Character> function = new PojoTypeExtractionTest.MyMapper3<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoTuple<Character, Boolean, Boolean>>() {}));
        Assert.assertTrue((ti instanceof TupleTypeInfo<?>));
        TupleTypeInfo<?> tti = ((TupleTypeInfo<?>) (ti));
        Assert.assertEquals(CHAR_TYPE_INFO, tti.getTypeAt(0));
        Assert.assertEquals(BOOLEAN_TYPE_INFO, tti.getTypeAt(1));
    }

    public static class PojoWithParameterizedFields1<Z> {
        public Tuple2<Z, Z> field;
    }

    public static class MyMapper4<A> implements MapFunction<PojoTypeExtractionTest.PojoWithParameterizedFields1<A>, A> {
        private static final long serialVersionUID = 1L;

        @Override
        public A map(PojoTypeExtractionTest.PojoWithParameterizedFields1<A> value) throws Exception {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference4() {
        PojoTypeExtractionTest.MyMapper4<Byte> function = new PojoTypeExtractionTest.MyMapper4<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoWithParameterizedFields1<Byte>>() {}));
        Assert.assertEquals(BYTE_TYPE_INFO, ti);
    }

    public static class PojoWithParameterizedFields2<Z> {
        public PojoTypeExtractionTest.PojoWithGenerics<Z, Z> field;
    }

    public static class MyMapper5<A> implements MapFunction<PojoTypeExtractionTest.PojoWithParameterizedFields2<A>, A> {
        private static final long serialVersionUID = 1L;

        @Override
        public A map(PojoTypeExtractionTest.PojoWithParameterizedFields2<A> value) throws Exception {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference5() {
        PojoTypeExtractionTest.MyMapper5<Byte> function = new PojoTypeExtractionTest.MyMapper5<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoWithParameterizedFields2<Byte>>() {}));
        Assert.assertEquals(BYTE_TYPE_INFO, ti);
    }

    public static class PojoWithParameterizedFields3<Z> {
        public Z[] field;
    }

    public static class MyMapper6<A> implements MapFunction<PojoTypeExtractionTest.PojoWithParameterizedFields3<A>, A> {
        private static final long serialVersionUID = 1L;

        @Override
        public A map(PojoTypeExtractionTest.PojoWithParameterizedFields3<A> value) throws Exception {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference6() {
        PojoTypeExtractionTest.MyMapper6<Integer> function = new PojoTypeExtractionTest.MyMapper6<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoWithParameterizedFields3<Integer>>() {}));
        Assert.assertEquals(INT_TYPE_INFO, ti);
    }

    public static class MyMapper7<A> implements MapFunction<PojoTypeExtractionTest.PojoWithParameterizedFields4<A>, A> {
        private static final long serialVersionUID = 1L;

        @Override
        public A map(PojoTypeExtractionTest.PojoWithParameterizedFields4<A> value) throws Exception {
            return null;
        }
    }

    public static class PojoWithParameterizedFields4<Z> {
        public Tuple1<Z>[] field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGenericPojoTypeInference7() {
        PojoTypeExtractionTest.MyMapper7<Integer> function = new PojoTypeExtractionTest.MyMapper7<>();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<PojoTypeExtractionTest.PojoWithParameterizedFields4<Integer>>() {}));
        Assert.assertEquals(INT_TYPE_INFO, ti);
    }

    public static class RecursivePojo1 {
        public PojoTypeExtractionTest.RecursivePojo1 field;
    }

    public static class RecursivePojo2 {
        public Tuple1<PojoTypeExtractionTest.RecursivePojo2> field;
    }

    public static class RecursivePojo3 {
        public PojoTypeExtractionTest.NestedPojo field;
    }

    public static class NestedPojo {
        public PojoTypeExtractionTest.RecursivePojo3 field;
    }

    @Test
    public void testRecursivePojo1() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.RecursivePojo1.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getPojoFieldAt(0).getTypeInformation().getClass());
    }

    @Test
    public void testRecursivePojo2() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.RecursivePojo2.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        PojoField pf = ((PojoTypeInfo) (ti)).getPojoFieldAt(0);
        Assert.assertTrue(((pf.getTypeInformation()) instanceof TupleTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getTypeAt(0).getClass());
    }

    @Test
    public void testRecursivePojo3() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.RecursivePojo3.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        PojoField pf = ((PojoTypeInfo) (ti)).getPojoFieldAt(0);
        Assert.assertTrue(((pf.getTypeInformation()) instanceof PojoTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getPojoFieldAt(0).getTypeInformation().getClass());
    }

    public static class FooBarPojo {
        public int foo;

        public int bar;

        public FooBarPojo() {
        }
    }

    public static class DuplicateMapper implements MapFunction<PojoTypeExtractionTest.FooBarPojo, Tuple2<PojoTypeExtractionTest.FooBarPojo, PojoTypeExtractionTest.FooBarPojo>> {
        @Override
        public Tuple2<PojoTypeExtractionTest.FooBarPojo, PojoTypeExtractionTest.FooBarPojo> map(PojoTypeExtractionTest.FooBarPojo value) throws Exception {
            return null;
        }
    }

    @Test
    public void testDualUseOfPojo() {
        MapFunction<?, ?> function = new PojoTypeExtractionTest.DuplicateMapper();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(function, ((TypeInformation) (TypeExtractor.createTypeInfo(PojoTypeExtractionTest.FooBarPojo.class))));
        Assert.assertTrue((ti instanceof TupleTypeInfo));
        TupleTypeInfo<?> tti = ((TupleTypeInfo) (ti));
        Assert.assertTrue(((tti.getTypeAt(0)) instanceof PojoTypeInfo));
        Assert.assertTrue(((tti.getTypeAt(1)) instanceof PojoTypeInfo));
    }

    public static class PojoWithRecursiveGenericField<K, V> {
        public PojoTypeExtractionTest.PojoWithRecursiveGenericField<K, V> parent;

        public PojoWithRecursiveGenericField() {
        }
    }

    @Test
    public void testPojoWithRecursiveGenericField() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.PojoWithRecursiveGenericField.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getPojoFieldAt(0).getTypeInformation().getClass());
    }

    public static class MutualPojoA {
        public PojoTypeExtractionTest.MutualPojoB field;
    }

    public static class MutualPojoB {
        public PojoTypeExtractionTest.MutualPojoA field;
    }

    @Test
    public void testPojosWithMutualRecursion() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.MutualPojoB.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        TypeInformation<?> pti = getPojoFieldAt(0).getTypeInformation();
        Assert.assertTrue((pti instanceof PojoTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getPojoFieldAt(0).getTypeInformation().getClass());
    }

    public static class Container<T> {
        public T field;
    }

    public static class MyType extends PojoTypeExtractionTest.Container<PojoTypeExtractionTest.Container<Object>> {}

    @Test
    public void testRecursivePojoWithTypeVariable() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(PojoTypeExtractionTest.MyType.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        TypeInformation<?> pti = getPojoFieldAt(0).getTypeInformation();
        Assert.assertTrue((pti instanceof PojoTypeInfo));
        Assert.assertEquals(GenericTypeInfo.class, getPojoFieldAt(0).getTypeInformation().getClass());
    }

    /**
     * POJO generated using Lombok.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TestLombok {
        private int age = 10;

        private String name;
    }

    @Test
    public void testLombokPojo() {
        TypeInformation<PojoTypeExtractionTest.TestLombok> ti = TypeExtractor.getForClass(PojoTypeExtractionTest.TestLombok.class);
        Assert.assertTrue((ti instanceof PojoTypeInfo));
        PojoTypeInfo<PojoTypeExtractionTest.TestLombok> pti = ((PojoTypeInfo<PojoTypeExtractionTest.TestLombok>) (ti));
        Assert.assertEquals(INT_TYPE_INFO, pti.getTypeAt(0));
        Assert.assertEquals(STRING_TYPE_INFO, pti.getTypeAt(1));
    }
}

