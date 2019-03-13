/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.constructor;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.mock.SerializableMode;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CreatingMocksWithConstructorTest extends TestBase {
    abstract static class AbstractMessage {
        private final String message;

        AbstractMessage() {
            this.message = "hey!";
        }

        AbstractMessage(String message) {
            this.message = message;
        }

        AbstractMessage(int i) {
            this.message = String.valueOf(i);
        }

        String getMessage() {
            return message;
        }
    }

    static class Message extends CreatingMocksWithConstructorTest.AbstractMessage {}

    class InnerClass extends CreatingMocksWithConstructorTest.AbstractMessage {}

    @Test
    public void can_create_mock_with_constructor() {
        CreatingMocksWithConstructorTest.Message mock = Mockito.mock(CreatingMocksWithConstructorTest.Message.class, Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));
        // the message is a part of state of the mocked type that gets initialized in constructor
        Assert.assertEquals("hey!", mock.getMessage());
    }

    @Test
    public void can_mock_abstract_classes() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("hey!", mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.spy(CreatingMocksWithConstructorTest.AbstractMessage.class);
        Assert.assertEquals("hey!", mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes_with_constructor_args() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor("hello!").defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("hello!", mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes_with_constructor_primitive_args() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor(7).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("7", mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes_with_constructor_array_of_nulls() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor(new Object[]{ null }).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertNull(mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes_with_casted_null() {
        CreatingMocksWithConstructorTest.AbstractMessage mock = Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor(((String) (null))).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertNull(mock.getMessage());
    }

    @Test
    public void can_spy_abstract_classes_with_null_varargs() {
        try {
            Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor(null).defaultAnswer(Mockito.CALLS_REAL_METHODS));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining(("constructorArgs should not be null. " + "If you need to pass null, please cast it to the right type, e.g.: useConstructor((String) null)"));
        }
    }

    @Test
    public void can_mock_inner_classes() {
        CreatingMocksWithConstructorTest.InnerClass mock = Mockito.mock(CreatingMocksWithConstructorTest.InnerClass.class, Mockito.withSettings().useConstructor().outerInstance(this).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("hey!", mock.getMessage());
    }

    public static class ThrowingConstructorClass {
        public ThrowingConstructorClass() {
            throw new RuntimeException();
        }
    }

    @Test
    public void explains_constructor_exceptions() {
        try {
            Mockito.mock(CreatingMocksWithConstructorTest.ThrowingConstructorClass.class, Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasRootCauseInstanceOf(RuntimeException.class);
            assertThat(e.getCause()).hasMessageContaining("Please ensure the target class has a 0-arg constructor and executes cleanly.");
        }
    }

    static class HasConstructor {
        HasConstructor(String x) {
        }
    }

    @Test
    public void exception_message_when_constructor_not_found() {
        try {
            // when
            Mockito.spy(CreatingMocksWithConstructorTest.HasConstructor.class);
            // then
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessage("Unable to create mock instance of type 'HasConstructor'");
            assertThat(e.getCause()).hasMessageContaining("Please ensure that the target class has a 0-arg constructor.");
        }
    }

    static class Base {}

    static class ExtendsBase extends CreatingMocksWithConstructorTest.Base {}

    static class ExtendsExtendsBase extends CreatingMocksWithConstructorTest.ExtendsBase {}

    static class UsesBase {
        public UsesBase(CreatingMocksWithConstructorTest.Base b) {
            constructorUsed = "Base";
        }

        public UsesBase(CreatingMocksWithConstructorTest.ExtendsBase b) {
            constructorUsed = "ExtendsBase";
        }

        private String constructorUsed = null;

        String getConstructorUsed() {
            return constructorUsed;
        }
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_base_class_exact_match() {
        CreatingMocksWithConstructorTest.UsesBase u = Mockito.mock(CreatingMocksWithConstructorTest.UsesBase.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.Base()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("Base", u.getConstructorUsed());
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_extending_class_exact_match() {
        CreatingMocksWithConstructorTest.UsesBase u = Mockito.mock(CreatingMocksWithConstructorTest.UsesBase.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.ExtendsBase()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("ExtendsBase", u.getConstructorUsed());
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_non_exact_match() {
        CreatingMocksWithConstructorTest.UsesBase u = Mockito.mock(CreatingMocksWithConstructorTest.UsesBase.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.ExtendsExtendsBase()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("ExtendsBase", u.getConstructorUsed());
    }

    static class UsesTwoBases {
        public UsesTwoBases(CreatingMocksWithConstructorTest.Base b1, CreatingMocksWithConstructorTest.Base b2) {
            constructorUsed = "Base,Base";
        }

        public UsesTwoBases(CreatingMocksWithConstructorTest.ExtendsBase b1, CreatingMocksWithConstructorTest.Base b2) {
            constructorUsed = "ExtendsBase,Base";
        }

        public UsesTwoBases(CreatingMocksWithConstructorTest.Base b1, CreatingMocksWithConstructorTest.ExtendsBase b2) {
            constructorUsed = "Base,ExtendsBase";
        }

        private String constructorUsed = null;

        String getConstructorUsed() {
            return constructorUsed;
        }
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_multiple_base_class_exact_match() {
        CreatingMocksWithConstructorTest.UsesTwoBases u = Mockito.mock(CreatingMocksWithConstructorTest.UsesTwoBases.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.Base(), new CreatingMocksWithConstructorTest.Base()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("Base,Base", u.getConstructorUsed());
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_first_extending_class_exact_match() {
        CreatingMocksWithConstructorTest.UsesTwoBases u = Mockito.mock(CreatingMocksWithConstructorTest.UsesTwoBases.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.ExtendsBase(), new CreatingMocksWithConstructorTest.Base()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("ExtendsBase,Base", u.getConstructorUsed());
    }

    @Test
    public void can_mock_unambigous_constructor_with_inheritance_second_extending_class_exact_match() {
        CreatingMocksWithConstructorTest.UsesTwoBases u = Mockito.mock(CreatingMocksWithConstructorTest.UsesTwoBases.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.Base(), new CreatingMocksWithConstructorTest.ExtendsBase()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("Base,ExtendsBase", u.getConstructorUsed());
    }

    @Test
    public void fail_when_multiple_matching_constructors_with_inheritence() {
        try {
            // when
            Mockito.mock(CreatingMocksWithConstructorTest.UsesTwoBases.class, Mockito.withSettings().useConstructor(new CreatingMocksWithConstructorTest.ExtendsBase(), new CreatingMocksWithConstructorTest.ExtendsBase()));
            // then
            Assert.fail();
        } catch (MockitoException e) {
            // TODO the exception message includes Mockito internals like the name of the generated class name.
            // I suspect that we could make this exception message nicer.
            assertThat(e).hasMessage("Unable to create mock instance of type 'UsesTwoBases'");
            assertThat(e.getCause()).hasMessageContaining(("Multiple constructors could be matched to arguments of types " + ("[org.mockitousage.constructor.CreatingMocksWithConstructorTest$ExtendsBase, " + "org.mockitousage.constructor.CreatingMocksWithConstructorTest$ExtendsBase]"))).hasMessageContaining(("If you believe that Mockito could do a better job deciding on which constructor to use, please let us know.\n" + ("Ticket 685 contains the discussion and a workaround for ambiguous constructors using inner class.\n" + "See https://github.com/mockito/mockito/issues/685")));
        }
    }

    @Test
    public void mocking_inner_classes_with_wrong_outer_instance() {
        try {
            // when
            Mockito.mock(CreatingMocksWithConstructorTest.InnerClass.class, Mockito.withSettings().useConstructor().outerInstance(123).defaultAnswer(Mockito.CALLS_REAL_METHODS));
            // then
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessage("Unable to create mock instance of type 'InnerClass'");
            // TODO it would be nice if all useful information was in the top level exception, instead of in the exception's cause
            // also applies to other scenarios in this test
            assertThat(e.getCause()).hasMessageContaining(("Please ensure that the target class has a 0-arg constructor" + " and provided outer instance is correct."));
        }
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void mocking_interfaces_with_constructor() {
        // at the moment this is allowed however we can be more strict if needed
        // there is not much sense in creating a spy of an interface
        Mockito.mock(IMethods.class, Mockito.withSettings().useConstructor());
        Mockito.spy(IMethods.class);
    }

    @Test
    public void prevents_across_jvm_serialization_with_constructor() {
        try {
            // when
            Mockito.mock(CreatingMocksWithConstructorTest.AbstractMessage.class, Mockito.withSettings().useConstructor().serializable(SerializableMode.ACROSS_CLASSLOADERS));
            // then
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertEquals((("Mocks instantiated with constructor cannot be combined with " + (SerializableMode.ACROSS_CLASSLOADERS)) + " serialization mode."), e.getMessage());
        }
    }

    abstract static class AbstractThing {
        abstract String name();

        String fullName() {
            return "abstract " + (name());
        }
    }

    @Test
    public void abstract_method_returns_default() {
        CreatingMocksWithConstructorTest.AbstractThing thing = Mockito.spy(CreatingMocksWithConstructorTest.AbstractThing.class);
        Assert.assertEquals("abstract null", thing.fullName());
    }

    @Test
    public void abstract_method_stubbed() {
        CreatingMocksWithConstructorTest.AbstractThing thing = Mockito.spy(CreatingMocksWithConstructorTest.AbstractThing.class);
        Mockito.when(thing.name()).thenReturn("me");
        Assert.assertEquals("abstract me", thing.fullName());
    }

    @Test
    public void interface_method_stubbed() {
        List<?> list = Mockito.spy(List.class);
        Mockito.when(list.size()).thenReturn(12);
        Assert.assertEquals(12, list.size());
    }

    @Test
    public void calls_real_interface_method() {
        List list = Mockito.mock(List.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertNull(list.get(1));
    }

    @Test
    public void handles_bridge_methods_correctly() {
        CreatingMocksWithConstructorTest.SomeConcreteClass<Integer> testBug = Mockito.spy(new CreatingMocksWithConstructorTest.SomeConcreteClass<Integer>());
        Assert.assertEquals("value", testBug.getValue(0));
    }

    public abstract class SomeAbstractClass<T> {
        protected abstract String getRealValue(T value);

        public String getValue(T value) {
            return getRealValue(value);
        }
    }

    public class SomeConcreteClass<T extends Number> extends CreatingMocksWithConstructorTest.SomeAbstractClass<T> {
        @Override
        protected String getRealValue(T value) {
            return "value";
        }
    }

    private static class AmbiguousWithPrimitive {
        public AmbiguousWithPrimitive(String s, int i) {
            data = s;
        }

        public AmbiguousWithPrimitive(Object o, int i) {
            data = "just an object";
        }

        private String data;

        public String getData() {
            return data;
        }
    }

    @Test
    public void can_spy_ambiguius_constructor_with_primitive() {
        CreatingMocksWithConstructorTest.AmbiguousWithPrimitive mock = Mockito.mock(CreatingMocksWithConstructorTest.AmbiguousWithPrimitive.class, Mockito.withSettings().useConstructor("String", 7).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        Assert.assertEquals("String", mock.getData());
    }
}

