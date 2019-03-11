package org.sqlite;


import SQLiteErrorCode.SQLITE_CONSTRAINT.code;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import org.junit.Assert;
import org.junit.Test;


/**
 * These tests are designed to stress PreparedStatements on memory dbs.
 */
public class PrepStmtTest {
    static byte[] b1 = new byte[]{ 1, 2, 7, 4, 2, 6, 2, 8, 5, 2, 3, 1, 5, 3, 6, 3, 3, 6, 2, 5 };

    static byte[] b2 = PrepStmtTest.getUtf8Bytes("To be or not to be.");

    static byte[] b3 = PrepStmtTest.getUtf8Bytes("Question!#$%");

    static String utf01 = "\ud840\udc40";

    static String utf02 = "\ud840\udc47 ";

    static String utf03 = " \ud840\udc43";

    static String utf04 = " \ud840\udc42 ";

    static String utf05 = "\ud840\udc40\ud840\udc44";

    static String utf06 = "Hello World, \ud840\udc40 \ud880\udc99";

    static String utf07 = "\ud840\udc41 testing \ud880\udc99";

    static String utf08 = "\ud840\udc40\ud840\udc44 testing";

    private Connection conn;

    private Statement stat;

    @Test
    public void update() throws SQLException {
        Assert.assertEquals(conn.prepareStatement("create table s1 (c1);").executeUpdate(), 0);
        PreparedStatement prep = conn.prepareStatement("insert into s1 values (?);");
        prep.setInt(1, 3);
        Assert.assertEquals(prep.executeUpdate(), 1);
        Assert.assertNull(prep.getResultSet());
        prep.setInt(1, 5);
        Assert.assertEquals(prep.executeUpdate(), 1);
        prep.setInt(1, 7);
        Assert.assertEquals(prep.executeUpdate(), 1);
        prep.close();
        // check results with normal statement
        ResultSet rs = stat.executeQuery("select sum(c1) from s1;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 15);
        rs.close();
    }

    @Test
    public void multiUpdate() throws SQLException {
        stat.executeUpdate("create table test (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into test values (?);");
        for (int i = 0; i < 10; i++) {
            prep.setInt(1, i);
            prep.executeUpdate();
            prep.execute();
        }
        prep.close();
        stat.executeUpdate("drop table test;");
    }

    @Test
    public void emptyRS() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select null limit 0;");
        ResultSet rs = prep.executeQuery();
        Assert.assertFalse(rs.next());
        rs.close();
        prep.close();
    }

