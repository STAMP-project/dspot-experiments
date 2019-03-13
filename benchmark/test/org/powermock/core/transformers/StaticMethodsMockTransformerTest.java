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


import MockGateway.PROCEED;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport;

import static org.powermock.core.transformers.mock.MockGatewaySpy.ConditionBuilder.registered;


public class StaticMethodsMockTransformerTest extends AbstractBaseMockTransformerTest {
    public StaticMethodsMockTransformerTest(final TransformStrategy strategy, final MockTransformer transformer, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, DefaultMockTransformerChain.newBuilder().append(transformer).build(), mockClassloaderFactory);
    }

    @Test
    public void should_skip_call_to_void_static_public_method_if_getaway_return_not_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("voidMethod", "");
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.StaticVoidMethodsTestClass.class.getName());
        WhiteboxImpl.invokeMethod(clazz, "voidMethod", "name", "field", 100.0);
        assertThat(WhiteboxImpl.getInternalState(clazz, "field")).as("Field name is not set").isNull();
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("voidMethod"));
    }

    @Test
    public void should_continue_executing_void_static_public_method_if_getaway_return_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("voidMethod", PROCEED);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.StaticVoidMethodsTestClass.class.getName());
        final String expectedFieldValue = "field";
        WhiteboxImpl.invokeMethod(clazz, "voidMethod", "name", expectedFieldValue, 100.0);
        assertThat(WhiteboxImpl.getInternalState(clazz, "field")).as("Field name is not set").isEqualTo(expectedFieldValue);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("voidMethod"));
    }
}

