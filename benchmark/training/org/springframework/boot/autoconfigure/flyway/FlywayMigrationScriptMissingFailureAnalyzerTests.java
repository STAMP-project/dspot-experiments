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
package org.springframework.boot.autoconfigure.flyway;


import org.junit.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;


/**
 * Tests for {@link FlywayMigrationScriptMissingFailureAnalyzer}.
 *
 * @author Anand Shastri
 */
public class FlywayMigrationScriptMissingFailureAnalyzerTests {
    @Test
    public void analysisForMissingScriptLocation() {
        FailureAnalysis failureAnalysis = performAnalysis();
        assertThat(failureAnalysis.getDescription()).contains("no migration scripts location is configured");
        assertThat(failureAnalysis.getAction()).contains("Check your Flyway configuration");
    }

    @Test
    public void analysisForScriptLocationsNotFound() {
        FailureAnalysis failureAnalysis = performAnalysis("classpath:db/migration");
        assertThat(failureAnalysis.getDescription()).contains("none of the following migration scripts locations could be found").contains("classpath:db/migration");
        assertThat(failureAnalysis.getAction()).contains("Review the locations above or check your Flyway configuration");
    }
}

