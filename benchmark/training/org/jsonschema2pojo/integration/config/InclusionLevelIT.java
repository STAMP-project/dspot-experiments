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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.USE_DEFAULTS;


public class InclusionLevelIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelAlways() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "ALWAYS"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(ALWAYS));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelNonAbsent() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "NON_ABSENT"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(NON_ABSENT));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelNonDefault() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "NON_DEFAULT"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(NON_DEFAULT));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelNonEmpty() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "NON_EMPTY"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(NON_EMPTY));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelNonNull() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "NON_NULL"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(NON_NULL));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelUseDefault() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2", "inclusionLevel", "USE_DEFAULTS"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(USE_DEFAULTS));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson2InclusionLevelNotSet() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson2"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(notNullValue()));
        Assert.assertThat(((JsonInclude) (generatedType.getAnnotation(JsonInclude.class))).value(), is(NON_NULL));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelAlways() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "ALWAYS"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.ALWAYS));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelNonAbsent() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "NON_ABSENT"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_NULL));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelNonDefault() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "NON_DEFAULT"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_DEFAULT));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelNonEmpty() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "NON_EMPTY"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_EMPTY));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelNonNull() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "NON_NULL"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_NULL));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelUseDefault() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1", "inclusionLevel", "USE_DEFAULTS"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_NULL));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void Jackson1InclusionLevelNotSet() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("annotationStyle", "jackson1"));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        JsonSerialize jsonSerialize = ((JsonSerialize) (generatedType.getAnnotation(JsonSerialize.class)));
        Assert.assertThat(jsonSerialize, is(notNullValue()));
        Assert.assertThat(jsonSerialize.include(), is(JsonSerialize.Inclusion.NON_NULL));
    }
}

