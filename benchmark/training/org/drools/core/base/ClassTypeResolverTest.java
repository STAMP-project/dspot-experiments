/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.base;


import java.util.HashSet;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.FirstClass;
import org.drools.core.test.model.Person;
import org.drools.core.test.model.SecondClass;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;


public class ClassTypeResolverTest {
    @Test
    public void testResolvePrimtiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(boolean.class, resolver.resolveType("boolean"));
        Assert.assertEquals(double.class, resolver.resolveType("double"));
        Assert.assertEquals(float.class, resolver.resolveType("float"));
        Assert.assertEquals(int.class, resolver.resolveType("int"));
        Assert.assertEquals(char.class, resolver.resolveType("char"));
        Assert.assertEquals(long.class, resolver.resolveType("long"));
        Assert.assertEquals(byte.class, resolver.resolveType("byte"));
        Assert.assertEquals(short.class, resolver.resolveType("short"));
    }

    @Test
    public void testResolveArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(boolean[].class, resolver.resolveType("boolean[]"));
        Assert.assertEquals(double[].class, resolver.resolveType("double[]"));
        Assert.assertEquals(float[].class, resolver.resolveType("float[]"));
        Assert.assertEquals(int[].class, resolver.resolveType("int[]"));
        Assert.assertEquals(char[].class, resolver.resolveType("char[]"));
        Assert.assertEquals(long[].class, resolver.resolveType("long[]"));
        Assert.assertEquals(byte[].class, resolver.resolveType("byte[]"));
        Assert.assertEquals(short[].class, resolver.resolveType("short[]"));
    }

    @Test
    public void testResolveMultidimensionnalArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(int[][].class, resolver.resolveType("int[][]"));
        Assert.assertEquals(int[][][].class, resolver.resolveType("int[][][]"));
        Assert.assertEquals(int[][][][].class, resolver.resolveType("int[][][][]"));
    }

    @Test
    public void testResolveObjectNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(String.class, resolver.resolveType("String"));
        Assert.assertEquals(String.class, resolver.resolveType("java.lang.String"));
        try {
            Assert.assertEquals(Cheese.class, resolver.resolveType("Cheese"));
            Assert.fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        Assert.assertEquals(Cheese.class, resolver.resolveType("org.drools.core.test.model.Cheese"));
    }

    @Test
    public void testResolveObjectFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        resolver.addImport("org.drools.core.test.model.FirstClass");
        resolver.addImport("org.drools.core.test.model.FirstClass.AlternativeKey");
        resolver.addImport("org.drools.core.test.model.SecondClass");
        resolver.addImport("org.drools.core.test.model.SecondClass.AlternativeKey");
        Assert.assertEquals(String.class, resolver.resolveType("String"));
        Assert.assertEquals(String.class, resolver.resolveType("java.lang.String"));
        Assert.assertEquals(Cheese.class, resolver.resolveType("Cheese"));
        Assert.assertEquals(Cheese.class, resolver.resolveType("org.drools.core.test.model.Cheese"));
        Assert.assertEquals(FirstClass.class, resolver.resolveType("org.drools.core.test.model.FirstClass"));
        Assert.assertEquals(FirstClass.AlternativeKey.class, resolver.resolveType("org.drools.core.test.model.FirstClass.AlternativeKey"));
        Assert.assertEquals(SecondClass.class, resolver.resolveType("org.drools.core.test.model.SecondClass"));
        Assert.assertEquals(SecondClass.AlternativeKey.class, resolver.resolveType("org.drools.core.test.model.SecondClass.AlternativeKey"));
    }

    @Test
    public void testResolveObjectFromImportNested() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.FirstClass");
        Assert.assertEquals(FirstClass.AlternativeKey.class, resolver.resolveType("FirstClass.AlternativeKey"));
    }

    @Test
    public void testResolveFullTypeName() throws Exception {
        final TypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        resolver.addImport("org.drools.core.test.model.FirstClass");
        Assert.assertEquals("org.drools.core.test.model.Cheese", resolver.getFullTypeName("Cheese"));
        Assert.assertEquals("org.drools.core.test.model.FirstClass", resolver.getFullTypeName("FirstClass"));
    }

    @Test
    public void testResolveObjectFromImportMultipleClassesDifferentPackages() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        Assert.assertEquals(String.class, resolver.resolveType("String"));
        Assert.assertEquals(String.class, resolver.resolveType("java.lang.String"));
        Assert.assertEquals(Cheese.class, resolver.resolveType("Cheese"));
        Assert.assertEquals(Cheese.class, resolver.resolveType("org.drools.core.test.model.Cheese"));
    }

    @Test
    public void testResolveArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(String[].class, resolver.resolveType("String[]"));
        Assert.assertEquals(String[].class, resolver.resolveType("java.lang.String[]"));
        try {
            Assert.assertEquals(Cheese[].class, resolver.resolveType("Cheese[]"));
            Assert.fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        Assert.assertEquals(Cheese[].class, resolver.resolveType("org.drools.core.test.model.Cheese[]"));
    }

    @Test
    public void testResolveArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        Assert.assertEquals(String[].class, resolver.resolveType("String[]"));
        Assert.assertEquals(String[].class, resolver.resolveType("java.lang.String[]"));
        Assert.assertEquals(Cheese[].class, resolver.resolveType("Cheese[]"));
        Assert.assertEquals(Cheese[].class, resolver.resolveType("org.drools.core.test.model.Cheese[]"));
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        Assert.assertEquals(String[][].class, resolver.resolveType("String[][]"));
        Assert.assertEquals(String[][].class, resolver.resolveType("java.lang.String[][]"));
        try {
            Assert.assertEquals(Cheese[][].class, resolver.resolveType("Cheese[][]"));
            Assert.fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        Assert.assertEquals(Cheese[][].class, resolver.resolveType("org.drools.core.test.model.Cheese[][]"));
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        Assert.assertEquals(String[][].class, resolver.resolveType("String[][]"));
        Assert.assertEquals(String[][].class, resolver.resolveType("java.lang.String[][]"));
        Assert.assertEquals(Cheese[][].class, resolver.resolveType("Cheese[][]"));
        Assert.assertEquals(Cheese[][].class, resolver.resolveType("org.drools.core.test.model.Cheese[][]"));
    }

    @Test
    public void testDefaultPackageImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("Goo");
        try {
            resolver.resolveType("Goo");
            Assert.fail("Can't import default namespace classes");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }

    @Test
    public void testNestedClassResolving() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        // single nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1");
        Assert.assertEquals(Person.Nested1.class, resolver.resolveType("Nested1"));
        // double nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1.Nested2");
        Assert.assertEquals(Person.Nested1.Nested2.class, resolver.resolveType("Nested2"));
        // triple nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1.Nested2.Nested3");
        Assert.assertEquals(Person.Nested1.Nested2.Nested3.class, resolver.resolveType("Nested3"));
    }

    @Test
    public void testMacOSXClassLoaderBehavior() throws Exception {
        SimulateMacOSXClassLoader simulatedMacOSXClassLoader = new SimulateMacOSXClassLoader(Thread.currentThread().getContextClassLoader(), new HashSet());
        simulatedMacOSXClassLoader.addClassInScope(Cheese.class);
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), simulatedMacOSXClassLoader);
        resolver.addImport("org.drools.core.test.model.*");
        Assert.assertEquals(Cheese.class, resolver.resolveType("Cheese"));
        try {
            resolver.resolveType("cheese");// <<- on Mac/OSX throws NoClassDefFoundError which escapes the try/catch and fail the test.

            // while on say Linux, it passes the test (catched as ClassNotFoundException)
            Assert.fail("the type cheese (lower-case c) should not exists at all");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }

    @Test
    public void testMacOSXClassLoaderBehaviorNested() throws Exception {
        SimulateMacOSXClassLoader simulatedMacOSXClassLoader = new SimulateMacOSXClassLoader(Thread.currentThread().getContextClassLoader(), new HashSet());
        simulatedMacOSXClassLoader.addClassInScope(Person.Nested1.Nested2.class);
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), simulatedMacOSXClassLoader);
        resolver.addImport("org.drools.core.test.model.*");
        Assert.assertEquals(Person.Nested1.Nested2.class, resolver.resolveType("Person.Nested1.Nested2"));
        try {
            resolver.resolveType("Person.nested1.nested2");// <<- on Mac/OSX throws NoClassDefFoundError which escapes the try/catch and fail the test.

            // while on say Linux, it passes the test (catched as ClassNotFoundException)
            Assert.fail("should have resolved nothing.");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }
}

