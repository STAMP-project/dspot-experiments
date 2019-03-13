package com.zendesk.maxwell.schema;


import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.replication.BinlogPosition;
import com.zendesk.maxwell.replication.Position;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MysqlSchemaStoreTest extends MaxwellTestWithIsolatedServer {
    @Test
    public void testGetSchemaID() throws Exception {
        BinlogPosition bl_pos = new BinlogPosition(0, "mysql.1234");
        Position pos = new Position(bl_pos, 1);
        MysqlSchemaStore schemaStore = new MysqlSchemaStore(buildContext(), pos);
        Assert.assertThat(schemaStore.getSchemaID(), CoreMatchers.is(1L));
        String sql = "CREATE DATABASE `testdb`;";
        String db = "testdb";
        BinlogPosition bl_pos2 = new BinlogPosition(1, "mysql.1234");
        Position pos2 = new Position(bl_pos2, 1);
        schemaStore.processSQL(sql, db, pos2);
        Assert.assertThat(schemaStore.getSchemaID(), CoreMatchers.is(2L));
    }
}

