/**
 * Copyright 2012-2014 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static ConfigurationPhase.REGISTER_BEAN;


/**
 *
 *
 * @author Andy Wilkinson
 */
public class ImportWithConditionTests {
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void conditionalThenUnconditional() throws Exception {
        this.context.register(ImportWithConditionTests.ConditionalThenUnconditional.class);
        this.context.refresh();
        Assert.assertFalse(this.context.containsBean("beanTwo"));
        Assert.assertTrue(this.context.containsBean("beanOne"));
    }

    @Test
    public void unconditionalThenConditional() throws Exception {
        this.context.register(ImportWithConditionTests.UnconditionalThenConditional.class);
        this.context.refresh();
        Assert.assertFalse(this.context.containsBean("beanTwo"));
        Assert.assertTrue(this.context.containsBean("beanOne"));
    }

    @Configuration
    @Import({ ImportWithConditionTests.ConditionalConfiguration.class, ImportWithConditionTests.UnconditionalConfiguration.class })
    protected static class ConditionalThenUnconditional {
        @Autowired
        private ImportWithConditionTests.BeanOne beanOne;
    }

    @Configuration
    @Import({ ImportWithConditionTests.UnconditionalConfiguration.class, ImportWithConditionTests.ConditionalConfiguration.class })
    protected static class UnconditionalThenConditional {
        @Autowired
        private ImportWithConditionTests.BeanOne beanOne;
    }

    @Configuration
    @Import(ImportWithConditionTests.BeanProvidingConfiguration.class)
    protected static class UnconditionalConfiguration {}

    @Configuration
    @Conditional(ImportWithConditionTests.NeverMatchingCondition.class)
    @Import(ImportWithConditionTests.BeanProvidingConfiguration.class)
    protected static class ConditionalConfiguration {}

    @Configuration
    protected static class BeanProvidingConfiguration {
        @Bean
        ImportWithConditionTests.BeanOne beanOne() {
            return new ImportWithConditionTests.BeanOne();
        }
    }

    private static final class BeanOne {}

    private static final class NeverMatchingCondition implements ConfigurationCondition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return REGISTER_BEAN;
        }
    }
}

