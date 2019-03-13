/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.diagnostics.analyzer;


import org.junit.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Tests for {@link BeanNotOfRequiredTypeFailureAnalyzer}.
 *
 * @author Andy Wilkinson
 */
public class BeanNotOfRequiredTypeFailureAnalyzerTests {
    private final FailureAnalyzer analyzer = new BeanNotOfRequiredTypeFailureAnalyzer();

    @Test
    public void jdkProxyCausesInjectionFailure() {
        FailureAnalysis analysis = performAnalysis(BeanNotOfRequiredTypeFailureAnalyzerTests.JdkProxyConfiguration.class);
        assertThat(analysis.getDescription()).startsWith("The bean 'asyncBean'");
        assertThat(analysis.getDescription()).contains((("'" + (BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBean.class.getName())) + "'"));
        assertThat(analysis.getDescription()).endsWith(String.format("%s%n", BeanNotOfRequiredTypeFailureAnalyzerTests.SomeInterface.class.getName()));
    }

    @Configuration
    @EnableAsync
    @Import(BeanNotOfRequiredTypeFailureAnalyzerTests.UserConfiguration.class)
    static class JdkProxyConfiguration {
        @Bean
        public BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBean asyncBean() {
            return new BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBean();
        }
    }

    @Configuration
    static class UserConfiguration {
        @Bean
        public BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBeanUser user(BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBean bean) {
            return new BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBeanUser(bean);
        }
    }

    static class AsyncBean implements BeanNotOfRequiredTypeFailureAnalyzerTests.SomeInterface {
        @Async
        public void foo() {
        }

        @Override
        public void bar() {
        }
    }

    interface SomeInterface {
        void bar();
    }

    static class AsyncBeanUser {
        AsyncBeanUser(BeanNotOfRequiredTypeFailureAnalyzerTests.AsyncBean asyncBean) {
        }
    }
}

