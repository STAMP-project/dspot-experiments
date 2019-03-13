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


import TransformStrategy.CLASSLOADER;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Test;
import org.powermock.core.MockRepository;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.reflect.Whitebox;
import powermock.test.support.MainMockTransformerTestSupport;


public class SuppressStaticInitializerMockTransformerTest extends AbstractBaseMockTransformerTest {
    public SuppressStaticInitializerMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_suppress_static_initialization_if_class_is_added_to_mock_repository() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.equalTo(CLASSLOADER));
        String className = MainMockTransformerTestSupport.StaticInitialization.class.getName();
        MockRepository.addSuppressStaticInitializer(className);
        Class<?> clazz = loadWithMockClassLoader(className);
        Object value = Whitebox.getInternalState(clazz, "value");
        assertThat(value).as("Value not initialized").isNull();
    }

    @Test
    public void should_not_suppress_static_initialization_if_class_is_not_added_to_mock_repository() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.equalTo(CLASSLOADER));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.StaticInitialization.class.getName());
        Object value = Whitebox.getInternalState(clazz, "value");
        assertThat(value).as("Value initialized").isNotNull();
    }

    @Test
    public void should_not_suppress_static_initialization_if_class_is_added_to_mock_repository_but_strategy_not_classloader() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(CLASSLOADER)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.StaticInitialization.class.getName());
        Object value = Whitebox.getInternalState(clazz, "value");
        assertThat(value).as("Value initialized").isNotNull();
    }
}

