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


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link ConditionalOnExpression}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ConditionalOnExpressionTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void expressionIsTrue() {
        this.contextRunner.withUserConfiguration(ConditionalOnExpressionTests.BasicConfiguration.class).run(( context) -> assertThat(context.getBean("foo")).isEqualTo("foo"));
    }

    @Test
    public void expressionEvaluatesToTrueRegistersBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnExpressionTests.MissingConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Test
    public void expressionEvaluatesToFalseDoesNotRegisterBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnExpressionTests.NullConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Test
    public void expressionEvaluationWithNoBeanFactoryDoesNotMatch() {
        OnExpressionCondition condition = new OnExpressionCondition();
        MockEnvironment environment = new MockEnvironment();
        ConditionContext conditionContext = Mockito.mock(ConditionContext.class);
        BDDMockito.given(conditionContext.getEnvironment()).willReturn(environment);
        ConditionOutcome outcome = condition.getMatchOutcome(conditionContext, mockMetaData("invalid-spel"));
        assertThat(outcome.isMatch()).isFalse();
        assertThat(outcome.getMessage()).contains("invalid-spel").contains("no BeanFactory available");
    }

    @Configuration
    @ConditionalOnExpression("false")
    protected static class MissingConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnExpression("true")
    protected static class BasicConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnExpression("true ? null : false")
    protected static class NullConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }
}

