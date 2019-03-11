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
package org.apache.shardingsphere.transaction.xa.jta.datasource.properties.dialect;


import java.util.Properties;
import org.apache.shardingsphere.core.config.DatabaseAccessConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLXAPropertiesTest {
    @Test
    public void assertBuild() {
        Properties actual = new MySQLXAProperties().build(new DatabaseAccessConfiguration("jdbc:mysql://127.0.0.1:3306/demo", "root", "root"));
        Assert.assertThat(actual.getProperty("user"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("password"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("URL"), CoreMatchers.is("jdbc:mysql://127.0.0.1:3306/demo"));
        Assert.assertThat(actual.getProperty("pinGlobalTxToPhysicalConnection"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("autoReconnect"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("useServerPrepStmts"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("cachePrepStmts"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("prepStmtCacheSize"), CoreMatchers.is("250"));
        Assert.assertThat(actual.getProperty("prepStmtCacheSqlLimit"), CoreMatchers.is("2048"));
        Assert.assertThat(actual.getProperty("useLocalSessionState"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("rewriteBatchedStatements"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("cacheResultSetMetadata"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("cacheServerConfiguration"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("elideSetAutoCommits"), CoreMatchers.is(Boolean.TRUE.toString()));
        Assert.assertThat(actual.getProperty("maintainTimeStats"), CoreMatchers.is(Boolean.FALSE.toString()));
        Assert.assertThat(actual.getProperty("netTimeoutForStreamingResults"), CoreMatchers.is("0"));
    }
}

