/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.languageserver.ide.editor.codeassist.snippet;


import java.util.List;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;


public class SnippetParsingTest {
    @Test
    public void simplePlaceHolder() {
        testParseAndRender("$1", e(Snippet.class, e(Placeholder.class)));
        testParseAndRender("{\n$1\n}", e(Snippet.class, e(Text.class), e(Placeholder.class), e(Text.class)));
        testParseAndRender("$1$3989$27", e(Snippet.class, e(Placeholder.class), e(Placeholder.class), e(Placeholder.class)));
        testParseAndRender("$1:Gurke 2", e(Snippet.class, e(Placeholder.class), e(Text.class)));
        testParseAndRender("$1$2   $3", e(Snippet.class, e(Placeholder.class), e(Placeholder.class), e(Text.class), e(Placeholder.class)));
    }

    @Test
    public void simpleVariable() {
        testParseAndRender("$foo", e(Snippet.class, e(Variable.class)));
        testParseAndRender("${foo:bar}", e(Snippet.class, e(Variable.class, e(Snippet.class, e(Text.class)))));
        testParseAndRender("${foo:$5233}", e(Snippet.class, e(Variable.class, e(Snippet.class, e(Placeholder.class)))));
    }

    @Test
    public void testChoice() {
        testParseAndRender("${3:|foo,bar,zoz|}", snippet(placeholder(choice())));
        testParseAndRender("{ ${1:somevalue}    ${2:|first,second,third|} ${3:some text $4}", snippet(text(), placeholder(snippet(text())), text(), placeholder(choice()), text(), placeholder(snippet(text(), placeholder()))));
    }

    @Test
    public void nestedPlaceHolder() {
        testParseAndRender("${1:Gurke $2}", e(Snippet.class, e(Placeholder.class, e(Snippet.class, e(Text.class), e(Placeholder.class)))));
        testParseAndRender("$1:Gurke ${2:3}", e(Snippet.class, e(Placeholder.class), e(Text.class), e(Placeholder.class, e(Snippet.class, e(Text.class)))));
    }

    @Test
    public void nestedVariable() {
        testParseAndRender("${1:${foo:$3}}", e(Snippet.class, e(Placeholder.class, e(Snippet.class, e(Variable.class, e(Snippet.class, e(Placeholder.class)))))));
    }

    private static class TestExpression {
        private Class<?> type;

        private List<SnippetParsingTest.TestExpression> children;

        public TestExpression(Class<?> type, List<SnippetParsingTest.TestExpression> children) {
            this.type = type;
            this.children = children;
        }

        public Class<?> getType() {
            return type;
        }

        public List<SnippetParsingTest.TestExpression> getChildren() {
            return children;
        }
    }

    private static class ExpressionAsserter implements ExpressionVisitor {
        private Stack<SnippetParsingTest.TestExpression> state = new Stack<>();

        public ExpressionAsserter(SnippetParsingTest.TestExpression expr) {
            state.push(expr);
        }

        @Override
        public void visit(DollarExpression e) {
            e.getValue().accept(this);
        }

        @Override
        public void visit(Choice e) {
            Assert.assertEquals(Choice.class, state.peek().getType());
        }

        @Override
        public void visit(Placeholder e) {
            SnippetParsingTest.TestExpression current = state.peek();
            Assert.assertEquals(Placeholder.class, current.getType());
            if ((e.getValue()) != null) {
                state.push(current.getChildren().get(0));
                e.getValue().accept(this);
                state.pop();
            }
        }

        @Override
        public void visit(Snippet e) {
            SnippetParsingTest.TestExpression current = state.peek();
            Assert.assertEquals(Snippet.class, current.getType());
            for (int i = 0; i < (e.getExpressions().size()); i++) {
                state.push(current.getChildren().get(i));
                e.getExpressions().get(i).accept(this);
                state.pop();
            }
        }

        @Override
        public void visit(Text e) {
            SnippetParsingTest.TestExpression current = state.peek();
            Assert.assertEquals(Text.class, current.getType());
        }

        @Override
        public void visit(Variable e) {
            SnippetParsingTest.TestExpression current = state.peek();
            Assert.assertEquals(Variable.class, current.getType());
            if ((e.getValue()) != null) {
                state.push(current.getChildren().get(0));
                e.getValue().accept(this);
                state.pop();
            }
        }
    }
}

