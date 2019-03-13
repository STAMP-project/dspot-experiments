/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.proxy;


import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.util.Utils;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.apache.derby.impl.jdbc.EmbedStatement;
import org.junit.Assert;


public class WrapImplTest extends TestCase {
    private static String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j,encoding,null:name=demo:jdbc:derby:classpath:petstore-db";

    public void test_clone() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");
        DruidDriver driver = ((DruidDriver) (DriverManager.getDriver(WrapImplTest.url)));
        ConnectionProxyImpl connection = ((ConnectionProxyImpl) (driver.connect(WrapImplTest.url, new Properties())));
        connection.getRawObject();
        FilterChain filterChain = ((FilterChain) (connection.createChain()));
        filterChain.cloneChain();
        DataSourceProxyImpl dataSource = ((DataSourceProxyImpl) (connection.getDirectDataSource()));
        dataSource.getId();
        Assert.assertEquals(4, dataSource.getProxyFilters().size());
        Assert.assertEquals(4, dataSource.getFilterClasses().length);
        Assert.assertNotNull(dataSource.getCreatedTime());
        Assert.assertTrue(((dataSource.getCreatedTime().getTime()) != 0));
        Assert.assertEquals("org.apache.derby.jdbc.EmbeddedDriver", dataSource.getRawDriverClassName());
        Assert.assertEquals(WrapImplTest.url, dataSource.getUrl());
        Assert.assertEquals("jdbc:derby:classpath:petstore-db", dataSource.getRawUrl());
        Assert.assertEquals(10, dataSource.getRawDriverMajorVersion());
        Assert.assertEquals(12, dataSource.getRawDriverMinorVersion());
        Class<?> mysql5ConnectionClass = Utils.loadClass("com.mysql.jdbc.Connection");
        if (mysql5ConnectionClass != null) {
            Assert.assertFalse(connection.isWrapperFor(mysql5ConnectionClass));
        }
        Assert.assertTrue(connection.isWrapperFor(ConnectionProxyImpl.class));
        Assert.assertTrue(connection.isWrapperFor(EmbedConnection.class));
        Assert.assertNotNull(connection.unwrap(ConnectionProxyImpl.class));
        Assert.assertNull(connection.unwrap(null));
        EmbedConnection derbyConnection = connection.unwrap(EmbedConnection.class);
        Assert.assertNotNull(derbyConnection);
        Statement statement = connection.createStatement();
        if (mysql5ConnectionClass != null) {
            Assert.assertFalse(statement.isWrapperFor(Class.forName("com.mysql.jdbc.Statement")));
        }
        Assert.assertFalse(statement.isWrapperFor(null));
        Assert.assertTrue(statement.isWrapperFor(EmbedStatement.class));
        EmbedStatement rayStatement = statement.unwrap(EmbedStatement.class);
        Assert.assertNotNull(rayStatement);
        statement.close();
    }
}

