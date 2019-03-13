package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????exceptionSorter_extend
 *
 * @author xiaoying [caohongxi001@gmail.com]
 */
public class DruidDataSourceTest_exceptionSorter_extend extends TestCase {
    public static class SubDriver extends Driver {
        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException
         * 		if a database error occurs.
         */
        public SubDriver() throws SQLException {
        }
    }

    public static class SubDriver1 implements java.sql.Driver {
        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException
         * 		if a database error occurs.
         */
        public SubDriver1() throws SQLException {
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return null;
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return false;
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return new DriverPropertyInfo[0];
        }

        @Override
        public int getMajorVersion() {
            return 0;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }

    /**
     * ?????com.mysql.jdbc.Driver???????sorter
     *
     * @throws Exception
     * 		
     */
    public void testExceptionSorter() throws Exception {
        DruidDataSource dataSource1 = new DruidDataSource();
        try {
            dataSource1.setDriverClassName(DruidDataSourceTest_exceptionSorter_extend.SubDriver.class.getName());
            dataSource1.init();
            Assert.assertNotNull(dataSource1.getExceptionSorter());
            Assert.assertEquals(MySqlExceptionSorter.class.getName(), dataSource1.getExceptionSorter().getClass().getName());
        } finally {
            JdbcUtils.close(dataSource1);
        }
    }

    /**
     * ?????java.sql.Driver?????sorter
     *
     * @throws Exception
     * 		
     */
    public void testExceptionSorterNull() throws Exception {
        DruidDataSource dataSource1 = new DruidDataSource();
        try {
            dataSource1.setDriverClassName(DruidDataSourceTest_exceptionSorter_extend.SubDriver1.class.getName());
            dataSource1.init();
            Assert.assertEquals("sorter is not null", null, dataSource1.getExceptionSorter());
        } finally {
            JdbcUtils.close(dataSource1);
        }
    }
}

