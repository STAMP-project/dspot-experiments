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


import javax.annotation.PreDestroy;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Arrault Fabien
 */
public class AutoProxyLazyInitTests {
    @Test
    public void withStaticBeanMethod() {
        AutoProxyLazyInitTests.MyBeanImpl.initialized = false;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AutoProxyLazyInitTests.ConfigWithStatic.class);
        AutoProxyLazyInitTests.MyBean bean = ctx.getBean("myBean", AutoProxyLazyInitTests.MyBean.class);
        Assert.assertFalse(AutoProxyLazyInitTests.MyBeanImpl.initialized);
        bean.doIt();
        Assert.assertTrue(AutoProxyLazyInitTests.MyBeanImpl.initialized);
    }

    @Test
    public void withStaticBeanMethodAndInterface() {
        AutoProxyLazyInitTests.MyBeanImpl.initialized = false;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AutoProxyLazyInitTests.ConfigWithStaticAndInterface.class);
        AutoProxyLazyInitTests.MyBean bean = ctx.getBean("myBean", AutoProxyLazyInitTests.MyBean.class);
        Assert.assertFalse(AutoProxyLazyInitTests.MyBeanImpl.initialized);
        bean.doIt();
        Assert.assertTrue(AutoProxyLazyInitTests.MyBeanImpl.initialized);
    }

    @Test
    public void withNonStaticBeanMethod() {
        AutoProxyLazyInitTests.MyBeanImpl.initialized = false;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AutoProxyLazyInitTests.ConfigWithNonStatic.class);
        AutoProxyLazyInitTests.MyBean bean = ctx.getBean("myBean", AutoProxyLazyInitTests.MyBean.class);
        Assert.assertFalse(AutoProxyLazyInitTests.MyBeanImpl.initialized);
        bean.doIt();
        Assert.assertTrue(AutoProxyLazyInitTests.MyBeanImpl.initialized);
    }

    @Test
    public void withNonStaticBeanMethodAndInterface() {
        AutoProxyLazyInitTests.MyBeanImpl.initialized = false;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AutoProxyLazyInitTests.ConfigWithNonStaticAndInterface.class);
        AutoProxyLazyInitTests.MyBean bean = ctx.getBean("myBean", AutoProxyLazyInitTests.MyBean.class);
        Assert.assertFalse(AutoProxyLazyInitTests.MyBeanImpl.initialized);
        bean.doIt();
        Assert.assertTrue(AutoProxyLazyInitTests.MyBeanImpl.initialized);
    }

    public static interface MyBean {
        public String doIt();
    }

    public static class MyBeanImpl implements AutoProxyLazyInitTests.MyBean {
        public static boolean initialized = false;

        public MyBeanImpl() {
            AutoProxyLazyInitTests.MyBeanImpl.initialized = true;
        }

        @Override
        public String doIt() {
            return "From implementation";
        }

        @PreDestroy
        public void destroy() {
        }
    }

    @Configuration
    public static class ConfigWithStatic {
        @Bean
        public BeanNameAutoProxyCreator lazyInitAutoProxyCreator() {
            BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
            autoProxyCreator.setCustomTargetSourceCreators(lazyInitTargetSourceCreator());
            return autoProxyCreator;
        }

        @Bean
        public LazyInitTargetSourceCreator lazyInitTargetSourceCreator() {
            return new AutoProxyLazyInitTests.StrictLazyInitTargetSourceCreator();
        }

        @Bean
        @Lazy
        public static AutoProxyLazyInitTests.MyBean myBean() {
            return new AutoProxyLazyInitTests.MyBeanImpl();
        }
    }

    @Configuration
    public static class ConfigWithStaticAndInterface implements ApplicationListener<ApplicationContextEvent> {
        @Bean
        public BeanNameAutoProxyCreator lazyInitAutoProxyCreator() {
            BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
            autoProxyCreator.setCustomTargetSourceCreators(lazyInitTargetSourceCreator());
            return autoProxyCreator;
        }

        @Bean
        public LazyInitTargetSourceCreator lazyInitTargetSourceCreator() {
            return new AutoProxyLazyInitTests.StrictLazyInitTargetSourceCreator();
        }

        @Bean
        @Lazy
        public static AutoProxyLazyInitTests.MyBean myBean() {
            return new AutoProxyLazyInitTests.MyBeanImpl();
        }

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
        }
    }

    @Configuration
    public static class ConfigWithNonStatic {
        @Bean
        public BeanNameAutoProxyCreator lazyInitAutoProxyCreator() {
            BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
            autoProxyCreator.setCustomTargetSourceCreators(lazyInitTargetSourceCreator());
            return autoProxyCreator;
        }

        @Bean
        public LazyInitTargetSourceCreator lazyInitTargetSourceCreator() {
            return new AutoProxyLazyInitTests.StrictLazyInitTargetSourceCreator();
        }

        @Bean
        @Lazy
        public AutoProxyLazyInitTests.MyBean myBean() {
            return new AutoProxyLazyInitTests.MyBeanImpl();
        }
    }

    @Configuration
    public static class ConfigWithNonStaticAndInterface implements ApplicationListener<ApplicationContextEvent> {
        @Bean
        public BeanNameAutoProxyCreator lazyInitAutoProxyCreator() {
            BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
            autoProxyCreator.setCustomTargetSourceCreators(lazyInitTargetSourceCreator());
            return autoProxyCreator;
        }

        @Bean
        public LazyInitTargetSourceCreator lazyInitTargetSourceCreator() {
            return new AutoProxyLazyInitTests.StrictLazyInitTargetSourceCreator();
        }

        @Bean
        @Lazy
        public AutoProxyLazyInitTests.MyBean myBean() {
            return new AutoProxyLazyInitTests.MyBeanImpl();
        }

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
        }
    }

    private static class StrictLazyInitTargetSourceCreator extends LazyInitTargetSourceCreator {
        @Override
        protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
            if ("myBean".equals(beanName)) {
                Assert.assertEquals(AutoProxyLazyInitTests.MyBean.class, beanClass);
            }
            return super.createBeanFactoryBasedTargetSource(beanClass, beanName);
        }
    }
}

