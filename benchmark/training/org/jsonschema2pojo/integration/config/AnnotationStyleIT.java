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
package org.jsonschema2pojo.integration.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.maven.plugin.MojoExecutionException;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.FileSearchMatcher;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AnnotationStyleIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void defaultAnnotationStyeIsJackson2() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example");
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(notNullValue()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void annotationStyleJacksonProducesJackson2Annotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(notNullValue()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void annotationStyleJackson2ProducesJackson2Annotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class generatedType = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2")).loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(schemaRule.getGenerateDir(), not(FileSearchMatcher.containsText("org.codehaus.jackson")));
        Assert.assertThat(schemaRule.getGenerateDir(), FileSearchMatcher.containsText("com.fasterxml.jackson"));
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(notNullValue()));
    }

    @Test
    public void annotationStyleJackson2ProducesJsonPropertyDescription() throws Exception {
        Class<?> generatedType = schemaRule.generateAndCompile("/schema/description/description.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2")).loadClass("com.example.Description");
        Field field = generatedType.getDeclaredField("description");
        Assert.assertThat(field.getAnnotation(JsonPropertyDescription.class).value(), is("A description for this property"));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void annotationStyleJackson1ProducesJackson1Annotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class generatedType = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1")).loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(schemaRule.getGenerateDir(), not(FileSearchMatcher.containsText("com.fasterxml.jackson")));
        Assert.assertThat(schemaRule.getGenerateDir(), FileSearchMatcher.containsText("org.codehaus.jackson"));
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonSerialize.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(notNullValue()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void annotationStyleNoneProducesNoAnnotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "none"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(nullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonSerialize.class), is(nullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(nullValue()));
    }

    @Test
    public void invalidAnnotationStyleCausesMojoException() {
        try {
            schemaRule.generate("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "invalidstyle"));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), is(instanceOf(MojoExecutionException.class)));
            Assert.assertThat(e.getCause().getMessage(), is(containsString("invalidstyle")));
        }
    }
}

