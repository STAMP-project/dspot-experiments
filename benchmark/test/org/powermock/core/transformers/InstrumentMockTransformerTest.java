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


import java.lang.reflect.Field;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;
import org.powermock.core.test.ClassLoaderTestHelper;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import powermock.test.support.MainMockTransformerTestSupport;


public class InstrumentMockTransformerTest extends AbstractBaseMockTransformerTest {
    private static final String SYNTHETIC_FIELD_VALUE = "Synthetic Field Value";

    public InstrumentMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_ignore_call_to_synthetic_field_when_instrument_call_to_method() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForFieldTests(classPool);
        mockTransformerChain.transform(wrap(ctClass));
        ClassLoaderTestHelper.runTestWithNewClassLoader(classPool, InstrumentMockTransformerTest.ShouldIgnoreCallToSyntheticField.class.getName());
    }

    public static class ShouldIgnoreCallToSyntheticField {
        public static void main(String[] args) throws Exception {
            Class clazz = MainMockTransformerTestSupport.SuperClassWithObjectMethod.class;
            Object instance = clazz.newInstance();
            clazz.getMethod("doSomething", Object.class).invoke(instance, new Object());
            assertThat(MockGatewaySpy.getFieldCalls()).doesNotContain(AbstractBaseMockTransformerTest.SYNTH_FIELD);
            Field field = clazz.getDeclaredField(AbstractBaseMockTransformerTest.SYNTH_FIELD);
            field.setAccessible(true);
            String fieldValue = ((String) (field.get(instance)));
            assertThat(fieldValue).isEqualTo("doSomething");
        }
    }
}

