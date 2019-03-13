/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class ArrayIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private static Class<?> classWithArrayProperties;

    @Test
    public void nonUniqueArraysAreLists() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getNonUniqueArray");
        Assert.assertThat(getterMethod.getReturnType().getName(), Matchers.is(List.class.getName()));
        Assert.assertThat(getterMethod.getGenericReturnType(), Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        Type genericType = ((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0];
        Assert.assertThat(genericType, Matchers.is(Matchers.instanceOf(Class.class)));
        Assert.assertThat(((Class<?>) (genericType)).getName(), Matchers.is(Integer.class.getName()));
    }

    @Test
    public void uniqueArraysAreSets() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getUniqueArray");
        Assert.assertThat(getterMethod.getReturnType().getName(), Matchers.is(Set.class.getName()));
        Assert.assertThat(getterMethod.getGenericReturnType(), Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        Type genericType = ((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0];
        Assert.assertThat(genericType, Matchers.is(Matchers.instanceOf(Class.class)));
        Assert.assertThat(((Class<?>) (genericType)).getName(), Matchers.is(Boolean.class.getName()));
    }

    @Test
    public void arraysAreNonUniqueByDefault() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getNonUniqueArrayByDefault");
        Assert.assertThat(getterMethod.getReturnType().getName(), Matchers.is(List.class.getName()));
    }

    @Test
    public void arraysCanHaveComplexTypes() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getComplexTypesArray");
        Assert.assertThat(getterMethod.getGenericReturnType(), Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        Type genericType = ((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0];
        Assert.assertThat(genericType, Matchers.is(Matchers.instanceOf(Class.class)));
        Assert.assertThat(((Class<?>) (genericType)).getName(), Matchers.is("com.example.ComplexTypesArray"));
    }

    @Test
    public void arrayItemTypeIsObjectByDefault() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getDefaultTypesArray");
        Assert.assertThat(getterMethod.getGenericReturnType(), Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        Type genericType = ((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0];
        Assert.assertThat(genericType, Matchers.is(Matchers.instanceOf(Class.class)));
        Assert.assertThat(((Class<?>) (genericType)).getName(), Matchers.is(Object.class.getName()));
    }

    @Test
    public void arraysCanBeMultiDimensional() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getMultiDimensionalArray");
        // assert List
        Assert.assertThat(getterMethod.getReturnType().getName(), Matchers.is(List.class.getName()));
        Assert.assertThat(getterMethod.getGenericReturnType(), Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        // assert List<List>
        Type genericType = ((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0];
        Assert.assertThat(genericType, Matchers.is(Matchers.instanceOf(ParameterizedType.class)));
        Assert.assertThat(((Class<?>) (((ParameterizedType) (genericType)).getRawType())).getName(), Matchers.is(List.class.getName()));
        // assert List<List<Object>>
        Type itemsType = ((ParameterizedType) (genericType)).getActualTypeArguments()[0];
        Assert.assertThat(itemsType, Matchers.is(Matchers.instanceOf(Class.class)));
        Assert.assertThat(((Class<?>) (itemsType)).getName(), Matchers.is(Object.class.getName()));
    }

    @Test
    public void arrayItemTypeIsSingularFormOfPropertyName() throws NoSuchMethodException {
        // assert List<Thing>
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getThings");
        Class<?> genericType = ((Class<?>) (((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(genericType.getName(), Matchers.is("com.example.Thing"));
        // assert List<Thing>
        getterMethod = ArrayIT.classWithArrayProperties.getMethod("getWidgetList");
        genericType = ((Class<?>) (((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(genericType.getName(), Matchers.is("com.example.Widget"));
        getterMethod = ArrayIT.classWithArrayProperties.getMethod("getAnimalList");
        genericType = ((Class<?>) (((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(genericType.getName(), Matchers.is("com.example.Animal"));
    }

    @Test
    public void arrayItemTypeIsSingularFormOfPropertyNameWhenNameEndsInIES() throws NoSuchMethodException {
        Method getterMethod = ArrayIT.classWithArrayProperties.getMethod("getProperties");
        // assert List<Property>
        Class<?> genericType = ((Class<?>) (((ParameterizedType) (getterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(genericType.getName(), Matchers.is("com.example.Property"));
    }

    /**
     *
     *
     * @see <a
    href="http://code.google.com/p/jsonschema2pojo/issues/detail?id=76">issue
    76</a>
     */
    @Test
    public void propertiesThatReferenceAnArraySchemaAlwaysHaveCorrectCollectionType() throws NoSuchMethodException {
        Method array1GetterMethod = ArrayIT.classWithArrayProperties.getMethod("getRefToArray1");
        // assert List<RootArrayItem>
        Class<?> array1GenericType = ((Class<?>) (((ParameterizedType) (array1GetterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(array1GenericType.getName(), Matchers.is("com.example.RootArrayItem"));
        Method array2GetterMethod = ArrayIT.classWithArrayProperties.getMethod("getRefToArray2");
        // assert List<RootArrayItem>
        Class<?> array2GenericType = ((Class<?>) (((ParameterizedType) (array2GetterMethod.getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(array2GenericType.getName(), Matchers.is("com.example.RootArrayItem"));
    }

    @Test
    public void uniqueArrayPreservesOrderJackson2() throws Exception {
        new ArrayIT.PreserveOrder("jackson2") {
            @Override
            protected Object roundTrip(Object original) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(mapper.writeValueAsString(original), original.getClass());
            }
        }.test();
    }

    @Test
    public void uniqueArrayPreservesOrderJackson1() throws Exception {
        new ArrayIT.PreserveOrder("jackson1") {
            @Override
            protected Object roundTrip(Object original) throws Exception {
                org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
                return mapper.readValue(mapper.writeValueAsString(original), original.getClass());
            }
        }.test();
    }

    abstract class PreserveOrder {
        String annotationStyle;

        public PreserveOrder(String annotationStyle) {
            this.annotationStyle = annotationStyle;
        }

        public void test() throws Exception {
            ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/array/typeWithArrayProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", annotationStyle));
            Class<?> jackson1Class = resultsClassLoader.loadClass("com.example.TypeWithArrayProperties");
            Object original = jackson1Class.newInstance();
            @SuppressWarnings("unchecked")
            Set<Integer> expected = ((Set<Integer>) (jackson1Class.getMethod("getUniqueIntegerArray").invoke(original)));
            expected.addAll(Arrays.asList(1, 3, 5, 7, 9, 2, 4, 6, 8, 10));
            Object roundTrip = roundTrip(original);
            @SuppressWarnings("unchecked")
            Set<Integer> actual = ((Set<Integer>) (jackson1Class.getMethod("getUniqueIntegerArray").invoke(roundTrip)));
            Iterator<Integer> expectedItr = expected.iterator();
            Iterator<Integer> actualItr = actual.iterator();
            while (expectedItr.hasNext()) {
                Assert.assertThat("The collection order is stable", actualItr.next(), Matchers.equalTo(expectedItr.next()));
            } 
            Assert.assertThat("All of the values were examined", actualItr.hasNext(), Matchers.equalTo(false));
        }

        protected abstract Object roundTrip(Object original) throws Exception;
    }
}

