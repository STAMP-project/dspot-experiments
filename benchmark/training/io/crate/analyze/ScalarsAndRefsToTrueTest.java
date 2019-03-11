/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import DataTypes.BOOLEAN;
import io.crate.expression.symbol.Symbol;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class ScalarsAndRefsToTrueTest extends CrateUnitTest {
    private SqlExpressions expressions;

    @Test
    public void testFalseAndMatchFunction() throws Exception {
        Symbol symbol = convertFromSQL("false and match (table_name, 'jalla')");
        assertThat(symbol, SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testTrueAndMatchFunction() throws Exception {
        Symbol symbol = convertFromSQL("true and match (table_name, 'jalla')");
        assertThat(symbol, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testComplexNestedDifferentMethods() throws Exception {
        Symbol symbol = convertFromSQL(("number_of_shards = 1 or (number_of_replicas = 3 and schema_name = 'sys') " + "or not (number_of_shards = 2) and substr(table_name, 1, 1) = '1'"));
        assertThat(symbol, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testIsNull() throws Exception {
        Symbol symbol = convertFromSQL("clustered_by is null");
        assertThat(symbol, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNot_NullAndSubstr() throws Exception {
        Symbol symbol = convertFromSQL("not (null and substr(table_name, 1, 1) = '1')");
        assertThat(symbol, SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testNot_NullWithInput() throws Exception {
        /**
         * regression test
         */
        Symbol symbol = convertFromSQL("NOT 30 >= NULL");
        assertThat(symbol, SymbolMatchers.isLiteral(null));
        symbol = convertFromSQL("NOT NULL");
        assertThat(symbol, SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testNot_FalseAndSubstr() throws Exception {
        Symbol symbol = convertFromSQL("not (false and substr(table_name, 1, 1) = '1')");
        assertThat(symbol, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNotPredicate() throws Exception {
        Symbol symbol = convertFromSQL("not (clustered_by = \'foo\')");
        assertThat(symbol, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testComplexNestedDifferentMethodsEvaluatesToFalse() throws Exception {
        Symbol symbol = convertFromSQL(("(number_of_shards = 1 or number_of_replicas = 3 and schema_name = 'sys' " + "or not (number_of_shards = 2)) and substr(table_name, 1, 1) = '1' and false"));
        assertThat(symbol, SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testNullAndMatchFunction() throws Exception {
        Symbol symbol = convertFromSQL("null and match (table_name, 'jalla')");
        assertThat(symbol, SymbolMatchers.isLiteral(null, BOOLEAN));
    }
}

