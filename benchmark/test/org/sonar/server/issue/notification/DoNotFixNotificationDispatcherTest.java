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


import Issue.RESOLUTION_FALSE_POSITIVE;
import Issue.RESOLUTION_FIXED;
import NotificationDispatcher.Context;
import NotificationDispatcherMetadata.GLOBAL_NOTIFICATION;
import NotificationDispatcherMetadata.PER_PROJECT_NOTIFICATION;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.web.UserRole;
import org.sonar.server.notification.NotificationDispatcherMetadata;
import org.sonar.server.notification.NotificationManager;


public class DoNotFixNotificationDispatcherTest {
    NotificationManager notifications = Mockito.mock(NotificationManager.class);

    Context context = Mockito.mock(Context.class);

    NotificationChannel emailChannel = Mockito.mock(NotificationChannel.class);

    NotificationChannel twitterChannel = Mockito.mock(NotificationChannel.class);

    DoNotFixNotificationDispatcher underTest = new DoNotFixNotificationDispatcher(notifications);

    @Test
    public void test_metadata() {
        NotificationDispatcherMetadata metadata = DoNotFixNotificationDispatcher.newMetadata();
        assertThat(metadata.getDispatcherKey()).isEqualTo(underTest.getKey());
        assertThat(metadata.getProperty(GLOBAL_NOTIFICATION)).isEqualTo("true");
        assertThat(metadata.getProperty(PER_PROJECT_NOTIFICATION)).isEqualTo("true");
    }

    @Test
    public void should_not_dispatch_if_other_notification_type() {
        Notification notification = new Notification("other");
        underTest.performDispatch(notification, context);
        Mockito.verify(context, Mockito.never()).addUser(ArgumentMatchers.any(String.class), ArgumentMatchers.any(NotificationChannel.class));
    }

    @Test
    public void should_dispatch_to_subscribers() {
        Multimap<String, NotificationChannel> recipients = HashMultimap.create();
        recipients.put("simon", emailChannel);
        recipients.put("freddy", twitterChannel);
        recipients.put("godin", twitterChannel);
        Mockito.when(notifications.findSubscribedRecipientsForDispatcher(underTest, "struts", new NotificationManager.SubscriberPermissionsOnProject(UserRole.USER))).thenReturn(recipients);
        Notification fpNotif = new IssueChangeNotification().setFieldValue("projectKey", "struts").setFieldValue("changeAuthor", "godin").setFieldValue("new.resolution", RESOLUTION_FALSE_POSITIVE).setFieldValue("assignee", "freddy");
        underTest.performDispatch(fpNotif, context);
        Mockito.verify(context).addUser("simon", emailChannel);
        Mockito.verify(context).addUser("freddy", twitterChannel);
        // do not notify the person who flagged the issue as false-positive
        Mockito.verify(context, Mockito.never()).addUser("godin", twitterChannel);
        Mockito.verifyNoMoreInteractions(context);
    }

    /**
     * Only false positive and won't fix resolutions
     */
    @Test
    public void ignore_other_resolutions() {
        Notification fixedNotif = new IssueChangeNotification().setFieldValue("projectKey", "struts").setFieldValue("changeAuthor", "godin").setFieldValue("new.resolution", RESOLUTION_FIXED).setFieldValue("assignee", "freddy");
        underTest.performDispatch(fixedNotif, context);
        Mockito.verifyZeroInteractions(context);
    }
}

