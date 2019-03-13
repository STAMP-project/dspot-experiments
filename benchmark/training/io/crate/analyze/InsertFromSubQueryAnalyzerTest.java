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


import DataTypes.INTEGER;
import StringType.INSTANCE;
import SubstrFunction.NAME;
import com.google.common.collect.ImmutableMap;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.exceptions.ColumnValidationException;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.Routing;
import io.crate.metadata.Schemas;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.metadata.table.TestingTableInfo;
import io.crate.sql.parser.ParsingException;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.types.StringType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class InsertFromSubQueryAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    private static final RelationName THREE_PK_TABLE_IDENT = new RelationName(Schemas.DOC_SCHEMA_NAME, "three_pk");

    private static final DocTableInfo THREE_PK_TABLE_INFO = new TestingTableInfo.Builder(InsertFromSubQueryAnalyzerTest.THREE_PK_TABLE_IDENT, new Routing(ImmutableMap.of())).add("a", INTEGER).add("b", INTEGER).add("c", INTEGER).add("d", INTEGER).addPrimaryKey("a").addPrimaryKey("b").addPrimaryKey("c").build();

    @Test
    public void testFromQueryWithoutColumns() throws Exception {
        InsertFromSubQueryAnalyzedStatement analysis = e.analyze("insert into users (select * from users where name = 'Trillian')");
        assertCompatibleColumns(analysis);
    }

    @Test
    public void testFromQueryWithSubQueryColumns() throws Exception {
        InsertFromSubQueryAnalyzedStatement analysis = e.analyze(("insert into users (id, name) (" + "  select id, name from users where name = 'Trillian' )"));
        assertCompatibleColumns(analysis);
    }

    @Test
    public void testFromQueryWithMissingSubQueryColumn() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        e.analyze(("insert into users (" + (((("  select id, other_id, name, details, awesome, counters, " + "       friends ") + "  from users ") + "  where name = 'Trillian'") + ")")));
    }

    @Test
    public void testFromQueryWithMissingInsertColumn() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        e.analyze(("insert into users (id, other_id, name, details, awesome, counters, friends) (" + (("  select * from users " + "  where name = 'Trillian'") + ")")));
    }

    @Test
    public void testFromQueryWithInsertColumns() throws Exception {
        InsertFromSubQueryAnalyzedStatement analysis = e.analyze(("insert into users (id, name, details) (" + (("  select id, name, details from users " + "  where name = 'Trillian'") + ")")));
        assertCompatibleColumns(analysis);
    }

    @Test
    public void testFromQueryWithWrongColumnTypes() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        e.analyze(("insert into users (id, details, name) (" + (("  select id, name, details from users " + "  where name = 'Trillian'") + ")")));
    }

    @Test
    public void testFromQueryWithConvertableInsertColumns() throws Exception {
        InsertFromSubQueryAnalyzedStatement analysis = e.analyze(("insert into users (id, name) (" + (("  select id, other_id from users " + "  where name = 'Trillian'") + ")")));
        assertCompatibleColumns(analysis);
    }

    @Test
    public void testFromQueryWithFunctionSubQuery() throws Exception {
        InsertFromSubQueryAnalyzedStatement analysis = e.analyze(("insert into users (id) (" + (("  select count(*) from users " + "  where name = 'Trillian'") + ")")));
        assertCompatibleColumns(analysis);
    }

    @Test
    public void testFromQueryWithOnDuplicateKey() throws Exception {
        var insert = "insert into users (id, name) (select id, name from users) " + "on conflict (id) do update set name = 'Arthur'";
        InsertFromSubQueryAnalyzedStatement statement = e.analyze(insert);
        Assert.assertThat(statement.onDuplicateKeyAssignments().size(), Matchers.is(1));
        for (Map.Entry<Reference, Symbol> entry : statement.onDuplicateKeyAssignments().entrySet()) {
            assertThat(entry.getKey(), SymbolMatchers.isReference("name"));
            assertThat(entry.getValue(), SymbolMatchers.isLiteral("Arthur", INSTANCE));
        }
    }

    @Test
    public void testFromQueryWithOnDuplicateKeyParameter() throws Exception {
        var insert = "insert into users (id, name) (select id, name from users) " + "on conflict (id) do update set name = ?";
        InsertFromSubQueryAnalyzedStatement statement = e.analyze(insert, new Object[]{ "Arthur" });
        Assert.assertThat(statement.onDuplicateKeyAssignments().size(), Matchers.is(1));
        for (Map.Entry<Reference, Symbol> entry : statement.onDuplicateKeyAssignments().entrySet()) {
            assertThat(entry.getKey(), SymbolMatchers.isReference("name"));
            assertThat(entry.getValue(), SymbolMatchers.isLiteral("Arthur", INSTANCE));
        }
    }

    @Test
    public void testFromQueryWithOnDuplicateKeyValues() throws Exception {
        var insert = "insert into users (id, name) (select id, name from users) " + "on conflict (id) do update set name = substr(excluded.name, 1, 1)";
        InsertFromSubQueryAnalyzedStatement statement = e.analyze(insert);
        Assert.assertThat(statement.onDuplicateKeyAssignments().size(), Matchers.is(1));
        for (Map.Entry<Reference, Symbol> entry : statement.onDuplicateKeyAssignments().entrySet()) {
            assertThat(entry.getKey(), SymbolMatchers.isReference("name"));
            assertThat(entry.getValue(), SymbolMatchers.isFunction(NAME));
            Function function = ((Function) (entry.getValue()));
            assertThat(function.arguments().get(0), Matchers.instanceOf(InputColumn.class));
            InputColumn inputColumn = ((InputColumn) (function.arguments().get(0)));
            assertThat(inputColumn.index(), Matchers.is(1));
            assertThat(inputColumn.valueType(), Matchers.instanceOf(StringType.class));
        }
    }

    @Test
    public void testFromQueryWithOnConflictAndMultiplePKs() {
        String insertStatement = "insert into three_pk (a, b, c) (select 1, 2, 3) on conflict (a, b, c) do update set d = 1";
        InsertFromSubQueryAnalyzedStatement statement = e.analyze(insertStatement);
        assertThat(statement.onDuplicateKeyAssignments().size(), Is.is(1));
        assertThat(statement.onDuplicateKeyAssignments().keySet().iterator().next(), SymbolMatchers.isReference("d"));
        assertThat(statement.onDuplicateKeyAssignments().values().iterator().next(), SymbolMatchers.isLiteral(1));
    }

    @Test
    public void testFromQueryWithUnknownOnDuplicateKeyValues() throws Exception {
        try {
            e.analyze(("insert into users (id, name) (select id, name from users) " + "on conflict (id) do update set name = excluded.does_not_exist"));
            fail("Analyze passed without a failure.");
        } catch (ColumnUnknownException e) {
            assertThat(e.getMessage(), Matchers.containsString("Column does_not_exist unknown"));
        }
    }

    @Test
    public void testFromQueryWithOnDuplicateKeyPrimaryKeyUpdate() {
        try {
            e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict (id) do update set id = id + 1");
            fail("Analyze passed without a failure.");
        } catch (ColumnValidationException e) {
            assertThat(e.getMessage(), Matchers.containsString("Updating a primary key is not supported"));
        }
    }

    @Test
    public void testUpdateOnConflictDoNothingProducesEmptyUpdateAssignments() {
        InsertFromSubQueryAnalyzedStatement statement = e.analyze("insert into users (id, name) (select 1, 'Jon') on conflict DO NOTHING");
        Map<Reference, Symbol> duplicateKeyAssignments = statement.onDuplicateKeyAssignments();
        assertThat(statement.isIgnoreDuplicateKeys(), Matchers.is(true));
        assertThat(duplicateKeyAssignments, Matchers.is(Matchers.notNullValue()));
        assertThat(duplicateKeyAssignments.size(), Matchers.is(0));
    }

    @Test
    public void testMissingPrimaryKey() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Column \"id\" is required but is missing from the insert statement");
        e.analyze("insert into users (name) (select name from users)");
    }

    @Test
    public void testTargetColumnsMustMatchSourceColumnsEvenWithGeneratedColumns() throws Exception {
        /**
         * We want the copy case (insert into target (select * from source)) to work so there is no special logic
         * to exclude generated columns from the target
         */
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Number of target columns (id, firstname, lastname, name) " + "of insert statement doesn't match number of source columns (id, firstname, lastname)"));
        e.analyze("insert into users_generated (select id, firstname, lastname from users_generated)");
    }

    @Test
    public void testFromQueryWithInvalidConflictTarget() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Conflict target ([id2]) did not match the primary key columns ([id])");
        e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict (id2) do update set name = excluded.name");
    }

    @Test
    public void testFromQueryWithConflictTargetNotMatchingPK() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number of conflict targets ([id, id2]) did not match the number of primary key columns ([id])");
        e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict (id, id2) do update set name = excluded.name");
    }

    @Test
    public void testInsertFromValuesWithConflictTargetNotMatchingMultiplePKs() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number of conflict targets ([a, b]) did not match the number of primary key columns ([a, b, c])");
        e.analyze(("insert into three_pk (a, b, c) (select 1, 2, 3) " + "on conflict (a, b) do update set d = 1"));
    }

    @Test
    public void testFromQueryWithMissingConflictTarget() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage("line 1:66: mismatched input 'update' expecting 'NOTHING'");
        e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict do update set name = excluded.name");
    }

    @Test
    public void testFromQueryWithInvalidConflictTargetDoNothing() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Conflict target ([id2]) did not match the primary key columns ([id])");
        e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict (id2) DO NOTHING");
    }

    @Test
    public void testFromQueryWithConflictTargetDoNothingNotMatchingPK() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number of conflict targets ([id, id2]) did not match the number of primary key columns ([id])");
        e.analyze("insert into users (id, name) (select 1, 'Arthur') on conflict (id, id2) DO NOTHING");
    }

    @Test
    public void testInsertFromValuesWithConflictTargetDoNothingNotMatchingMultiplePKs() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number of conflict targets ([a, b]) did not match the number of primary key columns ([a, b, c])");
        e.analyze(("insert into three_pk (a, b, c) (select 1, 2, 3) " + "on conflict (a, b) DO NOTHING"));
    }
}

