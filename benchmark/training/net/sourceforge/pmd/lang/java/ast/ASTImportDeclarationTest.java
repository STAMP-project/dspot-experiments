/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import JavaLanguageModule.NAME;
import java.util.Set;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class ASTImportDeclarationTest {
    @Test
    public void testImportOnDemand() {
        Set<ASTImportDeclaration> ops = ParserTstUtil.getNodes(ASTImportDeclaration.class, ASTImportDeclarationTest.TEST1);
        Assert.assertTrue(ops.iterator().next().isImportOnDemand());
    }

    @Test
    public void testGetImportedNameNode() {
        ASTImportDeclaration i = ParserTstUtil.getNodes(ASTImportDeclaration.class, ASTImportDeclarationTest.TEST2).iterator().next();
        Assert.assertEquals("foo.bar.Baz", i.getImportedName());
    }

    @Test
    public void testStaticImport() {
        Set<ASTImportDeclaration> ops = ParserTstUtil.getNodes(ASTImportDeclaration.class, ASTImportDeclarationTest.TEST3);
        ASTImportDeclaration i = ops.iterator().next();
        Assert.assertTrue(i.isStatic());
    }

    @Test(expected = ParseException.class)
    public void testStaticImportFailsWithJDK14() {
        ParserTstUtil.getNodes(LanguageRegistry.getLanguage(NAME).getVersion("1.4"), ASTImportDeclaration.class, ASTImportDeclarationTest.TEST3);
    }

    private static final String TEST1 = ("import foo.bar.*;" + (PMD.EOL)) + "public class Foo {}";

    private static final String TEST2 = ("import foo.bar.Baz;" + (PMD.EOL)) + "public class Foo {}";

    private static final String TEST3 = ("import static foo.bar.Baz;" + (PMD.EOL)) + "public class Foo {}";
}

