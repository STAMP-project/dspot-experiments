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


import SqlValidatorUtil.EXPR_SUGGESTER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.test.SqlTestFactory;
import org.apache.calcite.sql.test.SqlTester;
import org.apache.calcite.sql.test.SqlValidatorTester;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SqlValidatorUtil}.
 */
public class SqlValidatorUtilTest {
    @Test
    public void testUniquifyCaseSensitive() {
        List<String> nameList = Lists.newArrayList("col1", "COL1", "col_ABC", "col_abC");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, true);
        Assert.assertThat(nameList, CoreMatchers.sameInstance(resultList));
    }

    @Test
    public void testUniquifyNotCaseSensitive() {
        List<String> nameList = Lists.newArrayList("col1", "COL1", "col_ABC", "col_abC");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, false);
        Assert.assertThat(resultList, CoreMatchers.not(nameList));
        SqlValidatorUtilTest.checkChangedFieldList(nameList, resultList, false);
    }

    @Test
    public void testUniquifyOrderingCaseSensitive() {
        List<String> nameList = Lists.newArrayList("k68s", "def", "col1", "COL1", "abc", "123");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, true);
        Assert.assertThat(nameList, CoreMatchers.sameInstance(resultList));
    }

    @Test
    public void testUniquifyOrderingRepeatedCaseSensitive() {
        List<String> nameList = Lists.newArrayList("k68s", "def", "col1", "COL1", "def", "123");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, true);
        Assert.assertThat(nameList, CoreMatchers.not(resultList));
        SqlValidatorUtilTest.checkChangedFieldList(nameList, resultList, true);
    }

    @Test
    public void testUniquifyOrderingNotCaseSensitive() {
        List<String> nameList = Lists.newArrayList("k68s", "def", "col1", "COL1", "abc", "123");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, false);
        Assert.assertThat(resultList, CoreMatchers.not(nameList));
        SqlValidatorUtilTest.checkChangedFieldList(nameList, resultList, false);
    }

    @Test
    public void testUniquifyOrderingRepeatedNotCaseSensitive() {
        List<String> nameList = Lists.newArrayList("k68s", "def", "col1", "COL1", "def", "123");
        List<String> resultList = SqlValidatorUtil.uniquify(nameList, EXPR_SUGGESTER, false);
        Assert.assertThat(resultList, CoreMatchers.not(nameList));
        SqlValidatorUtilTest.checkChangedFieldList(nameList, resultList, false);
    }

    @SuppressWarnings("resource")
    @Test
    public void testCheckingDuplicatesWithCompoundIdentifiers() {
        final List<SqlNode> newList = new ArrayList<>(2);
        newList.add(new org.apache.calcite.sql.SqlIdentifier(Arrays.asList("f0", "c0"), SqlParserPos.ZERO));
        newList.add(new org.apache.calcite.sql.SqlIdentifier(Arrays.asList("f0", "c0"), SqlParserPos.ZERO));
        final SqlTester tester = new SqlValidatorTester(SqlTestFactory.INSTANCE);
        final SqlValidatorImpl validator = ((SqlValidatorImpl) (tester.getValidator()));
        try {
            SqlValidatorUtil.checkIdentifierListForDuplicates(newList, validator.getValidationErrorFunction());
            Assert.fail("expected exception");
        } catch (CalciteContextException e) {
            // ok
        }
        // should not throw
        newList.set(1, new org.apache.calcite.sql.SqlIdentifier(Arrays.asList("f0", "c1"), SqlParserPos.ZERO));
        SqlValidatorUtil.checkIdentifierListForDuplicates(newList, null);
    }

    @Test
    public void testNameMatcher() {
        final ImmutableList<String> beatles = ImmutableList.of("john", "paul", "ringo", "rinGo");
        final SqlNameMatcher insensitiveMatcher = SqlNameMatchers.withCaseSensitive(false);
        Assert.assertThat(insensitiveMatcher.frequency(beatles, "ringo"), CoreMatchers.is(2));
        Assert.assertThat(insensitiveMatcher.frequency(beatles, "rinGo"), CoreMatchers.is(2));
        final SqlNameMatcher sensitiveMatcher = SqlNameMatchers.withCaseSensitive(true);
        Assert.assertThat(sensitiveMatcher.frequency(beatles, "ringo"), CoreMatchers.is(1));
        Assert.assertThat(sensitiveMatcher.frequency(beatles, "rinGo"), CoreMatchers.is(1));
        Assert.assertThat(sensitiveMatcher.frequency(beatles, "Ringo"), CoreMatchers.is(0));
    }
}

/**
 * End SqlValidatorUtilTest.java
 */
