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
package org.sonar.ce.task.projectanalysis.step;


import NewIssuesStatistics.Metric.RULE_TYPE;
import NewIssuesStatistics.Stats;
import RuleType.SECURITY_HOTSPOT;
import System2.INSTANCE;
import Type.FILE;
import Type.PROJECT;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.rules.RuleType;
import org.sonar.api.utils.Duration;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.DefaultBranchImpl;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.issue.IssueCache;
import org.sonar.ce.task.projectanalysis.issue.RuleRepositoryRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.issue.notification.DistributedMetricStatsInt;
import org.sonar.server.issue.notification.MyNewIssuesNotification;
import org.sonar.server.issue.notification.NewIssuesNotification;
import org.sonar.server.issue.notification.NewIssuesNotificationFactory;
import org.sonar.server.issue.notification.NewIssuesStatistics;
import org.sonar.server.notification.NotificationService;


public class SendIssueNotificationsStepTest extends BaseStepTest {
    private static final String BRANCH_NAME = "feature";

    private static final String PULL_REQUEST_ID = "pr-123";

    private static final long ANALYSE_DATE = 123L;

    private static final int FIVE_MINUTES_IN_MS = (1000 * 60) * 5;

    private static final Duration ISSUE_DURATION = Duration.create(100L);

    private static final Component FILE = ReportComponent.builder(Type.FILE, 11).build();

