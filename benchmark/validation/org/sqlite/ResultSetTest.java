package org.sqlite;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;


public class ResultSetTest {
    private Connection conn;

    private Statement stat;

    @Test
    public void testTableColumnLowerNowFindLowerCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.findColumn("id"));
    }

    @Test
    public void testTableColumnLowerNowFindUpperCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.findColumn("ID"));
    }

    @Test
    public void testTableColumnLowerNowFindMixedCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.findColumn("Id"));
    }

    @Test
    public void testTableColumnUpperNowFindLowerCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(2, resultSet.findColumn("description"));
    }

    @Test
    public void testTableColumnUpperNowFindUpperCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(2, resultSet.findColumn("DESCRIPTION"));
    }

    @Test
    public void testTableColumnUpperNowFindMixedCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(2, resultSet.findColumn("Description"));
    }

    @Test
    public void testTableColumnMixedNowFindLowerCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(3, resultSet.findColumn("foo"));
    }

    @Test
    public void testTableColumnMixedNowFindUpperCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(3, resultSet.findColumn("FOO"));
    }

    @Test
    public void testTableColumnMixedNowFindMixedCaseColumn() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select * from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(3, resultSet.findColumn("fOo"));
    }

    @Test
    public void testSelectWithTableNameAliasNowFindWithoutTableNameAlias() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select t.id from test as t");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.findColumn("id"));
    }

    /**
     * Can't produce a case where column name contains table name
     * https://www.sqlite.org/c3ref/column_name.html :
     * "If there is no AS clause then the name of the column is unspecified"
     */
    @Test(expected = SQLException.class)
    public void testSelectWithTableNameAliasNowNotFindWithTableNameAlias() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select t.id from test as t");
        Assert.assertTrue(resultSet.next());
        resultSet.findColumn("t.id");
    }

    @Test
    public void testSelectWithTableNameNowFindWithoutTableName() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select test.id from test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.findColumn("id"));
    }

    @Test(expected = SQLException.class)
    public void testSelectWithTableNameNowNotFindWithTableName() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select test.id from test");
        Assert.assertTrue(resultSet.next());
        resultSet.findColumn("test.id");
    }

    @Test
    public void testCloseStatement() throws SQLException {
        ResultSet resultSet = stat.executeQuery("select test.id from test");
        stat.close();
        Assert.assertTrue(stat.isClosed());
        Assert.assertTrue(resultSet.isClosed());
        resultSet.close();
        Assert.assertTrue(resultSet.isClosed());
    }
}

