/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server;


import GitUriFailureAnalyzer.ACTION;
import GitUriFailureAnalyzer.DESCRIPTION;
import WebApplicationType.SERVLET;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.rule.OutputCapture;


/**
 *
 *
 * @author Ryan Baxter
 */
public class NativeBootstrapFailureAnalyzerTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void contextLoads() {
        try {
            new SpringApplicationBuilder(ConfigServerApplication.class).web(SERVLET).properties("spring.cloud.bootstrap.name:enable-nativebootstrap").profiles("test", "native").run();
            fail("Application started successfully");
        } catch (Exception ex) {
            assertThat(this.outputCapture.toString()).contains(ACTION);
            assertThat(this.outputCapture.toString()).contains(DESCRIPTION);
        }
    }
}

