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


import java.util.List;
import org.junit.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.diagnostics.FailureAnalysis;


/**
 * Tests for {@link UnboundConfigurationPropertyFailureAnalyzer}.
 *
 * @author Madhura Bhave
 */
public class UnboundConfigurationPropertyFailureAnalyzerTests {
    @Test
    public void bindExceptionDueToUnboundElements() {
        FailureAnalysis analysis = performAnalysis(UnboundConfigurationPropertyFailureAnalyzerTests.UnboundElementsFailureConfiguration.class, "test.foo.listValue[0]=hello", "test.foo.listValue[2]=world");
        assertThat(analysis.getDescription()).contains(UnboundConfigurationPropertyFailureAnalyzerTests.failure("test.foo.listvalue[2]", "world", "\"test.foo.listValue[2]\" from property source \"test\"", "The elements [test.foo.listvalue[2]] were left unbound."));
    }

    @EnableConfigurationProperties(UnboundConfigurationPropertyFailureAnalyzerTests.UnboundElementsFailureProperties.class)
    static class UnboundElementsFailureConfiguration {}

    @ConfigurationProperties("test.foo")
    static class UnboundElementsFailureProperties {
        private List<String> listValue;

        public List<String> getListValue() {
            return this.listValue;
        }

        public void setListValue(List<String> listValue) {
            this.listValue = listValue;
        }
    }
}

