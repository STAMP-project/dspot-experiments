/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import java.io.ByteArrayOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import net.bytebuddy.ClassFileVersion;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitoutil.SimpleSerializationUtil;
import org.mockitoutil.TestBase;


@SuppressWarnings({ "unchecked", "serial" })
public class MocksSerializationTest extends TestBase implements Serializable {
    private static final long serialVersionUID = 6160482220413048624L;

    @Test
    public void should_allow_throws_exception_to_be_serializable() throws Exception {
        // given
        MocksSerializationTest.Bar mock = Mockito.mock(MocksSerializationTest.Bar.class, new ThrowsException(new RuntimeException()));
        // when-serialize then-deserialize
        SimpleSerializationUtil.serializeAndBack(mock);
    }

    @Test
    public void should_allow_method_delegation() throws Exception {
        // given
        MocksSerializationTest.Bar barMock = Mockito.mock(MocksSerializationTest.Bar.class, Mockito.withSettings().serializable());
        MocksSerializationTest.Foo fooMock = Mockito.mock(MocksSerializationTest.Foo.class);
        Mockito.when(barMock.doSomething()).thenAnswer(new ThrowsException(new RuntimeException()));
        // when-serialize then-deserialize
        SimpleSerializationUtil.serializeAndBack(barMock);
    }

