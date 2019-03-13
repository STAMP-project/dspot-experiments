/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.camel.NoFactoryAvailableException;
import org.apache.camel.spi.ClassResolver;
import org.apache.camel.spi.Injector;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultFactoryFinderTest {
    public static class TestImplA implements DefaultFactoryFinderTest.TestType {}

    public static class TestImplB implements DefaultFactoryFinderTest.TestType {}

    public interface TestType {}

    private static final String TEST_RESOURCE_PATH = "/org/apache/camel/impl/";

    final DefaultFactoryFinder factoryFinder = new DefaultFactoryFinder(new DefaultClassResolver(), DefaultFactoryFinderTest.TEST_RESOURCE_PATH);

    @Test
    public void shouldComplainIfClassResolverCannotResolveClass() throws IOException {
        final ClassResolver classResolver = Mockito.mock(ClassResolver.class);
        final String properties = "class=" + (DefaultFactoryFinderTest.TestImplA.class.getName());
        Mockito.when(classResolver.loadResourceAsStream("/org/apache/camel/impl/TestImplA")).thenReturn(new ByteArrayInputStream(properties.getBytes()));
        Mockito.when(classResolver.resolveClass(DefaultFactoryFinderTest.TestImplA.class.getName())).thenReturn(null);
        final DefaultFactoryFinder factoryFinder = new DefaultFactoryFinder(classResolver, DefaultFactoryFinderTest.TEST_RESOURCE_PATH);
        try {
            factoryFinder.findClass("TestImplA", null);
            Assert.fail("Should have thrown ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            Assert.assertEquals(DefaultFactoryFinderTest.TestImplA.class.getName(), e.getMessage());
        }
    }

    @Test
    public void shouldComplainIfInstanceTypeIsNotAsExpected() throws IOException, ClassNotFoundException {
        final Injector injector = Mockito.mock(Injector.class);
        final DefaultFactoryFinderTest.TestImplA expected = new DefaultFactoryFinderTest.TestImplA();
        Mockito.when(injector.newInstance(DefaultFactoryFinderTest.TestImplA.class)).thenReturn(expected);
        try {
            factoryFinder.newInstances("TestImplA", injector, DefaultFactoryFinderTest.TestImplB.class);
            Assert.fail("ClassCastException should have been thrown");
        } catch (final ClassCastException e) {
            final String message = e.getMessage();
            Assert.assertThat(message, Matchers.matchesPattern(("Not instanceof org\\.apache\\.camel\\.impl\\.DefaultFactoryFinderTest\\$TestImplB " + "value: org\\.apache\\.camel\\.impl\\.DefaultFactoryFinderTest\\$TestImplA.*")));
        }
    }

    @Test
    public void shouldComplainIfUnableToCreateNewInstances() throws IOException, ClassNotFoundException {
        try {
            factoryFinder.newInstance("TestImplX");
            Assert.fail("NoFactoryAvailableException should have been thrown");
        } catch (final NoFactoryAvailableException e) {
            Assert.assertEquals("Could not find factory class for resource: TestImplX", e.getMessage());
        }
    }

    @Test
    public void shouldComplainNoClassKeyInPropertyFile() throws ClassNotFoundException {
        try {
            factoryFinder.findClass("TestImplNoProperty");
            Assert.fail("NoFactoryAvailableException should have been thrown");
        } catch (final IOException e) {
            Assert.assertEquals("Expected property is missing: class", e.getMessage());
        }
    }

    @Test
    public void shouldCreateNewInstances() throws IOException, ClassNotFoundException {
        final Object instance = factoryFinder.newInstance("TestImplA");
        Assert.assertThat(instance, IsInstanceOf.instanceOf(DefaultFactoryFinderTest.TestImplA.class));
    }

    @Test
    public void shouldCreateNewInstancesWithInjector() throws IOException, ClassNotFoundException {
        final Injector injector = Mockito.mock(Injector.class);
        final DefaultFactoryFinderTest.TestImplA expected = new DefaultFactoryFinderTest.TestImplA();
        Mockito.when(injector.newInstance(DefaultFactoryFinderTest.TestImplA.class)).thenReturn(expected);
        final List<DefaultFactoryFinderTest.TestType> instances = factoryFinder.newInstances("TestImplA", injector, DefaultFactoryFinderTest.TestType.class);
        Assert.assertEquals(1, instances.size());
        Assert.assertThat(instances, IsCollectionContaining.hasItem(expected));
        Assert.assertSame(expected, instances.get(0));
    }

    @Test
    public void shouldFindSingleClass() throws IOException, ClassNotFoundException {
        final Class<?> clazz = factoryFinder.findClass("TestImplA");
        Assert.assertEquals(DefaultFactoryFinderTest.TestImplA.class, clazz);
    }

    @Test
    public void shouldFindSingleClassFromClassMap() throws IOException, ClassNotFoundException {
        final DefaultFactoryFinder factoryFinder = new DefaultFactoryFinder(null, null);
        factoryFinder.addToClassMap("prefixkey", () -> .class);
        final Class<?> clazz = factoryFinder.findClass("key", "prefix");
        Assert.assertEquals(DefaultFactoryFinderTest.TestImplA.class, clazz);
    }

    @Test
    public void shouldFindSingleClassWithPropertyPrefix() throws IOException, ClassNotFoundException {
        final Class<?> clazz = factoryFinder.findClass("TestImplA", "prefix.");
        Assert.assertEquals(DefaultFactoryFinderTest.TestImplA.class, clazz);
    }

    @Test
    public void shouldFindSingleClassWithPropertyPrefixAndExpectedType() throws IOException, ClassNotFoundException {
        final Class<?> clazz = factoryFinder.findClass("TestImplA", "prefix.", DefaultFactoryFinderTest.TestType.class);
        Assert.assertEquals(DefaultFactoryFinderTest.TestImplA.class, clazz);
    }
}

