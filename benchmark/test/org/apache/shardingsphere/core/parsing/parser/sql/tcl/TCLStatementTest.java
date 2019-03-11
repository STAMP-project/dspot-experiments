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
package org.apache.shardingsphere.core.parsing.parser.sql.tcl;


import DatabaseType.MySQL;
import DefaultKeyword.BEGIN;
import DefaultKeyword.COMMIT;
import DefaultKeyword.ROLLBACK;
import DefaultKeyword.SAVEPOINT;
import DefaultKeyword.SELECT;
import DefaultKeyword.SET;
import org.apache.shardingsphere.core.parsing.antlr.sql.statement.tcl.TCLStatement;
import org.apache.shardingsphere.core.parsing.lexer.LexerEngine;
import org.apache.shardingsphere.core.parsing.lexer.LexerEngineFactory;
import org.junit.Assert;
import org.junit.Test;


public final class TCLStatementTest {
    @Test
    public void assertIsTCLForSetTransaction() {
        LexerEngine lexerEngine = LexerEngineFactory.newInstance(MySQL, "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE");
        lexerEngine.nextToken();
        Assert.assertTrue(TCLStatement.isTCLUnsafe(MySQL, SET, lexerEngine));
    }

    @Test
    public void assertIsTCLForSetAutoCommit() {
        LexerEngine lexerEngine = LexerEngineFactory.newInstance(MySQL, "SET AUTOCOMMIT = 0");
        lexerEngine.nextToken();
        Assert.assertTrue(TCLStatement.isTCLUnsafe(MySQL, SET, lexerEngine));
    }

    @Test
    public void assertIsTCLForCommit() {
        Assert.assertTrue(TCLStatement.isTCL(COMMIT));
    }

    @Test
    public void assertIsTCLForRollback() {
        Assert.assertTrue(TCLStatement.isTCL(ROLLBACK));
    }

    @Test
    public void assertIsTCLForSavePoint() {
        Assert.assertTrue(TCLStatement.isTCL(SAVEPOINT));
    }

    @Test
    public void assertIsTCLForBegin() {
        Assert.assertTrue(TCLStatement.isTCL(BEGIN));
    }

    @Test
    public void assertIsNotTCL() {
        Assert.assertFalse(TCLStatement.isTCL(SELECT));
    }
}