    @Test
    public void should_allow_mock_to_be_serializable() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        // when-serialize then-deserialize
        SimpleSerializationUtil.serializeAndBack(mock);
    }

    @Test
    public void should_allow_mock_and_boolean_value_to_serializable() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        Mockito.when(mock.booleanReturningMethod()).thenReturn(true);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertTrue(readObject.booleanReturningMethod());
    }

    @Test
    public void should_allow_mock_and_string_value_to_be_serializable() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        String value = "value";
        Mockito.when(mock.stringReturningMethod()).thenReturn(value);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertEquals(value, readObject.stringReturningMethod());
    }

    @Test
    public void should_all_mock_and_serializable_value_to_be_serialized() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        List<?> value = Collections.emptyList();
        Mockito.when(mock.objectReturningMethodNoArgs()).thenReturn(value);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertEquals(value, readObject.objectReturningMethodNoArgs());
    }

    @Test
    public void should_serialize_method_call_with_parameters_that_are_serializable() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        List<?> value = Collections.emptyList();
        Mockito.when(mock.objectArgMethod(value)).thenReturn(value);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertEquals(value, readObject.objectArgMethod(value));
    }

    @Test
    public void should_serialize_method_calls_using_any_string_matcher() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        List<?> value = Collections.emptyList();
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.anyString())).thenReturn(value);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertEquals(value, readObject.objectArgMethod(""));
    }

    @Test
    public void should_verify_called_n_times_for_serialized_mock() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        List<?> value = Collections.emptyList();
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.anyString())).thenReturn(value);
        mock.objectArgMethod("");
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Mockito.verify(readObject, Mockito.times(1)).objectArgMethod("");
    }

    @Test
    public void should_verify_even_if_some_methods_called_after_serialization() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        // when
        mock.simpleMethod(1);
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        readObject.simpleMethod(1);
        // then
        Mockito.verify(readObject, Mockito.times(2)).simpleMethod(1);
        // this test is working because it seems that java serialization mechanism replaces all instances
        // of serialized object in the object graph (if there are any)
    }

    class Bar implements Serializable {
        MocksSerializationTest.Foo foo;

        public MocksSerializationTest.Foo doSomething() {
            return foo;
        }
    }

    class Foo implements Serializable {
        MocksSerializationTest.Bar bar;

        Foo() {
            bar = new MocksSerializationTest.Bar();
            bar.foo = this;
        }
    }

    @Test
    public void should_serialization_work() throws Exception {
        // given
        MocksSerializationTest.Foo foo = new MocksSerializationTest.Foo();
        // when
        foo = SimpleSerializationUtil.serializeAndBack(foo);
        // then
        Assert.assertSame(foo, foo.bar.foo);
    }

    @Test
    public void should_stub_even_if_some_methods_called_after_serialization() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        // when
        Mockito.when(mock.simpleMethod(1)).thenReturn("foo");
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Mockito.when(readObject.simpleMethod(2)).thenReturn("bar");
        // then
        Assert.assertEquals("foo", readObject.simpleMethod(1));
        Assert.assertEquals("bar", readObject.simpleMethod(2));
    }

    @Test
    public void should_verify_call_order_for_serialized_mock() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        IMethods mock2 = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        mock.arrayReturningMethod();
        mock2.arrayReturningMethod();
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        ByteArrayOutputStream serialized2 = SimpleSerializationUtil.serializeMock(mock2);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        IMethods readObject2 = SimpleSerializationUtil.deserializeMock(serialized2, IMethods.class);
        InOrder inOrder = Mockito.inOrder(readObject, readObject2);
        inOrder.verify(readObject).arrayReturningMethod();
        inOrder.verify(readObject2).arrayReturningMethod();
    }

    @Test
    public void should_remember_interactions_for_serialized_mock() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        List<?> value = Collections.emptyList();
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.anyString())).thenReturn(value);
        mock.objectArgMethod("happened");
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Mockito.verify(readObject, Mockito.never()).objectArgMethod("never happened");
    }

    @Test
    public void should_serialize_with_stubbing_callback() throws Exception {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable());
        MocksSerializationTest.CustomAnswersMustImplementSerializableForSerializationToWork answer = new MocksSerializationTest.CustomAnswersMustImplementSerializableForSerializationToWork();
        answer.string = "return value";
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.anyString())).thenAnswer(answer);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        IMethods readObject = SimpleSerializationUtil.deserializeMock(serialized, IMethods.class);
        Assert.assertEquals(answer.string, readObject.objectArgMethod(""));
    }

    class CustomAnswersMustImplementSerializableForSerializationToWork implements Serializable , Answer<Object> {
        private String string;

        public Object answer(InvocationOnMock invocation) throws Throwable {
            invocation.getArguments();
            invocation.getMock();
            return string;
        }
    }

    @Test
    public void should_serialize_with_real_object_spy() throws Exception {
        // given
        MocksSerializationTest.SerializableClass sample = new MocksSerializationTest.SerializableClass();
        MocksSerializationTest.SerializableClass spy = Mockito.mock(MocksSerializationTest.SerializableClass.class, Mockito.withSettings().spiedInstance(sample).defaultAnswer(Mockito.CALLS_REAL_METHODS).serializable());
        Mockito.when(spy.foo()).thenReturn("foo");
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(spy);
        // then
        MocksSerializationTest.SerializableClass readObject = SimpleSerializationUtil.deserializeMock(serialized, MocksSerializationTest.SerializableClass.class);
        Assert.assertEquals("foo", readObject.foo());
    }

    @Test
    public void should_serialize_object_mock() throws Exception {
        // given
        Any mock = Mockito.mock(Any.class);
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        SimpleSerializationUtil.deserializeMock(serialized, Any.class);
    }

    @Test
    public void should_serialize_real_partial_mock() throws Exception {
        // given
        Any mock = Mockito.mock(Any.class, Mockito.withSettings().serializable());
        Mockito.when(mock.matches(ArgumentMatchers.anyObject())).thenCallRealMethod();
        // when
        ByteArrayOutputStream serialized = SimpleSerializationUtil.serializeMock(mock);
        // then
        Any readObject = SimpleSerializationUtil.deserializeMock(serialized, Any.class);
        readObject.matches("");
    }

    class AlreadySerializable implements Serializable {}

    @Test
    public void should_serialize_already_serializable_class() throws Exception {
        // given
        MocksSerializationTest.AlreadySerializable mock = Mockito.mock(MocksSerializationTest.AlreadySerializable.class, Mockito.withSettings().serializable());
        Mockito.when(mock.toString()).thenReturn("foo");
        // when
        mock = SimpleSerializationUtil.serializeAndBack(mock);
        // then
        Assert.assertEquals("foo", mock.toString());
    }

    @Test
    public void should_be_serialize_and_have_extra_interfaces() throws Exception {
        // when
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().serializable().extraInterfaces(List.class));
        IMethods mockTwo = Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(List.class).serializable());
        // then
        Assertions.assertThat(((Object) (SimpleSerializationUtil.serializeAndBack(((List) (mock)))))).isInstanceOf(List.class).isInstanceOf(IMethods.class);
        Assertions.assertThat(((Object) (SimpleSerializationUtil.serializeAndBack(((List) (mockTwo)))))).isInstanceOf(List.class).isInstanceOf(IMethods.class);
    }

    static class SerializableAndNoDefaultConstructor implements Serializable {
        SerializableAndNoDefaultConstructor(Observable o) {
            super();
        }
    }

    @Test
    public void should_be_able_to_serialize_type_that_implements_Serializable_but_but_dont_declare_a_no_arg_constructor() throws Exception {
        SimpleSerializationUtil.serializeAndBack(Mockito.mock(MocksSerializationTest.SerializableAndNoDefaultConstructor.class));
    }

    public static class AClassWithPrivateNoArgConstructor {
        private AClassWithPrivateNoArgConstructor() {
        }

        List returningSomething() {
            return Collections.emptyList();
        }
    }

    @Test
    public void private_constructor_currently_not_supported_at_the_moment_at_deserialization_time() throws Exception {
        // given
        MocksSerializationTest.AClassWithPrivateNoArgConstructor mockWithPrivateConstructor = Mockito.mock(MocksSerializationTest.AClassWithPrivateNoArgConstructor.class, Mockito.withSettings().serializable());
        try {
            // when
            SimpleSerializationUtil.serializeAndBack(mockWithPrivateConstructor);
            Assert.fail("should have thrown an ObjectStreamException or a subclass of it");
        } catch (ObjectStreamException e) {
            // then
            Assertions.assertThat(e.toString()).contains("no valid constructor");
        }
    }

    @Test
    public void BUG_ISSUE_399_try_some_mocks_with_current_answers() throws Exception {
        Assume.assumeTrue(ClassFileVersion.ofThisVm().isAtLeast(ClassFileVersion.JAVA_V7));
        IMethods iMethods = Mockito.mock(IMethods.class, Mockito.withSettings().serializable().defaultAnswer(Mockito.RETURNS_DEEP_STUBS));
        Mockito.when(iMethods.iMethodsReturningMethod().linkedListReturningMethod().contains(ArgumentMatchers.anyString())).thenReturn(false);
        SimpleSerializationUtil.serializeAndBack(iMethods);
    }

    public static class SerializableClass implements Serializable {
        public String foo() {
            return null;
        }
    }
}

