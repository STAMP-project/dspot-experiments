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
package org.springframework.boot.autoconfigure.aop;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link AopAutoConfiguration}.
 *
 * @author Eberhard Wolff
 * @author Stephane Nicoll
 */
public class AopAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(AopAutoConfiguration.class));

    @Test
    public void aopDisabled() {
        this.contextRunner.withUserConfiguration(AopAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.aop.auto:false").run(( context) -> {
            org.springframework.boot.autoconfigure.aop.TestAspect aspect = context.getBean(.class);
            assertThat(aspect.isCalled()).isFalse();
            org.springframework.boot.autoconfigure.aop.TestBean bean = context.getBean(.class);
            bean.foo();
            assertThat(aspect.isCalled()).isFalse();
        });
    }

    @Test
    public void aopWithDefaultSettings() {
        this.contextRunner.withUserConfiguration(AopAutoConfigurationTests.TestConfiguration.class).run(proxyTargetClassEnabled());
    }

    @Test
    public void aopWithEnabledProxyTargetClass() {
        this.contextRunner.withUserConfiguration(AopAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.aop.proxy-target-class:true").run(proxyTargetClassEnabled());
    }

    @Test
    public void aopWithDisabledProxyTargetClass() {
        this.contextRunner.withUserConfiguration(AopAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.aop.proxy-target-class:false").run(proxyTargetClassDisabled());
    }

    @Test
    public void customConfigurationWithProxyTargetClassDefaultDoesNotDisableProxying() {
        this.contextRunner.withUserConfiguration(AopAutoConfigurationTests.CustomTestConfiguration.class).run(proxyTargetClassEnabled());
    }

    @EnableAspectJAutoProxy
    @Configuration
    @Import(AopAutoConfigurationTests.TestConfiguration.class)
    protected static class CustomTestConfiguration {}

    @Configuration
    protected static class TestConfiguration {
        @Bean
        public AopAutoConfigurationTests.TestAspect aspect() {
            return new AopAutoConfigurationTests.TestAspect();
        }

        @Bean
        public AopAutoConfigurationTests.TestInterface bean() {
            return new AopAutoConfigurationTests.TestBean();
        }
    }

    protected static class TestBean implements AopAutoConfigurationTests.TestInterface {
        @Override
        public void foo() {
        }
    }

    @Aspect
    protected static class TestAspect {
        private boolean called;

        public boolean isCalled() {
            return this.called;
        }

        @Before("execution(* foo(..))")
        public void before() {
            this.called = true;
        }
    }

    public interface TestInterface {
        void foo();
    }
}

