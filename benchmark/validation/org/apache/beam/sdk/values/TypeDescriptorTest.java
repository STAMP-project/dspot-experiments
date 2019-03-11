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
package org.apache.beam.sdk.values;


import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Set;
import org.apache.beam.vendor.guava.v20_0.com.google.common.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for TypeDescriptor.
 */
@RunWith(JUnit4.class)
public class TypeDescriptorTest {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTypeDescriptorOfRawType() throws Exception {
        Assert.assertEquals(getRawType(), getRawType());
    }

    @Test
    public void testTypeDescriptorImmediate() throws Exception {
        Assert.assertEquals(Boolean.class, getRawType());
        Assert.assertEquals(Double.class, getRawType());
        Assert.assertEquals(Float.class, getRawType());
        Assert.assertEquals(Integer.class, getRawType());
        Assert.assertEquals(Long.class, getRawType());
        Assert.assertEquals(Short.class, getRawType());
        Assert.assertEquals(String.class, getRawType());
    }

    @Test
    public void testTypeDescriptorGeneric() throws Exception {
        TypeDescriptor<List<String>> descriptor = new TypeDescriptor<List<String>>() {};
        TypeToken<List<String>> token = new TypeToken<List<String>>() {};
        Assert.assertEquals(token.getType(), descriptor.getType());
    }

    private static class TypeRememberer<T> {
        public final TypeDescriptor<T> descriptorByClass;

        public final TypeDescriptor<T> descriptorByInstance;

        public TypeRememberer() {
            descriptorByClass = new TypeDescriptor<T>(getClass()) {};
            descriptorByInstance = new TypeDescriptor<T>(this) {};
        }
    }

    @Test
    public void testTypeDescriptorNested() throws Exception {
        TypeDescriptorTest.TypeRememberer<String> rememberer = new TypeDescriptorTest.TypeRememberer<String>() {};
        Assert.assertEquals(getType(), rememberer.descriptorByClass.getType());
        Assert.assertEquals(getType(), rememberer.descriptorByInstance.getType());
        TypeDescriptorTest.TypeRememberer<List<String>> genericRememberer = new TypeDescriptorTest.TypeRememberer<List<String>>() {};
        Assert.assertEquals(getType(), genericRememberer.descriptorByClass.getType());
        Assert.assertEquals(getType(), genericRememberer.descriptorByInstance.getType());
    }

    private static class Id<T> {
        // used via reflection
        @SuppressWarnings("unused")
        public T identity(T thingie) {
            return thingie;
        }
    }

    @Test
    public void testGetArgumentTypes() throws Exception {
        Method identity = TypeDescriptorTest.Id.class.getDeclaredMethod("identity", Object.class);
        TypeToken<TypeDescriptorTest.Id<String>> token = new TypeToken<TypeDescriptorTest.Id<String>>() {};
        TypeDescriptor<TypeDescriptorTest.Id<String>> descriptor = new TypeDescriptor<TypeDescriptorTest.Id<String>>() {};
        Assert.assertEquals(getType(), getType());
        TypeToken<TypeDescriptorTest.Id<List<String>>> genericToken = new TypeToken<TypeDescriptorTest.Id<List<String>>>() {};
        TypeDescriptor<TypeDescriptorTest.Id<List<String>>> genericDescriptor = new TypeDescriptor<TypeDescriptorTest.Id<List<String>>>() {};
        Assert.assertEquals(getType(), getType());
    }

    private static class TypeRemembererer<T1, T2> {
        public TypeDescriptor<T1> descriptor1;

        public TypeDescriptor<T2> descriptor2;

        public TypeRemembererer() {
            descriptor1 = new TypeDescriptor<T1>(getClass()) {};
            descriptor2 = new TypeDescriptor<T2>(getClass()) {};
        }
    }

    @Test
    public void testTypeDescriptorNested2() throws Exception {
        TypeDescriptorTest.TypeRemembererer<String, Integer> remembererer = new TypeDescriptorTest.TypeRemembererer<String, Integer>() {};
        Assert.assertEquals(getType(), remembererer.descriptor1.getType());
        Assert.assertEquals(getType(), remembererer.descriptor2.getType());
        TypeDescriptorTest.TypeRemembererer<List<String>, Set<Integer>> genericRemembererer = new TypeDescriptorTest.TypeRemembererer<List<String>, Set<Integer>>() {};
        Assert.assertEquals(getType(), genericRemembererer.descriptor1.getType());
        Assert.assertEquals(getType(), genericRemembererer.descriptor2.getType());
    }

    private static class GenericClass<BizzleT> {}

    @Test
    public void testGetTypeParameterGood() throws Exception {
        @SuppressWarnings("rawtypes")
        TypeVariable<Class<? super TypeDescriptorTest.GenericClass>> bizzleT = TypeDescriptor.of(TypeDescriptorTest.GenericClass.class).getTypeParameter("BizzleT");
        Assert.assertEquals(TypeDescriptorTest.GenericClass.class.getTypeParameters()[0], bizzleT);
    }

    @Test
    public void testGetTypeParameterBad() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("MerpleT");// just check that the message gives actionable details

        TypeDescriptor.of(TypeDescriptorTest.GenericClass.class).getTypeParameter("MerpleT");
    }

    private static class GenericMaker<T> {
        public TypeDescriptorTest.TypeRememberer<List<T>> getRememberer() {
            return new TypeDescriptorTest.TypeRememberer<List<T>>() {};
        }
    }

    private static class GenericMaker2<T> {
        public TypeDescriptorTest.GenericMaker<Set<T>> getGenericMaker() {
            return new TypeDescriptorTest.GenericMaker<Set<T>>() {};
        }
    }

    @Test
    public void testEnclosing() throws Exception {
        TypeDescriptorTest.TypeRememberer<List<String>> rememberer = new TypeDescriptorTest.GenericMaker<String>() {}.getRememberer();
        Assert.assertEquals(getType(), rememberer.descriptorByInstance.getType());
        // descriptorByClass *is not* able to find the type of T because it comes from the enclosing
        // instance of GenericMaker.
        // assertEquals(new TypeToken<List<T>>() {}.getType(), rememberer.descriptorByClass.getType());
    }

    @Test
    public void testEnclosing2() throws Exception {
        // If we don't override, the best we can get is List<Set<T>>
        // TypeRememberer<List<Set<String>>> rememberer =
        // new GenericMaker2<String>(){}.getGenericMaker().getRememberer();
        // assertNotEquals(
        // new TypeToken<List<Set<String>>>() {}.getType(),
        // rememberer.descriptorByInstance.getType());
        // If we've overridden the getGenericMaker we can determine the types.
        TypeDescriptorTest.TypeRememberer<List<Set<String>>> rememberer = new TypeDescriptorTest.GenericMaker2<String>() {
            @Override
            public TypeDescriptorTest.GenericMaker<Set<String>> getGenericMaker() {
                return new TypeDescriptorTest.GenericMaker<Set<String>>() {};
            }
        }.getGenericMaker().getRememberer();
        Assert.assertEquals(getType(), rememberer.descriptorByInstance.getType());
    }

    @Test
    public void testWhere() throws Exception {
        useWhereMethodToDefineTypeParam(new TypeDescriptor<String>() {});
    }
}

