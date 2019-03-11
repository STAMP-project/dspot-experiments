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


public final class PostgreSQLXAPropertiesTest {
    @Test
    public void assertBuild() {
        Properties actual = new PostgreSQLXAProperties().build(new DatabaseAccessConfiguration("jdbc:postgresql://db.psql:5432/test_db", "root", "root"));
        Assert.assertThat(actual.getProperty("user"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("password"), CoreMatchers.is("root"));
        Assert.assertThat(actual.getProperty("serverName"), CoreMatchers.is("db.psql"));
        Assert.assertThat(actual.getProperty("portNumber"), CoreMatchers.is("5432"));
        Assert.assertThat(actual.getProperty("databaseName"), CoreMatchers.is("test_db"));
    }
}

