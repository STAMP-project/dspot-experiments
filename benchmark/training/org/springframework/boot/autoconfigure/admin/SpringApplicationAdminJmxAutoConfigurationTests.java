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
package org.springframework.boot.autoconfigure.admin;


import WebApplicationType.NONE;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.admin.SpringApplicationAdminMXBeanRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;


/**
 * Tests for {@link SpringApplicationAdminJmxAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class SpringApplicationAdminJmxAutoConfigurationTests {
    private static final String ENABLE_ADMIN_PROP = "spring.application.admin.enabled=true";

    private static final String DEFAULT_JMX_NAME = "org.springframework.boot:type=Admin,name=SpringApplication";

    private final MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(SpringApplicationAdminJmxAutoConfigurationTests.MultipleMBeanExportersConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class));

    @Test
    public void notRegisteredByDefault() {
        this.contextRunner.run(( context) -> assertThatExceptionOfType(.class).isThrownBy(() -> this.server.getObjectInstance(createDefaultObjectName())));
    }

    @Test
    public void registeredWithProperty() {
        this.contextRunner.withPropertyValues(SpringApplicationAdminJmxAutoConfigurationTests.ENABLE_ADMIN_PROP).run(( context) -> {
            ObjectName objectName = createDefaultObjectName();
            ObjectInstance objectInstance = this.server.getObjectInstance(objectName);
            assertThat(objectInstance).as("Lifecycle bean should have been registered").isNotNull();
        });
    }

    @Test
    public void registerWithCustomJmxName() {
        String customJmxName = "org.acme:name=FooBar";
        this.contextRunner.withSystemProperties(("spring.application.admin.jmx-name=" + customJmxName)).withPropertyValues(SpringApplicationAdminJmxAutoConfigurationTests.ENABLE_ADMIN_PROP).run(( context) -> {
            try {
                this.server.getObjectInstance(createObjectName(customJmxName));
            } catch ( ex) {
                fail("Admin MBean should have been exposed with custom name");
            }
            assertThatExceptionOfType(.class).isThrownBy(() -> this.server.getObjectInstance(createDefaultObjectName()));
        });
    }

    @Test
    public void registerWithSimpleWebApp() throws Exception {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class, SpringApplicationAdminJmxAutoConfigurationTests.MultipleMBeanExportersConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class).run(("--" + (SpringApplicationAdminJmxAutoConfigurationTests.ENABLE_ADMIN_PROP)), "--server.port=0")) {
            assertThat(context).isInstanceOf(ServletWebServerApplicationContext.class);
            assertThat(this.server.getAttribute(createDefaultObjectName(), "EmbeddedWebApplication")).isEqualTo(Boolean.TRUE);
            int expected = getWebServer().getPort();
            String actual = getProperty(createDefaultObjectName(), "local.server.port");
            assertThat(actual).isEqualTo(String.valueOf(expected));
        }
    }

    @Test
    public void onlyRegisteredOnceWhenThereIsAChildContext() {
        SpringApplicationBuilder parentBuilder = new SpringApplicationBuilder().web(NONE).sources(SpringApplicationAdminJmxAutoConfigurationTests.MultipleMBeanExportersConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class);
        SpringApplicationBuilder childBuilder = parentBuilder.child(SpringApplicationAdminJmxAutoConfigurationTests.MultipleMBeanExportersConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class).web(NONE);
        try (ConfigurableApplicationContext parent = parentBuilder.run(("--" + (SpringApplicationAdminJmxAutoConfigurationTests.ENABLE_ADMIN_PROP)));ConfigurableApplicationContext child = childBuilder.run(("--" + (SpringApplicationAdminJmxAutoConfigurationTests.ENABLE_ADMIN_PROP)))) {
            BeanFactoryUtils.beanOfType(parent.getBeanFactory(), SpringApplicationAdminMXBeanRegistrar.class);
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> BeanFactoryUtils.beanOfType(child.getBeanFactory(), .class));
        }
    }

    @Configuration
    static class MultipleMBeanExportersConfiguration {
        @Bean
        public MBeanExporter firstMBeanExporter() {
            return new MBeanExporter();
        }

        @Bean
        public MBeanExporter secondMBeanExporter() {
            return new MBeanExporter();
        }
    }
}

