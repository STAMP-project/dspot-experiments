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


import SubscriberPermissionsOnProject.ALL_MUST_HAVE_ROLE_USER;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.notification.NotificationQueueDao;
import org.sonar.db.notification.NotificationQueueDto;
import org.sonar.db.permission.AuthorizationDao;
import org.sonar.db.property.PropertiesDao;
import org.sonar.db.property.Subscriber;
import org.sonar.server.notification.NotificationManager.SubscriberPermissionsOnProject;


public class DefaultNotificationManagerTest {
    private DefaultNotificationManager underTest;

    private PropertiesDao propertiesDao = Mockito.mock(PropertiesDao.class);

    private NotificationDispatcher dispatcher = Mockito.mock(NotificationDispatcher.class);

    private NotificationChannel emailChannel = Mockito.mock(NotificationChannel.class);

    private NotificationChannel twitterChannel = Mockito.mock(NotificationChannel.class);

    private NotificationQueueDao notificationQueueDao = Mockito.mock(NotificationQueueDao.class);

    private AuthorizationDao authorizationDao = Mockito.mock(AuthorizationDao.class);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private DbSession dbSession = Mockito.mock(DbSession.class);

    @Test
    public void shouldProvideChannelList() {
        assertThat(underTest.getChannels()).containsOnly(emailChannel, twitterChannel);
        underTest = new DefaultNotificationManager(new NotificationChannel[]{  }, dbClient);
        assertThat(underTest.getChannels()).hasSize(0);
    }

    @Test
    public void shouldPersist() {
        Notification notification = new Notification("test");
        underTest.scheduleForSending(notification);
        Mockito.verify(notificationQueueDao, Mockito.only()).insert(ArgumentMatchers.any(List.class));
    }

    @Test
    public void shouldGetFromQueueAndDelete() {
        Notification notification = new Notification("test");
        NotificationQueueDto dto = NotificationQueueDto.toNotificationQueueDto(notification);
        List<NotificationQueueDto> dtos = Arrays.asList(dto);
        Mockito.when(notificationQueueDao.selectOldest(1)).thenReturn(dtos);
        assertThat(underTest.getFromQueue()).isNotNull();
        InOrder inOrder = Mockito.inOrder(notificationQueueDao);
        inOrder.verify(notificationQueueDao).selectOldest(1);
        inOrder.verify(notificationQueueDao).delete(dtos);
    }

    // SONAR-4739
    @Test
    public void shouldNotFailWhenUnableToDeserialize() throws Exception {
        NotificationQueueDto dto1 = Mockito.mock(NotificationQueueDto.class);
        Mockito.when(dto1.toNotification()).thenThrow(new InvalidClassException("Pouet"));
        List<NotificationQueueDto> dtos = Arrays.asList(dto1);
        Mockito.when(notificationQueueDao.selectOldest(1)).thenReturn(dtos);
        underTest = Mockito.spy(underTest);
        assertThat(underTest.getFromQueue()).isNull();
        assertThat(underTest.getFromQueue()).isNull();
        Mockito.verify(underTest, VerificationModeFactory.times(1)).logDeserializationIssue();
    }

    @Test
    public void shouldFindNoRecipient() {
        assertThat(underTest.findSubscribedRecipientsForDispatcher(dispatcher, "uuid_45", new SubscriberPermissionsOnProject(UserRole.USER)).asMap().entrySet()).hasSize(0);
    }

