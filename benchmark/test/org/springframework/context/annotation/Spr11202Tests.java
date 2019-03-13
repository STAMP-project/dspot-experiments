/**
 * Copyright 2002-2017 the original author or authors.
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


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;


/**
 *
 *
 * @author Dave Syer
 */
public class Spr11202Tests {
    @Test
    public void testWithImporter() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr11202Tests.Wrapper.class);
        Assert.assertEquals("foo", context.getBean("value"));
    }

    @Test
    public void testWithoutImporter() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr11202Tests.Config.class);
        Assert.assertEquals("foo", context.getBean("value"));
    }

    @Configuration
    @Import(Spr11202Tests.Selector.class)
    protected static class Wrapper {}

    protected static class Selector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{ Spr11202Tests.Config.class.getName() };
        }
    }

    @Configuration
    protected static class Config {
        @Bean
        public Spr11202Tests.FooFactoryBean foo() {
            return new Spr11202Tests.FooFactoryBean();
        }

        @Bean
        public String value() throws Exception {
            String name = foo().getObject().getName();
            org.springframework.util.Assert.state((name != null), "Name cannot be null");
            return name;
        }

        @Bean
        @Conditional(Spr11202Tests.NoBarCondition.class)
        public String bar() throws Exception {
            return "bar";
        }
    }

    protected static class NoBarCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if ((context.getBeanFactory().getBeanNamesForAnnotation(Spr11202Tests.Bar.class).length) > 0) {
                return false;
            }
            return true;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Target(ElementType.TYPE)
    protected @interface Bar {}

    protected static class FooFactoryBean implements FactoryBean<Spr11202Tests.Foo> , InitializingBean {
        private Spr11202Tests.Foo foo = new Spr11202Tests.Foo();

        @Override
        public Spr11202Tests.Foo getObject() throws Exception {
            return foo;
        }

        @Override
        public Class<?> getObjectType() {
            return Spr11202Tests.Foo.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            this.foo.name = "foo";
        }
    }

    protected static class Foo {
        private String name;

        public String getName() {
            return name;
        }
    }
}

