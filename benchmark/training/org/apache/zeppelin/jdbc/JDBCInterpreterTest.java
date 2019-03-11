/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.zeppelin.jdbc;


import CompletionType.keyword;
import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.HTML;
import InterpreterResult.Type.TABLE;
import InterpreterResult.Type.TEXT;
import JDBCInterpreter.DEFAULT_KEY;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.apache.zeppelin.scheduler.FIFOScheduler;
import org.apache.zeppelin.scheduler.ParallelScheduler;
import org.apache.zeppelin.scheduler.Scheduler;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * JDBC interpreter unit tests.
 */
public class JDBCInterpreterTest extends BasicJDBCTestCaseAdapter {
    static String jdbcConnection;

    InterpreterContext interpreterContext;

    @Test
    public void testForParsePropertyKey() {
        JDBCInterpreter t = new JDBCInterpreter(new Properties());
        Map<String, String> localProperties = new HashMap<>();
        InterpreterContext interpreterContext = InterpreterContext.builder().setLocalProperties(localProperties).build();
        Assert.assertEquals(DEFAULT_KEY, t.getPropertyKey(interpreterContext));
        localProperties = new HashMap<>();
        localProperties.put("db", "mysql");
        interpreterContext = InterpreterContext.builder().setLocalProperties(localProperties).build();
        Assert.assertEquals("mysql", t.getPropertyKey(interpreterContext));
        localProperties = new HashMap<>();
        localProperties.put("hive", "hive");
        interpreterContext = InterpreterContext.builder().setLocalProperties(localProperties).build();
        Assert.assertEquals("hive", t.getPropertyKey(interpreterContext));
    }

    @Test
    public void testForMapPrefix() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table";
        Map<String, String> localProperties = new HashMap<>();
        localProperties.put("db", "fake");
        InterpreterContext context = InterpreterContext.builder().setAuthenticationInfo(new AuthenticationInfo("testUser")).setLocalProperties(localProperties).build();
        InterpreterResult interpreterResult = t.interpret(sqlQuery, context);
        // if prefix not found return ERROR and Prefix not found.
        Assert.assertEquals(ERROR, interpreterResult.code());
        Assert.assertEquals("Prefix not found.", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testDefaultProperties() throws SQLException {
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(JDBCInterpreterTest.getJDBCTestProperties());
        Assert.assertEquals("org.postgresql.Driver", jdbcInterpreter.getProperty(JDBCInterpreter.DEFAULT_DRIVER));
        Assert.assertEquals("jdbc:postgresql://localhost:5432/", jdbcInterpreter.getProperty(JDBCInterpreter.DEFAULT_URL));
        Assert.assertEquals("gpadmin", jdbcInterpreter.getProperty(JDBCInterpreter.DEFAULT_USER));
        Assert.assertEquals("", jdbcInterpreter.getProperty(JDBCInterpreter.DEFAULT_PASSWORD));
        Assert.assertEquals("1000", jdbcInterpreter.getProperty(JDBCInterpreter.COMMON_MAX_LINE));
    }

    @Test
    public void testSelectQuery() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table WHERE ID in ('a', 'b')";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\na\ta_name\nb\tb_name\n", interpreterResult.message().get(0).getData());
        interpreterContext.getLocalProperties().put("limit", "1");
        interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\na\ta_name\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testColumnAliasQuery() throws IOException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select NAME as SOME_OTHER_NAME from test_table limit 1";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("SOME_OTHER_NAME\na_name\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testSplitSqlQuery() throws IOException, SQLException {
        String sqlQuery = "insert into test_table(id, name) values (\'a\', \';\"\');" + (((((((("select * from test_table;" + "select * from test_table WHERE ID = \";\'\";") + "select * from test_table WHERE ID = ';';") + "select \'\n\', \';\';") + "select replace(\'A\\;B\', \'\\\', \'text\');") + "select \'\\\', \';\';") + "select '''', ';';") + "select /*+ scan */ * from test_table;") + "--singleLineComment\nselect * from test_table");
        Properties properties = new Properties();
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        List<String> multipleSqlArray = t.splitSqlQueries(sqlQuery);
        Assert.assertEquals(10, multipleSqlArray.size());
        Assert.assertEquals("insert into test_table(id, name) values (\'a\', \';\"\')", multipleSqlArray.get(0));
        Assert.assertEquals("select * from test_table", multipleSqlArray.get(1));
        Assert.assertEquals("select * from test_table WHERE ID = \";\'\"", multipleSqlArray.get(2));
        Assert.assertEquals("select * from test_table WHERE ID = ';'", multipleSqlArray.get(3));
        Assert.assertEquals("select \'\n\', \';\'", multipleSqlArray.get(4));
        Assert.assertEquals("select replace(\'A\\;B\', \'\\\', \'text\')", multipleSqlArray.get(5));
        Assert.assertEquals("select \'\\\', \';\'", multipleSqlArray.get(6));
        Assert.assertEquals("select '''', ';'", multipleSqlArray.get(7));
        Assert.assertEquals("select /*+ scan */ * from test_table", multipleSqlArray.get(8));
        Assert.assertEquals("--singleLineComment\nselect * from test_table", multipleSqlArray.get(9));
    }

