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
import java.lang.reflect.Modifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import powermock.test.support.MainMockTransformerTestSupport;


public class ConstructorModifiersMockTransformerTest extends AbstractBaseMockTransformerTest {
    public ConstructorModifiersMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_make_all_constructor_public_if_strategy_is_classloader() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.equalTo(CLASSLOADER));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.MultipleConstructors.class.getName());
        assertThat(clazz.getConstructors()).as("All constructor must be public").hasSize(5).extracting("modifiers").contains(Modifier.PUBLIC);
    }

    @Test
    public void should_leave_constructor_unchanged_if_strategy_is_not_classloader() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(CLASSLOADER)));
        Class<?> clazz = MainMockTransformerTestSupport.SupportClasses.MultipleConstructors.class;
        Class<?> modifiedClass = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.MultipleConstructors.class.getName());
        assertThatAllConstructorsHaveSameModifier(clazz, modifiedClass);
    }

    @Test
    public void should_not_change_constructors_of_test_class() throws Exception {
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        final Class<MainMockTransformerTestSupport.SupportClasses.MultipleConstructors> testClass = MainMockTransformerTestSupport.SupportClasses.MultipleConstructors.class;
        setTestClassToTransformers(testClass);
        Class<?> modifiedClass = loadWithMockClassLoader(testClass.getName());
        assertThatAllConstructorsHaveSameModifier(testClass, modifiedClass);
    }

    @Test
    public void should_not_change_constructors_of_nested_test_classes() throws Exception {
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        setTestClassToTransformers(MainMockTransformerTestSupport.ParentTestClass.class);
        final Class<?> originalClazz = MainMockTransformerTestSupport.ParentTestClass.NestedTestClass.class;
        Class<?> modifiedClass = loadWithMockClassLoader(originalClazz.getName());
        assertThatAllConstructorsHaveSameModifier(originalClazz, modifiedClass);
    }
}

