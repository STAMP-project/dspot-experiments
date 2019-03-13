/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.client.cli;


import org.apache.flink.table.client.cli.SqlCommandParser.SqlCommand;
import org.junit.Test;


/**
 * Tests for {@link SqlCommandParser}.
 */
public class SqlCommandParserTest {
    @Test
    public void testCommands() {
        testValidSqlCommand("QUIT;", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.QUIT));
        testValidSqlCommand("eXiT", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.QUIT));
        testValidSqlCommand("CLEAR", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.CLEAR));
        testValidSqlCommand("SHOW TABLES", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SHOW_TABLES));
        testValidSqlCommand("  SHOW   TABLES   ", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SHOW_TABLES));
        testValidSqlCommand("SHOW FUNCTIONS", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SHOW_FUNCTIONS));
        testValidSqlCommand("  SHOW    FUNCTIONS   ", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SHOW_FUNCTIONS));
        testValidSqlCommand("DESCRIBE MyTable", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.DESCRIBE, new String[]{ "MyTable" }));
        testValidSqlCommand("DESCRIBE         MyTable     ", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.DESCRIBE, new String[]{ "MyTable" }));
        testInvalidSqlCommand("DESCRIBE  ");// no table name

        testValidSqlCommand("EXPLAIN SELECT complicated FROM table", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.EXPLAIN, new String[]{ "SELECT complicated FROM table" }));
        testInvalidSqlCommand("EXPLAIN  ");// no query

        testValidSqlCommand("SELECT complicated FROM table", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SELECT, new String[]{ "SELECT complicated FROM table" }));
        testValidSqlCommand("   SELECT  complicated FROM table    ", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SELECT, new String[]{ "SELECT  complicated FROM table" }));
        testValidSqlCommand("INSERT INTO other SELECT 1+1", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.INSERT_INTO, new String[]{ "INSERT INTO other SELECT 1+1" }));
        testValidSqlCommand("CREATE VIEW x AS SELECT 1+1", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.CREATE_VIEW, new String[]{ "x", "SELECT 1+1" }));
        testValidSqlCommand("CREATE   VIEW    MyTable   AS     SELECT 1+1 FROM y", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.CREATE_VIEW, new String[]{ "MyTable", "SELECT 1+1 FROM y" }));
        testInvalidSqlCommand("CREATE VIEW x SELECT 1+1");// missing AS

        testValidSqlCommand("DROP VIEW MyTable", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.DROP_VIEW, new String[]{ "MyTable" }));
        testValidSqlCommand("DROP VIEW  MyTable", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.DROP_VIEW, new String[]{ "MyTable" }));
        testInvalidSqlCommand("DROP VIEW");
        testValidSqlCommand("SET", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SET));
        testValidSqlCommand("SET x=y", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SET, new String[]{ "x", "y" }));
        testValidSqlCommand("SET    x  = y", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SET, new String[]{ "x", " y" }));
        testValidSqlCommand("reset;", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.RESET));
        testValidSqlCommand("source /my/file", new org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall(SqlCommand.SOURCE, new String[]{ "/my/file" }));
        testInvalidSqlCommand("source");// missing path

    }
}

