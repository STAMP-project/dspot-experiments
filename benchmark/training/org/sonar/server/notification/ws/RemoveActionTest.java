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


import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.notification.NotificationDispatcherMetadata;
import org.sonar.server.notification.NotificationUpdater;
import org.sonar.server.notification.ws.RemoveAction.RemoveRequest;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class RemoveActionTest {
    private static final String NOTIF_MY_NEW_ISSUES = "Dispatcher1";

    private static final String NOTIF_NEW_ISSUES = "Dispatcher2";

    private static final String NOTIF_NEW_QUALITY_GATE_STATUS = "Dispatcher3";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private NotificationChannel emailChannel = new RemoveActionTest.FakeNotificationChannel("EmailChannel");

    private NotificationChannel twitterChannel = new RemoveActionTest.FakeNotificationChannel("TwitterChannel");

    // default channel, based on class simple name
    private NotificationChannel defaultChannel = new RemoveActionTest.FakeNotificationChannel("EmailNotificationChannel");

    private NotificationUpdater notificationUpdater = new NotificationUpdater(dbClient);

    private Dispatchers dispatchers = Mockito.mock(Dispatchers.class);

    private RemoveRequest request = new RemoveRequest().setType(RemoveActionTest.NOTIF_MY_NEW_ISSUES);

    private WsActionTester ws = new WsActionTester(new RemoveAction(new org.sonar.server.notification.NotificationCenter(new NotificationDispatcherMetadata[]{  }, new NotificationChannel[]{ emailChannel, twitterChannel, defaultChannel }), notificationUpdater, dispatchers, dbClient, TestComponentFinder.from(db), userSession));

    @Test
    public void remove_to_email_channel_by_default() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        call(request);
        db.notifications().assertDoesNotExist(defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user.getId(), null);
    }

    @Test
    public void remove_from_a_specific_channel() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        notificationUpdater.add(dbSession, twitterChannel.getKey(), RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS, user, null);
        dbSession.commit();
        call(request.setType(RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS).setChannel(twitterChannel.getKey()));
        db.notifications().assertDoesNotExist(twitterChannel.getKey(), RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS, user.getId(), null);
    }

    @Test
    public void remove_a_project_notification() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertPrivateProject();
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        dbSession.commit();
        call(request.setProject(project.getDbKey()));
        db.notifications().assertDoesNotExist(defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user.getId(), project);
    }

    @Test
    public void fail_when_remove_a_global_notification_when_a_project_one_exists() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertPrivateProject();
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, project);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Notification doesn't exist");
        call(request);
    }

    @Test
    public void fail_when_remove_a_project_notification_when_a_global_one_exists() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertPrivateProject();
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Notification doesn't exist");
        call(request.setProject(project.getDbKey()));
    }

    @Test
    public void http_no_content() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        TestResponse result = call(request);
        assertThat(result.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Test
    public void remove_a_notification_from_a_user_as_system_administrator() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        db.notifications().assertExists(defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user.getId(), null);
        userSession.logIn().setSystemAdministrator();
        dbSession.commit();
        call(request.setLogin(user.getLogin()));
        db.notifications().assertDoesNotExist(defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user.getId(), null);
    }

    @Test
    public void fail_if_login_is_provided_and_unknown() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User 'LOGIN 404' not found");
        call(request.setLogin("LOGIN 404"));
    }

    @Test
    public void fail_if_login_provided_and_not_system_administrator() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setNonSystemAdministrator();
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        notificationUpdater.add(dbSession, defaultChannel.getKey(), RemoveActionTest.NOTIF_MY_NEW_ISSUES, user, null);
        dbSession.commit();
        expectedException.expect(ForbiddenException.class);
        call(request.setLogin(user.getLogin()));
    }

    @Test
    public void fail_when_notification_does_not_exist() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Notification doesn't exist");
        call(request);
    }

    @Test
    public void fail_when_unknown_channel() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(IllegalArgumentException.class);
        call(request.setChannel("Channel42"));
    }

    @Test
    public void fail_when_unknown_global_dispatcher() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Arrays.asList(RemoveActionTest.NOTIF_MY_NEW_ISSUES, RemoveActionTest.NOTIF_NEW_ISSUES, RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Value of parameter 'type' (Dispatcher42) must be one of: [Dispatcher1, Dispatcher2, Dispatcher3]");
        call(request.setType("Dispatcher42"));
    }

    @Test
    public void fail_when_unknown_project_dispatcher() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Arrays.asList(RemoveActionTest.NOTIF_MY_NEW_ISSUES, RemoveActionTest.NOTIF_NEW_QUALITY_GATE_STATUS));
        ComponentDto project = db.components().insertPrivateProject();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Value of parameter 'type' (Dispatcher42) must be one of: [Dispatcher1, Dispatcher3]");
        call(request.setType("Dispatcher42").setProject(project.getDbKey()));
    }

    @Test
    public void fail_when_no_type_parameter() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'type' parameter is missing");
        ws.newRequest().execute();
    }

    @Test
    public void fail_when_project_is_unknown() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(NotFoundException.class);
        call(request.setProject("Project-42"));
    }

    @Test
    public void fail_when_component_is_not_a_project() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        db.components().insertViewAndSnapshot(newView(db.organizations().insert()).setDbKey("VIEW_1"));
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Component 'VIEW_1' must be a project");
        call(request.setProject("VIEW_1"));
    }

    @Test
    public void fail_when_not_authenticated() {
        userSession.anonymous();
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        expectedException.expect(UnauthorizedException.class);
        call(request);
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        Mockito.when(dispatchers.getGlobalDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        Mockito.when(dispatchers.getProjectDispatchers()).thenReturn(Collections.singletonList(RemoveActionTest.NOTIF_MY_NEW_ISSUES));
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        call(request.setProject(branch.getDbKey()));
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

