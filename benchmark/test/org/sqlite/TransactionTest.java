package org.sqlite;


import TransactionMode.DEFERRED;
import TransactionMode.EXCLUSIVE;
import TransactionMode.IMMEDIATE;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;
import org.sqlite.SQLiteConfig.TransactionMode;


/**
 * These tests assume that Statements and PreparedStatements are working as per
 * normal and test the interactions of commit(), rollback() and
 * setAutoCommit(boolean) with multiple connections to the same db.
 */
public class TransactionTest {
    private Connection conn1;

    private Connection conn2;

    private Connection conn3;

    private Statement stat1;

    private Statement stat2;

    private Statement stat3;

    boolean done = false;

    @Test
    public void failedUpdatePreventedFutureRollbackUnprepared() throws SQLException {
        failedUpdatedPreventedFutureRollback(false);
    }

    @Test
    public void failedUpdatePreventedFutureRollbackPrepared() throws SQLException {
        failedUpdatedPreventedFutureRollback(true);
    }

    @Test
    public void multiConn() throws SQLException {
        stat1.executeUpdate("create table test (c1);");
        stat1.executeUpdate("insert into test values (1);");
        stat2.executeUpdate("insert into test values (2);");
        stat3.executeUpdate("insert into test values (3);");
        ResultSet rs = stat1.executeQuery("select sum(c1) from test;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 6);
        rs.close();
        rs = stat3.executeQuery("select sum(c1) from test;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getInt(1), 6);
        rs.close();
    }

    @Test
    public void locking() throws SQLException {
        stat1.executeUpdate("create table test (c1);");
        stat1.executeUpdate("begin immediate;");
        stat2.executeUpdate("select * from test;");
    }

    @Test
    public void insert() throws SQLException {
        ResultSet rs;
        String countSql = "select count(*) from trans;";
        stat1.executeUpdate("create table trans (c1);");
        conn1.setAutoCommit(false);
        Assert.assertEquals(1, stat1.executeUpdate("insert into trans values (4);"));
        // transaction not yet commited, conn1 can see, conn2 can not
        rs = stat1.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        rs.close();
        rs = stat2.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
        rs.close();
        conn1.commit();
        // all connects can see data
        rs = stat2.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        rs.close();
    }

    @Test
    public void rollback() throws SQLException {
        String select = "select * from trans;";
        ResultSet rs;
        stat1.executeUpdate("create table trans (c1);");
        conn1.setAutoCommit(false);
        stat1.executeUpdate("insert into trans values (3);");
        rs = stat1.executeQuery(select);
        Assert.assertTrue(rs.next());
        rs.close();
        conn1.rollback();
        rs = stat1.executeQuery(select);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void multiRollback() throws SQLException {
        ResultSet rs;
        stat1.executeUpdate("create table t (c1);");
        conn1.setAutoCommit(false);
        stat1.executeUpdate("insert into t values (1);");
        conn1.commit();
        stat1.executeUpdate("insert into t values (1);");
        conn1.rollback();
        stat1.addBatch("insert into t values (2);");
        stat1.addBatch("insert into t values (3);");
        stat1.executeBatch();
        conn1.commit();
        stat1.addBatch("insert into t values (7);");
        stat1.executeBatch();
        conn1.rollback();
        stat1.executeUpdate("insert into t values (4);");
        conn1.setAutoCommit(true);
        stat1.executeUpdate("insert into t values (5);");
        conn1.setAutoCommit(false);
        PreparedStatement p = conn1.prepareStatement("insert into t values (?);");
        p.setInt(1, 6);
        p.executeUpdate();
        p.setInt(1, 7);
        p.executeUpdate();
        // conn1 can see (1+...+7), conn2 can see (1+...+5)
        rs = stat1.executeQuery("select sum(c1) from t;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(((((((1 + 2) + 3) + 4) + 5) + 6) + 7), rs.getInt(1));
        rs.close();
        rs = stat2.executeQuery("select sum(c1) from t;");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(((((1 + 2) + 3) + 4) + 5), rs.getInt(1));
        rs.close();
    }

    @Test
    public void transactionsDontMindReads() throws SQLException {
        stat1.executeUpdate("create table t (c1);");
        stat1.executeUpdate("insert into t values (1);");
        stat1.executeUpdate("insert into t values (2);");
        ResultSet rs = stat1.executeQuery("select * from t;");
        Assert.assertTrue(rs.next());// select is open

        conn2.setAutoCommit(false);
        stat1.executeUpdate("insert into t values (2);");
        rs.close();
        conn2.commit();
    }

    @Test
    public void secondConnWillWait() throws Exception {
        stat1.executeUpdate("create table t (c1);");
        stat1.executeUpdate("insert into t values (1);");
        stat1.executeUpdate("insert into t values (2);");
        ResultSet rs = stat1.executeQuery("select * from t;");
        Assert.assertTrue(rs.next());
        final TransactionTest lock = this;
        lock.done = false;
        new Thread() {
            @Override
            public void run() {
                try {
                    stat2.executeUpdate("insert into t values (3);");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
                synchronized(lock) {
                    lock.done = true;
                    lock.notify();
                }
            }
        }.start();
        Thread.sleep(100);
        rs.close();
        synchronized(lock) {
            if (!(lock.done)) {
                lock.wait(5000);
                if (!(lock.done))
                    throw new Exception("should be done");

            }
        }
    }

    @Test(expected = SQLException.class)
    public void secondConnMustTimeout() throws SQLException {
        stat1.setQueryTimeout(1);
        stat1.executeUpdate("create table t (c1);");
        stat1.executeUpdate("insert into t values (1);");
        stat1.executeUpdate("insert into t values (2);");
        ResultSet rs = stat1.executeQuery("select * from t;");
        Assert.assertTrue(rs.next());
        setBusyTimeout(10);
        stat2.executeUpdate("insert into t values (3);");// can't be done

    }

    // @Test(expected= SQLException.class)
    @Test
    public void cantUpdateWhileReading() throws SQLException {
        stat1.executeUpdate("create table t (c1);");
        stat1.executeUpdate("insert into t values (1);");
        stat1.executeUpdate("insert into t values (2);");
        ResultSet rs = conn1.createStatement().executeQuery("select * from t;");
        Assert.assertTrue(rs.next());
        // commit now succeeds since sqlite 3.6.5
        stat1.executeUpdate("insert into t values (3);");// can't be done

    }

    @Test(expected = SQLException.class)
    public void cantCommit() throws SQLException {
        conn1.commit();
    }

    @Test(expected = SQLException.class)
    public void cantRollback() throws SQLException {
        conn1.rollback();
    }

    @Test
    public void transactionModes() throws Exception {
        File tmpFile = File.createTempFile("test-trans", ".db");
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(("jdbc:sqlite:" + (tmpFile.getAbsolutePath())));
        // deferred
        SQLiteConnection con = ((SQLiteConnection) (ds.getConnection()));
        Assert.assertEquals(DEFERRED, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin;", con.getConnectionConfig().transactionPrefix());
        runUpdates(con, "tbl1");
        ds.setTransactionMode(DEFERRED.name());
        con = ((SQLiteConnection) (ds.getConnection()));
        Assert.assertEquals(DEFERRED, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin;", con.getConnectionConfig().transactionPrefix());
        // Misspelled deferred should be accepted for backwards compatibility
        ds.setTransactionMode("DEFFERED");
        con = ((SQLiteConnection) (ds.getConnection()));
        Assert.assertEquals(DEFERRED, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin;", con.getConnectionConfig().transactionPrefix());
        con = ((SQLiteConnection) (ds.getConnection()));
        con.getConnectionConfig().setTransactionMode(TransactionMode.valueOf("DEFFERED"));
        Assert.assertEquals(DEFERRED, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin;", con.getConnectionConfig().transactionPrefix());
        // immediate
        ds.setTransactionMode(IMMEDIATE.name());
        con = ((SQLiteConnection) (ds.getConnection()));
        Assert.assertEquals(IMMEDIATE, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin immediate;", con.getConnectionConfig().transactionPrefix());
        runUpdates(con, "tbl2");
        // exclusive
        ds.setTransactionMode(EXCLUSIVE.name());
        con = ((SQLiteConnection) (ds.getConnection()));
        Assert.assertEquals(EXCLUSIVE, con.getConnectionConfig().getTransactionMode());
        Assert.assertEquals("begin exclusive;", con.getConnectionConfig().transactionPrefix());
        runUpdates(con, "tbl3");
        tmpFile.delete();
    }
}

