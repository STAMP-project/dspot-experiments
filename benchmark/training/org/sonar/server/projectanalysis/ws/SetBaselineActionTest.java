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
package org.sonar.server.projectanalysis.ws;


import BranchType.LONG;
import BranchType.SHORT;
import System2.INSTANCE;
import WebService.Action;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


@RunWith(DataProviderRunner.class)
public class SetBaselineActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private WsActionTester ws = new WsActionTester(new SetBaselineAction(dbClient, userSession, TestComponentFinder.from(db)));

    @Test
    public void set_baseline_on_long_living_branch() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = ComponentTesting.newBranchDto(project.projectUuid(), LONG);
        db.components().insertProjectBranch(project, branch);
        ComponentDto branchComponentDto = ComponentTesting.newProjectBranch(project, branch);
        SnapshotDto analysis = db.components().insertSnapshot(branchComponentDto);
        logInAsProjectAdministrator(project);
        call(project.getKey(), branch.getKey(), analysis.getUuid());
        BranchDto loaded = dbClient.branchDao().selectByUuid(dbSession, branch.getUuid()).get();
        assertThat(loaded.getManualBaseline()).isEqualTo(analysis.getUuid());
    }

    @Test
    public void fail_when_user_is_not_admin() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = ComponentTesting.newBranchDto(project.projectUuid(), LONG);
        db.components().insertProjectBranch(project, branch);
        ComponentDto branchComponentDto = ComponentTesting.newProjectBranch(project, branch);
        SnapshotDto analysis = db.components().insertSnapshot(branchComponentDto);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        call(project.getKey(), branch.getKey(), analysis.getUuid());
    }

    @Test
    public void fail_when_branch_does_not_belong_to_project() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = ComponentTesting.newBranchDto(project.projectUuid(), LONG);
        db.components().insertProjectBranch(project, branch);
        ComponentDto branchComponentDto = ComponentTesting.newProjectBranch(project, branch);
        SnapshotDto analysis = db.components().insertSnapshot(branchComponentDto);
        logInAsProjectAdministrator(project);
        ComponentDto otherProject = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto otherBranch = ComponentTesting.newBranchDto(otherProject.projectUuid(), LONG);
        db.components().insertProjectBranch(otherProject, otherBranch);
        ComponentTesting.newProjectBranch(otherProject, otherBranch);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component '%s' on branch '%s' not found", project.getKey(), otherBranch.getKey()));
        call(project.getKey(), otherBranch.getKey(), analysis.getUuid());
    }

    @Test
    public void fail_when_analysis_does_not_belong_to_main_branch_of_project() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = new BranchDto().setBranchType(LONG).setProjectUuid(project.uuid()).setUuid(project.uuid()).setKey("master");
        db.components().insertComponent(project);
        db.getDbClient().branchDao().insert(dbSession, branch);
        logInAsProjectAdministrator(project);
        ComponentDto otherProject = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        SnapshotDto otherAnalysis = db.components().insertSnapshot(otherProject);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Analysis '%s' does not belong to project '%s'", otherAnalysis.getUuid(), project.getKey()));
        call(ImmutableMap.of(ProjectAnalysesWsParameters.PARAM_PROJECT, project.getKey(), ProjectAnalysesWsParameters.PARAM_ANALYSIS, otherAnalysis.getUuid()));
    }

    @Test
    public void fail_when_analysis_does_not_belong_to_non_main_branch_of_project() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = ComponentTesting.newBranchDto(project.projectUuid(), LONG);
        db.components().insertProjectBranch(project, branch);
        logInAsProjectAdministrator(project);
        ComponentDto otherProject = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        SnapshotDto otherAnalysis = db.components().insertProjectAndSnapshot(otherProject);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Analysis '%s' does not belong to branch '%s' of project '%s'", otherAnalysis.getUuid(), branch.getKey(), project.getKey()));
        call(project.getKey(), branch.getKey(), otherAnalysis.getUuid());
    }

    @Test
    public void fail_when_branch_is_not_long() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        BranchDto branch = ComponentTesting.newBranchDto(project.projectUuid(), SHORT);
        db.components().insertProjectBranch(project, branch);
        ComponentDto branchComponentDto = ComponentTesting.newProjectBranch(project, branch);
        SnapshotDto analysis = db.components().insertSnapshot(branchComponentDto);
        logInAsProjectAdministrator(project);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Not a long-living branch: '%s'", branch.getKey()));
        call(project.getKey(), branch.getKey(), analysis.getUuid());
    }

    @Test
    public void ws_parameters() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.key()).isEqualTo("set_baseline");
        assertThat(definition.since()).isEqualTo("7.7");
        assertThat(definition.isInternal()).isTrue();
    }

    private static class MapBuilder {
        private final Map<String, String> map;

        private MapBuilder() {
            this.map = Collections.emptyMap();
        }

        private MapBuilder(Map<String, String> map) {
            this.map = map;
        }

        public SetBaselineActionTest.MapBuilder put(String key, @Nullable
        String value) {
            Map<String, String> copy = new HashMap<>(map);
            if (value == null) {
                copy.remove(key);
            } else {
                copy.put(key, value);
            }
            return new SetBaselineActionTest.MapBuilder(copy);
        }
    }
}

