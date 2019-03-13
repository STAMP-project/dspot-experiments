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
package io.crate.analyze.where;


import AnyLikeOperator.LIKE;
import AnyOperators.Names.EQ;
import DataTypes.INTEGER;
import DataTypes.STRING;
import EqOperator.NAME;
import VersionInvalidException.ERROR_MSG;
import com.google.common.collect.ImmutableList;
import io.crate.action.sql.SessionContext;
import io.crate.analyze.AnalyzedUpdateStatement;
import io.crate.analyze.WhereClause;
import io.crate.exceptions.VersionInvalidException;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.RelationName;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.TestingHelpers;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import org.hamcrest.Matchers;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class WhereClauseAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private final CoordinatorTxnCtx coordinatorTxnCtx = new CoordinatorTxnCtx(SessionContext.systemSessionContext());

    private SQLExecutor e;

    @Test
    public void testSelectWherePartitionedByColumn() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select id from parted where date = 1395874800000");
        assertThat(whereClause.hasQuery(), Matchers.is(false));
        assertThat(whereClause.noMatch(), Matchers.is(false));
        assertThat(whereClause.partitions(), Matchers.contains(asIndexName()));
    }

    @Test
    public void testUpdateWherePartitionedByColumn() throws Exception {
        AnalyzedUpdateStatement update = analyzeUpdate("update parted set id = 2 where date = 1395874800000");
        assertThat(update.query(), SymbolMatchers.isFunction(NAME, SymbolMatchers.isReference("date"), SymbolMatchers.isLiteral(1395874800000L)));
    }

    @Test
    public void testSelectFromPartitionedTable() throws Exception {
        String partition1 = new io.crate.metadata.PartitionName(new RelationName("doc", "parted"), java.util.Arrays.asList("1395874800000")).asIndexName();
        String partition2 = new io.crate.metadata.PartitionName(new RelationName("doc", "parted"), java.util.Arrays.asList("1395961200000")).asIndexName();
        String partition3 = new io.crate.metadata.PartitionName(new RelationName("doc", "parted"), java.util.Collections.singletonList(null)).asIndexName();
        WhereClause whereClause = analyzeSelectWhere("select id, name from parted where date = 1395874800000");
        assertEquals(ImmutableList.of(partition1), whereClause.partitions());
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere(("select id, name from parted where date = 1395874800000 " + "and substr(name, 0, 4) = 'this'"));
        assertEquals(ImmutableList.of(partition1), whereClause.partitions());
        assertThat(whereClause.hasQuery(), Matchers.is(true));
        assertThat(whereClause.noMatch(), Matchers.is(false));
        whereClause = analyzeSelectWhere("select id, name from parted where date >= 1395874800000");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date < 1395874800000");
        assertEquals(ImmutableList.of(), whereClause.partitions());
        assertTrue(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date = 1395874800000 and date = 1395961200000");
        assertEquals(ImmutableList.of(), whereClause.partitions());
        assertTrue(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date = 1395874800000 or date = 1395961200000");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date < 1395874800000 or date > 1395874800000");
        assertEquals(ImmutableList.of(partition2), whereClause.partitions());
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date in (1395874800000, 1395961200000)");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date in (1395874800000, 1395961200000) and id = 1");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertTrue(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        /**
         * obj['col'] = 'undefined' => null as col doesn't exist
         *
         *  partition1: not (true  and null) -> not (null)  -> null -> no match
         *  partition2: not (false and null) -> not (false) -> true -> match
         *  partition3: not (null  and null) -> not (null)  -> null -> no match
         */
        whereClause = analyzeSelectWhere("select id, name from parted where not (date = 1395874800000 and obj['col'] = 'undefined')");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition2));
        assertThat(whereClause.hasQuery(), Matchers.is(false));
        assertThat(whereClause.noMatch(), Matchers.is(false));
        whereClause = analyzeSelectWhere("select id, name from parted where date in (1395874800000) or date in (1395961200000)");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date = 1395961200000 and id = 1");
        assertEquals(ImmutableList.of(partition2), whereClause.partitions());
        assertTrue(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where (date =1395874800000 or date = 1395961200000) and id = 1");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertTrue(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date = 1395874800000 and id is null");
        assertEquals(ImmutableList.of(partition1), whereClause.partitions());
        assertTrue(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where date is null and id = 1");
        assertEquals(ImmutableList.of(partition3), whereClause.partitions());
        assertTrue(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where 1395874700000 < date and date < 1395961200001");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
        whereClause = analyzeSelectWhere("select id, name from parted where '2014-03-16T22:58:20' < date and date < '2014-03-27T23:00:01'");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(partition1, partition2));
        assertFalse(whereClause.hasQuery());
        assertFalse(whereClause.noMatch());
    }

    @Test
    public void testSelectFromPartitionedTableUnsupported() throws Exception {
        // these queries won't work because we would have to execute 2 separate ESSearch tasks
        // and merge results which is not supported right now and maybe never will be
        String expectedMessage = "logical conjunction of the conditions in the WHERE clause which involve " + "partitioned columns led to a query that can't be executed.";
        try {
            analyzeSelectWhere("select id, name from parted where date = 1395961200000 or id = 1");
            fail(("Expected UnsupportedOperationException with message: " + expectedMessage));
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), Matchers.is(expectedMessage));
        }
        try {
            analyzeSelectWhere("select id, name from parted where id = 1 or date = 1395961200000");
            fail(("Expected UnsupportedOperationException with message: " + expectedMessage));
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), Matchers.is(expectedMessage));
        }
        try {
            analyzeSelectWhere("select id, name from parted where date = 1395961200000 or date/0 = 1");
            fail(("Expected UnsupportedOperationException with message: " + expectedMessage));
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), Matchers.is(expectedMessage));
        }
    }

    @Test
    public void testAnyInvalidArrayType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot cast ['foo', 'bar', 'baz'] to type boolean_array");
        analyzeSelectWhere("select * from users_multi_pk where awesome = any(['foo', 'bar', 'baz'])");
    }

    @Test
    public void testInConvertedToAnyIfOnlyLiterals() throws Exception {
        StringBuilder sb = new StringBuilder("select id from sys.shards where id in (");
        int i = 0;
        for (; i < 1500; i++) {
            sb.append(i);
            sb.append(',');
        }
        sb.append((i++));
        sb.append(')');
        String s = sb.toString();
        WhereClause whereClause = analyzeSelectWhere(s);
        assertThat(whereClause.query(), SymbolMatchers.isFunction(EQ, ImmutableList.<DataType>of(INTEGER, new io.crate.types.ArrayType(DataTypes.INTEGER))));
    }

    @Test
    public void testAnyEqConvertableArrayTypeLiterals() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from users where name = any([1, 2, 3])");
        assertThat(whereClause.query(), SymbolMatchers.isFunction(EQ, ImmutableList.<DataType>of(STRING, new io.crate.types.ArrayType(DataTypes.STRING))));
    }

    @Test
    public void testAnyLikeConvertableArrayTypeLiterals() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from users where name like any([1, 2, 3])");
        assertThat(whereClause.query(), SymbolMatchers.isFunction(LIKE, ImmutableList.<DataType>of(STRING, new io.crate.types.ArrayType(DataTypes.STRING))));
    }

    @Test
    public void testAnyLikeArrayLiteral() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from users where name like any(['a', 'b', 'c'])");
        assertThat(whereClause.query(), SymbolMatchers.isFunction(LIKE, ImmutableList.<DataType>of(STRING, new io.crate.types.ArrayType(DataTypes.STRING))));
    }

    @Test
    public void testEqualGenColOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where y = 1");
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(asIndexName()));
    }

    @Test
    public void testNonPartitionedNotOptimized() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where x = 1");
        assertThat(whereClause.query(), TestingHelpers.isSQL("(doc.generated_col.x = 1)"));
    }

    @Test
    public void testGtGenColOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where ts > '2015-01-02T12:00:00'");
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(asIndexName()));
    }

    @Test
    public void testGenColRoundingFunctionNoSwappingOperatorOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where ts >= '2015-01-02T12:00:00'");
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(asIndexName()));
    }

    @Test
    public void testMultiplicationGenColNoOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where y > 1");
        // no optimization is done
        assertThat(whereClause.partitions().size(), Matchers.is(0));
        assertThat(whereClause.noMatch(), Matchers.is(false));
    }

    @Test
    public void testMultipleColumnsOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where ts > '2015-01-01T12:00:00' and y = 1");
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(asIndexName()));
    }

    @Test
    public void testColumnReferencedTwiceInGeneratedColumnPartitioned() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from double_gen_parted where x = 4");
        assertThat(whereClause.query(), TestingHelpers.isSQL("(doc.double_gen_parted.x = 4)"));
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(".partitioned.double_gen_parted.0813a0hm"));
    }

    @Test
    public void testOptimizationNonRoundingFunctionGreater() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from double_gen_parted where x > 3");
        assertThat(whereClause.query(), TestingHelpers.isSQL("(doc.double_gen_parted.x > 3)"));
        assertThat(whereClause.partitions().size(), Matchers.is(1));
        assertThat(whereClause.partitions().get(0), Matchers.is(".partitioned.double_gen_parted.0813a0hm"));
    }

    @Test
    public void testGenColRangeOptimization() throws Exception {
        WhereClause whereClause = analyzeSelectWhere("select * from generated_col where ts >= '2015-01-01T12:00:00' and ts <= '2015-01-02T00:00:00'");
        RelationName relationName = new RelationName("doc", "generated_col");
        assertThat(whereClause.partitions(), Matchers.containsInAnyOrder(asIndexName(), asIndexName()));
    }

    @Test
    public void testRawNotAllowedInQuery() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("The _raw column is not searchable and cannot be used inside a query");
        analyzeSelectWhere("select * from users where _raw = 'foo'");
    }

    @Test
    public void testVersionOnlySupportedWithEqualOperator() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        analyzeSelectWhere("select * from users where _version > 1");
    }
}

