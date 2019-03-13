package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Statement;
import junit.framework.TestCase;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_recycle extends TestCase {
    private DruidDataSource dataSource;

    public void test_recycle() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(false);
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        conn.close();
    }
}

