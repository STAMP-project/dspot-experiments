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


import java.io.File;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class IncludeJsr305AnnotationsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void jsrAnnotationsAreNotIncludedByDefault() {
        File outputDirectory = schemaRule.generate("/schema/jsr303/all.json", "com.example");
        Assert.assertThat(outputDirectory, not(IncludeJsr305AnnotationsIT.containsText("javax.validation")));
    }

    @Test
    public void jsrAnnotationsAreNotIncludedWhenSwitchedOff() {
        File outputDirectory = schemaRule.generate("/schema/jsr303/all.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", false));
        Assert.assertThat(outputDirectory, not(IncludeJsr305AnnotationsIT.containsText("javax.validation")));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void jsr305NonnullAnnotationIsAddedForSchemaRuleRequired() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/required.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Required");
        try {
            IncludeJsr305AnnotationsIT.validateNonnullField(generatedType.getDeclaredField("required"));
        } catch (NoSuchFieldException e) {
            Assert.fail("Field is missing in generated class.");
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void jsr305NullableAnnotationIsAddedByDefault() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/required/required.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Required");
        try {
            IncludeJsr305AnnotationsIT.validateNonnullField(generatedType.getDeclaredField("requiredProperty"));
            IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("nonRequiredProperty"));
            IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("defaultNotRequiredProperty"));
        } catch (NoSuchFieldException e) {
            Assert.fail("Expected field is missing in generated class.");
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void jsr305RequiredArrayIsTakenIntoConsideration() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/required/requiredArray.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.RequiredArray");
        try {
            IncludeJsr305AnnotationsIT.validateNonnullField(generatedType.getDeclaredField("requiredProperty"));
            IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("nonRequiredProperty"));
            IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("defaultNotRequiredProperty"));
        } catch (NoSuchFieldException e) {
            Assert.fail("Expected field is missing in generated class.");
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void jsr305AnnotationsGeneratedProperlyInNestedArray() throws ClassNotFoundException, NoSuchFieldException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/required/requiredNestedInArray.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Nested");
        IncludeJsr305AnnotationsIT.validateNonnullField(generatedType.getDeclaredField("requiredProperty"));
        IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("nonRequiredProperty"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void jsr305AnnotationsGeneratedProperlyInNestedObject() throws ClassNotFoundException, NoSuchFieldException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/required/requiredNestedInObject.json", "com.example", CodeGenerationHelper.config("includeJsr305Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Nested");
        IncludeJsr305AnnotationsIT.validateNonnullField(generatedType.getDeclaredField("requiredProperty"));
        IncludeJsr305AnnotationsIT.validateNullableField(generatedType.getDeclaredField("nonRequiredProperty"));
    }
}

