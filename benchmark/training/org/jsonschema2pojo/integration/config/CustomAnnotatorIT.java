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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import java.lang.reflect.Method;
import org.apache.maven.plugin.MojoExecutionException;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class CustomAnnotatorIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void defaultCustomAnnotatorIsNoop() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "none"));// turn off core annotations

        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotations().length, is(0));
        Assert.assertThat(getter.getAnnotations().length, is(0));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void customAnnotatorIsAbleToAddCustomAnnotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", // turn off core annotations
        CodeGenerationHelper.config("annotationStyle", "none", "customAnnotator", CustomAnnotatorIT.DeprecatingAnnotator.class.getName()));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(Deprecated.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(Deprecated.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(Deprecated.class), is(notNullValue()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void customAnnotatorCanBeAppliedAlongsideCoreAnnotator() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("customAnnotator", CustomAnnotatorIT.DeprecatingAnnotator.class.getName()));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Method getter = generatedType.getMethod("getA");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(Deprecated.class), is(notNullValue()));
        Assert.assertThat(generatedType.getAnnotation(Deprecated.class), is(notNullValue()));
        Assert.assertThat(getter.getAnnotation(Deprecated.class), is(notNullValue()));
    }

    @Test
    public void invalidCustomAnnotatorClassCausesMojoException() {
        try {
            schemaRule.generate("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("customAnnotator", "java.lang.String"));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), is(instanceOf(MojoExecutionException.class)));
            Assert.assertThat(e.getCause().getMessage(), is(containsString("annotator")));
        }
    }

    /**
     * Example custom annotator that deprecates <em>everything</em>.
     */
    public static class DeprecatingAnnotator implements Annotator {
        @Override
        public void propertyOrder(JDefinedClass clazz, JsonNode propertiesNode) {
            clazz.annotate(Deprecated.class);
        }

        @Override
        public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        }

        @Override
        public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
            field.annotate(Deprecated.class);
        }

        @Override
        public void propertyGetter(JMethod getter, JDefinedClass clazz, String propertyName) {
            getter.annotate(Deprecated.class);
        }

        @Override
        public void propertySetter(JMethod setter, JDefinedClass clazz, String propertyName) {
            setter.annotate(Deprecated.class);
        }

        @Override
        public void anyGetter(JMethod getter, JDefinedClass clazz) {
            getter.annotate(Deprecated.class);
        }

        @Override
        public void anySetter(JMethod setter, JDefinedClass clazz) {
            setter.annotate(Deprecated.class);
        }

        @Override
        public void enumCreatorMethod(JDefinedClass _enum, JMethod creatorMethod) {
            creatorMethod.annotate(Deprecated.class);
        }

        @Override
        public void enumValueMethod(JDefinedClass _enum, JMethod valueMethod) {
            valueMethod.annotate(Deprecated.class);
        }

        @Override
        public void enumConstant(JDefinedClass _enum, JEnumConstant constant, String value) {
            constant.annotate(Deprecated.class);
        }

        @Override
        public boolean isAdditionalPropertiesSupported() {
            return true;
        }

        @Override
        public void additionalPropertiesField(JFieldVar field, JDefinedClass clazz, String propertyName) {
            field.annotate(Deprecated.class);
        }

        @Override
        public void dateField(JFieldVar field, JDefinedClass clazz, JsonNode propertyNode) {
            field.annotate(Deprecated.class);
        }

        @Override
        public void timeField(JFieldVar field, JDefinedClass clazz, JsonNode propertyNode) {
            field.annotate(Deprecated.class);
        }

        @Override
        public void dateTimeField(JFieldVar field, JDefinedClass clazz, JsonNode propertyNode) {
            field.annotate(Deprecated.class);
        }
    }
}

