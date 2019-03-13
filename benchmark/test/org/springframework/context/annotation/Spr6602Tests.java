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


import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Tests to verify that FactoryBean semantics are the same in Configuration
 * classes as in XML.
 *
 * @author Chris Beams
 */
public class Spr6602Tests {
    @Test
    public void testXmlBehavior() throws Exception {
        doAssertions(new ClassPathXmlApplicationContext("Spr6602Tests-context.xml", Spr6602Tests.class));
    }

    @Test
    public void testConfigurationClassBehavior() throws Exception {
        doAssertions(new AnnotationConfigApplicationContext(Spr6602Tests.FooConfig.class));
    }

    @Configuration
    public static class FooConfig {
        @Bean
        public Spr6602Tests.Foo foo() throws Exception {
            return new Spr6602Tests.Foo(barFactory().getObject());
        }

        @Bean
        public Spr6602Tests.BarFactory barFactory() {
            return new Spr6602Tests.BarFactory();
        }
    }

    public static class Foo {
        final Spr6602Tests.Bar bar;

        public Foo(Spr6602Tests.Bar bar) {
            this.bar = bar;
        }
    }

    public static class Bar {}

    public static class BarFactory implements FactoryBean<Spr6602Tests.Bar> {
        @Override
        public Spr6602Tests.Bar getObject() throws Exception {
            return new Spr6602Tests.Bar();
        }

        @Override
        public Class<? extends Spr6602Tests.Bar> getObjectType() {
            return Spr6602Tests.Bar.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}

