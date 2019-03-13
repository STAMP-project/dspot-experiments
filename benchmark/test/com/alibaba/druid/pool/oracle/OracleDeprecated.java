package com.alibaba.druid.pool.oracle;


import JdbcConstants.ORACLE_DRIVER2;
import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;


public class OracleDeprecated extends TestCase {
    public void test_deprecated() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(ORACLE_DRIVER2);
    }
}

