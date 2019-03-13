/**
 * Copyright 2017 The Bazel Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import FlowStatement.Kind;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the {@code toString} and pretty printing methods for {@link ASTNode} subclasses.
 */
/* Not tested explicitly because they're covered implicitly by tests for other nodes:
- LValue
- DictionaryEntryLiteral
- passed arguments / formal parameters
- ConditionalStatements
 */
@RunWith(JUnit4.class)
public class ASTPrettyPrintTest extends EvaluationTestCase {
    // Expressions.
    @Test
    public void abstractComprehension() {
        // Covers DictComprehension and ListComprehension.
        assertExprBothRoundTrip("[z for y in x if True for z in y]");
        assertExprBothRoundTrip("{z: x for y in x if True for z in y}");
    }

    @Test
    public void binaryOperatorExpression() {
        assertExprPrettyMatches("1 + 2", "(1 + 2)");
        assertExprTostringMatches("1 + 2", "1 + 2");
        assertExprPrettyMatches("1 + (2 * 3)", "(1 + (2 * 3))");
        assertExprTostringMatches("1 + (2 * 3)", "1 + 2 * 3");
    }

    @Test
    public void conditionalExpression() {
        assertExprBothRoundTrip("1 if True else 2");
    }

    @Test
    public void dictionaryLiteral() {
        assertExprBothRoundTrip("{1: \"a\", 2: \"b\"}");
    }

    @Test
    public void dotExpression() {
        assertExprBothRoundTrip("o.f");
    }

    @Test
    public void funcallExpression() {
        assertExprBothRoundTrip("f()");
        assertExprBothRoundTrip("f(a)");
        assertExprBothRoundTrip("f(a, b = B, *c, d = D, **e)");
        assertExprBothRoundTrip("o.f()");
    }

    @Test
    public void identifier() {
        assertExprBothRoundTrip("foo");
    }

    @Test
    public void indexExpression() {
        assertExprBothRoundTrip("a[i]");
    }

    @Test
    public void integerLiteral() {
        assertExprBothRoundTrip("5");
    }

    @Test
    public void listLiteralShort() {
        assertExprBothRoundTrip("[]");
        assertExprBothRoundTrip("[5]");
        assertExprBothRoundTrip("[5, 6]");
        assertExprBothRoundTrip("()");
        assertExprBothRoundTrip("(5,)");
        assertExprBothRoundTrip("(5, 6)");
    }

    @Test
    public void listLiteralLong() {
        // List literals with enough elements to trigger the abbreviated toString() format.
        assertExprPrettyMatches("[1, 2, 3, 4, 5, 6]", "[1, 2, 3, 4, 5, 6]");
        assertExprTostringMatches("[1, 2, 3, 4, 5, 6]", "[1, 2, 3, 4, <2 more arguments>]");
        assertExprPrettyMatches("(1, 2, 3, 4, 5, 6)", "(1, 2, 3, 4, 5, 6)");
        assertExprTostringMatches("(1, 2, 3, 4, 5, 6)", "(1, 2, 3, 4, <2 more arguments>)");
    }

    @Test
    public void listLiteralNested() {
        // Make sure that the inner list doesn't get abbreviated when the outer list is printed using
        // prettyPrint().
        assertExprPrettyMatches("[1, 2, 3, [10, 20, 30, 40, 50, 60], 4, 5, 6]", "[1, 2, 3, [10, 20, 30, 40, 50, 60], 4, 5, 6]");
        // It doesn't matter as much what toString does. This case demonstrates an apparent bug in how
        // Printer#printList abbreviates the nested contents. We can keep this test around to help
        // monitor changes in the buggy behavior or eventually fix it.
        assertExprTostringMatches("[1, 2, 3, [10, 20, 30, 40, 50, 60], 4, 5, 6]", "[1, 2, 3, [10, 20, 30, 40, <2 more argu...<2 more arguments>], <3 more arguments>]");
    }

    @Test
    public void sliceExpression() {
        assertExprBothRoundTrip("a[b:c:d]");
        assertExprBothRoundTrip("a[b:c]");
        assertExprBothRoundTrip("a[b:]");
        assertExprBothRoundTrip("a[:c:d]");
        assertExprBothRoundTrip("a[:c]");
        assertExprBothRoundTrip("a[::d]");
        assertExprBothRoundTrip("a[:]");
    }

    @Test
    public void stringLiteral() {
        assertExprBothRoundTrip("\"foo\"");
        assertExprBothRoundTrip("\"quo\\\"ted\"");
    }

    @Test
    public void unaryOperatorExpression() {
        assertExprPrettyMatches("not True", "not (True)");
        assertExprTostringMatches("not True", "not True");
        assertExprPrettyMatches("-5", "-(5)");
        assertExprTostringMatches("-5", "-5");
    }

