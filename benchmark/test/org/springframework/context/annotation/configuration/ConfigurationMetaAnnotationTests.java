/**
 * Copyright 2002-2013 the original author or authors.
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


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Ensures that @Configuration is supported properly as a meta-annotation.
 *
 * @author Chris Beams
 */
public class ConfigurationMetaAnnotationTests {
    @Test
    public void customConfigurationStereotype() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationMetaAnnotationTests.Config.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("customName"), CoreMatchers.is(true));
        TestBean a = ctx.getBean("a", TestBean.class);
        TestBean b = ctx.getBean("b", TestBean.class);
        Assert.assertThat(b, CoreMatchers.sameInstance(a.getSpouse()));
    }

    @ConfigurationMetaAnnotationTests.TestConfiguration("customName")
    static class Config {
        @Bean
        public TestBean a() {
            TestBean a = new TestBean();
            a.setSpouse(b());
            return a;
        }

        @Bean
        public TestBean b() {
            return new TestBean();
        }
    }

    @Configuration
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestConfiguration {
        String value() default "";
    }
}

