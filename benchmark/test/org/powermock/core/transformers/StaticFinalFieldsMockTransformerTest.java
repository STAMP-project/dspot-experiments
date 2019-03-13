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


import TransformStrategy.INST_REDEFINE;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport;


public class StaticFinalFieldsMockTransformerTest extends AbstractBaseMockTransformerTest {
    public StaticFinalFieldsMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_remove_final_modifier_from_static_final_field_if_strategy_not_redefine() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.class.getName());
        assertThatFieldIsNotFinal(clazz, "finalStaticField");
        assertThatFieldIsFinal(clazz, "finalField");
    }

    @Test
    public void should_not_remove_final_modifier_from_static_final_field_if_strategy_redefine() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.equalTo(INST_REDEFINE));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.class.getName());
        assertThatFieldIsFinal(clazz, "finalStaticField");
        assertThatFieldIsFinal(clazz, "finalField");
    }

    @Test
    public void should_remove_only_final_modifier_but_keep_transient() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.class.getName());
        Field finalTransientStaticField = WhiteboxImpl.getField(clazz, "finalStaticTransientField");
        assertThat(Modifier.isFinal(finalTransientStaticField.getModifiers())).isFalse();
        assertThat(Modifier.isTransient(finalTransientStaticField.getModifiers())).isTrue();
    }
}

