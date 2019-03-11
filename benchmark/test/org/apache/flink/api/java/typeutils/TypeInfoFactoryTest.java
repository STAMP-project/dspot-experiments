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


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.InvalidTypesException;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for extracting {@link org.apache.flink.api.common.typeinfo.TypeInformation} from types
 * using a {@link org.apache.flink.api.common.typeinfo.TypeInfoFactory}
 */
public class TypeInfoFactoryTest {
    @Test
    public void testSimpleType() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(TypeInfoFactoryTest.IntLike.class);
        Assert.assertEquals(BasicTypeInfo.INT_TYPE_INFO, ti);
        ti = TypeExtractor.getForClass(TypeInfoFactoryTest.IntLike.class);
        Assert.assertEquals(BasicTypeInfo.INT_TYPE_INFO, ti);
        ti = TypeExtractor.getForObject(new TypeInfoFactoryTest.IntLike());
        Assert.assertEquals(BasicTypeInfo.INT_TYPE_INFO, ti);
    }

    @Test
    public void testMyEitherGenericType() {
        MapFunction<Boolean, TypeInfoFactoryTest.MyEither<Boolean, String>> f = new TypeInfoFactoryTest.MyEitherMapper();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(f, BasicTypeInfo.BOOLEAN_TYPE_INFO);
        Assert.assertTrue((ti instanceof EitherTypeInfo));
        EitherTypeInfo eti = ((EitherTypeInfo) (ti));
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, eti.getLeftType());
        Assert.assertEquals(BasicTypeInfo.STRING_TYPE_INFO, eti.getRightType());
    }

    @Test
    public void testMyOptionGenericType() {
        TypeInformation<TypeInfoFactoryTest.MyOption<Tuple2<Boolean, String>>> inTypeInfo = new TypeInfoFactoryTest.MyOptionTypeInfo(new TupleTypeInfo<Tuple2<Boolean, String>>(BasicTypeInfo.BOOLEAN_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO));
        MapFunction<TypeInfoFactoryTest.MyOption<Tuple2<Boolean, String>>, TypeInfoFactoryTest.MyOption<Tuple2<Boolean, Boolean>>> f = new TypeInfoFactoryTest.MyOptionMapper();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(f, inTypeInfo);
        Assert.assertTrue((ti instanceof TypeInfoFactoryTest.MyOptionTypeInfo));
        TypeInfoFactoryTest.MyOptionTypeInfo oti = ((TypeInfoFactoryTest.MyOptionTypeInfo) (ti));
        Assert.assertTrue(((oti.getInnerType()) instanceof TupleTypeInfo));
        TupleTypeInfo tti = ((TupleTypeInfo) (oti.getInnerType()));
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, tti.getTypeAt(0));
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, tti.getTypeAt(1));
    }

    @Test
    public void testMyTuple() {
        TypeInformation<Tuple1<TypeInfoFactoryTest.MyTuple<Double, String>>> inTypeInfo = new TupleTypeInfo(new TypeInfoFactoryTest.MyTupleTypeInfo(BasicTypeInfo.DOUBLE_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO));
        MapFunction<Tuple1<TypeInfoFactoryTest.MyTuple<Double, String>>, Tuple1<TypeInfoFactoryTest.MyTuple<Boolean, Double>>> f = new TypeInfoFactoryTest.MyTupleMapperL2();
        TypeInformation<?> ti = TypeExtractor.getMapReturnTypes(f, inTypeInfo);
        Assert.assertTrue((ti instanceof TupleTypeInfo));
        TupleTypeInfo<?> tti = ((TupleTypeInfo<?>) (ti));
        Assert.assertTrue(((tti.getTypeAt(0)) instanceof TypeInfoFactoryTest.MyTupleTypeInfo));
        TypeInfoFactoryTest.MyTupleTypeInfo mtti = ((TypeInfoFactoryTest.MyTupleTypeInfo) (tti.getTypeAt(0)));
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, mtti.getField0());
        Assert.assertEquals(BasicTypeInfo.DOUBLE_TYPE_INFO, mtti.getField1());
    }

    @Test
    public void testMyTupleHierarchy() {
        TypeInformation<?> ti = TypeExtractor.createTypeInfo(TypeInfoFactoryTest.MyTuple2.class);
        Assert.assertTrue((ti instanceof TypeInfoFactoryTest.MyTupleTypeInfo));
        TypeInfoFactoryTest.MyTupleTypeInfo<?, ?> mtti = ((TypeInfoFactoryTest.MyTupleTypeInfo) (ti));
        Assert.assertEquals(BasicTypeInfo.STRING_TYPE_INFO, mtti.getField0());
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, mtti.getField1());
    }

    @Test
    public void testMyTupleHierarchyWithInference() {
        TypeInformation<Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple1<Float>>>> inTypeInfo = new TupleTypeInfo(new TypeInfoFactoryTest.MyTupleTypeInfo(new TupleTypeInfo<Tuple1<Float>>(BasicTypeInfo.FLOAT_TYPE_INFO), BasicTypeInfo.BOOLEAN_TYPE_INFO));
        MapFunction<Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple1<Float>>>, Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple2<Float, String>>>> f = new TypeInfoFactoryTest.MyTuple3Mapper();
        TypeInformation ti = TypeExtractor.getMapReturnTypes(f, inTypeInfo);
        Assert.assertTrue((ti instanceof TupleTypeInfo));
        TupleTypeInfo<?> tti = ((TupleTypeInfo) (ti));
        Assert.assertTrue(((tti.getTypeAt(0)) instanceof TypeInfoFactoryTest.MyTupleTypeInfo));
        TypeInfoFactoryTest.MyTupleTypeInfo mtti = ((TypeInfoFactoryTest.MyTupleTypeInfo) (tti.getTypeAt(0)));
        Assert.assertEquals(new TupleTypeInfo(BasicTypeInfo.FLOAT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO), mtti.getField0());
        Assert.assertEquals(BasicTypeInfo.BOOLEAN_TYPE_INFO, mtti.getField1());
    }

    @Test(expected = InvalidTypesException.class)
    public void testMissingTypeInfo() {
        MapFunction f = new TypeInfoFactoryTest.MyFaultyMapper();
        TypeExtractor.getMapReturnTypes(f, BasicTypeInfo.INT_TYPE_INFO);
    }

    @Test(expected = InvalidTypesException.class)
    public void testMissingTypeInference() {
        MapFunction f = new TypeInfoFactoryTest.MyFaultyMapper2();
        TypeExtractor.getMapReturnTypes(f, new TypeInfoFactoryTest.MyFaultyTypeInfo());
    }

    // --------------------------------------------------------------------------------------------
    // Utilities
    // --------------------------------------------------------------------------------------------
    public static class MyTuple3Mapper<Y> implements MapFunction<Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple1<Y>>>, Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple2<Y, String>>>> {
        @Override
        public Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple2<Y, String>>> map(Tuple1<TypeInfoFactoryTest.MyTuple3<Tuple1<Y>>> value) throws Exception {
            return null;
        }
    }

    // empty
    public static class MyTuple3<T> extends TypeInfoFactoryTest.MyTuple<T, Boolean> {}

    // empty
    public static class MyTuple2 extends TypeInfoFactoryTest.MyTuple<String, Boolean> {}

    public static class MyFaultyMapper2<T> implements MapFunction<TypeInfoFactoryTest.MyFaulty<T>, TypeInfoFactoryTest.MyFaulty<T>> {
        @Override
        public TypeInfoFactoryTest.MyFaulty<T> map(TypeInfoFactoryTest.MyFaulty<T> value) throws Exception {
            return null;
        }
    }

    public static class MyFaultyMapper<T> implements MapFunction<T, TypeInfoFactoryTest.MyFaulty<T>> {
        @Override
        public TypeInfoFactoryTest.MyFaulty<T> map(T value) throws Exception {
            return null;
        }
    }

    // empty
    @TypeInfo(TypeInfoFactoryTest.FaultyTypeInfoFactory.class)
    public static class MyFaulty<Y> {}

    public static class FaultyTypeInfoFactory extends TypeInfoFactory {
        @Override
        public TypeInformation createTypeInfo(Type t, Map genericParameters) {
            return null;
        }
    }

    public static class MyFaultyTypeInfo extends TypeInformation<TypeInfoFactoryTest.MyFaulty> {
        @Override
        public boolean isBasicType() {
            return false;
        }

        @Override
        public boolean isTupleType() {
            return false;
        }

        @Override
        public int getArity() {
            return 0;
        }

        @Override
        public int getTotalFields() {
            return 0;
        }

        @Override
        public Class<TypeInfoFactoryTest.MyFaulty> getTypeClass() {
            return null;
        }

        @Override
        public boolean isKeyType() {
            return false;
        }

        @Override
        public TypeSerializer<TypeInfoFactoryTest.MyFaulty> createSerializer(ExecutionConfig config) {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean canEqual(Object obj) {
            return false;
        }
    }

    public static class MyTupleMapperL1<A, B> implements MapFunction<Tuple1<TypeInfoFactoryTest.MyTuple<A, String>>, Tuple1<TypeInfoFactoryTest.MyTuple<B, A>>> {
        @Override
        public Tuple1<TypeInfoFactoryTest.MyTuple<B, A>> map(Tuple1<TypeInfoFactoryTest.MyTuple<A, String>> value) throws Exception {
            return null;
        }
    }

    // empty
    public static class MyTupleMapperL2<C> extends TypeInfoFactoryTest.MyTupleMapperL1<C, Boolean> {}

    // empty
    @TypeInfo(TypeInfoFactoryTest.MyTupleTypeInfoFactory.class)
    public static class MyTuple<T0, T1> {}

    public static class MyTupleTypeInfoFactory extends TypeInfoFactory<TypeInfoFactoryTest.MyTuple> {
        @Override
        @SuppressWarnings("unchecked")
        public TypeInformation<TypeInfoFactoryTest.MyTuple> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
            return new TypeInfoFactoryTest.MyTupleTypeInfo(genericParameters.get("T0"), genericParameters.get("T1"));
        }
    }

    public static class MyTupleTypeInfo<T0, T1> extends TypeInformation<TypeInfoFactoryTest.MyTuple<T0, T1>> {
        private TypeInformation field0;

        private TypeInformation field1;

        public TypeInformation getField0() {
            return field0;
        }

        public TypeInformation getField1() {
            return field1;
        }

        public MyTupleTypeInfo(TypeInformation field0, TypeInformation field1) {
            this.field0 = field0;
            this.field1 = field1;
        }

        @Override
        public boolean isBasicType() {
            return false;
        }

        @Override
        public boolean isTupleType() {
            return false;
        }

        @Override
        public int getArity() {
            return 0;
        }

        @Override
        public int getTotalFields() {
            return 0;
        }

        @Override
        public Class<TypeInfoFactoryTest.MyTuple<T0, T1>> getTypeClass() {
            return null;
        }

        @Override
        public boolean isKeyType() {
            return false;
        }

        @Override
        public TypeSerializer<TypeInfoFactoryTest.MyTuple<T0, T1>> createSerializer(ExecutionConfig config) {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean canEqual(Object obj) {
            return false;
        }

        @Override
        public Map<String, TypeInformation<?>> getGenericParameters() {
            Map<String, TypeInformation<?>> map = new HashMap<>(2);
            map.put("T0", field0);
            map.put("T1", field1);
            return map;
        }
    }

    public static class MyOptionMapper<T> implements MapFunction<TypeInfoFactoryTest.MyOption<Tuple2<T, String>>, TypeInfoFactoryTest.MyOption<Tuple2<T, T>>> {
        @Override
        public TypeInfoFactoryTest.MyOption<Tuple2<T, T>> map(TypeInfoFactoryTest.MyOption<Tuple2<T, String>> value) throws Exception {
            return null;
        }
    }

    // empty
    @TypeInfo(TypeInfoFactoryTest.MyOptionTypeInfoFactory.class)
    public static class MyOption<T> {}

    public static class MyOptionTypeInfoFactory<T> extends TypeInfoFactory<TypeInfoFactoryTest.MyOption<T>> {
        @Override
        @SuppressWarnings("unchecked")
        public TypeInformation<TypeInfoFactoryTest.MyOption<T>> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParams) {
            return new TypeInfoFactoryTest.MyOptionTypeInfo(genericParams.get("T"));
        }
    }

    public static class MyOptionTypeInfo<T> extends TypeInformation<TypeInfoFactoryTest.MyOption<T>> {
        private final TypeInformation<T> innerType;

        public MyOptionTypeInfo(TypeInformation<T> innerType) {
            this.innerType = innerType;
        }

        public TypeInformation<T> getInnerType() {
            return innerType;
        }

        @Override
        public boolean isBasicType() {
            return false;
        }

        @Override
        public boolean isTupleType() {
            return false;
        }

        @Override
        public int getArity() {
            return 0;
        }

        @Override
        public int getTotalFields() {
            return 1;
        }

        @Override
        public Class<TypeInfoFactoryTest.MyOption<T>> getTypeClass() {
            return null;
        }

        @Override
        public boolean isKeyType() {
            return false;
        }

        @Override
        public TypeSerializer<TypeInfoFactoryTest.MyOption<T>> createSerializer(ExecutionConfig config) {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean canEqual(Object obj) {
            return false;
        }

        @Override
        public Map<String, TypeInformation<?>> getGenericParameters() {
            Map<String, TypeInformation<?>> map = new HashMap<>(1);
            map.put("T", innerType);
            return map;
        }
    }

    public static class MyEitherMapper<T> implements MapFunction<T, TypeInfoFactoryTest.MyEither<T, String>> {
        @Override
        public TypeInfoFactoryTest.MyEither<T, String> map(T value) throws Exception {
            return null;
        }
    }

    // empty
    @TypeInfo(TypeInfoFactoryTest.MyEitherTypeInfoFactory.class)
    public static class MyEither<A, B> {}

    public static class MyEitherTypeInfoFactory<A, B> extends TypeInfoFactory<TypeInfoFactoryTest.MyEither<A, B>> {
        @Override
        @SuppressWarnings("unchecked")
        public TypeInformation<TypeInfoFactoryTest.MyEither<A, B>> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParams) {
            return new EitherTypeInfo(genericParams.get("A"), genericParams.get("B"));
        }
    }

    // empty
    @TypeInfo(TypeInfoFactoryTest.IntLikeTypeInfoFactory.class)
    public static class IntLike {}

    public static class IntLikeTypeInfoFactory extends TypeInfoFactory<TypeInfoFactoryTest.IntLike> {
        @Override
        @SuppressWarnings("unchecked")
        public TypeInformation<TypeInfoFactoryTest.IntLike> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParams) {
            return ((TypeInformation) (BasicTypeInfo.INT_TYPE_INFO));
        }
    }
}

