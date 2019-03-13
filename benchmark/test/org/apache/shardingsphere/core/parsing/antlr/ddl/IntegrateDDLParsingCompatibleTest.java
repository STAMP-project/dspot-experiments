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
package org.apache.shardingsphere.core.parsing.antlr.ddl;


import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.parsing.antlr.autogen.MySQLStatementLexer;
import org.apache.shardingsphere.core.parsing.antlr.autogen.OracleStatementLexer;
import org.apache.shardingsphere.core.parsing.antlr.autogen.PostgreSQLStatementLexer;
import org.apache.shardingsphere.core.parsing.antlr.autogen.SQLServerStatementLexer;
import org.apache.shardingsphere.core.parsing.antlr.parser.impl.dialect.MySQLParser;
import org.apache.shardingsphere.core.parsing.antlr.parser.impl.dialect.OracleParser;
import org.apache.shardingsphere.core.parsing.antlr.parser.impl.dialect.PostgreSQLParser;
import org.apache.shardingsphere.core.parsing.antlr.parser.impl.dialect.SQLServerParser;
import org.apache.shardingsphere.core.parsing.integrate.asserts.ParserResultSetLoader;
import org.apache.shardingsphere.core.parsing.integrate.asserts.SQLStatementAssert;
import org.apache.shardingsphere.core.parsing.integrate.engine.AbstractBaseIntegrateSQLParsingTest;
import org.apache.shardingsphere.test.sql.SQLCaseType;
import org.apache.shardingsphere.test.sql.SQLCasesLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@RequiredArgsConstructor
public final class IntegrateDDLParsingCompatibleTest extends AbstractBaseIntegrateSQLParsingTest {
    private static SQLCasesLoader sqlCasesLoader = SQLCasesLoader.getInstance();

    private static ParserResultSetLoader parserResultSetLoader = ParserResultSetLoader.getInstance();

    private final String sqlCaseId;

    private final DatabaseType databaseType;

    private final SQLCaseType sqlCaseType;

    @Test
    public void parsingSupportedSQL() throws Exception {
        String sql = IntegrateDDLParsingCompatibleTest.sqlCasesLoader.getSupportedSQL(sqlCaseId, sqlCaseType, Collections.emptyList());
        CodePointCharStream charStream = CharStreams.fromString(sql);
        switch (databaseType) {
            case H2 :
            case MySQL :
                execute(MySQLStatementLexer.class, MySQLParser.class, charStream);
                break;
            case Oracle :
                execute(OracleStatementLexer.class, OracleParser.class, charStream);
                break;
            case PostgreSQL :
                execute(PostgreSQLStatementLexer.class, PostgreSQLParser.class, charStream);
                break;
            case SQLServer :
                execute(SQLServerStatementLexer.class, SQLServerParser.class, charStream);
                break;
            default :
                break;
        }
    }

    @Test
    public void assertSupportedSQL() {
        String sql = IntegrateDDLParsingCompatibleTest.sqlCasesLoader.getSupportedSQL(sqlCaseId, sqlCaseType, IntegrateDDLParsingCompatibleTest.parserResultSetLoader.getParserResult(sqlCaseId).getParameters());
        DatabaseType execDatabaseType = databaseType;
        if ((DatabaseType.H2) == (databaseType)) {
            execDatabaseType = DatabaseType.MySQL;
        }
        new SQLStatementAssert(parse(), sqlCaseId, sqlCaseType, IntegrateDDLParsingCompatibleTest.sqlCasesLoader, IntegrateDDLParsingCompatibleTest.parserResultSetLoader).assertSQLStatement();
    }
}

