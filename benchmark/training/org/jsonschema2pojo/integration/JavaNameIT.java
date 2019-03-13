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


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Created by cmb on 31.01.16.
 */
public class JavaNameIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void propertiesHaveCorrectNames() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader javaNameClassLoader = schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        Class<?> classWithJavaNames = javaNameClassLoader.loadClass("com.example.javaname.JavaName");
        Object instance = classWithJavaNames.newInstance();
        Assert.assertThat(instance, hasProperty("javaProperty"));
        Assert.assertThat(instance, hasProperty("propertyWithoutJavaName"));
        Assert.assertThat(instance, hasProperty("javaEnum"));
        Assert.assertThat(instance, hasProperty("enumWithoutJavaName"));
        Assert.assertThat(instance, hasProperty("javaObject"));
        Assert.assertThat(instance, hasProperty("objectWithoutJavaName"));
    }

    @Test
    public void propertiesHaveCorrectTypes() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        ClassLoader javaNameClassLoader = schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        Class<?> classWithJavaNames = javaNameClassLoader.loadClass("com.example.javaname.JavaName");
        classWithJavaNames.newInstance();
        Assert.assertThat(classWithJavaNames.getDeclaredField("javaEnum").getType(), typeCompatibleWith(javaNameClassLoader.loadClass("com.example.javaname.JavaName$JavaEnum")));
        Assert.assertThat(classWithJavaNames.getDeclaredField("enumWithoutJavaName").getType(), typeCompatibleWith(javaNameClassLoader.loadClass("com.example.javaname.JavaName$EnumWithoutJavaName")));
        Assert.assertThat(classWithJavaNames.getDeclaredField("javaObject").getType(), typeCompatibleWith(javaNameClassLoader.loadClass("com.example.javaname.JavaObject")));
        Assert.assertThat(classWithJavaNames.getDeclaredField("objectWithoutJavaName").getType(), typeCompatibleWith(javaNameClassLoader.loadClass("com.example.javaname.ObjectWithoutJavaName")));
    }

    @Test
    public void gettersHaveCorrectNames() throws ClassNotFoundException, NoSuchMethodException {
        ClassLoader javaNameClassLoader = schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        Class<?> classWithJavaNames = javaNameClassLoader.loadClass("com.example.javaname.JavaName");
        classWithJavaNames.getMethod("getJavaProperty");
        classWithJavaNames.getMethod("getPropertyWithoutJavaName");
        classWithJavaNames.getMethod("getJavaEnum");
        classWithJavaNames.getMethod("getEnumWithoutJavaName");
        classWithJavaNames.getMethod("getJavaObject");
        classWithJavaNames.getMethod("getObjectWithoutJavaName");
    }

    @Test
    public void settersHaveCorrectNamesAndArgumentTypes() throws ClassNotFoundException, NoSuchMethodException {
        ClassLoader javaNameClassLoader = schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        Class<?> classWithJavaNames = javaNameClassLoader.loadClass("com.example.javaname.JavaName");
        classWithJavaNames.getMethod("setJavaProperty", String.class);
        classWithJavaNames.getMethod("setPropertyWithoutJavaName", String.class);
        classWithJavaNames.getMethod("setJavaEnum", javaNameClassLoader.loadClass("com.example.javaname.JavaName$JavaEnum"));
        classWithJavaNames.getMethod("setEnumWithoutJavaName", javaNameClassLoader.loadClass("com.example.javaname.JavaName$EnumWithoutJavaName"));
        classWithJavaNames.getMethod("setJavaObject", javaNameClassLoader.loadClass("com.example.javaname.JavaObject"));
        classWithJavaNames.getMethod("setObjectWithoutJavaName", javaNameClassLoader.loadClass("com.example.javaname.ObjectWithoutJavaName"));
    }

    @Test
    public void serializedPropertiesHaveCorrectNames() throws IntrospectionException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ClassLoader javaNameClassLoader = schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        Class<?> classWithJavaNames = javaNameClassLoader.loadClass("com.example.javaname.JavaName");
        Object instance = classWithJavaNames.newInstance();
        new PropertyDescriptor("javaProperty", classWithJavaNames).getWriteMethod().invoke(instance, "abc");
        new PropertyDescriptor("propertyWithoutJavaName", classWithJavaNames).getWriteMethod().invoke(instance, "abc");
        JsonNode serialized = mapper.valueToTree(instance);
        Assert.assertThat(serialized.has("propertyWithJavaName"), is(true));
        Assert.assertThat(serialized.has("propertyWithoutJavaName"), is(true));
    }

    @Test
    public void originalPropertyNamesAppearInJavaDoc() throws IOException {
        schemaRule.generateAndCompile("/schema/javaName/javaName.json", "com.example.javaname");
        File generatedJavaFile = schemaRule.generated("com/example/javaname/JavaName.java");
        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(generatedJavaFile);
        JavaClass classWithDescription = javaDocBuilder.getClassByName("com.example.javaname.JavaName");
        JavaField javaPropertyField = classWithDescription.getFieldByName("javaProperty");
        Assert.assertThat(javaPropertyField.getComment(), containsString("Corresponds to the \"propertyWithJavaName\" property."));
        JavaField javaEnumField = classWithDescription.getFieldByName("javaEnum");
        Assert.assertThat(javaEnumField.getComment(), containsString("Corresponds to the \"enumWithJavaName\" property."));
        JavaField javaObjectField = classWithDescription.getFieldByName("javaObject");
        Assert.assertThat(javaObjectField.getComment(), containsString("Corresponds to the \"objectWithJavaName\" property."));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowDuplicateNames() {
        schemaRule.generateAndCompile("/schema/javaName/duplicateName.json", "com.example");
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowDuplicateDefaultNames() {
        schemaRule.generateAndCompile("/schema/javaName/duplicateDefaultName.json", "com.example");
    }

    @Test
    public void arrayRequiredAppearsInFieldJavadoc() throws IOException {
        schemaRule.generateAndCompile("/schema/javaName/javaNameWithRequiredProperties.json", "com.example.required");
        File generatedJavaFileWithRequiredProperties = schemaRule.generated("com/example/required/JavaNameWithRequiredProperties.java");
        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(generatedJavaFileWithRequiredProperties);
        JavaClass classWithRequiredProperties = javaDocBuilder.getClassByName("com.example.required.JavaNameWithRequiredProperties");
        JavaField javaFieldWithoutJavaName = classWithRequiredProperties.getFieldByName("requiredPropertyWithoutJavaName");
        JavaField javaFieldWithJavaName = classWithRequiredProperties.getFieldByName("requiredPropertyWithoutJavaName");
        Assert.assertThat(javaFieldWithoutJavaName.getComment(), containsString("(Required)"));
        Assert.assertThat(javaFieldWithJavaName.getComment(), containsString("(Required)"));
    }

    @Test
    public void inlineRequiredAppearsInFieldJavadoc() throws IOException {
        schemaRule.generateAndCompile("/schema/javaName/javaNameWithRequiredProperties.json", "com.example.required");
        File generatedJavaFileWithRequiredProperties = schemaRule.generated("com/example/required/JavaNameWithRequiredProperties.java");
        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(generatedJavaFileWithRequiredProperties);
        JavaClass classWithRequiredProperties = javaDocBuilder.getClassByName("com.example.required.JavaNameWithRequiredProperties");
        JavaField javaFieldWithoutJavaName = classWithRequiredProperties.getFieldByName("inlineRequiredPropertyWithoutJavaName");
        JavaField javaFieldWithJavaName = classWithRequiredProperties.getFieldByName("inlineRequiredPropertyWithoutJavaName");
        Assert.assertThat(javaFieldWithoutJavaName.getComment(), containsString("(Required)"));
        Assert.assertThat(javaFieldWithJavaName.getComment(), containsString("(Required)"));
    }
}

