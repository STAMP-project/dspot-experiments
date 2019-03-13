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
package org.springframework.boot.autoconfigure.jdbc;


import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link HikariDriverConfigurationFailureAnalyzer}.
 *
 * @author Stephane Nicoll
 */
public class HikariDriverConfigurationFailureAnalyzerTests {
    @Test
    public void failureAnalysisIsPerformed() {
        FailureAnalysis failureAnalysis = performAnalysis(HikariDriverConfigurationFailureAnalyzerTests.TestConfiguration.class);
        assertThat(failureAnalysis).isNotNull();
        assertThat(failureAnalysis.getDescription()).isEqualTo(("Configuration of the Hikari connection pool failed: " + "'dataSourceClassName' is not supported."));
        assertThat(failureAnalysis.getAction()).contains("Spring Boot auto-configures only a driver");
    }

    @Test
    public void unrelatedIllegalStateExceptionIsSkipped() {
        FailureAnalysis failureAnalysis = new HikariDriverConfigurationFailureAnalyzer().analyze(new RuntimeException("foo", new IllegalStateException("bar")));
        assertThat(failureAnalysis).isNull();
    }

    @Configuration
    @ImportAutoConfiguration(DataSourceAutoConfiguration.class)
    static class TestConfiguration {}
}

