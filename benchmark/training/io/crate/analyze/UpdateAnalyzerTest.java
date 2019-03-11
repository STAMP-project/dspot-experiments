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


import ArrayType.ID;
import DataTypes.LONG;
import EqOperator.NAME;
import Row.EMPTY;
import VersionInvalidException.ERROR_MSG;
import io.crate.data.Row1;
import io.crate.data.RowN;
import io.crate.exceptions.ColumnValidationException;
import io.crate.exceptions.OperationOnInaccessibleRelationException;
import io.crate.exceptions.RelationUnknown;
import io.crate.exceptions.VersionInvalidException;
import io.crate.expression.symbol.Assignments;
import io.crate.expression.symbol.DynamicReference;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.ParameterSymbol;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.Schemas;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DoubleType;
import io.crate.types.ObjectType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UpdateAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testUpdateAnalysis() throws Exception {
        AnalyzedStatement analyzedStatement = analyze("update users set name='Ford Prefect'");
        assertThat(analyzedStatement, Matchers.instanceOf(AnalyzedUpdateStatement.class));
    }

    @Test(expected = RelationUnknown.class)
    public void testUpdateUnknownTable() throws Exception {
        analyze("update unknown set name='Prosser'");
    }

    @Test
    public void testUpdateSetColumnToColumnValue() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set name=name");
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(1));
        Symbol value = update.assignmentByTargetCol().entrySet().iterator().next().getValue();
        assertThat(value, SymbolMatchers.isReference("name"));
    }

    @Test
    public void testUpdateSetExpression() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=other_id+1");
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(1));
        Symbol value = update.assignmentByTargetCol().entrySet().iterator().next().getValue();
        assertThat(value, SymbolMatchers.isFunction("add"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateSameReferenceRepeated() throws Exception {
        analyze("update users set name='Trillian', name='Ford'");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateSameNestedReferenceRepeated() throws Exception {
        analyze("update users set details['arms']=3, details['arms']=5");
    }

    @Test
    public void testUpdateSysTables() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.nodes\" doesn\'t support or allow UPDATE " + "operations, as it is read-only."));
        analyze("update sys.nodes set fs=?", new Object[]{ new HashMap<String, Object>() {
            {
                put("free", 0);
            }
        } });
    }

    @Test
    public void testNumericTypeOutOfRange() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for shorts: Cannot cast -100000 to type short");
        analyze("update users set shorts=-100000");
    }

    @Test
    public void testNumericOutOfRangeFromFunction() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for bytes: Cannot cast 1234 to type byte");
        analyze("update users set bytes=abs(-1234)");
    }

    @Test
    public void testUpdateAssignments() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set name='Trillian'");
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(1));
        assertThat(tableInfo().ident(), Matchers.is(new RelationName(Schemas.DOC_SCHEMA_NAME, "users")));
        Reference ref = update.assignmentByTargetCol().keySet().iterator().next();
        assertThat(ref.ident().tableIdent().name(), Matchers.is("users"));
        assertThat(ref.column().name(), Matchers.is("name"));
        assertTrue(update.assignmentByTargetCol().containsKey(ref));
        Symbol value = update.assignmentByTargetCol().entrySet().iterator().next().getValue();
        assertThat(value, SymbolMatchers.isLiteral("Trillian"));
    }

    @Test
    public void testUpdateAssignmentNestedDynamicColumn() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set details['arms']=3");
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(1));
        Reference ref = update.assignmentByTargetCol().keySet().iterator().next();
        assertThat(ref, Matchers.instanceOf(DynamicReference.class));
        Assert.assertEquals(LONG, ref.valueType());
        assertThat(ref.column().isTopLevel(), Matchers.is(false));
        assertThat(ref.column().fqn(), Matchers.is("details.arms"));
    }

    @Test
    public void testUpdateAssignmentWrongType() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        analyze("update users set other_id='String'");
    }

    @Test
    public void testUpdateAssignmentConvertableType() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=9.9");
        Reference ref = update.assignmentByTargetCol().keySet().iterator().next();
        assertThat(ref, Matchers.not(Matchers.instanceOf(DynamicReference.class)));
        assertEquals(LONG, ref.valueType());
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        Symbol[] sources = assignments.bindSources(((DocTableInfo) (tableInfo())), EMPTY, SubQueryResults.EMPTY);
        assertThat(sources[0], SymbolMatchers.isLiteral(9L));
    }

    @Test
    public void testUpdateMuchAssignments() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=9.9, name='Trillian', details=?, stuff=true, foo='bar'", new Object[]{ new HashMap<String, Object>() });
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(5));
    }

    @Test
    public void testNoWhereClause() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=9");
        assertThat(update.query(), SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNoMatchWhereClause() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=9 where true=false");
        assertThat(update.query(), SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testUpdateWhereClause() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set other_id=9 where name='Trillian'");
        assertThat(update.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("name"), SymbolMatchers.isLiteral("Trillian")));
    }

    @Test
    public void testQualifiedNameReference() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Column reference \"users.name\" has too many parts. A column must not have a schema or a table here.");
        analyze("update users set users.name='Trillian'");
    }

    @Test
    public void testUpdateWithParameter() throws Exception {
        Map[] friends = new Map[]{ new HashMap<String, Object>() {
            {
                put("name", "Slartibartfast");
            }
        }, new HashMap<String, Object>() {
            {
                put("name", "Marvin");
            }
        } };
        AnalyzedUpdateStatement update = analyze("update users set name=?, other_id=?, friends=? where id=?", new Object[]{ "Jeltz", 0, friends, "9" });
        RelationName usersRelation = new RelationName("doc", "users");
        assertThat(update.assignmentByTargetCol().size(), Matchers.is(3));
        DocTableInfo tableInfo = e.schemas().getTableInfo(usersRelation);
        Reference name = tableInfo.getReference(new ColumnIdent("name"));
        Reference friendsRef = tableInfo.getReference(new ColumnIdent("friends"));
        Reference otherId = tableInfo.getReference(new ColumnIdent("other_id"));
        assertThat(update.assignmentByTargetCol().get(name), Matchers.instanceOf(ParameterSymbol.class));
        assertThat(update.assignmentByTargetCol().get(friendsRef), Matchers.instanceOf(ParameterSymbol.class));
        assertThat(update.assignmentByTargetCol().get(otherId), Matchers.instanceOf(ParameterSymbol.class));
        assertThat(update.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("id"), Matchers.instanceOf(ParameterSymbol.class)));
    }

    @Test
    public void testUpdateWithWrongParameters() throws Exception {
        Object[] params = new Object[]{ new HashMap<String, Object>(), new Map[0], new Long[]{ 1L, 2L, 3L } };
        AnalyzedUpdateStatement update = analyze("update users set name=?, friends=? where other_id=?");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast {} to type string");
        assignments.bindSources(((DocTableInfo) (tableInfo())), new RowN(params), SubQueryResults.EMPTY);
    }

    @Test
    public void testUpdateWithEmptyObjectArray() throws Exception {
        Object[] params = new Object[]{ new Map[0], 0 };
        AnalyzedUpdateStatement update = analyze("update users set friends=? where other_id=0");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        Symbol[] sources = assignments.bindSources(((DocTableInfo) (tableInfo())), new RowN(params), SubQueryResults.EMPTY);
        assertThat(sources[0].valueType().id(), Matchers.is(ID));
        assertThat(innerType().id(), Matchers.is(ObjectType.ID));
        assertThat(((Object[]) (value())).length, Matchers.is(0));
    }

    @Test
    public void testUpdateSystemColumn() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for _id: Updating a system column is not supported");
        analyze("update users set _id=1");
    }

    @Test
    public void testUpdatePrimaryKey() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        analyze("update users set id=1");
    }

    @Test
    public void testUpdateClusteredBy() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for id: Updating a clustered-by column is not supported");
        analyze("update users_clustered_by_only set id=1");
    }

    @Test(expected = ColumnValidationException.class)
    public void testUpdatePartitionedByColumn() throws Exception {
        analyze("update parted set date = 1395874800000");
    }

    @Test
    public void testUpdatePrimaryKeyIfNestedDoesNotWork() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        analyze("update t_nested_pk set o = {y=10}");
    }

    @Test
    public void testUpdateColumnReferencedInGeneratedPartitionByColumn() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Updating a column which is referenced in a partitioned by generated column expression is not supported");
        analyze("update parted_generated_column set ts = 1449999900000");
    }

    @Test
    public void testUpdateColumnReferencedInGeneratedPartitionByColumnNestedParent() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Updating a column which is referenced in a partitioned by generated column expression is not supported");
        analyze("update nested_parted_generated_column set \"user\" = {name = \'Ford\'}");
    }

    @Test
    public void testUpdateTableAlias() throws Exception {
        AnalyzedUpdateStatement expected = analyze("update users set awesome=true where awesome=false");
        AnalyzedUpdateStatement actual = analyze("update users as u set awesome=true where awesome=false");
        assertThat(expected.assignmentByTargetCol(), Matchers.is(actual.assignmentByTargetCol()));
        assertThat(expected.query(), Matchers.is(actual.query()));
    }

    @Test
    public void testUpdateObjectArrayField() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        analyze("update users set friends['id'] = ?");
    }

    @Test
    public void testUpdateArrayByElement() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Updating a single element of an array is not supported");
        analyze("update users set friends[1] = 2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhereClauseObjectArrayField() throws Exception {
        analyze("update users set awesome=true where friends['id'] = 5");
    }

    @Test
    public void testUpdateWithFQName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Column reference \"users.name\" has too many parts. A column must not have a schema or a table here.");
        analyze("update users set users.name = 'Ford Mustang'");
    }

    @Test
    public void testUpdateDynamicNestedArrayParamLiteral() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set new=[[1.9, 4.8], [9.7, 12.7]]");
        DataType dataType = update.assignmentByTargetCol().values().iterator().next().valueType();
        assertThat(dataType, Matchers.is(new ArrayType(new ArrayType(DoubleType.INSTANCE))));
    }

    @Test
    public void testUpdateDynamicNestedArrayParam() throws Exception {
        Object[] params = new Object[]{ new Object[]{ new Object[]{ 1.9, 4.8 }, new Object[]{ 9.7, 12.7 } } };
        AnalyzedUpdateStatement update = analyze("update users set new=? where id=1");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        Symbol[] sources = assignments.bindSources(((DocTableInfo) (tableInfo())), new RowN(params), SubQueryResults.EMPTY);
        DataType dataType = sources[0].valueType();
        assertThat(dataType, Matchers.is(new ArrayType(new ArrayType(DoubleType.INSTANCE))));
    }

    @Test
    public void testUpdateInvalidType() throws Exception {
        Object[] params = new Object[]{ new Object[]{ new Object[]{ "a", "b" } } };
        AnalyzedUpdateStatement update = analyze("update users set tags=? where id=1");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast [a, b] to type string");
        Assignments assignments = Assignments.convert(update.assignmentByTargetCol());
        assignments.bindSources(((DocTableInfo) (tableInfo())), new RowN(params), SubQueryResults.EMPTY);
    }

    @Test
    public void testUsingFQColumnNameShouldBePossibleInWhereClause() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users set name = 'foo' where users.name != 'foo'");
        assertThat(update.query(), SymbolMatchers.isFunction(NotPredicate.NAME, SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("name"), SymbolMatchers.isLiteral("foo"))));
    }

    @Test
    public void testTestUpdateOnTableWithAliasAndFQColumnNameInWhereClause() throws Exception {
        AnalyzedUpdateStatement update = analyze("update users  t set name = 'foo' where t.name != 'foo'");
        assertThat(update.query(), SymbolMatchers.isFunction(NotPredicate.NAME, SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("name"), SymbolMatchers.isLiteral("foo"))));
    }

    @Test
    public void testUpdateNestedClusteredByColumn() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for obj: Updating a clustered-by column is not supported");
        analyze("update nestedclustered set obj = {name='foobar'}");
    }

    @Test
    public void testUpdateNestedClusteredByColumnWithOtherObject() throws Exception {
        expectedException.expect(ColumnValidationException.class);
        expectedException.expectMessage("Validation failed for obj: Updating a clustered-by column is not supported");
        analyze("update nestedclustered set obj = other_obj");
    }

    @Test
    public void testUpdateWhereVersionUsingWrongOperator() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set text = ? where text = ? and \"_version\" >= ?"), new RowN(new Object[]{ "already in panic", "don't panic", 3 }));
    }

    @Test
    public void testUpdateWhereVersionIsColumn() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set col2 = ? where _version = id"), new Row1(1));
    }

    @Test
    public void testUpdateWhereVersionInOperatorColumn() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set col2 = 'x' where _version in (1,2,3)"), EMPTY);
    }

    @Test
    public void testUpdateWhereVersionOrOperatorColumn() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set col2 = ? where _version = 1 or _version = 2"), new Row1(1));
    }

    @Test
    public void testUpdateWhereVersionAddition() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set col2 = ? where _version + 1 = 2"), new Row1(1));
    }

    @Test
    public void testUpdateWhereVersionNotPredicate() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set text = ? where not (_version = 1 and id = 1)"), new Row1(1));
    }

    @Test
    public void testUpdateWhereVersionOrOperator() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set awesome = true where _version = 1 or _version = 2"), EMPTY);
    }

    @Test
    public void testUpdateWithVersionZero() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set awesome=true where name='Ford' and _version=0"), EMPTY);
    }

    @Test
    public void testSelectWhereVersionIsNullPredicate() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        execute(e.plan("update users set col2 = 'x' where _version is null"), EMPTY);
    }

    @Test
    public void testUpdateElementOfObjectArrayUsingParameterExpressionResultsInCorrectlyTypedParameterSymbol() {
        AnalyzedUpdateStatement stmt = e.analyze("UPDATE bag SET ob = [?] WHERE id = ?");
        assertThat(stmt.assignmentByTargetCol().keySet(), Matchers.contains(SymbolMatchers.isReference("ob", new ArrayType(ObjectType.untyped()))));
        assertThat(stmt.assignmentByTargetCol().values(), Matchers.contains(SymbolMatchers.isFunction("_array", Collections.singletonList(ObjectType.untyped()))));
    }

    @Test
    public void testUpdateElementOfObjectArrayUsingParameterExpressionInsideFunctionResultsInCorrectlyTypedParameterSymbol() {
        AnalyzedUpdateStatement stmt = e.analyze("UPDATE bag SET ob = array_cat([?], [{obb=1}]) WHERE id = ?");
        assertThat(stmt.assignmentByTargetCol().keySet(), Matchers.contains(SymbolMatchers.isReference("ob", new ArrayType(ObjectType.untyped()))));
        assertThat(stmt.assignmentByTargetCol().values(), Matchers.contains(SymbolMatchers.isFunction("array_cat", SymbolMatchers.isFunction("_array", Collections.singletonList(ObjectType.untyped())), Matchers.instanceOf(Literal.class))));
    }
}

