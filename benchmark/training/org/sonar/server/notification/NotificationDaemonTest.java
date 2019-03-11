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
package org.sonar.server.notification;


import NotificationDispatcher.Context;
import com.google.common.collect.Sets;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.Settings;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.db.DbClient;
import org.sonar.db.property.PropertiesDao;


public class NotificationDaemonTest {
    private static String CREATOR_SIMON = "simon";

    private static String CREATOR_EVGENY = "evgeny";

    private static String ASSIGNEE_SIMON = "simon";

    private DefaultNotificationManager manager = Mockito.mock(DefaultNotificationManager.class);

    private Notification notification = Mockito.mock(Notification.class);

    private NotificationChannel emailChannel = Mockito.mock(NotificationChannel.class);

    private NotificationChannel gtalkChannel = Mockito.mock(NotificationChannel.class);

    private NotificationDispatcher commentOnIssueAssignedToMe = Mockito.mock(NotificationDispatcher.class);

    private NotificationDispatcher commentOnIssueCreatedByMe = Mockito.mock(NotificationDispatcher.class);

    private NotificationDispatcher qualityGateChange = Mockito.mock(NotificationDispatcher.class);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private NotificationService service = new NotificationService(dbClient, new NotificationDispatcher[]{ commentOnIssueAssignedToMe, commentOnIssueCreatedByMe, qualityGateChange });

    private NotificationDaemon underTest = null;

