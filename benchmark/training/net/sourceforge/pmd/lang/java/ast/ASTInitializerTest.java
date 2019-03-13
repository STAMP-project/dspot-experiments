/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Test;


public class ASTInitializerTest {
    @Test
    public void testDontCrashOnBlockStatement() {
        ParserTstUtil.getNodes(ASTInitializer.class, ASTInitializerTest.TEST1);
    }

    private static final String TEST1 = ((((((("public class Foo {" + (PMD.EOL)) + " {") + (PMD.EOL)) + "   x = 5;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