    @Test
    public void singleRowRS() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ?;");
        prep.setInt(1, Integer.MAX_VALUE);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), Integer.MAX_VALUE);
        Assert.assertEquals(rs.getString(1), Integer.toString(Integer.MAX_VALUE));
        Assert.assertEquals(rs.getDouble(1), new Integer(Integer.MAX_VALUE).doubleValue(), 1.0E-4);
        Assert.assertFalse(rs.next());
        rs.close();
        prep.close();
    }

    @Test
    public void twoRowRS() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ? union all select ?;");
        prep.setDouble(1, Double.MAX_VALUE);
        prep.setDouble(2, Double.MIN_VALUE);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDouble(1), Double.MAX_VALUE, 1.0E-4);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDouble(1), Double.MIN_VALUE, 1.0E-4);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void stringRS() throws SQLException {
        String name = "Gandhi";
        PreparedStatement prep = conn.prepareStatement("select ?;");
        prep.setString(1, name);
        ResultSet rs = prep.executeQuery();
        Assert.assertEquals((-1), prep.getUpdateCount());
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), name);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void finalizePrep() throws SQLException {
        conn.prepareStatement("select null;");
        System.gc();
    }

    @Test
    public void set() throws UnsupportedEncodingException, SQLException {
        ResultSet rs;
        PreparedStatement prep = conn.prepareStatement("select ?, ?, ?;");
        // integers
        prep.setInt(1, Integer.MIN_VALUE);
        prep.setInt(2, Integer.MAX_VALUE);
        prep.setInt(3, 0);
        rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), Integer.MIN_VALUE);
        Assert.assertEquals(rs.getInt(2), Integer.MAX_VALUE);
        Assert.assertEquals(rs.getInt(3), 0);
        // strings
        String name = "Winston Leonard Churchill";
        String fn = name.substring(0, 7);
        String mn = name.substring(8, 15);
        String sn = name.substring(16, 25);
        prep.clearParameters();
        prep.setString(1, fn);
        prep.setString(2, mn);
        prep.setString(3, sn);
        prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), fn);
        Assert.assertEquals(rs.getString(2), mn);
        Assert.assertEquals(rs.getString(3), sn);
        // mixed
        prep.setString(1, name);
        prep.setString(2, null);
        prep.setLong(3, Long.MAX_VALUE);
        prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), name);
        Assert.assertNull(rs.getString(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(rs.getLong(3), Long.MAX_VALUE);
        // bytes
        prep.setBytes(1, PrepStmtTest.b1);
        prep.setBytes(2, PrepStmtTest.b2);
        prep.setBytes(3, PrepStmtTest.b3);
        prep.executeQuery();
        Assert.assertTrue(rs.next());
        assertArrayEq(rs.getBytes(1), PrepStmtTest.b1);
        assertArrayEq(rs.getBytes(2), PrepStmtTest.b2);
        assertArrayEq(rs.getBytes(3), PrepStmtTest.b3);
        Assert.assertFalse(rs.next());
        rs.close();
        // null date, time and timestamp (fix #363)
        prep.setDate(1, null);
        prep.setTime(2, null);
        prep.setTimestamp(3, null);
        rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getDate(1));
        Assert.assertNull(rs.getTime(2));
        Assert.assertNull(rs.getTimestamp(3));
        // streams
        ByteArrayInputStream inByte = new ByteArrayInputStream(PrepStmtTest.b1);
        prep.setBinaryStream(1, inByte, PrepStmtTest.b1.length);
        ByteArrayInputStream inAscii = new ByteArrayInputStream(PrepStmtTest.b2);
        prep.setAsciiStream(2, inAscii, PrepStmtTest.b2.length);
        byte[] b3 = PrepStmtTest.utf08.getBytes("UTF-8");
        ByteArrayInputStream inUnicode = new ByteArrayInputStream(b3);
        prep.setUnicodeStream(3, inUnicode, b3.length);
        rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        assertArrayEq(PrepStmtTest.b1, rs.getBytes(1));
        Assert.assertEquals(new String(PrepStmtTest.b2, "UTF-8"), rs.getString(2));
        Assert.assertEquals(new String(b3, "UTF-8"), rs.getString(3));
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void colNameAccess() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ? as col1, ? as col2, ? as bingo;");
        prep.setNull(1, 0);
        prep.setFloat(2, Float.MIN_VALUE);
        prep.setShort(3, Short.MIN_VALUE);
        prep.executeQuery();
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertNull(rs.getString("col1"));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(rs.getFloat("col2"), Float.MIN_VALUE, 1.0E-4);
        Assert.assertEquals(rs.getShort("bingo"), Short.MIN_VALUE);
        rs.close();
        prep.close();
    }

    @Test
    public void insert1000() throws SQLException {
        stat.executeUpdate("create table in1000 (a);");
        PreparedStatement prep = conn.prepareStatement("insert into in1000 values (?);");
        conn.setAutoCommit(false);
        for (int i = 0; i < 1000; i++) {
            prep.setInt(1, i);
            prep.executeUpdate();
        }
        conn.commit();
        ResultSet rs = stat.executeQuery("select count(a) from in1000;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 1000);
        rs.close();
    }

    @Test
    public void getObject() throws SQLException {
        stat.executeUpdate(("create table testobj (" + "c1 integer, c2 float, c3, c4 varchar, c5 bit, c6, c7);"));
        PreparedStatement prep = conn.prepareStatement("insert into testobj values (?,?,?,?,?,?,?);");
        prep.setInt(1, Integer.MAX_VALUE);
        prep.setFloat(2, Float.MAX_VALUE);
        prep.setDouble(3, Double.MAX_VALUE);
        prep.setLong(4, Long.MAX_VALUE);
        prep.setBoolean(5, false);
        prep.setByte(6, ((byte) (7)));
        prep.setBytes(7, PrepStmtTest.b1);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery("select c1,c2,c3,c4,c5,c6,c7 from testobj;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), Integer.MAX_VALUE);
        Assert.assertEquals(((int) (rs.getLong(1))), Integer.MAX_VALUE);
        Assert.assertEquals(rs.getFloat(2), Float.MAX_VALUE, 1.0E-4F);
        Assert.assertEquals(rs.getDouble(3), Double.MAX_VALUE, 1.0E-4);
        Assert.assertEquals(rs.getLong(4), Long.MAX_VALUE);
        Assert.assertFalse(rs.getBoolean(5));
        Assert.assertEquals(rs.getByte(6), ((byte) (7)));
        assertArrayEq(rs.getBytes(7), PrepStmtTest.b1);
        Assert.assertNotNull(rs.getObject(1));
        Assert.assertNotNull(rs.getObject(2));
        Assert.assertNotNull(rs.getObject(3));
        Assert.assertNotNull(rs.getObject(4));
        Assert.assertNotNull(rs.getObject(5));
        Assert.assertNotNull(rs.getObject(6));
        Assert.assertNotNull(rs.getObject(7));
        Assert.assertTrue(((rs.getObject(1)) instanceof Integer));
        Assert.assertTrue(((rs.getObject(2)) instanceof Double));
        Assert.assertTrue(((rs.getObject(3)) instanceof Double));
        Assert.assertTrue(((rs.getObject(4)) instanceof String));
        Assert.assertTrue(((rs.getObject(5)) instanceof Integer));
        Assert.assertTrue(((rs.getObject(6)) instanceof Integer));
        Assert.assertTrue(((rs.getObject(7)) instanceof byte[]));
        rs.close();
    }

    @Test
    public void tokens() throws SQLException {
        /* checks for a bug where a substring is read by the driver as the
        full original string, caused by my idiocyin assuming the
        pascal-style string was null terminated. Thanks Oliver Randschau.
         */
        StringTokenizer st = new StringTokenizer("one two three");
        st.nextToken();
        String substr = st.nextToken();
        PreparedStatement prep = conn.prepareStatement("select ?;");
        prep.setString(1, substr);
        ResultSet rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString(1), substr);
    }

    @Test
    public void utf() throws SQLException {
        ResultSet rs = stat.executeQuery((((((((((((((((("select '" + (PrepStmtTest.utf01)) + "','") + (PrepStmtTest.utf02)) + "','") + (PrepStmtTest.utf03)) + "','") + (PrepStmtTest.utf04)) + "','") + (PrepStmtTest.utf05)) + "','") + (PrepStmtTest.utf06)) + "','") + (PrepStmtTest.utf07)) + "','") + (PrepStmtTest.utf08)) + "';"));
        assertArrayEq(rs.getBytes(1), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf01));
        assertArrayEq(rs.getBytes(2), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf02));
        assertArrayEq(rs.getBytes(3), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf03));
        assertArrayEq(rs.getBytes(4), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf04));
        assertArrayEq(rs.getBytes(5), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf05));
        assertArrayEq(rs.getBytes(6), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf06));
        assertArrayEq(rs.getBytes(7), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf07));
        assertArrayEq(rs.getBytes(8), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf08));
        Assert.assertEquals(rs.getString(1), PrepStmtTest.utf01);
        Assert.assertEquals(rs.getString(2), PrepStmtTest.utf02);
        Assert.assertEquals(rs.getString(3), PrepStmtTest.utf03);
        Assert.assertEquals(rs.getString(4), PrepStmtTest.utf04);
        Assert.assertEquals(rs.getString(5), PrepStmtTest.utf05);
        Assert.assertEquals(rs.getString(6), PrepStmtTest.utf06);
        Assert.assertEquals(rs.getString(7), PrepStmtTest.utf07);
        Assert.assertEquals(rs.getString(8), PrepStmtTest.utf08);
        rs.close();
        PreparedStatement prep = conn.prepareStatement("select ?,?,?,?,?,?,?,?;");
        prep.setString(1, PrepStmtTest.utf01);
        prep.setString(2, PrepStmtTest.utf02);
        prep.setString(3, PrepStmtTest.utf03);
        prep.setString(4, PrepStmtTest.utf04);
        prep.setString(5, PrepStmtTest.utf05);
        prep.setString(6, PrepStmtTest.utf06);
        prep.setString(7, PrepStmtTest.utf07);
        prep.setString(8, PrepStmtTest.utf08);
        rs = prep.executeQuery();
        Assert.assertTrue(rs.next());
        assertArrayEq(rs.getBytes(1), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf01));
        assertArrayEq(rs.getBytes(2), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf02));
        assertArrayEq(rs.getBytes(3), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf03));
        assertArrayEq(rs.getBytes(4), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf04));
        assertArrayEq(rs.getBytes(5), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf05));
        assertArrayEq(rs.getBytes(6), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf06));
        assertArrayEq(rs.getBytes(7), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf07));
        assertArrayEq(rs.getBytes(8), PrepStmtTest.getUtf8Bytes(PrepStmtTest.utf08));
        Assert.assertEquals(rs.getString(1), PrepStmtTest.utf01);
        Assert.assertEquals(rs.getString(2), PrepStmtTest.utf02);
        Assert.assertEquals(rs.getString(3), PrepStmtTest.utf03);
        Assert.assertEquals(rs.getString(4), PrepStmtTest.utf04);
        Assert.assertEquals(rs.getString(5), PrepStmtTest.utf05);
        Assert.assertEquals(rs.getString(6), PrepStmtTest.utf06);
        Assert.assertEquals(rs.getString(7), PrepStmtTest.utf07);
        Assert.assertEquals(rs.getString(8), PrepStmtTest.utf08);
        rs.close();
    }

    @Test
    public void batch() throws SQLException {
        ResultSet rs;
        stat.executeUpdate("create table test (c1, c2, c3, c4);");
        PreparedStatement prep = conn.prepareStatement("insert into test values (?,?,?,?);");
        for (int i = 0; i < 10; i++) {
            prep.setInt(1, ((Integer.MIN_VALUE) + i));
            prep.setFloat(2, ((Float.MIN_VALUE) + i));
            prep.setString(3, ("Hello " + i));
            prep.setDouble(4, ((Double.MAX_VALUE) + i));
            prep.addBatch();
        }
        assertArrayEq(prep.executeBatch(), new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
        prep.close();
        rs = stat.executeQuery("select * from test;");
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), ((Integer.MIN_VALUE) + i));
            Assert.assertEquals(rs.getFloat(2), ((Float.MIN_VALUE) + i), 1.0E-4);
            Assert.assertEquals(rs.getString(3), ("Hello " + i));
            Assert.assertEquals(rs.getDouble(4), ((Double.MAX_VALUE) + i), 1.0E-4);
        }
        rs.close();
        stat.executeUpdate("drop table test;");
    }

    @Test
    public void testExecuteBatch() throws Exception {
        stat.executeUpdate("create table t (c text);");
        PreparedStatement prep = conn.prepareStatement("insert into t values (?);");
        prep.setString(1, "a");
        prep.addBatch();
        int call1_length = prep.executeBatch().length;
        prep.setString(1, "b");
        prep.addBatch();
        int call2_length = prep.executeBatch().length;
        Assert.assertEquals(1, call1_length);
        Assert.assertEquals(1, call2_length);
        ResultSet rs = stat.executeQuery("select * from t");
        rs.next();
        Assert.assertEquals("a", rs.getString(1));
        rs.next();
        Assert.assertEquals("b", rs.getString(1));
    }

    @Test
    public void dblock() throws SQLException {
        stat.executeUpdate("create table test (c1);");
        stat.executeUpdate("insert into test values (1);");
        conn.prepareStatement("select * from test;").executeQuery().close();
        stat.executeUpdate("drop table test;");
    }

    @Test
    public void dbclose() throws SQLException {
        conn.prepareStatement("select ?;").setString(1, "Hello World");
        conn.prepareStatement("select null;").close();
        conn.prepareStatement("select null;").executeQuery().close();
        conn.prepareStatement("create table t (c);").executeUpdate();
        conn.prepareStatement("select null;");
    }

    @Test
    public void batchOneParam() throws SQLException {
        stat.executeUpdate("create table test (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into test values (?);");
        for (int i = 0; i < 10; i++) {
            prep.setInt(1, ((Integer.MIN_VALUE) + i));
            prep.addBatch();
        }
        assertArrayEq(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, prep.executeBatch());
        prep.close();
        ResultSet rs = stat.executeQuery("select count(*) from test;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 10);
        rs.close();
    }

    @Test
    public void batchZeroParams() throws Exception {
        stat.executeUpdate("create table test (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into test values (5);");
        for (int i = 0; i < 10; i++) {
            prep.addBatch();
        }
        assertArrayEq(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, prep.executeBatch());
        prep.close();
        ResultSet rs = stat.executeQuery("select count(*) from test;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 10);
        rs.close();
    }

    @Test
    public void paramMetaData() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ?,?,?,?;");
        Assert.assertEquals(prep.getParameterMetaData().getParameterCount(), 4);
    }

    @Test
    public void metaData() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ? as col1, ? as col2, ? as delta;");
        ResultSetMetaData meta = prep.getMetaData();
        Assert.assertEquals(meta.getColumnCount(), 3);
        Assert.assertEquals(meta.getColumnName(1), "col1");
        Assert.assertEquals(meta.getColumnName(2), "col2");
        Assert.assertEquals(meta.getColumnName(3), "delta");
        /* assertEquals(meta.getColumnType(1), Types.INTEGER);
        assertEquals(meta.getColumnType(2), Types.INTEGER);
        assertEquals(meta.getColumnType(3), Types.INTEGER);
         */
        prep.setInt(1, 2);
        prep.setInt(2, 3);
        prep.setInt(3, (-1));
        meta = prep.executeQuery().getMetaData();
        Assert.assertEquals(meta.getColumnCount(), 3);
        prep.close();
    }

    @Test
    public void date1() throws SQLException {
        Date d1 = new Date(987654321);
        stat.execute("create table t (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into t values(?);");
        prep.setDate(1, d1);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery("select c1 from t;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getLong(1), d1.getTime());
        Assert.assertTrue(rs.getDate(1).equals(d1));
        rs.close();
    }

    @Test
    public void date2() throws SQLException {
        Date d1 = new Date(1092941466000L);
        stat.execute("create table t (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into t values (datetime(?/1000, 'unixepoch'));");
        prep.setDate(1, d1);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery("select strftime('%s', c1) * 1000 from t;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getLong(1), d1.getTime());
        Assert.assertTrue(rs.getDate(1).equals(d1));
    }

    @Test
    public void changeSchema() throws SQLException {
        stat.execute("create table t (c1);");
        PreparedStatement prep = conn.prepareStatement("insert into t values (?);");
        conn.createStatement().execute("create table t2 (c2);");
        prep.setInt(1, 1000);
        prep.execute();
        prep.executeUpdate();
    }

    // @Ignore
    // @Test
    // public void multipleStatements() throws SQLException
    // {
    // PreparedStatement prep = conn
    // .prepareStatement("create table person (id integer, name string); insert into person values(1, 'leo'); insert into person values(2, 'yui');");
    // prep.executeUpdate();
    // 
    // ResultSet rs = conn.createStatement().executeQuery("select * from person");
    // assertTrue(rs.next());
    // assertTrue(rs.next());
    // }
    @Test
    public void reusingSetValues() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ?,?;");
        prep.setInt(1, 9);
        for (int i = 0; i < 10; i++) {
            prep.setInt(2, i);
            ResultSet rs = prep.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), 9);
            Assert.assertEquals(rs.getInt(2), i);
        }
        for (int i = 0; i < 10; i++) {
            prep.setInt(2, i);
            ResultSet rs = prep.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getInt(1), 9);
            Assert.assertEquals(rs.getInt(2), i);
            rs.close();
        }
        prep.close();
    }

    @Test
    public void clearParameters() throws SQLException {
        stat.executeUpdate("create table tbl (colid integer primary key AUTOINCREMENT, col varchar)");
        stat.executeUpdate("insert into tbl(col) values (\"foo\")");
        PreparedStatement prep = conn.prepareStatement("select colid from tbl where col = ?");
        prep.setString(1, "foo");
        ResultSet rs = prep.executeQuery();
        prep.clearParameters();
        rs.next();
        Assert.assertEquals(1, rs.getInt(1));
        rs.close();
        try {
            prep.execute();
            Assert.fail("Returned result when values not bound to prepared statement");
        } catch (Exception e) {
            Assert.assertEquals("Values not bound to statement", e.getMessage());
        }
        try {
            rs = prep.executeQuery();
            Assert.fail("Returned result when values not bound to prepared statement");
        } catch (Exception e) {
            Assert.assertEquals("Values not bound to statement", e.getMessage());
        }
        prep.close();
        try {
            prep = conn.prepareStatement("insert into tbl(col) values (?)");
            prep.clearParameters();
            prep.executeUpdate();
            Assert.fail("Returned result when values not bound to prepared statement");
        } catch (Exception e) {
            Assert.assertEquals("Values not bound to statement", e.getMessage());
        }
    }

    @Test(expected = SQLException.class)
    public void preparedStatementShouldThrowIfNotAllParamsSet() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select ? as col1, ? as col2, ? as col3;");
        ResultSetMetaData meta = prep.getMetaData();
        Assert.assertEquals(meta.getColumnCount(), 3);
        // we only set one 1 param of the expected 3 params
        prep.setInt(1, 2);
        prep.executeQuery();
        prep.close();
    }

    @Test(expected = SQLException.class)
    public void preparedStatementShouldThrowIfNotAllParamsSetBatch() throws SQLException {
        stat.executeUpdate("create table test (c1, c2);");
        PreparedStatement prep = conn.prepareStatement("insert into test values (?,?);");
        prep.setInt(1, 1);
        // addBatch should throw since we added a command with invalid params set
        // which becomes immutable once added to the batch so it makes sense to verify
        // at the point when you add a command instead of delaying till batch execution
        prep.addBatch();
    }

    @Test(expected = SQLException.class)
    public void noSuchTable() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select * from doesnotexist;");
        prep.executeQuery();
    }

    @Test(expected = SQLException.class)
    public void noSuchCol() throws SQLException {
        PreparedStatement prep = conn.prepareStatement("select notacol from (select 1);");
        prep.executeQuery();
    }

    @Test(expected = SQLException.class)
    public void noSuchColName() throws SQLException {
        ResultSet rs = conn.prepareStatement("select 1;").executeQuery();
        Assert.assertTrue(rs.next());
        rs.getInt("noSuchColName");
    }

    @Test
    public void constraintErrorCodeExecute() throws SQLException {
        Assert.assertEquals(0, stat.executeUpdate("create table foo (id integer, CONSTRAINT U_ID UNIQUE (id));"));
        Assert.assertEquals(1, stat.executeUpdate("insert into foo values(1);"));
        // try to insert a row with duplicate id
        try {
            PreparedStatement statement = conn.prepareStatement("insert into foo values(?);");
            statement.setInt(1, 1);
            statement.execute();
            Assert.fail("expected exception");
        } catch (SQLException e) {
            Assert.assertEquals(code, e.getErrorCode());
        }
    }

    @Test
    public void constraintErrorCodeExecuteUpdate() throws SQLException {
        Assert.assertEquals(0, stat.executeUpdate("create table foo (id integer, CONSTRAINT U_ID UNIQUE (id));"));
        Assert.assertEquals(1, stat.executeUpdate("insert into foo values(1);"));
        // try to insert a row with duplicate id
        try {
            PreparedStatement statement = conn.prepareStatement("insert into foo values(?);");
            statement.setInt(1, 1);
            statement.executeUpdate();
            Assert.fail("expected exception");
        } catch (SQLException e) {
            Assert.assertEquals(code, e.getErrorCode());
        }
    }
}

