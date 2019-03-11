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
package org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.recognizer;


import DatabaseType.H2;
import DatabaseType.MySQL;
import DatabaseType.Oracle;
import DatabaseType.PostgreSQL;
import DatabaseType.SQLServer;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class JDBCDriverURLRecognizerEngineTest {
    @Test
    public void assertGetDriverClassName() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDriverClassName("jdbc:h2:xxx"), CoreMatchers.is("org.h2.Driver"));
    }

    @Test(expected = ShardingException.class)
    public void assertGetDriverClassNameFailure() {
        JDBCDriverURLRecognizerEngine.getDriverClassName("xxx");
    }

    @Test
    public void assertGetDatabaseTypeForMySQL() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDatabaseType("jdbc:mysql:xxx"), CoreMatchers.is(MySQL));
    }

    @Test
    public void assertGetDatabaseTypeForPostgreSQL() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDatabaseType("jdbc:postgresql:xxx"), CoreMatchers.is(PostgreSQL));
    }

    @Test
    public void assertGetDatabaseTypeForOracle() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDatabaseType("jdbc:oracle:xxx"), CoreMatchers.is(Oracle));
    }

    @Test
    public void assertGetDatabaseTypeForSQLServer() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDatabaseType("jdbc:sqlserver:xxx"), CoreMatchers.is(SQLServer));
    }

    @Test
    public void assertGetDatabaseTypeForH2() {
        Assert.assertThat(JDBCDriverURLRecognizerEngine.getDatabaseType("jdbc:h2:xxx"), CoreMatchers.is(H2));
    }

    @Test(expected = ShardingException.class)
    public void assertGetDatabaseTypeFailure() {
        JDBCDriverURLRecognizerEngine.getDatabaseType("xxx");
    }
}

