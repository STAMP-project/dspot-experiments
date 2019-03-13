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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class Spr15275Tests {
    @Test
    public void testWithFactoryBean() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithFactoryBean.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        Assert.assertSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Test
    public void testWithAbstractFactoryBean() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithAbstractFactoryBean.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        Assert.assertSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Test
    public void testWithAbstractFactoryBeanForInterface() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithAbstractFactoryBeanForInterface.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        Assert.assertSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Test
    public void testWithAbstractFactoryBeanAsReturnType() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithAbstractFactoryBeanAsReturnType.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        Assert.assertSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Test
    public void testWithFinalFactoryBean() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithFinalFactoryBean.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        Assert.assertSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Test
    public void testWithFinalFactoryBeanAsReturnType() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Spr15275Tests.ConfigWithFinalFactoryBeanAsReturnType.class);
        Assert.assertEquals("x", context.getBean(Spr15275Tests.Bar.class).foo.toString());
        // not same due to fallback to raw FinalFactoryBean instance with repeated getObject() invocations
        Assert.assertNotSame(context.getBean(Spr15275Tests.FooInterface.class), context.getBean(Spr15275Tests.Bar.class).foo);
    }

    @Configuration
    protected static class ConfigWithFactoryBean {
        @Bean
        public FactoryBean<Spr15275Tests.Foo> foo() {
            return new FactoryBean<Spr15275Tests.Foo>() {
                @Override
                public Spr15275Tests.Foo getObject() {
                    return new Spr15275Tests.Foo("x");
                }

                @Override
                public Class<?> getObjectType() {
                    return Spr15275Tests.Foo.class;
                }
            };
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(foo().isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    @Configuration
    protected static class ConfigWithAbstractFactoryBean {
        @Bean
        public FactoryBean<Spr15275Tests.Foo> foo() {
            return new org.springframework.beans.factory.config.AbstractFactoryBean<Spr15275Tests.Foo>() {
                @Override
                public Spr15275Tests.Foo createInstance() {
                    return new Spr15275Tests.Foo("x");
                }

                @Override
                public Class<?> getObjectType() {
                    return Spr15275Tests.Foo.class;
                }
            };
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(foo().isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    @Configuration
    protected static class ConfigWithAbstractFactoryBeanForInterface {
        @Bean
        public FactoryBean<Spr15275Tests.FooInterface> foo() {
            return new org.springframework.beans.factory.config.AbstractFactoryBean<Spr15275Tests.FooInterface>() {
                @Override
                public Spr15275Tests.FooInterface createInstance() {
                    return new Spr15275Tests.Foo("x");
                }

                @Override
                public Class<?> getObjectType() {
                    return Spr15275Tests.FooInterface.class;
                }
            };
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(foo().isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    @Configuration
    protected static class ConfigWithAbstractFactoryBeanAsReturnType {
        @Bean
        public org.springframework.beans.factory.config.AbstractFactoryBean<Spr15275Tests.FooInterface> foo() {
            return new org.springframework.beans.factory.config.AbstractFactoryBean<Spr15275Tests.FooInterface>() {
                @Override
                public Spr15275Tests.FooInterface createInstance() {
                    return new Spr15275Tests.Foo("x");
                }

                @Override
                public Class<?> getObjectType() {
                    return Spr15275Tests.Foo.class;
                }
            };
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(foo().isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    @Configuration
    protected static class ConfigWithFinalFactoryBean {
        @Bean
        public FactoryBean<Spr15275Tests.FooInterface> foo() {
            return new Spr15275Tests.FinalFactoryBean();
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(foo().isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    @Configuration
    protected static class ConfigWithFinalFactoryBeanAsReturnType {
        @Bean
        public Spr15275Tests.FinalFactoryBean foo() {
            return new Spr15275Tests.FinalFactoryBean();
        }

        @Bean
        public Spr15275Tests.Bar bar() throws Exception {
            Assert.assertTrue(isSingleton());
            return new Spr15275Tests.Bar(foo().getObject());
        }
    }

    private static final class FinalFactoryBean implements FactoryBean<Spr15275Tests.FooInterface> {
        @Override
        public Spr15275Tests.Foo getObject() {
            return new Spr15275Tests.Foo("x");
        }

        @Override
        public Class<?> getObjectType() {
            return Spr15275Tests.FooInterface.class;
        }
    }

    protected interface FooInterface {}

    protected static class Foo implements Spr15275Tests.FooInterface {
        private final String value;

        public Foo(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    protected static class Bar {
        public final Spr15275Tests.FooInterface foo;

        public Bar(Spr15275Tests.FooInterface foo) {
            this.foo = foo;
        }
    }
}

