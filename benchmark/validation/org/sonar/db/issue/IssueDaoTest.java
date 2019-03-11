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


import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.rules.RuleType;
import org.sonar.db.DbTester;
import org.sonar.db.RowNotFoundException;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleDto;
import org.sonar.db.rule.RuleTesting;


public class IssueDaoTest {
    private static final String PROJECT_UUID = "prj_uuid";

    private static final String PROJECT_KEY = "prj_key";

    private static final String FILE_UUID = "file_uuid";

    private static final String FILE_KEY = "file_key";

    private static final RuleDto RULE = RuleTesting.newXooX1();

    private static final String ISSUE_KEY1 = "I1";

    private static final String ISSUE_KEY2 = "I2";

    private static final RuleType[] RULE_TYPES_EXCEPT_HOTSPOT = Stream.of(RuleType.values()).filter(( r) -> r != RuleType.SECURITY_HOTSPOT).toArray(RuleType[]::new);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private IssueDao underTest = db.getDbClient().issueDao();

    @Test
    public void selectByKeyOrFail() {
        prepareTables();
        IssueDto issue = underTest.selectOrFailByKey(db.getSession(), IssueDaoTest.ISSUE_KEY1);
        assertThat(issue.getKee()).isEqualTo(IssueDaoTest.ISSUE_KEY1);
        assertThat(issue.getId()).isGreaterThan(0L);
        assertThat(issue.getComponentUuid()).isEqualTo(IssueDaoTest.FILE_UUID);
        assertThat(issue.getProjectUuid()).isEqualTo(IssueDaoTest.PROJECT_UUID);
        assertThat(issue.getRuleId()).isEqualTo(IssueDaoTest.RULE.getId());
        assertThat(issue.getLanguage()).isEqualTo(IssueDaoTest.RULE.getLanguage());
        assertThat(issue.getSeverity()).isEqualTo("BLOCKER");
        assertThat(issue.getType()).isEqualTo(2);
        assertThat(issue.isManualSeverity()).isFalse();
        assertThat(issue.getMessage()).isEqualTo("the message");
        assertThat(issue.getLine()).isEqualTo(500);
        assertThat(issue.getEffort()).isEqualTo(10L);
        assertThat(issue.getGap()).isEqualTo(3.14);
        assertThat(issue.getStatus()).isEqualTo("RESOLVED");
        assertThat(issue.getResolution()).isEqualTo("FIXED");
        assertThat(issue.getChecksum()).isEqualTo("123456789");
        assertThat(issue.getAuthorLogin()).isEqualTo("morgan");
        assertThat(issue.getAssigneeUuid()).isEqualTo("karadoc");
        assertThat(issue.getIssueAttributes()).isEqualTo("JIRA=FOO-1234");
        assertThat(issue.getIssueCreationDate()).isNotNull();
        assertThat(issue.getIssueUpdateDate()).isNotNull();
        assertThat(issue.getIssueCloseDate()).isNotNull();
        assertThat(issue.getCreatedAt()).isEqualTo(1440000000000L);
        assertThat(issue.getUpdatedAt()).isEqualTo(1440000000000L);
        assertThat(issue.getRuleRepo()).isEqualTo(IssueDaoTest.RULE.getRepositoryKey());
        assertThat(issue.getRule()).isEqualTo(IssueDaoTest.RULE.getRuleKey());
        assertThat(issue.getComponentKey()).isEqualTo(IssueDaoTest.FILE_KEY);
        assertThat(issue.getProjectKey()).isEqualTo(IssueDaoTest.PROJECT_KEY);
        assertThat(issue.getLocations()).isNull();
        assertThat(issue.parseLocations()).isNull();
        assertThat(issue.isExternal()).isTrue();
    }

    @Test
    public void selectByKeyOrFail_fails_if_key_not_found() {
        expectedException.expect(RowNotFoundException.class);
        expectedException.expectMessage("Issue with key 'DOES_NOT_EXIST' does not exist");
        prepareTables();
        underTest.selectOrFailByKey(db.getSession(), "DOES_NOT_EXIST");
    }

    @Test
    public void selectByKeys() {
        // contains I1 and I2
        prepareTables();
        List<IssueDto> issues = underTest.selectByKeys(db.getSession(), Arrays.asList("I1", "I2", "I3"));
        // results are not ordered, so do not use "containsExactly"
        assertThat(issues).extracting("key").containsOnly("I1", "I2");
    }

