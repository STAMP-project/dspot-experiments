/**
 * Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.powermock.core.transformers;


import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import powermock.test.support.ClassWithLargeMethods;


public class MethodSizeMockTransformerTest extends AbstractBaseMockTransformerTest {
    public MethodSizeMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_load_class_with_method_lower_than_jvm_limit() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodLowerThanLimit.class.getName());
        Assert.assertNotNull("Class has been loaded", clazz);
        // There should be no exception since method was not overridden
        clazz.getMethod("init").invoke(clazz);
    }

    @Test
    public void should_load_class_and_override_method_greater_than_jvm_limit() throws Exception {
        final Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodGreaterThanLimit.class.getName());
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                clazz.getMethod("init").invoke(clazz);
            }
        });
        assertThat(throwable).as("Overridden method should throw exception").isNotNull();
        assertThat(throwable.getCause()).as("Clause of exception should be IllegalAccessException").isInstanceOf(IllegalAccessException.class).hasMessageContaining("Method was too large and after instrumentation exceeded JVM limit");
    }
}

