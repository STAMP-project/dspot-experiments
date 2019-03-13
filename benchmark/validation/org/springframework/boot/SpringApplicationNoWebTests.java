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
package org.springframework.boot;


import WebApplicationType.NONE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticApplicationContext;


/**
 * Tests for {@link SpringApplication} when spring web is not on the classpath.
 *
 * @author Stephane Nicoll
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("spring-web*.jar")
public class SpringApplicationNoWebTests {
    private ConfigurableApplicationContext context;

    @Test
    public void detectWebApplicationTypeToNone() {
        SpringApplication application = new SpringApplication(SpringApplicationNoWebTests.ExampleConfig.class);
        assertThat(application.getWebApplicationType()).isEqualTo(NONE);
    }

    @Test
    public void specificApplicationContextClass() {
        SpringApplication application = new SpringApplication(SpringApplicationNoWebTests.ExampleConfig.class);
        application.setApplicationContextClass(StaticApplicationContext.class);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(StaticApplicationContext.class);
    }

    @Configuration
    static class ExampleConfig {}
}

