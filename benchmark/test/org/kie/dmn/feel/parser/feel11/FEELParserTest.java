/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.parser.feel11;


import BuiltInType.BOOLEAN;
import BuiltInType.LIST;
import BuiltInType.NUMBER;
import BuiltInType.RANGE;
import BuiltInType.STRING;
import BuiltInType.UNKNOWN;
import InfixOpNode.InfixOperator.ADD;
import InfixOpNode.InfixOperator.AND;
import InfixOpNode.InfixOperator.DIV;
import InfixOpNode.InfixOperator.MULT;
import InfixOpNode.InfixOperator.OR;
import InfixOpNode.InfixOperator.POW;
import InfixOpNode.InfixOperator.SUB;
import Msg.INVALID_VARIABLE_NAME;
import Msg.INVALID_VARIABLE_NAME_START;
import QuantifiedExpressionNode.Quantifier.EVERY;
import QuantifiedExpressionNode.Quantifier.SOME;
import RangeNode.IntervalBoundary.CLOSED;
import RangeNode.IntervalBoundary.OPEN;
import SignedUnaryNode.Sign.NEGATIVE;
import SignedUnaryNode.Sign.POSITIVE;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.util.DynamicTypeUtils;
import org.kie.dmn.feel.util.Msg;


public class FEELParserTest {
    @Test
    public void testIntegerLiteral() {
        String inputExpression = "10";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
    }

