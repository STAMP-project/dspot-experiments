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
package org.jsonschema2pojo.integration.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JsonTypesIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void simpleTypesInExampleAreMappedToCorrectJavaTypes() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/simpleTypes.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.SimpleTypes");
        Object deserialisedValue = JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/simpleTypes.json"), generatedType);
        Assert.assertThat(((String) (generatedType.getMethod("getA").invoke(deserialisedValue))), is("abc"));
        Assert.assertThat(((Integer) (generatedType.getMethod("getB").invoke(deserialisedValue))), is(123));
        Assert.assertThat(((Double) (generatedType.getMethod("getC").invoke(deserialisedValue))), is(1.3E22));
        Assert.assertThat(((Boolean) (generatedType.getMethod("getD").invoke(deserialisedValue))), is(true));
        Assert.assertThat(generatedType.getMethod("getE").invoke(deserialisedValue), is(nullValue()));
    }

    @Test
    public void integerIsMappedToBigInteger() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/simpleTypes.json", "com.example", CodeGenerationHelper.config("sourceType", "json", "useBigIntegers", true));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.SimpleTypes");
        Object deserialisedValue = JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/simpleTypes.json"), generatedType);
        Assert.assertThat(((BigInteger) (generatedType.getMethod("getB").invoke(deserialisedValue))), is(new BigInteger("123")));
    }

    @Test
    public void numberIsMappedToBigDecimal() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/simpleTypes.json", "com.example", CodeGenerationHelper.config("sourceType", "json", "useBigDecimals", true));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.SimpleTypes");
        Object deserialisedValue = JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/simpleTypes.json"), generatedType);
        Assert.assertThat(((BigDecimal) (generatedType.getMethod("getC").invoke(deserialisedValue))), is(new BigDecimal("12999999999999999999999.99")));
    }

    @Test(expected = ClassNotFoundException.class)
    public void simpleTypeAtRootProducesNoJavaTypes() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/simpleTypeAsRoot.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        resultsClassLoader.loadClass("com.example.SimpleTypeAsRoot");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void complexTypesProduceObjects() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/complexObject.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        Class<?> complexObjectClass = resultsClassLoader.loadClass("com.example.ComplexObject");
        Object complexObject = JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/complexObject.json"), complexObjectClass);
        Object a = complexObjectClass.getMethod("getA").invoke(complexObject);
        Object aa = a.getClass().getMethod("getAa").invoke(a);
        Assert.assertThat(aa.getClass().getMethod("getAaa").invoke(aa).toString(), is("aaaa"));
        Object b = complexObjectClass.getMethod("getB").invoke(complexObject);
        Assert.assertThat(b.getClass().getMethod("getAa").invoke(b), is(notNullValue()));
        Object _1 = complexObjectClass.getMethod("get1").invoke(complexObject);
        Object _2 = _1.getClass().getMethod("get2").invoke(_1);
        Assert.assertThat(_2, is(notNullValue()));
        Object _3 = _1.getClass().getMethod("get3").invoke(_1);
        Assert.assertThat(((List<Integer>) (_3)), is(equalTo(Arrays.asList(1, 2, 3))));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void arrayTypePropertiesProduceLists() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/array.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        Class<?> arrayType = resultsClassLoader.loadClass("com.example.Array");
        Class<?> itemType = resultsClassLoader.loadClass("com.example.A");
        Object deserialisedValue = JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/array.json"), arrayType);
        List<?> valueA = ((List) (arrayType.getMethod("getA").invoke(deserialisedValue)));
        Assert.assertThat(((ParameterizedType) (arrayType.getMethod("getA").getGenericReturnType())).getActualTypeArguments()[0], is(equalTo(((Type) (itemType)))));
        Assert.assertThat(((Integer) (itemType.getMethod("get0").invoke(valueA.get(0)))), is(0));
        Assert.assertThat(((Integer) (itemType.getMethod("get1").invoke(valueA.get(1)))), is(1));
        Assert.assertThat(((Integer) (itemType.getMethod("get2").invoke(valueA.get(2)))), is(2));
        Object valueB = arrayType.getMethod("getB").invoke(deserialisedValue);
        Assert.assertThat(valueB, is(instanceOf(List.class)));
        Assert.assertThat(((ParameterizedType) (arrayType.getMethod("getB").getGenericReturnType())).getActualTypeArguments()[0], is(equalTo(((Type) (Integer.class)))));
    }

    @Test
    public void arrayItemsAreRecursivelyMerged() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/complexPropertiesInArrayItem.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        Class<?> genType = resultsClassLoader.loadClass("com.example.ComplexPropertiesInArrayItem");
        Class<?> listItemType = resultsClassLoader.loadClass("com.example.List");
        Class<?> objItemType = resultsClassLoader.loadClass("com.example.Obj");
        Object[] items = ((Object[]) (JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/complexPropertiesInArrayItem.json"), Array.newInstance(genType, 0).getClass())));
        {
            Object item = items[0];
            List<?> itemList = ((List<?>) (genType.getMethod("getList").invoke(item)));
            Assert.assertThat(((Integer) (listItemType.getMethod("getA").invoke(itemList.get(0)))), is(1));
            Assert.assertThat(((String) (listItemType.getMethod("getC").invoke(itemList.get(0)))), is("hey"));
            Assert.assertNull(listItemType.getMethod("getB").invoke(itemList.get(0)));
            Object itemObj = genType.getMethod("getObj").invoke(item);
            Assert.assertThat(((String) (objItemType.getMethod("getName").invoke(itemObj))), is("k"));
            Assert.assertNull(objItemType.getMethod("getIndex").invoke(itemObj));
        }
        {
            Object item = items[1];
            List<?> itemList = ((List<?>) (genType.getMethod("getList").invoke(item)));
            Assert.assertThat(((Integer) (listItemType.getMethod("getB").invoke(itemList.get(0)))), is(177));
            Assert.assertThat(((String) (listItemType.getMethod("getC").invoke(itemList.get(0)))), is("hey again"));
            Assert.assertNull(listItemType.getMethod("getA").invoke(itemList.get(0)));
            Object itemObj = genType.getMethod("getObj").invoke(item);
            Assert.assertThat(((Integer) (objItemType.getMethod("getIndex").invoke(itemObj))), is(8));
            Assert.assertNull(objItemType.getMethod("getName").invoke(itemObj));
        }
    }

    @Test
    public void arrayItemsAreNotRecursivelyMerged() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/simplePropertiesInArrayItem.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        Class<?> genType = resultsClassLoader.loadClass("com.example.SimplePropertiesInArrayItem");
        // Different array items use different types for the same property;
        // we don't support union types, so we have to pick one
        Assert.assertEquals(Integer.class, genType.getMethod("getScalar").getReturnType());
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage(startsWith("Cannot deserialize value of type `java.lang.Integer` from String \"what\": not a valid Integer value"));
        JsonTypesIT.OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream("/json/simplePropertiesInArrayItem.json"), Array.newInstance(genType, 0).getClass());
    }

    @Test(expected = ClassNotFoundException.class)
    public void arrayAtRootProducesNoJavaTypes() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/arrayAsRoot.json", "com.example", CodeGenerationHelper.config("sourceType", "json"));
        resultsClassLoader.loadClass("com.example.ArrayAsRoot");
    }
}

