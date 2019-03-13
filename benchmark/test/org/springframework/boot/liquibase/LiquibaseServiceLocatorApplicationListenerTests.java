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
package org.springframework.boot.liquibase;


import WebApplicationType.NONE;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import liquibase.servicelocator.CustomResolverServiceLocator;
import liquibase.servicelocator.DefaultPackageScanClassResolver;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;


/**
 * Tests for {@link LiquibaseServiceLocatorApplicationListener}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class LiquibaseServiceLocatorApplicationListenerTests {
    private ConfigurableApplicationContext context;

    @Test
    public void replacesServiceLocator() throws IllegalAccessException {
        SpringApplication application = new SpringApplication(LiquibaseServiceLocatorApplicationListenerTests.Conf.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        Object resolver = getClassResolver();
        assertThat(resolver).isInstanceOf(SpringPackageScanClassResolver.class);
    }

    @Test
    public void replaceServiceLocatorBacksOffIfNotPresent() throws IllegalAccessException {
        SpringApplication application = new SpringApplication(LiquibaseServiceLocatorApplicationListenerTests.Conf.class);
        application.setWebApplicationType(NONE);
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        resourceLoader.setClassLoader(new LiquibaseServiceLocatorApplicationListenerTests.ClassHidingClassLoader(CustomResolverServiceLocator.class));
        application.setResourceLoader(resourceLoader);
        this.context = application.run();
        Object resolver = getClassResolver();
        assertThat(resolver).isInstanceOf(DefaultPackageScanClassResolver.class);
    }

    @Configuration
    public static class Conf {}

    private final class ClassHidingClassLoader extends URLClassLoader {
        private final List<Class<?>> hiddenClasses;

        private ClassHidingClassLoader(Class<?>... hiddenClasses) {
            super(new URL[0], LiquibaseServiceLocatorApplicationListenerTests.class.getClassLoader());
            this.hiddenClasses = Arrays.asList(hiddenClasses);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (isHidden(name)) {
                throw new ClassNotFoundException();
            }
            return super.loadClass(name);
        }

        private boolean isHidden(String name) {
            for (Class<?> hiddenClass : this.hiddenClasses) {
                if (hiddenClass.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}

