package com.zendesk.maxwell;


import com.zendesk.maxwell.replication.Position;
import com.zendesk.maxwell.schema.MaxwellContext;
import com.zendesk.maxwell.schema.columndef.ColumnDef;
import com.zendesk.maxwell.schema.columndef.DateTimeColumnDef;
import com.zendesk.maxwell.schema.columndef.IntColumnDef;
import com.zendesk.maxwell.schema.columndef.TimeColumnDef;
import com.zendesk.maxwell.schema.ddl.InvalidSchemaError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static CaseSensitivity.CASE_SENSITIVE;


public class MysqlSavedSchemaTest extends MaxwellTestWithIsolatedServer {
    private Schema schema;

    private Position position;

    private MysqlSavedSchema savedSchema;

    private CaseSensitivity caseSensitivity = CASE_SENSITIVE;

    String[] ary = new String[]{ "delete from `maxwell`.`positions`", "delete from `maxwell`.`schemas`", "CREATE TABLE shard_1.latin1 (id int(11), str1 varchar(255), str2 varchar(255) character set 'utf8') charset = 'latin1'", "CREATE TABLE shard_1.enums (id int(11), enum_col enum('foo', 'bar', 'baz'))", "CREATE TABLE shard_1.pks (id int(11), col2 varchar(255), col3 datetime, PRIMARY KEY(col2, col3, id))", "CREATE TABLE shard_1.pks_case (id int(11), Col2 varchar(255), COL3 datetime, PRIMARY KEY(col2, col3))", "CREATE TABLE shard_1.signed (badcol int(10) unsigned, CaseCol char)" };

    ArrayList<String> schemaSQL = new ArrayList(Arrays.asList(ary));

    private MaxwellContext context;

    @Test
    public void testSave() throws InvalidSchemaError, IOException, SQLException {
        this.savedSchema.save(context.getMaxwellConnection());
        MysqlSavedSchema restoredSchema = MysqlSavedSchema.restore(context, context.getInitialPosition());
        List<String> diff = this.schema.diff(restoredSchema.getSchema(), "captured schema", "restored schema");
        Assert.assertThat(StringUtils.join(diff, "\n"), diff.size(), CoreMatchers.is(0));
    }

