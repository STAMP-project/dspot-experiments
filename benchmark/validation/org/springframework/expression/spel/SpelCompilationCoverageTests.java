/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.expression.spel;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.standard.SpelCompiler;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.testdata.PersonInOtherPackage;

import static SpelCompilerMode.IMMEDIATE;
import static SpelCompilerMode.MIXED;
import static SpelCompilerMode.OFF;


/**
 * Checks SpelCompiler behavior. This should cover compilation all compiled node types.
 *
 * @author Andy Clement
 * @since 4.1
 */
public class SpelCompilationCoverageTests extends AbstractExpressionTests {
    /* Further TODOs for compilation:

    - OpMinus with a single literal operand could be treated as a negative literal. Will save a
      pointless loading of 0 and then a subtract instruction in code gen.
    - allow other accessors/resolvers to participate in compilation and create their own code
    - A TypeReference followed by (what ends up as) a static method invocation can really skip
      code gen for the TypeReference since once that is used to locate the method it is not
      used again.
    - The opEq implementation is quite basic. It will compare numbers of the same type (allowing
      them to be their boxed or unboxed variants) or compare object references. It does not
      compile expressions where numbers are of different types or when objects implement
      Comparable.

    Compiled nodes:

    TypeReference
    OperatorInstanceOf
    StringLiteral
    NullLiteral
    RealLiteral
    IntLiteral
    LongLiteral
    BooleanLiteral
    FloatLiteral
    OpOr
    OpAnd
    OperatorNot
    Ternary
    Elvis
    VariableReference
    OpLt
    OpLe
    OpGt
    OpGe
    OpEq
    OpNe
    OpPlus
    OpMinus
    OpMultiply
    OpDivide
    MethodReference
    PropertyOrFieldReference
    Indexer
    CompoundExpression
    ConstructorReference
    FunctionReference
    InlineList
    OpModulus

    Not yet compiled (some may never need to be):
    Assign
    BeanReference
    Identifier
    OpDec
    OpBetween
    OpMatches
    OpPower
    OpInc
    Projection
    QualifiedId
    Selection
     */
    private Expression expression;

    private SpelNodeImpl ast;

    @Test
    public void typeReference() throws Exception {
        expression = parse("T(String)");
        Assert.assertEquals(String.class, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(String.class, expression.getValue());
        expression = parse("T(java.io.IOException)");
        Assert.assertEquals(IOException.class, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(IOException.class, expression.getValue());
        expression = parse("T(java.io.IOException[])");
        Assert.assertEquals(IOException[].class, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(IOException[].class, expression.getValue());
        expression = parse("T(int[][])");
        Assert.assertEquals(int[][].class, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(int[][].class, expression.getValue());
        expression = parse("T(int)");
        Assert.assertEquals(Integer.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Integer.TYPE, expression.getValue());
        expression = parse("T(byte)");
        Assert.assertEquals(Byte.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Byte.TYPE, expression.getValue());
        expression = parse("T(char)");
        Assert.assertEquals(Character.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Character.TYPE, expression.getValue());
        expression = parse("T(short)");
        Assert.assertEquals(Short.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Short.TYPE, expression.getValue());
        expression = parse("T(long)");
        Assert.assertEquals(Long.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Long.TYPE, expression.getValue());
        expression = parse("T(float)");
        Assert.assertEquals(Float.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Float.TYPE, expression.getValue());
        expression = parse("T(double)");
        Assert.assertEquals(Double.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Double.TYPE, expression.getValue());
        expression = parse("T(boolean)");
        Assert.assertEquals(Boolean.TYPE, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(Boolean.TYPE, expression.getValue());
        expression = parse("T(Missing)");
        assertGetValueFail(expression);
        assertCantCompile(expression);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void operatorInstanceOf() throws Exception {
        expression = parse("'xyz' instanceof T(String)");
        Assert.assertEquals(true, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue());
        expression = parse("'xyz' instanceof T(Integer)");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
        List<String> list = new ArrayList<>();
        expression = parse("#root instanceof T(java.util.List)");
        Assert.assertEquals(true, expression.getValue(list));
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue(list));
        List<String>[] arrayOfLists = new List[]{ new ArrayList<String>() };
        expression = parse("#root instanceof T(java.util.List[])");
        Assert.assertEquals(true, expression.getValue(arrayOfLists));
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue(arrayOfLists));
        int[] intArray = new int[]{ 1, 2, 3 };
        expression = parse("#root instanceof T(int[])");
        Assert.assertEquals(true, expression.getValue(intArray));
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue(intArray));
        String root = null;
        expression = parse("#root instanceof T(Integer)");
        Assert.assertEquals(false, expression.getValue(root));
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue(root));
        // root still null
        expression = parse("#root instanceof T(java.lang.Object)");
        Assert.assertEquals(false, expression.getValue(root));
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue(root));
        root = "howdy!";
        expression = parse("#root instanceof T(java.lang.Object)");
        Assert.assertEquals(true, expression.getValue(root));
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue(root));
    }

    @Test
    public void operatorInstanceOf_SPR14250() throws Exception {
        // primitive left operand - should get boxed, return true
        expression = parse("3 instanceof T(Integer)");
        Assert.assertEquals(true, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue());
        // primitive left operand - should get boxed, return false
        expression = parse("3 instanceof T(String)");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
        // double slot left operand - should get boxed, return false
        expression = parse("3.0d instanceof T(Integer)");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
        // double slot left operand - should get boxed, return true
        expression = parse("3.0d instanceof T(Double)");
        Assert.assertEquals(true, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue());
        // Only when the right hand operand is a direct type reference
        // will it be compilable.
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("foo", String.class);
        expression = parse("3 instanceof #foo");
        Assert.assertEquals(false, expression.getValue(ctx));
        assertCantCompile(expression);
        // use of primitive as type for instanceof check - compilable
        // but always false
        expression = parse("3 instanceof T(int)");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
        expression = parse("3 instanceof T(long)");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
    }

