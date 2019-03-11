/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import JavaLanguageModule.NAME;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Test;


public class ASTAnnotationTest {
    @Test
    public void testAnnotationSucceedsWithDefaultMode() {
        ParserTstUtil.getNodes(ASTAnnotation.class, ASTAnnotationTest.TEST1);
    }

    @Test(expected = ParseException.class)
    public void testAnnotationFailsWithJDK14() {
        ParserTstUtil.getNodes(LanguageRegistry.getLanguage(NAME).getVersion("1.4"), ASTAnnotation.class, ASTAnnotationTest.TEST1);
    }

    @Test
    public void testAnnotationSucceedsWithJDK15() {
        ParserTstUtil.getNodes(LanguageRegistry.getLanguage(NAME).getVersion("1.5"), ASTAnnotation.class, ASTAnnotationTest.TEST1);
    }

    private static final String TEST1 = ((((((((("public class Foo extends Buz {" + (PMD.EOL)) + " @Override") + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  // overrides a superclass method") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

