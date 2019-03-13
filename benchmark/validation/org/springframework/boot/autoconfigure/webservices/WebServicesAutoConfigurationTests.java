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
package org.springframework.boot.autoconfigure.webservices;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link WebServicesAutoConfiguration}.
 *
 * @author Vedran Pavic
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Eneias Silva
 */
public class WebServicesAutoConfigurationTests {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebServicesAutoConfiguration.class));

    @Test
    public void defaultConfiguration() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void customPathMustBeginWithASlash() {
        this.contextRunner.withPropertyValues("spring.webservices.path=invalid").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("Failed to bind properties under 'spring.webservices'"));
    }

    @Test
    public void customPath() {
        this.contextRunner.withPropertyValues("spring.webservices.path=/valid").run(( context) -> assertThat(getUrlMappings(context)).contains("/valid/*"));
    }

    @Test
    public void customPathWithTrailingSlash() {
        this.contextRunner.withPropertyValues("spring.webservices.path=/valid/").run(( context) -> assertThat(getUrlMappings(context)).contains("/valid/*"));
    }

    @Test
    public void customLoadOnStartup() {
        this.contextRunner.withPropertyValues("spring.webservices.servlet.load-on-startup=1").run(( context) -> {
            ServletRegistrationBean<?> registrationBean = context.getBean(.class);
            assertThat(ReflectionTestUtils.getField(registrationBean, "loadOnStartup")).isEqualTo(1);
        });
    }

    @Test
    public void customInitParameters() {
        this.contextRunner.withPropertyValues("spring.webservices.servlet.init.key1=value1", "spring.webservices.servlet.init.key2=value2").run(( context) -> assertThat(getServletRegistrationBean(context).getInitParameters()).containsEntry("key1", "value1").containsEntry("key2", "value2"));
    }

    @Test
    public void withWsdlBeans() {
        this.contextRunner.withPropertyValues("spring.webservices.wsdl-locations=classpath:/wsdl").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).containsOnlyKeys("service");
            assertThat(context.getBeansOfType(.class)).containsOnlyKeys("types");
        });
    }

    @Test
    public void withWsdlBeansAsList() {
        this.contextRunner.withPropertyValues("spring.webservices.wsdl-locations[0]=classpath:/wsdl").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).containsOnlyKeys("service");
            assertThat(context.getBeansOfType(.class)).containsOnlyKeys("types");
        });
    }
}

