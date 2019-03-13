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
package org.apache.calcite.test;


import SqlAbstractParserImpl.Metadata;
import org.apache.calcite.sql.parser.SqlAbstractParserImpl;
import org.apache.calcite.sql.parser.SqlParserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the "Babel" SQL parser, that understands all dialects of SQL.
 */
public class BabelParserTest extends SqlParserTest {
    @Test
    public void testReservedWords() {
        Assert.assertThat(isReserved("escape"), CoreMatchers.is(false));
    }

    /**
     * {@inheritDoc }
     *
     * <p>Copy-pasted from base method, but with some key differences.
     */
    @Override
    @Test
    public void testMetadata() {
        SqlAbstractParserImpl.Metadata metadata = getSqlParser("").getMetadata();
        Assert.assertThat(metadata.isReservedFunctionName("ABS"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isReservedFunctionName("FOO"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isContextVariableName("CURRENT_USER"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isContextVariableName("CURRENT_CATALOG"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isContextVariableName("CURRENT_SCHEMA"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isContextVariableName("ABS"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isContextVariableName("FOO"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isNonReservedKeyword("A"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isNonReservedKeyword("KEY"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isNonReservedKeyword("SELECT"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isNonReservedKeyword("FOO"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isNonReservedKeyword("ABS"), CoreMatchers.is(true));// was false

        Assert.assertThat(metadata.isKeyword("ABS"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("CURRENT_USER"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("CURRENT_CATALOG"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("CURRENT_SCHEMA"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("KEY"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("SELECT"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("HAVING"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("A"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isKeyword("BAR"), CoreMatchers.is(false));
        Assert.assertThat(metadata.isReservedWord("SELECT"), CoreMatchers.is(true));
        Assert.assertThat(metadata.isReservedWord("CURRENT_CATALOG"), CoreMatchers.is(false));// was true

        Assert.assertThat(metadata.isReservedWord("CURRENT_SCHEMA"), CoreMatchers.is(false));// was true

        Assert.assertThat(metadata.isReservedWord("KEY"), CoreMatchers.is(false));
        String jdbcKeywords = metadata.getJdbcKeywords();
        Assert.assertThat(jdbcKeywords.contains(",COLLECT,"), CoreMatchers.is(false));// was true

        Assert.assertThat((!(jdbcKeywords.contains(",SELECT,"))), CoreMatchers.is(true));
    }

    @Test
    public void testSelect() {
        final String sql = "select 1 from t";
        final String expected = "SELECT 1\n" + "FROM `T`";
        sql(sql).ok(expected);
    }

    @Test
    public void testYearIsNotReserved() {
        final String sql = "select 1 as year from t";
        final String expected = "SELECT 1 AS `YEAR`\n" + "FROM `T`";
        sql(sql).ok(expected);
    }

    /**
     * In Babel, AS is not reserved.
     */
    @Test
    public void testAs() {
        final String expected = "SELECT `AS`\n" + "FROM `T`";
        sql("select as from t").ok(expected);
    }

    /**
     * In Babel, DESC is not reserved.
     */
    @Test
    public void testDesc() {
        final String sql = "select desc\n" + ("from t\n" + "order by desc asc, desc desc");
        final String expected = "SELECT `DESC`\n" + ("FROM `T`\n" + "ORDER BY `DESC`, `DESC` DESC");
        sql(sql).ok(expected);
    }
}

/**
 * End BabelParserTest.java
 */
