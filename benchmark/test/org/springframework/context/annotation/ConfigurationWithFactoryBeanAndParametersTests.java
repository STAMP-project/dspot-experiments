/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.context.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;


/**
 * Test case cornering the bug initially raised with SPR-8762, in which a
 * NullPointerException would be raised if a FactoryBean-returning @Bean method also
 * accepts parameters
 *
 * @author Chris Beams
 * @since 3.1
 */
public class ConfigurationWithFactoryBeanAndParametersTests {
    @Test
    public void test() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationWithFactoryBeanAndParametersTests.Config.class, ConfigurationWithFactoryBeanAndParametersTests.Bar.class);
        Assert.assertNotNull(ctx.getBean(ConfigurationWithFactoryBeanAndParametersTests.Bar.class).foo);
    }

    @Configuration
    static class Config {
        @Bean
        public FactoryBean<ConfigurationWithFactoryBeanAndParametersTests.Foo> fb(@Value("42")
        String answer) {
            return new ConfigurationWithFactoryBeanAndParametersTests.FooFactoryBean();
        }
    }

    static class Foo {}

    static class Bar {
        ConfigurationWithFactoryBeanAndParametersTests.Foo foo;

        @Autowired
        public Bar(ConfigurationWithFactoryBeanAndParametersTests.Foo foo) {
            this.foo = foo;
        }
    }

    static class FooFactoryBean implements FactoryBean<ConfigurationWithFactoryBeanAndParametersTests.Foo> {
        @Override
        public ConfigurationWithFactoryBeanAndParametersTests.Foo getObject() {
            return new ConfigurationWithFactoryBeanAndParametersTests.Foo();
        }

        @Override
        public Class<ConfigurationWithFactoryBeanAndParametersTests.Foo> getObjectType() {
            return ConfigurationWithFactoryBeanAndParametersTests.Foo.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}