    @Test
    public void scrollNonClosedByComponentUuid() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        IssueDto openIssue1OnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssue2OnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto closedIssueOnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("CLOSED").setResolution("FIXED").setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssueOnProject = db.issues().insert(rule, project, project, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto securityHotspot = db.issues().insert(rule, project, file, ( i) -> i.setType(RuleType.SECURITY_HOTSPOT));
        IssueDto manualVulnerability = db.issues().insert(rule, project, file, ( i) -> i.setType(RuleType.VULNERABILITY).setIsFromHotspot(true));
        RuleDefinitionDto external = db.rules().insert(( ruleDefinitionDto) -> ruleDefinitionDto.setIsExternal(true));
        IssueDto issueFromExteralruleOnFile = db.issues().insert(external, project, file, ( i) -> i.setKee("ON_FILE_FROM_EXTERNAL").setType(randomRuleTypeExceptHotspot()));
        assertThat(underTest.selectNonClosedByComponentUuidExcludingExternalsAndSecurityHotspots(db.getSession(), file.uuid())).extracting(IssueDto::getKey).containsExactlyInAnyOrder(Arrays.stream(new IssueDto[]{ openIssue1OnFile, openIssue2OnFile }).map(IssueDto::getKey).toArray(String[]::new));
        assertThat(underTest.selectNonClosedByComponentUuidExcludingExternalsAndSecurityHotspots(db.getSession(), project.uuid())).extracting(IssueDto::getKey).containsExactlyInAnyOrder(Arrays.stream(new IssueDto[]{ openIssueOnProject }).map(IssueDto::getKey).toArray(String[]::new));
        assertThat(underTest.selectNonClosedByComponentUuidExcludingExternalsAndSecurityHotspots(db.getSession(), "does_not_exist")).isEmpty();
    }

