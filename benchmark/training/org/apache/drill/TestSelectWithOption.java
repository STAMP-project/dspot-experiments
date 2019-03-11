/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill;


import java.io.File;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.store.dfs.WorkspaceSchemaFactory;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(SqlTest.class)
public class TestSelectWithOption extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceSchemaFactory.class);

    @Test
    public void testTextFieldDelimiter() throws Exception {
        String tableName = genCSVTable("testTextFieldDelimiter", "\"b\"|\"0\"", "\"b\"|\"1\"", "\"b\"|\"2\"");
        String queryTemplate = "select columns from table(%s (type => 'TeXT', fieldDelimiter => '%s'))";
        testWithResult(String.format(queryTemplate, tableName, ","), TestBuilder.listOf("b\"|\"0"), TestBuilder.listOf("b\"|\"1"), TestBuilder.listOf("b\"|\"2"));
        testWithResult(String.format(queryTemplate, tableName, "|"), TestBuilder.listOf("b", "0"), TestBuilder.listOf("b", "1"), TestBuilder.listOf("b", "2"));
    }

    @Test
    public void testTabFieldDelimiter() throws Exception {
        String tableName = genCSVTable("testTabFieldDelimiter", "1\ta", "2\tb");
        String fieldDelimiter = new String(new char[]{ 92, 116 });// represents \t

        testWithResult(String.format("select columns from table(%s(type=>'TeXT', fieldDelimiter => '%s'))", tableName, fieldDelimiter), TestBuilder.listOf("1", "a"), TestBuilder.listOf("2", "b"));
    }

    @Test
    public void testSingleTextLineDelimiter() throws Exception {
        String tableName = genCSVTable("testSingleTextLineDelimiter", "a|b|c");
        testWithResult(String.format("select columns from table(%s(type => 'TeXT', lineDelimiter => '|'))", tableName), TestBuilder.listOf("a"), TestBuilder.listOf("b"), TestBuilder.listOf("c"));
    }

    // '\n' is treated as standard delimiter
    // if user has indicated custom line delimiter but input file contains '\n', split will occur on both
    @Test
    public void testCustomTextLineDelimiterAndNewLine() throws Exception {
        String tableName = genCSVTable("testTextLineDelimiter", "b|1", "b|2");
        testWithResult(String.format("select columns from table(%s(type => 'TeXT', lineDelimiter => '|'))", tableName), TestBuilder.listOf("b"), TestBuilder.listOf("1"), TestBuilder.listOf("b"), TestBuilder.listOf("2"));
    }

    @Test
    public void testTextLineDelimiterWithCarriageReturn() throws Exception {
        String tableName = genCSVTable("testTextLineDelimiterWithCarriageReturn", "1, a\r", "2, b\r");
        String lineDelimiter = new String(new char[]{ 92, 114, 92, 110 });// represents \r\n

        testWithResult(String.format("select columns from table(%s(type=>'TeXT', lineDelimiter => '%s'))", tableName, lineDelimiter), TestBuilder.listOf("1, a"), TestBuilder.listOf("2, b"));
    }

    @Test
    public void testMultiByteLineDelimiter() throws Exception {
        String tableName = genCSVTable("testMultiByteLineDelimiter", "1abc2abc3abc");
        testWithResult(String.format("select columns from table(%s(type=>'TeXT', lineDelimiter => 'abc'))", tableName), TestBuilder.listOf("1"), TestBuilder.listOf("2"), TestBuilder.listOf("3"));
    }

    @Test
    public void testDataWithPartOfMultiByteLineDelimiter() throws Exception {
        String tableName = genCSVTable("testDataWithPartOfMultiByteLineDelimiter", "ab1abc2abc3abc");
        testWithResult(String.format("select columns from table(%s(type=>'TeXT', lineDelimiter => 'abc'))", tableName), TestBuilder.listOf("ab1"), TestBuilder.listOf("2"), TestBuilder.listOf("3"));
    }

    @Test
    public void testTextQuote() throws Exception {
        String tableName = genCSVTable("testTextQuote", "\"b\"|\"0\"", "\"b\"|\"1\"", "\"b\"|\"2\"");
        testWithResult(String.format("select columns from table(%s(type => 'TeXT', fieldDelimiter => '|', quote => '@'))", tableName), TestBuilder.listOf("\"b\"", "\"0\""), TestBuilder.listOf("\"b\"", "\"1\""), TestBuilder.listOf("\"b\"", "\"2\""));
        String quoteTableName = genCSVTable("testTextQuote2", "@b@|@0@", "@b$@c@|@1@");
        // It seems that a parameter can not be called "escape"
        // shouldn't $ be removed here?
        testWithResult(String.format("select columns from table(%s(`escape` => '$', type => 'TeXT', fieldDelimiter => '|', quote => '@'))", quoteTableName), TestBuilder.listOf("b", "0"), TestBuilder.listOf("b$@c", "1"));
    }

    @Test
    public void testTextComment() throws Exception {
        String commentTableName = genCSVTable("testTextComment", "b|0", "@ this is a comment", "b|1");
        testWithResult(String.format("select columns from table(%s(type => 'TeXT', fieldDelimiter => '|', comment => '@'))", commentTableName), TestBuilder.listOf("b", "0"), TestBuilder.listOf("b", "1"));
    }

    @Test
    public void testTextHeader() throws Exception {
        String headerTableName = genCSVTable("testTextHeader", "b|a", "b|0", "b|1");
        testWithResult(String.format("select columns from table(%s(type => 'TeXT', fieldDelimiter => '|', skipFirstLine => true))", headerTableName), TestBuilder.listOf("b", "0"), TestBuilder.listOf("b", "1"));
        BaseTestQuery.testBuilder().sqlQuery(String.format("select a, b from table(%s(type => 'TeXT', fieldDelimiter => '|', extractHeader => true))", headerTableName)).ordered().baselineColumns("b", "a").baselineValues("b", "0").baselineValues("b", "1").build().run();
    }

    @Test
    public void testVariationsCSV() throws Exception {
        String csvTableName = genCSVTable("testVariationsCSV", "a,b", "c|d");
        // Using the defaults in TextFormatConfig (the field delimiter is neither "," not "|")
        String[] csvQueries = // format("select columns from %s (type => 'TeXT')", csvTableName)
        new String[]{ // format("select columns from %s ('TeXT')", csvTableName),
        // format("select columns from %s('TeXT')", csvTableName),
        String.format("select columns from table(%s ('TeXT'))", csvTableName), String.format("select columns from table(%s (type => 'TeXT'))", csvTableName) }// format("select columns from %s (type => 'TeXT')", csvTableName)
        ;
        for (String csvQuery : csvQueries) {
            testWithResult(csvQuery, TestBuilder.listOf("a,b"), TestBuilder.listOf("c|d"));
        }
        // the drill config file binds .csv to "," delimited
        testWithResult(String.format("select columns from %s", csvTableName), TestBuilder.listOf("a", "b"), TestBuilder.listOf("c|d"));
        // setting the delimiter
        testWithResult(String.format("select columns from table(%s (type => 'TeXT', fieldDelimiter => ','))", csvTableName), TestBuilder.listOf("a", "b"), TestBuilder.listOf("c|d"));
        testWithResult(String.format("select columns from table(%s (type => 'TeXT', fieldDelimiter => '|'))", csvTableName), TestBuilder.listOf("a,b"), TestBuilder.listOf("c", "d"));
    }

    @Test
    public void testVariationsJSON() throws Exception {
        String jsonTableName = genCSVTable("testVariationsJSON", "{\"columns\": [\"f\",\"g\"]}");
        // the extension is actually csv
        testWithResult(String.format("select columns from %s", jsonTableName), TestBuilder.listOf("{\"columns\": [\"f\"", "g\"]}\n"));
        String[] jsonQueries = new String[]{ String.format("select columns from table(%s ('JSON'))", jsonTableName), String.format("select columns from table(%s(type => 'JSON'))", jsonTableName), // we can use named format plugin configurations too!
        String.format("select columns from table(%s(type => 'Named', name => 'json'))", jsonTableName) };
        for (String jsonQuery : jsonQueries) {
            testWithResult(jsonQuery, TestBuilder.listOf("f", "g"));
        }
    }

    @Test
    public void testUse() throws Exception {
        File f = genCSVFile("testUse", "{\"columns\": [\"f\",\"g\"]}");
        String jsonTableName = String.format("dfs.`%s`", f.getName());
        // the extension is actually csv
        BaseTestQuery.test("use dfs");
        try {
            String[] jsonQueries = new String[]{ String.format("select columns from table(%s ('JSON'))", jsonTableName), String.format("select columns from table(%s(type => 'JSON'))", jsonTableName) };
            for (String jsonQuery : jsonQueries) {
                testWithResult(jsonQuery, TestBuilder.listOf("f", "g"));
            }
            testWithResult(String.format("select length(columns[0]) as columns from table(%s ('JSON'))", jsonTableName), 1L);
        } finally {
            BaseTestQuery.test("use sys");
        }
    }

    @Test(expected = UserRemoteException.class)
    public void testAbsentTable() throws Exception {
        String schema = "cp.default";
        String tableName = "absent_table";
        try {
            BaseTestQuery.test("select * from table(`%s`.`%s`(type=>'parquet'))", schema, tableName);
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(String.format("Unable to find table [%s] in schema [%s]", tableName, schema)));
            throw e;
        }
    }
}

