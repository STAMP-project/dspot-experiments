package com.zendesk.maxwell.recovery;


import com.zendesk.maxwell.MaxwellTestSupport;
import com.zendesk.maxwell.MaxwellTestSupportCallback;
import com.zendesk.maxwell.MysqlIsolatedServer;
import com.zendesk.maxwell.TestWithNameLogging;
import com.zendesk.maxwell.replication.Position;
import com.zendesk.maxwell.row.RowMap;
import com.zendesk.maxwell.schema.MysqlSavedSchema;
import com.zendesk.maxwell.schema.Schema;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RecoveryTest extends TestWithNameLogging {
    private static MysqlIsolatedServer masterServer;

    private static MysqlIsolatedServer slaveServer;

    static final Logger LOGGER = LoggerFactory.getLogger(RecoveryTest.class);

    private static final int DATA_SIZE = 500;

    private static final int NEW_DATA_SIZE = 100;

    @Test
    public void testBasicRecovery() throws Exception {
        if (MaxwellTestSupport.inGtidMode()) {
            RecoveryTest.LOGGER.info("No need to test recovery under gtid-mode");
            return;
        }
        MaxwellContext slaveContext = getContext(RecoveryTest.slaveServer.getPort(), true);
        String[] input = generateMasterData();
        /* run the execution through with the replicator running so we get heartbeats */
        MaxwellTestSupport.getRowsWithReplicator(RecoveryTest.masterServer, input, null, null);
        Position slavePosition = MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection());
        generateNewMasterData(false, RecoveryTest.DATA_SIZE);
        RecoveryTest.slaveServer.waitForSlaveToBeCurrent(RecoveryTest.masterServer);
        RecoveryInfo recoveryInfo = slaveContext.getRecoveryInfo();
        Assert.assertThat(recoveryInfo, notNullValue());
        MaxwellConfig slaveConfig = getConfig(RecoveryTest.slaveServer.getPort(), true);
        Recovery recovery = new Recovery(slaveConfig.maxwellMysql, slaveConfig.databaseName, slaveContext.getReplicationConnectionPool(), slaveContext.getCaseSensitivity(), recoveryInfo);
        Position recoveredPosition = recovery.recover().getPosition();
        // lousy tests, but it's very hard to make firm assertions about the correct position.
        // It's in a ballpark.
        if (slavePosition.getBinlogPosition().getFile().equals(recoveredPosition.getBinlogPosition().getFile())) {
            long positionDiff = (recoveredPosition.getBinlogPosition().getOffset()) - (slavePosition.getBinlogPosition().getOffset());
            Assert.assertThat(Math.abs(positionDiff), lessThan(1500L));
        } else {
            // TODO: something something.
        }
    }

    @Test
    public void testOtherClientID() throws Exception {
        if (MaxwellTestSupport.inGtidMode()) {
            RecoveryTest.LOGGER.info("No need to test recovery under gtid-mode");
            return;
        }
        MaxwellContext slaveContext = getContext(RecoveryTest.slaveServer.getPort(), true);
        String[] input = generateMasterData();
        MaxwellTestSupport.getRowsWithReplicator(RecoveryTest.masterServer, input, null, null);
        generateNewMasterData(false, RecoveryTest.DATA_SIZE);
        RecoveryTest.slaveServer.waitForSlaveToBeCurrent(RecoveryTest.masterServer);
        RecoveryInfo recoveryInfo = slaveContext.getRecoveryInfo();
        Assert.assertThat(recoveryInfo, notNullValue());
        /* pretend that we're a seperate client trying to recover now */
        recoveryInfo.clientID = "another_client";
        MaxwellConfig slaveConfig = getConfig(RecoveryTest.slaveServer.getPort(), true);
        Recovery recovery = new Recovery(slaveConfig.maxwellMysql, slaveConfig.databaseName, slaveContext.getReplicationConnectionPool(), slaveContext.getCaseSensitivity(), recoveryInfo);
        Assert.assertEquals(null, recovery.recover());
    }

    @Test
    public void testRecoveryIntegration() throws Exception {
        if (MaxwellTestSupport.inGtidMode()) {
            RecoveryTest.LOGGER.info("No need to test recovery under gtid-mode");
            return;
        }
        String[] input = generateMasterData();
        /* run the execution through with the replicator running so we get heartbeats */
        List<RowMap> rows = MaxwellTestSupport.getRowsWithReplicator(RecoveryTest.masterServer, input, null, null);
        Position approximateRecoverPosition = MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection());
        RecoveryTest.LOGGER.warn(("slave master position at time of cut: " + approximateRecoverPosition));
        generateNewMasterData(false, RecoveryTest.DATA_SIZE);
        BufferedMaxwell maxwell = new BufferedMaxwell(getConfig(RecoveryTest.slaveServer.getPort(), true));
        new Thread(maxwell).start();
        drainReplication(maxwell, rows);
        for (long i = 0; i < ((RecoveryTest.DATA_SIZE) + (RecoveryTest.NEW_DATA_SIZE)); i++) {
            Assert.assertEquals((i + 1), rows.get(((int) (i))).getData("id"));
        }
        // assert that we created a schema that matches up with the matched position.
        ResultSet rs = RecoveryTest.slaveServer.getConnection().createStatement().executeQuery("select * from maxwell.schemas");
        boolean foundSchema = false;
        while (rs.next()) {
            if ((rs.getLong("server_id")) == 12345) {
                foundSchema = true;
                rs.getLong("base_schema_id");
                Assert.assertEquals(false, rs.wasNull());
            }
        } 
        Assert.assertEquals(true, foundSchema);
        maxwell.terminate();
        // assert that we deleted the old position row
        rs = RecoveryTest.slaveServer.getConnection().createStatement().executeQuery("select * from maxwell.positions");
        rs.next();
        Assert.assertEquals(12345, rs.getLong("server_id"));
        assert !(rs.next());
    }

    @Test
    public void testRecoveryIntegrationWithLaggedMaxwell() throws Exception {
        if (MaxwellTestSupport.inGtidMode()) {
            RecoveryTest.LOGGER.info("No need to test recovery under gtid-mode");
            return;
        }
        final String[] input = generateMasterData();
        MaxwellTestSupportCallback callback = new MaxwellTestSupportCallback() {
            @Override
            public void afterReplicatorStart(MysqlIsolatedServer mysql) throws SQLException {
                mysql.executeList(Arrays.asList(input));
            }

            @Override
            public void beforeTerminate(MysqlIsolatedServer mysql) {
                /* record some queries.  maxwell may continue to heartbeat but we will be behind. */
                try {
                    RecoveryTest.LOGGER.warn(("slave master position at time of cut: " + (MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection()))));
                    mysql.executeList(Arrays.asList(input));
                    mysql.execute("FLUSH LOGS");
                    mysql.executeList(Arrays.asList(input));
                    mysql.execute("FLUSH LOGS");
                } catch (Exception e) {
                    RecoveryTest.LOGGER.error("Exception in beforeTerminate:", e);
                }
            }
        };
        List<RowMap> rows = MaxwellTestSupport.getRowsWithReplicator(RecoveryTest.masterServer, callback, ( c) -> {
        });
        int expectedRows = input.length;
        Assert.assertEquals(expectedRows, rows.size());
        // we executed 2*input in beforeTerminate, as lag
        expectedRows += (input.length) * 2;
        // now add some more data
        generateNewMasterData(false, RecoveryTest.DATA_SIZE);
        expectedRows += RecoveryTest.NEW_DATA_SIZE;
        BufferedMaxwell maxwell = new BufferedMaxwell(getConfig(RecoveryTest.slaveServer.getPort(), true));
        new Thread(maxwell).start();
        drainReplication(maxwell, rows);
        Assert.assertEquals(expectedRows, rows.size());
        HashSet<Long> ids = new HashSet<>();
        for (RowMap r : rows) {
            Long id = ((Long) (r.getData("id")));
            if (id != null)
                ids.add(id);

        }
        for (long id = 1; id < (expectedRows + 1); id++) {
            Assert.assertEquals((((("didn't find id " + id) + " (out of ") + expectedRows) + " rows)"), true, ids.contains(id));
        }
        maxwell.terminate();
    }

    @Test
    public void testFailOver() throws Exception {
        String[] input = generateMasterData();
        // Have maxwell connect to master first
        List<RowMap> rows = MaxwellTestSupport.getRowsWithReplicator(RecoveryTest.masterServer, input, null, null);
        int expectedRowCount = RecoveryTest.DATA_SIZE;
        RecoveryTest.slaveServer.waitForSlaveToBeCurrent(RecoveryTest.masterServer);
        Position slavePosition1 = MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection());
        RecoveryTest.LOGGER.info(((("slave master position at time of cut: " + slavePosition1) + " rows: ") + (rows.size())));
        Assert.assertEquals(expectedRowCount, rows.size());
        // add 100 rows on master side
        generateNewMasterData(true, RecoveryTest.DATA_SIZE);
        expectedRowCount += RecoveryTest.NEW_DATA_SIZE;
        // connect to slave, maxwell should get these 100 rows from slave
        boolean masterRecovery = !(MaxwellTestSupport.inGtidMode());
        BufferedMaxwell maxwell = new BufferedMaxwell(getConfig(RecoveryTest.slaveServer.getPort(), masterRecovery));
        new Thread(maxwell).start();
        drainReplication(maxwell, rows);
        maxwell.terminate();
        Assert.assertEquals(expectedRowCount, rows.size());
        Position slavePosition2 = MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection());
        RecoveryTest.LOGGER.info(((("slave master position after failover: " + slavePosition2) + " rows: ") + (rows.size())));
        Assert.assertTrue(slavePosition2.newerThan(slavePosition1));
        // add another 100 rows on slave side
        generateNewMasterData(false, ((RecoveryTest.DATA_SIZE) + (RecoveryTest.NEW_DATA_SIZE)));
        expectedRowCount += RecoveryTest.NEW_DATA_SIZE;
        // reconnect to slave to resume, maxwell should get the new 100 rows
        maxwell = new BufferedMaxwell(getConfig(RecoveryTest.slaveServer.getPort(), false));
        new Thread(maxwell).start();
        drainReplication(maxwell, rows);
        Assert.assertEquals(expectedRowCount, rows.size());
        maxwell.terminate();
        Position slavePosition3 = MaxwellTestSupport.capture(RecoveryTest.slaveServer.getConnection());
        RecoveryTest.LOGGER.info(((("slave master position after resumption: " + slavePosition3) + " rows: ") + (rows.size())));
        Assert.assertTrue(slavePosition3.newerThan(slavePosition2));
        for (long i = 0; i < expectedRowCount; i++) {
            RowMap row = rows.get(((int) (i)));
            Assert.assertEquals((i + 1), row.getData("id"));
        }
    }

    @Test
    public void testSchemaIdRestore() throws Exception {
        MysqlIsolatedServer server = RecoveryTest.masterServer;
        Position oldlogPosition = MaxwellTestSupport.capture(server.getConnection());
        RecoveryTest.LOGGER.info(("Initial pos: " + oldlogPosition));
        MaxwellContext context = getContext(server.getPort(), false);
        context.getPositionStore().set(oldlogPosition);
        MysqlSavedSchema savedSchema = MysqlSavedSchema.restore(context, oldlogPosition);
        if (savedSchema == null) {
            Connection c = context.getMaxwellConnection();
            Schema newSchema = capture();
            savedSchema = new MysqlSavedSchema(context, newSchema, context.getInitialPosition());
            savedSchema.save(c);
        }
        Long oldSchemaId = savedSchema.getSchemaID();
        RecoveryTest.LOGGER.info(("old schema id: " + oldSchemaId));
        server.execute("CREATE TABLE shard_1.new (id int(11))");
        BufferedMaxwell maxwell = new BufferedMaxwell(getConfig(server.getPort(), false));
        List<RowMap> rows = new ArrayList<>();
        new Thread(maxwell).start();
        drainReplication(maxwell, rows);
        maxwell.terminate();
        Position newPosition = MaxwellTestSupport.capture(server.getConnection());
        RecoveryTest.LOGGER.info(("New pos: " + newPosition));
        MysqlSavedSchema newSavedSchema = MysqlSavedSchema.restore(context, newPosition);
        RecoveryTest.LOGGER.info(("New schema id: " + (newSavedSchema.getSchemaID())));
        Assert.assertEquals(new Long((oldSchemaId + 1)), newSavedSchema.getSchemaID());
        Assert.assertTrue(newPosition.newerThan(savedSchema.getPosition()));
        MysqlSavedSchema restored = MysqlSavedSchema.restore(context, oldlogPosition);
        Assert.assertEquals(oldSchemaId, restored.getSchemaID());
    }
}

