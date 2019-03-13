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
package org.springframework.boot.actuate.autoconfigure.context.properties;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ConfigurationPropertiesReportEndpointAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class ConfigurationPropertiesReportEndpointAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ConfigurationPropertiesReportEndpointAutoConfiguration.class));

    @Test
    public void runShouldHaveEndpointBean() {
        this.contextRunner.withUserConfiguration(ConfigurationPropertiesReportEndpointAutoConfigurationTests.Config.class).withPropertyValues("management.endpoints.web.exposure.include=configprops").run(validateTestProperties("******", "654321"));
    }

    @Test
    public void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.configprops.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void keysToSanitizeCanBeConfiguredViaTheEnvironment() {
        this.contextRunner.withUserConfiguration(ConfigurationPropertiesReportEndpointAutoConfigurationTests.Config.class).withPropertyValues("management.endpoint.configprops.keys-to-sanitize: .*pass.*, property").withPropertyValues("management.endpoints.web.exposure.include=configprops").run(validateTestProperties("******", "******"));
    }

    @Test
    public void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public ConfigurationPropertiesReportEndpointAutoConfigurationTests.TestProperties testProperties() {
            return new ConfigurationPropertiesReportEndpointAutoConfigurationTests.TestProperties();
        }
    }

    @ConfigurationProperties("test")
    static class TestProperties {
        private String dbPassword = "123456";

        private String myTestProperty = "654321";

        public String getDbPassword() {
            return this.dbPassword;
        }

        public void setDbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
        }

        public String getMyTestProperty() {
            return this.myTestProperty;
        }

        public void setMyTestProperty(String myTestProperty) {
            this.myTestProperty = myTestProperty;
        }
    }
}

