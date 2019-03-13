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
package org.springframework.boot.diagnostics;


import javax.annotation.PostConstruct;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.context.annotation.Configuration;


/**
 * Integration tests for {@link FailureAnalyzers}.
 *
 * @author Andy Wilkinson
 */
public class FailureAnalyzersIntegrationTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void analysisIsPerformed() {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> new SpringApplicationBuilder(.class).web(WebApplicationType.NONE).run());
        assertThat(this.outputCapture.toString()).contains("APPLICATION FAILED TO START");
    }

    @Configuration
    static class TestConfiguration {
        @PostConstruct
        public void fail() {
            throw new PortInUseException(8080);
        }
    }
}

