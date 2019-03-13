/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.core;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GenericTypeResolverTests {
    @Test
    public void simpleInterfaceType() {
        Assert.assertEquals(String.class, resolveTypeArgument(GenericTypeResolverTests.MySimpleInterfaceType.class, GenericTypeResolverTests.MyInterfaceType.class));
    }

    @Test
    public void simpleCollectionInterfaceType() {
        Assert.assertEquals(Collection.class, resolveTypeArgument(GenericTypeResolverTests.MyCollectionInterfaceType.class, GenericTypeResolverTests.MyInterfaceType.class));
    }

    @Test
    public void simpleSuperclassType() {
        Assert.assertEquals(String.class, resolveTypeArgument(GenericTypeResolverTests.MySimpleSuperclassType.class, GenericTypeResolverTests.MySuperclassType.class));
    }

    @Test
    public void simpleCollectionSuperclassType() {
        Assert.assertEquals(Collection.class, resolveTypeArgument(GenericTypeResolverTests.MyCollectionSuperclassType.class, GenericTypeResolverTests.MySuperclassType.class));
    }

    @Test
    public void nullIfNotResolvable() {
        GenericTypeResolverTests.GenericClass<String> obj = new GenericTypeResolverTests.GenericClass<>();
        Assert.assertNull(resolveTypeArgument(obj.getClass(), GenericTypeResolverTests.GenericClass.class));
    }

    @Test
    public void methodReturnTypes() {
        Assert.assertEquals(Integer.class, resolveReturnTypeArgument(findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "integer"), GenericTypeResolverTests.MyInterfaceType.class));
        Assert.assertEquals(String.class, resolveReturnTypeArgument(findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "string"), GenericTypeResolverTests.MyInterfaceType.class));
        Assert.assertEquals(null, resolveReturnTypeArgument(findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "raw"), GenericTypeResolverTests.MyInterfaceType.class));
        Assert.assertEquals(null, resolveReturnTypeArgument(findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "object"), GenericTypeResolverTests.MyInterfaceType.class));
    }

    @Test
    public void testResolveType() {
        Method intMessageMethod = findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "readIntegerInputMessage", GenericTypeResolverTests.MyInterfaceType.class);
        MethodParameter intMessageMethodParam = new MethodParameter(intMessageMethod, 0);
        Assert.assertEquals(GenericTypeResolverTests.MyInterfaceType.class, resolveType(intMessageMethodParam.getGenericParameterType(), new HashMap()));
        Method intArrMessageMethod = findMethod(GenericTypeResolverTests.MyTypeWithMethods.class, "readIntegerArrayInputMessage", GenericTypeResolverTests.MyInterfaceType[].class);
        MethodParameter intArrMessageMethodParam = new MethodParameter(intArrMessageMethod, 0);
        Assert.assertEquals(GenericTypeResolverTests.MyInterfaceType[].class, resolveType(intArrMessageMethodParam.getGenericParameterType(), new HashMap()));
        Method genericArrMessageMethod = findMethod(GenericTypeResolverTests.MySimpleTypeWithMethods.class, "readGenericArrayInputMessage", Object[].class);
        MethodParameter genericArrMessageMethodParam = new MethodParameter(genericArrMessageMethod, 0);
        Map<TypeVariable, Type> varMap = getTypeVariableMap(GenericTypeResolverTests.MySimpleTypeWithMethods.class);
        Assert.assertEquals(Integer[].class, resolveType(genericArrMessageMethodParam.getGenericParameterType(), varMap));
    }

    @Test
    public void testBoundParameterizedType() {
        Assert.assertEquals(GenericTypeResolverTests.B.class, resolveTypeArgument(GenericTypeResolverTests.TestImpl.class, GenericTypeResolverTests.TestIfc.class));
    }

    @Test
    public void testGetTypeVariableMap() throws Exception {
        Map<TypeVariable, Type> map;
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.MySimpleInterfaceType.class);
        Assert.assertThat(map.toString(), equalTo("{T=class java.lang.String}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.MyCollectionInterfaceType.class);
        Assert.assertThat(map.toString(), equalTo("{T=java.util.Collection<java.lang.String>}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.MyCollectionSuperclassType.class);
        Assert.assertThat(map.toString(), equalTo("{T=java.util.Collection<java.lang.String>}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.MySimpleTypeWithMethods.class);
        Assert.assertThat(map.toString(), equalTo("{T=class java.lang.Integer}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.TopLevelClass.class);
        Assert.assertThat(map.toString(), equalTo("{}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.TypedTopLevelClass.class);
        Assert.assertThat(map.toString(), equalTo("{T=class java.lang.Integer}"));
        map = GenericTypeResolver.GenericTypeResolver.getTypeVariableMap(GenericTypeResolverTests.TypedTopLevelClass.TypedNested.class);
        Assert.assertThat(map.size(), equalTo(2));
        Type t = null;
        Type x = null;
        for (Map.Entry<TypeVariable, Type> entry : map.entrySet()) {
            if (entry.getKey().toString().equals("T")) {
                t = entry.getValue();
            } else {
                x = entry.getValue();
            }
        }
        Assert.assertThat(t, equalTo(((Type) (Integer.class))));
        Assert.assertThat(x, equalTo(((Type) (Long.class))));
    }

    // SPR-11030
    @Test
    public void getGenericsCannotBeResolved() throws Exception {
        Class<?>[] resolved = GenericTypeResolver.GenericTypeResolver.resolveTypeArguments(List.class, Iterable.class);
        Assert.assertNull(resolved);
    }

    // SPR-11052
    @Test
    public void getRawMapTypeCannotBeResolved() throws Exception {
        Class<?>[] resolved = GenericTypeResolver.GenericTypeResolver.resolveTypeArguments(Map.class, Map.class);
        Assert.assertNull(resolved);
    }

    // SPR-11044
    @Test
    public void getGenericsOnArrayFromParamCannotBeResolved() throws Exception {
        MethodParameter methodParameter = MethodParameter.forExecutable(GenericTypeResolverTests.WithArrayBase.class.getDeclaredMethod("array", Object[].class), 0);
        Class<?> resolved = GenericTypeResolver.GenericTypeResolver.resolveParameterType(methodParameter, GenericTypeResolverTests.WithArray.class);
        Assert.assertThat(resolved, equalTo(((Class<?>) (Object[].class))));
    }

    // SPR-11044
    @Test
    public void getGenericsOnArrayFromReturnCannotBeResolved() throws Exception {
        Class<?> resolved = GenericTypeResolver.GenericTypeResolver.resolveReturnType(GenericTypeResolverTests.WithArrayBase.class.getDeclaredMethod("array", Object[].class), GenericTypeResolverTests.WithArray.class);
        Assert.assertThat(resolved, equalTo(((Class<?>) (Object[].class))));
    }

    // SPR-11763
    @Test
    public void resolveIncompleteTypeVariables() {
        Class<?>[] resolved = GenericTypeResolver.GenericTypeResolver.resolveTypeArguments(GenericTypeResolverTests.IdFixingRepository.class, GenericTypeResolverTests.Repository.class);
        Assert.assertNotNull(resolved);
        Assert.assertEquals(2, resolved.length);
        Assert.assertEquals(Object.class, resolved[0]);
        Assert.assertEquals(Long.class, resolved[1]);
    }

    public interface MyInterfaceType<T> {}

    public class MySimpleInterfaceType implements GenericTypeResolverTests.MyInterfaceType<String> {}

    public class MyCollectionInterfaceType implements GenericTypeResolverTests.MyInterfaceType<Collection<String>> {}

    public abstract class MySuperclassType<T> {}

    public class MySimpleSuperclassType extends GenericTypeResolverTests.MySuperclassType<String> {}

    public class MyCollectionSuperclassType extends GenericTypeResolverTests.MySuperclassType<Collection<String>> {}

    public static class MyTypeWithMethods<T> {
        public GenericTypeResolverTests.MyInterfaceType<Integer> integer() {
            return null;
        }

        public GenericTypeResolverTests.MySimpleInterfaceType string() {
            return null;
        }

        public Object object() {
            return null;
        }

        public GenericTypeResolverTests.MyInterfaceType raw() {
            return null;
        }

        public String notParameterized() {
            return null;
        }

        public String notParameterizedWithArguments(Integer x, Boolean b) {
            return null;
        }

        /**
         * Simulates a factory method that wraps the supplied object in a proxy of the
         * same type.
         */
        public static <T> T createProxy(T object) {
            return null;
        }

        /**
         * Similar to {@link #createProxy(Object)} but adds an additional argument before
         * the argument of type {@code T}. Note that they may potentially be of the same
         * time when invoked!
         */
        public static <T> T createNamedProxy(String name, T object) {
            return null;
        }

        /**
         * Simulates factory methods found in libraries such as Mockito and EasyMock.
         */
        public static <MOCK> MOCK createMock(Class<MOCK> toMock) {
            return null;
        }

        /**
         * Similar to {@link #createMock(Class)} but adds an additional method argument
         * before the parameterized argument.
         */
        public static <T> T createNamedMock(String name, Class<T> toMock) {
            return null;
        }

        /**
         * Similar to {@link #createNamedMock(String, Class)} but adds an additional
         * parameterized type.
         */
        public static <V extends Object, T> T createVMock(V name, Class<T> toMock) {
            return null;
        }

        /**
         * Extract some value of the type supported by the interface (i.e., by a concrete,
         * non-generic implementation of the interface).
         */
        public static <T> T extractValueFrom(GenericTypeResolverTests.MyInterfaceType<T> myInterfaceType) {
            return null;
        }

        /**
         * Extract some magic value from the supplied map.
         */
        public static <K, V> V extractMagicValue(Map<K, V> map) {
            return null;
        }

        public void readIntegerInputMessage(GenericTypeResolverTests.MyInterfaceType<Integer> message) {
        }

        public void readIntegerArrayInputMessage(GenericTypeResolverTests.MyInterfaceType<Integer>[] message) {
        }

        public void readGenericArrayInputMessage(T[] message) {
        }
    }

    public static class MySimpleTypeWithMethods extends GenericTypeResolverTests.MyTypeWithMethods<Integer> {}

    static class GenericClass<T> {}

    class A {}

    class B<T> {}

    class TestIfc<T> {}

    class TestImpl<I extends GenericTypeResolverTests.A, T extends GenericTypeResolverTests.B<I>> extends GenericTypeResolverTests.TestIfc<T> {}

    static class TopLevelClass<T> {
        class Nested<X> {}
    }

    static class TypedTopLevelClass extends GenericTypeResolverTests.TopLevelClass<Integer> {
        class TypedNested extends GenericTypeResolverTests.TopLevelClass<Integer>.Nested<Long> {}
    }

    abstract static class WithArrayBase<T> {
        public abstract T[] array(T... args);
    }

    abstract static class WithArray<T> extends GenericTypeResolverTests.WithArrayBase<T> {}

    interface Repository<T, ID extends Serializable> {}

    interface IdFixingRepository<T> extends GenericTypeResolverTests.Repository<T, Long> {}
}

