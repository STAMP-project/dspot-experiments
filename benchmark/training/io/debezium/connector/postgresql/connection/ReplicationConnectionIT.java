/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql.connection;


import io.debezium.connector.postgresql.DecoderDifferences;
import io.debezium.connector.postgresql.TestHelper;
import io.debezium.jdbc.JdbcConnection.ResultSetMapper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.apache.kafka.connect.errors.ConnectException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for {@link ReplicationConnection}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class ReplicationConnectionIT {
    @Test
    public void shouldCreateAndDropReplicationSlots() throws Exception {
        // create a replication connection which should be dropped once it's closed
        try (ReplicationConnection connection = TestHelper.createForReplication("test1", true)) {
            ReplicationStream stream = connection.startStreaming();
            Assert.assertNull(stream.lastReceivedLsn());
            stream.close();
        }
        // create a replication connection which should be dropped once it's closed
        try (ReplicationConnection connection = TestHelper.createForReplication("test2", true)) {
            ReplicationStream stream = connection.startStreaming();
            Assert.assertNull(stream.lastReceivedLsn());
            stream.close();
        }
    }

    @Test(expected = ConnectException.class)
    public void shouldNotAllowMultipleReplicationSlotsOnTheSameDBSlotAndPlugin() throws Exception {
        // create a replication connection which should be dropped once it's closed
        try (ReplicationConnection conn1 = TestHelper.createForReplication("test1", true)) {
            conn1.startStreaming();
            try (ReplicationConnection conn2 = TestHelper.createForReplication("test1", false)) {
                conn2.startStreaming();
                Assert.fail("Should not be able to create 2 replication connections on the same db, plugin and slot");
            }
        }
    }

    @Test
    public void shouldCloseConnectionOnInvalidSlotName() throws Exception {
        final int closeRetries = 60;
        final int waitPeriod = 2000;
        final String slotQuery = "select count(*) from pg_stat_replication where state = 'startup'";
        final ResultSetMapper<Integer> slotQueryMapper = ( rs) -> {
            rs.next();
            return rs.getInt(1);
        };
        final int slotsBefore;
        try (PostgresConnection connection = TestHelper.create()) {
            slotsBefore = connection.queryAndMap(slotQuery, slotQueryMapper);
        }
        try (ReplicationConnection conn1 = TestHelper.createForReplication("test1-", true)) {
            conn1.startStreaming();
            Assert.fail("Invalid slot name should fail");
        } catch (Exception e) {
            try (PostgresConnection connection = TestHelper.create()) {
                final int slotsAfter = connection.queryAndMap(slotQuery, slotQueryMapper);
                for (int retry = 1; retry <= closeRetries; retry++) {
                    if (slotsAfter <= slotsBefore) {
                        break;
                    }
                    if (retry == closeRetries) {
                        Assert.fail("Connection should not be active");
                    }
                    Thread.sleep(waitPeriod);
                }
            }
        }
    }

    @Test
    public void shouldReceiveAndDecodeIndividualChanges() throws Exception {
        // create a replication connection which should be dropped once it's closed
        try (ReplicationConnection connection = TestHelper.createForReplication("test", true)) {
            ReplicationStream stream = connection.startStreaming();// this creates the replication slot

            int expectedMessages = DecoderDifferences.updatesWithoutPK(insertLargeTestData(), 1);
            expectedMessagesFromStream(stream, expectedMessages);
        }
    }

    @Test
    public void shouldReceiveSameChangesIfNotFlushed() throws Exception {
        // don't drop the replication slot once this is finished
        String slotName = "test";
        int receivedMessagesCount = startInsertStop(slotName, null);
        // create a new replication connection with the same slot and check that without the LSN having been flushed,
        // we'll get back the same message again from before
        try (ReplicationConnection connection = TestHelper.createForReplication(slotName, true)) {
            ReplicationStream stream = connection.startStreaming();// this should receive the same message as before since we haven't flushed

            expectedMessagesFromStream(stream, receivedMessagesCount);
        }
    }

    @Test
    public void shouldNotReceiveSameChangesIfFlushed() throws Exception {
        // don't drop the replication slot once this is finished
        String slotName = "test";
        startInsertStop(slotName, this::flushLSN);
        // create a new replication connection with the same slot and check that we don't get back the same changes that we've
        // flushed
        try (ReplicationConnection connection = TestHelper.createForReplication(slotName, true)) {
            ReplicationStream stream = connection.startStreaming();
            // even when flushing the last received location, the server will send back the last record after reconnecting, not sure why that is...
            expectedMessagesFromStream(stream, 0);
        }
    }

    @Test
    public void shouldReceiveMissedChangesWhileDown() throws Exception {
        String slotName = "test";
        startInsertStop(slotName, this::flushLSN);
        // run some more SQL while the slot is stopped
        // this deletes 2 entries so each of them will have a message
        TestHelper.execute("DELETE FROM table_with_pk WHERE a < 3;");
        int additionalMessages = 2;
        // create a new replication connection with the same slot and check that we get the additional messages
        try (ReplicationConnection connection = TestHelper.createForReplication(slotName, true)) {
            ReplicationStream stream = connection.startStreaming();
            expectedMessagesFromStream(stream, additionalMessages);
        }
    }

    @Test
    public void shouldResumeFromLastReceivedLSN() throws Exception {
        String slotName = "test";
        AtomicLong lastReceivedLSN = new AtomicLong(0);
        startInsertStop(slotName, ( stream) -> lastReceivedLSN.compareAndSet(0, stream.lastReceivedLsn()));
        Assert.assertTrue(((lastReceivedLSN.get()) > 0));
        // resume replication from the last received LSN and don't expect anything else
        try (ReplicationConnection connection = TestHelper.createForReplication(slotName, true)) {
            ReplicationStream stream = connection.startStreaming(lastReceivedLSN.get());
            expectedMessagesFromStream(stream, 0);
        }
    }

    @Test
    public void shouldTolerateInvalidLSNValues() throws Exception {
        String slotName = "test";
        startInsertStop(slotName, null);
        // resume replication from the last received LSN and don't expect anything else
        try (ReplicationConnection connection = TestHelper.createForReplication(slotName, true)) {
            ReplicationStream stream = connection.startStreaming(Long.MAX_VALUE);
            expectedMessagesFromStream(stream, 0);
            // this deletes 2 entries so each of them will have a message
            TestHelper.execute("DELETE FROM table_with_pk WHERE a < 3;");
            // don't expect any messages because we've started stream from a very big (i.e. the end) position
            expectedMessagesFromStream(stream, 0);
        }
    }

    @Test
    public void shouldReceiveOneMessagePerDMLOnTransactionCommit() throws Exception {
        try (ReplicationConnection connection = TestHelper.createForReplication("test", true)) {
            ReplicationStream stream = connection.startStreaming();
            String statement = "DROP TABLE IF EXISTS table_with_pk;" + (((("DROP TABLE IF EXISTS table_without_pk;" + "CREATE TABLE table_with_pk (a SERIAL, b VARCHAR(30), c TIMESTAMP NOT NULL, PRIMARY KEY(a, c));") + "CREATE TABLE table_without_pk (a SERIAL, b NUMERIC(5,2), c TEXT);") + "INSERT INTO table_with_pk (b, c) VALUES('val1', now()); ") + "INSERT INTO table_with_pk (b, c) VALUES('val2', now()); ");
            TestHelper.execute(statement);
            expectedMessagesFromStream(stream, 2);
        }
    }

    @Test
    public void shouldNotReceiveMessagesOnTransactionRollback() throws Exception {
        try (ReplicationConnection connection = TestHelper.createForReplication("test", true)) {
            ReplicationStream stream = connection.startStreaming();
            String statement = "DROP TABLE IF EXISTS table_with_pk;" + (("CREATE TABLE table_with_pk (a SERIAL, b VARCHAR(30), c TIMESTAMP NOT NULL, PRIMARY KEY(a, c));" + "INSERT INTO table_with_pk (b, c) VALUES('val1', now()); ") + "ROLLBACK;");
            TestHelper.execute(statement);
            expectedMessagesFromStream(stream, 0);
        }
    }

    @Test
    public void shouldGeneratesEventsForMultipleSchemas() throws Exception {
        try (ReplicationConnection connection = TestHelper.createForReplication("test", true)) {
            ReplicationStream stream = connection.startStreaming();
            String statements = "CREATE SCHEMA schema1;" + (((((("CREATE SCHEMA schema2;" + "DROP TABLE IF EXISTS schema1.table;") + "DROP TABLE IF EXISTS schema2.table;") + "CREATE TABLE schema1.table (a SERIAL, b VARCHAR(30), c TIMESTAMP NOT NULL, PRIMARY KEY(a, c));") + "CREATE TABLE schema2.table (a SERIAL, b VARCHAR(30), c TIMESTAMP NOT NULL, PRIMARY KEY(a, c));") + "INSERT INTO schema1.table (b, c) VALUES('Value for schema1', now());") + "INSERT INTO schema2.table (b, c) VALUES('Value for schema2', now());");
            TestHelper.execute(statements);
            expectedMessagesFromStream(stream, 2);
        }
    }
}

