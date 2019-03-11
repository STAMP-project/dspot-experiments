/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.planner.logical;


import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import java.util.BitSet;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.drill.exec.planner.logical.partition.FindPartitionConditions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilterSplitTest {
    static final Logger logger = LoggerFactory.getLogger(FilterSplitTest.class);

    final JavaTypeFactory t = new JavaTypeFactoryImpl();

    final RexBuilder builder = new RexBuilder(t);

    final RelDataType intType = t.createSqlType(INTEGER);

    final RelDataType sType = t.createSqlType(VARCHAR, 20);

    @Test
    public void simpleCompound() {
        // a < 1 AND dir0 in (2,3)
        RexNode n = and(lt(c(0), lit(1)), or(eq(c(1), lit(2)), eq(c(1), lit(3))));
        BitSet bs = new BitSet();
        bs.set(1);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals(n.toString(), "AND(<($0, 1), OR(=($1, 2), =($1, 3)))");
        Assert.assertEquals(partNode.toString(), "OR(=($1, 2), =($1, 3))");
    }

    @Test
    public void twoLevelDir() {
        // (dir0 = 1 and dir1 = 2) OR (dir0 = 3 and dir1 = 4)
        RexNode n = or(and(eq(c(1), lit(1)), eq(c(2), lit(2))), and(eq(c(1), lit(3)), eq(c(2), lit(4))));
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(2);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("OR(AND(=($1, 1), =($2, 2)), AND(=($1, 3), =($2, 4)))", n.toString());
        Assert.assertEquals("OR(AND(=($1, 1), =($2, 2)), AND(=($1, 3), =($2, 4)))", partNode.toString());
    }

    @Test
    public void AndOrMix() {
        // b = 3 OR dir0 = 1 and a = 2
        RexNode n = or(eq(c(2), lit(3)), and(eq(c(0), lit(1)), eq(c(1), lit(2))));
        BitSet bs = new BitSet();
        bs.set(0);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("OR(=($2, 3), AND(=($0, 1), =($1, 2)))", n.toString());
        Assert.assertEquals(null, partNode);
    }

    @Test
    public void NotOnAnd() {
        // not (dir0 = 1 AND b = 2)
        RexNode n = not(and(eq(c(0), lit(1)), eq(c(1), lit(2))));
        BitSet bs = new BitSet();
        bs.set(0);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("NOT(AND(=($0, 1), =($1, 2)))", n.toString());
        Assert.assertEquals(null, partNode);
    }

    @Test
    public void AndNot() {
        // (not dir0 = 1) AND b = 2)
        RexNode n = and(not(eq(c(0), lit(1))), eq(c(1), lit(2)));
        BitSet bs = new BitSet();
        bs.set(0);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("AND(NOT(=($0, 1)), =($1, 2))", n.toString());
        Assert.assertEquals("NOT(=($0, 1))", partNode.toString());
    }

    @Test
    public void badOr() {
        // (dir0 = 1 and dir1 = 2) OR (a < 5)
        RexNode n = or(and(eq(c(1), lit(1)), eq(c(2), lit(2))), lt(c(0), lit(5)));
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(2);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("OR(AND(=($1, 1), =($2, 2)), <($0, 5))", n.toString());
        Assert.assertTrue((partNode == null));
    }

    @Test
    public void badFunc() {
        // (dir0 = 1 and dir1 = 2) OR (a < 5)
        RexNode n = fn(cs(0), cs(1));
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(2);
        FindPartitionConditions c = new FindPartitionConditions(bs, builder);
        c.analyze(n);
        RexNode partNode = c.getFinalCondition();
        Assert.assertEquals("||($0, $1)", n.toString());
        Assert.assertTrue((partNode == null));
    }
}

