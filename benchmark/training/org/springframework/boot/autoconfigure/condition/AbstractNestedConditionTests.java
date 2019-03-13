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
import ConfigurationPhase.REGISTER_BEAN;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link AbstractNestedCondition}.
 *
 * @author Razib Shahriar
 */
public class AbstractNestedConditionTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void validPhase() {
        this.contextRunner.withUserConfiguration(AbstractNestedConditionTests.ValidConfig.class).run(( context) -> assertThat(context).hasBean("myBean"));
    }

    @Test
    public void invalidMemberPhase() {
        this.contextRunner.withUserConfiguration(AbstractNestedConditionTests.InvalidConfig.class).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure().getCause()).isInstanceOf(.class).hasMessageContaining(((("Nested condition " + (.class.getName())) + " uses a configuration phase that is inappropriate for class ") + (.class.getName())));
        });
    }

    @Test
    public void invalidNestedMemberPhase() {
        this.contextRunner.withUserConfiguration(AbstractNestedConditionTests.DoubleNestedConfig.class).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure().getCause()).isInstanceOf(.class).hasMessageContaining(((("Nested condition " + (.class.getName())) + " uses a configuration phase that is inappropriate for class ") + (.class.getName())));
        });
    }

    @Configuration
    @Conditional(AbstractNestedConditionTests.ValidNestedCondition.class)
    public static class ValidConfig {
        @Bean
        public String myBean() {
            return "myBean";
        }
    }

    static class ValidNestedCondition extends AbstractNestedCondition {
        ValidNestedCondition() {
            super(REGISTER_BEAN);
        }

        @Override
        protected ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes) {
            return ConditionOutcome.match();
        }

        @ConditionalOnMissingBean(name = "myBean")
        static class MissingMyBean {}
    }

    @Configuration
    @Conditional(AbstractNestedConditionTests.InvalidNestedCondition.class)
    public static class InvalidConfig {
        @Bean
        public String myBean() {
            return "myBean";
        }
    }

    static class InvalidNestedCondition extends AbstractNestedCondition {
        InvalidNestedCondition() {
            super(PARSE_CONFIGURATION);
        }

        @Override
        protected ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes) {
            return ConditionOutcome.match();
        }

        @ConditionalOnMissingBean(name = "myBean")
        static class MissingMyBean {}
    }

    @Configuration
    @Conditional(AbstractNestedConditionTests.DoubleNestedCondition.class)
    public static class DoubleNestedConfig {}

    static class DoubleNestedCondition extends AbstractNestedCondition {
        DoubleNestedCondition() {
            super(PARSE_CONFIGURATION);
        }

        @Override
        protected ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes) {
            return ConditionOutcome.match();
        }

        @Conditional(AbstractNestedConditionTests.ValidNestedCondition.class)
        static class NestedConditionThatIsValid {}
    }
}

