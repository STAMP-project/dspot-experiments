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


import MyNewIssuesNotificationDispatcher.KEY;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.server.notification.NotificationCenter;
import org.sonar.server.notification.NotificationDispatcherMetadata;


public class DispatchersImplTest {
    private NotificationCenter notificationCenter = new NotificationCenter(new NotificationDispatcherMetadata[]{ NotificationDispatcherMetadata.create(KEY).setProperty(NotificationDispatcherMetadata.GLOBAL_NOTIFICATION, "true").setProperty(NotificationDispatcherMetadata.PER_PROJECT_NOTIFICATION, "true"), NotificationDispatcherMetadata.create(NewIssuesNotificationDispatcher.KEY).setProperty(NotificationDispatcherMetadata.GLOBAL_NOTIFICATION, "true"), NotificationDispatcherMetadata.create(NewAlerts.KEY).setProperty(NotificationDispatcherMetadata.GLOBAL_NOTIFICATION, "true").setProperty(NotificationDispatcherMetadata.PER_PROJECT_NOTIFICATION, "true"), NotificationDispatcherMetadata.create(DoNotFixNotificationDispatcher.KEY).setProperty(NotificationDispatcherMetadata.GLOBAL_NOTIFICATION, "true").setProperty(NotificationDispatcherMetadata.PER_PROJECT_NOTIFICATION, "true") }, new NotificationChannel[]{  });

    private final MapSettings settings = new MapSettings();

    private DispatchersImpl underTest = new DispatchersImpl(notificationCenter, settings.asConfig());

    @Test
    public void get_sorted_global_dispatchers() {
        underTest.start();
        assertThat(underTest.getGlobalDispatchers()).containsExactly(NewAlerts.KEY, DoNotFixNotificationDispatcher.KEY, NewIssuesNotificationDispatcher.KEY, KEY);
    }

    @Test
    public void get_global_dispatchers_on_sonar_cloud() {
        settings.setProperty("sonar.sonarcloud.enabled", "true");
        underTest.start();
        assertThat(underTest.getGlobalDispatchers()).containsOnly(KEY);
    }

    @Test
    public void get_sorted_project_dispatchers() {
        underTest.start();
        assertThat(underTest.getProjectDispatchers()).containsExactly(NewAlerts.KEY, DoNotFixNotificationDispatcher.KEY, KEY);
    }

    @Test
    public void get_project_dispatchers_on_sonar_cloud() {
        settings.setProperty("sonar.sonarcloud.enabled", "true");
        underTest.start();
        assertThat(underTest.getProjectDispatchers()).containsOnly(KEY, NewAlerts.KEY, DoNotFixNotificationDispatcher.KEY);
    }
}

