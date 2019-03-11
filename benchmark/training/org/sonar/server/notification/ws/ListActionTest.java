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
package org.sonar.server.notification.ws;


import WebService.Action;
import WebService.Param;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.notification.NotificationDispatcherMetadata;
import org.sonar.server.notification.NotificationUpdater;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Notifications.ListResponse;


public class ListActionTest {
    private static final String NOTIF_MY_NEW_ISSUES = "MyNewIssues";

    private static final String NOTIF_NEW_ISSUES = "NewIssues";

    private static final String NOTIF_NEW_QUALITY_GATE_STATUS = "NewQualityGateStatus";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private NotificationChannel emailChannel = new ListActionTest.FakeNotificationChannel("EmailChannel");

    private NotificationChannel twitterChannel = new ListActionTest.FakeNotificationChannel("TwitterChannel");

    private NotificationUpdater notificationUpdater = new NotificationUpdater(dbClient);

    private Dispatchers dispatchers = Mockito.mock(Dispatchers.class);

    private WsActionTester ws = new WsActionTester(new ListAction(new org.sonar.server.notification.NotificationCenter(new NotificationDispatcherMetadata[]{  }, new NotificationChannel[]{ emailChannel, twitterChannel }), dbClient, userSession, dispatchers));

    @Test
    public void channels() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        ListResponse result = call();
        assertThat(result.getChannelsList()).containsExactly(emailChannel.getKey(), twitterChannel.getKey());
    }

    @Test
    public void overall_dispatchers() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        ListResponse result = call();
        assertThat(result.getGlobalTypesList()).containsExactly(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS);
    }

    @Test
    public void per_project_dispatchers() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        ListResponse result = call();
        assertThat(result.getPerProjectTypesList()).containsExactly(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS);
    }

    @Test
    public void filter_unauthorized_projects() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(ListActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(ListActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertPrivateProject();
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        ComponentDto anotherProject = db.components().insertPrivateProject();
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, anotherProject);
        dbSession.commit();
        ListResponse result = call();
        assertThat(result.getNotificationsList()).extracting(Notification::getProject).containsOnly(project.getKey());
    }

    @Test
    public void filter_channels() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, "Unknown Channel", ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        ListResponse result = call();
        assertThat(result.getNotificationsList()).extracting(Notification::getChannel).containsOnly(emailChannel.getKey());
    }

    @Test
    public void filter_overall_dispatchers() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), "Unknown Notification", user, null);
        dbSession.commit();
        ListResponse result = call();
        assertThat(result.getNotificationsList()).extracting(Notification::getType).containsOnly(ListActionTest.NOTIF_MY_NEW_ISSUES);
    }

    @Test
    public void filter_per_project_dispatchers() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(ListActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertPrivateProject();
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), "Unknown Notification", user, project);
        dbSession.commit();
        ListResponse result = call();
        assertThat(result.getNotificationsList()).extracting(Notification::getType).containsOnly(ListActionTest.NOTIF_MY_NEW_ISSUES);
    }

    @Test
    public void order_with_global_then_by_channel_and_dispatcher() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        notificationUpdater.add(dbSession, twitterChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, twitterChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS, user, project);
        dbSession.commit();
        ListResponse result = call();
        assertThat(result.getNotificationsList()).extracting(Notification::getChannel, Notification::getOrganization, Notification::getType, Notification::getProject).containsExactly(tuple(emailChannel.getKey(), "", ListActionTest.NOTIF_MY_NEW_ISSUES, ""), tuple(emailChannel.getKey(), "", ListActionTest.NOTIF_NEW_ISSUES, ""), tuple(twitterChannel.getKey(), "", ListActionTest.NOTIF_MY_NEW_ISSUES, ""), tuple(emailChannel.getKey(), organization.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, project.getKey()), tuple(emailChannel.getKey(), organization.getKey(), ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS, project.getKey()), tuple(twitterChannel.getKey(), organization.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, project.getKey()));
    }

    @Test
    public void list_user_notifications_as_system_admin() {
        UserDto user = db.users().insertUser();
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        userSession.logIn(user).setSystemAdministrator();
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_NEW_ISSUES, user, null);
        dbSession.commit();
        ListResponse result = call(user.getLogin());
        assertThat(result.getNotificationsList()).extracting(Notification::getType).containsOnly(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES);
    }

    @Test
    public void fail_if_login_and_not_system_admin() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setNonSystemAdministrator();
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(ListActionTest.NOTIF_MY_NEW_ISSUES));
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        expectedException.expect(ForbiddenException.class);
        call(user.getLogin());
    }

    @Test
    public void fail_if_login_is_provided_and_unknown() {
        userSession.logIn().setSystemAdministrator();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User 'LOGIN 404' not found");
        call("LOGIN 404");
    }

    @Test
    public void json_example() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Arrays.asList(ListActionTest.NOTIF_MY_NEW_ISSUES, ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        OrganizationDto organization = db.organizations().insertForKey("my-org-1");
        ComponentDto project = db.components().insertPrivateProject(organization, ( p) -> p.setDbKey(KEY_PROJECT_EXAMPLE_001).setName("My Project"));
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        notificationUpdater.add(dbSession, twitterChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_NEW_ISSUES, user, null);
        notificationUpdater.add(dbSession, twitterChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        notificationUpdater.add(dbSession, emailChannel.getKey(), ListActionTest.NOTIF_NEW_QUALITY_GATE_STATUS, user, project);
        dbSession.commit();
        String result = ws.newRequest().execute().getInput();
        assertJson(ws.getDef().responseExampleAsString()).withStrictArrayOrder().isSimilarTo(result);
    }

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("list");
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.since()).isEqualTo("6.3");
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.params()).hasSize(1);
        WebService.Param loginParam = definition.param("login");
        assertThat(loginParam.since()).isEqualTo("6.4");
        assertThat(loginParam.isRequired()).isFalse();
    }

    @Test
    public void fail_when_not_authenticated() {
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        call();
    }

    private static class FakeNotificationChannel extends NotificationChannel {
        private final String key;

        private FakeNotificationChannel(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public boolean deliver(Notification notification, String username) {
            // do nothing
            return true;
        }
    }
}

