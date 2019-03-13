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
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;


/**
 * Tests for {@link ConditionalOnClass}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ConditionalOnClassTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void testVanillaOnClassCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassTests.BasicConfiguration.class, ConditionalOnClassTests.FooConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testMissingOnClassCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassTests.MissingConfiguration.class, ConditionalOnClassTests.FooConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean("bar");
            assertThat(context).hasBean("foo");
            assertThat(context.getBean("foo")).isEqualTo("foo");
        });
    }

    @Test
    public void testOnClassConditionWithXml() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassTests.BasicConfiguration.class, ConditionalOnClassTests.XmlConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testOnClassConditionWithCombinedXml() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassTests.CombinedXmlConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void onClassConditionOutputShouldNotContainConditionalOnMissingClassInMessage() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassTests.BasicConfiguration.class).run(( context) -> {
            Collection<ConditionEvaluationReport.ConditionAndOutcomes> conditionAndOutcomes = ConditionEvaluationReport.get(context.getSourceApplicationContext().getBeanFactory()).getConditionAndOutcomesBySource().values();
            String message = conditionAndOutcomes.iterator().next().iterator().next().getOutcome().getMessage();
            assertThat(message).doesNotContain("@ConditionalOnMissingClass did not find unwanted class");
        });
    }

    @Configuration
    @ConditionalOnClass(ConditionalOnClassTests.class)
    protected static class BasicConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnClass(name = "FOO")
    protected static class MissingConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    protected static class FooConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ImportResource("org/springframework/boot/autoconfigure/condition/foo.xml")
    protected static class XmlConfiguration {}

    @Configuration
    @Import(ConditionalOnClassTests.BasicConfiguration.class)
    @ImportResource("org/springframework/boot/autoconfigure/condition/foo.xml")
    protected static class CombinedXmlConfiguration {}
}

