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
package org.sonar.api.ce.measure.test;


import RuleType.BUG;
import Severity.BLOCKER;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.ce.measure.Issue;
import org.sonar.api.issue.Issue.RESOLUTION_FIXED;
import org.sonar.api.issue.Issue.STATUS_RESOLVED;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;


public class TestIssueTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void create_issue() throws Exception {
        Issue issue = new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setEffort(Duration.create(10L)).setType(BUG).build();
        assertThat(issue.key()).isEqualTo("ABCD");
        assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("xoo", "S01"));
        assertThat(issue.severity()).isEqualTo(BLOCKER);
        assertThat(issue.status()).isEqualTo(STATUS_RESOLVED);
        assertThat(issue.resolution()).isEqualTo(RESOLUTION_FIXED);
        assertThat(issue.effort()).isEqualTo(Duration.create(10L));
    }

    @Test
    public void create_issue_with_minimal_fields() throws Exception {
        Issue issue = new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setType(BUG).build();
        assertThat(issue.resolution()).isNull();
        assertThat(issue.effort()).isNull();
    }

    @Test
    public void fail_with_NPE_when_building_issue_without_key() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("key cannot be null");
        new TestIssue.Builder().setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_creating_issue_with_null_key() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("key cannot be null");
        new TestIssue.Builder().setKey(null);
    }

    @Test
    public void fail_with_NPE_when_building_issue_without_rule_key() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ruleKey cannot be null");
        new TestIssue.Builder().setKey("ABCD").setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_creating_issue_with_null_rule_key() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ruleKey cannot be null");
        new TestIssue.Builder().setRuleKey(null);
    }

    @Test
    public void fail_with_IAE_when_building_issue_with_invalid_resolution() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("resolution 'unknown' is invalid");
        new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus(STATUS_RESOLVED).setResolution("unknown").setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_building_issue_without_severity() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("severity cannot be null");
        new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_creating_issue_with_null_severity() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("severity cannot be null");
        new TestIssue.Builder().setSeverity(null);
    }

    @Test
    public void fail_with_IAE_when_building_issue_with_invalid_severity() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("severity 'unknown' is invalid");
        new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity("unknown").setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_building_issue_without_status() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("status cannot be null");
        new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }

    @Test
    public void fail_with_NPE_when_creating_issue_with_null_status() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("status cannot be null");
        new TestIssue.Builder().setStatus(null);
    }

    @Test
    public void fail_with_IAE_when_building_issue_with_invalid_status() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("status 'unknown' is invalid");
        new TestIssue.Builder().setKey("ABCD").setRuleKey(RuleKey.of("xoo", "S01")).setSeverity(BLOCKER).setStatus("unknown").setResolution(RESOLUTION_FIXED).setType(BUG).build();
    }
}

