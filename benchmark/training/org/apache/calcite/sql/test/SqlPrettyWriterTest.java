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
package org.apache.calcite.sql.test;


import SqlWriter.SubQueryStyle.BLACK;
import org.apache.calcite.sql.dialect.AnsiSqlDialect;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.junit.Test;


/**
 * Unit test for {@link SqlPrettyWriter}.
 *
 * <p>You must provide the system property "source.dir".
 */
public class SqlPrettyWriterTest {
    // ~ Static fields/initializers ---------------------------------------------
    public static final String NL = System.getProperty("line.separator");

    // ~ Constructors -----------------------------------------------------------
    public SqlPrettyWriterTest() {
    }

    @Test
    public void testDefault() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testIndent8() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setIndentation(8);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testClausesNotOnNewLine() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setClauseStartsLine(false);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testSelectListItemsOnSeparateLines() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setSelectListItemsOnSeparateLines(true);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testSelectListExtraIndentFlag() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setSelectListItemsOnSeparateLines(true);
        prettyWriter.setSelectListExtraIndentFlag(false);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testKeywordsLowerCase() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setKeywordsLowerCase(true);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testParenthesizeAllExprs() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setAlwaysUseParentheses(true);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testOnlyQuoteIdentifiersWhichNeedIt() throws Exception {
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setQuoteAllIdentifiers(false);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testDamiansSubQueryStyle() throws Exception {
        // Note that ( is at the indent, SELECT is on the same line, and ) is
        // below it.
        final SqlPrettyWriter prettyWriter = new SqlPrettyWriter(AnsiSqlDialect.DEFAULT);
        prettyWriter.setSubQueryStyle(BLACK);
        checkSimple(prettyWriter, "${desc}", "${formatted}");
    }

    @Test
    public void testCase() {
        // Note that CASE is rewritten to the searched form. Wish it weren't
        // so, but that's beyond the control of the pretty-printer.
        assertExprPrintsTo(true, "case 1 when 2 + 3 then 4 when case a when b then c else d end then 6 else 7 end", (((((((((((((((((((("CASE" + (SqlPrettyWriterTest.NL)) + "WHEN 1 = 2 + 3") + (SqlPrettyWriterTest.NL)) + "THEN 4") + (SqlPrettyWriterTest.NL)) + "WHEN 1 = CASE") + (SqlPrettyWriterTest.NL)) + "        WHEN `A` = `B`") + (SqlPrettyWriterTest.NL))// todo: indent should be 4 not 8
         + "        THEN `C`") + (SqlPrettyWriterTest.NL)) + "        ELSE `D`") + (SqlPrettyWriterTest.NL)) + "        END") + (SqlPrettyWriterTest.NL)) + "THEN 6") + (SqlPrettyWriterTest.NL)) + "ELSE 7") + (SqlPrettyWriterTest.NL)) + "END"));
    }

    @Test
    public void testCase2() {
        assertExprPrintsTo(false, "case 1 when 2 + 3 then 4 when case a when b then c else d end then 6 else 7 end", "CASE WHEN 1 = 2 + 3 THEN 4 WHEN 1 = CASE WHEN `A` = `B` THEN `C` ELSE `D` END THEN 6 ELSE 7 END");
    }

    @Test
    public void testBetween() {
        assertExprPrintsTo(true, "x not between symmetric y and z", "`X` NOT BETWEEN SYMMETRIC `Y` AND `Z`");// todo: remove leading

        // space
    }

    @Test
    public void testCast() {
        assertExprPrintsTo(true, "cast(x + y as decimal(5, 10))", "CAST(`X` + `Y` AS DECIMAL(5, 10))");
    }

    @Test
    public void testLiteralChain() {
        assertExprPrintsTo(true, (("'x' /* comment */ 'y'" + (SqlPrettyWriterTest.NL)) + "  'z' "), (((("'x'" + (SqlPrettyWriterTest.NL)) + "'y'") + (SqlPrettyWriterTest.NL)) + "'z'"));
    }

    @Test
    public void testOverlaps() {
        assertExprPrintsTo(true, "(x,xx) overlaps (y,yy) or x is not null", "PERIOD (`X`, `XX`) OVERLAPS PERIOD (`Y`, `YY`) OR `X` IS NOT NULL");
    }

    @Test
    public void testUnion() {
        // todo: SELECT should not be indented from UNION, like this:
        // UNION
        // SELECT *
        // FROM `W`
        assertPrintsTo(true, ("select * from t " + (((("union select * from (" + "  select * from u ") + "  union select * from v) ") + "union select * from w ") + "order by a, b")), "${formatted}");
    }

    @Test
    public void testMultiset() {
        assertPrintsTo(false, "values (multiset (select * from t))", "${formatted}");
    }

    @Test
    public void testInnerJoin() {
        assertPrintsTo(true, "select * from x inner join y on x.k=y.k", "${formatted}");
    }

    @Test
    public void testWhereListItemsOnSeparateLinesOr() throws Exception {
        checkPrettySeparateLines(("select x" + (((" from y" + " where h is not null and i < j") + " or ((a or b) is true) and d not in (f,g)") + " or x <> z")));
    }

    @Test
    public void testWhereListItemsOnSeparateLinesAnd() throws Exception {
        checkPrettySeparateLines(("select x" + (((" from y" + " where h is not null and (i < j") + " or ((a or b) is true)) and (d not in (f,g)") + " or v <> ((w * x) + y) * z)")));
    }
}

/**
 * End SqlPrettyWriterTest.java
 */