    @Test
    public void testNegativeIntegerLiteral() {
        String inputExpression = "-10";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(SignedUnaryNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
        SignedUnaryNode sun = ((SignedUnaryNode) (number));
        Assert.assertThat(sun.getSign(), CoreMatchers.is(NEGATIVE));
        Assert.assertThat(sun.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(sun.getExpression().getText(), CoreMatchers.is("10"));
    }

    @Test
    public void testPositiveIntegerLiteral() {
        String inputExpression = "+10";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(SignedUnaryNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
        SignedUnaryNode sun = ((SignedUnaryNode) (number));
        Assert.assertThat(sun.getSign(), CoreMatchers.is(POSITIVE));
        Assert.assertThat(sun.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(sun.getExpression().getText(), CoreMatchers.is("10"));
    }

    @Test
    public void testFloatLiteral() {
        String inputExpression = "10.5";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
    }

    @Test
    public void testNegativeFloatLiteral() {
        String inputExpression = "-10.5";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(SignedUnaryNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
        SignedUnaryNode sun = ((SignedUnaryNode) (number));
        Assert.assertThat(sun.getSign(), CoreMatchers.is(NEGATIVE));
        Assert.assertThat(sun.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(sun.getExpression().getText(), CoreMatchers.is("10.5"));
    }

    @Test
    public void testPositiveFloatLiteral() {
        String inputExpression = "+10.5";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(SignedUnaryNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        assertLocation(inputExpression, number);
        SignedUnaryNode sun = ((SignedUnaryNode) (number));
        Assert.assertThat(sun.getSign(), CoreMatchers.is(POSITIVE));
        Assert.assertThat(sun.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(sun.getExpression().getText(), CoreMatchers.is("10.5"));
    }

    @Test
    public void testBooleanTrueLiteral() {
        String inputExpression = "true";
        BaseNode bool = parse(inputExpression);
        Assert.assertThat(bool, CoreMatchers.is(CoreMatchers.instanceOf(BooleanNode.class)));
        Assert.assertThat(bool.getResultType(), CoreMatchers.is(BOOLEAN));
        assertLocation(inputExpression, bool);
    }

    @Test
    public void testBooleanFalseLiteral() {
        String inputExpression = "false";
        BaseNode bool = parse(inputExpression);
        Assert.assertThat(bool, CoreMatchers.is(CoreMatchers.instanceOf(BooleanNode.class)));
        Assert.assertThat(bool.getResultType(), CoreMatchers.is(BOOLEAN));
        assertLocation(inputExpression, bool);
    }

    @Test
    public void testNullLiteral() {
        String inputExpression = "null";
        BaseNode nullLit = parse(inputExpression);
        Assert.assertThat(nullLit, CoreMatchers.is(CoreMatchers.instanceOf(NullNode.class)));
        Assert.assertThat(nullLit.getResultType(), CoreMatchers.is(UNKNOWN));
        assertLocation(inputExpression, nullLit);
    }

    @Test
    public void testStringLiteral() {
        String inputExpression = "\"some string\"";
        BaseNode stringLit = parse(inputExpression);
        Assert.assertThat(stringLit, CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(stringLit.getResultType(), CoreMatchers.is(STRING));
        assertLocation(inputExpression, stringLit);
        Assert.assertThat(stringLit.getText(), CoreMatchers.is(inputExpression));
    }

    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        BaseNode nameRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("someSimpleName", STRING)));
        Assert.assertThat(nameRef, CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(nameRef.getResultType(), CoreMatchers.is(STRING));
        assertLocation(inputExpression, nameRef);
    }

    @Test
    public void testQualifiedName() {
        String inputExpression = "My Person.Full Name";
        MapBackedType personType = new MapBackedType("Person", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("Full Name", STRING), DynamicTypeUtils.entry("Age", NUMBER)));
        BaseNode qualRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("My Person", personType)));
        Assert.assertThat(qualRef, CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(qualRef.getResultType(), CoreMatchers.is(STRING));
        List<NameRefNode> parts = getParts();
        // `My Person` ...
        Assert.assertThat(parts.get(0), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(parts.get(0).getResultType(), CoreMatchers.is(personType));
        // ... `.Full Name`
        Assert.assertThat(parts.get(1), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(parts.get(1).getResultType(), CoreMatchers.is(STRING));
        assertLocation(inputExpression, qualRef);
    }

    @Test
    public void testParensWithLiteral() {
        String inputExpression = "(10.5 )";
        BaseNode number = parse(inputExpression);
        Assert.assertThat(number, CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(number.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(number.getText(), CoreMatchers.is("10.5"));
    }

    @Test
    public void testLogicalNegation() {
        String inputExpression = "not ( true )";
        BaseNode neg = parse(inputExpression);
        Assert.assertThat(neg, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(neg.getResultType(), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(neg.getText(), CoreMatchers.is("not ( true )"));
        FunctionInvocationNode not = ((FunctionInvocationNode) (neg));
        Assert.assertThat(not.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(BooleanNode.class)));
        Assert.assertThat(not.getParams().getElements().get(0).getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(not.getParams().getElements().get(0).getText(), CoreMatchers.is("true"));
    }

    @Test
    public void testMultiplication() {
        String inputExpression = "10 * x";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("x", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode mult = ((InfixOpNode) (infix));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("10"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("x"));
    }

    @Test
    public void testDivision() {
        String inputExpression = "y / 5 * ( x )";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("x", NUMBER), DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode mult = ((InfixOpNode) (infix));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("y / 5"));
        InfixOpNode div = ((InfixOpNode) (mult.getLeft()));
        Assert.assertThat(div.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(div.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(div.getOperator(), CoreMatchers.is(DIV));
        Assert.assertThat(div.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(div.getRight().getText(), CoreMatchers.is("5"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("x"));
    }

    @Test
    public void testPower1() {
        String inputExpression = "y * 5 ** 3";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode mult = ((InfixOpNode) (infix));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("5 ** 3"));
        InfixOpNode exp = ((InfixOpNode) (mult.getRight()));
        Assert.assertThat(exp.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(exp.getLeft().getText(), CoreMatchers.is("5"));
        Assert.assertThat(exp.getOperator(), CoreMatchers.is(POW));
        Assert.assertThat(exp.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(exp.getRight().getText(), CoreMatchers.is("3"));
    }

    @Test
    public void testPower2() {
        String inputExpression = "(y * 5) ** 3";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode exp = ((InfixOpNode) (infix));
        Assert.assertThat(exp.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(exp.getLeft().getText(), CoreMatchers.is("y * 5"));
        Assert.assertThat(exp.getOperator(), CoreMatchers.is(POW));
        Assert.assertThat(exp.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(exp.getRight().getText(), CoreMatchers.is("3"));
        InfixOpNode mult = ((InfixOpNode) (exp.getLeft()));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("5"));
    }

    @Test
    public void testPower3() {
        String inputExpression = "y ** 5 * 3";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode mult = ((InfixOpNode) (infix));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("y ** 5"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("3"));
        InfixOpNode exp = ((InfixOpNode) (mult.getLeft()));
        Assert.assertThat(exp.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(exp.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(exp.getOperator(), CoreMatchers.is(POW));
        Assert.assertThat(exp.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(exp.getRight().getText(), CoreMatchers.is("5"));
    }

    @Test
    public void testPower4() {
        String inputExpression = "y ** ( 5 * 3 )";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode exp = ((InfixOpNode) (infix));
        Assert.assertThat(exp.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(exp.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(exp.getOperator(), CoreMatchers.is(POW));
        Assert.assertThat(exp.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(exp.getRight().getText(), CoreMatchers.is("5 * 3"));
        InfixOpNode mult = ((InfixOpNode) (exp.getRight()));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("5"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("3"));
    }

    @Test
    public void testAdd1() {
        String inputExpression = "y + 5 * 3";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode add = ((InfixOpNode) (infix));
        Assert.assertThat(add.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(add.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(add.getOperator(), CoreMatchers.is(ADD));
        Assert.assertThat(add.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(add.getRight().getText(), CoreMatchers.is("5 * 3"));
        InfixOpNode mult = ((InfixOpNode) (add.getRight()));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("5"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(MULT));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("3"));
    }

    @Test
    public void testSub1() {
        String inputExpression = "(y - 5) ** 3";
        BaseNode infix = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("y", NUMBER)));
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode sub = ((InfixOpNode) (infix));
        Assert.assertThat(sub.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(sub.getLeft().getText(), CoreMatchers.is("y - 5"));
        Assert.assertThat(sub.getOperator(), CoreMatchers.is(POW));
        Assert.assertThat(sub.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(sub.getRight().getText(), CoreMatchers.is("3"));
        InfixOpNode mult = ((InfixOpNode) (sub.getLeft()));
        Assert.assertThat(mult.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(mult.getLeft().getText(), CoreMatchers.is("y"));
        Assert.assertThat(mult.getOperator(), CoreMatchers.is(SUB));
        Assert.assertThat(mult.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(mult.getRight().getText(), CoreMatchers.is("5"));
    }

    @Test
    public void testBetween() {
        String inputExpression = "x between 10+y and 3**z";
        BaseNode between = parse(inputExpression);
        Assert.assertThat(between, CoreMatchers.is(CoreMatchers.instanceOf(BetweenNode.class)));
        Assert.assertThat(between.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(between.getText(), CoreMatchers.is(inputExpression));
        BetweenNode btw = ((BetweenNode) (between));
        Assert.assertThat(btw.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(btw.getValue().getText(), CoreMatchers.is("x"));
        Assert.assertThat(btw.getStart(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(btw.getStart().getText(), CoreMatchers.is("10+y"));
        Assert.assertThat(btw.getEnd(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(btw.getEnd().getText(), CoreMatchers.is("3**z"));
    }

    @Test
    public void testInValueList() {
        // TODO review this test might be wrong as list is not homogeneous
        String inputExpression = "x / 4 in ( 10+y, true, 80, someVar )";
        BaseNode inNode = parse(inputExpression);
        Assert.assertThat(inNode, CoreMatchers.is(CoreMatchers.instanceOf(InNode.class)));
        Assert.assertThat(inNode.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(inNode.getText(), CoreMatchers.is(inputExpression));
        InNode in = ((InNode) (inNode));
        Assert.assertThat(in.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(in.getValue().getText(), CoreMatchers.is("x / 4"));
        Assert.assertThat(in.getExprs(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(in.getExprs().getText(), CoreMatchers.is("10+y, true, 80, someVar"));
        ListNode list = ((ListNode) (in.getExprs()));
        Assert.assertThat(list.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(list.getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(BooleanNode.class)));
        Assert.assertThat(list.getElements().get(2), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(list.getElements().get(3), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
    }

    @Test
    public void testInUnaryTestList() {
        String inputExpression = "x ** y in ( <=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b)) )";
        BaseNode inNode = parse(inputExpression);
        Assert.assertThat(inNode, CoreMatchers.is(CoreMatchers.instanceOf(InNode.class)));
        Assert.assertThat(inNode.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(inNode.getText(), CoreMatchers.is(inputExpression));
        InNode in = ((InNode) (inNode));
        Assert.assertThat(in.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(in.getValue().getText(), CoreMatchers.is("x ** y"));
        Assert.assertThat(in.getExprs(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(in.getExprs().getText(), CoreMatchers.is("<=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b))"));
        ListNode list = ((ListNode) (in.getExprs()));
        Assert.assertThat(list.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(UnaryTestNode.class)));
        Assert.assertThat(list.getElements().get(0).getText(), CoreMatchers.is("<=1000"));
        Assert.assertThat(list.getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(UnaryTestNode.class)));
        Assert.assertThat(list.getElements().get(1).getText(), CoreMatchers.is(">t"));
        Assert.assertThat(list.getElements().get(2), CoreMatchers.is(CoreMatchers.instanceOf(NullNode.class)));
        Assert.assertThat(list.getElements().get(2).getText(), CoreMatchers.is("null"));
        Assert.assertThat(list.getElements().get(3), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        RangeNode interval = ((RangeNode) (list.getElements().get(3)));
        Assert.assertThat(interval.getText(), CoreMatchers.is("(2000..z["));
        Assert.assertThat(interval.getLowerBound(), CoreMatchers.is(OPEN));
        Assert.assertThat(interval.getUpperBound(), CoreMatchers.is(OPEN));
        Assert.assertThat(interval.getStart(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(interval.getEnd(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(list.getElements().get(4), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        interval = ((RangeNode) (list.getElements().get(4)));
        Assert.assertThat(interval.getText(), CoreMatchers.is("]z..2000]"));
        Assert.assertThat(interval.getLowerBound(), CoreMatchers.is(OPEN));
        Assert.assertThat(interval.getUpperBound(), CoreMatchers.is(CLOSED));
        Assert.assertThat(interval.getStart(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(interval.getEnd(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(list.getElements().get(5), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        interval = ((RangeNode) (list.getElements().get(5)));
        Assert.assertThat(interval.getText(), CoreMatchers.is("[(10+5)..(a*b))"));
        Assert.assertThat(interval.getLowerBound(), CoreMatchers.is(CLOSED));
        Assert.assertThat(interval.getUpperBound(), CoreMatchers.is(OPEN));
        Assert.assertThat(interval.getStart(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(interval.getEnd(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
    }

    @Test
    public void testInUnaryTest() {
        String inputExpression = "x - y in [(10+5)..(a*b))";
        BaseNode inNode = parse(inputExpression);
        Assert.assertThat(inNode, CoreMatchers.is(CoreMatchers.instanceOf(InNode.class)));
        Assert.assertThat(inNode.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(inNode.getText(), CoreMatchers.is(inputExpression));
        InNode in = ((InNode) (inNode));
        Assert.assertThat(in.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(in.getValue().getText(), CoreMatchers.is("x - y"));
        Assert.assertThat(in.getExprs(), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        Assert.assertThat(in.getExprs().getText(), CoreMatchers.is("[(10+5)..(a*b))"));
    }

    @Test
    public void testInUnaryTestStrings() {
        final String inputExpression = "name in [\"A\"..\"Z...\")";
        final BaseNode inNode = parse(inputExpression);
        Assert.assertThat(inNode, CoreMatchers.is(CoreMatchers.instanceOf(InNode.class)));
        Assert.assertThat(inNode.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(inNode.getText(), CoreMatchers.is(inputExpression));
        final InNode in = ((InNode) (inNode));
        Assert.assertThat(in.getExprs(), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        final RangeNode range = ((RangeNode) (in.getExprs()));
        Assert.assertThat(range.getStart().getText(), CoreMatchers.is("\"A\""));
        Assert.assertThat(range.getEnd().getText(), CoreMatchers.is("\"Z...\""));
    }

    @Test
    public void testComparisonInFixOp() {
        String inputExpression = "foo >= bar * 10";
        BaseNode infix = parse(inputExpression);
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode in = ((InfixOpNode) (infix));
        Assert.assertThat(in.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(in.getLeft().getText(), CoreMatchers.is("foo"));
        Assert.assertThat(in.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(in.getRight().getText(), CoreMatchers.is("bar * 10"));
    }

    @Test
    public void testConditionalLogicalOp() {
        String inputExpression = "foo < 10 and bar = \"x\" or baz";
        BaseNode infix = parse(inputExpression);
        Assert.assertThat(infix, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(infix.getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(infix.getText(), CoreMatchers.is(inputExpression));
        InfixOpNode or = ((InfixOpNode) (infix));
        Assert.assertThat(or.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(or.getLeft().getText(), CoreMatchers.is("foo < 10 and bar = \"x\""));
        Assert.assertThat(or.getOperator(), CoreMatchers.is(OR));
        Assert.assertThat(or.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(or.getRight().getText(), CoreMatchers.is("baz"));
        InfixOpNode and = ((InfixOpNode) (or.getLeft()));
        Assert.assertThat(and.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(and.getLeft().getText(), CoreMatchers.is("foo < 10"));
        Assert.assertThat(and.getOperator(), CoreMatchers.is(AND));
        Assert.assertThat(and.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(and.getRight().getText(), CoreMatchers.is("bar = \"x\""));
    }

    @Test
    public void testEmptyList() {
        String inputExpression = "[]";
        BaseNode list = parse(inputExpression);
        Assert.assertThat(list, CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(list.getResultType(), CoreMatchers.is(LIST));
        Assert.assertThat(list.getText(), CoreMatchers.is(inputExpression));
        ListNode ln = ((ListNode) (list));
        Assert.assertThat(ln.getElements(), CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void testExpressionList() {
        // TODO review this test is potentially wrong as the list is not homogeneous
        String inputExpression = "[ 10, foo * bar, true ]";
        BaseNode list = parse(inputExpression);
        Assert.assertThat(list, CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(list.getResultType(), CoreMatchers.is(LIST));
        Assert.assertThat(list.getText(), CoreMatchers.is("10, foo * bar, true"));
        ListNode ln = ((ListNode) (list));
        Assert.assertThat(ln.getElements().size(), CoreMatchers.is(3));
        Assert.assertThat(ln.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(ln.getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(ln.getElements().get(2), CoreMatchers.is(CoreMatchers.instanceOf(BooleanNode.class)));
    }

    @Test
    public void testEmptyContext() {
        String inputExpression = "{}";
        BaseNode context = parse(inputExpression);
        Assert.assertThat(context, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(context.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (context));
        Assert.assertThat(ctx.getEntries(), CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void testContextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10," + (" a non-string key : foo+bar," + " a key.with + /' odd chars : [10..50] }");
        BaseNode ctxbase = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("foo", NUMBER), DynamicTypeUtils.entry("bar", NUMBER)));
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(3));
        ContextEntryNode entry = ctx.getEntries().get(0);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        StringNode nameNode = ((StringNode) (entry.getName()));
        Assert.assertThat(nameNode.getText(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(nameNode.getText(), CoreMatchers.is("\"a string key\""));// Reference String literal test, BaseNode#getText() return the FEEL equivalent expression, in this case quoted.

        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("10"));
        entry = ctx.getEntries().get(1);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getParts(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(name.getParts().size(), CoreMatchers.is(5));
        Assert.assertThat(entry.getName().getText(), CoreMatchers.is("a non-string key"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("foo+bar"));
        entry = ctx.getEntries().get(2);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getParts(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(name.getParts().size(), CoreMatchers.is(9));
        Assert.assertThat(entry.getName().getText(), CoreMatchers.is("a key.with + /' odd chars"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(RangeNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(RANGE));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("[10..50]"));
    }

    @Test
    public void testVariableWithInKeyword() {
        String inputExpression = "{ a variable with in keyword : 10, " + (" another variable : a variable with in keyword + 20, " + " another in variable : an external in variable / 2 }");
        BaseNode ctxbase = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("an external in variable", NUMBER)));
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(3));
        ContextEntryNode entry = ctx.getEntries().get(0);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getParts(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(name.getParts().size(), CoreMatchers.is(5));
        Assert.assertThat(entry.getName().getText(), CoreMatchers.is("a variable with in keyword"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("10"));
        entry = ctx.getEntries().get(1);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getParts(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(name.getParts().size(), CoreMatchers.is(2));
        Assert.assertThat(entry.getName().getText(), CoreMatchers.is("another variable"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("a variable with in keyword + 20"));
        entry = ctx.getEntries().get(2);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getParts(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(name.getParts().size(), CoreMatchers.is(3));
        Assert.assertThat(entry.getName().getText(), CoreMatchers.is("another in variable"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("an external in variable / 2"));
    }

    @Test
    public void testNestedContexts() {
        String inputExpression = "{ a value : 10," + ((((((((((" an applicant : { " + "    first name : \"Edson\", ") + "    last + name : \"Tirelli\", ") + "    full name : first name + last + name, ") + "    address : {") + "        street : \"55 broadway st\",") + "        city : \"New York\" ") + "    }, ") + "    xxx: last + name") + " } ") + "}");
        BaseNode ctxbase = parse(inputExpression);
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(2));
        ContextEntryNode entry = ctx.getEntries().get(0);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getText(), CoreMatchers.is("a value"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(NUMBER));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("10"));
        entry = ctx.getEntries().get(1);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getText(), CoreMatchers.is("an applicant"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        ContextNode applicant = ((ContextNode) (entry.getValue()));
        Assert.assertThat(applicant.getEntries().size(), CoreMatchers.is(5));
        Assert.assertThat(applicant.getEntries().get(0).getName().getText(), CoreMatchers.is("first name"));
        Assert.assertThat(applicant.getEntries().get(0).getResultType(), CoreMatchers.is(STRING));
        Assert.assertThat(applicant.getEntries().get(1).getName().getText(), CoreMatchers.is("last + name"));
        Assert.assertThat(applicant.getEntries().get(1).getResultType(), CoreMatchers.is(STRING));
        Assert.assertThat(applicant.getEntries().get(2).getName().getText(), CoreMatchers.is("full name"));
        Assert.assertThat(applicant.getEntries().get(2).getResultType(), CoreMatchers.is(STRING));
        Assert.assertThat(applicant.getEntries().get(3).getName().getText(), CoreMatchers.is("address"));
        Assert.assertThat(applicant.getEntries().get(3).getValue(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        ContextNode address = ((ContextNode) (applicant.getEntries().get(3).getValue()));
        Assert.assertThat(address.getEntries().size(), CoreMatchers.is(2));
        Assert.assertThat(address.getEntries().get(0).getName().getText(), CoreMatchers.is("street"));
        Assert.assertThat(address.getEntries().get(0).getResultType(), CoreMatchers.is(STRING));
        Assert.assertThat(address.getEntries().get(1).getName().getText(), CoreMatchers.is("city"));
        Assert.assertThat(address.getEntries().get(0).getResultType(), CoreMatchers.is(STRING));
    }

    @Test
    public void testNestedContexts2() {
        String inputExpression = "{ an applicant : { " + (((((("    home address : {" + "        street name: \"broadway st\",") + "        city : \"New York\" ") + "    } ") + " },\n ") + " street : an applicant.home address.street name \n") + "}");
        BaseNode ctxbase = parse(inputExpression);
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(2));
        ContextEntryNode entry = ctx.getEntries().get(1);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        Assert.assertThat(entry.getResultType(), CoreMatchers.is(STRING));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getText(), CoreMatchers.is("street"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        QualifiedNameNode qnn = ((QualifiedNameNode) (entry.getValue()));
        Assert.assertThat(qnn.getParts().get(0).getText(), CoreMatchers.is("an applicant"));
        Assert.assertThat(qnn.getParts().get(1).getText(), CoreMatchers.is("home address"));
        Assert.assertThat(qnn.getParts().get(2).getText(), CoreMatchers.is("street name"));
    }

    @Test
    public void testFunctionDefinition() {
        String inputExpression = "{ is minor : function( person's age ) person's age < 21 }";
        BaseNode ctxbase = parse(inputExpression);
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(1));
        ContextEntryNode entry = ctx.getEntries().get(0);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getText(), CoreMatchers.is("is minor"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(FunctionDefNode.class)));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is("function( person's age ) person's age < 21"));
        FunctionDefNode isMinorFunc = ((FunctionDefNode) (entry.getValue()));
        Assert.assertThat(isMinorFunc.getFormalParameters().size(), CoreMatchers.is(1));
        Assert.assertThat(isMinorFunc.getFormalParameters().get(0).getText(), CoreMatchers.is("person's age"));
        Assert.assertThat(isMinorFunc.isExternal(), CoreMatchers.is(false));
        Assert.assertThat(isMinorFunc.getBody(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
    }

    @Test
    public void testExternalFunctionDefinition() {
        String inputExpression = "{ trigonometric cosine : function( angle ) external {" + (((("    java : {" + "        class : \"java.lang.Math\",") + "        method signature : \"cos(double)\"") + "    }") + "}}");
        BaseNode ctxbase = parse(inputExpression);
        Assert.assertThat(ctxbase, CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(ctxbase.getText(), CoreMatchers.is(inputExpression));
        ContextNode ctx = ((ContextNode) (ctxbase));
        Assert.assertThat(ctx.getEntries().size(), CoreMatchers.is(1));
        ContextEntryNode entry = ctx.getEntries().get(0);
        Assert.assertThat(entry.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameDefNode.class)));
        NameDefNode name = ((NameDefNode) (entry.getName()));
        Assert.assertThat(name.getText(), CoreMatchers.is("trigonometric cosine"));
        Assert.assertThat(entry.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(FunctionDefNode.class)));
        Assert.assertThat(entry.getValue().getText(), CoreMatchers.is(("function( angle ) external {" + (((("    java : {" + "        class : \"java.lang.Math\",") + "        method signature : \"cos(double)\"") + "    }") + "}"))));
        FunctionDefNode cos = ((FunctionDefNode) (entry.getValue()));
        Assert.assertThat(cos.getFormalParameters().size(), CoreMatchers.is(1));
        Assert.assertThat(cos.getFormalParameters().get(0).getText(), CoreMatchers.is("angle"));
        Assert.assertThat(cos.isExternal(), CoreMatchers.is(true));
        Assert.assertThat(cos.getBody(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        ContextNode body = ((ContextNode) (cos.getBody()));
        Assert.assertThat(body.getEntries().size(), CoreMatchers.is(1));
        ContextEntryNode java = body.getEntries().get(0);
        Assert.assertThat(java.getName().getText(), CoreMatchers.is("java"));
        Assert.assertThat(java.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        ContextNode def = ((ContextNode) (java.getValue()));
        Assert.assertThat(def.getEntries().size(), CoreMatchers.is(2));
        Assert.assertThat(def.getEntries().get(0).getName().getText(), CoreMatchers.is("class"));
        Assert.assertThat(def.getEntries().get(0).getValue(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(def.getEntries().get(0).getValue().getText(), CoreMatchers.is("\"java.lang.Math\""));
        Assert.assertThat(def.getEntries().get(1).getName().getText(), CoreMatchers.is("method signature"));
        Assert.assertThat(def.getEntries().get(1).getValue(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(def.getEntries().get(1).getValue().getText(), CoreMatchers.is("\"cos(double)\""));
    }

    @Test
    public void testForExpression() {
        String inputExpression = "for item in order.items return item.price * item.quantity";
        BaseNode forbase = parse(inputExpression);
        Assert.assertThat(forbase, CoreMatchers.is(CoreMatchers.instanceOf(ForExpressionNode.class)));
        Assert.assertThat(forbase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(forbase.getResultType(), CoreMatchers.is(LIST));
        ForExpressionNode forExpr = ((ForExpressionNode) (forbase));
        Assert.assertThat(forExpr.getIterationContexts().size(), CoreMatchers.is(1));
        Assert.assertThat(forExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(forExpr.getExpression().getText(), CoreMatchers.is("item.price * item.quantity"));
        IterationContextNode ic = forExpr.getIterationContexts().get(0);
        Assert.assertThat(ic.getName().getText(), CoreMatchers.is("item"));
        Assert.assertThat(ic.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(ic.getExpression().getText(), CoreMatchers.is("order.items"));
    }

    @Test
    public void testIfExpression() {
        String inputExpression = "if applicant.age < 18 then \"declined\" else \"accepted\"";
        BaseNode ifBase = parse(inputExpression);
        Assert.assertThat(ifBase, CoreMatchers.is(CoreMatchers.instanceOf(IfExpressionNode.class)));
        Assert.assertThat(ifBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(ifBase.getResultType(), CoreMatchers.is(STRING));
        IfExpressionNode ifExpr = ((IfExpressionNode) (ifBase));
        Assert.assertThat(ifExpr.getCondition().getText(), CoreMatchers.is("applicant.age < 18"));
        Assert.assertThat(ifExpr.getThenExpression().getText(), CoreMatchers.is("\"declined\""));
        Assert.assertThat(ifExpr.getElseExpression().getText(), CoreMatchers.is("\"accepted\""));
    }

    @Test
    public void testQuantifiedExpressionSome() {
        String inputExpression = "some item in order.items satisfies item.price > 100";
        BaseNode someBase = parse(inputExpression);
        Assert.assertThat(someBase, CoreMatchers.is(CoreMatchers.instanceOf(QuantifiedExpressionNode.class)));
        Assert.assertThat(someBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(someBase.getResultType(), CoreMatchers.is(BOOLEAN));
        QuantifiedExpressionNode someExpr = ((QuantifiedExpressionNode) (someBase));
        Assert.assertThat(someExpr.getQuantifier(), CoreMatchers.is(SOME));
        Assert.assertThat(someExpr.getIterationContexts().size(), CoreMatchers.is(1));
        Assert.assertThat(someExpr.getIterationContexts().get(0).getText(), CoreMatchers.is("item in order.items"));
        Assert.assertThat(someExpr.getExpression().getText(), CoreMatchers.is("item.price > 100"));
    }

    @Test
    public void testQuantifiedExpressionEvery() {
        String inputExpression = "every item in order.items satisfies item.price > 100";
        BaseNode everyBase = parse(inputExpression);
        Assert.assertThat(everyBase, CoreMatchers.is(CoreMatchers.instanceOf(QuantifiedExpressionNode.class)));
        Assert.assertThat(everyBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(everyBase.getResultType(), CoreMatchers.is(BOOLEAN));
        QuantifiedExpressionNode everyExpr = ((QuantifiedExpressionNode) (everyBase));
        Assert.assertThat(everyExpr.getQuantifier(), CoreMatchers.is(EVERY));
        Assert.assertThat(everyExpr.getIterationContexts().size(), CoreMatchers.is(1));
        Assert.assertThat(everyExpr.getIterationContexts().get(0).getText(), CoreMatchers.is("item in order.items"));
        Assert.assertThat(everyExpr.getExpression().getText(), CoreMatchers.is("item.price > 100"));
    }

    @Test
    public void testInstanceOfExpression() {
        String inputExpression = "\"foo\" instance of string";
        BaseNode instanceOfBase = parse(inputExpression);
        Assert.assertThat(instanceOfBase, CoreMatchers.is(CoreMatchers.instanceOf(InstanceOfNode.class)));
        Assert.assertThat(instanceOfBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(instanceOfBase.getResultType(), CoreMatchers.is(BOOLEAN));
        InstanceOfNode ioExpr = ((InstanceOfNode) (instanceOfBase));
        Assert.assertThat(ioExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(ioExpr.getExpression().getText(), CoreMatchers.is("\"foo\""));
        Assert.assertThat(ioExpr.getType(), CoreMatchers.is(CoreMatchers.instanceOf(TypeNode.class)));
        Assert.assertThat(ioExpr.getType().getText(), CoreMatchers.is("string"));
    }

    @Test
    public void testInstanceOfExpressionAnd() {
        String inputExpression = "\"foo\" instance of string and 10 instance of number";
        BaseNode andExpr = parse(inputExpression);
        Assert.assertThat(andExpr, CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(andExpr.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(andExpr.getResultType(), CoreMatchers.is(BOOLEAN));
        InfixOpNode and = ((InfixOpNode) (andExpr));
        Assert.assertThat(and.getOperator(), CoreMatchers.is(AND));
        Assert.assertThat(and.getLeft(), CoreMatchers.is(CoreMatchers.instanceOf(InstanceOfNode.class)));
        Assert.assertThat(and.getRight(), CoreMatchers.is(CoreMatchers.instanceOf(InstanceOfNode.class)));
        Assert.assertThat(and.getLeft().getText(), CoreMatchers.is("\"foo\" instance of string"));
        Assert.assertThat(and.getRight().getText(), CoreMatchers.is("10 instance of number"));
        Assert.assertThat(and.getLeft().getResultType(), CoreMatchers.is(BOOLEAN));
        Assert.assertThat(and.getRight().getResultType(), CoreMatchers.is(BOOLEAN));
        InstanceOfNode ioExpr = ((InstanceOfNode) (and.getLeft()));
        Assert.assertThat(ioExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(ioExpr.getExpression().getText(), CoreMatchers.is("\"foo\""));
        Assert.assertThat(ioExpr.getType(), CoreMatchers.is(CoreMatchers.instanceOf(TypeNode.class)));
        Assert.assertThat(ioExpr.getType().getText(), CoreMatchers.is("string"));
        ioExpr = ((InstanceOfNode) (and.getRight()));
        Assert.assertThat(ioExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NumberNode.class)));
        Assert.assertThat(ioExpr.getExpression().getText(), CoreMatchers.is("10"));
        Assert.assertThat(ioExpr.getType(), CoreMatchers.is(CoreMatchers.instanceOf(TypeNode.class)));
        Assert.assertThat(ioExpr.getType().getText(), CoreMatchers.is("number"));
    }

    @Test
    public void testInstanceOfExpressionFunction() {
        String inputExpression = "duration instance of function";
        BaseNode instanceOfBase = parse(inputExpression);
        Assert.assertThat(instanceOfBase, CoreMatchers.is(CoreMatchers.instanceOf(InstanceOfNode.class)));
        Assert.assertThat(instanceOfBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(instanceOfBase.getResultType(), CoreMatchers.is(BOOLEAN));
        InstanceOfNode ioExpr = ((InstanceOfNode) (instanceOfBase));
        Assert.assertThat(ioExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(ioExpr.getExpression().getText(), CoreMatchers.is("duration"));
        Assert.assertThat(ioExpr.getType(), CoreMatchers.is(CoreMatchers.instanceOf(TypeNode.class)));
        Assert.assertThat(ioExpr.getType().getText(), CoreMatchers.is("function"));
    }

    @Test
    public void testPathExpression() {
        String inputExpression = "[ 10, 15 ].size";
        BaseNode pathBase = parse(inputExpression);
        Assert.assertThat(pathBase, CoreMatchers.is(CoreMatchers.instanceOf(PathExpressionNode.class)));
        Assert.assertThat(pathBase.getText(), CoreMatchers.is(inputExpression));
        PathExpressionNode pathExpr = ((PathExpressionNode) (pathBase));
        Assert.assertThat(pathExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(pathExpr.getExpression().getText(), CoreMatchers.is("10, 15"));
        Assert.assertThat(pathExpr.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(pathExpr.getName().getText(), CoreMatchers.is("size"));
    }

    @Test
    public void testFilterExpression() {
        String inputExpression = "[ {x:1, y:2}, {x:2, y:3} ][ x=1 ]";
        BaseNode filterBase = parse(inputExpression);
        Assert.assertThat(filterBase, CoreMatchers.is(CoreMatchers.instanceOf(FilterExpressionNode.class)));
        Assert.assertThat(filterBase.getText(), CoreMatchers.is(inputExpression));
        FilterExpressionNode filter = ((FilterExpressionNode) (filterBase));
        Assert.assertThat(filter.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(filter.getExpression().getText(), CoreMatchers.is("{x:1, y:2}, {x:2, y:3}"));
        Assert.assertThat(filter.getFilter(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(filter.getFilter().getText(), CoreMatchers.is("x=1"));
    }

    @Test
    public void testFunctionInvocationNamedParams() {
        String inputExpression = "my.test.Function( named parameter 1 : x+10, named parameter 2 : \"foo\" )";
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("my.test.Function"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements().size(), CoreMatchers.is(2));
        Assert.assertThat(function.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        Assert.assertThat(function.getParams().getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        NamedParameterNode named = ((NamedParameterNode) (function.getParams().getElements().get(0)));
        Assert.assertThat(named.getText(), CoreMatchers.is("named parameter 1 : x+10"));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("named parameter 1"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(named.getExpression().getText(), CoreMatchers.is("x+10"));
        named = ((NamedParameterNode) (function.getParams().getElements().get(1)));
        Assert.assertThat(named.getText(), CoreMatchers.is("named parameter 2 : \"foo\""));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("named parameter 2"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(named.getExpression().getText(), CoreMatchers.is("\"foo\""));
    }

    @Test
    public void testFunctionInvocationPositionalParams() {
        String inputExpression = "my.test.Function( x+10, \"foo\" )";
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("my.test.Function"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements().size(), CoreMatchers.is(2));
        Assert.assertThat(function.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(InfixOpNode.class)));
        Assert.assertThat(function.getParams().getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
    }

    @Test
    public void testFunctionInvocationWithKeyword() {
        String inputExpression = "date and time( \"2016-07-29T19:47:53\" )";
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("date and time"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements().size(), CoreMatchers.is(1));
        Assert.assertThat(function.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
    }

    @Test
    public void testFunctionInvocationWithExpressionParameters() {
        String inputExpression = "date and time( date(\"2016-07-29\"), time(\"19:47:53\") )";
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("date and time"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements().size(), CoreMatchers.is(2));
        Assert.assertThat(function.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(function.getParams().getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
    }

    @Test
    public void testFunctionInvocationEmptyParams() {
        String inputExpression = "my.test.Function()";
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("my.test.Function"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements(), CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void testFunctionDecisionTableInvocation() {
        String inputExpression = "decision table( " + (((((((("    outputs: \"Applicant Risk Rating\"," + "    input expression list: [\"Applicant Age\", \"Medical History\"],") + "    rule list: [") + "        [ >60      , \"good\" , \"Medium\" ],") + "        [ >60      , \"bad\"  , \"High\"   ],") + "        [ [25..60] , -        , \"Medium\" ],") + "        [ <25      , \"good\" , \"Low\"    ],") + "        [ <25      , \"bad\"  , \"Medium\" ] ],") + "    hit policy: \"Unique\" )");
        // need to call parse passing in the input variables
        BaseNode functionBase = parse(inputExpression);
        Assert.assertThat(functionBase, CoreMatchers.is(CoreMatchers.instanceOf(FunctionInvocationNode.class)));
        Assert.assertThat(functionBase.getText(), CoreMatchers.is(inputExpression));
        FunctionInvocationNode function = ((FunctionInvocationNode) (functionBase));
        Assert.assertThat(function.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(function.getName().getText(), CoreMatchers.is("decision table"));
        Assert.assertThat(function.getParams(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        Assert.assertThat(function.getParams().getElements().size(), CoreMatchers.is(4));
        Assert.assertThat(function.getParams().getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        Assert.assertThat(function.getParams().getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        Assert.assertThat(function.getParams().getElements().get(2), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        Assert.assertThat(function.getParams().getElements().get(3), CoreMatchers.is(CoreMatchers.instanceOf(NamedParameterNode.class)));
        NamedParameterNode named = ((NamedParameterNode) (function.getParams().getElements().get(0)));
        Assert.assertThat(named.getText(), CoreMatchers.is("outputs: \"Applicant Risk Rating\""));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("outputs"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(named.getExpression().getText(), CoreMatchers.is("\"Applicant Risk Rating\""));
        named = ((NamedParameterNode) (function.getParams().getElements().get(1)));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("input expression list"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        ListNode list = ((ListNode) (named.getExpression()));
        Assert.assertThat(list.getElements().size(), CoreMatchers.is(2));
        Assert.assertThat(list.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(list.getElements().get(0).getText(), CoreMatchers.is("\"Applicant Age\""));
        Assert.assertThat(list.getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(list.getElements().get(1).getText(), CoreMatchers.is("\"Medical History\""));
        named = ((NamedParameterNode) (function.getParams().getElements().get(2)));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("rule list"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        list = ((ListNode) (named.getExpression()));
        Assert.assertThat(list.getElements().size(), CoreMatchers.is(5));
        Assert.assertThat(list.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(ListNode.class)));
        ListNode rule = ((ListNode) (list.getElements().get(0)));
        Assert.assertThat(rule.getElements().size(), CoreMatchers.is(3));
        Assert.assertThat(rule.getElements().get(0), CoreMatchers.is(CoreMatchers.instanceOf(UnaryTestNode.class)));
        Assert.assertThat(rule.getElements().get(0).getText(), CoreMatchers.is(">60"));
        Assert.assertThat(rule.getElements().get(1), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(rule.getElements().get(1).getText(), CoreMatchers.is("\"good\""));
        Assert.assertThat(rule.getElements().get(2), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(rule.getElements().get(2).getText(), CoreMatchers.is("\"Medium\""));
        named = ((NamedParameterNode) (function.getParams().getElements().get(3)));
        Assert.assertThat(named.getName().getText(), CoreMatchers.is("hit policy"));
        Assert.assertThat(named.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(StringNode.class)));
        Assert.assertThat(named.getExpression().getText(), CoreMatchers.is("\"Unique\""));
    }

    @Test
    public void testContextPathExpression() {
        String inputExpression = "{ x : \"foo\" }.x";
        BaseNode pathBase = parse(inputExpression);
        Assert.assertThat(pathBase, CoreMatchers.is(CoreMatchers.instanceOf(PathExpressionNode.class)));
        Assert.assertThat(pathBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(pathBase.getResultType(), CoreMatchers.is(STRING));
        PathExpressionNode pathExpr = ((PathExpressionNode) (pathBase));
        Assert.assertThat(pathExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(pathExpr.getExpression().getText(), CoreMatchers.is("{ x : \"foo\" }"));
        Assert.assertThat(pathExpr.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(pathExpr.getName().getText(), CoreMatchers.is("x"));
    }

    @Test
    public void testContextPathExpression2() {
        String inputExpression = "{ x : { y : \"foo\" } }.x.y";
        BaseNode pathBase = parse(inputExpression);
        Assert.assertThat(pathBase, CoreMatchers.is(CoreMatchers.instanceOf(PathExpressionNode.class)));
        Assert.assertThat(pathBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(pathBase.getResultType(), CoreMatchers.is(STRING));
        PathExpressionNode pathExpr = ((PathExpressionNode) (pathBase));
        Assert.assertThat(pathExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(pathExpr.getExpression().getText(), CoreMatchers.is("{ x : { y : \"foo\" } }"));
        Assert.assertThat(pathExpr.getName(), CoreMatchers.is(CoreMatchers.instanceOf(QualifiedNameNode.class)));
        Assert.assertThat(pathExpr.getName().getText(), CoreMatchers.is("x.y"));
    }

    @Test
    public void testContextPathExpression3() {
        String inputExpression = "{ first name : \"bob\" }.first name";
        BaseNode pathBase = parse(inputExpression);
        Assert.assertThat(pathBase, CoreMatchers.is(CoreMatchers.instanceOf(PathExpressionNode.class)));
        Assert.assertThat(pathBase.getText(), CoreMatchers.is(inputExpression));
        Assert.assertThat(pathBase.getResultType(), CoreMatchers.is(STRING));
        PathExpressionNode pathExpr = ((PathExpressionNode) (pathBase));
        Assert.assertThat(pathExpr.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(ContextNode.class)));
        Assert.assertThat(pathExpr.getExpression().getText(), CoreMatchers.is("{ first name : \"bob\" }"));
        Assert.assertThat(pathExpr.getName(), CoreMatchers.is(CoreMatchers.instanceOf(NameRefNode.class)));
        Assert.assertThat(pathExpr.getName().getText(), CoreMatchers.is("first name"));
    }

    @Test
    public void testVariableName() {
        String var = "valid variable name";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(true));
    }

    @Test
    public void testVariableNameWithValidCharacters() {
        String var = "?_873./-'+*valid";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(true));
    }

    @Test
    public void testVariableNameWithInvalidCharacterPercent() {
        String var = "?_873./-'%+*valid";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(false));
        Assert.assertThat(FEELParser.checkVariableName(var).get(0).getMessage(), CoreMatchers.is(Msg.createMessage(INVALID_VARIABLE_NAME, "character", "%")));
    }

    @Test
    public void testVariableNameWithInvalidCharacterAt() {
        String var = "?_873./-'@+*valid";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(false));
        Assert.assertThat(FEELParser.checkVariableName(var).get(0).getMessage(), CoreMatchers.is(Msg.createMessage(INVALID_VARIABLE_NAME, "character", "@")));
    }

    @Test
    public void testVariableNameInvalidStartCharacter() {
        String var = "5variable can't start with a number";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(false));
        Assert.assertThat(FEELParser.checkVariableName(var).get(0).getMessage(), CoreMatchers.is(Msg.createMessage(INVALID_VARIABLE_NAME_START, "character", "5")));
    }

    @Test
    public void testVariableNameCantStartWithKeyword() {
        String var = "for keyword is an invalid start for a variable name";
        Assert.assertThat(FEELParser.isVariableNameValid(var), CoreMatchers.is(false));
        Assert.assertThat(FEELParser.checkVariableName(var).get(0).getMessage(), CoreMatchers.is(Msg.createMessage(INVALID_VARIABLE_NAME_START, "keyword", "for")));
    }
}

