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
package org.springframework.boot.admin;


import WebApplicationType.NONE;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Tests for {@link SpringApplicationAdminMXBeanRegistrar}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class SpringApplicationAdminMXBeanRegistrarTests {
    private static final String OBJECT_NAME = "org.springframework.boot:type=Test,name=SpringApplication";

    private MBeanServer mBeanServer;

    private ConfigurableApplicationContext context;

    @Test
    public void validateReadyFlag() {
        final ObjectName objectName = createObjectName(SpringApplicationAdminMXBeanRegistrarTests.OBJECT_NAME);
        SpringApplication application = new SpringApplication(SpringApplicationAdminMXBeanRegistrarTests.Config.class);
        application.setWebApplicationType(NONE);
        application.addListeners((ContextRefreshedEvent event) -> {
            try {
                assertThat(isApplicationReady(objectName)).isFalse();
            } catch ( ex) {
                throw new <ex>IllegalStateException("Could not contact spring application admin bean");
            }
        });
        this.context = application.run();
        assertThat(isApplicationReady(objectName)).isTrue();
    }

    @Test
    public void eventsFromOtherContextsAreIgnored() throws MalformedObjectNameException {
        SpringApplicationAdminMXBeanRegistrar registrar = new SpringApplicationAdminMXBeanRegistrar(SpringApplicationAdminMXBeanRegistrarTests.OBJECT_NAME);
        ConfigurableApplicationContext context = Mockito.mock(ConfigurableApplicationContext.class);
        registrar.setApplicationContext(context);
        registrar.onApplicationReadyEvent(new org.springframework.boot.context.event.ApplicationReadyEvent(new SpringApplication(), null, Mockito.mock(ConfigurableApplicationContext.class)));
        assertThat(isApplicationReady(registrar)).isFalse();
        registrar.onApplicationReadyEvent(new org.springframework.boot.context.event.ApplicationReadyEvent(new SpringApplication(), null, context));
        assertThat(isApplicationReady(registrar)).isTrue();
    }

    @Test
    public void environmentIsExposed() {
        final ObjectName objectName = createObjectName(SpringApplicationAdminMXBeanRegistrarTests.OBJECT_NAME);
        SpringApplication application = new SpringApplication(SpringApplicationAdminMXBeanRegistrarTests.Config.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--foo.bar=blam");
        assertThat(isApplicationReady(objectName)).isTrue();
        assertThat(isApplicationEmbeddedWebApplication(objectName)).isFalse();
        assertThat(getProperty(objectName, "foo.bar")).isEqualTo("blam");
        assertThat(getProperty(objectName, "does.not.exist.test")).isNull();
    }

    @Test
    public void shutdownApp() throws InstanceNotFoundException {
        final ObjectName objectName = createObjectName(SpringApplicationAdminMXBeanRegistrarTests.OBJECT_NAME);
        SpringApplication application = new SpringApplication(SpringApplicationAdminMXBeanRegistrarTests.Config.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.context.isRunning()).isTrue();
        invokeShutdown(objectName);
        assertThat(this.context.isRunning()).isFalse();
        // JMX cleanup
        assertThatExceptionOfType(InstanceNotFoundException.class).isThrownBy(() -> this.mBeanServer.getObjectInstance(objectName));
    }

    @Configuration
    static class Config {
        @Bean
        public SpringApplicationAdminMXBeanRegistrar springApplicationAdminRegistrar() throws MalformedObjectNameException {
            return new SpringApplicationAdminMXBeanRegistrar(SpringApplicationAdminMXBeanRegistrarTests.OBJECT_NAME);
        }
    }
}

