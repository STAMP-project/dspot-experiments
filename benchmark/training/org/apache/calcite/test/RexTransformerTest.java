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


import Logic.TRUE;
import Logic.TRUE_FALSE;
import Logic.TRUE_FALSE_UNKNOWN;
import Logic.UNKNOWN_AS_TRUE;
import SqlTypeName.BOOLEAN;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests transformations on rex nodes.
 */
public class RexTransformerTest {
    // ~ Instance fields --------------------------------------------------------
    RexBuilder rexBuilder = null;

    RexNode x;

    RexNode y;

    RexNode z;

    RexNode trueRex;

    RexNode falseRex;

    RelDataType boolRelDataType;

    RelDataTypeFactory typeFactory;

    @Test
    public void testPreTests() {
        // can make variable nullable?
        RexNode node = new org.apache.calcite.rex.RexInputRef(0, typeFactory.createTypeWithNullability(typeFactory.createSqlType(BOOLEAN), true));
        Assert.assertTrue(node.getType().isNullable());
        // can make variable not nullable?
        node = new org.apache.calcite.rex.RexInputRef(0, typeFactory.createTypeWithNullability(typeFactory.createSqlType(BOOLEAN), false));
        Assert.assertFalse(node.getType().isNullable());
    }

    @Test
    public void testNonBooleans() {
        RexNode node = plus(x, y);
        String expected = node.toString();
        check(Boolean.TRUE, node, expected);
        check(Boolean.FALSE, node, expected);
        check(null, node, expected);
    }

    /**
     * the or operator should pass through unchanged since e.g. x OR y should
     * return true if x=null and y=true if it was transformed into something
     * like (x IS NOT NULL) AND (y IS NOT NULL) AND (x OR y) an incorrect result
     * could be produced
     */
    @Test
    public void testOrUnchanged() {
        RexNode node = or(x, y);
        String expected = node.toString();
        check(Boolean.TRUE, node, expected);
        check(Boolean.FALSE, node, expected);
        check(null, node, expected);
    }

