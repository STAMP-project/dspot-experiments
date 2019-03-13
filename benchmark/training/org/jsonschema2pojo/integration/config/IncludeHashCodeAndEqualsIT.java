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


import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class IncludeHashCodeAndEqualsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static ClassLoader resultsClassLoader;

    @Test
    public void beansIncludeHashCodeAndEqualsByDefault() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example");
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        // throws NoSuchMethodException if method is not found
        generatedType.getDeclaredMethod("equals", java.lang.Object.class);
        generatedType.getDeclaredMethod("hashCode");
    }

    @Test
    public void beansOmitHashCodeAndEqualsWhenConfigIsSet() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("includeHashcodeAndEquals", false));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        try {
            generatedType.getDeclaredMethod("equals", java.lang.Object.class);
            Assert.fail(".equals method is present, it should have been omitted");
        } catch (NoSuchMethodException e) {
        }
        try {
            generatedType.getDeclaredMethod("hashCode");
            Assert.fail(".hashCode method is present, it should have been omitted");
        } catch (NoSuchMethodException e) {
        }
    }

    @Test
    public void objectWithoutFields() throws Exception {
        Class genType = IncludeHashCodeAndEqualsIT.resultsClassLoader.loadClass("com.example.Empty");
        Assert.assertEquals(genType.getDeclaredFields().length, 0);
        genType.getDeclaredMethod("equals", java.lang.Object.class);
        Assert.assertEquals("Should not use super.equals()", genType.newInstance(), genType.newInstance());
        Assert.assertEquals(genType.newInstance().hashCode(), genType.newInstance().hashCode());
    }

    @Test
    public void objectExtendingJavaType() throws Exception {
        Class genType = IncludeHashCodeAndEqualsIT.resultsClassLoader.loadClass("com.example.ExtendsJavaType");
        Assert.assertEquals(genType.getSuperclass(), IncludeHashCodeAndEqualsIT.Parent.class);
        Assert.assertEquals(genType.getDeclaredFields().length, 0);
        genType.getDeclaredMethod("equals", java.lang.Object.class);
        Assert.assertNotEquals("Should use super.equals() because parent is not Object; parent uses Object.equals()", genType.newInstance(), genType.newInstance());
    }

    @Test
    public void objectExtendingJavaTypeWithEquals() throws Exception {
        Class genType = IncludeHashCodeAndEqualsIT.resultsClassLoader.loadClass("com.example.ExtendsJavaTypeWithEquals");
        Assert.assertEquals(genType.getSuperclass(), IncludeHashCodeAndEqualsIT.ParentWithEquals.class);
        Assert.assertEquals(genType.getDeclaredFields().length, 0);
        genType.getDeclaredMethod("equals", java.lang.Object.class);
        Assert.assertEquals("Should use super.equals()", genType.newInstance(), genType.newInstance());
        Assert.assertEquals(genType.newInstance().hashCode(), genType.newInstance().hashCode());
    }

    @Test
    public void objectExtendingFalseObject() throws Exception {
        Class genType = IncludeHashCodeAndEqualsIT.resultsClassLoader.loadClass("com.example.ExtendsFalseObject");
        Assert.assertEquals(genType.getSuperclass(), IncludeHashCodeAndEqualsIT.Object.class);
        genType.getDeclaredMethod("equals", java.lang.Object.class);
        Assert.assertNotEquals("Should use super.equals() because parent is not java.lang.Object; parent uses Object.equals()", genType.newInstance(), genType.newInstance());
    }

    @Test
    public void objectExtendingEmptyParent() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/hashCodeAndEquals/extendsEmpty.json", "com.example");
        Class gen1Type = resultsClassLoader.loadClass("com.example.ExtendsEmptyParent");
        Class gen2Type = resultsClassLoader.loadClass("com.example.ExtendsEmpty");
        gen2Type.getDeclaredMethod("equals", java.lang.Object.class);
        gen2Type.getDeclaredMethod("hashCode");
        Assert.assertEquals(gen2Type.newInstance(), gen2Type.newInstance());
        Assert.assertEquals(gen2Type.newInstance().hashCode(), gen2Type.newInstance().hashCode());
        gen1Type.getDeclaredMethod("equals", java.lang.Object.class);
        gen1Type.getDeclaredMethod("hashCode");
        Assert.assertEquals(gen1Type.newInstance(), gen1Type.newInstance());
        Assert.assertEquals(gen1Type.newInstance().hashCode(), gen1Type.newInstance().hashCode());
    }

    public static class Object extends java.lang.Object {}

    public static class Parent {}

    public static class ParentWithEquals {
        @Override
        public boolean equals(java.lang.Object other) {
            if (other == (this)) {
                return true;
            }
            if ((other instanceof IncludeHashCodeAndEqualsIT.ParentWithEquals) == false) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}

