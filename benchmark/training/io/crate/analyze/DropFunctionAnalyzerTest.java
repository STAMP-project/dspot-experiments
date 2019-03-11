/**
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate.io licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * To enable or use any of the enterprise features, Crate.io must have given
 * you permission to enable and use the Enterprise Edition of CrateDB and you
 * must have a valid Enterprise or Subscription Agreement with Crate.io.  If
 * you enable or use features that are part of the Enterprise Edition, you
 * represent and warrant that you have a valid Enterprise or Subscription
 * Agreement with Crate.io.  Your use of features of the Enterprise Edition
 * is governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.analyze;


import DataTypes.LONG;
import ObjectType.ID;
import io.crate.action.sql.Option;
import io.crate.auth.user.User;
import io.crate.data.Row;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.sql.parser.SqlParser;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DropFunctionAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testDropFunctionSimple() throws Exception {
        AnalyzedStatement analyzedStatement = e.analyze("DROP FUNCTION bar(long, object)");
        assertThat(analyzedStatement, Matchers.instanceOf(DropFunctionAnalyzedStatement.class));
        DropFunctionAnalyzedStatement analysis = ((DropFunctionAnalyzedStatement) (analyzedStatement));
        assertThat(analysis.schema(), Matchers.is("doc"));
        assertThat(analysis.name(), Matchers.is("bar"));
        assertThat(analysis.ifExists(), Matchers.is(false));
        assertThat(analysis.argumentTypes().get(0), Matchers.is(LONG));
        assertThat(analysis.argumentTypes().get(1).id(), Matchers.is(ID));
    }

    @Test
    public void testDropFunctionWithSessionSetSchema() throws Exception {
        DropFunctionAnalyzedStatement analysis = ((DropFunctionAnalyzedStatement) (e.analyzer.boundAnalyze(SqlParser.createStatement("DROP FUNCTION bar(long, object)"), new CoordinatorTxnCtx(new io.crate.action.sql.SessionContext(0, Option.NONE, User.CRATE_USER, ( s) -> {
        }, ( t) -> {
        }, "my_schema")), new ParameterContext(Row.EMPTY, Collections.emptyList())).analyzedStatement()));
        assertThat(analysis.schema(), Matchers.is("my_schema"));
        assertThat(analysis.name(), Matchers.is("bar"));
    }

    @Test
    public void testDropFunctionExplicitSchemaSupersedesSessionSchema() throws Exception {
        DropFunctionAnalyzedStatement analysis = ((DropFunctionAnalyzedStatement) (e.analyzer.boundAnalyze(SqlParser.createStatement("DROP FUNCTION my_other_schema.bar(long, object)"), new CoordinatorTxnCtx(new io.crate.action.sql.SessionContext(0, Option.NONE, User.CRATE_USER, ( s) -> {
        }, ( t) -> {
        }, "my_schema")), new ParameterContext(Row.EMPTY, Collections.emptyList())).analyzedStatement()));
        assertThat(analysis.schema(), Matchers.is("my_other_schema"));
        assertThat(analysis.name(), Matchers.is("bar"));
    }

    @Test
    public void testDropFunctionIfExists() throws Exception {
        AnalyzedStatement analyzedStatement = e.analyze("DROP FUNCTION IF EXISTS bar(arg_long long, arg_obj object)");
        assertThat(analyzedStatement, Matchers.instanceOf(DropFunctionAnalyzedStatement.class));
        DropFunctionAnalyzedStatement analysis = ((DropFunctionAnalyzedStatement) (analyzedStatement));
        assertThat(analysis.name(), Matchers.is("bar"));
        assertThat(analysis.ifExists(), Matchers.is(true));
        assertThat(analysis.argumentTypes().get(0), Matchers.is(LONG));
        assertThat(analysis.argumentTypes().get(1).id(), Matchers.is(ID));
    }
}

