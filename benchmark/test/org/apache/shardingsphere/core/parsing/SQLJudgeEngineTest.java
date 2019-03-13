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
package org.apache.shardingsphere.core.parsing;


import org.apache.shardingsphere.core.parsing.antlr.sql.statement.tcl.TCLStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.DescribeStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowColumnsStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowCreateTableStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowDatabasesStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowIndexStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowOtherStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowTableStatusStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowTablesStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.UseStatement;
import org.apache.shardingsphere.core.parsing.parser.exception.SQLParsingException;
import org.apache.shardingsphere.core.parsing.parser.sql.SQLStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dal.set.SetStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dml.DMLStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dml.insert.InsertStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.DQLStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SQLJudgeEngineTest {
    @Test
    public void assertJudgeForSelect() {
        Assert.assertThat(new SQLJudgeEngine(" /*COMMENT*/  \t \n  \r \fsElecT\t\n  * from table  ").judge(), CoreMatchers.instanceOf(DQLStatement.class));
    }

    @Test
    public void assertJudgeForInsert() {
        Assert.assertThat(new SQLJudgeEngine(" - - COMMENT  \t \n  \r \finsert\t\n  into table  ").judge(), CoreMatchers.instanceOf(InsertStatement.class));
    }

    @Test
    public void assertJudgeForUpdate() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fuPdAte\t\n  table  ").judge(), CoreMatchers.instanceOf(DMLStatement.class));
    }

    @Test
    public void assertJudgeForDelete() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdelete\t\n  table  ").judge(), CoreMatchers.instanceOf(DMLStatement.class));
    }

    @Test
    public void assertJudgeForSetTransaction() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  transaction  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForSetAutoCommit() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  autocommit  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForSetOther() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  other  ").judge(), CoreMatchers.instanceOf(SetStatement.class));
    }

    @Test
    public void assertJudgeForCommit() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fcommit  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForRollback() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \frollback  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForSavePoint() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fSavePoint  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForBegin() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fbegin  ").judge(), CoreMatchers.instanceOf(TCLStatement.class));
    }

    @Test
    public void assertJudgeForUse() {
        SQLStatement statement = new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fuse sharding_db  ").judge();
        Assert.assertThat(statement, CoreMatchers.instanceOf(UseStatement.class));
        Assert.assertThat(getSchema(), CoreMatchers.is("sharding_db"));
    }

    @Test
    public void assertJudgeForDescribe() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdescribe t_order  ").judge(), CoreMatchers.instanceOf(DescribeStatement.class));
    }

    @Test
    public void assertJudgeForDesc() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdesc t_order  ").judge(), CoreMatchers.instanceOf(DescribeStatement.class));
    }

    @Test
    public void assertJudgeForShowDatabases() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow databases  ").judge(), CoreMatchers.instanceOf(ShowDatabasesStatement.class));
    }

    @Test
    public void assertJudgeForShowTableStatus() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow table status from logic_db").judge(), CoreMatchers.instanceOf(ShowTableStatusStatement.class));
    }

    @Test
    public void assertJudgeForShowTables() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow tables  ").judge(), CoreMatchers.instanceOf(ShowTablesStatement.class));
    }

    @Test
    public void assertJudgeForShowColumns() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow columns from t_order ").judge(), CoreMatchers.instanceOf(ShowColumnsStatement.class));
    }

    @Test
    public void assertJudgeForShowIndex() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow index from t_order ").judge(), CoreMatchers.instanceOf(ShowIndexStatement.class));
    }

    @Test
    public void assertJudgeForShowCreateTable() {
        SQLStatement sqlStatement = new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow create table logic_db.t_order  ").judge();
        Assert.assertThat(sqlStatement, CoreMatchers.instanceOf(ShowCreateTableStatement.class));
        Assert.assertThat(sqlStatement.getSQLTokens().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertJudgeForShowOthers() {
        Assert.assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow session ").judge(), CoreMatchers.instanceOf(ShowOtherStatement.class));
    }

    @Test(expected = SQLParsingException.class)
    public void assertJudgeForInvalidSQL() {
        new SQLJudgeEngine("int i = 0").judge();
    }
}

