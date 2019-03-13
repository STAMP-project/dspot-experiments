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


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.notifications.NotificationChannel;


public class NotificationCenterTest {
    private NotificationChannel emailChannel = Mockito.mock(NotificationChannel.class);

    private NotificationChannel gtalkChannel = Mockito.mock(NotificationChannel.class);

    private NotificationCenter underTest;

    @Test
    public void shouldReturnChannels() {
        assertThat(underTest.getChannels()).containsOnly(emailChannel, gtalkChannel);
    }

    @Test
    public void shouldReturnDispatcherKeysForSpecificPropertyValue() {
        assertThat(underTest.getDispatcherKeysForProperty("global", "true")).containsOnly("Dispatcher1", "Dispatcher2");
    }

    @Test
    public void shouldReturnDispatcherKeysForExistenceOfProperty() {
        assertThat(underTest.getDispatcherKeysForProperty("on-project", null)).containsOnly("Dispatcher1", "Dispatcher3");
    }

    @Test
    public void testDefaultConstructors() {
        underTest = new NotificationCenter(new NotificationChannel[]{ emailChannel });
        assertThat(underTest.getChannels()).hasSize(1);
        underTest = new NotificationCenter();
        assertThat(underTest.getChannels()).hasSize(0);
        underTest = new NotificationCenter(new NotificationDispatcherMetadata[]{ NotificationDispatcherMetadata.create("Dispatcher1").setProperty("global", "true") });
        assertThat(underTest.getChannels()).hasSize(0);
        assertThat(underTest.getDispatcherKeysForProperty("global", null)).hasSize(1);
    }
}

