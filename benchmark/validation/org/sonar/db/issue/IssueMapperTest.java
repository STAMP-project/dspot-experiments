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
package org.sonar.db.issue;


import RuleType.SECURITY_HOTSPOT;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.api.rules.RuleType;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleDto;


@RunWith(DataProviderRunner.class)
public class IssueMapperTest {
    private static final long NO_FILTERING_ON_CLOSE_DATE = 1L;

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbSession dbSession = dbTester.getSession();

    private IssueMapper underTest = dbSession.getMapper(IssueMapper.class);

    private ComponentDto project;

    private ComponentDto file;

    private ComponentDto file2;

    private RuleDto rule;

    private Random random = new Random();

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Test
    public void insert() {
        underTest.insert(newIssue());
        dbTester.getSession().commit();
        IssueDto result = underTest.selectByKey("ABCDE");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getKey()).isEqualTo("ABCDE");
        assertThat(result.getComponentUuid()).isEqualTo(file.uuid());
        assertThat(result.getProjectUuid()).isEqualTo(project.uuid());
        assertThat(result.getRuleId()).isEqualTo(rule.getId());
        assertThat(result.getType()).isEqualTo(2);
        assertThat(result.getLine()).isEqualTo(500);
        assertThat(result.getGap()).isEqualTo(3.14);
        assertThat(result.getEffort()).isEqualTo(10L);
        assertThat(result.getResolution()).isEqualTo("FIXED");
        assertThat(result.getStatus()).isEqualTo("RESOLVED");
        assertThat(result.getSeverity()).isEqualTo("BLOCKER");
        assertThat(result.getAuthorLogin()).isEqualTo("morgan");
        assertThat(result.getAssigneeUuid()).isEqualTo("karadoc");
        assertThat(result.getIssueAttributes()).isEqualTo("JIRA=FOO-1234");
        assertThat(result.getChecksum()).isEqualTo("123456789");
        assertThat(result.getMessage()).isEqualTo("the message");
        assertThat(result.getIssueCreationTime()).isEqualTo(1401000000000L);
        assertThat(result.getIssueUpdateTime()).isEqualTo(1402000000000L);
        assertThat(result.getIssueCloseTime()).isEqualTo(1403000000000L);
        assertThat(result.getCreatedAt()).isEqualTo(1400000000000L);
        assertThat(result.getUpdatedAt()).isEqualTo(1500000000000L);
    }

    @Test
    public void update() {
        underTest.insert(newIssue());
        dbTester.getSession().commit();
        IssueDto update = new IssueDto();
        update.setKee("ABCDE");
        update.setComponentUuid("other component uuid");
        update.setProjectUuid(project.uuid());
        update.setRuleId(rule.getId());
        update.setType(3);
        update.setLine(500);
        update.setGap(3.14);
        update.setEffort(10L);
        update.setResolution("FIXED");
        update.setStatus("RESOLVED");
        update.setSeverity("BLOCKER");
        update.setAuthorLogin("morgan");
        update.setAssigneeUuid("karadoc");
        update.setIssueAttributes("JIRA=FOO-1234");
        update.setChecksum("123456789");
        update.setMessage("the message");
        update.setIssueCreationTime(1550000000000L);
        update.setIssueUpdateTime(1550000000000L);
        update.setIssueCloseTime(1550000000000L);
        // Should not change
        update.setCreatedAt(1400123456789L);
        update.setUpdatedAt(1550000000000L);
        underTest.update(update);
        dbTester.getSession().commit();
        IssueDto result = underTest.selectByKey("ABCDE");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getKey()).isEqualTo("ABCDE");
        assertThat(result.getComponentUuid()).isEqualTo(file.uuid());
        assertThat(result.getProjectUuid()).isEqualTo(project.uuid());
        assertThat(result.getRuleId()).isEqualTo(rule.getId());
        assertThat(result.getType()).isEqualTo(3);
        assertThat(result.getLine()).isEqualTo(500);
        assertThat(result.getGap()).isEqualTo(3.14);
        assertThat(result.getEffort()).isEqualTo(10L);
        assertThat(result.getResolution()).isEqualTo("FIXED");
        assertThat(result.getStatus()).isEqualTo("RESOLVED");
        assertThat(result.getSeverity()).isEqualTo("BLOCKER");
        assertThat(result.getAuthorLogin()).isEqualTo("morgan");
        assertThat(result.getAssigneeUuid()).isEqualTo("karadoc");
        assertThat(result.getIssueAttributes()).isEqualTo("JIRA=FOO-1234");
        assertThat(result.getChecksum()).isEqualTo("123456789");
        assertThat(result.getMessage()).isEqualTo("the message");
        assertThat(result.getIssueCreationTime()).isEqualTo(1550000000000L);
        assertThat(result.getIssueUpdateTime()).isEqualTo(1550000000000L);
        assertThat(result.getIssueCloseTime()).isEqualTo(1550000000000L);
        assertThat(result.getCreatedAt()).isEqualTo(1400000000000L);
        assertThat(result.getUpdatedAt()).isEqualTo(1550000000000L);
    }

    @Test
    public void updateBeforeSelectedDate_without_conflict() {
        underTest.insert(newIssue());
        IssueDto dto = newIssue().setComponentUuid(file2.uuid()).setType(3).setLine(600).setGap(1.12).setEffort(50L).setIssueUpdateTime(1600000000000L).setUpdatedAt(1600000000000L);
        // selected after last update -> ok
        dto.setSelectedAt(1500000000000L);
        int count = underTest.updateIfBeforeSelectedDate(dto);
        assertThat(count).isEqualTo(1);
        dbTester.getSession().commit();
        IssueDto result = underTest.selectByKey("ABCDE");
        assertThat(result).isNotNull();
        assertThat(result.getComponentUuid()).isEqualTo(file2.uuid());
        assertThat(result.getType()).isEqualTo(3);
        assertThat(result.getLine()).isEqualTo(600);
        assertThat(result.getGap()).isEqualTo(1.12);
        assertThat(result.getEffort()).isEqualTo(50L);
        assertThat(result.getIssueUpdateTime()).isEqualTo(1600000000000L);
        assertThat(result.getUpdatedAt()).isEqualTo(1600000000000L);
    }

    @Test
    public void updateBeforeSelectedDate_with_conflict() {
        underTest.insert(newIssue());
        IssueDto dto = newIssue().setComponentUuid(file2.uuid()).setType(3).setLine(600).setGap(1.12).setEffort(50L).setIssueUpdateTime(1600000000000L).setUpdatedAt(1600000000000L);
        // selected before last update -> ko
        dto.setSelectedAt(1400000000000L);
        int count = underTest.updateIfBeforeSelectedDate(dto);
        assertThat(count).isEqualTo(0);
        dbTester.getSession().commit();
        // No change
        IssueDto result = underTest.selectByKey("ABCDE");
        assertThat(result).isNotNull();
        assertThat(result.getComponentUuid()).isEqualTo(file.uuid());
        assertThat(result.getType()).isEqualTo(2);
        assertThat(result.getLine()).isEqualTo(500);
        assertThat(result.getGap()).isEqualTo(3.14);
        assertThat(result.getEffort()).isEqualTo(10L);
        assertThat(result.getIssueUpdateTime()).isEqualTo(1402000000000L);
        assertThat(result.getUpdatedAt()).isEqualTo(1500000000000L);
    }

    @Test
    public void scrollClosedByComponentUuid_returns_empty_when_no_issue_for_component() {
        String componentUuid = randomAlphabetic(10);
        IssueMapperTest.RecorderResultHandler resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(componentUuid, new Date().getTime(), resultHandler);
        assertThat(resultHandler.issues).isEmpty();
    }

    @Test
    public void scrollClosedByComponentUuid_does_not_return_closed_issues_of_type_SECURITY_HOTSPOT() {
        RuleType ruleType = IssueMapperTest.randomSupportedRuleType();
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto component = randomComponent(organization);
        IssueDto securityHotspotIssue = insertNewClosedIssue(component, SECURITY_HOTSPOT);
        insertToClosedDiff(securityHotspotIssue);
        IssueDto issue = insertNewClosedIssue(component, ruleType);
        IssueChangeDto issueChange = insertToClosedDiff(issue);
        IssueMapperTest.RecorderResultHandler resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), IssueMapperTest.NO_FILTERING_ON_CLOSE_DATE, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey, ( t) -> t.getClosedChangeData().get()).containsOnly(tuple(issue.getKey(), issueChange.getChangeData()));
    }

    @Test
    public void scrollClosedByComponentUuid_returns_closed_issues_without_isHotspot_flag() {
        RuleType ruleType = IssueMapperTest.randomSupportedRuleType();
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto component = randomComponent(organization);
        IssueDto noHotspotFlagIssue = insertNewClosedIssue(component, ruleType);
        IssueChangeDto noFlagIssueChange = insertToClosedDiff(noHotspotFlagIssue);
        manuallySetToNullFromHotpotsColumn(noHotspotFlagIssue);
        IssueDto issue = insertNewClosedIssue(component, ruleType);
        IssueChangeDto issueChange = insertToClosedDiff(issue);
        IssueMapperTest.RecorderResultHandler resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), IssueMapperTest.NO_FILTERING_ON_CLOSE_DATE, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey, ( t) -> t.getClosedChangeData().get()).containsOnly(tuple(issue.getKey(), issueChange.getChangeData()), tuple(noHotspotFlagIssue.getKey(), noFlagIssueChange.getChangeData()));
    }

    @Test
    public void scrollClosedByComponentUuid_does_not_return_closed_issues_without_close_date() {
        RuleType ruleType = IssueMapperTest.randomSupportedRuleType();
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto component = randomComponent(organization);
        IssueDto issueWithoutCloseDate = insertNewClosedIssue(component, ruleType, ( t) -> t.setIssueCloseDate(null));
        insertToClosedDiff(issueWithoutCloseDate);
        IssueDto issueCloseDate = insertNewClosedIssue(component, ruleType);
        IssueChangeDto changeDto = insertToClosedDiff(issueCloseDate);
        IssueMapperTest.RecorderResultHandler resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), IssueMapperTest.NO_FILTERING_ON_CLOSE_DATE, resultHandler);
        assertThat(resultHandler.issues).hasSize(1);
        IssueDto issue = resultHandler.issues.iterator().next();
        assertThat(issue.getKey()).isEqualTo(issue.getKey());
        assertThat(issue.getClosedChangeData()).contains(changeDto.getChangeData());
    }

    @Test
    public void scrollClosedByComponentUuid_returns_closed_issues_which_close_date_is_greater_or_equal_to_requested() {
        RuleType ruleType = IssueMapperTest.randomSupportedRuleType();
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto component = randomComponent(organization);
        RuleDefinitionDto rule1 = dbTester.rules().insert(( t) -> t.setType(ruleType));
        IssueDto[] issues = new IssueDto[]{ insertNewClosedIssue(component, rule1, 1999999L), insertNewClosedIssue(component, rule1, 3999999L), insertNewClosedIssue(component, rule1, 2999999L), insertNewClosedIssue(component, rule1, 10999999L) };
        Arrays.stream(issues).forEach(this::insertToClosedDiff);
        IssueMapperTest.RecorderResultHandler resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), 4000000L, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey).containsOnly(issues[3].getKey());
        resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), 11999999L, resultHandler);
        assertThat(resultHandler.issues).isEmpty();
        resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), 3999999L, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey).containsOnly(issues[3].getKey(), issues[1].getKey());
        resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), 2999999L, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey).containsOnly(issues[3].getKey(), issues[1].getKey(), issues[2].getKey());
        resultHandler = new IssueMapperTest.RecorderResultHandler();
        underTest.scrollClosedByComponentUuid(component.uuid(), 1L, resultHandler);
        assertThat(resultHandler.issues).extracting(IssueDto::getKey).containsOnly(issues[3].getKey(), issues[1].getKey(), issues[2].getKey(), issues[0].getKey());
    }

    private static final RuleType[] SUPPORTED_RULE_TYPES = Arrays.stream(RuleType.values()).filter(( t) -> t != RuleType.SECURITY_HOTSPOT).toArray(RuleType[]::new);

    private static class RecorderResultHandler implements ResultHandler<IssueDto> {
        private final List<IssueDto> issues = new ArrayList<>();

        @Override
        public void handleResult(ResultContext<? extends IssueDto> resultContext) {
            issues.add(resultContext.getResultObject());
        }

        public List<IssueDto> getIssues() {
            return issues;
        }
    }
}

