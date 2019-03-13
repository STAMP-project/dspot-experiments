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


import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ExtendsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsWithEmbeddedSchemaGeneratesParentType() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/extendsEmbeddedSchema.json", "com.example");
        Class subtype = resultsClassLoader.loadClass("com.example.ExtendsEmbeddedSchema");
        Class supertype = resultsClassLoader.loadClass("com.example.ExtendsEmbeddedSchemaParent");
        Assert.assertThat(subtype.getSuperclass(), is(equalTo(supertype)));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsWithRefToAnotherSchema() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfA.json", "com.example");
        Class subtype = resultsClassLoader.loadClass("com.example.SubtypeOfA");
        Class supertype = resultsClassLoader.loadClass("com.example.A");
        Assert.assertThat(subtype.getSuperclass(), is(equalTo(supertype)));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsWithRefToAnotherSchemaThatIsAlreadyASubtype() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfA.json", "com.example");
        Class subtype = resultsClassLoader.loadClass("com.example.SubtypeOfSubtypeOfA");
        Class supertype = resultsClassLoader.loadClass("com.example.SubtypeOfA");
        Assert.assertThat(subtype.getSuperclass(), is(equalTo(supertype)));
    }

    @Test(expected = ClassNotFoundException.class)
    public void extendsStringCausesNoNewTypeToBeGenerated() throws ClassNotFoundException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/extendsString.json", "com.example");
        resultsClassLoader.loadClass("com.example.ExtendsString");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsEquals() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfA.json", "com.example2");
        Class generatedType = resultsClassLoader.loadClass("com.example2.SubtypeOfSubtypeOfA");
        Object instance = generatedType.newInstance();
        Object instance2 = generatedType.newInstance();
        new PropertyDescriptor("parent", generatedType).getWriteMethod().invoke(instance, "1");
        new PropertyDescriptor("child", generatedType).getWriteMethod().invoke(instance, "2");
        new PropertyDescriptor("parent", generatedType).getWriteMethod().invoke(instance2, "not-equal");
        new PropertyDescriptor("child", generatedType).getWriteMethod().invoke(instance2, "2");
        Assert.assertNotEquals(instance, instance2);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsSchemaWithinDefinitions() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/extendsSchemaWithinDefinitions.json", "com.example");
        Class subtype = resultsClassLoader.loadClass("com.example.Child");
        Assert.assertNotNull("no propertyOfChild field", subtype.getDeclaredField("propertyOfChild"));
        Class supertype = resultsClassLoader.loadClass("com.example.Parent");
        Assert.assertNotNull("no propertyOfParent field", supertype.getDeclaredField("propertyOfParent"));
        Assert.assertThat(subtype.getSuperclass(), is(equalTo(supertype)));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void constructorHasParentsProperties() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfB.json", "com.example", CodeGenerationHelper.config("includeConstructors", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfB");
        Class supertype = resultsClassLoader.loadClass("com.example.B");
        Assert.assertThat(type.getSuperclass(), is(equalTo(supertype)));
        Assert.assertNotNull("Parent constructor is missing", supertype.getConstructor(String.class));
        Assert.assertNotNull("Constructor is missing", type.getConstructor(String.class, String.class));
        Object typeInstance = type.getConstructor(String.class, String.class).newInstance("String1", "String2");
        Field chieldField = type.getDeclaredField("childProperty");
        chieldField.setAccessible(true);
        String childProp = ((String) (chieldField.get(typeInstance)));
        Field parentField = supertype.getDeclaredField("parentProperty");
        parentField.setAccessible(true);
        String parentProp = ((String) (parentField.get(typeInstance)));
        Assert.assertThat(childProp, is(equalTo("String1")));
        Assert.assertThat(parentProp, is(equalTo("String2")));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void constructorHasParentsParentProperties() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfB.json", "com.example", CodeGenerationHelper.config("includeConstructors", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfSubtypeOfB");
        Class supertype = resultsClassLoader.loadClass("com.example.SubtypeOfB");
        Class superSupertype = resultsClassLoader.loadClass("com.example.B");
        Assert.assertThat(type.getSuperclass(), is(equalTo(supertype)));
        Assert.assertNotNull("Parent Parent constructor is missing", superSupertype.getDeclaredConstructor(String.class));
        Assert.assertNotNull("Parent Constructor is missing", supertype.getDeclaredConstructor(String.class, String.class));
        Assert.assertNotNull("Constructor is missing", type.getDeclaredConstructor(String.class, String.class, String.class));
        Object typeInstance = type.getConstructor(String.class, String.class, String.class).newInstance("String1", "String2", "String3");
        Field chieldChildField = type.getDeclaredField("childChildProperty");
        chieldChildField.setAccessible(true);
        String childChildProp = ((String) (chieldChildField.get(typeInstance)));
        Field chieldField = supertype.getDeclaredField("childProperty");
        chieldField.setAccessible(true);
        String childProp = ((String) (chieldField.get(typeInstance)));
        Field parentField = superSupertype.getDeclaredField("parentProperty");
        parentField.setAccessible(true);
        String parentProp = ((String) (parentField.get(typeInstance)));
        Assert.assertThat(childChildProp, is(equalTo("String1")));
        Assert.assertThat(childProp, is(equalTo("String2")));
        Assert.assertThat(parentProp, is(equalTo("String3")));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void constructorHasParentsParentPropertiesInCorrectOrder() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfBDifferentType.json", "com.example", CodeGenerationHelper.config("includeConstructors", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfSubtypeOfBDifferentType");
        Class supertype = resultsClassLoader.loadClass("com.example.SubtypeOfB");
        Class superSupertype = resultsClassLoader.loadClass("com.example.B");
        Assert.assertThat(type.getSuperclass(), is(equalTo(supertype)));
        Assert.assertNotNull("Parent Parent constructor is missing", superSupertype.getDeclaredConstructor(String.class));
        Assert.assertNotNull("Parent Constructor is missing", supertype.getDeclaredConstructor(String.class, String.class));
        Assert.assertNotNull("Constructor is missing", type.getDeclaredConstructor(Integer.class, String.class, String.class));
        Object typeInstance = type.getConstructor(Integer.class, String.class, String.class).newInstance(5, "String2", "String3");
        Field chieldChildField = type.getDeclaredField("childChildProperty");
        chieldChildField.setAccessible(true);
        int childChildProp = ((Integer) (chieldChildField.get(typeInstance)));
        Field chieldField = supertype.getDeclaredField("childProperty");
        chieldField.setAccessible(true);
        String childProp = ((String) (chieldField.get(typeInstance)));
        Field parentField = superSupertype.getDeclaredField("parentProperty");
        parentField.setAccessible(true);
        String parentProp = ((String) (parentField.get(typeInstance)));
        Assert.assertThat(childChildProp, is(equalTo(5)));
        Assert.assertThat(childProp, is(equalTo("String2")));
        Assert.assertThat(parentProp, is(equalTo("String3")));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void constructorDoesNotDuplicateArgsFromDuplicatedParentProperties() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfC.json", "com.example", CodeGenerationHelper.config("includeConstructors", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfSubtypeOfC");
        Class supertype = resultsClassLoader.loadClass("com.example.SubtypeOfC");
        Class superSupertype = resultsClassLoader.loadClass("com.example.C");
        Assert.assertNotNull("Parent Parent constructor is missing", superSupertype.getDeclaredConstructor(String.class, Integer.class));
        Assert.assertNotNull("Parent Constructor is missing", supertype.getDeclaredConstructor(String.class, Boolean.class, Integer.class));
        Assert.assertNotNull("Constructor is missing", type.getDeclaredConstructor(String.class, Integer.class, Boolean.class, Integer.class));
        Object typeInstance = type.getConstructor(String.class, Integer.class, Boolean.class, Integer.class).newInstance("String1", 5, true, 6);
        Field chieldChildField = type.getDeclaredField("duplicatedProp");
        chieldChildField.setAccessible(true);
        String childChildProp = ((String) (chieldChildField.get(typeInstance)));
        Field chieldField = supertype.getDeclaredField("duplicatedProp");
        chieldField.setAccessible(true);
        String childProp = ((String) (chieldField.get(typeInstance)));
        Field parentField = superSupertype.getDeclaredField("duplicatedProp");
        parentField.setAccessible(true);
        String parentProp = ((String) (parentField.get(typeInstance)));
        Assert.assertThat(childChildProp, is(equalTo("String1")));
        Assert.assertThat(childProp, is(equalTo("String1")));
        Assert.assertThat(parentProp, is(equalTo("String1")));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void extendsBuilderMethods() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfSubtypeOfA.json", "com.example", CodeGenerationHelper.config("generateBuilders", true));
        Class subtype = resultsClassLoader.loadClass("com.example.SubtypeOfSubtypeOfA");
        Class supertype = resultsClassLoader.loadClass("com.example.SubtypeOfA");
        ExtendsIT.checkBuilderMethod(subtype, supertype, "withParent");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void builderMethodsOnChildWithProperties() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfB.json", "com.example", CodeGenerationHelper.config("generateBuilders", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfB");
        Class supertype = resultsClassLoader.loadClass("com.example.B");
        ExtendsIT.checkBuilderMethod(type, supertype, "withParentProperty");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void builderMethodsOnChildWithNoProperties() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/extends/subtypeOfBWithNoProperties.json", "com.example", CodeGenerationHelper.config("generateBuilders", true));
        Class type = resultsClassLoader.loadClass("com.example.SubtypeOfBWithNoProperties");
        Class supertype = resultsClassLoader.loadClass("com.example.B");
        ExtendsIT.checkBuilderMethod(type, supertype, "withParentProperty");
    }
}

