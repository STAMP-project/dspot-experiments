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
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.core.io.support.SpringFactoriesLoader;


/**
 * Tests for the {@link AutoConfigurationImportFilter} part of {@link OnClassCondition}.
 *
 * @author Phillip Webb
 */
public class OnClassConditionAutoConfigurationImportFilterTests {
    private OnClassCondition filter = new OnClassCondition();

    private DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void shouldBeRegistered() {
        assertThat(SpringFactoriesLoader.loadFactories(AutoConfigurationImportFilter.class, null)).hasAtLeastOneElementOfType(OnClassCondition.class);
    }

    @Test
    public void matchShouldMatchClasses() {
        String[] autoConfigurationClasses = new String[]{ "test.match", "test.nomatch" };
        boolean[] result = this.filter.match(autoConfigurationClasses, getAutoConfigurationMetadata());
        assertThat(result).containsExactly(true, false);
    }

    @Test
    public void matchShouldRecordOutcome() {
        String[] autoConfigurationClasses = new String[]{ "test.match", "test.nomatch" };
        this.filter.match(autoConfigurationClasses, getAutoConfigurationMetadata());
        ConditionEvaluationReport report = ConditionEvaluationReport.get(this.beanFactory);
        assertThat(report.getConditionAndOutcomesBySource()).hasSize(1).containsKey("test.nomatch");
    }
}

