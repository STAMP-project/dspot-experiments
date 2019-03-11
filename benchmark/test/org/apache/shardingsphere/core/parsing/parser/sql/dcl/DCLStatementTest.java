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
package org.apache.shardingsphere.core.parsing.parser.sql.dcl;


import DefaultKeyword.ALTER;
import DefaultKeyword.CREATE;
import DefaultKeyword.DENY;
import DefaultKeyword.DROP;
import DefaultKeyword.GRANT;
import DefaultKeyword.LOGIN;
import DefaultKeyword.RENAME;
import DefaultKeyword.REVOKE;
import DefaultKeyword.ROLE;
import DefaultKeyword.SELECT;
import DefaultKeyword.TABLE;
import DefaultKeyword.USER;
import org.apache.shardingsphere.core.parsing.antlr.sql.statement.dcl.DCLStatement;
import org.junit.Assert;
import org.junit.Test;


public final class DCLStatementTest {
    @Test
    public void assertIsDCLForGrant() {
        Assert.assertTrue(DCLStatement.isDCL(GRANT, SELECT));
    }

    @Test
    public void assertIsDCLForRevoke() {
        Assert.assertTrue(DCLStatement.isDCL(REVOKE, SELECT));
    }

    @Test
    public void assertIsDCLForDeny() {
        Assert.assertTrue(DCLStatement.isDCL(DENY, SELECT));
    }

    @Test
    public void assertIsDCLForCreateLogin() {
        Assert.assertTrue(DCLStatement.isDCL(CREATE, LOGIN));
    }

    @Test
    public void assertIsDCLForCreateUser() {
        Assert.assertTrue(DCLStatement.isDCL(CREATE, USER));
    }

    @Test
    public void assertIsDCLForCreateRole() {
        Assert.assertTrue(DCLStatement.isDCL(CREATE, ROLE));
    }

    @Test
    public void assertIsDCLForAlterLogin() {
        Assert.assertTrue(DCLStatement.isDCL(ALTER, LOGIN));
    }

    @Test
    public void assertIsDCLForAlterUser() {
        Assert.assertTrue(DCLStatement.isDCL(ALTER, USER));
    }

    @Test
    public void assertIsDCLForAlterRole() {
        Assert.assertTrue(DCLStatement.isDCL(ALTER, ROLE));
    }

    @Test
    public void assertIsDCLForDropLogin() {
        Assert.assertTrue(DCLStatement.isDCL(DROP, LOGIN));
    }

    @Test
    public void assertIsDCLForDropUser() {
        Assert.assertTrue(DCLStatement.isDCL(DROP, USER));
    }

    @Test
    public void assertIsDCLForDropRole() {
        Assert.assertTrue(DCLStatement.isDCL(DROP, ROLE));
    }

    @Test
    public void assertIsDCLForRenameLogin() {
        Assert.assertTrue(DCLStatement.isDCL(RENAME, LOGIN));
    }

    @Test
    public void assertIsDCLForRenameUser() {
        Assert.assertTrue(DCLStatement.isDCL(RENAME, USER));
    }

    @Test
    public void assertIsDCLForRenameRole() {
        Assert.assertTrue(DCLStatement.isDCL(RENAME, ROLE));
    }

    @Test
    public void assertIsNotDCLForCreateTable() {
        Assert.assertFalse(DCLStatement.isDCL(CREATE, TABLE));
    }
}

