/**
 * Copyright 2012-2019 the original author or authors.
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
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link InvalidConfigurationPropertyNameFailureAnalyzer}.
 *
 * @author Madhura Bhave
 */
public class InvalidConfigurationPropertyNameFailureAnalyzerTests {
    private InvalidConfigurationPropertyNameFailureAnalyzer analyzer = new InvalidConfigurationPropertyNameFailureAnalyzer();

    @Test
    public void analysisWhenRootCauseIsBeanCreationFailureShouldContainBeanName() {
        BeanCreationException failure = createFailure(InvalidConfigurationPropertyNameFailureAnalyzerTests.InvalidPrefixConfiguration.class);
        FailureAnalysis analysis = this.analyzer.analyze(failure);
        assertThat(analysis.getDescription()).contains(String.format("%n    Invalid characters: %s%n    Bean: %s%n    Reason: %s", "'F', 'P'", "invalidPrefixProperties", ("Canonical names should be kebab-case ('-' separated), " + "lowercase alpha-numeric characters and must start with a letter")));
    }

    @Configuration
    @EnableConfigurationProperties(InvalidConfigurationPropertyNameFailureAnalyzerTests.InvalidPrefixProperties.class)
    static class InvalidPrefixConfiguration {
        @Bean(name = "invalidPrefixProperties")
        public InvalidConfigurationPropertyNameFailureAnalyzerTests.InvalidPrefixProperties invalidPrefixProperties() {
            return new InvalidConfigurationPropertyNameFailureAnalyzerTests.InvalidPrefixProperties();
        }
    }

    @ConfigurationProperties("FooPrefix")
    static class InvalidPrefixProperties {
        private String value;

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

