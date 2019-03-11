/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;


import java.util.List;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.typeresolution.testdata.SuperClass;
import net.sourceforge.pmd.typeresolution.testdata.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.ThisExpression;
import net.sourceforge.pmd.typeresolution.testdata.UsesJavaStreams;
import net.sourceforge.pmd.typeresolution.testdata.UsesRepeatableAnnotations;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;


public class ClassTypeResolverJava8Test {
    @Test
    public void interfaceMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesJavaStreams.class);
    }

    @Test
    public void repeatableAnnotationsMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesRepeatableAnnotations.class);
    }

    @Test
    public void testThisExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(ThisExpression.class);
        List<ASTPrimaryExpression> expressions = ClassTypeResolverJava8Test.convertList(acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression"), ASTPrimaryExpression.class);
        List<ASTPrimaryPrefix> prefixes = ClassTypeResolverJava8Test.convertList(acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"), ASTPrimaryPrefix.class);
        int index = 0;
        Assert.assertEquals(ThisExpression.class, expressions.get(index).getType());
        Assert.assertEquals(ThisExpression.class, prefixes.get((index++)).getType());
        Assert.assertEquals(ThisExpression.PrimaryThisInterface.class, expressions.get(index).getType());
        Assert.assertEquals(ThisExpression.PrimaryThisInterface.class, prefixes.get((index++)).getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
        Assert.assertEquals("All expressions not tested", index, prefixes.size());
    }

    @Test
    public void testSuperExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(SuperExpression.class);
        List<AbstractJavaTypeNode> expressions = ClassTypeResolverJava8Test.convertList(acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"), AbstractJavaTypeNode.class);
        int index = 0;
        Assert.assertEquals(SuperClass.class, expressions.get((index++)).getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }
}

