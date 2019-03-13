/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTPackageDeclarationTest {
    private static final String PACKAGE_INFO_ANNOTATED = (("@Deprecated" + (PMD.EOL)) + "package net.sourceforge.pmd.foobar;") + (PMD.EOL);

    /**
     * Regression test for bug 3524607.
     */
    @Test
    public void testPackageName() {
        Set<ASTPackageDeclaration> nodes = ParserTstUtil.getNodes(ASTPackageDeclaration.class, ASTPackageDeclarationTest.PACKAGE_INFO_ANNOTATED);
        Assert.assertEquals(1, nodes.size());
        ASTPackageDeclaration packageNode = nodes.iterator().next();
        Assert.assertEquals("net.sourceforge.pmd.foobar", packageNode.getPackageNameImage());
    }
}

