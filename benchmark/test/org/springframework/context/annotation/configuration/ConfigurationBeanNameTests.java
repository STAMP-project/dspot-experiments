/**
 * Copyright 2002-2012 the original author or authors.
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;


/**
 * Unit tests ensuring that configuration class bean names as expressed via @Configuration
 * or @Component 'value' attributes are indeed respected, and that customization of bean
 * naming through a BeanNameGenerator strategy works as expected.
 *
 * @author Chris Beams
 * @since 3.1.1
 */
public class ConfigurationBeanNameTests {
    @Test
    public void registerOuterConfig() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationBeanNameTests.A.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("outer"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("imported"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("nested"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("nestedBean"), CoreMatchers.is(true));
    }

    @Test
    public void registerNestedConfig() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationBeanNameTests.A.B.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("outer"), CoreMatchers.is(false));
        Assert.assertThat(ctx.containsBean("imported"), CoreMatchers.is(false));
        Assert.assertThat(ctx.containsBean("nested"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("nestedBean"), CoreMatchers.is(true));
    }

    @Test
    public void registerOuterConfig_withBeanNameGenerator() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.setBeanNameGenerator(new AnnotationBeanNameGenerator() {
            @Override
            public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
                return "custom-" + (super.generateBeanName(definition, registry));
            }
        });
        ctx.register(ConfigurationBeanNameTests.A.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("custom-outer"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("custom-imported"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("custom-nested"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("nestedBean"), CoreMatchers.is(true));
    }

    @Configuration("outer")
    @Import(ConfigurationBeanNameTests.C.class)
    static class A {
        @Component("nested")
        static class B {
            @Bean
            public String nestedBean() {
                return "";
            }
        }
    }

    @Configuration("imported")
    static class C {
        @Bean
        public String s() {
            return "s";
        }
    }
}

