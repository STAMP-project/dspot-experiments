package com.alibaba.druid.test;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/12/2016.
 */
public class RaspberryPiMySqlTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_mysql() throws Exception {
        DruidPooledConnection connection = dataSource.getConnection();
        System.out.println(("variables : " + (connection.getVariables())));
        System.out.println(("gloabl variables : " + (connection.getGloablVariables())));
        connection.close();
    }
}

