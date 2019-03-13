/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;


public class FieldInitializerTest {
    private FieldInitializerTest.StaticClass alreadyInstantiated = new FieldInitializerTest.StaticClass();

    private FieldInitializerTest.StaticClass noConstructor;

    private FieldInitializerTest.StaticClassWithDefaultConstructor defaultConstructor;

    private FieldInitializerTest.StaticClassWithPrivateDefaultConstructor privateDefaultConstructor;

    private FieldInitializerTest.StaticClassWithoutDefaultConstructor noDefaultConstructor;

    private FieldInitializerTest.StaticClassThrowingExceptionDefaultConstructor throwingExDefaultConstructor;

    private FieldInitializerTest.AbstractStaticClass abstractType;

    private FieldInitializerTest.Interface interfaceType;

    private FieldInitializerTest.InnerClassType innerClassType;

    private FieldInitializerTest.AbstractStaticClass instantiatedAbstractType = new FieldInitializerTest.ConcreteStaticClass();

    private FieldInitializerTest.Interface instantiatedInterfaceType = new FieldInitializerTest.ConcreteStaticClass();

    private FieldInitializerTest.InnerClassType instantiatedInnerClassType = new FieldInitializerTest.InnerClassType();

    @Test
    public void should_keep_same_instance_if_field_initialized() throws Exception {
        final FieldInitializerTest.StaticClass backupInstance = alreadyInstantiated;
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("alreadyInstantiated"));
        FieldInitializationReport report = fieldInitializer.initialize();
        Assert.assertSame(backupInstance, report.fieldInstance());
        Assert.assertFalse(report.fieldWasInitialized());
        Assert.assertFalse(report.fieldWasInitializedUsingContructorArgs());
    }

    @Test
    public void should_instantiate_field_when_type_has_no_constructor() throws Exception {
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("noConstructor"));
        FieldInitializationReport report = fieldInitializer.initialize();
        Assert.assertNotNull(report.fieldInstance());
        Assert.assertTrue(report.fieldWasInitialized());
        Assert.assertFalse(report.fieldWasInitializedUsingContructorArgs());
    }

    @Test
    public void should_instantiate_field_with_default_constructor() throws Exception {
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("defaultConstructor"));
        FieldInitializationReport report = fieldInitializer.initialize();
        Assert.assertNotNull(report.fieldInstance());
        Assert.assertTrue(report.fieldWasInitialized());
        Assert.assertFalse(report.fieldWasInitializedUsingContructorArgs());
    }

    @Test
    public void should_instantiate_field_with_private_default_constructor() throws Exception {
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("privateDefaultConstructor"));
        FieldInitializationReport report = fieldInitializer.initialize();
        Assert.assertNotNull(report.fieldInstance());
        Assert.assertTrue(report.fieldWasInitialized());
        Assert.assertFalse(report.fieldWasInitializedUsingContructorArgs());
    }

    @Test(expected = MockitoException.class)
    public void should_fail_to_instantiate_field_if_no_default_constructor() throws Exception {
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("noDefaultConstructor"));
        fieldInitializer.initialize();
    }

    @Test
    public void should_fail_to_instantiate_field_if_default_constructor_throws_exception() throws Exception {
        FieldInitializer fieldInitializer = new FieldInitializer(this, field("throwingExDefaultConstructor"));
        try {
            fieldInitializer.initialize();
            Assert.fail();
        } catch (MockitoException e) {
            InvocationTargetException ite = ((InvocationTargetException) (e.getCause()));
            Assert.assertTrue(((ite.getTargetException()) instanceof NullPointerException));
            Assert.assertEquals("business logic failed", ite.getTargetException().getMessage());
        }
    }

    @Test(expected = MockitoException.class)
    public void should_fail_for_abstract_field() throws Exception {
        new FieldInitializer(this, field("abstractType"));
    }

    @Test
    public void should_not_fail_if_abstract_field_is_instantiated() throws Exception {
        new FieldInitializer(this, field("instantiatedAbstractType"));
    }

    @Test(expected = MockitoException.class)
    public void should_fail_for_interface_field() throws Exception {
        new FieldInitializer(this, field("interfaceType"));
    }

    @Test
    public void should_not_fail_if_interface_field_is_instantiated() throws Exception {
        new FieldInitializer(this, field("instantiatedInterfaceType"));
    }

    @Test(expected = MockitoException.class)
    public void should_fail_for_local_type_field() throws Exception {
        // when
        class LocalType {}
        class TheTestWithLocalType {
            @InjectMocks
            LocalType field;
        }
        TheTestWithLocalType testWithLocalType = new TheTestWithLocalType();
        // when
        new FieldInitializer(testWithLocalType, testWithLocalType.getClass().getDeclaredField("field"));
    }

    @Test
    public void should_not_fail_if_local_type_field_is_instantiated() throws Exception {
        // when
        class LocalType {}
        class TheTestWithLocalType {
            @InjectMocks
            LocalType field = new LocalType();
        }
        TheTestWithLocalType testWithLocalType = new TheTestWithLocalType();
        // when
        new FieldInitializer(testWithLocalType, testWithLocalType.getClass().getDeclaredField("field"));
    }

    @Test(expected = MockitoException.class)
    public void should_fail_for_inner_class_field() throws Exception {
        new FieldInitializer(this, field("innerClassType"));
    }

    @Test
    public void should_not_fail_if_inner_class_field_is_instantiated() throws Exception {
        new FieldInitializer(this, field("instantiatedInnerClassType"));
    }

    @Test
    public void can_instantiate_class_with_parameterized_constructor() throws Exception {
        FieldInitializer.ConstructorArgumentResolver resolver = BDDMockito.given(Mockito.mock(FieldInitializer.ConstructorArgumentResolver.class).resolveTypeInstances(ArgumentMatchers.any(Class.class))).willReturn(new Object[]{ null }).getMock();
        new FieldInitializer(this, field("noDefaultConstructor"), resolver).initialize();
        Assert.assertNotNull(noDefaultConstructor);
    }

    static class StaticClass {}

    static class StaticClassWithDefaultConstructor {
        StaticClassWithDefaultConstructor() {
        }
    }

    static class StaticClassWithPrivateDefaultConstructor {
        private StaticClassWithPrivateDefaultConstructor() {
        }
    }

    static class StaticClassWithoutDefaultConstructor {
        private StaticClassWithoutDefaultConstructor(String param) {
        }
    }

    static class StaticClassThrowingExceptionDefaultConstructor {
        StaticClassThrowingExceptionDefaultConstructor() throws Exception {
            throw new NullPointerException("business logic failed");
        }
    }

    abstract static class AbstractStaticClass {
        public AbstractStaticClass() {
        }
    }

    interface Interface {}

    static class ConcreteStaticClass extends FieldInitializerTest.AbstractStaticClass implements FieldInitializerTest.Interface {}

    class InnerClassType {
        InnerClassType() {
        }
    }
}