    @Test
    public void scrollNonClosedByModuleOrProject() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto anotherProject = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(module));
        IssueDto openIssue1OnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssue2OnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto closedIssueOnFile = db.issues().insert(rule, project, file, ( i) -> i.setStatus("CLOSED").setResolution("FIXED").setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssueOnModule = db.issues().insert(rule, project, module, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssueOnProject = db.issues().insert(rule, project, project, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto openIssueOnAnotherProject = db.issues().insert(rule, anotherProject, anotherProject, ( i) -> i.setStatus("OPEN").setResolution(null).setType(randomRuleTypeExceptHotspot()));
        IssueDto securityHotspot = db.issues().insert(rule, project, file, ( i) -> i.setType(RuleType.SECURITY_HOTSPOT));
        IssueDto manualVulnerability = db.issues().insert(rule, project, file, ( i) -> i.setType(RuleType.VULNERABILITY).setIsFromHotspot(true));
        RuleDefinitionDto external = db.rules().insert(( ruleDefinitionDto) -> ruleDefinitionDto.setIsExternal(true));
        IssueDto issueFromExteralruleOnFile = db.issues().insert(external, project, file, ( i) -> i.setKee("ON_FILE_FROM_EXTERNAL").setType(randomRuleTypeExceptHotspot()));
        assertThat(underTest.selectNonClosedByModuleOrProjectExcludingExternalsAndSecurityHotspots(db.getSession(), project)).extracting(IssueDto::getKey).containsExactlyInAnyOrder(Arrays.stream(new IssueDto[]{ openIssue1OnFile, openIssue2OnFile, openIssueOnModule, openIssueOnProject }).map(IssueDto::getKey).toArray(String[]::new));
        assertThat(underTest.selectNonClosedByModuleOrProjectExcludingExternalsAndSecurityHotspots(db.getSession(), module)).extracting(IssueDto::getKey).containsExactlyInAnyOrder(Arrays.stream(new IssueDto[]{ openIssue1OnFile, openIssue2OnFile, openIssueOnModule }).map(IssueDto::getKey).toArray(String[]::new));
        ComponentDto notPersisted = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization());
        assertThat(underTest.selectNonClosedByModuleOrProjectExcludingExternalsAndSecurityHotspots(db.getSession(), notPersisted)).isEmpty();
    }

    @Test
    public void selectOpenByComponentUuid() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto projectBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo").setBranchType(BranchType.SHORT));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(projectBranch));
        IssueDto openIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_OPEN).setResolution(null));
        IssueDto closedIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_CLOSED).setResolution(Issue.RESOLUTION_FIXED));
        IssueDto reopenedIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_REOPENED).setResolution(null));
        IssueDto confirmedIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_CONFIRMED).setResolution(null));
        IssueDto wontfixIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_RESOLVED).setResolution(Issue.RESOLUTION_WONT_FIX));
        IssueDto fpIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus(Issue.STATUS_RESOLVED).setResolution(Issue.RESOLUTION_FALSE_POSITIVE));
        assertThat(underTest.selectOpenByComponentUuids(db.getSession(), Collections.singletonList(file.uuid()))).extracting("kee").containsOnly(openIssue.getKey(), reopenedIssue.getKey(), confirmedIssue.getKey(), wontfixIssue.getKey(), fpIssue.getKey());
    }

    @Test
    public void selectOpenByComponentUuid_should_correctly_map_required_fields() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto projectBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo").setBranchType(BranchType.SHORT));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(projectBranch));
        IssueDto fpIssue = db.issues().insert(rule, projectBranch, file, ( i) -> i.setStatus("RESOLVED").setResolution("FALSE-POSITIVE"));
        ShortBranchIssueDto fp = underTest.selectOpenByComponentUuids(db.getSession(), Collections.singletonList(file.uuid())).get(0);
        assertThat(fp.getLine()).isEqualTo(fpIssue.getLine());
        assertThat(fp.getMessage()).isEqualTo(fpIssue.getMessage());
        assertThat(fp.getChecksum()).isEqualTo(fpIssue.getChecksum());
        assertThat(fp.getRuleKey()).isEqualTo(fpIssue.getRuleKey());
        assertThat(fp.getStatus()).isEqualTo(fpIssue.getStatus());
        assertThat(fp.getLine()).isNotNull();
        assertThat(fp.getLine()).isNotZero();
        assertThat(fp.getMessage()).isNotNull();
        assertThat(fp.getChecksum()).isNotNull();
        assertThat(fp.getChecksum()).isNotEmpty();
        assertThat(fp.getRuleKey()).isNotNull();
        assertThat(fp.getStatus()).isNotNull();
        assertThat(fp.getBranchName()).isEqualTo("feature/foo");
        assertThat(fp.getIssueCreationDate()).isNotNull();
    }

    @Test
    public void test_selectGroupsOfComponentTreeOnLeak_on_component_without_issues() {
        ComponentDto project = db.components().insertPublicProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        Collection<IssueGroupDto> groups = underTest.selectIssueGroupsByBaseComponent(db.getSession(), file, 1000L);
        assertThat(groups).isEmpty();
    }

    @Test
    public void selectGroupsOfComponentTreeOnLeak_on_file() {
        ComponentDto project = db.components().insertPublicProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        RuleDefinitionDto rule = db.rules().insert();
        IssueDto fpBug = db.issues().insert(rule, project, file, ( i) -> i.setStatus("RESOLVED").setResolution("FALSE-POSITIVE").setSeverity("MAJOR").setType(RuleType.BUG).setIssueCreationTime(1500L));
        IssueDto criticalBug1 = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setSeverity("CRITICAL").setType(RuleType.BUG).setIssueCreationTime(1600L));
        IssueDto criticalBug2 = db.issues().insert(rule, project, file, ( i) -> i.setStatus("OPEN").setResolution(null).setSeverity("CRITICAL").setType(RuleType.BUG).setIssueCreationTime(1700L));
        // closed issues are ignored
        IssueDto closed = db.issues().insert(rule, project, file, ( i) -> i.setStatus("CLOSED").setResolution("REMOVED").setSeverity("CRITICAL").setType(RuleType.BUG).setIssueCreationTime(1700L));
        Collection<IssueGroupDto> result = underTest.selectIssueGroupsByBaseComponent(db.getSession(), file, 1000L);
        assertThat(result.stream().mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(3);
        assertThat(result.stream().filter(( g) -> (g.getRuleType()) == (RuleType.BUG.getDbConstant())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(3);
        assertThat(result.stream().filter(( g) -> (g.getRuleType()) == (RuleType.CODE_SMELL.getDbConstant())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        assertThat(result.stream().filter(( g) -> (g.getRuleType()) == (RuleType.VULNERABILITY.getDbConstant())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        assertThat(result.stream().filter(( g) -> g.getSeverity().equals("CRITICAL")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(2);
        assertThat(result.stream().filter(( g) -> g.getSeverity().equals("MAJOR")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(1);
        assertThat(result.stream().filter(( g) -> g.getSeverity().equals("MINOR")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        assertThat(result.stream().filter(( g) -> g.getStatus().equals("OPEN")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(2);
        assertThat(result.stream().filter(( g) -> g.getStatus().equals("RESOLVED")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(1);
        assertThat(result.stream().filter(( g) -> g.getStatus().equals("CLOSED")).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        assertThat(result.stream().filter(( g) -> "FALSE-POSITIVE".equals(g.getResolution())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(1);
        assertThat(result.stream().filter(( g) -> (g.getResolution()) == null).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(2);
        assertThat(result.stream().filter(( g) -> g.isInLeak()).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(3);
        assertThat(result.stream().filter(( g) -> !(g.isInLeak())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        // test leak
        result = underTest.selectIssueGroupsByBaseComponent(db.getSession(), file, 999999999L);
        assertThat(result.stream().filter(( g) -> g.isInLeak()).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(0);
        assertThat(result.stream().filter(( g) -> !(g.isInLeak())).mapToLong(IssueGroupDto::getCount).sum()).isEqualTo(3);
    }
}