    @Test
    public void testRestorePK() throws Exception {
        this.savedSchema.save(context.getMaxwellConnection());
        MysqlSavedSchema restoredSchema = MysqlSavedSchema.restore(context, context.getInitialPosition());
        Table t = restoredSchema.getSchema().findDatabase("shard_1").findTable("pks");
        Assert.assertThat(t.getPKList(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        Assert.assertThat(t.getPKList().size(), CoreMatchers.is(3));
        Assert.assertThat(t.getPKList().get(0), CoreMatchers.is("col2"));
        Assert.assertThat(t.getPKList().get(1), CoreMatchers.is("col3"));
        Assert.assertThat(t.getPKList().get(2), CoreMatchers.is("id"));
    }

    @Test
    public void testPKCase() throws Exception {
        this.savedSchema.save(context.getMaxwellConnection());
        MysqlSavedSchema restoredSchema = MysqlSavedSchema.restore(context, context.getInitialPosition());
        Table t = restoredSchema.getSchema().findDatabase("shard_1").findTable("pks_case");
        Assert.assertThat(t.getPKList().get(0), CoreMatchers.is("Col2"));
        Assert.assertThat(t.getPKList().get(1), CoreMatchers.is("COL3"));
    }

    @Test
    public void testTimeWithLengthCase() throws Exception {
        requireMinimumVersion(MysqlSavedSchemaTest.server.VERSION_5_6);
        this.savedSchema.save(context.getMaxwellConnection());
        MysqlSavedSchema restored = MysqlSavedSchema.restore(context, context.getInitialPosition());
        DateTimeColumnDef cd = ((DateTimeColumnDef) (restored.getSchema().findDatabase("shard_1").findTable("time_with_length").findColumn("dt2")));
        Assert.assertThat(cd.getColumnLength(), CoreMatchers.is(3L));
        cd = ((DateTimeColumnDef) (restored.getSchema().findDatabase("shard_1").findTable("time_with_length").findColumn("ts2")));
        Assert.assertThat(cd.getColumnLength(), CoreMatchers.is(6L));
        TimeColumnDef cd2 = ((TimeColumnDef) (restored.getSchema().findDatabase("shard_1").findTable("time_with_length").findColumn("t2")));
        Assert.assertThat(cd2.getColumnLength(), CoreMatchers.is(6L));
    }

    @Test
    public void testFixUnsignedColumnBug() throws Exception {
        Connection c = context.getMaxwellConnection();
        this.savedSchema.save(c);
        c.createStatement().executeUpdate(("update maxwell.schemas set version = 0 where id = " + (this.savedSchema.getSchemaID())));
        c.createStatement().executeUpdate("update maxwell.columns set is_signed = 1 where name = 'badcol'");
        MysqlSavedSchema restored = MysqlSavedSchema.restore(context, context.getInitialPosition());
        IntColumnDef cd = ((IntColumnDef) (restored.getSchema().findDatabase("shard_1").findTable("signed").findColumn("badcol")));
        Assert.assertThat(cd.isSigned(), CoreMatchers.is(false));
    }

    @Test
    public void testFixColumnCasingOnUpgrade() throws Exception {
        Connection c = context.getMaxwellConnection();
        this.savedSchema.save(c);
        c.createStatement().executeUpdate(("update maxwell.schemas set version = 1 where id = " + (this.savedSchema.getSchemaID())));
        c.createStatement().executeUpdate("update maxwell.columns set name = 'casecol' where name = 'CaseCol'");
        MysqlSavedSchema restored = MysqlSavedSchema.restore(context, context.getInitialPosition());
        ColumnDef cd = restored.getSchema().findDatabase("shard_1").findTable("signed").findColumn("casecol");
        Assert.assertThat(cd.getName(), CoreMatchers.is("CaseCol"));
    }

    @Test
    public void testUpgradeSchemaStore() throws Exception {
        Connection c = context.getMaxwellConnection();
        c.createStatement().executeUpdate(("alter table `maxwell`.`schemas` drop column deleted, " + "drop column base_schema_id, drop column deltas, drop column version, drop column position_sha"));
        c.createStatement().executeUpdate("alter table maxwell.positions drop column client_id");
        c.createStatement().executeUpdate("alter table maxwell.positions drop column gtid_set");
        c.createStatement().executeUpdate("alter table maxwell.schemas drop column gtid_set");
        SchemaStoreSchema.upgradeSchemaStoreSchema(c);// just verify no-crash.

    }

    @Test
    public void testUpgradeAddColumnLength() throws Exception {
        requireMinimumVersion(MysqlSavedSchemaTest.server.VERSION_5_6);
        Connection c = context.getMaxwellConnection();
        this.savedSchema.save(c);
        c.createStatement().executeUpdate("alter table `maxwell`.`columns` drop column column_length");
        SchemaStoreSchema.upgradeSchemaStoreSchema(c);// just verify no-crash.

        Schema schemaBefore = MysqlSavedSchema.restoreFromSchemaID(this.savedSchema, context).getSchema();
        DateTimeColumnDef cd1 = ((DateTimeColumnDef) (schemaBefore.findDatabase("shard_1").findTable("without_col_length").findColumn("badcol")));
        Assert.assertEquals(((Long) (0L)), ((Long) (cd1.getColumnLength())));
    }

    @Test
    public void testUpgradeAddColumnLengthForExistingSchemas() throws Exception {
        requireMinimumVersion(MysqlSavedSchemaTest.server.VERSION_5_6);
        Connection c = context.getMaxwellConnection();
        this.savedSchema.save(c);
        c.createStatement().executeUpdate(("update maxwell.schemas set version = 2 where id = " + (this.savedSchema.getSchemaID())));
        c.createStatement().executeUpdate("update maxwell.columns set column_length = NULL where name = 'badcol'");
        SchemaStoreSchema.upgradeSchemaStoreSchema(c);
        Schema schemaBefore = MysqlSavedSchema.restoreFromSchemaID(savedSchema, context).getSchema();
        DateTimeColumnDef cd1 = ((DateTimeColumnDef) (schemaBefore.findDatabase("shard_1").findTable("without_col_length").findColumn("badcol")));
        Assert.assertEquals(((Long) (0L)), ((Long) (cd1.getColumnLength())));
        MysqlSavedSchema restored = MysqlSavedSchema.restore(context, context.getInitialPosition());
        DateTimeColumnDef cd = ((DateTimeColumnDef) (restored.getSchema().findDatabase("shard_1").findTable("without_col_length").findColumn("badcol")));
        Assert.assertEquals(((Long) (3L)), ((Long) (cd.getColumnLength())));
    }

    @Test
    public void testTableWithSameNameInTwoDBs() throws Exception {
        String[] sql = new String[]{ "create database dd1", "create database dd2", "create TABLE dd1.t1( i int )", "create TABLE dd2.t1( i int )" };
        MaxwellTestWithIsolatedServer.server.executeList(sql);
        this.schema = capture();
        this.savedSchema = new MysqlSavedSchema(this.context, this.schema, position);
        Connection c = context.getMaxwellConnection();
        this.savedSchema.save(c);
        MysqlSavedSchema restoredSchema = MysqlSavedSchema.restore(context, context.getInitialPosition());
        Table t = restoredSchema.getSchema().findDatabase("dd2").findTable("t1");
        Assert.assertThat(t, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testFindSchemaReturnsTheLatestSchemaForTheCurrentBinlog() throws Exception {
        if (context.getConfig().gtidMode) {
            return;
        }
        Connection c = context.getMaxwellConnection();
        long serverId = 100;
        long targetPosition = 500;
        long lastHeartbeat = 9000L;
        String targetFile = "binlog08";
        String previousFile = "binlog07";
        String newerFile = "binlog09";
        Position targetBinlogPosition = makePosition(targetPosition, targetFile, (lastHeartbeat + 10L));
        MysqlSavedSchema expectedSchema = new MysqlSavedSchema(serverId, caseSensitivity, buildSchema(), makePosition((targetPosition - 50L), targetFile, lastHeartbeat));
        expectedSchema.save(c);
        // older binlog position
        saveSchema(c);
        // Newer binlog position but older heartbeat
        saveSchema(c);
        populateSchemasSurroundingTarget(c, serverId, targetBinlogPosition, lastHeartbeat, previousFile, newerFile);
        MysqlSavedSchema foundSchema = MysqlSavedSchema.restore(context.getMaxwellConnectionPool(), serverId, caseSensitivity, targetBinlogPosition);
        Assert.assertThat(foundSchema.getBinlogPosition(), CoreMatchers.equalTo(expectedSchema.getBinlogPosition()));
        Assert.assertThat(foundSchema.getSchemaID(), CoreMatchers.equalTo(expectedSchema.getSchemaID()));
    }

    @Test
    public void testFindSchemaReturnsTheLatestSchemaForPreviousBinlog() throws Exception {
        if (context.getConfig().gtidMode) {
            return;
        }
        Connection c = context.getMaxwellConnection();
        long serverId = 100;
        long targetPosition = 500;
        long lastHeartbeat = 9000L;
        String targetFile = "binlog08";
        String previousFile = "binlog07";
        String newerFile = "binlog09";
        Position targetBinlogPosition = makePosition(targetPosition, targetFile, (lastHeartbeat + 10L));
        // the newest schema:
        MysqlSavedSchema expectedSchema = new MysqlSavedSchema(serverId, caseSensitivity, buildSchema(), makePosition((targetPosition + 50L), previousFile, lastHeartbeat));
        expectedSchema.save(c);
        // Newer binlog position but older heartbeat
        saveSchema(c);
        populateSchemasSurroundingTarget(c, serverId, targetBinlogPosition, lastHeartbeat, previousFile, newerFile);
        MysqlSavedSchema foundSchema = MysqlSavedSchema.restore(context.getMaxwellConnectionPool(), serverId, caseSensitivity, targetBinlogPosition);
        Assert.assertThat(foundSchema.getBinlogPosition(), CoreMatchers.equalTo(expectedSchema.getBinlogPosition()));
        Assert.assertThat(foundSchema.getSchemaID(), CoreMatchers.equalTo(expectedSchema.getSchemaID()));
    }
}

