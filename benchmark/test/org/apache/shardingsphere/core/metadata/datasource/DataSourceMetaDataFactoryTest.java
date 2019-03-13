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
package org.apache.shardingsphere.core.metadata.datasource;


import DatabaseType.H2;
import DatabaseType.MySQL;
import DatabaseType.Oracle;
import DatabaseType.PostgreSQL;
import DatabaseType.SQLServer;
import org.apache.shardingsphere.core.metadata.datasource.dialect.H2DataSourceMetaData;
import org.apache.shardingsphere.core.metadata.datasource.dialect.MySQLDataSourceMetaData;
import org.apache.shardingsphere.core.metadata.datasource.dialect.OracleDataSourceMetaData;
import org.apache.shardingsphere.core.metadata.datasource.dialect.PostgreSQLDataSourceMetaData;
import org.apache.shardingsphere.core.metadata.datasource.dialect.SQLServerDataSourceMetaData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DataSourceMetaDataFactoryTest {
    @Test
    public void assertAllNewInstanceForH2() {
        Assert.assertThat(DataSourceMetaDataFactory.newInstance(H2, "jdbc:h2:mem:ds_0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL"), CoreMatchers.instanceOf(H2DataSourceMetaData.class));
    }

    @Test
    public void assertAllNewInstanceForMySQL() {
        Assert.assertThat(DataSourceMetaDataFactory.newInstance(MySQL, "jdbc:mysql://127.0.0.1:3306/db_0"), CoreMatchers.instanceOf(MySQLDataSourceMetaData.class));
    }

    @Test
    public void assertAllNewInstanceForOracle() {
        Assert.assertThat(DataSourceMetaDataFactory.newInstance(Oracle, "jdbc:oracle:thin:@//127.0.0.1:3306/ds_0"), CoreMatchers.instanceOf(OracleDataSourceMetaData.class));
    }

    @Test
    public void assertAllNewInstanceForPostgreSQL() {
        Assert.assertThat(DataSourceMetaDataFactory.newInstance(PostgreSQL, "jdbc:postgresql://127.0.0.1:3306/ds_0"), CoreMatchers.instanceOf(PostgreSQLDataSourceMetaData.class));
    }

    @Test
    public void assertAllNewInstanceForSQLServer() {
        Assert.assertThat(DataSourceMetaDataFactory.newInstance(SQLServer, "jdbc:microsoft:sqlserver://127.0.0.1:3306;DatabaseName=ds_0"), CoreMatchers.instanceOf(SQLServerDataSourceMetaData.class));
    }
}

