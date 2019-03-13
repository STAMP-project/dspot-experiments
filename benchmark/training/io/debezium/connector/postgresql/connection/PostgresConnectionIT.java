/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql.connection;


import ServerInfo.ReplicaIdentity.DEFAULT;
import ServerInfo.ReplicationSlot;
import ServerInfo.ReplicationSlot.INVALID;
import Testing.Print;
import io.debezium.connector.postgresql.TestHelper;
import io.debezium.doc.FixFor;
import io.debezium.relational.TableId;
import io.debezium.util.Testing;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.postgresql.jdbc.PgConnection;


/**
 * Integration test for {@link PostgresConnection}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class PostgresConnectionIT {
    @Test
    public void shouldReportValidTxId() throws SQLException {
        try (PostgresConnection connection = TestHelper.create()) {
            connection.connect();
            Assert.assertTrue(((connection.currentTransactionId()) > 0));
        }
        try (PostgresConnection connection = TestHelper.create()) {
            connection.connect();
            connection.setAutoCommit(false);
            Long txId = connection.currentTransactionId();
            connection.executeWithoutCommitting("SELECT 1;");
            Assert.assertEquals("tx id should be the same", txId, connection.currentTransactionId());
            connection.connection().commit();
        }
    }

    @Test
    public void shouldReportValidXLogPos() throws SQLException {
        try (PostgresConnection connection = TestHelper.create()) {
            connection.connect();
            Assert.assertTrue(((connection.currentXLogLocation()) > 0));
        }
    }

    @Test
    public void shouldReadServerInformation() throws Exception {
        try (PostgresConnection connection = TestHelper.create()) {
            ServerInfo serverInfo = connection.serverInfo();
            Assert.assertNotNull(serverInfo);
            Assert.assertNotNull(serverInfo.server());
            Assert.assertNotNull(serverInfo.username());
            Assert.assertNotNull(serverInfo.database());
            Map<String, String> permissionsByRoleName = serverInfo.permissionsByRoleName();
            Assert.assertNotNull(permissionsByRoleName);
            Assert.assertTrue((!(permissionsByRoleName.isEmpty())));
        }
    }

    @Test
    public void shouldReadReplicationSlotInfo() throws Exception {
        try (PostgresConnection connection = TestHelper.create()) {
            ServerInfo.ReplicationSlot slotInfo = connection.readReplicationSlotInfo("test", "test");
            Assert.assertEquals(INVALID, slotInfo);
        }
    }

    @Test
    public void shouldPrintReplicateIdentityInfo() throws Exception {
        String statement = "DROP SCHEMA IF EXISTS public CASCADE;" + ("CREATE SCHEMA public;" + "CREATE TABLE test(pk serial, PRIMARY KEY (pk));");
        TestHelper.execute(statement);
        try (PostgresConnection connection = TestHelper.create()) {
            Assert.assertEquals(DEFAULT, connection.readReplicaIdentityInfo(TableId.parse("public.test")));
        }
    }

    @Test
    public void shouldDropReplicationSlot() throws Exception {
        try (PostgresConnection connection = TestHelper.create()) {
            // try to drop a non existent slot
            Assert.assertFalse(connection.dropReplicationSlot("test"));
        }
        // create a new replication slot via a replication connection
        try (ReplicationConnection connection = TestHelper.createForReplication("test", false)) {
            Assert.assertTrue(connection.isConnected());
        }
        // drop the slot from the previous connection
        try (PostgresConnection connection = TestHelper.create()) {
            // try to drop the previous slot
            Assert.assertTrue(connection.dropReplicationSlot("test"));
        }
    }

    @Test
    @FixFor("DBZ-934")
    public void temporaryReplicationSlotsShouldGetDroppedAutomatically() throws Exception {
        try (ReplicationConnection replicationConnection = TestHelper.createForReplication("test", true)) {
            PgConnection pgConnection = getUnderlyingConnection(replicationConnection);
            // temporary replication slots are not supported by Postgres < 10
            if ((pgConnection.getServerMajorVersion()) < 10) {
                return;
            }
            // simulate ungraceful shutdown by closing underlying database connection
            pgConnection.close();
            try (PostgresConnection connection = TestHelper.create()) {
                Assert.assertFalse("postgres did not drop replication slot", connection.dropReplicationSlot("test"));
            }
        }
    }

    @Test
    public void shouldDetectRunningConncurrentTxOnInit() throws Exception {
        Print.enable();
        // drop the slot from the previous connection
        final String slotName = "block";
        try (PostgresConnection connection = TestHelper.create()) {
            // try to drop the previous slot
            connection.dropReplicationSlot(slotName);
            connection.execute("DROP SCHEMA IF EXISTS public CASCADE", "CREATE SCHEMA public", "CREATE TABLE test(pk serial, PRIMARY KEY (pk))");
        }
        try (PostgresConnection blockingConnection = TestHelper.create("blocker")) {
            // Create an unfinished TX
            blockingConnection.connection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            blockingConnection.connection().setAutoCommit(false);
            blockingConnection.executeWithoutCommitting("INSERT INTO test VALUES(default)");
            Testing.print("Blocking exception started");
            final Future<?> f1 = Executors.newSingleThreadExecutor().submit(() -> {
                // Create a replication connection that is blocked till the concurrent TX is completed
                try (ReplicationConnection replConnection = TestHelper.createForReplication(slotName, false)) {
                    Testing.print("Connecting with replication connection 1");
                    Assert.assertTrue(replConnection.isConnected());
                    Testing.print("Replication connection 1 - completed");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(3000);
            final Future<?> f2 = Executors.newSingleThreadExecutor().submit(() -> {
                // Create a replication connection that receives confirmed_flush_lsn == null
                try (ReplicationConnection replConnection = TestHelper.createForReplication(slotName, false)) {
                    Testing.print("Connecting with replication connection 2");
                    Assert.assertTrue(replConnection.isConnected());
                    Testing.print("Replication connection 2 - completed");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(3000);
            blockingConnection.connection().commit();
            Testing.print("Blocking exception finished");
            Thread.sleep(6000);
            f1.get();
            f2.get();
            // drop the slot from the previous connection
            try (PostgresConnection connection = TestHelper.create()) {
                // try to drop the previous slot
                Assert.assertTrue(connection.dropReplicationSlot(slotName));
            }
        }
    }

    @Test
    public void shouldSupportPG95RestartLsn() throws Exception {
        String slotName = "pg95";
        try (ReplicationConnection replConnection = TestHelper.createForReplication(slotName, false)) {
            Assert.assertTrue(replConnection.isConnected());
        }
        try (PostgresConnection conn = buildPG95PGConn("pg95")) {
            ServerInfo.ReplicationSlot slotInfo = conn.readReplicationSlotInfo(slotName, TestHelper.decoderPlugin().getPostgresPluginName());
            Assert.assertNotNull(slotInfo);
            Assert.assertNotEquals(INVALID, slotInfo);
            conn.dropReplicationSlot(slotName);
        }
    }
}

