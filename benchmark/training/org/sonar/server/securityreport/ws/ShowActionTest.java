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
package org.sonar.server.securityreport.ws;


import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_WONT_FIX;
import Issue.STATUS_RESOLVED;
import RuleType.BUG;
import RuleType.CODE_SMELL;
import RuleType.SECURITY_HOTSPOT;
import RuleType.VULNERABILITY;
import UserRole.USER;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.StartupIndexer;
import org.sonar.server.issue.index.IssueIndex;
import org.sonar.server.issue.index.IssueIndexer;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class ShowActionTest {
    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DbSession session = db.getSession();

    private IssueIndex issueIndex = new IssueIndex(es.client(), System2.INSTANCE, userSessionRule, new WebAuthorizationTypeSupport(userSessionRule));

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), dbClient, new org.sonar.server.issue.index.IssueIteratorFactory(dbClient));

    private WsActionTester ws = new WsActionTester(new ShowAction(userSessionRule, TestComponentFinder.from(db), issueIndex, dbClient));

    private StartupIndexer permissionIndexer = new org.sonar.server.permission.index.PermissionIndexer(dbClient, es.client(), issueIndexer);

    private UserDto user;

    private ComponentDto project;

    private QProfileDto qualityProfile;

    private RuleDefinitionDto rule1;

    private RuleDefinitionDto rule2;

    private RuleDefinitionDto rule3;

    private RuleDefinitionDto rule4;

    private MetricDto qpMetric;

    @Test
    public void test_definition() {
        WebService.Action def = ws.getDef();
        assertThat(def.key()).isEqualTo("show");
        assertThat(def.isInternal()).isTrue();
        assertThat(def.isPost()).isFalse();
        assertThat(def.since()).isEqualTo("7.3");
        assertThat(def.params()).extracting("key").containsExactlyInAnyOrder("standard", "project", "branch", "includeDistribution");
    }

    @Test
    public void owasp_empty() {
        userSessionRule.addProjectPermission(USER, project);
        indexPermissions();
        ComponentDto file = insertComponent(newFileDto(project, null, "FILE_ID").setDbKey("FILE_KEY"));
        IssueDto issue1 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(CODE_SMELL);
        IssueDto issue2 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(BUG);
        IssueDto issue3 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(CODE_SMELL);
        dbClient.issueDao().insert(session, issue1, issue2, issue3);
        session.commit();
        indexIssues();
        assertJson(ws.newRequest().setParam("standard", "owaspTop10").setParam("project", project.getKey()).execute().getInput()).withStrictArrayOrder().isSimilarTo(this.getClass().getResource("ShowActionTest/empty.json"));
    }

    @Test
    public void owasp_without_cwe() {
        userSessionRule.addProjectPermission(USER, project);
        indexPermissions();
        ComponentDto file = insertComponent(newFileDto(project, null, "FILE_ID").setDbKey("FILE_KEY"));
        IssueDto issue1 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(VULNERABILITY);
        IssueDto issue2 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue3 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue4 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        dbClient.issueDao().insert(session, issue1, issue2, issue3, issue4);
        session.commit();
        indexIssues();
        assertJson(ws.newRequest().setParam("standard", "owaspTop10").setParam("project", project.getKey()).execute().getInput()).withStrictArrayOrder().isSimilarTo(this.getClass().getResource("ShowActionTest/owaspNoCwe.json"));
    }

    @Test
    public void owasp_with_cwe__matching_json_example() {
        userSessionRule.addProjectPermission(USER, project);
        indexPermissions();
        ComponentDto file = insertComponent(newFileDto(project, null, "FILE_ID").setDbKey("FILE_KEY"));
        IssueDto issue1 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(VULNERABILITY);
        IssueDto issue2 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue3 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue4 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        dbClient.issueDao().insert(session, issue1, issue2, issue3, issue4);
        session.commit();
        indexIssues();
        assertJson(ws.newRequest().setParam("standard", "owaspTop10").setParam("project", project.getKey()).setParam("includeDistribution", "true").execute().getInput()).withStrictArrayOrder().isSimilarTo(getClass().getResource("show-example.json"));
    }

    @Test
    public void sans_with_cwe() {
        userSessionRule.addProjectPermission(USER, project);
        indexPermissions();
        ComponentDto file = insertComponent(newFileDto(project, null, "FILE_ID").setDbKey("FILE_KEY"));
        IssueDto issue1 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(VULNERABILITY);
        IssueDto issue2 = newIssue(rule1, project, file).setStatus("OPEN").setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue3 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue4 = newIssue(rule1, project, file).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        dbClient.issueDao().insert(session, issue1, issue2, issue3, issue4);
        session.commit();
        indexIssues();
        assertJson(ws.newRequest().setParam("standard", "sansTop25").setParam("project", project.getKey()).setParam("includeDistribution", "true").execute().getInput()).withStrictArrayOrder().isSimilarTo(this.getClass().getResource("ShowActionTest/sansWithCwe.json"));
    }

    @Test
    public void sans_with_cwe_for_branches() {
        ComponentDto project1 = db.components().insertPrivateProject(( p) -> p.setDbKey("prj1"));
        ComponentDto project1Branch1 = db.components().insertProjectBranch(project1);
        ComponentDto fileOnProject1Branch1 = db.components().insertComponent(newFileDto(project1Branch1));
        ComponentDto project2 = db.components().insertPrivateProject(( p) -> p.setDbKey("prj2"));
        insertQPLiveMeasure(project1);
        insertQPLiveMeasure(project1Branch1);
        insertQPLiveMeasure(project2);
        userSessionRule.addProjectPermission(USER, project1);
        userSessionRule.addProjectPermission(USER, project2);
        indexPermissions();
        IssueDto issue1 = newIssue(rule1, project1Branch1, fileOnProject1Branch1).setStatus("OPEN").setSeverity("MAJOR").setType(VULNERABILITY);
        IssueDto issue2 = newIssue(rule1, project1Branch1, fileOnProject1Branch1).setStatus("OPEN").setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue3 = newIssue(rule1, project1Branch1, fileOnProject1Branch1).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        IssueDto issue4 = newIssue(rule1, project1Branch1, fileOnProject1Branch1).setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX).setSeverity("MAJOR").setType(SECURITY_HOTSPOT);
        dbClient.issueDao().insert(session, issue1, issue2, issue3, issue4);
        session.commit();
        indexIssues();
        assertJson(ws.newRequest().setParam("standard", "sansTop25").setParam("project", project1Branch1.getKey()).setParam("branch", project1Branch1.getBranch()).setParam("includeDistribution", "true").execute().getInput()).withStrictArrayOrder().isSimilarTo(this.getClass().getResource("ShowActionTest/sansWithCwe.json"));
    }
}

