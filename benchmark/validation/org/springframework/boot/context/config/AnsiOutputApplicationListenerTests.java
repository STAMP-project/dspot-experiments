/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.context.config;


import Enabled.ALWAYS;
import Enabled.NEVER;
import WebApplicationType.NONE;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiOutputEnabledValue;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;


/**
 * Tests for {@link AnsiOutputApplicationListener}.
 *
 * @author Phillip Webb
 */
public class AnsiOutputApplicationListenerTests {
    private ConfigurableApplicationContext context;

    @Test
    public void enabled() {
        SpringApplication application = new SpringApplication(AnsiOutputApplicationListenerTests.Config.class);
        application.setWebApplicationType(NONE);
        Map<String, Object> props = new HashMap<>();
        props.put("spring.output.ansi.enabled", "ALWAYS");
        application.setDefaultProperties(props);
        this.context = application.run();
        assertThat(AnsiOutputEnabledValue.get()).isEqualTo(ALWAYS);
    }

    @Test
    public void disabled() {
        SpringApplication application = new SpringApplication(AnsiOutputApplicationListenerTests.Config.class);
        application.setWebApplicationType(NONE);
        Map<String, Object> props = new HashMap<>();
        props.put("spring.output.ansi.enabled", "never");
        application.setDefaultProperties(props);
        this.context = application.run();
        assertThat(AnsiOutputEnabledValue.get()).isEqualTo(NEVER);
    }

    @Test
    public void disabledViaApplicationProperties() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, "spring.config.name=ansi");
        SpringApplication application = new SpringApplication(AnsiOutputApplicationListenerTests.Config.class);
        application.setEnvironment(environment);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(AnsiOutputEnabledValue.get()).isEqualTo(NEVER);
    }

    @Configuration
    public static class Config {}
}

