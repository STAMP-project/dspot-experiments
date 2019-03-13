package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getConnection_2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_conn_ok() throws Exception {
        {
            Connection conn = dataSource.getConnection("usr1", "pwd1");
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection("usr1", "pwd1");
            conn.close();
        }
    }
}

