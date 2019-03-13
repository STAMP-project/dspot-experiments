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
package org.springframework.boot.devtools.env;


import ErrorProperties.IncludeStacktrace.ALWAYS;
import WebApplicationType.NONE;
import java.net.URL;
import java.util.Collections;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.devtools.restart.RestartInitializer;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * Integration tests for the configuration of development-time properties
 *
 * @author Andy Wilkinson
 */
public class DevToolPropertiesIntegrationTests {
    private ConfigurableApplicationContext context;

    @Test
    public void classPropertyConditionIsAffectedByDevToolProperties() {
        SpringApplication application = new SpringApplication(DevToolPropertiesIntegrationTests.ClassConditionConfiguration.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        this.context.getBean(DevToolPropertiesIntegrationTests.ClassConditionConfiguration.class);
    }

    @Test
    public void beanMethodPropertyConditionIsAffectedByDevToolProperties() {
        SpringApplication application = new SpringApplication(DevToolPropertiesIntegrationTests.BeanConditionConfiguration.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        this.context.getBean(DevToolPropertiesIntegrationTests.MyBean.class);
    }

    @Test
    public void postProcessWhenRestarterDisabledAndRemoteSecretNotSetShouldNotAddPropertySource() {
        Restarter.clearInstance();
        Restarter.disable();
        SpringApplication application = new SpringApplication(DevToolPropertiesIntegrationTests.BeanConditionConfiguration.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Test
    public void postProcessWhenRestarterDisabledAndRemoteSecretSetShouldAddPropertySource() {
        Restarter.clearInstance();
        Restarter.disable();
        SpringApplication application = new SpringApplication(DevToolPropertiesIntegrationTests.BeanConditionConfiguration.class);
        application.setWebApplicationType(NONE);
        application.setDefaultProperties(Collections.singletonMap("spring.devtools.remote.secret", "donttell"));
        this.context = application.run();
        this.context.getBean(DevToolPropertiesIntegrationTests.MyBean.class);
    }

    @Test
    public void postProcessEnablesIncludeStackTraceProperty() {
        SpringApplication application = new SpringApplication(DevToolPropertiesIntegrationTests.TestConfiguration.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        ConfigurableEnvironment environment = this.context.getEnvironment();
        String property = environment.getProperty("server.error.include-stacktrace");
        assertThat(property).isEqualTo(ALWAYS.toString());
    }

    @Configuration
    static class TestConfiguration {}

    @Configuration
    @ConditionalOnProperty("spring.h2.console.enabled")
    static class ClassConditionConfiguration {}

    @Configuration
    static class BeanConditionConfiguration {
        @Bean
        @ConditionalOnProperty("spring.h2.console.enabled")
        public DevToolPropertiesIntegrationTests.MyBean myBean() {
            return new DevToolPropertiesIntegrationTests.MyBean();
        }
    }

    static class MyBean {}

    static class MockInitializer implements RestartInitializer {
        @Override
        public URL[] getInitialUrls(Thread thread) {
            return new URL[]{  };
        }
    }
}

