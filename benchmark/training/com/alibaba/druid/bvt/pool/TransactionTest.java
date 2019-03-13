package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;


public class TransactionTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        for (int i = 0; i < 100; ++i) {
            PreparedStatement stmt = conn.prepareStatement(("select + " + (i % 10)));
            stmt.executeUpdate();
            stmt.close();
        }
        conn.commit();
        conn.close();
    }
}

