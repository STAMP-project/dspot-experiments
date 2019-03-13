/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.module;


import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.navercorp.pinpoint.profiler.AgentInfoSender;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class DefaultApplicationContextTest {
    private static final String PINPOINT_PACKAGE_PREFIX = "com.navercorp.pinpoint.";

    @Test
    public void test() {
        DefaultApplicationContext applicationContext = newApplicationContext();
        try {
            Injector injector = applicationContext.getInjector();
            Map<Key<?>, Binding<?>> bindings = injector.getBindings();
            for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
                Key<?> key = e.getKey();
                Binding<?> binding = e.getValue();
                if (isPinpointBinding(key)) {
                    boolean isSingletonScoped = Scopes.isSingleton(binding);
                    Assert.assertTrue((("Binding " + key) + " is not Singleton scoped"), isSingletonScoped);
                }
            }
            AgentInfoSender instance1 = injector.getInstance(AgentInfoSender.class);
            AgentInfoSender instance2 = injector.getInstance(AgentInfoSender.class);
            Assert.assertSame(instance1, instance2);
        } finally {
            applicationContext.close();
        }
    }
}

