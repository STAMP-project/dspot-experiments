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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;


public class NotificationDispatcherTest {
    @Mock
    private NotificationChannel channel;

    @Mock
    private Notification notification;

    @Mock
    private Context context;

    @Test
    public void defaultMethods() {
        NotificationDispatcher dispatcher = new NotificationDispatcherTest.FakeGenericNotificationDispatcher();
        Assert.assertThat(dispatcher.getKey(), CoreMatchers.is("FakeGenericNotificationDispatcher"));
        Assert.assertThat(dispatcher.toString(), CoreMatchers.is("FakeGenericNotificationDispatcher"));
    }

    @Test
    public void shouldAlwaysRunDispatchForGenericDispatcher() {
        NotificationDispatcher dispatcher = new NotificationDispatcherTest.FakeGenericNotificationDispatcher();
        dispatcher.performDispatch(notification, context);
        Mockito.verify(context, Mockito.times(1)).addUser("user1", channel);
    }

    @Test
    public void shouldNotAlwaysRunDispatchForSpecificDispatcher() {
        NotificationDispatcher dispatcher = new NotificationDispatcherTest.FakeSpecificNotificationDispatcher();
        // a "event1" notif is sent
        dispatcher.performDispatch(notification, context);
        Mockito.verify(context, Mockito.never()).addUser("user1", channel);
        // now, a "specific-event" notif is sent
        Mockito.when(notification.getType()).thenReturn("specific-event");
        dispatcher.performDispatch(notification, context);
        Mockito.verify(context, Mockito.times(1)).addUser("user1", channel);
    }

    class FakeGenericNotificationDispatcher extends NotificationDispatcher {
        @Override
        public void dispatch(Notification notification, Context context) {
            context.addUser("user1", channel);
        }
    }

    class FakeSpecificNotificationDispatcher extends NotificationDispatcher {
        public FakeSpecificNotificationDispatcher() {
            super("specific-event");
        }

        @Override
        public void dispatch(Notification notification, Context context) {
            context.addUser("user1", channel);
        }
    }
}

