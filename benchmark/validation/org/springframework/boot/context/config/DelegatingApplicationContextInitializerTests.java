/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.context.config;


import org.junit.Test;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;


/**
 * Tests for {@link DelegatingApplicationContextInitializer}.
 *
 * @author Phillip Webb
 */
public class DelegatingApplicationContextInitializerTests {
    private final DelegatingApplicationContextInitializer initializer = new DelegatingApplicationContextInitializer();

    @Test
    public void orderedInitialize() {
        StaticApplicationContext context = new StaticApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, ((("context.initializer.classes=" + (DelegatingApplicationContextInitializerTests.MockInitB.class.getName())) + ",") + (DelegatingApplicationContextInitializerTests.MockInitA.class.getName())));
        this.initializer.initialize(context);
        assertThat(context.getBeanFactory().getSingleton("a")).isEqualTo("a");
        assertThat(context.getBeanFactory().getSingleton("b")).isEqualTo("b");
    }

    @Test
    public void noInitializers() {
        StaticApplicationContext context = new StaticApplicationContext();
        this.initializer.initialize(context);
    }

    @Test
    public void emptyInitializers() {
        StaticApplicationContext context = new StaticApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, "context.initializer.classes:");
        this.initializer.initialize(context);
    }

    @Test
    public void noSuchInitializerClass() {
        StaticApplicationContext context = new StaticApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, "context.initializer.classes=missing.madeup.class");
        assertThatExceptionOfType(ApplicationContextException.class).isThrownBy(() -> this.initializer.initialize(context));
    }

    @Test
    public void notAnInitializerClass() {
        StaticApplicationContext context = new StaticApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, ("context.initializer.classes=" + (Object.class.getName())));
        assertThatIllegalArgumentException().isThrownBy(() -> this.initializer.initialize(context));
    }

    @Test
    public void genericNotSuitable() {
        StaticApplicationContext context = new StaticApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, ("context.initializer.classes=" + (DelegatingApplicationContextInitializerTests.NotSuitableInit.class.getName())));
        assertThatIllegalArgumentException().isThrownBy(() -> this.initializer.initialize(context)).withMessageContaining("generic parameter");
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    private static class MockInitA implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().registerSingleton("a", "a");
        }
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    private static class MockInitB implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            assertThat(applicationContext.getBeanFactory().getSingleton("a")).isEqualTo("a");
            applicationContext.getBeanFactory().registerSingleton("b", "b");
        }
    }

    private static class NotSuitableInit implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
        @Override
        public void initialize(ConfigurableWebApplicationContext applicationContext) {
        }
    }
}

