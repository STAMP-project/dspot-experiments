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
package org.sonar.server.source.ws;


import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.source.HtmlSourceDecorator;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class IndexActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    WsActionTester tester = new WsActionTester(new IndexAction(db.getDbClient(), new org.sonar.server.source.SourceService(db.getDbClient(), new HtmlSourceDecorator()), userSession, TestComponentFinder.from(db)));

    @Test
    public void get_json() throws Exception {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(UserRole.CODEVIEWER, project);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        insertFileWithData(file, IndexActionTest.newData("public class HelloWorld {", "}"));
        TestResponse request = tester.newRequest().setParam("resource", file.getDbKey()).execute();
        assertJson(request.getInput()).isSimilarTo(("[\n" + (((("  {\n" + "    \"1\": \"public class HelloWorld {\",\n") + "    \"2\": \"}\"\n") + "  }\n") + "]")));
    }

    @Test
    public void limit_range() throws Exception {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(UserRole.CODEVIEWER, project);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        insertFileWithData(file, IndexActionTest.newData("/**", " */", "public class HelloWorld {", "}", "", "foo"));
        TestResponse request = tester.newRequest().setParam("resource", file.getDbKey()).setParam("from", "3").setParam("to", "5").execute();
        assertJson(request.getInput()).isSimilarTo(("[\n" + (((("  {\n" + "    \"3\": \"public class HelloWorld {\",\n") + "    \"4\": \"}\"\n") + "  }\n") + "]")));
    }

    @Test
    public void fail_when_missing_code_viewer_permission() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(UserRole.USER, project);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        expectedException.expect(ForbiddenException.class);
        tester.newRequest().setParam("resource", file.getDbKey()).execute();
    }

    @Test
    public void fail_when_component_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        tester.newRequest().setParam("resource", "unknown").execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        userSession.addProjectPermission(UserRole.USER, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        tester.newRequest().setParam("resource", branch.getDbKey()).execute();
    }
}

