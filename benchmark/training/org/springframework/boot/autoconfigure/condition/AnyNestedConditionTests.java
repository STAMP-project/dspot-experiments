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
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * Tests for {@link AnyNestedCondition}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
public class AnyNestedConditionTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void neither() {
        this.contextRunner.withUserConfiguration(AnyNestedConditionTests.Config.class).run(match(false));
    }

    @Test
    public void propertyA() {
        this.contextRunner.withUserConfiguration(AnyNestedConditionTests.Config.class).withPropertyValues("a:a").run(match(true));
    }

    @Test
    public void propertyB() {
        this.contextRunner.withUserConfiguration(AnyNestedConditionTests.Config.class).withPropertyValues("b:b").run(match(true));
    }

    @Test
    public void both() {
        this.contextRunner.withUserConfiguration(AnyNestedConditionTests.Config.class).withPropertyValues("a:a", "b:b").run(match(true));
    }

    @Configuration
    @Conditional(AnyNestedConditionTests.OnPropertyAorBCondition.class)
    public static class Config {
        @Bean
        public String myBean() {
            return "myBean";
        }
    }

    static class OnPropertyAorBCondition extends AnyNestedCondition {
        OnPropertyAorBCondition() {
            super(PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty("a")
        static class HasPropertyA {}

        @ConditionalOnExpression("true")
        @ConditionalOnProperty("b")
        static class HasPropertyB {}

        @Conditional(AnyNestedConditionTests.NonSpringBootCondition.class)
        static class SubclassC {}
    }

    static class NonSpringBootCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }
    }
}

