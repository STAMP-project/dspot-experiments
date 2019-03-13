/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;


import java.io.Serializable;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.handler.MockHandlerImpl;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationContainer;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.mock.SerializableMode;
import org.mockito.plugins.MockMaker;
import org.mockito.stubbing.Answer;
import org.mockitoutil.ClassLoaders;
import org.mockitoutil.SimpleSerializationUtil;
import org.objenesis.ObjenesisStd;


public abstract class AbstractByteBuddyMockMakerTest<MM extends MockMaker> {
    protected final MM mockMaker;

    public AbstractByteBuddyMockMakerTest(MM mockMaker) {
        this.mockMaker = mockMaker;
    }

    @Test
    public void should_create_mock_from_interface() throws Exception {
        AbstractByteBuddyMockMakerTest.SomeInterface proxy = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.SomeInterface.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        Class<?> superClass = proxy.getClass().getSuperclass();
        AbstractByteBuddyMockMakerTest.assertThat(superClass).isEqualTo(Object.class);
    }

    @Test
    public void should_create_mock_from_class() throws Exception {
        AbstractByteBuddyMockMakerTest<MM>.ClassWithoutConstructor proxy = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.ClassWithoutConstructor.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        Class<?> superClass = mockTypeOf(proxy.getClass());
        AbstractByteBuddyMockMakerTest.assertThat(superClass).isEqualTo(AbstractByteBuddyMockMakerTest.ClassWithoutConstructor.class);
    }

    @Test
    public void should_create_mock_from_class_even_when_constructor_is_dodgy() throws Exception {
        try {
            new ClassWithDodgyConstructor();
            Assert.fail();
        } catch (Exception expected) {
        }
        AbstractByteBuddyMockMakerTest<MM>.ClassWithDodgyConstructor mock = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.ClassWithDodgyConstructor.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        AbstractByteBuddyMockMakerTest.assertThat(mock).isNotNull();
    }

    @Test
    public void should_mocks_have_different_interceptors() throws Exception {
        AbstractByteBuddyMockMakerTest<MM>.SomeClass mockOne = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.SomeClass.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        AbstractByteBuddyMockMakerTest<MM>.SomeClass mockTwo = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.SomeClass.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        MockHandler handlerOne = mockMaker.getHandler(mockOne);
        MockHandler handlerTwo = mockMaker.getHandler(mockTwo);
        AbstractByteBuddyMockMakerTest.assertThat(handlerOne).isNotSameAs(handlerTwo);
    }

    @Test
    public void should_use_ancillary_Types() {
        AbstractByteBuddyMockMakerTest<MM>.SomeClass mock = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsFor(AbstractByteBuddyMockMakerTest.SomeClass.class, AbstractByteBuddyMockMakerTest.SomeInterface.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        AbstractByteBuddyMockMakerTest.assertThat(mock).isInstanceOf(AbstractByteBuddyMockMakerTest.SomeInterface.class);
    }

    @Test
    public void should_create_class_by_constructor() {
        AbstractByteBuddyMockMakerTest.OtherClass mock = mockMaker.createMock(AbstractByteBuddyMockMakerTest.settingsWithConstructorFor(AbstractByteBuddyMockMakerTest.OtherClass.class), AbstractByteBuddyMockMakerTest.dummyHandler());
        AbstractByteBuddyMockMakerTest.assertThat(mock).isNotNull();
    }

    @Test
    public void should_allow_serialization() throws Exception {
        AbstractByteBuddyMockMakerTest.SerializableClass proxy = mockMaker.createMock(AbstractByteBuddyMockMakerTest.serializableSettingsFor(AbstractByteBuddyMockMakerTest.SerializableClass.class, SerializableMode.BASIC), AbstractByteBuddyMockMakerTest.dummyHandler());
        AbstractByteBuddyMockMakerTest.SerializableClass serialized = SimpleSerializationUtil.serializeAndBack(proxy);
        AbstractByteBuddyMockMakerTest.assertThat(serialized).isNotNull();
        MockHandler handlerOne = mockMaker.getHandler(proxy);
        MockHandler handlerTwo = mockMaker.getHandler(serialized);
        AbstractByteBuddyMockMakerTest.assertThat(handlerOne).isNotSameAs(handlerTwo);
    }

    @Test
    public void should_create_mock_from_class_with_super_call_to_final_method() throws Exception {
        MockCreationSettings<AbstractByteBuddyMockMakerTest.CallingSuperMethodClass> settings = AbstractByteBuddyMockMakerTest.settingsWithSuperCall(AbstractByteBuddyMockMakerTest.CallingSuperMethodClass.class);
        AbstractByteBuddyMockMakerTest.SampleClass proxy = mockMaker.createMock(settings, new MockHandlerImpl<AbstractByteBuddyMockMakerTest.CallingSuperMethodClass>(settings));
        AbstractByteBuddyMockMakerTest.assertThat(proxy.foo()).isEqualTo("foo");
    }

    @Test
    public void should_reset_mock_and_set_new_handler() throws Throwable {
        MockCreationSettings<AbstractByteBuddyMockMakerTest.SampleClass> settings = AbstractByteBuddyMockMakerTest.settingsWithSuperCall(AbstractByteBuddyMockMakerTest.SampleClass.class);
        AbstractByteBuddyMockMakerTest.SampleClass proxy = mockMaker.createMock(settings, new MockHandlerImpl<AbstractByteBuddyMockMakerTest.SampleClass>(settings));
        MockHandler handler = new MockHandlerImpl<AbstractByteBuddyMockMakerTest.SampleClass>(settings);
        mockMaker.resetMock(proxy, handler, settings);
        AbstractByteBuddyMockMakerTest.assertThat(mockMaker.getHandler(proxy)).isSameAs(handler);
    }

    class SomeClass {}

    interface SomeInterface {}

    static class OtherClass {}

    static class SerializableClass implements Serializable {}

    private class ClassWithoutConstructor {}

    private class ClassWithDodgyConstructor {
        public ClassWithDodgyConstructor() {
            throw new RuntimeException();
        }
    }

    @Test
    public void instantiate_fine_when_objenesis_on_the_classpath() throws Exception {
        // given
        ClassLoader classpath_with_objenesis = ClassLoaders.excludingClassLoader().withCodeSourceUrlOf(Mockito.class, ByteBuddy.class, ObjenesisStd.class).withCodeSourceUrlOf(ClassLoaders.coverageTool()).build();
        Class<?> mock_maker_class_loaded_fine_until = Class.forName("org.mockito.internal.creation.bytebuddy.SubclassByteBuddyMockMaker", true, classpath_with_objenesis);
        // when
        mock_maker_class_loaded_fine_until.newInstance();
        // then everything went fine
    }

    private static class DummyMockHandler implements MockHandler<Object> {
        public Object handle(Invocation invocation) throws Throwable {
            return null;
        }

        public MockCreationSettings<Object> getMockSettings() {
            return null;
        }

        public InvocationContainer getInvocationContainer() {
            return null;
        }

        public void setAnswersForStubbing(List<Answer<?>> list) {
        }
    }

    private static class SampleClass {
        public String foo() {
            return "foo";
        }
    }

    private static class CallingSuperMethodClass extends AbstractByteBuddyMockMakerTest.SampleClass {
        @Override
        public String foo() {
            return super.foo();
        }
    }
}

