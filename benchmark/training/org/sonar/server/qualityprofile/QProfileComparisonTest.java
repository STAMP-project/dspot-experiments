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
package org.sonar.server.qualityprofile;


import Severity.BLOCKER;
import Severity.CRITICAL;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import java.util.Collections;
import org.assertj.core.data.MapEntry;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.qualityprofile.QProfileComparison.ActiveRuleDiff;
import org.sonar.server.qualityprofile.QProfileComparison.QProfileComparisonResult;
import org.sonar.server.tester.UserSessionRule;


public class QProfileComparisonTest {
    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone().anonymous();

    @Rule
    public DbTester dbTester = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    private DbClient db;

    private DbSession dbSession;

    private QProfileRules qProfileRules;

    private QProfileComparison comparison;

    private RuleDefinitionDto xooRule1;

    private RuleDefinitionDto xooRule2;

    private QProfileDto left;

    private QProfileDto right;

    @Test
    public void compare_empty_profiles() {
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isEmpty();
        assertThat(result.collectRuleKeys()).isEmpty();
    }

    @Test
    public void compare_same() {
        RuleActivation commonActivation = RuleActivation.create(xooRule1.getId(), CRITICAL, ImmutableMap.of("min", "7", "max", "42"));
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(commonActivation));
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(commonActivation));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isEmpty();
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
    }

    @Test
    public void compare_only_left() {
        RuleActivation activation = RuleActivation.create(xooRule1.getId());
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(activation));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isEmpty();
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
    }

    @Test
    public void compare_only_right() {
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(RuleActivation.create(xooRule1.getId())));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.modified()).isEmpty();
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
    }

    @Test
    public void compare_disjoint() {
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(RuleActivation.create(xooRule1.getId())));
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(RuleActivation.create(xooRule2.getId())));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.inRight()).isNotEmpty().containsOnlyKeys(xooRule2.getKey());
        assertThat(result.modified()).isEmpty();
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey(), xooRule2.getKey());
    }

    @Test
    public void compare_modified_severity() {
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(RuleActivation.create(xooRule1.getId(), CRITICAL, null)));
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(RuleActivation.create(xooRule1.getId(), BLOCKER, null)));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
        ActiveRuleDiff activeRuleDiff = result.modified().get(xooRule1.getKey());
        assertThat(activeRuleDiff.leftSeverity()).isEqualTo(CRITICAL);
        assertThat(activeRuleDiff.rightSeverity()).isEqualTo(BLOCKER);
        assertThat(activeRuleDiff.paramDifference().areEqual()).isTrue();
    }

    @Test
    public void compare_modified_param() {
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(RuleActivation.create(xooRule1.getId(), null, ImmutableMap.of("max", "20"))));
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(RuleActivation.create(xooRule1.getId(), null, ImmutableMap.of("max", "30"))));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
        ActiveRuleDiff activeRuleDiff = result.modified().get(xooRule1.getKey());
        assertThat(activeRuleDiff.leftSeverity()).isEqualTo(activeRuleDiff.rightSeverity()).isEqualTo(xooRule1.getSeverityString());
        assertThat(activeRuleDiff.paramDifference().areEqual()).isFalse();
        assertThat(activeRuleDiff.paramDifference().entriesDiffering()).isNotEmpty();
        MapDifference.ValueDifference<String> paramDiff = activeRuleDiff.paramDifference().entriesDiffering().get("max");
        assertThat(paramDiff.leftValue()).isEqualTo("20");
        assertThat(paramDiff.rightValue()).isEqualTo("30");
    }

    @Test
    public void compare_different_params() {
        qProfileRules.activateAndCommit(dbSession, left, Collections.singleton(RuleActivation.create(xooRule1.getId(), null, ImmutableMap.of("max", "20"))));
        qProfileRules.activateAndCommit(dbSession, right, Collections.singleton(RuleActivation.create(xooRule1.getId(), null, ImmutableMap.of("min", "5"))));
        QProfileComparisonResult result = comparison.compare(dbSession, left, right);
        assertThat(result.left().getKee()).isEqualTo(left.getKee());
        assertThat(result.right().getKee()).isEqualTo(right.getKee());
        assertThat(result.same()).isEmpty();
        assertThat(result.inLeft()).isEmpty();
        assertThat(result.inRight()).isEmpty();
        assertThat(result.modified()).isNotEmpty().containsOnlyKeys(xooRule1.getKey());
        assertThat(result.collectRuleKeys()).containsOnly(xooRule1.getKey());
        ActiveRuleDiff activeRuleDiff = result.modified().get(xooRule1.getKey());
        assertThat(activeRuleDiff.leftSeverity()).isEqualTo(activeRuleDiff.rightSeverity()).isEqualTo(xooRule1.getSeverityString());
        assertThat(activeRuleDiff.paramDifference().areEqual()).isFalse();
        assertThat(activeRuleDiff.paramDifference().entriesDiffering()).isEmpty();
        assertThat(activeRuleDiff.paramDifference().entriesOnlyOnLeft()).containsExactly(MapEntry.entry("max", "20"));
        assertThat(activeRuleDiff.paramDifference().entriesOnlyOnRight()).containsExactly(MapEntry.entry("min", "5"));
    }
}

