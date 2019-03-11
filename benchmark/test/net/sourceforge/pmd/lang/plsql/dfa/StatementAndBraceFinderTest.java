/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.dfa;


import NodeType.BREAK_STATEMENT;
import NodeType.CASE_LAST_STATEMENT;
import NodeType.FOR_BEFORE_FIRST_STATEMENT;
import NodeType.FOR_END;
import NodeType.FOR_EXPR;
import NodeType.IF_EXPR;
import NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE;
import NodeType.LABEL_STATEMENT;
import NodeType.SWITCH_END;
import NodeType.SWITCH_LAST_DEFAULT_STATEMENT;
import NodeType.SWITCH_START;
import NodeType.WHILE_EXPR;
import NodeType.WHILE_LAST_STATEMENT;
import PLSQLLanguageModule.NAME;
import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import org.junit.Assert;
import org.junit.Test;


public class StatementAndBraceFinderTest extends AbstractPLSQLParserTst {
    /**
     * Java ASTStatementExpression equivalent is inferred as an Expression()
     * which has an UnlabelledStatement as a parent.
     */
    @Test
    public void testExpressionParentChildLinks() {
        ASTExpression ex = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST1).get(0);
        DataFlowNode dfn = ex.getDataFlowNode();
        Assert.assertEquals(3, dfn.getLine());
        Assert.assertTrue(((dfn.getNode()) instanceof ASTExpression));
        List<DataFlowNode> dfns = dfn.getParents();
        Assert.assertEquals(1, dfns.size());
        DataFlowNode parentDfn = dfns.get(0);
        Assert.assertEquals(2, parentDfn.getLine());
        Assert.assertTrue(((parentDfn.getNode()) instanceof ASTProgramUnit));
        ASTProgramUnit exParent = ((ASTProgramUnit) (parentDfn.getNode()));
        // Validate the two-way link between Program Unit and Statement
        Assert.assertEquals(ex, exParent.getDataFlowNode().getChildren().get(0).getNode());
        Assert.assertEquals(exParent, ex.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testVariableOrConstantDeclaratorParentChildLinks() {
        ASTVariableOrConstantDeclarator vd = getOrderedNodes(ASTVariableOrConstantDeclarator.class, StatementAndBraceFinderTest.TEST2).get(0);
        // ASTMethodDeclaration vdParent = (ASTMethodDeclaration)
        // ((DataFlowNode) vd.getDataFlowNode().getParents().get(0)).getNode();
        ASTProgramUnit vdParent = ((ASTProgramUnit) (vd.getDataFlowNode().getParents().get(0).getNode()));
        // Validate the two-way link between Program Unit and Variable
        Assert.assertEquals(vd, vdParent.getDataFlowNode().getChildren().get(0).getNode());
        Assert.assertEquals(vdParent, vd.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testIfStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST3).get(0);
        Assert.assertEquals(5, exp.getDataFlowNode().getFlow().size());
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertTrue(dfn.isType(IF_EXPR));
        Assert.assertEquals(3, dfn.getLine());
        dfn = exp.getDataFlowNode().getFlow().get(3);
        Assert.assertTrue(dfn.isType(IF_LAST_STATEMENT_WITHOUT_ELSE));
        Assert.assertEquals(3, dfn.getLine());
    }

    @Test
    public void testWhileStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST4).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertTrue(dfn.isType(WHILE_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        Assert.assertTrue(dfn.isType(WHILE_LAST_STATEMENT));
    }

    @Test
    public void testForStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST5).get(0);
        DataFlowNode dfn = null;
        dfn = exp.getDataFlowNode().getFlow().get(0);
        Assert.assertTrue((dfn instanceof StartOrEndDataFlowNode));
        dfn = exp.getDataFlowNode().getFlow().get(1);
        Assert.assertTrue(((dfn.getNode()) instanceof ASTProgramUnit));
        Assert.assertEquals(2, dfn.getLine());
        dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertEquals(3, dfn.getLine());
        Assert.assertTrue(dfn.isType(FOR_EXPR));
        Assert.assertTrue(dfn.isType(FOR_BEFORE_FIRST_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        Assert.assertEquals(3, dfn.getLine());
        Assert.assertTrue(dfn.isType(FOR_END));
    }

    @Test
    public void testSimpleCaseStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST6).get(0);
        DataFlowNode dfn = null;
        dfn = exp.getDataFlowNode().getFlow().get(0);
        Assert.assertTrue((dfn instanceof StartOrEndDataFlowNode));
        dfn = exp.getDataFlowNode().getFlow().get(1);
        Assert.assertEquals(2, dfn.getLine());
        Assert.assertTrue(((dfn.getNode()) instanceof ASTProgramUnit));
        dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertEquals(4, dfn.getLine());
        Assert.assertTrue(dfn.isType(SWITCH_START));
        Assert.assertTrue(dfn.isType(CASE_LAST_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        Assert.assertEquals(5, dfn.getLine());
        Assert.assertTrue(dfn.isType(CASE_LAST_STATEMENT));
        Assert.assertTrue(dfn.isType(BREAK_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(4);
        Assert.assertEquals(6, dfn.getLine());
        Assert.assertTrue(dfn.isType(SWITCH_LAST_DEFAULT_STATEMENT));
        Assert.assertTrue(dfn.isType(BREAK_STATEMENT));
        dfn = exp.getDataFlowNode().getFlow().get(5);
        Assert.assertEquals(7, dfn.getLine());
        Assert.assertTrue(dfn.isType(SWITCH_END));
    }

    /* @Test public void testSearchedCaseStmtHasCorrectTypes()
    { List<ASTStatement> statements = getOrderedNodes(ASTStatement.class,
    TEST7); List<ASTExpression> expressions =
    getOrderedNodes(ASTExpression.class, TEST7);

    ASTStatement st = statements.get(0); ASTStatement st1 =
    statements.get(1); ASTStatement st2 = statements.get(2); ASTStatement st3
    = statements.get(3);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-st(0)="+st.
    getBeginLine());

    ASTExpression ex = expressions.get(0); ASTExpression ex1 =
    expressions.get(1); ASTExpression ex2 = expressions.get(2); ASTExpression
    ex3 = expressions.get(3); ASTExpression ex4 = expressions.get(4);
    System.err.println("ASTExpression="+ex );

    DataFlowNode dfn = null; //dfn = ex.getDataFlowNode().getFlow().get(0);
    //dfn = st.getDataFlowNode().getFlow().get(0); dfn = (DataFlowNode)
    st.getDataFlowNode(); System.err.println("DataFlowNode(st-0)="+dfn ) ;
    System.err.println("DataFlowNode(st-1)="+st1.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(st-2)="+st2.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(st-3)="+st3.getDataFlowNode() ) ;

    System.err.println("DataFlowNode(ex-0)="+ex.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(ex-1)="+ex1.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(ex-2)="+ex2.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(ex-3)="+ex3.getDataFlowNode() ) ;
    System.err.println("DataFlowNode(ex-4)="+ex4.getDataFlowNode() ) ;
    List<DataFlowNode> dfns = dfn.getFlow();
    System.err.println("DataFlowNodes List size="+dfns.size()) ; DataFlowNode
    firstDfn = dfns.get(0); System.err.println("firstDataFlowNode="+firstDfn
    ) ;
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(0)="+dfn);
    dfn = st.getDataFlowNode().getFlow().get(1);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(1)="+dfn);
    dfn = st.getDataFlowNode().getFlow().get(2);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(2)="+dfn);
    assertTrue(dfn.isType(NodeType.SWITCH_START)); dfn =
    st.getDataFlowNode().getFlow().get(3);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(3)="+dfn);
    assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT)); //dfn =
    st.getDataFlowNode().getFlow().get(4);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(5)="+dfn);
    assertTrue(dfn.isType(NodeType.CASE_LAST_STATEMENT)); dfn =
    st.getDataFlowNode().getFlow().get(5);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(7)="+dfn);
    assertTrue(dfn.isType(NodeType.SWITCH_LAST_DEFAULT_STATEMENT)); dfn =
    st.getDataFlowNode().getFlow().get(6);
    System.err.println("testSearchedCaseStmtHasCorrectTypes-dfn(8)="+dfn);
    assertTrue(dfn.isType(NodeType.SWITCH_END)); }
     */
    @Test
    public void testLabelledStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, StatementAndBraceFinderTest.TEST8).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        Assert.assertEquals(3, dfn.getLine());
        Assert.assertTrue(dfn.isType(LABEL_STATEMENT));
    }

    @Test
    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder(LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());
        PLSQLNode node = new ASTMethodDeclaration(1);
        testingOnlySetBeginColumn(1);
        sbf.buildDataFlowFor(node);
        // sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
        node = new ASTProgramUnit(1);
        testingOnlySetBeginColumn(1);
        sbf.buildDataFlowFor(node);
    }

