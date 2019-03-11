package org.sqlite;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;
import org.sqlite.jdbc3.JDBC3Statement;
import org.sqlite.jdbc4.JDBC4Statement;


/**
 * These tests are designed to stress Statements on memory databases.
 */
public class StatementTest {
    private Connection conn;

    private Statement stat;

    @Test
    public void executeUpdate() throws SQLException {
        Assert.assertEquals(stat.executeUpdate("create table s1 (c1);"), 0);
        Assert.assertEquals(stat.executeUpdate("insert into s1 values (0);"), 1);
        Assert.assertEquals(stat.executeUpdate("insert into s1 values (1);"), 1);
        Assert.assertEquals(stat.executeUpdate("insert into s1 values (2);"), 1);
        Assert.assertEquals(stat.executeUpdate("update s1 set c1 = 5;"), 3);
        // count_changes_pgrama. truncate_optimization
        Assert.assertEquals(stat.executeUpdate("delete from s1;"), 3);
        // multiple SQL statements
        Assert.assertEquals(stat.executeUpdate(("insert into s1 values (11);" + "insert into s1 values (12)")), 2);
        Assert.assertEquals(stat.executeUpdate(("update s1 set c1 = 21 where c1 = 11;" + ("update s1 set c1 = 22 where c1 = 12;" + "update s1 set c1 = 23 where c1 = 13"))), 2);// c1 = 13 does not exist

        Assert.assertEquals(stat.executeUpdate(("delete from s1 where c1 = 21;" + ("delete from s1 where c1 = 22;" + "delete from s1 where c1 = 23"))), 2);// c1 = 23 does not exist

        Assert.assertEquals(stat.executeUpdate("drop table s1;"), 0);
    }

