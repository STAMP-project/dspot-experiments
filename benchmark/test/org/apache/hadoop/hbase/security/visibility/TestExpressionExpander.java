/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.security.visibility;


import Operator.AND;
import Operator.NOT;
import Operator.OR;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.security.visibility.expression.ExpressionNode;
import org.apache.hadoop.hbase.security.visibility.expression.LeafExpressionNode;
import org.apache.hadoop.hbase.security.visibility.expression.NonLeafExpressionNode;
import org.apache.hadoop.hbase.security.visibility.expression.Operator;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, SmallTests.class })
public class TestExpressionExpander {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestExpressionExpander.class);

    @Test
    public void testPositiveCases() throws Exception {
        ExpressionExpander expander = new ExpressionExpander();
        // (!a) -> (!a)
        NonLeafExpressionNode exp1 = new NonLeafExpressionNode(Operator.NOT, new LeafExpressionNode("a"));
        ExpressionNode result = expander.expand(exp1);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        NonLeafExpressionNode nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(NOT, nlResult.getOperator());
        Assert.assertEquals("a", getIdentifier());
        // (a | b) -> (a | b)
        NonLeafExpressionNode exp2 = new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b"));
        result = expander.expand(exp2);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // (a & b) -> (a & b)
        NonLeafExpressionNode exp3 = new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("a"), new LeafExpressionNode("b"));
        result = expander.expand(exp3);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(AND, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // ((a | b) | c) -> (a | b | c)
        NonLeafExpressionNode exp4 = new NonLeafExpressionNode(Operator.OR, new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")), new LeafExpressionNode("c"));
        result = expander.expand(exp4);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(3, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        // ((a & b) & c) -> (a & b & c)
        NonLeafExpressionNode exp5 = new NonLeafExpressionNode(Operator.AND, new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("a"), new LeafExpressionNode("b")), new LeafExpressionNode("c"));
        result = expander.expand(exp5);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(AND, nlResult.getOperator());
        Assert.assertEquals(3, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        // (a | b) & c -> ((a & c) | (b & c))
        NonLeafExpressionNode exp6 = new NonLeafExpressionNode(Operator.AND, new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")), new LeafExpressionNode("c"));
        result = expander.expand(exp6);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        NonLeafExpressionNode temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        // (a & b) | c -> ((a & b) | c)
        NonLeafExpressionNode exp7 = new NonLeafExpressionNode(Operator.OR, new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("a"), new LeafExpressionNode("b")), new LeafExpressionNode("c"));
        result = expander.expand(exp7);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        nlResult = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // ((a & b) | c) & d -> (((a & b) & d) | (c & d))
        NonLeafExpressionNode exp8 = new NonLeafExpressionNode(Operator.AND);
        exp8.addChildExp(new NonLeafExpressionNode(Operator.OR, new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("a"), new LeafExpressionNode("b")), new LeafExpressionNode("c")));
        exp8.addChildExp(new LeafExpressionNode("d"));
        result = expander.expand(exp8);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // (a | b) | (c | d) -> (a | b | c | d)
        NonLeafExpressionNode exp9 = new NonLeafExpressionNode(Operator.OR);
        exp9.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")));
        exp9.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("c"), new LeafExpressionNode("d")));
        result = expander.expand(exp9);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(4, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // (a & b) & (c & d) -> (a & b & c & d)
        NonLeafExpressionNode exp10 = new NonLeafExpressionNode(Operator.AND);
        exp10.addChildExp(new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("a"), new LeafExpressionNode("b")));
        exp10.addChildExp(new NonLeafExpressionNode(Operator.AND, new LeafExpressionNode("c"), new LeafExpressionNode("d")));
        result = expander.expand(exp10);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(AND, nlResult.getOperator());
        Assert.assertEquals(4, nlResult.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // (a | b) & (c | d) -> ((a & c) | (a & d) | (b & c) | (b & d))
        NonLeafExpressionNode exp11 = new NonLeafExpressionNode(Operator.AND);
        exp11.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")));
        exp11.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("c"), new LeafExpressionNode("d")));
        result = expander.expand(exp11);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(4, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(2)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(3)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // (((a | b) | c) | d) & e -> ((a & e) | (b & e) | (c & e) | (d & e))
        NonLeafExpressionNode exp12 = new NonLeafExpressionNode(Operator.AND);
        NonLeafExpressionNode tempExp1 = new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b"));
        NonLeafExpressionNode tempExp2 = new NonLeafExpressionNode(Operator.OR, tempExp1, new LeafExpressionNode("c"));
        NonLeafExpressionNode tempExp3 = new NonLeafExpressionNode(Operator.OR, tempExp2, new LeafExpressionNode("d"));
        exp12.addChildExp(tempExp3);
        exp12.addChildExp(new LeafExpressionNode("e"));
        result = expander.expand(exp12);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(4, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(2)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(3)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("d", getIdentifier());
        Assert.assertEquals("e", getIdentifier());
        // (a | b | c) & d -> ((a & d) | (b & d) | (c & d))
        NonLeafExpressionNode exp13 = new NonLeafExpressionNode(Operator.AND, new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b"), new LeafExpressionNode("c")), new LeafExpressionNode("d"));
        result = expander.expand(exp13);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(3, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(2)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // ((a | b) & (c | d)) & (e | f) -> (((a & c) & e) | ((a & c) & f) | ((a & d) & e) | ((a & d) &
        // f) | ((b & c) & e) | ((b & c) & f) | ((b & d) & e) | ((b & d) & f))
        NonLeafExpressionNode exp15 = new NonLeafExpressionNode(Operator.AND);
        NonLeafExpressionNode temp1 = new NonLeafExpressionNode(Operator.AND);
        temp1.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")));
        temp1.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("c"), new LeafExpressionNode("d")));
        exp15.addChildExp(temp1);
        exp15.addChildExp(new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("e"), new LeafExpressionNode("f")));
        result = expander.expand(exp15);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(OR, nlResult.getOperator());
        Assert.assertEquals(8, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("f", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(2)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(3)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("f", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(4)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(5)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("f", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("c", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(6)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("e", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(7)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("f", getIdentifier());
        temp = ((NonLeafExpressionNode) (temp.getChildExps().get(0)));
        Assert.assertEquals(AND, temp.getOperator());
        Assert.assertEquals(2, temp.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // !(a | b) -> ((!a) & (!b))
        NonLeafExpressionNode exp16 = new NonLeafExpressionNode(Operator.NOT, new NonLeafExpressionNode(Operator.OR, new LeafExpressionNode("a"), new LeafExpressionNode("b")));
        result = expander.expand(exp16);
        Assert.assertTrue((result instanceof NonLeafExpressionNode));
        nlResult = ((NonLeafExpressionNode) (result));
        Assert.assertEquals(AND, nlResult.getOperator());
        Assert.assertEquals(2, nlResult.getChildExps().size());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(0)));
        Assert.assertEquals(NOT, temp.getOperator());
        Assert.assertEquals("a", getIdentifier());
        temp = ((NonLeafExpressionNode) (nlResult.getChildExps().get(1)));
        Assert.assertEquals(NOT, temp.getOperator());
        Assert.assertEquals("b", getIdentifier());
    }
}