    @Test
    public void testSimpleAnd() {
        RexNode node = and(x, y);
        check(Boolean.FALSE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), AND($0, $1))");
    }

    @Test
    public void testSimpleEquals() {
        RexNode node = equals(x, y);
        check(Boolean.TRUE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), =($0, $1))");
    }

    @Test
    public void testSimpleNotEquals() {
        RexNode node = notEquals(x, y);
        check(Boolean.FALSE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), <>($0, $1))");
    }

    @Test
    public void testSimpleGreaterThan() {
        RexNode node = greaterThan(x, y);
        check(Boolean.TRUE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), >($0, $1))");
    }

    @Test
    public void testSimpleGreaterEquals() {
        RexNode node = greaterThanOrEqual(x, y);
        check(Boolean.FALSE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), >=($0, $1))");
    }

    @Test
    public void testSimpleLessThan() {
        RexNode node = lessThan(x, y);
        check(Boolean.TRUE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), <($0, $1))");
    }

    @Test
    public void testSimpleLessEqual() {
        RexNode node = lessThanOrEqual(x, y);
        check(Boolean.FALSE, node, "AND(AND(IS NOT NULL($0), IS NOT NULL($1)), <=($0, $1))");
    }

    @Test
    public void testOptimizeNonNullLiterals() {
        RexNode node = lessThanOrEqual(x, trueRex);
        check(Boolean.TRUE, node, "AND(IS NOT NULL($0), <=($0, true))");
        node = lessThanOrEqual(trueRex, x);
        check(Boolean.FALSE, node, "AND(IS NOT NULL($0), <=(true, $0))");
    }

    @Test
    public void testSimpleIdentifier() {
        RexNode node = rexBuilder.makeInputRef(boolRelDataType, 0);
        check(Boolean.TRUE, node, "=(IS TRUE($0), true)");
    }

    @Test
    public void testMixed1() {
        // x=true AND y
        RexNode op1 = equals(x, trueRex);
        RexNode and = and(op1, y);
        check(Boolean.FALSE, and, "AND(IS NOT NULL($1), AND(AND(IS NOT NULL($0), =($0, true)), $1))");
    }

    @Test
    public void testMixed2() {
        // x!=true AND y>z
        RexNode op1 = notEquals(x, trueRex);
        RexNode op2 = greaterThan(y, z);
        RexNode and = and(op1, op2);
        check(Boolean.FALSE, and, "AND(AND(IS NOT NULL($0), <>($0, true)), AND(AND(IS NOT NULL($1), IS NOT NULL($2)), >($1, $2)))");
    }

    @Test
    public void testMixed3() {
        // x=y AND false>z
        RexNode op1 = equals(x, y);
        RexNode op2 = greaterThan(falseRex, z);
        RexNode and = and(op1, op2);
        check(Boolean.TRUE, and, "AND(AND(AND(IS NOT NULL($0), IS NOT NULL($1)), =($0, $1)), AND(IS NOT NULL($2), >(false, $2)))");
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-814">[CALCITE-814]
     * RexBuilder reverses precision and scale of DECIMAL literal</a>
     * and
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1344">[CALCITE-1344]
     * Incorrect inferred precision when BigDecimal value is less than 1</a>.
     */
    @Test
    public void testExactLiteral() {
        final RexLiteral literal = rexBuilder.makeExactLiteral(new BigDecimal("-1234.56"));
        Assert.assertThat(literal.getType().getFullTypeString(), CoreMatchers.is("DECIMAL(6, 2) NOT NULL"));
        Assert.assertThat(literal.getValue().toString(), CoreMatchers.is("-1234.56"));
        final RexLiteral literal2 = rexBuilder.makeExactLiteral(new BigDecimal("1234.56"));
        Assert.assertThat(literal2.getType().getFullTypeString(), CoreMatchers.is("DECIMAL(6, 2) NOT NULL"));
        Assert.assertThat(literal2.getValue().toString(), CoreMatchers.is("1234.56"));
        final RexLiteral literal3 = rexBuilder.makeExactLiteral(new BigDecimal("0.0123456"));
        Assert.assertThat(literal3.getType().getFullTypeString(), CoreMatchers.is("DECIMAL(8, 7) NOT NULL"));
        Assert.assertThat(literal3.getValue().toString(), CoreMatchers.is("0.0123456"));
        final RexLiteral literal4 = rexBuilder.makeExactLiteral(new BigDecimal("0.01234560"));
        Assert.assertThat(literal4.getType().getFullTypeString(), CoreMatchers.is("DECIMAL(9, 8) NOT NULL"));
        Assert.assertThat(literal4.getValue().toString(), CoreMatchers.is("0.01234560"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-833">[CALCITE-833]
     * RelOptUtil.splitJoinCondition attempts to split a Join-Condition which
     * has a remaining condition</a>.
     */
    @Test
    public void testSplitJoinCondition() {
        final String sql = "select * \n" + (("from emp a \n" + "INNER JOIN dept b \n") + "ON CAST(a.empno AS int) <> b.deptno");
        final RelNode relNode = RexTransformerTest.toRel(sql);
        final LogicalProject project = ((LogicalProject) (relNode));
        final LogicalJoin join = ((LogicalJoin) (project.getInput(0)));
        final List<RexNode> leftJoinKeys = new ArrayList<>();
        final List<RexNode> rightJoinKeys = new ArrayList<>();
        final ArrayList<RelDataTypeField> sysFieldList = new ArrayList<>();
        final RexNode remaining = RelOptUtil.splitJoinCondition(sysFieldList, join.getInputs().get(0), join.getInputs().get(1), join.getCondition(), leftJoinKeys, rightJoinKeys, null, null);
        Assert.assertThat(remaining.toString(), CoreMatchers.is("<>(CAST($0):INTEGER NOT NULL, $9)"));
        Assert.assertThat(leftJoinKeys.isEmpty(), CoreMatchers.is(true));
        Assert.assertThat(rightJoinKeys.isEmpty(), CoreMatchers.is(true));
    }

    /**
     * Test case for {@link org.apache.calcite.rex.LogicVisitor}.
     */
    @Test
    public void testLogic() {
        // x > FALSE AND ((y = z) IS NOT NULL)
        final RexNode node = and(greaterThan(x, falseRex), isNotNull(equals(y, z)));
        Assert.assertThat(deduceLogic(node, x, TRUE_FALSE), CoreMatchers.is(TRUE_FALSE));
        Assert.assertThat(deduceLogic(node, y, TRUE_FALSE), CoreMatchers.is(TRUE_FALSE_UNKNOWN));
        Assert.assertThat(deduceLogic(node, z, TRUE_FALSE), CoreMatchers.is(TRUE_FALSE_UNKNOWN));
        // TRUE means that a value of FALSE or UNKNOWN will kill the row
        // (therefore we can safely use a semijoin)
        Assert.assertThat(deduceLogic(and(x, y), x, TRUE), CoreMatchers.is(TRUE));
        Assert.assertThat(deduceLogic(and(x, y), y, TRUE), CoreMatchers.is(TRUE));
        Assert.assertThat(deduceLogic(and(x, and(y, z)), z, TRUE), CoreMatchers.is(TRUE));
        Assert.assertThat(deduceLogic(and(x, not(y)), x, TRUE), CoreMatchers.is(TRUE));
        Assert.assertThat(deduceLogic(and(x, not(y)), y, TRUE), CoreMatchers.is(UNKNOWN_AS_TRUE));
        Assert.assertThat(deduceLogic(and(x, not(not(y))), y, TRUE), CoreMatchers.is(TRUE_FALSE_UNKNOWN));// TRUE_FALSE would be better

        Assert.assertThat(deduceLogic(and(x, not(and(y, z))), z, TRUE), CoreMatchers.is(UNKNOWN_AS_TRUE));
        Assert.assertThat(deduceLogic(or(x, y), x, TRUE), CoreMatchers.is(TRUE_FALSE_UNKNOWN));
    }
}

/**
 * End RexTransformerTest.java
 */
