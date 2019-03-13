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
package org.springframework.boot.actuate.autoconfigure.audit;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link AuditEventsEndpointAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Vedran Pavic
 */
public class AuditEventsEndpointAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class, AuditEventsEndpointAutoConfiguration.class));

    @Test
    public void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=auditevents").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenEnabledPropertyIsFalseShouldNotHaveEndpoint() {
        this.contextRunner.withPropertyValues("management.endpoint.auditevents.enabled:false").withPropertyValues("management.endpoints.web.exposure.include=*").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }
}

