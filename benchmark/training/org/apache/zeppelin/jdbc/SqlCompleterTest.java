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


import ArgumentCompleter.WhitespaceArgumentDelimiter;
import CompletionType.column;
import CompletionType.keyword;
import CompletionType.schema;
import CompletionType.table;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jline.console.completer.ArgumentCompleter;
import org.apache.commons.lang.StringUtils;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SQL completer unit tests.
 */
public class SqlCompleterTest {
    public class CompleterTester {
        private SqlCompleter completer;

        private String buffer;

        private int fromCursor;

        private int toCursor;

        private Set<InterpreterCompletion> expectedCompletions;

        public CompleterTester(SqlCompleter completer) {
            this.completer = completer;
        }

        public SqlCompleterTest.CompleterTester buffer(String buffer) {
            this.buffer = buffer;
            return this;
        }

        public SqlCompleterTest.CompleterTester from(int fromCursor) {
            this.fromCursor = fromCursor;
            return this;
        }

        public SqlCompleterTest.CompleterTester to(int toCursor) {
            this.toCursor = toCursor;
            return this;
        }

        public SqlCompleterTest.CompleterTester expect(Set<InterpreterCompletion> expectedCompletions) {
            this.expectedCompletions = expectedCompletions;
            return this;
        }

        public void test() {
            for (int c = fromCursor; c <= (toCursor); c++) {
                expectedCompletions(buffer, c, expectedCompletions);
            }
        }

        private void expectedCompletions(String buffer, int cursor, Set<InterpreterCompletion> expected) {
            if ((StringUtils.isNotEmpty(buffer)) && ((buffer.length()) > cursor)) {
                buffer = buffer.substring(0, cursor);
            }
            List<InterpreterCompletion> candidates = new ArrayList<>();
            completer.complete(buffer, cursor, candidates);
            String explain = explain(buffer, cursor, candidates);
            logger.info(explain);
            Assert.assertEquals(((((("Buffer [" + (buffer.replace(" ", "."))) + "] and Cursor[") + cursor) + "] ") + explain), expected, Sets.newHashSet(candidates));
        }

        private String explain(String buffer, int cursor, List<InterpreterCompletion> candidates) {
            List<String> cndidateStrings = new ArrayList<>();
            for (InterpreterCompletion candidate : candidates) {
                cndidateStrings.add(candidate.getValue());
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i <= (Math.max(cursor, buffer.length())); i++) {
                if (i == cursor) {
                    sb.append("(");
                }
                if (i >= (buffer.length())) {
                    sb.append("_");
                } else {
                    if (Character.isWhitespace(buffer.charAt(i))) {
                        sb.append(".");
                    } else {
                        sb.append(buffer.charAt(i));
                    }
                }
                if (i == cursor) {
                    sb.append(")");
                }
            }
            sb.append(" >> [").append(Joiner.on(",").join(cndidateStrings)).append("]");
            return sb.toString();
        }
    }

    private Logger logger = LoggerFactory.getLogger(SqlCompleterTest.class);

    private static final Set<String> EMPTY = new HashSet<>();

    private SqlCompleterTest.CompleterTester tester;

    private WhitespaceArgumentDelimiter delimiter = new ArgumentCompleter.WhitespaceArgumentDelimiter();

    private SqlCompleter sqlCompleter = new SqlCompleter(0);

