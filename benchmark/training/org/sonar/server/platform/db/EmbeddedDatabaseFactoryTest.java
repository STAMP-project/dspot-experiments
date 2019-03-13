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
package org.sonar.server.platform.db;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;


public class EmbeddedDatabaseFactoryTest {
    private MapSettings settings = new MapSettings();

    private System2 system2 = Mockito.mock(System2.class);

    @Test
    public void should_start_and_stop_tcp_h2_database() {
        settings.setProperty(JDBC_URL.getKey(), "jdbc:h2:tcp:localhost");
        EmbeddedDatabase embeddedDatabase = Mockito.mock(EmbeddedDatabase.class);
        EmbeddedDatabaseFactory databaseFactory = new EmbeddedDatabaseFactory(settings.asConfig(), system2) {
            @Override
            EmbeddedDatabase createEmbeddedDatabase() {
                return embeddedDatabase;
            }
        };
        databaseFactory.start();
        databaseFactory.stop();
        Mockito.verify(embeddedDatabase).start();
        Mockito.verify(embeddedDatabase).stop();
    }

    @Test
    public void should_not_start_mem_h2_database() {
        settings.setProperty(JDBC_URL.getKey(), "jdbc:h2:mem");
        EmbeddedDatabase embeddedDatabase = Mockito.mock(EmbeddedDatabase.class);
        EmbeddedDatabaseFactory databaseFactory = new EmbeddedDatabaseFactory(settings.asConfig(), system2) {
            @Override
            EmbeddedDatabase createEmbeddedDatabase() {
                return embeddedDatabase;
            }
        };
        databaseFactory.start();
        Mockito.verify(embeddedDatabase, Mockito.never()).start();
    }
}

