/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.datasource.unpooled;


public class AmplUnpooledDataSourceTest {
    @org.junit.Test
    public void shouldNotRegisterTheSameDriverMultipleTimes() throws java.lang.Exception {
        // https://code.google.com/p/mybatis/issues/detail?id=430
        org.apache.ibatis.datasource.unpooled.UnpooledDataSource dataSource = null;
        dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
        dataSource.getConnection();
        int before = countRegisteredDrivers();
        dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
        dataSource.getConnection();
        org.junit.Assert.assertEquals(before, countRegisteredDrivers());
    }

    @org.junit.Ignore(value = "Requires MySQL server and a driver.")
    @org.junit.Test
    public void shouldRegisterDynamicallyLoadedDriver() throws java.lang.Exception {
        int before = countRegisteredDrivers();
        java.lang.ClassLoader driverClassLoader = null;
        org.apache.ibatis.datasource.unpooled.UnpooledDataSource dataSource = null;
        driverClassLoader = new java.net.URLClassLoader(new java.net.URL[]{ new java.net.URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
        dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
        dataSource.getConnection();
        org.junit.Assert.assertEquals((before + 1), countRegisteredDrivers());
        driverClassLoader = new java.net.URLClassLoader(new java.net.URL[]{ new java.net.URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
        dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
        dataSource.getConnection();
        org.junit.Assert.assertEquals((before + 1), countRegisteredDrivers());
    }

    protected int countRegisteredDrivers() {
        java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
        int count = 0;
        while (drivers.hasMoreElements()) {
            drivers.nextElement();
            count++;
        } 
        return count;
    }
}