    private static final String TEST1 = ((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS BEGIN") + (PMD.EOL)) + "  x := 2;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST2 = ((((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS ") + (PMD.EOL)) + "  int x; ") + (PMD.EOL)) + "  BEGIN NULL ;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST3 = ((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS BEGIN") + (PMD.EOL)) + "  if (x) THEN NULL; END IF; ") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST4 = ((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS BEGIN") + (PMD.EOL)) + "  while (x) LOOP NULL; END LOOP;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST5 = ((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS BEGIN") + (PMD.EOL)) + "  for i in 0..9 LOOP NULL; END LOOP;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST6 = ((((((((((((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS ") + (PMD.EOL)) + " BEGIN") + (PMD.EOL)) + " CASE 1 ") + (PMD.EOL)) + " WHEN 0 THEN NULL; ") + (PMD.EOL)) + " WHEN 1 THEN NULL; ") + (PMD.EOL)) + " ELSE NULL;") + (PMD.EOL)) + " END CASE; ") + (PMD.EOL)) + " END bar; ") + (PMD.EOL)) + "END foo;";

    private static final String TEST7 = ((((((((((((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS ") + (PMD.EOL)) + " BEGIN") + (PMD.EOL)) + " CASE ") + (PMD.EOL)) + " WHEN 0=1 THEN NULL; ") + (PMD.EOL)) + " WHEN 1=1 THEN NULL; ") + (PMD.EOL)) + " ELSE NULL;") + (PMD.EOL)) + " END CASE;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";

    private static final String TEST8 = ((((((("CREATE OR REPLACE PACKAGE BODY Foo AS" + (PMD.EOL)) + " PROCEDURE bar IS BEGIN") + (PMD.EOL)) + " <<label>> NULL;") + (PMD.EOL)) + " END bar;") + (PMD.EOL)) + "END foo;";
}

