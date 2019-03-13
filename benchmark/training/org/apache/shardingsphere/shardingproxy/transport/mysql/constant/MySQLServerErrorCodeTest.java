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
package org.apache.shardingsphere.shardingproxy.transport.mysql.constant;


import MySQLServerErrorCode.ER_ACCESS_DENIED_ERROR;
import MySQLServerErrorCode.ER_BAD_DB_ERROR;
import MySQLServerErrorCode.ER_ERROR_ON_MODIFYING_GTID_EXECUTED_TABLE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLServerErrorCodeTest {
    @Test
    public void assertAccessDeniedError() {
        Assert.assertThat(ER_ACCESS_DENIED_ERROR.getErrorCode(), CoreMatchers.is(1045));
        Assert.assertThat(ER_ACCESS_DENIED_ERROR.getSqlState(), CoreMatchers.is("28000"));
        Assert.assertThat(ER_ACCESS_DENIED_ERROR.getErrorMessage(), CoreMatchers.is("Access denied for user '%s'@'%s' (using password: %s)"));
    }

    @Test
    public void assertBadDbError() {
        Assert.assertThat(ER_BAD_DB_ERROR.getErrorCode(), CoreMatchers.is(1049));
        Assert.assertThat(ER_BAD_DB_ERROR.getSqlState(), CoreMatchers.is("42000"));
        Assert.assertThat(ER_BAD_DB_ERROR.getErrorMessage(), CoreMatchers.is("Unknown database '%s'"));
    }

    @Test
    public void assertErrorOnModifyingGtidExecutedTable() {
        Assert.assertThat(ER_ERROR_ON_MODIFYING_GTID_EXECUTED_TABLE.getErrorCode(), CoreMatchers.is(3176));
        Assert.assertThat(ER_ERROR_ON_MODIFYING_GTID_EXECUTED_TABLE.getSqlState(), CoreMatchers.is("HY000"));
        Assert.assertThat(ER_ERROR_ON_MODIFYING_GTID_EXECUTED_TABLE.getErrorMessage(), CoreMatchers.is(("Please do not modify the %s table with an XA transaction. " + ("This is an internal system table used to store GTIDs for committed transactions. " + "Although modifying it can lead to an inconsistent GTID state, if neccessary you can modify it with a non-XA transaction."))));
    }
}

