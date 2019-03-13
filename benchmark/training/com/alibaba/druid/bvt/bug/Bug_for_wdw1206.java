package com.alibaba.druid.bvt.bug;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


public class Bug_for_wdw1206 extends TestCase {
    private ClassLoader ctxClassLoader;

    private DruidDataSource dataSource;

    public void test_nullCtxClassLoader() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

