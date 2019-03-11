package org.sqlite;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;


/**
 * These tests assume that Statements and PreparedStatements are working as per
 * normal and test the interactions of commit(), setSavepoint(), setSavepoint(String),
 * rollback(Savepoint), and release(Savepoint).
 */
public class SavepointTest {
    private Connection conn1;

    private Connection conn2;

    private Statement stat1;

    private Statement stat2;

    boolean done = false;

    @Test
    public void insert() throws SQLException {
        ResultSet rs;
        String countSql = "select count(*) from trans;";
        stat1.executeUpdate("create table trans (c1);");
        conn1.setSavepoint();
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
        Savepoint sp = conn1.setSavepoint();
        stat1.executeUpdate("insert into trans values (3);");
        rs = stat1.executeQuery(select);
        Assert.assertTrue(rs.next());
        rs.close();
        conn1.rollback(sp);
        rs = stat1.executeQuery(select);
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void multiRollback() throws SQLException {
        ResultSet rs;
        stat1.executeUpdate("create table t (c1);");
        conn1.setSavepoint();
        stat1.executeUpdate("insert into t values (1);");
        conn1.commit();
        Savepoint sp = conn1.setSavepoint();
        stat1.executeUpdate("insert into t values (1);");
        conn1.rollback(sp);
        stat1.addBatch("insert into t values (2);");
        stat1.addBatch("insert into t values (3);");
        stat1.executeBatch();
        conn1.commit();
        Savepoint sp7 = conn1.setSavepoint("num7");
        stat1.addBatch("insert into t values (7);");
        stat1.executeBatch();
        // nested savepoint
        Savepoint sp8 = conn1.setSavepoint("num8");
        stat1.addBatch("insert into t values (8);");
        stat1.executeBatch();
        conn1.rollback(sp8);
        conn1.rollback(sp7);
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
    public void release() throws SQLException {
        ResultSet rs;
        String countSql = "select count(*) from trans;";
        stat1.executeUpdate("create table trans (c1);");
        Savepoint outerSP = conn1.setSavepoint("outer_sp");
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
        Savepoint innerSP = conn1.setSavepoint("inner_sp");
        Assert.assertEquals(1, stat1.executeUpdate("insert into trans values (5);"));
        // transaction not yet commited, conn1 can see, conn2 can not
        rs = stat1.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        rs.close();
        rs = stat2.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
        rs.close();
        // releasing an inner savepoint, statements are still wrapped by the outer savepoint
        conn1.releaseSavepoint(innerSP);
        rs = stat2.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
        rs.close();
        // releasing the outer savepoint is like a commit
        conn1.releaseSavepoint(outerSP);
        // all connects can see SP1 data
        rs = stat2.executeQuery(countSql);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        rs.close();
    }
}

