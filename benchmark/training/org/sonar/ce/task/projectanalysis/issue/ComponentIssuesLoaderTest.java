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
package org.sonar.ce.task.projectanalysis.issue;


import System2.INSTANCE;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.System2;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.rule.RuleDefinitionDto;


@RunWith(DataProviderRunner.class)
public class ComponentIssuesLoaderTest {
    private static final Date NOW = parseDateTime("2018-08-17T13:44:53+0000");

    private static final Date DATE_LIMIT_30_DAYS_BACK_MIDNIGHT = parseDateTime("2018-07-18T00:00:00+0000");

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = dbTester.getDbClient();

    private System2 system2 = Mockito.mock(System2.class);

    @Test
    public void loadClosedIssues_returns_single_DefaultIssue_by_issue_based_on_first_row() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = dbTester.rules().insert(( t) -> t.setType(RuleType.CODE_SMELL));
        Date issueDate = addDays(ComponentIssuesLoaderTest.NOW, (-10));
        IssueDto issue = dbTester.issues().insert(rule, project, file, ( t) -> t.setStatus(STATUS_CLOSED).setIssueCloseDate(issueDate).setIsFromHotspot(false));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(issueDate, 10));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(addDays(issueDate, 3), 20));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(addDays(issueDate, 1), 30));
        Mockito.when(system2.now()).thenReturn(ComponentIssuesLoaderTest.NOW.getTime());
        ComponentIssuesLoader underTest = newComponentIssuesLoader(ComponentIssuesLoaderTest.newEmptySettings());
        List<DefaultIssue> defaultIssues = underTest.loadClosedIssues(file.uuid());
        assertThat(defaultIssues).hasSize(1);
        assertThat(defaultIssues.iterator().next().getLine()).isEqualTo(20);
    }

    @Test
    public void loadClosedIssues_returns_single_DefaultIssue_with_null_line_if_first_row_has_no_line_diff() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = dbTester.rules().insert(( t) -> t.setType(RuleType.CODE_SMELL));
        Date issueDate = addDays(ComponentIssuesLoaderTest.NOW, (-10));
        IssueDto issue = dbTester.issues().insert(rule, project, file, ( t) -> t.setStatus(STATUS_CLOSED).setIssueCloseDate(issueDate).setIsFromHotspot(false));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(issueDate, 10));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(addDays(issueDate, 2), null));
        dbTester.issues().insertFieldDiffs(issue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(addDays(issueDate, 1), 30));
        Mockito.when(system2.now()).thenReturn(ComponentIssuesLoaderTest.NOW.getTime());
        ComponentIssuesLoader underTest = newComponentIssuesLoader(ComponentIssuesLoaderTest.newEmptySettings());
        List<DefaultIssue> defaultIssues = underTest.loadClosedIssues(file.uuid());
        assertThat(defaultIssues).hasSize(1);
        assertThat(defaultIssues.iterator().next().getLine()).isNull();
    }

    @Test
    public void loadClosedIssues_returns_only_closed_issues_with_close_date() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = dbTester.rules().insert(( t) -> t.setType(RuleType.CODE_SMELL));
        Date issueDate = addDays(ComponentIssuesLoaderTest.NOW, (-10));
        IssueDto closedIssue = dbTester.issues().insert(rule, project, file, ( t) -> t.setStatus(STATUS_CLOSED).setIssueCloseDate(issueDate).setIsFromHotspot(false));
        dbTester.issues().insertFieldDiffs(closedIssue, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(issueDate, 10));
        IssueDto issueNoCloseDate = dbTester.issues().insert(rule, project, file, ( t) -> t.setStatus(STATUS_CLOSED).setIsFromHotspot(false));
        dbTester.issues().insertFieldDiffs(issueNoCloseDate, ComponentIssuesLoaderTest.newToClosedDiffsWithLine(issueDate, 10));
        Mockito.when(system2.now()).thenReturn(ComponentIssuesLoaderTest.NOW.getTime());
        ComponentIssuesLoader underTest = newComponentIssuesLoader(ComponentIssuesLoaderTest.newEmptySettings());
        List<DefaultIssue> defaultIssues = underTest.loadClosedIssues(file.uuid());
        assertThat(defaultIssues).extracting(DefaultIssue::key).containsOnly(closedIssue.getKey());
    }

    @Test
    public void loadClosedIssues_returns_only_closed_issues_which_close_date_is_from_day_30_days_ago() {
        ComponentIssuesLoader underTest = newComponentIssuesLoader(ComponentIssuesLoaderTest.newEmptySettings());
        loadClosedIssues_returns_only_closed_issues_with_close_date_is_from_30_days_ago(underTest);
    }

    @Test
    public void loadClosedIssues_returns_only_closed_issues_with_close_date_is_from_30_days_ago_if_property_is_empty() {
        Configuration configuration = ComponentIssuesLoaderTest.newConfiguration(null);
        ComponentIssuesLoader underTest = newComponentIssuesLoader(configuration);
        loadClosedIssues_returns_only_closed_issues_with_close_date_is_from_30_days_ago(underTest);
    }

    @Test
    public void loadClosedIssues_returns_only_closed_with_close_date_is_from_30_days_ago_if_property_is_less_than_0() {
        Configuration configuration = ComponentIssuesLoaderTest.newConfiguration(String.valueOf((-(1 + (new Random().nextInt(10))))));
        ComponentIssuesLoader underTest = newComponentIssuesLoader(configuration);
        loadClosedIssues_returns_only_closed_issues_with_close_date_is_from_30_days_ago(underTest);
    }

    @Test
    public void loadClosedIssues_returns_only_closed_with_close_date_is_from_30_days_ago_if_property_is_30() {
        Configuration configuration = ComponentIssuesLoaderTest.newConfiguration("30");
        ComponentIssuesLoader underTest = newComponentIssuesLoader(configuration);
        loadClosedIssues_returns_only_closed_issues_with_close_date_is_from_30_days_ago(underTest);
    }

    @Test
    public void loadClosedIssues_returns_empty_without_querying_DB_if_property_is_0() {
        System2 system2 = Mockito.mock(System2.class);
        DbClient dbClient = Mockito.mock(DbClient.class);
        Configuration configuration = ComponentIssuesLoaderTest.newConfiguration("0");
        String componentUuid = randomAlphabetic(15);
        ComponentIssuesLoader underTest = /* not used in loadClosedIssues */
        /* not used in loadClosedIssues */
        new ComponentIssuesLoader(dbClient, null, null, configuration, system2);
        assertThat(underTest.loadClosedIssues(componentUuid)).isEmpty();
        Mockito.verifyZeroInteractions(dbClient, system2);
    }

    @Test
    public void loadLatestDiffChangesForReopeningOfClosedIssues_does_not_query_DB_if_issue_list_is_empty() {
        DbClient dbClient = Mockito.mock(DbClient.class);
        ComponentIssuesLoader underTest = /* not used in method */
        /* not used in method */
        /* not used by method */
        new ComponentIssuesLoader(dbClient, null, null, ComponentIssuesLoaderTest.newConfiguration("0"), null);
        underTest.loadLatestDiffChangesForReopeningOfClosedIssues(Collections.emptyList());
        Mockito.verifyZeroInteractions(dbClient, system2);
    }

    @Test
    public void loadLatestDiffChangesForReopeningOfClosedIssues_add_single_diff_change_when_most_recent_status_and_resolution_is_the_same_diff() {
        ComponentDto project = dbTester.components().insertPublicProject();
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = dbTester.rules().insert();
        IssueDto issue = dbTester.issues().insert(rule, project, file);
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus1")).setIssueChangeCreationDate(5));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus2")).setIssueChangeCreationDate(19));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus3", "resolution", "valRes3")).setIssueChangeCreationDate(20));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("resolution", "valRes4")).setIssueChangeCreationDate(13));
        ComponentIssuesLoader underTest = /* not used in method */
        /* not used in method */
        /* not used by method */
        new ComponentIssuesLoader(dbClient, null, null, ComponentIssuesLoaderTest.newConfiguration("0"), null);
        DefaultIssue defaultIssue = new DefaultIssue().setKey(issue.getKey());
        underTest.loadLatestDiffChangesForReopeningOfClosedIssues(ImmutableList.of(defaultIssue));
        assertThat(defaultIssue.changes()).hasSize(1);
        assertThat(defaultIssue.changes()).extracting(( t) -> t.get("status")).filteredOn(( t) -> hasValue(t, "valStatus3")).hasSize(1);
        assertThat(defaultIssue.changes()).extracting(( t) -> t.get("resolution")).filteredOn(( t) -> hasValue(t, "valRes3")).hasSize(1);
    }

    @Test
    public void loadLatestDiffChangesForReopeningOfClosedIssues_adds_2_diff_changes_if_most_recent_status_and_resolution_are_not_the_same_diff() {
        ComponentDto project = dbTester.components().insertPublicProject();
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = dbTester.rules().insert();
        IssueDto issue = dbTester.issues().insert(rule, project, file);
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus1")).setIssueChangeCreationDate(5));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus2", "resolution", "valRes2")).setIssueChangeCreationDate(19));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("status", "valStatus3")).setIssueChangeCreationDate(20));
        dbTester.issues().insertChange(issue, ( t) -> t.setChangeData(randomDiffWith("resolution", "valRes4")).setIssueChangeCreationDate(13));
        ComponentIssuesLoader underTest = /* not used in method */
        /* not used in method */
        /* not used by method */
        new ComponentIssuesLoader(dbClient, null, null, ComponentIssuesLoaderTest.newConfiguration("0"), null);
        DefaultIssue defaultIssue = new DefaultIssue().setKey(issue.getKey());
        underTest.loadLatestDiffChangesForReopeningOfClosedIssues(ImmutableList.of(defaultIssue));
        assertThat(defaultIssue.changes()).hasSize(2);
        assertThat(defaultIssue.changes()).extracting(( t) -> t.get("status")).filteredOn(( t) -> hasValue(t, "valStatus3")).hasSize(1);
        assertThat(defaultIssue.changes()).extracting(( t) -> t.get("resolution")).filteredOn(( t) -> hasValue(t, "valRes2")).hasSize(1);
    }

    private static final class Diff {
        private final String field;

        private final String oldValue;

        private final String newValue;

        private Diff(String field, @Nullable
        String oldValue, @Nullable
        String newValue) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}

