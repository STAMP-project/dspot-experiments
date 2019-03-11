/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
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
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import EqOperator.NAME;
import RowGranularity.DOC;
import io.crate.analyze.relations.DocTableRelation;
import io.crate.exceptions.OperationOnInaccessibleRelationException;
import io.crate.exceptions.RelationUnknown;
import io.crate.expression.symbol.ParameterSymbol;
import io.crate.metadata.table.TableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DeleteAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testDeleteWhere() throws Exception {
        AnalyzedDeleteStatement delete = e.analyze("delete from users where name='Trillian'");
        DocTableRelation tableRelation = delete.relation();
        TableInfo tableInfo = tableRelation.tableInfo();
        assertThat(TableDefinitions.USER_TABLE_IDENT, Matchers.equalTo(tableInfo.ident()));
        assertThat(tableInfo.rowGranularity(), Matchers.is(DOC));
        assertThat(delete.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("name"), SymbolMatchers.isLiteral("Trillian")));
    }

    @Test
    public void testDeleteSystemTable() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.nodes\" doesn\'t support or allow DELETE " + "operations, as it is read-only."));
        e.analyze("delete from sys.nodes where name='Trillian'");
    }

    @Test
    public void testDeleteWhereSysColumn() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'sys.nodes' unknown");
        e.analyze("delete from users where sys.nodes.id = 'node_1'");
    }

    @Test
    public void testDeleteWherePartitionedByColumn() throws Exception {
        AnalyzedDeleteStatement delete = e.analyze("delete from parted where date = 1395874800000");
        assertThat(delete.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("date"), SymbolMatchers.isLiteral(1395874800000L)));
    }

    @Test
    public void testDeleteTableAlias() throws Exception {
        AnalyzedDeleteStatement expectedStatement = e.analyze("delete from users where name='Trillian'");
        AnalyzedDeleteStatement actualStatement = e.analyze("delete from users as u where u.name='Trillian'");
        assertThat(actualStatement.relation().tableInfo(), Matchers.equalTo(expectedStatement.relation().tableInfo()));
        assertThat(actualStatement.query(), Matchers.equalTo(expectedStatement.query()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhereClauseObjectArrayField() throws Exception {
        e.analyze("delete from users where friends['id'] = 5");
    }

    @Test
    public void testBulkDelete() throws Exception {
        AnalyzedDeleteStatement delete = e.analyze("delete from users where id = ?", new Object[][]{ new Object[]{ 1 }, new Object[]{ 2 }, new Object[]{ 3 }, new Object[]{ 4 } });
        assertThat(delete.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("id"), Matchers.instanceOf(ParameterSymbol.class)));
    }

    @Test
    public void testSensibleErrorOnDeleteComplexRelation() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot delete from relations other than base tables");
        e.analyze("delete from (select * from users) u");
    }
}

