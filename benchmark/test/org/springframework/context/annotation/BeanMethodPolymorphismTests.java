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


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.RootBeanDefinition;


/**
 * Tests regarding overloading and overriding of bean methods.
 * Related to SPR-6618.
 *
 * @author Chris Beams
 * @author Phillip Webb
 * @author Juergen Hoeller
 */
@SuppressWarnings("resource")
public class BeanMethodPolymorphismTests {
    @Test
    public void beanMethodDetectedOnSuperClass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanMethodPolymorphismTests.Config.class);
        ctx.getBean("testBean", TestBean.class);
    }

    @Test
    public void beanMethodOverriding() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.OverridingConfig.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
        Assert.assertEquals("overridden", ctx.getBean("testBean", TestBean.class).toString());
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
    }

    @Test
    public void beanMethodOverridingOnASM() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBeanDefinition("config", new RootBeanDefinition(BeanMethodPolymorphismTests.OverridingConfig.class.getName()));
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
        Assert.assertEquals("overridden", ctx.getBean("testBean", TestBean.class).toString());
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
    }

    @Test
    public void beanMethodOverridingWithNarrowedReturnType() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.NarrowedOverridingConfig.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
        Assert.assertEquals("overridden", ctx.getBean("testBean", TestBean.class).toString());
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
    }

    @Test
    public void beanMethodOverridingWithNarrowedReturnTypeOnASM() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBeanDefinition("config", new RootBeanDefinition(BeanMethodPolymorphismTests.NarrowedOverridingConfig.class.getName()));
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
        Assert.assertEquals("overridden", ctx.getBean("testBean", TestBean.class).toString());
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("testBean"));
    }

    @Test
    public void beanMethodOverloadingWithoutInheritance() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.ConfigWithOverloading.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("regular"));
    }

    @Test
    public void beanMethodOverloadingWithoutInheritanceAndExtraDependency() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.ConfigWithOverloading.class);
        ctx.getDefaultListableBeanFactory().registerSingleton("anInt", 5);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("overloaded5"));
    }

    @Test
    public void beanMethodOverloadingWithAdditionalMetadata() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.ConfigWithOverloadingAndAdditionalMetadata.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("regular"));
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
    }

    @Test
    public void beanMethodOverloadingWithAdditionalMetadataButOtherMethodExecuted() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.ConfigWithOverloadingAndAdditionalMetadata.class);
        ctx.getDefaultListableBeanFactory().registerSingleton("anInt", 5);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("overloaded5"));
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
    }

    @Test
    public void beanMethodOverloadingWithInheritance() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.SubConfig.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("overloaded5"));
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
    }

    // SPR-11025
    @Test
    public void beanMethodOverloadingWithInheritanceAndList() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.SubConfigWithList.class);
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.refresh();
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("overloaded5"));
        Assert.assertTrue(ctx.getDefaultListableBeanFactory().containsSingleton("aString"));
    }

    /**
     * When inheritance is not involved, it is still possible to override a bean method from
     * the container's point of view. This is not strictly 'overloading' of a method per se,
     * so it's referred to here as 'shadowing' to distinguish the difference.
     */
    @Test
    public void beanMethodShadowing() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanMethodPolymorphismTests.ShadowConfig.class);
        Assert.assertThat(ctx.getBean(String.class), CoreMatchers.equalTo("shadow"));
    }

    @Test
    public void beanMethodThroughAopProxy() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BeanMethodPolymorphismTests.Config.class);
        ctx.register(AnnotationAwareAspectJAutoProxyCreator.class);
        ctx.register(BeanMethodPolymorphismTests.TestAdvisor.class);
        ctx.refresh();
        ctx.getBean("testBean", TestBean.class);
    }

    @Configuration
    static class BaseConfig {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    @Configuration
    static class Config extends BeanMethodPolymorphismTests.BaseConfig {}

    @Configuration
    static class OverridingConfig extends BeanMethodPolymorphismTests.BaseConfig {
        @Bean
        @Lazy
        @Override
        public TestBean testBean() {
            return new TestBean() {
                @Override
                public String toString() {
                    return "overridden";
                }
            };
        }
    }

    static class ExtendedTestBean extends TestBean {}

    @Configuration
    static class NarrowedOverridingConfig extends BeanMethodPolymorphismTests.BaseConfig {
        @Bean
        @Lazy
        @Override
        public BeanMethodPolymorphismTests.ExtendedTestBean testBean() {
            return new BeanMethodPolymorphismTests.ExtendedTestBean() {
                @Override
                public String toString() {
                    return "overridden";
                }
            };
        }
    }

    @Configuration
    static class ConfigWithOverloading {
        @Bean
        String aString() {
            return "regular";
        }

        @Bean
        String aString(Integer dependency) {
            return "overloaded" + dependency;
        }
    }

    @Configuration
    static class ConfigWithOverloadingAndAdditionalMetadata {
        @Bean
        @Lazy
        String aString() {
            return "regular";
        }

        @Bean
        @Lazy
        String aString(Integer dependency) {
            return "overloaded" + dependency;
        }
    }

    @Configuration
    static class SuperConfig {
        @Bean
        String aString() {
            return "super";
        }
    }

    @Configuration
    static class SubConfig extends BeanMethodPolymorphismTests.SuperConfig {
        @Bean
        Integer anInt() {
            return 5;
        }

        @Bean
        @Lazy
        String aString(Integer dependency) {
            return "overloaded" + dependency;
        }
    }

    @Configuration
    static class SubConfigWithList extends BeanMethodPolymorphismTests.SuperConfig {
        @Bean
        Integer anInt() {
            return 5;
        }

        @Bean
        @Lazy
        String aString(List<Integer> dependency) {
            return "overloaded" + (dependency.get(0));
        }
    }

    @Configuration
    @Import(BeanMethodPolymorphismTests.SubConfig.class)
    static class ShadowConfig {
        @Bean
        String aString() {
            return "shadow";
        }
    }

    @SuppressWarnings("serial")
    public static class TestAdvisor extends DefaultPointcutAdvisor {
        public TestAdvisor() {
            super(new SimpleTraceInterceptor());
        }
    }
}

