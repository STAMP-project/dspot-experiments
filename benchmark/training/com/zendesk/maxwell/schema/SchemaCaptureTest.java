package com.zendesk.maxwell.schema;


import com.zendesk.maxwell.CaseSensitivity;
import com.zendesk.maxwell.MaxwellTestSupport;
import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.MysqlIsolatedServer;
import com.zendesk.maxwell.schema.columndef.BigIntColumnDef;
import com.zendesk.maxwell.schema.columndef.DateTimeColumnDef;
import com.zendesk.maxwell.schema.columndef.IntColumnDef;
import com.zendesk.maxwell.schema.columndef.TimeColumnDef;
import com.zendesk.maxwell.schema.ddl.InvalidSchemaError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SchemaCaptureTest extends MaxwellTestWithIsolatedServer {
    private SchemaCapturer capturer;

    @Test
    public void testDatabases() throws InvalidSchemaError, SQLException {
        Schema s = capturer.capture();
        String dbs = StringUtils.join(s.getDatabaseNames().iterator(), ":");
        if (MaxwellTestWithIsolatedServer.server.getVersion().atLeast(SchemaCaptureTest.server.VERSION_5_7))
            Assert.assertEquals("maxwell:mysql:shard_1:shard_2:sys:test", dbs);
        else
            Assert.assertEquals("maxwell:mysql:shard_1:shard_2:test", dbs);

    }

    @Test
    public void testOneDatabase() throws InvalidSchemaError, SQLException {
        SchemaCapturer sc = new SchemaCapturer(MaxwellTestWithIsolatedServer.server.getConnection(), CaseSensitivity.CASE_SENSITIVE, "shard_1");
        Schema s = sc.capture();
        String dbs = StringUtils.join(s.getDatabaseNames().iterator(), ":");
        Assert.assertEquals("shard_1", dbs);
    }

    @Test
    public void testTables() throws InvalidSchemaError, SQLException {
        Schema s = capturer.capture();
        Database shard1DB = s.findDatabase("shard_1");
        assert shard1DB != null;
        List<String> nameList = shard1DB.getTableNames();
        Assert.assertEquals("ints:mediumints:minimal:sharded", StringUtils.join(nameList.iterator(), ":"));
    }

    @Test
    public void testColumns() throws InvalidSchemaError, SQLException {
        Schema s = capturer.capture();
        Table sharded = s.findDatabase("shard_1").findTable("sharded");
        assert sharded != null;
        ColumnDef[] columns;
        columns = sharded.getColumnList().toArray(new ColumnDef[0]);
        Assert.assertThat(columns[0], CoreMatchers.notNullValue());
        Assert.assertThat(columns[0], CoreMatchers.instanceOf(BigIntColumnDef.class));
        Assert.assertThat(columns[0].getName(), CoreMatchers.is("id"));
        Assert.assertEquals(0, columns[0].getPos());
        Assert.assertThat(columns[1], CoreMatchers.allOf(CoreMatchers.notNullValue(), CoreMatchers.instanceOf(IntColumnDef.class)));
        Assert.assertThat(columns[1].getName(), CoreMatchers.is("account_id"));
        Assert.assertThat(columns[1], CoreMatchers.instanceOf(IntColumnDef.class));
        Assert.assertThat(isSigned(), CoreMatchers.is(false));
        if (MaxwellTestWithIsolatedServer.server.getVersion().atLeast(SchemaCaptureTest.server.VERSION_5_6)) {
            Assert.assertThat(columns[10].getName(), CoreMatchers.is("timestamp2_field"));
            Assert.assertThat(columns[10], CoreMatchers.instanceOf(DateTimeColumnDef.class));
            Assert.assertThat(getColumnLength(), CoreMatchers.is(3L));
            Assert.assertThat(columns[11].getName(), CoreMatchers.is("datetime2_field"));
            Assert.assertThat(columns[11], CoreMatchers.instanceOf(DateTimeColumnDef.class));
            Assert.assertThat(getColumnLength(), CoreMatchers.is(6L));
            Assert.assertThat(columns[12].getName(), CoreMatchers.is("time2_field"));
            Assert.assertThat(columns[12], CoreMatchers.instanceOf(TimeColumnDef.class));
            Assert.assertThat(getColumnLength(), CoreMatchers.is(6L));
        }
    }

    @Test
    public void testPKs() throws InvalidSchemaError, SQLException {
        Schema s = capturer.capture();
        Table sharded = s.findDatabase("shard_1").findTable("sharded");
        List<String> pk = sharded.getPKList();
        Assert.assertThat(pk, CoreMatchers.notNullValue());
        Assert.assertThat(pk.size(), CoreMatchers.is(2));
        Assert.assertThat(pk.get(0), CoreMatchers.is("id"));
        Assert.assertThat(pk.get(1), CoreMatchers.is("account_id"));
    }

    @Test
    public void testEnums() throws InvalidSchemaError, IOException, SQLException {
        byte[] sql = Files.readAllBytes(Paths.get(((MaxwellTestSupport.getSQLDir()) + "/schema/enum.sql")));
        MaxwellTestWithIsolatedServer.server.executeList(Collections.singletonList(new String(sql)));
        Schema s = capturer.capture();
        Table enumTest = s.findDatabase("test").findTable("enum_test");
        assert enumTest != null;
        ColumnDef[] columns = enumTest.getColumnList().toArray(new ColumnDef[0]);
        Assert.assertThat(columns[0], CoreMatchers.notNullValue());
        Assert.assertThat(columns[0], CoreMatchers.instanceOf(EnumColumnDef.class));
        Assert.assertThat(columns[0].getName(), CoreMatchers.is("language"));
        Assert.assertArrayEquals(getEnumValues(), new String[]{ "en-US", "de-DE" });
        Assert.assertThat(columns[1], CoreMatchers.notNullValue());
        Assert.assertThat(columns[1], CoreMatchers.instanceOf(EnumColumnDef.class));
        Assert.assertThat(columns[1].getName(), CoreMatchers.is("decimal_separator"));
        Assert.assertArrayEquals(getEnumValues(), new String[]{ ",", "." });
    }

    @Test
    public void testExtractEnumValues() throws Exception {
        String expandedType = "enum('a')";
        String[] result = SchemaCapturer.extractEnumValues(expandedType);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals("a", result[0]);
        expandedType = "enum('a','b','c','d')";
        result = SchemaCapturer.extractEnumValues(expandedType);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals("a", result[0]);
        Assert.assertEquals("b", result[1]);
        Assert.assertEquals("c", result[2]);
        Assert.assertEquals("d", result[3]);
        expandedType = "enum('','b','c','d')";
        result = SchemaCapturer.extractEnumValues(expandedType);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals("", result[0]);
        Assert.assertEquals("b", result[1]);
        Assert.assertEquals("c", result[2]);
        Assert.assertEquals("d", result[3]);
        expandedType = "enum(\'a\',\'b\'b\',\'c\')";
        result = SchemaCapturer.extractEnumValues(expandedType);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals("a", result[0]);
        Assert.assertEquals("b'b", result[1]);
        Assert.assertEquals("c", result[2]);
        expandedType = "enum(\'\',\'.\',\',\',\'\\\',\'\\\'\',\'\\,\',\',\'\',\'b\')";
        result = SchemaCapturer.extractEnumValues(expandedType);
        Assert.assertEquals(8, result.length);
        Assert.assertEquals("", result[0]);
        Assert.assertEquals(".", result[1]);
        Assert.assertEquals(",", result[2]);
        Assert.assertEquals("\\", result[3]);
        Assert.assertEquals("\\\'", result[4]);
        Assert.assertEquals("\\,", result[5]);
        Assert.assertEquals(",'", result[6]);
        Assert.assertEquals("b", result[7]);
    }
}

