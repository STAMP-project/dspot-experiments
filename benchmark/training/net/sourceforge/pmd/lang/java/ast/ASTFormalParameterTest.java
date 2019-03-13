/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import JavaLanguageModule.NAME;
import java.util.Iterator;
import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTFormalParameterTest {
    @Test
    public void testVarargs() {
        int nrOfVarArgs = 0;
        int nrOfNoVarArgs = 0;
        Set<ASTFormalParameter> ops = ParserTstUtil.getNodes(LanguageRegistry.getLanguage(NAME).getVersion("1.5"), ASTFormalParameter.class, ASTFormalParameterTest.TEST1);
        for (Iterator<ASTFormalParameter> iter = ops.iterator(); iter.hasNext();) {
            ASTFormalParameter b = iter.next();
            ASTVariableDeclaratorId variableDeclId = b.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
            if (!("x".equals(variableDeclId.getImage()))) {
                Assert.assertTrue(b.isVarargs());
                nrOfVarArgs++;
            } else {
                Assert.assertFalse(b.isVarargs());
                nrOfNoVarArgs++;
            }
        }
        // Ensure that both possibilities are tested
        Assert.assertEquals(1, nrOfVarArgs);
        Assert.assertEquals(1, nrOfNoVarArgs);
    }

    private static final String TEST1 = ((("class Foo {" + (PMD.EOL)) + " void bar(int x, int... others) {}") + (PMD.EOL)) + "}";
}

