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
package org.sonar.server.platform.serverid;


import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.SonarQubeSide;
import org.sonar.core.platform.ServerId;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.server.platform.WebServer;


@RunWith(DataProviderRunner.class)
public class ServerIdManagerTest {
    private static final ServerId OLD_FORMAT_SERVER_ID = ServerId.parse("20161123150657");

    private static final ServerId NO_DATABASE_ID_SERVER_ID = ServerId.parse(randomAlphanumeric(UUID_DATASET_ID_LENGTH));

    private static final ServerId WITH_DATABASE_ID_SERVER_ID = ServerId.of(randomAlphanumeric(DATABASE_ID_LENGTH), randomAlphanumeric(NOT_UUID_DATASET_ID_LENGTH));

    private static final String CHECKSUM_1 = randomAlphanumeric(12);

    @Rule
    public final DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ServerIdChecksum serverIdChecksum = Mockito.mock(ServerIdChecksum.class);

    private ServerIdFactory serverIdFactory = Mockito.mock(ServerIdFactory.class);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private WebServer webServer = Mockito.mock(WebServer.class);

    private ServerIdManager underTest;

    @Test
    public void web_leader_persists_new_server_id_if_missing() {
        mockCreateNewServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFromScratch();
    }

    @Test
    public void web_leader_persists_new_server_id_if_format_is_old_date() {
        insertServerId(ServerIdManagerTest.OLD_FORMAT_SERVER_ID);
        mockCreateNewServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFromScratch();
    }

    @Test
    public void web_leader_persists_new_server_id_if_value_is_empty() {
        insertServerId("");
        mockCreateNewServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFromScratch();
    }

    @Test
    public void web_leader_keeps_existing_server_id_if_valid() {
        insertServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        insertChecksum(ServerIdManagerTest.CHECKSUM_1);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
    }

    @Test
    public void web_leader_creates_server_id_from_scratch_if_checksum_fails_for_serverId_in_deprecated_format() {
        ServerId currentServerId = ServerIdManagerTest.OLD_FORMAT_SERVER_ID;
        insertServerId(currentServerId);
        insertChecksum("invalid");
        mockChecksumOf(currentServerId, "valid");
        mockCreateNewServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFromScratch();
    }

    @Test
    public void web_leader_creates_server_id_from_current_serverId_without_databaseId_if_checksum_fails() {
        ServerId currentServerId = ServerId.parse(randomAlphanumeric(UUID_DATASET_ID_LENGTH));
        insertServerId(currentServerId);
        insertChecksum("does_not_match_WITH_DATABASE_ID_SERVER_ID");
        mockChecksumOf(currentServerId, "matches_WITH_DATABASE_ID_SERVER_ID");
        mockCreateNewServerIdFrom(currentServerId, ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFrom(currentServerId);
    }

    @Test
    public void web_leader_creates_server_id_from_current_serverId_with_databaseId_if_checksum_fails() {
        ServerId currentServerId = ServerId.of(randomAlphanumeric(DATABASE_ID_LENGTH), randomAlphanumeric(UUID_DATASET_ID_LENGTH));
        insertServerId(currentServerId);
        insertChecksum("does_not_match_WITH_DATABASE_ID_SERVER_ID");
        mockChecksumOf(currentServerId, "matches_WITH_DATABASE_ID_SERVER_ID");
        mockCreateNewServerIdFrom(currentServerId, ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        verifyCreateNewServerIdFrom(currentServerId);
    }

    @Test
    public void web_leader_generates_missing_checksum_for_current_serverId_with_databaseId() {
        insertServerId(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID);
        mockChecksumOf(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
        Mockito.when(webServer.isStartupLeader()).thenReturn(true);
        test(SonarQubeSide.SERVER);
        verifyDb(ServerIdManagerTest.WITH_DATABASE_ID_SERVER_ID, ServerIdManagerTest.CHECKSUM_1);
    }

    @Test
    public void web_follower_fails_if_server_id_is_missing() {
        Mockito.when(webServer.isStartupLeader()).thenReturn(false);
        expectMissingServerIdException();
        test(SonarQubeSide.SERVER);
    }

    @Test
    public void web_follower_fails_if_server_id_is_empty() {
        insertServerId("");
        Mockito.when(webServer.isStartupLeader()).thenReturn(false);
        expectEmptyServerIdException();
        test(SonarQubeSide.SERVER);
    }

    @Test
    public void compute_engine_fails_if_server_id_is_missing() {
        expectMissingServerIdException();
        test(SonarQubeSide.COMPUTE_ENGINE);
    }

    @Test
    public void compute_engine_fails_if_server_id_is_empty() {
        insertServerId("");
        expectEmptyServerIdException();
        test(SonarQubeSide.COMPUTE_ENGINE);
    }
}