    @Test
    public void emptyRS() throws SQLException {
        ResultSet rs = stat.executeQuery("select null limit 0;");
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void singleRowRS() throws SQLException {
        ResultSet rs = stat.executeQuery((("select " + (Integer.MAX_VALUE)) + ";"));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), Integer.MAX_VALUE);
        Assert.assertEquals(rs.getString(1), Integer.toString(Integer.MAX_VALUE));
        Assert.assertEquals(rs.getDouble(1), new Integer(Integer.MAX_VALUE).doubleValue(), 0.001);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void twoRowRS() throws SQLException {
        ResultSet rs = stat.executeQuery("select 9 union all select 7;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 9);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 7);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void autoClose() throws SQLException {
        conn.createStatement().executeQuery("select 1;");
    }

    @Test
    public void stringRS() throws SQLException {
        ResultSet rs = stat.executeQuery("select \"Russell\";");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), "Russell");
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void execute() throws SQLException {
        Assert.assertTrue(stat.execute("select null;"));
        ResultSet rs = stat.getResultSet();
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getString(1));
        Assert.assertTrue(rs.wasNull());
        Assert.assertFalse(stat.getMoreResults());
        Assert.assertEquals(stat.getUpdateCount(), (-1));
        Assert.assertTrue(stat.execute("select null;"));
        Assert.assertFalse(stat.getMoreResults());
        Assert.assertEquals(stat.getUpdateCount(), (-1));
        Assert.assertFalse(stat.execute("create table test (c1);"));
        Assert.assertEquals(stat.getUpdateCount(), 0);
        Assert.assertFalse(stat.getMoreResults());
        Assert.assertEquals(stat.getUpdateCount(), (-1));
    }

    @Test
    public void colNameAccess() throws SQLException {
        Assert.assertEquals(stat.executeUpdate("create table tab (id, firstname, surname);"), 0);
        Assert.assertEquals(stat.executeUpdate("insert into tab values (0, 'Bob', 'Builder');"), 1);
        Assert.assertEquals(stat.executeUpdate("insert into tab values (1, 'Fred', 'Blogs');"), 1);
        Assert.assertEquals(stat.executeUpdate("insert into tab values (2, 'John', 'Smith');"), 1);
        ResultSet rs = stat.executeQuery("select * from tab;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt("id"), 0);
        Assert.assertEquals(rs.getString("firstname"), "Bob");
        Assert.assertEquals(rs.getString("surname"), "Builder");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt("id"), 1);
        Assert.assertEquals(rs.getString("firstname"), "Fred");
        Assert.assertEquals(rs.getString("surname"), "Blogs");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt("id"), 2);
        Assert.assertEquals(rs.getString("id"), "2");
        Assert.assertEquals(rs.getString("firstname"), "John");
        Assert.assertEquals(rs.getString("surname"), "Smith");
        Assert.assertFalse(rs.next());
        rs.close();
        Assert.assertEquals(stat.executeUpdate("drop table tab;"), 0);
    }

    @Test
    public void nulls() throws SQLException {
        ResultSet rs = stat.executeQuery("select null union all select null;");
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getString(1));
        Assert.assertTrue(rs.wasNull());
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getString(1));
        Assert.assertTrue(rs.wasNull());
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void nullsForGetObject() throws SQLException {
        ResultSet rs = stat.executeQuery("select 1, null union all select null, null;");
        Assert.assertTrue(rs.next());
        Assert.assertNotNull(rs.getString(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertNull(rs.getObject(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getObject(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertNull(rs.getObject(1));
        Assert.assertTrue(rs.wasNull());
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void tempTable() throws SQLException {
        Assert.assertEquals(stat.executeUpdate("create temp table myTemp (a);"), 0);
        Assert.assertEquals(stat.executeUpdate("insert into myTemp values (2);"), 1);
    }

    @Test
    public void insert1000() throws SQLException {
        Assert.assertEquals(stat.executeUpdate("create table in1000 (a);"), 0);
        conn.setAutoCommit(false);
        for (int i = 0; i < 1000; i++)
            Assert.assertEquals(stat.executeUpdate((("insert into in1000 values (" + i) + ");")), 1);

        conn.commit();
        ResultSet rs = stat.executeQuery("select count(a) from in1000;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 1000);
        rs.close();
        Assert.assertEquals(stat.executeUpdate("drop table in1000;"), 0);
    }

    @Test
    public void batch() throws SQLException {
        stat.addBatch("create table batch (c1);");
        stat.addBatch("insert into batch values (1);");
        stat.addBatch("insert into batch values (1);");
        stat.addBatch("insert into batch values (2);");
        stat.addBatch("insert into batch values (3);");
        stat.addBatch("insert into batch values (4);");
        assertArrayEq(new int[]{ 0, 1, 1, 1, 1, 1 }, stat.executeBatch());
        assertArrayEq(new int[]{  }, stat.executeBatch());
        stat.clearBatch();
        stat.addBatch("insert into batch values (9);");
        assertArrayEq(new int[]{ 1 }, stat.executeBatch());
        assertArrayEq(new int[]{  }, stat.executeBatch());
        stat.clearBatch();
        stat.addBatch("insert into batch values (7);");
        stat.addBatch("insert into batch values (7);");
        assertArrayEq(new int[]{ 1, 1 }, stat.executeBatch());
        stat.clearBatch();
        ResultSet rs = stat.executeQuery("select count(*) from batch;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(8, rs.getInt(1));
        rs.close();
    }

    @Test
    public void closeOnFalseNext() throws SQLException {
        stat.executeUpdate("create table t1 (c1);");
        conn.createStatement().executeQuery("select * from t1;").next();
        stat.executeUpdate("drop table t1;");
    }

    @Test
    public void getGeneratedKeys() throws SQLException {
        ResultSet rs;
        stat.executeUpdate("create table t1 (c1 integer primary key, v);");
        stat.executeUpdate("insert into t1 (v) values ('red');");
        rs = stat.getGeneratedKeys();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 1);
        rs.close();
        stat.executeUpdate("insert into t1 (v) values ('blue');");
        rs = stat.getGeneratedKeys();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 2);
        rs.close();
        // closing one statement shouldn't close shared db metadata object.
        stat.close();
        Statement stat2 = conn.createStatement();
        rs = stat2.getGeneratedKeys();
        Assert.assertNotNull(rs);
        rs.close();
        stat2.close();
    }

    @Test
    public void isBeforeFirst() throws SQLException {
        ResultSet rs = stat.executeQuery("select 1 union all select 2;");
        Assert.assertTrue(rs.isBeforeFirst());
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.isFirst());
        Assert.assertEquals(rs.getInt(1), 1);
        Assert.assertTrue(rs.next());
        Assert.assertFalse(rs.isBeforeFirst());
        Assert.assertFalse(rs.isFirst());
        Assert.assertEquals(rs.getInt(1), 2);
        Assert.assertFalse(rs.next());
        Assert.assertFalse(rs.isBeforeFirst());
        rs.close();
    }

    @Test
    public void columnNaming() throws SQLException {
        stat.executeUpdate("create table t1 (c1 integer);");
        stat.executeUpdate("create table t2 (c1 integer);");
        stat.executeUpdate("insert into t1 values (1);");
        stat.executeUpdate("insert into t2 values (1);");
        ResultSet rs = stat.executeQuery("select a.c1 AS c1 from t1 a, t2 where a.c1=t2.c1;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt("c1"), 1);
        rs.close();
    }

    @Test
    public void nullDate() throws SQLException {
        ResultSet rs = stat.executeQuery("select null;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDate(1), null);
        Assert.assertEquals(rs.getTime(1), null);
        rs.close();
    }

    @Test(expected = SQLException.class)
    public void failToDropWhenRSOpen() throws SQLException {
        stat.executeUpdate("create table t1 (c1);");
        stat.executeUpdate("insert into t1 values (4);");
        stat.executeUpdate("insert into t1 values (4);");
        conn.createStatement().executeQuery("select * from t1;").next();
        stat.executeUpdate("drop table t1;");
    }

    @Test(expected = SQLException.class)
    public void executeNoRS() throws SQLException {
        Assert.assertFalse(stat.execute("insert into test values (8);"));
        stat.getResultSet();
    }

    @Test(expected = SQLException.class)
    public void executeClearRS() throws SQLException {
        Assert.assertTrue(stat.execute("select null;"));
        Assert.assertNotNull(stat.getResultSet());
        Assert.assertFalse(stat.getMoreResults());
        stat.getResultSet();
    }

    @Test(expected = BatchUpdateException.class)
    public void batchReturnsResults() throws SQLException {
        stat.addBatch("select null;");
        stat.executeBatch();
    }

    @Test(expected = SQLException.class)
    public void noSuchTable() throws SQLException {
        stat.executeQuery("select * from doesnotexist;");
    }

    @Test(expected = SQLException.class)
    public void noSuchCol() throws SQLException {
        stat.executeQuery("select notacol from (select 1);");
    }

    @Test(expected = SQLException.class)
    public void noSuchColName() throws SQLException {
        ResultSet rs = stat.executeQuery("select 1;");
        Assert.assertTrue(rs.next());
        rs.getInt("noSuchColName");
    }

    @Test
    public void multipleStatements() throws SQLException {
        // ; insert into person values(1,'leo')
        stat.executeUpdate(("create table person (id integer, name string); " + "insert into person values(1, 'leo'); insert into person values(2, 'yui');"));
        ResultSet rs = stat.executeQuery("select * from person");
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
    }

    @Test
    public void blobTest() throws SQLException {
        stat.executeUpdate("CREATE TABLE Foo (KeyId INTEGER, Stuff BLOB)");
    }

    @Test
    public void bytesTest() throws UnsupportedEncodingException, SQLException {
        stat.executeUpdate("CREATE TABLE blobs (Blob BLOB)");
        PreparedStatement prep = conn.prepareStatement("insert into blobs values(?)");
        String str = "This is a test";
        byte[] strBytes = str.getBytes("UTF-8");
        prep.setBytes(1, strBytes);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery("select * from blobs");
        Assert.assertTrue(rs.next());
        byte[] resultBytes = rs.getBytes(1);
        Assert.assertArrayEquals(strBytes, resultBytes);
        String resultStr = rs.getString(1);
        Assert.assertEquals(str, resultStr);
        byte[] resultBytesAfterConversionToString = rs.getBytes(1);
        Assert.assertArrayEquals(strBytes, resultBytesAfterConversionToString);
    }

    @Test
    public void dateTimeTest() throws SQLException {
        Date day = new Date(new java.util.Date().getTime());
        stat.executeUpdate("create table day (time datatime)");
        PreparedStatement prep = conn.prepareStatement("insert into day values(?)");
        prep.setDate(1, day);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery("select * from day");
        Assert.assertTrue(rs.next());
        Date d = rs.getDate(1);
        Assert.assertEquals(day.getTime(), d.getTime());
    }

    @Test
    public void maxRows() throws SQLException {
        stat.setMaxRows(1);
        ResultSet rs = stat.executeQuery("select 1 union select 2 union select 3");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertFalse(rs.next());
        rs.close();
        stat.setMaxRows(2);
        rs = stat.executeQuery("select 1 union select 2 union select 3");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void setEscapeProcessingToFals() throws SQLException {
        stat.setEscapeProcessing(false);
    }

    @Test(expected = SQLException.class)
    public void setEscapeProcessingToTrue() throws SQLException {
        stat.setEscapeProcessing(true);
    }

    @Test
    public void unwrapTest() throws SQLException {
        Assert.assertTrue(conn.isWrapperFor(Connection.class));
        Assert.assertFalse(conn.isWrapperFor(Statement.class));
        Assert.assertEquals(conn, conn.unwrap(Connection.class));
        Assert.assertEquals(conn, conn.unwrap(SQLiteConnection.class));
        Assert.assertTrue(stat.isWrapperFor(Statement.class));
        Assert.assertEquals(stat, stat.unwrap(Statement.class));
        Assert.assertEquals(stat, stat.unwrap(JDBC3Statement.class));
        ResultSet rs = stat.executeQuery("select 1");
        Assert.assertTrue(rs.isWrapperFor(ResultSet.class));
        Assert.assertEquals(rs, rs.unwrap(ResultSet.class));
        rs.close();
    }

    @Test
    public void closeOnCompletionTest() throws Exception {
        if (!((stat) instanceof JDBC4Statement))
            return;

        // Run the following code only for JDK7 or higher
        Method mIsCloseOnCompletion = JDBC4Statement.class.getDeclaredMethod("isCloseOnCompletion");
        Method mCloseOnCompletion = JDBC4Statement.class.getDeclaredMethod("closeOnCompletion");
        Assert.assertFalse(((Boolean) (mIsCloseOnCompletion.invoke(stat))));
        mCloseOnCompletion.invoke(stat);
        Assert.assertTrue(((Boolean) (mIsCloseOnCompletion.invoke(stat))));
        ResultSet rs = stat.executeQuery("select 1");
        rs.close();
        Assert.assertTrue(stat.isClosed());
    }
}

