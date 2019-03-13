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
package org.jsonschema2pojo.integration.yaml;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class YamlPropertiesIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SuppressWarnings("rawtypes")
    public void propertiesWithNullValuesAreOmittedWhenSerialized() throws IntrospectionException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/nullProperties.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema"));
        Class generatedType = resultsClassLoader.loadClass("com.example.NullProperties");
        Object instance = generatedType.newInstance();
        Method setter = new PropertyDescriptor("property", generatedType).getWriteMethod();
        setter.invoke(instance, "value");
        Assert.assertThat(mapper.valueToTree(instance).toString(), containsString("property"));
        setter.invoke(instance, ((Object) (null)));
        Assert.assertThat(mapper.valueToTree(instance).toString(), not(containsString("property")));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void usePrimitivesArgumentCausesPrimitiveTypes() throws IntrospectionException, ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/primitiveProperties.yaml", "com.example", CodeGenerationHelper.config("usePrimitives", true, "sourceType", "yamlschema"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(new PropertyDescriptor("a", generatedType).getReadMethod().getReturnType().getName(), is("int"));
        Assert.assertThat(new PropertyDescriptor("b", generatedType).getReadMethod().getReturnType().getName(), is("double"));
        Assert.assertThat(new PropertyDescriptor("c", generatedType).getReadMethod().getReturnType().getName(), is("boolean"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void wordDelimitersCausesCamelCase() throws IntrospectionException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/propertiesWithWordDelimiters.yaml", "com.example", CodeGenerationHelper.config("usePrimitives", true, "propertyWordDelimiters", "_ -", "sourceType", "yamlschema"));
        Class generatedType = resultsClassLoader.loadClass("com.example.WordDelimit");
        Object instance = generatedType.newInstance();
        new PropertyDescriptor("propertyWithUnderscores", generatedType).getWriteMethod().invoke(instance, "a_b_c");
        new PropertyDescriptor("propertyWithHyphens", generatedType).getWriteMethod().invoke(instance, "a-b-c");
        new PropertyDescriptor("propertyWithMixedDelimiters", generatedType).getWriteMethod().invoke(instance, "a b_c-d");
        JsonNode jsonified = mapper.valueToTree(instance);
        Assert.assertThat(jsonified.has("property_with_underscores"), is(true));
        Assert.assertThat(jsonified.has("property-with-hyphens"), is(true));
        Assert.assertThat(jsonified.has("property_with mixed-delimiters"), is(true));
    }

    @Test
    public void propertyNamesThatAreJavaKeywordsCanBeSerialized() throws IOException, ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/propertiesThatAreJavaKeywords.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema"));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.PropertiesThatAreJavaKeywords");
        String valuesAsJsonString = "{\"public\":\"a\",\"void\":\"b\",\"enum\":\"c\",\"abstract\":\"d\"}";
        Object valuesAsObject = mapper.readValue(valuesAsJsonString, generatedType);
        JsonNode valueAsJsonNode = mapper.valueToTree(valuesAsObject);
        Assert.assertThat(valueAsJsonNode.path("public").asText(), is("a"));
        Assert.assertThat(valueAsJsonNode.path("void").asText(), is("b"));
        Assert.assertThat(valueAsJsonNode.path("enum").asText(), is("c"));
        Assert.assertThat(valueAsJsonNode.path("abstract").asText(), is("d"));
    }

    @Test
    public void propertyCalledClassCanBeSerialized() throws IOException, ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/propertyCalledClass.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema"));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.PropertyCalledClass");
        String valuesAsJsonString = "{\"class\":\"a\"}";
        Object valuesAsObject = mapper.readValue(valuesAsJsonString, generatedType);
        JsonNode valueAsJsonNode = mapper.valueToTree(valuesAsObject);
        Assert.assertThat(valueAsJsonNode.path("class").asText(), is("a"));
    }

    @Test
    public void propertyNamesAreLowerCamelCase() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/yaml/properties/propertiesAreUpperCamelCase.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema"));
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.UpperCase");
        Object instance = generatedType.newInstance();
        new PropertyDescriptor("property1", generatedType).getWriteMethod().invoke(instance, "1");
        new PropertyDescriptor("propertyTwo", generatedType).getWriteMethod().invoke(instance, 2);
        new PropertyDescriptor("propertyThreeWithSpace", generatedType).getWriteMethod().invoke(instance, "3");
        new PropertyDescriptor("propertyFour", generatedType).getWriteMethod().invoke(instance, "4");
        JsonNode jsonified = mapper.valueToTree(instance);
        Assert.assertNotNull(generatedType.getDeclaredField("property1"));
        Assert.assertNotNull(generatedType.getDeclaredField("propertyTwo"));
        Assert.assertNotNull(generatedType.getDeclaredField("propertyThreeWithSpace"));
        Assert.assertNotNull(generatedType.getDeclaredField("propertyFour"));
        Assert.assertThat(jsonified.has("Property1"), is(true));
        Assert.assertThat(jsonified.has("PropertyTwo"), is(true));
        Assert.assertThat(jsonified.has(" PropertyThreeWithSpace"), is(true));
        Assert.assertThat(jsonified.has("propertyFour"), is(true));
    }
}

