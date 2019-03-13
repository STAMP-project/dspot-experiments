/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.context.annotation.configuration;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * System tests for {@link Import} annotation support.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class ImportTests {
    @Test
    public void testProcessImportsWithAsm() {
        int configClasses = 2;
        int beansInClasses = 2;
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ImportTests.ConfigurationWithImportAnnotation.class.getName()));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        Assert.assertThat(beanFactory.getBeanDefinitionCount(), CoreMatchers.equalTo((configClasses + beansInClasses)));
    }

    @Test
    public void testProcessImportsWithDoubleImports() {
        int configClasses = 3;
        int beansInClasses = 3;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.ConfigurationWithImportAnnotation.class, ImportTests.OtherConfigurationWithImportAnnotation.class);
    }

    @Test
    public void testProcessImportsWithExplicitOverridingBefore() {
        int configClasses = 2;
        int beansInClasses = 2;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.OtherConfiguration.class, ImportTests.ConfigurationWithImportAnnotation.class);
    }

    @Test
    public void testProcessImportsWithExplicitOverridingAfter() {
        int configClasses = 2;
        int beansInClasses = 2;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.ConfigurationWithImportAnnotation.class, ImportTests.OtherConfiguration.class);
    }

    @Configuration
    @Import(ImportTests.OtherConfiguration.class)
    static class ConfigurationWithImportAnnotation {
        @Bean
        public ITestBean one() {
            return new TestBean();
        }
    }

    @Configuration
    @Import(ImportTests.OtherConfiguration.class)
    static class OtherConfigurationWithImportAnnotation {
        @Bean
        public ITestBean two() {
            return new TestBean();
        }
    }

    @Configuration
    static class OtherConfiguration {
        @Bean
        public ITestBean three() {
            return new TestBean();
        }
    }

    // ------------------------------------------------------------------------
    @Test
    public void testImportAnnotationWithTwoLevelRecursion() {
        int configClasses = 2;
        int beansInClasses = 3;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.AppConfig.class);
    }

    @Configuration
    @Import(ImportTests.DataSourceConfig.class)
    static class AppConfig {
        @Bean
        public ITestBean transferService() {
            return new TestBean(accountRepository());
        }

        @Bean
        public ITestBean accountRepository() {
            return new TestBean();
        }
    }

    @Configuration
    static class DataSourceConfig {
        @Bean
        public ITestBean dataSourceA() {
            return new TestBean();
        }
    }

    // ------------------------------------------------------------------------
    @Test
    public void testImportAnnotationWithThreeLevelRecursion() {
        int configClasses = 4;
        int beansInClasses = 5;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.FirstLevel.class);
    }

    // ------------------------------------------------------------------------
    @Test
    public void testImportAnnotationWithMultipleArguments() {
        int configClasses = 3;
        int beansInClasses = 3;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.WithMultipleArgumentsToImportAnnotation.class);
    }

    @Test
    public void testImportAnnotationWithMultipleArgumentsResultingInOverriddenBeanDefinition() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ImportTests.WithMultipleArgumentsThatWillCauseDuplication.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        Assert.assertThat(beanFactory.getBeanDefinitionCount(), CoreMatchers.equalTo(4));
        Assert.assertThat(beanFactory.getBean("foo", ITestBean.class).getName(), CoreMatchers.equalTo("foo2"));
    }

    @Configuration
    @Import({ ImportTests.Foo1.class, ImportTests.Foo2.class })
    static class WithMultipleArgumentsThatWillCauseDuplication {}

    @Configuration
    static class Foo1 {
        @Bean
        public ITestBean foo() {
            return new TestBean("foo1");
        }
    }

    @Configuration
    static class Foo2 {
        @Bean
        public ITestBean foo() {
            return new TestBean("foo2");
        }
    }

    // ------------------------------------------------------------------------
    @Test
    public void testImportAnnotationOnInnerClasses() {
        int configClasses = 2;
        int beansInClasses = 2;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.OuterConfig.InnerConfig.class);
    }

    @Configuration
    static class OuterConfig {
        @Bean
        String whatev() {
            return "whatev";
        }

        @Configuration
        @Import(ImportTests.ExternalConfig.class)
        static class InnerConfig {
            @Bean
            public ITestBean innerBean() {
                return new TestBean();
            }
        }
    }

    @Configuration
    static class ExternalConfig {
        @Bean
        public ITestBean extBean() {
            return new TestBean();
        }
    }

    // ------------------------------------------------------------------------
    @Configuration
    @Import(ImportTests.SecondLevel.class)
    static class FirstLevel {
        @Bean
        public TestBean m() {
            return new TestBean();
        }
    }

    @Configuration
    @Import({ ImportTests.ThirdLevel.class, ImportTests.InitBean.class })
    static class SecondLevel {
        @Bean
        public TestBean n() {
            return new TestBean();
        }
    }

    @Configuration
    @DependsOn("org.springframework.context.annotation.configuration.ImportTests$InitBean")
    static class ThirdLevel {
        public ThirdLevel() {
            Assert.assertTrue(ImportTests.InitBean.initialized);
        }

        @Bean
        public ITestBean thirdLevelA() {
            return new TestBean();
        }

        @Bean
        public ITestBean thirdLevelB() {
            return new TestBean();
        }

        @Bean
        public ITestBean thirdLevelC() {
            return new TestBean();
        }
    }

    static class InitBean {
        public static boolean initialized = false;

        public InitBean() {
            ImportTests.InitBean.initialized = true;
        }
    }

    @Configuration
    @Import({ ImportTests.LeftConfig.class, ImportTests.RightConfig.class })
    static class WithMultipleArgumentsToImportAnnotation {
        @Bean
        public TestBean m() {
            return new TestBean();
        }
    }

    @Configuration
    static class LeftConfig {
        @Bean
        public ITestBean left() {
            return new TestBean();
        }
    }

    @Configuration
    static class RightConfig {
        @Bean
        public ITestBean right() {
            return new TestBean();
        }
    }

    // ------------------------------------------------------------------------
    @Test
    public void testImportNonConfigurationAnnotationClass() {
        int configClasses = 2;
        int beansInClasses = 0;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.ConfigAnnotated.class);
    }

    @Configuration
    @Import(ImportTests.NonConfigAnnotated.class)
    static class ConfigAnnotated {}

    static class NonConfigAnnotated {}

    // ------------------------------------------------------------------------
    /**
     * Test that values supplied to @Configuration(value="...") are propagated as the
     * bean name for the configuration class even in the case of inclusion via @Import
     * or in the case of automatic registration via nesting
     */
    @Test
    public void reproSpr9023() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportTests.B.class);
        ctx.refresh();
        System.out.println(ctx.getBeanFactory());
        Assert.assertThat(ctx.getBeanNamesForType(ImportTests.B.class)[0], CoreMatchers.is("config-b"));
        Assert.assertThat(ctx.getBeanNamesForType(ImportTests.A.class)[0], CoreMatchers.is("config-a"));
    }

    @Configuration("config-a")
    static class A {}

    @Configuration("config-b")
    @Import(ImportTests.A.class)
    static class B {}

    @Test
    public void testProcessImports() {
        int configClasses = 2;
        int beansInClasses = 2;
        assertBeanDefinitionCount((configClasses + beansInClasses), ImportTests.ConfigurationWithImportAnnotation.class);
    }
}

