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
package org.springframework.boot.context.logging;


import WebApplicationType.NONE;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


/**
 * Integration tests for {@link LoggingApplicationListener}.
 *
 * @author Stephane Nicoll
 */
public class LoggingApplicationListenerIntegrationTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void loggingSystemRegisteredInTheContext() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(LoggingApplicationListenerIntegrationTests.SampleService.class).web(NONE).run()) {
            LoggingApplicationListenerIntegrationTests.SampleService service = context.getBean(LoggingApplicationListenerIntegrationTests.SampleService.class);
            assertThat(service.loggingSystem).isNotNull();
        }
    }

    @Test
    public void loggingPerformedDuringChildApplicationStartIsNotLost() {
        new SpringApplicationBuilder(LoggingApplicationListenerIntegrationTests.Config.class).web(NONE).child(LoggingApplicationListenerIntegrationTests.Config.class).web(NONE).listeners(new org.springframework.context.ApplicationListener<ApplicationStartingEvent>() {
            private final Logger logger = LoggerFactory.getLogger(getClass());

            @Override
            public void onApplicationEvent(ApplicationStartingEvent event) {
                this.logger.info("Child application starting");
            }
        }).run();
        assertThat(this.outputCapture.toString()).contains("Child application starting");
    }

    @Component
    static class SampleService {
        private final LoggingSystem loggingSystem;

        SampleService(LoggingSystem loggingSystem) {
            this.loggingSystem = loggingSystem;
        }
    }

    static class Config {}
}

