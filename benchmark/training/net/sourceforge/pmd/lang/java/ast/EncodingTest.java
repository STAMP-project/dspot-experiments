/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class EncodingTest {
    @Test
    public void testDecodingOfUTF8() throws Exception {
        ASTCompilationUnit acu = ParserTstUtil.parseJava14(EncodingTest.TEST_UTF8);
        String methodName = acu.findDescendantsOfType(ASTMethodDeclarator.class).get(0).getImage();
        Assert.assertEquals("?", methodName);
    }

    private static final String TEST_UTF8 = ((((("class Foo {" + (PMD.EOL)) + "  void ?() {}") + (PMD.EOL)) + "  void fiddle() {}") + (PMD.EOL)) + "}";
}

