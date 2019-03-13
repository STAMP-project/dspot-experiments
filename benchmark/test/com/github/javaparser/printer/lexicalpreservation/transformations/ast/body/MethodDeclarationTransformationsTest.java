/**
 * Copyright (C) 2007-2010 J?lio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 *
 * This file is part of
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package com.github.javaparser.printer.lexicalpreservation.transformations.ast.body;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.lexicalpreservation.AbstractLexicalPreservingTest;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.utils.Utils.EOL;


/**
 * Transforming MethodDeclaration and verifying the LexicalPreservation works as expected.
 */
class MethodDeclarationTransformationsTest extends AbstractLexicalPreservingTest {
    @Test
    public void addingModifiersWithExistingAnnotationsShort() {
        MethodDeclaration it = consider("@Override void A(){}");
        it.setModifiers(NodeList.nodeList(Modifier.publicModifier(), Modifier.finalModifier()));
        assertTransformedToString("@Override public final void A(){}", it);
    }

    @Test
    public void addingModifiersWithExistingAnnotations() {
        considerCode(((((((((("class X {" + (EOL)) + "  @Test") + (EOL)) + "  void testCase() {") + (EOL)) + "  }") + (EOL)) + "}") + (EOL)));
        cu.getType(0).getMethods().get(0).addModifier(Modifier.finalModifier().getKeyword(), Modifier.publicModifier().getKeyword());
        String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
        TestUtils.assertEqualsNoEol(("class X {\n" + ((("  @Test\n" + "  final public void testCase() {\n") + "  }\n") + "}\n")), result);
    }

    @Test
    public void parseAndPrintAnonymousClassExpression() {
        Expression expression = StaticJavaParser.parseExpression((("new Object() {" + (EOL)) + "}"));
        String expected = ("new Object() {" + (EOL)) + "}";
        assertTransformedToString(expected, expression);
    }

    @Test
    public void parseAndPrintAnonymousClassStatement() {
        Statement statement = StaticJavaParser.parseStatement((("Object anonymous = new Object() {" + (EOL)) + "};"));
        String expected = ("Object anonymous = new Object() {" + (EOL)) + "};";
        assertTransformedToString(expected, statement);
    }

    @Test
    public void replaceBodyShouldNotBreakAnonymousClasses() {
        MethodDeclaration it = consider("public void method() { }");
        it.getBody().ifPresent(( body) -> {
            Statement statement = parseStatement((("Object anonymous = new Object() {" + (EOL)) + "};"));
            NodeList<Statement> statements = new NodeList<>();
            statements.add(statement);
            body.setStatements(statements);
        });
        String expected = ((((("public void method() {" + (EOL)) + "    Object anonymous = new Object() {") + (EOL)) + "    };") + (EOL)) + "}";
        assertTransformedToString(expected, it);
    }
}

