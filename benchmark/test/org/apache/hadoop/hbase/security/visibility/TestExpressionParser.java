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
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, SmallTests.class })
public class TestExpressionParser {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestExpressionParser.class);

    private ExpressionParser parser = new ExpressionParser();

    @Test
    public void testPositiveCases() throws Exception {
        // abc -> (abc)
        ExpressionNode node = parser.parse("abc");
        Assert.assertTrue((node instanceof LeafExpressionNode));
        Assert.assertEquals("abc", getIdentifier());
        // a&b|c&d -> (((a & b) | c) & )
        node = parser.parse("a&b|c&d");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        NonLeafExpressionNode nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("d", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("a", getIdentifier());
        // (a) -> (a)
        node = parser.parse("(a)");
        Assert.assertTrue((node instanceof LeafExpressionNode));
        Assert.assertEquals("a", getIdentifier());
        // (a&b) -> (a & b)
        node = parser.parse(" ( a & b )");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // ((((a&b)))) -> (a & b)
        node = parser.parse("((((a&b))))");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // (a|b)&(cc|def) -> ((a | b) & (cc | def))
        node = parser.parse("( a | b ) & (cc|def)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        NonLeafExpressionNode nlNodeLeft = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        NonLeafExpressionNode nlNodeRight = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(OR, nlNodeLeft.getOperator());
        Assert.assertEquals(2, nlNodeLeft.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals(OR, nlNodeRight.getOperator());
        Assert.assertEquals(2, nlNodeRight.getChildExps().size());
        Assert.assertEquals("cc", getIdentifier());
        Assert.assertEquals("def", getIdentifier());
        // a&(cc|de) -> (a & (cc | de))
        node = parser.parse("a&(cc|de)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("cc", getIdentifier());
        Assert.assertEquals("de", getIdentifier());
        // (a&b)|c -> ((a & b) | c)
        node = parser.parse("(a&b)|c");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // (a&b&c)|d -> (((a & b) & c) | d)
        node = parser.parse("(a&b&c)|d");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("d", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals("a", getIdentifier());
        // a&(b|(c|d)) -> (a & (b | (c | d)))
        node = parser.parse("a&(b|(c|d))");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertEquals("d", getIdentifier());
        // (!a) -> (!a)
        node = parser.parse("(!a)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals("a", getIdentifier());
        // a&(!b) -> (a & (!b))
        node = parser.parse("a&(!b)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals(1, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        // !a&b -> ((!a) & b)
        node = parser.parse("!a&b");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals(1, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        // !a&(!b) -> ((!a) & (!b))
        node = parser.parse("!a&(!b)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNodeLeft = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        nlNodeRight = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(NOT, nlNodeLeft.getOperator());
        Assert.assertEquals(1, nlNodeLeft.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals(NOT, nlNodeRight.getOperator());
        Assert.assertEquals(1, nlNodeRight.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        // !a&!b -> ((!a) & (!b))
        node = parser.parse("!a&!b");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNodeLeft = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        nlNodeRight = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(NOT, nlNodeLeft.getOperator());
        Assert.assertEquals(1, nlNodeLeft.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals(NOT, nlNodeRight.getOperator());
        Assert.assertEquals(1, nlNodeRight.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        // !(a&b) -> (!(a & b))
        node = parser.parse("!(a&b)");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals(1, nlNode.getChildExps().size());
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        // a&!b -> (a & (!b))
        node = parser.parse("a&!b");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals(1, nlNode.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
        // !((a|b)&!(c&!b)) -> (!((a | b) & (!(c & (!b)))))
        node = parser.parse("!((a | b) & !(c & !b))");
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(NOT, nlNode.getOperator());
        Assert.assertEquals(1, nlNode.getChildExps().size());
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        Assert.assertTrue(((nlNode.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNodeLeft = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        nlNodeRight = ((NonLeafExpressionNode) (nlNode.getChildExps().get(1)));
        Assert.assertEquals(OR, nlNodeLeft.getOperator());
        Assert.assertEquals("a", getIdentifier());
        Assert.assertEquals("b", getIdentifier());
        Assert.assertEquals(NOT, nlNodeRight.getOperator());
        Assert.assertEquals(1, nlNodeRight.getChildExps().size());
        nlNodeRight = ((NonLeafExpressionNode) (nlNodeRight.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNodeRight.getOperator());
        Assert.assertEquals(2, nlNodeRight.getChildExps().size());
        Assert.assertEquals("c", getIdentifier());
        Assert.assertTrue(((nlNodeRight.getChildExps().get(1)) instanceof NonLeafExpressionNode));
        nlNodeRight = ((NonLeafExpressionNode) (nlNodeRight.getChildExps().get(1)));
        Assert.assertEquals(NOT, nlNodeRight.getOperator());
        Assert.assertEquals(1, nlNodeRight.getChildExps().size());
        Assert.assertEquals("b", getIdentifier());
    }

    @Test
    public void testNegativeCases() throws Exception {
        executeNegativeCase("(");
        executeNegativeCase(")");
        executeNegativeCase("()");
        executeNegativeCase("(a");
        executeNegativeCase("a&");
        executeNegativeCase("a&|b");
        executeNegativeCase("!");
        executeNegativeCase("a!");
        executeNegativeCase("a!&");
        executeNegativeCase("&");
        executeNegativeCase("|");
        executeNegativeCase("!(a|(b&c)&!b");
        executeNegativeCase("!!a");
        executeNegativeCase("( a & b ) | ( c & d e)");
        executeNegativeCase("! a");
    }

    @Test
    public void testNonAsciiCases() throws Exception {
        ExpressionNode node = parser.parse((((((((CellVisibility.quote("\'")) + "&") + (CellVisibility.quote("+"))) + "|") + (CellVisibility.quote("-"))) + "&") + (CellVisibility.quote("?"))));
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        NonLeafExpressionNode nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("?", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("-", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("+", getIdentifier());
        Assert.assertEquals("\'", getIdentifier());
        node = parser.parse((((((((CellVisibility.quote("\'")) + "&") + (CellVisibility.quote("+"))) + "|") + (CellVisibility.quote("-"))) + "&") + (CellVisibility.quote("?"))));
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("?", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("-", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("+", getIdentifier());
        Assert.assertEquals("\'", getIdentifier());
    }

    @Test
    public void testCasesSeperatedByDoubleQuotes() throws Exception {
        ExpressionNode node = null;
        try {
            node = parser.parse("\'&\"|+&?");
            Assert.fail("Excpetion must be thrown as there are special characters without quotes");
        } catch (ParseException e) {
        }
        node = parser.parse((((((CellVisibility.quote("\'")) + "&") + (CellVisibility.quote("\""))) + "|") + (CellVisibility.quote(("+" + ("&" + "?"))))));
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        NonLeafExpressionNode nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals(("+" + ("&" + "?")), getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("\"", getIdentifier());
        Assert.assertEquals("\'", getIdentifier());
        try {
            node = parser.parse(((((CellVisibility.quote("\'&\\")) + "|") + (CellVisibility.quote(("+" + ("&" + "\\"))))) + (CellVisibility.quote("$$\""))));
            Assert.fail("Excpetion must be thrown as there is not operator");
        } catch (ParseException e) {
        }
        node = parser.parse((((((CellVisibility.quote(("\'" + ("&" + "\\")))) + "|") + (CellVisibility.quote(("?" + ("&" + "\\"))))) + "&") + (CellVisibility.quote("$$\""))));
        Assert.assertTrue((node instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (node));
        Assert.assertEquals(AND, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals("$$\"", getIdentifier());
        Assert.assertTrue(((nlNode.getChildExps().get(0)) instanceof NonLeafExpressionNode));
        nlNode = ((NonLeafExpressionNode) (nlNode.getChildExps().get(0)));
        Assert.assertEquals(OR, nlNode.getOperator());
        Assert.assertEquals(2, nlNode.getChildExps().size());
        Assert.assertEquals(("\'" + ("&" + "\\")), getIdentifier());
        Assert.assertEquals(("?" + ("&" + "\\")), getIdentifier());
        try {
            node = parser.parse((((((CellVisibility.quote("+&\\")) + "|") + (CellVisibility.quote("\'&\\"))) + "&") + "\"$$"));
            Assert.fail("Excpetion must be thrown as there is no end quote");
        } catch (ParseException e) {
        }
    }
}