    @Test
    public void shouldFindSubscribedRecipientForGivenResource() {
        String projectUuid = "uuid_45";
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Email", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user1", false), new Subscriber("user3", false), new Subscriber("user3", true)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", "uuid_56")).thenReturn(Sets.newHashSet(new Subscriber("user2", false)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", true)));
        Mockito.when(propertiesDao.findUsersForNotification("NewAlerts", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user4", false)));
        Mockito.when(authorizationDao.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet("user1", "user3"), projectUuid, "user")).thenReturn(Sets.newHashSet("user1", "user3"));
        Multimap<String, NotificationChannel> multiMap = underTest.findSubscribedRecipientsForDispatcher(dispatcher, projectUuid, ALL_MUST_HAVE_ROLE_USER);
        assertThat(multiMap.entries()).hasSize(3);
        Map<String, Collection<NotificationChannel>> map = multiMap.asMap();
        assertThat(map.get("user1")).containsOnly(emailChannel);
        assertThat(map.get("user2")).isNull();
        assertThat(map.get("user3")).containsOnly(emailChannel, twitterChannel);
        assertThat(map.get("user4")).isNull();
        // code is optimized to perform only 1 SQL requests for all channels
        Mockito.verify(authorizationDao, VerificationModeFactory.times(1)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void should_apply_distinct_permission_filtering_global_or_project_subscribers() {
        String globalPermission = RandomStringUtils.randomAlphanumeric(4);
        String projectPermission = RandomStringUtils.randomAlphanumeric(5);
        String projectUuid = "uuid_45";
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Email", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user1", false), new Subscriber("user3", false), new Subscriber("user3", true)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", "uuid_56")).thenReturn(Sets.newHashSet(new Subscriber("user2", false)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", true)));
        Mockito.when(propertiesDao.findUsersForNotification("NewAlerts", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user4", false)));
        Mockito.when(authorizationDao.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet("user3", "user4"), projectUuid, globalPermission)).thenReturn(Sets.newHashSet("user3"));
        Mockito.when(authorizationDao.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet("user1", "user3"), projectUuid, projectPermission)).thenReturn(Sets.newHashSet("user1", "user3"));
        Multimap<String, NotificationChannel> multiMap = underTest.findSubscribedRecipientsForDispatcher(dispatcher, projectUuid, new SubscriberPermissionsOnProject(globalPermission, projectPermission));
        assertThat(multiMap.entries()).hasSize(3);
        Map<String, Collection<NotificationChannel>> map = multiMap.asMap();
        assertThat(map.get("user1")).containsOnly(emailChannel);
        assertThat(map.get("user2")).isNull();
        assertThat(map.get("user3")).containsOnly(emailChannel, twitterChannel);
        assertThat(map.get("user4")).isNull();
        // code is optimized to perform only 2 SQL requests for all channels
        Mockito.verify(authorizationDao, VerificationModeFactory.times(1)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(globalPermission));
        Mockito.verify(authorizationDao, VerificationModeFactory.times(1)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(projectPermission));
    }

    @Test
    public void do_not_call_db_for_project_permission_filtering_if_there_is_no_project_subscriber() {
        String globalPermission = RandomStringUtils.randomAlphanumeric(4);
        String projectPermission = RandomStringUtils.randomAlphanumeric(5);
        String projectUuid = "uuid_45";
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Email", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", true)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", true)));
        Mockito.when(authorizationDao.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet("user3"), projectUuid, globalPermission)).thenReturn(Sets.newHashSet("user3"));
        Multimap<String, NotificationChannel> multiMap = underTest.findSubscribedRecipientsForDispatcher(dispatcher, projectUuid, new SubscriberPermissionsOnProject(globalPermission, projectPermission));
        assertThat(multiMap.entries()).hasSize(2);
        Map<String, Collection<NotificationChannel>> map = multiMap.asMap();
        assertThat(map.get("user3")).containsOnly(emailChannel, twitterChannel);
        Mockito.verify(authorizationDao, VerificationModeFactory.times(1)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(globalPermission));
        Mockito.verify(authorizationDao, VerificationModeFactory.times(0)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(projectPermission));
    }

    @Test
    public void do_not_call_db_for_project_permission_filtering_if_there_is_no_global_subscriber() {
        String globalPermission = RandomStringUtils.randomAlphanumeric(4);
        String projectPermission = RandomStringUtils.randomAlphanumeric(5);
        String projectUuid = "uuid_45";
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Email", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", false)));
        Mockito.when(propertiesDao.findUsersForNotification("NewViolations", "Twitter", projectUuid)).thenReturn(Sets.newHashSet(new Subscriber("user3", false)));
        Mockito.when(authorizationDao.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet("user3"), projectUuid, projectPermission)).thenReturn(Sets.newHashSet("user3"));
        Multimap<String, NotificationChannel> multiMap = underTest.findSubscribedRecipientsForDispatcher(dispatcher, projectUuid, new SubscriberPermissionsOnProject(globalPermission, projectPermission));
        assertThat(multiMap.entries()).hasSize(2);
        Map<String, Collection<NotificationChannel>> map = multiMap.asMap();
        assertThat(map.get("user3")).containsOnly(emailChannel, twitterChannel);
        Mockito.verify(authorizationDao, VerificationModeFactory.times(0)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(globalPermission));
        Mockito.verify(authorizationDao, VerificationModeFactory.times(1)).keepAuthorizedLoginsOnProject(ArgumentMatchers.eq(dbSession), ArgumentMatchers.anySet(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(projectPermission));
    }
}

