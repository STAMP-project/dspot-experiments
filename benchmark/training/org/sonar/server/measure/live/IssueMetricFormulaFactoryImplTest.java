/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.measure.live;


import CoreMetrics.BLOCKER_VIOLATIONS;
import CoreMetrics.BUGS;
import CoreMetrics.CODE_SMELLS;
import CoreMetrics.CONFIRMED_ISSUES;
import CoreMetrics.CRITICAL_VIOLATIONS;
import CoreMetrics.DEVELOPMENT_COST;
import CoreMetrics.EFFORT_TO_REACH_MAINTAINABILITY_RATING_A;
import CoreMetrics.FALSE_POSITIVE_ISSUES;
import CoreMetrics.INFO_VIOLATIONS;
import CoreMetrics.MAJOR_VIOLATIONS;
import CoreMetrics.MINOR_VIOLATIONS;
import CoreMetrics.NEW_BLOCKER_VIOLATIONS;
import CoreMetrics.NEW_BUGS;
import CoreMetrics.NEW_CODE_SMELLS;
import CoreMetrics.NEW_CRITICAL_VIOLATIONS;
import CoreMetrics.NEW_DEVELOPMENT_COST;
import CoreMetrics.NEW_INFO_VIOLATIONS;
import CoreMetrics.NEW_MAINTAINABILITY_RATING;
import CoreMetrics.NEW_MAJOR_VIOLATIONS;
import CoreMetrics.NEW_MINOR_VIOLATIONS;
import CoreMetrics.NEW_RELIABILITY_RATING;
import CoreMetrics.NEW_RELIABILITY_REMEDIATION_EFFORT;
import CoreMetrics.NEW_SECURITY_RATING;
import CoreMetrics.NEW_SECURITY_REMEDIATION_EFFORT;
import CoreMetrics.NEW_SQALE_DEBT_RATIO;
import CoreMetrics.NEW_TECHNICAL_DEBT;
import CoreMetrics.NEW_VIOLATIONS;
import CoreMetrics.NEW_VULNERABILITIES;
import CoreMetrics.OPEN_ISSUES;
import CoreMetrics.RELIABILITY_RATING;
import CoreMetrics.RELIABILITY_REMEDIATION_EFFORT;
import CoreMetrics.REOPENED_ISSUES;
import CoreMetrics.SECURITY_RATING;
import CoreMetrics.SECURITY_REMEDIATION_EFFORT;
import CoreMetrics.SQALE_DEBT_RATIO;
import CoreMetrics.SQALE_RATING;
import CoreMetrics.TECHNICAL_DEBT;
import CoreMetrics.VIOLATIONS;
import CoreMetrics.VULNERABILITIES;
import CoreMetrics.WONT_FIX_ISSUES;
import Issue.RESOLUTION_FALSE_POSITIVE;
import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_REMOVED;
import Issue.RESOLUTION_WONT_FIX;
import Issue.STATUS_CLOSED;
import Issue.STATUS_CONFIRMED;
import Issue.STATUS_OPEN;
import Issue.STATUS_REOPENED;
import Issue.STATUS_RESOLVED;
import Rating.A;
import Rating.B;
import Rating.C;
import Rating.D;
import Rating.E;
import RuleType.BUG;
import RuleType.CODE_SMELL;
import RuleType.SECURITY_HOTSPOT;
import RuleType.VULNERABILITY;
import Severity.BLOCKER;
import Severity.CRITICAL;
import Severity.INFO;
import Severity.MAJOR;
import Severity.MINOR;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.measures.Metric;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.issue.IssueGroupDto;
import org.sonar.server.measure.DebtRatingGrid;
import org.sonar.server.measure.Rating;


public class IssueMetricFormulaFactoryImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private IssueMetricFormulaFactoryImpl underTest = new IssueMetricFormulaFactoryImpl();

    @Test
    public void getFormulaMetrics_include_the_dependent_metrics() {
        for (IssueMetricFormula formula : underTest.getFormulas()) {
            assertThat(underTest.getFormulaMetrics()).contains(formula.getMetric());
            for (Metric dependentMetric : formula.getDependentMetrics()) {
                assertThat(underTest.getFormulaMetrics()).contains(dependentMetric);
            }
        }
    }

    @Test
    public void test_violations() {
        withNoIssues().assertThatValueIs(VIOLATIONS, 0);
        with(IssueMetricFormulaFactoryImplTest.newGroup(), IssueMetricFormulaFactoryImplTest.newGroup().setCount(4)).assertThatValueIs(VIOLATIONS, 5);
        // exclude resolved
        IssueGroupDto resolved = IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_FIXED, STATUS_RESOLVED);
        with(IssueMetricFormulaFactoryImplTest.newGroup(), IssueMetricFormulaFactoryImplTest.newGroup(), resolved).assertThatValueIs(VIOLATIONS, 2);
        // include issues on leak
        IssueGroupDto onLeak = IssueMetricFormulaFactoryImplTest.newGroup().setCount(11).setInLeak(true);
        with(IssueMetricFormulaFactoryImplTest.newGroup(), IssueMetricFormulaFactoryImplTest.newGroup(), onLeak).assertThatValueIs(VIOLATIONS, ((1 + 1) + 11));
    }

    @Test
    public void test_bugs() {
        withNoIssues().assertThatValueIs(BUGS, 0);
        // exclude resolved
        // not bugs
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newResolvedGroup(BUG).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setCount(11)).assertThatValueIs(BUGS, (3 + 5));
    }

    @Test
    public void test_code_smells() {
        withNoIssues().assertThatValueIs(CODE_SMELLS, 0);
        // exclude resolved
        // not code smells
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newResolvedGroup(CODE_SMELL).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setCount(11)).assertThatValueIs(CODE_SMELLS, (3 + 5));
    }

    @Test
    public void test_vulnerabilities() {
        withNoIssues().assertThatValueIs(VULNERABILITIES, 0);
        // exclude resolved
        // not vulnerabilities
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newResolvedGroup(VULNERABILITY).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setCount(11)).assertThatValueIs(VULNERABILITIES, (3 + 5));
    }

    @Test
    public void count_unresolved_by_severity() {
        withNoIssues().assertThatValueIs(BLOCKER_VIOLATIONS, 0).assertThatValueIs(CRITICAL_VIOLATIONS, 0).assertThatValueIs(MAJOR_VIOLATIONS, 0).assertThatValueIs(MINOR_VIOLATIONS, 0).assertThatValueIs(INFO_VIOLATIONS, 0);
        // exclude security hotspot
        // include leak
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MAJOR).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(SECURITY_HOTSPOT).setSeverity(CRITICAL).setCount(15), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setInLeak(true).setCount(13), IssueMetricFormulaFactoryImplTest.newResolvedGroup(VULNERABILITY).setSeverity(INFO).setCount(17), IssueMetricFormulaFactoryImplTest.newResolvedGroup(BUG).setSeverity(MAJOR).setCount(19)).assertThatValueIs(BLOCKER_VIOLATIONS, (11 + 13)).assertThatValueIs(CRITICAL_VIOLATIONS, 7).assertThatValueIs(MAJOR_VIOLATIONS, (3 + 5)).assertThatValueIs(MINOR_VIOLATIONS, 0).assertThatValueIs(INFO_VIOLATIONS, 0);
    }

    @Test
    public void count_resolved() {
        withNoIssues().assertThatValueIs(FALSE_POSITIVE_ISSUES, 0).assertThatValueIs(WONT_FIX_ISSUES, 0);
        // exclude security hotspot
        // exclude unresolved
        with(IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_FIXED, STATUS_RESOLVED).setCount(3), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_FALSE_POSITIVE, STATUS_CLOSED).setCount(5), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_WONT_FIX, STATUS_CLOSED).setSeverity(MAJOR).setCount(7), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_WONT_FIX, STATUS_CLOSED).setSeverity(BLOCKER).setCount(11), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_REMOVED, STATUS_CLOSED).setCount(13), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_WONT_FIX, STATUS_RESOLVED).setCount(15).setRuleType(SECURITY_HOTSPOT.getDbConstant()), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setCount(17), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setCount(19)).assertThatValueIs(FALSE_POSITIVE_ISSUES, 5).assertThatValueIs(WONT_FIX_ISSUES, (7 + 11));
    }

    @Test
    public void count_by_status() {
        withNoIssues().assertThatValueIs(CONFIRMED_ISSUES, 0).assertThatValueIs(OPEN_ISSUES, 0).assertThatValueIs(REOPENED_ISSUES, 0);
        // exclude security hotspot
        with(IssueMetricFormulaFactoryImplTest.newGroup().setStatus(STATUS_CONFIRMED).setSeverity(BLOCKER).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup().setStatus(STATUS_CONFIRMED).setSeverity(INFO).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup().setStatus(STATUS_REOPENED).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setStatus(STATUS_OPEN).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setStatus(STATUS_OPEN).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(SECURITY_HOTSPOT).setStatus(STATUS_OPEN).setCount(12), IssueMetricFormulaFactoryImplTest.newResolvedGroup(RESOLUTION_FALSE_POSITIVE, STATUS_CLOSED).setCount(13)).assertThatValueIs(CONFIRMED_ISSUES, (3 + 5)).assertThatValueIs(OPEN_ISSUES, (9 + 11)).assertThatValueIs(REOPENED_ISSUES, 7);
    }

    @Test
    public void test_technical_debt() {
        withNoIssues().assertThatValueIs(TECHNICAL_DEBT, 0);
        // exclude security hotspot
        // not code smells
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(3.0).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(5.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(SECURITY_HOTSPOT).setEffort(9).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(SECURITY_HOTSPOT).setEffort(11).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(7.0), IssueMetricFormulaFactoryImplTest.newResolvedGroup(CODE_SMELL).setEffort(17.0)).assertThatValueIs(TECHNICAL_DEBT, (3.0 + 5.0));
    }

    @Test
    public void test_reliability_remediation_effort() {
        withNoIssues().assertThatValueIs(RELIABILITY_REMEDIATION_EFFORT, 0);
        // not bugs
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(3.0), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(5.0).setSeverity(BLOCKER), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(7.0), IssueMetricFormulaFactoryImplTest.newResolvedGroup(BUG).setEffort(17.0)).assertThatValueIs(RELIABILITY_REMEDIATION_EFFORT, (3.0 + 5.0));
    }

    @Test
    public void test_security_remediation_effort() {
        withNoIssues().assertThatValueIs(SECURITY_REMEDIATION_EFFORT, 0);
        // not vulnerability
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setEffort(3.0), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setEffort(5.0).setSeverity(BLOCKER), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(7.0), IssueMetricFormulaFactoryImplTest.newResolvedGroup(VULNERABILITY).setEffort(17.0)).assertThatValueIs(SECURITY_REMEDIATION_EFFORT, (3.0 + 5.0));
    }

    @Test
    public void test_sqale_debt_ratio_and_sqale_rating() {
        withNoIssues().assertThatValueIs(SQALE_DEBT_RATIO, 0).assertThatValueIs(SQALE_RATING, A);
        // technical_debt not computed
        with(DEVELOPMENT_COST, 0).assertThatValueIs(SQALE_DEBT_RATIO, 0).assertThatValueIs(SQALE_RATING, A);
        with(DEVELOPMENT_COST, 20).assertThatValueIs(SQALE_DEBT_RATIO, 0).assertThatValueIs(SQALE_RATING, A);
        // development_cost not computed
        with(TECHNICAL_DEBT, 0).assertThatValueIs(SQALE_DEBT_RATIO, 0).assertThatValueIs(SQALE_RATING, A);
        with(TECHNICAL_DEBT, 20).assertThatValueIs(SQALE_DEBT_RATIO, 0).assertThatValueIs(SQALE_RATING, A);
        // input measures are available
        with(TECHNICAL_DEBT, 20.0).and(DEVELOPMENT_COST, 0.0).assertThatValueIs(SQALE_DEBT_RATIO, 0.0).assertThatValueIs(SQALE_RATING, A);
        with(TECHNICAL_DEBT, 20.0).and(DEVELOPMENT_COST, 160.0).assertThatValueIs(SQALE_DEBT_RATIO, 12.5).assertThatValueIs(SQALE_RATING, C);
        with(TECHNICAL_DEBT, 20.0).and(DEVELOPMENT_COST, 10.0).assertThatValueIs(SQALE_DEBT_RATIO, 200.0).assertThatValueIs(SQALE_RATING, E);
        // A is 5% --> min debt is exactly 200*0.05=10
        with(DEVELOPMENT_COST, 200.0).and(TECHNICAL_DEBT, 10.0).assertThatValueIs(SQALE_DEBT_RATIO, 5.0).assertThatValueIs(SQALE_RATING, A);
        with(TECHNICAL_DEBT, 0.0).and(DEVELOPMENT_COST, 0.0).assertThatValueIs(SQALE_DEBT_RATIO, 0.0).assertThatValueIs(SQALE_RATING, A);
        with(TECHNICAL_DEBT, 0.0).and(DEVELOPMENT_COST, 80.0).assertThatValueIs(SQALE_DEBT_RATIO, 0.0);
        with(TECHNICAL_DEBT, (-20.0)).and(DEVELOPMENT_COST, 0.0).assertThatValueIs(SQALE_DEBT_RATIO, 0.0).assertThatValueIs(SQALE_RATING, A);
        // bug, debt can't be negative
        with(TECHNICAL_DEBT, (-20.0)).and(DEVELOPMENT_COST, 80.0).assertThatValueIs(SQALE_DEBT_RATIO, 0.0).assertThatValueIs(SQALE_RATING, A);
        // bug, cost can't be negative
        with(TECHNICAL_DEBT, 20.0).and(DEVELOPMENT_COST, (-80.0)).assertThatValueIs(SQALE_DEBT_RATIO, 0.0).assertThatValueIs(SQALE_RATING, A);
    }

    @Test
    public void test_effort_to_reach_maintainability_rating_A() {
        withNoIssues().assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
        // technical_debt not computed
        with(DEVELOPMENT_COST, 0.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
        with(DEVELOPMENT_COST, 20.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
        // development_cost not computed
        with(TECHNICAL_DEBT, 0.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
        // development cost is considered as zero, so the effort is to reach... zero
        with(TECHNICAL_DEBT, 20.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 20.0);
        // B to A
        // B is 5% --> goal is to reach 200*0.05=10 --> effort is 40-10=30
        with(DEVELOPMENT_COST, 200.0).and(TECHNICAL_DEBT, 40.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, (40.0 - (200.0 * 0.05)));
        // E to A
        // B is 5% --> goal is to reach 200*0.05=10 --> effort is 180-10=170
        with(DEVELOPMENT_COST, 200.0).and(TECHNICAL_DEBT, 180.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, (180.0 - (200.0 * 0.05)));
        // already A
        // B is 5% --> goal is to reach 200*0.05=10 --> debt is already at 8 --> effort to reach A is zero
        with(DEVELOPMENT_COST, 200.0).and(TECHNICAL_DEBT, 8.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
        // exactly lower range of B
        // B is 5% --> goal is to reach 200*0.05=10 --> debt is 10 --> effort to reach A is zero
        // FIXME need zero to reach A but effective rating is B !
        with(DEVELOPMENT_COST, 200.0).and(TECHNICAL_DEBT, 10.0).assertThatValueIs(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, 0.0);
    }

    @Test
    public void test_reliability_rating() {
        withNoIssues().assertThatValueIs(RELIABILITY_RATING, A);
        // highest severity of bugs is CRITICAL --> D
        // excluded, not a bug
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setCount(1), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MINOR).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setCount(3)).assertThatValueIs(RELIABILITY_RATING, D);
        // no bugs --> A
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(CRITICAL).setCount(5)).assertThatValueIs(RELIABILITY_RATING, A);
    }

    @Test
    public void test_security_rating() {
        withNoIssues().assertThatValueIs(SECURITY_RATING, A);
        // highest severity of vulnerabilities is CRITICAL --> D
        // excluded, not a vulnerability
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(CRITICAL).setCount(1), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MINOR).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setCount(3)).assertThatValueIs(SECURITY_RATING, D);
        // no vulnerabilities --> A
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setCount(5)).assertThatValueIs(SECURITY_RATING, A);
    }

    @Test
    public void test_new_bugs() {
        withNoIssues().assertThatLeakValueIs(NEW_BUGS, 0.0);
        // not bugs
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(false).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(true).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(true).setSeverity(MINOR).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(true).setCount(11)).assertThatLeakValueIs(NEW_BUGS, (5 + 7));
    }

    @Test
    public void test_new_code_smells() {
        withNoIssues().assertThatLeakValueIs(NEW_CODE_SMELLS, 0.0);
        // not code smells
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(false).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(true).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(true).setSeverity(MINOR).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(true).setCount(11)).assertThatLeakValueIs(NEW_CODE_SMELLS, (5 + 7));
    }

    @Test
    public void test_new_vulnerabilities() {
        withNoIssues().assertThatLeakValueIs(NEW_VULNERABILITIES, 0.0);
        // not vulnerabilities
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(false).setSeverity(MAJOR).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(true).setSeverity(CRITICAL).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(true).setSeverity(MINOR).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(true).setCount(11)).assertThatLeakValueIs(NEW_VULNERABILITIES, (5 + 7));
    }

    @Test
    public void test_new_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_VIOLATIONS, 0.0);
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setInLeak(false).setCount(13), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setInLeak(false).setCount(17)).assertThatLeakValueIs(NEW_VIOLATIONS, ((5 + 7) + 9));
    }

    @Test
    public void test_new_blocker_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_BLOCKER_VIOLATIONS, 0.0);
        // not blocker
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setInLeak(true).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(BLOCKER).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(BLOCKER).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(BLOCKER).setInLeak(false).setCount(13)).assertThatLeakValueIs(NEW_BLOCKER_VIOLATIONS, ((3 + 5) + 7));
    }

    @Test
    public void test_new_critical_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_CRITICAL_VIOLATIONS, 0.0);
        // not CRITICAL
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(true).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(CRITICAL).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(CRITICAL).setInLeak(false).setCount(13)).assertThatLeakValueIs(NEW_CRITICAL_VIOLATIONS, ((3 + 5) + 7));
    }

    @Test
    public void test_new_major_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_MAJOR_VIOLATIONS, 0.0);
        // not MAJOR
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setInLeak(true).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MAJOR).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MAJOR).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MAJOR).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MAJOR).setInLeak(false).setCount(13)).assertThatLeakValueIs(NEW_MAJOR_VIOLATIONS, ((3 + 5) + 7));
    }

    @Test
    public void test_new_minor_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_MINOR_VIOLATIONS, 0.0);
        // not MINOR
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MINOR).setInLeak(true).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MINOR).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MINOR).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(MINOR).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MINOR).setInLeak(false).setCount(13)).assertThatLeakValueIs(NEW_MINOR_VIOLATIONS, ((3 + 5) + 7));
    }

    @Test
    public void test_new_info_violations() {
        withNoIssues().assertThatLeakValueIs(NEW_INFO_VIOLATIONS, 0.0);
        // not INFO
        // not in leak
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(INFO).setInLeak(true).setCount(3), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(INFO).setInLeak(true).setCount(5), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(INFO).setInLeak(true).setCount(7), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(CRITICAL).setInLeak(true).setCount(9), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(INFO).setInLeak(false).setCount(11), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(INFO).setInLeak(false).setCount(13)).assertThatLeakValueIs(NEW_INFO_VIOLATIONS, ((3 + 5) + 7));
    }

    @Test
    public void test_new_technical_debt() {
        withNoIssues().assertThatLeakValueIs(NEW_TECHNICAL_DEBT, 0.0);
        // not in leak
        // not code smells
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(3.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(5.0).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(SECURITY_HOTSPOT).setEffort(9.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(7.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newResolvedGroup(CODE_SMELL).setEffort(17.0).setInLeak(true)).assertThatLeakValueIs(NEW_TECHNICAL_DEBT, 3.0);
    }

    @Test
    public void test_new_reliability_remediation_effort() {
        withNoIssues().assertThatLeakValueIs(NEW_RELIABILITY_REMEDIATION_EFFORT, 0.0);
        // not in leak
        // not bugs
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(3.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setEffort(5.0).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(7.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newResolvedGroup(BUG).setEffort(17.0).setInLeak(true)).assertThatLeakValueIs(NEW_RELIABILITY_REMEDIATION_EFFORT, 3.0);
    }

    @Test
    public void test_new_security_remediation_effort() {
        withNoIssues().assertThatLeakValueIs(NEW_SECURITY_REMEDIATION_EFFORT, 0.0);
        // not in leak
        // not vulnerability
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setEffort(3.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setEffort(5.0).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setEffort(7.0).setInLeak(true), IssueMetricFormulaFactoryImplTest.newResolvedGroup(VULNERABILITY).setEffort(17.0).setInLeak(true)).assertThatLeakValueIs(NEW_SECURITY_REMEDIATION_EFFORT, 3.0);
    }

    @Test
    public void test_new_reliability_rating() {
        withNoIssues().assertThatLeakValueIs(NEW_RELIABILITY_RATING, A);
        // highest severity of bugs on leak period is minor -> B
        // not in leak
        // not bug
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(INFO).setCount(3).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(MINOR).setCount(1).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(BUG).setSeverity(BLOCKER).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setInLeak(true), IssueMetricFormulaFactoryImplTest.newResolvedGroup(BUG).setSeverity(BLOCKER).setInLeak(true)).assertThatLeakValueIs(NEW_RELIABILITY_RATING, B);
    }

    @Test
    public void test_new_security_rating() {
        withNoIssues().assertThatLeakValueIs(NEW_SECURITY_RATING, A);
        // highest severity of bugs on leak period is minor -> B
        // not in leak
        // not vulnerability
        // exclude resolved
        with(IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(INFO).setCount(3).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(MINOR).setCount(1).setInLeak(true), IssueMetricFormulaFactoryImplTest.newGroup(VULNERABILITY).setSeverity(BLOCKER).setInLeak(false), IssueMetricFormulaFactoryImplTest.newGroup(CODE_SMELL).setSeverity(BLOCKER).setInLeak(true), IssueMetricFormulaFactoryImplTest.newResolvedGroup(VULNERABILITY).setSeverity(BLOCKER).setInLeak(true)).assertThatLeakValueIs(NEW_SECURITY_RATING, B);
    }

    @Test
    public void test_new_sqale_debt_ratio_and_new_maintainability_rating() {
        withNoIssues().assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        // technical_debt not computed
        withLeak(NEW_DEVELOPMENT_COST, 0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        withLeak(NEW_DEVELOPMENT_COST, 20).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        // development_cost not computed
        withLeak(NEW_TECHNICAL_DEBT, 0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        withLeak(NEW_TECHNICAL_DEBT, 20).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        // input measures are available
        withLeak(NEW_TECHNICAL_DEBT, 20.0).andLeak(NEW_DEVELOPMENT_COST, 0.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        withLeak(NEW_TECHNICAL_DEBT, 20.0).andLeak(NEW_DEVELOPMENT_COST, 160.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 12.5).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, C);
        withLeak(NEW_TECHNICAL_DEBT, 20.0).andLeak(NEW_DEVELOPMENT_COST, 10.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 200.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, E);
        // A is 5% --> min debt is exactly 200*0.05=10
        withLeak(NEW_DEVELOPMENT_COST, 200.0).andLeak(NEW_TECHNICAL_DEBT, 10.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 5.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        withLeak(NEW_TECHNICAL_DEBT, 0.0).andLeak(NEW_DEVELOPMENT_COST, 0.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        withLeak(NEW_TECHNICAL_DEBT, 0.0).andLeak(NEW_DEVELOPMENT_COST, 80.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0);
        withLeak(NEW_TECHNICAL_DEBT, (-20.0)).andLeak(NEW_DEVELOPMENT_COST, 0.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        // bug, debt can't be negative
        withLeak(NEW_TECHNICAL_DEBT, (-20.0)).andLeak(NEW_DEVELOPMENT_COST, 80.0).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
        // bug, cost can't be negative
        withLeak(NEW_TECHNICAL_DEBT, 20.0).andLeak(NEW_DEVELOPMENT_COST, (-80.0)).assertThatLeakValueIs(NEW_SQALE_DEBT_RATIO, 0.0).assertThatLeakValueIs(NEW_MAINTAINABILITY_RATING, A);
    }

    private class Verifier {
        private final IssueGroupDto[] groups;

        private final Map<Metric, Double> values = new HashMap<>();

        private final Map<Metric, Double> leakValues = new HashMap<>();

        private Verifier(IssueGroupDto[] groups) {
            this.groups = groups;
        }

        IssueMetricFormulaFactoryImplTest.Verifier and(Metric metric, double value) {
            this.values.put(metric, value);
            return this;
        }

        IssueMetricFormulaFactoryImplTest.Verifier andLeak(Metric metric, double value) {
            this.leakValues.put(metric, value);
            return this;
        }

        IssueMetricFormulaFactoryImplTest.Verifier assertThatValueIs(Metric metric, double expectedValue) {
            IssueMetricFormulaFactoryImplTest.TestContext context = run(metric, false);
            assertThat(context.doubleValue).isNotNull().isEqualTo(expectedValue);
            return this;
        }

        IssueMetricFormulaFactoryImplTest.Verifier assertThatLeakValueIs(Metric metric, double expectedValue) {
            IssueMetricFormulaFactoryImplTest.TestContext context = run(metric, true);
            assertThat(context.doubleLeakValue).isNotNull().isEqualTo(expectedValue);
            return this;
        }

        IssueMetricFormulaFactoryImplTest.Verifier assertThatLeakValueIs(Metric metric, Rating expectedRating) {
            IssueMetricFormulaFactoryImplTest.TestContext context = run(metric, true);
            assertThat(context.ratingLeakValue).isNotNull().isEqualTo(expectedRating);
            return this;
        }

        IssueMetricFormulaFactoryImplTest.Verifier assertThatValueIs(Metric metric, Rating expectedValue) {
            IssueMetricFormulaFactoryImplTest.TestContext context = run(metric, false);
            assertThat(context.ratingValue).isNotNull().isEqualTo(expectedValue);
            return this;
        }

        private IssueMetricFormulaFactoryImplTest.TestContext run(Metric metric, boolean expectLeakFormula) {
            IssueMetricFormula formula = underTest.getFormulas().stream().filter(( f) -> f.getMetric().getKey().equals(metric.getKey())).findFirst().get();
            assertThat(formula.isOnLeak()).isEqualTo(expectLeakFormula);
            IssueMetricFormulaFactoryImplTest.TestContext context = new IssueMetricFormulaFactoryImplTest.TestContext(formula.getDependentMetrics(), values, leakValues);
            formula.compute(context, IssueMetricFormulaFactoryImplTest.newIssueCounter(groups));
            return context;
        }
    }

    private static class TestContext implements IssueMetricFormula.Context {
        private final Set<Metric> dependentMetrics;

        private Double doubleValue;

        private Rating ratingValue;

        private Double doubleLeakValue;

        private Rating ratingLeakValue;

        private final Map<Metric, Double> values;

        private final Map<Metric, Double> leakValues;

        private TestContext(Collection<Metric> dependentMetrics, Map<Metric, Double> values, Map<Metric, Double> leakValues) {
            this.dependentMetrics = new java.util.HashSet(dependentMetrics);
            this.values = values;
            this.leakValues = leakValues;
        }

        @Override
        public ComponentDto getComponent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DebtRatingGrid getDebtRatingGrid() {
            return new DebtRatingGrid(new double[]{ 0.05, 0.1, 0.2, 0.5 });
        }

        @Override
        public Optional<Double> getValue(Metric metric) {
            if (!(dependentMetrics.contains(metric))) {
                throw new IllegalStateException((("Metric " + (metric.getKey())) + " is not declared as a dependency"));
            }
            if (values.containsKey(metric)) {
                return Optional.of(values.get(metric));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Double> getLeakValue(Metric metric) {
            if (!(dependentMetrics.contains(metric))) {
                throw new IllegalStateException((("Metric " + (metric.getKey())) + " is not declared as a dependency"));
            }
            if (leakValues.containsKey(metric)) {
                return Optional.of(leakValues.get(metric));
            }
            return Optional.empty();
        }

        @Override
        public void setValue(double value) {
            this.doubleValue = value;
        }

        @Override
        public void setValue(Rating value) {
            this.ratingValue = value;
        }

        @Override
        public void setLeakValue(double value) {
            this.doubleLeakValue = value;
        }

        @Override
        public void setLeakValue(Rating value) {
            this.ratingLeakValue = value;
        }
    }
}

