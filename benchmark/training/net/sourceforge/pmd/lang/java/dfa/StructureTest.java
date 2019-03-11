/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;


import JavaLanguageModule.NAME;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import org.junit.Assert;
import org.junit.Test;


public class StructureTest {
    @Test
    public void testAddResultsinDFANodeContainingAddedNode() {
        Structure s = new Structure(LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());
        Node n = new ASTMethodDeclaration(1);
        Assert.assertEquals(n, s.createNewNode(n).getNode());
    }
}

