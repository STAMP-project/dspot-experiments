/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.dbcp.hive;


import Hive_1_1ConnectionPool.DATABASE_URL;
import Hive_1_1ConnectionPool.DB_PASSWORD;
import Hive_1_1ConnectionPool.DB_USER;
import Hive_1_1ConnectionPool.HIVE_CONFIGURATION_RESOURCES;
import Hive_1_1ConnectionPool.MAX_TOTAL_CONNECTIONS;
import Hive_1_1ConnectionPool.MAX_WAIT_TIME;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.registry.VariableDescriptor;
import org.apache.nifi.util.MockConfigurationContext;
import org.apache.nifi.util.MockVariableRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class Hive_1_1ConnectionPoolTest {
    private UserGroupInformation userGroupInformation;

    private Hive_1_1ConnectionPool hiveConnectionPool;

    private BasicDataSource basicDataSource;

    private ComponentLog componentLog;

    private File krb5conf = new File("src/test/resources/krb5.conf");

    @Test(expected = ProcessException.class)
    public void testGetConnectionSqlException() throws SQLException {
        SQLException sqlException = new SQLException("bad sql");
        Mockito.when(basicDataSource.getConnection()).thenThrow(sqlException);
        try {
            hiveConnectionPool.getConnection();
        } catch (ProcessException e) {
            Assert.assertEquals(sqlException, e.getCause());
            throw e;
        }
    }

    @Test
    public void testExpressionLanguageSupport() throws Exception {
        final String URL = "jdbc:hive2://localhost:10000/default";
        final String USER = "user";
        final String PASS = "pass";
        final int MAX_CONN = 7;
        final String MAX_WAIT = "10 sec";// 10000 milliseconds

        final String CONF = "/path/to/hive-site.xml";
        hiveConnectionPool = new Hive_1_1ConnectionPool();
        Map<PropertyDescriptor, String> props = new HashMap<PropertyDescriptor, String>() {
            {
                put(DATABASE_URL, "${url}");
                put(DB_USER, "${username}");
                put(DB_PASSWORD, "${password}");
                put(MAX_TOTAL_CONNECTIONS, "${maxconn}");
                put(MAX_WAIT_TIME, "${maxwait}");
                put(HIVE_CONFIGURATION_RESOURCES, "${hiveconf}");
            }
        };
        MockVariableRegistry registry = new MockVariableRegistry();
        registry.setVariable(new VariableDescriptor("url"), URL);
        registry.setVariable(new VariableDescriptor("username"), USER);
        registry.setVariable(new VariableDescriptor("password"), PASS);
        registry.setVariable(new VariableDescriptor("maxconn"), Integer.toString(MAX_CONN));
        registry.setVariable(new VariableDescriptor("maxwait"), MAX_WAIT);
        registry.setVariable(new VariableDescriptor("hiveconf"), CONF);
        MockConfigurationContext context = new MockConfigurationContext(props, null, registry);
        hiveConnectionPool.onConfigured(context);
        Field dataSourceField = Hive_1_1ConnectionPool.class.getDeclaredField("dataSource");
        dataSourceField.setAccessible(true);
        basicDataSource = ((BasicDataSource) (dataSourceField.get(hiveConnectionPool)));
        Assert.assertEquals(URL, basicDataSource.getUrl());
        Assert.assertEquals(USER, basicDataSource.getUsername());
        Assert.assertEquals(PASS, basicDataSource.getPassword());
        Assert.assertEquals(MAX_CONN, basicDataSource.getMaxActive());
        Assert.assertEquals(10000L, basicDataSource.getMaxWait());
        Assert.assertEquals(URL, hiveConnectionPool.getConnectionURL());
    }
}

