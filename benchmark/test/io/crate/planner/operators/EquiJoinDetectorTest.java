/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.planner.operators;


import JoinType.ANTI;
import JoinType.CROSS;
import JoinType.FULL;
import JoinType.INNER;
import JoinType.LEFT;
import JoinType.RIGHT;
import JoinType.SEMI;
import io.crate.expression.symbol.Symbol;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.T3;
import org.hamcrest.Matchers;
import org.junit.Test;


public class EquiJoinDetectorTest extends CrateUnitTest {
    private static final SqlExpressions SQL_EXPRESSIONS = new SqlExpressions(T3.SOURCES);

    @Test
    public void testPossibleOnInnerContainingEqCondition() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x = t2.y");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(true));
    }

    @Test
    public void testPossibleOnInnerContainingEqAndAnyCondition() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x > t2.y and t1.a = t2.b and not(t1.i = t2.i)");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(true));
    }

    @Test
    public void testNotPossibleOnInnerWithoutAnyEqCondition() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x > t2.y and t1.a > t2.b");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(false));
    }

    @Test
    public void testPossibleOnInnerWithEqAndScalarOnOneRelation() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x + t1.i = t2.b");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(true));
    }

    @Test
    public void testNotPossibleOnInnerWithEqAndScalarOnMultipleRelations() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x + t2.y = 4");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(false));
    }

    @Test
    public void testNotPossibleOnInnerContainingEqOrAnyCondition() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x = t2.y and t1.a = t2.b or t1.i = t2.i");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(false));
    }

    @Test
    public void testNotPossibleIfNotAnInnerJoin() {
        assertThat(EquiJoinDetector.isHashJoinPossible(CROSS, null), Matchers.is(false));
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x = t2.y");
        assertThat(EquiJoinDetector.isHashJoinPossible(LEFT, joinCondition), Matchers.is(false));
        assertThat(EquiJoinDetector.isHashJoinPossible(RIGHT, joinCondition), Matchers.is(false));
        assertThat(EquiJoinDetector.isHashJoinPossible(FULL, joinCondition), Matchers.is(false));
        assertThat(EquiJoinDetector.isHashJoinPossible(ANTI, joinCondition), Matchers.is(false));
        assertThat(EquiJoinDetector.isHashJoinPossible(SEMI, joinCondition), Matchers.is(false));
    }

    @Test
    public void testNotPossibleOnEqWithoutRelationFieldsOnBothSides() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("t1.x = 4");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(false));
    }

    @Test
    public void testNotPossibleOnNotWrappingEq() {
        Symbol joinCondition = EquiJoinDetectorTest.SQL_EXPRESSIONS.asSymbol("NOT (t1.a = t2.b)");
        assertThat(EquiJoinDetector.isHashJoinPossible(INNER, joinCondition), Matchers.is(false));
    }
}