    @Test
    public void testQueryWithEscapedCharacters() throws IOException, SQLException, InterpreterException {
        String sqlQuery = "select \'\\n\', \';\';" + (("select replace(\'A\\;B\', \'\\\', \'text\');" + "select \'\\\', \';\';") + "select '''', ';'");
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty("default.splitQueries", "true");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(TABLE, interpreterResult.message().get(1).getType());
        Assert.assertEquals(TABLE, interpreterResult.message().get(2).getType());
        Assert.assertEquals(TABLE, interpreterResult.message().get(3).getType());
        Assert.assertEquals("\'\\n\'\t\';\'\n\\n\t;\n", interpreterResult.message().get(0).getData());
        Assert.assertEquals("\'Atext;B\'\nAtext;B\n", interpreterResult.message().get(1).getData());
        Assert.assertEquals("\'\\\'\t\';\'\n\\\t;\n", interpreterResult.message().get(2).getData());
        Assert.assertEquals("\'\'\'\'\t\';\'\n\'\t;\n", interpreterResult.message().get(3).getData());
    }

    @Test
    public void testSelectMultipleQueries() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty("default.splitQueries", "true");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table;" + "select * from test_table WHERE ID = ';';";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(2, interpreterResult.message().size());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\na\ta_name\nb\tb_name\nc\tnull\n", interpreterResult.message().get(0).getData());
        Assert.assertEquals(TABLE, interpreterResult.message().get(1).getType());
        Assert.assertEquals("ID\tNAME\n", interpreterResult.message().get(1).getData());
    }

    @Test
    public void testDefaultSplitQuries() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table;" + "select * from test_table WHERE ID = ';';";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\na\ta_name\nb\tb_name\nc\tnull\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testSelectQueryWithNull() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table WHERE ID = 'c'";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\nc\tnull\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testSelectQueryMaxResult() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "select * from test_table";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\tNAME\na\ta_name\n", interpreterResult.message().get(0).getData());
        Assert.assertEquals(HTML, interpreterResult.message().get(1).getType());
        Assert.assertTrue(interpreterResult.message().get(1).getData().contains("alert-warning"));
    }

    @Test
    public void concurrentSettingTest() {
        Properties properties = new Properties();
        properties.setProperty("zeppelin.jdbc.concurrent.use", "true");
        properties.setProperty("zeppelin.jdbc.concurrent.max_connection", "10");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        Assert.assertTrue(jdbcInterpreter.isConcurrentExecution());
        Assert.assertEquals(10, jdbcInterpreter.getMaxConcurrentConnection());
        Scheduler scheduler = jdbcInterpreter.getScheduler();
        Assert.assertTrue((scheduler instanceof ParallelScheduler));
        properties.clear();
        properties.setProperty("zeppelin.jdbc.concurrent.use", "false");
        jdbcInterpreter = new JDBCInterpreter(properties);
        Assert.assertFalse(jdbcInterpreter.isConcurrentExecution());
        scheduler = jdbcInterpreter.getScheduler();
        Assert.assertTrue((scheduler instanceof FIFOScheduler));
    }

    @Test
    public void testAutoCompletion() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        jdbcInterpreter.interpret("", interpreterContext);
        List<InterpreterCompletion> completionList = jdbcInterpreter.completion("sel", 3, interpreterContext);
        InterpreterCompletion correctCompletionKeyword = new InterpreterCompletion("select", "select", keyword.name());
        Assert.assertEquals(1, completionList.size());
        Assert.assertEquals(true, completionList.contains(correctCompletionKeyword));
    }

    @Test
    public void testMultiTenant() throws IOException, SQLException, InterpreterException {
        /* assume that the database user is 'dbuser' and password is 'dbpassword'
        'jdbc1' interpreter has user('dbuser')/password('dbpassword') property
        'jdbc2' interpreter doesn't have user/password property
        'user1' doesn't have Credential information.
        'user2' has 'jdbc2' Credential information that is 'user2Id' / 'user2Pw' as id and password
         */
        JDBCInterpreter jdbc1 = new JDBCInterpreter(getDBProperty("dbuser", "dbpassword"));
        JDBCInterpreter jdbc2 = new JDBCInterpreter(getDBProperty("", ""));
        AuthenticationInfo user1Credential = getUserAuth("user1", null, null, null);
        AuthenticationInfo user2Credential = getUserAuth("user2", "jdbc.jdbc2", "user2Id", "user2Pw");
        // user1 runs jdbc1
        jdbc1.open();
        InterpreterContext ctx1 = InterpreterContext.builder().setAuthenticationInfo(user1Credential).setReplName("jdbc1").build();
        jdbc1.interpret("", ctx1);
        JDBCUserConfigurations user1JDBC1Conf = jdbc1.getJDBCConfiguration("user1");
        Assert.assertEquals("dbuser", user1JDBC1Conf.getPropertyMap("default").get("user"));
        Assert.assertEquals("dbpassword", user1JDBC1Conf.getPropertyMap("default").get("password"));
        jdbc1.close();
        // user1 runs jdbc2
        jdbc2.open();
        InterpreterContext ctx2 = InterpreterContext.builder().setAuthenticationInfo(user1Credential).setReplName("jdbc2").build();
        jdbc2.interpret("", ctx2);
        JDBCUserConfigurations user1JDBC2Conf = jdbc2.getJDBCConfiguration("user1");
        Assert.assertNull(user1JDBC2Conf.getPropertyMap("default").get("user"));
        Assert.assertNull(user1JDBC2Conf.getPropertyMap("default").get("password"));
        jdbc2.close();
        // user2 runs jdbc1
        jdbc1.open();
        InterpreterContext ctx3 = InterpreterContext.builder().setAuthenticationInfo(user2Credential).setReplName("jdbc1").build();
        jdbc1.interpret("", ctx3);
        JDBCUserConfigurations user2JDBC1Conf = jdbc1.getJDBCConfiguration("user2");
        Assert.assertEquals("dbuser", user2JDBC1Conf.getPropertyMap("default").get("user"));
        Assert.assertEquals("dbpassword", user2JDBC1Conf.getPropertyMap("default").get("password"));
        jdbc1.close();
        // user2 runs jdbc2
        jdbc2.open();
        InterpreterContext ctx4 = InterpreterContext.builder().setAuthenticationInfo(user2Credential).setReplName("jdbc2").build();
        jdbc2.interpret("", ctx4);
        JDBCUserConfigurations user2JDBC2Conf = jdbc2.getJDBCConfiguration("user2");
        Assert.assertEquals("user2Id", user2JDBC2Conf.getPropertyMap("default").get("user"));
        Assert.assertEquals("user2Pw", user2JDBC2Conf.getPropertyMap("default").get("password"));
        jdbc2.close();
    }

    @Test
    public void testPrecode() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty(JDBCInterpreter.DEFAULT_PRECODE, "create table test_precode (id int); insert into test_precode values (1);");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        jdbcInterpreter.executePrecode(interpreterContext);
        String sqlQuery = "select *from test_precode";
        InterpreterResult interpreterResult = jdbcInterpreter.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\n1\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testIncorrectPrecode() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty(JDBCInterpreter.DEFAULT_PRECODE, "select 1");
        properties.setProperty("incorrect.driver", "org.h2.Driver");
        properties.setProperty("incorrect.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("incorrect.user", "");
        properties.setProperty("incorrect.password", "");
        properties.setProperty(String.format(JDBCInterpreter.PRECODE_KEY_TEMPLATE, "incorrect"), "incorrect command");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        InterpreterResult interpreterResult = jdbcInterpreter.executePrecode(interpreterContext);
        Assert.assertEquals(ERROR, interpreterResult.code());
        Assert.assertEquals(TEXT, interpreterResult.message().get(0).getType());
    }

    @Test
    public void testPrecodeWithAnotherPrefix() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("anotherPrefix.driver", "org.h2.Driver");
        properties.setProperty("anotherPrefix.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("anotherPrefix.user", "");
        properties.setProperty("anotherPrefix.password", "");
        properties.setProperty(String.format(JDBCInterpreter.PRECODE_KEY_TEMPLATE, "anotherPrefix"), "create table test_precode_2 (id int); insert into test_precode_2 values (2);");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        Map<String, String> localProperties = new HashMap<>();
        localProperties.put("db", "anotherPrefix");
        InterpreterContext context = InterpreterContext.builder().setAuthenticationInfo(new AuthenticationInfo("testUser")).setLocalProperties(localProperties).build();
        jdbcInterpreter.executePrecode(context);
        String sqlQuery = "select * from test_precode_2";
        InterpreterResult interpreterResult = jdbcInterpreter.interpret(sqlQuery, context);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("ID\n2\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testStatementPrecode() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty(JDBCInterpreter.DEFAULT_STATEMENT_PRECODE, "set @v='statement'");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        String sqlQuery = "select @v";
        InterpreterResult interpreterResult = jdbcInterpreter.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("@V\nstatement\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testIncorrectStatementPrecode() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty(JDBCInterpreter.DEFAULT_STATEMENT_PRECODE, "set incorrect");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        String sqlQuery = "select 1";
        InterpreterResult interpreterResult = jdbcInterpreter.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(ERROR, interpreterResult.code());
        Assert.assertEquals(TEXT, interpreterResult.message().get(0).getType());
    }

    @Test
    public void testStatementPrecodeWithAnotherPrefix() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("anotherPrefix.driver", "org.h2.Driver");
        properties.setProperty("anotherPrefix.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("anotherPrefix.user", "");
        properties.setProperty("anotherPrefix.password", "");
        properties.setProperty(String.format(JDBCInterpreter.STATEMENT_PRECODE_KEY_TEMPLATE, "anotherPrefix"), "set @v='statementAnotherPrefix'");
        JDBCInterpreter jdbcInterpreter = new JDBCInterpreter(properties);
        jdbcInterpreter.open();
        Map<String, String> localProperties = new HashMap<>();
        localProperties.put("db", "anotherPrefix");
        InterpreterContext context = InterpreterContext.builder().setAuthenticationInfo(new AuthenticationInfo("testUser")).setLocalProperties(localProperties).build();
        String sqlQuery = "select @v";
        InterpreterResult interpreterResult = jdbcInterpreter.interpret(sqlQuery, context);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals("@V\nstatementAnotherPrefix\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testSplitSqlQueryWithComments() throws IOException, SQLException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", JDBCInterpreterTest.getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty("default.splitQueries", "true");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        String sqlQuery = "/* ; */\n" + (((((((("-- /* comment\n" + "--select * from test_table\n") + "select * from test_table; /* some comment ; */\n") + "/*\n") + "select * from test_table;\n") + "*/\n") + "-- a ; b\n") + "select * from test_table WHERE ID = \';--\';\n") + "select * from test_table WHERE ID = '/*' -- test");
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(3, interpreterResult.message().size());
    }
}

