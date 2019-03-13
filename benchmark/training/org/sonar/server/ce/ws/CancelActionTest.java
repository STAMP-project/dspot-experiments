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
package org.sonar.server.ce.ws;


import CeActivityDto.Status.CANCELED;
import UserRole.ADMIN;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.ce.queue.CeQueue;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbTester;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class CancelActionTest {
    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private System2 system2 = new TestSystem2();

    private CeQueue queue = new org.sonar.ce.queue.CeQueueImpl(system2, db.getDbClient(), UuidFactoryFast.getInstance(), defaultOrganizationProvider);

    private CancelAction underTest = new CancelAction(userSession, db.getDbClient(), queue);

    private WsActionTester tester = new WsActionTester(underTest);

    @Test
    public void cancel_pending_task_on_project() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        CeQueueDto queue = createTaskSubmit(project);
        tester.newRequest().setParam("id", queue.getUuid()).execute();
        assertThat(db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), queue.getUuid()).get().getStatus()).isEqualTo(CANCELED);
    }

    @Test
    public void cancel_pending_task_having_no_component() {
        logInAsSystemAdministrator();
        CeQueueDto queue = createTaskSubmit(null);
        tester.newRequest().setParam("id", queue.getUuid()).execute();
        assertThat(db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), queue.getUuid()).get().getStatus()).isEqualTo(CANCELED);
    }

    @Test
    public void cancel_pending_task_when_system_administer() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        CeQueueDto queue = createTaskSubmit(project);
        tester.newRequest().setParam("id", queue.getUuid()).execute();
        assertThat(db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), queue.getUuid()).get().getStatus()).isEqualTo(CANCELED);
    }

    @Test
    public void cancel_pending_task_when_project_administer() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(ADMIN, project);
        CeQueueDto queue = createTaskSubmit(project);
        tester.newRequest().setParam("id", queue.getUuid()).execute();
        assertThat(db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), queue.getUuid()).get().getStatus()).isEqualTo(CANCELED);
    }

    @Test
    public void does_not_fail_on_unknown_task() {
        logInAsSystemAdministrator();
        tester.newRequest().setParam("id", "UNKNOWN").execute();
    }

    @Test
    public void throw_IllegalArgumentException_if_missing_id() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'id' parameter is missing");
        tester.newRequest().execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_enough_permission_when_canceling_task_on_project() {
        userSession.logIn().setNonSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        CeQueueDto queue = createTaskSubmit(project);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        tester.newRequest().setParam("id", queue.getUuid()).execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_enough_permission_when_canceling_task_without_project() {
        userSession.logIn().setNonSystemAdministrator();
        CeQueueDto queue = createTaskSubmit(null);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        tester.newRequest().setParam("id", queue.getUuid()).execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_enough_permission_when_canceling_task_when_project_does_not_exist() {
        userSession.logIn().setNonSystemAdministrator();
        CeQueueDto queue = createTaskSubmit(CancelActionTest.nonExistentComponentDot());
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        tester.newRequest().setParam("id", queue.getUuid()).execute();
    }
}