    private static final Component PROJECT = ReportComponent.builder(Type.PROJECT, 1).setCodePeriodVersion(randomAlphanumeric(10)).addChildren(SendIssueNotificationsStepTest.FILE).build();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(SendIssueNotificationsStepTest.PROJECT);

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setBranch(new DefaultBranchImpl()).setAnalysisDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE));

    @Rule
    public RuleRepositoryRule ruleRepository = new RuleRepositoryRule();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private final Random random = new Random();

    private final RuleType[] RULE_TYPES_EXCEPT_HOTSPOTS = Stream.of(RuleType.values()).filter(( r) -> r != RuleType.SECURITY_HOTSPOT).toArray(RuleType[]::new);

    private final RuleType randomRuleType = RULE_TYPES_EXCEPT_HOTSPOTS[random.nextInt(RULE_TYPES_EXCEPT_HOTSPOTS.length)];

    private NotificationService notificationService = Mockito.mock(NotificationService.class);

    private NewIssuesNotificationFactory newIssuesNotificationFactory = Mockito.mock(NewIssuesNotificationFactory.class);

    private NewIssuesNotification newIssuesNotificationMock = createNewIssuesNotificationMock();

    private MyNewIssuesNotification myNewIssuesNotificationMock = createMyNewIssuesNotificationMock();

    private IssueCache issueCache;

    private SendIssueNotificationsStep underTest;

    @Test
    public void do_not_send_notifications_if_no_subscribers() {
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(false);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService, Mockito.never()).deliver(ArgumentMatchers.any());
        SendIssueNotificationsStepTest.verifyStatistics(context, 0, 0, 0);
    }

    @Test
    public void send_global_new_issues_notification() {
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getUuid()), ArgumentMatchers.any())).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        Mockito.verify(newIssuesNotificationMock).setProject(SendIssueNotificationsStepTest.PROJECT.getKey(), SendIssueNotificationsStepTest.PROJECT.getName(), null, null);
        Mockito.verify(newIssuesNotificationMock).setAnalysisDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE));
        Mockito.verify(newIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getName()), ArgumentMatchers.any());
        Mockito.verify(newIssuesNotificationMock).setDebt(SendIssueNotificationsStepTest.ISSUE_DURATION);
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 0, 0);
    }

    @Test
    public void send_global_new_issues_notification_only_for_non_backdated_issues() {
        Random random = new Random();
        Integer[] efforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10000 * i).toArray(Integer[]::new);
        Integer[] backDatedEfforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10 + (random.nextInt(100))).toArray(Integer[]::new);
        Duration expectedEffort = Duration.create(Arrays.stream(efforts).mapToInt(( i) -> i).sum());
        List<DefaultIssue> issues = Stream.concat(Arrays.stream(efforts).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))), Arrays.stream(backDatedEfforts).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setCreationDate(new Date(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS)))))).collect(Collectors.toList());
        Collections.shuffle(issues);
        DiskCache<DefaultIssue>.DiskAppender issueCache = this.issueCache.newAppender();
        issues.forEach(issueCache::append);
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        ArgumentCaptor<NewIssuesStatistics.Stats> statsCaptor = ArgumentCaptor.forClass(Stats.class);
        Mockito.verify(newIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getName()), statsCaptor.capture());
        Mockito.verify(newIssuesNotificationMock).setDebt(expectedEffort);
        NewIssuesStatistics.Stats stats = statsCaptor.getValue();
        assertThat(stats.hasIssues()).isTrue();
        // just checking all issues have been added to the stats
        DistributedMetricStatsInt severity = stats.getDistributedMetricStats(RULE_TYPE);
        assertThat(severity.getOnLeak()).isEqualTo(efforts.length);
        assertThat(severity.getTotal()).isEqualTo(((backDatedEfforts.length) + (efforts.length)));
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 0, 0);
    }

    @Test
    public void do_not_send_global_new_issues_notification_if_issue_has_been_backdated() {
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setCreationDate(new Date(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS))))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService, Mockito.never()).deliver(ArgumentMatchers.any());
        SendIssueNotificationsStepTest.verifyStatistics(context, 0, 0, 0);
    }

    @Test
    public void send_global_new_issues_notification_on_branch() {
        ComponentDto branch = setUpProjectWithBranch();
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(branch.uuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        analysisMetadataHolder.setBranch(SendIssueNotificationsStepTest.newBranch());
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        Mockito.verify(newIssuesNotificationMock).setProject(branch.getKey(), branch.longName(), SendIssueNotificationsStepTest.BRANCH_NAME, null);
        Mockito.verify(newIssuesNotificationMock).setAnalysisDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE));
        Mockito.verify(newIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(branch.longName()), ArgumentMatchers.any(Stats.class));
        Mockito.verify(newIssuesNotificationMock).setDebt(SendIssueNotificationsStepTest.ISSUE_DURATION);
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 0, 0);
    }

    @Test
    public void send_global_new_issues_notification_on_pull_request() {
        ComponentDto branch = setUpProjectWithBranch();
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(branch.uuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        analysisMetadataHolder.setBranch(SendIssueNotificationsStepTest.newPullRequest());
        analysisMetadataHolder.setPullRequestKey(SendIssueNotificationsStepTest.PULL_REQUEST_ID);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        Mockito.verify(newIssuesNotificationMock).setProject(branch.getKey(), branch.longName(), null, SendIssueNotificationsStepTest.PULL_REQUEST_ID);
        Mockito.verify(newIssuesNotificationMock).setAnalysisDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE));
        Mockito.verify(newIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(branch.longName()), ArgumentMatchers.any(Stats.class));
        Mockito.verify(newIssuesNotificationMock).setDebt(SendIssueNotificationsStepTest.ISSUE_DURATION);
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 0, 0);
    }

    @Test
    public void do_not_send_global_new_issues_notification_on_branch_if_issue_has_been_backdated() {
        ComponentDto branch = setUpProjectWithBranch();
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setCreationDate(new Date(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS))))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(branch.uuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        analysisMetadataHolder.setBranch(SendIssueNotificationsStepTest.newBranch());
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService, Mockito.never()).deliver(ArgumentMatchers.any());
        SendIssueNotificationsStepTest.verifyStatistics(context, 0, 0, 0);
    }

    @Test
    public void send_new_issues_notification_to_user() {
        UserDto user = db.users().insertUser();
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setAssigneeUuid(user.getUuid()).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getUuid()), ArgumentMatchers.any())).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        Mockito.verify(notificationService).deliver(myNewIssuesNotificationMock);
        Mockito.verify(myNewIssuesNotificationMock).setAssignee(ArgumentMatchers.any(UserDto.class));
        Mockito.verify(myNewIssuesNotificationMock).setProject(SendIssueNotificationsStepTest.PROJECT.getKey(), SendIssueNotificationsStepTest.PROJECT.getName(), null, null);
        Mockito.verify(myNewIssuesNotificationMock).setAnalysisDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE));
        Mockito.verify(myNewIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getName()), ArgumentMatchers.any(Stats.class));
        Mockito.verify(myNewIssuesNotificationMock).setDebt(SendIssueNotificationsStepTest.ISSUE_DURATION);
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 1, 0);
    }

    @Test
    public void send_new_issues_notification_to_user_only_for_those_assigned_to_her() throws IOException {
        UserDto perceval = db.users().insertUser(( u) -> u.setLogin("perceval"));
        Integer[] assigned = IntStream.range(0, 5).mapToObj(( i) -> 10000 * i).toArray(Integer[]::new);
        Duration expectedEffort = Duration.create(Arrays.stream(assigned).mapToInt(( i) -> i).sum());
        UserDto arthur = db.users().insertUser(( u) -> u.setLogin("arthur"));
        Integer[] assignedToOther = IntStream.range(0, 3).mapToObj(( i) -> 10).toArray(Integer[]::new);
        List<DefaultIssue> issues = Stream.concat(Arrays.stream(assigned).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setAssigneeUuid(perceval.getUuid()).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))), Arrays.stream(assignedToOther).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setAssigneeUuid(arthur.getUuid()).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE)))).collect(Collectors.toList());
        Collections.shuffle(issues);
        IssueCache issueCache = new IssueCache(temp.newFile(), System2.INSTANCE);
        DiskCache<DefaultIssue>.DiskAppender newIssueCache = issueCache.newAppender();
        issues.forEach(newIssueCache::append);
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        NewIssuesNotificationFactory newIssuesNotificationFactory = Mockito.mock(NewIssuesNotificationFactory.class);
        NewIssuesNotification newIssuesNotificationMock = createNewIssuesNotificationMock();
        Mockito.when(newIssuesNotificationFactory.newNewIssuesNotification()).thenReturn(newIssuesNotificationMock);
        MyNewIssuesNotification myNewIssuesNotificationMock1 = createMyNewIssuesNotificationMock();
        MyNewIssuesNotification myNewIssuesNotificationMock2 = createMyNewIssuesNotificationMock();
        Mockito.when(newIssuesNotificationFactory.newMyNewIssuesNotification()).thenReturn(myNewIssuesNotificationMock1).thenReturn(myNewIssuesNotificationMock2);
        TestComputationStepContext context = new TestComputationStepContext();
        new SendIssueNotificationsStep(issueCache, ruleRepository, treeRootHolder, notificationService, analysisMetadataHolder, newIssuesNotificationFactory, db.getDbClient()).execute(context);
        Mockito.verify(notificationService).deliver(myNewIssuesNotificationMock1);
        Map<String, MyNewIssuesNotification> myNewIssuesNotificationMocksByUsersName = new HashMap<>();
        ArgumentCaptor<UserDto> userCaptor1 = ArgumentCaptor.forClass(UserDto.class);
        Mockito.verify(myNewIssuesNotificationMock1).setAssignee(userCaptor1.capture());
        myNewIssuesNotificationMocksByUsersName.put(userCaptor1.getValue().getLogin(), myNewIssuesNotificationMock1);
        Mockito.verify(notificationService).deliver(myNewIssuesNotificationMock2);
        ArgumentCaptor<UserDto> userCaptor2 = ArgumentCaptor.forClass(UserDto.class);
        Mockito.verify(myNewIssuesNotificationMock2).setAssignee(userCaptor2.capture());
        myNewIssuesNotificationMocksByUsersName.put(userCaptor2.getValue().getLogin(), myNewIssuesNotificationMock2);
        MyNewIssuesNotification myNewIssuesNotificationMock = myNewIssuesNotificationMocksByUsersName.get("perceval");
        ArgumentCaptor<NewIssuesStatistics.Stats> statsCaptor = ArgumentCaptor.forClass(Stats.class);
        Mockito.verify(myNewIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getName()), statsCaptor.capture());
        Mockito.verify(myNewIssuesNotificationMock).setDebt(expectedEffort);
        NewIssuesStatistics.Stats stats = statsCaptor.getValue();
        assertThat(stats.hasIssues()).isTrue();
        // just checking all issues have been added to the stats
        DistributedMetricStatsInt severity = stats.getDistributedMetricStats(RULE_TYPE);
        assertThat(severity.getOnLeak()).isEqualTo(assigned.length);
        assertThat(severity.getTotal()).isEqualTo(assigned.length);
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 2, 0);
    }

    @Test
    public void send_new_issues_notification_to_user_only_for_non_backdated_issues() {
        UserDto user = db.users().insertUser();
        Random random = new Random();
        Integer[] efforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10000 * i).toArray(Integer[]::new);
        Integer[] backDatedEfforts = IntStream.range(0, (1 + (random.nextInt(10)))).mapToObj(( i) -> 10 + (random.nextInt(100))).toArray(Integer[]::new);
        Duration expectedEffort = Duration.create(Arrays.stream(efforts).mapToInt(( i) -> i).sum());
        List<DefaultIssue> issues = Stream.concat(Arrays.stream(efforts).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setAssigneeUuid(user.getUuid()).setCreationDate(new Date(SendIssueNotificationsStepTest.ANALYSE_DATE))), Arrays.stream(backDatedEfforts).map(( effort) -> new DefaultIssue().setType(randomRuleType).setEffort(Duration.create(effort)).setAssigneeUuid(user.getUuid()).setCreationDate(new Date(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS)))))).collect(Collectors.toList());
        Collections.shuffle(issues);
        DiskCache<DefaultIssue>.DiskAppender issueCache = this.issueCache.newAppender();
        issues.forEach(issueCache::append);
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService).deliver(newIssuesNotificationMock);
        Mockito.verify(notificationService).deliver(myNewIssuesNotificationMock);
        Mockito.verify(myNewIssuesNotificationMock).setAssignee(ArgumentMatchers.any(UserDto.class));
        ArgumentCaptor<NewIssuesStatistics.Stats> statsCaptor = ArgumentCaptor.forClass(Stats.class);
        Mockito.verify(myNewIssuesNotificationMock).setStatistics(ArgumentMatchers.eq(SendIssueNotificationsStepTest.PROJECT.getName()), statsCaptor.capture());
        Mockito.verify(myNewIssuesNotificationMock).setDebt(expectedEffort);
        NewIssuesStatistics.Stats stats = statsCaptor.getValue();
        assertThat(stats.hasIssues()).isTrue();
        // just checking all issues have been added to the stats
        DistributedMetricStatsInt severity = stats.getDistributedMetricStats(RULE_TYPE);
        assertThat(severity.getOnLeak()).isEqualTo(efforts.length);
        assertThat(severity.getTotal()).isEqualTo(((backDatedEfforts.length) + (efforts.length)));
        SendIssueNotificationsStepTest.verifyStatistics(context, 1, 1, 0);
    }

    @Test
    public void do_not_send_new_issues_notification_to_user_if_issue_is_backdated() {
        UserDto user = db.users().insertUser();
        issueCache.newAppender().append(new DefaultIssue().setType(randomRuleType).setEffort(SendIssueNotificationsStepTest.ISSUE_DURATION).setAssigneeUuid(user.getUuid()).setCreationDate(new Date(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS))))).close();
        Mockito.when(notificationService.hasProjectSubscribersForTypes(SendIssueNotificationsStepTest.PROJECT.getUuid(), SendIssueNotificationsStep.NOTIF_TYPES)).thenReturn(true);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService, Mockito.never()).deliver(ArgumentMatchers.any());
        SendIssueNotificationsStepTest.verifyStatistics(context, 0, 0, 0);
    }

    @Test
    public void send_issues_change_notification() {
        sendIssueChangeNotification(SendIssueNotificationsStepTest.ANALYSE_DATE);
    }

    @Test
    public void dont_send_issues_change_notification_for_hotspot() {
        UserDto user = db.users().insertUser();
        ComponentDto project = ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()).setDbKey(SendIssueNotificationsStepTest.PROJECT.getDbKey()).setLongName(SendIssueNotificationsStepTest.PROJECT.getName());
        ComponentDto file = ComponentTesting.newFileDto(project).setDbKey(SendIssueNotificationsStepTest.FILE.getDbKey()).setLongName(SendIssueNotificationsStepTest.FILE.getName());
        RuleDefinitionDto ruleDefinitionDto = newRule();
        DefaultIssue issue = prepareIssue(SendIssueNotificationsStepTest.ANALYSE_DATE, user, project, file, ruleDefinitionDto, SECURITY_HOTSPOT);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Mockito.verify(notificationService, Mockito.never()).deliver(ArgumentMatchers.any());
        SendIssueNotificationsStepTest.verifyStatistics(context, 0, 0, 0);
    }

    @Test
    public void send_issues_change_notification_even_if_issue_is_backdated() {
        sendIssueChangeNotification(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS)));
    }

    @Test
    public void send_issues_change_notification_on_branch() {
        sendIssueChangeNotificationOnBranch(SendIssueNotificationsStepTest.ANALYSE_DATE);
    }

    @Test
    public void send_issues_change_notification_on_branch_even_if_issue_is_backdated() {
        sendIssueChangeNotificationOnBranch(((SendIssueNotificationsStepTest.ANALYSE_DATE) - (SendIssueNotificationsStepTest.FIVE_MINUTES_IN_MS)));
    }
}

