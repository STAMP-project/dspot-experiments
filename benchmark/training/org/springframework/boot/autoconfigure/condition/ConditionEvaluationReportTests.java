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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcomes;
import org.springframework.boot.autoconfigure.condition.config.first.SampleAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.assertj.Matched;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;


/**
 * Tests for {@link ConditionEvaluationReport}.
 *
 * @author Greg Turnquist
 * @author Phillip Webb
 */
public class ConditionEvaluationReportTests {
    private DefaultListableBeanFactory beanFactory;

    private ConditionEvaluationReport report;

    @Mock
    private Condition condition1;

    @Mock
    private Condition condition2;

    @Mock
    private Condition condition3;

    private ConditionOutcome outcome1;

    private ConditionOutcome outcome2;

    private ConditionOutcome outcome3;

    @Test
    public void get() {
        assertThat(this.report).isNotEqualTo(Matchers.nullValue());
        assertThat(this.report).isSameAs(ConditionEvaluationReport.get(this.beanFactory));
    }

    @Test
    public void parent() {
        this.beanFactory.setParentBeanFactory(new DefaultListableBeanFactory());
        ConditionEvaluationReport.get(((ConfigurableListableBeanFactory) (this.beanFactory.getParentBeanFactory())));
        assertThat(this.report).isSameAs(ConditionEvaluationReport.get(this.beanFactory));
        assertThat(this.report).isNotEqualTo(Matchers.nullValue());
        assertThat(this.report.getParent()).isNotEqualTo(Matchers.nullValue());
        ConditionEvaluationReport.get(((ConfigurableListableBeanFactory) (this.beanFactory.getParentBeanFactory())));
        assertThat(this.report).isSameAs(ConditionEvaluationReport.get(this.beanFactory));
        assertThat(this.report.getParent()).isSameAs(ConditionEvaluationReport.get(((ConfigurableListableBeanFactory) (this.beanFactory.getParentBeanFactory()))));
    }

    @Test
    public void parentBottomUp() {
        this.beanFactory = new DefaultListableBeanFactory();// NB: overrides setup

        this.beanFactory.setParentBeanFactory(new DefaultListableBeanFactory());
        ConditionEvaluationReport.get(((ConfigurableListableBeanFactory) (this.beanFactory.getParentBeanFactory())));
        this.report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(this.report).isNotNull();
        assertThat(this.report).isNotSameAs(this.report.getParent());
        assertThat(this.report.getParent()).isNotNull();
        assertThat(this.report.getParent().getParent()).isNull();
    }

