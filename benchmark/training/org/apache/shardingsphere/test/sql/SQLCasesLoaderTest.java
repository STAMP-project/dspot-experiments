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
package org.apache.shardingsphere.test.sql;


import SQLCaseType.Literal;
import SQLCaseType.Placeholder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SQLCasesLoaderTest {
    @Test
    public void assertGetSupportedSQLForLiteralWithoutParameter() {
        Assert.assertThat(SQLCasesLoader.getInstance().getSupportedSQL("select_constant_without_table", Literal, Collections.emptyList()), CoreMatchers.is("SELECT 1 as a"));
    }

    @Test
    public void assertGetSupportedSQLForLiteralWithParameters() {
        Assert.assertThat(SQLCasesLoader.getInstance().getSupportedSQL("select_with_same_table_name_and_alias", Literal, Arrays.asList(10, 1000)), CoreMatchers.is("SELECT t_order.* FROM t_order t_order WHERE user_id = 10 AND order_id = 1000"));
    }

    @Test
    public void assertGetSupportedSQLForPlaceholder() {
        Assert.assertThat(SQLCasesLoader.getInstance().getSupportedSQL("select_with_same_table_name_and_alias", Placeholder, Arrays.asList(10, 1000)), CoreMatchers.is("SELECT t_order.* FROM t_order t_order WHERE user_id = ? AND order_id = ?"));
    }

    @Test(expected = IllegalStateException.class)
    public void assertGetSupportedSQLWithoutSQLCaseId() {
        SQLCasesLoader.getInstance().getSupportedSQL("no_sql_case_id", Literal, Collections.emptyList());
    }

    // @Test
    // public void assertGetUnsupportedSQLForLiteral() {
    // assertThat(SQLCasesLoader.getInstance().getUnsupportedSQL("assertSelectIntoSQL", SQLCaseType.Literal, Collections.emptyList()), is("SELECT * INTO t_order_new FROM t_order"));
    // }
    @Test(expected = IllegalStateException.class)
    public void assertGetUnsupportedSQLWithoutSQLCaseId() {
        SQLCasesLoader.getInstance().getUnsupportedSQL("no_sql_case_id", Literal, Collections.emptyList());
    }

    @Test
    public void assertGetSupportedSQLTestParameters() {
        Collection<Object[]> actual = SQLCasesLoader.getInstance().getSupportedSQLTestParameters(Collections.singletonList(DatabaseTypeEnum.H2), DatabaseTypeEnum.class);
        Assert.assertFalse(actual.isEmpty());
        Object[] actualRow = actual.iterator().next();
        Assert.assertThat(actualRow.length, CoreMatchers.is(3));
        Assert.assertThat(actualRow[0], CoreMatchers.instanceOf(String.class));
        Assert.assertThat(actualRow[1], CoreMatchers.instanceOf(DatabaseTypeEnum.class));
        Assert.assertThat(actualRow[2], CoreMatchers.instanceOf(SQLCaseType.class));
    }

    @Test
    public void assertGetUnsupportedSQLTestParameters() {
        Collection<Object[]> actual = SQLCasesLoader.getInstance().getUnsupportedSQLTestParameters(Collections.singletonList(DatabaseTypeEnum.H2), DatabaseTypeEnum.class);
        Assert.assertFalse(actual.isEmpty());
        Object[] actualRow = actual.iterator().next();
        Assert.assertThat(actualRow.length, CoreMatchers.is(3));
        Assert.assertThat(actualRow[0], CoreMatchers.instanceOf(String.class));
        Assert.assertThat(actualRow[1], CoreMatchers.instanceOf(DatabaseTypeEnum.class));
        Assert.assertThat(actualRow[2], CoreMatchers.instanceOf(SQLCaseType.class));
    }

    @Test
    public void assertCountAllSupportedSQLCases() {
        Assert.assertTrue(((SQLCasesLoader.getInstance().countAllSupportedSQLCases()) > 1));
    }
}

