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
package org.apache.shardingsphere.transaction.xa.jta.datasource.properties;


import DatabaseType.H2;
import DatabaseType.MySQL;
import DatabaseType.Oracle;
import DatabaseType.PostgreSQL;
import DatabaseType.SQLServer;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect.H2XAProperties;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect.MySQLXAProperties;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect.OracleXAProperties;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect.PostgreSQLXAProperties;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect.SQLServerXAProperties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class XAPropertiesFactoryTest {
    @Test
    public void assertCreateXAPropertiesForH2() {
        Assert.assertThat(XAPropertiesFactory.createXAProperties(H2), CoreMatchers.instanceOf(H2XAProperties.class));
    }

    @Test
    public void assertCreateXAPropertiesForMySQL() {
        Assert.assertThat(XAPropertiesFactory.createXAProperties(MySQL), CoreMatchers.instanceOf(MySQLXAProperties.class));
    }

    @Test
    public void assertCreateXAPropertiesForPostgreSQL() {
        Assert.assertThat(XAPropertiesFactory.createXAProperties(PostgreSQL), CoreMatchers.instanceOf(PostgreSQLXAProperties.class));
    }

    @Test
    public void assertCreateXAPropertiesForOracle() {
        Assert.assertThat(XAPropertiesFactory.createXAProperties(Oracle), CoreMatchers.instanceOf(OracleXAProperties.class));
    }

    @Test
    public void assertCreateXAPropertiesForSQLServer() {
        Assert.assertThat(XAPropertiesFactory.createXAProperties(SQLServer), CoreMatchers.instanceOf(SQLServerXAProperties.class));
    }
}

