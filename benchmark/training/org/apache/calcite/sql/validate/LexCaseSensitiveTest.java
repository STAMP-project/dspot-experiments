/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql.validate;


import Lex.JAVA;
import Lex.MYSQL;
import Lex.MYSQL_ANSI;
import Lex.ORACLE;
import Lex.SQL_SERVER;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.junit.Test;


/**
 * Testing {@link SqlValidator} and {@link Lex}.
 */
public class LexCaseSensitiveTest {
    @Test
    public void testCalciteCaseOracle() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select \"empid\" as EMPID, \"empid\" from\n" + " (select \"empid\" from \"emps\" order by \"emps\".\"deptno\")";
        LexCaseSensitiveTest.runProjectQueryWithLex(ORACLE, sql);
    }

    @Test(expected = ValidationException.class)
    public void testCalciteCaseOracleException() throws SqlParseException, RelConversionException, ValidationException {
        // Oracle is case sensitive, so EMPID should not be found.
        String sql = "select EMPID, \"empid\" from\n" + " (select \"empid\" from \"emps\" order by \"emps\".\"deptno\")";
        LexCaseSensitiveTest.runProjectQueryWithLex(ORACLE, sql);
    }

    @Test
    public void testCalciteCaseMySql() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select empid as EMPID, empid from (\n" + "  select empid from emps order by `EMPS`.DEPTNO)";
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL, sql);
    }

    @Test
    public void testCalciteCaseMySqlNoException() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select EMPID, empid from\n" + " (select empid from emps order by emps.deptno)";
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL, sql);
    }

    @Test
    public void testCalciteCaseMySqlAnsi() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select empid as EMPID, empid from (\n" + "  select empid from emps order by EMPS.DEPTNO)";
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL_ANSI, sql);
    }

    @Test
    public void testCalciteCaseMySqlAnsiNoException() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select EMPID, empid from\n" + " (select empid from emps order by emps.deptno)";
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL_ANSI, sql);
    }

    @Test
    public void testCalciteCaseSqlServer() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select empid as EMPID, empid from (\n" + "  select empid from emps order by EMPS.DEPTNO)";
        LexCaseSensitiveTest.runProjectQueryWithLex(SQL_SERVER, sql);
    }

    @Test
    public void testCalciteCaseSqlServerNoException() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select EMPID, empid from\n" + " (select empid from emps order by emps.deptno)";
        LexCaseSensitiveTest.runProjectQueryWithLex(SQL_SERVER, sql);
    }

    @Test
    public void testCalciteCaseJava() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select empid as EMPID, empid from (\n" + "  select empid from emps order by emps.deptno)";
        LexCaseSensitiveTest.runProjectQueryWithLex(JAVA, sql);
    }

    @Test(expected = ValidationException.class)
    public void testCalciteCaseJavaException() throws SqlParseException, RelConversionException, ValidationException {
        // JAVA is case sensitive, so EMPID should not be found.
        String sql = "select EMPID, empid from\n" + " (select empid from emps order by emps.deptno)";
        LexCaseSensitiveTest.runProjectQueryWithLex(JAVA, sql);
    }

    @Test
    public void testCalciteCaseJoinOracle() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select t.\"empid\" as EMPID, s.\"empid\" from\n" + (("(select * from \"emps\" where \"emps\".\"deptno\" > 100) t join\n" + "(select * from \"emps\" where \"emps\".\"deptno\" < 200) s\n") + "on t.\"empid\" = s.\"empid\"");
        LexCaseSensitiveTest.runProjectQueryWithLex(ORACLE, sql);
    }

    @Test
    public void testCalciteCaseJoinMySql() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select t.empid as EMPID, s.empid from\n" + ("(select * from emps where emps.deptno > 100) t join\n" + "(select * from emps where emps.deptno < 200) s on t.empid = s.empid");
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL, sql);
    }

    @Test
    public void testCalciteCaseJoinMySqlAnsi() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select t.empid as EMPID, s.empid from\n" + ("(select * from emps where emps.deptno > 100) t join\n" + "(select * from emps where emps.deptno < 200) s on t.empid = s.empid");
        LexCaseSensitiveTest.runProjectQueryWithLex(MYSQL_ANSI, sql);
    }

    @Test
    public void testCalciteCaseJoinSqlServer() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select t.empid as EMPID, s.empid from\n" + ("(select * from emps where emps.deptno > 100) t join\n" + "(select * from emps where emps.deptno < 200) s on t.empid = s.empid");
        LexCaseSensitiveTest.runProjectQueryWithLex(SQL_SERVER, sql);
    }

    @Test
    public void testCalciteCaseJoinJava() throws SqlParseException, RelConversionException, ValidationException {
        String sql = "select t.empid as EMPID, s.empid from\n" + ("(select * from emps where emps.deptno > 100) t join\n" + "(select * from emps where emps.deptno < 200) s on t.empid = s.empid");
        LexCaseSensitiveTest.runProjectQueryWithLex(JAVA, sql);
    }
}

/**
 * End LexCaseSensitiveTest.java
 */
