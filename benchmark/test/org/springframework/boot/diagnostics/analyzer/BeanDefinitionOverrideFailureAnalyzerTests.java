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
package org.springframework.boot.diagnostics.analyzer;


import org.junit.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link BeanDefinitionOverrideFailureAnalyzer}.
 *
 * @author Andy Wilkinson
 */
public class BeanDefinitionOverrideFailureAnalyzerTests {
    @Test
    public void analyzeBeanDefinitionOverrideException() {
        FailureAnalysis analysis = performAnalysis(BeanDefinitionOverrideFailureAnalyzerTests.BeanOverrideConfiguration.class);
        String description = analysis.getDescription();
        assertThat(description).contains((("The bean 'testBean', defined in " + (BeanDefinitionOverrideFailureAnalyzerTests.SecondConfiguration.class.getName())) + ", could not be registered."));
        assertThat(description).contains(BeanDefinitionOverrideFailureAnalyzerTests.FirstConfiguration.class.getName());
    }

    @Configuration
    @Import({ BeanDefinitionOverrideFailureAnalyzerTests.FirstConfiguration.class, BeanDefinitionOverrideFailureAnalyzerTests.SecondConfiguration.class })
    static class BeanOverrideConfiguration {}

    @Configuration
    static class FirstConfiguration {
        @Bean
        public String testBean() {
            return "test";
        }
    }

    @Configuration
    static class SecondConfiguration {
        @Bean
        public String testBean() {
            return "test";
        }
    }
}

