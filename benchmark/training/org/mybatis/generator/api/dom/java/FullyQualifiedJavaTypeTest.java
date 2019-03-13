/**
 * Copyright 2006-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.api.dom.java;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Jeff Butler
 */
public class FullyQualifiedJavaTypeTest {
    @Test
    public void testJavaType() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.lang.String");// $NON-NLS-1$

        Assertions.assertFalse(fqjt.isExplicitlyImported());
        Assertions.assertEquals("String", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.lang.String", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.lang", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(0, fqjt.getImportList().size());
    }

    @Test
    public void testSimpleType() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("com.foo.Bar");// $NON-NLS-1$

        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Bar", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("com.foo.Bar", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("com.foo", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("com.foo.Bar", fqjt.getImportList().get(0));
    }

    @Test
    public void testSimpleType2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("com.foo.bar");// $NON-NLS-1$

        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("bar", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("com.foo.bar", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("com.foo", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("com.foo.bar", fqjt.getImportList().get(0));
    }

    @Test
    public void testSimpleType3() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("int");// $NON-NLS-1$

        Assertions.assertFalse(fqjt.isExplicitlyImported());
        Assertions.assertEquals("int", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("int", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(0, fqjt.getImportList().size());
    }

    @Test
    public void testGenericType1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<java.lang.String>");// $NON-NLS-1$

        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("List<String>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.List<java.lang.String>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.List", fqjt.getImportList().get(0));
        Assertions.assertEquals("java.util.List", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testGenericType2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.util.List<java.lang.String>>");// $NON-NLS-1$

        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<String, List<String>>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<java.lang.String, java.util.List<java.lang.String>>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testGenericType3() {
        FullyQualifiedJavaType listOfStrings = new FullyQualifiedJavaType("java.util.List");// $NON-NLS-1$

        listOfStrings.addTypeArgument(new FullyQualifiedJavaType("java.lang.String"));// $NON-NLS-1$

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map");// $NON-NLS-1$

        fqjt.addTypeArgument(new FullyQualifiedJavaType("java.lang.String"));// $NON-NLS-1$

        fqjt.addTypeArgument(listOfStrings);
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<String, List<String>>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<java.lang.String, java.util.List<java.lang.String>>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testGenericType4() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<java.util.Map<java.lang.String, java.lang.Object>>");// $NON-NLS-1$

        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("List<Map<String, Object>>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.List<java.util.Map<java.lang.String, java.lang.Object>>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.List", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testWildcardType1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map<java.lang.String, ? extends com.foo.Bar>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<String, ? extends Bar>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<java.lang.String, ? extends com.foo.Bar>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testWildcardType2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map<java.lang.String, ?>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<String, ?>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<java.lang.String, ?>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getImportList().get(0));
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testWildcardType3() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map<? extends java.util.List<?>, ?>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<? extends List<?>, ?>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<? extends java.util.List<?>, ?>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testWildcardType4() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.Map<?, ?>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Map<?, ?>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.Map<?, ?>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.Map", fqjt.getImportList().get(0));
        Assertions.assertEquals("java.util.Map", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testWildcardType5() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<? extends java.util.Map<? super java.lang.Object, ?>>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("List<? extends Map<? super Object, ?>>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.List<? extends java.util.Map<? super java.lang.Object, ?>>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertEquals("java.util.List", fqjt.getFullyQualifiedNameWithoutTypeParameters());// $NON-NLS-1$

    }

    @Test
    public void testUppercasePackage1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("org.foo.Bar.Inner");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Inner", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("org.foo.Bar.Inner", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("org.foo.Bar", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("org.foo.Bar.Inner", fqjt.getImportList().get(0));
    }

    @Test
    public void testUppercasePackage2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("org.foo.Bar.Inner.Inner");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("Inner", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("org.foo.Bar.Inner.Inner", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("org.foo.Bar.Inner", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(1, fqjt.getImportList().size());
        Assertions.assertEquals("org.foo.Bar.Inner.Inner", fqjt.getImportList().get(0));
    }

    @Test
    public void testUppercasePackage3() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<org.foo.Bar.Inner>");
        Assertions.assertTrue(fqjt.isExplicitlyImported());
        Assertions.assertEquals("List<Inner>", fqjt.getShortName());// $NON-NLS-1$

        Assertions.assertEquals("java.util.List<org.foo.Bar.Inner>", fqjt.getFullyQualifiedName());// $NON-NLS-1$

        Assertions.assertEquals("java.util", fqjt.getPackageName());// $NON-NLS-1$

        Assertions.assertEquals(2, fqjt.getImportList().size());
        Assertions.assertTrue(fqjt.getImportList().contains("java.util.List"));
        Assertions.assertTrue(fqjt.getImportList().contains("org.foo.Bar.Inner"));
    }

    @Test
    public void testByteArray1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("byte[]");
        Assertions.assertFalse(fqjt.isPrimitive());
        Assertions.assertTrue(fqjt.isArray());
    }

    @Test
    public void testByteArray2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("byte[ ]");
        Assertions.assertFalse(fqjt.isPrimitive());
        Assertions.assertTrue(fqjt.isArray());
    }

    @Test
    public void testStringArray() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.lang.String[]");
        Assertions.assertFalse(fqjt.isPrimitive());
        Assertions.assertTrue(fqjt.isArray());
    }

    @Test
    public void testComplexArray() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<String>[]");
        Assertions.assertFalse(fqjt.isPrimitive());
        Assertions.assertTrue(fqjt.isArray());
    }

    @Test
    public void testComplexArrayWithoutGenerics() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List[]");
        Assertions.assertFalse(fqjt.isPrimitive());
        Assertions.assertTrue(fqjt.isArray());
        Assertions.assertTrue(fqjt.getImportList().contains("java.util.List"));
        Assertions.assertFalse(fqjt.getImportList().contains("java.util.List[]"));
    }
}

