package org.powermock.core.classloader;


import ByteCodeFramework.Javassist;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.powermock.PowerMockInternalException;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TestClassAwareTransformer;


public class MockClassLoaderBuilderTest {
    @Test
    public void should_create_instance_of_MockClassLoader_depends_on_provided_bytecode_framework() {
        MockClassLoader classLoader = MockClassLoaderBuilder.create(Javassist).forTestClass(getClass()).build();
        assertThat(classLoader).isExactlyInstanceOf(JavassistMockClassLoader.class);
    }

    @Test
    public void should_create_transformer_chain_depends_on_provided_bytecode_framework() {
        MockClassLoader classLoader = MockClassLoaderBuilder.create(Javassist).forTestClass(getClass()).build();
        assertThatJavassistMockTransformerChainCreated(classLoader);
    }

    @Test
    public void should_set_test_class_to_TestClassAwareTransformers() {
        final MockClassLoaderBuilderTest.SpyMockTransformer extraMockTransformer = new MockClassLoaderBuilderTest.SpyMockTransformer();
        MockClassLoaderBuilder.create(Javassist).forTestClass(MockClassLoaderBuilderTest.class).addExtraMockTransformers(extraMockTransformer).build();
        assertThat(extraMockTransformer.testClass).as("Test class is set ").isEqualTo(MockClassLoaderBuilderTest.class);
    }

    @Test
    public void should_throw_internal_exception_if_test_class_is_null() {
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() {
                MockClassLoaderBuilder.create(Javassist).build();
            }
        }).as("Internal exception has been thrown.").isExactlyInstanceOf(PowerMockInternalException.class);
    }

    private static class SpyMockTransformer<T> implements MockTransformer<T> , TestClassAwareTransformer {
        private Class<?> testClass;

        @Override
        public ClassWrapper<T> transform(final ClassWrapper<T> clazz) {
            return null;
        }

        @Override
        public void setTestClass(final Class<?> testClass) {
            this.testClass = testClass;
        }
    }
}

