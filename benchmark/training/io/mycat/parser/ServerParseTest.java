package io.mycat.parser;


import ServerParse.BEGIN;
import ServerParse.CALL;
import ServerParse.COMMIT;
import ServerParse.DELETE;
import ServerParse.DESCRIBE;
import ServerParse.EXPLAIN;
import ServerParse.HELP;
import ServerParse.INSERT;
import ServerParse.KILL;
import ServerParse.KILL_QUERY;
import ServerParse.REPLACE;
import ServerParse.ROLLBACK;
import ServerParse.SAVEPOINT;
import ServerParse.SELECT;
import ServerParse.SET;
import ServerParse.SHOW;
import ServerParse.START;
import ServerParse.UPDATE;
import ServerParse.USE;
import io.mycat.server.parser.ServerParse;
import junit.framework.Assert;
import org.junit.Test;


public class ServerParseTest {
    /**
     * public static final int OTHER = -1;
     * public static final int BEGIN = 1;
     * public static final int COMMIT = 2;
     * public static final int DELETE = 3;
     * public static final int INSERT = 4;
     * public static final int REPLACE = 5;
     * public static final int ROLLBACK = 6;
     * public static final int SELECT = 7;
     * public static final int SET = 8;
     * public static final int SHOW = 9;
     * public static final int START = 10;
     * public static final int UPDATE = 11;
     * public static final int KILL = 12;
     * public static final int SAVEPOINT = 13;
     * public static final int USE = 14;
     * public static final int EXPLAIN = 15;
     * public static final int KILL_QUERY = 16;
     * public static final int HELP = 17;
     * public static final int MYSQL_CMD_COMMENT = 18;
     * public static final int MYSQL_COMMENT = 19;
     * public static final int CALL = 20;
     * public static final int DESCRIBE = 21;
     */
    @Test
    public void testDesc() {
        String sql = "desc a";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(DESCRIBE, sqlType);
    }

    @Test
    public void testDescribe() {
        String sql = "describe a";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(DESCRIBE, sqlType);
    }

    @Test
    public void testDelete() {
        String sql = "delete from a where id = 1";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(DELETE, sqlType);
    }

    @Test
    public void testInsert() {
        String sql = "insert into a(name) values ('zhangsan')";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(INSERT, sqlType);
    }

    @Test
    public void testReplace() {
        String sql = "replace into t(id, update_time) select 1, now();  ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(REPLACE, sqlType);
    }

    @Test
    public void testSet() {
        String sql = "SET @var_name = 'value';  ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(SET, sqlType);
    }

    @Test
    public void testShow() {
        String sql = "show full tables";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(SHOW, sqlType);
    }

    @Test
    public void testStart() {
        String sql = "start ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(START, sqlType);
    }

    @Test
    public void testUpdate() {
        String sql = "update a set name='wdw' where id = 1";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(UPDATE, sqlType);
    }

    @Test
    public void testKill() {
        String sql = "kill 1";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(KILL, sqlType);
    }

    @Test
    public void testSavePoint() {
        String sql = "SAVEPOINT ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(SAVEPOINT, sqlType);
    }

    @Test
    public void testUse() {
        String sql = "use db1 ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(USE, sqlType);
    }

    @Test
    public void testExplain() {
        String sql = "explain select * from a ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(EXPLAIN, sqlType);
    }

    @Test
    public void testKillQuery() {
        String sql = "kill query 1102 ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(KILL_QUERY, sqlType);
    }

    @Test
    public void testHelp() {
        String sql = "help contents ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(HELP, sqlType);
    }

    @Test
    public void testCall() {
        String sql = "CALL demo_in_parameter(@p_in); ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(CALL, sqlType);
    }

    @Test
    public void testRollback() {
        String sql = "rollback; ";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(ROLLBACK, sqlType);
    }

    @Test
    public void testSelect() {
        String sql = "select * from a";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(SELECT, sqlType);
    }

    @Test
    public void testBegin() {
        String sql = "begin";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(BEGIN, sqlType);
    }

    @Test
    public void testCommit() {
        String sql = "COMMIT 'nihao'";
        int result = ServerParse.parse(sql);
        int sqlType = result & 255;
        Assert.assertEquals(COMMIT, sqlType);
    }
}

