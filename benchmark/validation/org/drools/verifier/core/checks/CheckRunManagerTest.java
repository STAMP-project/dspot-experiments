/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks;


import CheckType.REDUNDANT_ROWS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.RuleInspectorCache;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.Check;
import org.drools.verifier.core.checks.base.CheckRunManager;
import org.drools.verifier.core.checks.base.CheckStorage;
import org.drools.verifier.core.checks.base.JavaCheckRunner;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CheckRunManagerTest {
    @Spy
    private CheckRunManager checkRunManager = new CheckRunManager(new JavaCheckRunner());

    @Mock
    private RuleInspectorCache cache;

    private RuleInspector ruleInspector1;

    private RuleInspector ruleInspector2;

    private RuleInspector ruleInspector3;

    private ArrayList<RuleInspector> ruleInspectors;

    private CheckStorage checkStorage;

    private AnalyzerConfiguration configuration;

    @Test
    public void testChecksGetGenerated() throws Exception {
        Assert.assertEquals(5, ruleInspector1.getChecks().size());
        Assert.assertEquals(5, ruleInspector2.getChecks().size());
        Assert.assertEquals(5, ruleInspector3.getChecks().size());
    }

    @Test
    public void testRemove() throws Exception {
        this.checkRunManager.remove(ruleInspector2);
        final Set<Check> checks = ruleInspector1.getChecks();
        Assert.assertEquals(3, checks.size());
        Assert.assertTrue(ruleInspector2.getChecks().isEmpty());
        Assert.assertEquals(3, ruleInspector3.getChecks().size());
    }

    @Test
    public void testRunTests() throws Exception {
        for (RuleInspector ruleInspector : cache.all()) {
            assertNoIssues(ruleInspector);
        }
        this.checkRunManager.run(null, null);
        for (RuleInspector ruleInspector : cache.all()) {
            assertHasIssues(ruleInspector);
        }
    }

    @Test
    public void testOnlyTestChanges() throws Exception {
        // First run
        this.checkRunManager.run(null, null);
        RuleInspector newRuleInspector = mockRowInspector(3);
        ruleInspectors.add(newRuleInspector);
        this.checkRunManager.addChecks(newRuleInspector.getChecks());
        assertNoIssues(newRuleInspector);
        // Second run
        this.checkRunManager.run(null, null);
        assertHasIssues(newRuleInspector);
        Assert.assertEquals(7, ruleInspector1.getChecks().size());
        Assert.assertEquals(7, newRuleInspector.getChecks().size());
    }

    private class MockSingleCheck extends SingleCheck {
        public MockSingleCheck(RuleInspector ruleInspector) {
            super(ruleInspector, CheckRunManagerTest.this.configuration, REDUNDANT_ROWS);
        }

        @Override
        public boolean check() {
            return hasIssues = true;
        }

        @Override
        protected Severity getDefaultSeverity() {
            return Severity.NOTE;
        }

        @Override
        protected Issue makeIssue(final Severity severity, final CheckType checkType) {
            return new Issue(severity, checkType, Collections.emptySet());
        }
    }
}

