package com.alibaba.druid.pool.mysql;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_mysql() throws Exception {
        Connection connection = dataSource.getConnection();
        System.out.println(("----------- : " + (connection.unwrap(Connection.class).getClass())));
        Statement stmt = connection.createStatement();
        stmt.execute("select 1;select 1");
        ResultSet rs = stmt.getResultSet();
        Assert.assertFalse(rs.isClosed());
        Assert.assertTrue(stmt.getMoreResults());
        Assert.assertTrue(rs.isClosed());
        ResultSet rs2 = stmt.getResultSet();
        Assert.assertFalse(rs2.isClosed());
        rs2.close();
        Assert.assertTrue(rs2.isClosed());
        stmt.close();
        connection.close();
    }
}

