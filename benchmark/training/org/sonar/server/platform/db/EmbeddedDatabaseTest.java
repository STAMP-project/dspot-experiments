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


import NetworkUtilsImpl.INSTANCE;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.LogTester;


public class EmbeddedDatabaseTest {
    private static final String LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress().getHostAddress();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    private MapSettings settings = new MapSettings();

    private System2 system2 = Mockito.mock(System2.class);

    private EmbeddedDatabase underTest = new EmbeddedDatabase(settings.asConfig(), system2);

    @Test
    public void start_fails_with_IAE_if_property_Data_Path_is_not_set() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Missing property " + (PATH_DATA.getKey())));
        underTest.start();
    }

    @Test
    public void start_fails_with_IAE_if_property_Data_Path_is_empty() {
        settings.setProperty(PATH_DATA.getKey(), "");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Missing property " + (PATH_DATA.getKey())));
        underTest.start();
    }

    @Test
    public void start_fails_with_IAE_if_JDBC_URL_settings_is_not_set() throws IOException {
        settings.setProperty(PATH_DATA.getKey(), temporaryFolder.newFolder().getAbsolutePath());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Missing property " + (JDBC_URL.getKey())));
        underTest.start();
    }

    @Test
    public void start_fails_with_IAE_if_embedded_port_settings_is_not_set() throws IOException {
        settings.setProperty(PATH_DATA.getKey(), temporaryFolder.newFolder().getAbsolutePath()).setProperty(JDBC_URL.getKey(), "jdbc url");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Missing property " + (JDBC_EMBEDDED_PORT.getKey())));
        underTest.start();
    }

    @Test
    public void start_ignores_URL_to_create_database_and_uses_empty_username_and_password_when_then_are_not_set() throws IOException {
        int port = INSTANCE.getNextAvailablePort(InetAddress.getLoopbackAddress());
        settings.setProperty(PATH_DATA.getKey(), temporaryFolder.newFolder().getAbsolutePath()).setProperty(JDBC_URL.getKey(), "jdbc url").setProperty(JDBC_EMBEDDED_PORT.getKey(), ("" + port));
        underTest.start();
        checkDbIsUp(port, "", "");
    }

    @Test
    public void start_creates_db_and_adds_tcp_listener() throws IOException {
        int port = INSTANCE.getNextAvailablePort(InetAddress.getLoopbackAddress());
        settings.setProperty(PATH_DATA.getKey(), temporaryFolder.newFolder().getAbsolutePath()).setProperty(JDBC_URL.getKey(), "jdbc url").setProperty(JDBC_EMBEDDED_PORT.getKey(), ("" + port)).setProperty(JDBC_USERNAME.getKey(), "foo").setProperty(JDBC_PASSWORD.getKey(), "bar");
        underTest.start();
        checkDbIsUp(port, "foo", "bar");
        // H2 listens on loopback address only
        Mockito.verify(system2).setProperty("h2.bindAddress", EmbeddedDatabaseTest.LOOPBACK_ADDRESS);
    }

    @Test
    public void start_supports_in_memory_H2_JDBC_URL() throws IOException {
        int port = INSTANCE.getNextAvailablePort(InetAddress.getLoopbackAddress());
        settings.setProperty(PATH_DATA.getKey(), temporaryFolder.newFolder().getAbsolutePath()).setProperty(JDBC_URL.getKey(), "jdbc:h2:mem:sonar").setProperty(JDBC_EMBEDDED_PORT.getKey(), ("" + port)).setProperty(JDBC_USERNAME.getKey(), "foo").setProperty(JDBC_PASSWORD.getKey(), "bar");
        underTest.start();
        checkDbIsUp(port, "foo", "bar");
    }
}

