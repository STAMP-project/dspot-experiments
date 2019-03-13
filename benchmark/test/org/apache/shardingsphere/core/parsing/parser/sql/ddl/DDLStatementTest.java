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
package org.apache.shardingsphere.core.parsing.parser.sql.ddl;


import DefaultKeyword.ALTER;
import DefaultKeyword.CREATE;
import DefaultKeyword.DROP;
import DefaultKeyword.INDEX;
import DefaultKeyword.LOGIN;
import DefaultKeyword.ROLE;
import DefaultKeyword.TABLE;
import DefaultKeyword.TRUNCATE;
import DefaultKeyword.UNIQUE;
import DefaultKeyword.USER;
import org.apache.shardingsphere.core.parsing.antlr.sql.statement.ddl.DDLStatement;
import org.junit.Assert;
import org.junit.Test;


public final class DDLStatementTest {
    @Test
    public void assertIsDDLForCreateTable() {
        Assert.assertTrue(DDLStatement.isDDL(CREATE, TABLE));
    }

    @Test
    public void assertIsDDLForCreateIndex() {
        Assert.assertTrue(DDLStatement.isDDL(CREATE, INDEX));
    }

    @Test
    public void assertIsDDLForCreateUniqueIndex() {
        Assert.assertTrue(DDLStatement.isDDL(CREATE, UNIQUE));
    }

    @Test
    public void assertIsDDLForAlterTable() {
        Assert.assertTrue(DDLStatement.isDDL(ALTER, TABLE));
    }

    @Test
    public void assertIsDDLForDropTable() {
        Assert.assertTrue(DDLStatement.isDDL(DROP, TABLE));
    }

    @Test
    public void assertIsDDLForDropIndex() {
        Assert.assertTrue(DDLStatement.isDDL(DROP, INDEX));
    }

    @Test
    public void assertIsDDLForTruncateTable() {
        Assert.assertTrue(DDLStatement.isDDL(TRUNCATE, TABLE));
    }

    @Test
    public void assertIsNotDDLForCreateLogin() {
        Assert.assertFalse(DDLStatement.isDDL(CREATE, LOGIN));
    }

    @Test
    public void assertIsNotDDLForCreateUser() {
        Assert.assertFalse(DDLStatement.isDDL(CREATE, USER));
    }

    @Test
    public void assertIsNotDDLForCreateRole() {
        Assert.assertFalse(DDLStatement.isDDL(CREATE, ROLE));
    }
}

