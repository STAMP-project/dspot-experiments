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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.jersey;


import java.util.Collections;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.jersey.JerseySameManagementContextConfiguration;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link JerseyWebEndpointManagementContextConfiguration}.
 *
 * @author Michael Simons
 * @author Madhura Bhave
 */
public class JerseyWebEndpointManagementContextConfigurationTests {
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebEndpointAutoConfiguration.class, JerseyWebEndpointManagementContextConfiguration.class)).withUserConfiguration(JerseyWebEndpointManagementContextConfigurationTests.WebEndpointsSupplierConfig.class);

    @Test
    public void resourceConfigCustomizerForEndpointsIsAutoConfigured() {
        this.runner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationIsConditionalOnServletWebApplication() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JerseySameManagementContextConfiguration.class));
        contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void autoConfigurationIsConditionalOnClassResourceConfig() {
        this.runner.withClassLoader(new FilteredClassLoader(ResourceConfig.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    static class WebEndpointsSupplierConfig {
        @Bean
        public WebEndpointsSupplier webEndpointsSupplier() {
            return Collections::emptyList;
        }
    }
}

