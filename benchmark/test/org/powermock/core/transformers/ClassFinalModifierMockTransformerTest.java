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
import java.lang.reflect.Modifier;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import powermock.test.support.MainMockTransformerTestSupport;


public class ClassFinalModifierMockTransformerTest extends AbstractBaseMockTransformerTest {
    public ClassFinalModifierMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_remove_final_modifier_from_static_final_inner_classes_strategy_not_equals_to_inst_redefine() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.StaticFinalInnerClass.class.getName());
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void should_nit_remove_final_modifier_from_static_final_inner_classes_equals_to_inst_redefine() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.equalTo(INST_REDEFINE));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.StaticFinalInnerClass.class.getName());
        assertThat(Modifier.isFinal(clazz.getModifiers())).isTrue();
    }

    @Test
    public void should_remove_final_modifier_from_final_inner_classes() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.FinalInnerClass.class.getName());
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void should_remove_final_modifier_from_enums() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.EnumClass.class.getName());
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void should_remove_final_modifier_from_private_static_final_inner_classes() throws Exception {
        Assume.assumeThat(strategy, CoreMatchers.not(CoreMatchers.equalTo(INST_REDEFINE)));
        Class<?> clazz = loadWithMockClassLoader(((MainMockTransformerTestSupport.SupportClasses.class.getName()) + "$PrivateStaticFinalInnerClass"));
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void should_ignore_interfaces() throws Exception {
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                loadWithMockClassLoader(MainMockTransformerTestSupport.SomeInterface.class.getName());
            }
        });
        assertThat(throwable).isNull();
    }
}