    @Test
    public void recordConditionEvaluations() {
        this.outcome1 = new ConditionOutcome(false, "m1");
        this.outcome2 = new ConditionOutcome(false, "m2");
        this.outcome3 = new ConditionOutcome(false, "m3");
        this.report.recordConditionEvaluation("a", this.condition1, this.outcome1);
        this.report.recordConditionEvaluation("a", this.condition2, this.outcome2);
        this.report.recordConditionEvaluation("b", this.condition3, this.outcome3);
        Map<String, ConditionAndOutcomes> map = this.report.getConditionAndOutcomesBySource();
        assertThat(map.size()).isEqualTo(2);
        Iterator<ConditionAndOutcome> iterator = map.get("a").iterator();
        ConditionAndOutcome conditionAndOutcome = iterator.next();
        assertThat(conditionAndOutcome.getCondition()).isEqualTo(this.condition1);
        assertThat(conditionAndOutcome.getOutcome()).isEqualTo(this.outcome1);
        conditionAndOutcome = iterator.next();
        assertThat(conditionAndOutcome.getCondition()).isEqualTo(this.condition2);
        assertThat(conditionAndOutcome.getOutcome()).isEqualTo(this.outcome2);
        assertThat(iterator.hasNext()).isFalse();
        iterator = map.get("b").iterator();
        conditionAndOutcome = iterator.next();
        assertThat(conditionAndOutcome.getCondition()).isEqualTo(this.condition3);
        assertThat(conditionAndOutcome.getOutcome()).isEqualTo(this.outcome3);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void fullMatch() {
        prepareMatches(true, true, true);
        assertThat(this.report.getConditionAndOutcomesBySource().get("a").isFullMatch()).isTrue();
    }

    @Test
    public void notFullMatch() {
        prepareMatches(true, false, true);
        assertThat(this.report.getConditionAndOutcomesBySource().get("a").isFullMatch()).isFalse();
    }

    @Test
    @SuppressWarnings("resource")
    public void springBootConditionPopulatesReport() {
        ConditionEvaluationReport report = ConditionEvaluationReport.get(new AnnotationConfigApplicationContext(ConditionEvaluationReportTests.Config.class).getBeanFactory());
        assertThat(report.getConditionAndOutcomesBySource().size()).isNotEqualTo(0);
    }

    @Test
    public void testDuplicateConditionAndOutcomes() {
        ConditionAndOutcome outcome1 = new ConditionAndOutcome(this.condition1, new ConditionOutcome(true, "Message 1"));
        ConditionAndOutcome outcome2 = new ConditionAndOutcome(this.condition2, new ConditionOutcome(true, "Message 2"));
        ConditionAndOutcome outcome3 = new ConditionAndOutcome(this.condition3, new ConditionOutcome(true, "Message 2"));
        assertThat(outcome1).isEqualTo(outcome1);
        assertThat(outcome1).isNotEqualTo(outcome2);
        assertThat(outcome2).isEqualTo(outcome3);
        ConditionAndOutcomes outcomes = new ConditionAndOutcomes();
        outcomes.add(this.condition1, new ConditionOutcome(true, "Message 1"));
        outcomes.add(this.condition2, new ConditionOutcome(true, "Message 2"));
        outcomes.add(this.condition3, new ConditionOutcome(true, "Message 2"));
        assertThat(getNumberOfOutcomes(outcomes)).isEqualTo(2);
    }

    @Test
    public void duplicateOutcomes() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConditionEvaluationReportTests.DuplicateConfig.class);
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        String autoconfigKey = MultipartAutoConfiguration.class.getName();
        ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource().get(autoconfigKey);
        assertThat(outcomes).isNotEqualTo(Matchers.nullValue());
        assertThat(getNumberOfOutcomes(outcomes)).isEqualTo(2);
        List<String> messages = new ArrayList<>();
        for (ConditionAndOutcome outcome : outcomes) {
            messages.add(outcome.getOutcome().getMessage());
        }
        assertThat(messages).areAtLeastOne(Matched.by(Matchers.containsString(("@ConditionalOnClass found required classes " + (("'javax.servlet.Servlet', 'org.springframework.web.multipart." + "support.StandardServletMultipartResolver', ") + "'javax.servlet.MultipartConfigElement'")))));
        context.close();
    }

    @Test
    public void negativeOuterPositiveInnerBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("test.present=true").applyTo(context);
        context.register(ConditionEvaluationReportTests.NegativeOuterConfig.class);
        context.refresh();
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        Map<String, ConditionAndOutcomes> sourceOutcomes = report.getConditionAndOutcomesBySource();
        assertThat(context.containsBean("negativeOuterPositiveInnerBean")).isFalse();
        String negativeConfig = ConditionEvaluationReportTests.NegativeOuterConfig.class.getName();
        assertThat(sourceOutcomes.get(negativeConfig).isFullMatch()).isFalse();
        String positiveConfig = ConditionEvaluationReportTests.NegativeOuterConfig.PositiveInnerConfig.class.getName();
        assertThat(sourceOutcomes.get(positiveConfig).isFullMatch()).isFalse();
    }

    @Test
    public void reportWhenSameShortNamePresentMoreThanOnceShouldUseFullyQualifiedName() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebMvcAutoConfiguration.class, SampleAutoConfiguration.class, org.springframework.boot.autoconfigure.condition.config.second.SampleAutoConfiguration.class);
        context.refresh();
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        assertThat(report.getConditionAndOutcomesBySource()).containsKeys("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration", "org.springframework.boot.autoconfigure.condition.config.first.SampleAutoConfiguration", "org.springframework.boot.autoconfigure.condition.config.second.SampleAutoConfiguration");
        context.close();
    }

    @Test
    public void reportMessageWhenSameShortNamePresentMoreThanOnceShouldUseFullyQualifiedName() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebMvcAutoConfiguration.class, SampleAutoConfiguration.class, org.springframework.boot.autoconfigure.condition.config.second.SampleAutoConfiguration.class);
        context.refresh();
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        String reportMessage = new org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportMessage(report).toString();
        assertThat(reportMessage).contains("WebMvcAutoConfiguration", "org.springframework.boot.autoconfigure.condition.config.first.SampleAutoConfiguration", "org.springframework.boot.autoconfigure.condition.config.second.SampleAutoConfiguration");
        assertThat(reportMessage).doesNotContain("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration");
        context.close();
    }

    @Configuration
    @Import(WebMvcAutoConfiguration.class)
    static class Config {}

    @Configuration
    @Import(MultipartAutoConfiguration.class)
    static class DuplicateConfig {}

    @Configuration
    @Conditional({ ConditionEvaluationReportTests.MatchParseCondition.class, ConditionEvaluationReportTests.NoMatchBeanCondition.class })
    public static class NegativeOuterConfig {
        @Configuration
        @Conditional({ ConditionEvaluationReportTests.MatchParseCondition.class })
        public static class PositiveInnerConfig {
            @Bean
            public String negativeOuterPositiveInnerBean() {
                return "negativeOuterPositiveInnerBean";
            }
        }
    }

    static class TestMatchCondition extends SpringBootCondition implements ConfigurationCondition {
        private final ConfigurationPhase phase;

        private final boolean match;

        TestMatchCondition(ConfigurationPhase phase, boolean match) {
            this.phase = phase;
            this.match = match;
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return this.phase;
        }

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return new ConditionOutcome(this.match, ClassUtils.getShortName(getClass()));
        }
    }

    static class MatchParseCondition extends ConditionEvaluationReportTests.TestMatchCondition {
        MatchParseCondition() {
            super(PARSE_CONFIGURATION, true);
        }
    }

    static class MatchBeanCondition extends ConditionEvaluationReportTests.TestMatchCondition {
        MatchBeanCondition() {
            super(REGISTER_BEAN, true);
        }
    }

    static class NoMatchParseCondition extends ConditionEvaluationReportTests.TestMatchCondition {
        NoMatchParseCondition() {
            super(PARSE_CONFIGURATION, false);
        }
    }

    static class NoMatchBeanCondition extends ConditionEvaluationReportTests.TestMatchCondition {
        NoMatchBeanCondition() {
            super(REGISTER_BEAN, false);
        }
    }
}

