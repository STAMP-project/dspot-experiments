/**
 * Copyright 2002-2016 the original author or authors.
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests cornering bug SPR-8514.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ConfigurationWithFactoryBeanAndAutowiringTests {
    @Test
    public void withConcreteFactoryBeanImplementationAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.ConcreteFactoryBeanImplementationConfig.class);
        ctx.refresh();
    }

    @Test
    public void withParameterizedFactoryBeanImplementationAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.ParameterizedFactoryBeanImplementationConfig.class);
        ctx.refresh();
    }

    @Test
    public void withParameterizedFactoryBeanInterfaceAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.ParameterizedFactoryBeanInterfaceConfig.class);
        ctx.refresh();
    }

    @Test
    public void withNonPublicParameterizedFactoryBeanInterfaceAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.NonPublicParameterizedFactoryBeanInterfaceConfig.class);
        ctx.refresh();
    }

    @Test
    public void withRawFactoryBeanInterfaceAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.RawFactoryBeanInterfaceConfig.class);
        ctx.refresh();
    }

    @Test
    public void withWildcardParameterizedFactoryBeanInterfaceAsReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.WildcardParameterizedFactoryBeanInterfaceConfig.class);
        ctx.refresh();
    }

    @Test
    public void withFactoryBeanCallingBean() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.AppConfig.class);
        ctx.register(ConfigurationWithFactoryBeanAndAutowiringTests.FactoryBeanCallingConfig.class);
        ctx.refresh();
        Assert.assertEquals("true", ctx.getBean("myString"));
    }

    static class DummyBean {}

    static class MyFactoryBean implements FactoryBean<String> , InitializingBean {
        private boolean initialized = false;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.initialized = true;
        }

        @Override
        public String getObject() throws Exception {
            return "foo";
        }

        @Override
        public Class<String> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        public String getString() {
            return Boolean.toString(this.initialized);
        }
    }

    static class MyParameterizedFactoryBean<T> implements FactoryBean<T> {
        private final T obj;

        public MyParameterizedFactoryBean(T obj) {
            this.obj = obj;
        }

        @Override
        public T getObject() throws Exception {
            return obj;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<T> getObjectType() {
            return ((Class<T>) (obj.getClass()));
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    @Configuration
    static class AppConfig {
        @Bean
        public ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean() {
            return new ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean();
        }
    }

    @Configuration
    static class ConcreteFactoryBeanImplementationConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        public ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }
    }

    @Configuration
    static class ParameterizedFactoryBeanImplementationConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        public ConfigurationWithFactoryBeanAndAutowiringTests.MyParameterizedFactoryBean<String> factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyParameterizedFactoryBean<>("whatev");
        }
    }

    @Configuration
    static class ParameterizedFactoryBeanInterfaceConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        public FactoryBean<String> factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }
    }

    @Configuration
    static class NonPublicParameterizedFactoryBeanInterfaceConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        FactoryBean<String> factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }
    }

    @Configuration
    static class RawFactoryBeanInterfaceConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        @SuppressWarnings("rawtypes")
        public FactoryBean factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }
    }

    @Configuration
    static class WildcardParameterizedFactoryBeanInterfaceConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        public FactoryBean<?> factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }
    }

    @Configuration
    static class FactoryBeanCallingConfig {
        @Autowired
        private ConfigurationWithFactoryBeanAndAutowiringTests.DummyBean dummyBean;

        @Bean
        public ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean factoryBean() {
            org.springframework.util.Assert.notNull(dummyBean, "DummyBean was not injected.");
            return new ConfigurationWithFactoryBeanAndAutowiringTests.MyFactoryBean();
        }

        @Bean
        public String myString() {
            return factoryBean().getString();
        }
    }
}

