/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockitousage.IMethods;
import org.mockitoutil.ClassLoaders;
import org.mockitoutil.SimpleClassGenerator;


// See issue 453
public class MockingMultipleInterfacesTest {
    class Foo {}

    interface IFoo {}

    interface IBar {}

    @Test
    public void should_allow_multiple_interfaces() {
        // when
        MockingMultipleInterfacesTest.Foo mock = Mockito.mock(MockingMultipleInterfacesTest.Foo.class, Mockito.withSettings().extraInterfaces(MockingMultipleInterfacesTest.IFoo.class, MockingMultipleInterfacesTest.IBar.class));
        // then
        assertThat(mock).isInstanceOf(MockingMultipleInterfacesTest.IFoo.class);
        assertThat(mock).isInstanceOf(MockingMultipleInterfacesTest.IBar.class);
    }

    @Test
    public void should_scream_when_null_passed_instead_of_an_interface() {
        try {
            // when
            Mockito.mock(MockingMultipleInterfacesTest.Foo.class, Mockito.withSettings().extraInterfaces(MockingMultipleInterfacesTest.IFoo.class, null));
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e.getMessage()).contains("extraInterfaces() does not accept null parameters");
        }
    }

    @Test
    public void should_scream_when_no_args_passed() {
        try {
            // when
            Mockito.mock(MockingMultipleInterfacesTest.Foo.class, Mockito.withSettings().extraInterfaces());
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e.getMessage()).contains("extraInterfaces() requires at least one interface");
        }
    }

    @Test
    public void should_scream_when_null_passed_instead_of_an_array() {
        try {
            // when
            Mockito.mock(MockingMultipleInterfacesTest.Foo.class, Mockito.withSettings().extraInterfaces(((Class[]) (null))));
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e.getMessage()).contains("extraInterfaces() requires at least one interface");
        }
    }

    @Test
    public void should_scream_when_non_interface_passed() {
        try {
            // when
            Mockito.mock(MockingMultipleInterfacesTest.Foo.class, Mockito.withSettings().extraInterfaces(MockingMultipleInterfacesTest.Foo.class));
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e.getMessage()).contains("Foo which is not an interface");
        }
    }

    @Test
    public void should_scream_when_the_same_interfaces_passed() {
        try {
            // when
            Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(IMethods.class));
            Assert.fail();
        } catch (MockitoException e) {
            // then
            assertThat(e.getMessage()).contains("You mocked following type: IMethods");
        }
    }

    @Test
    public void should_mock_class_with_interfaces_of_different_class_loader_AND_different_classpaths() throws ClassNotFoundException {
        // Note : if classes are in the same classpath, SearchingClassLoader can find the class/classes and load them in the first matching classloader
        Class<?> interface1 = ClassLoaders.inMemoryClassLoader().withClassDefinition("test.Interface1", SimpleClassGenerator.makeMarkerInterface("test.Interface1")).build().loadClass("test.Interface1");
        Class<?> interface2 = ClassLoaders.inMemoryClassLoader().withClassDefinition("test.Interface2", SimpleClassGenerator.makeMarkerInterface("test.Interface2")).build().loadClass("test.Interface2");
        Object mocked = Mockito.mock(interface1, Mockito.withSettings().extraInterfaces(interface2));
        assertThat(interface2.isInstance(mocked)).describedAs("mock should be assignable from interface2 type").isTrue();
    }
}

