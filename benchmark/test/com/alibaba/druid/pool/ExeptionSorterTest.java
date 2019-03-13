package com.alibaba.druid.pool;


import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.mysql.jdbc.Driver;
import java.lang.reflect.Method;
import java.sql.SQLException;
import junit.framework.TestCase;


public class ExeptionSorterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_dataSource() throws Exception {
        Method method = DruidDataSource.class.getDeclaredMethod("initExceptionSorter");
        method.setAccessible(true);
        method.invoke(dataSource);
        TestCase.assertEquals(dataSource.getExceptionSorter().getClass(), MySqlExceptionSorter.class);
    }

    public static class MyDriver extends Driver {
        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException
         * 		if a database error occurs.
         */
        public MyDriver() throws SQLException {
        }
    }
}

