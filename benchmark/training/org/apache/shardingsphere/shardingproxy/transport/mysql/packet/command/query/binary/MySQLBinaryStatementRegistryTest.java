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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLBinaryStatementRegistryTest {
    private final String sql = "SELECT * FROM tbl WHERE id=?";

    @Test
    public void assertRegisterIfAbsent() {
        Assert.assertThat(MySQLBinaryStatementRegistry.getInstance().register(sql, 1), CoreMatchers.is(1));
        MySQLBinaryStatement actual = MySQLBinaryStatementRegistry.getInstance().getBinaryStatement(1);
        Assert.assertThat(actual.getSql(), CoreMatchers.is(sql));
        Assert.assertThat(actual.getParametersCount(), CoreMatchers.is(1));
    }

    @Test
    public void assertRegisterIfPresent() {
        Assert.assertThat(MySQLBinaryStatementRegistry.getInstance().register(sql, 1), CoreMatchers.is(1));
        Assert.assertThat(MySQLBinaryStatementRegistry.getInstance().register(sql, 1), CoreMatchers.is(1));
        MySQLBinaryStatement actual = MySQLBinaryStatementRegistry.getInstance().getBinaryStatement(1);
        Assert.assertThat(actual.getSql(), CoreMatchers.is(sql));
        Assert.assertThat(actual.getParametersCount(), CoreMatchers.is(1));
    }

    @Test
    public void assertRemoveIfPresent() {
        MySQLBinaryStatementRegistry.getInstance().register(sql, 1);
        MySQLBinaryStatementRegistry.getInstance().remove(1);
        MySQLBinaryStatement actual = MySQLBinaryStatementRegistry.getInstance().getBinaryStatement(1);
        Assert.assertNull(actual);
    }
}

