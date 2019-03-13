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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings("rawtypes")
public class IncludeJsr303AnnotationsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void jsrAnnotationsAreNotIncludedByDefault() {
        File outputDirectory = schemaRule.generate("/schema/jsr303/all.json", "com.example");
        Assert.assertThat(outputDirectory, not(IncludeJsr303AnnotationsIT.containsText("javax.validation")));
    }

    @Test
    public void jsrAnnotationsAreNotIncludedWhenSwitchedOff() {
        File outputDirectory = schemaRule.generate("/schema/jsr303/all.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", false));
        Assert.assertThat(outputDirectory, not(IncludeJsr303AnnotationsIT.containsText("javax.validation")));
    }

    @Test
    public void jsr303DecimalMinValidationIsAddedForSchemaRuleMinimum() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/minimum.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Minimum");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minimum", 2.0);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minimum", 0.9);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303DecimalMaxValidationIsAddedForSchemaRuleMaximum() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/maximum.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Maximum");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maximum", 8.9);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maximum", 10.9);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303SizeValidationIsAddedForSchemaRuleMinItems() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/minItems.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.MinItems");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minItems", Arrays.asList(1, 2, 3, 4, 5, 6));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minItems", Arrays.asList(1, 2, 3));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303SizeValidationIsAddedForSchemaRuleMaxItems() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/maxItems.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.MaxItems");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maxItems", Arrays.asList(1, 2, 3));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maxItems", Arrays.asList(1, 2, 3, 4, 5, 6));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303SizeValidationIsAddedForSchemaRuleMinItemsAndMaxItems() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/minAndMaxItems.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.MinAndMaxItems");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minAndMaxItems", Arrays.asList(1, 2, 3));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance1 = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minAndMaxItems", Collections.singletonList(1));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance1, is(1));
        Object invalidInstance2 = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minAndMaxItems", Arrays.asList(1, 2, 3, 4, 5));
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance2, is(1));
    }

    @Test
    public void jsr303PatternValidationIsAddedForSchemaRulePattern() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/pattern.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Pattern");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "pattern", "abc123");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "pattern", "123abc");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303NotNullValidationIsAddedForSchemaRuleRequired() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/required.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.Required");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "required", "abc");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "required", null);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303SizeValidationIsAddedForSchemaRuleMinLength() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/minLength.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.MinLength");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minLength", "Long enough");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "minLength", "Too short");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303SizeValidationIsAddedForSchemaRuleMaxLength() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/maxLength.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class generatedType = resultsClassLoader.loadClass("com.example.MaxLength");
        Object validInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maxLength", "Short");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validInstance, is(0));
        Object invalidInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(generatedType, "maxLength", "Tooooo long");
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(invalidInstance, is(1));
    }

    @Test
    public void jsr303ValidAnnotationIsAddedForObject() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/validObject.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class validObjectType = resultsClassLoader.loadClass("com.example.ValidObject");
        Class objectFieldType = resultsClassLoader.loadClass("com.example.Objectfield");
        Object invalidObjectFieldInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(objectFieldType, "childprimitivefield", "Too long");
        Object validObjectInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validObjectType, "objectfield", invalidObjectFieldInstance);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validObjectInstance, is(1));
        Object validObjectFieldInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(objectFieldType, "childprimitivefield", "OK");
        validObjectInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validObjectType, "objectfield", validObjectFieldInstance);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validObjectInstance, is(0));
    }

    @Test
    public void jsr303ValidAnnotationIsAddedForArray() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/validArray.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class validArrayType = resultsClassLoader.loadClass("com.example.ValidArray");
        Class objectArrayType = resultsClassLoader.loadClass("com.example.Objectarray");
        List<Object> objectArrayList = new ArrayList<>();
        Object objectArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(objectArrayType, "arrayitem", "OK");
        objectArrayList.add(objectArrayInstance);
        Object validArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validArrayType, "objectarray", objectArrayList);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validArrayInstance, is(0));
        Object invalidObjectArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(objectArrayType, "arrayitem", "Too long");
        objectArrayList.add(invalidObjectArrayInstance);
        validArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validArrayType, "objectarray", objectArrayList);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validArrayInstance, is(1));
    }

    @Test
    public void jsr303ValidAnnotationIsAddedForArrayWithRef() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/validArray.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class validArrayType = resultsClassLoader.loadClass("com.example.ValidArray");
        Class refarrayType = resultsClassLoader.loadClass("com.example.Product");
        List<Object> objectArrayList = new ArrayList<>();
        Object objectArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(refarrayType, "arrayitem", "OK");
        objectArrayList.add(objectArrayInstance);
        Object validArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validArrayType, "refarray", objectArrayList);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validArrayInstance, is(0));
        Object invalidObjectArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(refarrayType, "arrayitem", "Too long");
        objectArrayList.add(invalidObjectArrayInstance);
        validArrayInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(validArrayType, "refarray", objectArrayList);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(validArrayInstance, is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void jsr303AnnotionsValidatedForAdditionalProperties() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/jsr303/validAdditionalProperties.json", "com.example", CodeGenerationHelper.config("includeJsr303Annotations", true));
        Class parentType = resultsClassLoader.loadClass("com.example.ValidAdditionalProperties");
        Object parent = parentType.newInstance();
        Class subPropertyType = resultsClassLoader.loadClass("com.example.ValidAdditionalPropertiesProperty");
        Object validSubPropertyInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(subPropertyType, "maximum", 9.0);
        Object invalidSubPropertyInstance = IncludeJsr303AnnotationsIT.createInstanceWithPropertyValue(subPropertyType, "maximum", 11.0);
        Method setter = parentType.getMethod("setAdditionalProperty", String.class, subPropertyType);
        setter.invoke(parent, "maximum", validSubPropertyInstance);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(parent, is(0));
        setter.invoke(parent, "maximum", invalidSubPropertyInstance);
        IncludeJsr303AnnotationsIT.assertNumberOfConstraintViolationsOn(parent, is(1));
    }
}

