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
package org.sonar.server.issue.notification;


import NotificationDispatcher.Context;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.web.UserRole;
import org.sonar.server.notification.NotificationManager;


public class NewIssuesNotificationDispatcherTest {
    private NotificationManager notifications = Mockito.mock(NotificationManager.class);

    private Context context = Mockito.mock(Context.class);

    private NotificationChannel emailChannel = Mockito.mock(NotificationChannel.class);

    private NotificationChannel twitterChannel = Mockito.mock(NotificationChannel.class);

    private NewIssuesNotificationDispatcher dispatcher = Mockito.mock(NewIssuesNotificationDispatcher.class);

    @Test
    public void shouldNotDispatchIfNotNewViolationsNotification() {
        Notification notification = new Notification("other-notif");
        dispatcher.performDispatch(notification, context);
        Mockito.verify(context, Mockito.never()).addUser(ArgumentMatchers.any(String.class), ArgumentMatchers.any(NotificationChannel.class));
    }

    @Test
    public void shouldDispatchToUsersWhoHaveSubscribedAndFlaggedProjectAsFavourite() {
        Multimap<String, NotificationChannel> recipients = HashMultimap.create();
        recipients.put("user1", emailChannel);
        recipients.put("user2", twitterChannel);
        Mockito.when(notifications.findSubscribedRecipientsForDispatcher(dispatcher, "struts", new NotificationManager.SubscriberPermissionsOnProject(UserRole.USER))).thenReturn(recipients);
        Notification notification = setFieldValue("projectKey", "struts");
        dispatcher.performDispatch(notification, context);
        Mockito.verify(context).addUser("user1", emailChannel);
        Mockito.verify(context).addUser("user2", twitterChannel);
        Mockito.verifyNoMoreInteractions(context);
    }
}

