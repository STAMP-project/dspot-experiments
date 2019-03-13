/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.condition;


import ConfigurationPhase.PARSE_CONFIGURATION;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * Tests for {@link NoneNestedConditions}.
 */
public class NoneNestedConditionsTests {
    @Test
    public void neither() {
        AnnotationConfigApplicationContext context = load(NoneNestedConditionsTests.Config.class);
        assertThat(context.containsBean("myBean")).isTrue();
        context.close();
    }

    @Test
    public void propertyA() {
        AnnotationConfigApplicationContext context = load(NoneNestedConditionsTests.Config.class, "a:a");
        assertThat(context.containsBean("myBean")).isFalse();
        context.close();
    }

    @Test
    public void propertyB() {
        AnnotationConfigApplicationContext context = load(NoneNestedConditionsTests.Config.class, "b:b");
        assertThat(context.containsBean("myBean")).isFalse();
        context.close();
    }

    @Test
    public void both() {
        AnnotationConfigApplicationContext context = load(NoneNestedConditionsTests.Config.class, "a:a", "b:b");
        assertThat(context.containsBean("myBean")).isFalse();
        context.close();
    }

    @Configuration
    @Conditional(NoneNestedConditionsTests.NeitherPropertyANorPropertyBCondition.class)
    public static class Config {
        @Bean
        public String myBean() {
            return "myBean";
        }
    }

    static class NeitherPropertyANorPropertyBCondition extends NoneNestedConditions {
        NeitherPropertyANorPropertyBCondition() {
            super(PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty("a")
        static class HasPropertyA {}

        @ConditionalOnProperty("b")
        static class HasPropertyB {}

        @Conditional(NoneNestedConditionsTests.NonSpringBootCondition.class)
        static class SubClassC {}
    }

    static class NonSpringBootCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }
    }
}

