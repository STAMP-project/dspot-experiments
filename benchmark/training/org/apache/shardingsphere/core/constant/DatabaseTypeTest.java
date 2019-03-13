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
package org.apache.shardingsphere.core.constant;


import DatabaseType.H2;
import DatabaseType.MySQL;
import DatabaseType.Oracle;
import DatabaseType.PostgreSQL;
import DatabaseType.SQLServer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DatabaseTypeTest {
    @Test
    public void assertValueFromSuccess() {
        Assert.assertThat(DatabaseType.valueFrom("H2"), CoreMatchers.is(H2));
        Assert.assertThat(DatabaseType.valueFrom("MySQL"), CoreMatchers.is(MySQL));
        Assert.assertThat(DatabaseType.valueFrom("Oracle"), CoreMatchers.is(Oracle));
        Assert.assertThat(DatabaseType.valueFrom("Microsoft SQL Server"), CoreMatchers.is(SQLServer));
        Assert.assertThat(DatabaseType.valueFrom("PostgreSQL"), CoreMatchers.is(PostgreSQL));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertValueFromFailure() {
        DatabaseType.valueFrom("unknown");
    }
}

