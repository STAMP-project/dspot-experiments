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
package org.apache.calcite.plan;


import SqlStdOperatorTable.AND;
import SqlStdOperatorTable.EQUALS;
import SqlStdOperatorTable.IS_NOT_DISTINCT_FROM;
import SqlStdOperatorTable.IS_NULL;
import SqlStdOperatorTable.OR;
import SqlTypeName.DECIMAL;
import SqlTypeName.VARCHAR;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.TestUtil;
import org.apache.calcite.util.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link RelOptUtil} and other classes in this package.
 */
public class RelOptUtilTest {
    private static final RelBuilder REL_BUILDER = RelBuilder.create(RelOptUtilTest.config().build());

    private static final RelNode EMP_SCAN = RelOptUtilTest.REL_BUILDER.scan("EMP").build();

    private static final RelNode DEPT_SCAN = RelOptUtilTest.REL_BUILDER.scan("DEPT").build();

    private static final RelDataType EMP_ROW = RelOptUtilTest.EMP_SCAN.getRowType();

    private static final RelDataType DEPT_ROW = RelOptUtilTest.DEPT_SCAN.getRowType();

    private static final List<RelDataTypeField> EMP_DEPT_JOIN_REL_FIELDS = Lists.newArrayList(Iterables.concat(RelOptUtilTest.EMP_ROW.getFieldList(), RelOptUtilTest.DEPT_ROW.getFieldList()));

    // ~ Constructors -----------------------------------------------------------
    public RelOptUtilTest() {
    }

    // ~ Methods ----------------------------------------------------------------
    @Test
    public void testTypeDump() {
        RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RelDataType t1 = typeFactory.builder().add("f0", DECIMAL, 5, 2).add("f1", VARCHAR, 10).build();
        TestUtil.assertEqualsVerbose(TestUtil.fold("f0 DECIMAL(5, 2) NOT NULL,", "f1 VARCHAR(10) NOT NULL"), Util.toLinux(((RelOptUtil.dumpType(t1)) + "\n")));
        RelDataType t2 = typeFactory.builder().add("f0", t1).add("f1", typeFactory.createMultisetType(t1, (-1))).build();
        TestUtil.assertEqualsVerbose(TestUtil.fold("f0 RECORD (", "  f0 DECIMAL(5, 2) NOT NULL,", "  f1 VARCHAR(10) NOT NULL) NOT NULL,", "f1 RECORD (", "  f0 DECIMAL(5, 2) NOT NULL,", "  f1 VARCHAR(10) NOT NULL) NOT NULL MULTISET NOT NULL"), Util.toLinux(((RelOptUtil.dumpType(t2)) + "\n")));
    }

    /**
     * Tests the rules for how we name rules.
     */
    @Test
    public void testRuleGuessDescription() {
        Assert.assertEquals("Bar", RelOptRule.guessDescription("com.foo.Bar"));
        Assert.assertEquals("Baz", RelOptRule.guessDescription("com.flatten.Bar$Baz"));
        // yields "1" (which as an integer is an invalid
        try {
            Util.discard(RelOptRule.guessDescription("com.foo.Bar$1"));
            Assert.fail("expected exception");
        } catch (RuntimeException e) {
            Assert.assertEquals(("Derived description of rule class com.foo.Bar$1 is an " + "integer, not valid. Supply a description manually."), e.getMessage());
        }
    }

    /**
     * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}
     * where the join condition contains just one which is a EQUAL operator.
     */
    @Test
    public void testSplitJoinConditionEquals() {
        int leftJoinIndex = RelOptUtilTest.EMP_SCAN.getRowType().getFieldNames().indexOf("DEPTNO");
        int rightJoinIndex = RelOptUtilTest.DEPT_ROW.getFieldNames().indexOf("DEPTNO");
        RexNode joinCond = RelOptUtilTest.REL_BUILDER.call(EQUALS, RexInputRef.of(leftJoinIndex, RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS), RexInputRef.of(((RelOptUtilTest.EMP_ROW.getFieldCount()) + rightJoinIndex), RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS));
        RelOptUtilTest.splitJoinConditionHelper(joinCond, Collections.singletonList(leftJoinIndex), Collections.singletonList(rightJoinIndex), Collections.singletonList(true), RelOptUtilTest.REL_BUILDER.literal(true));
    }

    /**
     * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}
     * where the join condition contains just one which is a IS NOT DISTINCT operator.
     */
    @Test
    public void testSplitJoinConditionIsNotDistinctFrom() {
        int leftJoinIndex = RelOptUtilTest.EMP_SCAN.getRowType().getFieldNames().indexOf("DEPTNO");
        int rightJoinIndex = RelOptUtilTest.DEPT_ROW.getFieldNames().indexOf("DEPTNO");
        RexNode joinCond = RelOptUtilTest.REL_BUILDER.call(IS_NOT_DISTINCT_FROM, RexInputRef.of(leftJoinIndex, RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS), RexInputRef.of(((RelOptUtilTest.EMP_ROW.getFieldCount()) + rightJoinIndex), RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS));
        RelOptUtilTest.splitJoinConditionHelper(joinCond, Collections.singletonList(leftJoinIndex), Collections.singletonList(rightJoinIndex), Collections.singletonList(false), RelOptUtilTest.REL_BUILDER.literal(true));
    }

    /**
     * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}
     * where the join condition contains an expanded version of IS NOT DISTINCT
     */
    @Test
    public void testSplitJoinConditionExpandedIsNotDistinctFrom() {
        int leftJoinIndex = RelOptUtilTest.EMP_SCAN.getRowType().getFieldNames().indexOf("DEPTNO");
        int rightJoinIndex = RelOptUtilTest.DEPT_ROW.getFieldNames().indexOf("DEPTNO");
        RexInputRef leftKeyInputRef = RexInputRef.of(leftJoinIndex, RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS);
        RexInputRef rightKeyInputRef = RexInputRef.of(((RelOptUtilTest.EMP_ROW.getFieldCount()) + rightJoinIndex), RelOptUtilTest.EMP_DEPT_JOIN_REL_FIELDS);
        RexNode joinCond = RelOptUtilTest.REL_BUILDER.call(OR, RelOptUtilTest.REL_BUILDER.call(EQUALS, leftKeyInputRef, rightKeyInputRef), RelOptUtilTest.REL_BUILDER.call(AND, RelOptUtilTest.REL_BUILDER.call(IS_NULL, leftKeyInputRef), RelOptUtilTest.REL_BUILDER.call(IS_NULL, rightKeyInputRef)));
        RelOptUtilTest.splitJoinConditionHelper(joinCond, Collections.singletonList(leftJoinIndex), Collections.singletonList(rightJoinIndex), Collections.singletonList(false), RelOptUtilTest.REL_BUILDER.literal(true));
    }
}

/**
 * End RelOptUtilTest.java
 */