    @Test
    public void testFindAliasesInSQL_Simple() {
        String sql = "select * from prod_emart.financial_account a";
        Map<String, String> res = sqlCompleter.findAliasesInSQL(delimiter.delimit(sql, 0).getArguments());
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.get("a").equals("prod_emart.financial_account"));
    }

    @Test
    public void testFindAliasesInSQL_Two() {
        String sql = "select * from prod_dds.financial_account a, prod_dds.customer b";
        Map<String, String> res = sqlCompleter.findAliasesInSQL(sqlCompleter.getSqlDelimiter().delimit(sql, 0).getArguments());
        Assert.assertEquals(2, res.size());
        Assert.assertTrue(res.get("a").equals("prod_dds.financial_account"));
        Assert.assertTrue(res.get("b").equals("prod_dds.customer"));
    }

    @Test
    public void testFindAliasesInSQL_WrongTables() {
        String sql = "select * from prod_ddsxx.financial_account a, prod_dds.customerxx b";
        Map<String, String> res = sqlCompleter.findAliasesInSQL(sqlCompleter.getSqlDelimiter().delimit(sql, 0).getArguments());
        Assert.assertEquals(0, res.size());
    }

    @Test
    public void testCompleteName_Empty() {
        String buffer = "";
        int cursor = 0;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(9, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("prod_dds", "prod_dds", schema.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("prod_emart", "prod_emart", schema.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("SUM", "SUM", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("SUBSTRING", "SUBSTRING", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("SUBCLASS_ORIGIN", "SUBCLASS_ORIGIN", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("SELECT", "SELECT", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("ORDER", "ORDER", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("LIMIT", "LIMIT", keyword.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("FROM", "FROM", keyword.name())));
    }

    @Test
    public void testCompleteName_SimpleSchema() {
        String buffer = "prod_";
        int cursor = 3;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(2, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("prod_dds", "prod_dds", schema.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("prod_emart", "prod_emart", schema.name())));
    }

    @Test
    public void testCompleteName_SimpleTable() {
        String buffer = "prod_dds.fin";
        int cursor = 11;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(1, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("financial_account", "financial_account", table.name())));
    }

    @Test
    public void testCompleteName_SimpleColumn() {
        String buffer = "prod_dds.financial_account.acc";
        int cursor = 30;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(2, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_rk", "account_rk", column.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_id", "account_id", column.name())));
    }

    @Test
    public void testCompleteName_WithAlias() {
        String buffer = "a.acc";
        int cursor = 4;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        aliases.put("a", "prod_dds.financial_account");
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(2, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_rk", "account_rk", column.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_id", "account_id", column.name())));
    }

    @Test
    public void testCompleteName_WithAliasAndPoint() {
        String buffer = "a.";
        int cursor = 2;
        List<InterpreterCompletion> candidates = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        aliases.put("a", "prod_dds.financial_account");
        sqlCompleter.completeName(buffer, cursor, candidates, aliases);
        Assert.assertEquals(2, candidates.size());
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_rk", "account_rk", column.name())));
        Assert.assertTrue(candidates.contains(new InterpreterCompletion("account_id", "account_id", column.name())));
    }

    @Test
    public void testSchemaAndTable() {
        String buffer = "select * from prod_emart.fi";
        tester.buffer(buffer).from(20).to(23).expect(Sets.newHashSet(new InterpreterCompletion("prod_emart", "prod_emart", schema.name()))).test();
        tester.buffer(buffer).from(25).to(27).expect(Sets.newHashSet(new InterpreterCompletion("financial_account", "financial_account", table.name()))).test();
    }

    @Test
    public void testEdges() {
        String buffer = "  ORDER  ";
        tester.buffer(buffer).from(3).to(7).expect(Sets.newHashSet(new InterpreterCompletion("ORDER", "ORDER", keyword.name()))).test();
        tester.buffer(buffer).from(0).to(1).expect(Sets.newHashSet(new InterpreterCompletion("ORDER", "ORDER", keyword.name()), new InterpreterCompletion("SUBCLASS_ORIGIN", "SUBCLASS_ORIGIN", keyword.name()), new InterpreterCompletion("SUBSTRING", "SUBSTRING", keyword.name()), new InterpreterCompletion("prod_emart", "prod_emart", schema.name()), new InterpreterCompletion("LIMIT", "LIMIT", keyword.name()), new InterpreterCompletion("SUM", "SUM", keyword.name()), new InterpreterCompletion("prod_dds", "prod_dds", schema.name()), new InterpreterCompletion("SELECT", "SELECT", keyword.name()), new InterpreterCompletion("FROM", "FROM", keyword.name()))).test();
    }

    @Test
    public void testMultipleWords() {
        String buffer = "SELE FRO LIM";
        tester.buffer(buffer).from(2).to(4).expect(Sets.newHashSet(new InterpreterCompletion("SELECT", "SELECT", keyword.name()))).test();
        tester.buffer(buffer).from(6).to(8).expect(Sets.newHashSet(new InterpreterCompletion("FROM", "FROM", keyword.name()))).test();
        tester.buffer(buffer).from(10).to(12).expect(Sets.newHashSet(new InterpreterCompletion("LIMIT", "LIMIT", keyword.name()))).test();
    }

    @Test
    public void testMultiLineBuffer() {
        String buffer = " \n SELE\nFRO";
        tester.buffer(buffer).from(5).to(7).expect(Sets.newHashSet(new InterpreterCompletion("SELECT", "SELECT", keyword.name()))).test();
        tester.buffer(buffer).from(9).to(11).expect(Sets.newHashSet(new InterpreterCompletion("FROM", "FROM", keyword.name()))).test();
    }

    @Test
    public void testMultipleCompletionSuggestions() {
        String buffer = "SU";
        tester.buffer(buffer).from(2).to(2).expect(Sets.newHashSet(new InterpreterCompletion("SUBCLASS_ORIGIN", "SUBCLASS_ORIGIN", keyword.name()), new InterpreterCompletion("SUM", "SUM", keyword.name()), new InterpreterCompletion("SUBSTRING", "SUBSTRING", keyword.name()))).test();
    }

    @Test
    public void testSqlDelimiterCharacters() {
        Assert.assertTrue(sqlCompleter.getSqlDelimiter().isDelimiterChar("r,", 1));
        Assert.assertTrue(sqlCompleter.getSqlDelimiter().isDelimiterChar("SS,", 2));
        Assert.assertTrue(sqlCompleter.getSqlDelimiter().isDelimiterChar(",", 0));
        Assert.assertTrue(sqlCompleter.getSqlDelimiter().isDelimiterChar("ttt,", 3));
    }
}

