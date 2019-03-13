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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class DefaultIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private static Class<?> classWithDefaults;

    @Test
    public void emptyStringPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getEmptyStringWithDefault");
        Assert.assertThat(((String) (getter.invoke(instance))), is(equalTo("")));
    }

    @Test
    public void stringPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getStringWithDefault");
        Assert.assertThat(((String) (getter.invoke(instance))), is(equalTo("abc")));
    }

    @Test
    public void integerPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getIntegerWithDefault");
        Assert.assertThat(((Integer) (getter.invoke(instance))), is(equalTo(1337)));
    }

    @Test
    public void integerPropertyHasCorrectDefaultBigIntegerValue() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/default/default.json", "com.example", CodeGenerationHelper.config("useBigIntegers", true));
        Class<?> c = resultsClassLoader.loadClass("com.example.Default");
        Object instance = c.newInstance();
        Method getter = c.getMethod("getIntegerWithDefault");
        Assert.assertThat(((BigInteger) (getter.invoke(instance))), is(equalTo(new BigInteger("1337"))));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void integerEnumPropertyHasCorrectDefaultBigIntegerValue() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/default/default.json", "com.example", CodeGenerationHelper.config("useBigIntegers", true));
        Class<?> c = resultsClassLoader.loadClass("com.example.Default");
        Object instance = c.newInstance();
        Class<Enum> enumClass = ((Class<Enum>) (c.getClassLoader().loadClass("com.example.Default$IntegerEnumWithDefault")));
        Method getter = c.getMethod("getIntegerEnumWithDefault");
        Enum e = ((Enum) (getter.invoke(instance)));
        Assert.assertThat(e, is(equalTo(enumClass.getEnumConstants()[1])));
        Assert.assertThat(((BigInteger) (e.getClass().getMethod("value").invoke(e))), is(equalTo(new BigInteger("2"))));
    }

    @Test
    public void numberPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getNumberWithDefault");
        Assert.assertThat(((Double) (getter.invoke(instance))), is(equalTo(Double.valueOf("1.337"))));
    }

    @Test
    public void numberPropertyHasCorrectDefaultBigDecimalValue() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/default/default.json", "com.example", CodeGenerationHelper.config("useBigDecimals", true));
        Class<?> c = resultsClassLoader.loadClass("com.example.Default");
        Object instance = c.newInstance();
        Method getter = c.getMethod("getNumberWithDefault");
        Assert.assertThat(((BigDecimal) (getter.invoke(instance))), is(equalTo(new BigDecimal("1.337"))));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void numberEnumPropertyHasCorrectDefaultBigDecimalValue() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/default/default.json", "com.example", CodeGenerationHelper.config("useBigDecimals", true));
        Class<?> c = resultsClassLoader.loadClass("com.example.Default");
        Object instance = c.newInstance();
        Class<Enum> enumClass = ((Class<Enum>) (c.getClassLoader().loadClass("com.example.Default$NumberEnumWithDefault")));
        Method getter = c.getMethod("getNumberEnumWithDefault");
        Enum e = ((Enum) (getter.invoke(instance)));
        Assert.assertThat(e, is(equalTo(enumClass.getEnumConstants()[1])));
        Assert.assertThat(((BigDecimal) (e.getClass().getMethod("value").invoke(e))), is(equalTo(new BigDecimal("2.3"))));
    }

    @Test
    public void booleanPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getBooleanWithDefault");
        Assert.assertThat(((Boolean) (getter.invoke(instance))), is(equalTo(true)));
    }

    @Test
    public void dateTimeAsMillisecPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getDateTimeWithDefault");
        Assert.assertThat(((Date) (getter.invoke(instance))), is(equalTo(new Date(123456789))));
    }

    @Test
    public void dateTimeAsStringPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getDateTimeAsStringWithDefault");
        Assert.assertThat(((Date) (getter.invoke(instance))), is(equalTo(new Date(1298539523112L))));
    }

    @Test
    public void utcmillisecPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getUtcmillisecWithDefault");
        Assert.assertThat(((Long) (getter.invoke(instance))), is(equalTo(123456789L)));
    }

    @Test
    public void uriPropertyHasCorrectDefaultValue() throws Exception {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getUriWithDefault");
        Assert.assertThat(((URI) (getter.invoke(instance))), is(URI.create("http://example.com")));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void enumPropertyHasCorrectDefaultValue() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Class<Enum> enumClass = ((Class<Enum>) (DefaultIT.classWithDefaults.getClassLoader().loadClass("com.example.Default$EnumWithDefault")));
        Method getter = DefaultIT.classWithDefaults.getMethod("getEnumWithDefault");
        Assert.assertThat(((Enum) (getter.invoke(instance))), is(equalTo(enumClass.getEnumConstants()[1])));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void integerEnumPropertyHasCorrectDefaultValue() throws Exception {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Class<Enum> enumClass = ((Class<Enum>) (DefaultIT.classWithDefaults.getClassLoader().loadClass("com.example.Default$IntegerEnumWithDefault")));
        Method getter = DefaultIT.classWithDefaults.getMethod("getIntegerEnumWithDefault");
        Enum e = ((Enum) (getter.invoke(instance)));
        Assert.assertThat(e, is(equalTo(enumClass.getEnumConstants()[1])));
        Assert.assertThat(((Integer) (e.getClass().getMethod("value").invoke(e))), is(equalTo(2)));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void numberEnumPropertyHasCorrectDefaultValue() throws Exception {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Class<Enum> enumClass = ((Class<Enum>) (DefaultIT.classWithDefaults.getClassLoader().loadClass("com.example.Default$NumberEnumWithDefault")));
        Method getter = DefaultIT.classWithDefaults.getMethod("getNumberEnumWithDefault");
        Enum e = ((Enum) (getter.invoke(instance)));
        Assert.assertThat(e, is(equalTo(enumClass.getEnumConstants()[1])));
        Assert.assertThat(((Double) (e.getClass().getMethod("value").invoke(e))), is(equalTo(2.3)));
    }

    @Test
    public void complexPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getComplexPropertyWithDefault");
        Assert.assertThat(getter.invoke(instance), is(nullValue()));
    }

    @Test
    public void simplePropertyCanHaveNullDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getSimplePropertyWithNullDefault");
        Assert.assertThat(getter.invoke(instance), is(nullValue()));
    }

    @Test
    public void arrayPropertyCanHaveNullDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getArrayPropertyWithNullDefault");
        Assert.assertThat(getter.invoke(instance), is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getArrayWithDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(List.class)));
        List<String> defaultList = ((List<String>) (getter.invoke(instance)));
        Assert.assertThat(defaultList.size(), is(3));
        Assert.assertThat(defaultList.get(0), is(equalTo("one")));
        Assert.assertThat(defaultList.get(1), is(equalTo("two")));
        Assert.assertThat(defaultList.get(2), is(equalTo("three")));
        // list should be mutable
        Assert.assertThat(defaultList.add("anotherString"), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayPropertyHasCorrectDefaultUriValues() throws Exception {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getArrayWithUriDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(List.class)));
        List<URI> defaultList = ((List<URI>) (getter.invoke(instance)));
        Assert.assertThat(defaultList.size(), is(2));
        Assert.assertThat(defaultList.get(0), is(equalTo(URI.create("http://example.com/p/1"))));
        Assert.assertThat(defaultList.get(1), is(equalTo(URI.create("http://example.com/p/2"))));
        // list should be mutable
        Assert.assertThat(defaultList.add(URI.create("")), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayPropertyCanHaveEmptyDefaultArray() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getArrayWithEmptyDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(List.class)));
        List<String> defaultList = ((List<String>) (getter.invoke(instance)));
        Assert.assertThat(defaultList.size(), is(0));
        // list should be mutable
        Assert.assertThat(defaultList.add("anotherString"), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uniqueArrayPropertyHasCorrectDefaultValue() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getUniqueArrayWithDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(Set.class)));
        Set<Integer> defaultSet = ((Set<Integer>) (getter.invoke(instance)));
        Iterator<Integer> defaultSetIterator = defaultSet.iterator();
        Assert.assertThat(defaultSet.size(), is(3));
        Assert.assertThat(defaultSetIterator.next(), is(equalTo(100)));
        Assert.assertThat(defaultSetIterator.next(), is(equalTo(200)));
        Assert.assertThat(defaultSetIterator.next(), is(equalTo(300)));
        // set should be mutable
        Assert.assertThat(defaultSet.add(400), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void arrayPropertyWithoutDefaultIsEmptyList() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getArrayWithoutDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(List.class)));
        List<String> defaultList = ((List<String>) (getter.invoke(instance)));
        Assert.assertThat(defaultList.size(), is(0));
        // list should be mutable
        Assert.assertThat(defaultList.add("anotherString"), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uniqueArrayPropertyWithoutDefaultIsEmptySet() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance = DefaultIT.classWithDefaults.newInstance();
        Method getter = DefaultIT.classWithDefaults.getMethod("getUniqueArrayWithoutDefault");
        Assert.assertThat(getter.invoke(instance), is(instanceOf(Set.class)));
        Set<Boolean> defaultSet = ((Set<Boolean>) (getter.invoke(instance)));
        Assert.assertThat(defaultSet.size(), is(0));
        // set should be mutable
        Assert.assertThat(defaultSet.add(true), is(true));
    }
}