    /**
     * Given:
     * Simon wants to receive notifications by email on comments for reviews assigned to him or created by him.
     * <p/>
     * When:
     * Freddy adds comment to review created by Simon and assigned to Simon.
     * <p/>
     * Then:
     * Only one notification should be delivered to Simon by Email.
     */
    @Test
    public void scenario1() {
        setUpMocks();
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.ASSIGNEE_SIMON, emailChannel)).when(commentOnIssueAssignedToMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.CREATOR_SIMON, emailChannel)).when(commentOnIssueCreatedByMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        underTest.start();
        Mockito.verify(emailChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
        underTest.stop();
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
    }

    /**
     * Given:
     * Evgeny wants to receive notification by GTalk on comments for reviews created by him.
     * Simon wants to receive notification by Email on comments for reviews assigned to him.
     * <p/>
     * When:
     * Freddy adds comment to review created by Evgeny and assigned to Simon.
     * <p/>
     * Then:
     * Two notifications should be delivered - one to Simon by Email and another to Evgeny by GTalk.
     */
    @Test
    public void scenario2() {
        setUpMocks();
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.ASSIGNEE_SIMON, emailChannel)).when(commentOnIssueAssignedToMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.CREATOR_EVGENY, gtalkChannel)).when(commentOnIssueCreatedByMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        underTest.start();
        Mockito.verify(emailChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
        Mockito.verify(gtalkChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.CREATOR_EVGENY);
        underTest.stop();
        Mockito.verify(emailChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.CREATOR_EVGENY);
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
    }

    /**
     * Given:
     * Simon wants to receive notifications by Email and GTLak on comments for reviews assigned to him.
     * <p/>
     * When:
     * Freddy adds comment to review created by Evgeny and assigned to Simon.
     * <p/>
     * Then:
     * Two notifications should be delivered to Simon - one by Email and another by GTalk.
     */
    @Test
    public void scenario3() {
        setUpMocks();
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.ASSIGNEE_SIMON, new NotificationChannel[]{ emailChannel, gtalkChannel })).when(commentOnIssueAssignedToMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        underTest.start();
        Mockito.verify(emailChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
        Mockito.verify(gtalkChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
        underTest.stop();
        Mockito.verify(emailChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.CREATOR_EVGENY);
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.CREATOR_EVGENY);
    }

    /**
     * Given:
     * Nobody wants to receive notifications.
     * <p/>
     * When:
     * Freddy adds comment to review created by Evgeny and assigned to Simon.
     * <p/>
     * Then:
     * No notifications.
     */
    @Test
    public void scenario4() {
        setUpMocks();
        underTest.start();
        underTest.stop();
        Mockito.verify(emailChannel, Mockito.never()).deliver(ArgumentMatchers.any(Notification.class), ArgumentMatchers.anyString());
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(ArgumentMatchers.any(Notification.class), ArgumentMatchers.anyString());
    }

    // SONAR-4548
    @Test
    public void shouldNotStopWhenException() {
        setUpMocks();
        Mockito.when(manager.getFromQueue()).thenThrow(new RuntimeException("Unexpected exception")).thenReturn(notification).thenReturn(null);
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.ASSIGNEE_SIMON, emailChannel)).when(commentOnIssueAssignedToMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        Mockito.doAnswer(NotificationDaemonTest.addUser(NotificationDaemonTest.CREATOR_SIMON, emailChannel)).when(commentOnIssueCreatedByMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        underTest.start();
        Mockito.verify(emailChannel, Mockito.timeout(2000)).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
        underTest.stop();
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(notification, NotificationDaemonTest.ASSIGNEE_SIMON);
    }

    @Test
    public void shouldNotAddNullAsUser() {
        setUpMocks();
        Mockito.doAnswer(NotificationDaemonTest.addUser(null, gtalkChannel)).when(commentOnIssueCreatedByMe).dispatch(ArgumentMatchers.same(notification), ArgumentMatchers.any(Context.class));
        underTest.start();
        underTest.stop();
        Mockito.verify(emailChannel, Mockito.never()).deliver(ArgumentMatchers.any(Notification.class), ArgumentMatchers.anyString());
        Mockito.verify(gtalkChannel, Mockito.never()).deliver(ArgumentMatchers.any(Notification.class), ArgumentMatchers.anyString());
    }

    @Test
    public void getDispatchers() {
        setUpMocks();
        assertThat(service.getDispatchers()).containsOnly(commentOnIssueAssignedToMe, commentOnIssueCreatedByMe, qualityGateChange);
    }

    @Test
    public void getDispatchers_empty() {
        Settings settings = new MapSettings().setProperty("sonar.notifications.delay", 1L);
        service = new NotificationService(dbClient);
        assertThat(service.getDispatchers()).hasSize(0);
    }

    @Test
    public void shouldLogEvery10Minutes() {
        setUpMocks();
        // Emulate 2 notifications in DB
        Mockito.when(manager.getFromQueue()).thenReturn(notification).thenReturn(notification).thenReturn(null);
        Mockito.when(manager.count()).thenReturn(1L).thenReturn(0L);
        underTest = Mockito.spy(underTest);
        // Emulate processing of each notification take 10 min to have a log each time
        Mockito.when(underTest.now()).thenReturn(0L).thenReturn((((10 * 60) * 1000) + 1L)).thenReturn((((20 * 60) * 1000) + 2L));
        underTest.start();
        Mockito.verify(underTest, Mockito.timeout(200)).log(1, 1, 10);
        Mockito.verify(underTest, Mockito.timeout(200)).log(2, 0, 20);
        underTest.stop();
    }

    @Test
    public void hasProjectSubscribersForType() {
        setUpMocks();
        PropertiesDao dao = Mockito.mock(PropertiesDao.class);
        Mockito.when(dbClient.propertiesDao()).thenReturn(dao);
        // no subscribers
        Mockito.when(dao.hasProjectNotificationSubscribersForDispatchers("PROJECT_UUID", Arrays.asList("CommentOnIssueAssignedToMe", "CommentOnIssueCreatedByMe"))).thenReturn(false);
        assertThat(service.hasProjectSubscribersForTypes("PROJECT_UUID", Sets.newHashSet("issue-changes"))).isFalse();
        // has subscribers on one dispatcher (among the two)
        Mockito.when(dao.hasProjectNotificationSubscribersForDispatchers("PROJECT_UUID", Arrays.asList("CommentOnIssueAssignedToMe", "CommentOnIssueCreatedByMe"))).thenReturn(true);
        assertThat(service.hasProjectSubscribersForTypes("PROJECT_UUID", Sets.newHashSet("issue-changes"))).isTrue();
    }
}