    @Test
    public void stringLiteral() throws Exception {
        expression = parser.parseExpression("'abcde'");
        Assert.assertEquals("abcde", expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class));
        assertCanCompile(expression);
        String resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class);
        Assert.assertEquals("abcde", resultC);
        Assert.assertEquals("abcde", expression.getValue(String.class));
        Assert.assertEquals("abcde", expression.getValue());
        Assert.assertEquals("abcde", expression.getValue(new StandardEvaluationContext()));
        expression = parser.parseExpression("\"abcde\"");
        assertCanCompile(expression);
        Assert.assertEquals("abcde", expression.getValue(String.class));
    }

    @Test
    public void nullLiteral() throws Exception {
        expression = parser.parseExpression("null");
        Object resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Object.class);
        assertCanCompile(expression);
        Object resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Object.class);
        Assert.assertEquals(null, resultI);
        Assert.assertEquals(null, resultC);
        Assert.assertEquals(null, resultC);
    }

    @Test
    public void realLiteral() throws Exception {
        expression = parser.parseExpression("3.4d");
        double resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Double.TYPE);
        assertCanCompile(expression);
        double resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Double.TYPE);
        Assert.assertEquals(3.4, resultI, 0.1);
        Assert.assertEquals(3.4, resultC, 0.1);
        Assert.assertEquals(3.4, expression.getValue());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void inlineList() throws Exception {
        expression = parser.parseExpression("'abcde'.substring({1,3,4}[0])");
        Object o = expression.getValue();
        Assert.assertEquals("bcde", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        expression = parser.parseExpression("{'abc','def'}");
        List<?> l = ((List) (expression.getValue()));
        Assert.assertEquals("[abc, def]", l.toString());
        assertCanCompile(expression);
        l = ((List) (expression.getValue()));
        Assert.assertEquals("[abc, def]", l.toString());
        expression = parser.parseExpression("{'abc','def'}[0]");
        o = expression.getValue();
        Assert.assertEquals("abc", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("abc", o);
        expression = parser.parseExpression("{'abcde','ijklm'}[0].substring({1,3,4}[0])");
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        expression = parser.parseExpression("{'abcde','ijklm'}[0].substring({1,3,4}[0],{1,3,4}[1])");
        o = expression.getValue();
        Assert.assertEquals("bc", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("bc", o);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void nestedInlineLists() throws Exception {
        Object o = null;
        expression = parser.parseExpression("{{1,2,3},{4,5,6},{7,8,9}}");
        o = expression.getValue();
        Assert.assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", o.toString());
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", o.toString());
        expression = parser.parseExpression("{{1,2,3},{4,5,6},{7,8,9}}.toString()");
        o = expression.getValue();
        Assert.assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", o);
        expression = parser.parseExpression("{{1,2,3},{4,5,6},{7,8,9}}[1][0]");
        o = expression.getValue();
        Assert.assertEquals(4, o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals(4, o);
        expression = parser.parseExpression("{{1,2,3},'abc',{7,8,9}}[1]");
        o = expression.getValue();
        Assert.assertEquals("abc", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("abc", o);
        expression = parser.parseExpression("'abcde'.substring({{1,3},1,3,4}[0][1])");
        o = expression.getValue();
        Assert.assertEquals("de", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("de", o);
        expression = parser.parseExpression("'abcde'.substring({{1,3},1,3,4}[1])");
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        expression = parser.parseExpression("{'abc',{'def','ghi'}}");
        List<?> l = ((List) (expression.getValue()));
        Assert.assertEquals("[abc, [def, ghi]]", l.toString());
        assertCanCompile(expression);
        l = ((List) (expression.getValue()));
        Assert.assertEquals("[abc, [def, ghi]]", l.toString());
        expression = parser.parseExpression("{'abcde',{'ijklm','nopqr'}}[0].substring({1,3,4}[0])");
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("bcde", o);
        expression = parser.parseExpression("{'abcde',{'ijklm','nopqr'}}[1][0].substring({1,3,4}[0])");
        o = expression.getValue();
        Assert.assertEquals("jklm", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("jklm", o);
        expression = parser.parseExpression("{'abcde',{'ijklm','nopqr'}}[1][1].substring({1,3,4}[0],{1,3,4}[1])");
        o = expression.getValue();
        Assert.assertEquals("op", o);
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals("op", o);
    }

    @Test
    public void intLiteral() throws Exception {
        expression = parser.parseExpression("42");
        int resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Integer.TYPE);
        assertCanCompile(expression);
        int resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Integer.TYPE);
        Assert.assertEquals(42, resultI);
        Assert.assertEquals(42, resultC);
        expression = parser.parseExpression("T(Integer).valueOf(42)");
        expression.getValue(Integer.class);
        assertCanCompile(expression);
        Assert.assertEquals(new Integer(42), expression.getValue(Integer.class));
        // Code gen is different for -1 .. 6 because there are bytecode instructions specifically for those values
        // Not an int literal but an opminus with one operand:
        // expression = parser.parseExpression("-1");
        // assertCanCompile(expression);
        // assertEquals(-1, expression.getValue());
        expression = parser.parseExpression("0");
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue());
        expression = parser.parseExpression("2");
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue());
        expression = parser.parseExpression("7");
        assertCanCompile(expression);
        Assert.assertEquals(7, expression.getValue());
    }

    @Test
    public void longLiteral() throws Exception {
        expression = parser.parseExpression("99L");
        long resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Long.TYPE);
        assertCanCompile(expression);
        long resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Long.TYPE);
        Assert.assertEquals(99L, resultI);
        Assert.assertEquals(99L, resultC);
    }

    @Test
    public void booleanLiteral() throws Exception {
        expression = parser.parseExpression("true");
        boolean resultI = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertTrue(SpelCompiler.compile(expression));
        boolean resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultC);
        expression = parser.parseExpression("false");
        resultI = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultI);
        Assert.assertTrue(SpelCompiler.compile(expression));
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultC);
    }

    @Test
    public void floatLiteral() throws Exception {
        expression = parser.parseExpression("3.4f");
        float resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Float.TYPE);
        assertCanCompile(expression);
        float resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Float.TYPE);
        Assert.assertEquals(3.4F, resultI, 0.1F);
        Assert.assertEquals(3.4F, resultC, 0.1F);
        Assert.assertEquals(3.4F, expression.getValue());
    }

    @Test
    public void opOr() throws Exception {
        Expression expression = parser.parseExpression("false or false");
        boolean resultI = expression.getValue(1, Boolean.TYPE);
        SpelCompiler.compile(expression);
        boolean resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultI);
        Assert.assertEquals(false, resultC);
        expression = parser.parseExpression("false or true");
        resultI = expression.getValue(1, Boolean.TYPE);
        assertCanCompile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertEquals(true, resultC);
        expression = parser.parseExpression("true or false");
        resultI = expression.getValue(1, Boolean.TYPE);
        assertCanCompile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertEquals(true, resultC);
        expression = parser.parseExpression("true or true");
        resultI = expression.getValue(1, Boolean.TYPE);
        assertCanCompile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertEquals(true, resultC);
        SpelCompilationCoverageTests.TestClass4 tc = new SpelCompilationCoverageTests.TestClass4();
        expression = parser.parseExpression("getfalse() or gettrue()");
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCanCompile(expression);
        resultC = expression.getValue(tc, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertEquals(true, resultC);
        // Can't compile this as we aren't going down the getfalse() branch in our evaluation
        expression = parser.parseExpression("gettrue() or getfalse()");
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCantCompile(expression);
        expression = parser.parseExpression("getA() or getB()");
        tc.a = true;
        tc.b = true;
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCantCompile(expression);// Haven't yet been into second branch

        tc.a = false;
        tc.b = true;
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCanCompile(expression);// Now been down both

        Assert.assertTrue(resultI);
        boolean b = false;
        expression = parse("#root or #root");
        Object resultI2 = expression.getValue(b);
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (resultI2)));
        Assert.assertFalse(((Boolean) (expression.getValue(b))));
    }

    @Test
    public void opAnd() throws Exception {
        Expression expression = parser.parseExpression("false and false");
        boolean resultI = expression.getValue(1, Boolean.TYPE);
        SpelCompiler.compile(expression);
        boolean resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultI);
        Assert.assertEquals(false, resultC);
        expression = parser.parseExpression("false and true");
        resultI = expression.getValue(1, Boolean.TYPE);
        SpelCompiler.compile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultI);
        Assert.assertEquals(false, resultC);
        expression = parser.parseExpression("true and false");
        resultI = expression.getValue(1, Boolean.TYPE);
        SpelCompiler.compile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(false, resultI);
        Assert.assertEquals(false, resultC);
        expression = parser.parseExpression("true and true");
        resultI = expression.getValue(1, Boolean.TYPE);
        SpelCompiler.compile(expression);
        resultC = expression.getValue(1, Boolean.TYPE);
        Assert.assertEquals(true, resultI);
        Assert.assertEquals(true, resultC);
        SpelCompilationCoverageTests.TestClass4 tc = new SpelCompilationCoverageTests.TestClass4();
        // Can't compile this as we aren't going down the gettrue() branch in our evaluation
        expression = parser.parseExpression("getfalse() and gettrue()");
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCantCompile(expression);
        expression = parser.parseExpression("getA() and getB()");
        tc.a = false;
        tc.b = false;
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCantCompile(expression);// Haven't yet been into second branch

        tc.a = true;
        tc.b = false;
        resultI = expression.getValue(tc, Boolean.TYPE);
        assertCanCompile(expression);// Now been down both

        Assert.assertFalse(resultI);
        tc.a = true;
        tc.b = true;
        resultI = expression.getValue(tc, Boolean.TYPE);
        Assert.assertTrue(resultI);
        boolean b = true;
        expression = parse("#root and #root");
        Object resultI2 = expression.getValue(b);
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (resultI2)));
        Assert.assertTrue(((Boolean) (expression.getValue(b))));
    }

    @Test
    public void operatorNot() throws Exception {
        expression = parse("!true");
        Assert.assertEquals(false, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue());
        expression = parse("!false");
        Assert.assertEquals(true, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue());
        boolean b = true;
        expression = parse("!#root");
        Assert.assertEquals(false, expression.getValue(b));
        assertCanCompile(expression);
        Assert.assertEquals(false, expression.getValue(b));
        b = false;
        expression = parse("!#root");
        Assert.assertEquals(true, expression.getValue(b));
        assertCanCompile(expression);
        Assert.assertEquals(true, expression.getValue(b));
    }

    @Test
    public void ternary() throws Exception {
        Expression expression = parser.parseExpression("true?'a':'b'");
        String resultI = expression.getValue(String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(String.class);
        Assert.assertEquals("a", resultI);
        Assert.assertEquals("a", resultC);
        expression = parser.parseExpression("false?'a':'b'");
        resultI = expression.getValue(String.class);
        assertCanCompile(expression);
        resultC = expression.getValue(String.class);
        Assert.assertEquals("b", resultI);
        Assert.assertEquals("b", resultC);
        expression = parser.parseExpression("false?1:'b'");
        // All literals so we can do this straight away
        assertCanCompile(expression);
        Assert.assertEquals("b", expression.getValue());
        boolean root = true;
        expression = parser.parseExpression("(#root and true)?T(Integer).valueOf(1):T(Long).valueOf(3L)");
        Assert.assertEquals(1, expression.getValue(root));
        assertCantCompile(expression);// Have not gone down false branch

        root = false;
        Assert.assertEquals(3L, expression.getValue(root));
        assertCanCompile(expression);
        Assert.assertEquals(3L, expression.getValue(root));
        root = true;
        Assert.assertEquals(1, expression.getValue(root));
    }

    @Test
    public void ternaryWithBooleanReturn_SPR12271() {
        expression = parser.parseExpression("T(Boolean).TRUE?'abc':'def'");
        Assert.assertEquals("abc", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("abc", expression.getValue());
        expression = parser.parseExpression("T(Boolean).FALSE?'abc':'def'");
        Assert.assertEquals("def", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("def", expression.getValue());
    }

    @Test
    public void nullsafeFieldPropertyDereferencing_SPR16489() throws Exception {
        SpelCompilationCoverageTests.FooObjectHolder foh = new SpelCompilationCoverageTests.FooObjectHolder();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(foh);
        // First non compiled:
        SpelExpression expression = ((SpelExpression) (parser.parseExpression("foo?.object")));
        Assert.assertEquals("hello", expression.getValue(context));
        foh.foo = null;
        Assert.assertNull(expression.getValue(context));
        // Now revert state of foh and try compiling it:
        foh.foo = new SpelCompilationCoverageTests.FooObject();
        Assert.assertEquals("hello", expression.getValue(context));
        assertCanCompile(expression);
        Assert.assertEquals("hello", expression.getValue(context));
        foh.foo = null;
        Assert.assertNull(expression.getValue(context));
        // Static references
        expression = ((SpelExpression) (parser.parseExpression("#var?.propertya")));
        context.setVariable("var", SpelCompilationCoverageTests.StaticsHelper.class);
        Assert.assertEquals("sh", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", SpelCompilationCoverageTests.StaticsHelper.class);
        Assert.assertEquals("sh", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Single size primitive (boolean)
        expression = ((SpelExpression) (parser.parseExpression("#var?.a")));
        context.setVariable("var", new SpelCompilationCoverageTests.TestClass4());
        Assert.assertFalse(((Boolean) (expression.getValue(context))));
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", new SpelCompilationCoverageTests.TestClass4());
        Assert.assertFalse(((Boolean) (expression.getValue(context))));
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Double slot primitives
        expression = ((SpelExpression) (parser.parseExpression("#var?.four")));
        context.setVariable("var", new SpelCompilationCoverageTests.Three());
        Assert.assertEquals("0.04", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", new SpelCompilationCoverageTests.Three());
        Assert.assertEquals("0.04", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
    }

    @Test
    public void nullsafeMethodChaining_SPR16489() throws Exception {
        SpelCompilationCoverageTests.FooObjectHolder foh = new SpelCompilationCoverageTests.FooObjectHolder();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(foh);
        // First non compiled:
        SpelExpression expression = ((SpelExpression) (parser.parseExpression("getFoo()?.getObject()")));
        Assert.assertEquals("hello", expression.getValue(context));
        foh.foo = null;
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        foh.foo = new SpelCompilationCoverageTests.FooObject();
        Assert.assertEquals("hello", expression.getValue(context));
        foh.foo = null;
        Assert.assertNull(expression.getValue(context));
        // Static method references
        expression = ((SpelExpression) (parser.parseExpression("#var?.methoda()")));
        context.setVariable("var", SpelCompilationCoverageTests.StaticsHelper.class);
        Assert.assertEquals("sh", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", SpelCompilationCoverageTests.StaticsHelper.class);
        Assert.assertEquals("sh", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.intValue()")));
        context.setVariable("var", 4);
        Assert.assertEquals("4", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", 4);
        Assert.assertEquals("4", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.booleanValue()")));
        context.setVariable("var", false);
        Assert.assertEquals("false", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", false);
        Assert.assertEquals("false", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.booleanValue()")));
        context.setVariable("var", true);
        Assert.assertEquals("true", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", true);
        Assert.assertEquals("true", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.longValue()")));
        context.setVariable("var", 5L);
        Assert.assertEquals("5", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", 5L);
        Assert.assertEquals("5", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.floatValue()")));
        context.setVariable("var", 3.0F);
        Assert.assertEquals("3.0", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", 3.0F);
        Assert.assertEquals("3.0", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        // Nullsafe guard on expression element evaluating to primitive/null
        expression = ((SpelExpression) (parser.parseExpression("#var?.shortValue()")));
        context.setVariable("var", ((short) (8)));
        Assert.assertEquals("8", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
        assertCanCompile(expression);
        context.setVariable("var", ((short) (8)));
        Assert.assertEquals("8", expression.getValue(context).toString());
        context.setVariable("var", null);
        Assert.assertNull(expression.getValue(context));
    }

    @Test
    public void elvis() throws Exception {
        Expression expression = parser.parseExpression("'a'?:'b'");
        String resultI = expression.getValue(String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(String.class);
        Assert.assertEquals("a", resultI);
        Assert.assertEquals("a", resultC);
        expression = parser.parseExpression("null?:'a'");
        resultI = expression.getValue(String.class);
        assertCanCompile(expression);
        resultC = expression.getValue(String.class);
        Assert.assertEquals("a", resultI);
        Assert.assertEquals("a", resultC);
        String s = "abc";
        expression = parser.parseExpression("#root?:'b'");
        assertCantCompile(expression);
        resultI = expression.getValue(s, String.class);
        Assert.assertEquals("abc", resultI);
        assertCanCompile(expression);
    }

    @Test
    public void variableReference_root() throws Exception {
        String s = "hello";
        Expression expression = parser.parseExpression("#root");
        String resultI = expression.getValue(s, String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(s, String.class);
        Assert.assertEquals(s, resultI);
        Assert.assertEquals(s, resultC);
        expression = parser.parseExpression("#root");
        int i = ((Integer) (expression.getValue(42)));
        Assert.assertEquals(42, i);
        assertCanCompile(expression);
        i = ((Integer) (expression.getValue(42)));
        Assert.assertEquals(42, i);
    }

    @Test
    public void compiledExpressionShouldWorkWhenUsingCustomFunctionWithVarargs() throws Exception {
        StandardEvaluationContext context = null;
        // Here the target method takes Object... and we are passing a string
        expression = parser.parseExpression("#doFormat('hey %s', 'there')");
        context = new StandardEvaluationContext();
        context.registerFunction("doFormat", SpelCompilationCoverageTests.DelegatingStringFormat.class.getDeclaredMethod("format", String.class, Object[].class));
        ((SpelExpression) (expression)).setEvaluationContext(context);
        Assert.assertEquals("hey there", expression.getValue(String.class));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("hey there", expression.getValue(String.class));
        expression = parser.parseExpression("#doFormat([0], 'there')");
        context = new StandardEvaluationContext(new Object[]{ "hey %s" });
        context.registerFunction("doFormat", SpelCompilationCoverageTests.DelegatingStringFormat.class.getDeclaredMethod("format", String.class, Object[].class));
        ((SpelExpression) (expression)).setEvaluationContext(context);
        Assert.assertEquals("hey there", expression.getValue(String.class));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("hey there", expression.getValue(String.class));
        expression = parser.parseExpression("#doFormat([0], #arg)");
        context = new StandardEvaluationContext(new Object[]{ "hey %s" });
        context.registerFunction("doFormat", SpelCompilationCoverageTests.DelegatingStringFormat.class.getDeclaredMethod("format", String.class, Object[].class));
        context.setVariable("arg", "there");
        ((SpelExpression) (expression)).setEvaluationContext(context);
        Assert.assertEquals("hey there", expression.getValue(String.class));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("hey there", expression.getValue(String.class));
    }

    @Test
    public void functionReference() throws Exception {
        EvaluationContext ctx = new StandardEvaluationContext();
        Method m = getClass().getDeclaredMethod("concat", String.class, String.class);
        ctx.setVariable("concat", m);
        expression = parser.parseExpression("#concat('a','b')");
        Assert.assertEquals("ab", expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals("ab", expression.getValue(ctx));
        expression = parser.parseExpression("#concat(#concat('a','b'),'c').charAt(1)");
        Assert.assertEquals('b', expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals('b', expression.getValue(ctx));
        expression = parser.parseExpression("#concat(#a,#b)");
        ctx.setVariable("a", "foo");
        ctx.setVariable("b", "bar");
        Assert.assertEquals("foobar", expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals("foobar", expression.getValue(ctx));
        ctx.setVariable("b", "boo");
        Assert.assertEquals("fooboo", expression.getValue(ctx));
        m = Math.class.getDeclaredMethod("pow", Double.TYPE, Double.TYPE);
        ctx.setVariable("kapow", m);
        expression = parser.parseExpression("#kapow(2.0d,2.0d)");
        Assert.assertEquals("4.0", expression.getValue(ctx).toString());
        assertCanCompile(expression);
        Assert.assertEquals("4.0", expression.getValue(ctx).toString());
    }

    @Test
    public void functionReferenceVisibility_SPR12359() throws Exception {
        // Confirms visibility of what is being called.
        StandardEvaluationContext context = new StandardEvaluationContext(new Object[]{ "1" });
        context.registerFunction("doCompare", SpelCompilationCoverageTests.SomeCompareMethod.class.getDeclaredMethod("compare", Object.class, Object.class));
        context.setVariable("arg", "2");
        // type nor method are public
        expression = parser.parseExpression("#doCompare([0],#arg)");
        Assert.assertEquals("-1", expression.getValue(context, Integer.class).toString());
        assertCantCompile(expression);
        // type not public but method is
        context = new StandardEvaluationContext(new Object[]{ "1" });
        context.registerFunction("doCompare", SpelCompilationCoverageTests.SomeCompareMethod.class.getDeclaredMethod("compare2", Object.class, Object.class));
        context.setVariable("arg", "2");
        expression = parser.parseExpression("#doCompare([0],#arg)");
        Assert.assertEquals("-1", expression.getValue(context, Integer.class).toString());
        assertCantCompile(expression);
    }

    @Test
    public void functionReferenceNonCompilableArguments_SPR12359() throws Exception {
        StandardEvaluationContext context = new StandardEvaluationContext(new Object[]{ "1" });
        context.registerFunction("negate", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("negate", Integer.TYPE));
        context.setVariable("arg", "2");
        int[] ints = new int[]{ 1, 2, 3 };
        context.setVariable("ints", ints);
        expression = parser.parseExpression("#negate(#ints.?[#this<2][0])");
        Assert.assertEquals("-1", expression.getValue(context, Integer.class).toString());
        // Selection isn't compilable.
        Assert.assertFalse(isCompilable());
    }

    @Test
    public void functionReferenceVarargs_SPR12359() throws Exception {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.registerFunction("append", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("append", String[].class));
        context.registerFunction("append2", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("append2", Object[].class));
        context.registerFunction("append3", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("append3", String[].class));
        context.registerFunction("append4", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("append4", String.class, String[].class));
        context.registerFunction("appendChar", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("appendChar", char[].class));
        context.registerFunction("sum", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("sum", int[].class));
        context.registerFunction("sumDouble", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("sumDouble", double[].class));
        context.registerFunction("sumFloat", SpelCompilationCoverageTests.SomeCompareMethod2.class.getDeclaredMethod("sumFloat", float[].class));
        context.setVariable("stringArray", new String[]{ "x", "y", "z" });
        context.setVariable("intArray", new int[]{ 5, 6, 9 });
        context.setVariable("doubleArray", new double[]{ 5.0, 6.0, 9.0 });
        context.setVariable("floatArray", new float[]{ 5.0F, 6.0F, 9.0F });
        expression = parser.parseExpression("#append('a','b','c')");
        Assert.assertEquals("abc", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("abc", expression.getValue(context).toString());
        expression = parser.parseExpression("#append('a')");
        Assert.assertEquals("a", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("a", expression.getValue(context).toString());
        expression = parser.parseExpression("#append()");
        Assert.assertEquals("", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("", expression.getValue(context).toString());
        expression = parser.parseExpression("#append(#stringArray)");
        Assert.assertEquals("xyz", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("xyz", expression.getValue(context).toString());
        // This is a methodreference invocation, to compare with functionreference
        expression = parser.parseExpression("append(#stringArray)");
        Assert.assertEquals("xyz", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("xyz", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        expression = parser.parseExpression("#append2('a','b','c')");
        Assert.assertEquals("abc", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("abc", expression.getValue(context).toString());
        expression = parser.parseExpression("append2('a','b')");
        Assert.assertEquals("ab", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("ab", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        expression = parser.parseExpression("#append2('a','b')");
        Assert.assertEquals("ab", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("ab", expression.getValue(context).toString());
        expression = parser.parseExpression("#append2()");
        Assert.assertEquals("", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("", expression.getValue(context).toString());
        expression = parser.parseExpression("#append3(#stringArray)");
        Assert.assertEquals("xyz", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("xyz", expression.getValue(context, new SpelCompilationCoverageTests.SomeCompareMethod2()).toString());
        // TODO fails due to conversionservice handling of String[] to Object...
        // expression = parser.parseExpression("#append2(#stringArray)");
        // assertEquals("xyz", expression.getValue(context).toString());
        // assertTrue(((SpelNodeImpl)((SpelExpression) expression).getAST()).isCompilable());
        // assertCanCompile(expression);
        // assertEquals("xyz", expression.getValue(context).toString());
        expression = parser.parseExpression("#sum(1,2,3)");
        Assert.assertEquals(6, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(6, expression.getValue(context));
        expression = parser.parseExpression("#sum(2)");
        Assert.assertEquals(2, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(context));
        expression = parser.parseExpression("#sum()");
        Assert.assertEquals(0, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue(context));
        expression = parser.parseExpression("#sum(#intArray)");
        Assert.assertEquals(20, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(20, expression.getValue(context));
        expression = parser.parseExpression("#sumDouble(1.0d,2.0d,3.0d)");
        Assert.assertEquals(6, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(6, expression.getValue(context));
        expression = parser.parseExpression("#sumDouble(2.0d)");
        Assert.assertEquals(2, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(context));
        expression = parser.parseExpression("#sumDouble()");
        Assert.assertEquals(0, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue(context));
        expression = parser.parseExpression("#sumDouble(#doubleArray)");
        Assert.assertEquals(20, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(20, expression.getValue(context));
        expression = parser.parseExpression("#sumFloat(1.0f,2.0f,3.0f)");
        Assert.assertEquals(6, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(6, expression.getValue(context));
        expression = parser.parseExpression("#sumFloat(2.0f)");
        Assert.assertEquals(2, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(context));
        expression = parser.parseExpression("#sumFloat()");
        Assert.assertEquals(0, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue(context));
        expression = parser.parseExpression("#sumFloat(#floatArray)");
        Assert.assertEquals(20, expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals(20, expression.getValue(context));
        expression = parser.parseExpression("#appendChar('abc'.charAt(0),'abc'.charAt(1))");
        Assert.assertEquals("ab", expression.getValue(context));
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("ab", expression.getValue(context));
        expression = parser.parseExpression("#append4('a','b','c')");
        Assert.assertEquals("a::bc", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("a::bc", expression.getValue(context).toString());
        expression = parser.parseExpression("#append4('a','b')");
        Assert.assertEquals("a::b", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("a::b", expression.getValue(context).toString());
        expression = parser.parseExpression("#append4('a')");
        Assert.assertEquals("a::", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("a::", expression.getValue(context).toString());
        expression = parser.parseExpression("#append4('a',#stringArray)");
        Assert.assertEquals("a::xyz", expression.getValue(context).toString());
        Assert.assertTrue(isCompilable());
        assertCanCompile(expression);
        Assert.assertEquals("a::xyz", expression.getValue(context).toString());
    }

    @Test
    public void functionReferenceVarargs() throws Exception {
        EvaluationContext ctx = new StandardEvaluationContext();
        Method m = getClass().getDeclaredMethod("join", String[].class);
        ctx.setVariable("join", m);
        expression = parser.parseExpression("#join('a','b','c')");
        Assert.assertEquals("abc", expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals("abc", expression.getValue(ctx));
    }

    @Test
    public void variableReference_userDefined() throws Exception {
        EvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("target", "abc");
        expression = parser.parseExpression("#target");
        Assert.assertEquals("abc", expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals("abc", expression.getValue(ctx));
        ctx.setVariable("target", "123");
        Assert.assertEquals("123", expression.getValue(ctx));
        ctx.setVariable("target", 42);
        try {
            Assert.assertEquals(42, expression.getValue(ctx));
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertTrue(((see.getCause()) instanceof ClassCastException));
        }
        ctx.setVariable("target", "abc");
        expression = parser.parseExpression("#target.charAt(0)");
        Assert.assertEquals('a', expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals('a', expression.getValue(ctx));
        ctx.setVariable("target", "1");
        Assert.assertEquals('1', expression.getValue(ctx));
        ctx.setVariable("target", 42);
        try {
            Assert.assertEquals('4', expression.getValue(ctx));
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertTrue(((see.getCause()) instanceof ClassCastException));
        }
    }

    @Test
    public void opLt() throws Exception {
        expression = parse("3.0d < 4.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3446.0d < 1123.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3 < 1");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("2 < 4");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3.0f < 1.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("1.0f < 5.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("30L < 30L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("15L < 20L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        // Differing types of number, not yet supported
        expression = parse("1 < 3.0d");
        assertCantCompile(expression);
        expression = parse("T(Integer).valueOf(3) < 4");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) < T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("5 < T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
    }

    @Test
    public void opLe() throws Exception {
        expression = parse("3.0d <= 4.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3446.0d <= 1123.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3446.0d <= 3446.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 <= 1");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("2 <= 4");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 <= 3");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3.0f <= 1.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("1.0f <= 5.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("2.0f <= 2.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("30L <= 30L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("15L <= 20L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        // Differing types of number, not yet supported
        expression = parse("1 <= 3.0d");
        assertCantCompile(expression);
        expression = parse("T(Integer).valueOf(3) <= 4");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) <= T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5 <= T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
    }

    @Test
    public void opGt() throws Exception {
        expression = parse("3.0d > 4.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3446.0d > 1123.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 > 1");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("2 > 4");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3.0f > 1.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("1.0f > 5.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("30L > 30L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("15L > 20L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        // Differing types of number, not yet supported
        expression = parse("1 > 3.0d");
        assertCantCompile(expression);
        expression = parse("T(Integer).valueOf(3) > 4");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) > T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("5 > T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
    }

    @Test
    public void opGe() throws Exception {
        expression = parse("3.0d >= 4.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3446.0d >= 1123.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3446.0d >= 3446.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 >= 1");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("2 >= 4");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3 >= 3");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3.0f >= 1.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("1.0f >= 5.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3.0f >= 3.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("40L >= 30L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("15L >= 20L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("30L >= 30L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        // Differing types of number, not yet supported
        expression = parse("1 >= 3.0d");
        assertCantCompile(expression);
        expression = parse("T(Integer).valueOf(3) >= 4");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) >= T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5 >= T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
    }

    @Test
    public void opEq() throws Exception {
        String tvar = "35";
        expression = parse("#root == 35");
        Assert.assertFalse(((Boolean) (expression.getValue(tvar))));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue(tvar))));
        expression = parse("35 == #root");
        expression.getValue(tvar);
        Assert.assertFalse(((Boolean) (expression.getValue(tvar))));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue(tvar))));
        SpelCompilationCoverageTests.TestClass7 tc7 = new SpelCompilationCoverageTests.TestClass7();
        expression = parse("property == 'UK'");
        Assert.assertTrue(((Boolean) (expression.getValue(tc7))));
        SpelCompilationCoverageTests.TestClass7.property = null;
        Assert.assertFalse(((Boolean) (expression.getValue(tc7))));
        assertCanCompile(expression);
        SpelCompilationCoverageTests.TestClass7.reset();
        Assert.assertTrue(((Boolean) (expression.getValue(tc7))));
        SpelCompilationCoverageTests.TestClass7.property = "UK";
        Assert.assertTrue(((Boolean) (expression.getValue(tc7))));
        SpelCompilationCoverageTests.TestClass7.reset();
        SpelCompilationCoverageTests.TestClass7.property = null;
        Assert.assertFalse(((Boolean) (expression.getValue(tc7))));
        expression = parse("property == null");
        Assert.assertTrue(((Boolean) (expression.getValue(tc7))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(tc7))));
        expression = parse("3.0d == 4.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3446.0d == 3446.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 == 1");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3 == 3");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3.0f == 1.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("2.0f == 2.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("30L == 30L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("15L == 20L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        // number types are not the same
        expression = parse("1 == 3.0d");
        assertCantCompile(expression);
        Double d = 3.0;
        expression = parse("#root==3.0d");
        Assert.assertTrue(((Boolean) (expression.getValue(d))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(d))));
        Integer i = 3;
        expression = parse("#root==3");
        Assert.assertTrue(((Boolean) (expression.getValue(i))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(i))));
        Float f = 3.0F;
        expression = parse("#root==3.0f");
        Assert.assertTrue(((Boolean) (expression.getValue(f))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(f))));
        long l = 300L;
        expression = parse("#root==300l");
        Assert.assertTrue(((Boolean) (expression.getValue(l))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(l))));
        boolean b = true;
        expression = parse("#root==true");
        Assert.assertTrue(((Boolean) (expression.getValue(b))));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue(b))));
        expression = parse("T(Integer).valueOf(3) == 4");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) == T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5 == T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Float).valueOf(3.0f) == 4.0f");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Float).valueOf(3.0f) == T(Float).valueOf(3.0f)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5.0f == T(Float).valueOf(3.0f)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Long).valueOf(3L) == 4L");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Long).valueOf(3L) == T(Long).valueOf(3L)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5L == T(Long).valueOf(3L)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Double).valueOf(3.0d) == 4.0d");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Double).valueOf(3.0d) == T(Double).valueOf(3.0d)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5.0d == T(Double).valueOf(3.0d)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("false == true");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Boolean).valueOf('true') == T(Boolean).valueOf('true')");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Boolean).valueOf('true') == true");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("false == T(Boolean).valueOf('false')");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
    }

    @Test
    public void opNe() throws Exception {
        expression = parse("3.0d != 4.0d");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3446.0d != 3446.0d");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3 != 1");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("3 != 3");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("3.0f != 1.0f");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("2.0f != 2.0f");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("30L != 30L");
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("15L != 20L");
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        // not compatible number types
        expression = parse("1 != 3.0d");
        assertCantCompile(expression);
        expression = parse("T(Integer).valueOf(3) != 4");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Integer).valueOf(3) != T(Integer).valueOf(3)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("5 != T(Integer).valueOf(3)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Float).valueOf(3.0f) != 4.0f");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Float).valueOf(3.0f) != T(Float).valueOf(3.0f)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("5.0f != T(Float).valueOf(3.0f)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Long).valueOf(3L) != 4L");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Long).valueOf(3L) != T(Long).valueOf(3L)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("5L != T(Long).valueOf(3L)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Double).valueOf(3.0d) == 4.0d");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Double).valueOf(3.0d) == T(Double).valueOf(3.0d)");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("5.0d == T(Double).valueOf(3.0d)");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("false == true");
        Assert.assertFalse(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertFalse(((Boolean) (expression.getValue())));
        expression = parse("T(Boolean).valueOf('true') == T(Boolean).valueOf('true')");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("T(Boolean).valueOf('true') == true");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
        expression = parse("false == T(Boolean).valueOf('false')");
        Assert.assertTrue(((Boolean) (expression.getValue())));
        assertCanCompile(expression);
        Assert.assertTrue(((Boolean) (expression.getValue())));
    }

    @Test
    public void opNe_SPR14863() throws Exception {
        SpelParserConfiguration configuration = new SpelParserConfiguration(MIXED, ClassLoader.getSystemClassLoader());
        SpelExpressionParser parser = new SpelExpressionParser(configuration);
        Expression expression = parser.parseExpression("data['my-key'] != 'my-value'");
        Map<String, String> data = new HashMap<>();
        data.put("my-key", new String("my-value"));
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelCompilationCoverageTests.MyContext(data));
        Assert.assertFalse(expression.getValue(context, Boolean.class));
        assertCanCompile(expression);
        compileExpression();
        Assert.assertFalse(expression.getValue(context, Boolean.class));
        List<String> ls = new ArrayList<String>();
        ls.add(new String("foo"));
        context = new StandardEvaluationContext(ls);
        expression = parse("get(0) != 'foo'");
        Assert.assertFalse(expression.getValue(context, Boolean.class));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(context, Boolean.class));
        ls.remove(0);
        ls.add("goo");
        Assert.assertTrue(expression.getValue(context, Boolean.class));
    }

    @Test
    public void opEq_SPR14863() throws Exception {
        // Exercise the comparator invocation code that runs in
        // equalityCheck() (called from interpreted and compiled code)
        expression = parser.parseExpression("#aa==#bb");
        StandardEvaluationContext sec = new StandardEvaluationContext();
        SpelCompilationCoverageTests.Apple aa = new SpelCompilationCoverageTests.Apple(1);
        SpelCompilationCoverageTests.Apple bb = new SpelCompilationCoverageTests.Apple(2);
        sec.setVariable("aa", aa);
        sec.setVariable("bb", bb);
        boolean b = expression.getValue(sec, Boolean.class);
        // Verify what the expression caused aa to be compared to
        Assert.assertEquals(bb, aa.gotComparedTo);
        Assert.assertFalse(b);
        bb.setValue(1);
        b = expression.getValue(sec, Boolean.class);
        Assert.assertEquals(bb, aa.gotComparedTo);
        Assert.assertTrue(b);
        assertCanCompile(expression);
        // Similar test with compiled expression
        aa = new SpelCompilationCoverageTests.Apple(99);
        bb = new SpelCompilationCoverageTests.Apple(100);
        sec.setVariable("aa", aa);
        sec.setVariable("bb", bb);
        b = expression.getValue(sec, Boolean.class);
        Assert.assertFalse(b);
        Assert.assertEquals(bb, aa.gotComparedTo);
        bb.setValue(99);
        b = expression.getValue(sec, Boolean.class);
        Assert.assertTrue(b);
        Assert.assertEquals(bb, aa.gotComparedTo);
        List<String> ls = new ArrayList<String>();
        ls.add(new String("foo"));
        StandardEvaluationContext context = new StandardEvaluationContext(ls);
        expression = parse("get(0) == 'foo'");
        Assert.assertTrue(expression.getValue(context, Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(context, Boolean.class));
        ls.remove(0);
        ls.add("goo");
        Assert.assertFalse(expression.getValue(context, Boolean.class));
    }

    @Test
    public void opPlus() throws Exception {
        expression = parse("2+2");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue());
        expression = parse("2L+2L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4L, expression.getValue());
        expression = parse("2.0f+2.0f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4.0F, expression.getValue());
        expression = parse("3.0d+4.0d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(7.0, expression.getValue());
        expression = parse("+1");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1, expression.getValue());
        expression = parse("+1L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("+1.5f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1.5F, expression.getValue());
        expression = parse("+2.5d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(2.5, expression.getValue());
        expression = parse("+T(Double).valueOf(2.5d)");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(2.5, expression.getValue());
        expression = parse("T(Integer).valueOf(2)+6");
        Assert.assertEquals(8, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(8, expression.getValue());
        expression = parse("T(Integer).valueOf(1)+T(Integer).valueOf(3)");
        Assert.assertEquals(4, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue());
        expression = parse("1+T(Integer).valueOf(3)");
        Assert.assertEquals(4, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue());
        expression = parse("T(Float).valueOf(2.0f)+6");
        Assert.assertEquals(8.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(8.0F, expression.getValue());
        expression = parse("T(Float).valueOf(2.0f)+T(Float).valueOf(3.0f)");
        Assert.assertEquals(5.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(5.0F, expression.getValue());
        expression = parse("3L+T(Long).valueOf(4L)");
        Assert.assertEquals(7L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(7L, expression.getValue());
        expression = parse("T(Long).valueOf(2L)+6");
        Assert.assertEquals(8L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(8L, expression.getValue());
        expression = parse("T(Long).valueOf(2L)+T(Long).valueOf(3L)");
        Assert.assertEquals(5L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(5L, expression.getValue());
        expression = parse("1L+T(Long).valueOf(2L)");
        Assert.assertEquals(3L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(3L, expression.getValue());
    }

    @Test
    public void opDivide_mixedNumberTypes() throws Exception {
        SpelCompilationCoverageTests.PayloadX p = new SpelCompilationCoverageTests.PayloadX();
        // This is what you had to do before the changes in order for it to compile:
        // expression = parse("(T(java.lang.Double).parseDouble(payload.valueI.toString()))/60D");
        // right is a double
        checkCalc(p, "payload.valueSB/60D", 2.0);
        checkCalc(p, "payload.valueBB/60D", 2.0);
        checkCalc(p, "payload.valueFB/60D", 2.0);
        checkCalc(p, "payload.valueDB/60D", 2.0);
        checkCalc(p, "payload.valueJB/60D", 2.0);
        checkCalc(p, "payload.valueIB/60D", 2.0);
        checkCalc(p, "payload.valueS/60D", 2.0);
        checkCalc(p, "payload.valueB/60D", 2.0);
        checkCalc(p, "payload.valueF/60D", 2.0);
        checkCalc(p, "payload.valueD/60D", 2.0);
        checkCalc(p, "payload.valueJ/60D", 2.0);
        checkCalc(p, "payload.valueI/60D", 2.0);
        checkCalc(p, "payload.valueSB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueBB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueFB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueDB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueJB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueIB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueS/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueB/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueF/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueD/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueJ/payload.valueDB60", 2.0);
        checkCalc(p, "payload.valueI/payload.valueDB60", 2.0);
        // right is a float
        checkCalc(p, "payload.valueSB/60F", 2.0F);
        checkCalc(p, "payload.valueBB/60F", 2.0F);
        checkCalc(p, "payload.valueFB/60F", 2.0F);
        checkCalc(p, "payload.valueDB/60F", 2.0);
        checkCalc(p, "payload.valueJB/60F", 2.0F);
        checkCalc(p, "payload.valueIB/60F", 2.0F);
        checkCalc(p, "payload.valueS/60F", 2.0F);
        checkCalc(p, "payload.valueB/60F", 2.0F);
        checkCalc(p, "payload.valueF/60F", 2.0F);
        checkCalc(p, "payload.valueD/60F", 2.0);
        checkCalc(p, "payload.valueJ/60F", 2.0F);
        checkCalc(p, "payload.valueI/60F", 2.0F);
        checkCalc(p, "payload.valueSB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueBB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueFB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueDB/payload.valueFB60", 2.0);
        checkCalc(p, "payload.valueJB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueIB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueS/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueB/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueF/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueD/payload.valueFB60", 2.0);
        checkCalc(p, "payload.valueJ/payload.valueFB60", 2.0F);
        checkCalc(p, "payload.valueI/payload.valueFB60", 2.0F);
        // right is a long
        checkCalc(p, "payload.valueSB/60L", 2L);
        checkCalc(p, "payload.valueBB/60L", 2L);
        checkCalc(p, "payload.valueFB/60L", 2.0F);
        checkCalc(p, "payload.valueDB/60L", 2.0);
        checkCalc(p, "payload.valueJB/60L", 2L);
        checkCalc(p, "payload.valueIB/60L", 2L);
        checkCalc(p, "payload.valueS/60L", 2L);
        checkCalc(p, "payload.valueB/60L", 2L);
        checkCalc(p, "payload.valueF/60L", 2.0F);
        checkCalc(p, "payload.valueD/60L", 2.0);
        checkCalc(p, "payload.valueJ/60L", 2L);
        checkCalc(p, "payload.valueI/60L", 2L);
        checkCalc(p, "payload.valueSB/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueBB/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueFB/payload.valueJB60", 2.0F);
        checkCalc(p, "payload.valueDB/payload.valueJB60", 2.0);
        checkCalc(p, "payload.valueJB/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueIB/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueS/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueB/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueF/payload.valueJB60", 2.0F);
        checkCalc(p, "payload.valueD/payload.valueJB60", 2.0);
        checkCalc(p, "payload.valueJ/payload.valueJB60", 2L);
        checkCalc(p, "payload.valueI/payload.valueJB60", 2L);
        // right is an int
        checkCalc(p, "payload.valueSB/60", 2);
        checkCalc(p, "payload.valueBB/60", 2);
        checkCalc(p, "payload.valueFB/60", 2.0F);
        checkCalc(p, "payload.valueDB/60", 2.0);
        checkCalc(p, "payload.valueJB/60", 2L);
        checkCalc(p, "payload.valueIB/60", 2);
        checkCalc(p, "payload.valueS/60", 2);
        checkCalc(p, "payload.valueB/60", 2);
        checkCalc(p, "payload.valueF/60", 2.0F);
        checkCalc(p, "payload.valueD/60", 2.0);
        checkCalc(p, "payload.valueJ/60", 2L);
        checkCalc(p, "payload.valueI/60", 2);
        checkCalc(p, "payload.valueSB/payload.valueIB60", 2);
        checkCalc(p, "payload.valueBB/payload.valueIB60", 2);
        checkCalc(p, "payload.valueFB/payload.valueIB60", 2.0F);
        checkCalc(p, "payload.valueDB/payload.valueIB60", 2.0);
        checkCalc(p, "payload.valueJB/payload.valueIB60", 2L);
        checkCalc(p, "payload.valueIB/payload.valueIB60", 2);
        checkCalc(p, "payload.valueS/payload.valueIB60", 2);
        checkCalc(p, "payload.valueB/payload.valueIB60", 2);
        checkCalc(p, "payload.valueF/payload.valueIB60", 2.0F);
        checkCalc(p, "payload.valueD/payload.valueIB60", 2.0);
        checkCalc(p, "payload.valueJ/payload.valueIB60", 2L);
        checkCalc(p, "payload.valueI/payload.valueIB60", 2);
        // right is a short
        checkCalc(p, "payload.valueSB/payload.valueS", 1);
        checkCalc(p, "payload.valueBB/payload.valueS", 1);
        checkCalc(p, "payload.valueFB/payload.valueS", 1.0F);
        checkCalc(p, "payload.valueDB/payload.valueS", 1.0);
        checkCalc(p, "payload.valueJB/payload.valueS", 1L);
        checkCalc(p, "payload.valueIB/payload.valueS", 1);
        checkCalc(p, "payload.valueS/payload.valueS", 1);
        checkCalc(p, "payload.valueB/payload.valueS", 1);
        checkCalc(p, "payload.valueF/payload.valueS", 1.0F);
        checkCalc(p, "payload.valueD/payload.valueS", 1.0);
        checkCalc(p, "payload.valueJ/payload.valueS", 1L);
        checkCalc(p, "payload.valueI/payload.valueS", 1);
        checkCalc(p, "payload.valueSB/payload.valueSB", 1);
        checkCalc(p, "payload.valueBB/payload.valueSB", 1);
        checkCalc(p, "payload.valueFB/payload.valueSB", 1.0F);
        checkCalc(p, "payload.valueDB/payload.valueSB", 1.0);
        checkCalc(p, "payload.valueJB/payload.valueSB", 1L);
        checkCalc(p, "payload.valueIB/payload.valueSB", 1);
        checkCalc(p, "payload.valueS/payload.valueSB", 1);
        checkCalc(p, "payload.valueB/payload.valueSB", 1);
        checkCalc(p, "payload.valueF/payload.valueSB", 1.0F);
        checkCalc(p, "payload.valueD/payload.valueSB", 1.0);
        checkCalc(p, "payload.valueJ/payload.valueSB", 1L);
        checkCalc(p, "payload.valueI/payload.valueSB", 1);
        // right is a byte
        checkCalc(p, "payload.valueSB/payload.valueB", 1);
        checkCalc(p, "payload.valueBB/payload.valueB", 1);
        checkCalc(p, "payload.valueFB/payload.valueB", 1.0F);
        checkCalc(p, "payload.valueDB/payload.valueB", 1.0);
        checkCalc(p, "payload.valueJB/payload.valueB", 1L);
        checkCalc(p, "payload.valueIB/payload.valueB", 1);
        checkCalc(p, "payload.valueS/payload.valueB", 1);
        checkCalc(p, "payload.valueB/payload.valueB", 1);
        checkCalc(p, "payload.valueF/payload.valueB", 1.0F);
        checkCalc(p, "payload.valueD/payload.valueB", 1.0);
        checkCalc(p, "payload.valueJ/payload.valueB", 1L);
        checkCalc(p, "payload.valueI/payload.valueB", 1);
        checkCalc(p, "payload.valueSB/payload.valueBB", 1);
        checkCalc(p, "payload.valueBB/payload.valueBB", 1);
        checkCalc(p, "payload.valueFB/payload.valueBB", 1.0F);
        checkCalc(p, "payload.valueDB/payload.valueBB", 1.0);
        checkCalc(p, "payload.valueJB/payload.valueBB", 1L);
        checkCalc(p, "payload.valueIB/payload.valueBB", 1);
        checkCalc(p, "payload.valueS/payload.valueBB", 1);
        checkCalc(p, "payload.valueB/payload.valueBB", 1);
        checkCalc(p, "payload.valueF/payload.valueBB", 1.0F);
        checkCalc(p, "payload.valueD/payload.valueBB", 1.0);
        checkCalc(p, "payload.valueJ/payload.valueBB", 1L);
        checkCalc(p, "payload.valueI/payload.valueBB", 1);
    }

    @Test
    public void opPlus_mixedNumberTypes() throws Exception {
        SpelCompilationCoverageTests.PayloadX p = new SpelCompilationCoverageTests.PayloadX();
        // This is what you had to do before the changes in order for it to compile:
        // expression = parse("(T(java.lang.Double).parseDouble(payload.valueI.toString()))/60D");
        // right is a double
        checkCalc(p, "payload.valueSB+60D", 180.0);
        checkCalc(p, "payload.valueBB+60D", 180.0);
        checkCalc(p, "payload.valueFB+60D", 180.0);
        checkCalc(p, "payload.valueDB+60D", 180.0);
        checkCalc(p, "payload.valueJB+60D", 180.0);
        checkCalc(p, "payload.valueIB+60D", 180.0);
        checkCalc(p, "payload.valueS+60D", 180.0);
        checkCalc(p, "payload.valueB+60D", 180.0);
        checkCalc(p, "payload.valueF+60D", 180.0);
        checkCalc(p, "payload.valueD+60D", 180.0);
        checkCalc(p, "payload.valueJ+60D", 180.0);
        checkCalc(p, "payload.valueI+60D", 180.0);
        checkCalc(p, "payload.valueSB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueBB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueFB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueDB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueJB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueIB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueS+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueB+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueF+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueD+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueJ+payload.valueDB60", 180.0);
        checkCalc(p, "payload.valueI+payload.valueDB60", 180.0);
        // right is a float
        checkCalc(p, "payload.valueSB+60F", 180.0F);
        checkCalc(p, "payload.valueBB+60F", 180.0F);
        checkCalc(p, "payload.valueFB+60F", 180.0F);
        checkCalc(p, "payload.valueDB+60F", 180.0);
        checkCalc(p, "payload.valueJB+60F", 180.0F);
        checkCalc(p, "payload.valueIB+60F", 180.0F);
        checkCalc(p, "payload.valueS+60F", 180.0F);
        checkCalc(p, "payload.valueB+60F", 180.0F);
        checkCalc(p, "payload.valueF+60F", 180.0F);
        checkCalc(p, "payload.valueD+60F", 180.0);
        checkCalc(p, "payload.valueJ+60F", 180.0F);
        checkCalc(p, "payload.valueI+60F", 180.0F);
        checkCalc(p, "payload.valueSB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueBB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueFB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueDB+payload.valueFB60", 180.0);
        checkCalc(p, "payload.valueJB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueIB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueS+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueB+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueF+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueD+payload.valueFB60", 180.0);
        checkCalc(p, "payload.valueJ+payload.valueFB60", 180.0F);
        checkCalc(p, "payload.valueI+payload.valueFB60", 180.0F);
        // right is a long
        checkCalc(p, "payload.valueSB+60L", 180L);
        checkCalc(p, "payload.valueBB+60L", 180L);
        checkCalc(p, "payload.valueFB+60L", 180.0F);
        checkCalc(p, "payload.valueDB+60L", 180.0);
        checkCalc(p, "payload.valueJB+60L", 180L);
        checkCalc(p, "payload.valueIB+60L", 180L);
        checkCalc(p, "payload.valueS+60L", 180L);
        checkCalc(p, "payload.valueB+60L", 180L);
        checkCalc(p, "payload.valueF+60L", 180.0F);
        checkCalc(p, "payload.valueD+60L", 180.0);
        checkCalc(p, "payload.valueJ+60L", 180L);
        checkCalc(p, "payload.valueI+60L", 180L);
        checkCalc(p, "payload.valueSB+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueBB+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueFB+payload.valueJB60", 180.0F);
        checkCalc(p, "payload.valueDB+payload.valueJB60", 180.0);
        checkCalc(p, "payload.valueJB+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueIB+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueS+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueB+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueF+payload.valueJB60", 180.0F);
        checkCalc(p, "payload.valueD+payload.valueJB60", 180.0);
        checkCalc(p, "payload.valueJ+payload.valueJB60", 180L);
        checkCalc(p, "payload.valueI+payload.valueJB60", 180L);
        // right is an int
        checkCalc(p, "payload.valueSB+60", 180);
        checkCalc(p, "payload.valueBB+60", 180);
        checkCalc(p, "payload.valueFB+60", 180.0F);
        checkCalc(p, "payload.valueDB+60", 180.0);
        checkCalc(p, "payload.valueJB+60", 180L);
        checkCalc(p, "payload.valueIB+60", 180);
        checkCalc(p, "payload.valueS+60", 180);
        checkCalc(p, "payload.valueB+60", 180);
        checkCalc(p, "payload.valueF+60", 180.0F);
        checkCalc(p, "payload.valueD+60", 180.0);
        checkCalc(p, "payload.valueJ+60", 180L);
        checkCalc(p, "payload.valueI+60", 180);
        checkCalc(p, "payload.valueSB+payload.valueIB60", 180);
        checkCalc(p, "payload.valueBB+payload.valueIB60", 180);
        checkCalc(p, "payload.valueFB+payload.valueIB60", 180.0F);
        checkCalc(p, "payload.valueDB+payload.valueIB60", 180.0);
        checkCalc(p, "payload.valueJB+payload.valueIB60", 180L);
        checkCalc(p, "payload.valueIB+payload.valueIB60", 180);
        checkCalc(p, "payload.valueS+payload.valueIB60", 180);
        checkCalc(p, "payload.valueB+payload.valueIB60", 180);
        checkCalc(p, "payload.valueF+payload.valueIB60", 180.0F);
        checkCalc(p, "payload.valueD+payload.valueIB60", 180.0);
        checkCalc(p, "payload.valueJ+payload.valueIB60", 180L);
        checkCalc(p, "payload.valueI+payload.valueIB60", 180);
        // right is a short
        checkCalc(p, "payload.valueSB+payload.valueS", 240);
        checkCalc(p, "payload.valueBB+payload.valueS", 240);
        checkCalc(p, "payload.valueFB+payload.valueS", 240.0F);
        checkCalc(p, "payload.valueDB+payload.valueS", 240.0);
        checkCalc(p, "payload.valueJB+payload.valueS", 240L);
        checkCalc(p, "payload.valueIB+payload.valueS", 240);
        checkCalc(p, "payload.valueS+payload.valueS", 240);
        checkCalc(p, "payload.valueB+payload.valueS", 240);
        checkCalc(p, "payload.valueF+payload.valueS", 240.0F);
        checkCalc(p, "payload.valueD+payload.valueS", 240.0);
        checkCalc(p, "payload.valueJ+payload.valueS", 240L);
        checkCalc(p, "payload.valueI+payload.valueS", 240);
        checkCalc(p, "payload.valueSB+payload.valueSB", 240);
        checkCalc(p, "payload.valueBB+payload.valueSB", 240);
        checkCalc(p, "payload.valueFB+payload.valueSB", 240.0F);
        checkCalc(p, "payload.valueDB+payload.valueSB", 240.0);
        checkCalc(p, "payload.valueJB+payload.valueSB", 240L);
        checkCalc(p, "payload.valueIB+payload.valueSB", 240);
        checkCalc(p, "payload.valueS+payload.valueSB", 240);
        checkCalc(p, "payload.valueB+payload.valueSB", 240);
        checkCalc(p, "payload.valueF+payload.valueSB", 240.0F);
        checkCalc(p, "payload.valueD+payload.valueSB", 240.0);
        checkCalc(p, "payload.valueJ+payload.valueSB", 240L);
        checkCalc(p, "payload.valueI+payload.valueSB", 240);
        // right is a byte
        checkCalc(p, "payload.valueSB+payload.valueB", 240);
        checkCalc(p, "payload.valueBB+payload.valueB", 240);
        checkCalc(p, "payload.valueFB+payload.valueB", 240.0F);
        checkCalc(p, "payload.valueDB+payload.valueB", 240.0);
        checkCalc(p, "payload.valueJB+payload.valueB", 240L);
        checkCalc(p, "payload.valueIB+payload.valueB", 240);
        checkCalc(p, "payload.valueS+payload.valueB", 240);
        checkCalc(p, "payload.valueB+payload.valueB", 240);
        checkCalc(p, "payload.valueF+payload.valueB", 240.0F);
        checkCalc(p, "payload.valueD+payload.valueB", 240.0);
        checkCalc(p, "payload.valueJ+payload.valueB", 240L);
        checkCalc(p, "payload.valueI+payload.valueB", 240);
        checkCalc(p, "payload.valueSB+payload.valueBB", 240);
        checkCalc(p, "payload.valueBB+payload.valueBB", 240);
        checkCalc(p, "payload.valueFB+payload.valueBB", 240.0F);
        checkCalc(p, "payload.valueDB+payload.valueBB", 240.0);
        checkCalc(p, "payload.valueJB+payload.valueBB", 240L);
        checkCalc(p, "payload.valueIB+payload.valueBB", 240);
        checkCalc(p, "payload.valueS+payload.valueBB", 240);
        checkCalc(p, "payload.valueB+payload.valueBB", 240);
        checkCalc(p, "payload.valueF+payload.valueBB", 240.0F);
        checkCalc(p, "payload.valueD+payload.valueBB", 240.0);
        checkCalc(p, "payload.valueJ+payload.valueBB", 240L);
        checkCalc(p, "payload.valueI+payload.valueBB", 240);
    }

    @Test
    public void opPlusString() throws Exception {
        expression = parse("'hello' + 'world'");
        Assert.assertEquals("helloworld", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("helloworld", expression.getValue());
        // Method with string return
        expression = parse("'hello' + getWorld()");
        Assert.assertEquals("helloworld", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("helloworld", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        // Method with string return
        expression = parse("getWorld() + 'hello'");
        Assert.assertEquals("worldhello", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("worldhello", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        // Three strings, optimal bytecode would only use one StringBuilder
        expression = parse("'hello' + getWorld() + ' spring'");
        Assert.assertEquals("helloworld spring", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("helloworld spring", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        // Three strings, optimal bytecode would only use one StringBuilder
        expression = parse("'hello' + 3 + ' spring'");
        Assert.assertEquals("hello3 spring", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCantCompile(expression);
        expression = parse("object + 'a'");
        Assert.assertEquals("objecta", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("objecta", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        expression = parse("'a'+object");
        Assert.assertEquals("aobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("aobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        expression = parse("'a'+object+'a'");
        Assert.assertEquals("aobjecta", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("aobjecta", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        expression = parse("object+'a'+object");
        Assert.assertEquals("objectaobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("objectaobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        expression = parse("object+object");
        Assert.assertEquals("objectobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
        assertCanCompile(expression);
        Assert.assertEquals("objectobject", expression.getValue(new SpelCompilationCoverageTests.Greeter()));
    }

    @Test
    public void opMinus() throws Exception {
        expression = parse("2-2");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue());
        expression = parse("4L-2L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(2L, expression.getValue());
        expression = parse("4.0f-2.0f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(2.0F, expression.getValue());
        expression = parse("3.0d-4.0d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals((-1.0), expression.getValue());
        expression = parse("-1");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals((-1), expression.getValue());
        expression = parse("-1L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals((-1L), expression.getValue());
        expression = parse("-1.5f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals((-1.5F), expression.getValue());
        expression = parse("-2.5d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals((-2.5), expression.getValue());
        expression = parse("T(Integer).valueOf(2)-6");
        Assert.assertEquals((-4), expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals((-4), expression.getValue());
        expression = parse("T(Integer).valueOf(1)-T(Integer).valueOf(3)");
        Assert.assertEquals((-2), expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals((-2), expression.getValue());
        expression = parse("4-T(Integer).valueOf(3)");
        Assert.assertEquals(1, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1, expression.getValue());
        expression = parse("T(Float).valueOf(2.0f)-6");
        Assert.assertEquals((-4.0F), expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals((-4.0F), expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)-T(Float).valueOf(3.0f)");
        Assert.assertEquals(5.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(5.0F, expression.getValue());
        expression = parse("11L-T(Long).valueOf(4L)");
        Assert.assertEquals(7L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(7L, expression.getValue());
        expression = parse("T(Long).valueOf(9L)-6");
        Assert.assertEquals(3L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(3L, expression.getValue());
        expression = parse("T(Long).valueOf(4L)-T(Long).valueOf(3L)");
        Assert.assertEquals(1L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("8L-T(Long).valueOf(2L)");
        Assert.assertEquals(6L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(6L, expression.getValue());
    }

    @Test
    public void opMinus_mixedNumberTypes() throws Exception {
        SpelCompilationCoverageTests.PayloadX p = new SpelCompilationCoverageTests.PayloadX();
        // This is what you had to do before the changes in order for it to compile:
        // expression = parse("(T(java.lang.Double).parseDouble(payload.valueI.toString()))/60D");
        // right is a double
        checkCalc(p, "payload.valueSB-60D", 60.0);
        checkCalc(p, "payload.valueBB-60D", 60.0);
        checkCalc(p, "payload.valueFB-60D", 60.0);
        checkCalc(p, "payload.valueDB-60D", 60.0);
        checkCalc(p, "payload.valueJB-60D", 60.0);
        checkCalc(p, "payload.valueIB-60D", 60.0);
        checkCalc(p, "payload.valueS-60D", 60.0);
        checkCalc(p, "payload.valueB-60D", 60.0);
        checkCalc(p, "payload.valueF-60D", 60.0);
        checkCalc(p, "payload.valueD-60D", 60.0);
        checkCalc(p, "payload.valueJ-60D", 60.0);
        checkCalc(p, "payload.valueI-60D", 60.0);
        checkCalc(p, "payload.valueSB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueBB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueFB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueDB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueJB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueIB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueS-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueB-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueF-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueD-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueJ-payload.valueDB60", 60.0);
        checkCalc(p, "payload.valueI-payload.valueDB60", 60.0);
        // right is a float
        checkCalc(p, "payload.valueSB-60F", 60.0F);
        checkCalc(p, "payload.valueBB-60F", 60.0F);
        checkCalc(p, "payload.valueFB-60F", 60.0F);
        checkCalc(p, "payload.valueDB-60F", 60.0);
        checkCalc(p, "payload.valueJB-60F", 60.0F);
        checkCalc(p, "payload.valueIB-60F", 60.0F);
        checkCalc(p, "payload.valueS-60F", 60.0F);
        checkCalc(p, "payload.valueB-60F", 60.0F);
        checkCalc(p, "payload.valueF-60F", 60.0F);
        checkCalc(p, "payload.valueD-60F", 60.0);
        checkCalc(p, "payload.valueJ-60F", 60.0F);
        checkCalc(p, "payload.valueI-60F", 60.0F);
        checkCalc(p, "payload.valueSB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueBB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueFB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueDB-payload.valueFB60", 60.0);
        checkCalc(p, "payload.valueJB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueIB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueS-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueB-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueF-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueD-payload.valueFB60", 60.0);
        checkCalc(p, "payload.valueJ-payload.valueFB60", 60.0F);
        checkCalc(p, "payload.valueI-payload.valueFB60", 60.0F);
        // right is a long
        checkCalc(p, "payload.valueSB-60L", 60L);
        checkCalc(p, "payload.valueBB-60L", 60L);
        checkCalc(p, "payload.valueFB-60L", 60.0F);
        checkCalc(p, "payload.valueDB-60L", 60.0);
        checkCalc(p, "payload.valueJB-60L", 60L);
        checkCalc(p, "payload.valueIB-60L", 60L);
        checkCalc(p, "payload.valueS-60L", 60L);
        checkCalc(p, "payload.valueB-60L", 60L);
        checkCalc(p, "payload.valueF-60L", 60.0F);
        checkCalc(p, "payload.valueD-60L", 60.0);
        checkCalc(p, "payload.valueJ-60L", 60L);
        checkCalc(p, "payload.valueI-60L", 60L);
        checkCalc(p, "payload.valueSB-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueBB-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueFB-payload.valueJB60", 60.0F);
        checkCalc(p, "payload.valueDB-payload.valueJB60", 60.0);
        checkCalc(p, "payload.valueJB-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueIB-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueS-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueB-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueF-payload.valueJB60", 60.0F);
        checkCalc(p, "payload.valueD-payload.valueJB60", 60.0);
        checkCalc(p, "payload.valueJ-payload.valueJB60", 60L);
        checkCalc(p, "payload.valueI-payload.valueJB60", 60L);
        // right is an int
        checkCalc(p, "payload.valueSB-60", 60);
        checkCalc(p, "payload.valueBB-60", 60);
        checkCalc(p, "payload.valueFB-60", 60.0F);
        checkCalc(p, "payload.valueDB-60", 60.0);
        checkCalc(p, "payload.valueJB-60", 60L);
        checkCalc(p, "payload.valueIB-60", 60);
        checkCalc(p, "payload.valueS-60", 60);
        checkCalc(p, "payload.valueB-60", 60);
        checkCalc(p, "payload.valueF-60", 60.0F);
        checkCalc(p, "payload.valueD-60", 60.0);
        checkCalc(p, "payload.valueJ-60", 60L);
        checkCalc(p, "payload.valueI-60", 60);
        checkCalc(p, "payload.valueSB-payload.valueIB60", 60);
        checkCalc(p, "payload.valueBB-payload.valueIB60", 60);
        checkCalc(p, "payload.valueFB-payload.valueIB60", 60.0F);
        checkCalc(p, "payload.valueDB-payload.valueIB60", 60.0);
        checkCalc(p, "payload.valueJB-payload.valueIB60", 60L);
        checkCalc(p, "payload.valueIB-payload.valueIB60", 60);
        checkCalc(p, "payload.valueS-payload.valueIB60", 60);
        checkCalc(p, "payload.valueB-payload.valueIB60", 60);
        checkCalc(p, "payload.valueF-payload.valueIB60", 60.0F);
        checkCalc(p, "payload.valueD-payload.valueIB60", 60.0);
        checkCalc(p, "payload.valueJ-payload.valueIB60", 60L);
        checkCalc(p, "payload.valueI-payload.valueIB60", 60);
        // right is a short
        checkCalc(p, "payload.valueSB-payload.valueS20", 100);
        checkCalc(p, "payload.valueBB-payload.valueS20", 100);
        checkCalc(p, "payload.valueFB-payload.valueS20", 100.0F);
        checkCalc(p, "payload.valueDB-payload.valueS20", 100.0);
        checkCalc(p, "payload.valueJB-payload.valueS20", 100L);
        checkCalc(p, "payload.valueIB-payload.valueS20", 100);
        checkCalc(p, "payload.valueS-payload.valueS20", 100);
        checkCalc(p, "payload.valueB-payload.valueS20", 100);
        checkCalc(p, "payload.valueF-payload.valueS20", 100.0F);
        checkCalc(p, "payload.valueD-payload.valueS20", 100.0);
        checkCalc(p, "payload.valueJ-payload.valueS20", 100L);
        checkCalc(p, "payload.valueI-payload.valueS20", 100);
        checkCalc(p, "payload.valueSB-payload.valueSB20", 100);
        checkCalc(p, "payload.valueBB-payload.valueSB20", 100);
        checkCalc(p, "payload.valueFB-payload.valueSB20", 100.0F);
        checkCalc(p, "payload.valueDB-payload.valueSB20", 100.0);
        checkCalc(p, "payload.valueJB-payload.valueSB20", 100L);
        checkCalc(p, "payload.valueIB-payload.valueSB20", 100);
        checkCalc(p, "payload.valueS-payload.valueSB20", 100);
        checkCalc(p, "payload.valueB-payload.valueSB20", 100);
        checkCalc(p, "payload.valueF-payload.valueSB20", 100.0F);
        checkCalc(p, "payload.valueD-payload.valueSB20", 100.0);
        checkCalc(p, "payload.valueJ-payload.valueSB20", 100L);
        checkCalc(p, "payload.valueI-payload.valueSB20", 100);
        // right is a byte
        checkCalc(p, "payload.valueSB-payload.valueB20", 100);
        checkCalc(p, "payload.valueBB-payload.valueB20", 100);
        checkCalc(p, "payload.valueFB-payload.valueB20", 100.0F);
        checkCalc(p, "payload.valueDB-payload.valueB20", 100.0);
        checkCalc(p, "payload.valueJB-payload.valueB20", 100L);
        checkCalc(p, "payload.valueIB-payload.valueB20", 100);
        checkCalc(p, "payload.valueS-payload.valueB20", 100);
        checkCalc(p, "payload.valueB-payload.valueB20", 100);
        checkCalc(p, "payload.valueF-payload.valueB20", 100.0F);
        checkCalc(p, "payload.valueD-payload.valueB20", 100.0);
        checkCalc(p, "payload.valueJ-payload.valueB20", 100L);
        checkCalc(p, "payload.valueI-payload.valueB20", 100);
        checkCalc(p, "payload.valueSB-payload.valueBB20", 100);
        checkCalc(p, "payload.valueBB-payload.valueBB20", 100);
        checkCalc(p, "payload.valueFB-payload.valueBB20", 100.0F);
        checkCalc(p, "payload.valueDB-payload.valueBB20", 100.0);
        checkCalc(p, "payload.valueJB-payload.valueBB20", 100L);
        checkCalc(p, "payload.valueIB-payload.valueBB20", 100);
        checkCalc(p, "payload.valueS-payload.valueBB20", 100);
        checkCalc(p, "payload.valueB-payload.valueBB20", 100);
        checkCalc(p, "payload.valueF-payload.valueBB20", 100.0F);
        checkCalc(p, "payload.valueD-payload.valueBB20", 100.0);
        checkCalc(p, "payload.valueJ-payload.valueBB20", 100L);
        checkCalc(p, "payload.valueI-payload.valueBB20", 100);
    }

    @Test
    public void opMultiply_mixedNumberTypes() throws Exception {
        SpelCompilationCoverageTests.PayloadX p = new SpelCompilationCoverageTests.PayloadX();
        // This is what you had to do before the changes in order for it to compile:
        // expression = parse("(T(java.lang.Double).parseDouble(payload.valueI.toString()))/60D");
        // right is a double
        checkCalc(p, "payload.valueSB*60D", 7200.0);
        checkCalc(p, "payload.valueBB*60D", 7200.0);
        checkCalc(p, "payload.valueFB*60D", 7200.0);
        checkCalc(p, "payload.valueDB*60D", 7200.0);
        checkCalc(p, "payload.valueJB*60D", 7200.0);
        checkCalc(p, "payload.valueIB*60D", 7200.0);
        checkCalc(p, "payload.valueS*60D", 7200.0);
        checkCalc(p, "payload.valueB*60D", 7200.0);
        checkCalc(p, "payload.valueF*60D", 7200.0);
        checkCalc(p, "payload.valueD*60D", 7200.0);
        checkCalc(p, "payload.valueJ*60D", 7200.0);
        checkCalc(p, "payload.valueI*60D", 7200.0);
        checkCalc(p, "payload.valueSB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueBB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueFB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueDB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueJB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueIB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueS*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueB*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueF*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueD*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueJ*payload.valueDB60", 7200.0);
        checkCalc(p, "payload.valueI*payload.valueDB60", 7200.0);
        // right is a float
        checkCalc(p, "payload.valueSB*60F", 7200.0F);
        checkCalc(p, "payload.valueBB*60F", 7200.0F);
        checkCalc(p, "payload.valueFB*60F", 7200.0F);
        checkCalc(p, "payload.valueDB*60F", 7200.0);
        checkCalc(p, "payload.valueJB*60F", 7200.0F);
        checkCalc(p, "payload.valueIB*60F", 7200.0F);
        checkCalc(p, "payload.valueS*60F", 7200.0F);
        checkCalc(p, "payload.valueB*60F", 7200.0F);
        checkCalc(p, "payload.valueF*60F", 7200.0F);
        checkCalc(p, "payload.valueD*60F", 7200.0);
        checkCalc(p, "payload.valueJ*60F", 7200.0F);
        checkCalc(p, "payload.valueI*60F", 7200.0F);
        checkCalc(p, "payload.valueSB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueBB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueFB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueDB*payload.valueFB60", 7200.0);
        checkCalc(p, "payload.valueJB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueIB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueS*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueB*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueF*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueD*payload.valueFB60", 7200.0);
        checkCalc(p, "payload.valueJ*payload.valueFB60", 7200.0F);
        checkCalc(p, "payload.valueI*payload.valueFB60", 7200.0F);
        // right is a long
        checkCalc(p, "payload.valueSB*60L", 7200L);
        checkCalc(p, "payload.valueBB*60L", 7200L);
        checkCalc(p, "payload.valueFB*60L", 7200.0F);
        checkCalc(p, "payload.valueDB*60L", 7200.0);
        checkCalc(p, "payload.valueJB*60L", 7200L);
        checkCalc(p, "payload.valueIB*60L", 7200L);
        checkCalc(p, "payload.valueS*60L", 7200L);
        checkCalc(p, "payload.valueB*60L", 7200L);
        checkCalc(p, "payload.valueF*60L", 7200.0F);
        checkCalc(p, "payload.valueD*60L", 7200.0);
        checkCalc(p, "payload.valueJ*60L", 7200L);
        checkCalc(p, "payload.valueI*60L", 7200L);
        checkCalc(p, "payload.valueSB*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueBB*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueFB*payload.valueJB60", 7200.0F);
        checkCalc(p, "payload.valueDB*payload.valueJB60", 7200.0);
        checkCalc(p, "payload.valueJB*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueIB*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueS*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueB*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueF*payload.valueJB60", 7200.0F);
        checkCalc(p, "payload.valueD*payload.valueJB60", 7200.0);
        checkCalc(p, "payload.valueJ*payload.valueJB60", 7200L);
        checkCalc(p, "payload.valueI*payload.valueJB60", 7200L);
        // right is an int
        checkCalc(p, "payload.valueSB*60", 7200);
        checkCalc(p, "payload.valueBB*60", 7200);
        checkCalc(p, "payload.valueFB*60", 7200.0F);
        checkCalc(p, "payload.valueDB*60", 7200.0);
        checkCalc(p, "payload.valueJB*60", 7200L);
        checkCalc(p, "payload.valueIB*60", 7200);
        checkCalc(p, "payload.valueS*60", 7200);
        checkCalc(p, "payload.valueB*60", 7200);
        checkCalc(p, "payload.valueF*60", 7200.0F);
        checkCalc(p, "payload.valueD*60", 7200.0);
        checkCalc(p, "payload.valueJ*60", 7200L);
        checkCalc(p, "payload.valueI*60", 7200);
        checkCalc(p, "payload.valueSB*payload.valueIB60", 7200);
        checkCalc(p, "payload.valueBB*payload.valueIB60", 7200);
        checkCalc(p, "payload.valueFB*payload.valueIB60", 7200.0F);
        checkCalc(p, "payload.valueDB*payload.valueIB60", 7200.0);
        checkCalc(p, "payload.valueJB*payload.valueIB60", 7200L);
        checkCalc(p, "payload.valueIB*payload.valueIB60", 7200);
        checkCalc(p, "payload.valueS*payload.valueIB60", 7200);
        checkCalc(p, "payload.valueB*payload.valueIB60", 7200);
        checkCalc(p, "payload.valueF*payload.valueIB60", 7200.0F);
        checkCalc(p, "payload.valueD*payload.valueIB60", 7200.0);
        checkCalc(p, "payload.valueJ*payload.valueIB60", 7200L);
        checkCalc(p, "payload.valueI*payload.valueIB60", 7200);
        // right is a short
        checkCalc(p, "payload.valueSB*payload.valueS20", 2400);
        checkCalc(p, "payload.valueBB*payload.valueS20", 2400);
        checkCalc(p, "payload.valueFB*payload.valueS20", 2400.0F);
        checkCalc(p, "payload.valueDB*payload.valueS20", 2400.0);
        checkCalc(p, "payload.valueJB*payload.valueS20", 2400L);
        checkCalc(p, "payload.valueIB*payload.valueS20", 2400);
        checkCalc(p, "payload.valueS*payload.valueS20", 2400);
        checkCalc(p, "payload.valueB*payload.valueS20", 2400);
        checkCalc(p, "payload.valueF*payload.valueS20", 2400.0F);
        checkCalc(p, "payload.valueD*payload.valueS20", 2400.0);
        checkCalc(p, "payload.valueJ*payload.valueS20", 2400L);
        checkCalc(p, "payload.valueI*payload.valueS20", 2400);
        checkCalc(p, "payload.valueSB*payload.valueSB20", 2400);
        checkCalc(p, "payload.valueBB*payload.valueSB20", 2400);
        checkCalc(p, "payload.valueFB*payload.valueSB20", 2400.0F);
        checkCalc(p, "payload.valueDB*payload.valueSB20", 2400.0);
        checkCalc(p, "payload.valueJB*payload.valueSB20", 2400L);
        checkCalc(p, "payload.valueIB*payload.valueSB20", 2400);
        checkCalc(p, "payload.valueS*payload.valueSB20", 2400);
        checkCalc(p, "payload.valueB*payload.valueSB20", 2400);
        checkCalc(p, "payload.valueF*payload.valueSB20", 2400.0F);
        checkCalc(p, "payload.valueD*payload.valueSB20", 2400.0);
        checkCalc(p, "payload.valueJ*payload.valueSB20", 2400L);
        checkCalc(p, "payload.valueI*payload.valueSB20", 2400);
        // right is a byte
        checkCalc(p, "payload.valueSB*payload.valueB20", 2400);
        checkCalc(p, "payload.valueBB*payload.valueB20", 2400);
        checkCalc(p, "payload.valueFB*payload.valueB20", 2400.0F);
        checkCalc(p, "payload.valueDB*payload.valueB20", 2400.0);
        checkCalc(p, "payload.valueJB*payload.valueB20", 2400L);
        checkCalc(p, "payload.valueIB*payload.valueB20", 2400);
        checkCalc(p, "payload.valueS*payload.valueB20", 2400);
        checkCalc(p, "payload.valueB*payload.valueB20", 2400);
        checkCalc(p, "payload.valueF*payload.valueB20", 2400.0F);
        checkCalc(p, "payload.valueD*payload.valueB20", 2400.0);
        checkCalc(p, "payload.valueJ*payload.valueB20", 2400L);
        checkCalc(p, "payload.valueI*payload.valueB20", 2400);
        checkCalc(p, "payload.valueSB*payload.valueBB20", 2400);
        checkCalc(p, "payload.valueBB*payload.valueBB20", 2400);
        checkCalc(p, "payload.valueFB*payload.valueBB20", 2400.0F);
        checkCalc(p, "payload.valueDB*payload.valueBB20", 2400.0);
        checkCalc(p, "payload.valueJB*payload.valueBB20", 2400L);
        checkCalc(p, "payload.valueIB*payload.valueBB20", 2400);
        checkCalc(p, "payload.valueS*payload.valueBB20", 2400);
        checkCalc(p, "payload.valueB*payload.valueBB20", 2400);
        checkCalc(p, "payload.valueF*payload.valueBB20", 2400.0F);
        checkCalc(p, "payload.valueD*payload.valueBB20", 2400.0);
        checkCalc(p, "payload.valueJ*payload.valueBB20", 2400L);
        checkCalc(p, "payload.valueI*payload.valueBB20", 2400);
    }

    @Test
    public void opModulus_mixedNumberTypes() throws Exception {
        SpelCompilationCoverageTests.PayloadX p = new SpelCompilationCoverageTests.PayloadX();
        // This is what you had to do before the changes in order for it to compile:
        // expression = parse("(T(java.lang.Double).parseDouble(payload.valueI.toString()))/60D");
        // right is a double
        checkCalc(p, "payload.valueSB%58D", 4.0);
        checkCalc(p, "payload.valueBB%58D", 4.0);
        checkCalc(p, "payload.valueFB%58D", 4.0);
        checkCalc(p, "payload.valueDB%58D", 4.0);
        checkCalc(p, "payload.valueJB%58D", 4.0);
        checkCalc(p, "payload.valueIB%58D", 4.0);
        checkCalc(p, "payload.valueS%58D", 4.0);
        checkCalc(p, "payload.valueB%58D", 4.0);
        checkCalc(p, "payload.valueF%58D", 4.0);
        checkCalc(p, "payload.valueD%58D", 4.0);
        checkCalc(p, "payload.valueJ%58D", 4.0);
        checkCalc(p, "payload.valueI%58D", 4.0);
        checkCalc(p, "payload.valueSB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueBB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueFB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueDB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueJB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueIB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueS%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueB%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueF%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueD%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueJ%payload.valueDB58", 4.0);
        checkCalc(p, "payload.valueI%payload.valueDB58", 4.0);
        // right is a float
        checkCalc(p, "payload.valueSB%58F", 4.0F);
        checkCalc(p, "payload.valueBB%58F", 4.0F);
        checkCalc(p, "payload.valueFB%58F", 4.0F);
        checkCalc(p, "payload.valueDB%58F", 4.0);
        checkCalc(p, "payload.valueJB%58F", 4.0F);
        checkCalc(p, "payload.valueIB%58F", 4.0F);
        checkCalc(p, "payload.valueS%58F", 4.0F);
        checkCalc(p, "payload.valueB%58F", 4.0F);
        checkCalc(p, "payload.valueF%58F", 4.0F);
        checkCalc(p, "payload.valueD%58F", 4.0);
        checkCalc(p, "payload.valueJ%58F", 4.0F);
        checkCalc(p, "payload.valueI%58F", 4.0F);
        checkCalc(p, "payload.valueSB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueBB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueFB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueDB%payload.valueFB58", 4.0);
        checkCalc(p, "payload.valueJB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueIB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueS%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueB%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueF%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueD%payload.valueFB58", 4.0);
        checkCalc(p, "payload.valueJ%payload.valueFB58", 4.0F);
        checkCalc(p, "payload.valueI%payload.valueFB58", 4.0F);
        // right is a long
        checkCalc(p, "payload.valueSB%58L", 4L);
        checkCalc(p, "payload.valueBB%58L", 4L);
        checkCalc(p, "payload.valueFB%58L", 4.0F);
        checkCalc(p, "payload.valueDB%58L", 4.0);
        checkCalc(p, "payload.valueJB%58L", 4L);
        checkCalc(p, "payload.valueIB%58L", 4L);
        checkCalc(p, "payload.valueS%58L", 4L);
        checkCalc(p, "payload.valueB%58L", 4L);
        checkCalc(p, "payload.valueF%58L", 4.0F);
        checkCalc(p, "payload.valueD%58L", 4.0);
        checkCalc(p, "payload.valueJ%58L", 4L);
        checkCalc(p, "payload.valueI%58L", 4L);
        checkCalc(p, "payload.valueSB%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueBB%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueFB%payload.valueJB58", 4.0F);
        checkCalc(p, "payload.valueDB%payload.valueJB58", 4.0);
        checkCalc(p, "payload.valueJB%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueIB%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueS%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueB%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueF%payload.valueJB58", 4.0F);
        checkCalc(p, "payload.valueD%payload.valueJB58", 4.0);
        checkCalc(p, "payload.valueJ%payload.valueJB58", 4L);
        checkCalc(p, "payload.valueI%payload.valueJB58", 4L);
        // right is an int
        checkCalc(p, "payload.valueSB%58", 4);
        checkCalc(p, "payload.valueBB%58", 4);
        checkCalc(p, "payload.valueFB%58", 4.0F);
        checkCalc(p, "payload.valueDB%58", 4.0);
        checkCalc(p, "payload.valueJB%58", 4L);
        checkCalc(p, "payload.valueIB%58", 4);
        checkCalc(p, "payload.valueS%58", 4);
        checkCalc(p, "payload.valueB%58", 4);
        checkCalc(p, "payload.valueF%58", 4.0F);
        checkCalc(p, "payload.valueD%58", 4.0);
        checkCalc(p, "payload.valueJ%58", 4L);
        checkCalc(p, "payload.valueI%58", 4);
        checkCalc(p, "payload.valueSB%payload.valueIB58", 4);
        checkCalc(p, "payload.valueBB%payload.valueIB58", 4);
        checkCalc(p, "payload.valueFB%payload.valueIB58", 4.0F);
        checkCalc(p, "payload.valueDB%payload.valueIB58", 4.0);
        checkCalc(p, "payload.valueJB%payload.valueIB58", 4L);
        checkCalc(p, "payload.valueIB%payload.valueIB58", 4);
        checkCalc(p, "payload.valueS%payload.valueIB58", 4);
        checkCalc(p, "payload.valueB%payload.valueIB58", 4);
        checkCalc(p, "payload.valueF%payload.valueIB58", 4.0F);
        checkCalc(p, "payload.valueD%payload.valueIB58", 4.0);
        checkCalc(p, "payload.valueJ%payload.valueIB58", 4L);
        checkCalc(p, "payload.valueI%payload.valueIB58", 4);
        // right is a short
        checkCalc(p, "payload.valueSB%payload.valueS18", 12);
        checkCalc(p, "payload.valueBB%payload.valueS18", 12);
        checkCalc(p, "payload.valueFB%payload.valueS18", 12.0F);
        checkCalc(p, "payload.valueDB%payload.valueS18", 12.0);
        checkCalc(p, "payload.valueJB%payload.valueS18", 12L);
        checkCalc(p, "payload.valueIB%payload.valueS18", 12);
        checkCalc(p, "payload.valueS%payload.valueS18", 12);
        checkCalc(p, "payload.valueB%payload.valueS18", 12);
        checkCalc(p, "payload.valueF%payload.valueS18", 12.0F);
        checkCalc(p, "payload.valueD%payload.valueS18", 12.0);
        checkCalc(p, "payload.valueJ%payload.valueS18", 12L);
        checkCalc(p, "payload.valueI%payload.valueS18", 12);
        checkCalc(p, "payload.valueSB%payload.valueSB18", 12);
        checkCalc(p, "payload.valueBB%payload.valueSB18", 12);
        checkCalc(p, "payload.valueFB%payload.valueSB18", 12.0F);
        checkCalc(p, "payload.valueDB%payload.valueSB18", 12.0);
        checkCalc(p, "payload.valueJB%payload.valueSB18", 12L);
        checkCalc(p, "payload.valueIB%payload.valueSB18", 12);
        checkCalc(p, "payload.valueS%payload.valueSB18", 12);
        checkCalc(p, "payload.valueB%payload.valueSB18", 12);
        checkCalc(p, "payload.valueF%payload.valueSB18", 12.0F);
        checkCalc(p, "payload.valueD%payload.valueSB18", 12.0);
        checkCalc(p, "payload.valueJ%payload.valueSB18", 12L);
        checkCalc(p, "payload.valueI%payload.valueSB18", 12);
        // right is a byte
        checkCalc(p, "payload.valueSB%payload.valueB18", 12);
        checkCalc(p, "payload.valueBB%payload.valueB18", 12);
        checkCalc(p, "payload.valueFB%payload.valueB18", 12.0F);
        checkCalc(p, "payload.valueDB%payload.valueB18", 12.0);
        checkCalc(p, "payload.valueJB%payload.valueB18", 12L);
        checkCalc(p, "payload.valueIB%payload.valueB18", 12);
        checkCalc(p, "payload.valueS%payload.valueB18", 12);
        checkCalc(p, "payload.valueB%payload.valueB18", 12);
        checkCalc(p, "payload.valueF%payload.valueB18", 12.0F);
        checkCalc(p, "payload.valueD%payload.valueB18", 12.0);
        checkCalc(p, "payload.valueJ%payload.valueB18", 12L);
        checkCalc(p, "payload.valueI%payload.valueB18", 12);
        checkCalc(p, "payload.valueSB%payload.valueBB18", 12);
        checkCalc(p, "payload.valueBB%payload.valueBB18", 12);
        checkCalc(p, "payload.valueFB%payload.valueBB18", 12.0F);
        checkCalc(p, "payload.valueDB%payload.valueBB18", 12.0);
        checkCalc(p, "payload.valueJB%payload.valueBB18", 12L);
        checkCalc(p, "payload.valueIB%payload.valueBB18", 12);
        checkCalc(p, "payload.valueS%payload.valueBB18", 12);
        checkCalc(p, "payload.valueB%payload.valueBB18", 12);
        checkCalc(p, "payload.valueF%payload.valueBB18", 12.0F);
        checkCalc(p, "payload.valueD%payload.valueBB18", 12.0);
        checkCalc(p, "payload.valueJ%payload.valueBB18", 12L);
        checkCalc(p, "payload.valueI%payload.valueBB18", 12);
    }

    @Test
    public void opMultiply() throws Exception {
        expression = parse("2*2");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue());
        expression = parse("2L*2L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4L, expression.getValue());
        expression = parse("2.0f*2.0f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(4.0F, expression.getValue());
        expression = parse("3.0d*4.0d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(12.0, expression.getValue());
        expression = parse("T(Float).valueOf(2.0f)*6");
        Assert.assertEquals(12.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(12.0F, expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)*T(Float).valueOf(3.0f)");
        Assert.assertEquals(24.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(24.0F, expression.getValue());
        expression = parse("11L*T(Long).valueOf(4L)");
        Assert.assertEquals(44L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(44L, expression.getValue());
        expression = parse("T(Long).valueOf(9L)*6");
        Assert.assertEquals(54L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(54L, expression.getValue());
        expression = parse("T(Long).valueOf(4L)*T(Long).valueOf(3L)");
        Assert.assertEquals(12L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(12L, expression.getValue());
        expression = parse("8L*T(Long).valueOf(2L)");
        Assert.assertEquals(16L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(16L, expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)*-T(Float).valueOf(3.0f)");
        Assert.assertEquals((-24.0F), expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals((-24.0F), expression.getValue());
    }

    @Test
    public void opDivide() throws Exception {
        expression = parse("2/2");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1, expression.getValue());
        expression = parse("2L/2L");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("2.0f/2.0f");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(1.0F, expression.getValue());
        expression = parse("3.0d/4.0d");
        expression.getValue();
        assertCanCompile(expression);
        Assert.assertEquals(0.75, expression.getValue());
        expression = parse("T(Float).valueOf(6.0f)/2");
        Assert.assertEquals(3.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(3.0F, expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)/T(Float).valueOf(2.0f)");
        Assert.assertEquals(4.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(4.0F, expression.getValue());
        expression = parse("12L/T(Long).valueOf(4L)");
        Assert.assertEquals(3L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(3L, expression.getValue());
        expression = parse("T(Long).valueOf(44L)/11");
        Assert.assertEquals(4L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(4L, expression.getValue());
        expression = parse("T(Long).valueOf(4L)/T(Long).valueOf(2L)");
        Assert.assertEquals(2L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(2L, expression.getValue());
        expression = parse("8L/T(Long).valueOf(2L)");
        Assert.assertEquals(4L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(4L, expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)/-T(Float).valueOf(4.0f)");
        Assert.assertEquals((-2.0F), expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals((-2.0F), expression.getValue());
    }

    @Test
    public void opModulus_12041() throws Exception {
        expression = parse("2%2");
        Assert.assertEquals(0, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(0, expression.getValue());
        expression = parse("payload%2==0");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.TYPE));
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(5), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.TYPE));
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(5), Boolean.TYPE));
        expression = parse("8%3");
        Assert.assertEquals(2, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue());
        expression = parse("17L%5L");
        Assert.assertEquals(2L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(2L, expression.getValue());
        expression = parse("3.0f%2.0f");
        Assert.assertEquals(1.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1.0F, expression.getValue());
        expression = parse("3.0d%4.0d");
        Assert.assertEquals(3.0, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(3.0, expression.getValue());
        expression = parse("T(Float).valueOf(6.0f)%2");
        Assert.assertEquals(0.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(0.0F, expression.getValue());
        expression = parse("T(Float).valueOf(6.0f)%4");
        Assert.assertEquals(2.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(2.0F, expression.getValue());
        expression = parse("T(Float).valueOf(8.0f)%T(Float).valueOf(3.0f)");
        Assert.assertEquals(2.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(2.0F, expression.getValue());
        expression = parse("13L%T(Long).valueOf(4L)");
        Assert.assertEquals(1L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("T(Long).valueOf(44L)%12");
        Assert.assertEquals(8L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(8L, expression.getValue());
        expression = parse("T(Long).valueOf(9L)%T(Long).valueOf(2L)");
        Assert.assertEquals(1L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("7L%T(Long).valueOf(2L)");
        Assert.assertEquals(1L, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1L, expression.getValue());
        expression = parse("T(Float).valueOf(9.0f)%-T(Float).valueOf(4.0f)");
        Assert.assertEquals(1.0F, expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals(1.0F, expression.getValue());
    }

    @Test
    public void compilationOfBasicNullSafeMethodReference() {
        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(OFF, getClass().getClassLoader()));
        SpelExpression expression = parser.parseRaw("#it?.equals(3)");
        StandardEvaluationContext context = new StandardEvaluationContext(new Object[]{ 1 });
        context.setVariable("it", 3);
        expression.setEvaluationContext(context);
        Assert.assertTrue(expression.getValue(Boolean.class));
        context.setVariable("it", null);
        Assert.assertNull(expression.getValue(Boolean.class));
        assertCanCompile(expression);
        context.setVariable("it", 3);
        Assert.assertTrue(expression.getValue(Boolean.class));
        context.setVariable("it", null);
        Assert.assertNull(expression.getValue(Boolean.class));
    }

    @Test
    public void failsWhenSettingContextForExpression_SPR12326() {
        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(OFF, getClass().getClassLoader()));
        SpelCompilationCoverageTests.Person3 person = new SpelCompilationCoverageTests.Person3("foo", 1);
        SpelExpression expression = parser.parseRaw("#it?.age?.equals([0])");
        StandardEvaluationContext context = new StandardEvaluationContext(new Object[]{ 1 });
        context.setVariable("it", person);
        expression.setEvaluationContext(context);
        Assert.assertTrue(expression.getValue(Boolean.class));
        // This will trigger compilation (second usage)
        Assert.assertTrue(expression.getValue(Boolean.class));
        context.setVariable("it", null);
        Assert.assertNull(expression.getValue(Boolean.class));
        assertCanCompile(expression);
        context.setVariable("it", person);
        Assert.assertTrue(expression.getValue(Boolean.class));
        context.setVariable("it", null);
        Assert.assertNull(expression.getValue(Boolean.class));
    }

    /**
     * Test variants of using T(...) and static/non-static method/property/field references.
     */
    @Test
    public void constructorReference_SPR13781() {
        // Static field access on a T() referenced type
        expression = parser.parseExpression("T(java.util.Locale).ENGLISH");
        Assert.assertEquals("en", expression.getValue().toString());
        assertCanCompile(expression);
        Assert.assertEquals("en", expression.getValue().toString());
        // The actual expression from the bug report. It fails if the ENGLISH reference fails
        // to pop the type reference for Locale off the stack (if it isn't popped then
        // toLowerCase() will be called with a Locale parameter). In this situation the
        // code generation for ENGLISH should notice there is something on the stack that
        // is not required and pop it off.
        expression = parser.parseExpression("#userId.toString().toLowerCase(T(java.util.Locale).ENGLISH)");
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("userId", "RoDnEy");
        Assert.assertEquals("rodney", expression.getValue(context));
        assertCanCompile(expression);
        Assert.assertEquals("rodney", expression.getValue(context));
        // Property access on a class object
        expression = parser.parseExpression("T(String).name");
        Assert.assertEquals("java.lang.String", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("java.lang.String", expression.getValue());
        // Now the type reference isn't on the stack, and needs loading
        context = new StandardEvaluationContext(String.class);
        expression = parser.parseExpression("name");
        Assert.assertEquals("java.lang.String", expression.getValue(context));
        assertCanCompile(expression);
        Assert.assertEquals("java.lang.String", expression.getValue(context));
        expression = parser.parseExpression("T(String).getName()");
        Assert.assertEquals("java.lang.String", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("java.lang.String", expression.getValue());
        // These tests below verify that the chain of static accesses (either method/property or field)
        // leave the right thing on top of the stack for processing by any outer consuming code.
        // Here the consuming code is the String.valueOf() function.  If the wrong thing were on
        // the stack (for example if the compiled code for static methods wasn't popping the
        // previous thing off the stack) the valueOf() would operate on the wrong value.
        String shclass = SpelCompilationCoverageTests.StaticsHelper.class.getName();
        // Basic chain: property access then method access
        expression = parser.parseExpression("T(String).valueOf(T(String).name.valueOf(1))");
        Assert.assertEquals("1", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("1", expression.getValue());
        // chain of statics ending with static method
        expression = parser.parseExpression((("T(String).valueOf(T(" + shclass) + ").methoda().methoda().methodb())"));
        Assert.assertEquals("mb", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("mb", expression.getValue());
        // chain of statics ending with static field
        expression = parser.parseExpression((("T(String).valueOf(T(" + shclass) + ").fielda.fielda.fieldb)"));
        Assert.assertEquals("fb", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("fb", expression.getValue());
        // chain of statics ending with static property access
        expression = parser.parseExpression((("T(String).valueOf(T(" + shclass) + ").propertya.propertya.propertyb)"));
        Assert.assertEquals("pb", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("pb", expression.getValue());
        // variety chain
        expression = parser.parseExpression((("T(String).valueOf(T(" + shclass) + ").fielda.methoda().propertya.fieldb)"));
        Assert.assertEquals("fb", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("fb", expression.getValue());
        expression = parser.parseExpression("T(String).valueOf(fielda.fieldb)");
        Assert.assertEquals("fb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
        assertCanCompile(expression);
        Assert.assertEquals("fb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
        expression = parser.parseExpression("T(String).valueOf(propertya.propertyb)");
        Assert.assertEquals("pb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
        assertCanCompile(expression);
        Assert.assertEquals("pb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
        expression = parser.parseExpression("T(String).valueOf(methoda().methodb())");
        Assert.assertEquals("mb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
        assertCanCompile(expression);
        Assert.assertEquals("mb", expression.getValue(SpelCompilationCoverageTests.StaticsHelper.sh));
    }

    @Test
    public void constructorReference_SPR12326() {
        String type = getClass().getName();
        String prefix = ("new " + type) + ".Obj";
        expression = parser.parseExpression((prefix + "([0])"));
        Assert.assertEquals("test", ((SpelCompilationCoverageTests.Obj) (expression.getValue(new Object[]{ "test" }))).param1);
        assertCanCompile(expression);
        Assert.assertEquals("test", ((SpelCompilationCoverageTests.Obj) (expression.getValue(new Object[]{ "test" }))).param1);
        expression = parser.parseExpression((prefix + "2('foo','bar').output"));
        Assert.assertEquals("foobar", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("foobar", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "2('foo').output"));
        Assert.assertEquals("foo", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("foo", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "2().output"));
        Assert.assertEquals("", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3(1,2,3).output"));
        Assert.assertEquals("123", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("123", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3(1).output"));
        Assert.assertEquals("1", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("1", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3().output"));
        Assert.assertEquals("", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3('abc',5.0f,1,2,3).output"));
        Assert.assertEquals("abc:5.0:123", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("abc:5.0:123", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3('abc',5.0f,1).output"));
        Assert.assertEquals("abc:5.0:1", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("abc:5.0:1", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "3('abc',5.0f).output"));
        Assert.assertEquals("abc:5.0:", expression.getValue(String.class));
        assertCanCompile(expression);
        Assert.assertEquals("abc:5.0:", expression.getValue(String.class));
        expression = parser.parseExpression((prefix + "4(#root).output"));
        Assert.assertEquals("123", expression.getValue(new int[]{ 1, 2, 3 }, String.class));
        assertCanCompile(expression);
        Assert.assertEquals("123", expression.getValue(new int[]{ 1, 2, 3 }, String.class));
    }

    @Test
    public void methodReferenceMissingCastAndRootObjectAccessing_SPR12326() {
        // Need boxing code on the 1 so that toString() can be called
        expression = parser.parseExpression("1.toString()");
        Assert.assertEquals("1", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("1", expression.getValue());
        expression = parser.parseExpression("#it?.age.equals([0])");
        SpelCompilationCoverageTests.Person person = new SpelCompilationCoverageTests.Person(1);
        StandardEvaluationContext context = new StandardEvaluationContext(new Object[]{ person.getAge() });
        context.setVariable("it", person);
        Assert.assertTrue(expression.getValue(context, Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(context, Boolean.class));
        // Variant of above more like what was in the bug report:
        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(IMMEDIATE, getClass().getClassLoader()));
        SpelExpression ex = parser.parseRaw("#it?.age.equals([0])");
        context = new StandardEvaluationContext(new Object[]{ person.getAge() });
        context.setVariable("it", person);
        Assert.assertTrue(ex.getValue(context, Boolean.class));
        Assert.assertTrue(ex.getValue(context, Boolean.class));
        PersonInOtherPackage person2 = new PersonInOtherPackage(1);
        ex = parser.parseRaw("#it?.age.equals([0])");
        context = new StandardEvaluationContext(new Object[]{ person2.getAge() });
        context.setVariable("it", person2);
        Assert.assertTrue(ex.getValue(context, Boolean.class));
        Assert.assertTrue(ex.getValue(context, Boolean.class));
        ex = parser.parseRaw("#it?.age.equals([0])");
        context = new StandardEvaluationContext(new Object[]{ person2.getAge() });
        context.setVariable("it", person2);
        Assert.assertTrue(((Boolean) (ex.getValue(context))));
        Assert.assertTrue(((Boolean) (ex.getValue(context))));
    }

    @Test
    public void constructorReference() throws Exception {
        // simple ctor
        expression = parser.parseExpression("new String('123')");
        Assert.assertEquals("123", expression.getValue());
        assertCanCompile(expression);
        Assert.assertEquals("123", expression.getValue());
        String testclass8 = "org.springframework.expression.spel.SpelCompilationCoverageTests$TestClass8";
        // multi arg ctor that includes primitives
        expression = parser.parseExpression((("new " + testclass8) + "(42,'123',4.0d,true)"));
        Assert.assertEquals(testclass8, expression.getValue().getClass().getName());
        assertCanCompile(expression);
        Object o = expression.getValue();
        Assert.assertEquals(testclass8, o.getClass().getName());
        SpelCompilationCoverageTests.TestClass8 tc8 = ((SpelCompilationCoverageTests.TestClass8) (o));
        Assert.assertEquals(42, tc8.i);
        Assert.assertEquals("123", tc8.s);
        Assert.assertEquals(4.0, tc8.d, 0.5);
        Assert.assertEquals(true, tc8.z);
        // no-arg ctor
        expression = parser.parseExpression((("new " + testclass8) + "()"));
        Assert.assertEquals(testclass8, expression.getValue().getClass().getName());
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals(testclass8, o.getClass().getName());
        // pass primitive to reference type ctor
        expression = parser.parseExpression((("new " + testclass8) + "(42)"));
        Assert.assertEquals(testclass8, expression.getValue().getClass().getName());
        assertCanCompile(expression);
        o = expression.getValue();
        Assert.assertEquals(testclass8, o.getClass().getName());
        tc8 = ((SpelCompilationCoverageTests.TestClass8) (o));
        Assert.assertEquals(42, tc8.i);
        // private class, can't compile it
        String testclass9 = "org.springframework.expression.spel.SpelCompilationCoverageTests$TestClass9";
        expression = parser.parseExpression((("new " + testclass9) + "(42)"));
        Assert.assertEquals(testclass9, expression.getValue().getClass().getName());
        assertCantCompile(expression);
    }

    @Test
    public void methodReferenceReflectiveMethodSelectionWithVarargs() throws Exception {
        SpelCompilationCoverageTests.TestClass10 tc = new SpelCompilationCoverageTests.TestClass10();
        // Should call the non varargs version of concat
        // (which causes the '::' prefix in test output)
        expression = parser.parseExpression("concat('test')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("::test", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("::test", tc.s);
        tc.reset();
        // This will call the varargs concat with an empty array
        expression = parser.parseExpression("concat()");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        tc.reset();
        // Should call the non varargs version of concat
        // (which causes the '::' prefix in test output)
        expression = parser.parseExpression("concat2('test')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("::test", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("::test", tc.s);
        tc.reset();
        // This will call the varargs concat with an empty array
        expression = parser.parseExpression("concat2()");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        tc.reset();
    }

    @Test
    public void methodReferenceVarargs() throws Exception {
        SpelCompilationCoverageTests.TestClass5 tc = new SpelCompilationCoverageTests.TestClass5();
        // varargs string
        expression = parser.parseExpression("eleven()");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("", tc.s);
        tc.reset();
        // varargs string
        expression = parser.parseExpression("eleven('aaa')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaa", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaa", tc.s);
        tc.reset();
        // varargs string
        expression = parser.parseExpression("eleven(stringArray)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        tc.reset();
        // varargs string
        expression = parser.parseExpression("eleven('aaa','bbb','ccc')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        tc.reset();
        expression = parser.parseExpression("sixteen('aaa','bbb','ccc')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaabbbccc", tc.s);
        tc.reset();
        // TODO Fails related to conversion service converting a String[] to satisfy Object...
        // expression = parser.parseExpression("sixteen(stringArray)");
        // assertCantCompile(expression);
        // expression.getValue(tc);
        // assertEquals("aaabbbccc", tc.s);
        // assertCanCompile(expression);
        // tc.reset();
        // expression.getValue(tc);
        // assertEquals("aaabbbccc", tc.s);
        // tc.reset();
        // varargs int
        expression = parser.parseExpression("twelve(1,2,3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals(6, tc.i);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(6, tc.i);
        tc.reset();
        expression = parser.parseExpression("twelve(1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals(1, tc.i);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(1, tc.i);
        tc.reset();
        // one string then varargs string
        expression = parser.parseExpression("thirteen('aaa','bbb','ccc')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaa::bbbccc", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaa::bbbccc", tc.s);
        tc.reset();
        // nothing passed to varargs parameter
        expression = parser.parseExpression("thirteen('aaa')");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaa::", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaa::", tc.s);
        tc.reset();
        // nested arrays
        expression = parser.parseExpression("fourteen('aaa',stringArray,stringArray)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaa::{aaabbbccc}{aaabbbccc}", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaa::{aaabbbccc}{aaabbbccc}", tc.s);
        tc.reset();
        // nested primitive array
        expression = parser.parseExpression("fifteen('aaa',intArray,intArray)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("aaa::{112233}{112233}", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("aaa::{112233}{112233}", tc.s);
        tc.reset();
        // varargs boolean
        expression = parser.parseExpression("arrayz(true,true,false)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("truetruefalse", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("truetruefalse", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayz(true)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("true", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("true", tc.s);
        tc.reset();
        // varargs short
        expression = parser.parseExpression("arrays(s1,s2,s3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrays(s1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1", tc.s);
        tc.reset();
        // varargs double
        expression = parser.parseExpression("arrayd(1.0d,2.0d,3.0d)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1.02.03.0", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1.02.03.0", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayd(1.0d)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1.0", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1.0", tc.s);
        tc.reset();
        // varargs long
        expression = parser.parseExpression("arrayj(l1,l2,l3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayj(l1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1", tc.s);
        tc.reset();
        // varargs char
        expression = parser.parseExpression("arrayc(c1,c2,c3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("abc", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("abc", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayc(c1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("a", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("a", tc.s);
        tc.reset();
        // varargs byte
        expression = parser.parseExpression("arrayb(b1,b2,b3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("656667", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("656667", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayb(b1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("65", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("65", tc.s);
        tc.reset();
        // varargs float
        expression = parser.parseExpression("arrayf(f1,f2,f3)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1.02.03.0", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1.02.03.0", tc.s);
        tc.reset();
        expression = parser.parseExpression("arrayf(f1)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("1.0", tc.s);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("1.0", tc.s);
        tc.reset();
    }

    @Test
    public void methodReference() throws Exception {
        SpelCompilationCoverageTests.TestClass5 tc = new SpelCompilationCoverageTests.TestClass5();
        // non-static method, no args, void return
        expression = parser.parseExpression("one()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(1, tc.i);
        tc.reset();
        // static method, no args, void return
        expression = parser.parseExpression("two()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(1, SpelCompilationCoverageTests.TestClass5._i);
        tc.reset();
        // non-static method, reference type return
        expression = parser.parseExpression("three()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        Assert.assertEquals("hello", expression.getValue(tc));
        tc.reset();
        // non-static method, primitive type return
        expression = parser.parseExpression("four()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        Assert.assertEquals(3277700L, expression.getValue(tc));
        tc.reset();
        // static method, reference type return
        expression = parser.parseExpression("five()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        Assert.assertEquals("hello", expression.getValue(tc));
        tc.reset();
        // static method, primitive type return
        expression = parser.parseExpression("six()");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        Assert.assertEquals(3277700L, expression.getValue(tc));
        tc.reset();
        // non-static method, one parameter of reference type
        expression = parser.parseExpression("seven(\"foo\")");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("foo", tc.s);
        tc.reset();
        // static method, one parameter of reference type
        expression = parser.parseExpression("eight(\"bar\")");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals("bar", SpelCompilationCoverageTests.TestClass5._s);
        tc.reset();
        // non-static method, one parameter of primitive type
        expression = parser.parseExpression("nine(231)");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(231, tc.i);
        tc.reset();
        // static method, one parameter of primitive type
        expression = parser.parseExpression("ten(111)");
        assertCantCompile(expression);
        expression.getValue(tc);
        assertCanCompile(expression);
        tc.reset();
        expression.getValue(tc);
        Assert.assertEquals(111, SpelCompilationCoverageTests.TestClass5._i);
        tc.reset();
        // method that gets type converted parameters
        // Converting from an int to a string
        expression = parser.parseExpression("seven(123)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        assertCantCompile(expression);// Uncompilable as argument conversion is occurring

        Expression expression = parser.parseExpression("'abcd'.substring(index1,index2)");
        String resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class);
        Assert.assertEquals("bc", resultI);
        Assert.assertEquals("bc", resultC);
        // Converting from an int to a Number
        expression = parser.parseExpression("takeNumber(123)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        tc.reset();
        assertCanCompile(expression);// The generated code should include boxing of the int to a Number

        expression.getValue(tc);
        Assert.assertEquals("123", tc.s);
        // Passing a subtype
        expression = parser.parseExpression("takeNumber(T(Integer).valueOf(42))");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("42", tc.s);
        tc.reset();
        assertCanCompile(expression);// The generated code should include boxing of the int to a Number

        expression.getValue(tc);
        Assert.assertEquals("42", tc.s);
        // Passing a subtype
        expression = parser.parseExpression("takeString(T(Integer).valueOf(42))");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("42", tc.s);
        tc.reset();
        assertCantCompile(expression);// method takes a string and we are passing an Integer

    }

    @Test
    public void errorHandling() throws Exception {
        SpelCompilationCoverageTests.TestClass5 tc = new SpelCompilationCoverageTests.TestClass5();
        // changing target
        // from primitive array to reference type array
        int[] is = new int[]{ 1, 2, 3 };
        String[] strings = new String[]{ "a", "b", "c" };
        expression = parser.parseExpression("[1]");
        Assert.assertEquals(2, expression.getValue(is));
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(is));
        try {
            Assert.assertEquals(2, expression.getValue(strings));
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertTrue(((see.getCause()) instanceof ClassCastException));
        }
        SpelCompiler.revertToInterpreted(expression);
        Assert.assertEquals("b", expression.getValue(strings));
        assertCanCompile(expression);
        Assert.assertEquals("b", expression.getValue(strings));
        tc.field = "foo";
        expression = parser.parseExpression("seven(field)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("foo", tc.s);
        assertCanCompile(expression);
        tc.reset();
        tc.field = "bar";
        expression.getValue(tc);
        // method with changing parameter types (change reference type)
        tc.obj = "foo";
        expression = parser.parseExpression("seven(obj)");
        assertCantCompile(expression);
        expression.getValue(tc);
        Assert.assertEquals("foo", tc.s);
        assertCanCompile(expression);
        tc.reset();
        tc.obj = new Integer(42);
        try {
            expression.getValue(tc);
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertTrue(((see.getCause()) instanceof ClassCastException));
        }
        // method with changing target
        expression = parser.parseExpression("#root.charAt(0)");
        Assert.assertEquals('a', expression.getValue("abc"));
        assertCanCompile(expression);
        try {
            expression.getValue(new Integer(42));
            Assert.fail();
        } catch (SpelEvaluationException see) {
            // java.lang.Integer cannot be cast to java.lang.String
            Assert.assertTrue(((see.getCause()) instanceof ClassCastException));
        }
    }

    @Test
    public void methodReference_staticMethod() throws Exception {
        Expression expression = parser.parseExpression("T(Integer).valueOf(42)");
        int resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Integer.TYPE);
        assertCanCompile(expression);
        int resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), Integer.TYPE);
        Assert.assertEquals(42, resultI);
        Assert.assertEquals(42, resultC);
    }

    @Test
    public void methodReference_literalArguments_int() throws Exception {
        Expression expression = parser.parseExpression("'abcd'.substring(1,3)");
        String resultI = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(new SpelCompilationCoverageTests.TestClass1(), String.class);
        Assert.assertEquals("bc", resultI);
        Assert.assertEquals("bc", resultC);
    }

    @Test
    public void methodReference_simpleInstanceMethodNoArg() throws Exception {
        Expression expression = parser.parseExpression("toString()");
        String resultI = expression.getValue(42, String.class);
        assertCanCompile(expression);
        String resultC = expression.getValue(42, String.class);
        Assert.assertEquals("42", resultI);
        Assert.assertEquals("42", resultC);
    }

    @Test
    public void methodReference_simpleInstanceMethodNoArgReturnPrimitive() throws Exception {
        expression = parser.parseExpression("intValue()");
        int resultI = expression.getValue(new Integer(42), Integer.TYPE);
        Assert.assertEquals(42, resultI);
        assertCanCompile(expression);
        int resultC = expression.getValue(new Integer(42), Integer.TYPE);
        Assert.assertEquals(42, resultC);
    }

    @Test
    public void methodReference_simpleInstanceMethodOneArgReturnPrimitive1() throws Exception {
        Expression expression = parser.parseExpression("indexOf('b')");
        int resultI = expression.getValue("abc", Integer.TYPE);
        assertCanCompile(expression);
        int resultC = expression.getValue("abc", Integer.TYPE);
        Assert.assertEquals(1, resultI);
        Assert.assertEquals(1, resultC);
    }

    @Test
    public void methodReference_simpleInstanceMethodOneArgReturnPrimitive2() throws Exception {
        expression = parser.parseExpression("charAt(2)");
        char resultI = expression.getValue("abc", Character.TYPE);
        Assert.assertEquals('c', resultI);
        assertCanCompile(expression);
        char resultC = expression.getValue("abc", Character.TYPE);
        Assert.assertEquals('c', resultC);
    }

    @Test
    public void compoundExpression() throws Exception {
        SpelCompilationCoverageTests.Payload payload = new SpelCompilationCoverageTests.Payload();
        expression = parser.parseExpression("DR[0]");
        Assert.assertEquals("instanceof Two", expression.getValue(payload).toString());
        assertCanCompile(expression);
        Assert.assertEquals("instanceof Two", expression.getValue(payload).toString());
        ast = getAst();
        Assert.assertEquals("Lorg/springframework/expression/spel/SpelCompilationCoverageTests$Two", ast.getExitDescriptor());
        expression = parser.parseExpression("holder.three");
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Three", expression.getValue(payload).getClass().getName());
        assertCanCompile(expression);
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Three", expression.getValue(payload).getClass().getName());
        ast = getAst();
        Assert.assertEquals("Lorg/springframework/expression/spel/SpelCompilationCoverageTests$Three", ast.getExitDescriptor());
        expression = parser.parseExpression("DR[0]");
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Two", expression.getValue(payload).getClass().getName());
        assertCanCompile(expression);
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Two", expression.getValue(payload).getClass().getName());
        Assert.assertEquals("Lorg/springframework/expression/spel/SpelCompilationCoverageTests$Two", getAst().getExitDescriptor());
        expression = parser.parseExpression("DR[0].three");
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Three", expression.getValue(payload).getClass().getName());
        assertCanCompile(expression);
        Assert.assertEquals("org.springframework.expression.spel.SpelCompilationCoverageTests$Three", expression.getValue(payload).getClass().getName());
        ast = getAst();
        Assert.assertEquals("Lorg/springframework/expression/spel/SpelCompilationCoverageTests$Three", ast.getExitDescriptor());
        expression = parser.parseExpression("DR[0].three.four");
        Assert.assertEquals(0.04, expression.getValue(payload));
        assertCanCompile(expression);
        Assert.assertEquals(0.04, expression.getValue(payload));
        Assert.assertEquals("D", getAst().getExitDescriptor());
    }

    @Test
    public void mixingItUp_indexerOpEqTernary() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("andy", "778");
        expression = parse("['andy']==null?1:2");
        Assert.assertEquals(2, expression.getValue(m));
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(m));
        m.remove("andy");
        Assert.assertEquals(1, expression.getValue(m));
    }

    @Test
    public void propertyReference() throws Exception {
        SpelCompilationCoverageTests.TestClass6 tc = new SpelCompilationCoverageTests.TestClass6();
        // non static field
        expression = parser.parseExpression("orange");
        assertCantCompile(expression);
        Assert.assertEquals("value1", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value1", expression.getValue(tc));
        // static field
        expression = parser.parseExpression("apple");
        assertCantCompile(expression);
        Assert.assertEquals("value2", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value2", expression.getValue(tc));
        // non static getter
        expression = parser.parseExpression("banana");
        assertCantCompile(expression);
        Assert.assertEquals("value3", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value3", expression.getValue(tc));
        // static getter
        expression = parser.parseExpression("plum");
        assertCantCompile(expression);
        Assert.assertEquals("value4", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value4", expression.getValue(tc));
    }

    @Test
    public void propertyReferenceVisibility_SPR12771() {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("httpServletRequest", SpelCompilationCoverageTests.HttpServlet3RequestFactory.getOne());
        // Without a fix compilation was inserting a checkcast to a private type
        expression = parser.parseExpression("#httpServletRequest.servletPath");
        Assert.assertEquals("wibble", expression.getValue(ctx));
        assertCanCompile(expression);
        Assert.assertEquals("wibble", expression.getValue(ctx));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void indexer() throws Exception {
        String[] sss = new String[]{ "a", "b", "c" };
        Number[] ns = new Number[]{ 2, 8, 9 };
        int[] is = new int[]{ 8, 9, 10 };
        double[] ds = new double[]{ 3.0, 4.0, 5.0 };
        long[] ls = new long[]{ 2L, 3L, 4L };
        short[] ss = new short[]{ ((short) (33)), ((short) (44)), ((short) (55)) };
        float[] fs = new float[]{ 6.0F, 7.0F, 8.0F };
        byte[] bs = new byte[]{ ((byte) (2)), ((byte) (3)), ((byte) (4)) };
        char[] cs = new char[]{ 'a', 'b', 'c' };
        // Access String (reference type) array
        expression = parser.parseExpression("[0]");
        Assert.assertEquals("a", expression.getValue(sss));
        assertCanCompile(expression);
        Assert.assertEquals("a", expression.getValue(sss));
        Assert.assertEquals("Ljava/lang/String", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1]");
        Assert.assertEquals(8, expression.getValue(ns));
        assertCanCompile(expression);
        Assert.assertEquals(8, expression.getValue(ns));
        Assert.assertEquals("Ljava/lang/Number", getAst().getExitDescriptor());
        // Access int array
        expression = parser.parseExpression("[2]");
        Assert.assertEquals(10, expression.getValue(is));
        assertCanCompile(expression);
        Assert.assertEquals(10, expression.getValue(is));
        Assert.assertEquals("I", getAst().getExitDescriptor());
        // Access double array
        expression = parser.parseExpression("[1]");
        Assert.assertEquals(4.0, expression.getValue(ds));
        assertCanCompile(expression);
        Assert.assertEquals(4.0, expression.getValue(ds));
        Assert.assertEquals("D", getAst().getExitDescriptor());
        // Access long array
        expression = parser.parseExpression("[0]");
        Assert.assertEquals(2L, expression.getValue(ls));
        assertCanCompile(expression);
        Assert.assertEquals(2L, expression.getValue(ls));
        Assert.assertEquals("J", getAst().getExitDescriptor());
        // Access short array
        expression = parser.parseExpression("[2]");
        Assert.assertEquals(((short) (55)), expression.getValue(ss));
        assertCanCompile(expression);
        Assert.assertEquals(((short) (55)), expression.getValue(ss));
        Assert.assertEquals("S", getAst().getExitDescriptor());
        // Access float array
        expression = parser.parseExpression("[0]");
        Assert.assertEquals(6.0F, expression.getValue(fs));
        assertCanCompile(expression);
        Assert.assertEquals(6.0F, expression.getValue(fs));
        Assert.assertEquals("F", getAst().getExitDescriptor());
        // Access byte array
        expression = parser.parseExpression("[2]");
        Assert.assertEquals(((byte) (4)), expression.getValue(bs));
        assertCanCompile(expression);
        Assert.assertEquals(((byte) (4)), expression.getValue(bs));
        Assert.assertEquals("B", getAst().getExitDescriptor());
        // Access char array
        expression = parser.parseExpression("[1]");
        Assert.assertEquals('b', expression.getValue(cs));
        assertCanCompile(expression);
        Assert.assertEquals('b', expression.getValue(cs));
        Assert.assertEquals("C", getAst().getExitDescriptor());
        // Collections
        List<String> strings = new ArrayList<>();
        strings.add("aaa");
        strings.add("bbb");
        strings.add("ccc");
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("bbb", expression.getValue(strings));
        assertCanCompile(expression);
        Assert.assertEquals("bbb", expression.getValue(strings));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        List<Integer> ints = new ArrayList<>();
        ints.add(123);
        ints.add(456);
        ints.add(789);
        expression = parser.parseExpression("[2]");
        Assert.assertEquals(789, expression.getValue(ints));
        assertCanCompile(expression);
        Assert.assertEquals(789, expression.getValue(ints));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        // Maps
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("aaa", 111);
        map1.put("bbb", 222);
        map1.put("ccc", 333);
        expression = parser.parseExpression("['aaa']");
        Assert.assertEquals(111, expression.getValue(map1));
        assertCanCompile(expression);
        Assert.assertEquals(111, expression.getValue(map1));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        // Object
        SpelCompilationCoverageTests.TestClass6 tc = new SpelCompilationCoverageTests.TestClass6();
        expression = parser.parseExpression("['orange']");
        Assert.assertEquals("value1", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value1", expression.getValue(tc));
        Assert.assertEquals("Ljava/lang/String", getAst().getExitDescriptor());
        expression = parser.parseExpression("['peach']");
        Assert.assertEquals(34L, expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals(34L, expression.getValue(tc));
        Assert.assertEquals("J", getAst().getExitDescriptor());
        // getter
        expression = parser.parseExpression("['banana']");
        Assert.assertEquals("value3", expression.getValue(tc));
        assertCanCompile(expression);
        Assert.assertEquals("value3", expression.getValue(tc));
        Assert.assertEquals("Ljava/lang/String", getAst().getExitDescriptor());
        // list of arrays
        List<String[]> listOfStringArrays = new ArrayList<>();
        listOfStringArrays.add(new String[]{ "a", "b", "c" });
        listOfStringArrays.add(new String[]{ "d", "e", "f" });
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("d e f", stringify(expression.getValue(listOfStringArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("d e f", stringify(expression.getValue(listOfStringArrays)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1][0]");
        Assert.assertEquals("d", stringify(expression.getValue(listOfStringArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("d", stringify(expression.getValue(listOfStringArrays)));
        Assert.assertEquals("Ljava/lang/String", getAst().getExitDescriptor());
        List<Integer[]> listOfIntegerArrays = new ArrayList<>();
        listOfIntegerArrays.add(new Integer[]{ 1, 2, 3 });
        listOfIntegerArrays.add(new Integer[]{ 4, 5, 6 });
        expression = parser.parseExpression("[0]");
        Assert.assertEquals("1 2 3", stringify(expression.getValue(listOfIntegerArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("1 2 3", stringify(expression.getValue(listOfIntegerArrays)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("[0][1]");
        Assert.assertEquals(2, expression.getValue(listOfIntegerArrays));
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(listOfIntegerArrays));
        Assert.assertEquals("Ljava/lang/Integer", getAst().getExitDescriptor());
        // array of lists
        List<String>[] stringArrayOfLists = new ArrayList[2];
        stringArrayOfLists[0] = new ArrayList<>();
        stringArrayOfLists[0].add("a");
        stringArrayOfLists[0].add("b");
        stringArrayOfLists[0].add("c");
        stringArrayOfLists[1] = new ArrayList<>();
        stringArrayOfLists[1].add("d");
        stringArrayOfLists[1].add("e");
        stringArrayOfLists[1].add("f");
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("d e f", stringify(expression.getValue(stringArrayOfLists)));
        assertCanCompile(expression);
        Assert.assertEquals("d e f", stringify(expression.getValue(stringArrayOfLists)));
        Assert.assertEquals("Ljava/util/ArrayList", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1][2]");
        Assert.assertEquals("f", stringify(expression.getValue(stringArrayOfLists)));
        assertCanCompile(expression);
        Assert.assertEquals("f", stringify(expression.getValue(stringArrayOfLists)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        // array of arrays
        String[][] referenceTypeArrayOfArrays = new String[][]{ new String[]{ "a", "b", "c" }, new String[]{ "d", "e", "f" } };
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("d e f", stringify(expression.getValue(referenceTypeArrayOfArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("[Ljava/lang/String", getAst().getExitDescriptor());
        Assert.assertEquals("d e f", stringify(expression.getValue(referenceTypeArrayOfArrays)));
        Assert.assertEquals("[Ljava/lang/String", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1][2]");
        Assert.assertEquals("f", stringify(expression.getValue(referenceTypeArrayOfArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("f", stringify(expression.getValue(referenceTypeArrayOfArrays)));
        Assert.assertEquals("Ljava/lang/String", getAst().getExitDescriptor());
        int[][] primitiveTypeArrayOfArrays = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 } };
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("4 5 6", stringify(expression.getValue(primitiveTypeArrayOfArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("4 5 6", stringify(expression.getValue(primitiveTypeArrayOfArrays)));
        Assert.assertEquals("[I", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1][2]");
        Assert.assertEquals("6", stringify(expression.getValue(primitiveTypeArrayOfArrays)));
        assertCanCompile(expression);
        Assert.assertEquals("6", stringify(expression.getValue(primitiveTypeArrayOfArrays)));
        Assert.assertEquals("I", getAst().getExitDescriptor());
        // list of lists of reference types
        List<List<String>> listOfListOfStrings = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        listOfListOfStrings.add(list);
        list = new ArrayList<>();
        list.add("d");
        list.add("e");
        list.add("f");
        listOfListOfStrings.add(list);
        expression = parser.parseExpression("[1]");
        Assert.assertEquals("d e f", stringify(expression.getValue(listOfListOfStrings)));
        assertCanCompile(expression);
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        Assert.assertEquals("d e f", stringify(expression.getValue(listOfListOfStrings)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("[1][2]");
        Assert.assertEquals("f", stringify(expression.getValue(listOfListOfStrings)));
        assertCanCompile(expression);
        Assert.assertEquals("f", stringify(expression.getValue(listOfListOfStrings)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        // Map of lists
        Map<String, List<String>> mapToLists = new HashMap<>();
        list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        mapToLists.put("foo", list);
        expression = parser.parseExpression("['foo']");
        Assert.assertEquals("a b c", stringify(expression.getValue(mapToLists)));
        assertCanCompile(expression);
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        Assert.assertEquals("a b c", stringify(expression.getValue(mapToLists)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("['foo'][2]");
        Assert.assertEquals("c", stringify(expression.getValue(mapToLists)));
        assertCanCompile(expression);
        Assert.assertEquals("c", stringify(expression.getValue(mapToLists)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        // Map to array
        Map<String, int[]> mapToIntArray = new HashMap<>();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.addPropertyAccessor(new SpelCompilationCoverageTests.CompilableMapAccessor());
        mapToIntArray.put("foo", new int[]{ 1, 2, 3 });
        expression = parser.parseExpression("['foo']");
        Assert.assertEquals("1 2 3", stringify(expression.getValue(mapToIntArray)));
        assertCanCompile(expression);
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        Assert.assertEquals("1 2 3", stringify(expression.getValue(mapToIntArray)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("['foo'][1]");
        Assert.assertEquals(2, expression.getValue(mapToIntArray));
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(mapToIntArray));
        expression = parser.parseExpression("foo");
        Assert.assertEquals("1 2 3", stringify(expression.getValue(ctx, mapToIntArray)));
        assertCanCompile(expression);
        Assert.assertEquals("1 2 3", stringify(expression.getValue(ctx, mapToIntArray)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
        expression = parser.parseExpression("foo[1]");
        Assert.assertEquals(2, expression.getValue(ctx, mapToIntArray));
        assertCanCompile(expression);
        Assert.assertEquals(2, expression.getValue(ctx, mapToIntArray));
        expression = parser.parseExpression("['foo'][2]");
        Assert.assertEquals("3", stringify(expression.getValue(ctx, mapToIntArray)));
        assertCanCompile(expression);
        Assert.assertEquals("3", stringify(expression.getValue(ctx, mapToIntArray)));
        Assert.assertEquals("I", getAst().getExitDescriptor());
        // Map array
        Map<String, String>[] mapArray = new Map[1];
        mapArray[0] = new HashMap<>();
        mapArray[0].put("key", "value1");
        expression = parser.parseExpression("[0]");
        Assert.assertEquals("{key=value1}", stringify(expression.getValue(mapArray)));
        assertCanCompile(expression);
        Assert.assertEquals("Ljava/util/Map", getAst().getExitDescriptor());
        Assert.assertEquals("{key=value1}", stringify(expression.getValue(mapArray)));
        Assert.assertEquals("Ljava/util/Map", getAst().getExitDescriptor());
        expression = parser.parseExpression("[0]['key']");
        Assert.assertEquals("value1", stringify(expression.getValue(mapArray)));
        assertCanCompile(expression);
        Assert.assertEquals("value1", stringify(expression.getValue(mapArray)));
        Assert.assertEquals("Ljava/lang/Object", getAst().getExitDescriptor());
    }

    @Test
    public void plusNeedingCheckcast_SPR12426() {
        expression = parser.parseExpression("object + ' world'");
        Object v = expression.getValue(new SpelCompilationCoverageTests.FooObject());
        Assert.assertEquals("hello world", v);
        assertCanCompile(expression);
        Assert.assertEquals("hello world", v);
        expression = parser.parseExpression("object + ' world'");
        v = expression.getValue(new SpelCompilationCoverageTests.FooString());
        Assert.assertEquals("hello world", v);
        assertCanCompile(expression);
        Assert.assertEquals("hello world", v);
    }

    @Test
    public void mixingItUp_propertyAccessIndexerOpLtTernaryRootNull() throws Exception {
        SpelCompilationCoverageTests.Payload payload = new SpelCompilationCoverageTests.Payload();
        expression = parser.parseExpression("DR[0].three");
        Object v = expression.getValue(payload);
        Assert.assertEquals("Lorg/springframework/expression/spel/SpelCompilationCoverageTests$Three", getAst().getExitDescriptor());
        Expression expression = parser.parseExpression("DR[0].three.four lt 0.1d?#root:null");
        v = expression.getValue(payload);
        SpelExpression sExpr = ((SpelExpression) (expression));
        Ternary ternary = ((Ternary) (sExpr.getAST()));
        OpLT oplt = ((OpLT) (ternary.getChild(0)));
        CompoundExpression cExpr = ((CompoundExpression) (oplt.getLeftOperand()));
        String cExprExitDescriptor = cExpr.getExitDescriptor();
        Assert.assertEquals("D", cExprExitDescriptor);
        Assert.assertEquals("Z", oplt.getExitDescriptor());
        assertCanCompile(expression);
        Object vc = expression.getValue(payload);
        Assert.assertEquals(payload, v);
        Assert.assertEquals(payload, vc);
        payload.DR[0].three.four = 0.13;
        vc = expression.getValue(payload);
        Assert.assertNull(vc);
    }

    @Test
    public void variantGetter() throws Exception {
        SpelCompilationCoverageTests.Payload2Holder holder = new SpelCompilationCoverageTests.Payload2Holder();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.addPropertyAccessor(new SpelCompilationCoverageTests.MyAccessor());
        expression = parser.parseExpression("payload2.var1");
        Object v = expression.getValue(ctx, holder);
        Assert.assertEquals("abc", v);
        // // time it interpreted
        // long stime = System.currentTimeMillis();
        // for (int i = 0; i < 100000; i++) {
        // v = expression.getValue(ctx,holder);
        // }
        // System.out.println((System.currentTimeMillis() - stime));
        assertCanCompile(expression);
        v = expression.getValue(ctx, holder);
        Assert.assertEquals("abc", v);
        // // time it compiled
        // stime = System.currentTimeMillis();
        // for (int i = 0; i < 100000; i++) {
        // v = expression.getValue(ctx,holder);
        // }
        // System.out.println((System.currentTimeMillis() - stime));
    }

    @Test
    public void compilerWithGenerics_12040() {
        expression = parser.parseExpression("payload!=2");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.class));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(2), Boolean.class));
        expression = parser.parseExpression("2!=payload");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.class));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(2), Boolean.class));
        expression = parser.parseExpression("payload!=6L");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4L), Boolean.class));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6L), Boolean.class));
        expression = parser.parseExpression("payload==2");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(2), Boolean.class));
        expression = parser.parseExpression("2==payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(2), Boolean.class));
        expression = parser.parseExpression("payload==6L");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4L), Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6L), Boolean.class));
        expression = parser.parseExpression("2==payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4), Boolean.class));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(2), Boolean.class));
        expression = parser.parseExpression("payload/2");
        Assert.assertEquals(2, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(3, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6)));
        expression = parser.parseExpression("100/payload");
        Assert.assertEquals(25, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(10, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10)));
        expression = parser.parseExpression("payload+2");
        Assert.assertEquals(6, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(8, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6)));
        expression = parser.parseExpression("100+payload");
        Assert.assertEquals(104, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(110, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10)));
        expression = parser.parseExpression("payload-2");
        Assert.assertEquals(2, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6)));
        expression = parser.parseExpression("100-payload");
        Assert.assertEquals(96, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(90, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10)));
        expression = parser.parseExpression("payload*2");
        Assert.assertEquals(8, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(12, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6)));
        expression = parser.parseExpression("100*payload");
        Assert.assertEquals(400, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4)));
        assertCanCompile(expression);
        Assert.assertEquals(1000, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10)));
        expression = parser.parseExpression("payload/2L");
        Assert.assertEquals(2L, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4L)));
        assertCanCompile(expression);
        Assert.assertEquals(3L, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6L)));
        expression = parser.parseExpression("100L/payload");
        Assert.assertEquals(25L, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4L)));
        assertCanCompile(expression);
        Assert.assertEquals(10L, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10L)));
        expression = parser.parseExpression("payload/2f");
        Assert.assertEquals(2.0F, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4.0F)));
        assertCanCompile(expression);
        Assert.assertEquals(3.0F, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6.0F)));
        expression = parser.parseExpression("100f/payload");
        Assert.assertEquals(25.0F, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4.0F)));
        assertCanCompile(expression);
        Assert.assertEquals(10.0F, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10.0F)));
        expression = parser.parseExpression("payload/2d");
        Assert.assertEquals(2.0, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4.0)));
        assertCanCompile(expression);
        Assert.assertEquals(3.0, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(6.0)));
        expression = parser.parseExpression("100d/payload");
        Assert.assertEquals(25.0, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(4.0)));
        assertCanCompile(expression);
        Assert.assertEquals(10.0, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper(10.0)));
    }

    // The new helper class here uses an upper bound on the generic
    @Test
    public void compilerWithGenerics_12040_2() {
        expression = parser.parseExpression("payload/2");
        Assert.assertEquals(2, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(3, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6)));
        expression = parser.parseExpression("9/payload");
        Assert.assertEquals(1, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(9)));
        assertCanCompile(expression);
        Assert.assertEquals(3, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(3)));
        expression = parser.parseExpression("payload+2");
        Assert.assertEquals(6, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(8, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6)));
        expression = parser.parseExpression("100+payload");
        Assert.assertEquals(104, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(110, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(10)));
        expression = parser.parseExpression("payload-2");
        Assert.assertEquals(2, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(4, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6)));
        expression = parser.parseExpression("100-payload");
        Assert.assertEquals(96, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(90, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(10)));
        expression = parser.parseExpression("payload*2");
        Assert.assertEquals(8, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(12, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6)));
        expression = parser.parseExpression("100*payload");
        Assert.assertEquals(400, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4)));
        assertCanCompile(expression);
        Assert.assertEquals(1000, expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(10)));
    }

    // The other numeric operators
    @Test
    public void compilerWithGenerics_12040_3() {
        expression = parser.parseExpression("payload >= 2");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        expression = parser.parseExpression("2 >= payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(5), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        expression = parser.parseExpression("payload > 2");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(4), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        expression = parser.parseExpression("2 > payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(5), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        expression = parser.parseExpression("payload <=2");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6), Boolean.TYPE));
        expression = parser.parseExpression("2 <= payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6), Boolean.TYPE));
        expression = parser.parseExpression("payload < 2");
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6), Boolean.TYPE));
        expression = parser.parseExpression("2 < payload");
        Assert.assertFalse(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(1), Boolean.TYPE));
        assertCanCompile(expression);
        Assert.assertTrue(expression.getValue(new SpelCompilationCoverageTests.GenericMessageTestHelper2(6), Boolean.TYPE));
    }

    @Test
    public void indexerMapAccessor_12045() throws Exception {
        SpelParserConfiguration spc = new SpelParserConfiguration(IMMEDIATE, getClass().getClassLoader());
        SpelExpressionParser sep = new SpelExpressionParser(spc);
        expression = sep.parseExpression("headers[command]");
        SpelCompilationCoverageTests.MyMessage root = new SpelCompilationCoverageTests.MyMessage();
        Assert.assertEquals("wibble", expression.getValue(root));
        // This next call was failing because the isCompilable check in Indexer
        // did not check on the key being compilable (and also generateCode in the
        // Indexer was missing the optimization that it didn't need necessarily
        // need to call generateCode for that accessor)
        Assert.assertEquals("wibble", expression.getValue(root));
        assertCanCompile(expression);
        // What about a map key that is an expression - ensure the getKey() is evaluated in the right scope
        expression = sep.parseExpression("headers[getKey()]");
        Assert.assertEquals("wobble", expression.getValue(root));
        Assert.assertEquals("wobble", expression.getValue(root));
        expression = sep.parseExpression("list[getKey2()]");
        Assert.assertEquals("wobble", expression.getValue(root));
        Assert.assertEquals("wobble", expression.getValue(root));
        expression = sep.parseExpression("ia[getKey2()]");
        Assert.assertEquals(3, expression.getValue(root));
        Assert.assertEquals(3, expression.getValue(root));
    }

    @Test
    public void elvisOperator_SPR15192() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(IMMEDIATE, null);
        Expression exp;
        exp = parseExpression("bar()");
        Assert.assertEquals("BAR", exp.getValue(new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("BAR", exp.getValue(new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("bar('baz')");
        Assert.assertEquals("BAZ", exp.getValue(new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("BAZ", exp.getValue(new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map", Collections.singletonMap("foo", "qux"));
        exp = parseExpression("bar(#map['foo'])");
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("bar(#map['foo'] ?: 'qux')");
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // When the condition is a primitive
        exp = parseExpression("3?:'foo'");
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // When the condition is a double slot primitive
        exp = parseExpression("3L?:'foo'");
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // When the condition is an empty string
        exp = parseExpression("''?:4L");
        Assert.assertEquals("4", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("4", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // null condition
        exp = parseExpression("null?:4L");
        Assert.assertEquals("4", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("4", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // variable access returning primitive
        exp = parseExpression("#x?:'foo'");
        context.setVariable("x", 50);
        Assert.assertEquals("50", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("50", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("#x?:'foo'");
        context.setVariable("x", null);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // variable access returning array
        exp = parseExpression("#x?:'foo'");
        context.setVariable("x", new int[]{ 1, 2, 3 });
        Assert.assertEquals("1,2,3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("1,2,3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
    }

    @Test
    public void elvisOperator_SPR17214() throws Exception {
        SpelParserConfiguration spc = new SpelParserConfiguration(IMMEDIATE, null);
        SpelExpressionParser sep = new SpelExpressionParser(spc);
        SpelCompilationCoverageTests.RecordHolder rh = null;
        expression = sep.parseExpression("record.get('abc')?:record.put('abc',expression.someLong?.longValue())");
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        assertCanCompile(expression);
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        expression = sep.parseExpression("record.get('abc')?:record.put('abc',3L.longValue())");
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        assertCanCompile(expression);
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        expression = sep.parseExpression("record.get('abc')?:record.put('abc',3L.longValue())");
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        assertCanCompile(expression);
        rh = new SpelCompilationCoverageTests.RecordHolder();
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(3L, expression.getValue(rh));
        expression = sep.parseExpression("record.get('abc')==null?record.put('abc',expression.someLong?.longValue()):null");
        rh = new SpelCompilationCoverageTests.RecordHolder();
        rh.expression.someLong = 6L;
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(6L, rh.get("abc"));
        Assert.assertNull(expression.getValue(rh));
        assertCanCompile(expression);
        rh = new SpelCompilationCoverageTests.RecordHolder();
        rh.expression.someLong = 6L;
        Assert.assertNull(expression.getValue(rh));
        Assert.assertEquals(6L, rh.get("abc"));
        Assert.assertNull(expression.getValue(rh));
    }

    @Test
    public void ternaryOperator_SPR15192() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(IMMEDIATE, null);
        Expression exp;
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map", Collections.singletonMap("foo", "qux"));
        exp = parseExpression("bar(#map['foo'] != null ? #map['foo'] : 'qux')");
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("QUX", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("3==3?3:'foo'");
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("3!=3?3:'foo'");
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // When the condition is a double slot primitive
        exp = parseExpression("3==3?3L:'foo'");
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("3!=3?3L:'foo'");
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // When the condition is an empty string
        exp = parseExpression("''==''?'abc':4L");
        Assert.assertEquals("abc", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("abc", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // null condition
        exp = parseExpression("3==3?null:4L");
        Assert.assertEquals(null, exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals(null, exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // variable access returning primitive
        exp = parseExpression("#x==#x?50:'foo'");
        context.setVariable("x", 50);
        Assert.assertEquals("50", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("50", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        exp = parseExpression("#x!=#x?50:'foo'");
        context.setVariable("x", null);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("foo", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
        // variable access returning array
        exp = parseExpression("#x==#x?'1,2,3':'foo'");
        context.setVariable("x", new int[]{ 1, 2, 3 });
        Assert.assertEquals("1,2,3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertCanCompile(exp);
        Assert.assertEquals("1,2,3", exp.getValue(context, new SpelCompilationCoverageTests.Foo(), String.class));
        assertIsCompiled(exp);
    }

    @Test
    public void repeatedCompilation() throws Exception {
        // Verifying that after a number of compilations, the classloaders
        // used to load the compiled expressions are discarded/replaced.
        // See SpelCompiler.loadClass()
        Field f = SpelExpression.class.getDeclaredField("compiledAst");
        Set<Object> classloadersUsed = new HashSet<>();
        for (int i = 0; i < 1500; i++) {
            // 1500 is greater than SpelCompiler.CLASSES_DEFINED_LIMIT
            expression = parser.parseExpression("4 + 5");
            Assert.assertEquals(9, ((int) (expression.getValue(Integer.class))));
            assertCanCompile(expression);
            f.setAccessible(true);
            CompiledExpression cEx = ((CompiledExpression) (f.get(expression)));
            classloadersUsed.add(cEx.getClass().getClassLoader());
            Assert.assertEquals(9, ((int) (expression.getValue(Integer.class))));
        }
        Assert.assertTrue(((classloadersUsed.size()) > 1));
    }

    // nested types
    public interface Message<T> {
        SpelCompilationCoverageTests.MessageHeaders getHeaders();

        @SuppressWarnings("rawtypes")
        List getList();

        int[] getIa();
    }

    public static class MyMessage implements SpelCompilationCoverageTests.Message<String> {
        public SpelCompilationCoverageTests.MessageHeaders getHeaders() {
            SpelCompilationCoverageTests.MessageHeaders mh = new SpelCompilationCoverageTests.MessageHeaders();
            mh.put("command", "wibble");
            mh.put("command2", "wobble");
            return mh;
        }

        public int[] getIa() {
            return new int[]{ 5, 3 };
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public List getList() {
            List l = new ArrayList();
            l.add("wibble");
            l.add("wobble");
            return l;
        }

        public String getKey() {
            return "command2";
        }

        public int getKey2() {
            return 1;
        }
    }

    @SuppressWarnings("serial")
    public static class MessageHeaders extends HashMap<String, Object> {}

    public static class GenericMessageTestHelper<T> {
        private T payload;

        GenericMessageTestHelper(T value) {
            this.payload = value;
        }

        public T getPayload() {
            return payload;
        }
    }

    // This test helper has a bound on the type variable
    public static class GenericMessageTestHelper2<T extends Number> {
        private T payload;

        GenericMessageTestHelper2(T value) {
            this.payload = value;
        }

        public T getPayload() {
            return payload;
        }
    }

    static class MyAccessor implements CompilablePropertyAccessor {
        private Method method;

        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{ SpelCompilationCoverageTests.Payload2.class };
        }

        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            // target is a Payload2 instance
            return true;
        }

        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            SpelCompilationCoverageTests.Payload2 payload2 = ((SpelCompilationCoverageTests.Payload2) (target));
            return new TypedValue(payload2.getField(name));
        }

        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return false;
        }

        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        }

        @Override
        public boolean isCompilable() {
            return true;
        }

        @Override
        public Class<?> getPropertyType() {
            return Object.class;
        }

        @Override
        public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
            if ((method) == null) {
                try {
                    method = SpelCompilationCoverageTests.Payload2.class.getDeclaredMethod("getField", String.class);
                } catch (Exception ex) {
                }
            }
            String descriptor = cf.lastDescriptor();
            String memberDeclaringClassSlashedDescriptor = method.getDeclaringClass().getName().replace('.', '/');
            if (descriptor == null) {
                cf.loadTarget(mv);
            }
            if ((descriptor == null) || (!(memberDeclaringClassSlashedDescriptor.equals(descriptor.substring(1))))) {
                mv.visitTypeInsn(CHECKCAST, memberDeclaringClassSlashedDescriptor);
            }
            mv.visitLdcInsn(propertyName);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberDeclaringClassSlashedDescriptor, method.getName(), CodeFlow.createSignatureDescriptor(method), false);
        }
    }

    static class CompilableMapAccessor implements CompilablePropertyAccessor {
        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            Map<?, ?> map = ((Map<?, ?>) (target));
            return map.containsKey(name);
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            Map<?, ?> map = ((Map<?, ?>) (target));
            Object value = map.get(name);
            if ((value == null) && (!(map.containsKey(name)))) {
                throw new SpelCompilationCoverageTests.MapAccessException(name);
            }
            return new TypedValue(value);
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            Map<String, Object> map = ((Map<String, Object>) (target));
            map.put(name, newValue);
        }

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{ Map.class };
        }

        @Override
        public boolean isCompilable() {
            return true;
        }

        @Override
        public Class<?> getPropertyType() {
            return Object.class;
        }

        @Override
        public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
            String descriptor = cf.lastDescriptor();
            if (descriptor == null) {
                cf.loadTarget(mv);
            }
            mv.visitLdcInsn(propertyName);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        }
    }

    /**
     * Exception thrown from {@code read} in order to reset a cached
     * PropertyAccessor, allowing other accessors to have a try.
     */
    @SuppressWarnings("serial")
    private static class MapAccessException extends AccessException {
        private final String key;

        public MapAccessException(String key) {
            super(null);
            this.key = key;
        }

        @Override
        public String getMessage() {
            return ("Map does not contain a value for key '" + (this.key)) + "'";
        }
    }

    public static class Greeter {
        public String getWorld() {
            return "world";
        }

        public Object getObject() {
            return "object";
        }
    }

    public static class FooObjectHolder {
        private SpelCompilationCoverageTests.FooObject foo = new SpelCompilationCoverageTests.FooObject();

        public SpelCompilationCoverageTests.FooObject getFoo() {
            return foo;
        }
    }

    public static class FooObject {
        public Object getObject() {
            return "hello";
        }
    }

    public static class FooString {
        public String getObject() {
            return "hello";
        }
    }

    public static class Payload {
        SpelCompilationCoverageTests.Two[] DR = new SpelCompilationCoverageTests.Two[]{ new SpelCompilationCoverageTests.Two() };

        public SpelCompilationCoverageTests.Two holder = new SpelCompilationCoverageTests.Two();

        public SpelCompilationCoverageTests.Two[] getDR() {
            return DR;
        }
    }

    public static class Payload2 {
        String var1 = "abc";

        String var2 = "def";

        public Object getField(String name) {
            if (name.equals("var1")) {
                return var1;
            } else
                if (name.equals("var2")) {
                    return var2;
                }

            return null;
        }
    }

    public static class Payload2Holder {
        public SpelCompilationCoverageTests.Payload2 payload2 = new SpelCompilationCoverageTests.Payload2();
    }

    public class Person {
        private int age;

        public Person(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public class Person3 {
        private int age;

        public Person3(String name, int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Two {
        SpelCompilationCoverageTests.Three three = new SpelCompilationCoverageTests.Three();

        public SpelCompilationCoverageTests.Three getThree() {
            return three;
        }

        public String toString() {
            return "instanceof Two";
        }
    }

    public static class Three {
        double four = 0.04;

        public double getFour() {
            return four;
        }
    }

    public class PayloadX {
        public int valueI = 120;

        public Integer valueIB = 120;

        public Integer valueIB58 = 58;

        public Integer valueIB60 = 60;

        public long valueJ = 120L;

        public Long valueJB = 120L;

        public Long valueJB58 = 58L;

        public Long valueJB60 = 60L;

        public double valueD = 120.0;

        public Double valueDB = 120.0;

        public Double valueDB58 = 58.0;

        public Double valueDB60 = 60.0;

        public float valueF = 120.0F;

        public Float valueFB = 120.0F;

        public Float valueFB58 = 58.0F;

        public Float valueFB60 = 60.0F;

        public byte valueB = ((byte) (120));

        public byte valueB18 = ((byte) (18));

        public byte valueB20 = ((byte) (20));

        public Byte valueBB = ((byte) (120));

        public Byte valueBB18 = ((byte) (18));

        public Byte valueBB20 = ((byte) (20));

        public char valueC = ((char) (120));

        public Character valueCB = ((char) (120));

        public short valueS = ((short) (120));

        public short valueS18 = ((short) (18));

        public short valueS20 = ((short) (20));

        public Short valueSB = ((short) (120));

        public Short valueSB18 = ((short) (18));

        public Short valueSB20 = ((short) (20));

        public SpelCompilationCoverageTests.PayloadX payload;

        public PayloadX() {
            payload = this;
        }
    }

    public static class TestClass1 {
        public int index1 = 1;

        public int index2 = 3;

        public String word = "abcd";
    }

    public static class TestClass4 {
        public boolean a;

        public boolean b;

        public boolean gettrue() {
            return true;
        }

        public boolean getfalse() {
            return false;
        }

        public boolean getA() {
            return a;
        }

        public boolean getB() {
            return b;
        }
    }

    public static class TestClass10 {
        public String s = null;

        public void reset() {
            s = null;
        }

        public void concat(String arg) {
            s = "::" + arg;
        }

        public void concat(String... vargs) {
            if (vargs == null) {
                s = "";
            } else {
                s = "";
                for (String varg : vargs) {
                    s += varg;
                }
            }
        }

        public void concat2(Object arg) {
            s = "::" + arg;
        }

        public void concat2(Object... vargs) {
            if (vargs == null) {
                s = "";
            } else {
                s = "";
                for (Object varg : vargs) {
                    s += varg;
                }
            }
        }
    }

    public static class TestClass5 {
        public int i = 0;

        public String s = null;

        public static int _i = 0;

        public static String _s = null;

        public static short s1 = ((short) (1));

        public static short s2 = ((short) (2));

        public static short s3 = ((short) (3));

        public static long l1 = 1L;

        public static long l2 = 2L;

        public static long l3 = 3L;

        public static float f1 = 1.0F;

        public static float f2 = 2.0F;

        public static float f3 = 3.0F;

        public static char c1 = 'a';

        public static char c2 = 'b';

        public static char c3 = 'c';

        public static byte b1 = ((byte) (65));

        public static byte b2 = ((byte) (66));

        public static byte b3 = ((byte) (67));

        public static String[] stringArray = new String[]{ "aaa", "bbb", "ccc" };

        public static int[] intArray = new int[]{ 11, 22, 33 };

        public Object obj = null;

        public String field = null;

        public void reset() {
            i = 0;
            SpelCompilationCoverageTests.TestClass5._i = 0;
            s = null;
            SpelCompilationCoverageTests.TestClass5._s = null;
            field = null;
        }

        public void one() {
            i = 1;
        }

        public static void two() {
            SpelCompilationCoverageTests.TestClass5._i = 1;
        }

        public String three() {
            return "hello";
        }

        public long four() {
            return 3277700L;
        }

        public static String five() {
            return "hello";
        }

        public static long six() {
            return 3277700L;
        }

        public void seven(String toset) {
            s = toset;
        }

        // public void seven(Number n) { s = n.toString(); }
        public void takeNumber(Number n) {
            s = n.toString();
        }

        public void takeString(String s) {
            this.s = s;
        }

        public static void eight(String toset) {
            SpelCompilationCoverageTests.TestClass5._s = toset;
        }

        public void nine(int toset) {
            i = toset;
        }

        public static void ten(int toset) {
            SpelCompilationCoverageTests.TestClass5._i = toset;
        }

        public void eleven(String... vargs) {
            if (vargs == null) {
                s = "";
            } else {
                s = "";
                for (String varg : vargs) {
                    s += varg;
                }
            }
        }

        public void twelve(int... vargs) {
            if (vargs == null) {
                i = 0;
            } else {
                i = 0;
                for (int varg : vargs) {
                    i += varg;
                }
            }
        }

        public void thirteen(String a, String... vargs) {
            if (vargs == null) {
                s = a + "::";
            } else {
                s = a + "::";
                for (String varg : vargs) {
                    s += varg;
                }
            }
        }

        public void arrayz(boolean... bs) {
            s = "";
            if (bs != null) {
                s = "";
                for (boolean b : bs) {
                    s += Boolean.toString(b);
                }
            }
        }

        public void arrays(short... ss) {
            s = "";
            if (ss != null) {
                s = "";
                for (short s : ss) {
                    this.s += Short.toString(s);
                }
            }
        }

        public void arrayd(double... vargs) {
            s = "";
            if (vargs != null) {
                s = "";
                for (double v : vargs) {
                    this.s += Double.toString(v);
                }
            }
        }

        public void arrayf(float... vargs) {
            s = "";
            if (vargs != null) {
                s = "";
                for (float v : vargs) {
                    this.s += Float.toString(v);
                }
            }
        }

        public void arrayj(long... vargs) {
            s = "";
            if (vargs != null) {
                s = "";
                for (long v : vargs) {
                    this.s += Long.toString(v);
                }
            }
        }

        public void arrayb(byte... vargs) {
            s = "";
            if (vargs != null) {
                s = "";
                for (Byte v : vargs) {
                    this.s += Byte.toString(v);
                }
            }
        }

        public void arrayc(char... vargs) {
            s = "";
            if (vargs != null) {
                s = "";
                for (char v : vargs) {
                    this.s += Character.toString(v);
                }
            }
        }

        public void fourteen(String a, String[]... vargs) {
            if (vargs == null) {
                s = a + "::";
            } else {
                s = a + "::";
                for (String[] varg : vargs) {
                    s += "{";
                    for (String v : varg) {
                        s += v;
                    }
                    s += "}";
                }
            }
        }

        public void fifteen(String a, int[]... vargs) {
            if (vargs == null) {
                s = a + "::";
            } else {
                s = a + "::";
                for (int[] varg : vargs) {
                    s += "{";
                    for (int v : varg) {
                        s += Integer.toString(v);
                    }
                    s += "}";
                }
            }
        }

        public void sixteen(Object... vargs) {
            if (vargs == null) {
                s = "";
            } else {
                s = "";
                for (Object varg : vargs) {
                    s += varg;
                }
            }
        }
    }

    public static class TestClass6 {
        public String orange = "value1";

        public static String apple = "value2";

        public long peach = 34L;

        public String getBanana() {
            return "value3";
        }

        public static String getPlum() {
            return "value4";
        }
    }

    public static class TestClass7 {
        public static String property;

        static {
            String s = "UK 123";
            StringTokenizer st = new StringTokenizer(s);
            SpelCompilationCoverageTests.TestClass7.property = st.nextToken();
        }

        public static void reset() {
            String s = "UK 123";
            StringTokenizer st = new StringTokenizer(s);
            SpelCompilationCoverageTests.TestClass7.property = st.nextToken();
        }
    }

    public static class TestClass8 {
        public int i;

        public String s;

        public double d;

        public boolean z;

        public TestClass8(int i, String s, double d, boolean z) {
            this.i = i;
            this.s = s;
            this.d = d;
            this.z = z;
        }

        public TestClass8() {
        }

        public TestClass8(Integer i) {
            this.i = i;
        }

        @SuppressWarnings("unused")
        private TestClass8(String a, String b) {
            this.s = a + b;
        }
    }

    public static class Obj {
        private final String param1;

        public Obj(String param1) {
            this.param1 = param1;
        }
    }

    public static class Obj2 {
        public final String output;

        public Obj2(String... params) {
            StringBuilder b = new StringBuilder();
            for (String param : params) {
                b.append(param);
            }
            output = b.toString();
        }
    }

    public static class Obj3 {
        public final String output;

        public Obj3(int... params) {
            StringBuilder b = new StringBuilder();
            for (int param : params) {
                b.append(Integer.toString(param));
            }
            output = b.toString();
        }

        public Obj3(String s, Float f, int... ints) {
            StringBuilder b = new StringBuilder();
            b.append(s);
            b.append(":");
            b.append(Float.toString(f));
            b.append(":");
            for (int param : ints) {
                b.append(Integer.toString(param));
            }
            output = b.toString();
        }
    }

    public static class Obj4 {
        public final String output;

        public Obj4(int[] params) {
            StringBuilder b = new StringBuilder();
            for (int param : params) {
                b.append(Integer.toString(param));
            }
            output = b.toString();
        }
    }

    @SuppressWarnings("unused")
    private static class TestClass9 {
        public TestClass9(int i) {
        }
    }

    // These test classes simulate a pattern of public/private classes seen in Spring Security
    // final class HttpServlet3RequestFactory implements HttpServletRequestFactory
    static class HttpServlet3RequestFactory {
        static SpelCompilationCoverageTests.HttpServlet3RequestFactory.Servlet3SecurityContextHolderAwareRequestWrapper getOne() {
            SpelCompilationCoverageTests.HttpServlet3RequestFactory outer = new SpelCompilationCoverageTests.HttpServlet3RequestFactory();
            return outer.new Servlet3SecurityContextHolderAwareRequestWrapper();
        }

        // private class Servlet3SecurityContextHolderAwareRequestWrapper extends SecurityContextHolderAwareRequestWrapper
        private class Servlet3SecurityContextHolderAwareRequestWrapper extends SpelCompilationCoverageTests.SecurityContextHolderAwareRequestWrapper {}
    }

    // public class SecurityContextHolderAwareRequestWrapper extends HttpServletRequestWrapper
    static class SecurityContextHolderAwareRequestWrapper extends SpelCompilationCoverageTests.HttpServletRequestWrapper {}

    public static class HttpServletRequestWrapper {
        public String getServletPath() {
            return "wibble";
        }
    }

    // Here the declaring class is not public
    static class SomeCompareMethod {
        // method not public
        static int compare(Object o1, Object o2) {
            return -1;
        }

        // public
        public static int compare2(Object o1, Object o2) {
            return -1;
        }
    }

    public static class SomeCompareMethod2 {
        public static int negate(int i1) {
            return -i1;
        }

        public static String append(String... strings) {
            StringBuilder b = new StringBuilder();
            for (String string : strings) {
                b.append(string);
            }
            return b.toString();
        }

        public static String append2(Object... objects) {
            StringBuilder b = new StringBuilder();
            for (Object object : objects) {
                b.append(object.toString());
            }
            return b.toString();
        }

        public static String append3(String[] strings) {
            StringBuilder b = new StringBuilder();
            for (String string : strings) {
                b.append(string);
            }
            return b.toString();
        }

        public static String append4(String s, String... strings) {
            StringBuilder b = new StringBuilder();
            b.append(s).append("::");
            for (String string : strings) {
                b.append(string);
            }
            return b.toString();
        }

        public static String appendChar(char... values) {
            StringBuilder b = new StringBuilder();
            for (char ch : values) {
                b.append(ch);
            }
            return b.toString();
        }

        public static int sum(int... ints) {
            int total = 0;
            for (int i : ints) {
                total += i;
            }
            return total;
        }

        public static int sumDouble(double... values) {
            int total = 0;
            for (double i : values) {
                total += i;
            }
            return total;
        }

        public static int sumFloat(float... values) {
            int total = 0;
            for (float i : values) {
                total += i;
            }
            return total;
        }
    }

    public static class DelegatingStringFormat {
        public static String format(String s, Object... args) {
            return String.format(s, args);
        }
    }

    public static class StaticsHelper {
        static SpelCompilationCoverageTests.StaticsHelper sh = new SpelCompilationCoverageTests.StaticsHelper();

        public static SpelCompilationCoverageTests.StaticsHelper fielda = SpelCompilationCoverageTests.StaticsHelper.sh;

        public static String fieldb = "fb";

        public static SpelCompilationCoverageTests.StaticsHelper methoda() {
            return SpelCompilationCoverageTests.StaticsHelper.sh;
        }

        public static String methodb() {
            return "mb";
        }

        public static SpelCompilationCoverageTests.StaticsHelper getPropertya() {
            return SpelCompilationCoverageTests.StaticsHelper.sh;
        }

        public static String getPropertyb() {
            return "pb";
        }

        public String toString() {
            return "sh";
        }
    }

    public static class Apple implements Comparable<SpelCompilationCoverageTests.Apple> {
        public Object gotComparedTo = null;

        public int i;

        public Apple(int i) {
            this.i = i;
        }

        public void setValue(int i) {
            this.i = i;
        }

        @Override
        public int compareTo(SpelCompilationCoverageTests.Apple that) {
            this.gotComparedTo = that;
            if ((this.i) < (that.i)) {
                return -1;
            } else
                if ((this.i) > (that.i)) {
                    return +1;
                } else {
                    return 0;
                }

        }
    }

    // For opNe_SPR14863
    public static class MyContext {
        private final Map<String, String> data;

        public MyContext(Map<String, String> data) {
            this.data = data;
        }

        public Map<String, String> getData() {
            return data;
        }
    }

    public static class Foo {
        public String bar() {
            return "BAR";
        }

        public String bar(String arg) {
            return arg.toUpperCase();
        }
    }

    public static class RecordHolder {
        public Map<String, Long> record = new HashMap<>();

        public SpelCompilationCoverageTests.LongHolder expression = new SpelCompilationCoverageTests.LongHolder();

        public void add(String key, Long value) {
            record.put(key, value);
        }

        public long get(String key) {
            return record.get(key);
        }
    }

    public static class LongHolder {
        public Long someLong = 3L;
    }
}

