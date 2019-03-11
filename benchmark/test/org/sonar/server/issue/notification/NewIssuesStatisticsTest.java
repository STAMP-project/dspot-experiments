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
package org.sonar.server.issue.notification;


import Metric.ASSIGNEE;
import Metric.COMPONENT;
import Metric.RULE;
import Metric.RULE_TYPE;
import Metric.TAG;
import NewIssuesStatistics.Stats;
import RuleType.BUG;
import RuleType.CODE_SMELL;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleType;
import org.sonar.api.utils.Duration;
import org.sonar.core.issue.DefaultIssue;


public class NewIssuesStatisticsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Random random = new Random();

    private RuleType randomRuleTypeExceptHotspot = RuleType.values()[random.nextInt(((RuleType.values().length) - 1))];

    private NewIssuesStatistics underTest = new NewIssuesStatistics(Issue::isNew);

    @Test
    public void add_fails_with_NPE_if_RuleType_is_null() {
        String assignee = randomAlphanumeric(10);
        DefaultIssue issue = new DefaultIssue().setType(null).setAssigneeUuid(assignee).setNew(new Random().nextBoolean());
        expectedException.expect(NullPointerException.class);
        underTest.add(issue);
    }

    @Test
    public void add_issues_with_correct_global_statistics() {
        DefaultIssue issue = new DefaultIssue().setAssigneeUuid("maynard").setComponentUuid("file-uuid").setNew(true).setType(BUG).setRuleKey(RuleKey.of("SonarQube", "rule-the-world")).setTags(Lists.newArrayList("bug", "owasp")).setEffort(Duration.create(5L));
        underTest.add(issue);
        underTest.add(issue.setAssigneeUuid("james"));
        underTest.add(issue.setAssigneeUuid("keenan"));
        assertThat(countDistributionTotal(ASSIGNEE, "maynard")).isEqualTo(1);
        assertThat(countDistributionTotal(ASSIGNEE, "james")).isEqualTo(1);
        assertThat(countDistributionTotal(ASSIGNEE, "keenan")).isEqualTo(1);
        assertThat(countDistributionTotal(ASSIGNEE, "wrong.login")).isNull();
        assertThat(countDistributionTotal(COMPONENT, "file-uuid")).isEqualTo(3);
        assertThat(countDistributionTotal(COMPONENT, "wrong-uuid")).isNull();
        assertThat(countDistributionTotal(RULE_TYPE, BUG.name())).isEqualTo(3);
        assertThat(countDistributionTotal(RULE_TYPE, CODE_SMELL.name())).isNull();
        assertThat(countDistributionTotal(TAG, "owasp")).isEqualTo(3);
        assertThat(countDistributionTotal(TAG, "wrong-tag")).isNull();
        assertThat(countDistributionTotal(RULE, "SonarQube:rule-the-world")).isEqualTo(3);
        assertThat(countDistributionTotal(RULE, "SonarQube:has-a-fake-rule")).isNull();
        assertThat(underTest.globalStatistics().effort().getTotal()).isEqualTo(15L);
        assertThat(underTest.globalStatistics().hasIssues()).isTrue();
        assertThat(underTest.hasIssues()).isTrue();
        assertThat(underTest.getAssigneesStatistics().get("maynard").hasIssues()).isTrue();
    }

    @Test
    public void add_counts_issue_per_RuleType_on_leak_globally_and_per_assignee() {
        String assignee = randomAlphanumeric(10);
        Arrays.stream(RuleType.values()).map(( ruleType) -> new DefaultIssue().setType(ruleType).setAssigneeUuid(assignee).setNew(true)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(RULE_TYPE);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(RULE_TYPE);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> Arrays.stream(RuleType.values()).forEach(( ruleType) -> assertStats(distribution, ruleType.name(), 1, 0, 1)));
    }

    @Test
    public void add_counts_issue_per_RuleType_off_leak_globally_and_per_assignee() {
        String assignee = randomAlphanumeric(10);
        Arrays.stream(RuleType.values()).map(( ruleType) -> new DefaultIssue().setType(ruleType).setAssigneeUuid(assignee).setNew(false)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(RULE_TYPE);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(RULE_TYPE);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> Arrays.stream(RuleType.values()).forEach(( ruleType) -> assertStats(distribution, ruleType.name(), 0, 1, 1)));
    }

    @Test
    public void add_counts_issue_per_component_on_leak_globally_and_per_assignee() {
        List<String> componentUuids = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        componentUuids.stream().map(( componentUuid) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setComponentUuid(componentUuid).setAssigneeUuid(assignee).setNew(true)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(COMPONENT);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(COMPONENT);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> componentUuids.forEach(( componentUuid) -> assertStats(distribution, componentUuid, 1, 0, 1)));
    }

    @Test
    public void add_counts_issue_per_component_off_leak_globally_and_per_assignee() {
        List<String> componentUuids = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        componentUuids.stream().map(( componentUuid) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setComponentUuid(componentUuid).setAssigneeUuid(assignee).setNew(false)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(COMPONENT);
        NewIssuesStatistics.Stats stats = underTest.getAssigneesStatistics().get(assignee);
        DistributedMetricStatsInt assigneeDistribution = stats.getDistributedMetricStats(COMPONENT);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> componentUuids.forEach(( componentUuid) -> assertStats(distribution, componentUuid, 0, 1, 1)));
    }

    @Test
    public void add_does_not_count_component_if_null_neither_globally_nor_per_assignee() {
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setComponentUuid(null).setAssigneeUuid(assignee).setNew(new Random().nextBoolean()));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(COMPONENT);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(COMPONENT);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> {
            assertThat(distribution.getTotal()).isEqualTo(0);
            assertThat(distribution.getForLabel(null).isPresent()).isFalse();
        });
    }

    @Test
    public void add_counts_issue_per_ruleKey_on_leak_globally_and_per_assignee() {
        String repository = randomAlphanumeric(3);
        List<String> ruleKeys = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        ruleKeys.stream().map(( ruleKey) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setRuleKey(RuleKey.of(repository, ruleKey)).setAssigneeUuid(assignee).setNew(true)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(RULE);
        NewIssuesStatistics.Stats stats = underTest.getAssigneesStatistics().get(assignee);
        DistributedMetricStatsInt assigneeDistribution = stats.getDistributedMetricStats(RULE);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> ruleKeys.forEach(( ruleKey) -> assertStats(distribution, RuleKey.of(repository, ruleKey).toString(), 1, 0, 1)));
    }

    @Test
    public void add_counts_issue_per_ruleKey_off_leak_globally_and_per_assignee() {
        String repository = randomAlphanumeric(3);
        List<String> ruleKeys = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        ruleKeys.stream().map(( ruleKey) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setRuleKey(RuleKey.of(repository, ruleKey)).setAssigneeUuid(assignee).setNew(false)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(RULE);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(RULE);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> ruleKeys.forEach(( ruleKey) -> assertStats(distribution, RuleKey.of(repository, ruleKey).toString(), 0, 1, 1)));
    }

    @Test
    public void add_does_not_count_ruleKey_if_null_neither_globally_nor_per_assignee() {
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setRuleKey(null).setAssigneeUuid(assignee).setNew(new Random().nextBoolean()));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(RULE);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(RULE);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> {
            assertThat(distribution.getTotal()).isEqualTo(0);
            assertThat(distribution.getForLabel(null).isPresent()).isFalse();
        });
    }

    @Test
    public void add_counts_issue_per_assignee_on_leak_globally_and_per_assignee() {
        List<String> assignees = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        assignees.stream().map(( assignee) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setAssigneeUuid(assignee).setNew(true)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(ASSIGNEE);
        assignees.forEach(( assignee) -> assertStats(globalDistribution, assignee, 1, 0, 1));
        assignees.forEach(( assignee) -> {
            NewIssuesStatistics.Stats stats = underTest.getAssigneesStatistics().get(assignee);
            DistributedMetricStatsInt assigneeStats = stats.getDistributedMetricStats(ASSIGNEE);
            assertThat(assigneeStats.getOnLeak()).isEqualTo(1);
            assertThat(assigneeStats.getTotal()).isEqualTo(1);
            assignees.forEach(( s) -> {
                Optional<MetricStatsInt> forLabelOpts = assigneeStats.getForLabel(s);
                if (s.equals(assignee)) {
                    assertThat(forLabelOpts.isPresent()).isTrue();
                    MetricStatsInt forLabel = forLabelOpts.get();
                    assertThat(forLabel.getOnLeak()).isEqualTo(1);
                    assertThat(forLabel.getTotal()).isEqualTo(1);
                } else {
                    assertThat(forLabelOpts.isPresent()).isFalse();
                }
            });
        });
    }

    @Test
    public void add_counts_issue_per_assignee_off_leak_globally_and_per_assignee() {
        List<String> assignees = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        assignees.stream().map(( assignee) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setAssigneeUuid(assignee).setNew(false)).forEach(underTest::add);
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(ASSIGNEE);
        assignees.forEach(( assignee) -> assertStats(globalDistribution, assignee, 0, 1, 1));
        assignees.forEach(( assignee) -> {
            NewIssuesStatistics.Stats stats = underTest.getAssigneesStatistics().get(assignee);
            DistributedMetricStatsInt assigneeStats = stats.getDistributedMetricStats(ASSIGNEE);
            assertThat(assigneeStats.getOnLeak()).isEqualTo(0);
            assertThat(assigneeStats.getTotal()).isEqualTo(1);
            assignees.forEach(( s) -> {
                Optional<MetricStatsInt> forLabelOpts = assigneeStats.getForLabel(s);
                if (s.equals(assignee)) {
                    assertThat(forLabelOpts.isPresent()).isTrue();
                    MetricStatsInt forLabel = forLabelOpts.get();
                    assertThat(forLabel.getOnLeak()).isEqualTo(0);
                    assertThat(forLabel.getTotal()).isEqualTo(1);
                } else {
                    assertThat(forLabelOpts.isPresent()).isFalse();
                }
            });
        });
    }

    @Test
    public void add_does_not_assignee_if_empty_neither_globally_nor_per_assignee() {
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setAssigneeUuid(null).setNew(new Random().nextBoolean()));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(ASSIGNEE);
        assertThat(globalDistribution.getTotal()).isEqualTo(0);
        assertThat(globalDistribution.getForLabel(null).isPresent()).isFalse();
        assertThat(underTest.getAssigneesStatistics()).isEmpty();
    }

    @Test
    public void add_counts_issue_per_tags_on_leak_globally_and_per_assignee() {
        List<String> tags = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setTags(tags).setAssigneeUuid(assignee).setNew(true));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(TAG);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(TAG);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> tags.forEach(( tag) -> assertStats(distribution, tag, 1, 0, 1)));
    }

    @Test
    public void add_counts_issue_per_tags_off_leak_globally_and_per_assignee() {
        List<String> tags = IntStream.range(0, (1 + (new Random().nextInt(10)))).mapToObj(( i) -> randomAlphabetic(3)).collect(Collectors.toList());
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setTags(tags).setAssigneeUuid(assignee).setNew(false));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(TAG);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(TAG);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> tags.forEach(( tag) -> assertStats(distribution, tag, 0, 1, 1)));
    }

    @Test
    public void add_does_not_count_tags_if_empty_neither_globally_nor_per_assignee() {
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setTags(Collections.emptyList()).setAssigneeUuid(assignee).setNew(new Random().nextBoolean()));
        DistributedMetricStatsInt globalDistribution = underTest.globalStatistics().getDistributedMetricStats(TAG);
        DistributedMetricStatsInt assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).getDistributedMetricStats(TAG);
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> {
            assertThat(distribution.getTotal()).isEqualTo(0);
            assertThat(distribution.getForLabel(null).isPresent()).isFalse();
        });
    }

    @Test
    public void add_sums_effort_on_leak_globally_and_per_assignee() {
        Random random = new Random();
        List<Integer> efforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10000 * i).collect(Collectors.toList());
        int expected = efforts.stream().mapToInt(( s) -> s).sum();
        String assignee = randomAlphanumeric(10);
        efforts.stream().map(( effort) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setEffort(Duration.create(effort)).setAssigneeUuid(assignee).setNew(true)).forEach(underTest::add);
        MetricStatsLong globalDistribution = underTest.globalStatistics().effort();
        MetricStatsLong assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).effort();
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> {
            assertThat(distribution.getOnLeak()).isEqualTo(expected);
            assertThat(distribution.getOffLeak()).isEqualTo(0);
            assertThat(distribution.getTotal()).isEqualTo(expected);
        });
    }

    @Test
    public void add_sums_effort_off_leak_globally_and_per_assignee() {
        Random random = new Random();
        List<Integer> efforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10000 * i).collect(Collectors.toList());
        int expected = efforts.stream().mapToInt(( s) -> s).sum();
        String assignee = randomAlphanumeric(10);
        efforts.stream().map(( effort) -> new DefaultIssue().setType(randomRuleTypeExceptHotspot).setEffort(Duration.create(effort)).setAssigneeUuid(assignee).setNew(false)).forEach(underTest::add);
        MetricStatsLong globalDistribution = underTest.globalStatistics().effort();
        MetricStatsLong assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).effort();
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> {
            assertThat(distribution.getOnLeak()).isEqualTo(0);
            assertThat(distribution.getOffLeak()).isEqualTo(expected);
            assertThat(distribution.getTotal()).isEqualTo(expected);
        });
    }

    @Test
    public void add_does_not_sum_effort_if_null_neither_globally_nor_per_assignee() {
        String assignee = randomAlphanumeric(10);
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setEffort(null).setAssigneeUuid(assignee).setNew(new Random().nextBoolean()));
        MetricStatsLong globalDistribution = underTest.globalStatistics().effort();
        MetricStatsLong assigneeDistribution = underTest.getAssigneesStatistics().get(assignee).effort();
        Stream.of(globalDistribution, assigneeDistribution).forEach(( distribution) -> assertThat(distribution.getTotal()).isEqualTo(0));
    }

    @Test
    public void do_not_have_issues_when_no_issue_added() {
        assertThat(underTest.globalStatistics().hasIssues()).isFalse();
    }

    @Test
    public void verify_toString() {
        String componentUuid = randomAlphanumeric(2);
        String tag = randomAlphanumeric(3);
        String assignee = randomAlphanumeric(4);
        int effort = 10 + (new Random().nextInt(5));
        RuleKey ruleKey = RuleKey.of(randomAlphanumeric(5), randomAlphanumeric(6));
        underTest.add(new DefaultIssue().setType(randomRuleTypeExceptHotspot).setComponentUuid(componentUuid).setTags(ImmutableSet.of(tag)).setAssigneeUuid(assignee).setRuleKey(ruleKey).setEffort(Duration.create(effort)));
        assertThat(underTest.toString()).isEqualTo(((((((((((((((((((((((((((((((((((((((((((((((((((("NewIssuesStatistics{" + "assigneesStatistics={") + assignee) + "=") + "Stats{distributions={") + "RULE_TYPE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + (randomRuleTypeExceptHotspot.name())) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "TAG=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + tag) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "COMPONENT=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + componentUuid) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "ASSIGNEE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + assignee) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "RULE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + (ruleKey.toString())) + "=MetricStatsInt{onLeak=1, offLeak=0}}}}, ") + "effortStats=MetricStatsLong{onLeak=") + effort) + ", offLeak=0}}}, ") + "globalStatistics=Stats{distributions={") + "RULE_TYPE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + (randomRuleTypeExceptHotspot.name())) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "TAG=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + tag) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "COMPONENT=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + componentUuid) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "ASSIGNEE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + assignee) + "=MetricStatsInt{onLeak=1, offLeak=0}}}, ") + "RULE=DistributedMetricStatsInt{globalStats=MetricStatsInt{onLeak=1, offLeak=0}, ") + "statsPerLabel={") + (ruleKey.toString())) + "=MetricStatsInt{onLeak=1, offLeak=0}}}}, ") + "effortStats=MetricStatsLong{onLeak=") + effort) + ", offLeak=0}}}"));
    }
}

