/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;


import JavaLanguageModule.NAME;
import NodeType.FOR_BEFORE_FIRST_STATEMENT;
import NodeType.FOR_END;
import NodeType.FOR_EXPR;
import NodeType.FOR_INIT;
import NodeType.FOR_UPDATE;
import NodeType.IF_EXPR;
import NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE;
import NodeType.WHILE_EXPR;
import NodeType.WHILE_LAST_STATEMENT;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import org.junit.Assert;
import org.junit.Test;


public class StatementAndBraceFinderTest {
    @Test
    public void testStatementExpressionParentChildLinks() {
        ASTStatementExpression se = ParserTstUtil.getOrderedNodes(ASTStatementExpression.class, StatementAndBraceFinderTest.TEST1).get(0);
        ASTMethodDeclaration seParent = ((ASTMethodDeclaration) (se.getDataFlowNode().getParents().get(0).getNode()));
        Assert.assertEquals(se, seParent.getDataFlowNode().getChildren().get(0).getNode());
        Assert.assertEquals(seParent, se.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testVariableDeclaratorParentChildLinks() {
        ASTVariableDeclarator vd = ParserTstUtil.getOrderedNodes(ASTVariableDeclarator.class, StatementAndBraceFinderTest.TEST2).get(0);
        ASTMethodDeclaration vdParent = ((ASTMethodDeclaration) (vd.getDataFlowNode().getParents().get(0).getNode()));
        Assert.assertEquals(vd, vdParent.getDataFlowNode().getChildren().get(0).getNode());
        Assert.assertEquals(vdParent, vd.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testIfStmtHasCorrectTypes() {
        ASTExpression exp = ParserTstUtil.getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST3).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertTrue(dfn.isType(IF_EXPR));
        Assert.assertTrue(dfn.isType(IF_LAST_STATEMENT_WITHOUT_ELSE));
    }

    @Test
    public void testWhileStmtHasCorrectTypes() {
        ASTExpression exp = ParserTstUtil.getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST4).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertTrue(dfn.isType(WHILE_EXPR));
        Assert.assertTrue(dfn.isType(WHILE_LAST_STATEMENT));
    }

    @Test
    public void testForStmtHasCorrectTypes() {
        ASTExpression exp = ParserTstUtil.getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST5).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertTrue(dfn.isType(FOR_INIT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        Assert.assertTrue(dfn.isType(FOR_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(4);
        Assert.assertTrue(dfn.isType(FOR_UPDATE));
        Assert.assertTrue(dfn.isType(FOR_BEFORE_FIRST_STATEMENT));
        Assert.assertTrue(dfn.isType(FOR_END));
    }

    @Test(expected = RuntimeException.class)
    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder(LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());
        sbf.buildDataFlowFor(new ASTMethodDeclaration(1));
        sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
        sbf.buildDataFlowFor(new ASTCompilationUnit(1));
    }

    private static final String TEST1 = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int x;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  if (x) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST4 = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  while (x) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final String TEST5 = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  for (int i=0; i<10; i++) {}") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