    // Statements.
    @Test
    public void assignmentStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, "x = y", "  x = y\n");
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, "x = y", "x = y\n");
    }

    @Test
    public void augmentedAssignmentStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, "x += y", "  x += y\n");
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, "x += y", "x += y\n");
    }

    @Test
    public void expressionStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, "5", "  5\n");
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, "5", "5\n");
    }

    @Test
    public void functionDefStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.TOP_LEVEL, join("def f(x):", "  print(x)"), join("  def f(x):", "    print(x)", ""));
        assertStmtTostringMatches(ParsingLevel.TOP_LEVEL, join("def f(x):", "  print(x)"), "def f(x): ...\n");
        assertStmtIndentedPrettyMatches(ParsingLevel.TOP_LEVEL, join("def f(a, b=B, *c, d=D, **e):", "  print(x)"), join("  def f(a, b=B, *c, d=D, **e):", "    print(x)", ""));
        assertStmtTostringMatches(ParsingLevel.TOP_LEVEL, join("def f(a, b=B, *c, d=D, **e):", "  print(x)"), "def f(a, b = B, *c, d = D, **e): ...\n");
        assertStmtIndentedPrettyMatches(ParsingLevel.TOP_LEVEL, join("def f():", "  pass"), join("  def f():", "    pass", ""));
        assertStmtTostringMatches(ParsingLevel.TOP_LEVEL, join("def f():", "  pass"), "def f(): ...\n");
    }

    @Test
    public void flowStatement() {
        // The parser would complain if we tried to construct them from source.
        ASTNode breakNode = new FlowStatement(Kind.BREAK);
        assertIndentedPrettyMatches(breakNode, "  break\n");
        assertTostringMatches(breakNode, "break\n");
        ASTNode continueNode = new FlowStatement(Kind.CONTINUE);
        assertIndentedPrettyMatches(continueNode, "  continue\n");
        assertTostringMatches(continueNode, "continue\n");
    }

    @Test
    public void forStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, join("for x in y:", "  print(x)"), join("  for x in y:", "    print(x)", ""));
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, join("for x in y:", "  print(x)"), "for x in y: ...\n");
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, join("for x in y:", "  pass"), join("  for x in y:", "    pass", ""));
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, join("for x in y:", "  pass"), "for x in y: ...\n");
    }

    @Test
    public void ifStatement() {
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, join("if True:", "  print(x)"), join("  if True:", "    print(x)", ""));
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, join("if True:", "  print(x)"), "if True: ...\n");
        assertStmtIndentedPrettyMatches(ParsingLevel.LOCAL_LEVEL, join("if True:", "  print(x)", "elif False:", "  print(y)", "else:", "  print(z)"), join("  if True:", "    print(x)", "  elif False:", "    print(y)", "  else:", "    print(z)", ""));
        assertStmtTostringMatches(ParsingLevel.LOCAL_LEVEL, join("if True:", "  print(x)", "elif False:", "  print(y)", "else:", "  print(z)"), "if True: ...\n");
    }

    @Test
    public void loadStatement() {
        // load("foo.bzl", a="A", "B")
        ASTNode loadStatement = new LoadStatement(new StringLiteral("foo.bzl"), ImmutableList.of(new LoadStatement.Binding(Identifier.of("a"), Identifier.of("A")), new LoadStatement.Binding(Identifier.of("B"), Identifier.of("B"))));
        assertIndentedPrettyMatches(loadStatement, "  load(\"foo.bzl\", a=\"A\", \"B\")\n");
        assertTostringMatches(loadStatement, "load(\"foo.bzl\", a=\"A\", \"B\")\n");
    }

    @Test
    public void returnStatement() {
        assertIndentedPrettyMatches(new ReturnStatement(new StringLiteral("foo")), "  return \"foo\"\n");
        assertTostringMatches(new ReturnStatement(new StringLiteral("foo")), "return \"foo\"\n");
        assertIndentedPrettyMatches(new ReturnStatement(Identifier.of("None")), "  return None\n");
        assertTostringMatches(new ReturnStatement(Identifier.of("None")), "return None\n");
        assertIndentedPrettyMatches(new ReturnStatement(null), "  return\n");
        assertTostringMatches(new ReturnStatement(null), "return\n");
    }

    // Miscellaneous.
    @Test
    public void buildFileAST() {
        ASTNode node = parseBuildFileASTWithoutValidation("print(x)\nprint(y)");
        assertIndentedPrettyMatches(node, join("  print(x)", "  print(y)", ""));
        assertTostringMatches(node, "<BuildFileAST with 2 statements>");
    }

    @Test
    public void comment() {
        Comment node = new Comment("foo");
        assertIndentedPrettyMatches(node, "  # foo");
        assertTostringMatches(node, "foo");
    }
}

