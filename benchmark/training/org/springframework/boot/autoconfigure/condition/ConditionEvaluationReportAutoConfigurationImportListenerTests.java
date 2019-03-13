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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationImportEvent;
import org.springframework.boot.autoconfigure.AutoConfigurationImportListener;
import org.springframework.core.io.support.SpringFactoriesLoader;


/**
 * Tests for {@link ConditionEvaluationReportAutoConfigurationImportListener}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ConditionEvaluationReportAutoConfigurationImportListenerTests {
    private ConditionEvaluationReportAutoConfigurationImportListener listener;

    private final ConfigurableListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void shouldBeInSpringFactories() {
        List<AutoConfigurationImportListener> factories = SpringFactoriesLoader.loadFactories(AutoConfigurationImportListener.class, null);
        assertThat(factories).hasAtLeastOneElementOfType(ConditionEvaluationReportAutoConfigurationImportListener.class);
    }

    @Test
    public void onAutoConfigurationImportEventShouldRecordCandidates() {
        List<String> candidateConfigurations = Collections.singletonList("Test");
        Set<String> exclusions = Collections.emptySet();
        AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, candidateConfigurations, exclusions);
        this.listener.onAutoConfigurationImportEvent(event);
        ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(report.getUnconditionalClasses()).containsExactlyElementsOf(candidateConfigurations);
    }

    @Test
    public void onAutoConfigurationImportEventShouldRecordExclusions() {
        List<String> candidateConfigurations = Collections.emptyList();
        Set<String> exclusions = Collections.singleton("Test");
        AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, candidateConfigurations, exclusions);
        this.listener.onAutoConfigurationImportEvent(event);
        ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(report.getExclusions()).containsExactlyElementsOf(exclusions);
    }

    @Test
    public void onAutoConfigurationImportEventShouldApplyExclusionsGlobally() {
        AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, Arrays.asList("First", "Second"), Collections.emptySet());
        this.listener.onAutoConfigurationImportEvent(event);
        AutoConfigurationImportEvent anotherEvent = new AutoConfigurationImportEvent(this, Collections.emptyList(), Collections.singleton("First"));
        this.listener.onAutoConfigurationImportEvent(anotherEvent);
        ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(report.getUnconditionalClasses()).containsExactly("Second");
        assertThat(report.getExclusions()).containsExactly("First");
    }

    @Test
    public void onAutoConfigurationImportEventShouldApplyExclusionsGloballyWhenExclusionIsAlreadyApplied() {
        AutoConfigurationImportEvent excludeEvent = new AutoConfigurationImportEvent(this, Collections.emptyList(), Collections.singleton("First"));
        this.listener.onAutoConfigurationImportEvent(excludeEvent);
        AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, Arrays.asList("First", "Second"), Collections.emptySet());
        this.listener.onAutoConfigurationImportEvent(event);
        ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(report.getUnconditionalClasses()).containsExactly("Second");
        assertThat(report.getExclusions()).containsExactly("First");
    }
}

